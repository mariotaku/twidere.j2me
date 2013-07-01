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
 * Creation date : 22 nov. 07
 * Copyright (c) Kalmeo 2007-2008. All rights reserved.
 * http://www.kalmeo.org
 */

package org.kalmeo.kuix.widget;

import org.kalmeo.kuix.core.KuixConstants;

/**
 * This class represents a radio button. <br>
 * <br>
 * <strong>For further informations, visit the <a
 * href="http://www.kalmeo.org/files/kuix/widgetdoc/index.html"
 * target="new">Kuix widgets reference page</a></strong>.
 * 
 * @author bbeaulant
 */
public class RadioButton extends CheckBox {
	
	// The associated string value
	private String value;

	/**
	 * Construct a {@link RadioButton}
	 */
	public RadioButton() {
		super(KuixConstants.RADIO_BUTTON_WIDGET_TAG);
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.CheckBox#setAttribute(java.lang.String, java.lang.String)
	 */
	public boolean setAttribute(String name, String value) {
		if (KuixConstants.VALUE_ATTRIBUTE.equals(name)) {
			setValue(value);
			return true;
		}
		return super.setAttribute(name, value);
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String name) {
		if (KuixConstants.VALUE_ATTRIBUTE.equals(name)) {
			return getValue();
		}
		return super.getAttribute(name);
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.CheckBox#setSelected(boolean)
	 */
	public void setSelected(boolean selected) {
		internalSetSelected(selected, true);
	}

	/**
	 * Used to intercept selection change and propagate the event to the ragio
	 * groupg according to <code>propagateToRadioGroup</code> value.
	 * 
	 * @param selected
	 * @param propagateToRadioGroup
	 */
	protected void internalSetSelected(boolean selected, boolean propagateToRadioGroup) {
		if (propagateToRadioGroup) {
			RadioGroup group = getRadioGroup();
			if (group != null) {
				group.setSelectedRadioButton(this, true);
			} else {
				super.setSelected(selected);
			}
		} else {
			super.setSelected(selected);
		}
	}
	
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
		RadioGroup radioGroup = getRadioGroup();
		if (radioGroup != null) {
			radioGroup.processNewRadioButtonValue(this);
		}
	}

	/**
	 * @return the group
	 */
	public RadioGroup getRadioGroup() {
		if (parent instanceof RadioGroup) {
			return (RadioGroup) parent;
		}
		return null;
	}
	
}

