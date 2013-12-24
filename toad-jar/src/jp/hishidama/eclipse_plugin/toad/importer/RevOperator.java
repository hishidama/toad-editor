package jp.hishidama.eclipse_plugin.toad.importer;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.asakusafw.vocabulary.flow.graph.FlowElement;
import com.asakusafw.vocabulary.flow.graph.FlowElementPortDescription;
import com.asakusafw.vocabulary.flow.graph.OperatorDescription;
import com.asakusafw.vocabulary.flow.graph.OperatorDescription.Declaration;
import com.asakusafw.vocabulary.flow.graph.OperatorDescription.Parameter;
import com.asakusafw.vocabulary.flow.graph.OperatorHelper;

public class RevOperator extends RevOperatorBase {

	private final OperatorDescription description;

	public RevOperator(AtomicInteger id, FlowElement flowElement) {
		super(id, flowElement);
		description = (OperatorDescription) flowElement.getDescription();

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
		Declaration declaration = description.getDeclaration();

		String type = null;
		String name = description.getName();
		if (name != null) {
			map.put("name", name);
			if (name.lastIndexOf('.') < 0) {
				type = name;
			}
		} else {
			map.put("name", declaration.getName());
		}

		if (type == null) {
			Class<? extends Annotation> typeClass = declaration.getAnnotationType();
			if (typeClass != null) {
				type = "@" + typeClass.getSimpleName();
			}
		}
		map.put("type", type);

		Class<?> declaring = declaration.getDeclaring();
		map.put("className", declaring.getName());
		attributeToMap(map);

		portToMap(map);

		List<Map<String, Object>> params = new ArrayList<Map<String, Object>>();
		for (Parameter param : description.getParameters()) {
			addParam(params, param);
		}
		map.put("parameter", params);
	}

	private void attributeToMap(Map<String, Object> map) {
		OperatorHelper helper = description.getAttribute(OperatorHelper.class);
		if (helper != null) {
			map.put("operatorHelper.name", helper.getName());
		}
	}
}
