package jp.hishidama.eclipse_plugin.toad.model.node.operator;

import jp.hishidama.eclipse_plugin.toad.model.dialog.OperatorPropertyDialog;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.RectangleNodeEditPart;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.EllipseAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.Request;

public class EllipseOperatorEditPart extends RectangleNodeEditPart {

	private String text;

	public EllipseOperatorEditPart(String text) {
		this.text = text;
	}

	@Override
	protected IFigure createFigure() {
		EllipseLabelFigure figure = new EllipseLabelFigure(text);

		refreshName(figure);

		return figure;
	}

	private void refreshName(EllipseLabelFigure figure) {
		NodeElement model = getModel();
		Label tip = new Label(model.getDescription());
		figure.setToolTip(tip);
	}

	@Override
	public EllipseLabelFigure getFigure() {
		return (EllipseLabelFigure) super.getFigure();
	}

	@Override
	public OperatorNode getModel() {
		return (OperatorNode) super.getModel();
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connectioneditpart) {
		return new EllipseAnchor(getFigure());
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return new EllipseAnchor(getFigure());
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connectioneditpart) {
		return new EllipseAnchor(getFigure());
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return new EllipseAnchor(getFigure());
	}

	@Override
	protected void performOpen() {
		OperatorPropertyDialog dialog = new OperatorPropertyDialog(getEditor(), getModel());
		dialog.open();
	}
}
