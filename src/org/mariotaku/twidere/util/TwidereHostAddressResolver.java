/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mariotaku.twidere.util;

import java.io.IOException;
import twitter2me.http.HostAddressResolver;

/**
 *
 * @author mariotaku
 */
public class TwidereHostAddressResolver implements HostAddressResolver {

	public String resolve(String host) throws IOException {
		if ("gtap-120306.appspot.com".equals(host)) {
			return "203.208.46.200";
		}
		return host;
	}
	
}
