package jp.hishidama.eclipse_plugin.toad.editor.action;

import java.util.List;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElementEditPart;
import jp.hishidama.eclipse_plugin.toad.model.property.datamodel.HasDataModelNode;
import jp.hishidama.eclipse_plugin.toad.view.SiblingDataModelView;

import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;

public class SiblingDataModelAction extends SelectionAction {

	public static final String ID = "TOAD_SIBILING_DATAMODEL";

	public SiblingDataModelAction(IWorkbenchPart part) {
		super(part);
	}

	@Override
	protected void init() {
		super.init();
		setText("View Sibling DataModel");
		setToolTipText("open sibling DataModel view");
		setId(ID);
	}

	@Override
	protected boolean calculateEnabled() {
		int count = 0;
		List<?> list = getSelectedObjects();
		for (Object obj : list) {
			if (obj instanceof NodeElementEditPart) {
				NodeElementEditPart part = (NodeElementEditPart) obj;
				if (part.getModel() instanceof HasDataModelNode) {
					if (++count >= 2) {
						return false;
					}
				}
			}
		}
		return count == 1;
	}

	@Override
	public void run() {
		NodeElementEditPart part = null;
		List<?> list = getSelectedObjects();
		for (Object obj : list) {
			if (obj instanceof NodeElementEditPart) {
				NodeElementEditPart p = (NodeElementEditPart) obj;
				if (p.getModel() instanceof HasDataModelNode) {
					part = p;
					break;
				}
			}
		}
		if (part == null) {
			return;
		}

		ToadEditor editor = part.getEditor();
		IWorkbenchPage page = editor.getSite().getWorkbenchWindow().getActivePage();
		SiblingDataModelView view;
		try {
			view = (SiblingDataModelView) page.showView(SiblingDataModelView.ID, null, IWorkbenchPage.VIEW_VISIBLE);
		} catch (PartInitException e) {
			view = (SiblingDataModelView) page.findView(SiblingDataModelView.ID);
		}
		if (view != null) {
			view.setInput(part, (HasDataModelNode) part.getModel());
		}
	}
}
