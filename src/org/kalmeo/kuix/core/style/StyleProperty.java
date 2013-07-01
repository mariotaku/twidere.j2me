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

package org.kalmeo.kuix.core.style;

import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.util.LinkedListItem;

/**
 * A {@link StyleProperty} represents a name / value pair. For example
 * <code>name : "color"</code> and <code>value : Color.RED</code>.
 * 
 * @author bbeaulant
 */
public class StyleProperty implements LinkedListItem {

	// Style property name
	private final String name;

	// Style property value
	private String rawValue;
	private Object value;

	// The parent and next StyleProperty in the style linked list
	private StyleProperty parent;
	private StyleProperty next;

	/**
	 * Construct a {@link StyleAttribute}
	 * 
	 * @param name
	 * @param rawValue
	 */
	public StyleProperty(String name, String rawValue) {
		this.name = name.toLowerCase();
		this.rawValue = rawValue;
	}
	
	/**
	 * Construct a {@link StyleAttribute}
	 * 
	 * @param name
	 * @param value
	 */
	public StyleProperty(String name, Object value) {
		this(name, null);
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see com.kalmeo.util.LinkedListItem#getNext()
	 */
	public LinkedListItem getNext() {
		return next;
	}

	/* (non-Javadoc)
	 * @see com.kalmeo.util.LinkedListItem#getParent()
	 */
	public LinkedListItem getPrevious() {
		return parent;
	}

	/* (non-Javadoc)
	 * @see com.kalmeo.util.LinkedListItem#setNext(com.kalmeo.util.LinkedListItem)
	 */
	public void setNext(LinkedListItem next) {
		this.next = (StyleProperty) next;
	}

	/* (non-Javadoc)
	 * @see com.kalmeo.util.LinkedListItem#setParent(com.kalmeo.util.LinkedListItem)
	 */
	public void setPrevious(LinkedListItem parent) {
		this.parent = (StyleProperty) parent;
	}

	/**
	 * Returns the name of the styleProperty.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retuns the value of the styleProperty.
	 * 
	 * @return the value
	 */
	public Object getValue() {
		if (rawValue != null) {
			
			// Convert the property value
			value = Kuix.getConverter().convertStyleProperty(name, rawValue);
			
			// Reset the rawValue to tag it as converted
			rawValue = null;
			
		}
		return value;
	}

	/**
	 * Returns a copy of this {@link StyleProperty}. Only <code>name</code>
	 * and <code>value</code> fields ae copied.
	 * 
	 * @return A copy of this {@link StyleProperty}
	 */
	public StyleProperty copy() {
		StyleProperty styleProperty = new StyleProperty(name, rawValue);
		styleProperty.value = value;
		return styleProperty;
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.util.LinkedListItem#compareTo(org.kalmeo.util.LinkedListItem, int)
	 */
	public int compareTo(LinkedListItem item, int flag) {
		return 0;
	}

}
