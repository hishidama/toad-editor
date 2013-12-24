package jp.hishidama.eclipse_plugin.util;

import java.util.List;

import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;
import jp.hishidama.eclipse_plugin.toad.model.node.port.OpePort;

import org.eclipse.draw2d.PositionConstants;

public class ToadLayoutUtil {

	public static void addPorts(OperatorNode node, List<OpePort> inList, List<OpePort> outList) {
		int count = Math.max(inList.size(), outList.size());
		{
			int height = 20 * count;
			if (node.getHeight() < height) {
				node.setHeight(height);
			}
		}
		{
			int x = node.getX();
			int size = inList.size();
			int position = (size <= 1) ? PositionConstants.NONE : PositionConstants.LEFT;
			int i = 0;
			for (OpePort port : inList) {
				port.setIn(true);
				setLocation(port, x, node.getHeight(), i++, size, position);
				node.addPort(port, NodeElement.LEFT);
			}
		}
		{
			int x = node.getX() + node.getWidth();
			int size = outList.size();
			int position = (size <= 1) ? PositionConstants.NONE : PositionConstants.RIGHT;
			int i = 0;
			for (OpePort port : outList) {
				port.setOut(true);
				setLocation(port, x, node.getHeight(), i++, size, position);
				node.addPort(port, NodeElement.RIGHT);
			}
		}
	}

	public static void setLocation(OpePort port, int x, int height, int i, int size, int position) {
		port.setCx(x);
		port.setCy(height * (i + 1) / (size + 1));
		port.setNamePosition(position);
	}
}
