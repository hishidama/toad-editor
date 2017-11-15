package jp.hishidama.eclipse_plugin.toad.editor.drop;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.node.RectangleNode;
import jp.hishidama.eclipse_plugin.toad.model.node.command.CreateNodeCommand;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.swt.dnd.Transfer;

public abstract class ToadDropTargetListener extends AbstractTransferDropTargetListener {
	protected final ToadEditor editor;

	public ToadDropTargetListener(ToadEditor editor, EditPartViewer viewer, Transfer xfer) {
		super(viewer, xfer);
		this.editor = editor;
	}

	@Override
	protected void updateTargetRequest() {
	}

	@Override
	protected Request createTargetRequest() {
		return new CreateRequest();
	}

	protected Command newCreateNodeCommand(Diagram diagram, RectangleNode node) {
		EditPartViewer v = getViewer();
		FigureCanvas c = (FigureCanvas) v.getControl();
		// Point base = c.getViewport().getViewLocation();
		Point location = getDropLocation();
		// int x = base.x + location.x - node.getWidth() / 2;
		// int y = base.y + location.y - node.getHeight() / 2;
		c.getViewport().translateFromParent(location);
		int x = location.x - node.getWidth() / 2;
		int y = location.y - node.getHeight() / 2;
		return new CreateNodeCommand(diagram, node, x, y);
	}
}
