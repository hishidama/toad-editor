package jp.hishidama.eclipse_plugin.toad.editor.drop.nodegen;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jp.hishidama.eclipse_plugin.toad.clazz.JavaDelegator.Parameter;
import jp.hishidama.eclipse_plugin.toad.clazz.JavaDelegator.Parameter.Value;
import jp.hishidama.eclipse_plugin.toad.clazz.JavadocClass;
import jp.hishidama.eclipse_plugin.toad.clazz.OperatorMethod;
import jp.hishidama.eclipse_plugin.toad.clazz.UserOperatorType;
import jp.hishidama.eclipse_plugin.toad.internal.LogUtil;
import jp.hishidama.eclipse_plugin.toad.model.node.Attribute;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.delegator.UserConvert;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.delegator.UserFold;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.delegator.UserLogging;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.delegator.UserMasterCheck;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.delegator.UserMasterJoin;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.delegator.UserMasterJoinUpdate;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.delegator.UserSummarize;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.delegator.UserUpdate;
import jp.hishidama.eclipse_plugin.toad.model.node.port.OpePort;
import jp.hishidama.eclipse_plugin.util.StringUtil;
import jp.hishidama.eclipse_plugin.util.ToadLayoutUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

public abstract class OperatorNodeGenerator extends NodeGenerator {

	public OperatorNodeGenerator(IProject project) {
		super(project);
	}

	public OperatorNode createOperatorNode(OperatorMethod operator, int id) {
		OperatorNode node = new OperatorNode();
		node.setId(id);
		UserOperatorType type = operator.getOperatorType();
		node.setType("@" + type.name());
		String desc = operator.getName();
		node.setDescription(desc);
		node.setClassName(operator.getClassName());
		node.setMethodName(operator.getName());
		List<Attribute> attributeList = operator.getAttributes();

		List<OpePort> inList = new ArrayList<OpePort>();
		List<OpePort> outList = new ArrayList<OpePort>();
		switch (type) {
		case Branch:
			ioBranch(node, operator, inList, outList);
			break;
		case CoGroup:
		case GroupSort:
			ioCoGroup(node, operator, inList, outList);
			break;
		case Convert:
			ioConvert(node, operator, inList, outList, attributeList);
			break;
		case Extract:
			ioExtract(node, operator, inList, outList);
			break;
		case Fold:
			ioFold(node, operator, inList, outList, attributeList);
			break;
		case Logging:
			ioLogging(node, operator, inList, outList, attributeList);
			break;
		case MasterBranch:
			ioMasterBranch(node, operator, inList, outList);
			break;
		case MasterCheck:
		case MasterJoin:
		case MasterJoinUpdate:
			ioMasterCheck(node, operator, inList, outList, attributeList);
			break;
		case Split:
			ioSplit(node, operator, inList, outList);
			break;
		case Summarize:
			ioSummarize(node, operator, inList, outList, attributeList);
			break;
		case Update:
			ioUpdate(node, operator, inList, outList, attributeList);
			break;
		default:
			throw new UnsupportedOperationException("type=" + type);
		}
		node.setAttributeList(attributeList);

		ToadLayoutUtil.addPorts(node, inList, outList);
		node.getDelegate().setDescription(operator.getJavadoc());

		return node;
	}

	private void ioBranch(OperatorNode node, OperatorMethod operator, List<OpePort> inList, List<OpePort> outList) {
		String modelClassName = createSimplePort(true, node, operator, 0, inList, "", "in");

		createBranchOutPort(node, operator, outList, modelClassName);

		createValueParameters(node, operator, 1);
	}

	private void ioMasterBranch(OperatorNode node, OperatorMethod operator, List<OpePort> inList, List<OpePort> outList) {
		createSimplePort(true, node, operator, 0, inList, "master", "master");
		String txClassName = createSimplePort(true, node, operator, 1, inList, "tx", "tx");

		createBranchOutPort(node, operator, outList, txClassName);

		createValueParameters(node, operator, 2);
	}

	private void createBranchOutPort(OperatorNode node, OperatorMethod operator, List<OpePort> outList,
			String modelClassName) {
		try {
			String rtype = operator.getReturnType();
			node.setProperty(OperatorNode.KEY_RETURN_ENUM_NAME, rtype);

			IJavaProject javaProject = JavaCore.create(project);
			IType type = javaProject.findType(rtype);
			if (type != null) {
				JavadocClass javadoc = JavadocClass.getJavadoc(type);
				if (javadoc != null) {
					node.setProperty(OperatorNode.KEY_RETURN_DESCRIPTION, javadoc.getTitle());
				}

				for (IField field : type.getFields()) {
					if (field.isEnumConstant()) {
						String name = StringUtil.toLowerCamelCase(field.getElementName().toLowerCase());
						createOpePort(false, outList, "", name, modelClassName, null);
					}
				}
			}
		} catch (JavaModelException e) {
			LogUtil.logWarn("find enum error.", e);
		}
	}

	private void ioCoGroup(OperatorNode node, OperatorMethod operator, List<OpePort> inList, List<OpePort> outList) {
		for (Parameter param : operator.getParameters()) {
			if ("java.util.List".equals(param.className)) {
				createOpePort(true, inList, "", param.name, param.typeParameter, param.attributes);
			} else if ("com.asakusafw.runtime.core.Result".equals(param.className)) {
				createOpePort(false, outList, "", param.name, param.typeParameter, param.attributes);
			} else {
				createOpeParameter(node, null, param.name, param.className);
			}
		}
	}

	private void ioConvert(OperatorNode node, OperatorMethod operator, List<OpePort> inList, List<OpePort> outList,
			List<Attribute> attributeList) {
		String modelClassName = createSimplePort(true, node, operator, 0, inList, "", "in");

		String out = getNameFromAnnotation(attributeList, UserConvert.CLASS, UserConvert.CONVERTED_PORT, "out");
		String original = getNameFromAnnotation(attributeList, UserConvert.CLASS, UserConvert.ORIGINAL_PORT, "original");
		createOpePort(false, outList, "out", out, operator.getReturnType(), null);
		createOpePort(false, outList, "original", original, modelClassName, null);

		createValueParameters(node, operator, 1);
	}

	private void ioExtract(OperatorNode node, OperatorMethod operator, List<OpePort> inList, List<OpePort> outList) {
		createSimplePort(true, node, operator, 0, inList, "", "in");

		List<Parameter> params = operator.getParameters();
		for (int i = 1; i < params.size(); i++) {
			Parameter param = params.get(i);
			if ("com.asakusafw.runtime.core.Result".equals(param.className)) {
				createOpePort(false, outList, "", param.name, param.typeParameter, param.attributes);
			} else {
				createOpeParameter(node, null, param.name, param.className);
			}
		}
	}

	private void ioFold(OperatorNode node, OperatorMethod operator, List<OpePort> inList, List<OpePort> outList,
			List<Attribute> attributeList) {
		createSimplePort(true, node, operator, 0, inList, "", "in", "in");

		String out = getNameFromAnnotation(attributeList, UserFold.CLASS, UserFold.OUTPUT_PORT, "out");
		createSimplePort(false, node, operator, 1, outList, "", out, out);

		createValueParameters(node, operator, 2);
	}

	private void ioLogging(OperatorNode node, OperatorMethod operator, List<OpePort> inList, List<OpePort> outList,
			List<Attribute> attributeList) {
		String modelClassName = createSimplePort(true, node, operator, 0, inList, "", "in");

		String out = getNameFromAnnotation(attributeList, UserLogging.CLASS, UserLogging.OUTPUT_PORT, "out");
		createOpePort(false, outList, "", out, modelClassName, null);

		createValueParameters(node, operator, 1);
	}

	private void ioMasterCheck(OperatorNode node, OperatorMethod operator, List<OpePort> inList, List<OpePort> outList,
			List<Attribute> attributeList) {
		createSimplePort(true, node, operator, 0, inList, "master", "master");
		String txClassName = createSimplePort(true, node, operator, 1, inList, "tx", "tx");

		String role, ftype, found, missed;
		switch (operator.getOperatorType()) {
		default:
			role = "found";
			ftype = txClassName;
			found = getNameFromAnnotation(attributeList, UserMasterCheck.CLASS, UserMasterCheck.FOUND_PORT, "found");
			missed = getNameFromAnnotation(attributeList, UserMasterCheck.CLASS, UserMasterCheck.MISSED_PORT, "missed");
			break;
		case MasterJoin:
			role = "joined";
			ftype = operator.getReturnType();
			found = getNameFromAnnotation(attributeList, UserMasterJoin.CLASS, UserMasterJoin.JOINED_PORT, "joined");
			missed = getNameFromAnnotation(attributeList, UserMasterJoin.CLASS, UserMasterJoin.MISSED_PORT, "missed");
			break;
		case MasterJoinUpdate:
			role = "updated";
			ftype = txClassName;
			found = getNameFromAnnotation(attributeList, UserMasterJoinUpdate.CLASS, UserMasterJoinUpdate.UPDATED_PORT,
					"updated");
			missed = getNameFromAnnotation(attributeList, UserMasterJoinUpdate.CLASS, UserMasterJoinUpdate.MISSED_PORT,
					"missed");
			break;
		}
		createOpePort(false, outList, role, found, ftype, null);
		createOpePort(false, outList, "missed", missed, txClassName, null);

		switch (operator.getOperatorType()) {
		case MasterJoinUpdate:
			createValueParameters(node, operator, 2);
			break;
		default:
			break;
		}
	}

	private void ioSplit(OperatorNode node, OperatorMethod operator, List<OpePort> inList, List<OpePort> outList) {
		createSimplePort(true, node, operator, 0, inList, "", "in");

		createTypeParameterPort(node, operator, 1, outList, "left");
		createTypeParameterPort(node, operator, 2, outList, "right");
	}

	private void ioSummarize(OperatorNode node, OperatorMethod operator, List<OpePort> inList, List<OpePort> outList,
			List<Attribute> attributeList) {
		createSimplePort(true, node, operator, 0, inList, "", "in");

		String out = getNameFromAnnotation(attributeList, UserSummarize.CLASS, UserSummarize.SUMMARIZED_PORT, "out");
		createOpePort(false, outList, "", out, operator.getReturnType(), null);
	}

	private void ioUpdate(OperatorNode node, OperatorMethod operator, List<OpePort> inList, List<OpePort> outList,
			List<Attribute> attributeList) {
		String modelClassName = createSimplePort(true, node, operator, 0, inList, "", "in");

		String out = getNameFromAnnotation(attributeList, UserUpdate.CLASS, UserUpdate.OUTPUT_PORT, "out");
		createOpePort(false, outList, "", out, modelClassName, null);

		createValueParameters(node, operator, 1);
	}

	private String getNameFromAnnotation(List<Attribute> attributeList, String annotationName, String memberName,
			String defaultName) {
		for (Attribute attr : attributeList) {
			if (annotationName.equals(attr.getAnnotationName()) && memberName.equals(attr.getParameterName())) {
				attributeList.remove(attr);
				return attr.getValue().get(0);
			}
		}
		return defaultName;
	}

	private String createSimplePort(boolean in, OperatorNode node, OperatorMethod operator, int index,
			List<OpePort> list, String role, String defaultName) {
		return createSimplePort(in, node, operator, index, list, role, defaultName, null);
	}

	private String createSimplePort(boolean in, OperatorNode node, OperatorMethod operator, int index,
			List<OpePort> list, String role, String defaultName, String forceName) {
		String name, className;
		Map<String, Value> attributes;
		List<Parameter> params = operator.getParameters();
		if (index < params.size()) {
			Parameter param = params.get(index);
			name = param.name;
			className = param.className;
			attributes = param.attributes;
		} else {
			name = defaultName;
			className = null;
			attributes = new LinkedHashMap<String, Value>();
		}
		if (forceName != null) {
			name = forceName;
		}

		List<Attribute> annotation = node.getDefaultPortAnnotation();
		if (annotation != null) {
			for (Attribute a : annotation) {
				String key = a.getAnnotationName() + "#" + a.getParameterName();
				if (!attributes.containsKey(key)) {
					attributes.put(key, new Value(a.getValueType()));
				}
			}
		}

		createOpePort(in, list, role, name, className, attributes);

		return className;
	}

	private String createTypeParameterPort(OperatorNode node, OperatorMethod operator, int index, List<OpePort> list,
			String defaultName) {
		List<Parameter> params = operator.getParameters();
		if (index < params.size()) {
			Parameter param = params.get(index);
			createOpePort(false, list, "", param.name, param.typeParameter, param.attributes);
			return param.typeParameter;
		} else {
			createOpePort(false, list, "", defaultName, null, null);
			return null;
		}
	}

	private void createValueParameters(OperatorNode node, OperatorMethod operator, int index) {
		List<Parameter> params = operator.getParameters();
		for (int i = index; i < params.size(); i++) {
			Parameter param = params.get(i);
			createOpeParameter(node, null, param.name, param.className);
		}
	}
}
