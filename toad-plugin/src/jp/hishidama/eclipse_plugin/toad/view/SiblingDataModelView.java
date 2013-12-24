package jp.hishidama.eclipse_plugin.toad.view;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.internal.ToadImages;
import jp.hishidama.eclipse_plugin.toad.model.diagram.DiagramEditPart;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElementEditPart;
import jp.hishidama.eclipse_plugin.toad.model.property.datamodel.DataModelNodeUtil;
import jp.hishidama.eclipse_plugin.toad.model.property.datamodel.HasDataModelNode;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

public class SiblingDataModelView extends ViewPart implements IPartListener {
	public static final String ID = "jp.hishidama.toadEditor.view.siblingDataModelView";

	private DiagramEditPart diagram;
	private ToadEditor editor;

	private TreeViewer viewer;

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
		initializeTree(viewer);

		setInput();

		viewer.getTree().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem item = (TreeItem) e.item;
				if (item == null) {
					e.doit = false;
					return;
				}
				SiblingDataModelTreeElement te = (SiblingDataModelTreeElement) item.getData();
				HasDataModelNode node = te.getDataModelNode();
				EditPart part = findEditPart(node);
				if (part != null) {
					EditPartViewer viewer = diagram.getViewer();
					viewer.select(part);
					viewer.reveal(part);
				}
			}
		});

		IWorkbenchPage page = getSite().getWorkbenchWindow().getActivePage();
		page.addPartListener(this);
	}

	@Override
	public void dispose() {
		IWorkbenchPage page = getSite().getWorkbenchWindow().getActivePage();
		page.removePartListener(this);

		super.dispose();
	}

	private void setInput() {
		ISelection selection = getSite().getWorkbenchWindow().getSelectionService().getSelection();
		if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			Object element = ss.getFirstElement();
			if (element instanceof NodeElementEditPart) {
				NodeElementEditPart part = (NodeElementEditPart) element;
				NodeElement model = part.getModel();
				if (model instanceof HasDataModelNode) {
					setInput(part, (HasDataModelNode) model);
				}
			}
		}
	}

	public void setInput(NodeElementEditPart part, HasDataModelNode node) {
		this.diagram = findDiagramEditPart(part);
		this.editor = diagram.getEditor();
		SiblingDataModelTreeElement root = DataModelNodeUtil.getSiblingDataModelNode(node);
		viewer.setInput(root);
		viewer.expandAll();
	}

	private DiagramEditPart findDiagramEditPart(EditPart part) {
		for (; part != null; part = part.getParent()) {
			if (part instanceof DiagramEditPart) {
				return (DiagramEditPart) part;
			}
		}
		return null;
	}

	private EditPart findEditPart(HasDataModelNode node) {
		for (Object c : diagram.getChildren()) {
			EditPart part = (EditPart) c;
			if (part.getModel() == node) {
				return part;
			}
		}
		return null;
	}

	@Override
	public void setFocus() {
	}

	public static void initializeTree(TreeViewer viewer) {
		Tree tree = viewer.getTree();
		tree.setLayoutData(new GridData(GridData.FILL_BOTH));
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		{
			TreeColumn tc = new TreeColumn(tree, SWT.NONE);
			tc.setText("name");
			tc.setWidth(256 + 64);
		}
		{
			TreeColumn tc = new TreeColumn(tree, SWT.NONE);
			tc.setText("type");
			tc.setWidth(96);
		}
		{
			TreeColumn tc = new TreeColumn(tree, SWT.NONE);
			tc.setText("description");
			tc.setWidth(128);
		}
		{
			TreeColumn tc = new TreeColumn(tree, SWT.NONE);
			tc.setText("data model");
			tc.setWidth(128);
		}
		{
			TreeColumn tc = new TreeColumn(tree, SWT.NONE);
			tc.setText("description");
			tc.setWidth(128);
		}
		viewer.setContentProvider(new ContentProvider());
		viewer.setLabelProvider(new LabelProvider());
	}

	public static class ContentProvider implements ITreeContentProvider {
		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			SiblingDataModelTreeElement te = (SiblingDataModelTreeElement) parentElement;
			return te.getChildren().toArray();
		}

		@Override
		public Object getParent(Object element) {
			SiblingDataModelTreeElement te = (SiblingDataModelTreeElement) element;
			return te.getParent();
		}

		@Override
		public boolean hasChildren(Object element) {
			SiblingDataModelTreeElement te = (SiblingDataModelTreeElement) element;
			return !te.getChildren().isEmpty();
		}
	}

	public static class LabelProvider extends CellLabelProvider {
		@Override
		public void update(ViewerCell cell) {
			SiblingDataModelTreeElement te = (SiblingDataModelTreeElement) cell.getElement();
			NodeElement ne = (NodeElement) te.getDataModelNode();
			switch (cell.getColumnIndex()) {
			case 0:
				cell.setImage(ToadImages.getImage(ne));
				cell.setText(ne.getDisplayName());
				break;
			case 1:
				cell.setImage(ToadImages.getImage(ne));
				cell.setText(ne.getType());
				break;
			case 2:
				cell.setText(ne.getQualifiedDescription());
				break;
			case 3:
				cell.setText(te.getDataModelNode().getModelName());
				break;
			case 4:
				cell.setText(te.getDataModelNode().getModelDescription());
				break;
			default:
				throw new UnsupportedOperationException("index=" + cell.getColumnIndex());
			}
		}
	}

	@Override
	public void partOpened(IWorkbenchPart part) {
	}

	@Override
	public void partClosed(IWorkbenchPart part) {
		if (part == editor) {
			diagram = null;
			editor = null;
			viewer.setInput(null);
		}
	}

	@Override
	public void partActivated(IWorkbenchPart part) {
	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
	}
}
