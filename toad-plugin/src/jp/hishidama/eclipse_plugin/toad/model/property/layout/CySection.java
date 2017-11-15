package jp.hishidama.eclipse_plugin.toad.model.property.layout;

import jp.hishidama.eclipse_plugin.toad.model.node.port.BasePort;
import jp.hishidama.eclipse_plugin.toad.model.property.IntSection;

public class CySection extends IntSection<BasePort> {

	public CySection() {
		super("center y");
	}

	@Override
	protected int getIntValue(BasePort model) {
		return model.getCy();
	}

	@Override
	protected void setIntValue(BasePort model, int value) {
		model.setCy(value);
	}
}
