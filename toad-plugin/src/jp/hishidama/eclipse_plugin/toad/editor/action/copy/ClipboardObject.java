package jp.hishidama.eclipse_plugin.toad.editor.action.copy;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.hishidama.eclipse_plugin.toad.model.AbstractNameModel;
import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;
import jp.hishidama.eclipse_plugin.toad.model.frame.FlowpartFrameNode;
import jp.hishidama.eclipse_plugin.toad.model.frame.FlowpartParameterDef;
import jp.hishidama.eclipse_plugin.toad.model.gson.NodeElementTypeAdapter;
import jp.hishidama.eclipse_plugin.toad.model.node.Attribute;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OpeParameter;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;
import jp.hishidama.eclipse_plugin.toad.model.node.port.BasePort;
import jp.hishidama.eclipse_plugin.toad.model.property.attribute.HasAttributeNode;
import jp.hishidama.xtext.dmdl_editor.ui.internal.LogUtil;

import org.eclipse.gef.EditPart;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class ClipboardObject {

	@Expose
	private List<NodeElement> contents;
	@Expose
	private List<Connection> connections;

	public List<NodeElement> getContents() {
		return contents;
	}

	public List<Connection> getConnections() {
		return connections;
	}

	public static ClipboardObject create(List<?> list) {
		List<NodeElement> models = getContents(list);
		if (models.isEmpty()) {
			return null;
		}
		List<Connection> conns = getConnection(models);

		ClipboardObject object = new ClipboardObject();
		object.contents = models;
		object.connections = conns;
		return object;
	}

	private static List<NodeElement> getContents(List<?> list) {
		List<NodeElement> result = new ArrayList<NodeElement>(list.size() * 5);

		Set<AbstractNameModel> set = new HashSet<AbstractNameModel>(list.size() * 5);
		List<BasePort> ports = new ArrayList<BasePort>();
		for (Object obj : list) {
			EditPart part = (EditPart) obj;
			Object model = part.getModel();
			if (model instanceof BasePort) {
				ports.add((BasePort) model);
				continue;
			}
			if (model instanceof NodeElement) {
				NodeElement node = (NodeElement) model;
				result.add(node);
				set.add(node);
				for (NodeElement c : node.getChildren()) {
					result.add(c);
					set.add(c);
				}
			}
		}

		for (BasePort port : ports) {
			if (!set.contains(port)) {
				result.add(port);
				set.add(port);
			}
		}

		return result;
	}

	private static List<Connection> getConnection(List<NodeElement> list) {
		Map<Integer, NodeElement> map = new HashMap<Integer, NodeElement>(list.size());
		for (NodeElement node : list) {
			map.put(node.getId(), node);
		}

		Set<Connection> set = new LinkedHashSet<Connection>(list.size());
		for (NodeElement node : list) {
			collectConnetion(node, map, set);
		}

		return new ArrayList<Connection>(set);
	}

	private static void collectConnetion(NodeElement node, Map<Integer, NodeElement> map, Set<Connection> set) {
		List<Connection> list = new ArrayList<Connection>();
		list.addAll(node.getIncomings());
		list.addAll(node.getOutgoings());
		for (Connection c : list) {
			NodeElement t = c.getOpposite(node);
			if (map.containsKey(t.getId())) {
				if (!set.contains(c)) {
					set.add(c);
				}
			}
		}
		for (NodeElement c : node.getChildren()) {
			collectConnetion(c, map, set);
		}
	}

	public String serialize() {
		StringWriter sw = new StringWriter(4096);
		JsonWriter writer = new JsonWriter(sw);
		try {
			writer.setIndent("  ");
			Gson gson = createGson();
			gson.toJson(this, getClass(), writer);
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				LogUtil.logWarn("close error.", e);
			}
		}
		return sw.toString();
	}

	public static ClipboardObject deserialize(String text) {
		ClipboardObject object;
		{
			StringReader in = new StringReader(text);
			try {
				JsonReader reader = new JsonReader(in);
				Gson gson = createGson();
				object = gson.fromJson(reader, ClipboardObject.class);
			} catch (Exception e) {
				// LogUtil.logWarn("clipboard deserialize error.", e);
				return null;
			} finally {
				in.close();
			}
		}
		object.postLoad();
		return object;
	}

	private void postLoad() {
		Map<Integer, NodeElement> map = new HashMap<Integer, NodeElement>();
		collectNode(map, null, contents);
		if (connections != null) {
			for (Connection c : connections) {
				NodeElement source = map.get(c.getSourceId());
				NodeElement target = map.get(c.getTargetId());
				if (source != null && target != null) {
					c.setSource(source);
					c.setTarget(target);
					source.addOutgoing(c);
					target.addIncoming(c);
				} else {
					LogUtil.logWarn(MessageFormat.format(
							"clipboard deserialize illegal connection: {0}({1})->{2}({3})", c.getSourceId(), source,
							c.getTargetId(), target));
				}
			}
		}
	}

	private void collectNode(Map<Integer, NodeElement> map, NodeElement parent, List<NodeElement> list) {
		if (list == null) {
			return;
		}
		for (NodeElement node : list) {
			node.setParent(parent);
			map.put(node.getId(), node);
			if (node instanceof HasAttributeNode) {
				HasAttributeNode anode = (HasAttributeNode) node;
				for (Attribute attr : anode.getAttributeList()) {
					attr.setParent(anode);
				}
			}
			if (node instanceof FlowpartFrameNode) {
				FlowpartFrameNode frame = (FlowpartFrameNode) node;
				for (FlowpartParameterDef param : frame.getParameterList()) {
					param.setParent(frame);
				}
			}
			if (node instanceof OperatorNode) {
				OperatorNode operator = (OperatorNode) node;
				for (OpeParameter param : operator.getParameterList()) {
					param.setParent(operator);
				}
			}
			collectNode(map, node, node.getChildren());
		}
	}

	private static Gson createGson() {
		return new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
				.registerTypeAdapter(NodeElement.class, new NodeElementTypeAdapter("clipboard")).create();
	}

	@Override
	public String toString() {
		return String.format("ClipboardObject(\n  contents=%s,\n  collections=%s\n)", contents, connections);
	}
}
