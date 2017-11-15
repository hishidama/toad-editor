package jp.hishidama.eclipse_plugin.toad.model.frame;

import java.beans.PropertyChangeEvent;

import jp.hishidama.eclipse_plugin.toad.model.dialog.FramePropertyDialog;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.RectangleNodeEditPart;

import org.eclipse.draw2d.IFigure;

public class FlowpartFrameEditPart extends RectangleNodeEditPart {

	@Override
	protected IFigure createFigure() {
		FlowpartFrameFigure figure = new FlowpartFrameFigure();

		refreshName(figure);

		return figure;
	}

	@Override
	public FlowpartFrameFigure getFigure() {
		return (FlowpartFrameFigure) super.getFigure();
	}

	@Override
	public FlowpartFrameNode getModel() {
		return (FlowpartFrameNode) super.getModel();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		String name = event.getPropertyName();
		if (FlowpartFrameNode.PROP_DESCRIPTION.equals(name)) {
			refreshName(getFigure());
		} else if (NodeElement.PROP_CHILDREN.equals(name)) {
			getParent().refresh();
		}
		super.propertyChange(event);
	}

	private void refreshName(FlowpartFrameFigure figure) {
		FlowpartFrameNode model = getModel();
		String desc = model.getDescription();
		figure.setName(desc);
	}

	@Override
	protected void performOpen() {
		FramePropertyDialog dialog = new FramePropertyDialog("フローパート", getEditor(), getModel());
		dialog.open();
	}
}
