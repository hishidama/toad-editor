package jp.hishidama.eclipse_plugin.toad.validation;

import java.util.Set;

import jp.hishidama.eclipse_plugin.toad.model.AbstractModel;
import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;
import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.xtext.dmdl_editor.ui.internal.LogUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;

public class ToadMarker {
	public static void deleteAllMarkers(IFile file) {
		try {
			file.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			LogUtil.log(e.getStatus());
		}
	}

	public static void deleteMarkers(IFile file, Set<String> idSet) {
		try {
			IMarker[] markers = file.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
			for (IMarker marker : markers) {
				String id = marker.getAttribute(IMarker.SOURCE_ID, null);
				if (idSet.contains(id)) {
					marker.delete();
				}
			}
		} catch (CoreException e) {
			LogUtil.log(e.getStatus());
		}
	}

	public static void createMarker(IFile file, AbstractModel model, IStatus status) {
		createMarker(file, status, model.getDisplayLocation(), model.getIdString());
	}

	private static void createMarker(IFile file, IStatus status, String location, String id) {
		try {
			IMarker marker = file.createMarker(IMarker.PROBLEM);
			marker.setAttribute(IMarker.SEVERITY, getMarkerSeverity(status));
			marker.setAttribute(IMarker.MESSAGE, status.getMessage());
			marker.setAttribute(IMarker.LOCATION, location);
			marker.setAttribute(IMarker.SOURCE_ID, id);
		} catch (CoreException e) {
			LogUtil.log(e.getStatus());
		}
	}

	private static int getMarkerSeverity(IStatus status) {
		switch (status.getSeverity()) {
		default:
		case IStatus.ERROR:
			return IMarker.SEVERITY_ERROR;
		case IStatus.WARNING:
			return IMarker.SEVERITY_WARNING;
		case IStatus.INFO:
			return IMarker.SEVERITY_INFO;
		}
	}

	public static void gotoMarker(EditPartViewer viewer, Diagram diagram, IMarker marker) {
		String id = marker.getAttribute(IMarker.SOURCE_ID, null);
		AbstractModel node = findNode(diagram, id);
		if (node != null) {
			EditPart part = node.getEditPart();
			viewer.select(part);
			viewer.reveal(part);
		}

	}

	private static AbstractModel findNode(Diagram diagram, String idString) {
		if (idString == null) {
			return null;
		}
		try {
			int n = idString.indexOf("->");
			if (n >= 0) {
				int sid = Integer.parseInt(idString.substring(0, n).trim());
				int tid = Integer.parseInt(idString.substring(n + 2).trim());
				NodeElement s = diagram.findContent(sid);
				if (s != null) {
					for (Connection c : s.getOutgoings()) {
						NodeElement t = c.getOpposite(s);
						if (t != null && t.getId() == tid) {
							return c;
						}
					}
				}
				return null;
			} else {
				int id = Integer.parseInt(idString);
				return diagram.findContent(id);
			}
		} catch (NumberFormatException e) {
			LogUtil.logError("wrong idString=" + idString, e);
			return null;
		}
	}
}
