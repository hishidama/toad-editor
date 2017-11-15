package jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.gen;

import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;
import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;

public abstract class DiagramFileGenerator {

	public abstract Diagram createEmptyDiagram();

	protected static void createConnection(NodeElement s, NodeElement t) {
		Connection c = new Connection();
		c.setSource(s);
		c.setTarget(t);
		s.addOutgoing(c);
		t.addIncoming(c);
	}
}
