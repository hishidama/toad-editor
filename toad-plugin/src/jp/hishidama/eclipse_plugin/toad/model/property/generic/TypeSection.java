package jp.hishidama.eclipse_plugin.toad.model.property.generic;

import jp.hishidama.eclipse_plugin.toad.model.AbstractNameModel;
import jp.hishidama.eclipse_plugin.toad.model.property.TextSection;

public class TypeSection extends TextSection<AbstractNameModel> {

	public TypeSection() {
		super("type");
	}

	@Override
	protected String getValue(AbstractNameModel model) {
		return model.getType();
	}
}
