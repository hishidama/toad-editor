package jp.hishidama.eclipse_plugin.toad.internal.extension;

import jp.hishidama.eclipse_plugin.toad.extension.ToadImporterExporterProperty;
import jp.hishidama.eclipse_plugin.toad.model.node.datafile.DataFileNode;

public abstract class DirectioPorterProperty extends ToadImporterExporterProperty {

	protected void appendBasePath(StringBuilder sb, DataFileNode node) {
		String methodName = "getBasePath";
		String rtype = "String";
		String rvalue = "\"" + node.getProperty("getBasePath") + "\"";
		appendGetterMethod(sb, methodName, rtype, rvalue);
	}

	protected void appendResourcePattern(StringBuilder sb, DataFileNode node) {
		String methodName = "getResourcePattern";
		String rtype = "String";
		String rvalue = "\"" + node.getProperty("getResourcePattern") + "\"";
		appendGetterMethod(sb, methodName, rtype, rvalue);
	}
}
