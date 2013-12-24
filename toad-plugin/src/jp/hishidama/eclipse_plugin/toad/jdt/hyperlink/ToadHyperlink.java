package jp.hishidama.eclipse_plugin.toad.jdt.hyperlink;

import java.util.List;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.model.diagram.DiagramEditPart;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;
import jp.hishidama.eclipse_plugin.toad.model.property.generic.NameNode;
import jp.hishidama.eclipse_plugin.util.ToadFileUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorPart;

public class ToadHyperlink implements IHyperlink {

	private IFile file;
	private String name;
	private IRegion region;

	public ToadHyperlink(IFile file, String name, IRegion region) {
		this.file = file;
		this.name = name;
		this.region = region;
	}

	@Override
	public IRegion getHyperlinkRegion() {
		return region;
	}

	@Override
	public String getTypeLabel() {
		return "Open Asakusa Toad diagram";
	}

	@Override
	public String getHyperlinkText() {
		return "Open Asakusa Toad diagram";
	}

	@Override
	public void open() {
		IEditorPart editPart = ToadFileUtil.openFile(file);
		if (editPart instanceof ToadEditor) {
			ToadEditor editor = (ToadEditor) editPart;

			if (name != null) {
				DiagramEditPart diagram = editor.getDiagramEditPart();
				EditPart found = findEditPart(diagram);
				if (found != null) {
					EditPartViewer viewer = diagram.getViewer();
					viewer.select(found);
					viewer.reveal(found);
				}
			}
		}
	}

	private EditPart findEditPart(DiagramEditPart diagram) {
		EditPart found = null;

		@SuppressWarnings("unchecked")
		List<EditPart> list = diagram.getChildren();
		for (EditPart part : list) {
			Object obj = part.getModel();
			if (obj instanceof OperatorNode) {
				OperatorNode operator = (OperatorNode) obj;
				if (name.equals(operator.getMethodName())) {
					if (found == null) {
						found = part;
					} else {
						return null; // 複数見つかったので、ひとつに絞れない
					}
				}
			} else if (obj instanceof NameNode) {
				NameNode model = (NameNode) obj;
				if (name.equals(model.getName())) {
					if (found == null) {
						found = part;
					} else {
						return null; // 複数見つかったので、ひとつに絞れない
					}
				}
			}
		}
		return found;
	}
}
