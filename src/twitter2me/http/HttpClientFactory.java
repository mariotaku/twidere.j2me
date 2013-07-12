/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter2me.http;

import twitter2me.conf.Configuration;

/**
 *
 * @author mariotaku
 */
public interface HttpClientFactory {
	
	public HttpClient newInstance(final Configuration conf);
	
}
