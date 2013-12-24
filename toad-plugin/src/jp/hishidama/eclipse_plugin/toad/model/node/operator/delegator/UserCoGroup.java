package jp.hishidama.eclipse_plugin.toad.model.node.operator.delegator;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen.OperatorMethodGenerator;
import jp.hishidama.eclipse_plugin.toad.model.node.Attribute;
import jp.hishidama.eclipse_plugin.toad.model.node.port.OpePort;

/**
 * CoGroup.
 */
class UserCoGroup extends UserOperatorDelegate {
	private static final String CLASS = "com.asakusafw.vocabulary.operator.CoGroup";

	public UserCoGroup() {
		super("coGroup", "グループ結合演算子", 1, Integer.MAX_VALUE, 1, Integer.MAX_VALUE);
	}

	@Override
	public List<Attribute> getDefaultAttribute() {
		List<Attribute> list = new ArrayList<Attribute>();
		list.add(new Attribute(CLASS, "inputBuffer", "com.asakusafw.vocabulary.flow.processor.InputBuffer"));
		return list;
	}

	@Override
	public List<Attribute> getDefaultPortAnnotation() {
		List<Attribute> list = new ArrayList<Attribute>();
		list.add(new Attribute(KEY, "group", "java.lang.String"));
		list.add(new Attribute(KEY, "order", "java.lang.String"));
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
	public OperatorMethodGenerator getSourceCode() {
		OperatorMethodGenerator gen = new OperatorMethodGenerator();
		gen.addAnnotation(CLASS);
		gen.setAbstract(false);
		gen.setReturnVoid();
		for (OpePort port : node.getInputPorts()) {
			gen.addArgumentModelList(port);
		}
		for (OpePort port : node.getOutputPorts()) {
			gen.addArgumentModelResult(port);
		}
		return gen;
	}
}
