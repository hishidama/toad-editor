package jp.hishidama.eclipse_plugin.toad.editor.action.layout;

import java.util.List;
import java.util.Map;

import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;
import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.frame.FrameNode;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.RectangleNode;
import jp.hishidama.eclipse_plugin.toad.model.node.port.BasePort;
import jp.hishidama.eclipse_plugin.toad.model.node.port.JobPort;

import org.eclipse.swt.graphics.Rectangle;

public abstract class AutoLayout {

	private Map<NodeElement, Rectangle> rectMap;

	public AutoLayout(Map<NodeElement, Rectangle> rectMap) {
		this.rectMap = rectMap;
	}

	protected final FrameNode getFrameNode(List<NodeElement> list) {
		for (NodeElement node : list) {
			Diagram diagram = node.getDiagram();
			if (diagram == null) {
				continue;
			}
			for (NodeElement n : diagram.getContents()) {
				if (n instanceof FrameNode) {
					return (FrameNode) n;
				}
			}
			return null;
		}
		return null;
	}

	protected final boolean isImporter(NodeElement node) {
		return getImporterJobPort(node) != null;
	}

	protected final JobPort getImporterJobPort(NodeElement node) {
		List<Connection> list = node.getOutgoings();
		for (Connection connection : list) {
			NodeElement o = connection.getOpposite(node);
			if (o instanceof JobPort) {
				JobPort port = (JobPort) o;
				return port.isIn() ? port : null;
			}
		}
		return null;
	}

	protected final boolean isExporter(NodeElement node) {
		return getExporterJobPort(node) != null;
	}

	protected final JobPort getExporterJobPort(NodeElement node) {
		List<Connection> list = node.getIncomings();
		for (Connection connection : list) {
			NodeElement o = connection.getOpposite(node);
			if (o instanceof JobPort) {
				JobPort port = (JobPort) o;
				return port.isOut() ? port : null;
			}
		}
		return null;
	}

	protected final Rectangle getCoreBounds(NodeElement node) {
		return node.getCoreBounds(rectMap);
	}

	protected final Rectangle getOuterBounds(NodeElement node) {
		return node.getOuterBounds(rectMap);
	}

	protected final int getX(RectangleNode node) {
		Rectangle r = rectMap.get(node);
		if (r != null) {
			return r.x;
		}
		return node.getX();
	}

	protected final int getY(RectangleNode node) {
		Rectangle r = rectMap.get(node);
		if (r != null) {
			return r.y;
		}
		return node.getY();
	}

	protected final int getWidth(RectangleNode node) {
		Rectangle r = rectMap.get(node);
		if (r != null) {
			return r.width;
		}
		return node.getWidth();
	}

	protected final int getHeight(RectangleNode node) {
		Rectangle r = rectMap.get(node);
		if (r != null) {
			return r.height;
		}
		return node.getHeight();
	}

	protected final int getCy(BasePort port) {
		Rectangle r = rectMap.get(port);
		if (r != null) {
			return r.y + r.height / 2;
		}
		return port.getCy();
	}

	protected final void putRect(NodeElement node, int x, int y, int width, int height) {
		Rectangle rect = new Rectangle(x, y, width, height);
		rectMap.put(node, rect);
	}
}
