package jp.hishidama.eclipse_plugin.toad.model.property.attribute;

import java.util.List;

import jp.hishidama.eclipse_plugin.toad.model.node.Attribute;
import jp.hishidama.eclipse_plugin.toad.model.property.EditDialog;

import org.eclipse.swt.widgets.Shell;

public abstract class AttributeDialog extends EditDialog {

	protected Attribute attribute;
	protected String annotationName;
	protected String parameterName;
	protected String valueType;

	public AttributeDialog(Shell parentShell, String windowTitle) {
		super(parentShell, windowTitle);
	}

	public void setAttribute(Attribute attr) {
		this.attribute = attr;

		annotationName = attr.getAnnotationName();
		parameterName = attr.getParameterName();
		valueType = attr.getValueType();
	}

	public abstract List<String> getValue();
}
