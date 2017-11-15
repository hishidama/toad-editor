package jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen;

import java.util.List;

import org.eclipse.core.resources.IProject;

import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.frame.FlowpartFrameNode;
import jp.hishidama.eclipse_plugin.toad.model.frame.FlowpartParameterDef;
import jp.hishidama.eclipse_plugin.toad.model.node.port.JobPort;
import jp.hishidama.eclipse_plugin.util.StringUtil;

public class FlowpartClassGenerator extends FlowClassGenerator<FlowpartFrameNode> {

	public FlowpartClassGenerator(IProject project, Diagram diagram) {
		super(project, diagram);
	}

	@Override
	protected void defaultImport() {
		super.defaultImport();
		getCachedClassName("com.asakusafw.vocabulary.flow.FlowPart");
	}

	@Override
	protected void appendClassAnnotation(StringBuilder sb) {
		sb.append("@FlowPart\n");
	}

	@Override
	protected void appendConstructorArgumentAnnotation(StringBuilder sb, JobPort port) {
		// do nothing
	}

	@Override
	protected void appendField(StringBuilder sb) {
		super.appendField(sb);

		List<FlowpartParameterDef> parameters = frame.getParameterList();
		for (FlowpartParameterDef param : parameters) {
			sb.append("\t/** ");
			sb.append(StringUtil.nonNull(param.getDescription()));
			sb.append(" */\n");
			sb.append("\tprivate ");
			sb.append(getCachedClassName(param.getClassName()));
			sb.append(" ");
			sb.append(param.getName());
			sb.append(";\n");
		}
	}

	@Override
	protected void appendConstructorArguments(StringBuilder sb, StringBuilder let) {
		super.appendConstructorArguments(sb, let);

		List<FlowpartParameterDef> parameters = ((FlowpartFrameNode) frame).getParameterList();
		for (FlowpartParameterDef param : parameters) {
			String name = param.getName();

			sb.append(",\n");
			sb.append("\t\t\t");
			sb.append(getCachedClassName(param.getClassName()));
			sb.append(" ");
			sb.append(name);

			let.append("\t\tthis.");
			let.append(name);
			let.append(" = ");
			let.append(name);
			let.append(";\n");
		}
	}
}
