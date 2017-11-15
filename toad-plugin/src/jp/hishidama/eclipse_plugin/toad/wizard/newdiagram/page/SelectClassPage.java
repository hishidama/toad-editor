package jp.hishidama.eclipse_plugin.toad.wizard.newdiagram.page;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.dialogs.WizardExportResourcesPage;

public class SelectClassPage extends WizardExportResourcesPage {

	public SelectClassPage(String diagramName, String dslName, IStructuredSelection selection) {
		super("SelectClassPage", resourceSelection(selection));

		setTitle(MessageFormat.format("{0}クラスの選択", diagramName));
		setDescription(MessageFormat.format("{0}が書かれているクラスを選択して下さい。", dslName));
	}

	private static IStructuredSelection resourceSelection(IStructuredSelection selection) {
		if (selection == null) {
			return selection;
		}

		List<IResource> list = new ArrayList<IResource>();
		for (Iterator<?> i = selection.iterator(); i.hasNext();) {
			Object object = i.next();
			if (object instanceof IResource) {
				IResource resource = (IResource) object;
				list.add(resource);
			} else if (object instanceof ICompilationUnit) {
				IResource resource = ((ICompilationUnit) object).getResource();
				list.add(resource);
			}
		}
		return new StructuredSelection(list);
	}

	@Override
	public void handleEvent(Event event) {
	}

	@Override
	protected void createDestinationGroup(Composite parent) {
	}

	@Override
	protected boolean validateSourceGroup() {
		List<?> list = super.getSelectedResources();
		if (list == null || list.size() < 1) {
			setErrorMessage("ファイルを選択して下さい。");
			return false;
		}
		setErrorMessage(null);
		return true;
	}

	@SuppressWarnings("unchecked")
	public List<IFile> getSelectedFile() {
		List<IFile> list = new ArrayList<IFile>(32);
		for (Iterator<IFile> i = getSelectedResourcesIterator(); i.hasNext();) {
			IFile file = i.next();
			if ("java".equals(file.getFileExtension())) {
				list.add(file);
			}
		}
		Collections.sort(list, new Comparator<IFile>() {
			@Override
			public int compare(IFile o1, IFile o2) {
				String p1 = o1.getFullPath().toPortableString();
				String p2 = o2.getFullPath().toPortableString();
				return p1.compareTo(p2);
			}
		});
		return list;
	}
}
