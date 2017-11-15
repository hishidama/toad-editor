package jp.hishidama.eclipse_plugin.toad.internal;

import jp.hishidama.eclipse_plugin.toad.model.AbstractNameModel;
import jp.hishidama.eclipse_plugin.toad.model.frame.FrameNode;
import jp.hishidama.eclipse_plugin.toad.model.node.datafile.DataFileNode;
import jp.hishidama.eclipse_plugin.toad.model.node.port.BasePort;

import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class ToadImages {

	public static Image getImage(Object node) {
		if (node instanceof BasePort) {
			String imageName;
			if (((BasePort) node).isIn()) {
				imageName = ISharedImages.IMG_TOOL_REDO;
			} else {
				imageName = ISharedImages.IMG_TOOL_UNDO;
			}
			return PlatformUI.getWorkbench().getSharedImages().getImage(imageName);
		}
		if (node instanceof DataFileNode) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
		}
		if (node instanceof FrameNode) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_DEF_VIEW);
		}
		if (node instanceof AbstractNameModel) {
			return JavaUI.getSharedImages().getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_PUBLIC);
		}
		return null;
	}
}
