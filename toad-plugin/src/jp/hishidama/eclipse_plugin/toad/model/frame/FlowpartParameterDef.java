package jp.hishidama.eclipse_plugin.toad.model.frame;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

public class FlowpartParameterDef implements Serializable {
	private static final long serialVersionUID = -2313700564346005220L;

	@Expose
	private String name;
	@Expose
	private String className;
	@Expose
	private String description;

	private FlowpartFrameNode parent;

	public FlowpartParameterDef() {
	}

	public FlowpartParameterDef(FlowpartParameterDef from) {
		this.name = from.name;
		this.className = from.className;
		this.description = from.description;
	}

	public void setParent(FlowpartFrameNode frame) {
		this.parent = frame;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String desc) {
		this.description = desc;
		if (parent != null) {
			parent.fireParameterChange();
		}
	}
}
