package jp.hishidama.eclipse_plugin.toad.clazz;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class JavadocClass {

	public static JavadocClass getJavadoc(IJavaProject javaProject, String className) {
		try {
			IType type = javaProject.findType(className);
			if (type != null) {
				return getJavadoc(type);
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return new JavadocClass();
	}

	public static JavadocClass getJavadoc(IType type) {
		ISourceRange range;
		try {
			range = type.getJavadocRange();
		} catch (JavaModelException e) {
			range = null;
		}
		return getJavadoc(type.getCompilationUnit(), range, null);
	}

	public static JavadocClass getJavadoc(IJavaProject javaProject, String className, String methodName) {
		try {
			IType type = javaProject.findType(className);
			if (type != null) {
				for (IMethod m : type.getMethods()) {
					if (m.getElementName().equals(methodName)) {
						return getJavadoc(m);
					}
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return new JavadocClass();
	}

	public static JavadocClass getJavadoc(IMethod method) {
		ISourceRange range;
		try {
			range = method.getSourceRange();
		} catch (JavaModelException e) {
			range = null;
		}
		return getJavadoc(method.getCompilationUnit(), range, method.getElementName());
	}

	private static JavadocClass getJavadoc(ICompilationUnit unit, ISourceRange range, String methodName) {
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(unit);
		if (range != null) {
			parser.setSourceRange(range.getOffset(), range.getLength());
		}
		ASTNode node = parser.createAST(null);

		JavadocClass javadoc = new JavadocClass();
		Visitor visitor = javadoc.new Visitor(methodName);
		node.accept(visitor);
		return javadoc;
	}

	private List<String> textList = new ArrayList<String>();
	private List<Tag> tagList = new ArrayList<Tag>();

	public static class Tag {
		public String tagName;
		public List<String> values;

		public String value(int i) {
			if (i < values.size()) {
				return values.get(i);
			}
			return null;
		}
	}

	private class Visitor extends ASTVisitor {

		private String methodName;

		public Visitor(String methodName) {
			this.methodName = methodName;
		}

		@Override
		public boolean visit(TypeDeclaration node) {
			if (methodName != null) {
				return true;
			}
			collect(node.getJavadoc());
			return false;
		}

		@Override
		public boolean visit(MethodDeclaration node) {
			if (methodName != null && methodName.equals(node.getName().getFullyQualifiedName())) {
				collect(node.getJavadoc());
			}
			return false;
		}

		private void collect(Javadoc javadoc) {
			if (javadoc == null) {
				return;
			}
			@SuppressWarnings("unchecked")
			List<TagElement> tags = javadoc.tags();
			for (TagElement te : tags) {
				collect(te);
			}
		}

		private void collect(TagElement node) {
			List<String> list = new ArrayList<String>();
			for (Object obj : node.fragments()) {
				if (obj instanceof Name) {
					list.add(((Name) obj).getFullyQualifiedName());
				} else if (obj instanceof TextElement) {
					list.add(((TextElement) obj).getText());
				} else {
					list.add(obj.toString());
				}
			}

			String tagName = node.getTagName();
			if (tagName == null) {
				textList.addAll(list);
			} else {
				Tag tag = new Tag();
				tag.tagName = tagName;
				tag.values = list;
				tagList.add(tag);
			}
		}
	}

	public String getTitle() {
		if (textList.isEmpty()) {
			return null;
		}
		return textList.get(0).trim();
	}

	public String getMemo() {
		return toString(textList, 1, "\n");
	}

	public String getParamValue(String name) {
		for (Tag tag : tagList) {
			if ("@param".equals(tag.tagName)) {
				if (name.equals(tag.value(0))) {
					return toString(tag.values, 1, " ");
				}
			}
		}
		return null;
	}

	public String getReturnValue() {
		for (Tag tag : tagList) {
			if ("@return".equals(tag.tagName)) {
				return toString(tag.values, 0, " ");
			}
		}
		return null;
	}

	private static String toString(List<String> list, int begin, String separator) {
		if (begin >= list.size()) {
			return null;
		}

		StringBuilder sb = new StringBuilder(256);
		for (int i = begin; i < list.size(); i++) {
			if (i != begin) {
				sb.append(separator);
			}
			sb.append(list.get(i));
		}
		return sb.toString();
	}
}
