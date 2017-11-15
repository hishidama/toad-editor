package jp.hishidama.eclipse_plugin.toad.model.node.operator.delegator;

import java.util.List;
import java.util.Set;

import jp.hishidama.eclipse_plugin.toad.model.property.datamodel.HasDataModelNode;
import jp.hishidama.eclipse_plugin.toad.validation.ValidateType;
import jp.hishidama.eclipse_plugin.toad.view.SiblingDataModelTreeElement;

import org.eclipse.core.runtime.IStatus;

/**
 * core checkpoint.
 */
class CoreCheckPoint extends CoreOperatorDelegate {
	public CoreCheckPoint() {
		super("checkpoint", "チェックポイント演算子", 1, 1, 1, 1);
	}

	private static final String[] SAME = { "in.0", "out.0" };

	@Override
	public void collectSiblingDataModelNode(SiblingDataModelTreeElement list, Set<Object> set, Set<Integer> idSet,
			HasDataModelNode src) {
		collectSameSiblingDataModelNode(list, set, idSet, src, SAME);
	}

	@Override
	public void validate(ValidateType vtype, List<IStatus> result) {
		super.validate(vtype, result);
		validateDataModel(result, SAME);
	}
}
