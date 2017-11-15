package jp.hishidama.eclipse_plugin.toad.editor.drop;

import jp.hishidama.eclipse_plugin.toad.clazz.FileClass;
import jp.hishidama.eclipse_plugin.toad.clazz.FlowPartClass;
import jp.hishidama.eclipse_plugin.toad.clazz.JavadocClass;
import jp.hishidama.eclipse_plugin.toad.clazz.JobFlowClass;
import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.editor.drop.nodegen.FlowpartNodeGenerator;
import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.diagram.DiagramType;
import jp.hishidama.eclipse_plugin.toad.model.gson.ToadGson;
import jp.hishidama.eclipse_plugin.toad.model.node.datafile.DataFileNode;
import jp.hishidama.eclipse_plugin.toad.model.node.jobflow.JobNode;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;
import jp.hishidama.eclipse_plugin.toad.model.node.port.OpePort;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;

public class ToadFileDropTargetListener extends ToadDropTargetListener {

	public ToadFileDropTargetListener(ToadEditor editor, EditPartViewer viewer) {
		super(editor, viewer, FileTransfer.getInstance());
	}

	@Override
	protected Command getCommand() {
		DropTargetEvent event = getCurrentEvent();
		String[] names = (String[]) event.data;
		String filePath = names[0];

		IProject project = editor.getProject();
		IPath projectPath = project.getLocation();
		IPath path = Path.fromOSString(filePath).makeRelativeTo(projectPath);
		IFile file = project.getFile(path);
		if (!file.exists()) {
			return null;
		}
		String ext = file.getFileExtension();
		if ("java".equals(ext)) {
			return getJavaCommand(file);
		} else if ("btoad".equals(ext) || "jtoad".equals(ext) || "ftoad".equals(ext)) {
			return getToadCommand(file);
		} else {
			return null;
		}
	}

	private Command getJavaCommand(IFile file) {
		Diagram diagram = (Diagram) getTargetEditPart().getModel();
		IJavaElement element = JavaCore.create(file);
		if (element == null) {
			return null;
		}
		IType[] types;
		try {
			types = ((ICompilationUnit) element).getAllTypes();
		} catch (JavaModelException e) {
			return null;
		}
		for (IType type : types) {
			switch (diagram.getDiagramType()) {
			case BATCH:
				JobFlowClass job = new JobFlowClass(type);
				if (job.isDsl()) {
					return getCreateJobCommand(diagram, job);
				}
				break;
			case JOBFLOW:
				FileClass porter = new FileClass(type);
				if (porter.isDsl()) {
					return getCreateFileCommand(diagram, porter);
				}
				// fall through
			case FLOWPART:
				FlowPartClass flowpart = new FlowPartClass(type);
				if (flowpart.isDsl()) {
					return getCreateFlowpartCommand(diagram, flowpart);
				}
				break;
			default:
				break;
			}
		}

		return null;
	}

	private Command getCreateJobCommand(Diagram diagram, JobFlowClass job) {
		JobNode node = new JobNode();
		node.setId(editor.newId());
		node.setName(job.getJobFlowId());
		String className = job.getClassName();
		node.setClassName(className);

		JavadocClass javadoc = job.getJavadoc();
		node.setDescription(javadoc.getTitle());
		node.setMemo(javadoc.getMemo());

		return newCreateNodeCommand(diagram, node);
	}

	private Command getCreateFileCommand(Diagram diagram, FileClass file) {
		DataFileNode node = new DataFileNode();
		node.setId(editor.newId());
		String className = file.getClassName();
		node.setClassName(className);
		node.load(file.getType().getJavaProject());

		return newCreateNodeCommand(diagram, node);
	}

	private Command getCreateFlowpartCommand(Diagram diagram, FlowPartClass flowpart) {
		FlowpartNodeGenerator gen = new FlowpartNodeGenerator(editor.getProject()) {
			@Override
			protected int newPortId(OpePort port, boolean in, String name) {
				return editor.newId();
			}
		};
		OperatorNode node = gen.createFlowpart(flowpart, editor.newId());
		return newCreateNodeCommand(diagram, node);
	}

	private Command getToadCommand(IFile file) {
		Diagram diagram = (Diagram) getTargetEditPart().getModel();

		Diagram source;
		try {
			ToadGson gson = new ToadGson();
			source = gson.load(file);
		} catch (CoreException e) {
			return null;
		}

		switch (diagram.getDiagramType()) {
		case BATCH:
			if (source.getDiagramType() == DiagramType.JOBFLOW) {
				return getCreateJobCommand(diagram, source);
			}
			break;
		case JOBFLOW:
		case FLOWPART:
			if (source.getDiagramType() == DiagramType.FLOWPART) {
				return getCreateFlowpartCommand(diagram, source);
			}
			break;
		default:
			break;
		}

		return null;
	}

	private Command getCreateJobCommand(Diagram diagram, Diagram source) {
		JobNode node = new JobNode();
		node.setId(editor.newId());
		node.setName(source.getName());
		node.setDescription(source.getDescription());
		node.setMemo(source.getMemo());
		node.setClassName(source.getClassName());

		return newCreateNodeCommand(diagram, node);
	}

	private Command getCreateFlowpartCommand(Diagram diagram, Diagram source) {
		FlowpartNodeGenerator gen = new FlowpartNodeGenerator(editor.getProject()) {
			@Override
			protected int newPortId(OpePort port, boolean in, String name) {
				return editor.newId();
			}
		};
		OperatorNode node = gen.createFlowpart(source, editor.newId());
		return newCreateNodeCommand(diagram, node);
	}
}
