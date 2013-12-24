package jp.hishidama.eclipse_plugin.toad.editor.drop;

import jp.hishidama.eclipse_plugin.toad.clazz.OperatorMethod;
import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.editor.drop.nodegen.OperatorNodeGenerator;
import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;
import jp.hishidama.eclipse_plugin.toad.model.node.port.OpePort;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.dnd.DropTargetEvent;

public class ToadMethodDropTargetListener extends ToadDropTargetListener {

	public ToadMethodDropTargetListener(ToadEditor editor, EditPartViewer viewer) {
		super(editor, viewer, LocalSelectionTransfer.getTransfer());
	}

	@Override
	protected Command getCommand() {
		DropTargetEvent event = getCurrentEvent();
		if (!(event.data instanceof TreeSelection)) {
			return null;
		}
		TreeSelection selection = (TreeSelection) event.data;
		TreePath[] paths = selection.getPaths();
		TreePath path = paths[0];
		Object seg = path.getLastSegment();
		if (!(seg instanceof IMethod)) {
			return null;
		}
		IMethod method = (IMethod) seg;
		return getJavaCommand(method);
	}

	private Command getJavaCommand(IMethod method) {
		Diagram diagram = (Diagram) getTargetEditPart().getModel();
		switch (diagram.getDiagramType()) {
		case JOBFLOW:
		case FLOWPART:
			OperatorMethod operator = new OperatorMethod(method);
			if (operator.isDsl()) {
				return getCreateOperatorCommand(diagram, operator);
			}
			break;
		default:
			break;
		}
		return null;
	}

	private Command getCreateOperatorCommand(Diagram diagram, OperatorMethod operator) {
		OperatorNodeGenerator gen = new OperatorNodeGenerator(editor.getProject()) {
			@Override
			protected int newPortId(OpePort port, boolean in, String name) {
				return editor.newId();
			}
		};
		OperatorNode node = gen.createOperatorNode(operator, editor.newId());
		return newCreateNodeCommand(diagram, node);
	}
}
