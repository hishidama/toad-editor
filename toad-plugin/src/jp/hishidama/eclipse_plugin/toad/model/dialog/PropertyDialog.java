package jp.hishidama.eclipse_plugin.toad.model.dialog;

import java.io.IOException;
import java.text.MessageFormat;

import jp.hishidama.eclipse_plugin.toad.Activator;
import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.model.AbstractModel;
import jp.hishidama.eclipse_plugin.toad.model.AbstractNameModel;
import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.dialog.section.AttributeSection;
import jp.hishidama.eclipse_plugin.toad.model.dialog.section.BaseSection;
import jp.hishidama.eclipse_plugin.toad.model.dialog.section.ClassNameSection;
import jp.hishidama.eclipse_plugin.toad.model.dialog.section.ClassNameSection.SelectToadFileHandler;
import jp.hishidama.eclipse_plugin.toad.model.dialog.section.DataModelSection;
import jp.hishidama.eclipse_plugin.toad.model.dialog.section.MemoSection;
import jp.hishidama.eclipse_plugin.toad.model.dialog.section.PropertySection;
import jp.hishidama.eclipse_plugin.toad.model.gson.ToadGson;
import jp.hishidama.eclipse_plugin.toad.model.node.ClassNameNode;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;
import jp.hishidama.eclipse_plugin.toad.model.property.attribute.HasAttributeNode;
import jp.hishidama.eclipse_plugin.toad.model.property.datamodel.HasDataModelNode;
import jp.hishidama.eclipse_plugin.toad.model.property.generic.NameNode;
import jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.page.FlowFileCreationPage;
import jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.page.FlowpartFileCreationPage;
import jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.page.JobflowFileCreationPage;
import jp.hishidama.eclipse_plugin.util.FileUtil;
import jp.hishidama.eclipse_plugin.util.ToadFileUtil;
import jp.hishidama.xtext.dmdl_editor.dmdl.ModelUiUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.ui.IEditorPart;

public abstract class PropertyDialog extends PropertyDialogWizard {
	protected BaseSection baseSection;
	protected MemoSection memoSection;
	protected ClassNameSection classNameSection;
	protected DataModelSection dataModelSection;
	private AttributeSection attrTab;

	public PropertyDialog(String title, ToadEditor editor, AbstractModel model) {
		this(title, editor, model, false);
	}

	public PropertyDialog(String title, ToadEditor editor, AbstractModel model, boolean directEdit) {
		super(editor, model, directEdit);
		setWindowTitle(title);
	}

	/*
	 * 基本
	 */
	protected Composite createBasicTabItem(TabFolder tab) {
		return PropertySection.createTabItem(tab, "Basic");
	}

	protected void createBaseSection(Composite composite) {
		baseSection = new BaseSection(this, (AbstractNameModel) model);
		baseSection.createSection(composite);
	}

	protected void createMemoTab(TabFolder tab) {
		memoSection = new MemoSection(this, model);
		memoSection.createMemoTab(tab);
	}

	/*
	 * クラス名
	 */
	protected void createClassNameSection(Composite composite) {
		classNameSection = new ClassNameSection(this, (ClassNameNode) model);
		classNameSection.createSection(composite);
	}

	protected void setSelectToadFileHandler(SelectToadFileHandler handler) {
		classNameSection.setSelectToadFileHandler(handler);
	}

	/*
	 * データモデル
	 */
	protected void createModelNameSection(Composite composite) {
		createModelNameSection(composite, true);
	}

	protected void createModelNameSection(Composite composite, boolean enableOpen) {
		dataModelSection = new DataModelSection(this, (HasDataModelNode) model, enableOpen);
		dataModelSection.createModelNameSection(composite);
	}

	protected void createModelLayoutTab(TabFolder tab) {
		dataModelSection.createModelLayoutTab(tab);
	}

	/*
	 * 属性
	 */
	protected void createAttributeTab(TabFolder tab) {
		attrTab = new AttributeSection(this, (HasAttributeNode) model);
		attrTab.createTab(tab);
	}

	/*
	 * 適用
	 */
	@Override
	protected final void apply() {
		if (directEdit) {
			return;
		}
		CompoundCommand compound = new CompoundCommand();
		addComamnd(compound);
		execute(compound.unwrap());
	}

	protected void addComamnd(CompoundCommand compound) {
		srcModel.getCommand(editor, compound, model);
		if (dataModelSection != null) {
			dataModelSection.addCommand(compound);
		}
	}

	/*
	 * ユーティリティー
	 */
	public void openModel(String modelName) {
		apply();
		try {
			if (ModelUiUtil.openEditor(project, modelName)) {
				this.close();
			} else {
				MessageDialog.openError(null, "open error",
						MessageFormat.format("DataModel not found. name={0}", modelName));
			}
		} catch (Exception e) {
			IStatus status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, "file open error", e);
			e.printStackTrace();
			ErrorDialog.openError(null, "open error", MessageFormat.format("open error. data_model={0}", modelName),
					status);
		}
	}

	public void openClass(String className, String methodName) {
		apply();
		try {
			if (FileUtil.openFile(project, className, methodName)) {
				this.close();
				return;
			}
			MessageDialog.openWarning(null, "open error", MessageFormat.format("open error. className={0}", className));
		} catch (Exception e) {
			IStatus status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, "file open error", e);
			e.printStackTrace();
			ErrorDialog.openError(null, "open error", MessageFormat.format("open error. className={0}", className),
					status);
		}
	}

	public void openToadFile(String path) {
		IFile file = ToadFileUtil.getFile(project, path);
		if (file == null) {
			return;
		}
		if (!file.exists()) {
			boolean ok = MessageDialog.openConfirm(null, "confirm create",
					MessageFormat.format("{0} is not exists.\nDoes it create?", file.getFullPath().toPortableString()));
			if (!ok) {
				return;
			}
			try {
				FileUtil.createFolder(project, file);
				initializeFileContents(file);
			} catch (CoreException e) {
				IStatus status = e.getStatus();
				ErrorDialog.openError(null, "create error", MessageFormat.format("create error. file={0}", path),
						status);
				return;
			} catch (Exception e) {
				IStatus status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, "file create error", e);
				e.printStackTrace();
				ErrorDialog.openError(null, "create error", MessageFormat.format("create error. file={0}", path),
						status);
				return;
			}
		}

		IEditorPart e = ToadFileUtil.openFile(file);
		if (e != null) {
			this.close();
		}
	}

	private void initializeFileContents(IFile file) throws CoreException, IOException {
		FlowFileCreationPage page;
		if (model instanceof OperatorNode) {
			page = new FlowpartFileCreationPage(file.getProject(), (OperatorNode) model);
		} else {
			page = new JobflowFileCreationPage(file.getProject());
		}
		Diagram diagram = page.generateDiagram();
		if (model instanceof NameNode) {
			diagram.setName(((NameNode) model).getName());
		}
		if (model instanceof AbstractNameModel) {
			diagram.setDescription(((AbstractNameModel) model).getDescription());
		}
		if (model instanceof ClassNameNode) {
			diagram.setClassName(((ClassNameNode) model).getClassName());
		}
		ToadGson gson = new ToadGson();
		gson.save(file, diagram);
	}
}
