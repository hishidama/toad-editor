package jp.hishidama.eclipse_plugin.toad.model.dialog.section;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.jface.ModifiableTable;
import jp.hishidama.eclipse_plugin.toad.model.dialog.PropertyDialog;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public abstract class KeySection extends PropertySection {
	protected final OperatorNode model;

	protected List<String> titleList = new ArrayList<String>();

	protected ModifiableTable<Row> table;

	protected static class Row {
		public List<String> name = new ArrayList<String>();

		public void set(int i, String s) {
			while (i >= name.size()) {
				name.add("");
			}
			name.set(i, s);
		}

		public String get(int i) {
			if (i < name.size()) {
				return name.get(i);
			}
			return null;
		}
	}

	public KeySection(PropertyDialog dialog, OperatorNode model) {
		super(dialog);
		this.model = model;
	}

	public void createTab(TabFolder tab) {
		final Composite composite = createTabItem(tab, "Key");
		createKeySection(composite);

		tab.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TabFolder tab = (TabFolder) e.getSource();
				TabItem item = tab.getItem(tab.getSelectionIndex());
				if (item.getControl() == composite) {
					buildKeyTable();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
	}

	private void createKeySection(Composite composite) {
		Label label = new Label(composite, SWT.NONE);
		label.setText(model.getKeyTitle());

		titleList = getTitle();

		table = createKeyTable(composite);
	}

	protected abstract List<String> getTitle();

	private ModifiableTable<Row> createKeyTable(Composite composite) {
		Composite pane = new Composite(composite, SWT.NONE);
		pane.setLayoutData(new GridData(GridData.FILL_BOTH));
		pane.setLayout(new GridLayout(1, false));

		ModifiableTable<Row> table = new ModifiableTable<Row>(pane, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI) {
			@Override
			protected String getText(Row element, int columnIndex) {
				if (columnIndex < element.name.size()) {
					return element.name.get(columnIndex);
				}
				return "";
			}

			@Override
			protected Row createElement() {
				return KeySection.this.createElement();
			}

			@Override
			protected void editElement(Row element) {
				KeySection.this.editElement(element);
			}

			@Override
			public void refresh() {
				super.refresh();
				KeySection.this.refreshTable();
			}
		};
		for (String name : titleList) {
			table.addColumn(name, 128, SWT.NONE);
		}

		Composite field = new Composite(pane, SWT.NONE);
		// field.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		field.setLayout(new FillLayout(SWT.HORIZONTAL));
		createButtonArea(table, field);

		return table;
	}

	protected void createButtonArea(ModifiableTable<Row> table, Composite field) {
		table.createButtonArea(field);
	}

	private boolean builded = false;

	protected void buildKeyTable() {
		if (builded) {
			return;
		}
		builded = true;

		rebuildKeyTable(null);
	}

	protected void rebuildKeyTable(String modelName) {
		List<Row> rowList = buildKey(modelName);

		table.removeAll();
		for (Row row : rowList) {
			table.addItem(row);
		}
		table.refresh();
	}

	protected abstract List<Row> buildKey(String modelName);

	Row createElement() {
		Row element = new Row();
		for (int i = 0; i < titleList.size(); i++) {
			element.name.add("");
		}

		if (editElement(element)) {
			return element;
		}
		return null;
	}

	protected boolean editElement(Row element) {
		return false; // do override
	}

	protected void refreshTable() {
		// do override
	}
}
