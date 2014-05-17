package jp.hishidama.eclipse_plugin.toad.model.dialog.section;

import static jp.hishidama.eclipse_plugin.util.StringUtil.nonEmpty;
import static jp.hishidama.eclipse_plugin.util.StringUtil.nonNull;
import jp.hishidama.eclipse_plugin.dialog.ClassSelectionDialog;
import jp.hishidama.eclipse_plugin.toad.model.dialog.PropertyDialog;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;

import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;

public class EnumNameSection extends PropertySection {
	private OperatorNode model;

	private Text enumName;
	private Text description;

	public EnumNameSection(PropertyDialog dialog, OperatorNode model) {
		super(dialog);
		this.model = model;
	}

	public void createTab(TabFolder tab) {
		Composite composite = createTabItem(tab, "Emum");
		createSection(composite);
	}

	private void createSection(Composite composite) {
		{
			description = createTextField(composite, "description");
			description.setText(nonNull(model.getProperty(OperatorNode.KEY_RETURN_DESCRIPTION)));
			description.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					model.setProperty(OperatorNode.KEY_RETURN_DESCRIPTION, description.getText());
					dialog.doValidate();
				}
			});
		}

		{
			TextButtonPair r = createTextField(composite, "enum name", "open");
			enumName = r.text;
			enumName.setText(nonNull(model.getProperty(OperatorNode.KEY_RETURN_ENUM_NAME)));
			enumName.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					model.setProperty(OperatorNode.KEY_RETURN_ENUM_NAME, enumName.getText().trim());
					dialog.doValidate();
				}
			});
			r.button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					dialog.openClass(enumName.getText(), null);
				}
			});
		}

		{
			Composite field = createField(composite, "enum", 1);
			{
				Button button = new Button(field, SWT.PUSH);
				button.setText("既存クラスから選択");
				button.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						selectClassFile(enumName.getText());
					}
				});
			}
		}
	}

	void selectClassFile(String initialClassName) {
		ClassSelectionDialog d = ClassSelectionDialog.create(getShell(), getProject(), getContainer(),
				IJavaSearchConstants.ENUM, null);
		d.setTitle("select enum");
		if (nonEmpty(initialClassName)) {
			d.setInitialPattern(initialClassName);
		}
		if (d.open() != Window.OK) {
			return;
		}

		String name = d.getSelectedClassName();
		enumName.setText(nonNull(name));
	}
}
