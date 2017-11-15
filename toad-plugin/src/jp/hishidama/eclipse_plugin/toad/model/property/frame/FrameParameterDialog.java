package jp.hishidama.eclipse_plugin.toad.model.property.frame;

import jp.hishidama.eclipse_plugin.toad.model.frame.FlowpartParameterDef;
import jp.hishidama.eclipse_plugin.toad.model.property.EditDialog;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class FrameParameterDialog extends EditDialog {

	private FlowpartParameterDef parameter;

	private Text descText;
	private Text nameText;
	private Text classNameText;

	private String description;
	private String name;
	private String className;

	public FrameParameterDialog(Shell parentShell, FlowpartParameterDef param) {
		super(parentShell, "パラメーター編集");
		this.parameter = param;
	}

	@Override
	protected void createFields(Composite composite) {
		descText = createTextField(composite, "description");
		nameText = createTextField(composite, "name");
		classNameText = createTextField(composite, "class name");
	}

	@Override
	protected void refresh() {
		String desc = "";
		String name = "";
		String className = "java.lang.Object";
		if (parameter != null) {
			desc = parameter.getDescription();
			name = parameter.getName();
			className = parameter.getClassName();
		}
		descText.setText(nonNull(desc));
		nameText.setText(nonNull(name));
		classNameText.setText(nonNull(className));
	}

	@Override
	protected boolean validate() {
		description = descText.getText();
		name = nameText.getText();
		className = classNameText.getText();
		if (name.isEmpty() || className.isEmpty()) {
			return false;
		}
		return true;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public String getClassName() {
		return className;
	}
}
