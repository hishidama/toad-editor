package jp.hishidama.eclipse_plugin.toad.model.property.attribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hishidama.eclipse_plugin.toad.model.node.Attribute;
import jp.hishidama.eclipse_plugin.util.StringUtil;
import jp.hishidama.xtext.dmdl_editor.dmdl.ModelUiUtil;
import jp.hishidama.xtext.dmdl_editor.dmdl.ModelUtil.PropertyFilter;
import jp.hishidama.xtext.dmdl_editor.dmdl.Property;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public abstract class KeyDialog extends AttributeDialog {
	protected final IProject project;
	protected final String modelName;
	protected List<String> value;

	protected Table table;
	protected List<Property> propertyList;
	protected List<String> originalList;

	public KeyDialog(Shell parentShell, String windowTitle, IProject project, String modelName) {
		super(parentShell, windowTitle);
		this.project = project;
		this.modelName = modelName;
	}

	public void setAttribute(Attribute attr) {
		super.setAttribute(attr);
		this.value = getValueFromAttribute(attr);
	}

	protected abstract List<String> getValueFromAttribute(Attribute attr);

	@Override
	protected void createFields(Composite composite) {
		Text nameText = createTextField(composite, "name");
		nameText.setText(annotationName);
		nameText.setEditable(false);
		Text modelText = createTextField(composite, "data model");
		modelText.setText(nonNull(modelName));
		modelText.setEditable(false);

		table = createCheckedTable(composite, parameterName);
		createTableColumns(table);

		propertyList = ModelUiUtil.getProperties(project, modelName, PropertyFilter.ALL);
		if (propertyList == null) {
			propertyList = Collections.emptyList();
		}
		originalList = new ArrayList<String>(propertyList.size());
		for (Property p : propertyList) {
			originalList.add(p.getName());
		}
		{
			Map<String, String> map = new HashMap<String, String>();
			for (String s : originalList) {
				map.put(StringUtil.toSmallCamelCase(s), s);
			}
			for (int i = 0; i < value.size(); i++) {
				String s = value.get(i);
				if (!propertyList.contains(s)) {
					String t = map.get(s);
					if (t != null) {
						value.set(i, t);
					}
				}
			}
		}
		Collections.sort(propertyList, new Comparator<Property>() {
			@Override
			public int compare(Property o1, Property o2) {
				String name1 = o1.getName();
				String name2 = o2.getName();
				int n1 = value.indexOf(name1);
				int n2 = value.indexOf(name2);
				if (n1 < 0) {
					n1 = value.size() + originalList.indexOf(name1);
				}
				if (n2 < 0) {
					n2 = value.size() + originalList.indexOf(name2);
				}
				return n1 - n2;
			}
		});
		refresh();

		{ // button
			createLabel(composite, ""); // dummy for gird0
			Composite field = new Composite(composite, SWT.NONE); // grid1
			field.setLayout(new RowLayout(SWT.HORIZONTAL));
			createButtonField(field);
		}
	}

	protected abstract void createTableColumns(Table table);

	protected void createButtonField(Composite field) {
		{
			Button button = new Button(field, SWT.PUSH);
			button.setText("up");
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					move(-1);
				}
			});
		}
		{
			Button button = new Button(field, SWT.PUSH);
			button.setText("down");
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					move(+1);
				}
			});
		}
	}

	private void move(int z) {
		int n = table.getSelectionIndex();
		if (n < 0) {
			return;
		}
		int m = n + z;
		if (m < 0 || m >= propertyList.size()) {
			return;
		}
		setValue();

		Property p = propertyList.remove(n);
		propertyList.add(m, p);
		refresh();
		setValue();
		table.setSelection(m);
	}

	private void setValue() {
		value = new ArrayList<String>();
		for (TableItem item : table.getItems()) {
			if (item.getChecked()) {
				value.add(item.getText(0));
			}
		}
	}

	@Override
	protected void refresh() {
		table.removeAll();
		for (Property p : propertyList) {
			TableItem item = createTableItem(table, p);
			if (value.contains(p.getName())) {
				item.setChecked(true);
			}
		}
	}

	protected abstract TableItem createTableItem(Table table, Property p);

	@Override
	protected boolean validate() {
		setValue();
		return true;
	}
}
