package jp.hishidama.eclipse_plugin.toad.importer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

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
		FlowGraphGenerator generator = getFlowGraphGenerator();
		FlowGraph graph = generator.analyze(className, parameterNames, parameterValues);
		return getDependencies(graph, seed);
	}

	private FlowGraphGenerator getFlowGraphGenerator() {
		List<String> versionList = Arrays.asList("0100", "040");

		Throwable last = null;
		for (String version : versionList) {
			try {
				@SuppressWarnings("unchecked")
				Class<FlowGraphGenerator> c = (Class<FlowGraphGenerator>) Class.forName(FlowGraphGenerator.class
						.getName() + version);
				FlowGraphGenerator generator = c.newInstance();
				generator.checkVersion();
				return generator;
			} catch (NoClassDefFoundError e) {
				last = e;
			} catch (Exception e) {
				last = e;
			}
		}

		if (last instanceof Error) {
			throw (Error) last;
		}
		throw new RuntimeException(last);
	}

	protected Collection<Map<String, Object>> getDependencies(FlowGraph graph, int seed) {
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

		sortImporter(graph.getFlowInputs());
		sortExporter(graph.getFlowOutputs());

		return list;
	}

	private void sortImporter(List<FlowIn<?>> list) {
		final List<String> names = new ArrayList<String>(list.size());
		for (FlowIn<?> in : list) {
			String name = in.getDescription().getName();
			names.add(name);
		}
		Collections.sort(importer, new Sorter(names));
	}

	private void sortExporter(List<FlowOut<?>> list) {
		final List<String> names = new ArrayList<String>(list.size());
		for (FlowOut<?> out : list) {
			String name = out.getDescription().getName();
			names.add(name);
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
