package jp.hishidama.eclipse_plugin.toad.model.node.port;

public class OpePortFigure extends BasePortFigure {

	@Override
	protected void getArrowPoint(ArrowPoint point) {
		int w = ARROW_NX / 2;
		int h = ARROW_NY / 4;
		point.sx = -w + 2;
		point.mx = w / 2;
		point.ex = w - 2;
		point.sy = -h;
		point.my = 0;
		point.ey = h;
	}
}
