package jp.hishidama.eclipse_plugin.toad.model.property;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public abstract class TableSection extends AbstractPropertySection {

	private String label;
	protected Table table;

	protected ToadEditor editor;

	public TableSection(String label) {
		this.label = label;
	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		Composite pane = factory.createComposite(parent, SWT.NONE);
		pane.setLayout(new GridLayout(1, false));
		createTableField(pane, factory);
	}

	private void createTableField(Composite parent, TabbedPropertySheetWidgetFactory factory) {
		Composite composite = factory.createFlatFormComposite(parent);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		{
			table = factory.createTable(composite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.SINGLE);
			FormData data = new FormData();
			data.left = new FormAttachment(0, 96);
			data.right = new FormAttachment(100, 0);
			data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
			data.height = table.getItemHeight() * 5;
			table.setLayoutData(data);
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			createTableColumns(table);
		}
		{
			CLabel label = factory.createCLabel(composite, this.label + " :");
			FormData data = new FormData();
			data.left = new FormAttachment(0, 0);
			data.right = new FormAttachment(table, -ITabbedPropertyConstants.HSPACE);
			data.top = new FormAttachment(table, 0, SWT.TOP);
			label.setLayoutData(data);
		}
	}

	protected abstract void createTableColumns(Table table);

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);

		editor = (ToadEditor) part.getSite().getPage().getActiveEditor();
	}

	protected IProject getProject() {
		return editor.getProject();
	}
}
