package jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.gen;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hishidama.eclipse_plugin.jdt.util.TypeUtil;
import jp.hishidama.eclipse_plugin.toad.clazz.FlowPartClass;
import jp.hishidama.eclipse_plugin.toad.clazz.JavadocClass;
import jp.hishidama.eclipse_plugin.toad.clazz.OperatorMethod;
import jp.hishidama.eclipse_plugin.toad.editor.action.layout.FrameAutoLayout;
import jp.hishidama.eclipse_plugin.toad.editor.action.layout.GefAutoLayout;
import jp.hishidama.eclipse_plugin.toad.editor.action.layout.MarkerAutoLayout;
import jp.hishidama.eclipse_plugin.toad.editor.drop.nodegen.FlowpartNodeGenerator;
import jp.hishidama.eclipse_plugin.toad.editor.drop.nodegen.OperatorNodeGenerator;
import jp.hishidama.eclipse_plugin.toad.internal.util.JarUtil;
import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;
import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.frame.FrameNode;
import jp.hishidama.eclipse_plugin.toad.model.frame.JobFrameNode;
import jp.hishidama.eclipse_plugin.toad.model.node.Attribute;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.datafile.DataFileNode;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OpeParameter;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;
import jp.hishidama.eclipse_plugin.toad.model.node.port.JobPort;
import jp.hishidama.eclipse_plugin.toad.model.node.port.OpePort;
import jp.hishidama.eclipse_plugin.util.StringUtil;
import jp.hishidama.xtext.dmdl_editor.dmdl.ModelDefinition;
import jp.hishidama.xtext.dmdl_editor.dmdl.ModelUiUtil;
import jp.hishidama.xtext.dmdl_editor.dmdl.ModelUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

public abstract class FlowDiagramGenerator extends DiagramFileGenerator {

	private IProject project;
	private IJavaProject javaProject;
	protected Diagram diagram;
	protected FrameNode frame;

	public FlowDiagramGenerator(IProject project) {
		this.project = project;
	}

	protected final IJavaProject getJavaProject() {
		if (javaProject == null) {
			javaProject = JavaCore.create(project);
		}
		return javaProject;
	}

	@Override
	public Diagram createEmptyDiagram() {
		diagram = new Diagram();
		initializeDiagram(diagram);

		frame = createEmptyFrame();
		frame.setId(1);
		frame.setX(frame.getHorizontalMargin());
		frame.setY(frame.getVerticalMargin());
		frame.setWidth(256 + 128);
		frame.setHeight(256);
		diagram.addContent(frame);

		return diagram;
	}

	protected abstract FrameNode createEmptyFrame();

	protected abstract void initializeDiagram(Diagram diagram);

	public void initialize() {
		frame.setType(diagram.getType());
	}

	public void addDefaultPort(IProject project) {
		{
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", 2);
			map.put("name", "in");
			map.put("description", "入力");
			createInPort(frame, map, 1, 0, project);
		}
		{
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", 3);
			map.put("name", "out");
			map.put("description", "出力");
			createOutPort(frame, map, 1, 0, project);
		}
	}

	protected JobPort createInPort(FrameNode frame, Map<String, Object> map, int count, int index, IProject project) {
		JobPort port = createPort(frame, map, count, index, project);

		port.setIn(true);
		port.setCx(frame.getX());
		port.setNamePosition(PositionConstants.TOP);
		frame.addPort(port, NodeElement.LEFT);

		return port;
	}

	protected JobPort createOutPort(FrameNode frame, Map<String, Object> map, int count, int index, IProject project) {
		JobPort port = createPort(frame, map, count, index, project);

		port.setOut(true);
		port.setCx(frame.getX() + frame.getWidth());
		port.setNamePosition(PositionConstants.BOTTOM);
		frame.addPort(port, NodeElement.RIGHT);

		return port;
	}

	private JobPort createPort(FrameNode frame, Map<String, Object> map, int count, int index, IProject project) {
		JobPort port = new JobPort();

		port.setId(getInt(map, "id"));
		port.setName(getString(map, "name"));
		port.setDescription(getString(map, "description"));
		String modelName = getString(map, "modelName");
		String modelClassName = getString(map, "modelClassName");
		ModelDefinition model = ModelUiUtil.findModel(project, modelName);
		if (model == null) {
			model = ModelUiUtil.findModelByClass(project, modelClassName);
		}
		if (model != null) {
			port.setModelName(model.getName());
			port.setModelDescription(ModelUtil.getDecodedDescription(model));
		} else {
			if (modelName != null) {
				port.setModelName(modelName);
			} else {
				port.setModelName(StringUtil.toSnakeCase(StringUtil.getSimpleName(modelClassName)));
			}
			port.setModelDescription(getString(map, "modelDescription"));
		}
		port.setCy(frame.getY() + frame.getHeight() * (index + 1) / (count + 1));
		port.setMemo(getString(map, "memo"));

		idMap.put(port.getId(), port);
		return port;
	}

	protected Diagram generateDiagram(IType type, JavadocClass javadoc, String name, List<String> parameterNames,
			Map<String, Object> parameterValues) throws IOException {
		Collection<Map<String, Object>> list = getDependencies(type, parameterNames, parameterValues);

		IJavaProject javaProject = type.getJavaProject();
		IProject project = javaProject.getProject();

		diagram = createEmptyDiagram();
		// diagram.setClassName(type.getFullyQualifiedName());
		// diagram.setName(name);
		{
			frame.setType(diagram.getType());
			if (frame instanceof JobFrameNode) {
				((JobFrameNode) frame).setName(name);
			}
			String className = type.getFullyQualifiedName();
			String desc = javadoc.getTitle();
			if (desc == null) {
				desc = StringUtil.getSimpleName(className);
			}
			frame.setDescription(desc);
			frame.setMemo(javadoc.getMemo());
			frame.setClassName(className);
			int count = Math.max(importer.size(), exporter.size());
			int h = (48 + 16) * (count + 1);
			if (h > frame.getHeight()) {
				frame.setHeight(h);
			}
			{ // Importer
				int i = 0;
				for (Map<String, Object> port : importer) {
					JobPort c = createInPort(frame, port, importer.size(), i++, project);
					createInputFileNode(port, c);
				}
			}
			{ // Exporter
				int i = 0;
				for (Map<String, Object> port : exporter) {
					JobPort c = createOutPort(frame, port, exporter.size(), i++, project);
					createOutputFileNode(port, c);
				}
			}
			createParameter(diagram.getFrameNode());
		}

		// Operator
		List<OperatorNode> opeList = new ArrayList<OperatorNode>();
		for (Map<String, Object> attrs : list) {
			OperatorNode op = createOperator(javaProject, attrs);
			opeList.add(op);
			diagram.addContent(op);
		}

		{ // Connection
			for (int[] c : connection) {
				NodeElement source = idMap.get(c[0]);
				NodeElement target = idMap.get(c[1]);
				if (source == null || target == null) {
					System.out.printf("not found connection %d->%d (%s->%s)%n", c[0], c[1], source, target);
					continue;
				}
				createConnection(source, target);
			}
		}

		// CoreOperator
		for (OperatorNode op : opeList) {
			modifyCoreOperator(op);
		}

		{ // layout
			List<NodeElement> nodeList = new ArrayList<NodeElement>(opeList.size());
			for (OperatorNode job : opeList) {
				nodeList.add(job);
			}
			new GefAutoLayout().run(nodeList, null);

			new FrameAutoLayout().run(diagram, null);
			new MarkerAutoLayout().run(diagram, null);
		}

		return diagram;
	}

	protected void createParameter(FrameNode frame) {
		// do override
	}

	protected void createInputFileNode(Map<String, Object> port, JobPort c) {
		// do override
	}

	protected void createOutputFileNode(Map<String, Object> port, JobPort c) {
		// do override
	}

	protected DataFileNode createFile(Map<String, Object> map, int y) {
		DataFileNode node = new DataFileNode();
		node.setId(getInt(map, "fileId"));
		String className = getString(map, "fileClassName");
		node.setClassName(className);
		JavadocClass javadoc = JavadocClass.getJavadoc(getJavaProject(), className);
		String desc = javadoc.getTitle();
		if (desc == null) {
			desc = StringUtil.getSimpleName(node.getClassName());
		}
		node.setDescription(desc);
		node.setMemo(javadoc.getMemo());
		node.load(getJavaProject());
		node.setY(y - node.getHeight() / 2);

		idMap.put(node.getId(), node);
		return node;
	}

	private List<Map<String, Object>> importer;
	private List<Map<String, Object>> exporter;
	private List<int[]> connection;

	@SuppressWarnings("unchecked")
	private Collection<Map<String, Object>> getDependencies(IType type, List<String> parameterNames,
			Map<String, Object> parameterValues) throws IOException {
		try {
			IJavaProject javaProject = type.getJavaProject();
			Class<?> clazz = JarUtil.loadClass(javaProject,
					"jp.hishidama.eclipse_plugin.toad.importer.GenerateFlowDiagram");

			Object generator = clazz.newInstance();
			Method getter = clazz.getMethod("getDependencies", String.class, List.class, Map.class, int.class);

			int seed = 1; // JobFrameのIDを1とし、リバースしたノードのIDはそれ以降を使う
			Collection<Map<String, Object>> map = (Collection<Map<String, Object>>) getter.invoke(generator,
					type.getFullyQualifiedName(), parameterNames, parameterValues, seed);
			{
				Method m = clazz.getMethod("getImporter");
				importer = (List<Map<String, Object>>) m.invoke(generator);
			}
			{
				Method m = clazz.getMethod("getExporter");
				exporter = (List<Map<String, Object>>) m.invoke(generator);
			}
			{
				Method m = clazz.getMethod("getConnection");
				connection = (List<int[]>) m.invoke(generator);
			}
			return map;
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			String causeMessage = "Diagram生成中のエラー";
			for (Throwable t = e; t != null; t = t.getCause()) {
				String message = t.getMessage();
				if (message != null && !message.isEmpty()) {
					causeMessage = t.getMessage();
				}
			}
			throw new IOException(causeMessage, e);
		}
	}

	private OperatorNode createOperator(IJavaProject javaProject, Map<String, Object> map) {
		int id = getInt(map, "id");
		String type = getString(map, "type");
		String className = getString(map, "className");
		String name = StringUtil.getSimpleName(getString(map, "name"));

		OperatorNode op;
		if ("FlowPart".equals(type)) {
			op = createFlowpart(javaProject, className, id, map);
		} else {
			char c = type.charAt(0);
			if (c == '@') {
				op = createUserOperator(javaProject, className, name, id, map);
			} else {
				op = createCoreOperator(type, name, id, map);
			}
		}

		op.setX(frame.getX() + 32);
		op.setY(frame.getY() + 48);

		idMap.put(op.getId(), op);
		return op;
	}

	private OperatorNode createUserOperator(IJavaProject javaProject, final String className, final String methodName,
			int id, final Map<String, Object> map) {
		OperatorMethod operator;
		try {
			IType type = javaProject.findType(className);
			IMethod method = TypeUtil.findMethod(type, methodName);
			if (method == null) {
				throw new IllegalStateException(MessageFormat.format("not found method. name={0}#{1}", className,
						methodName));
			}
			operator = new OperatorMethod(method);
		} catch (JavaModelException e) {
			throw new IllegalStateException(e);
		}
		OperatorNodeGenerator gen = new OperatorNodeGenerator(project) {
			@Override
			protected int newPortId(OpePort port, boolean in, String name) {
				return getPortId(className, methodName, port, in, name, map);
			}
		};
		OperatorNode node = gen.createOperatorNode(operator, id);
		createParameters(node, map);
		return node;
	}

	private OperatorNode createFlowpart(IJavaProject javaProject, final String className, int id,
			final Map<String, Object> map) {
		FlowPartClass flowpart;
		try {
			IType type = javaProject.findType(className);
			flowpart = new FlowPartClass(type);
		} catch (JavaModelException e) {
			throw new IllegalStateException(e);
		}
		FlowpartNodeGenerator gen = new FlowpartNodeGenerator(project) {
			@Override
			protected int newPortId(OpePort port, boolean in, String name) {
				return getPortId(className, "create", port, in, name, map);
			}
		};
		OperatorNode node = gen.createFlowpart(flowpart, id);
		createParameters(node, map);
		return node;
	}

	int getPortId(String className, String methodName, OpePort port, boolean in, String name, Map<String, Object> map) {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) (in ? map.get("input") : map.get("output"));
		for (Map<String, Object> m : list) {
			if (name.equals(getString(m, "name"))) {
				int id = getInt(m, "id");
				idMap.put(id, port);
				return id;
			}
		}
		throw new IllegalStateException(MessageFormat.format(
				"not found port. class={0}#{1}, portName={2}({3}), list={4}", className, methodName, name, in ? "in"
						: "out", list));
	}

	@SuppressWarnings("unchecked")
	private OperatorNode createCoreOperator(String type, String name, int id, Map<String, Object> map) {
		OperatorNode op = new OperatorNode();
		op.setId(id);
		if ("pseud".equals(type)) {
			type = name;
		}
		op.setType(type);
		op.setDescription(name);
		op.setClassName("com.asakusafw.vocabulary.flow.util.CoreOperators");
		op.setMethodName(name);
		op.setAttributeList(op.getDefaultAttribute());

		{
			List<Map<String, Object>> input = (List<Map<String, Object>>) map.get("input");
			List<Map<String, Object>> output = (List<Map<String, Object>>) map.get("output");
			int count = Math.max(input.size(), output.size());
			int h = 18 * (count + 1);
			if (h > op.getHeight()) {
				op.setHeight(h);
			}
			{
				int i = 0;
				for (Map<String, Object> port : input) {
					createInPort(op, port, input.size(), i++, project);
				}
			}
			{
				int i = 0;
				for (Map<String, Object> port : output) {
					createOutPort(op, port, output.size(), i++, project);
				}
			}
		}

		op.setDescription(op.getDelegate().getDescription());

		return op;
	}

	private OpePort createInPort(OperatorNode op, Map<String, Object> map, int count, int index, IProject project) {
		OpePort port = createPort(op, map, count, index, project);

		port.setIn(true);
		port.setCx(op.getX());
		port.setNamePosition((count <= 1) ? PositionConstants.NONE : PositionConstants.LEFT);
		op.addPort(port, NodeElement.LEFT);

		return port;
	}

	private OpePort createOutPort(OperatorNode op, Map<String, Object> map, int count, int index, IProject project) {
		OpePort port = createPort(op, map, count, index, project);

		port.setOut(true);
		port.setCx(op.getX() + op.getWidth());
		port.setNamePosition((count <= 1) ? PositionConstants.NONE : PositionConstants.RIGHT);
		op.addPort(port, NodeElement.RIGHT);

		return port;
	}

	private OpePort createPort(OperatorNode op, Map<String, Object> map, int count, int index, IProject project) {
		OpePort port = new OpePort();

		port.setId(getInt(map, "id"));
		port.setName(getString(map, "name"));
		String modelClassName = getString(map, "className");
		ModelDefinition model = ModelUiUtil.findModelByClass(project, modelClassName);
		if (model != null) {
			port.setModelName(model.getName());
			port.setModelDescription(ModelUtil.getDecodedDescription(model));
		} else {
			port.setModelName(StringUtil.toSnakeCase(StringUtil.getSimpleName(modelClassName)));
		}
		port.setCy(op.getY() + op.getHeight() * (index + 1) / (count + 1));

		if (!"@Summarize".equals(op.getType())) {
			String KEY = "com.asakusafw.vocabulary.model.Key";
			createAttribute(port, map, "shuffleKey.group", KEY, "group", "java.lang.String");
			createAttribute(port, map, "shuffleKey.order", KEY, "order", "java.lang.String");
		}
		idMap.put(port.getId(), port);
		return port;
	}

	private void createAttribute(OpePort port, Map<String, Object> map, String mapName, String annotationName,
			String parameterName, String valueType) {
		@SuppressWarnings("unchecked")
		List<String> list = (List<String>) map.get(mapName);
		if (list != null) {
			Attribute attr = new Attribute(annotationName, parameterName, valueType);
			for (String s : list) {
				attr.addValue(s);
			}
			port.addAttribute(attr);
		}
	}

	private void createParameters(OperatorNode op, Map<String, Object> map) {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> parameter = (List<Map<String, Object>>) map.get("parameter");
		for (Map<String, Object> param : parameter) {
			createParameter(op, param);
		}
	}

	private void createParameter(OperatorNode op, Map<String, Object> map) {
		String name = getString(map, "name");
		for (OpeParameter param : op.getParameterList()) {
			if (name.equals(param.getName())) {
				param.setValue(getString(map, "value"));
				return;
			}
		}

		OpeParameter p = new OpeParameter();
		p.setName(name);
		p.setClassName(getString(map, "className"));
		p.setValue(getString(map, "value"));
		op.addParameter(p);
	}

	private Map<Integer, NodeElement> idMap = new HashMap<Integer, NodeElement>();

	private static int getInt(Map<String, Object> map, String key) {
		return (Integer) map.get(key);
	}

	private static String getString(Map<String, Object> map, String key) {
		return (String) map.get(key);
	}

	private void modifyCoreOperator(OperatorNode op) {
		String type = op.getType();
		if ("stop".equals(type) || "empty".equals(type) || "confluent".equals(type)) {
			upPort(op);
			op.setWidth(16);
			op.setHeight(16);
		}
	}

	private void upPort(OperatorNode op) {
		for (OpePort port : op.getInputPorts()) {
			for (Connection c : port.getIncomings()) {
				NodeElement old = c.getTarget();
				old.removeOutgoing(c);
				c.setTarget(op);
				op.addIncoming(c);
			}
			op.removePort(port);
		}
		for (OpePort port : op.getOutputPorts()) {
			for (Connection c : port.getOutgoings()) {
				NodeElement old = c.getSource();
				old.removeIncoming(c);
				c.setSource(op);
				op.addOutgoing(c);
			}
			op.removePort(port);
		}
	}
}
