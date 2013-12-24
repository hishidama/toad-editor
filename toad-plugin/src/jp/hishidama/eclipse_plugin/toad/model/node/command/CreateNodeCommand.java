package jp.hishidama.eclipse_plugin.toad.model.node.command;

import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.node.RectangleNode;

import org.eclipse.gef.commands.Command;

public class CreateNodeCommand extends Command {

	private Diagram diagram;
	private RectangleNode node;
	private int x;
	private int y;

	public CreateNodeCommand(Diagram diagram, RectangleNode node, int x, int y) {
		this.diagram = diagram;
		this.node = node;
		this.x = x;
		this.y = y;
	}

	@Override
	public void execute() {
		node.setX(x);
		node.setY(y);
		diagram.addContent(node);
	}

	@Override
	public void undo() {
		diagram.removeContent(node);
	}
}
