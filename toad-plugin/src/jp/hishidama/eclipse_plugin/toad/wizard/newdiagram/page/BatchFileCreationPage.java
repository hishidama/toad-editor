package jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.page;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.gen.BatchDiagramGenerator;

import org.eclipse.core.resources.IProject;

public class BatchFileCreationPage extends DiagramFileCreationPage {

	public BatchFileCreationPage(IProject project) {
		super("BatchFileCreationPage", project, "btoad");

		setTitle("バッチの指定");
		setDescription("作成するバッチの情報を入力して下さい。\n"
				+ "（例：クラス名がcom.example.batch.Ex1Batchのとき、src/main/toad/com/example/batch/Ex1Batch.btoadが作られます）");
	}

	@Override
	protected String validateName(String name) {
		if (name.isEmpty()) {
			// TODO 実装時なら必須
			// return "バッチ名を入力して下さい。";
			return null;
		}

		if (!matches(name)) {
			return "バッチ名は英数字（先頭は英字のみ）とそれらをピリオドでつないだものしか使用できません。";
		}

		return null;
	}

	private static final Pattern namePattern = Pattern.compile("[a-zA-Z][a-zA-Z0-9]*");

	private boolean matches(String name) {
		String[] ss = name.split("\\.", -1);
		for (String s : ss) {
			Matcher matcher = namePattern.matcher(s);
			if (!matcher.matches()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Diagram generateDiagram() {
		BatchDiagramGenerator generator = new BatchDiagramGenerator();
		return generator.createEmptyDiagram();
	}

	@Override
	protected void setTo(Diagram diagram, String name, String description, String className) {
		diagram.setName(name);
		diagram.setDescription(description);
		diagram.setClassName(className);
	}
}
