package jp.hishidama.eclipse_plugin.toad.model.node.port;

import java.beans.PropertyChangeEvent;

import jp.hishidama.eclipse_plugin.toad.model.dialog.PortPropertyDialog;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElementEditPart;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.Request;

public abstract class BasePortEditPart extends NodeElementEditPart {

	@Override
	protected IFigure createFigure() {
		BasePortFigure figure = createPortFigure();

		refreshName(figure);
		refreshDescription(figure);
		refreshModelName(figure);
		refreshPosition(figure);

		return figure;
	}

	protected abstract BasePortFigure createPortFigure();

	@Override
	public BasePortFigure getFigure() {
		return (BasePortFigure) super.getFigure();
	}

	@Override
	public BasePort getModel() {
		return (BasePort) super.getModel();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		String name = event.getPropertyName();
		if (BasePort.PROP_NAME.equals(name)) {
			refreshName(getFigure());
		} else if (BasePort.PROP_DESCRIPTION.equals(name)) {
			refreshDescription(getFigure());
		} else if (BasePort.PROP_MODEL_NAME.equals(name) || BasePort.PROP_MODEL_DESCRIPTION.equals(name)) {
			refreshModelName(getFigure());
		} else if (BasePort.PROP_CX.equals(name) || BasePort.PROP_CY.equals(name)) {
			refreshVisuals();
		} else if (BasePort.PROP_POSITION.equals(name)) {
			refreshPosition(getFigure());
			refreshVisuals();
		}
		super.propertyChange(event);
	}

	private void refreshName(BasePortFigure figure) {
		BasePort model = getModel();
		figure.setName(model.getName());
	}

	private void refreshDescription(BasePortFigure figure) {
		BasePort model = getModel();
		figure.setDescription(model.getDescription());
	}

	private void refreshModelName(BasePortFigure figure) {
		BasePort model = getModel();
		figure.setModelName(model.getModelName(), model.getModelDescription());
	}

	private void refreshPosition(BasePortFigure figure) {
		BasePort model = getModel();
		figure.setLabelPosition(model.getNamePosition());
	}

	@Override
	protected Rectangle calculateFigureBounds() {
		BasePort model = getModel();
		Rectangle rect = getFigure().calculateBounds(model.getCx(), model.getCy(), model.getName(),
				model.getNamePosition());
		return rect;
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connectioneditpart) {
		return new BaseAnchor(getFigure(), true);
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return new BaseAnchor(getFigure(), true);
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connectioneditpart) {
		return new BaseAnchor(getFigure(), false);
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return new BaseAnchor(getFigure(), false);
	}

	@Override
	protected void performOpen() {
		PortPropertyDialog dialog = new PortPropertyDialog(getEditor(), getModel());
		dialog.open();
	}
}
