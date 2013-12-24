package jp.hishidama.eclipse_plugin.toad.editor.handler;

import jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.ReverseWizard;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

public class ReverseHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelectionChecked(event);
		if (!(selection instanceof IStructuredSelection)) {
			return null;
		}
		IStructuredSelection ss = (IStructuredSelection) selection;
		Object element = ss.getFirstElement();
		if (element instanceof ICompilationUnit) {
			openWizard(ss);
			return null;
		}

		MessageDialog.openWarning(null, "error", "not found class.");
		return null;
	}

	private void openWizard(IStructuredSelection selection) {
		ReverseWizard wizard = new ReverseWizard();
		wizard.setSelection(selection);
		WizardDialog dialog = new WizardDialog(null, wizard);
		dialog.open();
	}
}
