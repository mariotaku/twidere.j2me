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

import java.util.Vector;

/**
 * A {@link StyleSelector} represents the full CSS defined path that represent a
 * style.<br>
 * For example : <code>screen container.myclass button:hover</code> will be
 * represented by a tree of three StyleSelectors where names are respectively :
 * <code>screen</code>, <code>container.myclass</code>,
 * <code>button:hover</code>.
 * 
 * @author bbeaulant
 */
public class StyleSelector {

	// The name of this selector
	public String name;

	// The parent key
	public StyleSelector parent;

	private boolean hasTag = false;
	private boolean hasId = false;
	private boolean hasStyleClass = false;
	private boolean hasPseudoClass = false;
	
	private String tag;
	private String id;
	private String styleClass;
	private String[] pseudoClasses;

	/**
	 * Construct a {@link StyleSelector}
	 * 
	 * @param name
	 */
	public StyleSelector(String name) {
		this.name = name;
		
		StringBuffer tagReader = new StringBuffer();
		StringBuffer idReader = new StringBuffer();
		StringBuffer classReader = new StringBuffer();
		Vector pseudoClassReaders = new Vector();
		StringBuffer currentReader = tagReader;
		
		for (int i = 0; i<name.length(); ++i) {
			char c = name.charAt(i);
			switch (c) {
				
				case '#':
					currentReader = idReader;
					break;

				case '.':
					currentReader = classReader;
					break;
					
				case ':':
					currentReader = new StringBuffer();
					pseudoClassReaders.addElement(currentReader);
					break;
					
				default:
					currentReader.append(c);
					break;
			}
		}
		
		hasTag = tagReader.length() != 0;
		if (hasTag) {
			tag = tagReader.toString();
		}
		hasId = idReader.length() != 0;
		if (hasId) {
			id = idReader.toString();
		}
		hasStyleClass = classReader.length() != 0;
		if (hasStyleClass) {
			styleClass = classReader.toString();
		}
		int size = pseudoClassReaders.size();
		hasPseudoClass = size != 0;
		if (hasPseudoClass) {
			pseudoClasses = new String[size];
			for (int i = 0; i<size; ++i) {
				pseudoClasses[i] = ((StringBuffer) pseudoClassReaders.elementAt(i)).toString();
			}
		}
		
	}

	/**
	 * @return <code>true</code> if this {@link StyleSelector} has parent
	 */
	public boolean hasParent() {
		return parent != null;
	}
	
	/**
	 * @return the hasTag
	 */
	public boolean hasTag() {
		return hasTag;
	}

	/**
	 * @return the hasId
	 */
	public boolean hasId() {
		return hasId;
	}

	/**
	 * @return the hasClass
	 */
	public boolean hasClass() {
		return hasStyleClass;
	}

	/**
	 * @return the hasPseudoClass
	 */
	public boolean hasPseudoClass() {
		return hasPseudoClass;
	}
	
	/**
	 * @return the pseudoClasses
	 */
	public String[] getPseudoClasses() {
		return pseudoClasses;
	}
	
	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the styleClass
	 */
	public String getStyleClass() {
		return styleClass;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		StyleSelector styleSelector = (StyleSelector) obj;
		if (styleSelector != null) {
			boolean parentEquality = styleSelector.parent == null;
			if (parent != null) {
				parentEquality = parent.equals(styleSelector.parent);
			}
			return parentEquality && name.equals(styleSelector.name);
		}
		return false;
	}

}
