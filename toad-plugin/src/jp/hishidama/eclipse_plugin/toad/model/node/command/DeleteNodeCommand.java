package jp.hishidama.eclipse_plugin.toad.model.node.command;

import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;

import org.eclipse.gef.commands.Command;

public class DeleteNodeCommand extends Command {

	private Diagram diagram;
	private NodeElement node;

	public DeleteNodeCommand(Diagram diagram, NodeElement node) {
		this.diagram = diagram;
		this.node = node;
	}

	@Override
	public void execute() {
		diagram.removeContent(node);
	}

	@Override
	public void undo() {
		diagram.addContent(node);
	}
}
