package jp.hishidama.eclipse_plugin.toad.editor.action.copy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.command.CreateNodeCommand;
import jp.hishidama.eclipse_plugin.toad.model.node.jobflow.JobNode;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

public class BatchPaste extends PasteCommandGenerator {

	@Override
	public Command getPasteCommand() {
		Command command = getPasteJobNodeCommand();
		if (command == null) {
			command = getPasteJobFrameCommand();
		}
		return command;
	}

	/**
	 * JobNodeを貼り付ける
	 * 
	 * @return コマンド
	 */
	private Command getPasteJobNodeCommand() {
		List<JobNode> list = getJobNode();
		if (list.isEmpty()) {
			return null;
		}
		setPasteNodeList(list);

		applyOffset(list);
		Map<Integer, Integer> convertMap = convertNewId(list);

		CompoundCommand compound = new CompoundCommand();
		Diagram diagram = editor.getDiagram();
		for (JobNode node : list) {
			int x = node.getX();
			int y = node.getY();
			CreateNodeCommand c = new CreateNodeCommand(diagram, node, x, y);
			compound.add(c);
		}

		addConnectionCommand(compound, clip, list, convertMap);

		return compound.unwrap();
	}

	private List<JobNode> getJobNode() {
		List<NodeElement> contents = clip.getContents();
		List<JobNode> list = new ArrayList<JobNode>(contents.size());
		for (NodeElement node : contents) {
			if (node instanceof JobNode) {
				list.add((JobNode) node);
			}
		}
		return list;
	}

	/**
	 * JobFrameをJobNodeに変換して貼り付ける
	 * 
	 * @return コマンド
	 */
	private Command getPasteJobFrameCommand() {
		return null; // TODO JobFrameをJobNodeに変換して貼り付け
	}
}
