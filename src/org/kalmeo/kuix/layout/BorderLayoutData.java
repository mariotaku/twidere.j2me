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

package org.kalmeo.kuix.layout;

/**
 * @author bbeaulant
 */
public class BorderLayoutData implements LayoutData {

	public static final byte CENTER = 0;
	public static final byte NORTH = 1;
	public static final byte EAST = 2;
	public static final byte WEST = 3;
	public static final byte SOUTH = 4;
	
	// Public static instances
	public static final BorderLayoutData instanceCenter = new BorderLayoutData(CENTER);
	public static final BorderLayoutData instanceNorth = new BorderLayoutData(NORTH);
	public static final BorderLayoutData instanceEast = new BorderLayoutData(EAST);
	public static final BorderLayoutData instanceWest = new BorderLayoutData(WEST);
	public static final BorderLayoutData instanceSouth = new BorderLayoutData(SOUTH);
	
	public byte position;

	/**
	 * Construct a {@link BorderLayoutData}
	 *
	 * @param position
	 */
	private BorderLayoutData(byte position) {
		this.position = position;
	}
	
}
