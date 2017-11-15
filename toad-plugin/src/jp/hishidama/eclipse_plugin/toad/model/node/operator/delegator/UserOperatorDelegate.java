package jp.hishidama.eclipse_plugin.toad.model.node.operator.delegator;

import java.util.List;

import jp.hishidama.eclipse_plugin.toad.clazz.JavadocClass;
import jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen.OperatorMethodGenerator;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OpeParameter;
import jp.hishidama.eclipse_plugin.toad.model.node.port.OpePort;

public abstract class UserOperatorDelegate extends OperatorDelegate {

	protected UserOperatorDelegate(String methodName, String description, int inMin, int inMax, int outMin, int outMax) {
		super(methodName, description, inMin, inMax, outMin, outMax);
	}

	@Override
	public void setDescription(JavadocClass javadoc) {
		String title = javadoc.getTitle();
		if (title != null) {
			node.setDescription(title);
		}
		node.setMemo(javadoc.getMemo());
		for (OpePort port : node.getPorts()) {
			setPortDescription(port, javadoc.getParamValue(port.getName()));
		}
		for (OpeParameter param : node.getParameterList()) {
			String desc = javadoc.getParamValue(param.getName());
			param.setDescription((desc != null) ? desc.trim() : null);
		}
	}

	protected final void setPortDescriptionFromParam(boolean in, int index, JavadocClass javadoc) {
		OpePort port = getPort(in, index);
		if (port != null) {
			String desc = javadoc.getParamValue(port.getName());
			setPortDescription(port, desc);
		}
	}

	protected final void setPortDescriptionFromReturn(boolean in, int index, JavadocClass javadoc) {
		OpePort port = getPort(in, index);
		if (port != null) {
			String desc = javadoc.getReturnValue();
			setPortDescription(port, desc);
		}
	}

	protected final void setPortDescription(OpePort port, String description) {
		port.setDescription((description != null) ? description.trim() : null);
	}

	protected final OpePort getPort(boolean in, int index) {
		List<OpePort> ports = node.getPorts(in);
		if (index < ports.size()) {
			OpePort port = ports.get(index);
			return port;
		}
		return null;
	}

	protected final OpePort getPort(boolean in, String role) {
		List<OpePort> ports = node.getPorts(in);
		for (OpePort port : ports) {
			if (role.equals(port.getRole())) {
				return port;
			}
		}
		return null;
	}

	protected final void addPortNameAnnotation(OperatorMethodGenerator gen, int index, String annotationName,
			String parameterName, String defaultPortName) {
		OpePort port = getPort(false, index);
		addPortNameAnnotation(gen, port, annotationName, parameterName, defaultPortName);
	}

	protected final void addPortNameAnnotation(OperatorMethodGenerator gen, String role, String annotationName,
			String parameterName, String defaultPortName) {
		OpePort port = getPort(false, role);
		addPortNameAnnotation(gen, port, annotationName, parameterName, defaultPortName);
	}

	private void addPortNameAnnotation(OperatorMethodGenerator gen, OpePort port, String annotationName,
			String parameterName, String defaultPortName) {
		if (port == null) {
			return;
		}
		String portName = port.getName();
		if (defaultPortName.equals(portName)) {
			return;
		}

		String arg = String.format("%s = \"%s\"", parameterName, portName);
		gen.addAnnotation(annotationName, arg);
	}
}
