package jp.hishidama.eclipse_plugin.toad.editor.action;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElementEditPart;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;

import org.eclipse.core.resources.IProject;
import org.eclipse.gef.ui.actions.SelectionAction;

public abstract class OperatorDslAction extends SelectionAction {

	protected final IProject project;

	public OperatorDslAction(ToadEditor editor) {
		super(editor);
		this.project = editor.getProject();
	}

	@Override
	protected boolean calculateEnabled() {
		List<OperatorNode> list = getSelectedObjects();
		return !list.isEmpty();
	}

	@Override
	protected List<OperatorNode> getSelectedObjects() {
		List<?> list = super.getSelectedObjects();
		List<OperatorNode> result = new ArrayList<OperatorNode>(list.size());
		for (Object obj : list) {
			if (obj instanceof NodeElementEditPart) {
				NodeElementEditPart part = (NodeElementEditPart) obj;
				NodeElement model = part.getModel();
				if (model instanceof OperatorNode) {
					OperatorNode operator = (OperatorNode) model;
					if (operator.isUserOperator()) {
						result.add(operator);
					}
				}
			}
		}
		return result;
	}
}
