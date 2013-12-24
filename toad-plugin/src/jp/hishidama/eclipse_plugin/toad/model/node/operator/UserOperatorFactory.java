package jp.hishidama.eclipse_plugin.toad.model.node.operator;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElementFactory;
import jp.hishidama.eclipse_plugin.toad.model.node.port.OpePort;

import org.eclipse.draw2d.PositionConstants;

public class UserOperatorFactory extends NodeElementFactory {

	private String name;
	private String group;
	private String description;
	private String memo;

	private List<String> incomings = new ArrayList<String>(2);

	private List<String> outgoings = new ArrayList<String>(2);

	public UserOperatorFactory(ToadEditor editor, String name) {
		super(editor);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getGroup() {
		return group;
	}

	public void setDescription(String message) {
		this.description = message;
	}

	public String getDescription() {
		return description;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getMemo() {
		return memo;
	}

	public void addIn(String... name) {
		for (String s : name) {
			incomings.add(s);
		}
	}

	public void addOut(String... name) {
		for (String s : name) {
			outgoings.add(s);
		}
	}

	@Override
	public OperatorNode getNewObject() {
		OperatorNode node = createOperatorNode(name);

		{
			int i = 0;
			for (String name : incomings) {
				OpePort port = createInPort(node, name, incomings.size(), i++);
				port.setAttributeList(node.getDefaultPortAnnotation());
			}
		}
		{
			int i = 0;
			for (String name : outgoings) {
				createOutPort(node, name, outgoings.size(), i++);
			}
		}

		return node;
	}

	protected OperatorNode createOperatorNode(String type) {
		OperatorNode node;
		if ("empty".equals(type) || "stop".equals(type) || "confluent".equals(type)) {
			node = new EllipseOperatorNode();
		} else {
			node = new OperatorNode();
		}

		int id = newId();
		node.setId(id);
		node.setType(type);
		node.setDescription(getTemporaryDescription(node));
		node.setMethodName(getTemporaryMethodName(node));
		node.setAttributeList(node.getDefaultAttribute());

		return node;
	}

	protected String getTemporaryDescription(OperatorNode node) {
		return node.getDelegate().getDescription();
	}

	protected String getTemporaryMethodName(OperatorNode node) {
		if (node.isFlowPart()) {
			return "create";
		}
		return node.getDelegate().getMethodName();
	}

	protected OpePort createInPort(OperatorNode node, String nameDesc, int count, int index) {
		OpePort c = createPort(node, nameDesc, count, index);

		c.setIn(true);
		c.setCx(0);
		c.setNamePosition((count <= 1) ? PositionConstants.NONE : PositionConstants.LEFT);
		c.setRole(getPortRole(node, true, index));
		node.addPort(c, NodeElement.LEFT);

		return c;
	}

	protected OpePort createOutPort(OperatorNode node, String nameDesc, int count, int index) {
		OpePort c = createPort(node, nameDesc, count, index);

		c.setOut(true);
		c.setCx(node.getWidth());
		c.setNamePosition((count <= 1) ? PositionConstants.NONE : PositionConstants.RIGHT);
		c.setRole(getPortRole(node, false, index));
		node.addPort(c, NodeElement.RIGHT);

		return c;
	}

	private OpePort createPort(OperatorNode node, String nameDesc, int count, int index) {
		OpePort c = new OpePort();

		String name, desc;
		{
			int n = nameDesc.indexOf(':');
			if (n >= 0) {
				name = nameDesc.substring(0, n).trim();
				desc = nameDesc.substring(n + 1).trim();
			} else {
				name = nameDesc;
				desc = "";
			}
		}

		int id = newId();
		c.setId(id);
		c.setName(name);
		c.setDescription(desc);
		c.setCy(node.getHeight() * (index + 1) / (count + 1));

		return c;
	}

	private String getPortRole(OperatorNode node, boolean in, int index) {
		List<String> list = node.getDelegate().getPortRoleList(in);
		if (index < list.size()) {
			return list.get(index);
		}
		return "";
	}

	@Override
	public Object getObjectType() {
		return OperatorNode.class;
	}
}
