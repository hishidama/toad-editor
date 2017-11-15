package jp.hishidama.eclipse_plugin.toad.editor.action;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.model.node.ClassNameNode;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElementEditPart;
import jp.hishidama.eclipse_plugin.util.ToadFileUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.gef.ui.actions.SelectionAction;

public class OpenDiagramAction extends SelectionAction {
	public static final String ID = "TOAD_OPEN_DIAGRAM";

	private final IProject project;

	public OpenDiagramAction(ToadEditor editor) {
		super(editor);
		this.project = editor.getProject();
	}

	@Override
	protected void init() {
		super.init();
		setText("Open Toad diagram");
		setToolTipText("open Toad diagram");
		setId(ID);
	}

	@Override
	protected boolean calculateEnabled() {
		List<ClassNameNode> list = getSelectedObjects();
		return list.size() == 1;
	}

	@Override
	protected List<ClassNameNode> getSelectedObjects() {
		List<?> list = super.getSelectedObjects();
		List<ClassNameNode> result = new ArrayList<ClassNameNode>(list.size());
		for (Object obj : list) {
			if (obj instanceof NodeElementEditPart) {
				NodeElementEditPart part = (NodeElementEditPart) obj;
				NodeElement model = part.getModel();
				if (model instanceof ClassNameNode) {
					ClassNameNode node = (ClassNameNode) model;
					if (node.hasToadFile()) {
						result.add(node);
					}
				}
			}
		}
		return result;
	}

	public void run() {
		List<ClassNameNode> list = getSelectedObjects();
		for (ClassNameNode node : list) {
			String className = node.getClassName();
			String ext = node.getToadFileExtension();
			IFile file = ToadFileUtil.getToadFile(project, className, ext);
			if (file != null) {
				ToadFileUtil.openFile(file);
				return;
			}
		}
	}
}
