package jp.hishidama.eclipse_plugin.toad.model.property;

public abstract class IntSection<M> extends TextSection<M> {

	protected IntSection(String label) {
		super(label);
	}

	@Override
	protected final String getValue(M model) {
		int value = getIntValue(model);
		return Integer.toString(value);
	}

	protected abstract int getIntValue(M model);

	@Override
	protected boolean editableText() {
		return true;
	}

	@Override
	protected boolean validate(M model, String text) {
		try {
			Integer.parseInt(text.trim());
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	protected final void setValue(M model, String text) {
		int value = Integer.parseInt(text.trim());
		setIntValue(model, value);
	}

	protected abstract void setIntValue(M model, int value);
}
