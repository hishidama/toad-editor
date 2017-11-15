package jp.hishidama.eclipse_plugin.toad.jdt.hyperlink;

import java.util.List;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.model.diagram.DiagramEditPart;
import jp.hishidama.eclipse_plugin.toad.model.frame.FrameNode;
import jp.hishidama.eclipse_plugin.toad.model.node.port.JobPort;
import jp.hishidama.eclipse_plugin.util.ToadFileUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorPart;

public class ToadFrameHyperlink implements IHyperlink {

	private IFile file;
	private String name;
	private IRegion region;

	public ToadFrameHyperlink(IFile file, String name, IRegion region) {
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

			DiagramEditPart diagram = editor.getDiagramEditPart();
			EditPart found;
			if (name == null) {
				found = findEditPart(diagram);
			} else {
				found = findPort(diagram, name);
			}
			if (found == null) {
				found = diagram;
			}
			EditPartViewer viewer = diagram.getViewer();
			viewer.select(found);
			viewer.reveal(found);
		}
	}

	private EditPart findEditPart(DiagramEditPart diagram) {
		@SuppressWarnings("unchecked")
		List<EditPart> list = diagram.getChildren();
		for (EditPart part : list) {
			Object obj = part.getModel();
			if (obj instanceof FrameNode) {
				return part;
			}
		}
		return null;
	}

	private EditPart findPort(DiagramEditPart diagram, String name) {
		@SuppressWarnings("unchecked")
		List<EditPart> list = diagram.getChildren();
		for (EditPart part : list) {
			Object obj = part.getModel();
			if (obj instanceof JobPort) {
				if (name.equals(((JobPort) obj).getName())) {
					return part;
				}
			}
		}
		return null;
	}
}
