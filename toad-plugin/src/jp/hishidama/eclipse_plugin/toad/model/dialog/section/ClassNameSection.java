package jp.hishidama.eclipse_plugin.toad.model.dialog.section;

import static jp.hishidama.eclipse_plugin.util.StringUtil.nonEmpty;
import static jp.hishidama.eclipse_plugin.util.StringUtil.nonNull;
import jp.hishidama.eclipse_plugin.dialog.ClassSelectionDialog;
import jp.hishidama.eclipse_plugin.dialog.ProjectFileSelectionDialog;
import jp.hishidama.eclipse_plugin.toad.model.dialog.OperatorPropertyDialog;
import jp.hishidama.eclipse_plugin.toad.model.dialog.PropertyDialog;
import jp.hishidama.eclipse_plugin.toad.model.node.ClassNameNode;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;
import jp.hishidama.eclipse_plugin.util.ToadFileUtil;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class ClassNameSection extends PropertySection {
	private ClassNameNode model;

	private Text className;
	private Text methodName;
	private Text toadFile;

	public ClassNameSection(PropertyDialog dialog, ClassNameNode model) {
		super(dialog);
		this.model = model;
	}

	public void createSection(Composite composite) {
		boolean coreOperator = isCoreOperator();
		final OperatorNode operator = (model instanceof OperatorNode) ? (OperatorNode) model : null;
		{
			TextButtonPair r = createTextField(composite, "class name", "open");
			className = r.text;
			className.setText(nonNull(model.getClassName()));
			className.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					model.setClassName(className.getText().trim());
					dialog.doValidate();
				}
			});
			r.button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					dialog.openClass(className.getText(), null);
				}
			});
			className.setEditable(!coreOperator);
		}

		if (operator != null) {
			TextButtonPair r = createTextField(composite, "method name", "open");
			methodName = r.text;
			methodName.setText(nonNull(operator.getMethodName()));
			methodName.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					operator.setMethodName(methodName.getText().trim());
					dialog.doValidate();
				}
			});
			r.button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					dialog.openClass(className.getText(), methodName.getText());
				}
			});
			methodName.setEditable(!coreOperator && !operator.isFlowPart());
		}

		{
			int n = 1 + ((dialog instanceof OperatorPropertyDialog) ? 1 : 0);
			Composite field = createField(composite, "class", n);
			{
				Button button = new Button(field, SWT.PUSH);
				button.setText("既存クラスから選択");
				button.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						selectClassFile(className.getText());
					}
				});
				button.setEnabled(!coreOperator);
			}
			if (dialog instanceof OperatorPropertyDialog) {
				Button button = new Button(field, SWT.PUSH);
				button.setText("Operator DSLの雛形を表示");
				button.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						((OperatorPropertyDialog) dialog).previewOperatorDsl();
					}
				});
				button.setEnabled(!coreOperator && !operator.isFlowPart());
			}
		}

		if (model.hasToadFile()) {
			final TextButtonPair r = createTextField(composite, "toad file", "open");
			toadFile = r.text;
			final String toadFileExtension = model.getToadFileExtension();
			toadFile.setText(nonNull(ToadFileUtil.getToadFile(model.getClassName(), toadFileExtension)));
			toadFile.setEditable(false);
			r.button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					dialog.openToadFile(toadFile.getText());
				}
			});
			className.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					String name = className.getText().trim();
					model.setClassName(name);
					String file = ToadFileUtil.getToadFile(name, toadFileExtension);
					toadFile.setText(nonNull(file));
					r.button.setEnabled(nonEmpty(file));
					dialog.doValidate();
				}
			});

			Composite field = createField(composite, "toad file", 1);
			Button button = new Button(field, SWT.PUSH);
			button.setText("ファイル選択");
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					selectToadFile(toadFile.getText());
				}
			});
		}
	}

	private boolean isCoreOperator() {
		if (model instanceof OperatorNode) {
			return ((OperatorNode) model).isCoreOperator();
		}
		return false;
	}

	void selectClassFile(String initialClassName) {
		ClassSelectionDialog d = ClassSelectionDialog.create(getShell(), getProject(), getContainer(),
				model.getClassNameFilter());
		d.setTitle("select class");
		if (nonEmpty(initialClassName)) {
			d.setInitialPattern(initialClassName);
		} else {
			d.setInitialPattern(model.getClassNamePattern());
		}
		if (d.open() != Window.OK) {
			return;
		}

		String name = d.getSelectedClassName();
		className.setText(nonNull(name));
	}

	void selectToadFile(String path) {
		ProjectFileSelectionDialog d = new ProjectFileSelectionDialog(getShell(), getProject());
		d.setTitle("select toad file");
		d.addFileterExtension(model.getToadFileExtension());
		d.setInitialSelection(path);
		if (d.open() != Window.OK) {
			return;
		}

		String[] r = d.getResult();
		if (r.length > 0) {
			String toadPath = r[0];
			toadFile.setText(toadPath);
			String cname = ToadFileUtil.getToadClassName(toadPath);
			className.setText(cname);
			if (selectToadFileHandler != null) {
				selectToadFileHandler.handleSelectToadFile(toadPath, cname);
			}
		}
	}

	public static interface SelectToadFileHandler {
		public void handleSelectToadFile(String toadPath, String className);
	}

	private SelectToadFileHandler selectToadFileHandler;

	public void setSelectToadFileHandler(SelectToadFileHandler handler) {
		this.selectToadFileHandler = handler;
	}
}
