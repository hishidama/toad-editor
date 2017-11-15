package jp.hishidama.eclipse_plugin.toad.editor.action.layout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.frame.FrameNode;
import jp.hishidama.eclipse_plugin.toad.model.marker.MarkerNode;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.command.MoveNodeCommand;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.swt.graphics.Rectangle;

public class MarkerAutoLayout extends AutoLayout {
	public MarkerAutoLayout() {
		this(new HashMap<NodeElement, Rectangle>());
	}

	public MarkerAutoLayout(Map<NodeElement, Rectangle> rectMap) {
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

	public Command getCommand(Diagram diagram) {
		if (diagram == null) {
			return null;
		}
		MarkerNode marker = null;
		List<NodeElement> list = diagram.getContents();
		for (NodeElement node : list) {
			if (node instanceof MarkerNode) {
				marker = (MarkerNode) node;
				break;
			}
		}
		if (marker == null) {
			return null;
		}

		Command command = layout(marker, list);
		return command;
	}

	private Command layout(MarkerNode marker, List<NodeElement> list) {
		int x = 0, y = 0;
		for (NodeElement node : list) {
			if (node instanceof MarkerNode) {
				continue;
			}

			Rectangle r = getOuterBounds(node);
			int ex = r.x + r.width;
			int ey = r.y + r.height;
			if (node instanceof FrameNode) {
				FrameNode frame = (FrameNode) node;
				ex += frame.getHorizontalMargin() - MarkerNode.WIDTH;
				ey += frame.getVerticalMargin() - MarkerNode.HEIGHT;
			}

			x = Math.max(x, ex);
			y = Math.max(y, ey);
		}

		if (x == getX(marker) && y == getY(marker)) {
			return null;
		}
		int w = MarkerNode.WIDTH;
		int h = MarkerNode.HEIGHT;
		putRect(marker, x, y, w, h);
		return new MoveNodeCommand(marker, x, y, w, h);
	}
}
