package jp.hishidama.eclipse_plugin.toad.model.marker;

import java.util.List;

import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.RectangleNode;
import jp.hishidama.eclipse_plugin.toad.validation.ValidateType;

import org.eclipse.core.runtime.IStatus;

public class MarkerNode extends RectangleNode {
	private static final long serialVersionUID = 882759353991481799L;

	public static final int WIDTH = 32;
	public static final int HEIGHT = 32;

	public MarkerNode() {
		setX(640 - WIDTH);
		setY(480 - HEIGHT);
		setWidth(WIDTH);
		setHeight(HEIGHT);
	}

	@Override
	public MarkerNode cloneEdit() {
		MarkerNode to = new MarkerNode();
		to.copyFrom(this);
		return to;
	}

	@Override
	public boolean canConnectTo(Connection connection, NodeElement target) {
		return false;
	}

	@Override
	public boolean canConnectFrom(Connection connection, NodeElement source) {
		return false;
	}

	@Override
	public void validate(ValidateType vtype, boolean edit, List<IStatus> result) {
	}

	@Override
	public String getDisplayLocation() {
		return "Marker";
	}
}
