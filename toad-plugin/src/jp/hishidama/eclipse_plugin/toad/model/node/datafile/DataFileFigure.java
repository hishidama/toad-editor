package jp.hishidama.eclipse_plugin.toad.model.node.datafile;

import jp.hishidama.eclipse_plugin.toad.model.node.BasicNodeFigure;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.MarginBorder;

public class DataFileFigure extends BasicNodeFigure {

	public DataFileFigure() {
	}

	@Override
	protected Border constructBorder() {
		return new MarginBorder(4);
	}
}
