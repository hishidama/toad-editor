package jp.hishidama.eclipse_plugin.toad.model.property.operator;

import static jp.hishidama.eclipse_plugin.util.StringUtil.nonNull;

import java.util.List;

import jp.hishidama.eclipse_plugin.toad.model.node.operator.OpeParameter;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;
import jp.hishidama.eclipse_plugin.toad.model.property.TableSection;

import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPart;

public class OpeParameterTableSection extends TableSection {

	private OperatorNode model;

	public OpeParameterTableSection() {
		super("parameter");
	}

	@Override
	protected void createTableColumns(Table table) {
		{
			TableColumn col = new TableColumn(table, SWT.NONE);
			col.setText("name");
			col.setWidth(128);
		}
		{
			TableColumn col = new TableColumn(table, SWT.NONE);
			col.setText("class name");
			col.setWidth(256);
		}
		{
			TableColumn col = new TableColumn(table, SWT.NONE);
			col.setText("value");
			col.setWidth(256);
		}
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);

		IStructuredSelection ss = (IStructuredSelection) selection;
		AbstractEditPart editPart = (AbstractEditPart) ss.getFirstElement();
		model = (OperatorNode) editPart.getModel();
	}

	@Override
	public void refresh() {
		if (table.isDisposed()) {
			return;
		}
		table.removeAll();
		List<OpeParameter> list = model.getParameterList();
		for (OpeParameter param : list) {
			createItem(param);
		}
	}

	private void createItem(OpeParameter param) {
		TableItem item = new TableItem(table, SWT.NONE);
		item.setData(param);

		item.setText(0, nonNull(param.getName()));
		item.setText(1, nonNull(param.getClassName()));
		item.setText(2, nonNull(param.getValue()));
	}
}
