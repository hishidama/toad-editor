package jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.hishidama.eclipse_plugin.toad.model.property.datamodel.HasDataModelNode;
import jp.hishidama.eclipse_plugin.util.StringUtil;
import jp.hishidama.xtext.dmdl_editor.util.DMDLStringUtil;

import org.eclipse.core.resources.IProject;

public abstract class DslClassGenerator {

	protected IProject project;
	protected String className;

	public DslClassGenerator(IProject project, String className) {
		this.project = project;
		this.className = className;
		if (StringUtil.isEmpty(className)) {
			throw new IllegalArgumentException(MessageFormat.format("className={0}", className));
		}
	}

	public String generate() {
		defaultImport();

		StringBuilder body = new StringBuilder(1024);
		appendClass(body);

		StringBuilder sb = new StringBuilder(body.length() + 512);
		appendPackage(sb);
		appendImport(sb);
		appendClassJavadoc(sb);
		appendClassAnnotation(sb);
		sb.append(body);
		return sb.toString();
	}

	private void appendPackage(StringBuilder sb) {
		int n = className.lastIndexOf('.');
		if (n >= 0) {
			sb.append("package ");
			sb.append(className.substring(0, n));
			sb.append(";\n\n");
		}
	}

	protected abstract void defaultImport();

	private void appendImport(StringBuilder sb) {
		List<String> list = new ArrayList<String>(classNameMap.keySet());
		Collections.sort(list);
		for (String s : list) {
			if (s.startsWith("java.lang.")) {
				continue;
			}
			if (!s.isEmpty()) {
				sb.append("import ");
				sb.append(s);
				sb.append(";\n");
			}
		}
		sb.append("\n");
	}

	protected abstract void appendClassJavadoc(StringBuilder sb);

	protected abstract void appendClassAnnotation(StringBuilder sb);

	protected abstract void appendClass(StringBuilder sb);

	//
	private Map<String, String> classNameMap = new HashMap<String, String>();
	private Set<String> simpleNameSet = new HashSet<String>();

	public String getCachedClassName(String className) {
		if (className == null) {
			className = "";
		}
		String name = classNameMap.get(className);
		if (name != null) {
			return name;
		}
		String sname = StringUtil.getSimpleName(className);
		if (simpleNameSet.contains(sname)) {
			classNameMap.put(className, className);
			return className;
		} else {
			classNameMap.put(className, sname);
			simpleNameSet.add(sname);
			return sname;
		}
	}

	//
	private Map<String, Integer> identifiedNameMap = new HashMap<String, Integer>();

	protected String getIdentifiedName(String name) {
		Integer id = identifiedNameMap.get(name);
		if (id == null) {
			identifiedNameMap.put(name, 1);
			return name;
		}

		for (int newId = id + 1;; newId++) {
			String newName = name + newId;
			if (!identifiedNameMap.containsKey(newName)) {
				identifiedNameMap.put(name, newId);
				return newName;
			}
		}
	}

	protected String getModelClassName(HasDataModelNode node) {
		String modelName = node.getModelName();
		return DMDLStringUtil.getModelClass(project, modelName);
	}
}
