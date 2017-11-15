package jp.hishidama.eclipse_plugin.toad.model.property.port;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.model.AbstractModel.ChangeIntCommand;
import jp.hishidama.eclipse_plugin.toad.model.node.port.BasePort;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class BasePortSection extends AbstractPropertySection {

	private String label;

	private BasePort model;
	protected CommandStack commandStack;

	private Button leftRadio;
	private Button rightRadio;
	private Button topRadio;
	private Button bottomRadio;
	private Button noneRadio;
	private Button[] radios;

	public BasePortSection() {
		this.label = "label position";
	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		Composite composite = factory.createFlatFormComposite(parent);

		Composite field = factory.createComposite(composite);
		{
			FormData data = new FormData();
			data.left = new FormAttachment(0, 120);
			data.right = new FormAttachment(100, 0);
			data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
			data.bottom = new FormAttachment(100, -ITabbedPropertyConstants.VSPACE);
			field.setLayoutData(data);

			field.setLayout(new RowLayout(SWT.HORIZONTAL));
			leftRadio = factory.createButton(field, "left", SWT.RADIO);
			leftRadio.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					setNamePosition(PositionConstants.LEFT);
				}
			});
			rightRadio = factory.createButton(field, "right", SWT.RADIO);
			rightRadio.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					setNamePosition(PositionConstants.RIGHT);
				}
			});
			topRadio = factory.createButton(field, "top", SWT.RADIO);
			topRadio.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					setNamePosition(PositionConstants.TOP);
				}
			});
			bottomRadio = factory.createButton(field, "bottom", SWT.RADIO);
			bottomRadio.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					setNamePosition(PositionConstants.BOTTOM);
				}
			});
			noneRadio = factory.createButton(field, "none", SWT.RADIO);
			noneRadio.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					setNamePosition(PositionConstants.NONE);
				}
			});
			radios = new Button[] { leftRadio, rightRadio, topRadio, bottomRadio, noneRadio };
		}
		{
			CLabel label = factory.createCLabel(composite, this.label + " :");
			FormData data = new FormData();
			data.left = new FormAttachment(0, 0);
			data.right = new FormAttachment(field, -ITabbedPropertyConstants.HSPACE);
			data.top = new FormAttachment(field, 0, SWT.TOP);
			label.setLayoutData(data);
		}
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);

		IStructuredSelection ss = (IStructuredSelection) selection;
		AbstractEditPart editPart = (AbstractEditPart) ss.getFirstElement();
		model = (BasePort) editPart.getModel();

		ToadEditor editor = (ToadEditor) part.getSite().getPage().getActiveEditor();
		commandStack = (CommandStack) editor.getAdapter(CommandStack.class);
	}

	@Override
	public void refresh() {
		for (Button button : radios) {
			button.setSelection(false);
		}
		switch (model.getNamePosition()) {
		default:
			leftRadio.setSelection(true);
			break;
		case PositionConstants.RIGHT:
			rightRadio.setSelection(true);
			break;
		case PositionConstants.TOP:
			topRadio.setSelection(true);
			break;
		case PositionConstants.BOTTOM:
			bottomRadio.setSelection(true);
			break;
		case PositionConstants.NONE:
			noneRadio.setSelection(true);
			break;
		}
	}

	private void setNamePosition(int position) {
		ChangeIntCommand command = model.getNamePositionCommand(position);
		commandStack.execute(command);
	}
}
