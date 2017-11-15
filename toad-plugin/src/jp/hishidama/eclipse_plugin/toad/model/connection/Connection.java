package jp.hishidama.eclipse_plugin.toad.model.connection;

import java.util.List;
import java.util.Set;

import jp.hishidama.eclipse_plugin.toad.model.AbstractModel;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.property.datamodel.HasDataModelNode;
import jp.hishidama.eclipse_plugin.toad.validation.ValidateType;
import jp.hishidama.eclipse_plugin.toad.view.SiblingDataModelTreeElement;
import jp.hishidama.eclipse_plugin.util.StringUtil;
import jp.hishidama.xtext.dmdl_editor.validation.ErrorStatus;

import org.eclipse.core.runtime.IStatus;

import com.google.gson.annotations.Expose;

public class Connection extends AbstractModel implements HasDataModelNode {
	private static final long serialVersionUID = -5187283868772648384L;

	public static final String PROP_SOURCE = "source";
	public static final String PROP_TARGET = "target";

	private NodeElement source;
	private NodeElement target;
	@Expose
	private int sourceId;
	@Expose
	private int targetId;

	@Override
	public Connection cloneEdit() {
		Connection to = new Connection();
		to.copyFrom(this);
		return to;
	}

	@Override
	public void copyFrom(AbstractModel fromModel) {
		super.copyFrom(fromModel);

		Connection from = (Connection) fromModel;
		this.source = from.source;
		this.sourceId = from.sourceId;
		this.target = from.target;
		this.targetId = from.targetId;
	}

	public NodeElement getSource() {
		return source;
	}

	public void setSource(NodeElement source) {
		NodeElement old = this.source;
		this.source = source;
		this.sourceId = (source != null) ? source.getId() : -1;
		firePropertyChange(PROP_SOURCE, old, source);
	}

	public int getSourceId() {
		return sourceId;
	}

	public NodeElement getTarget() {
		return target;
	}

	public void setTarget(NodeElement target) {
		NodeElement old = this.target;
		this.target = target;
		this.targetId = (target != null) ? target.getId() : -1;
		firePropertyChange(PROP_TARGET, old, target);
	}

	public int getTargetId() {
		return targetId;
	}

	@Override
	public String getIdString() {
		return String.format("%d->%d", sourceId, targetId);
	}

	/**
	 * 反対側のノードを返す.
	 * 
	 * @param node
	 *            ノード
	 * @return 反対側のノード
	 */
	public NodeElement getOpposite(NodeElement node) {
		if (node == source) {
			return target;
		} else if (node == target) {
			return source;
		} else {
			return null;
		}
	}

	@Override
	public void setModelName(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getModelName() {
		HasDataModelNode model = getModel();
		if (model != null) {
			return model.getModelName();
		}
		return null;
	}

	@Override
	public ChangeTextCommand getModelNameCommand(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setModelDescription(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getModelDescription() {
		HasDataModelNode model = getModel();
		if (model != null) {
			return model.getModelDescription();
		}
		return null;
	}

	@Override
	public ChangeTextCommand getModelDescriptionCommand(String description) {
		throw new UnsupportedOperationException();
	}

	private HasDataModelNode getModel() {
		if (source instanceof HasDataModelNode) {
			return (HasDataModelNode) source;
		}
		if (target instanceof HasDataModelNode) {
			return (HasDataModelNode) target;
		}
		return null;
	}

	@Override
	public void collectSiblingDataModelNode(SiblingDataModelTreeElement list, Set<Object> set, Set<Integer> idSet) {
		if (set.contains(this)) {
			return;
		}
		set.add(this);

		if (source instanceof HasDataModelNode) {
			((HasDataModelNode) source).collectSiblingDataModelNode(list, set, idSet);
		} else if (source != null) {
			source.collectSiblingDataModelNode(list, set, idSet, null);
		}
		if (target instanceof HasDataModelNode) {
			((HasDataModelNode) target).collectSiblingDataModelNode(list, set, idSet);
		} else if (target != null) {
			target.collectSiblingDataModelNode(list, set, idSet, null);
		}
	}

	@Override
	public void validate(ValidateType vtype, boolean edit, List<IStatus> result) {
		if (source instanceof HasDataModelNode && target instanceof HasDataModelNode) {
			String s = ((HasDataModelNode) source).getModelName();
			String t = ((HasDataModelNode) target).getModelName();
			if (StringUtil.isEmpty(s) || StringUtil.isEmpty(t)) {
				return;
			}
			if (!s.equals(t)) {
				result.add(new ErrorStatus("接続の両端のデータモデルが異なっています。source={0}, target={1}", s, t));
			}
		}
	}

	@Override
	public String getDisplayLocation() {
		return String.format("Connection{ %s -> %s }", source.getDisplayName(), target.getDisplayName());
	}

	@Override
	public String toString() {
		return String.format("Connection{ %s->%s }", source, target);
	}
}
