package jp.hishidama.eclipse_plugin.toad.model.property.layout;

import jp.hishidama.eclipse_plugin.toad.model.node.RectangleNode;
import jp.hishidama.eclipse_plugin.toad.model.property.IntSection;

public class WidthSection extends IntSection<RectangleNode> {

	public WidthSection() {
		super("width");
	}

	@Override
	protected int getIntValue(RectangleNode model) {
		return model.getWidth();
	}

	@Override
	protected void setIntValue(RectangleNode model, int value) {
		model.setWidth(value);
	}
}
