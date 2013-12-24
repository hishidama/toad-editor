package jp.hishidama.eclipse_plugin.toad.editor.action.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;
import jp.hishidama.eclipse_plugin.toad.model.frame.FrameNode;
import jp.hishidama.eclipse_plugin.toad.model.marker.MarkerNode;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.RectangleNode;
import jp.hishidama.eclipse_plugin.toad.model.node.command.MoveNodeCommand;
import jp.hishidama.eclipse_plugin.toad.model.node.datafile.DataFileNode;
import jp.hishidama.eclipse_plugin.toad.model.node.port.BasePort;
import jp.hishidama.eclipse_plugin.toad.model.node.port.JobPort;
import jp.hishidama.eclipse_plugin.toad.model.node.port.OpePort;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.DirectedGraphLayout;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.EdgeList;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.draw2d.graph.NodeList;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.swt.graphics.Rectangle;

public class GefAutoLayout extends AutoLayout {
	/** ノード間の間隔 */
	private static final int GAP_W = 12, GAP_H = 16;

	public GefAutoLayout() {
		this(new HashMap<NodeElement, Rectangle>());
	}

	public GefAutoLayout(Map<NodeElement, Rectangle> rectMap) {
		super(rectMap);
	}

	public void run(List<NodeElement> list, CommandStack commandStack) {
		Command command = getCommand(list);
		if (commandStack != null) {
			commandStack.execute(command);
		} else {
			if (command != null) {
				command.execute();
			}
		}
	}

	public Command getCommand(List<NodeElement> list) {
		NodeList nodeList = new NodeList();
		EdgeList edgeList = new EdgeList();
		Point base = collect(list, nodeList, edgeList);
		calculateLayout(nodeList, edgeList);
		Command command = layout(base, nodeList);
		return command;
	}

	@SuppressWarnings("unchecked")
	private Point collect(List<NodeElement> list, NodeList nodeList, EdgeList edgeList) {
		Point base = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);

		List<NodeElement> list2 = new ArrayList<NodeElement>(list.size());
		Map<NodeElement, Node> map = new HashMap<NodeElement, Node>();
		{
			for (NodeElement node : list) {
				if (node instanceof OpePort) {
					continue;
				}
				if (node instanceof FrameNode || node instanceof DataFileNode || node instanceof MarkerNode) {
					continue;
				}
				list2.add(node);

				Node gefNode = new Node();
				nodeList.add(gefNode);
				map.put(node, gefNode);

				Rectangle r = getCoreBounds(node);
				Rectangle or = getOuterBounds(node);
				Dimension size = new Dimension(or.width, or.height);
				gefNode.setSize(size);

				if (!(node instanceof JobPort)) {
					base.x = Math.min(base.x, or.x);
					base.y = Math.min(base.y, or.y);
				}
				// 初期位置を与えても考慮されない
				// node.x = getX(model);
				// node.y = getY(model);

				gefNode.data = new Data(node, r, or);
			}
		}

		Set<Connection> set = new HashSet<Connection>(map.size() * 2);
		for (NodeElement node : list2) {
			for (Connection c : node.getOutgoings()) {
				collectConnection(edgeList, c, map, set);
			}
			for (Connection c : node.getIncomings()) {
				collectConnection(edgeList, c, map, set);
			}
			for (NodeElement child : node.getChildren()) {
				for (Connection c : child.getOutgoings()) {
					collectConnection(edgeList, c, map, set);
				}
				for (Connection c : child.getIncomings()) {
					collectConnection(edgeList, c, map, set);
				}
			}
		}

		return base;
	}

	@SuppressWarnings("unchecked")
	private void collectConnection(EdgeList edgeList, Connection c, Map<NodeElement, Node> map, Set<Connection> set) {
		if (set.contains(c)) {
			return;
		}

		NodeElement s = c.getSource();
		if (s instanceof OpePort) {
			s = s.getParent();
		}
		Node source = map.get(s);
		if (source == null) {
			return;
		}

		NodeElement t = c.getTarget();
		if (t instanceof OpePort) {
			t = t.getParent();
		}
		Node target = map.get(t);
		if (target == null) {
			return;
		}

		Edge edge = new Edge(source, target);
		edgeList.add(edge);
		set.add(c);
	}

	private void calculateLayout(NodeList nodeList, EdgeList edgeList) {
		DirectedGraph graph = new DirectedGraph();
		graph.setDirection(PositionConstants.EAST);
		graph.edges = edgeList;
		graph.nodes = nodeList;
		graph.setDefaultPadding(new Insets(GAP_H, GAP_W, GAP_H, GAP_W));
		DirectedGraphLayout layout = new DirectedGraphLayout();
		layout.visit(graph);
	}

	private Command layout(Point oldBase, NodeList nodeList) {
		Point base = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
		for (Object obj : nodeList) {
			Node gefNode = (Node) obj;
			Data data = (Data) gefNode.data;
			NodeElement node = data.node;
			if (!(node instanceof JobPort)) {
				base.x = Math.min(base.x, gefNode.x);
				base.y = Math.min(base.y, gefNode.y);
			}
		}

		CompoundCommand command = new CompoundCommand();
		for (Object obj : nodeList) {
			Node gefNode = (Node) obj;
			Data data = (Data) gefNode.data;
			NodeElement node = data.node;
			MoveNodeCommand c;
			if (node instanceof RectangleNode) {
				RectangleNode rnode = (RectangleNode) node;
				Rectangle cr = data.core;
				Rectangle or = data.outer;
				int x = oldBase.x + gefNode.x - base.x + cr.x - or.x;
				int y = oldBase.y + gefNode.y - base.y + cr.y - or.y;
				int w = getWidth(rnode);
				int h = getHeight(rnode);
				putRect(rnode, x, y, w, h);
				c = new MoveNodeCommand(rnode, x, y, w, h);
			} else if (node instanceof BasePort) {
				c = null;
			} else {
				throw new UnsupportedOperationException("class=" + node.getClass());
			}
			command.add(c);
		}
		if (command.isEmpty()) {
			return null;
		}
		return command.unwrap();
	}

	private static class Data {
		public NodeElement node;
		public Rectangle core;
		public Rectangle outer;

		public Data(NodeElement node, Rectangle core, Rectangle outer) {
			this.node = node;
			this.core = core;
			this.outer = outer;
		}
	}
}