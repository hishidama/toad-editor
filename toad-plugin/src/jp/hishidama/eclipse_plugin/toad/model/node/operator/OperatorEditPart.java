package jp.hishidama.eclipse_plugin.toad.model.node.operator;

import java.beans.PropertyChangeEvent;

import jp.hishidama.eclipse_plugin.toad.model.dialog.OperatorPropertyDialog;
import jp.hishidama.eclipse_plugin.toad.model.node.BasicNodeEditPart;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;

import org.eclipse.draw2d.IFigure;

public class OperatorEditPart extends BasicNodeEditPart {

	@Override
	protected IFigure createFigure() {
		OperatorFigure figure = new OperatorFigure();

		refreshType(figure);
		refreshDescription(figure);

		return figure;
	}

	@Override
	public OperatorFigure getFigure() {
		return (OperatorFigure) super.getFigure();
	}

	@Override
	public OperatorNode getModel() {
		return (OperatorNode) super.getModel();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		String name = event.getPropertyName();
		if (NodeElement.PROP_CHILDREN.equals(name)) {
			getParent().refresh();
		}
		super.propertyChange(event);
	}

	@Override
	protected void performOpen() {
		OperatorPropertyDialog dialog = new OperatorPropertyDialog(getEditor(), getModel());
		dialog.open();
	}
}
