package jp.hishidama.eclipse_plugin.toad.model.property.layout;

import jp.hishidama.eclipse_plugin.toad.model.node.RectangleNode;
import jp.hishidama.eclipse_plugin.toad.model.property.IntSection;

public class XSection extends IntSection<RectangleNode> {

	public XSection() {
		super("x");
	}

	@Override
	protected int getIntValue(RectangleNode model) {
		return model.getX();
	}

	@Override
	protected void setIntValue(RectangleNode model, int value) {
		model.setX(value);
	}
}
