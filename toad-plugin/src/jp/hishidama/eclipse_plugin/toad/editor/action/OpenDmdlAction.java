package jp.hishidama.eclipse_plugin.toad.editor.action;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElementEditPart;
import jp.hishidama.eclipse_plugin.toad.model.property.datamodel.HasDataModelNode;
import jp.hishidama.eclipse_plugin.util.StringUtil;
import jp.hishidama.xtext.dmdl_editor.dmdl.ModelUiUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.gef.ui.actions.SelectionAction;

public class OpenDmdlAction extends SelectionAction {
	public static final String ID = "TOAD_OPEN_DMDL";

	private final IProject project;

	public OpenDmdlAction(ToadEditor editor) {
		super(editor);
		this.project = editor.getProject();
	}

	@Override
	protected void init() {
		super.init();
		setText("Open DMDL");
		setToolTipText("open DMDL");
		setId(ID);
	}

	@Override
	protected boolean calculateEnabled() {
		List<HasDataModelNode> list = getSelectedObjects();
		return list.size() == 1;
	}

	@Override
	protected List<HasDataModelNode> getSelectedObjects() {
		List<?> list = super.getSelectedObjects();
		List<HasDataModelNode> result = new ArrayList<HasDataModelNode>(list.size());
		for (Object obj : list) {
			if (obj instanceof NodeElementEditPart) {
				NodeElementEditPart part = (NodeElementEditPart) obj;
				NodeElement model = part.getModel();
				if (model instanceof HasDataModelNode) {
					result.add((HasDataModelNode) model);
				}
			}
		}
		return result;
	}

	public void run() {
		List<HasDataModelNode> list = getSelectedObjects();
		for (HasDataModelNode node : list) {
			String modelName = node.getModelName();
			if (StringUtil.isEmpty(modelName)) {
				return;
			}
			ModelUiUtil.openEditor(project, modelName);
		}
	}
}
