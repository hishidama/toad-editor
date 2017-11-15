package jp.hishidama.eclipse_plugin.toad.editor.outline;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;

import jp.hishidama.eclipse_plugin.toad.internal.ToadImages;
import jp.hishidama.eclipse_plugin.toad.model.AbstractNameModel;
import jp.hishidama.eclipse_plugin.toad.model.property.generic.NameNode;

import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.swt.graphics.Image;

public abstract class ToadTreeEditPart extends AbstractTreeEditPart implements PropertyChangeListener {

	@Override
	public void activate() {
		super.activate();
		getModel().addPropertyChangeListener(this);
	}

	@Override
	public void deactivate() {
		getModel().removePropertyChangeListener(this);
		super.deactivate();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		String name = event.getPropertyName();
		if (AbstractNameModel.PROP_TYPE.equals(name) || NameNode.PROP_NAME.equals(name)
				|| AbstractNameModel.PROP_DESCRIPTION.equals(name)) {
			refreshVisuals();
		}
	}

	@Override
	public AbstractNameModel getModel() {
		return (AbstractNameModel) super.getModel();
	}

	@Override
	protected String getText() {
		AbstractNameModel model = getModel();
		String type = model.getFigureLabel();
		String desc = model.getDescription();
		if (model instanceof NameNode) {
			String name = ((NameNode) model).getName();
			return MessageFormat.format("{1} {2} <{0}>", type, name, desc);
		}
		return MessageFormat.format("{1} <{0}>", type, desc);
	}

	@Override
	protected Image getImage() {
		return ToadImages.getImage(getModel());
	}
}
