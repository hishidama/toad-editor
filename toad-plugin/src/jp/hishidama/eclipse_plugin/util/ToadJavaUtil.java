package jp.hishidama.eclipse_plugin.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;

public class ToadJavaUtil {

	public static ICompilationUnit getJavaUnit(IFile file) {
		IJavaElement element = JavaCore.create(file);
		if (element == null) {
			return null;
		}
		ICompilationUnit unit = (ICompilationUnit) element.getAncestor(IJavaElement.COMPILATION_UNIT);
		if (unit == null) {
			return null;
		}
		return unit;
	}
}
