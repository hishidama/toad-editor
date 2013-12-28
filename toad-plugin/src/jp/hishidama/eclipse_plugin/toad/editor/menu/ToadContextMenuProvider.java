package jp.hishidama.eclipse_plugin.toad.editor.menu;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.toad.editor.action.AutoLayoutAction;
import jp.hishidama.eclipse_plugin.toad.editor.action.GenerateDslClassAction;
import jp.hishidama.eclipse_plugin.toad.editor.action.OpenClassAction;
import jp.hishidama.eclipse_plugin.toad.editor.action.OpenDiagramAction;
import jp.hishidama.eclipse_plugin.toad.editor.action.OpenDmdlAction;
import jp.hishidama.eclipse_plugin.toad.editor.action.SiblingDataModelAction;
import jp.hishidama.eclipse_plugin.toad.editor.action.ValidateAction;
import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.port.JobPort;
import jp.hishidama.eclipse_plugin.toad.validation.ValidateType;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.ActionFactory;

public class ToadContextMenuProvider extends ContextMenuProvider {

	private ActionRegistry registry;

	public ToadContextMenuProvider(EditPartViewer viewer, ActionRegistry registry) {
		super(viewer);
		this.registry = registry;
	}

	@Override
	public void buildContextMenu(IMenuManager menu) {
		GEFActionConstants.addStandardActionGroups(menu);

		menu.appendToGroup(GEFActionConstants.GROUP_UNDO, registry.getAction(ActionFactory.UNDO.getId()));
		menu.appendToGroup(GEFActionConstants.GROUP_UNDO, registry.getAction(ActionFactory.REDO.getId()));

		menu.appendToGroup(GEFActionConstants.GROUP_COPY, registry.getAction(ActionFactory.CUT.getId()));
		menu.appendToGroup(GEFActionConstants.GROUP_COPY, registry.getAction(ActionFactory.COPY.getId()));
		menu.appendToGroup(GEFActionConstants.GROUP_COPY, registry.getAction(ActionFactory.PASTE.getId()));

		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, registry.getAction(ActionFactory.DELETE.getId()));
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, registry.getAction(ActionFactory.SELECT_ALL.getId()));

		// @see ToadEditor#createActions()
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, registry.getAction(AutoLayoutAction.ID));
		menu.appendToGroup(GEFActionConstants.GROUP_VIEW, registry.getAction(SiblingDataModelAction.ID));

		EditPartViewer viewer = super.getViewer();
		List<?> list = viewer.getSelectedEditParts();
		if (list.size() == 1) {
			EditPart part = (EditPart) list.get(0);
			addJumpMenu(menu, part.getModel());
		}

		MenuManager open = new MenuManager("Open");
		{
			IAction maction = registry.getAction(OpenDmdlAction.ID);
			if (maction.isEnabled()) {
				open.add(maction);
			}
			IAction daction = registry.getAction(OpenDiagramAction.ID);
			if (daction.isEnabled()) {
				open.add(daction);
			}
			IAction caction = registry.getAction(OpenClassAction.ID);
			if (caction.isEnabled()) {
				open.add(caction);
			}
		}
		menu.appendToGroup(GEFActionConstants.GROUP_VIEW, open);

		MenuManager validate = new MenuManager("Validate");
		for (ValidateType type : ValidateType.values()) {
			validate.add(registry.getAction(ValidateAction.getId(type)));
		}
		menu.appendToGroup(GEFActionConstants.GROUP_VIEW, validate);

		// menu.appendToGroup(GEFActionConstants.GROUP_VIEW,
		// registry.getAction(ShowOperatorDslTemplateAction.ID));

		MenuManager generate = new MenuManager("Generate");
		{
			IAction action = registry.getAction(GenerateDslClassAction.ID);
			if (action.isEnabled()) {
				generate.add(action);
			}
		}
		menu.appendToGroup(GEFActionConstants.GROUP_VIEW, generate);
	}

	private void addJumpMenu(IMenuManager menu, Object model) {
		String menuName = "Jump Connected Node";

		List<IAction> in = new ArrayList<IAction>();
		List<IAction> out = new ArrayList<IAction>();
		collectConnectedNode(model, null, in, out, true, true);
		if (in.isEmpty() && out.isEmpty()) {
			menu.appendToGroup(GEFActionConstants.GROUP_VIEW, new DisableMenu(menuName));
		} else {
			MenuManager jump = new MenuManager(menuName);
			for (IAction action : in) {
				jump.add(action);
			}
			if (!out.isEmpty()) {
				jump.add(new Separator());
			}
			for (IAction action : out) {
				jump.add(action);
			}
			menu.appendToGroup(GEFActionConstants.GROUP_VIEW, jump);
		}
	}

	private void collectConnectedNode(Object model, String parent, List<IAction> in, List<IAction> out, boolean ein,
			boolean eout) {
		if (model instanceof Connection) {
			Connection c = (Connection) model;
			if (ein) {
				in.add(new JumpAction(parent, c.getSource()));
			}
			if (eout) {
				out.add(new JumpAction(parent, c.getTarget()));
			}
			return;
		}

		if (!(model instanceof NodeElement)) {
			return;
		}
		NodeElement node = (NodeElement) model;
		if (ein) {
			for (NodeElement t : node.getInputNodes()) {
				in.add(new JumpAction(parent, t));
			}
		}
		if (eout) {
			for (NodeElement t : node.getOutputNodes()) {
				out.add(new JumpAction(parent, t));
			}
		}
		for (NodeElement c : node.getChildren()) {
			boolean cin = ein, cout = eout;
			if (c instanceof JobPort) {
				cin = ((JobPort) c).isIn();
				cout = ((JobPort) c).isOut();
			}
			collectConnectedNode(c, c.getSimpleDisplayName(), in, out, cin, cout);
		}
	}

	private static class DisableMenu extends Action {
		public DisableMenu(String text) {
			super(text);
			setEnabled(false);
		}
	}

	private static class JumpAction extends Action {
		private NodeElement target;

		public JumpAction(String parent, NodeElement target) {
			this.target = target;
			String desc = target.getNodeDescription();
			String name = target.getDisplayName();
			String text = String.format("<%s> %s", desc, name);
			if (parent != null) {
				text = String.format("%s - %s", parent, text);
			}
			setText(text);
		}

		@Override
		public void run() {
			EditPart part = target.getEditPart();
			EditPartViewer viewer = part.getViewer();
			viewer.select(part);
			viewer.reveal(part);
		}
	}
}
