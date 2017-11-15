package jp.hishidama.eclipse_plugin.toad.editor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.EventObject;
import java.util.List;

import jp.hishidama.eclipse_plugin.toad.Activator;
import jp.hishidama.eclipse_plugin.toad.editor.action.AutoLayoutAction;
import jp.hishidama.eclipse_plugin.toad.editor.action.CopyAction;
import jp.hishidama.eclipse_plugin.toad.editor.action.CutAction;
import jp.hishidama.eclipse_plugin.toad.editor.action.GenerateDslClassAction;
import jp.hishidama.eclipse_plugin.toad.editor.action.OpenClassAction;
import jp.hishidama.eclipse_plugin.toad.editor.action.OpenDiagramAction;
import jp.hishidama.eclipse_plugin.toad.editor.action.OpenDmdlAction;
import jp.hishidama.eclipse_plugin.toad.editor.action.PasteAction;
import jp.hishidama.eclipse_plugin.toad.editor.action.SiblingDataModelAction;
import jp.hishidama.eclipse_plugin.toad.editor.action.ValidateAction;
import jp.hishidama.eclipse_plugin.toad.editor.drop.ToadFileDropTargetListener;
import jp.hishidama.eclipse_plugin.toad.editor.drop.ToadMethodDropTargetListener;
import jp.hishidama.eclipse_plugin.toad.editor.menu.ToadContextMenuProvider;
import jp.hishidama.eclipse_plugin.toad.editor.outline.ToadOutlinePage;
import jp.hishidama.eclipse_plugin.toad.model.AssignmentId;
import jp.hishidama.eclipse_plugin.toad.model.ToadEditPartFactory;
import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.diagram.DiagramEditPart;
import jp.hishidama.eclipse_plugin.toad.model.gson.ToadGson;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.validation.ToadMarker;
import jp.hishidama.eclipse_plugin.toad.validation.ValidateType;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.SelectionManager;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.DirectEditAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite.FlyoutPreferences;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class ToadEditor extends GraphicalEditorWithFlyoutPalette implements ITabbedPropertySheetPageContributor,
		IGotoMarker {

	private AssignmentId assignmentId = new AssignmentId();

	public ToadEditor() {
		DefaultEditDomain domain = new DefaultEditDomain(this);
		setEditDomain(domain);
	}

	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);

		String name = input.getName();
		setPartName(name);
	}

	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();

		GraphicalViewer viewer = getGraphicalViewer();
		viewer.setEditPartFactory(new ToadEditPartFactory());

		ActionRegistry registry = getActionRegistry();
		{ // zoom
			ScalableRootEditPart rootEditPart = new ScalableRootEditPart();
			viewer.setRootEditPart(rootEditPart);

			ZoomManager manager = rootEditPart.getZoomManager();
			registry.registerAction(new ZoomInAction(manager));
			registry.registerAction(new ZoomOutAction(manager));

			// 可能なスケールのリスト
			double[] zoomLevels = { 0.1, 0.2, 0.25, 0.5, 0.75, 1.0, 1.25, 1.5, 1.75, 2.0, 2.5, 3.0 };
			manager.setZoomLevels(zoomLevels);
			List<String> zoomContributions = Arrays.asList(ZoomManager.FIT_ALL, ZoomManager.FIT_HEIGHT,
					ZoomManager.FIT_WIDTH);
			manager.setZoomLevelContributions(zoomContributions);

			// マウスホイールによるズームハンドラ、キーハンドラをビューアに設定
			// viewer.setProperty(MouseWheelHandler.KeyGenerator.getKey(SWT.NONE),
			// MouseWheelZoomHandler.SINGLETON);
		}

		{ // context menu
			ToadContextMenuProvider provider = new ToadContextMenuProvider(viewer, registry);
			viewer.setContextMenu(provider);
			getSite().registerContextMenu(provider, viewer);
		}
		{ // key
			KeyHandler handler = new GraphicalViewerKeyHandler(viewer);
			viewer.setKeyHandler(handler);
			handler.put(KeyStroke.getPressed(SWT.F2, 0), registry.getAction(GEFActionConstants.DIRECT_EDIT));
			handler.put(KeyStroke.getPressed('+', SWT.KEYPAD_ADD, 0), registry.getAction(GEFActionConstants.ZOOM_IN));
			handler.put(KeyStroke.getPressed('-', SWT.KEYPAD_SUBTRACT, 0),
					registry.getAction(GEFActionConstants.ZOOM_OUT));
		}
		{ // file drop
			viewer.addDropTargetListener(new ToadFileDropTargetListener(this, viewer));
			viewer.addDropTargetListener(new ToadMethodDropTargetListener(this, viewer));
		}

		viewer.getControl().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				GraphicalViewer viewer = getGraphicalViewer();
				ScalableRootEditPart root = (ScalableRootEditPart) viewer.getRootEditPart();
				IFigure viewport = root.getFigure();
				IFigure found = viewport.findFigureAt(e.x, e.y);
				if (found == viewport) {
					DiagramEditPart editPart = getDiagramEditPart();
					editPart.performOpen();
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void createActions() {
		super.createActions();
		ActionRegistry registry = getActionRegistry();
		{
			IAction action = new CopyAction(this);
			registry.registerAction(action);
			getSelectionActions().add(action.getId());
		}
		{
			IAction action = new CutAction(this, registry); // CopyActionより後に生成する
			registry.registerAction(action);
			getSelectionActions().add(action.getId());
		}
		{
			IAction action = new PasteAction(this);
			registry.registerAction(action);
			getSelectionActions().add(action.getId());
		}
		{
			IAction action = new DirectEditAction(this);
			registry.registerAction(action);
			getSelectionActions().add(action.getId());
		}
		{
			IAction action = new AutoLayoutAction(this);
			registry.registerAction(action);
			getSelectionActions().add(action.getId());
		}
		{
			IAction action = new SiblingDataModelAction(this);
			registry.registerAction(action);
			getSelectionActions().add(action.getId());
		}
		{
			IAction action = new OpenDmdlAction(this);
			registry.registerAction(action);
			getSelectionActions().add(action.getId());
		}
		{
			IAction action = new OpenClassAction(this);
			registry.registerAction(action);
			getSelectionActions().add(action.getId());
		}
		{
			IAction action = new OpenDiagramAction(this);
			registry.registerAction(action);
			getSelectionActions().add(action.getId());
		}
		for (ValidateType type : ValidateType.values()) {
			IAction action = new ValidateAction(this, type);
			registry.registerAction(action);
			getSelectionActions().add(action.getId());
		}
		// {
		// IAction action = new ShowOperatorDslTemplateAction(this);
		// registry.registerAction(action);
		// getSelectionActions().add(action.getId());
		// }
		{
			IAction action = new GenerateDslClassAction(this);
			registry.registerAction(action);
			getSelectionActions().add(action.getId());
		}
	}

	@Override
	protected void initializeGraphicalViewer() {
		super.initializeGraphicalViewer();

		GraphicalViewer viewer = getGraphicalViewer();
		{
			Diagram diagram;
			try {
				diagram = load();
			} catch (CoreException e) {
				ILog log = Activator.getDefault().getLog();
				log.log(e.getStatus());
				diagram = new Diagram();
			}
			viewer.setContents(diagram);
		}
		appendPalette();

		// palette drag
		PaletteViewer paletteViewer = getPaletteViewerProvider().getEditDomain().getPaletteViewer();
		paletteViewer.addDragSourceListener(new TemplateTransferDragSourceListener(paletteViewer));
		// palette copy
		final CopyAction copy = (CopyAction) getActionRegistry().getAction(ActionFactory.COPY.getId());
		paletteViewer.addSelectionChangedListener(copy);
		// palette context-menu
		paletteViewer.getContextMenu().addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				manager.appendToGroup(GEFActionConstants.GROUP_COPY, copy);
			}
		});

		// editor drop
		viewer.addDropTargetListener(new TemplateTransferDropTargetListener(viewer));
	}

	public ScalableRootEditPart getScalableRootEditPart() {
		return (ScalableRootEditPart) getGraphicalViewer().getRootEditPart();
	}

	@Override
	public DefaultEditDomain getEditDomain() {
		return super.getEditDomain();
	}

	public void addSelectionSynchronizerViewer(EditPartViewer viewer) {
		getSelectionSynchronizer().addViewer(viewer);
	}

	private ToadEditorPalette paletteRoot;

	@Override
	protected PaletteRoot getPaletteRoot() {
		this.paletteRoot = new ToadEditorPalette(this);
		return paletteRoot;
	}

	private void appendPalette() {
		Diagram diagram = getDiagram();
		IProject project = getFile().getProject();
		paletteRoot.addSecond(diagram, project);
	}

	@Override
	protected FlyoutPreferences getPalettePreferences() {
		FlyoutPreferences pref = super.getPalettePreferences();
		if (pref.getPaletteWidth() <= 0) {
			pref.setDockLocation(PositionConstants.EAST);
			pref.setPaletteState(FlyoutPaletteComposite.STATE_PINNED_OPEN);
			pref.setPaletteWidth(160);
		}
		return pref;
	}

	public Diagram getDiagram() {
		DiagramEditPart editPart = getDiagramEditPart();
		Diagram diagram = editPart.getModel();
		return diagram;
	}

	public DiagramEditPart getDiagramEditPart() {
		return (DiagramEditPart) getGraphicalViewer().getContents();
	}

	public SelectionManager getSelectionManager() {
		return getGraphicalViewer().getSelectionManager();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		monitor.beginTask("", 4);
		try {
			Diagram diagram = getDiagram();

			byte[] buf;
			try {
				ToadGson gson = new ToadGson();
				buf = gson.serialize(getFile().getName(), diagram, monitor);
			} catch (Exception e) {
				ILog log = Activator.getDefault().getLog();
				log.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "save error", e));
				return;
			}

			InputStream is = new ByteArrayInputStream(buf);
			try {
				IFile file = getFile();
				file.setContents(is, true, false, new SubProgressMonitor(monitor, 1));
			} catch (CoreException e) {
				ILog log = Activator.getDefault().getLog();
				log.log(e.getStatus());
				return;
			}
			getCommandStack().markSaveLocation();
		} finally {
			monitor.done();
		}
	}

	@Override
	public void commandStackChanged(EventObject event) {
		firePropertyChange(PROP_DIRTY);
		super.commandStackChanged(event);
	}

	protected Diagram load() throws CoreException {
		IFile file = getFile();

		ToadGson gson = new ToadGson();
		Diagram diagram = gson.load(file);

		for (NodeElement node : diagram.getContents()) {
			initializedId(node);
		}

		return diagram;
	}

	private void initializedId(NodeElement node) {
		assignmentId.initializeId(node.getId());

		for (NodeElement child : node.getChildren()) {
			initializedId(child);
		}
	}

	public int newId() {
		return assignmentId.newId();
	}

	public IFile getFile() {
		return ((IFileEditorInput) getEditorInput()).getFile();
	}

	public IProject getProject() {
		return getFile().getProject();
	}

	@Override
	public String getContributorId() {
		return getSite().getId();
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == IPropertySheetPage.class) {
			return new TabbedPropertySheetPage(this);
		}
		if (adapter == ZoomManager.class) {
			return getScalableRootEditPart().getZoomManager();
		}
		if (adapter == IContentOutlinePage.class) {
			return new ToadOutlinePage(this);
		}
		if (adapter == IGotoMarker.class) {
			return this;
		}
		return super.getAdapter(adapter);
	}

	public static ToadEditor getActiveToadEditor() {
		return (ToadEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	}

	@Override
	public void gotoMarker(IMarker marker) {
		ToadMarker.gotoMarker(getGraphicalViewer(), getDiagram(), marker);
	}

	public static ToadEditor getToadEditor(EditPartViewer viewer) {
		DefaultEditDomain domain = (DefaultEditDomain) viewer.getEditDomain();
		ToadEditor editor = (ToadEditor) domain.getEditorPart();
		if (editor == null) {
			editor = getActiveToadEditor();
		}
		return editor;
	}
}
