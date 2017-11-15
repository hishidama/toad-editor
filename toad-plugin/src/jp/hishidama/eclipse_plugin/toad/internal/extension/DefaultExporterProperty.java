package jp.hishidama.eclipse_plugin.toad.internal.extension;

import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.core.IType;

import jp.hishidama.eclipse_plugin.toad.extension.ClassGenerator;
import jp.hishidama.eclipse_plugin.toad.extension.ToadImporterExporterProperty;
import jp.hishidama.eclipse_plugin.toad.model.node.datafile.DataFileNode;
import jp.hishidama.eclipse_plugin.util.StringUtil;

public class DefaultExporterProperty extends ToadImporterExporterProperty {
	public static final String NAME = "default";
	public static final String INTERFACE_NAME = "com.asakusafw.vocabulary.external.ExporterDescription";

	@Override
	public String getBaseName() {
		return NAME;
	}

	@Override
	public boolean isImporter() {
		return false;
	}

	@Override
	public boolean acceptable(IType type) {
		return isImplements(type, INTERFACE_NAME);
	}

	@Override
	public List<PorterProperty> getProperties() {
		return Collections.emptyList();
	}

	@Override
	public void generateClass(ClassGenerator generator, DataFileNode node, StringBuilder sb) {
		sb.append("public class ");
		sb.append(StringUtil.getSimpleName(node.getClassName()));
		sb.append(" implements ");
		sb.append(generator.getCachedClassName(INTERFACE_NAME));
		sb.append(" {\n");
		appendModelType(sb, node, generator);
		sb.append("}\n");
	}
}
