package jp.hishidama.eclipse_plugin.toad.model.node;

import java.util.Map;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.model.AbstractModel;
import jp.hishidama.eclipse_plugin.util.ToadCommandUtil;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.swt.graphics.Rectangle;

import com.google.gson.annotations.Expose;

@SuppressWarnings("serial")
public abstract class RectangleNode extends NodeElement {
	static final String PROP_X = "x";
	static final String PROP_Y = "y";
	static final String PROP_WIDTH = "width";
	static final String PROP_HEIGHT = "height";

	@Expose
	private int x;
	@Expose
	private int y;
	@Expose
	private int width;
	@Expose
	private int height;

	@Override
	public void copyFrom(AbstractModel fromModel) {
		super.copyFrom(fromModel);

		RectangleNode from = (RectangleNode) fromModel;
		this.x = from.x;
		this.y = from.y;
		this.width = from.width;
		this.height = from.height;
	}

	@Override
	public Command getCommand(ToadEditor editor, CompoundCommand compound, AbstractModel fromModel) {
		super.getCommand(editor, compound, fromModel);

		RectangleNode from = (RectangleNode) fromModel;
		ToadCommandUtil.add(compound, getXCommand(from.getX()));
		ToadCommandUtil.add(compound, getYCommand(from.getY()));
		ToadCommandUtil.add(compound, getWidthCommand(from.getWidth()));
		ToadCommandUtil.add(compound, getHeightCommand(from.getHeight()));

		return compound;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		int old = this.x;
		this.x = x;
		firePropertyChange(PROP_X, old, x);

		int zx = x - old;
		if (zx != 0) {
			for (NodeElement c : getChildren()) {
				c.addX(zx);
			}
		}
	}

	public ChangeIntCommand getXCommand(int x) {
		return new ChangeIntCommand(x) {
			@Override
			protected void setValue(int value) {
				setX(value);
			}

			@Override
			protected int getValue() {
				return getX();
			}
		};
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		int old = this.y;
		this.y = y;
		firePropertyChange(PROP_Y, old, y);

		int zy = y - old;
		if (zy != 0) {
			for (NodeElement c : getChildren()) {
				c.addY(zy);
			}
		}
	}

	public Command getYCommand(int y) {
		return new ChangeIntCommand(y) {
			@Override
			protected void setValue(int value) {
				setY(value);
			}

			@Override
			protected int getValue() {
				return getY();
			}
		};
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		int old = this.width;
		this.width = width;
		firePropertyChange(PROP_WIDTH, old, width);

		int zx = width - old;
		if (zx != 0) {
			for (NodeElement c : getChildren(RIGHT)) {
				c.addX(zx);
			}
		}
	}

	public Command getWidthCommand(int width) {
		return new ChangeIntCommand(width) {
			@Override
			protected void setValue(int value) {
				setWidth(value);
			}

			@Override
			protected int getValue() {
				return getWidth();
			}
		};
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		int old = this.height;
		this.height = height;
		firePropertyChange(PROP_HEIGHT, old, height);

		int zy = height - old;
		if (zy != 0) {
			for (NodeElement c : getChildren(BOTTOM)) {
				c.addY(zy);
			}
		}
	}

	public Command getHeightCommand(int height) {
		return new ChangeIntCommand(height) {
			@Override
			protected void setValue(int value) {
				setHeight(value);
			}

			@Override
			protected int getValue() {
				return getHeight();
			}
		};
	}

	@Override
	public void addX(int zx) {
		setX(x + zx);
	}

	@Override
	public void addY(int zy) {
		setY(y + zy);
	}

	@Override
	public Rectangle getCoreBounds() {
		return new Rectangle(x, y, width, height);
	}

	@Override
	public Rectangle getCoreBounds(Map<NodeElement, Rectangle> rectMap) {
		Rectangle r = rectMap.get(this);
		if (r == null) {
			return getCoreBounds();
		}
		return new Rectangle(r.x, r.y, r.width, r.height);
	}

	@Override
	public Rectangle getOuterBounds() {
		Rectangle rect = getCoreBounds();
		for (NodeElement child : getChildren()) {
			rect.add(child.getOuterBounds());
		}
		return rect;
	}

	@Override
	public Rectangle getOuterBounds(Map<NodeElement, Rectangle> rectMap) {
		Rectangle rect = getCoreBounds(rectMap);
		for (NodeElement child : getChildren()) {
			rect.add(child.getOuterBounds(rectMap));
		}
		return rect;
	}
}
