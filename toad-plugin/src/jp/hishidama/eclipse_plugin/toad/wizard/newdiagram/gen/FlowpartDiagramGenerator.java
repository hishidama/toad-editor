package jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.gen;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hishidama.eclipse_plugin.toad.clazz.FlowPartClass;
import jp.hishidama.eclipse_plugin.toad.clazz.JavaDelegator.Parameter;
import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.diagram.DiagramType;
import jp.hishidama.eclipse_plugin.toad.model.frame.FlowpartFrameNode;
import jp.hishidama.eclipse_plugin.toad.model.frame.FlowpartParameterDef;
import jp.hishidama.eclipse_plugin.toad.model.frame.FrameNode;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OpeParameter;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;
import jp.hishidama.eclipse_plugin.toad.model.node.port.OpePort;
import jp.hishidama.eclipse_plugin.util.ToadJavaUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class FlowpartDiagramGenerator extends FlowDiagramGenerator {
	private OperatorNode operator;

	public FlowpartDiagramGenerator(IProject project, OperatorNode operator) {
		super(project);
		this.operator = operator;
	}

	public static FlowPartClass findFlowpart(IFile file) {
		ICompilationUnit unit = ToadJavaUtil.getJavaUnit(file);
		if (unit == null) {
			return null;
		}
		return findFlowpart(unit);
	}

	public static FlowPartClass findFlowpart(ICompilationUnit unit) {
		try {
			IType[] types = unit.getTypes();
			for (IType type : types) {
				FlowPartClass flow = new FlowPartClass(type);
				if (flow.isDsl()) {
					return flow;
				}
			}
		} catch (JavaModelException e) {
			return null;
		}
		return null;
	}

	private FlowPartClass flow;

	public Diagram generateDiagram(FlowPartClass flow, Map<String, Object> parameterValues) throws IOException {
		this.flow = flow;
		return generateDiagram(flow.getType(), flow.getJavadoc(), flow.getName(), flow.getParameterNames(),
				parameterValues);
	}

	@Override
	protected FrameNode createEmptyFrame() {
		FlowpartFrameNode frame = new FlowpartFrameNode();
		return frame;
	}

	@Override
	protected void initializeDiagram(Diagram diagram) {
		diagram.setDiagramType(DiagramType.FLOWPART);
		diagram.setType("flowpart");
	}

	@Override
	public void initialize() {
		super.initialize();
		if (operator != null) {
			frame.setMemo(operator.getMemo());
			FlowpartFrameNode fframe = (FlowpartFrameNode) frame;
			for (OpeParameter param : operator.getParameterList()) {
				FlowpartParameterDef def = new FlowpartParameterDef();
				def.setName(param.getName());
				def.setClassName(param.getClassName());
				fframe.addParameter(def);
			}
		}
	}

	@Override
	public void addDefaultPort(IProject project) {
		if (operator == null) {
			super.addDefaultPort(project);
			return;
		}

		int id = 2;
		{
			List<OpePort> list = operator.getInputPorts();
			int i = 0;
			for (OpePort port : list) {
				Map<String, Object> map = createMap(port, id++);
				createInPort(frame, map, list.size(), i++, project);
			}
		}
		{
			List<OpePort> list = operator.getOutputPorts();
			int i = 0;
			for (OpePort port : list) {
				Map<String, Object> map = createMap(port, id++);
				createOutPort(frame, map, list.size(), i++, project);
			}
		}
	}

	private static Map<String, Object> createMap(OpePort port, int id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("name", port.getName());
		map.put("description", port.getDescription());
		map.put("modelName", port.getModelName());
		map.put("modelDescription", port.getModelDescription());
		map.put("memo", port.getMemo());
		return map;
	}

	@Override
	protected void createParameter(FrameNode frame0) {
		FlowpartFrameNode frame = (FlowpartFrameNode) frame0;
		for (Parameter param : flow.getParameters()) {
			FlowpartParameterDef def = new FlowpartParameterDef();
			def.setName(param.name);
			def.setClassName(param.className);
			frame.addParameter(def);
		}
	}
}
