package jp.hishidama.eclipse_plugin.toad.model.node.datafile;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElementFactory;

public class DataFileFactory extends NodeElementFactory {

	private String fileType;

	public DataFileFactory(ToadEditor editor, String fileType) {
		super(editor);
		this.fileType = fileType;
	}

	@Override
	public Object getNewObject() {
		DataFileNode node = new DataFileNode();

		int id = newId();
		node.setId(id);
		node.setDescription(getDescription());
		node.setClassName("");
		node.setFileType(fileType);

		return node;
	}

	private String getDescription() {
		if (fileType.endsWith("Importer")) {
			return "入力ファイル";
		} else if (fileType.endsWith("Exporter")) {
			return "出力ファイル";
		} else {
			return "ファイル";
		}
	}

	@Override
	public Object getObjectType() {
		return DataFileNode.class;
	}
}
