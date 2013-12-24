package jp.hishidama.eclipse_plugin.toad.internal.extension;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.toad.extension.ClassGenerator;
import jp.hishidama.eclipse_plugin.toad.model.node.datafile.DataFileNode;
import jp.hishidama.eclipse_plugin.util.StringUtil;

import org.eclipse.jdt.core.IType;

public class WindgateCsvExporterProperty extends WindgatePorterProperty {

	@Override
	public String getBaseName() {
		return "@windgate.csv";
	}

	@Override
	public boolean isImporter() {
		return false;
	}

	@Override
	public boolean acceptable(IType type) {
		return isExtends(type, "com.asakusafw.vocabulary.windgate.FsExporterDescription");
	}

	@Override
	public List<PorterProperty> getProperties() {
		List<PorterProperty> list = new ArrayList<PorterProperty>();
		list.add(create("getProfileName", "プロファイル名", true));
		list.add(create("getPath", "ファイルパス", true));
		return list;
	}

	@Override
	public void generateClass(ClassGenerator generator, DataFileNode node, StringBuilder sb) {
		sb.append("public class ");
		sb.append(StringUtil.getSimpleName(node.getClassName()));
		sb.append(" extends ");
		String superClass = getSuperClassName(generator, node.getModelName(), "csv", "CsvExporterDescription");
		sb.append(generator.getCachedClassName(superClass));
		sb.append(" {\n");
		appendProfileName(sb, node);
		appendPath(sb, node);
		sb.append("}\n");
	}
}
