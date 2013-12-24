package jp.hishidama.eclipse_plugin.toad.clazz;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jp.hishidama.eclipse_plugin.toad.clazz.JavaDelegator.Parameter.Value;
import jp.hishidama.eclipse_plugin.toad.model.node.Attribute;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class OperatorMethod extends JavaDelegator {

	private IMethod method;

	private JavadocClass javadoc;
	private UserOperatorType operatorType;
	private List<Parameter> parameterList;

	public OperatorMethod(IMethod method) {
		this.method = method;
	}

	public IMethod getMethod() {
		return method;
	}

	@Override
	public boolean isDsl() {
		return getOperatorType() != null;
	}

	public JavadocClass getJavadoc() {
		if (javadoc == null) {
			javadoc = JavadocClass.getJavadoc(method);
		}
		return javadoc;
	}

	public String getName() {
		return method.getElementName();
	}

	public String getClassName() {
		IType type = method.getDeclaringType();
		return type.getFullyQualifiedName();
	}

	public List<Attribute> getAttributes() {
		Map<String, Value> map;
		{
			IType type = method.getDeclaringType();
			Parameter r = new Parameter();
			createAttribute(type, method, r);
			map = r.attributes;
		}

		List<Attribute> list = new ArrayList<Attribute>(map.size());
		for (Entry<String, Value> entry : map.entrySet()) {
			String[] ss = entry.getKey().split("#");
			Value value = entry.getValue();
			Attribute attr = new Attribute(ss[0], ss[1], value.type);
			attr.setValue(value.value);
			list.add(attr);
		}
		return list;
	}

	private static Set<String> ANNOTATION_SET;
	static {
		Set<String> set = new HashSet<String>();
		for (UserOperatorType type : UserOperatorType.values()) {
			set.add("com.asakusafw.vocabulary.operator." + type.name());
		}
		ANNOTATION_SET = set;
	}

	public UserOperatorType getOperatorType() {
		if (operatorType == null) {
			IType type = method.getDeclaringType();
			IAnnotation ann = getAnnotation(type, method, ANNOTATION_SET);
			if (ann != null) {
				operatorType = UserOperatorType.valueOf(ann.getElementName());
			}
		}
		return operatorType;
	}

	public String getReturnType() {
		try {
			IType type = method.getDeclaringType();
			String name = getClassNameFromSignature(method.getReturnType());
			return getQualifiedClassName(type, name);
		} catch (JavaModelException e) {
			return null;
		}
	}

	public List<Parameter> getParameters() {
		if (parameterList == null) {
			parameterList = getParameters(method);
		}
		return parameterList;
	}
}
