package jp.hishidama.eclipse_plugin.toad.extension;

public interface ClassGenerator {

	public String getCachedClassName(String className);

	public String getModelClassName(String modelName);

	public String getCachedModelClassName(String modelName);
}
