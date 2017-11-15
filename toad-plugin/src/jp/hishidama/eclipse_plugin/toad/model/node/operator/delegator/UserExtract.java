package jp.hishidama.eclipse_plugin.toad.model.node.operator.delegator;

import jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen.OperatorMethodGenerator;
import jp.hishidama.eclipse_plugin.toad.model.node.port.OpePort;

/**
 * Extract.
 */
class UserExtract extends UserOperatorDelegate {
	private static final String CLASS = "com.asakusafw.vocabulary.operator.Extract";

	public UserExtract() {
		super("extract", "抽出演算子", 1, 1, 1, Integer.MAX_VALUE);
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
		OperatorMethodGenerator gen = new OperatorMethodGenerator();
		gen.addAnnotation(CLASS);
		gen.setAbstract(false);
		gen.setReturnVoid();
		gen.addArgumentModel(getPort(true, 0));
		for (OpePort port : node.getOutputPorts()) {
			gen.addArgumentModelResult(port);
		}
		return gen;
	}
}
