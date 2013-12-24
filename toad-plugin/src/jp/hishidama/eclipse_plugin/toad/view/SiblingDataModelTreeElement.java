package jp.hishidama.eclipse_plugin.toad.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.GuessDataModelType;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;
import jp.hishidama.eclipse_plugin.toad.model.node.port.OpePort;
import jp.hishidama.eclipse_plugin.toad.model.property.datamodel.HasDataModelNode;

public final class SiblingDataModelTreeElement {
	private final HasDataModelNode modelNode;
	private final SiblingDataModelTreeElement parent;
	private final List<SiblingDataModelTreeElement> children = new ArrayList<SiblingDataModelTreeElement>();
	private boolean checked = true;

	public SiblingDataModelTreeElement(HasDataModelNode modelNode, SiblingDataModelTreeElement parent) {
		this.modelNode = modelNode;
		this.parent = parent;
	}

	public SiblingDataModelTreeElement add(HasDataModelNode modelNode) {
		SiblingDataModelTreeElement c = new SiblingDataModelTreeElement(modelNode, this);
		children.add(c);
		return c;
	}

	public HasDataModelNode getDataModelNode() {
		return modelNode;
	}

	public SiblingDataModelTreeElement getParent() {
		return parent;
	}

	public List<SiblingDataModelTreeElement> getChildren() {
		return children;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public boolean getChecked() {
		return checked;
	}

	/**
	 * データモデルの種類を推測する.
	 * 
	 * @return データモデルの種類
	 */
	public GuessDataModelType guessDataModelType() {
		List<GuessDataModelType> guess = null;
		if (modelNode instanceof OpePort) {
			OpePort port = (OpePort) modelNode;
			NodeElement parent = port.getParent();
			if (parent instanceof OperatorNode) {
				OperatorNode operator = (OperatorNode) parent;
				GuessDataModelType type = operator.guessDataModelType(port);
				if (type != null) {
					if (type.isDecision()) {
						return type;
					}
					guess = new ArrayList<GuessDataModelType>();
					guess.add(type);
				}
			}
		}

		for (SiblingDataModelTreeElement c : children) {
			GuessDataModelType type = c.guessDataModelType();
			if (type != null) {
				if (type.isDecision()) {
					return type;
				}
				if (guess == null) {
					guess = new ArrayList<GuessDataModelType>();
				}
				guess.add(type);
			}
		}

		if (guess != null) {
			Collections.sort(guess, new Comparator<GuessDataModelType>() {
				@Override
				public int compare(GuessDataModelType o1, GuessDataModelType o2) {
					return o2.getGuessLevel() - o1.getGuessLevel();
				}
			});
			return guess.get(0);
		}

		return null;
	}
}
