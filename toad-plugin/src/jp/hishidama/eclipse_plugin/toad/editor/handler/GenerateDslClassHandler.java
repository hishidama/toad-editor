package jp.hishidama.eclipse_plugin.toad.editor.handler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

import jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen.BatchClassGenerator;
import jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen.DslClassGenerator;
import jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen.FlowpartClassGenerator;
import jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen.JobClassGenerator;
import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.gson.ToadGson;
import jp.hishidama.eclipse_plugin.util.FileUtil;
import jp.hishidama.eclipse_plugin.util.ToadFileUtil;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class GenerateDslClassHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelectionChecked(event);
		if (!(selection instanceof IStructuredSelection)) {
			return null;
		}
		IStructuredSelection ss = (IStructuredSelection) selection;
		Object element = ss.getFirstElement();
		Target target = null;
		if (element instanceof IFile) {
			IFile file = (IFile) element;
			target = generateDslClass(file);
		} else if (element instanceof IFolder) {
			IFolder folder = (IFolder) element;
			target = generateDslClasses(folder);
		}

		if (target != null) {
			ToadFileUtil.openFile(target.file, target.className);
		}

		return null;
	}

	private Target generateDslClasses(IContainer folder) throws ExecutionException {
		Target result = null;
		try {
			for (IResource r : folder.members()) {
				Target t = null;
				if (r instanceof IFile) {
					t = generateDslClass((IFile) r);
				} else if (r instanceof IContainer) {
					t = generateDslClasses((IContainer) r);
				}
				if (t != null) {
					result = t;
				}
			}
		} catch (CoreException e) {
			throw new ExecutionException("generate DSL Class error", e);
		}
		return result;
	}

	private Target generateDslClass(IFile toadFile) throws ExecutionException {
		String ext = toadFile.getFileExtension();
		if ("btoad".equals(ext) || "jtoad".equals(ext) || "ftoad".equals(ext)) {
			try {
				ToadGson gson = new ToadGson();
				Diagram diagram = gson.load(toadFile);
				return generateDslClass(toadFile.getProject(), diagram);
			} catch (Exception e) {
				throw new ExecutionException("generate DSL Class error", e);
			}
		}
		return null;
	}

	public Target generateDslClass(IProject project, Diagram diagram) throws CoreException {
		DslClassGenerator generator;
		switch (diagram.getDiagramType()) {
		case BATCH:
			generator = new BatchClassGenerator(project, diagram);
			break;
		case JOBFLOW:
			generator = new JobClassGenerator(project, diagram);
			break;
		case FLOWPART:
			generator = new FlowpartClassGenerator(project, diagram);
			break;
		default:
			MessageDialog.openWarning(null, "error",
					MessageFormat.format("diagram is unknown type. type={0}", diagram.getDiagramType()));
			return null;
		}

		IFile target = ToadFileUtil.getJavaFile(project, diagram);
		generateDslClass(target, generator);

		Target result = new Target();
		result.file = target;
		result.className = diagram.getClassName();
		return result;
	}

	public static class Target {
		public IFile file;
		public String className;
	}

	public void generateDslClass(IFile targetFile, DslClassGenerator generator) throws CoreException {
		String contents = generator.generate();

		IProject project = targetFile.getProject();
		FileUtil.createFolder(project, targetFile);
		InputStream is;
		try {
			is = new ByteArrayInputStream(contents.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		try {
			if (targetFile.exists()) {
				targetFile.setContents(is, true, false, null);
			} else {
				targetFile.create(is, false, null);
			}
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
