package jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.page;

import jp.hishidama.eclipse_plugin.toad.editor.action.layout.MarkerAutoLayout;
import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.frame.FrameNode;
import jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.gen.FlowDiagramGenerator;

import org.eclipse.core.resources.IProject;

public abstract class FlowFileCreationPage extends DiagramFileCreationPage {

	protected FlowFileCreationPage(String pageName, IProject project, String ext) {
		super(pageName, project, ext);
	}

	@Override
	public Diagram generateDiagram() {
		FlowDiagramGenerator generator = createGenerator();
		Diagram diagram = generator.createEmptyDiagram();
		generator.initialize();
		generator.addDefaultPort(project);
		new MarkerAutoLayout().run(diagram, null);
		return diagram;
	}

	protected abstract FlowDiagramGenerator createGenerator();

	@Override
	protected void setTo(Diagram diagram, String name, String description, String className) {
		FrameNode frame = diagram.getFrameNode();
		frame.setDescription(description);
		frame.setClassName(className);
	}
}
