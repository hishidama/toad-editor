package jp.hishidama.eclipse_plugin.toad.importer;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.asakusafw.vocabulary.external.ExporterDescription;
import com.asakusafw.vocabulary.flow.graph.FlowElement;
import com.asakusafw.vocabulary.flow.graph.FlowElementPort;
import com.asakusafw.vocabulary.flow.graph.OutputDescription;

public class RevOutput extends RevNode {

	private final int id;
	private final int fileId;
	private final OutputDescription description;

	public RevOutput(AtomicInteger id, FlowElement flowElement) {
		super(flowElement);
		this.id = id.incrementAndGet();
		this.fileId = id.incrementAndGet();
		this.description = (OutputDescription) flowElement.getDescription();
	}

	@Override
	public Kind getKind() {
		return Kind.OUTPUT;
	}

	@Override
	public int getId(FlowElementPort port) {
		return id;
	}

	@Override
	protected void toMap(Map<String, Object> map) {
		map.put("id", id);
		map.put("name", description.getName());
		map.put("modelClassName", getClassName(description.getDataType()));

		ExporterDescription edesc = description.getExporterDescription();
		map.put("fileId", fileId);
		map.put("fileClassName", getClassName(edesc.getClass()));
		map.put("fileModelClassName", getClassName(edesc.getModelType()));
	}
}
