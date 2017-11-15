package jp.hishidama.eclipse_plugin.toad.model.gson;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

import jp.hishidama.eclipse_plugin.toad.Activator;
import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.gen.BatchDiagramGenerator;
import jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.gen.FlowpartDiagramGenerator;
import jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.gen.JobDiagramGenerator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class ToadGson {

	private static final String ENC = "UTF-8";

	public void save(IFile file, Diagram diagram) throws CoreException, IOException {
		ByteArrayInputStream is = new ByteArrayInputStream(serialize(file.getName(), diagram, null));
		try {
			if (file.exists()) {
				file.setContents(is, true, false, null);
			} else {
				file.create(is, true, null);
			}
		} finally {
			is.close();
		}
	}

	public byte[] serialize(String fileIdentifier, Diagram diagram, IProgressMonitor monitor) throws IOException {
		return serialize(fileIdentifier, diagram).getBytes(ENC);
	}

	public String serialize(String fileIdentifier, Diagram diagram) throws IOException {
		StringWriter sw = new StringWriter(4096);
		JsonWriter writer = new JsonWriter(sw);
		IOException save = null;
		try {
			writer.setIndent("  ");
			Gson gson = createGson(fileIdentifier);

			diagram.prepareSave();
			try {
				gson.toJson(diagram, diagram.getClass(), writer);
			} finally {
				diagram.postSave();
			}
		} catch (Exception e) {
			save = new IOException(e);
			throw save;
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				if (save == null) {
					throw e;
				}
			}
		}
		return sw.toString();
	}

	public Diagram load(IProject project, File file) throws IOException {
		try {
			Reader reader = new InputStreamReader(new FileInputStream(file), ENC);
			return load(project, file.getAbsolutePath(), reader);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	public Diagram load(IFile file) throws CoreException {
		try {
			Reader reader = new InputStreamReader(file.getContents(true), ENC);
			return load(file.getProject(), file.getName(), reader);
		} catch (Exception e) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "load error", e);
			throw new CoreException(status);
		}
	}

	public Diagram load(IProject project, String fileIdentifier, Reader in) {
		Diagram diagram;
		{
			JsonReader reader = new JsonReader(in);
			Gson gson = createGson(fileIdentifier);
			diagram = gson.fromJson(reader, Diagram.class);
		}

		if (diagram != null) {
			diagram.postLoad();
		} else {
			// ファイルが空のとき
			diagram = createEmptyDiagram(project, fileIdentifier);
		}

		return diagram;
	}

	private Gson createGson(String fileIdentifier) {
		return new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
				.registerTypeAdapter(NodeElement.class, new NodeElementTypeAdapter(fileIdentifier)).create();
	}

	private Diagram createEmptyDiagram(IProject project, String fileIdentifier) {
		if (fileIdentifier.endsWith(".btoad")) {
			BatchDiagramGenerator gen = new BatchDiagramGenerator();
			return gen.createEmptyDiagram();
		}
		if (fileIdentifier.endsWith(".jtoad")) {
			JobDiagramGenerator gen = new JobDiagramGenerator(project);
			return gen.createEmptyDiagram();
		}
		if (fileIdentifier.endsWith(".ftoad")) {
			FlowpartDiagramGenerator gen = new FlowpartDiagramGenerator(project, null);
			return gen.createEmptyDiagram();
		}

		return new Diagram();
	}
}
