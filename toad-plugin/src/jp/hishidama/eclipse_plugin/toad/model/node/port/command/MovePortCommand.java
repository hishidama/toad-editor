package jp.hishidama.eclipse_plugin.toad.model.node.port.command;

import jp.hishidama.eclipse_plugin.toad.model.node.port.BasePort;

import org.eclipse.gef.commands.Command;

public class MovePortCommand extends Command {

	private BasePort port;
	private int cx, old_cx;
	private int cy, old_cy;

	public MovePortCommand(BasePort port, int cx, int cy) {
		this.port = port;
		this.cx = cx;
		this.cy = cy;
	}

	@Override
	public void execute() {
		old_cx = port.getCx();
		old_cy = port.getCy();
		port.setCx(cx);
		port.setCy(cy);
	}

	@Override
	public void undo() {
		port.setCx(old_cx);
		port.setCy(old_cy);
	}
}
