package jp.hishidama.eclipse_plugin.toad.model.property.datamodel;

import jp.hishidama.eclipse_plugin.toad.model.property.TextSection;

public class DataModelDescriptionSection extends TextSection<HasDataModelNode> {

	public DataModelDescriptionSection() {
		super("description");
	}

	@Override
	protected String getValue(HasDataModelNode model) {
		return model.getModelDescription();
	}
}
