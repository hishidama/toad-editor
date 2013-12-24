package jp.hishidama.eclipse_plugin.toad.model;

import java.beans.PropertyChangeListener;

import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

public abstract class AbstractModelEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener {

	@Override
	public void activate() {
		super.activate();
		AbstractModel model = getModel();
		model.addPropertyChangeListener(this);
	}

	@Override
	public void deactivate() {
		super.deactivate();
		AbstractModel model = getModel();
		model.removePropertyChangeListener(this);
	}

	@Override
	public AbstractModel getModel() {
		return (AbstractModel) super.getModel();
	}

	@Override
	public GraphicalEditPart getParent() {
		return (GraphicalEditPart) super.getParent();
	}
}
