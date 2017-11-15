package jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.page;

import java.text.MessageFormat;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

//TODO もう使用していないので廃止
public class SelectSourcePage extends WizardPage {
	private String dslName;

	private Button newButton;
	private Button genButton;

	public SelectSourcePage(String diagramName, String dslName) {
		super("SelectSourcePage");
		setTitle(MessageFormat.format("{0}ダイアグラム作成方法の指定", diagramName));
		setDescription(MessageFormat.format("{0}ダイアグラムの作成方法を選択して下さい。", diagramName));
		this.dslName = dslName;
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		{
			newButton = new Button(composite, SWT.RADIO);
			newButton.setText("空のダイアグラムを作成する");
			newButton.setSelection(true);
		}
		{
			genButton = new Button(composite, SWT.RADIO);
			genButton.setText(MessageFormat.format("既存の{0}から生成する", dslName));
		}

		setControl(composite);

		setPageComplete(true);
	}

	public boolean isNew() {
		return newButton.getSelection();
	}

	public boolean isGenerate() {
		return genButton.getSelection();
	}
}
