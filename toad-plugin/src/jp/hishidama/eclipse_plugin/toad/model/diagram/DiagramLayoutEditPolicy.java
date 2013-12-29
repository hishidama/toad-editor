package jp.hishidama.eclipse_plugin.toad.model.diagram;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;
import jp.hishidama.eclipse_plugin.toad.model.connection.command.CreateConnectionCommand;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElementEditPart;
import jp.hishidama.eclipse_plugin.toad.model.node.RectangleNode;
import jp.hishidama.eclipse_plugin.toad.model.node.command.CreateNodeCommand;
import jp.hishidama.eclipse_plugin.toad.model.node.command.MoveNodeCommand;
import jp.hishidama.eclipse_plugin.toad.model.node.command.NodeGraphicalNodeEditPolicy;
import jp.hishidama.eclipse_plugin.toad.model.node.port.BasePort;
import jp.hishidama.eclipse_plugin.toad.model.node.port.BasePortEditPart;
import jp.hishidama.eclipse_plugin.toad.model.node.port.command.MovePortCommand;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

public class DiagramLayoutEditPolicy extends XYLayoutEditPolicy {

	@Override
	protected Command getCreateCommand(CreateRequest request) {
		Rectangle rect = (Rectangle) getConstraintFor(request); // 相対座標へ変換
		Point point = rect.getLocation();
		RectangleNode node = (RectangleNode) request.getNewObject();
		Diagram diagram = (Diagram) getHost().getModel();
		return new CreateNodeCommand(diagram, node, point.x, point.y);
	}

	@Override
	protected Command createChangeConstraintCommand(ChangeBoundsRequest request, EditPart child, Object constraint) {
		NodeElementEditPart part = (NodeElementEditPart) request.getExtendedData().get(
				NodeGraphicalNodeEditPolicy.DROP_TARGET_EDITPART);
		if (part == null) {
			return createMoveNodeCommand(child, constraint);
		} else {
			return createConnectionCommand(child, part);
		}
	}

	private Command createMoveNodeCommand(EditPart child, Object constraint) {
		NodeElement node = (NodeElement) child.getModel();

		{ // 親ノードが選択範囲に含まれている場合は何もしない
			Set<NodeElement> set = new HashSet<NodeElement>();
			{
				@SuppressWarnings("unchecked")
				List<Object> selected = child.getViewer().getSelectedEditParts();
				for (Object select : selected) {
					if (select instanceof NodeElementEditPart) {
						NodeElement n = ((NodeElementEditPart) select).getModel();
						set.add(n);
					}
				}
			}
			for (NodeElement parent = node.getParent(); parent != null; parent = parent.getParent()) {
				if (set.contains(parent)) {
					return new Command() { // nothing
					};
				}
			}
		}

		Rectangle rect = (Rectangle) constraint;
		if (node instanceof BasePort) {
			BasePort port = (BasePort) node;
			BasePortEditPart part = (BasePortEditPart) child;
			Rectangle old = part.getFigure().getBounds();
			int cx = port.getCx() + rect.x - old.x;
			int cy = port.getCy() + rect.y - old.y;
			return new MovePortCommand(port, cx, cy);
		}
		return new MoveNodeCommand((RectangleNode) node, rect.x, rect.y, rect.width, rect.height);
	}

	private Command createConnectionCommand(EditPart child, NodeElementEditPart part) {
		NodeElement source = (NodeElement) child.getModel();
		NodeElement target = part.getModel();

		Connection connection = new Connection();
		CreateConnectionCommand command = new CreateConnectionCommand(connection);
		command.setSource(source);
		command.setTarget(target);
		return NodeGraphicalNodeEditPolicy.transmitDataModel(command);
	}
}
