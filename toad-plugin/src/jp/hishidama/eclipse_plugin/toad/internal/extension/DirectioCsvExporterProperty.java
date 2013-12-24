package jp.hishidama.eclipse_plugin.toad.internal.extension;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.toad.extension.ClassGenerator;
import jp.hishidama.eclipse_plugin.toad.model.node.datafile.DataFileNode;
import jp.hishidama.eclipse_plugin.util.StringUtil;

import org.eclipse.jdt.core.IType;

public class DirectioCsvExporterProperty extends DirectioPorterProperty {

	@Override
	public String getBaseName() {
		return "@directio.csv";
	}

	@Override
	public boolean isImporter() {
		return false;
	}

	@Override
	public boolean acceptable(IType type) {
		return isExtends(type, "com.asakusafw.vocabulary.directio.DirectFileOutputDescription");
	}

	@Override
	public List<PorterProperty> getProperties() {
		List<PorterProperty> list = new ArrayList<PorterProperty>();
		list.add(create("getBasePath", "ベースパス", true));
		list.add(create("getResourcePattern", "リソースパターン", true));
		list.add(create("getOrder", "ソート項目", false));
		list.add(create("getDeletePatterns", "削除パターン", false));
		return list;
	}

	@Override
	public void generateClass(ClassGenerator generator, DataFileNode node, StringBuilder sb) {
		sb.append("public class ");
		sb.append(StringUtil.getSimpleName(node.getClassName()));
		sb.append(" extends ");
		String superClass = getSuperClassName(generator, node.getModelName(), "csv", "CsvOutputDescription");
		sb.append(generator.getCachedClassName(superClass));
		sb.append(" {\n");
		appendBasePath(sb, node);
		appendResourcePattern(sb, node);
		appendOrder(sb, node, generator);
		appendDeletePatterns(sb, node, generator);
		sb.append("}\n");
	}

	protected void appendOrder(StringBuilder sb, DataFileNode node, ClassGenerator generator) {
		String methodName = "getOrder";
		String value = node.getProperty("getOrder");
		appendGetterListMethod(sb, methodName, value, generator);
	}

	protected void appendDeletePatterns(StringBuilder sb, DataFileNode node, ClassGenerator generator) {
		String methodName = "getDeletePatterns";
		String value = node.getProperty("getDeletePatterns");
		appendGetterListMethod(sb, methodName, value, generator);
	}
}
