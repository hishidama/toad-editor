package jp.hishidama.eclipse_plugin.toad.model.node;

import jp.hishidama.eclipse_plugin.dialog.ClassSelectionDialog.Filter;
import jp.hishidama.eclipse_plugin.toad.model.AbstractModel.ChangeTextCommand;

public interface ClassNameNode {

	public static final String PROP_CLASS_NAME = "className";

	public String getClassName();

	public void setClassName(String className);

	public ChangeTextCommand getClassNameCommand(String className);

	public String getClassNamePattern();

	public Filter getClassNameFilter();

	public boolean hasToadFile();

	public String getToadFileExtension();
}
