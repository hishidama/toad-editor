package jp.hishidama.eclipse_plugin.toad.model.dialog;

import static jp.hishidama.eclipse_plugin.util.StringUtil.nonNull;

import java.util.List;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;
import jp.hishidama.eclipse_plugin.toad.model.connection.ConnectionFigure;
import jp.hishidama.eclipse_plugin.toad.model.diagram.DiagramEditPart;
import jp.hishidama.eclipse_plugin.toad.model.dialog.section.PropertySection;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;

public class ConnectionPropertyDialog extends PropertyDialog {
	private Connection connection;

	private Text source;
	private Text target;

	public ConnectionPropertyDialog(ToadEditor editor, Connection connection) {
		super("コネクション", editor, connection);
		this.connection = (Connection) super.model;
	}

	@Override
	protected void createFields(TabFolder tab) {
		createBasicTab(tab);
		createMemoTab(tab);
		createModelLayoutTab(tab);
	}

	private void createBasicTab(TabFolder tab) {
		Composite composite = createBasicTabItem(tab);
		createConnectionSection(composite);
		createModelNameSection(composite);
	}

	private void createConnectionSection(Composite composite) {
		new ConnectionSection().createSection(composite);
	}

	class ConnectionSection extends PropertySection {
		public ConnectionSection() {
			super(ConnectionPropertyDialog.this);
		}

		public void createSection(Composite composite) {
			TextButtonPair s = createTextField(composite, "source", "jump");
			source = s.text;
			source.setEditable(false);
			source.setText(nonNull(ConnectionFigure.name(connection.getSource())));
			s.button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					jumpPort(connection.getSource());
				}
			});
			TextButtonPair t = createTextField(composite, "target", "jump");
			target = t.text;
			target.setEditable(false);
			target.setText(nonNull(ConnectionFigure.name(connection.getTarget())));
			t.button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					jumpPort(connection.getTarget());
				}
			});
		}
	}

	void jumpPort(NodeElement node) {
		DiagramEditPart diagram = editor.getDiagramEditPart();
		EditPart found = findEditPart(diagram, node);
		if (found != null) {
			EditPartViewer viewer = diagram.getViewer();
			viewer.select(found);
			viewer.reveal(found);
			this.close();
		}
	}

	private EditPart findEditPart(EditPart parent, NodeElement node) {
		@SuppressWarnings("unchecked")
		List<EditPart> list = parent.getChildren();
		for (EditPart part : list) {
			Object obj = part.getModel();
			if (obj == node) {
				return part;
			}
			EditPart r = findEditPart(part, node);
			if (r != null) {
				return r;
			}
		}
		return null;
	}
}
