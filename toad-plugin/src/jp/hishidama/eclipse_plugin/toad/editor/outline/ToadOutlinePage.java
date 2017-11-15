package jp.hishidama.eclipse_plugin.toad.editor.outline;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class ToadOutlinePage extends ContentOutlinePage {
	private final ToadEditor toadEditor;

	private SashForm sash;
	private ScrollableThumbnail thumbnail;

	public ToadOutlinePage(ToadEditor toadEditor) {
		super(new TreeViewer());
		this.toadEditor = toadEditor;
	}

	@Override
	public void createControl(Composite parent) {
		sash = new SashForm(parent, SWT.VERTICAL);

		// thumbnail
		Canvas canvas = new Canvas(sash, SWT.BORDER);
		LightweightSystem lws = new LightweightSystem(canvas);

		ScalableRootEditPart root = toadEditor.getScalableRootEditPart();
		thumbnail = new ScrollableThumbnail((Viewport) root.getFigure());
		thumbnail.setSource(root.getLayer(LayerConstants.PRINTABLE_LAYERS));
		lws.setContents(thumbnail);

		// tree
		EditPartViewer viewer = getViewer();
		viewer.createControl(sash);
		viewer.setEditDomain(toadEditor.getEditDomain());
		viewer.setEditPartFactory(new ToadOutlineTreePartFactory());
		viewer.setContents(toadEditor.getDiagram());
		{
			Tree tree = (Tree) viewer.getControl();
			int n = tree.getItemCount();
			if (n >= 1) {
				TreeItem item = tree.getItem(n - 1);
				item.setExpanded(true);
			}
		}
		toadEditor.addSelectionSynchronizerViewer(viewer);

		sash.setWeights(new int[] { 3, 7 });
	}

	@Override
	public Control getControl() {
		return sash;
	}

	@Override
	public void dispose() {
		thumbnail.deactivate();
		super.dispose();
	}
}