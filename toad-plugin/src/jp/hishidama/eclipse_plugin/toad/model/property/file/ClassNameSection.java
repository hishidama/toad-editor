package jp.hishidama.eclipse_plugin.toad.model.property.file;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

import jp.hishidama.eclipse_plugin.toad.Activator;
import jp.hishidama.eclipse_plugin.toad.editor.handler.GenerateDslClassHandler;
import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.node.ClassNameNode;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;
import jp.hishidama.eclipse_plugin.toad.model.property.TextSection;
import jp.hishidama.eclipse_plugin.util.FileUtil;
import jp.hishidama.eclipse_plugin.util.ToadFileUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.ui.IEditorPart;

public class ClassNameSection extends TextSection<ClassNameNode> {

	public ClassNameSection() {
		super("class name");
	}

	@Override
	protected String getValue(ClassNameNode model) {
		return model.getClassName();
	}

	@Override
	protected String getButtonText() {
		return "open";
	}

	@Override
	protected void buttonEvent(ClassNameNode model, SelectionEvent event) {
		try {
			IProject project = getProject();
			IJavaProject javaProject = JavaCore.create(project);
			IType type = javaProject.findType(model.getClassName());
			if (type != null) {
				IMethod method = findMethod(model, type);
				if (method != null) {
					IEditorPart part = JavaUI.openInEditor(method);
					if (part != null) {
						return;
					}
				}
				IEditorPart part = JavaUI.openInEditor(type);
				if (part != null) {
					return;
				}
				MessageDialog.openError(null, "open error", MessageFormat.format("open error. type={0}", type));
				return;
			}
			MessageDialog.openWarning(null, "open error",
					MessageFormat.format("class not found. className={0}", model.getClassName()));
			// openFile(project, model, event);
		} catch (Exception e) {
			IStatus status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, "file open error", e);
			e.printStackTrace();
			ErrorDialog.openError(null, "open error",
					MessageFormat.format("open error. className={0}", model.getClassName()), status);
		}
	}

	private IMethod findMethod(ClassNameNode model, IType type) throws JavaModelException {
		if (!(model instanceof OperatorNode)) {
			return null;
		}
		OperatorNode operator = (OperatorNode) model;
		String opeName = operator.getMethodName();
		if (opeName == null) {
			return null;
		}
		for (IMethod m : type.getMethods()) {
			String name = m.getElementName();
			if (opeName.equals(name)) {
				return m;
			}
		}
		return null;
	}

	// TODO 廃止（クラス生成はウィザードへ）
	void openFile(IProject project, ClassNameNode model, SelectionEvent event) throws CoreException, IOException {
		IFile file = ToadFileUtil.getJavaFile(project, model);
		if (!file.exists()) {
			boolean ok = MessageDialog.openConfirm(null, "confirm create",
					MessageFormat.format("{0} is not exists.\nDoes it create?", file.getFullPath().toPortableString()));
			if (!ok) {
				event.doit = false;
				return;
			}
			FileUtil.createFolder(project, file);
			initializeFileContents(file, model);
		}

		String className = model.getClassName();
		ToadFileUtil.openFile(file, className);
	}

	private void initializeFileContents(IFile file, ClassNameNode model) throws CoreException {
		if (model instanceof Diagram) {
			Diagram diagram = (Diagram) model;
			GenerateDslClassHandler handler = new GenerateDslClassHandler();
			handler.generateDslClass(file.getProject(), diagram);
			return;
		}

		InputStream is;
		try {
			is = new ByteArrayInputStream("".getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		try {
			file.create(is, false, null);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
