package jp.hishidama.eclipse_plugin.toad.model;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.util.ToadCommandUtil;

import com.google.gson.annotations.Expose;

@SuppressWarnings("serial")
public abstract class AbstractNameModel extends AbstractModel {

	public static final String PROP_TYPE = "type";
	public static final String PROP_DESCRIPTION = "description";

	@Expose
	private String type;
	@Expose
	private String description;

	@Override
	public void copyFrom(AbstractModel fromModel) {
		super.copyFrom(fromModel);

		AbstractNameModel from = (AbstractNameModel) fromModel;
		this.type = from.type;
		this.description = from.description;
	}

	@Override
	public Command getCommand(ToadEditor editor, CompoundCommand compound, AbstractModel fromModel) {
		super.getCommand(editor, compound, fromModel);

		AbstractNameModel from = (AbstractNameModel) fromModel;
		ToadCommandUtil.add(compound, getTypeCommand(from.getType()));
		ToadCommandUtil.add(compound, getDescriptionCommand(from.getDescription()));

		return compound;
	}

	public String getType() {
		if (type == null) {
			return "";
		}
		return type;
	}

	public void setType(String type) {
		String old = this.type;
		this.type = type;
		firePropertyChange(PROP_TYPE, old, type);
	}

	public ChangeTextCommand getTypeCommand(String type) {
		return new ChangeTextCommand(type) {
			@Override
			protected void setValue(String value) {
				setType(value);
			}

			@Override
			protected String getValue() {
				return getType();
			}
		};
	}

	public String getFigureLabel() {
		return getType();
	}

	public String getDescription() {
		if (description == null) {
			return "";
		}
		return description;
	}

	public void setDescription(String description) {
		String old = this.description;
		this.description = description;
		firePropertyChange(PROP_DESCRIPTION, old, description);
	}

	public ChangeTextCommand getDescriptionCommand(String description) {
		return new ChangeTextCommand(description) {
			@Override
			protected void setValue(String value) {
				setDescription(value);
			}

			@Override
			protected String getValue() {
				return getDescription();
			}
		};
	}

	public String getQualifiedDescription() {
		return getDescription();
	}

	public String getNodeDescription() {
		return getClass().getSimpleName();
	}

	public String getDisplayName() {
		return getSimpleDisplayName();
	}

	public String getSimpleDisplayName() {
		return getDescription();
	}

	public String getToolTipInformation() {
		return null;
	}

	@Override
	public final String toString() {
		return String.format("%s(%s)", getNodeDescription(), getDisplayName());
	}
}
