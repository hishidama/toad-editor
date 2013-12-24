package jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.page;

import java.io.IOException;

import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.gson.ToadGson;
import jp.hishidama.eclipse_plugin.util.FileUtil;
import jp.hishidama.eclipse_plugin.util.ToadFileUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public abstract class DiagramFileCreationPage extends WizardPage {
	protected final IProject project;
	private final String toadFileExtension;
	private String packageName = "";

	private Text diagramName;
	private Text diagramDescription;
	private Text diagramClassName;

	protected DiagramFileCreationPage(String pageName, IProject project, String ext) {
		super(pageName);
		this.project = project;
		this.toadFileExtension = ext;
	}

	public void setPackageName(String packageName) {
		if (packageName != null) {
			this.packageName = packageName;
		}
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		{
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			composite.setLayout(new GridLayout(2, false));
		}

		initializeComposite(composite);

		setControl(composite);
	}

	protected void initializeComposite(Composite composite) {
		ModifyListener listener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				validate(true);
			}
		};

		Text projectText = createTextField(composite, "project");
		if (project != null) {
			projectText.setText(project.getName());
		}
		projectText.setEditable(false);

		if (hasName()) {
			diagramName = createTextField(composite, "name");
			diagramName.addModifyListener(listener);
		}
		diagramDescription = createTextField(composite, "description");
		diagramDescription.addModifyListener(listener);
		diagramClassName = createTextField(composite, "class name");
		diagramClassName.setText(packageName);
		diagramClassName.addModifyListener(listener);

		validate(false);
	}

	protected boolean hasName() {
		return true;
	}

	protected Text createTextField(Composite composite, String labelText) {
		Label label = new Label(composite, SWT.NONE);
		label.setText(labelText);

		Text text = new Text(composite, SWT.SINGLE | SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return text;
	}

	void validate(boolean putError) {
		if (project == null) {
			setErrorMessage("プロジェクトが指定されていません。一旦ウィザードを閉じて、プロジェクトを選択してから再度実行して下さい。");
			setPageComplete(false);
			return;
		}

		if (diagramName != null) {
			String message = validateName(diagramName.getText().trim());
			if (message != null) {
				setPageComplete(false);
				if (putError) {
					setErrorMessage(message);
				}
				return;
			}
		}
		{
			String message = validateDescription(diagramDescription.getText().trim());
			if (message != null) {
				setPageComplete(false);
				if (putError) {
					setErrorMessage(message);
				}
				return;
			}
		}
		{
			String message = validateClassName(diagramClassName.getText().trim());
			if (message != null) {
				setPageComplete(false);
				if (putError) {
					setErrorMessage(message);
				}
				return;
			}
		}

		setPageComplete(true);
		setErrorMessage(null);
	}

	protected abstract String validateName(String name);

	private String validateDescription(String desc) {
		if (desc.isEmpty()) {
			return "descriptionを入力して下さい。";
		}
		return null;
	}

	private String validateClassName(String className) {
		if (className.isEmpty()) {
			return "クラス名を入力して下さい。";
		}

		IFile file = ToadFileUtil.getToadFile(project, className, toadFileExtension);
		if (file.exists()) {
			return file.getProjectRelativePath().toPortableString() + " は既に存在しています。";
		}

		IStatus s = JavaConventions.validateJavaTypeName(className, JavaCore.VERSION_1_6, JavaCore.VERSION_1_6);
		if (!s.isOK()) {
			return s.getMessage();
		}

		return null;
	}

	public IFile createNewFile() throws CoreException, IOException {
		Diagram diagram = generateDiagram();

		String name = (diagramName != null) ? diagramName.getText().trim() : null;
		String description = diagramDescription.getText().trim();
		String className = diagramClassName.getText().trim();
		setTo(diagram, name, description, className);

		IFile file = saveFile(diagram);
		return file;
	}

	protected abstract Diagram generateDiagram();

	protected abstract void setTo(Diagram diagram, String name, String description, String className);

	private IFile saveFile(Diagram diagram) throws CoreException, IOException {
		String className = diagram.getClassName();
		IFile file = ToadFileUtil.getToadFile(project, className, toadFileExtension);
		FileUtil.createFolder(project, file);

		ToadGson gson = new ToadGson();
		gson.save(file, diagram);

		return file;
	}
}
