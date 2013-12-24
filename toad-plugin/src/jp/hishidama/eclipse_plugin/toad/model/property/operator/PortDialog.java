package jp.hishidama.eclipse_plugin.toad.model.property.operator;

import jp.hishidama.eclipse_plugin.toad.model.node.port.BasePort;
import jp.hishidama.eclipse_plugin.toad.model.property.EditDialog;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class PortDialog extends EditDialog {

	private BasePort port;

	private Button inRadio;
	private Text nameText;
	private Text descText;

	private boolean in;
	private String name;
	private String description;

	public PortDialog(Shell parentShell, BasePort c) {
		super(parentShell, "ポート編集");
		this.port = c;
	}

	@Override
	protected void createFields(Composite composite) {
		inRadio = createRadioField(composite, "in/out :", "in", "out");
		nameText = createTextField(composite, "name :");
		descText = createTextField(composite, "description :");
	}

	@Override
	protected void refresh() {
		boolean in = true;
		String name = "";
		String desc = "";
		if (port != null) {
			in = port.isIn();
			name = port.getName();
			desc = port.getDescription();
		}
		inRadio.setSelection(in);
		nameText.setText(nonNull(name));
		descText.setText(nonNull(desc));
	}

	@Override
	protected boolean validate() {
		in = inRadio.getSelection();
		name = nameText.getText();
		description = descText.getText();

		return !name.isEmpty();
	}

	public boolean getIn() {
		return in;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
}
