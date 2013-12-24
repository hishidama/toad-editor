package jp.hishidama.eclipse_plugin.toad.model;

import java.text.MessageFormat;

import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;
import jp.hishidama.eclipse_plugin.toad.model.connection.ConnectionEditPart;
import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.diagram.DiagramEditPart;
import jp.hishidama.eclipse_plugin.toad.model.frame.FlowpartFrameEditPart;
import jp.hishidama.eclipse_plugin.toad.model.frame.FlowpartFrameNode;
import jp.hishidama.eclipse_plugin.toad.model.frame.JobFrameEditPart;
import jp.hishidama.eclipse_plugin.toad.model.frame.JobFrameNode;
import jp.hishidama.eclipse_plugin.toad.model.marker.MarkerEditPart;
import jp.hishidama.eclipse_plugin.toad.model.marker.MarkerNode;
import jp.hishidama.eclipse_plugin.toad.model.node.datafile.DataFileEditPart;
import jp.hishidama.eclipse_plugin.toad.model.node.datafile.DataFileNode;
import jp.hishidama.eclipse_plugin.toad.model.node.jobflow.JobEditPart;
import jp.hishidama.eclipse_plugin.toad.model.node.jobflow.JobNode;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.EllipseOperatorEditPart;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorEditPart;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;
import jp.hishidama.eclipse_plugin.toad.model.node.port.JobPort;
import jp.hishidama.eclipse_plugin.toad.model.node.port.JobPortEditPart;
import jp.hishidama.eclipse_plugin.toad.model.node.port.OpePort;
import jp.hishidama.eclipse_plugin.toad.model.node.port.OpePortEditPart;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

public class ToadEditPartFactory implements EditPartFactory {

	@Override
	public final EditPart createEditPart(EditPart editpart, Object model) {
		EditPart editPart = createEditPart(model);
		if (editPart == null) {
			if (model instanceof Diagram) {
				editPart = new DiagramEditPart();
			} else if (model instanceof Connection) {
				editPart = new ConnectionEditPart();
			} else {
				throw new UnsupportedOperationException(MessageFormat.format("model={0}", model));
			}
		}

		editPart.setModel(model);
		((AbstractModel) model).setEditPart(editPart);
		return editPart;
	}

	protected EditPart createEditPart(Object model) {
		if (model instanceof OperatorNode) {
			String text = ((OperatorNode) model).getEllipseFigureText();
			if (text != null) {
				return new EllipseOperatorEditPart(text);
			}
			return new OperatorEditPart();
		} else if (model instanceof OpePort) {
			return new OpePortEditPart();
		} else if (model instanceof JobNode) {
			return new JobEditPart();
		} else if (model instanceof JobFrameNode) {
			return new JobFrameEditPart();
		} else if (model instanceof FlowpartFrameNode) {
			return new FlowpartFrameEditPart();
		} else if (model instanceof JobPort) {
			return new JobPortEditPart();
		} else if (model instanceof DataFileNode) {
			return new DataFileEditPart();
		} else if (model instanceof MarkerNode) {
			return new MarkerEditPart();
		}
		return null;
	}
}
