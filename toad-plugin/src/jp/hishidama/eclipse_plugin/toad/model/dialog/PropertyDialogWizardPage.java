package jp.hishidama.eclipse_plugin.toad.model.dialog;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;

public abstract class PropertyDialogWizardPage extends WizardPage {

	protected PropertyDialogWizardPage() {
		super("PropertyDialogWizardPage");
	}

	@Override
	public void createControl(Composite parent) {
		TabFolder tab = createDialogAreaComposite(parent);
		createFields(tab);

		setControl(tab);
	}

	private TabFolder createDialogAreaComposite(Composite parent) {
		// http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/SWTTabControl.htm
		TabFolder tab = new TabFolder(parent, SWT.NONE);

		tab.setLayoutData(new GridData(GridData.FILL_BOTH));

		return tab;
	}

	protected abstract void createFields(TabFolder tab);
}
