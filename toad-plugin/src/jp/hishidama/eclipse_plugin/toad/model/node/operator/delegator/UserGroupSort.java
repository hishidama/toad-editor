package jp.hishidama.eclipse_plugin.toad.model.node.operator.delegator;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen.OperatorMethodGenerator;
import jp.hishidama.eclipse_plugin.toad.model.node.Attribute;
import jp.hishidama.eclipse_plugin.toad.model.node.port.OpePort;

/**
 * GroupSort.
 */
class UserGroupSort extends UserOperatorDelegate {
	private static final String CLASS = "com.asakusafw.vocabulary.operator.GroupSort";

	public UserGroupSort() {
		super("groupSort", "グループ整列演算子", 1, 1, 1, Integer.MAX_VALUE);
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
		return "集計キー";
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
		gen.addArgumentModelList(getPort(true, 0));
		for (OpePort port : node.getOutputPorts()) {
			gen.addArgumentModelResult(port);
		}
		return gen;
	}
}
