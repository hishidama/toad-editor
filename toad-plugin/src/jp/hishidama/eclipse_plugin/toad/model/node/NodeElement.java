package jp.hishidama.eclipse_plugin.toad.model.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.editor.action.layout.JobPortAutoLayout;
import jp.hishidama.eclipse_plugin.toad.editor.action.layout.OpePortAutoLayout;
import jp.hishidama.eclipse_plugin.toad.model.AbstractModel;
import jp.hishidama.eclipse_plugin.toad.model.AbstractNameModel;
import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;
import jp.hishidama.eclipse_plugin.toad.model.connection.command.CreateConnectionCommand;
import jp.hishidama.eclipse_plugin.toad.model.connection.command.DeleteConnectionCommand;
import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.node.command.CreateNodeCommand;
import jp.hishidama.eclipse_plugin.toad.model.node.command.DeleteNodeCommand;
import jp.hishidama.eclipse_plugin.toad.model.node.command.NodeComponentEditPolicy;
import jp.hishidama.eclipse_plugin.toad.model.node.port.BasePort;
import jp.hishidama.eclipse_plugin.toad.model.node.port.OpePort;
import jp.hishidama.eclipse_plugin.toad.model.node.port.command.CreatePortCommand;
import jp.hishidama.eclipse_plugin.toad.model.node.port.command.DeletePortCommand;
import jp.hishidama.eclipse_plugin.toad.model.property.datamodel.HasDataModelNode;
import jp.hishidama.eclipse_plugin.toad.model.property.port.HasPortNode;
import jp.hishidama.eclipse_plugin.toad.view.SiblingDataModelTreeElement;
import jp.hishidama.eclipse_plugin.util.ToadCommandUtil;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.swt.graphics.Rectangle;

import com.google.gson.annotations.Expose;

@SuppressWarnings("serial")
public abstract class NodeElement extends AbstractNameModel {

	static final String PROP_INCOMINGS = "incomings";
	static final String PROP_OUTGOINGS = "outgoings";
	public static final String PROP_CHILDREN = "children";

	// direction
	public static final int FREE = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 4;
	public static final int TOP = 8;
	public static final int BOTTOM = 16;

	@Expose
	private int id;
	private Diagram diagram;
	private List<Connection> incomings;
	private List<Connection> outgoings;
	private NodeElement parent;
	@Expose
	private int direction; // 自分が子のとき、parentに対する位置
	@Expose
	private List<NodeElement> children;

	@Override
	public abstract NodeElement cloneEdit();

	@Override
	public void copyFrom(AbstractModel fromModel) {
		super.copyFrom(fromModel);

		NodeElement from = (NodeElement) fromModel;
		this.id = from.id;
		if (from.diagram != null) {
			this.diagram = from.diagram.cloneEdit();
		}
		for (Connection f : from.getIncomings()) {
			Connection c = f.cloneEdit();
			c.setTarget(this);
			addIncoming(c);
		}
		for (Connection f : from.getOutgoings()) {
			Connection c = f.cloneEdit();
			c.setSource(this);
			addOutgoing(c);
		}
		this.direction = from.direction;
		for (NodeElement f : from.getChildren()) {
			NodeElement c = f.cloneEdit();
			addChild(c, c.direction);
		}
	}

	@Override
	public Command getCommand(ToadEditor editor, CompoundCommand compound, AbstractModel fromModel) {
		super.getCommand(editor, compound, fromModel);

		NodeElement from = (NodeElement) fromModel;
		ToadCommandUtil.add(compound, getIncomingListCommand(from.getIncomings()));
		ToadCommandUtil.add(compound, getOutgoingListCommand(from.getOutgoings()));
		ToadCommandUtil.add(compound, getChildrenCommand(editor, from.getChildren()));

		return compound;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String getIdString() {
		return Integer.toString(id);
	}

	public abstract void addX(int zx);

	public abstract void addY(int zy);

	public abstract Rectangle getCoreBounds();

	public abstract Rectangle getCoreBounds(Map<NodeElement, Rectangle> rectMap);

	public abstract Rectangle getOuterBounds();

	public abstract Rectangle getOuterBounds(Map<NodeElement, Rectangle> rectMap);

	public Diagram getDiagram() {
		return diagram;
	}

	public void setDiagram(Diagram parent) {
		this.diagram = parent;
	}

	public List<Connection> getIncomings() {
		if (incomings == null) {
			return Collections.emptyList();
		}
		return incomings;
	}

	public void addIncoming(Connection connection) {
		if (incomings == null) {
			incomings = new ArrayList<Connection>();
		}
		incomings.add(connection);
		firePropertyChange(PROP_INCOMINGS, null, null);
	}

	public void removeIncoming(Connection connection) {
		if (incomings != null) {
			incomings.remove(connection);
			if (incomings.isEmpty()) {
				incomings = null;
			}
		}
		firePropertyChange(PROP_INCOMINGS, null, null);
	}

	public Command getIncomingListCommand(List<Connection> list) {
		CompoundCommand compound = new CompoundCommand();
		for (Connection c : getIncomings()) {
			compound.add(new DeleteConnectionCommand(c));
		}
		for (Connection c : list) {
			CreateConnectionCommand command = new CreateConnectionCommand(c);
			command.setSource(c.getSource());
			command.setTarget(this);
			compound.add(command);
		}
		if (compound.isEmpty()) {
			return null;
		}
		return compound.unwrap();
	}

	public List<Connection> getOutgoings() {
		if (outgoings == null) {
			return Collections.emptyList();
		}
		return outgoings;
	}

	public void addOutgoing(Connection connection) {
		if (outgoings == null) {
			outgoings = new ArrayList<Connection>();
		}
		outgoings.add(connection);
		firePropertyChange(PROP_OUTGOINGS, null, null);
	}

	public void removeOutgoing(Connection connection) {
		if (outgoings != null) {
			outgoings.remove(connection);
			if (outgoings.isEmpty()) {
				outgoings = null;
			}
		}
		firePropertyChange(PROP_OUTGOINGS, null, null);
	}

	public Command getOutgoingListCommand(List<Connection> list) {
		CompoundCommand compound = new CompoundCommand();
		for (Connection c : getOutgoings()) {
			compound.add(new DeleteConnectionCommand(c));
		}
		for (Connection c : list) {
			CreateConnectionCommand command = new CreateConnectionCommand(c);
			command.setSource(this);
			command.setTarget(c.getTarget());
			compound.add(command);
		}
		if (compound.isEmpty()) {
			return null;
		}
		return compound.unwrap();
	}

	public List<NodeElement> getInputNodes() {
		List<Connection> cs = getIncomings();
		List<NodeElement> list = new ArrayList<NodeElement>(cs.size());
		for (Connection c : cs) {
			list.add(c.getOpposite(this));
		}
		return list;
	}

	public List<NodeElement> getOutputNodes() {
		List<Connection> cs = getOutgoings();
		List<NodeElement> list = new ArrayList<NodeElement>(cs.size());
		for (Connection c : cs) {
			list.add(c.getOpposite(this));
		}
		return list;
	}

	public boolean canStartConnect() {
		return true;
	}

	public abstract boolean canConnectTo(Connection connection, NodeElement target);

	public abstract boolean canConnectFrom(Connection connection, NodeElement source);

	public void setParent(NodeElement parent) {
		this.parent = parent;
	}

	public NodeElement getParent() {
		return parent;
	}

	public List<NodeElement> getChildren() {
		if (children == null) {
			return Collections.emptyList();
		}
		return children;
	}

	public List<NodeElement> getChildren(int direction) {
		return getChildren(null, direction);
	}

	@SuppressWarnings("unchecked")
	public <N extends NodeElement> List<N> getChildren(Class<N> clazz, int direction) {
		if (children == null) {
			return Collections.emptyList();
		}
		List<N> list = new ArrayList<N>(children.size());
		for (NodeElement c : children) {
			if ((c.direction & direction) != 0) {
				if (clazz == null || clazz.isInstance(c)) {
					list.add((N) c);
				}
			}
		}
		return list;
	}

	public void addChild(NodeElement child) {
		addChild(child, FREE);
	}

	public void addChild(NodeElement child, int direction) {
		addChild(-1, child, direction);
	}

	public void addChild(int index, NodeElement child, int direction) {
		child.direction = direction;

		if (children == null) {
			children = new ArrayList<NodeElement>();
		}
		if (index >= 0) {
			children.add(index, child);
		} else {
			children.add(child);
		}
		child.setParent(this);

		firePropertyChange(PROP_CHILDREN, null, null);
	}

	public void removeChild(NodeElement child) {
		if (children == null) {
			return;
		}
		children.remove(child);
		child.setParent(null);
		if (children.isEmpty()) {
			children = null;
		}

		firePropertyChange(PROP_CHILDREN, null, null);
	}

	public void moveChild(NodeElement child, int index) {
		if (children == null) {
			return;
		}
		children.remove(child);
		children.add(index, child);

		firePropertyChange(PROP_CHILDREN, null, null);
	}

	private Command getMoveChildCommand(final NodeElement child, final int index) {
		return new Command() {
			private int oldIndex;

			@Override
			public void execute() {
				this.oldIndex = children.indexOf(child);
				moveChild(child, index);
			}

			@Override
			public void undo() {
				moveChild(child, oldIndex);
			}
		};
	}

	public NodeElement findChild(int id) {
		if (getId() == id) {
			return this;
		}
		for (NodeElement c : getChildren()) {
			NodeElement f = c.findChild(id);
			if (f != null) {
				return f;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Command getChildrenCommand(ToadEditor editor, List<NodeElement> list) {
		CompoundCommand compound = new CompoundCommand();
		List<NodeElement> children = getChildren();
		List<NodeElement> temp = new ArrayList<NodeElement>();
		List<NodeElement> remove = new ArrayList<NodeElement>();
		for (NodeElement child : children) {
			NodeElement found = null;
			for (NodeElement n : list) {
				if (n.getId() == child.getId()) {
					found = n;
					break;
				}
			}
			if (found != null) {
				child.getCommand(editor, compound, found);
				temp.add(child);
			} else {
				remove.add(child);
			}
		}

		// 削除
		for (NodeElement c : remove) {
			if (c instanceof BasePort) {
				BasePort port = (BasePort) c;
				NodeComponentEditPolicy.deleteConnection(compound, port);
				compound.add(new DeletePortCommand(this, port));
			} else {
				NodeComponentEditPolicy.deleteConnection(compound, c);
				compound.add(new DeleteNodeCommand(diagram, c));
			}
		}

		// 新規追加
		List<NodeElement> temp2 = new ArrayList<NodeElement>(temp);
		for (NodeElement c : list) {
			NodeElement found = null;
			for (NodeElement t : temp) {
				if (t.getId() == c.getId()) {
					found = t;
					break;
				}
			}
			if (found == null) {
				c.setId(editor.newId());
				Command command;
				if (c instanceof BasePort) {
					BasePort port = (BasePort) c;
					port.setCy(calculatePortY(list, port));
					command = new CreatePortCommand<BasePort>((HasPortNode<BasePort>) this, port, port.getDirection());
				} else {
					RectangleNode node = (RectangleNode) c;
					command = new CreateNodeCommand(diagram, node, node.getX(), node.getY());
				}
				compound.add(command);
				temp2.add(c);
			}
		}

		// 順序変更
		for (int i = 0; i < list.size(); i++) {
			NodeElement node = list.get(i);
			for (int j = i; j < temp2.size(); j++) {
				NodeElement t = temp2.get(j);
				if (t.getId() == node.getId()) {
					if (j != i) {
						compound.add(getMoveChildCommand(t, i));
						temp2.remove(j);
						temp2.add(i, t);
					}
					break;
				}
			}
		}

		if (compound.isEmpty()) {
			return null;
		}
		return compound.unwrap();
	}

	private int calculatePortY(List<NodeElement> list, BasePort port) {
		int y = 0;
		int size = 0;
		for (int i = 0; i < list.size(); i++) {
			BasePort c = (BasePort) list.get(i);
			if (c.isIn() != port.isIn()) {
				continue;
			}
			y = Math.max(y, c.getCy());
			size++;
		}

		if (size == 1) {
			port.setNamePosition(PositionConstants.NONE);
		}
		if (y <= 0) {
			Rectangle bounds = getCoreBounds();
			return bounds.y + bounds.height / 2;
		}
		if (port instanceof OpePort) {
			y += OpePortAutoLayout.H_SPAN;
		} else {
			y += JobPortAutoLayout.H_SPAN;
		}
		return y;
	}

	public int getDirection() {
		return direction;
	}

	public void collectSiblingDataModelNode(SiblingDataModelTreeElement list, Set<Object> set, Set<Integer> idSet,
			HasDataModelNode node) {
		// do override
	}
}
