package jp.hishidama.eclipse_plugin.toad.model.node.operator.delegator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen.OperatorMethodGenerator;
import jp.hishidama.eclipse_plugin.toad.model.node.Attribute;
import jp.hishidama.eclipse_plugin.toad.model.node.port.OpePort;
import jp.hishidama.eclipse_plugin.toad.model.property.datamodel.HasDataModelNode;
import jp.hishidama.eclipse_plugin.toad.validation.ValidateType;
import jp.hishidama.eclipse_plugin.toad.view.SiblingDataModelTreeElement;

import org.eclipse.core.runtime.IStatus;

/**
 * Fold.
 */
public class UserFold extends UserOperatorDelegate {
	public static final String CLASS = "com.asakusafw.vocabulary.operator.Fold";
	public static final String OUTPUT_PORT = "outputPort";

	public UserFold() {
		super("fold", "畳み込み演算子", 1, 1, 1, 1);
	}

	@Override
	public List<Attribute> getDefaultAttribute() {
		List<Attribute> list = new ArrayList<Attribute>();
		list.add(new Attribute(CLASS, "com.asakusafw.vocabulary.flow.processor.PartialAggregation",
				"partialAggregation"));
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
		gen.setReturnVoid();
		OpePort port = getPort(true, 0);
		if (port != null) {
			gen.addArgumentModel(port, "left");
			gen.addArgumentModel(null, port.getModelName(), "right", port.getDescription());
		}
		return gen;
	}
}
