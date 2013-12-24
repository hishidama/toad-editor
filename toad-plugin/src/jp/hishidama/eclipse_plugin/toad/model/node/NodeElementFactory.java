package jp.hishidama.eclipse_plugin.toad.model.node;

import jp.hishidama.eclipse_plugin.toad.editor.ToadEditor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.gef.requests.CreationFactory;

public abstract class NodeElementFactory implements CreationFactory {
	private ToadEditor editor;

	protected NodeElementFactory(ToadEditor editor) {
		this.editor = editor;
	}

	protected final int newId() {
		return editor.newId();
	}

	protected final String getClassName(String name) {
		String pack = getToadPackage();
		return pack + "." + name;
	}

	protected final String getToadPackage() {
		IFile file = editor.getFile();
		IPath dir = file.getProjectRelativePath().removeLastSegments(1);
		String path = dir.toPortableString();
		if (path.startsWith("src/main/toad/")) {
			path = dir.removeFirstSegments(3).toPortableString();
		}
		return path.replace('/', '.');
	}
}
