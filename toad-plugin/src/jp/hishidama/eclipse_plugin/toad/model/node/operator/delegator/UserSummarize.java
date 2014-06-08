package jp.hishidama.eclipse_plugin.toad.model.node.operator.delegator;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.asakusafw_wrapper.dmdl.DataModelType;
import jp.hishidama.eclipse_plugin.toad.clazz.JavadocClass;
import jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen.OperatorMethodGenerator;
import jp.hishidama.eclipse_plugin.toad.model.node.Attribute;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.GuessDataModelType;
import jp.hishidama.eclipse_plugin.toad.model.node.port.OpePort;
import jp.hishidama.eclipse_plugin.toad.validation.ValidateType;

import org.eclipse.core.runtime.IStatus;

/**
 * Summarize.
 */
public class UserSummarize extends UserOperatorDelegate {
	public static final String CLASS = "com.asakusafw.vocabulary.operator.Summarize";
	public static final String SUMMARIZED_PORT = "summarizedPort";

	public UserSummarize() {
		super("summarize", "単純集計演算子", 1, 1, 1, 1);
	}

	@Override
	public List<Attribute> getDefaultAttribute() {
		List<Attribute> list = new ArrayList<Attribute>();
		list.add(new Attribute(CLASS, "com.asakusafw.vocabulary.flow.processor.PartialAggregation",
				"partialAggregation"));
		return list;
	}

	@Override
	public String getKeyTitle() {
		return "集計キー";
	}

	@Override
	public void setDescription(JavadocClass javadoc) {
		super.setDescription(javadoc);
		setPortDescriptionFromReturn(false, 0, javadoc);
	}

	@Override
	public GuessDataModelType guessDataModelType(OpePort port) {
		for (OpePort p : node.getOutputPorts()) {
			if (p == port) {
				GuessDataModelType type = new GuessDataModelType(DataModelType.SUMMARIZED, port.getModelName());
				List<OpePort> list = node.getInputPorts();
				if (list.size() > 0) {
					type.setFirst(list.get(0).getModelName());
				}
				return type;
			}
		}
		return null;
	}

	@Override
	public void validate(ValidateType vtype, List<IStatus> result) {
		super.validate(vtype, result);
		// TODO 出力が集計モデルであること
	}

	@Override
	public OperatorMethodGenerator getSourceCode() {
		OperatorMethodGenerator gen = new OperatorMethodGenerator();
		gen.addAnnotation(CLASS);
		addPortNameAnnotation(gen, 0, CLASS, SUMMARIZED_PORT, "out");
		gen.setAbstract(true);
		gen.setReturnModel(getPort(false, 0));
		gen.addArgumentModel(getPort(true, 0));
		return gen;
	}
}
