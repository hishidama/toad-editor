package jp.hishidama.eclipse_plugin.toad.internal.extension;

import jp.hishidama.eclipse_plugin.toad.extension.ClassGenerator;
import jp.hishidama.eclipse_plugin.toad.extension.ToadImporterExporterProperty;
import jp.hishidama.eclipse_plugin.toad.model.node.datafile.DataFileNode;

public abstract class WindgatePorterProperty extends ToadImporterExporterProperty {

	protected void appendProfileName(StringBuilder sb, DataFileNode node) {
		String methodName = "getProfileName";
		String rtype = "String";
		String rvalue = "\"" + node.getProperty("getProfileName") + "\"";
		appendGetterMethod(sb, methodName, rtype, rvalue);
	}

	protected void appendPath(StringBuilder sb, DataFileNode node) {
		String methodName = "getPath";
		String rtype = "String";
		String rvalue = "\"" + node.getProperty("getPath") + "\"";
		appendGetterMethod(sb, methodName, rtype, rvalue);
	}

	protected void appendTableName(StringBuilder sb, DataFileNode node) {
		String methodName = "getTableName";
		String rtype = "String";
		String rvalue = "\"" + node.getProperty("getTableName") + "\"";
		appendGetterMethod(sb, methodName, rtype, rvalue);
	}

	protected void appendColumnNames(StringBuilder sb, DataFileNode node, ClassGenerator generator) {
		String methodName = "getColumnNames";
		String value = node.getProperty("getColumnNames");
		appendGetterListMethod(sb, methodName, value, generator);
	}
}
