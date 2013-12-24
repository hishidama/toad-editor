package jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.gen;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.hishidama.eclipse_plugin.toad.clazz.BatchClass;
import jp.hishidama.eclipse_plugin.toad.clazz.JavadocClass;
import jp.hishidama.eclipse_plugin.toad.clazz.JobFlowClass;
import jp.hishidama.eclipse_plugin.toad.editor.action.layout.GefAutoLayout;
import jp.hishidama.eclipse_plugin.toad.internal.util.JarUtil;
import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.diagram.DiagramType;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.jobflow.JobNode;
import jp.hishidama.eclipse_plugin.util.StringUtil;
import jp.hishidama.eclipse_plugin.util.ToadJavaUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class BatchDiagramGenerator extends DiagramFileGenerator {

	public static BatchClass findBatch(IFile file) {
		ICompilationUnit unit = ToadJavaUtil.getJavaUnit(file);
		if (unit == null) {
			return null;
		}
		return findBatch(unit);
	}

	public static BatchClass findBatch(ICompilationUnit unit) {
		try {
			IType[] types = unit.getTypes();
			for (IType type : types) {
				BatchClass batch = new BatchClass(type);
				if (batch.isDsl()) {
					return batch;
				}
			}
		} catch (JavaModelException e) {
			return null;
		}
		return null;
	}

	@Override
	public Diagram createEmptyDiagram() {
		Diagram diagram = new Diagram();
		diagram.setDiagramType(DiagramType.BATCH);
		diagram.setType("Batch");
		return diagram;
	}

	public Diagram generateDiagram(BatchClass batch) throws IOException {
		IType type = batch.getType();
		String batchId = batch.getBatchId();

		Map<String, List<String>> map = getDependencies(type);

		IJavaProject javaProject = type.getJavaProject();

		Diagram diagram = createEmptyDiagram();
		diagram.setClassName(type.getFullyQualifiedName());
		diagram.setName(batchId);

		JavadocClass javadoc = batch.getJavadoc();
		diagram.setDescription(javadoc.getTitle());
		diagram.setMemo(javadoc.getMemo());

		Map<String, JobNode> jobMap = new HashMap<String, JobNode>();
		int id = 0;
		for (String name : map.keySet()) {
			JobNode job = createJob(javaProject, ++id, name);
			jobMap.put(name, job);
			diagram.addContent(job);
		}

		// connection
		for (Entry<String, List<String>> entry : map.entrySet()) {
			String name = entry.getKey();
			List<String> list = entry.getValue();

			JobNode job = jobMap.get(name);
			for (String source : list) {
				JobNode s = jobMap.get(source);
				if (s != null) {
					createConnection(s, job);
				}
			}
		}

		{ // layout
			List<NodeElement> list = new ArrayList<NodeElement>(jobMap.size());
			for (JobNode job : jobMap.values()) {
				list.add(job);
			}
			new GefAutoLayout().run(list, null);
		}

		return diagram;
	}

	private Map<String, List<String>> getDependencies(IType type) throws IOException {
		try {
			IJavaProject javaProject = type.getJavaProject();
			Class<?> clazz = JarUtil.loadClass(javaProject,
					"jp.hishidama.eclipse_plugin.toad.importer.GenerateBatchDiagram");

			Object generator = clazz.newInstance();
			Method getter = clazz.getMethod("getDependencies", String.class);

			@SuppressWarnings("unchecked")
			Map<String, List<String>> map = (Map<String, List<String>>) getter.invoke(generator,
					type.getFullyQualifiedName());
			return map;
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			String causeMessage = "Diagram生成中のエラー";
			for (Throwable t = e; t != null; t = t.getCause()) {
				String message = t.getMessage();
				if (message != null && !message.isEmpty()) {
					causeMessage = t.getMessage();
				}
			}
			throw new IOException(causeMessage, e);
		}
	}

	private JobNode createJob(IJavaProject javaProject, int id, String className) {
		JobNode job = new JobNode();
		job.setId(id);
		job.setClassName(className);
		job.setX(32);
		job.setY(32);
		try {
			JobFlowClass jobClass = new JobFlowClass(javaProject.findType(className));
			String jobId = jobClass.getJobFlowId();
			job.setName(jobId);
			JavadocClass javadoc = jobClass.getJavadoc();
			job.setDescription(javadoc.getTitle());
			job.setMemo(javadoc.getMemo());
		} catch (JavaModelException e) {
			job.setDescription(StringUtil.getSimpleName(className));
		}
		return job;
	}
}
