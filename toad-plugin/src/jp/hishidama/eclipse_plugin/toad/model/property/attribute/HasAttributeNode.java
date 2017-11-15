package jp.hishidama.eclipse_plugin.toad.model.property.attribute;

import java.util.List;

import jp.hishidama.eclipse_plugin.toad.model.AbstractModel.ChangeListCommand;
import jp.hishidama.eclipse_plugin.toad.model.node.Attribute;

public interface HasAttributeNode {
	public static final String PROP_ATTRIBUTE = "attribute";

	public List<Attribute> getAttributeList();

	public void setAttributeList(List<Attribute> list);

	public void fireAttributeChange();

	public ChangeListCommand<Attribute> getAttributeListCommand(List<Attribute> list);
}
