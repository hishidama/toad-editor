package jp.hishidama.eclipse_plugin.toad.model.node.port.command;

import jp.hishidama.eclipse_plugin.toad.model.node.port.BasePort;
import jp.hishidama.eclipse_plugin.toad.model.property.port.HasPortNode;

import org.eclipse.gef.commands.Command;

public class CreatePortCommand<C extends BasePort> extends Command {

	private HasPortNode<C> node;
	private int index;
	private C port;
	private int direction;

	public CreatePortCommand(HasPortNode<C> node, C port, int direction) {
		this(node, -1, port, direction);
	}

	public CreatePortCommand(HasPortNode<C> node, int index, C port, int direction) {
		this.node = node;
		this.index = index;
		this.port = port;
		this.direction = direction;
	}

	@Override
	public void execute() {
		node.addPort(index, port, direction);
	}

	@Override
	public void undo() {
		node.removePort(port);
	}
}
