package jp.hishidama.eclipse_plugin.toad.model.property.frame;

import static jp.hishidama.eclipse_plugin.util.StringUtil.nonNull;

import java.util.List;

import jp.hishidama.eclipse_plugin.toad.model.frame.FlowpartFrameNode;
import jp.hishidama.eclipse_plugin.toad.model.frame.FlowpartParameterDef;
import jp.hishidama.eclipse_plugin.toad.model.property.TableSection;

import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPart;

public class FrameParameterTableSection extends TableSection {

	private FlowpartFrameNode model;

	public FrameParameterTableSection() {
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
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);

		IStructuredSelection ss = (IStructuredSelection) selection;
		AbstractEditPart editPart = (AbstractEditPart) ss.getFirstElement();
		model = (FlowpartFrameNode) editPart.getModel();
	}

	@Override
	public void refresh() {
		if (table.isDisposed()) {
			return;
		}
		table.removeAll();
		List<FlowpartParameterDef> list = model.getParameterList();
		for (FlowpartParameterDef param : list) {
			createItem(param);
		}
	}

	private void createItem(FlowpartParameterDef param) {
		TableItem item = new TableItem(table, SWT.NONE);
		item.setData(param);

		item.setText(0, nonNull(param.getName()));
		item.setText(1, nonNull(param.getClassName()));
	}
}
