/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mariotaku.twidere.util;

import twitter2me.internal.util.HttpUrl;

/**
 *
 * @author mariotaku
 */
public class Utils {

	private static final String NOKIA_SYSTEM_PROPERTY_TIMEFORMAT = "com.nokia.mid.timeformat";

	public static boolean is24HourFormat() {
		final String format = System.getProperty(NOKIA_SYSTEM_PROPERTY_TIMEFORMAT);
		if (format == null) {
			return true;
		}
		return !format.startsWith("hh");
	}

	public static boolean isValidUrl(String urlStr) {
		try {
			final HttpUrl url = new HttpUrl(urlStr);
			return url.getAuthority() != null && url.getHost() != null;
		} catch (Exception e) {
			return false;
		}
	}
}
