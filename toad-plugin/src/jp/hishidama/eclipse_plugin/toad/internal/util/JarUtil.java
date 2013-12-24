package jp.hishidama.eclipse_plugin.toad.internal.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.toad.Activator;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.osgi.framework.Bundle;

public class JarUtil {

	public static Class<?> loadClass(IJavaProject javaProject, String className) throws JavaModelException,
			IOException, ClassNotFoundException {
		List<URL> list = new ArrayList<URL>(128);

		IProject project = javaProject.getProject();
		getURL(list, project, javaProject.getOutputLocation());
		IClasspathEntry[] classpath = javaProject.getRawClasspath();
		getURL(list, project, classpath);
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

	private static void getURL(List<URL> list, IProject project, IClasspathEntry[] classpath) {
		for (IClasspathEntry entry : classpath) {
			IPath pp = entry.getOutputLocation();
			if (pp == null) {
				pp = entry.getPath();
			}
			getURL(list, project, pp);
		}
	}

	private static void getURL(List<URL> list, IProject project, IPath pp) {
		try {
			if (pp.toFile().exists()) {
				URL url = pp.toFile().toURI().toURL();
				list.add(url);
				return;
			}
			IPath vp = JavaCore.getResolvedVariablePath(pp);
			if (vp != null) {
				URL url = vp.toFile().toURI().toURL();
				list.add(url);
				return;
			}
			IFolder folder = project.getParent().getFolder(pp);
			if (folder != null) {
				URI uri = folder.getLocationURI();
				if (uri != null) {
					String s = uri.toURL().toExternalForm() + "/";
					list.add(new URL(s));
					return;
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
