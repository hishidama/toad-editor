package jp.hishidama.eclipse_plugin.toad.model.property.attribute;

import java.util.Arrays;
import java.util.List;

import jp.hishidama.eclipse_plugin.toad.model.node.Attribute;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class MasterSelectionDialog extends AttributeDialog {

	private Text valueText;
	private String value;

	public MasterSelectionDialog(Shell parentShell, Attribute attr) {
		super(parentShell, "マスター選択属性編集");
		setAttribute(attr);
	}

	@Override
	public void setAttribute(Attribute attr) {
		super.setAttribute(attr);

		List<String> list = attr.getValue();
		if (list == null || list.isEmpty()) {
			this.value = null;
		} else {
			this.value = list.get(0);
		}
	}

	@Override
	protected void createFields(Composite composite) {
		Text nameText = createTextField(composite, "annotation");
		nameText.setText(nonNull(annotationName));
		nameText.setEditable(false);

		valueText = createTextField(composite, parameterName);
	}

	@Override
	protected void refresh() {
		valueText.setText(nonNull(value));
	}

	@Override
	protected boolean validate() {
		this.value = valueText.getText();
		return true;
	}

	@Override
	public List<String> getValue() {
		return Arrays.asList(value);
	}
}
