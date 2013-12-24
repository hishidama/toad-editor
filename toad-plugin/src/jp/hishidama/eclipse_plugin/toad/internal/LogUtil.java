package jp.hishidama.eclipse_plugin.toad.internal;

import jp.hishidama.eclipse_plugin.toad.Activator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class LogUtil {

	public static IStatus infoStatus(String message) {
		return new Status(IStatus.INFO, Activator.PLUGIN_ID, message);
	}

	public static IStatus warnStatus(String message, Throwable t) {
		return new Status(IStatus.WARNING, Activator.PLUGIN_ID, message, t);
	}

	public static IStatus errorStatus(String message, Throwable t) {
		return new Status(IStatus.ERROR, Activator.PLUGIN_ID, message, t);
	}

	public static IStatus logInfo(String message) {
		IStatus status = infoStatus(message);
		log(status);
		return status;
	}

	public static IStatus logWarn(String message) {
		return logWarn(message, null);
	}

	public static IStatus logWarn(String message, Throwable t) {
		IStatus status = warnStatus(message, t);
		log(status);
		return status;
	}

	public static IStatus logError(String message, Throwable t) {
		IStatus status = errorStatus(message, t);
		log(status);
		return status;
	}

	public static void log(IStatus status) {
		Activator.getDefault().getLog().log(status);
	}
}
