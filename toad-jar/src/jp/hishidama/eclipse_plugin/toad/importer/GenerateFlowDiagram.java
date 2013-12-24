package jp.hishidama.eclipse_plugin.toad.importer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import com.asakusafw.compiler.flow.plan.FlowGraphUtil;
import com.asakusafw.vocabulary.flow.graph.FlowElement;
import com.asakusafw.vocabulary.flow.graph.FlowElementInput;
import com.asakusafw.vocabulary.flow.graph.FlowElementOutput;
import com.asakusafw.vocabulary.flow.graph.FlowGraph;
import com.asakusafw.vocabulary.flow.graph.FlowIn;
import com.asakusafw.vocabulary.flow.graph.FlowOut;

public class GenerateFlowDiagram {

	private List<Map<String, Object>> importer = new ArrayList<Map<String, Object>>();
	private List<Map<String, Object>> exporter = new ArrayList<Map<String, Object>>();
	private List<int[]> connection = new ArrayList<int[]>();

	public Collection<Map<String, Object>> getDependencies(String className, List<String> parameterNames,
			Map<String, Object> parameterValues, int seed) throws Exception {
		FlowDriver driver = new FlowDriver(className, parameterNames, parameterValues);
		FlowGraph graph = driver.analyze();
		List<Object> ports = driver.getPorts();
		return getDependencies(graph, ports, seed);
	}

	protected Collection<Map<String, Object>> getDependencies(FlowGraph graph, List<Object> ports, int seed) {
		// node
		AtomicInteger id = new AtomicInteger(seed);
		Map<FlowElement, RevNode> map = new LinkedHashMap<FlowElement, RevNode>();
		for (FlowElement element : FlowGraphUtil.collectElements(graph)) {
			RevNode node = RevNode.analyze(id, element);
			map.put(element, node);
		}

		// connection
		for (Entry<FlowElement, RevNode> entry : map.entrySet()) {
			FlowElement element = entry.getKey();
			RevNode node = entry.getValue();
			for (FlowElementOutput port : element.getOutputPorts()) {
				for (FlowElementInput oppositePort : port.getOpposites()) {
					RevNode oppositeNode = map.get(oppositePort.getOwner());
					int sourceId = node.getId(port);
					int targetId = oppositeNode.getId(oppositePort);
					connection.add(new int[] { sourceId, targetId });
				}
			}
		}

		// convert to Map
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (RevNode node : map.values()) {
			switch (node.getKind()) {
			case OPERATOR:
			case FLOWPART:
				list.add(node.toMap());
				break;
			case INPUT:
				importer.add(node.toMap());
				break;
			case OUTPUT:
				exporter.add(node.toMap());
				break;
			default:
				throw new UnsupportedOperationException("kind=" + node.getKind());
			}
		}

		sortImporter(ports);
		sortExporter(ports);

		return list;
	}

	private void sortImporter(List<Object> ports) {
		final List<String> names = new ArrayList<String>();
		for (Object obj : ports) {
			if (obj instanceof FlowIn) {
				FlowIn<?> in = (FlowIn<?>) obj;
				String name = in.getDescription().getName();
				names.add(name);
			}
		}
		Collections.sort(importer, new Sorter(names));
	}

	private void sortExporter(List<Object> ports) {
		final List<String> names = new ArrayList<String>();
		for (Object obj : ports) {
			if (obj instanceof FlowOut) {
				FlowOut<?> out = (FlowOut<?>) obj;
				String name = out.getDescription().getName();
				names.add(name);
			}
		}
		Collections.sort(exporter, new Sorter(names));
	}

	private static class Sorter implements Comparator<Map<String, Object>> {
		private List<String> names;

		public Sorter(List<String> names) {
			this.names = names;
		}

		@Override
		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			String name1 = (String) o1.get("name");
			String name2 = (String) o2.get("name");
			int n1 = names.indexOf(name1);
			int n2 = names.indexOf(name2);
			return n1 - n2;
		}
	}

	public List<Map<String, Object>> getImporter() {
		return importer;
	}

	public List<Map<String, Object>> getExporter() {
		return exporter;
	}

	public List<int[]> getConnection() {
		return connection;
	}
}
