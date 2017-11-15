package jp.hishidama.eclipse_plugin.toad.model.frame;

import java.util.List;

import jp.hishidama.eclipse_plugin.dialog.ClassSelectionAnnotationFilter;
import jp.hishidama.eclipse_plugin.dialog.ClassSelectionDialog.Filter;
import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.model.AbstractModel;
import jp.hishidama.eclipse_plugin.toad.model.property.generic.NameNode;
import jp.hishidama.eclipse_plugin.toad.validation.ToadValidator;
import jp.hishidama.eclipse_plugin.toad.validation.ValidateType;
import jp.hishidama.eclipse_plugin.util.StringUtil;
import jp.hishidama.eclipse_plugin.util.ToadCommandUtil;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

import com.google.gson.annotations.Expose;

public class JobFrameNode extends FrameNode implements NameNode {
	private static final long serialVersionUID = 5947064285891976520L;

	@Expose
	private String name;

	public JobFrameNode() {
		setType("JobFrame");
		setWidth(80);
		setHeight(48);
	}

	@Override
	public JobFrameNode cloneEdit() {
		JobFrameNode to = new JobFrameNode();
		to.copyFrom(this);
		return to;
	}

	@Override
	public void copyFrom(AbstractModel fromModel) {
		super.copyFrom(fromModel);

		JobFrameNode from = (JobFrameNode) fromModel;
		this.name = from.name;
	}

	@Override
	public Command getCommand(ToadEditor editor, CompoundCommand compound, AbstractModel fromModel) {
		super.getCommand(editor, compound, fromModel);

		JobFrameNode from = (JobFrameNode) fromModel;
		ToadCommandUtil.add(compound, getNameCommand(from.getName()));

		return compound;
	}

	@Override
	public int getHorizontalMargin() {
		return 128 + 32;
	}

	@Override
	public String getToadFileExtension() {
		return "jtoad";
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
	public void validate(ValidateType vtype, boolean edit, List<IStatus> result) {
		ToadValidator.validateJobName(vtype, result, getName());
		ToadValidator.validateDescription(vtype, result, "ジョブフロー", getDescription());
		ToadValidator.validatePorts(vtype, result, this);
	}

	@Override
	public String getDisplayLocation() {
		return String.format("JobFrame(%s)", getDisplayName());
	}
}
