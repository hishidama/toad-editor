package jp.hishidama.eclipse_plugin.toad.model.node.operator;

import java.util.HashSet;
import java.util.Set;

import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;
import jp.hishidama.eclipse_plugin.toad.model.property.datamodel.DataModelNodeUtil;
import jp.hishidama.eclipse_plugin.toad.model.property.datamodel.HasDataModelNode;
import jp.hishidama.eclipse_plugin.toad.view.SiblingDataModelTreeElement;

public class EllipseOperatorNode extends OperatorNode implements HasDataModelNode {
	private static final long serialVersionUID = 3946689782143021104L;

	@Override
	public EllipseOperatorNode cloneEdit() {
		EllipseOperatorNode to = new EllipseOperatorNode();
		to.copyFrom(this);
		return to;
	}

	@Override
	public void setModelName(String name) {
	}

	@Override
	public String getModelName() {
		return getModelName(new HashSet<HasDataModelNode>());
	}

	public String getModelName(Set<HasDataModelNode> set) {
		HasDataModelNode model = getConnectedNode(set);
		if (model != null) {
			return model.getModelName();
		}
		return null;
	}

	@Override
	public ChangeTextCommand getModelNameCommand(String name) {
		return null;
	}

	@Override
	public void setModelDescription(String name) {
	}

	@Override
	public String getModelDescription() {
		HasDataModelNode model = getConnectedNode(new HashSet<HasDataModelNode>());
		if (model != null) {
			return model.getModelDescription();
		}
		return null;
	}

	@Override
	public ChangeTextCommand getModelDescriptionCommand(String description) {
		return null;
	}

	protected HasDataModelNode getConnectedNode(Set<HasDataModelNode> set) {
		if (set.contains(this)) {
			return null;
		}
		set.add(this);

		for (Connection c : getIncomings()) {
			HasDataModelNode opposite = (HasDataModelNode) c.getOpposite(this);
			if (getModelName(opposite, set) != null) {
				return opposite;
			}
		}
		for (Connection c : getOutgoings()) {
			HasDataModelNode opposite = (HasDataModelNode) c.getOpposite(this);
			if (getModelName(opposite, set) != null) {
				return opposite;
			}
		}
		return null;
	}

	private String getModelName(HasDataModelNode opposite, Set<HasDataModelNode> set) {
		if (opposite instanceof EllipseOperatorNode) {
			return ((EllipseOperatorNode) opposite).getModelName(set);
		}
		return opposite.getModelName();
	}

	@Override
	public void collectSiblingDataModelNode(SiblingDataModelTreeElement list, Set<Object> set, Set<Integer> idSet) {
		DataModelNodeUtil.collectSiblingDataModelNode(list, set, idSet, this);
	}
}
