package jp.hishidama.eclipse_plugin.toad.model.node.operator;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;

public class CoreOperatorFactory extends UserOperatorFactory {

	public CoreOperatorFactory(ToadEditor editor, String name) {
		super(editor, name);
	}

	@Override
	public OperatorNode getNewObject() {
		OperatorNode node = super.getNewObject();

		node.setClassName("com.asakusafw.vocabulary.flow.util.CoreOperators");
		if (node.getEllipseFigureText() != null) {
			node.setWidth(16);
			node.setHeight(16);
		}

		return node;
	}
}
