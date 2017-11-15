package jp.hishidama.eclipse_plugin.toad.model.node.operator.delegator;

import jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen.OperatorMethodGenerator;

/**
 * FlowPart.
 */
class UserFlowPart extends UserOperatorDelegate {
	public UserFlowPart() {
		super("flowPart", "フロー演算子", 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
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
	public OperatorMethodGenerator getSourceCode() {
		return null;
	}
}
