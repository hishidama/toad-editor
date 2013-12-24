package jp.hishidama.eclipse_plugin.toad.model.property.layout;

import jp.hishidama.eclipse_plugin.toad.model.node.port.BasePort;
import jp.hishidama.eclipse_plugin.toad.model.property.IntSection;

public class CxSection extends IntSection<BasePort> {

	public CxSection() {
		super("center x");
	}

	@Override
	protected int getIntValue(BasePort model) {
		return model.getCx();
	}

	@Override
	protected void setIntValue(BasePort model, int value) {
		model.setCx(value);
	}
}
