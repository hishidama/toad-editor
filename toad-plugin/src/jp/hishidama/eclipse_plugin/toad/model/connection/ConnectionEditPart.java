package jp.hishidama.eclipse_plugin.toad.model.connection;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.model.connection.command.ConnectionComponentEditPolicy;
import jp.hishidama.eclipse_plugin.toad.model.dialog.ConnectionPropertyDialog;
import jp.hishidama.eclipse_plugin.toad.model.node.jobflow.JobNode;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;

public class ConnectionEditPart extends AbstractConnectionEditPart implements PropertyChangeListener {

	@Override
	public Connection getModel() {
		return (Connection) super.getModel();
	}

	@Override
	protected IFigure createFigure() {
		ConnectionFigure figure = new ConnectionFigure();

		Connection model = getModel();
		figure.setSource(model.getSource());
		figure.setTarget(model.getTarget());

		return figure;
	}

	@Override
	public ConnectionFigure getFigure() {
		return (ConnectionFigure) super.getFigure();
	}

	@Override
	public void activate() {
		super.activate();

		Connection model = getModel();
		model.addPropertyChangeListener(this);
	}

	@Override
	public void deactivate() {
		super.deactivate();

		Connection model = getModel();
		model.removePropertyChangeListener(this);
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ConnectionComponentEditPolicy());
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new ConnectionEndpointEditPolicy());
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		String name = event.getPropertyName();
		if (Connection.PROP_SOURCE.equals(name)) {
			getFigure().setSource(getModel().getSource());
		} else if (Connection.PROP_TARGET.equals(name)) {
			getFigure().setTarget(getModel().getTarget());
		}
	}

	@Override
	public void performRequest(Request request) {
		Object t = request.getType();
		if (t == RequestConstants.REQ_OPEN) {
			performOpen();
		}
	}

	protected void performOpen() {
		Connection conn = getModel();
		if (conn.getSource() instanceof JobNode) {
			return;
		}
		if (conn.getTarget() instanceof JobNode) {
			return;
		}

		ConnectionPropertyDialog dialog = new ConnectionPropertyDialog(getEditor(), conn);
		dialog.open();
	}

	protected final ToadEditor getEditor() {
		return ToadEditor.getToadEditor(getViewer());
	}
}
