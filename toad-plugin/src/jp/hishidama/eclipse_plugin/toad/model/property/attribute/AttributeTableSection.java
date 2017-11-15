package jp.hishidama.eclipse_plugin.toad.model.property.attribute;

import static jp.hishidama.eclipse_plugin.util.StringUtil.mkString;
import static jp.hishidama.eclipse_plugin.util.StringUtil.nonNull;

import java.util.List;

import jp.hishidama.eclipse_plugin.toad.model.node.Attribute;
import jp.hishidama.eclipse_plugin.toad.model.property.TableSection;

import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPart;

public class AttributeTableSection extends TableSection {

	private HasAttributeNode node;

	public AttributeTableSection() {
		super("attribute");
	}

	@Override
	protected void createTableColumns(Table table) {
		{
			TableColumn col = new TableColumn(table, SWT.NONE);
			col.setText("annotation");
			col.setWidth(256);
		}
		{
			TableColumn col = new TableColumn(table, SWT.NONE);
			col.setText("name");
			col.setWidth(128);
		}
		{
			TableColumn col = new TableColumn(table, SWT.NONE);
			col.setText("type");
			col.setWidth(128);
		}
		{
			TableColumn col = new TableColumn(table, SWT.NONE);
			col.setText("values");
			col.setWidth(256);
		}
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);

		IStructuredSelection ss = (IStructuredSelection) selection;
		AbstractEditPart editPart = (AbstractEditPart) ss.getFirstElement();
		node = (HasAttributeNode) editPart.getModel();
	}

	@Override
	public void refresh() {
		if (table.isDisposed()) {
			return;
		}
		table.removeAll();
		List<Attribute> list = node.getAttributeList();
		for (Attribute attr : list) {
			createItem(attr);
		}
	}

	private void createItem(Attribute param) {
		TableItem item = new TableItem(table, SWT.NONE);
		item.setData(param);

		item.setText(0, nonNull(param.getAnnotationName()));
		item.setText(1, nonNull(param.getParameterName()));
		item.setText(2, nonNull(param.getValueType()));
		item.setText(3, nonNull(mkString(param.getValue())));
	}
}
