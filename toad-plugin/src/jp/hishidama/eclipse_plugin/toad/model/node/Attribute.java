package jp.hishidama.eclipse_plugin.toad.model.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.hishidama.eclipse_plugin.toad.model.property.attribute.HasAttributeNode;

import com.google.gson.annotations.Expose;

public class Attribute implements Serializable {
	private static final long serialVersionUID = -4382542466540956394L;

	@Expose
	private String annotationName;
	@Expose
	private String parameterName;
	@Expose
	private String valueType;
	@Expose
	private List<String> valueList;

	private HasAttributeNode node;

	public Attribute(String annotationName, String parameterName, String type) {
		setAnnotationName(annotationName);
		setParameterName(parameterName);
		setValueType(type);
	}

	public Attribute(Attribute a) {
		this.annotationName = a.annotationName;
		this.parameterName = a.parameterName;
		this.valueType = a.valueType;
		if (a.valueList != null) {
			this.valueList = new ArrayList<String>(a.valueList);
		}
	}

	public void setParent(HasAttributeNode node) {
		this.node = node;
	}

	public String getAnnotationName() {
		return annotationName;
	}

	public void setAnnotationName(String name) {
		this.annotationName = name;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String name) {
		this.parameterName = name;
	}

	public String getValueType() {
		return valueType;
	}

	public void setValueType(String type) {
		this.valueType = type;
	}

	public List<String> getValue() {
		if (valueList == null) {
			return Collections.emptyList();
		}
		return valueList;
	}

	public void addValue(String value) {
		if (valueList == null) {
			valueList = new ArrayList<String>();
		}
		valueList.add(value);
	}

	public void setValue(List<String> value) {
		if (value == null) {
			valueList = null;
		} else {
			valueList = new ArrayList<String>(value);
		}
		if (node != null) {
			node.fireAttributeChange();
		}
	}

	@Override
	public String toString() {
		return String.format("@%s(%s=%s : %s)", annotationName, parameterName, valueList, valueType);
	}
}
