package jp.hishidama.eclipse_plugin.toad.importer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.asakusafw.vocabulary.flow.graph.FlowElement;
import com.asakusafw.vocabulary.flow.graph.FlowElementPort;
import com.asakusafw.vocabulary.flow.graph.OperatorDescription.Parameter;

public abstract class RevOperatorBase extends RevNode {

	protected final int id;
	protected final List<RevPort> inputPort = new ArrayList<RevPort>();
	protected final List<RevPort> outputPort = new ArrayList<RevPort>();

	public RevOperatorBase(AtomicInteger id, FlowElement flowElement) {
		super(flowElement);
		this.id = id.incrementAndGet();
	}

	@Override
	public int getId(FlowElementPort port) {
		String portName = port.getDescription().getName();
		for (RevPort p : inputPort) {
			if (portName.equals(p.getName())) {
				return p.getId();
			}
		}
		for (RevPort p : outputPort) {
			if (portName.equals(p.getName())) {
				return p.getId();
			}
		}
		return id;
	}

	protected void portToMap(Map<String, Object> map) {
		List<Map<String, Object>> input = new ArrayList<Map<String, Object>>();
		for (RevPort port : inputPort) {
			addPort(input, port);
		}
		map.put("input", input);
		List<Map<String, Object>> output = new ArrayList<Map<String, Object>>();
		for (RevPort port : outputPort) {
			addPort(output, port);
		}
		map.put("output", output);
	}

	private void addPort(List<Map<String, Object>> ports, RevPort port) {
		Map<String, Object> map = port.toMap();
		ports.add(map);
	}

	protected void addParam(List<Map<String, Object>> params, Parameter param) {
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("name", param.getName());
		map.put("className", ((Class<?>) param.getType()).getName());
		Object value = param.getValue();
		map.put("value", (value != null) ? String.valueOf(param.getValue()) : null);

		params.add(map);
	}
}
