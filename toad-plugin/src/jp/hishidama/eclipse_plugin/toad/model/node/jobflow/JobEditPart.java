package jp.hishidama.eclipse_plugin.toad.model.node.jobflow;

import java.beans.PropertyChangeEvent;

import jp.hishidama.eclipse_plugin.toad.model.dialog.JobflowPropertyDialog;
import jp.hishidama.eclipse_plugin.toad.model.node.BasicNodeEditPart;
import jp.hishidama.eclipse_plugin.toad.model.property.generic.NameNode;

import org.eclipse.draw2d.IFigure;

public class JobEditPart extends BasicNodeEditPart {

	@Override
	protected IFigure createFigure() {
		JobFigure figure = new JobFigure();

		refreshType(figure);
		refreshDescription(figure);

		return figure;
	}

	@Override
	public JobFigure getFigure() {
		return (JobFigure) super.getFigure();
	}

	@Override
	public JobNode getModel() {
		return (JobNode) super.getModel();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		String name = event.getPropertyName();
		if (NameNode.PROP_NAME.equals(name)) {
			refreshType(getFigure());
		}
		super.propertyChange(event);
	}

	@Override
	protected void performOpen() {
		JobflowPropertyDialog dialog = new JobflowPropertyDialog(getEditor(), getModel());
		dialog.open();
	}
}
