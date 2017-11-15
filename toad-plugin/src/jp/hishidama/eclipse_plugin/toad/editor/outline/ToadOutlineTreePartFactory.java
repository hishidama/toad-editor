package jp.hishidama.eclipse_plugin.toad.editor.outline;

import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

public class ToadOutlineTreePartFactory implements EditPartFactory {

	@Override
	public EditPart createEditPart(EditPart context, Object model) {
		EditPart part = null;
		if (model instanceof NodeElement) {
			part = new NodeTreePart();
		} else if (model instanceof Diagram) {
			part = new DiagramTreePart();
		} else if (model instanceof TreeGroup) {
			part = new TreeGroup.EditPart();
		}
		if (part != null) {
			part.setModel(model);
		}
		return part;
	}
}
