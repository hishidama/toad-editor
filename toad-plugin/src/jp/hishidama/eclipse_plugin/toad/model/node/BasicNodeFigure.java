package jp.hishidama.eclipse_plugin.toad.model.node;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.text.BlockFlow;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.ParagraphTextLayout;
import org.eclipse.draw2d.text.TextFlow;

public abstract class BasicNodeFigure extends RectangleFigure {

	protected Label typeLabel;
	protected TextFlow descLabel;
	private Label tip;

	public BasicNodeFigure() {
		this.setBorder(constructBorder());

		setLayoutManager(new BorderLayout());

		typeLabel = new Label();
		this.add(typeLabel, BorderLayout.TOP);

		FlowPage page = new FlowPage();
		{
			descLabel = new TextFlow();
			LayoutManager manager = new ParagraphTextLayout(descLabel, ParagraphTextLayout.WORD_WRAP_SOFT);
			descLabel.setLayoutManager(manager);

			BlockFlow block = new BlockFlow();
			block.setHorizontalAligment(PositionConstants.CENTER);
			block.add(descLabel);
			page.add(block);
		}
		this.add(page, BorderLayout.CENTER);
	}

	protected abstract Border constructBorder();

	public void setType(String type) {
		typeLabel.setText(type);
		resetToolTip();
	}

	public void setDescription(String description) {
		descLabel.setText(description);
		resetToolTip();
	}

	public IFigure getDirectEditTarget() {
		return descLabel;
	}

	private void resetToolTip() {
		StringBuilder sb = new StringBuilder(64);
		String type = typeLabel.getText();
		if (!type.isEmpty()) {
			sb.append(type);
		}
		String desc = descLabel.getText();
		if (!desc.isEmpty()) {
			if (sb.length() > 0) {
				sb.append('\n');
			}
			sb.append(desc);
		}
		if (sb.length() > 0) {
			if (tip == null) {
				tip = new Label(sb.toString());
			} else {
				tip.setText(sb.toString());
			}
			setToolTip(tip);
		}
	}
}
