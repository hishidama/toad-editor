package jp.hishidama.eclipse_plugin.toad.jdt.hyperlink;

import jp.hishidama.eclipse_plugin.jdt.hyperlink.JdtHyperlinkDetector;
import jp.hishidama.eclipse_plugin.util.ToadFileUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

public class OpenToadHyperlinkDetector extends JdtHyperlinkDetector {

	@Override
	protected IHyperlink[] detectTypeHyperlinks(IType type, IRegion word) {
		IFile file = findToadFile(type);
		if (file == null || !file.exists()) {
			return null;
		}
		return new IHyperlink[] { new ToadHyperlink(file, null, word) };
	}

	@Override
	protected IHyperlink[] detectFieldHyperlinks(IField field, IRegion word) {
		IType type = field.getDeclaringType();
		IFile file = findToadFile(type);
		if (file == null) {
			return null;
		}
		return new IHyperlink[] { new ToadFrameHyperlink(file, field.getElementName(), word) };
	}

	@Override
	protected IHyperlink[] detectConstructorHyperlinks(IMethod method, IRegion word) {
		IType type = method.getDeclaringType();
		IFile file = findToadFile(type);
		if (file == null) {
			return null;
		}
		return new IHyperlink[] { new ToadFrameHyperlink(file, null, word) };
	}

	@Override
	protected IHyperlink[] detectVariableHyperlinks(ILocalVariable variable, IRegion word) {
		IMember member = variable.getDeclaringMember();
		IType type = member.getDeclaringType();
		IFile file = findToadFile(type);
		if (file == null) {
			return null;
		}
		String name = variable.getElementName();
		return new IHyperlink[] { new ToadHyperlink(file, name, word) };
	}

	private static IFile findToadFile(IType type) {
		IProject project = type.getJavaProject().getProject();
		String className = type.getFullyQualifiedName();
		IFile file = ToadFileUtil.getToadFile(project, className, "btoad");
		if (file.exists()) {
			return file;
		}
		file = ToadFileUtil.getToadFile(project, className, "jtoad");
		if (file.exists()) {
			return file;
		}
		file = ToadFileUtil.getToadFile(project, className, "ftoad");
		if (file.exists()) {
			return file;
		}
		return null;
	}
}
