/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter2me.http;

import twitter2me.TwitterException;
import twitter2me.auth.Authorization;

/**
 *
 * @author mariotaku
 */
public interface HttpClient extends HttpResponseCode {
	
	public static final String HEADER_KEY_HOST = "Host";
	public static final String HEADER_KEY_AUTHORIZATION = "Authorization";
	public static final String HEADER_KEY_USER_AGENT = "User-Agent";
	public static final String HEADER_KEY_ACCEPT_ENCODING = "Accept-Encoding";
	public static final String HEADER_KEY_CONTENT_TYPE = "Content-Type";
	public static final String HEADER_KEY_CONTENT_LENGTH = "Content-Length";
	public static final String DEFAULT_USER_AGENT = "Twitter2ME 1.0";

	public HttpResponse get(final String url, final String sign_url, final HttpParameter[] parameters, final Authorization authorization) throws TwitterException;
	
	public HttpResponse post(final String url, final String sign_url, final HttpParameter[] parameters, final Authorization authorization) throws TwitterException;
}
