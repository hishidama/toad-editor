package jp.hishidama.eclipse_plugin.toad.editor.action.layout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hishidama.eclipse_plugin.toad.model.frame.FrameNode;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.command.MoveNodeCommand;
import jp.hishidama.eclipse_plugin.toad.model.node.datafile.DataFileNode;
import jp.hishidama.eclipse_plugin.toad.model.node.port.JobPort;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.swt.graphics.Rectangle;

public class DataFileAutoLayout extends AutoLayout {
	public DataFileAutoLayout() {
		this(new HashMap<NodeElement, Rectangle>());
	}

	public DataFileAutoLayout(Map<NodeElement, Rectangle> rectMap) {
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
		FrameNode frame = getFrameNode(list);
		if (frame == null) {
			return null;
		}
		int fx = getX(frame) + getWidth(frame) + FrameAutoLayout.OUT_FILE_PADDING;

		CompoundCommand command = new CompoundCommand();
		for (NodeElement node : list) {
			if (!(node instanceof DataFileNode)) {
				continue;
			}
			DataFileNode dnode = (DataFileNode) node;
			{
				JobPort port = getImporterJobPort(dnode);
				if (port != null) {
					command.add(getCommand(dnode, port, 32));
					continue;
				}
			}
			{
				JobPort port = getExporterJobPort(dnode);
				if (port != null) {
					command.add(getCommand(dnode, port, fx));
					continue;
				}
			}
		}
		if (command.isEmpty()) {
			return null;
		}
		return command.unwrap();
	}

	private MoveNodeCommand getCommand(DataFileNode node, JobPort port, int x) {
		int ox = getX(node);
		int oy = getY(node);
		int w = getWidth(node);
		int h = getHeight(node);
		int y = getCy(port) - h / 2;
		if (x == ox && y == oy) {
			return null;
		}
		putRect(node, x, y, w, h);
		return new MoveNodeCommand(node, x, y, w, h);
	}
}
