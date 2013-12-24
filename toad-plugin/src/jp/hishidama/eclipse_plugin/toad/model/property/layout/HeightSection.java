package jp.hishidama.eclipse_plugin.toad.model.property.layout;

import jp.hishidama.eclipse_plugin.toad.model.node.RectangleNode;
import jp.hishidama.eclipse_plugin.toad.model.property.IntSection;

public class HeightSection extends IntSection<RectangleNode> {

	public HeightSection() {
		super("height");
	}

	@Override
	protected int getIntValue(RectangleNode model) {
		return model.getHeight();
	}

	@Override
	protected void setIntValue(RectangleNode model, int value) {
		model.setHeight(value);
	}
}
