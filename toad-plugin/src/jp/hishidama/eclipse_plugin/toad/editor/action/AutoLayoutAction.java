package jp.hishidama.eclipse_plugin.toad.editor.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hishidama.eclipse_plugin.toad.editor.action.layout.DataFileAutoLayout;
import jp.hishidama.eclipse_plugin.toad.editor.action.layout.FrameAutoLayout;
import jp.hishidama.eclipse_plugin.toad.editor.action.layout.GefAutoLayout;
import jp.hishidama.eclipse_plugin.toad.editor.action.layout.JobPortAutoLayout;
import jp.hishidama.eclipse_plugin.toad.editor.action.layout.MarkerAutoLayout;
import jp.hishidama.eclipse_plugin.toad.editor.action.layout.OpePortAutoLayout;
import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElementEditPart;

import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.ui.IWorkbenchPart;

public class AutoLayoutAction extends SelectionAction {

	public static final String ID = "TOAD_AUTO_LAYOUT";

	public AutoLayoutAction(IWorkbenchPart part) {
		super(part);
	}

	@Override
	protected void init() {
		super.init();
		setText("auto layout");
		setToolTipText("auto layout");
		setId(ID);
	}

	@Override
	protected boolean calculateEnabled() {
		List<?> list = getSelectedObjects();
		int count = 0;
		for (Object obj : list) {
			if (obj instanceof NodeElementEditPart) {
				if (++count >= 1) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void run() {
		Diagram diagram = null;
		List<?> list = getSelectedObjects();
		List<NodeElement> models = new ArrayList<NodeElement>(list.size());
		for (Object obj : list) {
			NodeElementEditPart part = (NodeElementEditPart) obj;
			NodeElement node = part.getModel();
			models.add(node);
			if (diagram == null) {
				diagram = node.getDiagram();
			}
		}
		Map<NodeElement, Rectangle> rectMap = new HashMap<NodeElement, Rectangle>(list.size());

		CompoundCommand command = new CompoundCommand();
		command.add(new GefAutoLayout(rectMap).getCommand(models));
		command.add(new OpePortAutoLayout(rectMap).getCommand(models));
		command.add(new JobPortAutoLayout(rectMap).getCommand(models));
		command.add(new DataFileAutoLayout(rectMap).getCommand(models));
		command.add(new FrameAutoLayout(rectMap).getCommand(models));
		command.add(new MarkerAutoLayout(rectMap).getCommand(diagram));
		getCommandStack().execute(command);
	}
}
