package jp.hishidama.eclipse_plugin.toad.model.dialog;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;
import jp.hishidama.eclipse_plugin.toad.model.dialog.section.FlowpartParameterSection;
import jp.hishidama.eclipse_plugin.toad.model.dialog.section.PortSection;
import jp.hishidama.eclipse_plugin.toad.model.frame.FlowpartFrameNode;
import jp.hishidama.eclipse_plugin.toad.model.frame.FrameNode;
import jp.hishidama.eclipse_plugin.toad.model.node.port.JobPort;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;

public class FramePropertyDialog extends PropertyDialog {
	private FrameNode frame;

	private PortSection<JobPort> portTab;
	private FlowpartParameterSection paramTab;

	public FramePropertyDialog(String title, ToadEditor editor, FrameNode frame) {
		super(title, editor, frame);
		this.frame = (FrameNode) super.model;
	}

	@Override
	protected void createFields(TabFolder tab) {
		createBasicTab(tab);
		createMemoTab(tab);
		createPortTab(tab);
		if (frame instanceof FlowpartFrameNode) {
			createParameterTab(tab);
		}
	}

	private void createBasicTab(TabFolder tab) {
		Composite composite = createBasicTabItem(tab);
		createBaseSection(composite);
		createClassNameSection(composite);
	}

	private void createPortTab(TabFolder tab) {
		portTab = new PortSection<JobPort>(editor, this, frame) {
			@Override
			protected JobPort createPort(boolean in) {
				JobPort port = new JobPort();
				port.setIn(in);
				port.setNamePosition(in ? PositionConstants.TOP : PositionConstants.BOTTOM);
				return port;
			}
		};
		portTab.createTab(tab);
	}

	private void createParameterTab(TabFolder tab) {
		paramTab = new FlowpartParameterSection(this, (FlowpartFrameNode) frame);
		paramTab.createTab(tab);
	}
}
