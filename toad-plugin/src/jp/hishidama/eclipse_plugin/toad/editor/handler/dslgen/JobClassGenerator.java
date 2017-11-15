package jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen;

import java.text.MessageFormat;

import org.eclipse.core.resources.IProject;

import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.frame.JobFrameNode;
import jp.hishidama.eclipse_plugin.toad.model.node.datafile.DataFileNode;
import jp.hishidama.eclipse_plugin.toad.model.node.port.JobPort;
import jp.hishidama.eclipse_plugin.util.StringUtil;

public class JobClassGenerator extends FlowClassGenerator<JobFrameNode> {

	public JobClassGenerator(IProject project, Diagram diagram) {
		super(project, diagram);
	}

	@Override
	protected void initialize() {
		if (StringUtil.isEmpty(diagramName)) {
			throw new IllegalStateException(MessageFormat.format("name={0}", diagramName));
		}
		super.initialize();
	}

	@Override
	protected void defaultImport() {
		super.defaultImport();
		getCachedClassName("com.asakusafw.vocabulary.flow.JobFlow");
		getCachedClassName("com.asakusafw.vocabulary.flow.Import");
		getCachedClassName("com.asakusafw.vocabulary.flow.Export");
	}

	@Override
	protected void appendClassAnnotation(StringBuilder sb) {
		sb.append("@JobFlow(name = \"");
		sb.append(diagramName);
		sb.append("\")\n");
	}

	@Override
	protected void appendConstructorArgumentAnnotation(StringBuilder sb, JobPort port) {
		String name = port.getName();
		String fileClassName = null;
		DataFileNode file = port.getConnectedFile();
		if (file != null) {
			fileClassName = getCachedClassName(file.getClassName());
		}
		if (StringUtil.isEmpty(fileClassName)) {
			fileClassName = "UNDEFINED";
		}

		sb.append(port.isIn() ? "@Import" : "@Export");
		sb.append("(name = \"");
		sb.append(name);
		sb.append("\", description = ");
		sb.append(fileClassName);
		sb.append(".class) ");
	}
}
