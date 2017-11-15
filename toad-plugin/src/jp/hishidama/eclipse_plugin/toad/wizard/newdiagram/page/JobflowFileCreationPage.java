package jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.page;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.frame.JobFrameNode;
import jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.gen.FlowDiagramGenerator;
import jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.gen.JobDiagramGenerator;

import org.eclipse.core.resources.IProject;

public class JobflowFileCreationPage extends FlowFileCreationPage {

	public JobflowFileCreationPage(IProject project) {
		super("JobflowFileCreationPage", project, "jtoad");

		setTitle("ジョブフローの指定");
		setDescription("作成するジョブフローの情報を入力して下さい。\n"
				+ "（例：クラス名がcom.example.jobflow.Ex1Jobのとき、src/main/toad/com/example/jobflow/Ex1Job.jtoadが作られます）");
	}

	private static final Pattern namePattern = Pattern.compile("[a-zA-Z][a-zA-Z0-9]*");

	@Override
	protected String validateName(String name) {
		if (name.isEmpty()) {
			// TODO 実装時なら必須
			// return "ジョブフロー名を入力して下さい。";
			return null;
		}

		Matcher matcher = namePattern.matcher(name);
		if (!matcher.matches()) {
			return "ジョブフロー名は英数字（先頭は英字のみ）しか使用できません。";
		}

		return null;
	}

	@Override
	protected FlowDiagramGenerator createGenerator() {
		return new JobDiagramGenerator(project);
	}

	@Override
	protected void setTo(Diagram diagram, String name, String description, String className) {
		super.setTo(diagram, name, description, className);

		JobFrameNode frame = (JobFrameNode) diagram.getFrameNode();
		frame.setName(name);
	}
}
