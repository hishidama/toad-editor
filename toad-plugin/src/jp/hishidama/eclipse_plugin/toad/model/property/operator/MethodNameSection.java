package jp.hishidama.eclipse_plugin.toad.model.property.operator;

import java.io.IOException;
import java.text.MessageFormat;

import jp.hishidama.eclipse_plugin.toad.Activator;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;
import jp.hishidama.eclipse_plugin.toad.model.property.TextSection;
import jp.hishidama.eclipse_plugin.util.FileUtil;
import jp.hishidama.eclipse_plugin.util.ToadFileUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionEvent;

public class MethodNameSection extends TextSection<OperatorNode> {

	public MethodNameSection() {
		super("method");
	}

	@Override
	protected String getValue(OperatorNode model) {
		return model.getMethodName();
	}

	@Override
	protected String getButtonText() {
		return "open";
	}

	@Override
	protected void buttonEvent(OperatorNode model, SelectionEvent event) {
		try {
			IProject project = getProject();
			if (openFile(project, model)) {
				return;
			}
			MessageDialog.openWarning(
					null,
					"open error",
					MessageFormat.format("open error. className={0}, methodName={1}", model.getClassName(),
							model.getMethodName()));
		} catch (Exception e) {
			IStatus status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, "file open error", e);
			e.printStackTrace();
			ErrorDialog.openError(
					null,
					"open error",
					MessageFormat.format("open error. className={0}, methodName={1}", model.getClassName(),
							model.getMethodName()), status);
		}
	}

	private boolean openFile(IProject project, OperatorNode model) throws CoreException, IOException {
		IFile file = ToadFileUtil.getJavaFile(project, model);
		if (!file.exists()) {
			return false;
		}

		String className = model.getClassName();
		String methodName = model.getMethodName();
		return FileUtil.openFile(file, className, methodName);
	}
}
