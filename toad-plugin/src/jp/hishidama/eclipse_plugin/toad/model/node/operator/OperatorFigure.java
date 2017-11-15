package jp.hishidama.eclipse_plugin.toad.model.node.operator;

import jp.hishidama.eclipse_plugin.toad.editor.ToadColorManager;
import jp.hishidama.eclipse_plugin.toad.model.node.BasicNodeFigure;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.SimpleRaisedBorder;
import org.eclipse.swt.graphics.Color;

public class OperatorFigure extends BasicNodeFigure {

	public OperatorFigure() {
		Color bg = ToadColorManager.getOperatorColor();
		setBackgroundColor(bg);
	}

	@Override
	protected Border constructBorder() {
		SimpleRaisedBorder frameBorder = new SimpleRaisedBorder();
		MarginBorder marginBoder = new MarginBorder(4);
		return new CompoundBorder(frameBorder, marginBoder);
	}
}
