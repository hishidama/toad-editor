package jp.hishidama.eclipse_plugin.toad.internal.extension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import jp.hishidama.eclipse_plugin.toad.Activator;
import jp.hishidama.eclipse_plugin.toad.extension.ToadImporterExporterProperty;
import jp.hishidama.eclipse_plugin.toad.internal.LogUtil;

public class ImporterExporterExtensionUtil {
	private static final String EXTENSION_POINT_ID = Activator.PLUGIN_ID + ".importerExporterPropertyExtension";

	public static ToadImporterExporterProperty getExtension(String name) {
		if (name == null) {
			return null;
		}

		List<ToadImporterExporterProperty> list = getExtensionList();
		for (ToadImporterExporterProperty p : list) {
			if (name.equals(p.getName())) {
				return p;
			}
		}
		return null;
	}

	private static List<ToadImporterExporterProperty> list;

	public static List<ToadImporterExporterProperty> getExtensionList() {
		if (list != null) {
			return list;
		}

		IExtensionRegistry registory = Platform.getExtensionRegistry();
		IExtensionPoint point = registory.getExtensionPoint(EXTENSION_POINT_ID);
		if (point == null) {
			throw new IllegalStateException(EXTENSION_POINT_ID);
		}

		list = new ArrayList<ToadImporterExporterProperty>();
		for (IExtension extension : point.getExtensions()) {
			for (IConfigurationElement element : extension.getConfigurationElements()) {
				try {
					Object obj = element.createExecutableExtension("class"); // class属性
					if (obj instanceof ToadImporterExporterProperty) {
						list.add((ToadImporterExporterProperty) obj);
					}
				} catch (CoreException e) {
					LogUtil.log(e.getStatus());
				}
			}
		}
		Collections.sort(list, new Sorter());

		list.add(new DefaultImporterProperty());
		list.add(new DefaultExporterProperty());
		return list;
	}

	private static class Sorter implements Comparator<ToadImporterExporterProperty> {
		@Override
		public int compare(ToadImporterExporterProperty o1, ToadImporterExporterProperty o2) {
			int c = o1.getBaseName().compareTo(o2.getBaseName());
			if (c != 0) {
				return c;
			}
			int i1 = o1.isImporter() ? 0 : 1;
			int i2 = o2.isImporter() ? 0 : 1;
			return i1 - i2;
		}
	}
}
