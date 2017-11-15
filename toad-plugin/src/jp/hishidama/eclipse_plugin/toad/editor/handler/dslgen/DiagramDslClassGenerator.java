package jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen;

import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;

import org.eclipse.core.resources.IProject;

public abstract class DiagramDslClassGenerator extends DslClassGenerator {

	protected Diagram diagram;
	protected String diagramName;

	public DiagramDslClassGenerator(IProject project, Diagram diagram) {
		super(project, diagram.getClassName());

		this.diagram = diagram;
		this.diagramName = diagram.getName();

		initialize();
	}

	protected abstract void initialize();
}
