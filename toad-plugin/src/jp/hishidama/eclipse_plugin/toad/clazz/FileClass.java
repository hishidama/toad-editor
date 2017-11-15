package jp.hishidama.eclipse_plugin.toad.clazz;

import org.eclipse.jdt.core.IType;

public class FileClass extends ClassDelegator {

	private Boolean isImporter, isExporter;

	public FileClass(IType type) {
		super(type);
	}

	@Override
	public boolean isDsl() {
		return isImporter() || isExporter();
	}

	public boolean isImporter() {
		if (isImporter == null) {
			isImporter = existsInterface(type, "com.asakusafw.vocabulary.external.ImporterDescription");
		}
		return isImporter;
	}

	public boolean isExporter() {
		if (isExporter == null) {
			isExporter = existsInterface(type, "com.asakusafw.vocabulary.external.ExporterDescription");
		}
		return isExporter;
	}
}
