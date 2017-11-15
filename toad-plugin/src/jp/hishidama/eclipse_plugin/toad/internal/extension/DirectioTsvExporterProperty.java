package jp.hishidama.eclipse_plugin.toad.internal.extension;

public class DirectioTsvExporterProperty extends DirectioExporterProperty {

	@Override
	public String getBaseName() {
		return "@directio.tsv";
	}

	@Override
	protected String getSuperClassPackagePart() {
		return "tsv";
	}

	@Override
	protected String getSuperClassNameSuffix() {
		return "TsvOutputDescription";
	}
}
