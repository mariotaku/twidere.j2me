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
 * @author bbeaulant
 */
public class Color {

	// Default colors
	public static final Color RED 	= new Color(0xFF0000);
	public static final Color GREEN = new Color(0x00FF00);
	public static final Color BLUE 	= new Color(0x0000FF);
	public static final Color BLACK = new Color(0x000000);
	public static final Color WHITE = new Color(0xFFFFFF);

	private int rgb;

	/**
	 * Construct a {@link Color}
	 * 
	 * @param rgb
	 */
	public Color(int rgb) {
		this.rgb = rgb;
	}

	/**
	 * Construct a {@link Color}
	 * 
	 * @param rgbHex The RGB value in Hexadecimal format
	 */
	public Color(String rgbHex) {
		if (rgbHex.startsWith("#")) {
			rgbHex = rgbHex.substring(1);
		}
		this.rgb = Integer.parseInt(rgbHex, 16);
	}

	/**
	 * @return the rgb
	 */
	public int getRGB() {
		return rgb;
	}

}
