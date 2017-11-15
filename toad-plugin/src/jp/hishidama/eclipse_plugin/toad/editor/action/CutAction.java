package jp.hishidama.eclipse_plugin.toad.editor.action;

import java.util.List;

import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.internal.WorkbenchMessages;

@SuppressWarnings("restriction")
public class CutAction extends SelectionAction {

	private CopyAction copyAction;
	private IAction deleteAction;

	public CutAction(IWorkbenchPart part, ActionRegistry registry) {
		super(part);
		copyAction = (CopyAction) registry.getAction(ActionFactory.COPY.getId());
		deleteAction = registry.getAction(ActionFactory.DELETE.getId());
	}

	@Override
	protected void init() {
		setId(ActionFactory.CUT.getId());
		setText(WorkbenchMessages.Workbench_cut);
		setToolTipText(WorkbenchMessages.Workbench_cutToolTip);
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_CUT));
		setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_CUT_DISABLED));
	}

	@Override
	protected boolean calculateEnabled() {
		List<?> list = getSelectedObjects();
		return copyAction.enableCopyNode(list);
	}

	@Override
	public void run() {
		copyAction.run();
		deleteAction.run();
	}
}
