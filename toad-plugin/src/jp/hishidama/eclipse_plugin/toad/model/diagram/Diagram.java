package jp.hishidama.eclipse_plugin.toad.model.diagram;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import jp.hishidama.eclipse_plugin.dialog.ClassSelectionAnnotationFilter;
import jp.hishidama.eclipse_plugin.dialog.ClassSelectionDialog.Filter;
import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.model.AbstractModel;
import jp.hishidama.eclipse_plugin.toad.model.AbstractNameModel;
import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;
import jp.hishidama.eclipse_plugin.toad.model.frame.FlowpartFrameNode;
import jp.hishidama.eclipse_plugin.toad.model.frame.FlowpartParameterDef;
import jp.hishidama.eclipse_plugin.toad.model.frame.FrameNode;
import jp.hishidama.eclipse_plugin.toad.model.frame.JobFrameNode;
import jp.hishidama.eclipse_plugin.toad.model.marker.MarkerNode;
import jp.hishidama.eclipse_plugin.toad.model.node.Attribute;
import jp.hishidama.eclipse_plugin.toad.model.node.ClassNameNode;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OpeParameter;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;
import jp.hishidama.eclipse_plugin.toad.model.property.attribute.HasAttributeNode;
import jp.hishidama.eclipse_plugin.toad.model.property.generic.NameNode;
import jp.hishidama.eclipse_plugin.toad.validation.ValidateType;
import jp.hishidama.eclipse_plugin.util.ToadCommandUtil;
import jp.hishidama.xtext.dmdl_editor.ui.internal.LogUtil;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

import com.google.gson.annotations.Expose;

public class Diagram extends AbstractNameModel implements NameNode, ClassNameNode {
	private static final long serialVersionUID = -383465183772018139L;

	public static final String PROP_CONTENTS = "contents";

	@Expose
	private DiagramType diagramType;
	@Expose
	private String name;
	@Expose
	private String className;

	@Expose
	private List<NodeElement> contents;

	// Gsonの保存・復元のみ使用
	@Expose
	private Collection<Connection> connections;

	public Diagram() {
		MarkerNode marker = new MarkerNode();
		this.addContent(marker);
	}

	@Override
	public Diagram cloneEdit() {
		Diagram to = new Diagram();
		to.copyFrom(this);
		return to;
	}

	@Override
	public void copyFrom(AbstractModel fromModel) {
		super.copyFrom(fromModel);

		Diagram from = (Diagram) fromModel;
		this.diagramType = from.diagramType;
		this.name = from.name;
		this.className = from.className;
	}

	@Override
	public Command getCommand(ToadEditor editor, CompoundCommand compound, AbstractModel fromModel) {
		super.getCommand(editor, compound, fromModel);

		Diagram from = (Diagram) fromModel;
		ToadCommandUtil.add(compound, getNameCommand(from.getName()));
		ToadCommandUtil.add(compound, getClassNameCommand(from.getClassName()));

		return compound;
	}

	public DiagramType getDiagramType() {
		if (diagramType == null) {
			diagramType = DiagramType.UNKOWN;
		}
		return diagramType;
	}

	public void setDiagramType(DiagramType type) {
		this.diagramType = type;
	}

	@Override
	public String getIdString() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getDescription() {
		FrameNode frame = getFrameNode();
		if (frame != null) {
			return frame.getDescription();
		}
		return super.getDescription();
	}

	@Override
	public void setDescription(String description) {
		FrameNode frame = getFrameNode();
		if (frame != null) {
			frame.setDescription(description);
		}
		super.setDescription(description);
	}

	@Override
	public String getMemo() {
		FrameNode frame = getFrameNode();
		if (frame != null) {
			return frame.getMemo();
		}
		return super.getMemo();
	}

	@Override
	public String getName() {
		FrameNode frame = getFrameNode();
		if (frame instanceof JobFrameNode) {
			return ((JobFrameNode) frame).getName();
		}
		if (name == null) {
			return "";
		}
		return name;
	}

	@Override
	public void setName(String name) {
		FrameNode frame = getFrameNode();
		if (frame instanceof JobFrameNode) {
			((JobFrameNode) frame).setName(name);
		}
		String old = this.name;
		this.name = name;
		firePropertyChange(PROP_NAME, old, name);
	}

	@Override
	public ChangeTextCommand getNameCommand(String name) {
		return new ChangeTextCommand(name) {
			@Override
			protected void setValue(String value) {
				setName(value);
			}

			@Override
			protected String getValue() {
				return getName();
			}
		};
	}

	@Override
	public String getClassName() {
		FrameNode frame = getFrameNode();
		if (frame != null) {
			return frame.getClassName();
		}
		return className;
	}

	@Override
	public void setClassName(String className) {
		FrameNode frame = getFrameNode();
		if (frame != null) {
			frame.setClassName(className);
		}
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
		switch (diagramType) {
		case BATCH:
			return "*Batch";
		case JOBFLOW:
			return "*Job";
		case FLOWPART:
			return "*FlowPart";
		default:
			return null;
		}
	}

	@Override
	public Filter getClassNameFilter() {
		switch (diagramType) {
		case BATCH:
			return new ClassSelectionAnnotationFilter("com.asakusafw.vocabulary.batch.Batch");
		case JOBFLOW:
			return new ClassSelectionAnnotationFilter("com.asakusafw.vocabulary.flow.JobFlow");
		case FLOWPART:
			return new ClassSelectionAnnotationFilter("com.asakusafw.vocabulary.flow.FlowPart");
		default:
			return null;
		}
	}

	@Override
	public boolean hasToadFile() {
		return false;
	}

	@Override
	public String getToadFileExtension() {
		switch (diagramType) {
		case BATCH:
			return "btoad";
		case JOBFLOW:
			return "jtoad";
		case FLOWPART:
			return "ftoad";
		default:
			return null;
		}
	}

	public void addContent(NodeElement node) {
		if (contents == null) {
			contents = new ArrayList<NodeElement>();
		}
		if (node instanceof FrameNode) {
			contents.add(0, node);
		} else {
			contents.add(node);
		}
		node.setDiagram(this);
		firePropertyChange(PROP_CONTENTS, null, null);
	}

	public void removeContent(NodeElement node) {
		if (contents == null) {
			return;
		}
		contents.remove(node);
		node.setDiagram(null);
		firePropertyChange(PROP_CONTENTS, null, null);
	}

	public List<NodeElement> getContents() {
		if (contents == null) {
			return Collections.emptyList();
		}
		return contents;
	}

	public NodeElement findContent(int id) {
		for (NodeElement c : getContents()) {
			NodeElement f = c.findChild(id);
			if (f != null) {
				return f;
			}
		}
		return null;
	}

	public FrameNode getFrameNode() {
		for (NodeElement node : getContents()) {
			if (node instanceof FrameNode) {
				return (FrameNode) node;
			}
		}
		return null;
	}

	public void prepareSave() {
		connections = new LinkedHashSet<Connection>();
		for (NodeElement node : getContents()) {
			collectConnection(node);
		}
	}

	private void collectConnection(NodeElement node) {
		connections.addAll(node.getIncomings());
		connections.addAll(node.getOutgoings());

		for (NodeElement child : node.getChildren()) {
			collectConnection(child);
		}
	}

	public void postSave() {
		connections = null;
	}

	public void postLoad() {
		Map<Integer, NodeElement> map = new HashMap<Integer, NodeElement>();
		collectNode(map, null, contents);
		if (connections != null) {
			for (Connection c : connections) {
				NodeElement source = map.get(c.getSourceId());
				NodeElement target = map.get(c.getTargetId());
				if (source != null && target != null) {
					c.setSource(source);
					c.setTarget(target);
					source.addOutgoing(c);
					target.addIncoming(c);
				} else {
					LogUtil.logWarn(MessageFormat.format("illegal connection: {0}({1})->{2}({3})", c.getSourceId(),
							source, c.getTargetId(), target));
				}
			}
		}

		connections = null;
	}

	private void collectNode(Map<Integer, NodeElement> map, NodeElement parent, List<NodeElement> list) {
		if (list == null) {
			return;
		}
		for (NodeElement node : list) {
			node.setDiagram(this);
			node.setParent(parent);
			map.put(node.getId(), node);
			if (node instanceof HasAttributeNode) {
				HasAttributeNode anode = (HasAttributeNode) node;
				for (Attribute attr : anode.getAttributeList()) {
					attr.setParent(anode);
				}
			}
			if (node instanceof FlowpartFrameNode) {
				FlowpartFrameNode frame = (FlowpartFrameNode) node;
				for (FlowpartParameterDef param : frame.getParameterList()) {
					param.setParent(frame);
				}
			}
			if (node instanceof OperatorNode) {
				OperatorNode operator = (OperatorNode) node;
				for (OpeParameter param : operator.getParameterList()) {
					param.setParent(operator);
				}
			}
			collectNode(map, node, node.getChildren());
		}
	}

	@Override
	public void validate(ValidateType vtype, boolean edit, List<IStatus> result) {
	}

	@Override
	public String getDisplayLocation() {
		return "Diagram";
	}
}
