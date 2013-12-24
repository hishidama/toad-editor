package jp.hishidama.eclipse_plugin.toad.model.dialog.section;

import static jp.hishidama.eclipse_plugin.util.StringUtil.nonNull;

import java.util.List;

import jp.hishidama.eclipse_plugin.toad.model.dialog.PropertyDialog;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.GuessDataModelType;
import jp.hishidama.eclipse_plugin.toad.model.property.datamodel.DataModelNodeUtil;
import jp.hishidama.eclipse_plugin.toad.model.property.datamodel.HasDataModelNode;
import jp.hishidama.eclipse_plugin.toad.view.SiblingDataModelTreeElement;
import jp.hishidama.eclipse_plugin.util.ToadCommandUtil;
import jp.hishidama.xtext.dmdl_editor.dmdl.ModelDefinition;
import jp.hishidama.xtext.dmdl_editor.dmdl.ModelUiUtil;
import jp.hishidama.xtext.dmdl_editor.dmdl.ModelUtil;
import jp.hishidama.xtext.dmdl_editor.dmdl.Property;
import jp.hishidama.xtext.dmdl_editor.dmdl.PropertyUtil;
import jp.hishidama.xtext.dmdl_editor.ui.dialog.DmdlModelSelectionDialog;
import jp.hishidama.xtext.dmdl_editor.ui.wizard.EditDataModelWizard;
import jp.hishidama.xtext.dmdl_editor.ui.wizard.NewDataModelWizard;
import jp.hishidama.xtext.dmdl_editor.ui.wizard.page.DataModelType;

import org.eclipse.core.resources.IFile;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class DataModelSection extends PropertySection {
	private HasDataModelNode model;
	private boolean enableOpen;

	private Text modelName;
	private Text modelDescription;
	private Table modelTable;
	private Button editButton;

	public DataModelSection(PropertyDialog dialog, HasDataModelNode model, boolean enableOpen) {
		super(dialog);
		this.model = model;
		this.enableOpen = enableOpen;
	}

	public void createModelNameSection(Composite composite) {
		if (enableOpen) {
			TextButtonPair n = createTextField(composite, "model name", "open");
			modelName = n.text;
			n.button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					dialog.openModel(getModelName());
				}
			});
		} else {
			modelName = createTextField(composite, "model name");
		}
		modelName.setText(nonNull(model.getModelName()));
		modelName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String name = modelName.getText().trim();
				model.setModelName(name);
				dialog.doValidate();
			}
		});

		modelDescription = createTextField(composite, "model description");
		modelDescription.setText(nonNull(model.getModelDescription()));
		modelDescription.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String desc = modelDescription.getText();
				model.setModelDescription(desc);
				dialog.doValidate();
			}
		});

		Composite field = createField(composite, "data model", 3);
		Button select = new Button(field, SWT.PUSH);
		select.setText("既存データモデルから選択");
		select.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectDataModel();
			}
		});
		Button create = new Button(field, SWT.PUSH);
		create.setText("新規データモデルを作成");
		create.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				createDataModel();
			}
		});
		Button check = new Button(field, SWT.PUSH);
		check.setText("連動データモデルを確認");
		check.setToolTipText("データモデル設定時に連動して変更されるノードを選択します。");
		check.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				confirmSiblingDataModel();
			}
		});
	}

	void selectDataModel() {
		DmdlModelSelectionDialog dialog = new DmdlModelSelectionDialog(getShell(), getProject());
		dialog.setInitialModel(getModelName());
		if (dialog.open() != Window.OK) {
			return;
		}

		ModelDefinition m = dialog.getSelectedDataModel();
		modelName.setText(nonNull(m.getName()));
		modelDescription.setText(ModelUtil.getDecodedDescriptionText(m));
	}

	void createDataModel() {
		NewDataModelWizard wizard = new NewDataModelWizard();
		GuessDataModelType type = getGuessModelType();
		wizard.init(getProject(), getModelType(type));
		if (GuessDataModelType.isSourceDecision(type)) {
			wizard.initSource(type.getFirst(), type.getSecond());
		}
		WizardDialog dialog = new WizardDialog(getShell(), wizard);
		if (dialog.open() != Window.OK) {
			return;
		}

		modelName.setText(wizard.getDataModelName());
		modelDescription.setText(wizard.getDataModelDescription());
	}

	void editDataModel() {
		IFile file = ModelUtil.getFile(modelLayout);
		EditDataModelWizard wizard = new EditDataModelWizard();
		GuessDataModelType type = getGuessModelType();
		wizard.init(file, modelLayout, getModelType(type), getModelDescription());
		if (GuessDataModelType.isSourceDecision(type)) {
			wizard.initSource(type.getFirst(), type.getSecond());
		}
		WizardDialog dialog = new WizardDialog(getShell(), wizard);
		if (dialog.open() != Window.OK) {
			return;
		}

		modelTableName = null;
		buildModelTable(wizard.getDataModelName());
	}

	private GuessDataModelType getGuessModelType() {
		SiblingDataModelTreeElement root = getSiblingDataModels();
		return root.guessDataModelType();
	}

	private DataModelType getModelType(GuessDataModelType type) {
		if (type != null) {
			return type.getType();
		}

		ModelDefinition m = ModelUiUtil.findModel(getProject(), model.getModelName());
		return DataModelType.valueOf(m);
	}

	void confirmSiblingDataModel() {
		SiblingDataModelTreeElement root = getSiblingDataModels();
		SiblingDataModelNodeDialog dialog = new SiblingDataModelNodeDialog(getShell(), root);
		dialog.open();
	}

	private SiblingDataModelTreeElement treeRoot;

	private SiblingDataModelTreeElement getSiblingDataModels() {
		if (treeRoot == null) {
			treeRoot = DataModelNodeUtil.getSiblingDataModelNode(model);
		}
		return treeRoot;
	}

	public void createModelLayoutTab(TabFolder tab) {
		final Composite composite = createTabItem(tab, "DataModel");

		Table table = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION);
		{
			GridData grid = new GridData(GridData.FILL_BOTH);
			grid.horizontalSpan = 3;
			table.setLayoutData(grid);
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
		}
		{
			TableColumn col = new TableColumn(table, SWT.NONE);
			col.setText("name");
			col.setWidth(128 + 32);
		}
		{
			TableColumn col = new TableColumn(table, SWT.NONE);
			col.setText("description");
			col.setWidth(256 - 32);
		}
		{
			TableColumn col = new TableColumn(table, SWT.NONE);
			col.setText("type");
			col.setWidth(96);
		}

		tab.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TabFolder tab = (TabFolder) e.getSource();
				TabItem item = tab.getItem(tab.getSelectionIndex());
				if (item.getControl() == composite) {
					buildModelTable(modelName.getText());
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		this.modelTable = table;

		Composite field = new Composite(composite, SWT.NONE);
		{
			GridData grid = new GridData(GridData.FILL_HORIZONTAL);
			grid.horizontalSpan = 3;
			field.setLayoutData(grid);
			field.setLayout(new GridLayout(1, false));
		}
		{
			Button button = new Button(field, SWT.PUSH);
			button.setText("データモデルの編集");
			button.setEnabled(false);
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					editDataModel();
				}
			});
			editButton = button;
		}
	}

	String modelTableName;
	private ModelDefinition modelLayout;

	void buildModelTable(String modelName) {
		if (modelName.equals(modelTableName) && modelTable.getItemCount() != 0) {
			return;
		}
		modelTableName = modelName;

		modelTable.removeAll();
		ModelDefinition model = ModelUiUtil.findModel(getProject(), modelName, getContainer());
		if (model != null) {
			List<Property> properties = ModelUtil.getProperties(model);
			for (Property p : properties) {
				TableItem item = new TableItem(modelTable, SWT.NONE);
				item.setText(0, p.getName());
				item.setText(1, PropertyUtil.getDecodedDescriptionText(p));
				item.setText(2, PropertyUtil.getResolvedDataTypeText(p));
			}
		}
		modelLayout = model;
		editButton.setEnabled(modelLayout != null);
	}

	public void addCommand(CompoundCommand command) {
		String name = getModelName();
		String desc = getModelDescription();
		addCommand(command, name, desc);
	}

	public void addCommand(CompoundCommand command, String name, String desc) {
		addSiblingModelCommand(command, getSiblingDataModels(), name, desc);
	}

	private void addSiblingModelCommand(CompoundCommand command, SiblingDataModelTreeElement te, String name,
			String desc) {
		HasDataModelNode node = te.getDataModelNode();
		if (node == model || te.getChecked()) {
			addCommand(command, node, name, desc);
		}

		for (SiblingDataModelTreeElement c : te.getChildren()) {
			addSiblingModelCommand(command, c, name, desc);
		}
	}

	private static void addCommand(CompoundCommand command, HasDataModelNode node, String name, String desc) {
		if (node != null) {
			ToadCommandUtil.add(command, node.getModelNameCommand(name));
			ToadCommandUtil.add(command, node.getModelDescriptionCommand(desc));
		}
	}

	public String getModelName() {
		return modelName.getText();
	}

	public String getModelDescription() {
		return modelDescription.getText();
	}
}
