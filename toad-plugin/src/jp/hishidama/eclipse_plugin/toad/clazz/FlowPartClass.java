package jp.hishidama.eclipse_plugin.toad.clazz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class FlowPartClass extends ClassDelegator {

	public FlowPartClass(IType type) {
		super(type);
	}

	private Boolean isDsl;

	@Override
	public boolean isDsl() {
		if (isDsl == null) {
			isDsl = getAnnotation(type, "com.asakusafw.vocabulary.flow.FlowPart") != null;
		}
		return isDsl;
	}

	private String name;

	public String getName() {
		if (name == null) {
			name = type.getElementName();
		}
		return name;
	}

	private List<Parameter> allParameters;

	public List<Parameter> getAllParameters() {
		if (allParameters == null) {
			try {
				IMethod ctor = findConstructor();
				if (ctor != null) {
					allParameters = getParameters(ctor);
				} else {
					allParameters = Collections.emptyList();
				}
			} catch (JavaModelException e) {
				allParameters = Collections.emptyList();
			}
		}
		return allParameters;
	}

	private List<Parameter> parameters;

	public List<Parameter> getParameters() {
		if (parameters == null) {
			parameters = new ArrayList<Parameter>();
			for (Parameter param : getAllParameters()) {
				if ("com.asakusafw.vocabulary.flow.In".equals(param.className)
						|| "com.asakusafw.vocabulary.flow.Out".equals(param.className)) {
					continue;
				}
				parameters.add(param);
			}
		}
		return parameters;
	}
}
