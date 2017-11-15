package jp.hishidama.eclipse_plugin.toad.clazz;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public abstract class ClassDelegator extends JavaDelegator {

	protected final IType type;

	public ClassDelegator(IType type) {
		this.type = type;
	}

	public final IType getType() {
		return type;
	}

	private String className;

	public final String getClassName() {
		if (className == null) {
			className = type.getFullyQualifiedName();
		}
		return className;
	}

	private JavadocClass javadoc;

	public final JavadocClass getJavadoc() {
		if (javadoc == null) {
			javadoc = JavadocClass.getJavadoc(type);
		}
		return javadoc;
	}

	public List<String> getParameterNames() throws IOException {
		try {
			IMethod ctor = findConstructor();
			if (ctor == null) {
				throw new IOException(MessageFormat.format("constructor not found. class={0}", getClassName()));
			}
			return getParameterNames(ctor);
		} catch (JavaModelException e) {
			throw new IOException(e);
		}
	}

	private IMethod constructor;

	protected IMethod findConstructor() throws JavaModelException {
		if (constructor == null) {
			for (IMethod method : type.getMethods()) {
				if (method.isConstructor()) {
					constructor = method;
					break;
				}
			}
		}
		return constructor;
	}

	protected IAnnotation getAnnotation(IType type, String annotationName) {
		return super.getAnnotation(type, type, annotationName);
	}

	protected <T> T getAnnotationValue(IType type, String annotationName, String memberName) {
		return super.getAnnotationValue(type, type, annotationName, memberName);
	}
}
