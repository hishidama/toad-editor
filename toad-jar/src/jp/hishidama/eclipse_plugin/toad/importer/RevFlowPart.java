package jp.hishidama.eclipse_plugin.toad.importer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.asakusafw.vocabulary.flow.graph.FlowElement;
import com.asakusafw.vocabulary.flow.graph.FlowElementPortDescription;
import com.asakusafw.vocabulary.flow.graph.FlowGraph;
import com.asakusafw.vocabulary.flow.graph.FlowPartDescription;

public class RevFlowPart extends RevOperatorBase {

	private final FlowPartDescription description;

	public RevFlowPart(AtomicInteger id, FlowElement flowElement) {
		super(id, flowElement);
		description = (FlowPartDescription) flowElement.getDescription();

		for (FlowElementPortDescription port : description.getInputPorts()) {
			RevPort rport = new RevPort(id, port);
			inputPort.add(rport);
		}
		for (FlowElementPortDescription port : description.getOutputPorts()) {
			RevPort rport = new RevPort(id, port);
			outputPort.add(rport);
		}
	}

	@Override
	public Kind getKind() {
		return Kind.FLOWPART;
	}

	@Override
	protected void toMap(Map<String, Object> map) {
		map.put("id", id);

		map.put("type", "FlowPart");
		map.put("name", description.getName());

		FlowGraph graph = description.getFlowGraph();
		map.put("className", graph.getDescription().getName());

		portToMap(map);

		List<Map<String, Object>> params = new ArrayList<Map<String, Object>>();
		// TODO パラメーター
		// for (Parameter param : description.getParameters()) {
		// addParam(params, param);
		// }
		map.put("parameter", params);
	}
}
