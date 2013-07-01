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
 * Creation date : 26 mai 08
 * Copyright (c) Kalmeo 2007-2008. All rights reserved.
 * http://www.kalmeo.org
 */

package org.kalmeo.util;

/**
 * @author bbeaulant
 */
public class StringUtil {

	/**
	 * Format a string like "Where is {0}, in the {1}" withe the corresponding
	 * <code>args</code> values.
	 * 
	 * @param pattern
	 * @param args
	 * @return the formated string
	 */
	public static String format(String pattern, Object[] args) {
		if (pattern != null) {
			StringBuffer toAppendTo = new StringBuffer();
			int l = pattern.length();
			int n = 0, lIndex = -1, lastIndex = 0;
			for (int i = 0; i < l; i++) {
				if (pattern.charAt(i) == '{') {
					n++;
					if (n == 1) {
						lIndex = i;
						toAppendTo.append(pattern.substring(lastIndex, i));
						lastIndex = i;
					}
				}
				if (pattern.charAt(i) == '}') {
					if (n == 1) {
						toAppendTo.append(processPattern(pattern.substring(lIndex + 1, i), args));
						lIndex = -1;
						lastIndex = i + 1;
					}
					n--;
				}
			}
			if (n > 0) {
				toAppendTo.append(processPattern(pattern.substring(lIndex + 1), args));
			} else {
				toAppendTo.append(pattern.substring(lastIndex));
			}
			return toAppendTo.toString();
		}
		return null;
	}

	/**
	 * Extracts N-th from an array of argumens.
	 * 
	 * @param indexString a String number
	 * @param args array of arguments
	 * @return the indexString-th parameter from the array
	 */
	private static String processPattern(String indexString, Object[] args) {
		try {
			int index = Integer.parseInt(indexString);
			if ((args != null) && (index >= 0) && (index < args.length)) {
				if (args[index] != null) {
					return args[index].toString();
				}
			}
		} catch (NumberFormatException nfe) {
			// NFE - nothing bad basically - the argument is not a number
			// swallow it for the time being and show default string
		}
		return "?";
	}

	/**
	 * Extract parameters from a <code>prefix(params)</code> syntaxed string.
	 * 
	 * @param prefix
	 * @param rawData
	 * @return The paramters full string
	 */
	public static String extractRawParams(String prefix, String rawData) {
		if (rawData.startsWith(prefix)) {
			int posStart = rawData.indexOf(prefix + "(");
			int posEnd = rawData.indexOf(")");
			if (posStart != -1 && posEnd != -1 && posStart < posEnd) {
				return rawData.substring(posStart + prefix.length() + 1, posEnd);
			}
		}
		return null;
	}

}
