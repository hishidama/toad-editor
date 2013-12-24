package jp.hishidama.eclipse_plugin.toad.model.dialog.section;

import jp.hishidama.eclipse_plugin.toad.view.SiblingDataModelTreeElement;
import jp.hishidama.eclipse_plugin.toad.view.SiblingDataModelView;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class SiblingDataModelNodeDialog extends Dialog {

	private SiblingDataModelTreeElement treeRoot;
	private Tree tree;

	public SiblingDataModelNodeDialog(Shell parentShell, SiblingDataModelTreeElement root) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.treeRoot = root;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText("連動データモデル");

		Composite composite = (Composite) super.createDialogArea(parent);
		CheckboxTreeViewer viewer = new CheckboxTreeViewer(composite, SWT.BORDER);
		SiblingDataModelView.initializeTree(viewer);
		viewer.setInput(treeRoot);
		viewer.expandAll();
		tree = viewer.getTree();
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				Point point = new Point(e.x, e.y);
				TreeItem item = tree.getItem(point);
				if (item == null) {
					return;
				}
				if (item.getBounds().contains(point)) {
					boolean checked = !item.getChecked();
					item.setChecked(checked);
				}
			}
		});

		Composite field = new Composite(composite, SWT.NONE);
		field.setLayout(new FillLayout());
		{
			Button button = new Button(field, SWT.PUSH);
			button.setText("select all");
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					setCheckedAll(tree.getItems(), true);
				}
			});
		}
		{
			Button button = new Button(field, SWT.PUSH);
			button.setText("deselect all");
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					setCheckedAll(tree.getItems(), false);
				}
			});
		}

		syncChecked(tree.getItems());

		return composite;
	}

	private void syncChecked(TreeItem[] items) {
		for (TreeItem item : items) {
			SiblingDataModelTreeElement te = (SiblingDataModelTreeElement) item.getData();
			item.setChecked(te.getChecked());
			syncChecked(item.getItems());
		}
	}

	void setCheckedAll(TreeItem[] items, boolean checked) {
		for (TreeItem item : items) {
			item.setChecked(checked);
			setCheckedAll(item.getItems(), checked);
		}
	}

	@Override
	protected void okPressed() {
		syncRoot(tree.getItems());

		super.okPressed();
	}

	private void syncRoot(TreeItem[] items) {
		for (TreeItem item : items) {
			SiblingDataModelTreeElement te = (SiblingDataModelTreeElement) item.getData();
			te.setChecked(item.getChecked());

			syncRoot(item.getItems());
		}
	}
}
