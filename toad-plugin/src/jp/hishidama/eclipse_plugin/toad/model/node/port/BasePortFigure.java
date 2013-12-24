package jp.hishidama.eclipse_plugin.toad.model.node.port;

import jp.hishidama.eclipse_plugin.util.StringUtil;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

public abstract class BasePortFigure extends Figure {
	protected static final int ARROW_NX = 16;
	protected static final int ARROW_NY = 16;

	private int cx, cy;
	private ArrowShape arrowShape;
	private Label nameLabel;
	private String description;
	private String modelName, modelDescription;
	private Label tip;

	public BasePortFigure() {
		setLayoutManager(new BorderLayout());

		nameLabel = new Label();
		add(nameLabel, BorderLayout.LEFT);
		arrowShape = new ArrowShape();
		add(arrowShape, BorderLayout.CENTER);
	}

	public void setName(String name) {
		nameLabel.setText(name);
		resetToolTip();
	}

	public void setDescription(String description) {
		this.description = description;
		resetToolTip();
	}

	public void setModelName(String name, String desc) {
		this.modelName = name;
		this.modelDescription = desc;
		resetToolTip();
	}

	public Label getNameLabel() {
		return nameLabel;
	}

	private void resetToolTip() {
		StringBuilder sb = new StringBuilder(64);
		sb.append(nameLabel.getText());
		if (StringUtil.nonEmpty(description)) {
			if (sb.length() > 0) {
				sb.append('\n');
			}
			sb.append(description);
		}
		if (StringUtil.nonEmpty(modelName)) {
			if (sb.length() > 0) {
				sb.append('\n');
			}
			sb.append(modelName);
			if (StringUtil.nonEmpty(modelDescription)) {
				sb.append(" : ");
				sb.append(modelDescription);
			}
		} else {
			if (StringUtil.nonEmpty(modelDescription)) {
				sb.append(modelDescription);
			}
		}
		if (sb.length() > 0) {
			if (tip == null) {
				tip = new Label(sb.toString());
			} else {
				tip.setText(sb.toString());
			}
			setToolTip(tip);
		}
	}

	public void setLabelPosition(int position) {
		switch (position) {
		case PositionConstants.NONE:
			if (nameLabel.getParent() != null) {
				remove(nameLabel);
			}
			return;
		case PositionConstants.LEFT:
		case PositionConstants.RIGHT:
		case PositionConstants.TOP:
		case PositionConstants.BOTTOM:
			break;
		default:
			position = PositionConstants.LEFT;
			break;
		}
		if (nameLabel.getParent() == null) {
			add(nameLabel, (Object) position);
		} else {
			setConstraint(nameLabel, position);
		}
	}

	public Rectangle calculateBounds(int cx, int cy, String name, int position) {
		nameLabel.setText(name);
		Rectangle t;
		if (nameLabel.getParent() != null) {
			t = nameLabel.getTextBounds();
		} else {
			t = new Rectangle();
		}
		int x, y, w, h;
		switch (position) {
		default:
			x = cx - ARROW_NX / 2 - t.width();
			w = ARROW_NX + t.width();
			h = Math.max(ARROW_NY, t.height());
			y = cy - h / 2;
			break;
		case PositionConstants.RIGHT:
			x = cx - ARROW_NX / 2;
			w = ARROW_NX + t.width();
			h = Math.max(ARROW_NY, t.height());
			y = cy - h / 2;
			break;
		case PositionConstants.TOP:
			w = Math.max(ARROW_NX, t.width());
			x = cx - w / 2;
			y = cy - ARROW_NY / 2 - t.height();
			h = ARROW_NY + t.height();
			break;
		case PositionConstants.BOTTOM:
			w = Math.max(ARROW_NX, t.width());
			x = cx - w / 2;
			y = cy - ARROW_NY / 2;
			h = ARROW_NY + t.height();
			break;
		case PositionConstants.NONE:
			w = 16;
			h = 16;
			x = cx - w / 2;
			y = cy - h / 2;
			break;
		}
		this.cx = cx;
		this.cy = cy;
		arrowShape.setCenterLocation(cx, cy);
		return new Rectangle(x, y, w, h);
	}

	public Point getConnectionPoint(boolean out) {
		ArrowPoint point = new ArrowPoint();
		getArrowPoint(point);
		if (out) {
			return new Point(this.cx + point.ex, this.cy);
		} else {
			return new Point(this.cx + point.sx, this.cy);
		}
	}

	private class ArrowShape extends Shape {
		private PointList pointList = new PointList(5);

		public ArrowShape() {
			setSize(ARROW_NX, ARROW_NY);
		}

		public void setCenterLocation(int cx, int cy) {
			ArrowPoint point = new ArrowPoint();
			getArrowPoint(point);
			int sx = cx + point.sx;
			int mx = cx + point.mx;
			int ex = cx + point.ex;
			int sy = cy + point.sy;
			int my = cy + point.my;
			int ey = cy + point.ey;
			createList(pointList, sx, mx, ex, sy, my, ey);
		}

		@Override
		protected void fillShape(Graphics graphics) {
			// background color (white)
			graphics.fillPolygon(pointList);
		}

		@Override
		protected void outlineShape(Graphics graphics) {
			// foreground color (black)
			graphics.drawPolygon(pointList);
		}

		private void createList(PointList list, int sx, int mx, int ex, int sy, int my, int ey) {
			list.removeAllPoints();
			list.addPoint(new Point(sx, sy));
			list.addPoint(new Point(mx, sy));
			list.addPoint(new Point(ex, my));
			list.addPoint(new Point(mx, ey));
			list.addPoint(new Point(sx, ey));
		}
	}

	protected static class ArrowPoint {
		public int sx, mx, ex;
		public int sy, my, ey;
	}

	protected abstract void getArrowPoint(ArrowPoint point);
}
