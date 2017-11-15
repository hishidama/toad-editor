package jp.hishidama.eclipse_plugin.toad.editor.outline;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.List;

import jp.hishidama.eclipse_plugin.toad.editor.outline.TreeGroup.SortType;
import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.frame.FrameNode;
import jp.hishidama.eclipse_plugin.toad.model.marker.MarkerNode;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.datafile.DataFileNode;

public class DiagramTreePart extends ToadTreeEditPart {

	@Override
	public Diagram getModel() {
		return (Diagram) super.getModel();
	}

	@Override
	protected List<?> getModelChildren() {
		Diagram diagram = getModel();
		switch (diagram.getDiagramType()) {
		case BATCH:
			return getBatchTree(diagram);
		case JOBFLOW:
			return getJobflowTree(diagram);
		case FLOWPART:
			return getFlowpartTree(diagram);
		default:
			return getModel().getContents();
		}
	}

	private List<TreeGroup> getBatchTree(Diagram diagram) {
		TreeGroup job = new TreeGroup("job", SortType.NAME);
		for (NodeElement node : diagram.getContents()) {
			if (node instanceof MarkerNode) {
				continue;
			} else {
				job.add(node);
			}
		}
		return Arrays.asList(job);
	}

	private List<TreeGroup> getJobflowTree(Diagram diagram) {
		TreeGroup frame = new TreeGroup("frame", SortType.NONE);
		TreeGroup file = new TreeGroup("importer/exporter", SortType.NONE);
		TreeGroup operator = new TreeGroup("operator", SortType.NAME);
		for (NodeElement node : diagram.getContents()) {
			if (node instanceof MarkerNode) {
				continue;
			} else if (node instanceof FrameNode) {
				frame.add(node);
			} else if (node instanceof DataFileNode) {
				file.add(node);
			} else {
				operator.add(node);
			}
		}
		return Arrays.asList(frame, file, operator);
	}

	private List<TreeGroup> getFlowpartTree(Diagram diagram) {
		TreeGroup frame = new TreeGroup("frame", SortType.NONE);
		TreeGroup operator = new TreeGroup("operator", SortType.NAME);
		for (NodeElement node : diagram.getContents()) {
			if (node instanceof MarkerNode) {
				continue;
			} else if (node instanceof FrameNode) {
				frame.add(node);
			} else {
				operator.add(node);
			}
		}
		return Arrays.asList(frame, operator);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		String name = event.getPropertyName();
		if (Diagram.PROP_CONTENTS.equals(name)) {
			refreshChildren();
		}
		super.propertyChange(event);
	}
}
