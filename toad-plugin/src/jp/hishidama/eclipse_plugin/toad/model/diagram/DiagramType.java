package jp.hishidama.eclipse_plugin.toad.model.diagram;

public enum DiagramType {
	UNKOWN, BATCH, JOBFLOW, FLOWPART;

	@Override
	public String toString() {
		String name = name();
		return name.charAt(0) + name.substring(1).toLowerCase();
	}
}
