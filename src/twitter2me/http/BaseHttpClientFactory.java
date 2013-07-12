/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter2me.http;

import twitter2me.conf.Configuration;
import twitter2me.http.impl.HttpClientImpl;

/**
 *
 * @author mariotaku
 */
public class BaseHttpClientFactory implements HttpClientFactory {

	public HttpClient newInstance(Configuration conf) {
		return HttpClientImpl.getInstance(conf);
	}
	
}
