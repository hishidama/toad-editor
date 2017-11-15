package jp.hishidama.eclipse_plugin.toad.model.node.port;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.model.AbstractModel;
import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;
import jp.hishidama.eclipse_plugin.toad.model.node.Attribute;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.property.attribute.HasAttributeNode;
import jp.hishidama.eclipse_plugin.toad.validation.ValidateType;
import jp.hishidama.eclipse_plugin.util.ToadCommandUtil;
import jp.hishidama.xtext.dmdl_editor.validation.ErrorStatus;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

import com.google.gson.annotations.Expose;

public class OpePort extends BasePort implements HasAttributeNode {
	private static final long serialVersionUID = 2142253631722198464L;

	@Expose
	private List<Attribute> attributeList;

	public OpePort() {
		setType("OpePort");
	}

	@Override
	public OpePort cloneEdit() {
		OpePort to = new OpePort();
		to.copyFrom(this);
		return to;
	}

	@Override
	public void copyFrom(AbstractModel fromModel) {
		super.copyFrom(fromModel);

		OpePort from = (OpePort) fromModel;
		attributeList = null;
		for (Attribute attr : from.getAttributeList()) {
			addAttribute(new Attribute(attr));
		}
	}

	@Override
	public Command getCommand(ToadEditor editor, CompoundCommand compound, AbstractModel fromModel) {
		super.getCommand(editor, compound, fromModel);

		OpePort from = (OpePort) fromModel;
		ToadCommandUtil.add(compound, getAttributeListCommand(from.getAttributeList()));

		return compound;
	}

	@Override
	public boolean canStartConnect() {
		return isOut();
	}

	@Override
	public boolean canConnectFrom(Connection connection, NodeElement source) {
		if (isOut()) {
			return false;
		}
		for (Connection c : getIncomings()) {
			if (c == connection) {
				continue;
			}
			return false; // 1件でも有ったら不可
		}
		return true;
	}

	@Override
	public boolean canConnectTo(Connection connection, NodeElement target) {
		if (target instanceof OpePort) {
			return ((OpePort) target).isIn();
		}
		return true;
	}

	@Override
	public List<Attribute> getAttributeList() {
		if (attributeList == null) {
			return Collections.emptyList();
		}
		return attributeList;
	}

	@Override
	public void setAttributeList(List<Attribute> list) {
		attributeList = null;
		if (list != null) {
			for (Attribute attr : list) {
				addAttribute(attr);
			}
		}
	}

	public void setAttribute(Attribute attr) {
		if (attributeList != null) {
			for (int i = 0; i < attributeList.size(); i++) {
				Attribute a = attributeList.get(i);
				if (a.getAnnotationName().equals(attr.getAnnotationName())
						&& a.getParameterName().equals(attr.getParameterName())) {
					a.setParent(null);
					attr.setParent(this);
					attributeList.set(i, attr);
					return;
				}
			}
		}
		addAttribute(attr);
	}

	public void addAttribute(Attribute attr) {
		addAttribute(-1, attr);
	}

	public void addAttribute(int index, Attribute attr) {
		attr.setParent(this);
		if (attributeList == null) {
			attributeList = new ArrayList<Attribute>();
		}
		if (index < 0) {
			attributeList.add(attr);
		} else {
			attributeList.add(index, attr);
		}
	}

	public void removeAttribute(Attribute attr) {
		attr.setParent(null);
		if (attributeList == null) {
			return;
		}
		attributeList.remove(attr);
	}

	public void moveAttribute(Attribute attr, int index) {
		if (attributeList == null) {
			return;
		}
		attributeList.remove(attr);
		attributeList.add(index, attr);
	}

	public Command getAttributeCommand(String annotationName, String parameterName, String valueType, List<String> value) {
		Attribute attr = new Attribute(annotationName, parameterName, valueType);
		attr.setValue(value);

		List<Attribute> list = new ArrayList<Attribute>(getAttributeList());
		Attribute found = null;
		for (int i = 0; i < list.size(); i++) {
			Attribute a = list.get(i);
			if (annotationName.equals(a.getAnnotationName()) && parameterName.equals(a.getParameterName())) {
				list.set(i, attr);
				found = a;
				break;
			}
		}
		if (found == null) {
			list.add(attr);
		}

		return getAttributeListCommand(list);
	}

	@Override
	public ChangeListCommand<Attribute> getAttributeListCommand(List<Attribute> list) {
		return new ChangeListCommand<Attribute>(list) {
			@Override
			protected List<Attribute> getValue() {
				return getAttributeList();
			}

			@Override
			protected void setValue(List<Attribute> value) {
				attributeList = null;
				for (Attribute attr : value) {
					addAttribute(attr);
				}
			}
		};
	}

	@Override
	public void fireAttributeChange() {
		firePropertyChange(PROP_ATTRIBUTE, null, null);
	}

	@Override
	public void validate(ValidateType vtype, boolean edit, List<IStatus> result) {
		super.validate(vtype, edit, result);

		if (isIn() && !edit) {
			switch (getIncomings().size()) {
			case 0:
				result.add(new ErrorStatus("入力ポートへの接続がありません。入力が無い場合は空演算子から接続して下さい。"));
				break;
			case 1:
				break;
			default:
				result.add(new ErrorStatus("入力ポートへの接続が多すぎます。"));
				break;
			}
		}
		if (isOut() && !edit) {
			if (getOutgoings().isEmpty()) {
				result.add(new ErrorStatus("出力ポートからどこにも接続されていません。出力結果を使わない場合は停止演算子へ接続して下さい。"));
			}
		}
	}

	@Override
	public String getDisplayLocation() {
		return String.format("OpePort(%s)", getDisplayName());
	}
}
