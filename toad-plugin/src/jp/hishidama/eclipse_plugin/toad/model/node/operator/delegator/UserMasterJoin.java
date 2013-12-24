package jp.hishidama.eclipse_plugin.toad.model.node.operator.delegator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import jp.hishidama.eclipse_plugin.toad.clazz.JavadocClass;
import jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen.OperatorMethodGenerator;
import jp.hishidama.eclipse_plugin.toad.model.node.Attribute;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.GuessDataModelType;
import jp.hishidama.eclipse_plugin.toad.model.node.port.OpePort;
import jp.hishidama.eclipse_plugin.toad.model.property.datamodel.HasDataModelNode;
import jp.hishidama.eclipse_plugin.toad.validation.ValidateType;
import jp.hishidama.eclipse_plugin.toad.view.SiblingDataModelTreeElement;
import jp.hishidama.xtext.dmdl_editor.ui.wizard.page.DataModelType;

import org.eclipse.core.runtime.IStatus;

/**
 * MasterJoin.
 */
public class UserMasterJoin extends UserOperatorDelegate {
	public static final String CLASS = "com.asakusafw.vocabulary.operator.MasterJoin";
	public static final String JOINED_PORT = "joinedPort";
	public static final String MISSED_PORT = "missedPort";

	public UserMasterJoin() {
		super("masterJoin", "マスター結合演算子", 2, 2, 2, 2);
	}

	@Override
	public List<Attribute> getDefaultAttribute() {
		List<Attribute> list = new ArrayList<Attribute>();
		list.add(new Attribute(CLASS, "selection", "java.lang.String"));
		return list;
	}

	@Override
	public String getKeyTitle() {
		return "結合キー";
	}

	@Override
	public void setDescription(JavadocClass javadoc) {
		super.setDescription(javadoc);
		setPortDescriptionFromReturn(false, 0, javadoc);
	}

	@Override
	public List<String> getPortRoleList(boolean in) {
		if (in) {
			return Arrays.asList("master", "tx");
		} else {
			return Arrays.asList("joined", "missed");
		}
	}

	private static final String[] SAME = { "in.tx", "out.missed" };

	@Override
	public void collectSiblingDataModelNode(SiblingDataModelTreeElement list, Set<Object> set, Set<Integer> idSet,
			HasDataModelNode src) {
		collectSameSiblingDataModelNode(list, set, idSet, src, SAME);
	}

	@Override
	public GuessDataModelType guessDataModelType(OpePort port) {
		List<OpePort> out = node.getOutputPorts();
		if (out.size() <= 0) {
			return null;
		}
		OpePort p = out.get(0);
		if (p == port) {
			GuessDataModelType type = new GuessDataModelType(DataModelType.JOINED, port.getModelName());
			List<OpePort> list = node.getInputPorts();
			if (list.size() > 0) {
				type.setFirst(list.get(0).getModelName());
			}
			if (list.size() > 1) {
				type.setSecond(list.get(1).getModelName());
			}
			return type;
		}
		return null;
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
		addPortNameAnnotation(gen, "joined", CLASS, JOINED_PORT, "joined");
		addPortNameAnnotation(gen, "missed", CLASS, MISSED_PORT, "missed");
		gen.setAbstract(true);
		gen.setReturnModel(getPort(false, "joined"));

		List<OpePort> list = Arrays.asList(getPort(true, "master"), getPort(true, "tx"));
		for (OpePort port : list) {
			if (port != null) {
				gen.addArgumentModel(null, port.getModelName(), port.getName(), port.getDescription());
			}
		}
		return gen;
	}
}
