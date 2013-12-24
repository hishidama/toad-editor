package jp.hishidama.eclipse_plugin.toad.model.node.operator;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.Label;

public class EllipseLabelFigure extends Ellipse {

	public EllipseLabelFigure(String text) {
		// this.setBorder(new LineBorder()); //枠線は引かない

		setLayoutManager(new BorderLayout());

		Label label = new Label(text);
		this.add(label, BorderLayout.CENTER);
	}
}
