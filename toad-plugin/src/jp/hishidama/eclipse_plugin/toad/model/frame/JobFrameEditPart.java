package jp.hishidama.eclipse_plugin.toad.model.frame;

import java.beans.PropertyChangeEvent;

import jp.hishidama.eclipse_plugin.toad.model.dialog.FramePropertyDialog;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.RectangleNodeEditPart;

import org.eclipse.draw2d.IFigure;

public class JobFrameEditPart extends RectangleNodeEditPart {

	@Override
	protected IFigure createFigure() {
		JobFrameFigure figure = new JobFrameFigure();

		refreshName(figure);

		return figure;
	}

	@Override
	public JobFrameFigure getFigure() {
		return (JobFrameFigure) super.getFigure();
	}

	@Override
	public JobFrameNode getModel() {
		return (JobFrameNode) super.getModel();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		String name = event.getPropertyName();
		if (JobFrameNode.PROP_NAME.equals(name) || JobFrameNode.PROP_DESCRIPTION.equals(name)) {
			refreshName(getFigure());
		} else if (NodeElement.PROP_CHILDREN.equals(name)) {
			getParent().refresh();
		}
		super.propertyChange(event);
	}

	private void refreshName(JobFrameFigure figure) {
		JobFrameNode model = getModel();
		String name = model.getName();
		String desc = model.getDescription();
		figure.setName(String.format("%s %s", name, desc));
	}

	@Override
	protected void performOpen() {
		FramePropertyDialog dialog = new FramePropertyDialog("ジョブフロー", getEditor(), getModel());
		dialog.open();
	}
}
