package jp.hishidama.eclipse_plugin.toad.editor.action;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class SourceCodeDialog extends Dialog {

	private String imports;
	private String text;

	public SourceCodeDialog(Shell parentShell, String imports, String text) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.imports = imports;
		this.text = text;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText("Operator DSL template");

		Composite composite = (Composite) super.createDialogArea(parent);
		{
			int style = SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL;
			Text text = new Text(composite, style);
			GridData grid = new GridData(GridData.FILL_HORIZONTAL);
			grid.heightHint = 64;
			text.setLayoutData(grid);
			text.setText(this.imports);
		}
		{
			int style = SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL;
			Text text = new Text(composite, style);
			GridData grid = new GridData(GridData.FILL_BOTH);
			grid.widthHint = 256 + 128;
			grid.heightHint = 192;
			text.setLayoutData(grid);
			text.setText(this.text);
		}

		return composite;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	}
}
