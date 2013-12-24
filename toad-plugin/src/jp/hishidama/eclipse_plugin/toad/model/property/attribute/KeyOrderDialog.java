package jp.hishidama.eclipse_plugin.toad.model.property.attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hishidama.eclipse_plugin.toad.model.node.Attribute;
import jp.hishidama.xtext.dmdl_editor.dmdl.Property;
import jp.hishidama.xtext.dmdl_editor.dmdl.PropertyUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class KeyOrderDialog extends KeyDialog {

	private Map<String, String> orderMap = new HashMap<String, String>();

	public KeyOrderDialog(Shell parentShell, IProject project, String modelName, Attribute attr) {
		super(parentShell, "ソートキー属性編集", project, modelName);
		setAttribute(attr);
	}

	@Override
	protected List<String> getValueFromAttribute(Attribute attr) {
		List<String> list = new ArrayList<String>();
		for (String s : attr.getValue()) {
			String name, order;
			if (s.contains("+")) {
				name = s.replaceAll("\\+", "").trim();
				order = "ASC";
			} else if (s.contains("-")) {
				name = s.replaceAll("\\-", "").trim();
				order = "DESC";
			} else {
				String[] ss = s.trim().split("[ \t]");
				if (ss.length <= 1) {
					name = s.trim();
					order = "ASC";
				} else {
					name = ss[0];
					order = ss[1];
				}
			}
			list.add(name);
			orderMap.put(name, order);
		}
		return list;
	}

	@Override
	protected void createFields(Composite composite) {
		super.createFields(composite);
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				for (TableItem item : table.getSelection()) {
					String asc = item.getText(1);
					if ("ASC".equals(asc)) {
						asc = "DESC";
					} else {
						asc = "ASC";
					}
					item.setText(1, asc);
					orderMap.put(item.getText(0), asc);
				}
			}
		});
	}

	@Override
	protected void createButtonField(Composite field) {
		super.createButtonField(field);
		{
			Button button = new Button(field, SWT.PUSH);
			button.setText("ASC");
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					updateAsc("ASC");
				}
			});
		}
		{
			Button button = new Button(field, SWT.PUSH);
			button.setText("DESC");
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					updateAsc("DESC");
				}
			});
		}
	}

	private void updateAsc(String asc) {
		for (TableItem item : table.getSelection()) {
			item.setText(1, asc);
			orderMap.put(item.getText(0), asc);
		}
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
			col.setText("a/d");
			col.setWidth(48);
		}
		{
			TableColumn col = new TableColumn(table, SWT.NONE);
			col.setText("description");
			col.setWidth(128 - 48);
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
		item.setText(1, nonNull(orderMap.get(p.getName())));
		item.setText(2, PropertyUtil.getDecodedDescriptionText(p));
		item.setText(3, PropertyUtil.getResolvedDataTypeText(p));

		return item;
	}

	@Override
	public List<String> getValue() {
		List<String> list = new ArrayList<String>();
		for (String name : value) {
			String asc = orderMap.get(name);
			if (!"DESC".equals(asc)) {
				asc = "ASC";
			}
			list.add(name + " " + asc);
		}
		return list;
	}
}
