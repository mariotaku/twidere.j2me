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
import org.kalmeo.kuix.layout.GridLayout;
import org.kalmeo.kuix.layout.Layout;
import org.kalmeo.kuix.layout.LayoutData;
import org.kalmeo.kuix.layout.StaticLayoutData;
import org.kalmeo.util.BooleanUtil;

/**
 * This class represents a tab item. <br>
 * <br>
 * <strong>For further informations, visit the <a
 * href="http://www.kalmeo.org/files/kuix/widgetdoc/index.html"
 * target="new">Kuix widgets reference page</a></strong>.
 * 
 * @author bbeaulant
 */
public class TabItem extends Widget {

	/**
	 * This class represents the tabItem button.
	 */
	public class TabItemButton extends CheckBox {
		
		private Text labelWidget;
		private Picture iconWidget;
		
		/**
		 * Construct a {@link TabItemButton}
		 */
		public TabItemButton() {
			super(KuixConstants.TAB_ITEM_BUTTON_WIDGET_TAG);
		}

		/* (non-Javadoc)
		 * @see org.kalmeo.kuix.widget.AbstractActionWidget#processActionEvent()
		 */
		public boolean processActionEvent() {
			if (tabFolder != null) {
				tabFolder.setCurrentTabItem(TabItem.this);
			}
			return true;
		}

		/* (non-Javadoc)
		 * @see org.kalmeo.kuix.widget.AbstractFocusableWidget#isFocusable()
		 */
		public boolean isFocusable() {
			return false;
		}
		
		/**
		 * Set the label of this tab button.
		 * 
		 * @param label
		 */
		public void setLabel(String label) {
			if (labelWidget == null) {
				labelWidget = new Text();
				this.add(labelWidget);
			} else if (label == null) {
				labelWidget.remove();
				labelWidget = null;
				return;
			}
			labelWidget.setText(label);
		}
		
		/**
		 * Set the icon of this tab button.
		 * 
		 * @param icon
		 */
		public void setIcon(String icon) {
			if (iconWidget == null) {
				iconWidget = new Picture();
				this.add(iconWidget);
			} else if (icon == null) {
				iconWidget.remove();
				iconWidget = null;
				return;
			}
			iconWidget.setSource(icon);
		}
		
	}
	
	// Defaults
	private static final Layout TAB_ITEM_DEFAULT_LAYOUT = GridLayout.instanceOneByOne;

	// Tab item properties
	private String label;
	private String icon;
	
	// The associated tabFolder
	private TabFolder tabFolder;
	
	// The associated TabButton
	private final TabItemButton button;
	
	// FocusManager
	private final FocusManager focusManager;
	
	// Selection actions
	private String onSelect;
	private String onUnselect;

	/**
	 * Construct a {@link TabItem}
	 */
	public TabItem() {
		super(KuixConstants.TAB_ITEM_WIDGET_TAG);
		
		// Create the tabItem button
		button = new TabItemButton();
		
		// Init focusManagers
		focusManager = new FocusManager(this, false);
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#setAttribute(java.lang.String, java.lang.String)
	 */
	public boolean setAttribute(String name, String value) {
		if (KuixConstants.LABEL_ATTRIBUTE.equals(name)) {
			setLabel(value);
			return true;
		}
		if (KuixConstants.ICON_ATTRIBUTE.equals(name)) {
			setIcon(value);
			return true;
		}
		if (KuixConstants.ENABLED_ATTRIBUTE.equals(name)) {
			setEnabled(BooleanUtil.parseBoolean(value));
			return true;
		}
		if (KuixConstants.SELECTED_ATTRIBUTE.equals(name)) {
			setSelected(BooleanUtil.parseBoolean(value));
			return true;
		}
		if (KuixConstants.ON_SELECT_ATTRIBUTE.equals(name)) {
			setOnSelect(value);
			return true;
		}
		if (KuixConstants.ON_UNSELECT_ATTRIBUTE.equals(name)) {
			setOnUnselect(value);
			return true;
		}
		return super.setAttribute(name, value);
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getInternalChildInstance(java.lang.String)
	 */
	public Widget getInternalChildInstance(String tag) {
		if (KuixConstants.TAB_ITEM_BUTTON_WIDGET_TAG.equals(tag)) {
			return getButton();
		}
		return super.getInternalChildInstance(tag);
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getFocusManager()
	 */
	public FocusManager getFocusManager() {
		return focusManager;
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getLayoutData()
	 */
	public LayoutData getLayoutData() {
		return StaticLayoutData.instanceFull;
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getDefaultStyleAttributeValue(java.lang.String)
	 */
	protected Object getDefaultStylePropertyValue(String name) {
		if (KuixConstants.LAYOUT_STYLE_PROPERTY.equals(name)) {
			return TAB_ITEM_DEFAULT_LAYOUT;
		}
		return super.getDefaultStylePropertyValue(name);
	}
	
	/**
	 * @return the tabItem button
	 */
	public TabItemButton getButton() {
		return button;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		if (button != null) {
			button.setLabel(label);
		}
		this.label = label;
	}

	/**
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}

	/**
	 * @param icon the icon to set
	 */
	public void setIcon(String icon) {
		if (button != null) {
			button.setIcon(icon);
		}
		this.icon = icon;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return button.isEnabled();
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		button.setEnabled(enabled);
		if (tabFolder != null) {
			if (enabled) {
				if (tabFolder.getCurrentTabItem() == null) {
					tabFolder.setCurrentTabItem(this);
				}
			} else if (tabFolder.getCurrentTabItem() == this) {
				tabFolder.selectNextTab();
				if (tabFolder.getCurrentTabItem() == this) {
					tabFolder.setCurrentTabItem(null);
				}
			}
		}
	}
	
	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return button.isSelected();
	}

	/**
	 * @param selected the selected to set
	 */
	public void setSelected(boolean selected) {
		if (selected != isSelected()) {
			internalSetSelected(selected, true);
		}
	}

	/**
	 * @param selected the selected to set
	 * @param propagateToTabFolder
	 */
	protected void internalSetSelected(boolean selected, boolean propagateToTabFolder) {
		if (propagateToTabFolder && tabFolder != null) {
			if (selected) {
				tabFolder.setCurrentTabItem(this);
			} else {
				tabFolder.selectOtherTab(true, true);
			}
		}
		if (selected != isSelected()) {
			button.setSelected(selected);
			if (selected && onSelect != null) {
				Kuix.callActionMethod(Kuix.parseMethod(onSelect, this));
			} else if (!selected && onUnselect != null) {
				Kuix.callActionMethod(Kuix.parseMethod(onUnselect, this));
			}
		}
	}
	
	/**
	 * The onSelect to set
	 */
	public void setOnSelect(String onSelect) {
		this.onSelect = onSelect;
	}

	/**
	 * The onUnselect to set
	 */
	public void setOnUnselect(String onUnselect) {
		this.onUnselect = onUnselect;
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#doLayout()
	 */
	protected void doLayout() {
		super.doLayout();
		
		// Check if current focused widget is visible (special for tabitem because it has its own focusManager)
		Widget focusedWidget = focusManager.getFocusedWidget();
		if (focusedWidget == null || focusedWidget != null && !focusedWidget.isVisible()) {
			focusManager.requestFirstFocus();
		}
		
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#onAdded(org.kalmeo.kuix.widget.Widget)
	 */
	protected void onAdded(Widget parent) {
		if (parent.parent instanceof TabFolder) {
			tabFolder = (TabFolder) parent.parent;
		}
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#onRemoved(org.kalmeo.kuix.widget.Widget)
	 */
	protected void onRemoved(Widget parent) {
		if (button != null) {
			button.remove();
		}
		tabFolder = null;
	}

}
