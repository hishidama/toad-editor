package jp.hishidama.eclipse_plugin.toad.model.dialog.section;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.asakusafw_wrapper.dmdl.DataModelType;
import jp.hishidama.eclipse_plugin.jface.ModifiableTable;
import jp.hishidama.eclipse_plugin.toad.model.dialog.PropertyDialog;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;
import jp.hishidama.eclipse_plugin.toad.model.node.port.OpePort;
import jp.hishidama.eclipse_plugin.util.StringUtil;
import jp.hishidama.xtext.dmdl_editor.dmdl.Grouping;
import jp.hishidama.xtext.dmdl_editor.dmdl.ModelDefinition;
import jp.hishidama.xtext.dmdl_editor.dmdl.ModelUiUtil;
import jp.hishidama.xtext.dmdl_editor.dmdl.ModelUtil;
import jp.hishidama.xtext.dmdl_editor.dmdl.Property;
import jp.hishidama.xtext.dmdl_editor.dmdl.SummarizeExpression;
import jp.hishidama.xtext.dmdl_editor.dmdl.SummarizeTerm;
import jp.hishidama.xtext.dmdl_editor.ui.wizard.EditDataModelWizard;
import jp.hishidama.xtext.dmdl_editor.ui.wizard.NewDataModelWizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class SummarizeKeySection extends KeySection {

	public SummarizeKeySection(PropertyDialog dialog, OperatorNode model) {
		super(dialog, model);
	}

	@Override
	protected List<String> getTitle() {
		List<String> list = new ArrayList<String>(1);

		OpePort port = model.getOutputPort();
		String modelName = (port != null) ? port.getModelName() : "";
		list.add(modelName);
		return list;
	}

	@Override
	protected void createButtonArea(ModifiableTable<Row> table, Composite field) {
		Button button = new Button(field, SWT.PUSH);
		button.setText("集計モデルを編集する");
		button.setToolTipText("単純集計演算子（@Summarize）では集計モデルで集計キーを指定します。");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				editElement(null);
			}
		});
	}

	private ModelDefinition summarizeModel;

	@Override
	protected List<Row> buildKey(String modelName) {
		if (modelName == null) {
			OpePort port = model.getOutputPort();
			modelName = (port != null) ? port.getModelName() : null;
		}

		List<Row> rowList = new ArrayList<Row>();

		ModelDefinition m = ModelUiUtil.findModel(getProject(), modelName, getContainer());
		if (m != null && m.getRhs() instanceof SummarizeExpression) {
			SummarizeExpression expression = (SummarizeExpression) m.getRhs();
			for (SummarizeTerm term : expression.getTerms()) {
				Grouping group = term.getGrouping();
				for (Property p : group.getName()) {
					Row row = new Row();
					row.set(0, p.getName());
					rowList.add(row);
				}
			}
			summarizeModel = m;
		}

		return rowList;
	}

	@Override
	protected boolean editElement(Row element) {
		if (summarizeModel == null) {
			return createDataModel();
		}
		IFile file = ModelUtil.getFile(summarizeModel);
		EditDataModelWizard wizard = new EditDataModelWizard();
		wizard.init(file, summarizeModel, DataModelType.SUMMARIZED, ModelUtil.getDecodedDescription(summarizeModel));
		List<String> list = ModelUtil.getSourceModelName(summarizeModel);
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
		wizard.init(getProject(), DataModelType.SUMMARIZED);
		List<OpePort> ports = model.getInputPorts();
		List<String> list = new ArrayList<String>(ports.size());
		for (OpePort port : ports) {
			String name = port.getModelName();
			if (StringUtil.nonEmpty(name)) {
				list.add(name);
			}
		}
		wizard.initSource(list);

		WizardDialog dialog = new WizardDialog(getShell(), wizard);
		if (dialog.open() != Window.OK) {
			return false;
		}

		String modelName = wizard.getDataModelName();
		rebuildKeyTable(modelName);
		return true;
	}
}
