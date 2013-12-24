package jp.hishidama.eclipse_plugin.toad.model.property.attribute;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.toad.model.node.Attribute;
import jp.hishidama.xtext.dmdl_editor.dmdl.Property;
import jp.hishidama.xtext.dmdl_editor.dmdl.PropertyUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class KeyGroupDialog extends KeyDialog {

	public KeyGroupDialog(Shell parentShell, IProject project, String modelName, Attribute attr) {
		super(parentShell, "キー属性編集", project, modelName);
		setAttribute(attr);
	}

	@Override
	protected List<String> getValueFromAttribute(Attribute attr) {
		return new ArrayList<String>(attr.getValue());
	}

	@Override
	protected void createTableColumns(Table table) {
		{
			TableColumn col = new TableColumn(table, SWT.NONE);
			col.setText("property name");
			col.setWidth(128 + 32);
		}
		{
			TableColumn col = new TableColumn(table, SWT.NONE);
			col.setText("description");
			col.setWidth(128);
		}
		{
			TableColumn col = new TableColumn(table, SWT.NONE);
			col.setText("data type");
			col.setWidth(128 - 32);
		}
	}

	@Override
	protected TableItem createTableItem(Table table, Property p) {
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(0, p.getName());
		item.setText(1, PropertyUtil.getDecodedDescriptionText(p));
		item.setText(2, PropertyUtil.getResolvedDataTypeText(p));

		return item;
	}

	@Override
	public List<String> getValue() {
		return value;
	}
}
