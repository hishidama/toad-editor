package jp.hishidama.eclipse_plugin.toad.model.property.file;

import java.io.IOException;
import java.text.MessageFormat;

import jp.hishidama.eclipse_plugin.toad.Activator;
import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.gson.ToadGson;
import jp.hishidama.eclipse_plugin.toad.model.node.ClassNameNode;
import jp.hishidama.eclipse_plugin.toad.model.node.jobflow.JobNode;
import jp.hishidama.eclipse_plugin.toad.model.property.TextSection;
import jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.page.JobflowFileCreationPage;
import jp.hishidama.eclipse_plugin.util.FileUtil;
import jp.hishidama.eclipse_plugin.util.ToadFileUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionEvent;

public class ToadFileSection extends TextSection<ClassNameNode> {

	public ToadFileSection() {
		super("toad file");
	}

	@Override
	protected String getValue(ClassNameNode model) {
		String ext = model.getToadFileExtension();
		if (ext != null) {
			String className = model.getClassName();
			return ToadFileUtil.getToadFile(className, ext);
		}
		return null;
	}

	@Override
	protected String getButtonText() {
		return "open";
	}

	@Override
	protected boolean isEnabledButton(ClassNameNode model, String text) {
		return !text.isEmpty();
	}

	@Override
	protected void buttonEvent(ClassNameNode model, SelectionEvent event) {
		try {
			openFile(getProject(), model, event);
		} catch (Exception e) {
			IStatus status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, "file open error", e);
			e.printStackTrace();
			ErrorDialog.openError(null, "open error",
					MessageFormat.format("open error. fileName={0}", getValue(model)), status);
		}
	}

	private void openFile(IProject project, ClassNameNode model, SelectionEvent event) throws CoreException,
			IOException {
		IFile file = ToadFileUtil.getFile(project, getValue(model));
		if (file == null) {
			return;
		}
		if (!file.exists()) {
			boolean ok = MessageDialog.openConfirm(null, "confirm create",
					MessageFormat.format("{0} is not exists.\nDoes it create?", file.getFullPath().toPortableString()));
			if (!ok) {
				event.doit = false;
				return;
			}
			FileUtil.createFolder(project, file);
			initializeFileContents(file, model);
		}

		ToadFileUtil.openFile(file);
	}

	private void initializeFileContents(IFile file, ClassNameNode model) throws CoreException, IOException {
		if (model instanceof JobNode) {
			JobNode job = (JobNode) model;
			JobflowFileCreationPage page = new JobflowFileCreationPage(file.getProject());
			Diagram diagram = page.generateDiagram();
			diagram.setDescription(job.getDescription());
			diagram.setClassName(job.getClassName());
			ToadGson gson = new ToadGson();
			gson.save(file, diagram);
			return;
		}

		file.create(null, false, null);
	}
}
