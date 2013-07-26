/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mariotaku.twidere.util;

/**
 *
 * @author mariotaku
 */
public class ParseUtils {

	public static byte parseByte(Object obj) {
		return parseByte(obj, (byte) -1);
	}

	public static byte parseByte(Object obj, byte def) {
		try {
			return Byte.parseByte(parseString(obj, String.valueOf(def)));
		} catch (NumberFormatException e) {
			return def;
		}
	}

	public static String parseString(Object obj) {
		return parseString(obj, null);
	}

	public static String parseString(Object obj, String def) {
		return obj != null ? obj.toString() : def;
	}
}
