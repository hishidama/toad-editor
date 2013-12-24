package jp.hishidama.eclipse_plugin.toad.model.node.port;

public class OpePortEditPart extends BasePortEditPart {

	@Override
	protected BasePortFigure createPortFigure() {
		return new OpePortFigure();
	}

	@Override
	public OpePortFigure getFigure() {
		return (OpePortFigure) super.getFigure();
	}

	@Override
	public OpePort getModel() {
		return (OpePort) super.getModel();
	}
}
