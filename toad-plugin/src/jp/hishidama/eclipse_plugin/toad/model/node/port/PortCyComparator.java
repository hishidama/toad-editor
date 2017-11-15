package jp.hishidama.eclipse_plugin.toad.model.node.port;

import java.util.Comparator;
import java.util.List;

import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;

public class PortCyComparator implements Comparator<BasePort> {

	public static final PortCyComparator COMPARATOR = new PortCyComparator();

	@Override
	public int compare(BasePort o1, BasePort o2) {
		int c = o1.getCy() - o2.getCy();
		if (c != 0) {
			return c;
		}

		NodeElement p1 = o1.getParent();
		NodeElement p2 = o2.getParent();
		List<NodeElement> l1 = p1.getChildren();
		List<NodeElement> l2 = p2.getChildren();
		assert l1 == l2;
		int i1 = l1.indexOf(o1);
		int i2 = l2.indexOf(o2);
		return i1 - i2;
	}
}
