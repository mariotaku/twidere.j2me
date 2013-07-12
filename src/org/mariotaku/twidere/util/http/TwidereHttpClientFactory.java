/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mariotaku.twidere.util.http;

import twitter2me.conf.Configuration;
import twitter2me.http.HttpClient;
import twitter2me.http.HttpClientFactory;

/**
 *
 * @author mariotaku
 */
public class TwidereHttpClientFactory implements HttpClientFactory {

	public HttpClient newInstance(final Configuration conf) {
		return new TwidereHttpClientImpl(conf);
	}
	
}
