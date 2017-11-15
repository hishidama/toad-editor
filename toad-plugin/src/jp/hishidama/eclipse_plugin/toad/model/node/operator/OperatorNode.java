package jp.hishidama.eclipse_plugin.toad.model.node.operator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.hishidama.eclipse_plugin.dialog.ClassSelectionAbstractClassFilter;
import jp.hishidama.eclipse_plugin.dialog.ClassSelectionDialog.Filter;
import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen.OperatorMethodGenerator;
import jp.hishidama.eclipse_plugin.toad.model.AbstractModel;
import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;
import jp.hishidama.eclipse_plugin.toad.model.node.Attribute;
import jp.hishidama.eclipse_plugin.toad.model.node.ClassNameNode;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.RectangleNode;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.delegator.OperatorDelegate;
import jp.hishidama.eclipse_plugin.toad.model.node.port.OpePort;
import jp.hishidama.eclipse_plugin.toad.model.property.attribute.HasAttributeNode;
import jp.hishidama.eclipse_plugin.toad.model.property.datamodel.HasDataModelNode;
import jp.hishidama.eclipse_plugin.toad.model.property.port.HasPortNode;
import jp.hishidama.eclipse_plugin.toad.validation.ToadValidator;
import jp.hishidama.eclipse_plugin.toad.validation.ValidateType;
import jp.hishidama.eclipse_plugin.toad.view.SiblingDataModelTreeElement;
import jp.hishidama.eclipse_plugin.util.StringUtil;
import jp.hishidama.eclipse_plugin.util.ToadCommandUtil;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

import com.google.gson.annotations.Expose;

public class OperatorNode extends RectangleNode implements ClassNameNode, HasPortNode<OpePort>, HasAttributeNode {
	private static final long serialVersionUID = 6191204147926884892L;

	public static final String PROP_METHOD_NAME = "methodName";

	public static final String KEY_RETURN_ENUM_NAME = "returnEnumName";
	public static final String KEY_RETURN_DESCRIPTION = "returnDescription";

	@Expose
	private String className;
	@Expose
	private String methodName;
	@Expose
	private List<Attribute> attributeList;
	@Expose
	private List<OpeParameter> parameterList;
	@Expose
	private Map<String, String> propertyMap;

	public OperatorNode() {
		setWidth(80);
		setHeight(48);
	}

	@Override
	public OperatorNode cloneEdit() {
		OperatorNode to = new OperatorNode();
		to.copyFrom(this);
		return to;
	}

	@Override
	public void copyFrom(AbstractModel fromModel) {
		super.copyFrom(fromModel);

		OperatorNode from = (OperatorNode) fromModel;
		this.className = from.className;
		this.methodName = from.methodName;
		attributeList = null;
		for (Attribute attr : from.getAttributeList()) {
			addAttribute(new Attribute(attr));
		}
		parameterList = null;
		for (OpeParameter param : from.getParameterList()) {
			addParameter(new OpeParameter(param));
		}
		if (from.propertyMap != null) {
			this.propertyMap = new LinkedHashMap<String, String>(from.propertyMap);
		}
	}

	@Override
	public Command getCommand(ToadEditor editor, CompoundCommand compound, AbstractModel fromModel) {
		super.getCommand(editor, compound, fromModel);

		OperatorNode from = (OperatorNode) fromModel;
		ToadCommandUtil.add(compound, getClassNameCommand(from.getClassName()));
		ToadCommandUtil.add(compound, getMethodNameCommand(from.getMethodName()));
		ToadCommandUtil.add(compound, getAttributeListCommand(from.getAttributeList()));
		ToadCommandUtil.add(compound, getParameterListCommand(from.getParameterList()));
		ToadCommandUtil.add(compound, getPropertyCommand(new LinkedHashMap<String, String>(from.getPropertyMap())));

		return compound;
	}

	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public void setClassName(String className) {
		String old = this.className;
		this.className = className;
		firePropertyChange(PROP_CLASS_NAME, old, className);
	}

	@Override
	public ChangeTextCommand getClassNameCommand(String className) {
		return new ChangeTextCommand(className) {
			@Override
			protected void setValue(String value) {
				setClassName(value);
			}

			@Override
			protected String getValue() {
				return getClassName();
			}
		};
	}

	@Override
	public String getClassNamePattern() {
		return "*Operator";
	}

	@Override
	public Filter getClassNameFilter() {
		return new ClassSelectionAbstractClassFilter();
	}

	@Override
	public boolean hasToadFile() {
		return isFlowPart();
	}

	@Override
	public String getToadFileExtension() {
		if (isFlowPart()) {
			return "ftoad";
		}
		return null;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		String old = this.methodName;
		this.methodName = methodName;
		firePropertyChange(PROP_METHOD_NAME, old, methodName);
	}

	public ChangeTextCommand getMethodNameCommand(String methodName) {
		return new ChangeTextCommand(methodName) {
			@Override
			protected void setValue(String value) {
				setMethodName(value);
			}

			@Override
			protected String getValue() {
				return getMethodName();
			}
		};
	}

	public final boolean isUserOperator() {
		return !isCoreOperator() && !isFlowPart();
	}

	public final boolean isCoreOperator() {
		String type = getType();
		if (type.length() >= 1) {
			char c = type.charAt(0);
			return 'a' <= c && c <= 'z';
		}
		return false;
	}

	public final boolean isFlowPart() {
		String type = getType();
		return "FlowPart".equals(type);
	}

	public final boolean isReturnEnum() {
		return getDelegate().isReturnEnum();
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

	public List<OpeParameter> getParameterList() {
		if (parameterList == null) {
			return Collections.emptyList();
		}
		return parameterList;
	}

	public void setParameterList(List<OpeParameter> list) {
		parameterList = null;
		for (OpeParameter param : list) {
			addParameter(param);
		}
	}

	public void addParameter(OpeParameter param) {
		addParameter(-1, param);
	}

	public void addParameter(int index, OpeParameter param) {
		param.setParent(this);
		if (parameterList == null) {
			parameterList = new ArrayList<OpeParameter>();
		}
		if (index < 0) {
			parameterList.add(param);
		} else {
			parameterList.add(index, param);
		}
		fireParameterChange();
	}

	public void removeParameter(OpeParameter param) {
		param.setParent(null);
		if (parameterList == null) {
			return;
		}
		parameterList.remove(param);
		fireParameterChange();
	}

	public void moveParameter(OpeParameter param, int index) {
		if (parameterList == null) {
			return;
		}
		parameterList.remove(param);
		parameterList.add(index, param);
		fireParameterChange();
	}

	public ChangeListCommand<OpeParameter> getParameterListCommand(List<OpeParameter> list) {
		return new ChangeListCommand<OpeParameter>(list) {
			@Override
			protected List<OpeParameter> getValue() {
				return getParameterList();
			}

			@Override
			protected void setValue(List<OpeParameter> value) {
				parameterList = null;
				for (OpeParameter param : value) {
					addParameter(param);
				}
			}
		};
	}

	public void fireParameterChange() {
		firePropertyChange(PROP_PARAMETER, null, null);
	}

	private void setPropertyMap(Map<String, String> map) {
		propertyMap = map;
	}

	public void setProperty(String key, String value) {
		if (propertyMap == null) {
			propertyMap = new LinkedHashMap<String, String>();
		}
		propertyMap.put(key, value);
	}

	public String getProperty(String key) {
		if (propertyMap == null) {
			return null;
		}
		return propertyMap.get(key);
	}

	public Map<String, String> getPropertyMap() {
		if (propertyMap == null) {
			return Collections.emptyMap();
		}
		return propertyMap;
	}

	public ChangeMapCommand getPropertyCommand(Map<String, String> map) {
		return new ChangeMapCommand(map) {
			@Override
			protected void setValue(Map<String, String> value) {
				setPropertyMap(value);
			}

			@Override
			protected Map<String, String> getValue() {
				return getPropertyMap();
			}
		};
	}

	public List<OpePort> getInputPorts() {
		return getPorts(true);
	}

	public List<OpePort> getOutputPorts() {
		return getPorts(false);
	}

	public OpePort getOutputPort() {
		List<OpePort> list = getOutputPorts();
		if (list.size() >= 1) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public List<OpePort> getPorts() {
		List<NodeElement> cs = super.getChildren();
		List<OpePort> list = new ArrayList<OpePort>(cs.size());
		for (NodeElement c : cs) {
			if (c instanceof OpePort) {
				list.add((OpePort) c);
			}
		}
		return list;
	}

	@Override
	public List<OpePort> getPorts(int direction) {
		return super.getChildren(OpePort.class, direction);
	}

	@Override
	public List<OpePort> getPorts(boolean in) {
		List<NodeElement> cs = super.getChildren();
		List<OpePort> list = new ArrayList<OpePort>(cs.size());
		for (NodeElement c : cs) {
			if (c instanceof OpePort) {
				OpePort port = (OpePort) c;
				if (port.isIn() == in) {
					list.add(port);
				}
			}
		}
		return list;
	}

	@Override
	public void setPorts(boolean in, List<OpePort> list) {
		List<OpePort> old = getPorts(in);
		for (OpePort port : old) {
			removePort(port);
		}
		for (OpePort port : list) {
			addPort(port, port.getDirection());
		}
	}

	@Override
	public void addPort(OpePort c, int direction) {
		addChild(c, direction);
	}

	@Override
	public void addPort(int index, OpePort port, int direction) {
		addChild(index, port, direction);
	}

	@Override
	public void removePort(OpePort c) {
		removeChild(c);
	}

	@Override
	public boolean canStartConnect() {
		return getDelegate().canStartConnect();
	}

	@Override
	public boolean canConnectTo(Connection connection, NodeElement target) {
		return getDelegate().canConnectTo();
	}

	@Override
	public boolean canConnectFrom(Connection connection, NodeElement source) {
		return getDelegate().canConnectFrom();
	}

	public String getEllipseFigureText() {
		return getDelegate().getEllipseFigureText();
	}

	public List<Attribute> getDefaultAttribute() {
		return getDelegate().getDefaultAttribute();
	}

	public List<Attribute> getDefaultPortAnnotation() {
		return getDelegate().getDefaultPortAnnotation();
	}

	public String getKeyTitle() {
		return getDelegate().getKeyTitle();
	}

	public boolean enableValueParameter() {
		return getDelegate().enableValueParameter();
	}

	public boolean enableTypeParameter() {
		return getDelegate().enableTypeParameter();
	}

	@Override
	public String getToolTipInformation() {
		return getDelegate().getToolTipInformation();
	}

	@Override
	public void collectSiblingDataModelNode(SiblingDataModelTreeElement list, Set<Object> set, Set<Integer> idSet,
			HasDataModelNode node) {
		if (set.contains(this) || idSet.contains(this.getId())) {
			return;
		}
		set.add(this);
		idSet.add(this.getId());

		delegate.collectSiblingDataModelNode(list, set, idSet, node);
	}

	public GuessDataModelType guessDataModelType(OpePort port) {
		return getDelegate().guessDataModelType(port);
	}

	@Override
	public void validate(ValidateType vtype, boolean edit, List<IStatus> result) {
		ToadValidator.validateClassName(vtype, result, getClassName());
		ToadValidator.validateMethodName(vtype, result, getMethodName());
		ToadValidator.validatePorts(vtype, result, this);
		getDelegate().validate(vtype, result);
		// TODO attributeやparameterの精査
	}

	public OperatorMethodGenerator getSourceCode() {
		OperatorMethodGenerator gen = getDelegate().getSourceCode();
		if (gen != null) {
			gen.addJavadoc(getDescription());
			String memo = getMemo();
			if (StringUtil.nonEmpty(memo)) {
				String[] ss = memo.split("[\r\n]+");
				for (String text : ss) {
					gen.addJavadoc(text);
				}
			}
			gen.addAnnotation(this);
			gen.setMethodName(getMethodName());
			for (OpeParameter param : getParameterList()) {
				gen.addArgumentClass(null, param.getClassName(), param.getName(), param.getDescription());
			}
		}
		return gen;
	}

	private OperatorDelegate delegate;

	public final OperatorDelegate getDelegate() {
		if (delegate == null) {
			delegate = OperatorDelegate.getDelegate(this);
		}
		return delegate;
	}

	@Override
	public String getDisplayLocation() {
		return String.format("Operator(%s)", getDisplayName());
	}
}
