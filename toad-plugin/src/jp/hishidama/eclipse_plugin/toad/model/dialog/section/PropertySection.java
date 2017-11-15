package jp.hishidama.eclipse_plugin.toad.model.dialog.section;

import static jp.hishidama.eclipse_plugin.util.StringUtil.nonNull;

import java.util.List;

import jp.hishidama.eclipse_plugin.toad.model.dialog.PropertyDialog;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class PropertySection {
	protected final PropertyDialog dialog;

	public PropertySection(PropertyDialog dialog) {
		this.dialog = dialog;
	}

	public static Composite createTabItem(TabFolder tab, String title) {
		TabItem item = new TabItem(tab, SWT.NONE);
		item.setText(title);

		Composite composite = new Composite(tab, SWT.NONE);
		{
			GridLayout layout = new GridLayout(3, false);
			composite.setLayout(layout);
		}
		item.setControl(composite);

		return composite;
	}

	protected Composite createField(Composite composite, String labelText, int numColumns) {
		Label label = new Label(composite, SWT.NONE);
		label.setText(labelText);

		Composite field = new Composite(composite, SWT.NONE);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		field.setLayoutData(data);
		field.setLayout(new GridLayout(numColumns, false));
		return field;
	}

	protected Text createTextField(Composite composite, String labelText) {
		Label label = new Label(composite, SWT.NONE);
		label.setText(labelText);

		Text text = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		text.setLayoutData(data);
		return text;
	}

	protected TextButtonPair createTextField(Composite composite, String labelText, String buttonText) {
		Label label = new Label(composite, SWT.NONE);
		label.setText(labelText);

		Text text = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		text.setLayoutData(data);

		Button button = new Button(composite, SWT.PUSH);
		button.setText(buttonText);

		return new TextButtonPair(text, button);
	}

	protected static class TextButtonPair {
		public Text text;
		public Button button;

		public TextButtonPair(Text text, Button button) {
			this.text = text;
			this.button = button;
		}
	}

	protected Text createMultiTextField(Composite composite, String labelText) {
		Label label = new Label(composite, SWT.NONE);
		label.setText(labelText);

		Text text = new Text(composite, SWT.MULTI | SWT.BORDER);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 2;
		text.setLayoutData(data);
		return text;
	}

	protected Combo createComboField(Composite composite, String labelText, List<String> values, String value) {
		Label label = new Label(composite, SWT.NONE);
		label.setText(labelText);

		Combo combo = new Combo(composite, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		combo.setLayoutData(data);

		if (value == null) {
			value = values.get(0);
		}
		boolean found = false;
		for (String s : values) {
			combo.add(s);
			if (s.equals(value)) {
				found = true;
			}
		}
		if (!found) {
			value = values.get(0);
		}
		combo.setText(nonNull(value));

		return combo;
	}

	protected IProject getProject() {
		return dialog.getProject();
	}

	protected Shell getShell() {
		return dialog.getShell();
	}

	protected IWizardContainer getContainer() {
		return dialog.getContainer();
	}
}
