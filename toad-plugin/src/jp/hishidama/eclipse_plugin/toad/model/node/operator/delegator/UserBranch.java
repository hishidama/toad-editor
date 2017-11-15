package jp.hishidama.eclipse_plugin.toad.model.node.operator.delegator;

import java.util.List;
import java.util.Set;

import jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen.OperatorMethodGenerator;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;
import jp.hishidama.eclipse_plugin.toad.model.property.datamodel.HasDataModelNode;
import jp.hishidama.eclipse_plugin.toad.validation.ValidateType;
import jp.hishidama.eclipse_plugin.toad.view.SiblingDataModelTreeElement;

import org.eclipse.core.runtime.IStatus;

/**
 * Branch.
 */
class UserBranch extends UserOperatorDelegate {
	private static final String CLASS = "com.asakusafw.vocabulary.operator.Branch";

	public UserBranch() {
		super("branch", "分岐演算子", 1, 1, 1, Integer.MAX_VALUE);
	}

	@Override
	public boolean enableValueParameter() {
		return true;
	}

	@Override
	public boolean enableTypeParameter() {
		return true;
	}

	@Override
	public boolean isReturnEnum() {
		return true;
	}

	@Override
	public void collectSiblingDataModelNode(SiblingDataModelTreeElement list, Set<Object> set, Set<Integer> idSet,
			HasDataModelNode src) {
		collectAllSiblingDataModelNode(list, set, idSet, src);
	}

	@Override
	public void validate(ValidateType vtype, List<IStatus> result) {
		super.validate(vtype, result);
		validateDataModelAllPorts(result);
	}

	@Override
	public OperatorMethodGenerator getSourceCode() {
		OperatorMethodGenerator gen = new OperatorMethodGenerator();
		gen.addAnnotation(CLASS);
		gen.setAbstract(false);
		gen.setReturnClass(node.getProperty(OperatorNode.KEY_RETURN_ENUM_NAME),
				node.getProperty(OperatorNode.KEY_RETURN_DESCRIPTION));
		gen.addArgumentModel(getPort(true, 0));
		return gen;
	}
}
