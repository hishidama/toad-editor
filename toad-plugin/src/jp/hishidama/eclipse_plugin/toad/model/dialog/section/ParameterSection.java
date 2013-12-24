package jp.hishidama.eclipse_plugin.toad.model.dialog.section;

import jp.hishidama.eclipse_plugin.jface.ModifiableTable;
import jp.hishidama.eclipse_plugin.toad.model.dialog.PropertyDialog;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OpeParameter;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;
import jp.hishidama.eclipse_plugin.toad.model.property.operator.OpeParameterDialog;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;

public class ParameterSection extends PropertySection {
	private OperatorNode model;

	private ModifiableTable<OpeParameter> table;

	public ParameterSection(PropertyDialog dialog, OperatorNode model) {
		super(dialog);
		this.model = model;
	}

	public void createTab(TabFolder tab) {
		Composite composite = createTabItem(tab, "Parameter");
		createParameterSection(composite);
	}

	private void createParameterSection(Composite composite) {
		Label label = new Label(composite, SWT.NONE);
		label.setText("parameter");

		table = createParameterTable(composite);

		for (OpeParameter param : model.getParameterList()) {
			addToTable(table, param);
		}
		table.refresh();
	}

	private ModifiableTable<OpeParameter> createParameterTable(Composite composite) {
		Composite pane = new Composite(composite, SWT.NONE);
		pane.setLayoutData(new GridData(GridData.FILL_BOTH));
		pane.setLayout(new GridLayout(1, false));

		ModifiableTable<OpeParameter> table = new ModifiableTable<OpeParameter>(pane, SWT.BORDER | SWT.FULL_SELECTION
				| SWT.MULTI) {
			@Override
			protected String getText(OpeParameter element, int columnIndex) {
				switch (columnIndex) {
				case 0:
					return element.getDescription();
				case 1:
					return element.getName();
				case 2:
					return element.getClassName();
				case 3:
					return element.getValue();
				default:
					throw new UnsupportedOperationException("index=" + columnIndex);
				}
			}

			@Override
			protected OpeParameter createElement() {
				return ParameterSection.this.createElement();
			}

			@Override
			protected void editElement(OpeParameter element) {
				ParameterSection.this.editElement(element);
			}

			@Override
			public void refresh() {
				super.refresh();
				model.setParameterList(getElementList());
				dialog.doValidate();
			}
		};
		table.addColumn("description", 128, SWT.NONE);
		table.addColumn("name", 128, SWT.NONE);
		table.addColumn("class name", 128, SWT.NONE);
		table.addColumn("value", 128, SWT.NONE);

		Composite field = new Composite(pane, SWT.NONE);
		// field.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		field.setLayout(new FillLayout(SWT.HORIZONTAL));
		table.createButtonArea(field);

		return table;
	}

	private void addToTable(ModifiableTable<OpeParameter> table, OpeParameter element) {
		table.addItem(element);
	}

	OpeParameter createElement() {
		OpeParameter element = new OpeParameter();
		if (editElement(element)) {
			return element;
		}
		return null;
	}

	boolean editElement(OpeParameter element) {
		OpeParameterDialog dialog = new OpeParameterDialog(getShell(), element);
		if (dialog.open() != Window.OK) {
			return false;
		}

		element.setName(dialog.getName());
		element.setClassName(dialog.getClassName());
		element.setValue(dialog.getValue());
		element.setDescription(dialog.getDescription());
		return true;
	}
}
