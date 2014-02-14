package jp.hishidama.eclipse_plugin.toad.editor.drop.nodegen;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.toad.clazz.FlowPartClass;
import jp.hishidama.eclipse_plugin.toad.clazz.JavaDelegator.Parameter;
import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.frame.FlowpartFrameNode;
import jp.hishidama.eclipse_plugin.toad.model.frame.FlowpartParameterDef;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;
import jp.hishidama.eclipse_plugin.toad.model.node.port.JobPort;
import jp.hishidama.eclipse_plugin.toad.model.node.port.OpePort;
import jp.hishidama.eclipse_plugin.util.ToadLayoutUtil;

import org.eclipse.core.resources.IProject;

public abstract class FlowpartNodeGenerator extends NodeGenerator {

	public FlowpartNodeGenerator(IProject project) {
		super(project);
	}

	public OperatorNode createFlowpart(FlowPartClass flowpart, int id) {
		OperatorNode node = new OperatorNode();
		node.setId(id);
		node.setType("FlowPart");
		node.setDescription(flowpart.getJavadoc().getTitle());
		node.setMemo(flowpart.getJavadoc().getMemo());
		String className = flowpart.getClassName();
		node.setClassName(className);
		node.setMethodName("create");

		List<OpePort> inList = new ArrayList<OpePort>();
		List<OpePort> outList = new ArrayList<OpePort>();
		for (Parameter param : flowpart.getAllParameters()) {
			if ("com.asakusafw.vocabulary.flow.In".equals(param.className)) {
				createOpePort(true, inList, "", param.name, param.typeParameter, param.attributes);
			} else if ("com.asakusafw.vocabulary.flow.Out".equals(param.className)) {
				createOpePort(false, outList, "", param.name, param.typeParameter, param.attributes);
			} else {
				createOpeParameter(node, null, param.name, param.className);
			}
		}
		ToadLayoutUtil.addPorts(node, inList, outList);

		return node;
	}

	public OperatorNode createFlowpart(Diagram source, int id) {
		OperatorNode node = new OperatorNode();
		node.setId(id);
		node.setType("FlowPart");
		node.setDescription(source.getDescription());
		node.setMemo(source.getMemo());
		node.setClassName(source.getClassName());
		node.setMethodName("create");

		FlowpartFrameNode frame = (FlowpartFrameNode) source.getFrameNode();
		List<OpePort> inList = new ArrayList<OpePort>();
		List<OpePort> outList = new ArrayList<OpePort>();
		for (JobPort port : frame.getPorts()) {
			boolean in = port.isIn();
			List<OpePort> list = in ? inList : outList;
			OpePort p = createOpePort(in, list, "", port.getName(), port.getModelName(), port.getModelDescription(),
					null);
			p.setDescription(port.getDescription());
		}
		for (FlowpartParameterDef param : frame.getParameterList()) {
			createOpeParameter(node, param.getDescription(), param.getName(), param.getClassName());
		}

		ToadLayoutUtil.addPorts(node, inList, outList);

		return node;
	}
}
