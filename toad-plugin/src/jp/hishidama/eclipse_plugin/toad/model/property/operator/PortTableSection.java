package jp.hishidama.eclipse_plugin.toad.model.property.operator;

import static jp.hishidama.eclipse_plugin.util.StringUtil.nonNull;

import java.util.List;

import jp.hishidama.eclipse_plugin.toad.model.node.port.BasePort;
import jp.hishidama.eclipse_plugin.toad.model.property.TableSection;
import jp.hishidama.eclipse_plugin.toad.model.property.port.HasPortNode;

import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPart;

public class PortTableSection<C extends BasePort> extends TableSection {

	private HasPortNode<C> model;

	public PortTableSection() {
		super("port");
	}

	@Override
	protected void createTableColumns(Table table) {
		{
			TableColumn col = new TableColumn(table, SWT.NONE);
			col.setText("in/out");
			col.setWidth(48);
		}
		{
			TableColumn col = new TableColumn(table, SWT.NONE);
			col.setText("name");
			col.setWidth(128);
		}
		{
			TableColumn col = new TableColumn(table, SWT.NONE);
			col.setText("description");
			col.setWidth(128);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);

		IStructuredSelection ss = (IStructuredSelection) selection;
		AbstractEditPart editPart = (AbstractEditPart) ss.getFirstElement();
		model = (HasPortNode<C>) editPart.getModel();
	}

	@Override
	public void refresh() {
		if (table.isDisposed()) {
			return;
		}
		// table.removeModifyListener(listener);
		table.removeAll();
		List<C> list = model.getPorts();
		for (C c : list) {
			if (c.isIn()) {
				createItem(c);
			}
		}
		for (C c : list) {
			if (c.isOut()) {
				createItem(c);
			}
		}
		// String value = getValue(model);
		// table.setText((value != null) ? value : "");
		// table.addModifyListener(listener);
	}

	private void createItem(C c) {
		TableItem item = new TableItem(table, SWT.NONE);
		item.setData(c);

		item.setText(0, c.isIn() ? "in" : "out");
		item.setText(1, nonNull(c.getName()));
		item.setText(2, nonNull(c.getDescription()));
	}
}
