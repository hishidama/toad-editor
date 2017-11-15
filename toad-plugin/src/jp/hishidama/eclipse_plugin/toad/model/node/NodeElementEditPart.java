package jp.hishidama.eclipse_plugin.toad.model.node;

import java.beans.PropertyChangeEvent;
import java.util.List;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.model.AbstractModelEditPart;
import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;
import jp.hishidama.eclipse_plugin.toad.model.node.command.NodeComponentEditPolicy;
import jp.hishidama.eclipse_plugin.toad.model.node.command.NodeGraphicalNodeEditPolicy;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.ui.views.properties.tabbed.TabContents;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public abstract class NodeElementEditPart extends AbstractModelEditPart implements NodeEditPart {

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		String name = event.getPropertyName();
		if (NodeElement.PROP_INCOMINGS.equals(name)) {
			refreshTargetConnections();
		} else if (NodeElement.PROP_OUTGOINGS.equals(name)) {
			refreshSourceConnections();
		}

		refreshPropertySheet();
	}

	private void refreshPropertySheet() {
		PropertySheet view = (PropertySheet) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findView("org.eclipse.ui.views.PropertySheet");
		if (view == null) {
			return;
		}
		TabbedPropertySheetPage page = (TabbedPropertySheetPage) view.getCurrentPage();
		TabContents currentTab = page.getCurrentTab();
		if (currentTab != null) {
			currentTab.refresh();
		}
	}

	protected final void refreshToolTipInformation(BasicNodeFigure figure) {
		NodeElement model = getModel();
		figure.setToolTipInformation(model.getToolTipInformation());
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new NodeComponentEditPolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new NodeGraphicalNodeEditPolicy());
	}

	@Override
	public void performRequest(Request request) {
		Object t = request.getType();
		if (t == RequestConstants.REQ_OPEN) {
			performOpen();
		}
	}

	protected void performOpen() {
		// do override
	}

	public final ToadEditor getEditor() {
		return ToadEditor.getToadEditor(getViewer());
	}

	@Override
	protected final void refreshVisuals() {
		IFigure figure = getFigure();

		Rectangle rect = calculateFigureBounds();

		GraphicalEditPart parent = getParent();
		parent.setLayoutConstraint(this, figure, rect);
	}

	protected abstract Rectangle calculateFigureBounds();

	@Override
	public NodeElement getModel() {
		return (NodeElement) super.getModel();
	}

	@Override
	protected List<Connection> getModelSourceConnections() {
		NodeElement node = getModel();
		return node.getOutgoings();
	}

	@Override
	protected List<Connection> getModelTargetConnections() {
		NodeElement node = getModel();
		return node.getIncomings();
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connectioneditpart) {
		return new ChopboxAnchor(getFigure());
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return new ChopboxAnchor(getFigure());
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connectioneditpart) {
		return new ChopboxAnchor(getFigure());
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return new ChopboxAnchor(getFigure());
	}
}
