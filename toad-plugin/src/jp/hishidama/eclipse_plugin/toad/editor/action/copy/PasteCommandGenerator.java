package jp.hishidama.eclipse_plugin.toad.editor.action.copy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;
import jp.hishidama.eclipse_plugin.toad.model.connection.command.CreateConnectionCommand;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.RectangleNode;
import jp.hishidama.eclipse_plugin.toad.model.node.port.BasePort;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.swt.graphics.Rectangle;

public abstract class PasteCommandGenerator {

	protected ToadEditor editor;
	protected ClipboardObject clip;

	private List<? extends NodeElement> pasteNodeList;

	public void initialize(ToadEditor editor, ClipboardObject clip) {
		this.editor = editor;
		this.clip = clip;
	}

	public abstract Command getPasteCommand();

	public List<? extends NodeElement> getPasteNodeList() {
		return pasteNodeList;
	}

	protected final void setPasteNodeList(List<? extends NodeElement> list) {
		this.pasteNodeList = list;
	}

	protected void applyOffset(List<? extends NodeElement> list) {
		Dimension offset = calculateOffset(list);
		for (NodeElement node : list) {
			if (node instanceof RectangleNode) {
				RectangleNode rnode = (RectangleNode) node;
				rnode.setX(rnode.getX() + offset.width);
				rnode.setY(rnode.getY() + offset.height);
			} else if (node instanceof BasePort) {
				BasePort port = (BasePort) node;
				port.setCx(port.getCx() + offset.width);
				port.setCy(port.getCy() + offset.height);
			} else {
				throw new UnsupportedOperationException("node=" + node.getClass());
			}
		}
	}

	private Dimension calculateOffset(List<? extends NodeElement> list) {
		Rectangle base = getBaseNode(list);
		Map<Integer, List<NodeElement>> xmap = getXMap();
		for (int y = 0;; y++) {
			for (int x = 0; x < 8; x++) {
				Dimension offset = new Dimension(x * 16, (x + y) * 16);
				if (!existsSamePosition(base, offset, xmap)) {
					return offset;
				}
			}
		}
	}

	private Rectangle getBaseNode(List<? extends NodeElement> list) {
		NodeElement found = null;
		Rectangle frect = null;
		for (NodeElement node : list) {
			boolean change = false;
			Rectangle rect = node.getCoreBounds();
			if (found == null) {
				change = true;
			} else {
				if (rect.x < frect.x) {
					change = true;
				} else if (rect.x == frect.x) {
					if (rect.y < frect.y) {
						change = true;
					} else if (rect.y == frect.y) {
						if (node.getId() < found.getId()) {
							change = true;
						}
					}
				}
			}
			if (change) {
				found = node;
				frect = rect;
			}
		}
		return frect;
	}

	private Map<Integer, List<NodeElement>> getXMap() {
		Map<Integer, List<NodeElement>> map = new HashMap<Integer, List<NodeElement>>();
		for (NodeElement node : editor.getDiagram().getContents()) {
			int x = node.getCoreBounds().x;
			List<NodeElement> list = map.get(x);
			if (list == null) {
				list = new ArrayList<NodeElement>();
				map.put(x, list);
			}
			list.add(node);
		}
		return map;
	}

	private boolean existsSamePosition(Rectangle base, Dimension offset, Map<Integer, List<NodeElement>> xmap) {
		int x = base.x + offset.width;
		int y = base.y + offset.height;
		List<NodeElement> list = xmap.get(x);
		if (list == null) {
			return false;
		}
		for (NodeElement node : list) {
			if (node.getCoreBounds().y == y) {
				return true;
			}
		}
		return false;
	}

	protected <T extends NodeElement> Map<Integer, Integer> convertNewId(List<T> list) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>(list.size() * 5);
		for (T node : list) {
			int newId = editor.newId();
			map.put(node.getId(), newId);
			node.setId(newId);
			removeAllConnections(node);
			map.putAll(convertNewId(node.getChildren()));
		}
		return map;
	}

	private static void removeAllConnections(NodeElement node) {
		{
			List<Connection> list = new ArrayList<Connection>(node.getIncomings());
			for (Connection c : list) {
				node.removeIncoming(c);
			}
		}
		{
			List<Connection> list = new ArrayList<Connection>(node.getOutgoings());
			for (Connection c : list) {
				node.removeOutgoing(c);
			}
		}
	}

	protected <T extends NodeElement> void addConnectionCommand(CompoundCommand compound, ClipboardObject clip,
			List<T> list, Map<Integer, Integer> convertMap) {
		Map<Integer, NodeElement> idMap = getIdMap(list);
		for (Connection conn : clip.getConnections()) {
			NodeElement s = idMap.get(convertMap.get(conn.getSourceId()));
			NodeElement t = idMap.get(convertMap.get(conn.getTargetId()));
			if (s != null && t != null) {
				CreateConnectionCommand c = new CreateConnectionCommand(new Connection());
				c.setSource(s);
				c.setTarget(t);
				compound.add(c);
			}
		}
	}

	private static Map<Integer, NodeElement> getIdMap(List<? extends NodeElement> list) {
		Map<Integer, NodeElement> map = new HashMap<Integer, NodeElement>();
		for (NodeElement node : list) {
			map.put(node.getId(), node);
			map.putAll(getIdMap(node.getChildren()));
		}
		return map;
	}
}
