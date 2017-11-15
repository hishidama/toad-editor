package jp.hishidama.eclipse_plugin.toad.model.connection.command;

import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

public class ConnectionComponentEditPolicy extends ComponentEditPolicy {

	@Override
	protected Command createDeleteCommand(GroupRequest request) {
		Connection connection = (Connection) getHost().getModel();
		return new DeleteConnectionCommand(connection);
	}
}
