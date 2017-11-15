package jp.hishidama.eclipse_plugin.toad.editor.action.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.frame.FrameNode;
import jp.hishidama.eclipse_plugin.toad.model.marker.MarkerNode;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.RectangleNode;
import jp.hishidama.eclipse_plugin.toad.model.node.command.MoveNodeCommand;
import jp.hishidama.eclipse_plugin.toad.model.node.datafile.DataFileNode;
import jp.hishidama.eclipse_plugin.toad.model.node.port.BasePort;
import jp.hishidama.eclipse_plugin.toad.model.node.port.JobPort;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.swt.graphics.Rectangle;

public class FrameAutoLayout extends AutoLayout {
	public static final int IN_FILE_PADDING = 64;
	public static final int IN_PORT_PADDING = 64;
	public static final int OUT_PORT_PADDING = 64;
	public static final int OUT_FILE_PADDING = 64;
	public static final int BOTTOM_PADDING = 32;

	public FrameAutoLayout() {
		this(new HashMap<NodeElement, Rectangle>());
	}

	public FrameAutoLayout(Map<NodeElement, Rectangle> rectMap) {
		super(rectMap);
	}

	public void run(Diagram diagram, CommandStack commandStack) {
		Command command = getCommand(diagram);
		if (commandStack != null) {
			commandStack.execute(command);
		} else {
			if (command != null) {
				command.execute();
			}
		}
	}

	public Command getCommand(List<NodeElement> list) {
		FrameNode frame = null;
		for (NodeElement node : list) {
			if (node instanceof FrameNode) {
				frame = (FrameNode) node;
				break;
			}
		}
		if (frame == null) {
			return null;
		}

		Command command = layout(frame);
		return command;
	}

	public Command getCommand(Diagram diagram) {
		if (diagram == null) {
			return null;
		}
		FrameNode frame = diagram.getFrameNode();
		if (frame == null) {
			return null;
		}

		Command command = layout(frame);
		return command;
	}

	private Command layout(FrameNode frame) {
		Diagram diagram = frame.getDiagram();
		List<NodeElement> all = diagram.getContents();

		List<NodeElement> importer = new ArrayList<NodeElement>();
		List<NodeElement> exporter = new ArrayList<NodeElement>();
		List<NodeElement> list = new ArrayList<NodeElement>(all.size());
		List<NodeElement> other = new ArrayList<NodeElement>();
		for (NodeElement node : all) {
			if (node instanceof MarkerNode) {
				continue;
			}
			if (isImporter(node)) {
				importer.add(node);
			} else if (isExporter(node)) {
				exporter.add(node);
			} else if (node instanceof DataFileNode) {
				other.add(node);
			} else if (isInnerNode(node)) {
				list.add(node);
			}
		}

		int sx0 = getRight(importer);
		int sx1 = getLeft(list);
		int sw = getPortWidth(frame.getInputPorts()) / 2;
		int sx = Math.max(sx0 + sw + IN_FILE_PADDING, frame.getHorizontalMargin());
		int deltaNodeX = (sx + sw + IN_PORT_PADDING) - sx1;

		int ex0 = getRight(list);
		int ex1 = getLeft(exporter);
		int ew = getPortWidth(frame.getOutputPorts()) / 2;
		int ex = ex0 + ew + OUT_PORT_PADDING;
		ex += deltaNodeX;
		int deltaExporterX = (ex + ew + OUT_FILE_PADDING) - ex1;

		int ey = 0;
		ey = Math.max(ey, getBottom(importer));
		ey = Math.max(ey, getBottom(exporter));
		ey = Math.max(ey, getBottom(list));
		ey = Math.max(ey, getBottom(other));
		ey = Math.max(ey, getBottom(frame.getChildren()) + 8);
		ey += BOTTOM_PADDING;

		CompoundCommand command = new CompoundCommand();
		if (deltaNodeX != 0) {
			for (NodeElement node : list) {
				Command c = getMoveCommand((RectangleNode) node, deltaNodeX);
				command.add(c);
			}
		}
		if (deltaExporterX != 0) {
			for (NodeElement node : exporter) {
				Command c = getMoveCommand((RectangleNode) node, deltaExporterX);
				command.add(c);
			}
		}
		{
			Command c = getResizeCommand(frame, sx, ex, ey);
			command.add(c);
		}

		if (command.isEmpty()) {
			return null;
		}
		return command.unwrap();
	}

	private boolean isInnerNode(NodeElement node) {
		if (node instanceof FrameNode || node instanceof BasePort) {
			return false;
		}
		return true;
	}

	private int getRight(List<NodeElement> list) {
		int x = 0;
		for (NodeElement node : list) {
			Rectangle rect = getNodeBounds(node);
			x = Math.max(x, rect.x + rect.width);
		}
		return x;
	}

	private int getLeft(List<NodeElement> list) {
		int x = Integer.MAX_VALUE;
		for (NodeElement node : list) {
			Rectangle rect = getNodeBounds(node);
			x = Math.min(x, rect.x);
		}
		if (x == Integer.MAX_VALUE) {
			return 0;
		}
		return x;
	}

	private int getPortWidth(List<JobPort> list) {
		int w = 0;
		for (JobPort port : list) {
			Rectangle rect = getNodeBounds(port);
			w = Math.max(w, rect.width);
		}
		return w;
	}

	private int getBottom(List<NodeElement> list) {
		int y = 0;
		for (NodeElement node : list) {
			Rectangle rect = getNodeBounds(node);
			y = Math.max(y, rect.y + rect.height);
		}
		return y;
	}

	private Rectangle getNodeBounds(NodeElement node) {
		return getOuterBounds(node);
	}

	private Command getMoveCommand(RectangleNode node, int deltaX) {
		int x = getX(node) + deltaX;
		int y = getY(node);
		int w = getWidth(node);
		int h = getHeight(node);
		putRect(node, x, y, w, h);
		for (NodeElement c : node.getChildren()) {
			Rectangle r = getCoreBounds(c);
			putRect(c, r.x + deltaX, r.y, r.width, r.height);
		}
		return new MoveNodeCommand(node, x, y, w, h);
	}

	private Command getResizeCommand(RectangleNode node, int sx, int ex, int ey) {
		int x = getX(node);
		int y = getY(node);
		int w = getWidth(node);
		int h = getHeight(node);
		if (sx == x && ex == x + w && ey == y + h) {
			return null;
		}
		int sy = y;
		int x2 = sx;
		int y2 = y;
		int w2 = ex - sx;
		int h2 = ey - sy;
		putRect(node, x2, y2, w2, h2);
		int deltaX = x2 - x;
		int deltaW = w2 - w;
		for (NodeElement c : node.getChildren()) {
			Rectangle r = getCoreBounds(c);
			int x3 = r.x + deltaX;
			if (c.getDirection() == NodeElement.RIGHT) {
				x3 += deltaW;
			}
			putRect(c, x3, r.y, r.width, r.height);
		}
		return new MoveNodeCommand(node, x2, y2, w2, h2);
	}
}
