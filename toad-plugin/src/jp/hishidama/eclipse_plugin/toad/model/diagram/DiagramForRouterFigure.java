package jp.hishidama.eclipse_plugin.toad.model.diagram;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.toad.model.frame.FrameFigure;

import org.eclipse.draw2d.IFigure;

public class DiagramForRouterFigure extends DelegateFigure {

	public DiagramForRouterFigure(IFigure figure) {
		super(figure);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getChildren() {
		List list = super.getChildren();
		List<Object> ret = new ArrayList<Object>(list.size());
		for (Object obj : list) {
			if (obj instanceof FrameFigure) {
				continue;
			}
			ret.add(obj);
		}
		return ret;
	}

	@Override
	public void remove(IFigure figure) {
		if (figure instanceof FrameFigure) {
			return;
		}
		super.remove(figure);
	}
}
