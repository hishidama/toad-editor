package jp.hishidama.eclipse_plugin.toad.extension;

import java.util.Arrays;
import java.util.List;

import jp.hishidama.eclipse_plugin.jdt.util.TypeUtil;
import jp.hishidama.eclipse_plugin.toad.model.node.datafile.DataFileNode;
import jp.hishidama.eclipse_plugin.util.StringUtil;

import org.eclipse.jdt.core.IType;

public abstract class ToadImporterExporterProperty {
	public final String getName() {
		return String.format("%s %s", getBaseName(), isImporter() ? "Importer" : "Exporter");
	}

	public abstract String getBaseName();

	public abstract boolean isImporter();

	public abstract boolean acceptable(IType type);

	public abstract List<PorterProperty> getProperties();

	public static class PorterProperty {
		public String methodName;
		public String description;
		public boolean required;
		public List<String> candidate;
	}

	protected static PorterProperty create(String methodName, String description, boolean required) {
		PorterProperty p = new PorterProperty();
		p.methodName = methodName;
		p.description = description;
		p.required = required;
		return p;
	}

	protected static PorterProperty create(String methodName, String description, boolean required, String... candidate) {
		PorterProperty p = new PorterProperty();
		p.methodName = methodName;
		p.description = description;
		p.required = required;
		p.candidate = Arrays.asList(candidate);
		return p;
	}

	protected static PorterProperty createDataSize() {
		return create("getDataSize", "データサイズ", true, "UNKOWN", "TINY", "SMALL", "LARGE");
	}

	protected boolean isExtends(IType type, String name) {
		return TypeUtil.isExtends(type, name);
	}

	protected boolean isImplements(IType type, String name) {
		return TypeUtil.isImplements(type, name);
	}

	public abstract void generateClass(ClassGenerator generator, DataFileNode node, StringBuilder sb);

	protected final String getSuperClassName(ClassGenerator generator, String modelName, String packagePart,
			String suffix) {
		String modelClassName = generator.getModelClassName(modelName);

		StringBuilder sb = new StringBuilder(modelClassName.length() + 32);
		String[] ss = modelClassName.split("\\.");
		for (int i = 0; i < ss.length - 2; i++) {
			sb.append(ss[i]);
			sb.append(".");
		}
		sb.append(packagePart);
		sb.append(".Abstract");
		sb.append(ss[ss.length - 1]);
		sb.append(suffix);
		return sb.toString();
	}

	protected void appendModelType(StringBuilder sb, DataFileNode node, ClassGenerator generator) {
		String methodName = "getModelType";
		String rtype = "Class<?>";
		String rvalue = generator.getCachedModelClassName(node.getModelName()) + ".class";
		appendGetterMethod(sb, methodName, rtype, rvalue);
	}

	protected void appendDataSize(StringBuilder sb, DataFileNode node, ClassGenerator generator) {
		String methodName = "getDataSize";
		String rtype = "DataSize";
		String rvalue = rtype + "." + node.getProperty("getDataSize");
		appendGetterMethod(sb, methodName, rtype, rvalue);
	}

	protected static void appendGetterListMethod(StringBuilder sb, String methodName, String value,
			ClassGenerator generator) {
		if (StringUtil.isEmpty(value)) {
			return;
		}

		String rtype = generator.getCachedClassName("java.util.List") + "<String>";

		String[] ss = value.split(",");
		StringBuilder vb = new StringBuilder(value.length() + 64);
		vb.append(generator.getCachedClassName("java.util.Arrays"));
		vb.append(".asList(");
		for (String s : ss) {
			if (vb.charAt(vb.length() - 1) != '(') {
				vb.append(", ");
			}
			vb.append("\"");
			vb.append(s.trim());
			vb.append("\"");
		}
		vb.append(")");

		appendGetterMethod(sb, methodName, rtype, vb.toString());
	}

	protected static void appendGetterMethod(StringBuilder sb, String methodName, String rtype, String rvalue) {
		sb.append("\n");
		sb.append("\t@Override\n");
		sb.append("\tpublic ");
		sb.append(rtype);
		sb.append(" ");
		sb.append(methodName);
		sb.append("() {\n");
		sb.append("\t\treturn ");
		sb.append(rvalue);
		sb.append(";\n");
		sb.append("\t}\n");
	}
}
