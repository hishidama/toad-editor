package jp.hishidama.eclipse_plugin.toad.model.node.jobflow;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElementFactory;

public class JobFactory extends NodeElementFactory {

	public JobFactory(ToadEditor editor) {
		super(editor);
	}

	@Override
	public JobNode getNewObject() {
		JobNode node = new JobNode();
		{
			int id = newId();
			node.setId(id);
			node.setName("undefined" + id);
			node.setDescription("ジョブ" + id);
			node.setClassName("com.example.jobflow.MyJob");
		}

		return node;
	}

	@Override
	public Object getObjectType() {
		return JobNode.class;
	}
}
