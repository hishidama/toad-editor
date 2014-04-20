package jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.hishidama.eclipse_plugin.toad.model.node.Attribute;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;
import jp.hishidama.eclipse_plugin.toad.model.node.port.OpePort;
import jp.hishidama.eclipse_plugin.toad.model.property.attribute.HasAttributeNode;
import jp.hishidama.eclipse_plugin.util.StringUtil;
import jp.hishidama.xtext.dmdl_editor.dmdl.ModelUiUtil;

import org.eclipse.core.resources.IProject;

public class OperatorMethodGenerator {

	public static abstract class AbstractType {
		public String description;

		public abstract String resolvedType(TypeResolver resolver);

		@Override
		public String toString() {
			return resolvedType(SIMPLE_RESOLVER);
		}

		public boolean isVoid() {
			return false;
		}
	}

	public static class NormalClass extends AbstractType {
		private String type;

		public NormalClass(String type) {
			this.type = type;
		}

		@Override
		public String resolvedType(TypeResolver resolver) {
			return resolver.resolve(type);
		}

		@Override
		public boolean isVoid() {
			return "void".equals(type);
		}
	}

	public static final NormalClass VOID = new NormalClass("void");

	public static class TypedClass extends AbstractType {
		private NormalClass type;
		private List<AbstractType> list;

		public TypedClass(String name, AbstractType... types) {
			this(name, Arrays.asList(types));
		}

		public TypedClass(String name, List<AbstractType> list) {
			this.type = new NormalClass(name);
			this.list = list;
		}

		@Override
		public String resolvedType(TypeResolver resolver) {
			StringBuilder sb = new StringBuilder(64);
			sb.append(type.resolvedType(resolver));
			sb.append("<");
			for (AbstractType t : list) {
				if (sb.charAt(sb.length() - 1) != '<') {
					sb.append(", ");
				}
				sb.append(t.resolvedType(resolver));
			}
			sb.append(">");
			return sb.toString();
		}
	}

	public class DataModel extends AbstractType {
		private String name;

		public DataModel(String name) {
			this.name = name;
		}

		@Override
		public String resolvedType(TypeResolver resolver) {
			String type = ModelUiUtil.getModelClassName(project, name);
			if (type == null) {
				type = name;
			}
			return resolver.resolve(type);
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private static class Annotations {
		private Map<String, List<String>> map = new LinkedHashMap<String, List<String>>();

		public void add(String annotationName, String argument) {
			List<String> list = map.get(annotationName);
			if (list == null) {
				list = new ArrayList<String>();
				map.put(annotationName, list);
			}
			if (argument != null) {
				list.add(argument);
			}
		}

		public String resolvedType(TypeResolver resolver, String separator) {
			StringBuilder sb = new StringBuilder(128);
			for (Entry<String, List<String>> entry : map.entrySet()) {
				String name = entry.getKey();
				List<String> arguments = entry.getValue();

				sb.append("@");
				sb.append(resolver.resolve(name));
				if (arguments != null && !arguments.isEmpty()) {
					sb.append("(");
					for (String arg : arguments) {
						if (sb.charAt(sb.length() - 1) != '(') {
							sb.append(", ");
						}
						sb.append(arg);
					}
					sb.append(")");
				}
				sb.append(separator);
			}
			return sb.toString();
		}
	}

	private IProject project;
	private List<String> javadocList = new ArrayList<String>();
	private Annotations annotation;
	private boolean isAbstract;
	private AbstractType returnType;
	private String methodName;
	private List<Argument> arguments = new ArrayList<Argument>();

	public void setProject(IProject project) {
		this.project = project;
	}

	public void addJavadoc(String text) {
		javadocList.add(text);
	}

	public void addAnnotation(String name) {
		addAnnotation(name, null);
	}

	public void addAnnotation(String name, String argument) {
		if (annotation == null) {
			annotation = new Annotations();
		}
		this.annotation.add(name, argument);
	}

	public void addAnnotation(OperatorNode operator) {
		annotation = getAnnotation(annotation, operator);
	}

	public void setAbstract(boolean b) {
		this.isAbstract = b;
	}

	public void setReturnClass(String className, String description) {
		this.returnType = new NormalClass(className);
		returnType.description = description;
	}

	public void setReturnModel(OpePort port) {
		String desc = port.getDescription();
		if (StringUtil.isEmpty(desc)) {
			desc = port.getModelDescription();
		}
		this.returnType = new DataModel(port.getModelName());
		returnType.description = desc;
	}

	public void setReturnVoid() {
		this.returnType = VOID;
	}

	public void setMethodName(String name) {
		this.methodName = name;
	}

	public void addArgumentClass(Annotations annotation, String className, String name, String description) {
		NormalClass type = new NormalClass(className);
		arguments.add(new Argument(annotation, type, name, description));
	}

	public void addArgumentModel(OpePort port) {
		if (port == null) {
			return;
		}
		String name = port.getName();
		addArgumentModel(port, name);
	}

	public void addArgumentModel(OpePort port, String name) {
		if (port == null) {
			return;
		}
		Annotations annotation = getAnnotation(null, port);
		String modelName = port.getModelName();
		addArgumentModel(annotation, modelName, name, port.getDescription());
	}

	public void addArgumentModel(Annotations annotation, String modelName, String name, String description) {
		DataModel type = new DataModel(modelName);
		arguments.add(new Argument(annotation, type, name, description));
	}

	public void addArgumentModelList(OpePort port) {
		if (port == null) {
			return;
		}
		Annotations annotation = getAnnotation(null, port);
		TypedClass type = new TypedClass("java.util.List", new DataModel(port.getModelName()));
		String name = port.getName();
		arguments.add(new Argument(annotation, type, name, port.getDescription()));
	}

	public void addArgumentModelResult(OpePort port) {
		if (port == null) {
			return;
		}
		Annotations annotation = getAnnotation(null, port);
		TypedClass type = new TypedClass("com.asakusafw.runtime.core.Result", new DataModel(port.getModelName()));
		String name = port.getName();
		arguments.add(new Argument(annotation, type, name, port.getDescription()));
	}

	private Annotations getAnnotation(Annotations annotation, HasAttributeNode port) {
		List<Attribute> attrs = port.getAttributeList();
		if (attrs.isEmpty()) {
			return annotation;
		}
		if (annotation == null) {
			annotation = new Annotations();
		}

		for (Attribute attr : attrs) {
			List<String> values = attr.getValue();
			if (values.isEmpty()) {
				continue;
			}

			String name = attr.getParameterName();

			StringBuilder sb = new StringBuilder(64);
			if (values.size() >= 2) {
				sb.append("{ ");
			}
			boolean first = true;
			for (String s : values) {
				if (first) {
					first = false;
				} else {
					sb.append(", ");
				}
				if ("java.lang.String".equals(attr.getValueType())) {
					sb.append("\"");
					sb.append(s);
					sb.append("\"");
				} else if ("char".equals(attr.getValueType())) {
					sb.append("'");
					sb.append(s);
					sb.append("'");
				} else {
					sb.append(s);
				}
			}
			if (values.size() >= 2) {
				sb.append(" }");
			}

			String value = sb.toString();
			String arg = StringUtil.isEmpty(name) ? value : String.format("%s = %s", name, value);
			annotation.add(attr.getAnnotationName(), arg);
		}

		return annotation;
	}

	public static class Argument {
		public Annotations annotation;
		public AbstractType type;
		public String name;
		public String description;

		public Argument(Annotations annotation, AbstractType type, String name, String description) {
			this.annotation = annotation;
			this.type = type;
			this.name = name;
			this.description = description;
		}
	}

	public String getMethodName() {
		return methodName;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public String toSourceString(TypeResolver resolver) {
		StringBuilder sb = new StringBuilder(256);
		javadoc(sb);
		if (annotation != null) {
			sb.append(annotation.resolvedType(resolver, "\n"));
		}
		sb.append("public ");
		if (isAbstract) {
			sb.append("abstract ");
		}
		sb.append(StringUtil.get(returnType.resolvedType(resolver), "UNDEFINED"));
		sb.append(" ");
		sb.append(methodName);
		sb.append("(");
		for (Argument arg : arguments) {
			if (sb.charAt(sb.length() - 1) != '(') {
				sb.append(", ");
			}
			if (arguments.size() > 1) {
				sb.append("\n    ");
			}
			if (arg.annotation != null) {
				sb.append(arg.annotation.resolvedType(resolver, " "));
			}
			sb.append(StringUtil.get(arg.type.resolvedType(resolver), "UNDEFINED"));
			sb.append(" ");
			sb.append(arg.name);
		}
		sb.append(")");
		if (isAbstract) {
			sb.append(";");
		} else {
			if (returnType.isVoid()) {
				sb.append(" {\n");
				sb.append("\t//TODO generated by Toad Editor\n");
				sb.append("}");
			} else {
				sb.append(" {\n");
				sb.append("\treturn null; //TODO generated by Toad Editor\n");
				sb.append("}");
			}
		}
		return sb.toString();
	}

	private void javadoc(StringBuilder sb) {
		sb.append("/**\n");
		for (String text : javadocList) {
			sb.append(" * ");
			sb.append(text);
			sb.append("\n");
		}
		for (Argument arg : arguments) {
			sb.append(" * @param ");
			sb.append(arg.name);
			sb.append(" ");
			sb.append(StringUtil.nonNull(arg.description));
			sb.append("\n");
		}
		if (!returnType.isVoid()) {
			sb.append(" * @return ");
			sb.append(StringUtil.nonNull(returnType.description));
			sb.append("\n");
		}
		sb.append(" */\n");
	}

	@Override
	public String toString() {
		return toSourceString(SIMPLE_RESOLVER);
	}

	public static interface TypeResolver {
		public String resolve(String type);
	}

	private static final TypeResolver SIMPLE_RESOLVER = new TypeResolver() {
		@Override
		public String resolve(String type) {
			return StringUtil.getSimpleName(type);
		}
	};
}
