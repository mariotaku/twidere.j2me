/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mariotaku.twidere.util;

/**
 *
 * @author mariotaku
 */
public class TextUtils {

	public static boolean isDigitsOnly(String str) {
		final int len = str.length();
		for (int i = 0; i < len; i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static boolean isEmpty(final String str) {
		return str == null || str.length() == 0;
	}
}
