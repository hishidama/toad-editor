package jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.page;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import jp.hishidama.eclipse_plugin.toad.clazz.FlowPartClass;
import jp.hishidama.eclipse_plugin.toad.clazz.JavaDelegator.Parameter;
import jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.task.FlowpartParameterTask;
import jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.task.FlowpartParameterTask.Item;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class FlowpartParameterPage extends WizardPage {
	private List<IFile> list;
	private Map<String, Object> params;

	private Table table;

	private static final int COLUMN_VALUE = 3;

	public FlowpartParameterPage() {
		super("SelectSourcePage");
		setTitle("フローパートの引数の指定");
		setDescription("フローパートの（In/Out以外の）引数の値を指定して下さい。");
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		{
			table = new Table(composite, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
			GridData data = new GridData();
			data.heightHint = table.getItemHeight() * 10;
			table.setLayoutData(data);
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			{
				TableColumn column = new TableColumn(table, SWT.NONE);
				column.setText("FlowPart class");
				column.setWidth(256 + 32);
			}
			{
				TableColumn column = new TableColumn(table, SWT.NONE);
				column.setText("parameter");
				column.setWidth(128);
			}
			{
				TableColumn column = new TableColumn(table, SWT.NONE);
				column.setText("type");
				column.setWidth(128 + 32);
			}
			{
				TableColumn column = new TableColumn(table, SWT.NONE);
				column.setText("value");
				column.setWidth(128 + 32);
			}
			table.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					TableItem item = (TableItem) e.item;
					createEditor(item);
				}
			});
		}

		setControl(composite);

		setPageComplete(false);
	}

	public void setFiles(List<IFile> list) {
		this.list = list;
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			refresh();
		}
		super.setVisible(visible);
	}

	public void refresh() {
		table.removeAll();

		FlowpartParameterTask task = new FlowpartParameterTask(getDialogSettings(), list);
		try {
			getContainer().run(true, true, task); // table も更新する
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return;
		} catch (InterruptedException e) {
			return;
		}

		List<Item> items = task.getTableItems();
		for (Item i : items) {
			createItem(i.file, i.flowpart, i.param, i.value);
		}
		this.params = task.getParameters();

		if (table.getItemCount() <= 0) {
			setMessage("引数の値が必要なフローパートはありません。");
		}
		setPageComplete(true);
	}

	public final void createItem(IFile file, FlowPartClass flowpart, Parameter param, Object value) {
		TableItem item = new TableItem(table, SWT.NONE);
		item.setData(new Data(file, flowpart, param));
		item.setText(0, flowpart.getClassName());
		item.setText(1, param.name);
		item.setText(2, param.className);
		setValueToItem(item, value);
	}

	private void setValueToItem(TableItem item, Object value) {
		String s = toValidateString(value);
		item.setText(COLUMN_VALUE, (s != null) ? s : "(null)");
	}

	private static class Data {
		public IFile file;
		public FlowPartClass flowpart;
		public Parameter param;

		public Data(IFile file, FlowPartClass flowpart, Parameter param) {
			this.file = file;
			this.flowpart = flowpart;
			this.param = param;
		}
	}

	private static String getKey(Data data) {
		return FlowpartParameterTask.getKey(data.flowpart, data.param);
	}

	private void createEditor(final TableItem item) {
		final Text text = new Text(table, SWT.NONE);
		text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				Data data = (Data) item.getData();
				Object value = FlowpartParameterTask.toValidateValue(data.param.className, text.getText());
				setValueToItem(item, value);
				params.put(getKey(data), value);
				text.dispose();
			}
		});
		text.addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e) {
				switch (e.detail) {
				case SWT.TRAVERSE_RETURN:
					Data data = (Data) item.getData();
					Object value = FlowpartParameterTask.toValidateValue(data.param.className, text.getText());
					setValueToItem(item, value);
					params.put(getKey(data), value);
					text.dispose();
					break;
				case SWT.TRAVERSE_ESCAPE:
					text.dispose();
					e.doit = false;
					break;
				}
			}
		});

		TableEditor editor = new TableEditor(table);
		editor.grabHorizontal = true;
		editor.grabVertical = true;
		editor.minimumWidth = 64;
		editor.setEditor(text, item, COLUMN_VALUE);

		text.setText(item.getText(COLUMN_VALUE));
		text.selectAll();
		text.setFocus();
	}

	private static String toValidateString(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof String) {
			return (String) value;
		}
		if (value instanceof Number || value instanceof Boolean) {
			return value.toString();
		}
		if (value instanceof Character) {
			char c = (Character) value;
			if (c < ' ') {
				return "\\" + (int) c;
			} else {
				return Character.toString(c);
			}
		}
		return null;
	}

	public Map<String, Object> getValues() {
		IDialogSettings settings = getDialogSettings();
		for (TableItem item : table.getItems()) {
			Data data = (Data) item.getData();
			String key = getKey(data);
			Object value = params.get(key);

			IProject project = data.file.getProject();
			String settingKey = String.format("%s#%s#%s", project.getName(), getClass().getSimpleName(), key);
			settings.put(settingKey, toValidateString(value));
		}

		return params;
	}
}
