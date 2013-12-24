package jp.hishidama.eclipse_plugin.toad.editor.action;

import java.util.List;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.editor.action.copy.BatchPaste;
import jp.hishidama.eclipse_plugin.toad.editor.action.copy.ClipboardObject;
import jp.hishidama.eclipse_plugin.toad.editor.action.copy.FlowPaste;
import jp.hishidama.eclipse_plugin.toad.editor.action.copy.PasteCommandGenerator;
import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.diagram.DiagramType;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.datafile.DataFileFactory;
import jp.hishidama.eclipse_plugin.toad.model.node.jobflow.JobFactory;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.UserOperatorFactory;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.ui.actions.PasteTemplateAction;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.internal.WorkbenchMessages;

@SuppressWarnings("restriction")
public class PasteAction extends SelectionAction {
	private Clipboard clipboard = new Clipboard(null);
	private ToadEditor editor;
	private PalettePasteAction palettePasteAction;

	/**
	 * @see PasteTemplateAction
	 */
	private class PalettePasteAction {
		private int positionX = 0;
		private int positionY = 0;

		public Command createPasteCommand() {
			Object template = getClipboardContents();
			if (template != null) {
				CreationFactory factory = getFactory(template);
				if (factory != null) {
					GraphicalEditPart gep = editor.getDiagramEditPart();

					CreateRequest request = new CreateRequest();
					request.setFactory(factory);
					request.setLocation(getPasteLocation(gep));
					Command command = gep.getCommand(request);
					return command;
				}
			}
			return null;
		}

		protected Object getClipboardContents() {
			return org.eclipse.gef.ui.actions.Clipboard.getDefault().getContents();
		}

		protected CreationFactory getFactory(Object template) {
			if (acceptFactory(template)) {
				return (CreationFactory) template;
			}
			return null;
		}

		private boolean acceptFactory(Object template) {
			Diagram diagram = editor.getDiagram();
			DiagramType type = diagram.getDiagramType();
			if (template instanceof UserOperatorFactory) {
				return type == DiagramType.JOBFLOW || type == DiagramType.FLOWPART;
			} else if (template instanceof DataFileFactory) {
				return type == DiagramType.JOBFLOW;
			} else if (template instanceof JobFactory) {
				return type == DiagramType.BATCH;
			} else {
				return false;
			}
		}

		protected Point getPasteLocation(GraphicalEditPart container) {
			// スクロール表示範囲の左上
			int x = (positionX + 1) * 16;
			int y = x + positionY * 16;
			if (++positionX % 8 == 0) {
				positionX = 0;
				positionY++;
			}
			Point result = new Point(x, y);
			return result;
		}
	}

	public PasteAction(ToadEditor editor) {
		super(editor);
		this.editor = editor;
		palettePasteAction = new PalettePasteAction();
	}

	@Override
	protected void init() {
		setId(ActionFactory.PASTE.getId());
		setText(WorkbenchMessages.Workbench_paste);
		setToolTipText(WorkbenchMessages.Workbench_pasteToolTip);
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
		setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));
	}

	@Override
	public void dispose() {
		clipboard.dispose();
		super.dispose();
	}

	@Override
	protected boolean calculateEnabled() {
		Object template = palettePasteAction.getClipboardContents();
		if (template != null) {
			return true;
		}
		return true;
	}

	@Override
	public void run() {
		Command command = palettePasteAction.createPasteCommand();
		if (command != null) {
			execute(command);
			return;
		}

		PasteCommandGenerator paste = null;
		String text = (String) clipboard.getContents(TextTransfer.getInstance());
		ClipboardObject clip = ClipboardObject.deserialize(text);
		if (clip != null) {
			DiagramType type = editor.getDiagram().getDiagramType();
			switch (type) {
			case BATCH:
				paste = new BatchPaste();
				break;
			case JOBFLOW:
			case FLOWPART:
				paste = new FlowPaste(type);
				break;
			default:
				break;
			}
		}
		if (paste == null) {
			return;
		}
		paste.initialize(editor, clip);
		command = paste.getPasteCommand();
		if (command != null) {
			execute(command);

			EditPartViewer viewer = editor.getDiagramEditPart().getViewer();
			viewer.deselectAll();
			List<? extends NodeElement> list = paste.getPasteNodeList();
			for (NodeElement node : list) {
				EditPart part = node.getEditPart();
				if (part != null) {
					viewer.appendSelection(part);
				}
			}
		}
	}
}
