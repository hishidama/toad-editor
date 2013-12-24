package jp.hishidama.eclipse_plugin.toad.clazz;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.hishidama.eclipse_plugin.jdt.util.TypeUtil;

import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public abstract class JavaDelegator {

	public abstract boolean isDsl();

	protected List<String> getParameterNames(IMethod method) throws IOException {
		try {
			return Arrays.asList(method.getParameterNames());
		} catch (JavaModelException e) {
			throw new IOException(e);
		}
	}

	protected IAnnotation getAnnotation(IType type, IAnnotatable a, String annotationName) {
		return getAnnotation(type, a, Collections.singleton(annotationName));
	}

	protected IAnnotation getAnnotation(IType type, IAnnotatable a, Set<String> annotationName) {
		try {
			// http://www.eclipse.org/forums/index.php/m/257652/
			for (IAnnotation ann : a.getAnnotations()) {
				String[][] annTypes = type.resolveType(ann.getElementName());
				if (annTypes != null) {
					for (String[] name : annTypes) {
						String annName = name[0] + "." + name[1];
						if (annotationName.contains(annName)) {
							return ann;
						}
					}
				}
			}
		} catch (JavaModelException e) {
			return null;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected <T> T getAnnotationValue(IType type, IAnnotatable a, String annotationName, String memberName) {
		IAnnotation ann = getAnnotation(type, a, annotationName);
		if (ann != null) {
			try {
				for (IMemberValuePair pair : ann.getMemberValuePairs()) {
					if (memberName.equals(pair.getMemberName())) {
						return (T) pair.getValue();
					}
				}
			} catch (JavaModelException e) {
				return null;
			}
		}
		return null;
	}

	protected boolean existsInterface(IType type, String interfaceName) {
		return TypeUtil.isImplements(type, interfaceName);
	}

	protected static String getClassNameFromSignature(String signature) {
		int start = 0;
		int end = signature.length();
		if (signature.startsWith("Q")) {
			start++;
		}
		if (signature.endsWith(";")) {
			end--;
		}
		return signature.substring(start, end);
	}

	protected static String getQualifiedClassName(IType type, String name) {
		if (name == null) {
			return null;
		}
		try {
			String[][] resolved = type.resolveType(name);
			if (resolved == null) {
				return getTypeFromSimple(name);
			}
			String resPack = resolved[0][0];
			String resName = resolved[0][1];
			return resPack + "." + resName;
		} catch (JavaModelException e) {
			return name;
		}
	}

	private static final Map<String, String> PRIMITIVE_MAP;
	static {
		Map<String, String> map = new HashMap<String, String>();
		map.put("B", "byte");
		map.put("C", "char");
		map.put("D", "double");
		map.put("F", "float");
		map.put("I", "int");
		map.put("J", "long");
		map.put("S", "short");
		map.put("Z", "boolean");
		PRIMITIVE_MAP = map;
	}

	private static String getTypeFromSimple(String name) {
		String s = PRIMITIVE_MAP.get(name);
		if (s != null) {
			return s;
		}
		return name;
	}

	public List<Parameter> getParameters(IMethod method) {
		IType type = method.getDeclaringType();
		ILocalVariable[] ps;
		try {
			ps = method.getParameters();
		} catch (JavaModelException e) {
			return Collections.emptyList();
		}

		List<Parameter> list = new ArrayList<Parameter>();
		for (ILocalVariable param : ps) {
			String rsig = param.getTypeSignature();
			String rtype, rparm;
			int n = rsig.indexOf('<');
			if (n < 0) {
				rtype = getClassNameFromSignature(rsig);
				rparm = null;
			} else {
				rtype = getClassNameFromSignature(rsig.substring(0, n).trim());
				rparm = getClassNameFromSignature(rsig.substring(n + 1, rsig.lastIndexOf('>')).trim());
			}
			Parameter r = new Parameter();
			r.name = param.getElementName();
			r.className = getQualifiedClassName(type, rtype);
			r.typeParameter = getQualifiedClassName(type, rparm);
			createAttribute(type, param, r);
			list.add(r);
		}
		return list;
	}

	protected final void createAttribute(IType type, IAnnotatable a, Parameter r) {
		try {
			for (IAnnotation ann : a.getAnnotations()) {
				String aname = getQualifiedClassName(type, ann.getElementName());
				for (IMemberValuePair pair : ann.getMemberValuePairs()) {
					String mname = pair.getMemberName();
					Object value = pair.getValue();
					switch (pair.getValueKind()) {
					case IMemberValuePair.K_STRING:
						if (value instanceof String) {
							r.addAttribute(aname, mname, "java.lang.String", (String) value);
						} else if (value instanceof Object[]) {
							Object[] os = (Object[]) value;
							for (Object obj : os) {
								r.addAttribute(aname, mname, "java.lang.String", (String) obj);
							}
						}
						break;
					case IMemberValuePair.K_UNKNOWN:
						if (value instanceof Object[]) {
							Object[] os = (Object[]) value;
							if (os.length <= 0) {
								r.addAttribute(aname, mname, "java.lang.Object", null);
							} else {
								for (Object obj : os) {
									r.addAttribute(aname, mname, "java.lang.Object", obj.toString());
								}
							}
						}
						break;
					default:
						throw new UnsupportedOperationException(MessageFormat.format("annotation={0}, kind={1}", aname,
								pair.getValueKind()));
					}
				}
			}
		} catch (JavaModelException e) {
			// do nothing
		}
	}

	public static class Parameter {
		public String name;
		public String className;
		public String typeParameter;
		public Map<String, Value> attributes = new LinkedHashMap<String, Value>();

		public static class Value {
			public String type;
			public List<String> value = new ArrayList<String>();

			public Value() {
			}

			public Value(String type) {
				this.type = type;
			}
		}

		void addAttribute(String aname, String mname, String type, String value) {
			String key = String.format("%s#%s", aname, mname);
			Value v = attributes.get(key);
			if (v == null) {
				v = new Value();
				attributes.put(key, v);
			}
			v.type = type;
			if (value != null) {
				v.value.add(value);
			}
		}
	}
}
