package jp.hishidama.eclipse_plugin.toad.editor.action;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.model.AbstractModel;
import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;
import jp.hishidama.eclipse_plugin.toad.model.diagram.DiagramEditPart;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.validation.ToadMarker;
import jp.hishidama.eclipse_plugin.toad.validation.ValidateType;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

public class ValidateAction extends SelectionAction {

	public static String getId(ValidateType type) {
		return "TOAD_VALIDATE." + type.name();
	}

	private ValidateType type;

	public ValidateAction(ToadEditor editor, ValidateType type) {
		super(editor);
		this.type = type;
		setId(getId(type));
		setText(type.getMenuName());
		setToolTipText(type.getDescription());
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}

	protected final ToadEditor getEditor() {
		return (ToadEditor) getWorkbenchPart();
	}

	@Override
	public void run() {
		Set<String> idSet = new HashSet<String>();
		Set<AbstractModel> list = getTargetObjects(idSet);
		if (list.isEmpty()) {
			return;
		}

		IFile file = getEditor().getFile();
		deleteMarkers(file, idSet);
		int count = validate(file, list);
		if (count == 0) {
			MessageDialog.openInformation(null, "Validate result", "エラーはありませんでした。");
		} else {
			String ID = "org.eclipse.ui.views.ProblemView";
			IWorkbenchPage page = getEditor().getSite().getWorkbenchWindow().getActivePage();
			try {
				page.showView(ID, null, IWorkbenchPage.VIEW_VISIBLE);
			} catch (PartInitException e) {
				page.findView(ID);
			}

			String message = MessageFormat.format("{0}個のエラーまたは警告が見つかりました。\n詳細は「問題ビュー」で確認して下さい。", count);
			MessageDialog.openWarning(null, "Validate result", message);
		}
	}

	private Set<AbstractModel> getTargetObjects(Set<String> idSet) {
		List<?> selected = getSelectedObjects();
		if (selected.isEmpty()) {
			selected = Arrays.asList(getEditor().getDiagramEditPart());
		}
		Set<AbstractModel> list = new LinkedHashSet<AbstractModel>(selected.size() * 5);
		for (Object obj : selected) {
			if (obj instanceof DiagramEditPart) {
				addAll(list, (DiagramEditPart) obj);
				idSet.clear();
				break;
			} else if (obj instanceof AbstractGraphicalEditPart) {
				AbstractGraphicalEditPart part = (AbstractGraphicalEditPart) obj;
				AbstractModel model = (AbstractModel) part.getModel();
				list.add(model);
				idSet.add(model.getIdString());
			}
		}
		return list;
	}

	private void addAll(Set<AbstractModel> list, AbstractGraphicalEditPart editPart) {
		if (list.contains(editPart.getModel())) {
			return;
		}
		for (Object c : editPart.getChildren()) {
			if (c instanceof AbstractGraphicalEditPart) {
				AbstractGraphicalEditPart part = (AbstractGraphicalEditPart) c;
				AbstractModel model = (AbstractModel) part.getModel();
				if (list.contains(model)) {
					continue;
				}
				list.add(model);
				if (model instanceof NodeElement) {
					NodeElement node = (NodeElement) model;
					for (Connection conn : node.getIncomings()) {
						list.add(conn);
					}
					for (Connection conn : node.getOutgoings()) {
						list.add(conn);
					}
				}
				addAll(list, part);
			}
		}
	}

	private void deleteMarkers(IFile file, Set<String> idSet) {
		if (idSet.isEmpty()) {
			ToadMarker.deleteAllMarkers(file);
		} else {
			ToadMarker.deleteMarkers(file, idSet);
		}
	}

	private int validate(IFile file, Set<AbstractModel> list) {
		int count = 0;
		for (AbstractModel model : list) {
			List<IStatus> results = new ArrayList<IStatus>();
			model.validate(type, false, results);
			for (IStatus s : results) {
				if (s != null && !s.isOK()) {
					ToadMarker.createMarker(file, model, s);
					count++;
				}
			}
		}
		return count;
	}
}
