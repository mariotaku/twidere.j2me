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
 * Creation date : 8 dev. 2007
 * Copyright (c) Kalmeo 2007-2008. All rights reserved.
 * http://www.kalmeo.org
 */

package org.kalmeo.kuix.widget;

import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.core.KuixConstants;
import org.kalmeo.kuix.core.focus.FocusManager;
import org.kalmeo.kuix.layout.BorderLayout;
import org.kalmeo.kuix.layout.BorderLayoutData;
import org.kalmeo.kuix.layout.Layout;
import org.kalmeo.kuix.layout.LayoutData;
import org.kalmeo.kuix.layout.StaticLayout;
import org.kalmeo.kuix.widget.TabItem.TabItemButton;

/**
 * This class represents a tab folder. <br>
 * <br>
 * <strong>For further informations, visit the <a
 * href="http://www.kalmeo.org/files/kuix/widgetdoc/index.html"
 * target="new">Kuix widgets reference page</a></strong>.
 * 
 * @author bbeaulant
 */
public class TabFolder extends List {

	// The default widget visible if there's no valid tabs
	private TabItem defaultTabItem;
	
	// Tab navigation
	private int backwardTabKey = KuixConstants.KUIX_KEY_LEFT;
	private int forwardTabKey = KuixConstants.KUIX_KEY_RIGHT;
	
	// Internal widgets
	private final ScrollPane buttonsContainer;
	private final Widget container;
	private TabItem currentTabItem;
	
	/**
	 * Construct a {@link TabFolder}
	 */
	public TabFolder() {
		super(KuixConstants.TAB_FOLDER_WIDGET_TAG);
		
		buttonsContainer = new ScrollPane(KuixConstants.TAB_FOLDER_BUTTONS_CONTAINER_WIDGET_TAG, false) {

			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.Widget#getDefaultStylePropertyValue(java.lang.String)
			 */
			protected Object getDefaultStylePropertyValue(String name) {
				if (KuixConstants.LAYOUT_DATA_STYLE_PROPERTY.equals(name)) {
					return BorderLayoutData.instanceNorth;
				}
				return super.getDefaultStylePropertyValue(name);
			}

		};
		buttonsContainer.setHorizontal(true);
		buttonsContainer.setShowScrollBar(false);
		super.add(buttonsContainer);
		
		container = new Widget(KuixConstants.TAB_FOLDER_CONTAINER_WIDGET_TAG) {

			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.Widget#getLayout()
			 */
			public Layout getLayout() {
				return StaticLayout.instance;
			}

			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.Widget#getLayoutData()
			 */
			public LayoutData getLayoutData() {
				return BorderLayoutData.instanceCenter;
			}
			
			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.Widget#onChildRemoved(org.kalmeo.kuix.widget.Widget)
			 */
			protected void onChildRemoved(Widget widget) {
				if (widget == currentTabItem) {
					selectOtherTab(true, true);
				}
			}
			
		};
		super.add(container);
		
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.List#setAttribute(java.lang.String, java.lang.String)
	 */
	public boolean setAttribute(String name, String value) {
		if (KuixConstants.BACKWARD_TAB_KEY_ATTRIBUTE.equals(name)) {
			setBackwardTabKey(Kuix.getConverter().convertKuixKeyCode(value));
			return true;
		}
		if (KuixConstants.FORWARD_TAB_KEY_ATTRIBUTE.equals(name)) {
			setForwardTabKey(Kuix.getConverter().convertKuixKeyCode(value));
			return true;
		}
		return super.setAttribute(name, value);
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String name) {
		if (KuixConstants.CURRENT_TABITEM_ATTRIBUTE.equals(name)) {
			return getCurrentTabItem();
		}
		return super.getAttribute(name);
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getInternalChildInstance(java.lang.String)
	 */
	public Widget getInternalChildInstance(String tag) {
		if (KuixConstants.TAB_FOLDER_DEFAULT_TAB_ITEM_WIDGET_TAG.equals(tag)) {
			return getDefaultTabItem();
		}
		if (KuixConstants.TAB_FOLDER_BUTTONS_CONTAINER_WIDGET_TAG.equals(tag)) {
			return buttonsContainer;
		}
		if (KuixConstants.TAB_FOLDER_CONTAINER_WIDGET_TAG.equals(tag)) {
			return container;
		}
		return super.getInternalChildInstance(tag);
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#isFocusWidgetChild()
	 */
	public boolean isFocusWidgetChild() {
		return false;	// Special case for TabFolder focus stop recursion
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getLayout()
	 */
	public Layout getLayout() {
		return BorderLayout.instance;
	}

	/**
	 * Returns the defaultTabItem. The default tab item is the {@link TabItem}
	 * used if no other tab are enabled.
	 * 
	 * @return the defaultTabItem
	 */
	public Widget getDefaultTabItem() {
		if (defaultTabItem == null) {
			defaultTabItem = new TabItem();
			initDefaultTabItem();
		}
		return defaultTabItem;
	}
	
	/**
	 * Returns the backwardTabKey. The backward tab key represents the
	 * KuixKeyCode used as shortcut to switch to the backward tab.
	 * 
	 * @return the previousTabKey
	 */
	public int getBackwardTabKey() {
		return backwardTabKey;
	}

	/**
	 * Set the backwardTabKey. The backward tab key represents the KuixKeyCode
	 * used as shortcut to switch to the backward tab.
	 * 
	 * @param backwardTabKey to set
	 */
	public void setBackwardTabKey(int backwardTabKey) {
		this.backwardTabKey = backwardTabKey;
	}

	/**
	 * Returns the forwardTabKey. The forward tab key represents the
	 * KuixKeyCode used as shortcut to switch to the forward tab.
	 * 
	 * @return the nextTabKey
	 */
	public int getForwardTabKey() {
		return forwardTabKey;
	}

	/**
	 * Set the forwardTabKey. The forward tab key represents the KuixKeyCode
	 * used as shortcut to switch to the forward tab.
	 * 
	 * @param forwardTabKey to set
	 */
	public void setForwardTabKey(int forwardTabKey) {
		this.forwardTabKey = forwardTabKey;
	}

	/**
	 * Returns the buttonsContainer. The buttonsContainer is the widget that
	 * holds tabItems buttons ({@link TabItemButton}).
	 * 
	 * @return the buttonsContainer
	 */
	public ScrollPane getButtonsContainer() {
		return buttonsContainer;
	}

	/**
	 * Returns the container. The container is the widget that holds tabItems.
	 * 
	 * @return the container
	 */
	public Widget getContainer() {
		return container;
	}

	/**
	 * @return the currentTabItem
	 */
	public TabItem getCurrentTabItem() {
		return currentTabItem;
	}
	
	/**
	 * Set the current {@link TabItem} (only if this instance is a child of the
	 * {@link TabFolder})
	 * 
	 * @param tabItem
	 */
	public void setCurrentTabItem(TabItem tabItem) {
		if (tabItem != null && tabItem.parent != container) {
			return;
		}
		if (currentTabItem != null) {
			currentTabItem.internalSetSelected(false, false);
			currentTabItem.setVisible(false);
		}
		currentTabItem = tabItem;
		if (tabItem != null) {
			tabItem.internalSetSelected(true, false);
			tabItem.setVisible(true);
			buttonsContainer.bestScrollToChild(tabItem.getButton(), false);
		}
		if (defaultTabItem != null) {
			defaultTabItem.setVisible(currentTabItem == null);
			buttonsContainer.setVisible(currentTabItem != null);
		}
	}
	
	/**
	 * Initialize the defaultTabItem
	 */
	private void initDefaultTabItem() {
		if (defaultTabItem != null) {
			container.add(defaultTabItem);
			defaultTabItem.setVisible(currentTabItem == null);
			buttonsContainer.setVisible(currentTabItem != null);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#add(org.kalmeo.kuix.widget.Widget)
	 */
	public Widget add(Widget widget) {
		if (widget instanceof TabItem) {
			addTabItem((TabItem) widget);
		}
		return this;
	}

	/**
	 * Add a new {@link TabItem}
	 * 
	 * @param tabItem
	 * @return This {@link TabFolder}
	 */
	public void addTabItem(final TabItem tabItem) {
		if (tabItem != null && tabItem.parent != container) {
			
			// Add the tabButton to the buttonsContainer
			buttonsContainer.add(tabItem.getButton());
			
			// Add tabItem
			container.add(tabItem);
			if ((currentTabItem == null || tabItem.isSelected()) && tabItem.isEnabled()) {
				setCurrentTabItem(tabItem);
			} else {
				tabItem.setVisible(false);
			}
			
		}
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.List#removeAllItems()
	 */
	public void removeAllItems() {
		if (defaultTabItem != null) {
			defaultTabItem.remove();		// Remove the default tab item to keep the data binding it will be restaured later by the removeAll method
		}
		super.removeAllItems();
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#removeAll()
	 */
	public void removeAll() {
		container.removeAll();
		initDefaultTabItem();				// Restaure the defaultTabItem
		setCurrentTabItem(null);			// No tabItem : setCurrent to null
	}
	
	/**
	 * Select an other enabled tab.
	 * 
	 * @param forward
	 * @param unselectIfNoOther
	 */
	protected void selectOtherTab(boolean forward, boolean unselectIfNoOther) {
		Widget currentTabButton = currentTabItem != null ? currentTabItem.getButton() : (forward ? buttonsContainer.getContainer().getChild() : buttonsContainer.getContainer().getLastChild());
		Widget tabButton = currentTabButton;
		while (tabButton != null) {
			tabButton = forward ? tabButton.next : tabButton.previous;
			if (tabButton == null) {
				tabButton = (forward ? buttonsContainer.getContainer().getChild() : buttonsContainer.getContainer().getLastChild());
			}
			if (tabButton != null) {
				if (tabButton == currentTabButton) {
					break;
				}
				if (((CheckBox) tabButton).isEnabled()) {
					tabButton.processActionEvent();
					return;
				}
			}
		}
		if (unselectIfNoOther) {
			setCurrentTabItem(null);
		}
	}

	/**
	 * Select the previous enabled tab.
	 */
	public void selectPreviousTab() {
		selectOtherTab(false, false);
	}
	
	/**
	 * Select the next enabled tab.
	 */
	public void selectNextTab() {
		selectOtherTab(true, false);
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#processKeyEvent(byte, int)
	 */
	public boolean processKeyEvent(byte type, int kuixKeyCode) {
		if (currentTabItem != null) {
			
			// Tab navigation
			if (type == KuixConstants.KEY_PRESSED_EVENT_TYPE	
					|| type == KuixConstants.KEY_REPEATED_EVENT_TYPE) {
				if (kuixKeyCode == backwardTabKey) {
					selectPreviousTab();
					return true;
				}
				if (kuixKeyCode == forwardTabKey) {
					selectNextTab();
					return true;
				}
			}
		
			// Default key process
			return currentTabItem.getFocusManager().processKeyEvent(type, kuixKeyCode);
			
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#propagateFocusEvent(org.kalmeo.kuix.widget.Widget, boolean)
	 */
	protected void propagateFocusEvent(Widget focusedWidget, boolean lost) {
		if (lost) {
			onLostFocus(focusedWidget);
		} else {
			onFocus(focusedWidget);
		}
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#onAdded(org.kalmeo.kuix.widget.Widget)
	 */
	protected void onAdded(Widget parent) {
		FocusManager focusManager = getFocusManager();
		if (focusManager != null) {
			// By default the TabFolder catch the focus if its parent has a focusManager
			focusManager.requestFocus(this);
		}
	}

}