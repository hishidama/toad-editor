package jp.hishidama.eclipse_plugin.toad.model.node.operator.delegator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen.OperatorMethodGenerator;
import jp.hishidama.eclipse_plugin.toad.model.node.Attribute;
import jp.hishidama.eclipse_plugin.toad.model.property.datamodel.HasDataModelNode;
import jp.hishidama.eclipse_plugin.toad.validation.ValidateType;
import jp.hishidama.eclipse_plugin.toad.view.SiblingDataModelTreeElement;

import org.eclipse.core.runtime.IStatus;

/**
 * Logging.
 */
public class UserLogging extends UserOperatorDelegate {
	public static final String CLASS = "com.asakusafw.vocabulary.operator.Logging";
	public static final String OUTPUT_PORT = "outputPort";

	public UserLogging() {
		super("logging", "ロギング演算子", 1, 1, 1, 1);
	}

	@Override
	public List<Attribute> getDefaultAttribute() {
		List<Attribute> list = new ArrayList<Attribute>();
		list.add(new Attribute(CLASS, "value", "com.asakusafw.vocabulary.operator.Logging.Level"));
		return list;
	}

	@Override
	public boolean enableValueParameter() {
		return true;
	}

	@Override
	public boolean enableTypeParameter() {
		return true;
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

	@Override
	public OperatorMethodGenerator getSourceCode() {
		OperatorMethodGenerator gen = new OperatorMethodGenerator();
		gen.addAnnotation(CLASS);
		addPortNameAnnotation(gen, 0, CLASS, OUTPUT_PORT, "out");
		gen.setAbstract(false);
		gen.setReturnClass("java.lang.String", "ログメッセージ");
		gen.addArgumentModel(getPort(true, 0));
		return gen;
	}
}
