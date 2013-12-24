package jp.hishidama.eclipse_plugin.toad.model.node.operator.delegator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen.OperatorMethodGenerator;
import jp.hishidama.eclipse_plugin.toad.model.node.Attribute;
import jp.hishidama.eclipse_plugin.toad.model.node.port.OpePort;
import jp.hishidama.eclipse_plugin.toad.model.property.datamodel.HasDataModelNode;
import jp.hishidama.eclipse_plugin.toad.validation.ValidateType;
import jp.hishidama.eclipse_plugin.toad.view.SiblingDataModelTreeElement;
import jp.hishidama.eclipse_plugin.util.StringUtil;

import org.eclipse.core.runtime.IStatus;

/**
 * MasterCheck.
 */
public class UserMasterCheck extends UserOperatorDelegate {
	public static final String CLASS = "com.asakusafw.vocabulary.operator.MasterCheck";
	public static final String FOUND_PORT = "foundPort";
	public static final String MISSED_PORT = "missedPort";

	public UserMasterCheck() {
		super("masterCheck", "マスター確認演算子", 2, 2, 2, 2);
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
	public boolean enableTypeParameter() {
		return true;
	}

	@Override
	public List<String> getPortRoleList(boolean in) {
		if (in) {
			return Arrays.asList("master", "tx");
		} else {
			return Arrays.asList("found", "missed");
		}
	}

	private static final String[] SAME = { "in.tx", "out.found", "out.missed" };

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
		addPortNameAnnotation(gen, "found", CLASS, FOUND_PORT, "found");
		addPortNameAnnotation(gen, "missed", CLASS, MISSED_PORT, "missed");
		gen.setAbstract(true);
		{
			String name = null;
			OpePort port = getPort(true, "master");
			if (port != null) {
				name = port.getName();
			}
			if (StringUtil.isEmpty(name)) {
				name = "マスター";
			}
			gen.setReturnClass("boolean", String.format("%sが存在する場合true", name));
		}
		gen.addArgumentModel(getPort(true, "master"));
		gen.addArgumentModel(getPort(true, "tx"));
		return gen;
	}
}
