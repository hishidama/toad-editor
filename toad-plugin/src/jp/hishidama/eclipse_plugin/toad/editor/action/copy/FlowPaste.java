package jp.hishidama.eclipse_plugin.toad.editor.action.copy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.diagram.DiagramType;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.RectangleNode;
import jp.hishidama.eclipse_plugin.toad.model.node.command.CreateNodeCommand;
import jp.hishidama.eclipse_plugin.toad.model.node.datafile.DataFileNode;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

public class FlowPaste extends PasteCommandGenerator {

	private final DiagramType diagramType;

	public FlowPaste(DiagramType type) {
		this.diagramType = type;
	}

	@Override
	public Command getPasteCommand() {
		Command command = getPasteOperatorNodeCommand();
		if (command == null) {
			command = getPasteFlowpartFrameCommand();
		}
		if (command == null) {
			command = getPastePortCommand();
		}
		return command;
	}

	/**
	 * OperatorNodeを貼り付ける
	 * 
	 * @return コマンド
	 */
	private Command getPasteOperatorNodeCommand() {
		List<RectangleNode> list = getOperatorNode();
		if (list.isEmpty()) {
			return null;
		}
		setPasteNodeList(list);

		applyOffset(list);
		Map<Integer, Integer> convertMap = convertNewId(list);

		CompoundCommand compound = new CompoundCommand();
		Diagram diagram = editor.getDiagram();
		for (RectangleNode node : list) {
			int x = node.getX();
			int y = node.getY();
			CreateNodeCommand c = new CreateNodeCommand(diagram, node, x, y);
			compound.add(c);
		}

		addConnectionCommand(compound, clip, list, convertMap);

		return compound.unwrap();
	}

	private List<RectangleNode> getOperatorNode() {
		List<NodeElement> contents = clip.getContents();
		List<RectangleNode> list = new ArrayList<RectangleNode>(contents.size());
		for (NodeElement node : contents) {
			if (node instanceof OperatorNode) {
				list.add((OperatorNode) node);
			} else if ((node instanceof DataFileNode) && diagramType == DiagramType.JOBFLOW) {
				list.add((DataFileNode) node);
			}
		}
		return list;
	}

	/**
	 * FlowpartFrameをOperatorNodeに変換して貼り付ける
	 * 
	 * @return コマンド
	 */
	private Command getPasteFlowpartFrameCommand() {
		return null; // TODO FlowpartFrameをOperatorNodeに変換して貼り付ける
	}

	/**
	 * PortをOperatorNodeまたはFrameに貼り付ける
	 * 
	 * @return コマンド
	 */
	private Command getPastePortCommand() {
		return null; // TODO PortをOperatorNodeまたはFrameに貼り付ける
	}
}
