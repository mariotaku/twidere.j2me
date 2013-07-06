/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mariotaku.twidere.util.http;

import java.io.IOException;
import javax.microedition.io.Connection;
import org.mariotaku.twidere.util.Utils;
import twitter2me.conf.Configuration;
import twitter2me.http.impl.HttpClientImpl;

/**
 *
 * @author mariotaku
 */
public class TwidereHttpClientImpl extends HttpClientImpl {

	public TwidereHttpClientImpl(Configuration conf) {
		super(conf);
	}

	protected Connection createConnection(String url) throws IOException {
		if (url == null) throw new NullPointerException();
		if (Utils.hasSunJavaConnector() && url.startsWith("https://") && conf.isSSLErrorIgnored()) {
			return UnsafeHttpsConnection.open(null);
		}
		return super.createConnection(url);
	}
	
}
