/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter2me.http;

import javax.microedition.io.HttpConnection;
import twitter2me.TwitterException;
import twitter2me.auth.Authorization;
import twitter2me.conf.Configuration;

/**
 *
 * @author mariotaku
 */
public class HttpClientWrapper implements HttpClient {

	public HttpResponse request(HttpRequest req) throws TwitterException {
		return http.request(req);
	}
	private final HttpClient http;

	public HttpClientWrapper(Configuration conf) {
		http = conf.getHttpClientFactory().newInstance(conf);
	}

	public HttpResponse post(String url, String sign_url, Authorization authorization) throws TwitterException {
		return post(url, sign_url, null, authorization);
	}

	public HttpResponse get(String url, String sign_url, HttpParameter[] parameters, Authorization authorization) throws
			TwitterException {
		return request(new HttpRequest(HttpConnection.GET, url, sign_url, parameters, authorization));
	}

	public HttpResponse post(String url, String sign_url, HttpParameter[] parameters, Authorization authorization)
			throws TwitterException {
		return request(new HttpRequest(HttpConnection.POST, url, sign_url, parameters, authorization));
	}

	public void shutdown() {
		http.shutdown();
	}
}
