package jp.hishidama.eclipse_plugin.toad.model.property.port;

import java.util.List;

import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.port.BasePort;

public interface HasPortNode<C extends BasePort> {
	public List<NodeElement> getChildren();

	public List<C> getPorts();

	public List<C> getPorts(int direction);

	public List<C> getPorts(boolean in);

	public void setPorts(boolean in, List<C> list);

	public void addPort(C child, int direction);

	public void addPort(int index, C child, int direction);

	// TODO removePort()やmoveChild()は廃止してよいか？
	public void removePort(C child);

	public void moveChild(NodeElement child, int index);

	public Diagram getDiagram();

	public int getX();

	public int getY();

	public int getWidth();

	public int getHeight();
}
