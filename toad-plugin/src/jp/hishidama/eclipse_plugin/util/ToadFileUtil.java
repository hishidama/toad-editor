package jp.hishidama.eclipse_plugin.util;

import jp.hishidama.eclipse_plugin.toad.model.node.ClassNameNode;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class ToadFileUtil {

	public static IFile getFile(IProject project, String path) {
		if (project == null || path == null) {
			return null;
		}
		path = path.trim();
		if (path.isEmpty()) {
			return null;
		}
		return project.getFile(path);
	}

	public static IFile getJavaFile(IProject project, ClassNameNode model) {
		String className = model.getClassName();
		return getJavaFile(project, className);
	}

	public static IFile getJavaFile(IProject project, String className) {
		// IFolder folder = project.getFolder(model.getDirectory());
		IFolder folder = project.getFolder("src/main/java"); // TODO
		String fileName = className.replace('.', '/') + ".java";
		return folder.getFile(fileName);
	}

	public static IFile getToadFile(IProject project, String className, String  ext) {
		String path = getToadFile(className, ext);
		return getFile(project, path);
	}

	public static String getToadFile(String className, String ext) {
		if (className == null || className.trim().isEmpty()) {
			return null;
		}
		return "src/main/toad/" + className.trim().replace('.', '/') + "." + ext;
	}

	public static String getToadClassName(String path) {
		int n = path.indexOf("/src");
		if (n >= 0) {
			path = path.substring(n + 1);
		}
		if (path.startsWith("src/main/toad/")) {
			path = path.substring("src/main/toad/".length());
		}
		n = path.lastIndexOf('.');
		if (n >= 0) {
			path = path.substring(0, n);
		}
		return path.replace('/', '.');
	}

	public static String getPortablePath(IFile file) {
		String path = file.getProjectRelativePath().toPortableString();
		while (path.startsWith("/")) {
			path = path.substring(1);
		}
		return path;
	}

	public static boolean openFile(IFile file, String className) {
		if (className != null) {
			IProject project = file.getProject();
			IJavaProject javaProject = JavaCore.create(project);
			if (javaProject != null) {
				try {
					IType type = javaProject.findType(className);
					JavaUI.openInEditor(type);
					return true;
				} catch (Exception e) {
					// fall through
				}
			}
		}

		return openFile(file) != null;
	}

	public static IEditorPart openFile(IFile file) {
		if (file.exists()) {
			try {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				return IDE.openEditor(page, file);
			} catch (Exception e) {
				// fall through
			}
		}
		return null;
	}
}
