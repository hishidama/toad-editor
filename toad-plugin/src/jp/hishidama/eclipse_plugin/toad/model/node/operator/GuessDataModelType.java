package jp.hishidama.eclipse_plugin.toad.model.node.operator;

import jp.hishidama.eclipse_plugin.util.StringUtil;
import jp.hishidama.xtext.dmdl_editor.ui.wizard.page.DataModelType;

public class GuessDataModelType {

	private DataModelType type;
	private String name;
	private String first;
	private String second;

	public GuessDataModelType(DataModelType type, String modelName) {
		this.type = type;
		this.name = modelName;
	}

	public DataModelType getType() {
		return type;
	}

	public void setFirst(String modelName) {
		this.first = modelName;
	}

	public String getFirst() {
		return first;
	}

	public void setSecond(String modelName) {
		this.second = modelName;
	}

	public String getSecond() {
		return second;
	}

	/**
	 * 決定されているかどうか.
	 * 
	 * @return true: 決定
	 */
	public boolean isDecision() {
		if (StringUtil.isEmpty(name)) {
			return false;
		}
		switch (type) {
		case SUMMARIZED:
			if (StringUtil.nonEmpty(first)) {
				return true;
			}
			return false;
		case JOINED:
			if (StringUtil.nonEmpty(first) && StringUtil.nonEmpty(second)) {
				return true;
			}
			return false;
		default:
			return true;
		}
	}

	public static boolean isSourceDecision(GuessDataModelType type) {
		if (type == null) {
			return false;
		}
		switch (type.type) {
		case SUMMARIZED:
			if (StringUtil.nonEmpty(type.first)) {
				return true;
			}
			return false;
		case JOINED:
			if (StringUtil.nonEmpty(type.first) && StringUtil.nonEmpty(type.second)) {
				return true;
			}
			return false;
		default:
			return false;
		}
	}

	public int getGuessLevel() {
		int n = 0;
		if (StringUtil.nonEmpty(name)) {
			n++;
		}
		if (StringUtil.nonEmpty(first)) {
			n++;
		}
		if (StringUtil.nonEmpty(second)) {
			n++;
		}
		return n;
	}
}
