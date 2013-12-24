package jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IProject;

import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;
import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.frame.FrameNode;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OpeParameter;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;
import jp.hishidama.eclipse_plugin.toad.model.node.port.JobPort;
import jp.hishidama.eclipse_plugin.toad.model.node.port.OpePort;
import jp.hishidama.eclipse_plugin.toad.model.property.datamodel.HasDataModelNode;
import jp.hishidama.eclipse_plugin.util.StringUtil;

public abstract class FlowClassGenerator<F extends FrameNode> extends DiagramDslClassGenerator {

	private static final String CORE_OP_FACTORY = "com.asakusafw.vocabulary.flow.util.CoreOperatorFactory";

	protected F frame;

	public FlowClassGenerator(IProject project, Diagram diagram) {
		super(project, diagram);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void initialize() {
		this.frame = (F) diagram.getFrameNode();
		if (frame == null) {
			throw new IllegalStateException(MessageFormat.format("frame={0}", frame));
		}
	}

	@Override
	protected void defaultImport() {
		getCachedClassName("com.asakusafw.vocabulary.flow.FlowDescription");
		getCachedClassName("com.asakusafw.vocabulary.flow.In");
		getCachedClassName("com.asakusafw.vocabulary.flow.Out");
	}

	@Override
	protected void appendClass(StringBuilder sb) {
		sb.append("public class ");
		sb.append(StringUtil.getSimpleName(className));
		sb.append(" extends FlowDescription {\n");
		appendField(sb);
		sb.append("\n");
		appendConstructor(sb);
		appendDescribe(sb);
		sb.append("}\n");
	}

	protected void appendField(StringBuilder sb) {
		for (JobPort port : frame.getInputPorts()) {
			appendField(sb, port, "In");
		}
		for (JobPort port : frame.getOutputPorts()) {
			appendField(sb, port, "Out");
		}
	}

	private void appendField(StringBuilder sb, JobPort port, String inOut) {
		sb.append("\t/** ");
		sb.append(StringUtil.nonNull(port.getDescription()));
		sb.append(" */\n");
		sb.append("\tprivate final ");
		sb.append(inOut);
		sb.append("<");
		sb.append(getCachedClassName(getModelClassName(port)));
		sb.append("> ");
		sb.append(port.getName());
		sb.append(";\n");
	}

	private void appendConstructor(StringBuilder sb) {
		sb.append("\tpublic ");
		sb.append(StringUtil.getSimpleName(className));
		sb.append("(\n");

		StringBuilder let = new StringBuilder(256);
		appendConstructorArguments(sb, let);
		sb.append(") {\n");
		sb.append(let);
		sb.append("\t}\n\n");
	}

	protected void appendConstructorArguments(StringBuilder sb, StringBuilder let) {
		boolean first = true;
		for (JobPort port : frame.getInputPorts()) {
			if (first) {
				first = false;
			} else {
				sb.append(",\n");
			}
			appendConstructorArgument(sb, let, port);
		}
		for (JobPort port : frame.getOutputPorts()) {
			if (first) {
				first = false;
			} else {
				sb.append(",\n");
			}
			appendConstructorArgument(sb, let, port);
		}
	}

	private void appendConstructorArgument(StringBuilder sb, StringBuilder let, JobPort port) {
		String name = port.getName();
		String modelClassName = getCachedClassName(getModelClassName(port));

		sb.append("\t\t\t");
		appendConstructorArgumentAnnotation(sb, port);
		sb.append(port.isIn() ? "In" : "Out");
		sb.append("<");
		sb.append(modelClassName);
		sb.append("> ");
		sb.append(name);

		let.append("\t\tthis.");
		let.append(name);
		let.append(" = ");
		let.append(name);
		let.append(";\n");
	}

	protected abstract void appendConstructorArgumentAnnotation(StringBuilder sb, JobPort port);

	private Map<String, String> factoryVarMap = new LinkedHashMap<String, String>();

	private String getFactoryVariableName(String factoryName, boolean isFlowPart) {
		String name = factoryVarMap.get(factoryName);
		if (name != null) {
			return name;
		}
		if (factoryName.equals(CORE_OP_FACTORY)) {
			name = "core";
		} else {
			name = factoryName;
			int n = name.lastIndexOf('.');
			if (n >= 0) {
				name = name.substring(n + 1);
			}
			if (!isFlowPart) {
				name = StringUtil.removeEnds(name, "Factory");
			}
			name = StringUtil.toFirstLower(name);
		}
		name = getIdentifiedName(name);
		factoryVarMap.put(factoryName, name);
		return name;
	}

	private Map<NodeElement, GenNode> nodeMap = new HashMap<NodeElement, GenNode>();

	private void appendDescribe(StringBuilder sb) {
		sb.append("\t@Override\n");
		sb.append("\tpublic void describe() {\n");

		Map<Integer, GenNode> map = new HashMap<Integer, GenNode>();
		for (JobPort port : frame.getInputPorts()) {
			GenImporter gen = new GenImporter(port);
			map.put(port.getId(), gen);
			nodeMap.put(port, gen);
		}

		List<GenOperator> opeList = new ArrayList<GenOperator>();
		for (NodeElement node : diagram.getContents()) {
			if (node instanceof OperatorNode) {
				OperatorNode operator = (OperatorNode) node;
				GenOperator gop = new GenOperator(operator);
				opeList.add(gop);
				getFactoryVariableName(gop.getFactoryName(), gop.isFlowPart());
				nodeMap.put(node, gop);
				for (OpePort port : operator.getPorts()) {
					nodeMap.put(port, new GenPort(port));
				}
			}
		}

		appendDescribeVariable(sb);

		for (int i = 0;;) {
			if (opeList.isEmpty()) {
				break;
			}
			if (i >= opeList.size()) {
				i = 0;
			}
			GenOperator node = opeList.get(i);
			if (node.complete(map)) {
				appendNode(sb, node, map);
				map.put(node.getId(), node);
				opeList.remove(node);
			} else {
				i++;
			}
		}

		sb.append("\n");
		for (JobPort port : frame.getOutputPorts()) {
			appendExporter(sb, port);
		}

		sb.append("\t}\n");
	}

	private void appendDescribeVariable(StringBuilder sb) {
		for (Entry<String, String> entry : factoryVarMap.entrySet()) {
			String factory = getCachedClassName(entry.getKey());
			String variable = entry.getValue();
			sb.append("\t\t");
			sb.append(factory);
			sb.append(" ");
			sb.append(variable);
			sb.append(" = new ");
			sb.append(factory);
			sb.append("();\n");
		}
		sb.append("\n");
	}

	private void appendNode(StringBuilder sb, GenOperator node, Map<Integer, GenNode> map) {
		sb.append("\t\t");
		sb.append("// ");
		sb.append(node.getDescription());
		sb.append("\n");

		sb.append("\t\t");
		String rtype = node.getReturnTypeName();
		if (rtype != null) {
			sb.append(rtype);
			sb.append(" ");
			sb.append(node.getDefVariableName());
			sb.append(" = ");
		}
		sb.append(getFactoryVariableName(node.getFactoryName(), node.isFlowPart()));
		sb.append(".");
		sb.append(node.getMethodName());
		sb.append("(");
		node.appendArgsTo(sb);
		sb.append(");\n");
	}

	private void appendExporter(StringBuilder sb, JobPort port) {
		for (NodeElement node : port.getInputNodes()) {
			GenNode gen = nodeMap.get(node);
			sb.append("\t\tthis.");
			sb.append(port.getName());
			sb.append(".add(");
			sb.append(gen.getUseVariableName());
			sb.append(");\n");
		}
	}

	private abstract class GenNode {
		protected List<NodeElement> incomings = new ArrayList<NodeElement>();

		public GenNode(NodeElement node) {
			for (Connection c : node.getIncomings()) {
				NodeElement source = c.getSource();
				incomings.add(source);
			}
		}

		public boolean complete(Map<Integer, GenNode> map) {
			for (NodeElement node : incomings) {
				if (!map.containsKey(node.getId())) {
					NodeElement parent = node.getParent();
					if (parent == null) {
						return false;
					}
					if (!map.containsKey(parent.getId())) {
						return false;
					}
				}
			}
			return true;
		}

		private String variableName;

		public String getDefVariableName() {
			if (variableName == null) {
				variableName = getIdentifiedName(createVariableName());
			}
			return variableName;
		}

		public String getUseVariableName() {
			return getDefVariableName();
		}

		protected abstract String createVariableName();
	}

	private class GenImporter extends GenNode {
		private JobPort port;

		public GenImporter(JobPort port) {
			super(port);
			this.port = port;
		}

		@Override
		public String createVariableName() {
			return "this." + port.getName();
		}
	}

	private class GenOperator extends GenNode {
		private OperatorNode node;

		public GenOperator(OperatorNode node) {
			super(node);
			this.node = node;
			for (OpePort port : node.getInputPorts()) {
				NodeElement cnode = port.getConnectedNode();
				if (cnode != null) {
					incomings.add(cnode);
				}
			}
		}

		public Integer getId() {
			return node.getId();
		}

		public boolean isFlowPart() {
			return node.isFlowPart();
		}

		public String getMethodName() {
			if (node.isCoreOperator()) {
				return node.getType();
			}
			if (node.isFlowPart()) {
				return "create";
			}
			return node.getMethodName();
		}

		public String getClassName() {
			String name = node.getClassName();
			if (StringUtil.isEmpty(name)) {
				return "undefined_className_" + getId();
			} else {
				return name;
			}
		}

		public String getFactoryName() {
			if (node.isCoreOperator()) {
				return CORE_OP_FACTORY;
			}
			return getClassName() + "Factory";
		}

		public String getReturnTypeName() {
			if ("stop".equals(node.getType())) {
				return null;
			}
			if (node.isFlowPart()) {
				return getCachedClassName(getFactoryName() + "." + StringUtil.getSimpleName(getClassName()));
			}

			String type = getMethodName();
			String className = getCachedClassName(getFactoryName() + "." + StringUtil.toFirstUpper(type));

			String nodeType = node.getType();
			if ("restructure".equals(nodeType) || "extend".equals(nodeType) || "project".equals(nodeType)
					|| "checkpoint".equals(nodeType)) {
				OpePort port = node.getOutputPort();
				String modelClass = getCachedClassName(getModelClassName(port));
				return className + "<" + modelClass + ">";
			}
			if ("empty".equals(nodeType) || "confluent".equals(nodeType)) {
				String modelClass = findModelClass(node);
				modelClass = getCachedClassName(modelClass);
				return className + "<" + modelClass + ">";
			}

			return className;
		}

		public Object getDescription() {
			return node.getDescription();
		}

		@Override
		public String createVariableName() {
			return StringUtil.toFirstLower(node.getMethodName());
		}

		@Override
		public String getUseVariableName() {
			if (node.getOutputPorts().isEmpty()) { // empty, confluent
				return super.getUseVariableName() + ".out";
			}
			return super.getUseVariableName();
		}

		public void appendArgsTo(StringBuilder sb) {
			List<String> args = new ArrayList<String>();
			for (OpePort port : node.getInputPorts()) {
				NodeElement cnode = port.getConnectedNode();
				GenNode gen = nodeMap.get(cnode);
				String varName = (gen != null) ? gen.getUseVariableName() : "null";
				args.add(varName);
			}
			for (Connection c : node.getIncomings()) {
				NodeElement cnode = c.getOpposite(node);
				GenNode gen = nodeMap.get(cnode);
				String varName = (gen != null) ? gen.getUseVariableName() : "null";
				args.add(varName);
			}
			for (OpeParameter param : node.getParameterList()) {
				args.add(getQualifiedValue(param.getClassName(), param.getValue()));
			}
			String nodeType = node.getType();
			if ("restructure".equals(nodeType) || "extend".equals(nodeType) || "project".equals(nodeType)) {
				OpePort port = node.getOutputPort();
				String modelClass = getModelClassName(port);
				args.add(getQualifiedValue("java.lang.Class", modelClass));
			} else if ("empty".equals(nodeType)) {
				String modelClass = findModelClass(node);
				args.add(getQualifiedValue("java.lang.Class", modelClass));
			}

			if ("confluent".equals(nodeType)) {
				switch (args.size()) {
				case 2:
				case 3:
				case 4:
					break;
				default:
					sb.append(getCachedClassName("java.util.Arrays"));
					sb.append(".asList(");
					StringUtil.mkString(sb, args);
					sb.append(")");
					return;
				}
			}
			StringUtil.mkString(sb, args);
		}
	}

	private class GenPort extends GenNode {
		private OpePort port;

		public GenPort(OpePort port) {
			super(port);
			this.port = port;
		}

		@Override
		public String createVariableName() {
			NodeElement operator = port.getParent();
			GenNode gen = nodeMap.get(operator);
			return gen.getUseVariableName() + "." + port.getName();
		}
	}

	private Map<NodeElement, String> modelClassMap = new HashMap<NodeElement, String>();

	private static final String UNDEFINED_CLASS = new String("UNIDEFINED");

	private String findModelClass(NodeElement node) {
		String name = modelClassMap.get(node);
		if (name != null) {
			return name;
		}
		if (node instanceof HasDataModelNode) {
			name = getModelClassName((HasDataModelNode) node);
			if (name == null) {
				name = UNDEFINED_CLASS;
			}
			modelClassMap.put(node, name);
			return name;
		}
		modelClassMap.put(node, UNDEFINED_CLASS);
		for (Connection c : node.getIncomings()) {
			NodeElement cnode = c.getOpposite(node);
			name = findModelClass(cnode);
			if (name != UNDEFINED_CLASS) {
				modelClassMap.put(node, name);
				return name;
			}
		}
		for (Connection c : node.getOutgoings()) {
			NodeElement cnode = c.getOpposite(node);
			name = findModelClass(cnode);
			if (name != UNDEFINED_CLASS) {
				modelClassMap.put(node, name);
				return name;
			}
		}
		return UNDEFINED_CLASS;
	}

	protected String getQualifiedValue(String className, String value) {
		if ("java.lang.String".equals(className)) {
			return "\"" + value + "\"";
		}
		if ("java.lang.Class".equals(className)) {
			String name = getCachedClassName(value);
			return name + ".class";
		}
		return value;
	}
}
