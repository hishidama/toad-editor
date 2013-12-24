package jp.hishidama.eclipse_plugin.toad.model.node.operator.delegator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen.OperatorMethodGenerator;
import jp.hishidama.eclipse_plugin.toad.model.node.Attribute;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;
import jp.hishidama.eclipse_plugin.toad.model.property.datamodel.HasDataModelNode;
import jp.hishidama.eclipse_plugin.toad.validation.ValidateType;
import jp.hishidama.eclipse_plugin.toad.view.SiblingDataModelTreeElement;

import org.eclipse.core.runtime.IStatus;

/**
 * MasterBranch.
 */
class UserMasterBranch extends UserOperatorDelegate {
	private static final String CLASS = "com.asakusafw.vocabulary.operator.MasterBranch";

	public UserMasterBranch() {
		super("masterBranch", "マスター分岐演算子", 2, 2, 1, Integer.MAX_VALUE);
	}

	@Override
	public List<Attribute> getDefaultAttribute() {
		List<Attribute> list = new ArrayList<Attribute>();
		list.add(new Attribute(CLASS, "selection", "java.lang.String"));
		return list;
	}

	@Override
	public List<Attribute> getDefaultPortAnnotation() {
		List<Attribute> list = new ArrayList<Attribute>();
		list.add(new Attribute(KEY, "group", "java.lang.String"));
		return list;
	}

	@Override
	public String getKeyTitle() {
		return "結合キー";
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
	public List<String> getPortRoleList(boolean in) {
		if (in) {
			return Arrays.asList("master", "tx");
		} else {
			return Collections.emptyList();
		}
	}

	private static final String[] SAME = { "in.tx", "out.0-" };

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
		gen.setAbstract(false);
		gen.setReturnClass(node.getProperty(OperatorNode.KEY_RETURN_ENUM_NAME),
				node.getProperty(OperatorNode.KEY_RETURN_DESCRIPTION));
		gen.addArgumentModel(getPort(true, "master"));
		gen.addArgumentModel(getPort(true, "tx"));
		return gen;
	}
}
