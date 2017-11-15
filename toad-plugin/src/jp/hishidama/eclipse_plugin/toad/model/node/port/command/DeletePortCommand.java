package jp.hishidama.eclipse_plugin.toad.model.node.port.command;

import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.port.BasePort;

import org.eclipse.gef.commands.Command;

public class DeletePortCommand extends Command {

	private NodeElement node;
	private BasePort port;
	private int old_direction;
	private int old_index;

	public DeletePortCommand(NodeElement node, BasePort port) {
		this.node = node;
		this.port = port;
	}

	@Override
	public void execute() {
		old_direction = port.getDirection();
		old_index = node.getChildren().indexOf(port);
		node.removeChild(port);
	}

	@Override
	public void undo() {
		node.addChild(old_index, port, old_direction);
	}
}
