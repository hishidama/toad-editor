package jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.task;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import jp.hishidama.eclipse_plugin.toad.clazz.BatchClass;
import jp.hishidama.eclipse_plugin.toad.clazz.FlowPartClass;
import jp.hishidama.eclipse_plugin.toad.clazz.JobFlowClass;
import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.gson.ToadGson;
import jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.gen.BatchDiagramGenerator;
import jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.gen.FlowpartDiagramGenerator;
import jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.gen.JobDiagramGenerator;
import jp.hishidama.eclipse_plugin.util.FileUtil;
import jp.hishidama.eclipse_plugin.util.ToadFileUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class GenerateDiagramTask implements IRunnableWithProgress {
	private final List<IFile> list;
	private final Map<String, Object> values;

	private IFile first;

	public GenerateDiagramTask(List<IFile> list, Map<String, Object> values) {
		this.list = list;
		this.values = values;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		monitor.beginTask("ダイアグラムファイルの作成", list.size());
		try {
			first = null;
			for (IFile file : list) {
				if (monitor.isCanceled()) {
					throw new InterruptedException();
				}
				monitor.subTask(file.getFullPath().toPortableString());
				try {
					IFile target = generateDiagramFile(file);
					if (target != null && first == null) {
						first = target;
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (CoreException e) {
					e.printStackTrace();
				}
				monitor.worked(1);
			}
		} finally {
			monitor.done();
		}
	}

	private IFile generateDiagramFile(IFile file) throws IOException, CoreException {
		Diagram diagram = generateDiagram(file);
		if (diagram == null) {
			return null;
		}
		IProject project = file.getProject();
		String className = diagram.getClassName();
		IFile target = ToadFileUtil.getToadFile(project, className, diagram.getToadFileExtension());
		FileUtil.createFolder(project, target);
		ToadGson gson = new ToadGson();
		gson.save(target, diagram);
		return target;
	}

	private Diagram generateDiagram(IFile file) throws IOException {
		BatchClass batch = BatchDiagramGenerator.findBatch(file);
		if (batch != null) {
			BatchDiagramGenerator generator = new BatchDiagramGenerator();
			return generator.generateDiagram(batch);
		}
		JobFlowClass job = JobDiagramGenerator.findJobflow(file);
		if (job != null) {
			JobDiagramGenerator generator = new JobDiagramGenerator(file.getProject());
			return generator.generateDiagram(job);
		}
		FlowPartClass flowpart = FlowpartDiagramGenerator.findFlowpart(file);
		if (flowpart != null) {
			FlowpartDiagramGenerator generator = new FlowpartDiagramGenerator(file.getProject(), null);
			return generator.generateDiagram(flowpart, values);
		}
		return null;
	}

	public IFile getFirstFile() {
		return first;
	}
}
