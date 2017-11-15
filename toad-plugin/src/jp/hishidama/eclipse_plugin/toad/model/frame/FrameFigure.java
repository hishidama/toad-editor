package jp.hishidama.eclipse_plugin.toad.model.frame;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FrameBorder;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.TitleBarBorder;
import org.eclipse.draw2d.TreeSearch;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

public abstract class FrameFigure extends Figure {

	private FrameBorder border;

	public FrameFigure() {
		border = new FrameBorder();
		{
			TitleBarBorder bar = (TitleBarBorder) border.getInnerBorder();
			Color bg = getColor();
			bar.setBackgroundColor(bg);
			bar.setTextColor(ColorConstants.black);
		}
		this.setBorder(border);

		setLayoutManager(new FreeformLayout());
	}

	protected abstract Color getColor();

	public void setName(String name) {
		border.setLabel(name);
		repaint();
	}

	@Override
	public IFigure findFigureAt(int x, int y, TreeSearch search) {
		// 四方の辺の部分だけ選択可能とする

		Rectangle rect = getBounds();
		Dimension barSize = border.getInnerBorder().getPreferredSize(this);
		if ((rect.x <= x && x < rect.x + 4) || (rect.y <= y && y < rect.y + barSize.height)
				|| (rect.right() - 4 <= x && x < rect.right()) || (rect.bottom() - 4 <= y && y < rect.bottom())) {
			return super.findFigureAt(x, y, search);
		}

		return null;
	}
}
