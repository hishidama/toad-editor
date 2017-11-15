package jp.hishidama.eclipse_plugin.toad.model.marker;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

public class MarkerFigure extends Shape {

	private Label tip;

	public MarkerFigure() {
		this.setBorder(constructBorder());

		// setLayoutManager(new BorderLayout());

		resetToolTip();
	}

	protected Border constructBorder() {
		Border b = new Border() {
			@Override
			public void paint(IFigure figure, Graphics g, Insets insets) {
				Rectangle r = figure.getBounds();
				g.setLineWidth(1);
				g.drawLine(r.getBottomLeft(), r.getTopRight());
				g.setLineWidth(2);
				g.drawLine(r.getTopRight(), r.getBottomRight());
				g.drawLine(r.getBottomRight(), r.getBottomLeft());
			}

			@Override
			public boolean isOpaque() {
				return false;
			}

			@Override
			public Dimension getPreferredSize(IFigure arg0) {
				return null;
			}

			@Override
			public Insets getInsets(IFigure figure) {
				return null;
			}
		};
		return b;
	}

	private void resetToolTip() {
		if (tip == null) {
			tip = new Label("右下マーカー");
			setToolTip(tip);
		}
	}

	@Override
	protected void fillShape(Graphics g) {
	}

	@Override
	protected void outlineShape(Graphics g) {
	}
}
