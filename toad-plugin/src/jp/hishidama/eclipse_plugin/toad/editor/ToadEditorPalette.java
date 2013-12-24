package jp.hishidama.eclipse_plugin.toad.editor;

import jp.hishidama.eclipse_plugin.toad.internal.extension.DefaultImporterProperty;
import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;
import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.diagram.DiagramType;
import jp.hishidama.eclipse_plugin.toad.model.node.datafile.DataFileFactory;
import jp.hishidama.eclipse_plugin.toad.model.node.jobflow.JobFactory;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.CoreOperatorFactory;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.UserOperatorFactory;

import org.eclipse.core.resources.IProject;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.requests.SimpleFactory;

public class ToadEditorPalette extends PaletteRoot {
	private ToadEditor editor;

	public ToadEditorPalette(ToadEditor editor) {
		this.editor = editor;

		addSelectionTool();
	}

	private void addSelectionTool() {
		PaletteGroup group = new PaletteGroup("選択ツール");
		group.add(new PanningSelectionToolEntry(null, "ノードを選択します。"));
		// group.add(new MarqueeToolEntry());
		group.add(new ConnectionCreationToolEntry("Connection",
				"ノード同士を接続します。\nノードをクリックしてから別のノードをクリックすると、その2つのノードを接続します。", new SimpleFactory(Connection.class), null,
				null));
		this.add(group);
	}

	public void addSecond(Diagram diagram, IProject project) {
		DiagramType t = diagram.getDiagramType();
		switch (t) {
		case BATCH:
			addEmptyNode(true, false, false);
			break;
		default:
			addEmptyNode(false, true, t == DiagramType.JOBFLOW);
			addEmpryOperator();
			break;
		}
	}

	private void addEmptyNode(boolean batch, boolean other, boolean file) {
		PaletteDrawer drawer = new PaletteDrawer("空要素");
		if (other) {
			// drawer.add(new CombinedTemplateCreationEntry("data model",
			// "empty data model", new DataModelFactory(a), null, null));
			if (file) {
				drawer.add(new CombinedTemplateCreationEntry("インポーター", "Importer", new DataFileFactory(editor,
						DefaultImporterProperty.NAME + " Importer"), null, null));
				drawer.add(new CombinedTemplateCreationEntry("エクスポーター", "Exporter", new DataFileFactory(editor,
						DefaultImporterProperty.NAME + " Exporter"), null, null));
			}
		}
		if (batch) {
			drawer.add(new CombinedTemplateCreationEntry("ジョブフロー", "JobFlow", new JobFactory(editor), null, null));
		}
		if (other) {
			// drawer.add(new CombinedTemplateCreationEntry("job frame",
			// "job frame", new JobFrameFactory(a), null, null));
		}
		if (!drawer.getChildren().isEmpty()) {
			this.add(drawer);
		}
	}

	private void addEmpryOperator() {
		PaletteDrawer drawer = new PaletteDrawer("演算子");
		{
			UserOperatorFactory factory = new UserOperatorFactory(editor, "@Branch");
			factory.setGroup("フロー制御");
			factory.setDescription("分岐演算子");
			factory.setMemo("データの内容に応じて処理を分岐させる演算子です。");
			factory.addIn("in:入力");
			factory.addOut("out1:出力1", "out2:出力2");
			add(drawer, factory);
		}
		{
			CoreOperatorFactory factory = new CoreOperatorFactory(editor, "confluent");
			factory.setGroup("フロー制御");
			factory.setDescription("合流演算子");
			factory.setMemo("複数の入力を合流して1つにまとめて出力する演算子です。");
			add(drawer, factory);
		}
		{
			UserOperatorFactory factory = new UserOperatorFactory(editor, "@Update");
			factory.setGroup("データ操作");
			factory.setDescription("更新演算子");
			factory.setMemo("レコードの内容を更新する演算子です。");
			factory.addIn("in:入力");
			factory.addOut("out:出力");
			add(drawer, factory);
		}
		{
			UserOperatorFactory factory = new UserOperatorFactory(editor, "@Convert");
			factory.setGroup("データ操作");
			factory.setDescription("変換演算子");
			factory.setMemo("レコードを別のデータモデルに変換する演算子です。");
			factory.addIn("in:入力");
			factory.addOut("out:変換", "original:無変換");
			add(drawer, factory);
		}
		{
			CoreOperatorFactory factory = new CoreOperatorFactory(editor, "extend");
			factory.setGroup("データ操作");
			factory.setDescription("拡張演算子");
			factory.setMemo("プロパティーが追加されたデータモデルに変換する演算子です。");
			factory.addIn("in:入力");
			factory.addOut("out:出力");
			add(drawer, factory);
		}
		{
			CoreOperatorFactory factory = new CoreOperatorFactory(editor, "project");
			factory.setGroup("データ操作");
			factory.setDescription("射影演算子");
			factory.setMemo("プロパティーが除去されたデータモデルに変換する演算子です。");
			factory.addIn("in:入力");
			factory.addOut("out:出力");
			add(drawer, factory);
		}
		{
			CoreOperatorFactory factory = new CoreOperatorFactory(editor, "restructure");
			factory.setGroup("データ操作");
			factory.setDescription("再構築演算子");
			factory.setMemo("プロパティーが追加削除されたデータモデルに変換する演算子です。");
			factory.addIn("in:入力");
			factory.addOut("out:出力");
			add(drawer, factory);
		}
		{
			UserOperatorFactory factory = new UserOperatorFactory(editor, "@Extract");
			factory.setGroup("データ操作");
			factory.setDescription("抽出演算子");
			factory.setMemo("レコードからデータを抽出して複数のレコードを出力する演算子です。");
			factory.addIn("in:入力");
			factory.addOut("out:出力");
			add(drawer, factory);
		}
		{
			UserOperatorFactory factory = new UserOperatorFactory(editor, "@MasterCheck");
			factory.setGroup("結合");
			factory.setDescription("マスター確認演算子");
			factory.setMemo("マスターデータの存在有無に応じて出力先を振り分ける演算子です。");
			factory.addIn("master:マスター", "tx:トランザクション");
			factory.addOut("found:マスター有り", "missed:マスター無し");
			add(drawer, factory);
		}
		{
			UserOperatorFactory factory = new UserOperatorFactory(editor, "@MasterJoin");
			factory.setGroup("結合");
			factory.setDescription("マスター結合演算子");
			factory.setMemo("マスターデータと結合したレコードを出力する演算子です。");
			factory.addIn("master:マスター", "tx:トランザクション");
			factory.addOut("joined:結合済", "missed:マスター無し");
			add(drawer, factory);
		}
		{
			UserOperatorFactory factory = new UserOperatorFactory(editor, "@MasterBranch");
			factory.setGroup("結合");
			factory.setDescription("マスター分岐演算子");
			factory.setMemo("マスターデータの内容に応じて処理を分岐させる演算子です。");
			factory.addIn("master:マスター", "tx:トランザクション");
			factory.addOut("out1:出力1", "out2:出力2");
			add(drawer, factory);
		}
		{
			UserOperatorFactory factory = new UserOperatorFactory(editor, "@MasterJoinUpdate");
			factory.setGroup("結合");
			factory.setDescription("マスターつき更新演算子");
			factory.setMemo("マスターデータを使ってレコードの内容を更新する演算子です。");
			factory.addIn("master:マスター", "tx:トランザクション");
			factory.addOut("updated:更新後", "missed:マスター無し");
			add(drawer, factory);
		}
		{
			UserOperatorFactory factory = new UserOperatorFactory(editor, "@CoGroup");
			factory.setGroup("結合");
			factory.setDescription("グループ結合演算子");
			factory.setMemo("複数の入力をキーでグループ化して複数のレコードを出力する演算子です。");
			factory.addIn("in1:入力1", "in2:入力2");
			factory.addOut("out1:出力1", "out2:出力2");
			add(drawer, factory);
		}
		{
			UserOperatorFactory factory = new UserOperatorFactory(editor, "@Split");
			factory.setGroup("結合");
			factory.setDescription("分割演算子");
			factory.setMemo("結合モデルを結合元のデータモデルに分割する演算子です。");
			factory.addIn("in:入力");
			factory.addOut("left:出力1", "right:出力2");
			add(drawer, factory);
		}
		{
			UserOperatorFactory factory = new UserOperatorFactory(editor, "@Summarize");
			factory.setGroup("集計");
			factory.setDescription("単純集計演算子");
			factory.setMemo("キー毎に集計を行う演算子です。");
			factory.addIn("in:入力");
			factory.addOut("out:集計結果");
			add(drawer, factory);
		}
		{
			UserOperatorFactory factory = new UserOperatorFactory(editor, "@Fold");
			factory.setGroup("集計");
			factory.setDescription("畳み込み演算子");
			factory.setMemo("キー毎に畳み込みを行う演算子です。");
			factory.addIn("in:入力");
			factory.addOut("out:集計結果");
			add(drawer, factory);
		}
		{
			UserOperatorFactory factory = new UserOperatorFactory(editor, "@GroupSort");
			factory.setGroup("集計");
			factory.setDescription("グループ整列演算子");
			factory.setMemo("入力をキーでグループ化・ソートして複数のレコードを出力する演算子です。");
			factory.addIn("in:入力");
			factory.addOut("out1:出力1", "out2:出力2");
			add(drawer, factory);
		}
		{
			UserOperatorFactory factory = new UserOperatorFactory(editor, "FlowPart");
			factory.setGroup("特殊");
			factory.setDescription("フロー演算子");
			factory.setMemo("フロー部品を表す演算子です。");
			factory.addIn("in:入力");
			factory.addOut("out:出力");
			add(drawer, factory);
		}
		{
			CoreOperatorFactory factory = new CoreOperatorFactory(editor, "checkpoint");
			factory.setGroup("特殊");
			factory.setDescription("チェックポイント演算子");
			factory.setMemo("処理の途中結果を保存するチェックポイントを生成する演算子です。\n普通は使用しません。");
			factory.addIn("in:入力");
			factory.addOut("out:出力");
			add(drawer, factory);
		}
		{
			UserOperatorFactory factory = new UserOperatorFactory(editor, "@Logging");
			factory.setGroup("特殊");
			factory.setDescription("ロギング演算子");
			factory.setMemo("レコード毎のアプリケーションログを出力する演算子です。");
			factory.addIn("in:入力");
			factory.addOut("out:出力");
			add(drawer, factory);
		}
		{
			CoreOperatorFactory factory = new CoreOperatorFactory(editor, "empty");
			factory.setGroup("特殊");
			factory.setDescription("空演算子");
			factory.setMemo("0件データを生成する演算子です。");
			add(drawer, factory);
		}
		{
			CoreOperatorFactory factory = new CoreOperatorFactory(editor, "stop");
			factory.setGroup("特殊");
			factory.setDescription("停止演算子");
			factory.setMemo("どこにも出力しないデータの出力先として使用する演算子です。");
			add(drawer, factory);
		}
		this.add(drawer);
	}

	private void add(PaletteDrawer drawer, UserOperatorFactory factory) {
		String label = factory.getDescription();
		String desc = String.format("%s %s\n%s", factory.getGroup(), factory.getName(), factory.getMemo());
		drawer.add(new CombinedTemplateCreationEntry(label, desc, factory, null, null));
	}
}
