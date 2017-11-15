package jp.hishidama.eclipse_plugin.toad.internal.extension;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.toad.extension.ClassGenerator;
import jp.hishidama.eclipse_plugin.toad.model.node.datafile.DataFileNode;
import jp.hishidama.eclipse_plugin.util.StringUtil;

import org.eclipse.jdt.core.IType;

public class WindgateJdbcImporterProperty extends WindgatePorterProperty {

	@Override
	public String getBaseName() {
		return "@windgate.jdbc";
	}

	@Override
	public boolean isImporter() {
		return true;
	}

	@Override
	public boolean acceptable(IType type) {
		return isExtends(type, "com.asakusafw.vocabulary.windgate.JdbcImporterDescription");
	}

	@Override
	public List<PorterProperty> getProperties() {
		List<PorterProperty> list = new ArrayList<PorterProperty>();
		list.add(create("getProfileName", "プロファイル名", true));
		list.add(create("getTableName", "テーブル名", false));
		list.add(create("getColumnNames", "カラム名", false));
		list.add(create("getCondition", "検索条件", false));
		list.add(create("getDataSize", "データサイズ", true, "UNKOWN", "TINY", "SMALL", "LARGE"));
		return list;
	}

	@Override
	public void generateClass(ClassGenerator generator, DataFileNode node, StringBuilder sb) {
		sb.append("public class ");
		sb.append(StringUtil.getSimpleName(node.getClassName()));
		sb.append(" extends ");
		String superClass = getSuperClassName(generator, node.getModelName(), "jdbc", "JdbcExporterDescription");
		sb.append(generator.getCachedClassName(superClass));
		sb.append(" {\n");
		appendProfileName(sb, node);
		appendTableName(sb, node);
		appendColumnNames(sb, node, generator);
		appendCondition(sb, node);
		appendDataSize(sb, node, generator);
		sb.append("}\n");
	}

	protected void appendCondition(StringBuilder sb, DataFileNode node) {
		String methodName = "getCondition";
		String rtype = "String";
		String rvalue = "\"" + node.getProperty("getCondition") + "\"";
		appendGetterMethod(sb, methodName, rtype, rvalue);
	}
}
