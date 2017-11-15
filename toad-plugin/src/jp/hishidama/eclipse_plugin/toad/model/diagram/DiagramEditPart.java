package jp.hishidama.eclipse_plugin.toad.model.diagram;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.model.AbstractModelEditPart;
import jp.hishidama.eclipse_plugin.toad.model.dialog.DiagramPropertyDialog;
import jp.hishidama.eclipse_plugin.toad.model.dialog.FramePropertyDialog;
import jp.hishidama.eclipse_plugin.toad.model.frame.FrameNode;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ShortestPathConnectionRouter;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;

public class DiagramEditPart extends AbstractModelEditPart {

	@Override
	protected IFigure createFigure() {
		FreeformLayer layer = new FreeformLayer();
		layer.setLayoutManager(new FreeformLayout());

		{ // コネクションルーター
			ConnectionLayer connectionLayer = (ConnectionLayer) getLayer(LayerConstants.CONNECTION_LAYER);
			connectionLayer.setConnectionRouter(new ShortestPathConnectionRouter(new DiagramForRouterFigure(layer)));
		}

		return layer;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new DiagramLayoutEditPolicy());
	}

	@Override
	public Diagram getModel() {
		return (Diagram) super.getModel();
	}

	@Override
	protected List<NodeElement> getModelChildren() {
		Diagram diagram = getModel();
		List<NodeElement> contents = diagram.getContents();
		List<NodeElement> list = new ArrayList<NodeElement>(contents.size() * 4);
		list.addAll(contents);
		for (NodeElement child : contents) {
			addChildren(list, child);
		}
		return list;
	}

	private void addChildren(List<NodeElement> list, NodeElement node) {
		List<NodeElement> contents = node.getChildren();
		list.addAll(contents);
		for (NodeElement child : contents) {
			addChildren(list, child);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		String name = event.getPropertyName();
		if (Diagram.PROP_CONTENTS.equals(name)) {
			refreshChildren();
		}
	}

	public void performOpen() {
		Diagram diagram = getModel();
		DiagramType type = diagram.getDiagramType();
		switch (type) {
		case BATCH: {
			DiagramPropertyDialog dialog = new DiagramPropertyDialog(getEditor(), diagram);
			dialog.open();
			break;
		}
		case JOBFLOW:
			FrameNode frame = diagram.getFrameNode();
			if (frame != null) {
				FramePropertyDialog dialog = new FramePropertyDialog("ジョブフロー", getEditor(), frame);
				dialog.open();
			}
			break;
		case FLOWPART:
			FrameNode flowPart = diagram.getFrameNode();
			if (flowPart != null) {
				FramePropertyDialog dialog = new FramePropertyDialog("フローパート", getEditor(), flowPart);
				dialog.open();
			}
			break;
		default:
			break;
		}
	}

	public final ToadEditor getEditor() {
		return ToadEditor.getToadEditor(getViewer());
	}
}
