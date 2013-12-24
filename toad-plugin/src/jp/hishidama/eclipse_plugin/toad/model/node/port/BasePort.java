package jp.hishidama.eclipse_plugin.toad.model.node.port;

import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.model.AbstractModel;
import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.property.datamodel.DataModelNodeUtil;
import jp.hishidama.eclipse_plugin.toad.model.property.datamodel.HasDataModelNode;
import jp.hishidama.eclipse_plugin.toad.model.property.generic.NameNode;
import jp.hishidama.eclipse_plugin.toad.validation.ToadValidator;
import jp.hishidama.eclipse_plugin.toad.validation.ValidateType;
import jp.hishidama.eclipse_plugin.toad.view.SiblingDataModelTreeElement;
import jp.hishidama.eclipse_plugin.util.StringUtil;
import jp.hishidama.eclipse_plugin.util.ToadCommandUtil;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.swt.graphics.Rectangle;

import com.google.gson.annotations.Expose;

@SuppressWarnings("serial")
public abstract class BasePort extends NodeElement implements NameNode, HasDataModelNode {
	public static final int WIDTH = 16;
	public static final int HEIGHT = 16;

	protected static final String PROP_CX = "cx";
	protected static final String PROP_CY = "cy";
	protected static final String PROP_INOUT = "in/out";
	protected static final String PROP_POSITION = "namePosition";
	protected static final String PROP_ROLE = "role";

	@Expose
	private int cx;
	@Expose
	private int cy;
	@Expose
	private boolean out;
	@Expose
	private String name;
	@Expose
	private int namePosition;
	@Expose
	private String role;
	@Expose
	private String modelName;
	@Expose
	private String modelDescription;

	public BasePort() {
	}

	@Override
	public abstract BasePort cloneEdit();

	@Override
	public void copyFrom(AbstractModel fromModel) {
		super.copyFrom(fromModel);

		BasePort from = (BasePort) fromModel;
		this.cx = from.cx;
		this.cy = from.cy;
		this.out = from.out;
		this.name = from.name;
		this.namePosition = from.namePosition;
		this.role = from.role;
		this.modelName = from.modelName;
		this.modelDescription = from.modelDescription;
	}

	@Override
	public Command getCommand(ToadEditor editor, CompoundCommand compound, AbstractModel fromModel) {
		super.getCommand(editor, compound, fromModel);

		BasePort from = (BasePort) fromModel;
		ToadCommandUtil.add(compound, getCxCommand(from.getCx()));
		ToadCommandUtil.add(compound, getCyCommand(from.getCy()));
		ToadCommandUtil.add(compound, getInOutCommand(from.isIn()));
		ToadCommandUtil.add(compound, getNameCommand(from.getName()));
		ToadCommandUtil.add(compound, getNamePositionCommand(from.getNamePosition()));
		ToadCommandUtil.add(compound, getRoleCommand(from.getRole()));
		ToadCommandUtil.add(compound, getModelNameCommand(from.getModelName()));
		ToadCommandUtil.add(compound, getModelDescriptionCommand(from.getModelDescription()));

		return compound;
	}

	@Override
	public String getName() {
		if (name == null) {
			return "";
		}
		return name;
	}

	@Override
	public void setName(String name) {
		String old = this.name;
		this.name = name;
		firePropertyChange(PROP_NAME, old, name);
	}

	@Override
	public ChangeTextCommand getNameCommand(String name) {
		return new ChangeTextCommand(name) {
			@Override
			protected void setValue(String value) {
				setName(value);
			}

			@Override
			protected String getValue() {
				return getName();
			}
		};
	}

	@Override
	public String getQualifiedDescription() {
		String desc = getDescription();
		NodeElement parent = getParent();
		if (parent != null) {
			String pdesc = parent.getQualifiedDescription();
			if (StringUtil.nonEmpty(pdesc) && StringUtil.nonEmpty(desc)) {
				return String.format("%s.%s", pdesc, desc);
			}
		}
		return desc;
	}

	@Override
	public String getDisplayName() {
		String sname = getSimpleDisplayName();
		NodeElement parent = getParent();
		if (parent != null) {
			String pname = parent.getDisplayName();
			if (StringUtil.nonEmpty(pname) && StringUtil.nonEmpty(sname)) {
				return String.format("%s.%s", pname, sname);
			}
		}
		return sname;
	}

	@Override
	public String getSimpleDisplayName() {
		String name = getName();
		if (StringUtil.nonEmpty(name)) {
			return name;
		}
		return getDescription();
	}

	public void setCx(int cx) {
		int old = this.cx;
		this.cx = cx;
		firePropertyChange(PROP_CX, old, cx);
	}

	public int getCx() {
		return cx;
	}

	public ChangeIntCommand getCxCommand(int cx) {
		return new ChangeIntCommand(cx) {
			@Override
			protected void setValue(int value) {
				setCx(value);
			}

			@Override
			protected int getValue() {
				return getCx();
			}
		};
	}

	public void setCy(int cy) {
		int old = this.cy;
		this.cy = cy;
		firePropertyChange(PROP_CY, old, cy);
	}

	public int getCy() {
		return cy;
	}

	public ChangeIntCommand getCyCommand(int cy) {
		return new ChangeIntCommand(cy) {
			@Override
			protected void setValue(int value) {
				setCy(value);
			}

			@Override
			protected int getValue() {
				return getCy();
			}
		};
	}

	@Override
	public void addX(int zx) {
		setCx(cx + zx);
	}

	@Override
	public void addY(int zy) {
		setCy(cy + zy);
	}

	@Override
	public Rectangle getCoreBounds() {
		return new Rectangle(cx - WIDTH / 2, cy - HEIGHT / 2, WIDTH, HEIGHT);
	}

	@Override
	public Rectangle getCoreBounds(Map<NodeElement, Rectangle> rectMap) {
		Rectangle r = rectMap.get(this);
		if (r == null) {
			return getCoreBounds();
		}
		return new Rectangle(r.x, r.y, WIDTH, HEIGHT);
	}

	@Override
	public Rectangle getOuterBounds() {
		Rectangle rect = getCoreBounds();
		return getOuterBounds(rect);
	}

	@Override
	public Rectangle getOuterBounds(Map<NodeElement, Rectangle> rectMap) {
		Rectangle rect = getCoreBounds(rectMap);
		return getOuterBounds(rect);
	}

	private Rectangle getOuterBounds(Rectangle rect) {
		// テキストの幅・高さ（本来ならFont.sizeから算出したいところ）
		int tw = 6 * getName().length();
		int th = 16;

		switch (namePosition) {
		case PositionConstants.LEFT:
			rect.x -= tw;
			rect.width += tw;
			break;
		case PositionConstants.RIGHT:
			rect.width += tw;
			break;
		case PositionConstants.TOP:
		case PositionConstants.BOTTOM:
			if (namePosition == PositionConstants.TOP) {
				rect.y -= th;
				rect.height += th;
			} else {
				rect.height += th;
			}
			if (tw > rect.width) {
				rect.x += rect.width / 2 - tw / 2;
				rect.width = tw;
			}
			break;
		default:
			break;
		}
		return rect;
	}

	public void setIn(boolean in) {
		boolean old = this.out;
		this.out = !in;
		firePropertyChange(PROP_INOUT, old, this.out);
	}

	public void setOut(boolean out) {
		boolean old = this.out;
		this.out = out;
		firePropertyChange(PROP_INOUT, old, this.out);
	}

	public boolean isIn() {
		return !out;
	}

	public boolean isOut() {
		return out;
	}

	public ChangeBooleanCommand getInOutCommand(boolean in) {
		return new ChangeBooleanCommand(in) {
			@Override
			protected void setValue(boolean value) {
				setIn(value);
			}

			@Override
			protected boolean getValue() {
				return isIn();
			}
		};
	}

	/**
	 * position of name.
	 * 
	 * @param position
	 *            {@link PositionConstants#NONE}, {@link PositionConstants#LEFT}
	 *            , {@link PositionConstants#RIGHT},
	 *            {@link PositionConstants#TOP},
	 *            {@link PositionConstants#BOTTOM}
	 */
	public void setNamePosition(int position) {
		int old = this.namePosition;
		this.namePosition = position;
		firePropertyChange(PROP_POSITION, old, position);
	}

	public int getNamePosition() {
		return namePosition;
	}

	public ChangeIntCommand getNamePositionCommand(int position) {
		return new ChangeIntCommand(position) {
			@Override
			protected void setValue(int value) {
				setNamePosition(value);
			}

			@Override
			protected int getValue() {
				return getNamePosition();
			}
		};
	}

	public String getRole() {
		if (role == null) {
			return "";
		}
		return role;
	}

	public void setRole(String role) {
		String old = this.role;
		this.role = role;
		firePropertyChange(PROP_ROLE, old, role);
	}

	public ChangeTextCommand getRoleCommand(String role) {
		return new ChangeTextCommand(role) {
			@Override
			protected void setValue(String value) {
				setRole(value);
			}

			@Override
			protected String getValue() {
				return getRole();
			}
		};
	}

	@Override
	public void setModelName(String name) {
		String old = this.modelName;
		this.modelName = name;
		firePropertyChange(PROP_MODEL_NAME, old, name);
	}

	@Override
	public String getModelName() {
		return modelName;
	}

	@Override
	public ChangeTextCommand getModelNameCommand(String name) {
		return new ChangeTextCommand(name) {
			@Override
			protected void setValue(String value) {
				setModelName(value);
			}

			@Override
			protected String getValue() {
				return getModelName();
			}
		};
	}

	@Override
	public void setModelDescription(String description) {
		String old = this.modelDescription;
		this.modelDescription = description;
		firePropertyChange(PROP_MODEL_DESCRIPTION, old, description);
	}

	@Override
	public String getModelDescription() {
		return modelDescription;
	}

	@Override
	public ChangeTextCommand getModelDescriptionCommand(String description) {
		return new ChangeTextCommand(description) {
			@Override
			protected void setValue(String value) {
				setModelDescription(value);
			}

			@Override
			protected String getValue() {
				return getModelDescription();
			}
		};
	}

	public NodeElement getConnectedNode() {
		if (isIn()) {
			for (Connection c : getIncomings()) {
				return c.getOpposite(this);
			}
		} else {
			for (Connection c : getOutgoings()) {
				return c.getOpposite(this);
			}
		}
		return null;
	}

	@Override
	public void collectSiblingDataModelNode(SiblingDataModelTreeElement list, Set<Object> set, Set<Integer> idSet) {
		DataModelNodeUtil.collectSiblingDataModelNode(list, set, idSet, this);
	}

	@Override
	public void validate(ValidateType vtype, boolean edit, List<IStatus> result) {
		ToadValidator.validatePortName(vtype, result, getName());
		ToadValidator.validateModelName(vtype, edit, result, getModelName());
	}
}
