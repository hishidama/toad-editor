package jp.hishidama.eclipse_plugin.toad.wizard.newdiagram;

import jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.page.BatchFileCreationPage;
import jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.page.DiagramFileCreationPage;

import org.eclipse.core.resources.IProject;

public class NewBatchWizard extends NewDiagramWizard {

	public NewBatchWizard() {
		super("バッチ", "Batch DSL");
	}

	@Override
	protected DiagramFileCreationPage createFileCreationPage(IProject project) {
		return new BatchFileCreationPage(project);
	}
}
