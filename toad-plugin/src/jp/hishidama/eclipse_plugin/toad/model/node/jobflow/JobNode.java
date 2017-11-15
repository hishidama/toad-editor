package jp.hishidama.eclipse_plugin.toad.model.node.jobflow;

import java.util.List;

import jp.hishidama.eclipse_plugin.dialog.ClassSelectionAnnotationFilter;
import jp.hishidama.eclipse_plugin.dialog.ClassSelectionDialog.Filter;
import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.model.AbstractModel;
import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;
import jp.hishidama.eclipse_plugin.toad.model.node.ClassNameNode;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.RectangleNode;
import jp.hishidama.eclipse_plugin.toad.model.property.generic.NameNode;
import jp.hishidama.eclipse_plugin.toad.validation.ToadValidator;
import jp.hishidama.eclipse_plugin.toad.validation.ValidateType;
import jp.hishidama.eclipse_plugin.util.StringUtil;
import jp.hishidama.eclipse_plugin.util.ToadCommandUtil;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

import com.google.gson.annotations.Expose;

public class JobNode extends RectangleNode implements NameNode, ClassNameNode {
	private static final long serialVersionUID = -2377266593726665028L;

	@Expose
	private String name;
	@Expose
	private String className;

	public JobNode() {
		setType("Jobflow");
		setWidth(80);
		setHeight(48);
	}

	@Override
	public JobNode cloneEdit() {
		JobNode to = new JobNode();
		to.copyFrom(this);
		return to;
	}

	@Override
	public void copyFrom(AbstractModel fromModel) {
		super.copyFrom(fromModel);

		JobNode from = (JobNode) fromModel;
		this.name = from.name;
		this.className = from.className;
	}

	@Override
	public Command getCommand(ToadEditor editor, CompoundCommand compound, AbstractModel fromModel) {
		super.getCommand(editor, compound, fromModel);

		JobNode from = (JobNode) fromModel;
		ToadCommandUtil.add(compound, getNameCommand(from.getName()));
		ToadCommandUtil.add(compound, getClassNameCommand(from.getClassName()));

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
	public String getSimpleDisplayName() {
		String desc = getDescription();
		if (StringUtil.nonEmpty(desc)) {
			return desc;
		}
		return getName();
	}

	@Override
	public String getFigureLabel() {
		return getName();
	}

	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public void setClassName(String className) {
		String old = this.className;
		this.className = className;
		firePropertyChange(PROP_CLASS_NAME, old, className);
	}

	@Override
	public ChangeTextCommand getClassNameCommand(String className) {
		return new ChangeTextCommand(className) {
			@Override
			protected void setValue(String value) {
				setClassName(value);
			}

			@Override
			protected String getValue() {
				return getClassName();
			}
		};
	}

	@Override
	public String getClassNamePattern() {
		return "*Job";
	}

	@Override
	public Filter getClassNameFilter() {
		return new ClassSelectionAnnotationFilter("com.asakusafw.vocabulary.flow.JobFlow");
	}

	@Override
	public boolean hasToadFile() {
		return true;
	}

	@Override
	public String getToadFileExtension() {
		return "jtoad";
	}

	@Override
	public boolean canConnectTo(Connection connection, NodeElement target) {
		return target instanceof JobNode;
	}

	@Override
	public boolean canConnectFrom(Connection connection, NodeElement source) {
		return source instanceof JobNode;
	}

	@Override
	public void validate(ValidateType vtype, boolean edit, List<IStatus> result) {
		ToadValidator.validateJobName(vtype, result, getName());
		ToadValidator.validateDescription(vtype, result, "ジョブフロー", getDescription());
	}

	@Override
	public String getDisplayLocation() {
		return String.format("JobFlow(%s)", getDisplayName());
	}
}
