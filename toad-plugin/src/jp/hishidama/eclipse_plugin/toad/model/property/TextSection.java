package jp.hishidama.eclipse_plugin.toad.model.property;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.model.AbstractModel.ChangeTextCommand;

import org.eclipse.core.resources.IProject;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public abstract class TextSection<M> extends AbstractPropertySection {

	private String label;
	private Text text;
	private FocusListener focusListener = new FocusListener() {
		@Override
		public void focusGained(FocusEvent event) {
			command = getChanteTextCommand(model);
		}

		@Override
		public void focusLost(FocusEvent event) {
			if (command != null) {
				String value = text.getText();
				if (validate(model, value)) {
					command.setNewValue(value);
					commandStack.execute(command);
				} else {
					text.setText(command.getOldValue());
				}
			}
		}
	};
	private TraverseListener traverseListener = new TraverseListener() {
		@Override
		public void keyTraversed(TraverseEvent event) {
			if (event.detail == SWT.TRAVERSE_ESCAPE) {
				text.setText(command.getOldValue());
			}
		}
	};

	private Button button;

	private M model;
	protected ToadEditor editor;
	protected CommandStack commandStack;
	private ChangeTextCommand command;

	protected TextSection(String label) {
		this.label = label;
	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		Composite composite = factory.createFlatFormComposite(parent);

		String buttonText = getButtonText();
		if (buttonText != null) {
			button = factory.createButton(composite, buttonText, SWT.NONE);
			FormData data = new FormData();
			data.left = null;
			data.right = new FormAttachment(100, 0);
			data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
			button.setLayoutData(data);
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					buttonEvent(model, e);
				}
			});
		}
		{
			int style = getTextStyle();
			text = factory.createText(composite, "", style);
			FormData data = new FormData();
			data.left = new FormAttachment(0, 96);
			if (button != null) {
				data.right = new FormAttachment(button, -ITabbedPropertyConstants.HSPACE);
			} else {
				data.right = new FormAttachment(100, 0);
			}
			data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
			if ((style & SWT.MULTI) != 0) {
				data.bottom = new FormAttachment(100, 0);
			}
			text.setLayoutData(data);
			text.addFocusListener(focusListener);
			text.addTraverseListener(traverseListener);
			text.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					if (button != null) {
						button.setEnabled(isEnabledButton(model, text.getText()));
					}
				}
			});
			text.setEditable(editableText());
		}
		{
			CLabel label = factory.createCLabel(composite, this.label + " :");
			FormData data = new FormData();
			data.left = new FormAttachment(0, 0);
			data.right = new FormAttachment(text, -ITabbedPropertyConstants.HSPACE);
			data.top = new FormAttachment(text, 0, SWT.CENTER);
			label.setLayoutData(data);
		}
	}

	protected int getTextStyle() {
		return SWT.SINGLE;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);

		IStructuredSelection ss = (IStructuredSelection) selection;
		AbstractEditPart editPart = (AbstractEditPart) ss.getFirstElement();
		model = (M) editPart.getModel();

		editor = (ToadEditor) part.getSite().getPage().getActiveEditor();
		commandStack = (CommandStack) editor.getAdapter(CommandStack.class);
	}

	protected IProject getProject() {
		return editor.getProject();
	}

	@Override
	public void refresh() {
		String value = getValue(model);
		text.setText((value != null) ? value : "");
	}

	protected boolean editableText() {
		return false;
	}

	protected void setValue(M model, String value) {
		// do override if editableText() is true
		throw new UnsupportedOperationException("sectionClass=" + getClass().getName());
	}

	protected abstract String getValue(M model);

	protected ChangeTextCommand getChanteTextCommand(final M model) {
		if (!editableText()) {
			return null;
		}
		return new ChangeTextCommand(text.getText()) {
			@Override
			protected String getValue() {
				return TextSection.this.getValue(model);
			}

			@Override
			protected void setValue(String value) {
				TextSection.this.setValue(model, value);
			}
		};
	}

	protected boolean validate(M model, String text) {
		return true;
	}

	protected String getButtonText() {
		return null; // do override
	}

	protected boolean isEnabledButton(M model, String text) {
		return true; // do override
	}

	protected void buttonEvent(M model, SelectionEvent event) {
		// do override
	}
}
