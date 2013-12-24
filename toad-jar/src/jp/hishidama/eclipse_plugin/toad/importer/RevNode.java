package jp.hishidama.eclipse_plugin.toad.importer;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.asakusafw.vocabulary.flow.graph.FlowElement;
import com.asakusafw.vocabulary.flow.graph.FlowElementDescription;
import com.asakusafw.vocabulary.flow.graph.FlowElementPort;

public abstract class RevNode {
	public static enum Kind {
		INPUT, OUTPUT, OPERATOR, FLOWPART
	}

	// private final FlowElement flowElement;

	public RevNode(FlowElement flowElement) {
		// this.flowElement = flowElement;
	}

	public static RevNode analyze(AtomicInteger id, FlowElement element) {
		FlowElementDescription desc = element.getDescription();
		switch (desc.getKind()) {
		case INPUT:
			return new RevInput(id, element);
		case OUTPUT:
			return new RevOutput(id, element);
		case OPERATOR:
			return new RevOperator(id, element);
		case FLOW_COMPONENT:
			return new RevFlowPart(id, element);
		case PSEUD:
			return new RevPseudOperator(id, element);
		default:
			throw new UnsupportedOperationException("kind=" + desc.getKind());
		}
	}

	public abstract Kind getKind();

	public final Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("kind", getKind().name());
		toMap(map);
		return map;
	}

	protected abstract void toMap(Map<String, Object> map);

	protected static String getClassName(Type type) {
		return ((Class<?>) type).getName();
	}

	public abstract int getId(FlowElementPort port);
}
