package jp.hishidama.eclipse_plugin.toad.editor.action;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.jdt.util.TypeUtil;
import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.model.node.ClassNameNode;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElementEditPart;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;
import jp.hishidama.xtext.dmdl_editor.ui.internal.LogUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.JavaUI;

public class OpenClassAction extends SelectionAction {
	public static final String ID = "TOAD_OPEN_CLASS";

	private final IProject project;

	public OpenClassAction(ToadEditor editor) {
		super(editor);
		this.project = editor.getProject();
	}

	@Override
	protected void init() {
		super.init();
		setText("Open DSL (Java class)");
		setToolTipText("open DSL class");
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
					result.add(node);
				}
			}
		}
		return result;
	}

	public void run() {
		IJavaProject javaProject = JavaCore.create(project);

		List<ClassNameNode> list = getSelectedObjects();
		for (ClassNameNode node : list) {
			try {
				IType type = javaProject.findType(node.getClassName());
				if (node instanceof OperatorNode) {
					OperatorNode operator = (OperatorNode) node;
					IMethod method = TypeUtil.findMethod(type, operator.getMethodName());
					if (method != null) {
						try {
							JavaUI.openInEditor(method);
							return;
						} catch (Exception e) {
						}
					}
				}
				JavaUI.openInEditor(type);
				return;
			} catch (Exception e) {
				LogUtil.logError("", e);
			}
		}
	}
}
