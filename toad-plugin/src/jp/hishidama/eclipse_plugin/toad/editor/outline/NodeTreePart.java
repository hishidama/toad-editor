package jp.hishidama.eclipse_plugin.toad.editor.outline;

import java.beans.PropertyChangeEvent;
import java.util.List;

import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;

public class NodeTreePart extends ToadTreeEditPart {

	@Override
	public NodeElement getModel() {
		return (NodeElement) super.getModel();
	}

	@Override
	protected List<NodeElement> getModelChildren() {
		return getModel().getChildren();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		String name = event.getPropertyName();
		if (NodeElement.PROP_CHILDREN.equals(name)) {
			refreshChildren();
		}
		super.propertyChange(event);
	}
}
