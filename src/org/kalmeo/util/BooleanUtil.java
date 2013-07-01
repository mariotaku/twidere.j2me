/*
 * This file is part of org.kalmeo.util.
 * 
 * org.kalmeo.util is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * org.kalmeo.util is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with org.kalmeo.util.  If not, see <http://www.gnu.org/licenses/>.
 *  
 * Creation date : 13 févr. 08
 * Copyright (c) Kalmeo 2008. All rights reserved.
 */

package org.kalmeo.util;

/**
 * @author bbeaulant
 */
public class BooleanUtil {
	
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	public static final String NOT_FALSE = "!" + FALSE;
	
	/**
	 * Returns a String object representing this Boolean's value. If this object
	 * represents the value true, a string equal to "true" is returned.
	 * Otherwise, a string equal to "false" is returned.
	 * 
	 * @param value
	 * @return a string representation of this object.
	 */
	public static String toString(boolean value) {
		if (value) {
			return TRUE;
		} else {
			return FALSE;
		}
	}
	
	/**
	 * Parses the string argument as a boolean.
	 * 
	 * @param s a string.
	 * @return the boolean represented by the argument.
	 */
	public static boolean parseBoolean(String s) {
		if (s != null) {
			s = s.trim();
			return (TRUE.equals(s) || NOT_FALSE.equals(s)); 
		}
		return false;
	}

}
