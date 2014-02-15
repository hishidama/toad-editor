package jp.hishidama.eclipse_plugin.toad.internal.extension;

public class DirectioCsvImporterProperty extends DirectioImporterProperty {

	@Override
	public String getBaseName() {
		return "@directio.csv";
	}

	@Override
	protected String getSuperClassPackagePart() {
		return "csv";
	}

	@Override
	protected String getSuperClassNameSuffix() {
		return "CsvInputDescription";
	}
}
