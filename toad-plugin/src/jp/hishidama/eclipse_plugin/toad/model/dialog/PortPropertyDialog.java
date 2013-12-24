package jp.hishidama.eclipse_plugin.toad.model.dialog;

import java.util.Collections;
import java.util.List;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.model.dialog.section.PropertySection;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.OperatorNode;
import jp.hishidama.eclipse_plugin.toad.model.node.operator.delegator.OperatorDelegate;
import jp.hishidama.eclipse_plugin.toad.model.node.port.BasePort;
import jp.hishidama.eclipse_plugin.toad.model.node.port.OpePort;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;

public class PortPropertyDialog extends PropertyDialog {
	private BasePort port;
	private OperatorDelegate delegate;

	public PortPropertyDialog(ToadEditor editor, BasePort port) {
		this(editor, port, port.getParent(), false);
	}

	public PortPropertyDialog(ToadEditor editor, BasePort port, NodeElement parent, boolean directEdit) {
		super("ポート", editor, port, directEdit);
		this.port = (BasePort) super.model;
		if (parent instanceof OperatorNode) {
			this.delegate = ((OperatorNode) parent).getDelegate();
		}
	}

	@Override
	protected void createFields(TabFolder tab) {
		createBasicTab(tab);
		createMemoTab(tab);
		createModelLayoutTab(tab);
		if (port instanceof OpePort) {
			createAttributeTab(tab);
		}
	}

	private void createBasicTab(TabFolder tab) {
		Composite composite = createBasicTabItem(tab);
		createInOutSection(composite);
		createRoleSection(composite);
		createBaseSection(composite);
		createModelNameSection(composite, !directEdit);
	}

	private void createInOutSection(Composite composite) {
		new PropertySection(this) {
			public void createSection(Composite composite) {
				Text text = createTextField(composite, "in/out");
				text.setText(port.isIn() ? "in" : "out");
				text.setEditable(false);
			}
		}.createSection(composite);
	}

	private void createRoleSection(Composite composite) {
		final List<String> roleList = getRoleList();

		new PropertySection(this) {
			public void createSection(Composite composite) {
				if (!roleList.isEmpty()) {
					final Combo role = createComboField(composite, "role", roleList, port.getRole());
					role.addModifyListener(new ModifyListener() {
						@Override
						public void modifyText(ModifyEvent e) {
							port.setRole(role.getText());
						}
					});
				} else {
					Text text = createTextField(composite, "role");
					text.setText(port.getRole());
					text.setEditable(false);
				}
			}
		}.createSection(composite);
	}

	private List<String> getRoleList() {
		if (delegate == null) {
			return Collections.emptyList();
		}
		return delegate.getPortRoleList(port.isIn());
	}
}
