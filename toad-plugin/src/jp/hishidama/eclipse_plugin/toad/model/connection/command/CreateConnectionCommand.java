package jp.hishidama.eclipse_plugin.toad.model.connection.command;

import java.util.List;

import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;
import jp.hishidama.eclipse_plugin.toad.model.frame.FrameNode;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;

import org.eclipse.gef.commands.Command;

public class CreateConnectionCommand extends Command {

	private Connection connection;
	private NodeElement source, old_source;
	private NodeElement target, old_target;

	public CreateConnectionCommand(Connection connection) {
		this.connection = connection;
		old_source = connection.getSource();
		old_target = connection.getTarget();
	}

	public void setSource(NodeElement source) {
		this.source = source;
	}

	public void setTarget(NodeElement target) {
		this.target = target;
	}

	public NodeElement getSource() {
		return source;
	}

	public NodeElement getTarget() {
		return target;
	}

	@Override
	public boolean canExecute() {
		if (source == null || target == null) {
			return false;
		}
		if (source.equals(target)) {
			return false;
		}
		NodeElement sourceParent = source.getParent();
		if (sourceParent != null) {
			if (sourceParent.equals(target)) {
				return false;
			}
		}
		NodeElement targetParent = target.getParent();
		if (targetParent != null) {
			if (targetParent.equals(source)) {
				return false;
			}
			if (targetParent.equals(sourceParent) && !(targetParent instanceof FrameNode)) {
				return false;
			}
		}

		List<Connection> list = target.getIncomings();
		for (Connection c : list) {
			if (c == connection) {
				continue;
			}
			if (source.equals(c.getSource())) {
				return false;
			}
		}

		return source.canConnectTo(connection, target) && target.canConnectFrom(connection, source);
	}

	@Override
	public void execute() {
		if (old_source != null) {
			old_source.removeOutgoing(connection);
		}
		if (old_target != null) {
			old_target.removeIncoming(connection);
		}
		connection.setSource(source);
		connection.setTarget(target);
		source.addOutgoing(connection);
		target.addIncoming(connection);
	}

	@Override
	public void undo() {
		source.removeOutgoing(connection);
		target.removeIncoming(connection);
		connection.setSource(old_source);
		connection.setTarget(old_target);
		if (old_source != null) {
			old_source.addOutgoing(connection);
		}
		if (old_target != null) {
			old_target.addIncoming(connection);
		}
	}
}
