package jp.hishidama.eclipse_plugin.toad.model.node.operator.delegator;

import jp.hishidama.eclipse_plugin.toad.clazz.JavadocClass;
import jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen.OperatorMethodGenerator;

public abstract class CoreOperatorDelegate extends OperatorDelegate {

	protected CoreOperatorDelegate(String methodName, String description, int inMin, int inMax, int outMin, int outMax) {
		super(methodName, description, inMin, inMax, outMin, outMax);
	}

	@Override
	public void setDescription(JavadocClass javadoc) {
		// do nothing
	}

	@Override
	public OperatorMethodGenerator getSourceCode() {
		return null;
	}
}
