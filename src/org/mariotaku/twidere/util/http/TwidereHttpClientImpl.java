/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mariotaku.twidere.util.http;

import java.io.IOException;
import javax.microedition.io.Connection;
import twitter2me.conf.Configuration;
import twitter2me.http.impl.HttpClientImpl;
import twitter2me.internal.util.HttpUrl;

/**
 *
 * @author mariotaku
 */
public final class TwidereHttpClientImpl extends HttpClientImpl {

	public TwidereHttpClientImpl(Configuration conf) {
		super(conf);
	}

	protected Connection createConnection(String url) throws IOException {
		if (url == null) throw new NullPointerException();
		final SocketHttpConnection conn = SocketHttpConnection.open(url);
		conn.setSSLErrorsIgnored(conf.isSSLErrorsIgnored());
		return conn;
	}
	
}
