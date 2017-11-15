package jp.hishidama.eclipse_plugin.toad.model.property.datamodel;

import java.text.MessageFormat;

import jp.hishidama.eclipse_plugin.toad.Activator;
import jp.hishidama.eclipse_plugin.toad.model.property.TextSection;
import jp.hishidama.xtext.dmdl_editor.dmdl.ModelUiUtil;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionEvent;

public class DataModelNameSection extends TextSection<HasDataModelNode> {

	public DataModelNameSection() {
		super("name");
	}

	@Override
	protected String getValue(HasDataModelNode model) {
		return model.getModelName();
	}

	@Override
	protected String getButtonText() {
		return "open";
	}

	@Override
	protected void buttonEvent(HasDataModelNode model, SelectionEvent event) {
		String modelName = getValue(model);
		try {
			if (!ModelUiUtil.openEditor(getProject(), modelName)) {
				MessageDialog.openError(null, "open error",
						MessageFormat.format("DataModel not found. name={0}", modelName));
			}
		} catch (Exception e) {
			IStatus status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, "file open error", e);
			e.printStackTrace();
			ErrorDialog.openError(null, "open error", MessageFormat.format("open error. data_model={0}", modelName),
					status);
		}
	}
}
