package jp.hishidama.eclipse_plugin.toad.importer;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.asakusafw.vocabulary.external.ImporterDescription;
import com.asakusafw.vocabulary.flow.graph.FlowElement;
import com.asakusafw.vocabulary.flow.graph.FlowElementPort;
import com.asakusafw.vocabulary.flow.graph.InputDescription;

public class RevInput extends RevNode {

	private final int id;
	private final int fileId;
	private final InputDescription description;

	public RevInput(AtomicInteger id, FlowElement flowElement) {
		super(flowElement);
		this.id = id.incrementAndGet();
		this.fileId = id.incrementAndGet();
		this.description = (InputDescription) flowElement.getDescription();
	}

	@Override
	public Kind getKind() {
		return Kind.INPUT;
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

		ImporterDescription idesc = description.getImporterDescription();
		if (idesc != null) {
			map.put("fileId", fileId);
			map.put("fileClassName", getClassName(idesc.getClass()));
			map.put("fileModelClassName", getClassName(idesc.getModelType()));
			map.put("fileDataSize", idesc.getDataSize().name());
		}
	}
}
