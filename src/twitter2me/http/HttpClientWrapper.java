/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter2me.http;

import twitter2me.TwitterException;
import twitter2me.auth.Authorization;
import twitter2me.conf.Configuration;

/**
 *
 * @author mariotaku
 */
public class HttpClientWrapper implements HttpClient {

	private final HttpClient http;

	public HttpClientWrapper(Configuration conf) {
		http = conf.getHttpClient();
	}

	public HttpResponse get(String url, String sign_url, HttpParameter[] parameters, Authorization authorization) throws TwitterException {
		return http.get(url, sign_url, parameters, authorization);
	}

	public HttpResponse post(String url, String sign_url, HttpParameter[] parameters, Authorization authorization) throws TwitterException {
		return http.post(url, sign_url, parameters, authorization);
	}
	
	public HttpResponse post(String url, String sign_url, Authorization authorization) throws TwitterException {
		return http.post(url, sign_url, null, authorization);
	}
}
