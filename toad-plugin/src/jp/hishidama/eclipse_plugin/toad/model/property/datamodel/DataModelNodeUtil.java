package jp.hishidama.eclipse_plugin.toad.model.property.datamodel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.view.SiblingDataModelTreeElement;

public class DataModelNodeUtil {

	public static SiblingDataModelTreeElement getSiblingDataModelNode(HasDataModelNode node) {
		SiblingDataModelTreeElement root = new SiblingDataModelTreeElement(null, null);
		Set<Object> set = new HashSet<Object>();
		Set<Integer> idSet = new HashSet<Integer>();
		node.collectSiblingDataModelNode(root, set, idSet);
		return root;
	}

	public static <N extends NodeElement & HasDataModelNode> void collectSiblingDataModelNode(
			SiblingDataModelTreeElement list, Set<Object> set, Set<Integer> idSet, N node) {
		if (set.contains(node) || idSet.contains(node.getId())) {
			return;
		}
		set.add(node);
		idSet.add(node.getId());
		SiblingDataModelTreeElement c = list.add(node);

		collectConnectionSiblingDataModelNode(c, set, idSet, node);

		NodeElement parent = node.getParent();
		if (parent != null) {
			parent.collectSiblingDataModelNode(c, set, idSet, node);
		}
	}

	public static void collectConnectionSiblingDataModelNode(SiblingDataModelTreeElement list, Set<Object> set,
			Set<Integer> idSet, NodeElement node) {
		collectSiblingDataModelNode(list, set, idSet, node.getIncomings());
		collectSiblingDataModelNode(list, set, idSet, node.getOutgoings());
	}

	private static void collectSiblingDataModelNode(SiblingDataModelTreeElement list, Set<Object> set,
			Set<Integer> idSet, List<Connection> clist) {
		for (Connection c : clist) {
			c.collectSiblingDataModelNode(list, set, idSet);
		}
	}
}
