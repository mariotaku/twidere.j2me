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

package org.kalmeo.kuix.widget;

import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.core.KuixConstants;
import org.kalmeo.kuix.core.focus.FocusManager;
import org.kalmeo.util.BooleanUtil;

/**
 * This class is base for all focusable widgets. <br>
 * <br>
 * <strong>For further informations, visit the <a
 * href="http://www.kalmeo.org/files/kuix/widgetdoc/index.html"
 * target="new">Kuix widgets reference page</a></strong>.
 * 
 * @author bbeaulant
 */
public class FocusableWidget extends Widget {

	// Widget's pseudo class list
	public static final String HOVER_PSEUDO_CLASS = "hover";
	public static final String DISABLED_PSEUDO_CLASS = "disabled";
	protected static final String[] PSEUDO_CLASSES = new String[] { HOVER_PSEUDO_CLASS, DISABLED_PSEUDO_CLASS };

	// Focusable ?
	protected boolean focusable = true;
	
	// Button focus state
	protected boolean focused = false;
	
	// The focus methods
	private String onFocus;
	private String onLostFocus;
	
	// Define the action widget state
	protected boolean enabled = true;
	
	// Internal properties
	private boolean requestFocusOnAdded = false;
	
	/**
	 * Construct a {@link FocusableWidget}
	 */
	public FocusableWidget() {
		super();
	}
	
	/**
	 * Construct a {@link FocusableWidget}
	 *
	 * @param tag
	 */
	public FocusableWidget(String tag) {
		super(tag);
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#setAttribute(java.lang.String, java.lang.String)
	 */
	public boolean setAttribute(String name, String value) {
		if (KuixConstants.ON_FOCUS_ATTRIBUTE.equals(name)) {
			setOnFocus(value);
			return true;
		}
		if (KuixConstants.ON_LOST_FOCUS_ATTRIBUTE.equals(name)) {
			setOnLostFocus(value);
			return true;
		}
		if (KuixConstants.ENABLED_ATTRIBUTE.equals(name)) {
			setEnabled(BooleanUtil.parseBoolean(value));
			return true;
		}
		if (KuixConstants.FOCUSABLE_ATTRIBUTE.equals(name)) {
			setFocusable(BooleanUtil.parseBoolean(value));
			return true;
		}
		if (KuixConstants.FOCUSED.equals(name)) {
			if (BooleanUtil.parseBoolean(value)) {
				requestFocus();
			}
			return true;
		}
		return super.setAttribute(name, value);
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String name) {
		if (KuixConstants.FOCUSED.equals(name)) {
			return BooleanUtil.toString(isFocused());
		}
		return super.getAttribute(name);
	}

	/**
	 * @param focusable the focusable to set
	 */
	public void setFocusable(boolean focusable) {
		this.focusable = focusable;
		if (!focusable) {
			giveFocusToNearestWidget();
		}
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#isFocusable()
	 */
	public boolean isFocusable() {
		return enabled && focusable;
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#isFocused()
	 */
	public boolean isFocused() {
		return focused;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		invalidateStylePropertiesCache(true);
		if (!enabled) {
			giveFocusToNearestWidget();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		if (!visible) {
			giveFocusToNearestWidget();
		}
		super.setVisible(visible);
	}

	/**
	 * @return the onFocus
	 */
	public String getOnFocus() {
		return onFocus;
	}

	/**
	 * @param onFocus the onFocus to set
	 */
	public void setOnFocus(String onFocus) {
		this.onFocus = onFocus;
	}

	/**
	 * @return the onLostFocus
	 */
	public String getOnLostFocus() {
		return onLostFocus;
	}

	/**
	 * @param onLostFocus the onLostFocus to set
	 */
	public void setOnLostFocus(String onLostFocus) {
		this.onLostFocus = onLostFocus;
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getPseudoClass()
	 */
	public String[] getAvailablePseudoClasses() {
		return PSEUDO_CLASSES;
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#isPseudoClassCompatible(java.lang.String)
	 */
	public boolean isPseudoClassCompatible(String pseudoClass) {
		if (HOVER_PSEUDO_CLASS.equals(pseudoClass)) {
			return isFocused();
		}
		if (DISABLED_PSEUDO_CLASS.equals(pseudoClass)) {
			return !isEnabled();
		}
		return false;
	}
	
	/**
	 * Give the focus to the nearest focusable widget.
	 */
	public void giveFocusToNearestWidget() {
		if (isFocused()) {
			FocusManager focusManager = getFocusManager();
			if (focusManager != null) {
				focusManager.requestOtherFocus(true, null);				// Request forward focus
				if (focusManager.getFocusedWidget() == this) {			// No next focus ?
					focusManager.requestOtherFocus(false, null);		// Request backward focus
					if (focusManager.getFocusedWidget() == this) {		// No previous focus ?
						focusManager.requestFocus(null);				// No focus
					}
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#remove()
	 */
	public void remove() {
		giveFocusToNearestWidget();
		super.remove();
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#requestFocus()
	 */
	public void requestFocus() {
		if (isFocusable()) {
			FocusManager focusManager = getFocusManager();
			if (focusManager != null) {
				ScrollPane scrollContainer = focusManager.findFirstScrollPaneParent(this);
				if (scrollContainer != null) {
					scrollContainer.bestScrollToChild(this, false);
				}
				focusManager.requestFocus(this);
			} else {
				requestFocusOnAdded = true;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#processEvent(org.kalmeo.kuix.core.event.KuixEvent)
	 */
	public boolean processFocusEvent(byte type) {
		switch (type) {
			case KuixConstants.FOCUS_GAINED_EVENT_TYPE: {
				focused = true;
				invalidateStylePropertiesCache(true);
				if (onFocus != null) {
					Kuix.callActionMethod(Kuix.parseMethod(onFocus, this));
				}
				propagateFocusEvent(this, false);
				return true;
			}
			case KuixConstants.FOCUS_LOST_EVENT_TYPE: {
				focused = false;
				invalidateStylePropertiesCache(true);
				if (onLostFocus != null) {
					Kuix.callActionMethod(Kuix.parseMethod(onLostFocus, this));
				}
				propagateFocusEvent(this, true);
				return true;
			}
		}
		return super.processFocusEvent(type);
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#processPointerEvent(byte, int, int)
	 */
	public boolean processPointerEvent(byte type, int x, int y) {
		if (isFocusable() && type == KuixConstants.POINTER_RELEASED_EVENT_TYPE) {
			requestFocus();
			return true;
		}
		return super.processPointerEvent(type, x, y);
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#onAdded(org.kalmeo.kuix.widget.Widget)
	 */
	protected void onAdded(Widget parent) {
		if (requestFocusOnAdded) {
			requestFocus();
			requestFocusOnAdded = false;
		}
	}
	
}
