package jp.hishidama.eclipse_plugin.toad.editor.action.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;
import jp.hishidama.eclipse_plugin.toad.model.frame.FrameNode;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.port.BasePort;
import jp.hishidama.eclipse_plugin.toad.model.node.port.JobPort;
import jp.hishidama.eclipse_plugin.toad.model.node.port.PortCyComparator;
import jp.hishidama.eclipse_plugin.toad.model.node.port.command.MovePortCommand;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.swt.graphics.Rectangle;

public class JobPortAutoLayout extends AutoLayout {
	public static final int H_SPAN = 80;

	public JobPortAutoLayout() {
		this(new HashMap<NodeElement, Rectangle>());
	}

	public JobPortAutoLayout(Map<NodeElement, Rectangle> rectMap) {
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
		FrameNode frame = null;
		List<JobPort> ilist = new ArrayList<JobPort>(list.size() / 2);
		List<JobPort> olist = new ArrayList<JobPort>(list.size() / 2);
		for (NodeElement node : list) {
			if (node instanceof JobPort) {
				JobPort port = (JobPort) node;
				if (port.isIn()) {
					ilist.add(port);
				} else {
					olist.add(port);
				}
				if (frame == null && port.getParent() instanceof FrameNode) {
					frame = (FrameNode) port.getParent();
				}
			}
		}
		if (frame == null) {
			return null;
		}

		Command icommand = layout(frame, ilist, frame.getInputPorts());
		Command ocommand = layout(frame, olist, frame.getOutputPorts());
		if (icommand == null) {
			return ocommand;
		} else if (ocommand == null) {
			return icommand;
		}
		CompoundCommand command = new CompoundCommand();
		command.add(icommand);
		command.add(ocommand);
		return command;
	}

	private Command layout(FrameNode frame, List<JobPort> list, List<JobPort> all) {
		if (list.isEmpty()) {
			return null;
		}

		int frameY = getY(frame);
		int frameX = getX(frame);
		int frameW = getWidth(frame);

		Collections.sort(all, PortCyComparator.COMPARATOR);
		Map<JobPort, Integer> map = layoutAll(list, all, frameY);

		CompoundCommand command = new CompoundCommand();
		for (JobPort port : list) {
			int cx = frameX;
			if (port.isOut()) {
				cx += frameW;
			}
			int cy = map.get(port);
			putRect(port, cx - BasePort.WIDTH / 2, cy - BasePort.HEIGHT / 2, BasePort.WIDTH, BasePort.HEIGHT);
			MovePortCommand move = new MovePortCommand(port, cx, cy);
			command.add(move);
		}
		if (command.isEmpty()) {
			return null;
		}
		return command.unwrap();
	}

	private Map<JobPort, Integer> layoutAll(List<JobPort> list, List<JobPort> all, int frameY) {
		Set<JobPort> set = new HashSet<JobPort>(list);

		Map<JobPort, Integer> map = new HashMap<JobPort, Integer>(all.size());
		for (JobPort port : all) {
			int cy;
			if (set.contains(port)) {
				cy = calculateCy(port);
			} else {
				cy = getCy(port);
			}
			map.put(port, cy);
		}

		for (int i = 0; i < all.size(); i++) {
			JobPort port = all.get(i);
			if (!set.contains(port)) {
				continue;
			}

			int py = frameY + 32;
			if (i - 1 >= 0) {
				JobPort prev = all.get(i - 1);
				py = map.get(prev) + H_SPAN;
			}
			JobPort next = null;
			int ny = Integer.MAX_VALUE;
			if (i + 1 < all.size()) {
				next = all.get(i + 1);
				ny = map.get(next) - H_SPAN;
			}
			int cy = map.get(port);
			if (py <= cy && cy <= ny) {
				// map.put(port, cy);
				continue;
			}
			if (py <= ny) {
				if (ny != Integer.MAX_VALUE) {
					map.put(port, ny);
				} else {
					map.put(port, py);
				}
				continue;
			}
			if (set.contains(next)) {
				map.put(port, py);
				continue;
			}

			map.put(port, (py + ny) / 2);
		}

		return map;
	}

	private int calculateCy(JobPort port) {
		List<Connection> list;
		if (port.isIn()) {
			list = port.getOutgoings();
		} else {
			list = port.getIncomings();
		}
		if (list.size() == 1) {
			Connection c = list.get(0);
			NodeElement node = c.getOpposite(port);
			Rectangle r = getCoreBounds(node);
			return r.y + r.height / 2;
		}
		return calculateCyFromPorter(port);
	}

	private int calculateCyFromPorter(JobPort port) {
		List<Connection> list;
		if (port.isIn()) {
			list = port.getIncomings();
		} else {
			list = port.getOutgoings();
		}
		if (!list.isEmpty()) {
			Connection c = list.get(0);
			NodeElement node = c.getOpposite(port);
			Rectangle r = getCoreBounds(node);
			return r.y + r.height / 2;
		}
		return getCy(port);
	}
}
