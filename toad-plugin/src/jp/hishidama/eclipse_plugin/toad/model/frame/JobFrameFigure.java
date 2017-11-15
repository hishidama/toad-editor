package jp.hishidama.eclipse_plugin.toad.model.frame;

import jp.hishidama.eclipse_plugin.toad.editor.ToadColorManager;

import org.eclipse.swt.graphics.Color;

public class JobFrameFigure extends FrameFigure {

	@Override
	protected Color getColor() {
		return ToadColorManager.getJobFlowColor();
	}
}
