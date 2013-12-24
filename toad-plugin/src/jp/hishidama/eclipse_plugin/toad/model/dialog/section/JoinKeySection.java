package jp.hishidama.eclipse_plugin.toad.model.dialog.section;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.jface.ModifiableTable;
import jp.hishidama.eclipse_plugin.toad.model.dialog.PropertyDialog;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;
import jp.hishidama.eclipse_plugin.toad.model.node.port.OpePort;
import jp.hishidama.eclipse_plugin.util.StringUtil;
import jp.hishidama.xtext.dmdl_editor.dmdl.Grouping;
import jp.hishidama.xtext.dmdl_editor.dmdl.JoinExpression;
import jp.hishidama.xtext.dmdl_editor.dmdl.JoinTerm;
import jp.hishidama.xtext.dmdl_editor.dmdl.ModelDefinition;
import jp.hishidama.xtext.dmdl_editor.dmdl.ModelUiUtil;
import jp.hishidama.xtext.dmdl_editor.dmdl.ModelUtil;
import jp.hishidama.xtext.dmdl_editor.dmdl.Property;
import jp.hishidama.xtext.dmdl_editor.ui.wizard.EditDataModelWizard;
import jp.hishidama.xtext.dmdl_editor.ui.wizard.NewDataModelWizard;
import jp.hishidama.xtext.dmdl_editor.ui.wizard.page.DataModelType;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class JoinKeySection extends KeySection {

	public JoinKeySection(PropertyDialog dialog, OperatorNode model) {
		super(dialog, model);
	}

	@Override
	protected List<String> getTitle() {
		List<OpePort> ports = model.getOutputPorts();
		List<String> list = new ArrayList<String>(ports.size());
		for (OpePort port : ports) {
			String modelName = port.getModelName();
			list.add(modelName);
		}
		return list;
	}

	@Override
	protected void createButtonArea(ModifiableTable<Row> table, Composite field) {
		Button button = new Button(field, SWT.PUSH);
		button.setText("結合モデルを編集する");
		button.setToolTipText("マスター結合演算子（@MasterJoin）では結合モデルで結合キーを指定します。");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				editElement(null);
			}
		});
	}

	private ModelDefinition joinModel;

	@Override
	protected List<Row> buildKey(String modelName) {
		if (modelName == null) {
			OpePort port = model.getOutputPort();
			modelName = (port != null) ? port.getModelName() : null;
		}

		List<Row> rowList = new ArrayList<Row>();

		ModelDefinition m = ModelUiUtil.findModel(getProject(), modelName, getContainer());
		if (m != null && m.getRhs() instanceof JoinExpression) {
			JoinExpression expression = (JoinExpression) m.getRhs();
			int i = 0;
			for (JoinTerm term : expression.getTerms()) {
				Grouping group = term.getGrouping();
				int j = 0;
				for (Property p : group.getName()) {
					while (j >= rowList.size()) {
						rowList.add(new Row());
					}
					Row row = rowList.get(j);
					row.set(i, p.getName());
					j++;
				}
				i++;
			}
			joinModel = m;
		}

		return rowList;
	}

	@Override
	protected boolean editElement(Row element) {
		if (joinModel == null) {
			return createDataModel();
		}
		IFile file = ModelUtil.getFile(joinModel);
		EditDataModelWizard wizard = new EditDataModelWizard();
		wizard.init(file, joinModel, DataModelType.JOINED, ModelUtil.getDecodedDescription(joinModel));
		List<String> list = ModelUtil.getSourceModelName(joinModel);
		wizard.initSource(list);

		WizardDialog dialog = new WizardDialog(getShell(), wizard);
		if (dialog.open() != Window.OK) {
			return false;
		}

		rebuildKeyTable(null);
		return true;
	}

	private boolean createDataModel() {
		NewDataModelWizard wizard = new NewDataModelWizard();
		wizard.init(getProject(), DataModelType.JOINED);
		List<OpePort> ports = model.getInputPorts();
		List<String> list = new ArrayList<String>(ports.size());
		for (OpePort port : ports) {
			String name = port.getModelName();
			if (StringUtil.nonEmpty(name)) {
				list.add(name);
			}
		}
		if (list.size() >= 2) {
			wizard.initSource(list);
		}

		WizardDialog dialog = new WizardDialog(getShell(), wizard);
		if (dialog.open() != Window.OK) {
			return false;
		}

		String modelName = wizard.getDataModelName();
		rebuildKeyTable(modelName);
		return true;
	}
}
