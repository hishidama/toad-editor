package jp.hishidama.eclipse_plugin.toad.model.property.operator;

import jp.hishidama.eclipse_plugin.toad.model.node.operator.OpeParameter;
import jp.hishidama.eclipse_plugin.toad.model.property.EditDialog;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class OpeParameterDialog extends EditDialog {

	private OpeParameter parameter;

	private Text descText;
	private Text nameText;
	private Text classNameText;
	private Text valueText;

	private String description;
	private String name;
	private String className;
	private String value;

	public OpeParameterDialog(Shell parentShell, OpeParameter param) {
		super(parentShell, "パラメーター編集");
		this.parameter = param;
	}

	@Override
	protected void createFields(Composite composite) {
		descText = createTextField(composite, "description");
		nameText = createTextField(composite, "name");
		classNameText = createTextField(composite, "class name");
		valueText = createTextField(composite, "value");
	}

	@Override
	protected void refresh() {
		String desc = "";
		String name = "";
		String className = "java.lang.Object";
		String value = "";
		if (parameter != null) {
			desc = parameter.getDescription();
			name = parameter.getName();
			className = parameter.getClassName();
			value = parameter.getValue();
		}
		descText.setText(nonNull(desc));
		nameText.setText(nonNull(name));
		classNameText.setText(nonNull(className));
		valueText.setText(nonNull(value));
	}

	@Override
	protected boolean validate() {
		description = descText.getText();
		name = nameText.getText();
		className = classNameText.getText();
		value = valueText.getText();
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

	public String getValue() {
		return value;
	}
}
