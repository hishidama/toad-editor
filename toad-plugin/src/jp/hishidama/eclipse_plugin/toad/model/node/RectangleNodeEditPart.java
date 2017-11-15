package jp.hishidama.eclipse_plugin.toad.model.node;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.geometry.Rectangle;

public abstract class RectangleNodeEditPart extends NodeElementEditPart {

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		String name = event.getPropertyName();
		if (RectangleNode.PROP_X.equals(name)
				|| RectangleNode.PROP_Y.equals(name)
				|| RectangleNode.PROP_WIDTH.equals(name)
				|| RectangleNode.PROP_HEIGHT.equals(name)) {
			refreshVisuals();
		}

		super.propertyChange(event);
	}

	protected Rectangle calculateFigureBounds() {
		RectangleNode node = getModel();
		int x = node.getX();
		int y = node.getY();
		int w = node.getWidth();
		int h = node.getHeight();
		Rectangle rect = new Rectangle(x, y, w, h);
		return rect;
	}

	@Override
	public RectangleNode getModel() {
		return (RectangleNode) super.getModel();
	}
}
