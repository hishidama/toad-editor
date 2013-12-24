package jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.gen;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import jp.hishidama.eclipse_plugin.toad.clazz.JobFlowClass;
import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.diagram.DiagramType;
import jp.hishidama.eclipse_plugin.toad.model.frame.FrameNode;
import jp.hishidama.eclipse_plugin.toad.model.frame.JobFrameNode;
import jp.hishidama.eclipse_plugin.toad.model.node.datafile.DataFileNode;
import jp.hishidama.eclipse_plugin.toad.model.node.port.JobPort;
import jp.hishidama.eclipse_plugin.util.ToadJavaUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class JobDiagramGenerator extends FlowDiagramGenerator {

	public JobDiagramGenerator(IProject project) {
		super(project);
	}

	public static JobFlowClass findJobflow(IFile file) {
		ICompilationUnit unit = ToadJavaUtil.getJavaUnit(file);
		if (unit == null) {
			return null;
		}
		return findJobflow(unit);
	}

	public static JobFlowClass findJobflow(ICompilationUnit unit) {
		try {
			IType[] types = unit.getTypes();
			for (IType type : types) {
				JobFlowClass flow = new JobFlowClass(type);
				if (flow.isDsl()) {
					return flow;
				}
			}
		} catch (JavaModelException e) {
			return null;
		}
		return null;
	}

	public Diagram generateDiagram(JobFlowClass job) throws IOException {
		return generateDiagram(job.getType(), job.getJavadoc(), job.getJobFlowId(), job.getParameterNames(),
				Collections.<String, Object> emptyMap());
	}

	@Override
	protected FrameNode createEmptyFrame() {
		JobFrameNode frame = new JobFrameNode();
		return frame;
	}

	@Override
	protected void initializeDiagram(Diagram diagram) {
		diagram.setDiagramType(DiagramType.JOBFLOW);
		diagram.setType("Jobflow");
	}

	@Override
	public void initialize() {
		super.initialize();
		((JobFrameNode) frame).setName(diagram.getName());
	}

	@Override
	protected void createInputFileNode(Map<String, Object> port, JobPort c) {
		DataFileNode f = createFile(port, c.getCy());
		f.setX(frame.getX() - f.getWidth() - 32);
		diagram.addContent(f);
		createConnection(f, c);
	}

	@Override
	protected void createOutputFileNode(Map<String, Object> port, JobPort c) {
		DataFileNode f = createFile(port, c.getCy());
		f.setX(frame.getX() + frame.getWidth() + 32);
		diagram.addContent(f);
		createConnection(c, f);
	}
}
