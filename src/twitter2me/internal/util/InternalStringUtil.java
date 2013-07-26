/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter2me.internal.util;

import java.util.Vector;

/**
 *
 * @author mariotaku
 */
public class InternalStringUtil {

	public static boolean equalsIgnoreCase(final String str1, final String str2) {
		if (str1 == null || str2 == null) {
			return str1 == str2;
		}
		return str1.toLowerCase().equals(str2.toLowerCase());
	}

	public static String mask(final String str) {
		if (str == null) {
			return null;
		}
		final int length = str.length();
		final StringBuffer sb = new StringBuffer(length);
		for (int i = 0; i < length; i++) {
			sb.append('*');
		}
		return sb.toString();
	}

	public static String[] split(final String str, final String separator) {
		String[] returnValue;
		int index = str.indexOf(separator);
		if (index == -1) {
			returnValue = new String[] { str };
		} else {
			final Vector strList = new Vector();
			int oldIndex = 0;
			while (index != -1) {
				final String subStr = str.substring(oldIndex, index);
				strList.addElement(subStr);
				oldIndex = index + separator.length();
				index = str.indexOf(separator, oldIndex);
			}
			if (oldIndex != str.length()) {
				strList.addElement(str.substring(oldIndex));
			}
			final int size = strList.size();
			returnValue = new String[size];
			for (int i = 0; i < size; i++) {
				returnValue[i] = (String) strList.elementAt(i);
			}
		}

		return returnValue;
	}

	public static String replace(final String str, final String pattern, final String replacement) {
		int s = 0;
		int e = 0;
		StringBuffer result = new StringBuffer();

		while ((e = str.indexOf(pattern, s)) >= 0) {
			result.append(str.substring(s, e));
			result.append(replacement);
			s = e + pattern.length();
		}
		result.append(str.substring(s));
		return result.toString();
	}
}
