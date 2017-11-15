package jp.hishidama.eclipse_plugin.toad.model.dialog.section;

import jp.hishidama.eclipse_plugin.jface.ModifiableTable;
import jp.hishidama.eclipse_plugin.toad.model.dialog.PropertyDialog;
import jp.hishidama.eclipse_plugin.toad.model.node.Attribute;
import jp.hishidama.eclipse_plugin.toad.model.property.attribute.AttributeDialog;
import jp.hishidama.eclipse_plugin.toad.model.property.attribute.AttributeEditDialog;
import jp.hishidama.eclipse_plugin.toad.model.property.attribute.HasAttributeNode;
import jp.hishidama.eclipse_plugin.toad.model.property.attribute.KeyGroupDialog;
import jp.hishidama.eclipse_plugin.toad.model.property.attribute.KeyOrderDialog;
import jp.hishidama.eclipse_plugin.toad.model.property.attribute.MasterSelectionDialog;
import jp.hishidama.eclipse_plugin.toad.model.property.datamodel.HasDataModelNode;
import jp.hishidama.eclipse_plugin.util.StringUtil;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;

public class AttributeSection extends PropertySection {
	private HasAttributeNode model;

	private ModifiableTable<Attribute> table;

	public AttributeSection(PropertyDialog dialog, HasAttributeNode model) {
		super(dialog);
		this.model = model;
	}

	public void createTab(TabFolder tab) {
		Composite composite = createTabItem(tab, "Attribute");
		createAttributeSection(composite);
	}

	private void createAttributeSection(Composite composite) {
		Label label = new Label(composite, SWT.NONE);
		label.setText("attribute");

		table = createAttributeTable(composite);

		for (Attribute attr : model.getAttributeList()) {
			addToTable(table, attr);
		}
		table.refresh();
	}

	private ModifiableTable<Attribute> createAttributeTable(Composite composite) {
		Composite pane = new Composite(composite, SWT.NONE);
		pane.setLayoutData(new GridData(GridData.FILL_BOTH));
		pane.setLayout(new GridLayout(1, false));

		ModifiableTable<Attribute> table = new ModifiableTable<Attribute>(pane, SWT.BORDER | SWT.FULL_SELECTION
				| SWT.MULTI) {
			@Override
			protected String getText(Attribute element, int columnIndex) {
				switch (columnIndex) {
				case 0:
					return element.getAnnotationName();
				case 1:
					return element.getParameterName();
				case 2:
					return element.getValueType();
				case 3:
					return StringUtil.mkString(element.getValue());
				default:
					throw new UnsupportedOperationException("index=" + columnIndex);
				}
			}

			@Override
			protected Attribute createElement() {
				return AttributeSection.this.createElement();
			}

			@Override
			protected void editElement(Attribute element) {
				AttributeSection.this.editElement(element);
			}

			@Override
			public void refresh() {
				super.refresh();
				model.setAttributeList(getElementList());
				dialog.doValidate();
			}
		};
		table.addColumn("annotation", 256 + 48, SWT.NONE);
		table.addColumn("name", 128, SWT.NONE);
		table.addColumn("type", 128, SWT.NONE);
		table.addColumn("values", 256, SWT.NONE);

		Composite field = new Composite(pane, SWT.NONE);
		// field.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		field.setLayout(new FillLayout(SWT.HORIZONTAL));
		table.createButtonArea(field);

		return table;
	}

	private void addToTable(ModifiableTable<Attribute> table, Attribute attr) {
		table.addItem(attr);
	}

	Attribute createElement() {
		Attribute element = new Attribute("", "", "java.lang.String");
		if (editElement(element)) {
			return element;
		}
		return null;
	}

	boolean editElement(Attribute element) {
		AttributeDialog dialog = null;
		String aname = element.getAnnotationName();
		if ("com.asakusafw.vocabulary.model.Key".equals(aname)) {
			if (model instanceof HasDataModelNode) {
				HasDataModelNode node = (HasDataModelNode) model;
				String modelName = node.getModelName();
				String name = element.getParameterName();
				if ("group".equals(name)) {
					dialog = new KeyGroupDialog(getShell(), getProject(), modelName, element);
				} else if ("order".equals(name)) {
					dialog = new KeyOrderDialog(getShell(), getProject(), modelName, element);
				}
			}
		} else if ("com.asakusafw.vocabulary.operator.MasterSelection".equals(aname)) {
			dialog = new MasterSelectionDialog(getShell(), element);
		}
		if (dialog == null) {
			dialog = new AttributeEditDialog(getShell(), element);
		}
		if (dialog.open() != Window.OK) {
			return false;
		}

		element.setValue(dialog.getValue());
		if (dialog instanceof AttributeEditDialog) {
			AttributeEditDialog ad = (AttributeEditDialog) dialog;
			element.setAnnotationName(ad.getAnnotationName());
			element.setParameterName(ad.getParameterName());
			element.setValueType(ad.getValueType());
		}
		return true;
	}
}
