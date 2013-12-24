package jp.hishidama.eclipse_plugin.toad.model.property.generic;

import jp.hishidama.eclipse_plugin.toad.model.property.TextSection;

public class NameSection extends TextSection<NameNode> {

	public NameSection() {
		super("name");
	}

	@Override
	protected String getValue(NameNode model) {
		return model.getName();
	}
}
