package jp.hishidama.eclipse_plugin.toad.model.node.operator;

import java.io.Serializable;

import jp.hishidama.eclipse_plugin.util.StringUtil;

import com.google.gson.annotations.Expose;

public class OpeParameter implements Serializable {
	private static final long serialVersionUID = -390403280746396283L;

	@Expose
	private String name;
	@Expose
	private String className;
	@Expose
	private String value;
	@Expose
	private String description;

	private OperatorNode parent;

	public OpeParameter() {
	}

	public OpeParameter(OpeParameter from) {
		this.name = from.name;
		this.className = from.className;
		this.value = from.value;
		this.description = from.description;
	}

	public void setParent(OperatorNode node) {
		this.parent = node;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		if (parent != null) {
			parent.fireParameterChange();
		}
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
		if (parent != null) {
			parent.fireParameterChange();
		}
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
		if (parent != null) {
			parent.fireParameterChange();
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String desc) {
		this.description = desc;
		if (parent != null) {
			parent.fireParameterChange();
		}
	}

	@Override
	public int hashCode() {
		int h = 0;
		h ^= (name != null) ? name.hashCode() : 0;
		h ^= (className != null) ? className.hashCode() : 0;
		h ^= (value != null) ? value.hashCode() : 0;
		h ^= (description != null) ? description.hashCode() : 0;
		return h;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof OpeParameter)) {
			return false;
		}

		OpeParameter that = (OpeParameter) obj;
		if (!StringUtil.equals(name, that.name)) {
			return false;
		}
		if (!StringUtil.equals(className, that.className)) {
			return false;
		}
		if (!StringUtil.equals(value, that.value)) {
			return false;
		}
		if (!StringUtil.equals(description, that.description)) {
			return false;
		}
		return true;
	}
}
