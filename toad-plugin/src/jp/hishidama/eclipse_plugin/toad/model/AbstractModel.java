package jp.hishidama.eclipse_plugin.toad.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.validation.ValidateType;
import jp.hishidama.eclipse_plugin.util.ToadCommandUtil;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

import com.google.gson.annotations.Expose;

/**
 * 『Eclipse 3.4 プラグイン開発 徹底攻略』p.436
 */
@SuppressWarnings("serial")
public abstract class AbstractModel implements Serializable {
	public static final String PROP_MEMO = "memo";

	private PropertyChangeSupport support;

	private EditPart editPart;
	@Expose
	private String memo;

	public AbstractModel() {
		support = new PropertyChangeSupport(this);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		support.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		support.removePropertyChangeListener(listener);
	}

	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		support.firePropertyChange(propertyName, oldValue, newValue);
	}

	protected void firePropertyChange(String propertyName, int oldValue, int newValue) {
		support.firePropertyChange(propertyName, oldValue, newValue);
	}

	protected void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
		support.firePropertyChange(propertyName, oldValue, newValue);
	}

	public abstract AbstractModel cloneEdit();

	public void copyFrom(AbstractModel from) {
		this.memo = from.memo;
	}

	public Command getCommand(ToadEditor editor, CompoundCommand compound, AbstractModel from) {
		ToadCommandUtil.add(compound, getMemoCommand(from.getMemo()));
		return compound;
	}

	public abstract String getIdString();

	public void setEditPart(EditPart editPart) {
		this.editPart = editPart;
	}

	public EditPart getEditPart() {
		return editPart;
	}

	public String getMemo() {
		if (memo == null) {
			return "";
		}
		return memo;
	}

	public void setMemo(String memo) {
		String old = this.memo;
		this.memo = memo;
		firePropertyChange(PROP_MEMO, old, memo);
	}

	public ChangeTextCommand getMemoCommand(String memo) {
		return new ChangeTextCommand(memo) {
			@Override
			protected void setValue(String value) {
				setMemo(value);
			}

			@Override
			protected String getValue() {
				return getMemo();
			}
		};
	}

	public static abstract class ChangeTextCommand extends Command {
		private String value, old;

		public ChangeTextCommand(String value) {
			setNewValue(value);
			this.old = getValue();
		}

		public void setNewValue(String value) {
			this.value = value;
		}

		public String getOldValue() {
			return old;
		}

		@Override
		public boolean canExecute() {
			return value != null && !value.equals(old);
		}

		@Override
		public void execute() {
			setValue(value);
		}

		@Override
		public void undo() {
			setValue(old);
		}

		protected abstract String getValue();

		protected abstract void setValue(String value);
	}

	public static abstract class ChangeIntCommand extends Command {
		private int value, old;

		public ChangeIntCommand(int value) {
			setNewValue(value);
			this.old = getValue();
		}

		public void setNewValue(int value) {
			this.value = value;
		}

		@Override
		public boolean canExecute() {
			return value != old;
		}

		@Override
		public void execute() {
			setValue(value);
		}

		@Override
		public void undo() {
			setValue(old);
		}

		protected abstract int getValue();

		protected abstract void setValue(int value);
	}

	public static abstract class ChangeBooleanCommand extends Command {
		private boolean value, old;

		public ChangeBooleanCommand(boolean value) {
			setNewValue(value);
			this.old = getValue();
		}

		public void setNewValue(boolean value) {
			this.value = value;
		}

		@Override
		public boolean canExecute() {
			return value != old;
		}

		@Override
		public void execute() {
			setValue(value);
		}

		@Override
		public void undo() {
			setValue(old);
		}

		protected abstract boolean getValue();

		protected abstract void setValue(boolean value);
	}

	public static abstract class ChangeListCommand<C> extends Command {
		private List<C> value, old;

		public ChangeListCommand(List<C> value) {
			setNewValue(value);
			this.old = getValue();
		}

		public void setNewValue(List<C> value) {
			this.value = value;
		}

		public List<C> getOldValue() {
			return old;
		}

		@Override
		public boolean canExecute() {
			return value != null && !value.equals(old);
		}

		@Override
		public void execute() {
			setValue(value);
		}

		@Override
		public void undo() {
			setValue(old);
		}

		protected abstract List<C> getValue();

		protected abstract void setValue(List<C> value);
	}

	public static abstract class ChangeMapCommand extends Command {
		private Map<String, String> value, old;

		public ChangeMapCommand(Map<String, String> value) {
			setNewValue(value);
			this.old = getValue();
		}

		public void setNewValue(Map<String, String> value) {
			this.value = value;
		}

		public Map<String, String> getOldValue() {
			return old;
		}

		@Override
		public boolean canExecute() {
			return value != null && !value.equals(old);
		}

		@Override
		public void execute() {
			setValue(value);
		}

		@Override
		public void undo() {
			setValue(old);
		}

		protected abstract Map<String, String> getValue();

		protected abstract void setValue(Map<String, String> value);
	}

	public void validate(ValidateType vtype, boolean edit, List<IStatus> result) {
		// TODO abstractメソッド化
	}

	public abstract String getDisplayLocation();
}
