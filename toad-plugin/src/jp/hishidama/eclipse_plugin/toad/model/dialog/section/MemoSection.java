package jp.hishidama.eclipse_plugin.toad.model.dialog.section;

import jp.hishidama.eclipse_plugin.toad.model.AbstractModel;
import jp.hishidama.eclipse_plugin.toad.model.dialog.PropertyDialog;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;

public class MemoSection extends PropertySection {
	private AbstractModel model;

	private Text memo;

	public MemoSection(PropertyDialog dialog, AbstractModel model) {
		super(dialog);
		this.model = model;
	}

	public void createMemoTab(TabFolder tab) {
		Composite composite = createTabItem(tab, "Memo");
		memo = createMultiTextField(composite, "memo");
		memo.setText(model.getMemo());
		memo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String text = memo.getText();
				model.setMemo(text);
				dialog.doValidate();
			}
		});
	}
}
