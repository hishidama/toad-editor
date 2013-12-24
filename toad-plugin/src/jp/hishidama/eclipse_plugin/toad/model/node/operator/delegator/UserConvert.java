package jp.hishidama.eclipse_plugin.toad.model.node.operator.delegator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import jp.hishidama.eclipse_plugin.toad.clazz.JavadocClass;
import jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen.OperatorMethodGenerator;
import jp.hishidama.eclipse_plugin.toad.model.property.datamodel.HasDataModelNode;
import jp.hishidama.eclipse_plugin.toad.validation.ValidateType;
import jp.hishidama.eclipse_plugin.toad.view.SiblingDataModelTreeElement;

import org.eclipse.core.runtime.IStatus;

/**
 * Convert.
 */
public class UserConvert extends UserOperatorDelegate {
	public static final String CLASS = "com.asakusafw.vocabulary.operator.Convert";
	public static final String CONVERTED_PORT = "convertedPort";
	public static final String ORIGINAL_PORT = "originalPort";

	public UserConvert() {
		super("convert", "変換演算子", 1, 1, 2, 2);
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
	public void setDescription(JavadocClass javadoc) {
		super.setDescription(javadoc);
		setPortDescriptionFromReturn(false, 0, javadoc);
	}

	@Override
	public List<String> getPortRoleList(boolean in) {
		if (in) {
			return Collections.emptyList();
		} else {
			return Arrays.asList("out", "original");
		}
	}

	private static final String[] SAME = { "in.0", "out.original" };

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
		addPortNameAnnotation(gen, "out", CLASS, CONVERTED_PORT, "out");
		addPortNameAnnotation(gen, "original", CLASS, ORIGINAL_PORT, "original");
		gen.setAbstract(false);
		gen.setReturnModel(getPort(false, "out"));
		gen.addArgumentModel(getPort(true, 0));
		return gen;
	}
}
