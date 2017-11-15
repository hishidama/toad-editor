package jp.hishidama.eclipse_plugin.toad.model.dialog.section;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.toad.model.property.EditDialog;
import jp.hishidama.eclipse_plugin.util.StringUtil;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class EditKeyDialog extends EditDialog {
	private List<String> titles;
	private List<String> values;
	private List<List<String>> lists;

	private List<Combo> valueCombo;

	public EditKeyDialog(Shell parentShell, List<String> titles, List<String> values, List<List<String>> lists) {
		super(parentShell, "キー編集");
		this.titles = titles;
		this.values = values;
		this.lists = lists;
	}

	@Override
	protected void createFields(Composite composite) {
		valueCombo = new ArrayList<Combo>(titles.size());
		int i = 0;
		for (String s : titles) {
			String value = (i < values.size()) ? values.get(i) : "";
			List<String> list = lists.get(i);
			Combo combo = createComboField(composite, s, value, list);
			valueCombo.add(combo);
			i++;
		}
	}

	@Override
	protected void refresh() {
		refreshOkButton();
	}

	@Override
	protected boolean validate() {
		values.clear();
		for (Combo combo : valueCombo) {
			String text = combo.getText();
			if (StringUtil.isEmpty(text)) {
				return false;
			}
			values.add(text);
		}
		return true;
	}

	public List<String> getValue() {
		return values;
	}
}
