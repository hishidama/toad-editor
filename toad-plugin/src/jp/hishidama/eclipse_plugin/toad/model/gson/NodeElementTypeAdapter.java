package jp.hishidama.eclipse_plugin.toad.model.gson;

import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import jp.hishidama.eclipse_plugin.toad.model.frame.FlowpartFrameNode;
import jp.hishidama.eclipse_plugin.toad.model.frame.JobFrameNode;
import jp.hishidama.eclipse_plugin.toad.model.marker.MarkerNode;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.datafile.DataFileNode;
import jp.hishidama.eclipse_plugin.toad.model.node.jobflow.JobNode;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.EllipseOperatorNode;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;
import jp.hishidama.eclipse_plugin.toad.model.node.port.JobPort;
import jp.hishidama.eclipse_plugin.toad.model.node.port.OpePort;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class NodeElementTypeAdapter implements JsonSerializer<NodeElement>, JsonDeserializer<NodeElement> {

	private static final String CLASSNAME = "CLASSNAME";
	private static final String INSTANCE = "INSTANCE";

	private static final Map<String, Class<?>> MAP = new HashMap<String, Class<?>>();
	static {
		Class<?>[] cs = { OperatorNode.class, EllipseOperatorNode.class, OpePort.class, JobNode.class,
				DataFileNode.class, JobPort.class, JobFrameNode.class, FlowpartFrameNode.class, MarkerNode.class };
		for (Class<?> c : cs) {
			MAP.put(c.getSimpleName(), c);
		}
	}

	private String fileName;

	public NodeElementTypeAdapter(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public JsonElement serialize(NodeElement src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject retValue = new JsonObject();
		String className = src.getClass().getSimpleName();
		if (!MAP.containsKey(className)) {
			throw new UnsupportedOperationException(
					MessageFormat.format("file={0}, className={1}", fileName, className));
		}
		retValue.addProperty(CLASSNAME, className);
		JsonElement elem = context.serialize(src);
		retValue.add(INSTANCE, elem);
		return retValue;
	}

	@Override
	public NodeElement deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();
		JsonPrimitive prim = (JsonPrimitive) jsonObject.get(CLASSNAME);
		String className = prim.getAsString();
		Class<?> clazz = MAP.get(className);
		if (clazz == null) {
			throw new UnsupportedOperationException(
					MessageFormat.format("file={0}, className={1}", fileName, className));
		}
		return context.deserialize(jsonObject.get(INSTANCE), clazz);
	}
}
