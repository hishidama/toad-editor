package jp.hishidama.eclipse_plugin.toad.model.dialog;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.model.AbstractModel;
import jp.hishidama.eclipse_plugin.toad.validation.ValidateType;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;

public abstract class PropertyDialogWizard extends Wizard {
	protected final IProject project;
	protected final ToadEditor editor;
	protected final boolean directEdit;
	protected final AbstractModel srcModel;
	protected final AbstractModel model;

	private WizardDialog dialog;
	private PropertyDialogWizardPage page;

	protected PropertyDialogWizard(ToadEditor editor, AbstractModel model, boolean directEdit) {
		this.project = editor.getProject();
		this.editor = editor;
		this.srcModel = model;
		this.directEdit = directEdit;
		if (directEdit) {
			this.model = model;
		} else {
			this.model = model.cloneEdit();
		}
		setNeedsProgressMonitor(true);
	}

	public IProject getProject() {
		return project;
	}

	public AbstractModel getSourceModel() {
		return srcModel;
	}

	public int open() {
		dialog = new WizardDialog(editor.getSite().getShell(), this) {
			@Override
			protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
				if (id == IDialogConstants.FINISH_ID) {
					label = IDialogConstants.OK_LABEL;
				}
				return super.createButton(parent, id, label, defaultButton);
			}
		};
		dialog.setHelpAvailable(false);
		return dialog.open();
	}

	protected boolean close() {
		return dialog.close();
	}

	@Override
	public void addPages() {
		page = createPage();
		addPage(page);
	}

	protected PropertyDialogWizardPage createPage() {
		return new PropertyDialogWizardPage() {
			@Override
			protected void createFields(TabFolder tab) {
				createdFields = false;
				PropertyDialogWizard.this.createFields(tab);
				createdFields = true;
				doValidate();
			}
		};
	}

	protected abstract void createFields(TabFolder tab);

	boolean createdFields;

	public void doValidate() {
		if (!createdFields) {
			page.setPageComplete(false);
			return;
		}
		List<IStatus> list = new ArrayList<IStatus>();
		validate(list);

		for (IStatus status : list) {
			if (status.getSeverity() == IStatus.ERROR) {
				page.setPageComplete(false);
				page.setErrorMessage(status.getMessage());
				return;
			}
		}
		page.setErrorMessage(null);

		for (IStatus status : list) {
			if (status.getSeverity() == IStatus.WARNING) {
				page.setPageComplete(true);
				page.setMessage(status.getMessage(), IMessageProvider.WARNING);
				return;
			}
		}
		page.setMessage(null, IMessageProvider.WARNING);

		page.setPageComplete(true);
	}

	private void validate(List<IStatus> list) {
		model.validate(ValidateType.DESIGN, true, list); // TODO ValidateType
	}

	@Override
	public boolean performFinish() {
		apply();
		return true;
	}

	protected abstract void apply();

	protected void execute(Command command) {
		CommandStack commandStack = (CommandStack) editor.getAdapter(CommandStack.class);
		commandStack.execute(command);
	}
}
