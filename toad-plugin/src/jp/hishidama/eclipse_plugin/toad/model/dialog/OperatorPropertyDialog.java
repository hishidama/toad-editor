package jp.hishidama.eclipse_plugin.toad.model.dialog;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.editor.action.ShowOperatorDslTemplateAction;
import jp.hishidama.eclipse_plugin.toad.model.dialog.section.EnumNameSection;
import jp.hishidama.eclipse_plugin.toad.model.dialog.section.JoinKeySection;
import jp.hishidama.eclipse_plugin.toad.model.dialog.section.KeyAnnotationSection;
import jp.hishidama.eclipse_plugin.toad.model.dialog.section.KeySection;
import jp.hishidama.eclipse_plugin.toad.model.dialog.section.ParameterSection;
import jp.hishidama.eclipse_plugin.toad.model.dialog.section.PortSection;
import jp.hishidama.eclipse_plugin.toad.model.dialog.section.PropertySection;
import jp.hishidama.eclipse_plugin.toad.model.dialog.section.SummarizeKeySection;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;
import jp.hishidama.eclipse_plugin.toad.model.node.port.OpePort;
import jp.hishidama.eclipse_plugin.toad.model.property.datamodel.HasDataModelNode;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;

public class OperatorPropertyDialog extends PropertyDialog {
	private OperatorNode operator;

	private KeySection keyTab;
	private EnumNameSection enumTab;
	private PortSection<OpePort> portTab;
	private ParameterSection paramTab;

	public OperatorPropertyDialog(ToadEditor editor, OperatorNode operator) {
		super("オペレーター", editor, operator);
		this.operator = (OperatorNode) super.model;
	}

	@Override
	protected void createFields(TabFolder tab) {
		createBasicTab(tab);
		createMemoTab(tab);
		if (operator instanceof HasDataModelNode) {
			createModelLayoutTab(tab);
		} else {
			if (operator.getKeyTitle() != null) {
				createKeyTab(tab);
			}
			createPortTab(tab);
		}
		if (operator.isReturnEnum()) {
			createEnumTab(tab);
		}
		if (operator.enableValueParameter()) {
			createParameterTab(tab);
		}
		if (!operator.isCoreOperator()) {
			createAttributeTab(tab);
		}
	}

	private void createBasicTab(TabFolder tab) {
		Composite composite = createBasicTabItem(tab);
		createTypeSection(composite);
		createBaseSection(composite);
		createClassNameSection(composite);
		if (operator instanceof HasDataModelNode) {
			createModelNameSection(composite);
		}
	}

	private void createTypeSection(Composite composite) {
		new PropertySection(this) {
			public void createSection(Composite composite) {
				Text text = createTextField(composite, "type");
				text.setText(operator.getType());
				text.setEditable(false);
			}
		}.createSection(composite);
	}

	private void createKeyTab(TabFolder tab) {
		String type = operator.getType();
		if (type.equals("@Summarize")) {
			keyTab = new SummarizeKeySection(this, operator);
		} else if (type.equals("@MasterJoin")) {
			keyTab = new JoinKeySection(this, operator);
		} else {
			keyTab = new KeyAnnotationSection(this, operator);
		}
		keyTab.createTab(tab);
	}

	private void createEnumTab(TabFolder tab) {
		enumTab = new EnumNameSection(this, operator);
		enumTab.createTab(tab);
	}

	private void createPortTab(TabFolder tab) {
		portTab = new PortSection<OpePort>(editor, this, operator) {
			@Override
			protected OpePort createPort(boolean in) {
				OpePort port = new OpePort();
				port.setIn(in);
				port.setNamePosition(in ? PositionConstants.LEFT : PositionConstants.RIGHT);
				return port;
			}
		};
		portTab.createTab(tab);
	}

	private void createParameterTab(TabFolder tab) {
		paramTab = new ParameterSection(this, operator);
		paramTab.createTab(tab);
	}

	public void previewOperatorDsl() {
		ShowOperatorDslTemplateAction action = new ShowOperatorDslTemplateAction(editor);
		action.run(operator);
	}
}
