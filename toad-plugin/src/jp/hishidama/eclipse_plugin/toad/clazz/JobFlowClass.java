package jp.hishidama.eclipse_plugin.toad.clazz;

import org.eclipse.jdt.core.IType;

public class JobFlowClass extends ClassDelegator {

	private static final String EMPTY = new String();
	private String jobId = EMPTY;

	public JobFlowClass(IType type) {
		super(type);
	}

	@Override
	public boolean isDsl() {
		return getJobFlowId() != null;
	}

	public String getJobFlowId() {
		if (jobId == EMPTY) {
			jobId = getAnnotationValue(type, "com.asakusafw.vocabulary.flow.JobFlow", "name");
		}
		return jobId;
	}
}
