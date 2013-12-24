package jp.hishidama.eclipse_plugin.toad.editor.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen.OperatorMethodGenerator;
import jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen.OperatorMethodGenerator.TypeResolver;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;
import jp.hishidama.eclipse_plugin.util.StringUtil;

public class ShowOperatorDslTemplateAction extends OperatorDslAction {
	public static final String ID = "TOAD_OPERATOR_DSL_TEMPLATE";

	public ShowOperatorDslTemplateAction(ToadEditor editor) {
		super(editor);
	}

	@Override
	protected void init() {
		super.init();
		setText("Show Operator DSL");
		setToolTipText("show Operator DSL temlate");
		setId(ID);
	}

	@Override
	public void run() {
		List<OperatorNode> list = getSelectedObjects();

		Resolver resolver = new Resolver();
		StringBuilder sb = new StringBuilder(64 * list.size());
		for (OperatorNode operator : list) {
			buffering(operator, sb, resolver);
		}

		SourceCodeDialog dialog = new SourceCodeDialog(null, resolver.getImports(), sb.toString());
		dialog.open();
	}

	public void run(OperatorNode operator) {
		Resolver resolver = new Resolver();
		StringBuilder sb = new StringBuilder(256);
		buffering(operator, sb, resolver);

		SourceCodeDialog dialog = new SourceCodeDialog(null, resolver.getImports(), sb.toString());
		dialog.open();
	}

	private void buffering(OperatorNode operator, StringBuilder sb, Resolver resolver) {
		OperatorMethodGenerator generator = operator.getSourceCode();
		if (generator == null) {
			return;
		}

		generator.setProject(project);
		String text = generator.toSourceString(resolver);
		if (sb.length() != 0) {
			sb.append("\n\n");
		}
		sb.append(text);
	}

	private static class Resolver implements TypeResolver {

		private Map<String, String> map = new HashMap<String, String>();

		@Override
		public String resolve(String type) {
			if (type == null) {
				return null;
			}
			if (!type.contains(".")) {
				return type;
			}
			String sname = StringUtil.getSimpleName(type);
			String fname = map.get(sname);
			if (fname == null) {
				fname = type;
				map.put(sname, fname);
			}
			if (fname.equals(type)) {
				return sname;
			}
			return type;
		}

		public String getImports() {
			List<String> list = new ArrayList<String>(map.values());
			if (list.isEmpty()) {
				return "";
			}
			Collections.sort(list);
			StringBuilder sb = new StringBuilder(32 * list.size());
			for (String s : list) {
				if (s.startsWith("java.lang.")) {
					continue;
				}
				sb.append("import ");
				sb.append(s);
				sb.append(";\n");
			}
			return sb.toString();
		}
	}
}
