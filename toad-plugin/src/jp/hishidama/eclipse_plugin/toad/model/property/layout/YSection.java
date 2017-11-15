package jp.hishidama.eclipse_plugin.toad.model.property.layout;

import jp.hishidama.eclipse_plugin.toad.model.node.RectangleNode;
import jp.hishidama.eclipse_plugin.toad.model.property.IntSection;

public class YSection extends IntSection<RectangleNode> {

	public YSection() {
		super("y");
	}

	@Override
	protected int getIntValue(RectangleNode model) {
		return model.getY();
	}

	@Override
	protected void setIntValue(RectangleNode model, int value) {
		model.setY(value);
	}
}
