package jp.hishidama.eclipse_plugin.toad.editor.outline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.property.generic.NameNode;

import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class TreeGroup {
	private String name;
	private List<NodeElement> list = new ArrayList<NodeElement>();
	private Comparator<NodeElement> comparator;
	private boolean sorted = false;

	public static enum SortType {
		NONE, NAME
	}

	public TreeGroup(String name, SortType type) {
		this.name = name;
		switch (type) {
		case NAME:
			comparator = new NameComaprator();
			break;
		default:
			comparator = null;
			break;
		}
	}

	public void add(NodeElement node) {
		list.add(node);
		sorted = false;
	}

	public String getName() {
		return name;
	}

	public List<NodeElement> getChildren() {
		if (!sorted) {
			if (comparator != null) {
				Collections.sort(list, comparator);
			}
			sorted = true;
		}
		return list;
	}

	private static class NameComaprator implements Comparator<NodeElement> {
		@Override
		public int compare(NodeElement o1, NodeElement o2) {
			int c;
			if (o1 instanceof NameNode && o2 instanceof NameNode) {
				c = ((NameNode) o1).getName().compareTo(((NameNode) o2).getName());
				if (c != 0) {
					return c;
				}
			}
			c = o1.getDescription().compareTo(o2.getDescription());
			if (c != 0) {
				return c;
			}
			Rectangle r1 = o1.getCoreBounds();
			Rectangle r2 = o2.getCoreBounds();
			int x1 = r1.x /* + r1.width */;
			int x2 = r2.x /* + r2.width */;
			c = x1 - x2;
			if (c != 0) {
				return c;
			}
			int y1 = r1.y /* + r1.height */;
			int y2 = r2.y /* + r2.height */;
			c = y1 - y2;
			if (c != 0) {
				return c;
			}
			return o1.getId() - o2.getId();
		}
	}

	public static class EditPart extends AbstractTreeEditPart {

		@Override
		public TreeGroup getModel() {
			return (TreeGroup) super.getModel();
		}

		@Override
		protected List<NodeElement> getModelChildren() {
			return getModel().getChildren();
		}

		@Override
		protected String getText() {
			return getModel().getName();
		}

		@Override
		protected Image getImage() {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
		}
	}
}
