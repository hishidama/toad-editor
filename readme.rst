Asakusa Toad Editor
===================
Toad Editorは、
`GEF <http://www.ne.jp/asahi/hishidama/home/tech/eclipse/plugin/develop/gef/index.html>`_ を用いて作成している、
`Asakusa Framework <http://www.ne.jp/asahi/hishidama/home/tech/asakusafw/index.html>`_ の
`Batch DSL <http://www.ne.jp/asahi/hishidama/home/tech/asakusafw/batch_dsl.html>`_ ・
`Flow DSL <http://www.ne.jp/asahi/hishidama/home/tech/asakusafw/flow_dsl.html>`_
をGUIで描くツール（を目指した）エディター（Eclipseプラグイン）です。


インストール方法
----------------
Toad Editorは `DMDL EditorX <https://github.com/hishidama/xtext-dmdl-editor>`_ の機能を使用しているので、
DMDL EditorXをインストールしておく必要があります。

Toad Editorプラグインのインストールは、Eclipseの ``[新規ソフトウェアのインストール]`` で
更新サイトとして http://hishidama.github.io/toad-editor/site/ を指定して下さい。


出来ること
----------
詳細は `Toad Editorの説明サイト <http://www.ne.jp/asahi/hishidama/home/tech/soft/asakusafw/toad-editor/index.html>`_ を参照して下さい。

* Batch DSLをGUIで描き、Javaソース（Batchクラス）を生成する。
* Flow DSLをGUIで描き、Javaソース（JobFlow・FlowPart・Importer/Exporterクラス。および、Operatorの雛形）を生成する。
* JavaソースからBatch DSL・Flow DSLのtoadファイルを生成する。

