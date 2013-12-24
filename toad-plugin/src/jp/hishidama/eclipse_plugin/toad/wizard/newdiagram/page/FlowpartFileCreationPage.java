package jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.page;

import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;
import jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.gen.FlowDiagramGenerator;
import jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.gen.FlowpartDiagramGenerator;

import org.eclipse.core.resources.IProject;

public class FlowpartFileCreationPage extends FlowFileCreationPage {
	private OperatorNode operator;

	public FlowpartFileCreationPage(IProject project) {
		this(project, null);
	}

	public FlowpartFileCreationPage(IProject project, OperatorNode operator) {
		super("FlowpartFileCreationPage", project, "ftoad");
		this.operator = operator;

		setTitle("フローパートの指定");
		setDescription("作成するフローパートの情報を入力して下さい。\n"
				+ "（例：クラス名がcom.example.flowpart.Ex1FlowPartのとき、src/main/toad/com/example/flowpart/Ex1FlowPart.ftoadが作られます）");
	}

	@Override
	protected boolean hasName() {
		return false;
	}

	@Override
	protected String validateName(String name) {
		return null;
	}

	@Override
	protected FlowDiagramGenerator createGenerator() {
		return new FlowpartDiagramGenerator(project, operator);
	}
}
