package jp.hishidama.eclipse_plugin.toad.clazz;

import org.eclipse.jdt.core.IType;

public class BatchClass extends ClassDelegator {

	private static final String EMPTY = new String();
	private String batchId = EMPTY;

	public BatchClass(IType type) {
		super(type);
	}

	@Override
	public boolean isDsl() {
		return getBatchId() != null;
	}

	public String getBatchId() {
		if (batchId == EMPTY) {
			batchId = getAnnotationValue(type, "com.asakusafw.vocabulary.batch.Batch", "name");
		}
		return batchId;
	}
}
