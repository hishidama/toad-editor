package jp.hishidama.eclipse_plugin.toad.model.dialog;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;

public class DiagramPropertyDialog extends PropertyDialog {

	public DiagramPropertyDialog(ToadEditor editor, Diagram diagram) {
		super(diagram.getType(), editor, diagram);
	}

	@Override
	protected void createFields(TabFolder tab) {
		createBasicTab(tab);
		createMemoTab(tab);
	}

	private void createBasicTab(TabFolder tab) {
		Composite composite = createBasicTabItem(tab);
		createBaseSection(composite);
		createClassNameSection(composite);
	}
}
