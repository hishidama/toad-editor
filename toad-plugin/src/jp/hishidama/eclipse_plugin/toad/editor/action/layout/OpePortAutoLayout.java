package jp.hishidama.eclipse_plugin.toad.editor.action.layout;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;
import jp.hishidama.eclipse_plugin.toad.model.node.port.BasePort;
import jp.hishidama.eclipse_plugin.toad.model.node.port.OpePort;
import jp.hishidama.eclipse_plugin.toad.model.node.port.PortCyComparator;
import jp.hishidama.eclipse_plugin.toad.model.node.port.command.MovePortCommand;
import jp.hishidama.eclipse_plugin.util.ToadCommandUtil;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.swt.graphics.Rectangle;

public class OpePortAutoLayout extends AutoLayout {
	public static final int H_SPAN = 12;

	public OpePortAutoLayout() {
		this(new HashMap<NodeElement, Rectangle>());
	}

	public OpePortAutoLayout(Map<NodeElement, Rectangle> rectMap) {
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
		Set<OpePort> set = new HashSet<OpePort>();
		Set<OperatorNode> opeSet = new LinkedHashSet<OperatorNode>();
		for (NodeElement node : list) {
			if (node instanceof OpePort) {
				OpePort port = (OpePort) node;
				set.add(port);
				opeSet.add((OperatorNode) port.getParent());
			}
		}
		if (opeSet.isEmpty()) {
			return null;
		}

		CompoundCommand command = new CompoundCommand();
		for (OperatorNode operator : opeSet) {
			ToadCommandUtil.add(command, layout(operator, operator.getInputPorts(), set));
			ToadCommandUtil.add(command, layout(operator, operator.getOutputPorts(), set));
		}
		if (command.isEmpty()) {
			return null;
		}
		return command.unwrap();
	}

	private Command layout(OperatorNode operator, List<OpePort> list, Set<OpePort> set) {
		if (!isTarget(list, set)) {
			return null;
		}

		Rectangle bounds = getCoreBounds(operator);

		Collections.sort(list, PortCyComparator.COMPARATOR);
		Map<OpePort, Integer> map = layoutAll(list, set, bounds.y, bounds.height);

		CompoundCommand command = new CompoundCommand();
		for (OpePort port : list) {
			int cx = bounds.x;
			if (port.isOut()) {
				cx += bounds.width;
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

	private boolean isTarget(List<OpePort> list, Set<OpePort> set) {
		for (OpePort port : list) {
			if (set.contains(port)) {
				return true;
			}
		}
		return false;
	}

	private Map<OpePort, Integer> layoutAll(List<OpePort> list, Set<OpePort> set, int y, int h) {
		Map<OpePort, Integer> map = new HashMap<OpePort, Integer>(list.size());
		for (int i = 0; i < list.size(); i++) {
			OpePort port = list.get(i);
			int cy;
			if (set.contains(port)) {
				cy = calculateCy(y, h, i, list.size());
			} else {
				cy = getCy(port);
			}
			map.put(port, cy);
		}

		for (int i = 0; i < list.size(); i++) {
			OpePort port = list.get(i);
			if (!set.contains(port)) {
				continue;
			}

			int py = y + 4;
			if (i - 1 >= 0) {
				OpePort prev = list.get(i - 1);
				py = map.get(prev) + H_SPAN;
			}
			OpePort next = null;
			int ny = Integer.MAX_VALUE;
			if (i + 1 < list.size()) {
				next = list.get(i + 1);
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

	private int calculateCy(int y, int h, int i, int size) {
		return y + h * (i + 1) / (size + 1);
	}
}
