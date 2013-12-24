package jp.hishidama.eclipse_plugin.toad.wizard.newdiagram;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import jp.hishidama.eclipse_plugin.toad.Activator;
import jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.page.FlowpartParameterPage;
import jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.page.SelectClassPage;
import jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.task.GenerateDiagramTask;
import jp.hishidama.eclipse_plugin.util.ToadFileUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class ReverseWizard extends Wizard implements INewWizard {
	private IStructuredSelection selection;
	private IProject project;

	private SelectClassPage classPage;
	private FlowpartParameterPage paramPage;

	public ReverseWizard() {
		setWindowTitle("既存DSLからダイアグラム作成");
		setDialogSettings(Activator.getDefault().getDialogSettings());
	}

	public void setSelection(IStructuredSelection selection) {
		this.selection = selection;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
		Object element = selection.getFirstElement();
		if (element instanceof IResource) {
			project = ((IResource) element).getProject();
		} else if (element instanceof IJavaElement) {
			project = ((IJavaElement) element).getJavaProject().getProject();
		}
		if (project == null) {
			IEditorPart editor = workbench.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			if (editor != null) {
				IEditorInput input = editor.getEditorInput();
				if (input instanceof IFileEditorInput) {
					IFile file = ((IFileEditorInput) input).getFile();
					project = file.getProject();
					this.selection = new StructuredSelection(file);
				}
			}
		}
	}

	@Override
	public void addPages() {
		classPage = new SelectClassPage("各種", "Asakusa DSL", selection);
		addPage(classPage);
		paramPage = new FlowpartParameterPage();
		addPage(paramPage);
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		IWizardPage nextPage = super.getNextPage(page);
		if (nextPage == paramPage) {
			setNeedsProgressMonitor(true);
			paramPage.setFiles(classPage.getSelectedFile());
		}
		return nextPage;
	}

	@Override
	public boolean performFinish() {
		List<IFile> list = classPage.getSelectedFile();
		Map<String, Object> values = paramPage.getValues();

		setNeedsProgressMonitor(true);
		GenerateDiagramTask task = new GenerateDiagramTask(list, values);
		try {
			getContainer().run(true, true, task);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			return false;
		}

		IFile first = task.getFirstFile();
		if (first != null) {
			ToadFileUtil.openFile(first);
		}
		return true;
	}
}
