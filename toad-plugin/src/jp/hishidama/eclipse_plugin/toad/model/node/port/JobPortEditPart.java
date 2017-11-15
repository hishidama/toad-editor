package jp.hishidama.eclipse_plugin.toad.model.node.port;

public class JobPortEditPart extends BasePortEditPart {

	@Override
	protected BasePortFigure createPortFigure() {
		return new JobPortFigure();
	}

	@Override
	public JobPortFigure getFigure() {
		return (JobPortFigure) super.getFigure();
	}

	@Override
	public JobPort getModel() {
		return (JobPort) super.getModel();
	}
}
