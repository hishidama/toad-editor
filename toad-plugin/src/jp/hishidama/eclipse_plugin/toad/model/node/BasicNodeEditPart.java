package jp.hishidama.eclipse_plugin.toad.model.node;

import java.beans.PropertyChangeEvent;

public abstract class BasicNodeEditPart extends RectangleNodeEditPart {

	@Override
	public BasicNodeFigure getFigure() {
		return (BasicNodeFigure) super.getFigure();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		String name = event.getPropertyName();
		if (NodeElement.PROP_DESCRIPTION.equals(name)) {
			refreshDescription(getFigure());
		} else if (NodeElement.PROP_TYPE.equals(name)) {
			refreshType(getFigure());
		}
		super.propertyChange(event);
	}

	protected final void refreshType(BasicNodeFigure figure) {
		NodeElement model = getModel();
		String type = model.getFigureLabel();
		figure.setType(type);
	}

	protected final void refreshDescription(BasicNodeFigure figure) {
		NodeElement model = getModel();
		figure.setDescription(model.getDescription());
	}
}
