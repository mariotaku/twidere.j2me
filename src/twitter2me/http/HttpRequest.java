/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter2me.http;

import javax.microedition.io.HttpConnection;
import twitter2me.auth.Authorization;

/**
 *
 * @author mariotaku
 */
public final class HttpRequest {
	
	private final String method;
	
	private final String url, sign_url;

	private final HttpParameter[] parameters;

	private final Authorization authorization;

	private static final HttpParameter[] NULL_PARAMETERS = new HttpParameter[0];
	
	/**
	 * @param method Specifies the HTTP method
	 * @param url the request to request
	 * @param parameters parameters
	 * @param authorization Authentication implementation. Currently
	 *            BasicAuthentication, OAuthAuthentication and
	 *            NullAuthentication are supported.
	 * @param requestHeaders
	 */
	public HttpRequest(final String method, final String url, final String sign_url,
			final HttpParameter[] parameters, final Authorization authorization) {
		this.method = method;
		if (!HttpConnection.POST.equals(method) && parameters != null && parameters.length != 0) {
			final String param_string = HttpParameter.encodeParameters(parameters);
			this.url = url + "?" + param_string;
			this.sign_url = sign_url + "?" + param_string;
			this.parameters = NULL_PARAMETERS;
		} else {
			this.url = url;
			this.sign_url = sign_url;
			this.parameters = parameters;
		}
		this.authorization = authorization;
	}
	
	public HttpRequest post(final String url, final String sign_url,
			final HttpParameter[] parameters, final Authorization authorization) {
		return new HttpRequest(HttpConnection.POST, url, sign_url, parameters, authorization);
	}
	
	public HttpRequest get(final String url, final String sign_url,
			final HttpParameter[] parameters, final Authorization authorization) {
		return new HttpRequest(HttpConnection.GET, url, sign_url, parameters, authorization);
	}
	
		public Authorization getAuthorization() {
		return authorization;
	}

	public String getMethod() {
		return method;
	}

	public HttpParameter[] getParameters() {
		return parameters;
	}

	public String getSignURL() {
		return sign_url != null ? sign_url : url;
	}

	public String getURL() {
		return url;
	}
}
