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

import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.core.KuixConstants;

/**
 * This class represents a radio buttons group. <br>
 * <br>
 * <strong>For further informations, visit the <a
 * href="http://www.kalmeo.org/files/kuix/widgetdoc/index.html"
 * target="new">Kuix widgets reference page</a></strong>.
 * 
 * @author bbeaulant
 */
public class RadioGroup extends List {

	// The selected RadioButton
	private RadioButton selectedRadioButton = null;
	
	// The change method
	private String onChange;
	
	// The value is kept in this variable only if no radio button corresponding
	private String wantedValue;
	
	/**
	 * Construct a {@link RadioGroup}
	 */
	public RadioGroup() {
		this(KuixConstants.RADIO_GROUP_WIDGET_TAG);
	}
	
	/**
	 * Construct a {@link RadioGroup}
	 *
	 * @param tag
	 */
	public RadioGroup(String tag) {
		super(tag);
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.CheckBox#setAttribute(java.lang.String, java.lang.String)
	 */
	public boolean setAttribute(String name, String value) {
		if (KuixConstants.VALUE_ATTRIBUTE.equals(name)) {
			setValue(value);
			return true;
		}
		if (KuixConstants.ON_CHANGE_ATTRIBUTE.equals(name)) {
			setOnChange(value);
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
		if (KuixConstants.SELECTED_RADIOBUTTON_ATTRIBUTE.equals(name)) {
			return getSelectedRadioButton();
		}
		return super.getAttribute(name);
	}

	/**
	 * @return the select radio button value
	 */
	public String getValue() {
		if (selectedRadioButton != null) {
			return selectedRadioButton.getValue();
		}
		return null;
	}
	
	/**
	 * Check if the value exists in RadioButton children value and the select
	 * the first occurrence
	 * 
	 * @param value
	 */
	public void setValue(String value) {
		if (value != null) {
			RadioButton radioButton = null;
			for (Widget widget = getChild(); widget != null; widget = widget.next) {
				if (widget instanceof RadioButton) {
					radioButton = (RadioButton) widget;
					if (value.equals(radioButton.getValue())) {
						setSelectedRadioButton(radioButton, false);
						return;
					}
				}
			}
		}
		setSelectedRadioButton(null, false);
		wantedValue = value;
	}
	
	/**
	 * @return the onChange
	 */
	public String getOnChange() {
		return onChange;
	}

	/**
	 * @param onChange the onChange to set
	 */
	public void setOnChange(String onChange) {
		this.onChange = onChange;
	}

	/**
	 * @return the selectedButton
	 */
	public RadioButton getSelectedRadioButton() {
		return selectedRadioButton;
	}

	/**
	 * Set the current selected {@link RadioButton}.
	 * 
	 * @param radioButton the selectedButton to set
	 * @param propagateChangeEvent if <code>true</code> the onChange event could be propagated
	 */
	public void setSelectedRadioButton(RadioButton radioButton, boolean propagateChangeEvent) {
		if (radioButton != null && radioButton.parent != this) {
			return;
		}
		wantedValue = null;
		if (selectedRadioButton != radioButton) {
			if (selectedRadioButton != null) {
				selectedRadioButton.internalSetSelected(false, false);
			}
			selectedRadioButton = radioButton;
			if (radioButton != null) {
				radioButton.internalSetSelected(true, false);
			}
			if (propagateChangeEvent && onChange != null) {
				Kuix.callActionMethod(Kuix.parseMethod(onChange, this));
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#add(org.kalmeo.kuix.widget.Widget)
	 */
	public Widget add(Widget widget) {
		super.add(widget);
		if (widget instanceof RadioButton) {
			processNewRadioButtonValue((RadioButton) widget);
		}
		return this;
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#cleanUp()
	 */
	public void cleanUp() {
		selectedRadioButton = null;
		wantedValue = null;
		super.cleanUp();
	}
	
	/**
	 * @param radioButton
	 */
	protected void processNewRadioButtonValue(RadioButton radioButton) {
		if (radioButton != null && radioButton.parent == this) {
			if (radioButton.isSelected() || (wantedValue != null && wantedValue.equals(radioButton.getValue()))) {
				setSelectedRadioButton(radioButton, false);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#onChildRemoved(org.kalmeo.kuix.widget.Widget)
	 */
	protected void onChildRemoved(Widget widget) {
		if (widget == selectedRadioButton) {
			setSelectedRadioButton(null, false);
		}
		super.onChildRemoved(widget);
	}
	
}

