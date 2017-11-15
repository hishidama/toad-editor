package jp.hishidama.eclipse_plugin.toad.model.node.command;

import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;
import jp.hishidama.eclipse_plugin.toad.model.connection.command.DeleteConnectionCommand;
import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.marker.MarkerNode;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.port.BasePort;
import jp.hishidama.eclipse_plugin.toad.model.node.port.command.DeletePortCommand;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

public class NodeComponentEditPolicy extends ComponentEditPolicy {

	@Override
	protected Command createDeleteCommand(GroupRequest request) {
		Diagram diagram = (Diagram) getHost().getParent().getModel();
		NodeElement node = (NodeElement) getHost().getModel();
		if (node instanceof MarkerNode) {
			return null;
		}

		CompoundCommand command = new CompoundCommand();
		deleteConnection(command, node);

		if (node instanceof BasePort) {
			BasePort c = (BasePort) node;
			NodeElement parent = c.getParent();
			if (parent == null) {
				return null;
			}
			command.add(new DeletePortCommand(c.getParent(), c));
		} else {
			command.add(new DeleteNodeCommand(diagram, node));
		}

		return command.unwrap();
	}

	public static void deleteConnection(CompoundCommand command, NodeElement node) {
		for (Connection c : node.getIncomings()) {
			command.add(new DeleteConnectionCommand(c));
		}
		for (Connection c : node.getOutgoings()) {
			command.add(new DeleteConnectionCommand(c));
		}
		for (NodeElement child : node.getChildren()) {
			deleteConnection(command, child);
		}
	}
}
