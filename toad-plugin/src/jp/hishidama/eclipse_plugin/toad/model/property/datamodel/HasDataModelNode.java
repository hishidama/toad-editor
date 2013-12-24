package jp.hishidama.eclipse_plugin.toad.model.property.datamodel;

import java.util.Set;

import jp.hishidama.eclipse_plugin.toad.model.AbstractModel.ChangeTextCommand;
import jp.hishidama.eclipse_plugin.toad.view.SiblingDataModelTreeElement;

public interface HasDataModelNode {

	public static final String PROP_MODEL_NAME = "modelName";
	public static final String PROP_MODEL_DESCRIPTION = "modelDescription";
	public static final String PROP_MODEL_CLASSNAME = "modelClassName";

	public void setModelName(String name);

	public String getModelName();

	public ChangeTextCommand getModelNameCommand(String name);

	public void setModelDescription(String name);

	public String getModelDescription();

	public ChangeTextCommand getModelDescriptionCommand(String description);

	public void collectSiblingDataModelNode(SiblingDataModelTreeElement list, Set<Object> set, Set<Integer> idSet);
}
