package jp.hishidama.eclipse_plugin.toad.editor.action;

import java.util.List;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.editor.action.copy.ClipboardObject;
import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;
import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.CopyTemplateAction;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class CopyAction extends SelectionAction implements ISelectionChangedListener {
	private Clipboard clipboard = new Clipboard(null);
	private final PaletteCopyAction paletteCopyAction;
	private boolean isNode;

	private static class PaletteCopyAction extends CopyTemplateAction {
		public PaletteCopyAction(IEditorPart part) {
			super(part);
		}

		@Override
		public boolean calculateEnabled() {
			return super.calculateEnabled();
		}
	};

	public CopyAction(ToadEditor editor) {
		super(editor);
		paletteCopyAction = new PaletteCopyAction(editor);
		setId(paletteCopyAction.getId());
		setText(paletteCopyAction.getText());
		setToolTipText(paletteCopyAction.getToolTipText());
	}

	@Override
	protected void init() {
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
	}

	@Override
	protected boolean calculateEnabled() {
		if (paletteCopyAction.calculateEnabled()) {
			return true;
		}

		if (getSelection() == null) {
			return false;
		}
		List<?> list = getSelectedObjects();
		return enableCopyNode(list);
	}

	public boolean enableCopyNode(List<?> list) {
		for (Object obj : list) {
			if (obj instanceof EditPart) {
				EditPart part = (EditPart) obj;
				Object model = part.getModel();
				if (model instanceof Diagram) {
					continue;
				}
				if (model instanceof Connection) {
					continue;
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public void dispose() {
		paletteCopyAction.dispose();
		clipboard.dispose();
		super.dispose();
	}

	@Override
	public void run() {
		if (isNode) {
			List<?> list = getSelectedObjects();

			ClipboardObject clip = ClipboardObject.create(list);
			if (clip != null) {
				String text = clip.serialize();
				Object[] data = { text };
				Transfer[] dataTypes = { TextTransfer.getInstance() };
				clipboard.setContents(data, dataTypes);
			}
		} else {
			paletteCopyAction.run();
		}
	}

	@Override
	protected void handleSelectionChanged() {
		isNode = (getSelection() != null);

		super.handleSelectionChanged();
	}

	/**
	 * パレットに登録するリスナー
	 */
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		paletteCopyAction.selectionChanged(event);
		setSelection(null);
	}
}
