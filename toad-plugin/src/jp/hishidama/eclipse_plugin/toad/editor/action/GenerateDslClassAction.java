package jp.hishidama.eclipse_plugin.toad.editor.action;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.jdt.util.TypeUtil;
import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.editor.handler.GenerateDslClassHandler;
import jp.hishidama.eclipse_plugin.toad.editor.handler.GenerateDslClassHandler.Target;
import jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen.OperatorClassAst;
import jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen.PorterClassGenerator;
import jp.hishidama.eclipse_plugin.toad.internal.LogUtil;
import jp.hishidama.eclipse_plugin.toad.model.AbstractNameModel;
import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.diagram.DiagramEditPart;
import jp.hishidama.eclipse_plugin.toad.model.node.ClassNameNode;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElementEditPart;
import jp.hishidama.eclipse_plugin.toad.model.node.datafile.DataFileNode;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;
import jp.hishidama.eclipse_plugin.toad.validation.ToadValidator;
import jp.hishidama.eclipse_plugin.toad.validation.ValidateType;
import jp.hishidama.eclipse_plugin.util.FileUtil;
import jp.hishidama.eclipse_plugin.util.StringUtil;
import jp.hishidama.eclipse_plugin.util.ToadFileUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;

public class GenerateDslClassAction extends SelectionAction {
	public static final String ID = "TOAD_DSL_GENARATE";

	private final IProject project;

	public GenerateDslClassAction(ToadEditor editor) {
		super(editor);
		this.project = editor.getProject();
	}

	@Override
	protected void init() {
		super.init();
		setText("Generate DSL (Java class)");
		setToolTipText("generate DSL skelton");
		setId(ID);
	}

	@Override
	protected boolean calculateEnabled() {
		List<AbstractNameModel> list = getSelectedObjects();
		return !list.isEmpty();
	}

	@Override
	protected List<AbstractNameModel> getSelectedObjects() {
		List<?> list = super.getSelectedObjects();
		List<AbstractNameModel> result = new ArrayList<AbstractNameModel>(list.size());
		for (Object obj : list) {
			if (obj instanceof NodeElementEditPart) {
				NodeElementEditPart part = (NodeElementEditPart) obj;
				NodeElement model = part.getModel();
				if (model instanceof OperatorNode) {
					OperatorNode operator = (OperatorNode) model;
					if (operator.isUserOperator()) {
						result.add(operator);
					}
				} else if (model instanceof DataFileNode) {
					result.add(model);
				}
			} else if (obj instanceof DiagramEditPart) {
				DiagramEditPart part = (DiagramEditPart) obj;
				Diagram model = part.getModel();
				result.add(model);
			}
		}
		return result;
	}

	@Override
	public void run() {
		Object result = null;

		List<AbstractNameModel> list = getSelectedObjects();
		if (!confirm(list)) {
			return;
		}

		for (AbstractNameModel node : list) {
			try {
				if (node instanceof OperatorNode) {
					IMethod method = generateOperator((OperatorNode) node);
					if (result == null) {
						result = method;
					}
				} else if (node instanceof DataFileNode) {
					IFile file = generatePorter((DataFileNode) node);
					if (result == null) {
						result = file;
					}
				} else if (node instanceof Diagram) {
					IFile file = generateDslClass((Diagram) node);
					if (result == null) {
						result = file;
					}
				} else {
					throw new UnsupportedOperationException("node=" + node);
				}
			} catch (Exception e) {
				IStatus status = LogUtil.logError("DSLクラス生成時に例外発生 node=" + node, e);
				String message = MessageFormat.format("DSLクラスの生成時に例外が発生しました。\nnode={0}", node);
				ErrorDialog.openError(null, "Generate DSL error", message, status);
			}
		}

		if (result != null) {
			if (result instanceof IJavaElement) {
				IJavaElement element = (IJavaElement) result;
				try {
					JavaUI.openInEditor(element);
				} catch (Exception e) {
					IStatus status = LogUtil.logError("JavaElement open error.", e);
					String message = MessageFormat.format("Javaソースを開く際に例外が発生しました。\njavaElement={0}", element);
					ErrorDialog.openError(null, "Generate DSL error", message, status);
				}
			} else {
				IFile file = (IFile) result;
				FileUtil.openEditor(file);
			}
		}
	}

	private boolean confirm(List<AbstractNameModel> list) {
		StringBuilder sb = new StringBuilder(256);
		sb.append("以下のJavaソースを生成します。よろしいですか？\n");
		for (AbstractNameModel model : list) {
			sb.append("\n");
			if (model instanceof OperatorNode) {
				OperatorNode operator = (OperatorNode) model;
				sb.append(operator.getClassName());
				sb.append("#");
				sb.append(operator.getMethodName());
				sb.append("()");
			} else {
				ClassNameNode node = (ClassNameNode) model;
				sb.append(node.getClassName());
			}
		}

		boolean r = MessageDialog.openConfirm(null, "Confirm Generage DSL (Java class)", sb.toString());
		return r;
	}

	private IMethod generateOperator(OperatorNode operator) throws CoreException {
		List<IStatus> result = new ArrayList<IStatus>();
		operator.validate(ValidateType.GENERATE, false, result);
		String message = ToadValidator.getErrorMessage(result);
		if (message != null) {
			MessageDialog.openError(null, "Operator DSL (Java class) generate error", message);
			return null;
		}

		String className = operator.getClassName();
		String methodName = operator.getMethodName();

		IJavaProject javaProject = JavaCore.create(project);
		IType type = findType(javaProject, className);
		if (type == null) {
			createNewOperatorFile(className);
			type = findType(javaProject, className);
		}
		return modifyMethod(type, operator, methodName);
	}

	private void createNewOperatorFile(String className) throws CoreException {
		IFile file = ToadFileUtil.getJavaFile(project, className);
		try {
			FileUtil.createFolder(project, file);
		} catch (CoreException e) {
			LogUtil.logError("create folder error.", e);
		}
		StringBuilder sb = new StringBuilder(256);
		sb.append("package ");
		sb.append(StringUtil.getPackageName(className));
		sb.append(";\n\n");
		sb.append("public abstract class ");
		sb.append(StringUtil.getSimpleName(className));
		sb.append(" {\n}\n");
		FileUtil.save(file, sb.toString());
	}

	private IMethod modifyMethod(IType type, OperatorNode operator, String methodName) {
		OperatorClassAst ast = new OperatorClassAst(type);
		try {
			ast.addMethod(operator.getSourceCode());
		} finally {
			ast.close();
		}
		return TypeUtil.findMethod(type, methodName);
	}

	private static IType findType(IJavaProject javaProject, String className) {
		try {
			return javaProject.findType(className);
		} catch (JavaModelException e) {
			return null;
		}
	}

	private IFile generatePorter(DataFileNode node) throws CoreException {
		List<IStatus> result = new ArrayList<IStatus>();
		node.validate(ValidateType.GENERATE, false, result);
		String message = ToadValidator.getErrorMessage(result);
		if (message != null) {
			MessageDialog.openError(null, "Importer/Exporter class generate error", message);
			return null;
		}

		String className = node.getClassName();
		IFile file = ToadFileUtil.getJavaFile(project, className);
		try {
			FileUtil.createFolder(project, file);
		} catch (CoreException e) {
			LogUtil.logError("create folder error.", e);
		}

		GenerateDslClassHandler handler = new GenerateDslClassHandler();
		PorterClassGenerator generator = new PorterClassGenerator(project, node);
		handler.generateDslClass(file, generator);

		return file;
	}

	private IFile generateDslClass(Diagram diagram) throws CoreException {
		List<IStatus> result = new ArrayList<IStatus>();
		diagram.validate(ValidateType.IMPLEMENTS, false, result);
		String message = ToadValidator.getErrorMessage(result);
		if (message != null) {
			MessageDialog.openError(null, "DSL (Java class) generate error", message);
			return null;
		}

		GenerateDslClassHandler handler = new GenerateDslClassHandler();
		Target target = handler.generateDslClass(project, diagram);
		if (target == null) {
			return null;
		}
		return target.file;
	}
}
