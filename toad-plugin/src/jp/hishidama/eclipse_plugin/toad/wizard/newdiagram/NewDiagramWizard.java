package jp.hishidama.eclipse_plugin.toad.wizard.newdiagram;

import java.text.MessageFormat;

import jp.hishidama.eclipse_plugin.toad.Activator;
import jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.page.DiagramFileCreationPage;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public abstract class NewDiagramWizard extends Wizard implements INewWizard {
	protected String diagramName;
	protected String dslName;

	private IProject project;
	private String packageName;
	private DiagramFileCreationPage filePage;

	public NewDiagramWizard(String diagramName, String dslName) {
		this.diagramName = diagramName;
		this.dslName = dslName;

		setWindowTitle(MessageFormat.format("{0}ダイアグラム作成", diagramName));
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		Object element = selection.getFirstElement();
		if (element instanceof IResource) {
			IResource r = (IResource) element;
			project = r.getProject();
			initPath(r);
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
				}
			}
		}
	}

	private void initPath(IResource r) {
		IPath path = r.getProjectRelativePath();
		if (r instanceof IFile) {
			path = path.removeLastSegments(1);
		}
		if (path.segmentCount() > 3) {
			path = path.removeFirstSegments(3);
		}
		packageName = path.toPortableString().replace('/', '.');
	}

	@Override
	public void addPages() {
		filePage = createFileCreationPage(project);
		filePage.setPackageName(packageName);
		addPage(filePage);
	}

	protected abstract DiagramFileCreationPage createFileCreationPage(IProject project);

	@Override
	public boolean performFinish() {
		try {
			IFile target = filePage.createNewFile();
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IDE.openEditor(page, target);
		} catch (PartInitException e) {
			IStatus status = e.getStatus();
			Activator.getDefault().getLog().log(status);
			return true;
		} catch (CoreException e) {
			IStatus status = e.getStatus();
			Activator.getDefault().getLog().log(status);
			return false;
		} catch (Exception e) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
			Activator.getDefault().getLog().log(status);
			return false;
		}
		return true;
	}
}
