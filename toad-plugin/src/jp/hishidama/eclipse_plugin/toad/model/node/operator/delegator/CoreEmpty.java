package jp.hishidama.eclipse_plugin.toad.model.node.operator.delegator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.property.datamodel.HasDataModelNode;
import jp.hishidama.eclipse_plugin.toad.validation.ValidateType;
import jp.hishidama.eclipse_plugin.toad.view.SiblingDataModelTreeElement;
import jp.hishidama.xtext.dmdl_editor.validation.ErrorStatus;

import org.eclipse.core.runtime.IStatus;

/**
 * core empty.
 */
class CoreEmpty extends CoreOperatorDelegate {
	public CoreEmpty() {
		super("empty", "空演算子", 0, 0, 0, 0);
	}

	@Override
	public String getEllipseFigureText() {
		return "φ";
	}

	@Override
	public boolean canStartConnect() {
		return true;
	}

	@Override
	public boolean canConnectTo() {
		return true;
	}

	@Override
	public boolean canConnectFrom() {
		return false;
	}

	@Override
	public void collectSiblingDataModelNode(SiblingDataModelTreeElement list, Set<Object> set, Set<Integer> idSet,
			HasDataModelNode src) {
		collectAllSiblingDataModelNode(list, set, idSet, src);
	}

	@Override
	public void validate(ValidateType vtype, List<IStatus> result) {
		super.validate(vtype, result);

		List<Connection> incomings = node.getIncomings();
		if (!incomings.isEmpty()) {
			result.add(new ErrorStatus("空演算子へは入力できません。"));
		}

		List<Connection> outgoings = node.getOutgoings();
		if (outgoings.isEmpty()) {
			result.add(new ErrorStatus("空演算子からの出力がありません。"));
		}

		List<NodeElement> list = new ArrayList<NodeElement>(outgoings.size());
		for (Connection c : outgoings) {
			NodeElement n = c.getOpposite(node);
			if (n instanceof HasDataModelNode) {
				list.add(n);
			}
		}
		validateDataModel(result, list);
	}
}
