package jp.hishidama.eclipse_plugin.toad.model.dialog;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.dialog.section.ClassNameSection.SelectToadFileHandler;
import jp.hishidama.eclipse_plugin.toad.model.gson.ToadGson;
import jp.hishidama.eclipse_plugin.toad.model.node.jobflow.JobNode;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;

public class JobflowPropertyDialog extends PropertyDialog implements SelectToadFileHandler {

	public JobflowPropertyDialog(ToadEditor editor, JobNode job) {
		super("ジョブフロー", editor, job);
	}

	@Override
	protected void createFields(TabFolder tab) {
		createBasicTab(tab);
		createMemoTab(tab);
	}

	private void createBasicTab(TabFolder tab) {
		Composite composite = createBasicTabItem(tab);
		createBaseSection(composite);
		createClassNameSection(composite);
		setSelectToadFileHandler(this);
	}

	@Override
	public void handleSelectToadFile(String toadPath, String className) {
		Diagram diagram = loadDiagram(toadPath);
		if (diagram != null) {
			baseSection.setName(diagram.getName());
			baseSection.setDescription(diagram.getDescription());
		}
	}

	private Diagram loadDiagram(String toadPath) {
		IFile file = project.getFile(toadPath);
		if (file.exists()) {
			ToadGson gson = new ToadGson();
			try {
				return gson.load(file);
			} catch (CoreException e) {
				return null;
			}
		}
		return null;
	}
}
