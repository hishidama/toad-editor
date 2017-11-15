package jp.hishidama.eclipse_plugin.toad.internal.util;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.toad.Activator;
import jp.hishidama.eclipse_plugin.util.JdtUtil;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.osgi.framework.Bundle;

public class JarUtil {

	public static Class<?> loadClass(IJavaProject javaProject, String className) throws JavaModelException,
			IOException, ClassNotFoundException {
		List<URL> list = new ArrayList<URL>(128);

		JdtUtil.collectProjectClassPath(list, javaProject);
		{
			Bundle bundle = Activator.getDefault().getBundle();
			IPath path = Path.fromPortableString("resource/toad-sub.jar");
			URL bundleUrl = FileLocator.find(bundle, path, null);
			URL url = FileLocator.resolve(bundleUrl);
			list.add(url);
		}
		URL[] urls = list.toArray(new URL[list.size()]);
		ClassLoader loader = URLClassLoader.newInstance(urls);
		Class<?> clazz = loader.loadClass(className);

		return clazz;
	}
}
