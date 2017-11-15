package jp.hishidama.eclipse_plugin.toad.wizard.newdiagram;

import jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.page.DiagramFileCreationPage;
import jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.page.JobflowFileCreationPage;

import org.eclipse.core.resources.IProject;

public class NewJobflowWizard extends NewDiagramWizard {

	public NewJobflowWizard() {
		super("ジョブフロー", "Flow DSL");
	}

	@Override
	protected DiagramFileCreationPage createFileCreationPage(IProject project) {
		return new JobflowFileCreationPage(project);
	}
}
