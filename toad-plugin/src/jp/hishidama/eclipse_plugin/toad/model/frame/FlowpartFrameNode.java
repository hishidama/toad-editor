package jp.hishidama.eclipse_plugin.toad.model.frame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.hishidama.eclipse_plugin.dialog.ClassSelectionAnnotationFilter;
import jp.hishidama.eclipse_plugin.dialog.ClassSelectionDialog.Filter;
import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.model.AbstractModel;
import jp.hishidama.eclipse_plugin.toad.validation.ToadValidator;
import jp.hishidama.eclipse_plugin.toad.validation.ValidateType;
import jp.hishidama.eclipse_plugin.util.ToadCommandUtil;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

import com.google.gson.annotations.Expose;

public class FlowpartFrameNode extends FrameNode {
	private static final long serialVersionUID = -1081533365415696798L;

	@Expose
	private List<FlowpartParameterDef> parameterList;

	public FlowpartFrameNode() {
		setType("FlowPartFrame");
		setWidth(80);
		setHeight(48);
	}

	@Override
	public FlowpartFrameNode cloneEdit() {
		FlowpartFrameNode to = new FlowpartFrameNode();
		to.copyFrom(this);
		return to;
	}

	@Override
	public void copyFrom(AbstractModel fromModel) {
		super.copyFrom(fromModel);

		FlowpartFrameNode from = (FlowpartFrameNode) fromModel;
		for (FlowpartParameterDef param : from.getParameterList()) {
			addParameter(new FlowpartParameterDef(param));
		}
	}

	@Override
	public Command getCommand(ToadEditor editor, CompoundCommand compound, AbstractModel fromModel) {
		super.getCommand(editor, compound, fromModel);

		FlowpartFrameNode from = (FlowpartFrameNode) fromModel;
		ToadCommandUtil.add(compound, getParameterListCommand(from.getParameterList()));

		return compound;
	}

	@Override
	public int getHorizontalMargin() {
		return 32;
	}

	@Override
	public String getToadFileExtension() {
		return "ftoad";
	}

	@Override
	public String getClassNamePattern() {
		return "*FlowPart";
	}

	@Override
	public Filter getClassNameFilter() {
		return new ClassSelectionAnnotationFilter("com.asakusafw.vocabulary.flow.FlowPart");
	}

	public List<FlowpartParameterDef> getParameterList() {
		if (parameterList == null) {
			return Collections.emptyList();
		}
		return parameterList;
	}

	public void setParameterList(List<FlowpartParameterDef> list) {
		parameterList = null;
		for (FlowpartParameterDef param : list) {
			addParameter(param);
		}
	}

	public void addParameter(FlowpartParameterDef param) {
		addParameter(-1, param);
	}

	public void addParameter(int index, FlowpartParameterDef param) {
		param.setParent(this);
		if (parameterList == null) {
			parameterList = new ArrayList<FlowpartParameterDef>();
		}
		if (index < 0) {
			parameterList.add(param);
		} else {
			parameterList.add(index, param);
		}
		fireParameterChange();
	}

	public void removeParameter(FlowpartParameterDef param) {
		param.setParent(null);
		if (parameterList == null) {
			return;
		}
		parameterList.remove(param);
		fireParameterChange();
	}

	public void moveParameter(FlowpartParameterDef param, int index) {
		if (parameterList == null) {
			return;
		}
		parameterList.remove(param);
		parameterList.add(index, param);
		fireParameterChange();
	}

	public ChangeListCommand<FlowpartParameterDef> getParameterListCommand(List<FlowpartParameterDef> list) {
		return new ChangeListCommand<FlowpartParameterDef>(list) {
			@Override
			protected List<FlowpartParameterDef> getValue() {
				return getParameterList();
			}

			@Override
			protected void setValue(List<FlowpartParameterDef> value) {
				parameterList = null;
				for (FlowpartParameterDef param : value) {
					addParameter(param);
				}
			}
		};
	}

	public void fireParameterChange() {
		firePropertyChange(PROP_PARAMETER, null, null);
	}

	@Override
	public void validate(ValidateType vtype, boolean edit, List<IStatus> result) {
		ToadValidator.validateDescription(vtype, result, "フローパート", getDescription());
		ToadValidator.validatePorts(vtype, result, this);
	}

	@Override
	public String getDisplayLocation() {
		return String.format("FlowpartFrame(%s)", getDisplayName());
	}
}
