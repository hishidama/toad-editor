package jp.hishidama.eclipse_plugin.toad.model.diagram;

import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.List;

import org.eclipse.draw2d.AncestorListener;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.CoordinateListener;
import org.eclipse.draw2d.EventDispatcher;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.FocusEvent;
import org.eclipse.draw2d.FocusListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IClippingStrategy;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.KeyEvent;
import org.eclipse.draw2d.KeyListener;
import org.eclipse.draw2d.LayoutListener;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.TreeSearch;
import org.eclipse.draw2d.UpdateManager;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.geometry.Translatable;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;

/**
 * @see org.eclipse.draw2d.ShortestPathConnectionRouter
 */
@SuppressWarnings("rawtypes")
public class DelegateFigure implements IFigure {

	protected final IFigure delegate;

	public DelegateFigure(IFigure figure) {
		this.delegate = figure;
	}

	@Override
	public void add(IFigure figure) {
		delegate.add(figure);
	}

	@Override
	public void add(IFigure figure, int index) {
		delegate.add(figure, index);
	}

	@Override
	public void add(IFigure figure, Object constraint) {
		delegate.add(figure, constraint);
	}

	@Override
	public void add(IFigure figure, Object constraint, int index) {
		delegate.add(figure, constraint, index);
	}

	@Override
	public void addAncestorListener(AncestorListener listener) {
		delegate.addAncestorListener(listener);
	}

	@Override
	public void addCoordinateListener(CoordinateListener listener) {
		delegate.addCoordinateListener(listener);
	}

	@Override
	public void addFigureListener(FigureListener listener) {
		delegate.addFigureListener(listener);
	}

	@Override
	public void addFocusListener(FocusListener listener) {
		delegate.addFocusListener(listener);
	}

	@Override
	public void addKeyListener(KeyListener listener) {
		delegate.addKeyListener(listener);
	}

	@Override
	public void addLayoutListener(LayoutListener listener) {
		delegate.addLayoutListener(listener);
	}

	@Override
	public void addMouseListener(MouseListener listener) {
		delegate.addMouseListener(listener);
	}

	@Override
	public void addMouseMotionListener(MouseMotionListener listener) {
		delegate.addMouseMotionListener(listener);
	}

	@Override
	public void addNotify() {
		delegate.addNotify();
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		delegate.addPropertyChangeListener(listener);
	}

	@Override
	public void addPropertyChangeListener(String property,
			PropertyChangeListener listener) {
		delegate.addPropertyChangeListener(property, listener);
	}

	@Override
	public boolean containsPoint(int x, int y) {
		return delegate.containsPoint(x, y);
	}

	@Override
	public boolean containsPoint(Point p) {
		return delegate.containsPoint(p);
	}

	@Override
	public void erase() {
		delegate.erase();
	}

	@Override
	public IFigure findFigureAt(int x, int y) {
		return delegate.findFigureAt(x, y);
	}

	@Override
	public IFigure findFigureAt(int x, int y, TreeSearch search) {
		return delegate.findFigureAt(x, y, search);
	}

	@Override
	public IFigure findFigureAt(Point p) {
		return delegate.findFigureAt(p);
	}

	@Override
	public IFigure findFigureAtExcluding(int x, int y, Collection collection) {
		return delegate.findFigureAtExcluding(x, y, collection);
	}

	@Override
	public IFigure findMouseEventTargetAt(int x, int y) {
		return delegate.findMouseEventTargetAt(x, y);
	}

	@Override
	public Color getBackgroundColor() {
		return delegate.getBackgroundColor();
	}

	@Override
	public Border getBorder() {
		return delegate.getBorder();
	}

	@Override
	public Rectangle getBounds() {
		return delegate.getBounds();
	}

	@Override
	public List getChildren() {
		return delegate.getChildren();
	}

	@Override
	public Rectangle getClientArea() {
		return delegate.getClientArea();
	}

	@Override
	public Rectangle getClientArea(Rectangle rect) {
		return delegate.getClientArea(rect);
	}

	@Override
	public IClippingStrategy getClippingStrategy() {
		return delegate.getClippingStrategy();
	}

	@Override
	public Cursor getCursor() {
		return delegate.getCursor();
	}

	@Override
	public Font getFont() {
		return delegate.getFont();
	}

	@Override
	public Color getForegroundColor() {
		return delegate.getForegroundColor();
	}

	@Override
	public Insets getInsets() {
		return delegate.getInsets();
	}

	@Override
	public LayoutManager getLayoutManager() {
		return delegate.getLayoutManager();
	}

	@Override
	public Color getLocalBackgroundColor() {
		return delegate.getLocalBackgroundColor();
	}

	@Override
	public Color getLocalForegroundColor() {
		return delegate.getLocalForegroundColor();
	}

	@Override
	public Dimension getMaximumSize() {
		return delegate.getMaximumSize();
	}

	@Override
	public Dimension getMinimumSize() {
		return delegate.getMinimumSize();
	}

	@Override
	public Dimension getMinimumSize(int wHint, int hHint) {
		return delegate.getMinimumSize(wHint, hHint);
	}

	@Override
	public IFigure getParent() {
		return delegate.getParent();
	}

	@Override
	public Dimension getPreferredSize() {
		return delegate.getPreferredSize();
	}

	@Override
	public Dimension getPreferredSize(int wHint, int hHint) {
		return delegate.getPreferredSize(wHint, hHint);
	}

	@Override
	public Dimension getSize() {
		return delegate.getSize();
	}

	@Override
	public IFigure getToolTip() {
		return delegate.getToolTip();
	}

	@Override
	public UpdateManager getUpdateManager() {
		return delegate.getUpdateManager();
	}

	@Override
	public void handleFocusGained(FocusEvent event) {
		delegate.handleFocusGained(event);
	}

	@Override
	public void handleFocusLost(FocusEvent event) {
		delegate.handleFocusLost(event);
	}

	@Override
	public void handleKeyPressed(KeyEvent event) {
		delegate.handleKeyPressed(event);
	}

	@Override
	public void handleKeyReleased(KeyEvent event) {
		delegate.handleKeyReleased(event);
	}

	@Override
	public void handleMouseDoubleClicked(MouseEvent event) {
		delegate.handleMouseDoubleClicked(event);
	}

	@Override
	public void handleMouseDragged(MouseEvent event) {
		delegate.handleMouseDragged(event);
	}

	@Override
	public void handleMouseEntered(MouseEvent event) {
		delegate.handleMouseEntered(event);
	}

	@Override
	public void handleMouseExited(MouseEvent event) {
		delegate.handleMouseExited(event);
	}

	@Override
	public void handleMouseHover(MouseEvent event) {
		delegate.handleMouseHover(event);
	}

	@Override
	public void handleMouseMoved(MouseEvent event) {
		delegate.handleMouseMoved(event);
	}

	@Override
	public void handleMousePressed(MouseEvent event) {
		delegate.handleMousePressed(event);
	}

	@Override
	public void handleMouseReleased(MouseEvent event) {
		delegate.handleMouseReleased(event);
	}

	@Override
	public boolean hasFocus() {
		return delegate.hasFocus();
	}

	@Override
	public EventDispatcher internalGetEventDispatcher() {
		return delegate.internalGetEventDispatcher();
	}

	@Override
	public boolean intersects(Rectangle rect) {
		return delegate.intersects(rect);
	}

	@Override
	public void invalidate() {
		delegate.invalidate();
	}

	@Override
	public void invalidateTree() {
		delegate.invalidateTree();
	}

	@Override
	public boolean isCoordinateSystem() {
		return delegate.isCoordinateSystem();
	}

	@Override
	public boolean isEnabled() {
		return delegate.isEnabled();
	}

	@Override
	public boolean isFocusTraversable() {
		return delegate.isFocusTraversable();
	}

	@Override
	public boolean isMirrored() {
		return delegate.isMirrored();
	}

	@Override
	public boolean isOpaque() {
		return delegate.isOpaque();
	}

	@Override
	public boolean isRequestFocusEnabled() {
		return delegate.isRequestFocusEnabled();
	}

	@Override
	public boolean isShowing() {
		return delegate.isShowing();
	}

	@Override
	public boolean isVisible() {
		return delegate.isVisible();
	}

	@Override
	public void paint(Graphics graphics) {
		delegate.paint(graphics);
	}

	@Override
	public void remove(IFigure figure) {
		delegate.remove(figure);
	}

	@Override
	public void removeAncestorListener(AncestorListener listener) {
		delegate.removeAncestorListener(listener);
	}

	@Override
	public void removeCoordinateListener(CoordinateListener listener) {
		delegate.removeCoordinateListener(listener);
	}

	@Override
	public void removeFigureListener(FigureListener listener) {
		delegate.removeFigureListener(listener);
	}

	@Override
	public void removeFocusListener(FocusListener listener) {
		delegate.removeFocusListener(listener);
	}

	@Override
	public void removeKeyListener(KeyListener listener) {
		delegate.removeKeyListener(listener);
	}

	@Override
	public void removeLayoutListener(LayoutListener listener) {
		delegate.removeLayoutListener(listener);
	}

	@Override
	public void removeMouseListener(MouseListener listener) {
		delegate.removeMouseListener(listener);
	}

	@Override
	public void removeMouseMotionListener(MouseMotionListener listener) {
		delegate.removeMouseMotionListener(listener);
	}

	@Override
	public void removeNotify() {
		delegate.removeNotify();
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		delegate.removePropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(String property,
			PropertyChangeListener listener) {
		delegate.removePropertyChangeListener(property, listener);
	}

	@Override
	public void repaint() {
		delegate.repaint();
	}

	@Override
	public void repaint(int x, int y, int w, int h) {
		delegate.repaint(x, y, w, h);
	}

	@Override
	public void repaint(Rectangle rect) {
		delegate.repaint(rect);
	}

	@Override
	public void requestFocus() {
		delegate.requestFocus();
	}

	@Override
	public void revalidate() {
		delegate.revalidate();
	}

	@Override
	public void setBackgroundColor(Color c) {
		delegate.setBackgroundColor(c);
	}

	@Override
	public void setBorder(Border b) {
		delegate.setBorder(b);
	}

	@Override
	public void setBounds(Rectangle rect) {
		delegate.setBounds(rect);
	}

	@Override
	public void setClippingStrategy(IClippingStrategy clippingStrategy) {
		delegate.setClippingStrategy(clippingStrategy);
	}

	@Override
	public void setConstraint(IFigure child, Object constraint) {
		delegate.setConstraint(child, constraint);
	}

	@Override
	public void setCursor(Cursor cursor) {
		delegate.setCursor(cursor);
	}

	@Override
	public void setEnabled(boolean value) {
		delegate.setEnabled(value);
	}

	@Override
	public void setFocusTraversable(boolean value) {
		delegate.setFocusTraversable(value);
	}

	@Override
	public void setFont(Font f) {
		delegate.setFont(f);
	}

	@Override
	public void setForegroundColor(Color c) {
		delegate.setForegroundColor(c);
	}

	@Override
	public void setLayoutManager(LayoutManager lm) {
		delegate.setLayoutManager(lm);
	}

	@Override
	public void setLocation(Point p) {
		delegate.setLocation(p);
	}

	@Override
	public void setMaximumSize(Dimension size) {
		delegate.setMaximumSize(size);
	}

	@Override
	public void setMinimumSize(Dimension size) {
		delegate.setMinimumSize(size);
	}

	@Override
	public void setOpaque(boolean isOpaque) {
		delegate.setOpaque(isOpaque);
	}

	@Override
	public void setParent(IFigure parent) {
		delegate.setParent(parent);
	}

	@Override
	public void setPreferredSize(Dimension size) {
		delegate.setPreferredSize(size);
	}

	@Override
	public void setRequestFocusEnabled(boolean requestFocusEnabled) {
		delegate.setRequestFocusEnabled(requestFocusEnabled);
	}

	@Override
	public void setSize(Dimension d) {
		delegate.setSize(d);
	}

	@Override
	public void setSize(int w, int h) {
		delegate.setSize(w, h);
	}

	@Override
	public void setToolTip(IFigure figure) {
		delegate.setToolTip(figure);
	}

	@Override
	public void setVisible(boolean visible) {
		delegate.setVisible(visible);
	}

	@Override
	public void translate(int x, int y) {
		delegate.translate(x, y);
	}

	@Override
	public void translateFromParent(Translatable t) {
		delegate.translateFromParent(t);
	}

	@Override
	public void translateToAbsolute(Translatable t) {
		delegate.translateToAbsolute(t);
	}

	@Override
	public void translateToParent(Translatable t) {
		delegate.translateToParent(t);
	}

	@Override
	public void translateToRelative(Translatable t) {
		delegate.translateToRelative(t);
	}

	@Override
	public void validate() {
		delegate.validate();
	}
}
