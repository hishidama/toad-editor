package jp.hishidama.eclipse_plugin.toad.model.frame;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.model.AbstractModel;
import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;
import jp.hishidama.eclipse_plugin.toad.model.node.ClassNameNode;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.RectangleNode;
import jp.hishidama.eclipse_plugin.toad.model.node.port.JobPort;
import jp.hishidama.eclipse_plugin.toad.model.property.port.HasPortNode;
import jp.hishidama.eclipse_plugin.util.ToadCommandUtil;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

import com.google.gson.annotations.Expose;

@SuppressWarnings("serial")
public abstract class FrameNode extends RectangleNode implements ClassNameNode, HasPortNode<JobPort> {

	@Expose
	private String className;

	public abstract int getHorizontalMargin();

	public int getVerticalMargin() {
		return 48;
	}

	@Override
	public void copyFrom(AbstractModel fromModel) {
		super.copyFrom(fromModel);

		FrameNode from = (FrameNode) fromModel;
		this.className = from.className;
	}

	@Override
	public Command getCommand(ToadEditor editor, CompoundCommand compound, AbstractModel fromModel) {
		super.getCommand(editor, compound, fromModel);

		FrameNode from = (FrameNode) fromModel;
		ToadCommandUtil.add(compound, getClassNameCommand(from.getClassName()));

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
	public boolean hasToadFile() {
		return false;
	}

	@Override
	public List<JobPort> getPorts() {
		List<NodeElement> cs = super.getChildren();
		List<JobPort> list = new ArrayList<JobPort>(cs.size());
		for (NodeElement c : cs) {
			if (c instanceof JobPort) {
				list.add((JobPort) c);
			}
		}
		return list;
	}

	@Override
	public List<JobPort> getPorts(int direction) {
		return super.getChildren(JobPort.class, direction);
	}

	@Override
	public List<JobPort> getPorts(boolean in) {
		List<NodeElement> cs = super.getChildren();
		List<JobPort> list = new ArrayList<JobPort>(cs.size());
		for (NodeElement c : cs) {
			if (c instanceof JobPort) {
				JobPort port = (JobPort) c;
				if (port.isIn() == in) {
					list.add(port);
				}
			}
		}
		return list;
	}

	@Override
	public void setPorts(boolean in, List<JobPort> list) {
		List<JobPort> old = getPorts(in);
		for (JobPort port : old) {
			removePort(port);
		}
		for (JobPort port : list) {
			addPort(port, port.getDirection());
		}
	}

	@Override
	public void addPort(JobPort port, int direction) {
		addChild(port, direction);
	}

	@Override
	public void addPort(int index, JobPort port, int direction) {
		addChild(index, port, direction);
	}

	@Override
	public void removePort(JobPort port) {
		removeChild(port);
	}

	@Override
	public boolean canConnectTo(Connection connection, NodeElement target) {
		return false;
	}

	@Override
	public boolean canConnectFrom(Connection connection, NodeElement source) {
		return false;
	}

	public List<JobPort> getInputPorts() {
		return getPorts(true);
	}

	public List<JobPort> getOutputPorts() {
		return getPorts(false);
	}

	public JobPort getInputPort(String name) {
		for (JobPort port : getInputPorts()) {
			if (name.equals(port.getName())) {
				return port;
			}
		}
		return null;
	}

	public JobPort getOutputPort(String name) {
		for (JobPort port : getOutputPorts()) {
			if (name.equals(port.getName())) {
				return port;
			}
		}
		return null;
	}
}
