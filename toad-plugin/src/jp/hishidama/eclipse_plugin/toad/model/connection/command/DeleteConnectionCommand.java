package jp.hishidama.eclipse_plugin.toad.model.connection.command;

import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;

import org.eclipse.gef.commands.Command;

public class DeleteConnectionCommand extends Command {

	private Connection connection;
	private NodeElement old_source;
	private NodeElement old_target;

	public DeleteConnectionCommand(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void execute() {
		old_source = connection.getSource();
		old_target = connection.getTarget();

		if (old_source != null) {
			old_source.removeOutgoing(connection);
		}
		if (old_target != null) {
			old_target.removeIncoming(connection);
		}
		connection.setSource(null);
		connection.setTarget(null);
	}

	@Override
	public void undo() {
		if (old_source != null) {
			old_source.addOutgoing(connection);
		}
		if (old_target != null) {
			old_target.addIncoming(connection);
		}
		connection.setSource(old_source);
		connection.setTarget(old_target);
	}
}
