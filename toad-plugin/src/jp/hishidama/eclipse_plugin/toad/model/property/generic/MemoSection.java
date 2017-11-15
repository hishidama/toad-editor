package jp.hishidama.eclipse_plugin.toad.model.property.generic;

import org.eclipse.swt.SWT;

import jp.hishidama.eclipse_plugin.toad.model.AbstractModel;
import jp.hishidama.eclipse_plugin.toad.model.property.TextSection;

public class MemoSection extends TextSection<AbstractModel> {

	public MemoSection() {
		super("memo");
	}

	@Override
	protected int getTextStyle() {
		return SWT.MULTI;
	}

	@Override
	protected String getValue(AbstractModel model) {
		return model.getMemo();
	}
}
