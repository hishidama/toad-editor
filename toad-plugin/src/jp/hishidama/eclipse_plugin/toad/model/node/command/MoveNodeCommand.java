package jp.hishidama.eclipse_plugin.toad.model.node.command;

import jp.hishidama.eclipse_plugin.toad.model.node.RectangleNode;

import org.eclipse.gef.commands.Command;

public class MoveNodeCommand extends Command {

	private RectangleNode node;
	private int x, old_x;
	private int y, old_y;
	private int width, old_width;
	private int height, old_height;

	public MoveNodeCommand(RectangleNode node, int x, int y, int width,
			int height) {
		this.node = node;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	@Override
	public void execute() {
		old_x = node.getX();
		old_y = node.getY();
		old_width = node.getWidth();
		old_height = node.getHeight();
		node.setX(x);
		node.setY(y);
		node.setWidth(width);
		node.setHeight(height);
	}

	@Override
	public void undo() {
		node.setX(old_x);
		node.setY(old_y);
		node.setWidth(old_width);
		node.setHeight(old_height);
	}
}
