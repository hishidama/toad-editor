package jp.hishidama.eclipse_plugin.toad.internal.extension;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.toad.extension.ClassGenerator;
import jp.hishidama.eclipse_plugin.toad.model.node.datafile.DataFileNode;
import jp.hishidama.eclipse_plugin.util.StringUtil;

import org.eclipse.jdt.core.IType;

public class WindgateCsvImporterProperty extends WindgatePorterProperty {

	@Override
	public String getBaseName() {
		return "@windgate.csv";
	}

	@Override
	public boolean isImporter() {
		return true;
	}

	@Override
	public boolean acceptable(IType type) {
		return isExtends(type, "com.asakusafw.vocabulary.windgate.FsImporterDescription");
	}

	@Override
	public List<PorterProperty> getProperties() {
		List<PorterProperty> list = new ArrayList<PorterProperty>();
		list.add(create("getProfileName", "プロファイル名", true));
		list.add(create("getPath", "ファイルパス", true));
		list.add(create("getDataSize", "データサイズ", true, "UNKOWN", "TINY", "SMALL", "LARGE"));
		return list;
	}

	@Override
	public void generateClass(ClassGenerator generator, DataFileNode node, StringBuilder sb) {
		sb.append("public class ");
		sb.append(StringUtil.getSimpleName(node.getClassName()));
		sb.append(" extends ");
		String superClass = getSuperClassName(generator, node.getModelName(), "csv", "CsvImporterDescription");
		sb.append(generator.getCachedClassName(superClass));
		sb.append(" {\n");
		appendProfileName(sb, node);
		appendPath(sb, node);
		appendDataSize(sb, node, generator);
		sb.append("}\n");
	}
}
