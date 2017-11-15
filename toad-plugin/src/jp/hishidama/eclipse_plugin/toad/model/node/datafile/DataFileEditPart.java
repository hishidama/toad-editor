package jp.hishidama.eclipse_plugin.toad.model.node.datafile;

import java.beans.PropertyChangeEvent;

import jp.hishidama.eclipse_plugin.toad.model.dialog.DataFilePropertyDialog;
import jp.hishidama.eclipse_plugin.toad.model.node.BasicNodeEditPart;

import org.eclipse.draw2d.IFigure;

public class DataFileEditPart extends BasicNodeEditPart {

	@Override
	protected IFigure createFigure() {
		DataFileFigure figure = new DataFileFigure();

		refreshType(figure);
		refreshDescription(figure);

		return figure;
	}

	@Override
	public DataFileFigure getFigure() {
		return (DataFileFigure) super.getFigure();
	}

	@Override
	public DataFileNode getModel() {
		return (DataFileNode) super.getModel();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		String name = event.getPropertyName();
		if (DataFileNode.PROP_FILE_TYPE.equals(name)) {
			refreshType(getFigure());
		}
		super.propertyChange(event);
	}

	@Override
	protected void performOpen() {
		DataFilePropertyDialog dialog = new DataFilePropertyDialog(getEditor(), getModel());
		dialog.open();
	}
}
