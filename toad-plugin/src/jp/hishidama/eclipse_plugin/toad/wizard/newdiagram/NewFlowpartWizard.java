package jp.hishidama.eclipse_plugin.toad.wizard.newdiagram;

import jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.page.DiagramFileCreationPage;
import jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.page.FlowpartFileCreationPage;

import org.eclipse.core.resources.IProject;

public class NewFlowpartWizard extends NewDiagramWizard {

	public NewFlowpartWizard() {
		super("フローパート", "Flow DSL");
	}

	@Override
	protected DiagramFileCreationPage createFileCreationPage(IProject project) {
		return new FlowpartFileCreationPage(project);
	}
}
