/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mariotaku.twidere.util;

import java.io.IOException;
import twitter2me.http.HostAddressResolver;
import twitter2me.internal.util.InternalStringUtil;

/**
 *
 * @author mariotaku
 */
public class TwidereHostAddressResolver implements HostAddressResolver {

	public String resolve(String host) throws IOException {
		if (host == null) {
			return null;
		}
		final String[] host_segments = InternalStringUtil.split(host, ".");
		final int host_segments_length = host_segments.length;
		if (host_segments_length > 2) {
			final String top_domain = host_segments[host_segments_length - 2] + "."
					+ host_segments[host_segments_length - 1];
			if ("appspot.com".equals(top_domain)) {
				return "203.208.46.200";
			}
		}
		return host;
	}
}
