package jp.hishidama.eclipse_plugin.toad.model.property.generic;

import jp.hishidama.eclipse_plugin.toad.model.AbstractNameModel;
import jp.hishidama.eclipse_plugin.toad.model.property.TextSection;

public class DescriptionSection extends TextSection<AbstractNameModel> {

	public DescriptionSection() {
		super("description");
	}

	@Override
	protected String getValue(AbstractNameModel model) {
		return model.getDescription();
	}
}
