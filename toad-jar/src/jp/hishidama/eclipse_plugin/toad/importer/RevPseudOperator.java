package jp.hishidama.eclipse_plugin.toad.importer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.asakusafw.vocabulary.flow.graph.FlowElement;
import com.asakusafw.vocabulary.flow.graph.FlowElementPortDescription;
import com.asakusafw.vocabulary.flow.util.PseudElementDescription;

public class RevPseudOperator extends RevOperatorBase {

	private final PseudElementDescription description;

	public RevPseudOperator(AtomicInteger id, FlowElement flowElement) {
		super(id, flowElement);
		description = (PseudElementDescription) flowElement.getDescription();

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
		return Kind.OPERATOR;
	}

	@Override
	protected void toMap(Map<String, Object> map) {
		map.put("id", id);

		String name = description.getName();
		map.put("name", name);
		map.put("type", "pseud");

		// Class<?> declaring = declaration.getDeclaring();
		// map.put("className", declaring.getName());

		portToMap(map);

		List<Map<String, Object>> params = new ArrayList<Map<String, Object>>();
		// for (Parameter param : description.getParameters()) {
		// addParam(params, param);
		// }
		map.put("parameter", params);
	}
}
