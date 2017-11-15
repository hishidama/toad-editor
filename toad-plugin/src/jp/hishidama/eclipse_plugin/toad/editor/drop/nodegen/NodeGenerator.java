package jp.hishidama.eclipse_plugin.toad.editor.drop.nodegen;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.hishidama.eclipse_plugin.toad.clazz.JavaDelegator.Parameter.Value;
import jp.hishidama.eclipse_plugin.toad.model.node.Attribute;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OpeParameter;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;
import jp.hishidama.eclipse_plugin.toad.model.node.port.OpePort;
import jp.hishidama.eclipse_plugin.util.StringUtil;
import jp.hishidama.xtext.dmdl_editor.dmdl.ModelDefinition;
import jp.hishidama.xtext.dmdl_editor.dmdl.ModelUiUtil;
import jp.hishidama.xtext.dmdl_editor.dmdl.ModelUtil;

import org.eclipse.core.resources.IProject;

public abstract class NodeGenerator {

	protected final IProject project;

	public NodeGenerator(IProject project) {
		this.project = project;
	}

	protected final OpePort createOpePort(boolean in, List<OpePort> list, String role, String name,
			String modelClassName, Map<String, Value> attributes) {
		String modelName;
		String modelDescription;
		ModelDefinition model = ModelUiUtil.findModelByClass(project, modelClassName);
		if (model != null) {
			modelName = model.getName();
			modelDescription = ModelUtil.getDecodedDescription(model);
		} else {
			modelName = StringUtil.toSnakeCase(StringUtil.getSimpleName(modelClassName));
			modelDescription = null;
		}

		return createOpePort(in, list, role, name, modelName, modelDescription, attributes);
	}

	protected final OpePort createOpePort(boolean in, List<OpePort> list, String role, String name, String modelName,
			String modelDescription, Map<String, Value> attributes) {
		OpePort port = new OpePort();
		port.setIn(in);
		port.setId(newPortId(port, in, name));
		port.setRole(role);
		port.setName(name);
		port.setModelName(modelName);
		port.setModelDescription(modelDescription);
		if (in && attributes != null) {
			for (Entry<String, Value> entry : attributes.entrySet()) {
				String[] ss = entry.getKey().split("#");
				String annotationName = ss[0];
				String parameterName = ss.length >= 2 ? ss[1] : "";
				Value value = entry.getValue();
				Attribute attr = new Attribute(annotationName, parameterName, value.type);
				attr.setValue(value.value);
				port.setAttribute(attr);
			}
		}
		list.add(port);
		return port;
	}

	protected abstract int newPortId(OpePort port, boolean in, String name);

	protected final void createOpeParameter(OperatorNode node, String description, String name, String className) {
		OpeParameter param = new OpeParameter();
		param.setDescription(description);
		param.setName(name);
		param.setClassName(className);
		node.addParameter(param);
	}
}
