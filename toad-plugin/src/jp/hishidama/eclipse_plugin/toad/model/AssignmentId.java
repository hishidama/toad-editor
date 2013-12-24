package jp.hishidama.eclipse_plugin.toad.model;

public class AssignmentId {

	private int id;

	public synchronized void initializeId(int id) {
		this.id = Math.max(this.id, id);
	}

	public synchronized int newId() {
		return ++id;
	}
}
