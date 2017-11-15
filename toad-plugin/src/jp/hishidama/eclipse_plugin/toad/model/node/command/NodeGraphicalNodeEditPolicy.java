package jp.hishidama.eclipse_plugin.toad.model.node.command;

import java.util.List;

import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;
import jp.hishidama.eclipse_plugin.toad.model.connection.command.CreateConnectionCommand;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElementEditPart;
import jp.hishidama.eclipse_plugin.toad.model.property.datamodel.DataModelNodeUtil;
import jp.hishidama.eclipse_plugin.toad.model.property.datamodel.HasDataModelNode;
import jp.hishidama.eclipse_plugin.toad.view.SiblingDataModelTreeElement;
import jp.hishidama.eclipse_plugin.util.StringUtil;
import jp.hishidama.eclipse_plugin.util.ToadCommandUtil;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;

public class NodeGraphicalNodeEditPolicy extends GraphicalNodeEditPolicy {

	public static final String DROP_TARGET_EDITPART = "drop_target_editPart";
	private RectangleFigure feedbackFigure;
	private NodeElementEditPart nowTarget;

	@Override
	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
		Connection connection = (Connection) request.getNewObject();
		NodeElement node = (NodeElement) request.getTargetEditPart().getModel();
		if (!node.canStartConnect()) {
			return null;
		}

		CreateConnectionCommand command = new CreateConnectionCommand(connection);
		command.setSource(node);

		request.setStartCommand(command);
		return command;
	}

	@Override
	protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
		CreateConnectionCommand command = (CreateConnectionCommand) request.getStartCommand();

		NodeElement node = (NodeElement) request.getTargetEditPart().getModel();
		command.setTarget(node);

		return transmitDataModel(command);
	}

	@Override
	protected Command getReconnectTargetCommand(ReconnectRequest request) {
		Connection connection = (Connection) request.getConnectionEditPart().getModel();
		CreateConnectionCommand command = new CreateConnectionCommand(connection);
		command.setSource(connection.getSource());

		NodeElementEditPart part = (NodeElementEditPart) request.getTarget();
		command.setTarget(part.getModel());
		return transmitDataModel(command);
	}

	@Override
	protected Command getReconnectSourceCommand(ReconnectRequest request) {
		Connection connection = (Connection) request.getConnectionEditPart().getModel();
		CreateConnectionCommand command = new CreateConnectionCommand(connection);
		NodeElementEditPart part = (NodeElementEditPart) request.getTarget();
		command.setSource(part.getModel());

		command.setTarget(connection.getTarget());
		return transmitDataModel(command);
	}

	public static Command transmitDataModel(CreateConnectionCommand command) {
		NodeElement source = command.getSource();
		if (!(source instanceof HasDataModelNode)) {
			return command;
		}
		NodeElement target = command.getTarget();
		if (!(target instanceof HasDataModelNode)) {
			return command;
		}
		HasDataModelNode s = (HasDataModelNode) source;
		HasDataModelNode t = (HasDataModelNode) target;

		SiblingDataModelTreeElement root = null;
		HasDataModelNode from = null;
		if (StringUtil.nonEmpty(s.getModelName()) && StringUtil.isEmpty(t.getModelName())) {
			root = DataModelNodeUtil.getSiblingDataModelNode(t);
			from = s;
		} else if (StringUtil.nonEmpty(t.getModelName()) && StringUtil.isEmpty(s.getModelName())) {
			root = DataModelNodeUtil.getSiblingDataModelNode(s);
			from = t;
		}
		if (root == null) {
			return command;
		}

		CompoundCommand compound = new CompoundCommand();
		compound.add(command);
		collectCommand(compound, root.getChildren(), from);
		return compound.unwrap();
	}

	private static void collectCommand(CompoundCommand compound, List<SiblingDataModelTreeElement> list, HasDataModelNode from) {
		for (SiblingDataModelTreeElement c : list) {
			HasDataModelNode to = c.getDataModelNode();
			if (StringUtil.isEmpty(to.getModelName())) {
				ToadCommandUtil.add(compound, to.getModelNameCommand(from.getModelName()));
				if (StringUtil.isEmpty(to.getModelDescription())) {
					ToadCommandUtil.add(compound, to.getModelDescriptionCommand(from.getModelDescription()));
				}

				collectCommand(compound, c.getChildren(), from);
			}
		}
	}

	@Override
	public void eraseSourceFeedback(Request request) {
		feedback(null);
		super.eraseSourceFeedback(request);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void showSourceFeedback(Request request) {
		if (REQ_MOVE.equals(request.getType())) {
			ChangeBoundsRequest crequest = (ChangeBoundsRequest) request;
			if (crequest.getEditParts().size() == 1 && isFeedbackSource()) {
				Point mouse = crequest.getLocation();
				NodeElementEditPart target = getFeedbackTarget(mouse);
				feedback(target);
				request.getExtendedData().put(DROP_TARGET_EDITPART, target);
			}
		}
		super.showSourceFeedback(request);
	}

	private boolean isFeedbackSource() {
		EditPart editPart = getHost();
		if (editPart instanceof NodeElementEditPart) {
			NodeElementEditPart part = (NodeElementEditPart) editPart;
			NodeElement node = part.getModel();
			return node.canStartConnect();
		}
		return false;
	}

	private NodeElementEditPart getFeedbackTarget(Point mouse) {
		NodeElementEditPart part = (NodeElementEditPart) getHost();
		GraphicalEditPart diagramPart = part.getParent();
		EditPart targetEditPart = diagramPart.getViewer().findObjectAt(mouse);
		if (targetEditPart == part) {
			return null;
		}
		if (targetEditPart instanceof NodeElementEditPart) {
			NodeElementEditPart target = (NodeElementEditPart) targetEditPart;
			NodeElement node = target.getModel();
			Connection connection = new Connection(); // dummy
			if (node.canConnectFrom(connection, part.getModel())) {
				return target;
			}
		}
		return null;
	}

	private void feedback(NodeElementEditPart target) {
		if (target == null || nowTarget != target) {
			if (feedbackFigure != null) {
				getFeedbackLayer().remove(feedbackFigure);
				feedbackFigure = null;
				nowTarget = null;
			}
		}

		if (target != null && feedbackFigure == null) {
			feedbackFigure = new RectangleFigure();
			feedbackFigure.setBackgroundColor(ColorConstants.blue);
			feedbackFigure.setFillXOR(true);
			feedbackFigure.setBounds(target.getFigure().getBounds());
			getFeedbackLayer().add(feedbackFigure);
			nowTarget = target;
		}
	}
}
