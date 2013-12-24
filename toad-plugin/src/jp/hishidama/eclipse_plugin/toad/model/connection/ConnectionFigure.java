package jp.hishidama.eclipse_plugin.toad.model.connection;

import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.port.BasePort;
import jp.hishidama.eclipse_plugin.toad.model.property.generic.NameNode;
import static jp.hishidama.eclipse_plugin.util.StringUtil.*;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;

public class ConnectionFigure extends PolylineConnection {
	private NodeElement source;
	private NodeElement target;
	private PolygonDecoration decoration;
	private Label tip;

	public void setSource(NodeElement s) {
		source = s;
		resetToolTip();
	}

	public void setTarget(NodeElement t) {
		target = t;

		if (t instanceof BasePort) {
			setTargetDecoration(null);
		} else {
			// 黒塗り三角にする http://codezine.jp/article/detail/40?p=2
			if (decoration == null) {
				decoration = new PolygonDecoration();
			}
			setTargetDecoration(decoration);
		}

		resetToolTip();
	}

	private void resetToolTip() {
		StringBuilder sb = new StringBuilder(64);
		sb.append(name(source));
		sb.append(" -> ");
		sb.append(name(target));

		BasePort port = port();
		if (port != null) {
			String modelName = port.getModelName();
			String modelDescription = port.getModelDescription();
			if (nonEmpty(modelName)) {
				sb.append('\n');
				sb.append(modelName);
				if (nonEmpty(modelDescription)) {
					sb.append(" : ");
					sb.append(modelDescription);
				}
			} else {
				if (nonEmpty(modelDescription)) {
					sb.append('\n');
					sb.append(modelDescription);
				}
			}
		}

		if (tip == null) {
			tip = new Label();
		}
		tip.setText(sb.toString());
		setToolTip(tip);
	}

	public static String name(NodeElement node) {
		if (node == null) {
			return "null";
		}

		String name = null;
		if (node instanceof NameNode) {
			name = ((NameNode) node).getName();
		}
		if (isEmpty(name)) {
			name = node.getDescription();
		}
		NodeElement parent = node.getParent();
		if (parent != null) {
			return String.format("%s.%s", parent.getDescription(), name);
		} else {
			return name;
		}
	}

	private BasePort port() {
		if (source instanceof BasePort) {
			BasePort port = (BasePort) source;
			if (port.getModelName() != null) {
				return port;
			}
		}
		if (target instanceof BasePort) {
			BasePort port = (BasePort) target;
			if (port.getModelName() != null) {
				return port;
			}
		}
		return null;
	}
}
