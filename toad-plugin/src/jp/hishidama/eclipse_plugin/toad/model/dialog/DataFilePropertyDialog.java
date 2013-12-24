package jp.hishidama.eclipse_plugin.toad.model.dialog;

import static jp.hishidama.eclipse_plugin.util.StringUtil.nonNull;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.extension.ToadImporterExporterProperty;
import jp.hishidama.eclipse_plugin.toad.extension.ToadImporterExporterProperty.PorterProperty;
import jp.hishidama.eclipse_plugin.toad.internal.extension.DefaultExporterProperty;
import jp.hishidama.eclipse_plugin.toad.internal.extension.ImporterExporterExtensionUtil;
import jp.hishidama.eclipse_plugin.toad.model.dialog.section.PropertySection;
import jp.hishidama.eclipse_plugin.toad.model.node.datafile.DataFileNode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

public class DataFilePropertyDialog extends PropertyDialog {
	private DataFileNode node;

	private Combo fileType;
	private Composite propertyComposite;
	private List<Widget> fieldList;

	public DataFilePropertyDialog(ToadEditor editor, DataFileNode node) {
		super("ファイル（Importer/Exporter）", editor, node);
		this.node = (DataFileNode) super.model;
	}

	@Override
	protected void createFields(TabFolder tab) {
		createBasicTab(tab);
		createMemoTab(tab);
		createModelLayoutTab(tab);
		createFileTab(tab);
	}

	private void createBasicTab(TabFolder tab) {
		Composite composite = createBasicTabItem(tab);
		createBaseSection(composite);
		createClassNameSection(composite);
		createModelNameSection(composite);
	}

	private void createFileTab(TabFolder tab) {
		new DataFileSection().createSection(tab);
	}

	private class DataFileSection extends PropertySection {
		public DataFileSection() {
			super(DataFilePropertyDialog.this);
		}

		public void createSection(TabFolder tab) {
			Composite composite = createTabItem(tab, "DataFile");

			List<ToadImporterExporterProperty> list = ImporterExporterExtensionUtil.getExtensionList();
			List<String> nlist = new ArrayList<String>(list.size());
			for (ToadImporterExporterProperty p : list) {
				nlist.add(p.getName());
			}
			fileType = createComboField(composite, "file type", nlist, node.getFileType());
			fileType.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					String newType = fileType.getText();
					node.setFileType(newType);
					fieldToMap();
					fileType.setText(newType);
					for (Widget c : propertyComposite.getChildren()) {
						c.dispose();
					}
					buildFileTab(propertyComposite, newType);
					propertyComposite.layout();
				}
			});

			propertyComposite = new Composite(composite, SWT.NONE);
			{
				GridData grid = new GridData(GridData.FILL_BOTH);
				grid.horizontalSpan = 3;
				propertyComposite.setLayoutData(grid);
			}
			{
				GridLayout layout = new GridLayout(4, false);
				propertyComposite.setLayout(layout);
			}
			buildFileTab(propertyComposite, node.getFileType());
		}
	}

	private void buildFileTab(Composite composite, String fileType) {
		ToadImporterExporterProperty extension = ImporterExporterExtensionUtil.getExtension(fileType);
		if (extension == null) {
			extension = new DefaultExporterProperty();
		}
		fieldList = new ArrayList<Widget>();
		for (final PorterProperty p : extension.getProperties()) {
			String value = node.getProperty(p.methodName);
			if (p.candidate == null) {
				final Text text = createTextField2(composite, p.methodName + "()", p.description, value);
				text.setData(p);
				text.addModifyListener(new ModifyListener() {
					@Override
					public void modifyText(ModifyEvent e) {
						node.setProperty(p.methodName, text.getText());
					}
				});
				fieldList.add(text);
			} else {
				final Combo combo = createComboField2(composite, p.methodName + "()", p.description, p.candidate, value);
				combo.setData(p);
				combo.addModifyListener(new ModifyListener() {
					@Override
					public void modifyText(ModifyEvent e) {
						node.setProperty(p.methodName, combo.getText());
					}
				});
				fieldList.add(combo);
			}
		}
	}

	protected Text createTextField2(Composite composite, String labelText, String labelText2, String value) {
		Label label = new Label(composite, SWT.NONE);
		label.setText(labelText);
		Label label2 = new Label(composite, SWT.NONE);
		label2.setText(labelText2);

		Text text = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		text.setLayoutData(data);
		text.setText(nonNull(value));
		return text;
	}

	protected Combo createComboField2(Composite composite, String labelText, String labelText2, List<String> values,
			String value) {
		Label label = new Label(composite, SWT.NONE);
		label.setText(labelText);
		Label label2 = new Label(composite, SWT.NONE);
		label2.setText(labelText2);

		Combo combo = new Combo(composite, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		combo.setLayoutData(data);

		if (value == null) {
			value = values.get(0);
		}
		boolean found = false;
		for (String s : values) {
			combo.add(s);
			if (s.equals(value)) {
				found = true;
			}
		}
		if (!found) {
			value = values.get(0);
		}
		combo.setText(nonNull(value));

		return combo;
	}

	private void fieldToMap() {
		for (Widget w : fieldList) {
			PorterProperty p = (PorterProperty) w.getData();
			String key = p.methodName;
			if (w instanceof Text) {
				Text text = (Text) w;
				node.setProperty(key, text.getText());
			} else if (w instanceof Combo) {
				Combo combo = (Combo) w;
				node.setProperty(key, combo.getText());
			} else {
				throw new IllegalStateException("widget=" + w);
			}
		}
	}
}
