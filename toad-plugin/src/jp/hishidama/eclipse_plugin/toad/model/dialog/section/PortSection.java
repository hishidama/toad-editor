package jp.hishidama.eclipse_plugin.toad.model.dialog.section;

import jp.hishidama.eclipse_plugin.jface.ModifiableTable;
import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.model.dialog.PortPropertyDialog;
import jp.hishidama.eclipse_plugin.toad.model.dialog.PropertyDialog;
import jp.hishidama.eclipse_plugin.toad.model.node.port.BasePort;
import jp.hishidama.eclipse_plugin.toad.model.property.port.HasPortNode;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;

public abstract class PortSection<C extends BasePort> extends PropertySection {
	private ToadEditor editor;
	private HasPortNode<C> model;

	private ModifiableTable<C> inTable;
	private ModifiableTable<C> outTable;

	public PortSection(ToadEditor editor, PropertyDialog dialog, HasPortNode<C> model) {
		super(dialog);
		this.editor = editor;
		this.model = model;
	}

	public void createTab(TabFolder tab) {
		Composite composite = createTabItem(tab, "Port");
		createPortSection(composite);
	}

	private void createPortSection(Composite composite) {
		Label label = new Label(composite, SWT.NONE);
		label.setText("port");

		inTable = createPortTable(composite, true);
		outTable = createPortTable(composite, false);

		for (C port : model.getPorts()) {
			ModifiableTable<C> table = port.isIn() ? inTable : outTable;
			addToTable(table, port);
		}
		inTable.refresh();
		outTable.refresh();
	}

	private ModifiableTable<C> createPortTable(Composite composite, final boolean in) {
		Composite pane = new Composite(composite, SWT.NONE);
		pane.setLayoutData(new GridData(GridData.FILL_BOTH));
		pane.setLayout(new GridLayout(1, false));

		Label label = new Label(pane, SWT.NONE);
		label.setText(in ? "in" : "out");

		ModifiableTable<C> table = new ModifiableTable<C>(pane, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI) {
			@Override
			protected String getText(C element, int columnIndex) {
				switch (columnIndex) {
				case 0:
					return element.getRole();
				case 1:
					return element.getName();
				case 2:
					return element.getDescription();
				case 3:
					return element.getModelName();
				default:
					throw new UnsupportedOperationException("index=" + columnIndex);
				}
			}

			@Override
			protected C createElement() {
				return PortSection.this.createElement(in);
			}

			@Override
			protected void editElement(C element) {
				PortSection.this.editElement(in, element);
			}

			@Override
			public void refresh() {
				super.refresh();
				model.setPorts(in, getElementList());
				dialog.doValidate();
			}
		};
		table.addColumn("role", 64, SWT.NONE);
		table.addColumn("name", 128, SWT.NONE);
		table.addColumn("description", 128, SWT.NONE);
		table.addColumn("data model", 128, SWT.NONE);

		Composite field = new Composite(pane, SWT.NONE);
		// field.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		field.setLayout(new FillLayout(SWT.HORIZONTAL));
		table.createButtonArea(field);

		return table;
	}

	private void addToTable(ModifiableTable<C> table, C port) {
		table.addItem(port);
	}

	C createElement(boolean in) {
		C element = createPort(in);
		if (editElement(in, element)) {
			if (in) {
				element.setCx(model.getX());
			} else {
				element.setCx(model.getX() + model.getWidth());
			}
			// cyはNodeElementに登録するときに計算する
			// @see NodeElement#calculatePortY()

			return element;
		}
		return null;
	}

	boolean editElement(boolean in, C port) {
		BasePort dummy = port.cloneEdit();
		PortPropertyDialog dialog = new PortPropertyDialog(editor, dummy, port.getParent(), true);
		if (dialog.open() != Window.OK) {
			return false;
		}

		port.copyFrom(dummy);
		return true;
	}

	protected abstract C createPort(boolean in);
}
