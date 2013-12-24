package jp.hishidama.eclipse_plugin.toad.model.marker;

import jp.hishidama.eclipse_plugin.toad.model.node.RectangleNodeEditPart;

import org.eclipse.draw2d.IFigure;

public class MarkerEditPart extends RectangleNodeEditPart {

	@Override
	protected IFigure createFigure() {
		return new MarkerFigure();
	}

	@Override
	public MarkerFigure getFigure() {
		return (MarkerFigure) super.getFigure();
	}
}
