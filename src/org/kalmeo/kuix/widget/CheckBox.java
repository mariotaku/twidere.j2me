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
import org.kalmeo.util.BooleanUtil;

/**
 * This class represents a check box. <br>
 * <br>
 * <strong>For further informations, visit the <a
 * href="http://www.kalmeo.org/files/kuix/widgetdoc/index.html"
 * target="new">Kuix widgets reference page</a></strong>.
 * 
 * @author bbeaulant
 */
public class CheckBox extends ActionWidget {

	// Widget's pseudo class list
	public static final String SELECTED_PSEUDO_CLASS = "selected";
	protected static final String[] PSEUDO_CLASSES = new String[] { HOVER_PSEUDO_CLASS, DISABLED_PSEUDO_CLASS, SELECTED_PSEUDO_CLASS };

	// The selection state
	private boolean selected = false;
	
	// Selection actions
	private String onSelect;
	private String onUnselect;

	/**
	 * Construct a {CheckBox}
	 */
	public CheckBox() {
		super(KuixConstants.CHECKBOX_WIDGET_TAG);
	}

	/**
	 * Construct a {CheckBox}
	 * 
	 * @param tag
	 */
	public CheckBox(String tag) {
		super(tag);
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.AbstractActionWidget#setAttribute(java.lang.String, java.lang.String)
	 */
	public boolean setAttribute(String name, String value) {
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
	 * @see org.kalmeo.kuix.widget.Widget#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String name) {
		if (KuixConstants.SELECTED_ATTRIBUTE.equals(name)) {
			return BooleanUtil.toString(isSelected());
		}
		return super.getAttribute(name);
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.AbstractFocusableWidget#isPseudoClassCompatible(java.lang.String)
	 */
	public boolean isPseudoClassCompatible(String pseudoClass) {
		if (SELECTED_PSEUDO_CLASS.equals(pseudoClass)) {
			return isSelected();
		}
		return super.isPseudoClassCompatible(pseudoClass);
	}

	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected the selected to set
	 */
	public void setSelected(boolean selected) {
		if (this.selected != selected) {
			this.selected = selected;
			invalidateStylePropertiesCache(true);
			if (selected && onSelect != null) {
				Kuix.callActionMethod(Kuix.parseMethod(onSelect, this));
			} else if (!selected && onUnselect != null) {
				Kuix.callActionMethod(Kuix.parseMethod(onUnselect, this));
			}
		}
	}
	
	/**
	 * @return the onSelect
	 */
	public String getOnSelect() {
		return onSelect;
	}

	/**
	 * The onSelect to set
	 */
	public void setOnSelect(String onSelect) {
		this.onSelect = onSelect;
	}

	/**
	 * @return the onUnselect
	 */
	public String getOnUnselect() {
		return onUnselect;
	}

	/**
	 * The onUnselect to set
	 */
	public void setOnUnselect(String onUnselect) {
		this.onUnselect = onUnselect;
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.AbstractActionWidget#processActionEvent()
	 */
	public boolean processActionEvent() {
		setSelected(!selected);
		super.processActionEvent();
		return true;
	}
	
}
