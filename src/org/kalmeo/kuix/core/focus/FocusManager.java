/*
 * This file is part of org.kalmeo.kuix.
 * 
 * org.kalmeo.kuix is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * org.kalmeo.kuix is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with org.kalmeo.kuix.  If not, see <http://www.gnu.org/licenses/>.
 *  
 * Creation date : 21 nov. 2007
 * Copyright (c) Kalmeo 2007-2008. All rights reserved.
 * http://www.kalmeo.org
 */

package org.kalmeo.kuix.core.focus;

import java.util.Vector;

import org.kalmeo.kuix.core.KuixConstants;
import org.kalmeo.kuix.util.Alignment;
import org.kalmeo.kuix.widget.Menu;
import org.kalmeo.kuix.widget.Screen;
import org.kalmeo.kuix.widget.ScrollPane;
import org.kalmeo.kuix.widget.TabFolder;
import org.kalmeo.kuix.widget.TabItem;
import org.kalmeo.kuix.widget.Widget;

/**
 * @author bbeaulant
 */
public class FocusManager {

	// The widget that this FocusManager is reltative to
	protected Widget rootWidget;
	
	// Define if the selection can loop
	private boolean loop;
	
	// The current focused widget
	protected Widget focusedWidget;
	
	// Key shortcut handler list
	private Vector shortcuts;
	
	// Represent the widget where the drag event starts
	private Widget draggedEventWidget = null;
	
	/**
	 * Construct a {@link FocusManager}
	 *
	 * @param rootWidget
	 * @param loop
	 */
	public FocusManager(Widget rootWidget, boolean loop) {
		this.rootWidget = rootWidget;
		setLoop(loop);
	}
	
	/**
	 * @return the loop
	 */
	public boolean isLoop() {
		return loop;
	}

	/**
	 * @param loop the loop to set
	 */
	public void setLoop(boolean loop) {
		this.loop = loop;
	}

	/**
	 * Returns the focused widget.
	 * 
	 * @return the focusedWidget
	 */
	public Widget getFocusedWidget() {
		return focusedWidget;
	}
	
	/**
	 * Returns the focused widget. <br/><b>Caution</b> : if focused widget is a
	 * {@link TabFolder} the current {@link TabItem}'s focused widget is returned.<br/>
	 * For focusManager internal use, prefer to use the protected <code>focusedWidget</code> member.
	 * 
	 * @return the focusedWidget
	 */
	public Widget getVirtualFocusedWidget() {
		if (focusedWidget instanceof TabFolder) {
			TabItem tabItem = ((TabFolder) focusedWidget).getCurrentTabItem();
			if (tabItem != null) {
				return tabItem.getFocusManager().getFocusedWidget();
			}
		}
		return getFocusedWidget();
	}
	
	/**
	 * Reset the focues widget
	 */
	public void reset() {
		requestFocus(null);
	}
	
	/**
	 * Add a shortcut key event handler to shortcuts list
	 * 
	 * @param widget the widget that handle the shortcut key event
	 */
	public void addShortcutHandler(Widget widget) {
		if (widget != null) {
			if (shortcuts == null) {
				shortcuts = new Vector();
			}
			if (!shortcuts.contains(widget)) {
				shortcuts.addElement(widget);
			}
		}
	}
	
	/**
	 * Remove a shortcut key event handler to shortcuts list
	 * 
	 * @param widget the widget that handle the shortcut key event
	 */
	public void removeShortcutHandler(Widget widget) {
		if (widget != null && shortcuts != null) {
			if (shortcuts.contains(widget)) {
				shortcuts.removeElement(widget);
			}
		}
	}
	
	/**
	 * Try to retrieve the direct or indirect parent scrollPane of the given
	 * <code>widget</code> instance.
	 * 
	 * @param widget
	 * @return The direct or indirect parent {@link ScrollPane} if it exists or
	 *         <code>null</code>
	 */
	public ScrollPane findFirstScrollPaneParent(Widget widget) {
		for (Widget container = widget.parent; container != null; container = container.parent) {
			if (container instanceof ScrollPane) {
				return ((ScrollPane) container);
			}
		}
		return null;
	}

	/**
	 * Request the <code>widget</code> focus
	 * 
	 * @param widget
	 */
	public void requestFocus(Widget widget) {
		if (focusedWidget != widget) {
			Widget previous = focusedWidget;
			focusedWidget = widget;
			if (previous != null) {
				previous.processFocusEvent(KuixConstants.FOCUS_LOST_EVENT_TYPE);
			}
			if (focusedWidget != null) {
				focusedWidget.processFocusEvent(KuixConstants.FOCUS_GAINED_EVENT_TYPE);
			}
		}
	}
	
	/**
	 * Request the previous/next focusable {@link Widget}. The previous/next
	 * focusable widget search start from <code>startWidget</code>.<br>
	 * The <code>direction</code> helps to catch the best previous focusable
	 * widget. Set <code>direction</code> to <code>null</code> to use only
	 * widget tree navigation.
	 * 
	 * @param startWidget
	 * @param forward
	 * @param direction
	 * @param loopCount
	 */
	private void requestOtherFocus(Widget startWidget, boolean forward, Alignment direction, int loopCount) {
		if (loopCount > 1) {
			return;
		}
		Widget otherFocus = ((startWidget == null) ? rootWidget : startWidget).getOtherFocus(rootWidget, startWidget, null, forward, direction, true, true, true);
		if (otherFocus != null) {
			ScrollPane scrollPane = findFirstScrollPaneParent(otherFocus);
			if (scrollPane != null) {
				if (!scrollPane.bestScrollToChild(otherFocus, startWidget != null)) {
					return;
				}
				if (scrollPane.isMarkerWidget(otherFocus)) {
					Widget nextOtherFocus = otherFocus.getOtherFocus(rootWidget, otherFocus, null, forward, direction, false, true, true);
					if (nextOtherFocus == null) {
						return;
					}
					if (findFirstScrollPaneParent(nextOtherFocus) != scrollPane || scrollPane.isChildInsideClippedArea(nextOtherFocus) && !scrollPane.isMarkerWidget(nextOtherFocus)) {
						otherFocus = nextOtherFocus;
					}
				}
			}
			requestFocus(otherFocus);
		} else if (!loop && focusedWidget != null) {
			ScrollPane scrollPane = findFirstScrollPaneParent(focusedWidget);
			if (scrollPane != null) {
				scrollPane.bestScrollToChild(focusedWidget, true);
			}
		} else if (loop) {
			requestFocus(null);
			requestOtherFocus(null, forward, direction, ++loopCount);
		}
	}
	
	/**
	 * Request the forward or backward focusable {@link Widget}. The forward or
	 * backward focusable widget search start from <code>widget</code>.<br>
	 * The <code>direction</code> helps to catch the best previous focusable
	 * widget. Set <code>direction</code> to <code>null</code> to use only
	 * widget tree navigation.
	 * 
	 * @param startWidget
	 * @param direction
	 */
	public void requestOtherFocus(Widget startWidget, boolean forward, Alignment direction) {
		requestOtherFocus(startWidget, forward, direction, 0);
	}

	/**
	 * Request the forward or backward focusable {@link Widget}.<br>
	 * The <code>direction</code> helps to catch the best previous focusable
	 * widget. Set <code>direction</code> to <code>null</code> to use only
	 * widget tree navigation.
	 * 
	 * @param direction
	 */
	public void requestOtherFocus(boolean forward, Alignment direction) {
		requestOtherFocus(focusedWidget, forward, direction);
	}

	/**
	 * Request focus for the first focusable widget
	 */
	public void requestFirstFocus() {
		requestFocus(null);
		requestOtherFocus(true, null);
	}
	
	/**
	 * Request focus for the last focusable widget
	 */
	public void requestLastFocus() {
		requestFocus(null);
		requestOtherFocus(false, null);
	}
	
	/**
	 * Process a key event
	 * 
	 * @param type
	 * @param kuixKeyCode
	 * @return <code>true</code> if the event do something, else <code>false</code>
	 */
	public boolean processKeyEvent(byte type, int kuixKeyCode) {
		
		// Check focusedWidget event process
		if (focusedWidget != null) {
			if (focusedWidget.processKeyEvent(type, kuixKeyCode)) {
				return true;
			}
		}
		
		// Check shortcuts
		boolean processDefault = true;
		if (shortcuts != null && !shortcuts.isEmpty()) {
			Widget widget = null;
			for (int i = shortcuts.size() - 1; i >= 0; --i) {
				widget = (Widget) shortcuts.elementAt(i);

				// If the widget has a Released or Repeated shortcut associated with the current kuixKeyCode default processing will be passed
				if (processDefault 
					&& type == KuixConstants.KEY_PRESSED_EVENT_TYPE
					&& (widget.isShortcutKeyCodeCompatible(kuixKeyCode, KuixConstants.KEY_RELEASED_EVENT_TYPE)
							|| widget.isShortcutKeyCodeCompatible(kuixKeyCode, KuixConstants.KEY_REPEATED_EVENT_TYPE))) {
						processDefault = false;
				}
				
				if (widget != null && widget.isVisible() && widget.isInWidgetTree() && widget.hasShortcutKeyCodes(type)) {
					if (widget.isShortcutKeyCodeCompatible(kuixKeyCode, type)) {
						if (widget.processShortcutKeyEvent(type, kuixKeyCode)) {
							return true;
						} else {
							break;
						}
					}
				}
			}
		}
		
		if (!processDefault) {
			return true;
		}
		
		switch (type) {
			
			case KuixConstants.KEY_PRESSED_EVENT_TYPE:	
			case KuixConstants.KEY_REPEATED_EVENT_TYPE: {
				
				switch (kuixKeyCode) {
					
					case KuixConstants.KUIX_KEY_UP:
						requestOtherFocus(false, Alignment.TOP);
						return true;
						
					case KuixConstants.KUIX_KEY_LEFT:
						requestOtherFocus(false, Alignment.LEFT);
						return true;
	
					case KuixConstants.KUIX_KEY_DOWN:
						requestOtherFocus(true, Alignment.BOTTOM);
						return true;
						
					case KuixConstants.KUIX_KEY_RIGHT:
						requestOtherFocus(true, Alignment.RIGHT);
						return true;
						
				}
				break;
				
			}
		}
		return false;
	}
	
	/**
	 * Process key event if it's generated by soft key
	 * 
	 * @param type
	 * @param kuixKeyCode
	 * @return <code>true</code> if the event do something, else <code>false</code>
	 */
	protected boolean processSoftKeyEvent(byte type, int kuixKeyCode) {
		// Default event process
		switch (type) {
			
			case KuixConstants.KEY_PRESSED_EVENT_TYPE:
			case KuixConstants.KEY_REPEATED_EVENT_TYPE: {
				
				if (kuixKeyCode == KuixConstants.KUIX_KEY_SOFT_LEFT || kuixKeyCode == KuixConstants.KUIX_KEY_SOFT_RIGHT) {
					Screen screen = rootWidget.getDesktop().getCurrentScreen();
					if (screen != null) {
						Menu menu = screen.getScreenMenu(kuixKeyCode);
						if (menu != null) {
							menu.processActionEvent();
						}
						return true;
					}
				}
				break;
				
			}

		}
		return false;	
		
	}
	
	/**
	 * Process a pointer event
	 * 
	 * @param type
	 * @param x
	 * @param y
	 * @return <code>true</code> if the event do something, else <code>false</code>
	 */
	public boolean processPointerEvent(byte type, int x, int y) {
		
		// Convert coordinates
		for (Widget widget = rootWidget.parent; widget != null; widget = widget.parent) {
			x -= widget.getX();
			y -= widget.getY();
		}
		
		if (type == KuixConstants.POINTER_DRAGGED_EVENT_TYPE) {
			if (draggedEventWidget == null) {
				draggedEventWidget = rootWidget.getWidgetAt(x, y);
			}
			if (draggedEventWidget != null) {
				return draggedEventWidget.processPointerEvent(type, x, y);
			}
			return false;
		}
		
		draggedEventWidget = null;
		
		// Find targeted widget
		Widget targetedWidget = rootWidget.getWidgetAt(x, y);
		if (targetedWidget != null) {
			return targetedWidget.processPointerEvent(type, x, y);
		}
		return false;
		
	}
	
}
