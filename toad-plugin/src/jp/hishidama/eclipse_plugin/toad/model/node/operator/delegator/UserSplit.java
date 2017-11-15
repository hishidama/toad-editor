package jp.hishidama.eclipse_plugin.toad.model.node.operator.delegator;

import java.util.List;

import jp.hishidama.eclipse_plugin.asakusafw_wrapper.dmdl.DataModelType;
import jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen.OperatorMethodGenerator;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.GuessDataModelType;
import jp.hishidama.eclipse_plugin.toad.model.node.port.OpePort;
import jp.hishidama.eclipse_plugin.toad.validation.ValidateType;

import org.eclipse.core.runtime.IStatus;

/**
 * Split.
 */
class UserSplit extends UserOperatorDelegate {
	private static final String CLASS = "com.asakusafw.vocabulary.operator.Split";

	public UserSplit() {
		super("split", "分割演算子", 1, 1, 2, 2);
	}

	@Override
	public GuessDataModelType guessDataModelType(OpePort port) {
		for (OpePort p : node.getInputPorts()) {
			if (p == port) {
				GuessDataModelType type = new GuessDataModelType(DataModelType.JOINED, port.getModelName());
				List<OpePort> list = node.getOutputPorts();
				if (list.size() > 0) {
					type.setFirst(list.get(0).getModelName());
				}
				if (list.size() > 1) {
					type.setSecond(list.get(1).getModelName());
				}
				return type;
			}
		}
		return null;
	}

	@Override
	public void validate(ValidateType vtype, List<IStatus> result) {
		super.validate(vtype, result);
		// TODO 入力が結合モデルであること
	}

	@Override
	public OperatorMethodGenerator getSourceCode() {
		OperatorMethodGenerator gen = new OperatorMethodGenerator();
		gen.addAnnotation(CLASS);
		gen.setAbstract(true);
		gen.setReturnVoid();
		for (OpePort port : node.getInputPorts()) {
			gen.addArgumentModel(port);
		}
		for (OpePort port : node.getOutputPorts()) {
			gen.addArgumentModelResult(port);
		}
		return gen;
	}
}
