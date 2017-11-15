package jp.hishidama.eclipse_plugin.util;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

public class ToadCommandUtil {

	public static void add(CompoundCommand compound, Command command) {
		if (command != null && command.canExecute()) {
			compound.add(command);
		}
	}
}
