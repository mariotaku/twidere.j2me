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

package org.kalmeo.kuix.util;

/**
 * An <code>Insets</code> object is a representation of the borders of a
 * container. It specifies the space that a container must leave at each of its
 * edges. The space can be a margin, a border or a padding.
 */
public class Insets {

	public int top;
	public int left;
	public int bottom;
	public int right;

	/**
	 * Creates and initializes a new <code>Insets</code> object with null
	 * values
	 */
	public Insets() {
		this(0, 0, 0, 0);
	}

	/**
	 * Creates and initializes a new <code>Insets</code> object with the
	 * specified top, left, bottom, and right insets.
	 * 
	 * @param top the inset from the top.
	 * @param right the inset from the right.
	 * @param bottom the inset from the bottom.
	 * @param left the inset from the left.
	 */
	public Insets(int top, int right, int bottom, int left) {
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
	}

}
