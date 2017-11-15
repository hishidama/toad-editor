package jp.hishidama.eclipse_plugin.toad.model.node.operator.delegator;

import java.util.List;
import java.util.Set;

import jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen.OperatorMethodGenerator;
import jp.hishidama.eclipse_plugin.toad.model.property.datamodel.HasDataModelNode;
import jp.hishidama.eclipse_plugin.toad.validation.ValidateType;
import jp.hishidama.eclipse_plugin.toad.view.SiblingDataModelTreeElement;

import org.eclipse.core.runtime.IStatus;

/**
 * Update.
 */
public class UserUpdate extends UserOperatorDelegate {
	public static final String CLASS = "com.asakusafw.vocabulary.operator.Update";
	public static final String OUTPUT_PORT = "outputPort";

	public UserUpdate() {
		super("update", "更新演算子", 1, 1, 1, 1);
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
		addPortNameAnnotation(gen, 0, CLASS, OUTPUT_PORT, "out");
		gen.setAbstract(false);
		gen.setReturnVoid();
		gen.addArgumentModel(getPort(true, 0));
		return gen;
	}
}
