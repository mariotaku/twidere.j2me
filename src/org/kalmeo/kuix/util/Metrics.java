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

import org.kalmeo.kuix.widget.Widget;

/**
 * @author bbeaulant
 */
public class Metrics {

	public int x;
	public int y;

	public int width;
	public int height;

	// The metrics associated widget
	public Widget widget;

	// Used in Metrics linked list structure
	public Metrics next;

	/**
	 * Construct a {@link Metrics}
	 */
	public Metrics() {
	}

	/**
	 * Construct a {@link Metrics}
	 * 
	 * @param widget
	 */
	public Metrics(Widget widget) {
		this.widget = widget;
	}

	/**
	 * Construct a {@link Metrics}
	 *
	 * @param widget
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public Metrics(Widget widget, int x, int y, int width, int height) {
		this(widget);
		setBounds(x, y, width, height);
	}
	
	/**
	 * Tests whether or not this {@link Metrics} represents an empty region.
	 * 
	 * @return <code>true</code> if the region represented by this
	 *         {@link Metrics} is empty
	 */
	public boolean isEmpty() {
		return width == 0 || height == 0;
	}

	/**
	 * Set the size and position of the region represented by this
	 * {@link Metrics}.
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void setBounds(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	/**
	 * Modifies this {@link Metrics} so that it represents the smallest region
	 * that contains both the existing region and the specified region.
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void add(int x, int y, int width, int height) {
		if (isEmpty()) {
			setBounds(x, y, width, height);
		} else {
			int right = Math.max(this.x + this.width, x + width);
			int bottom = Math.max(this.y + this.height, y + height);
			this.x = Math.min(this.x, x);
			this.y = Math.min(this.y, y);
			this.width = right - this.x;
			this.height = bottom - this.y;
		}
	}

}
