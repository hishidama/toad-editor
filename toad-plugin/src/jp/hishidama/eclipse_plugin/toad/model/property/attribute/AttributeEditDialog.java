package jp.hishidama.eclipse_plugin.toad.model.property.attribute;

import static jp.hishidama.eclipse_plugin.util.StringUtil.isEmpty;
import static jp.hishidama.eclipse_plugin.util.StringUtil.mkString;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.toad.model.node.Attribute;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AttributeEditDialog extends AttributeDialog {

	private Text annotationText;
	private Text parameterText;
	private Text typeText;
	private Text valueText;

	private String value;

	public AttributeEditDialog(Shell parentShell, Attribute attr) {
		super(parentShell, "属性編集");
		setAttribute(attr);
	}

	@Override
	protected void createFields(Composite composite) {
		annotationText = createTextField(composite, "annotation");
		parameterText = createTextField(composite, "name");
		typeText = createTextField(composite, "type");
		valueText = createTextField(composite, "values");
	}

	@Override
	protected void refresh() {
		annotationText.setText(nonNull(attribute.getAnnotationName()));
		parameterText.setText(nonNull(attribute.getParameterName()));
		typeText.setText(nonNull(attribute.getValueType()));
		valueText.setText(nonNull(mkString(attribute.getValue())));
	}

	@Override
	protected boolean validate() {
		this.annotationName = annotationText.getText();
		this.parameterName = parameterText.getText();
		this.valueType = typeText.getText();
		this.value = valueText.getText();

		if (isEmpty(annotationName)) {
			return false;
		}
		// if (isEmpty(parameterName)) {
		// return false;
		// }
		if (isEmpty(valueType)) {
			return false;
		}

		return true;
	}

	public String getAnnotationName() {
		return annotationName;
	}

	public String getParameterName() {
		return parameterName;
	}

	public String getValueType() {
		return valueType;
	}

	@Override
	public List<String> getValue() {
		String[] ss = value.split(",");
		List<String> list = new ArrayList<String>(ss.length);
		for (String s : ss) {
			list.add(s.trim());
		}
		return list;
	}
}
