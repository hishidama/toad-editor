package jp.hishidama.eclipse_plugin.toad.model.node.port;

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.geometry.Point;

public class BaseAnchor extends AbstractConnectionAnchor {

	private boolean out;

	public BaseAnchor(BasePortFigure figure, boolean out) {
		super(figure);
		this.out = out;
	}

	@Override
	public BasePortFigure getOwner() {
		return (BasePortFigure) super.getOwner();
	}

	@Override
	public Point getLocation(Point reference) {
		BasePortFigure figure = getOwner();
		Point point = figure.getConnectionPoint(out);
		figure.translateToAbsolute(point);
		return point;
	}

	@Override
	public Point getReferencePoint() {
		return getLocation(null);
	}
}
