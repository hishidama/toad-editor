package jp.hishidama.eclipse_plugin.toad.model.property.generic;

import jp.hishidama.eclipse_plugin.toad.model.AbstractModel.ChangeTextCommand;

public interface NameNode {

	public static final String PROP_NAME = "name";

	public String getName();

	public void setName(String name);

	public ChangeTextCommand getNameCommand(String name);
}
