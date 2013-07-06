/*
 * Copyright 2007 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package twitter2me.auth;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import java.util.Vector;

import twitter2me.TwitterException;
import twitter2me.conf.Configuration;
import twitter2me.http.HttpClientWrapper;
import twitter2me.http.HttpParameter;
import twitter2me.http.HttpRequest;
import twitter2me.internal.util.InternalStringUtil;
import twitter2me.internal.util.URLDecoder;

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @see <a href="http://oauth.net/core/1.0a/">OAuth Core 1.0a</a>
 */
public class OAuthAuthorization implements Authorization, OAuthSupport {

	private final Configuration conf;
	private transient static HttpClientWrapper http;
	private static final HttpParameter OAUTH_SIGNATURE_METHOD = new HttpParameter("oauth_signature_method", "HMAC-SHA1");
	private String consumerKey = "";
	private String consumerSecret;
	private String realm = null;
	private OAuthToken oauthToken = null;
	// constructors
	private static Random RAND = new Random();

	/**
	 * @param conf configuration
	 */
	public OAuthAuthorization(final Configuration conf) {
		this.conf = conf;
		http = new HttpClientWrapper(conf);
		setOAuthConsumer(conf.getOAuthConsumerKey(), conf.getOAuthConsumerSecret());
		if (conf.getOAuthAccessToken() != null && conf.getOAuthAccessTokenSecret() != null) {
			setOAuthAccessToken(new AccessToken(conf.getOAuthAccessToken(), conf.getOAuthAccessTokenSecret()));
		}
	}

	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof OAuthSupport)) {
			return false;
		}

		final OAuthAuthorization that = (OAuthAuthorization) o;

		if (consumerKey != null ? !consumerKey.equals(that.consumerKey) : that.consumerKey != null) {
			return false;
		}
		if (consumerSecret != null ? !consumerSecret.equals(that.consumerSecret) : that.consumerSecret != null) {
			return false;
		}
		if (oauthToken != null ? !oauthToken.equals(that.oauthToken) : that.oauthToken != null) {
			return false;
		}

		return true;
	}

	public Vector generateOAuthSignatureHttpParams(final String method, final String sign_url) {
		final long timestamp = System.currentTimeMillis() / 1000;
		final long nonce = timestamp + RAND.nextInt();

		final Vector oauthHeaderParams = new Vector(5);
		oauthHeaderParams.addElement(new HttpParameter("oauth_consumer_key", consumerKey));
		oauthHeaderParams.addElement(OAUTH_SIGNATURE_METHOD);
		oauthHeaderParams.addElement(new HttpParameter("oauth_timestamp", timestamp));
		oauthHeaderParams.addElement(new HttpParameter("oauth_nonce", nonce));
		oauthHeaderParams.addElement(new HttpParameter("oauth_version", "1.0"));
		if (oauthToken != null) {
			oauthHeaderParams.addElement(new HttpParameter("oauth_token", oauthToken.getToken()));
		}

		final Vector signatureBaseParams = new Vector(oauthHeaderParams.size());
		addAll(signatureBaseParams, oauthHeaderParams);
		parseGetParameters(sign_url, signatureBaseParams);

		final StringBuffer base = new StringBuffer(method).append("&").append(HttpParameter.encode(constructRequestURL(sign_url))).append("&");
		base.append(HttpParameter.encode(normalizeRequestParameters(signatureBaseParams)));

		final String oauthBaseString = base.toString();
		final String signature = generateSignature(oauthBaseString, oauthToken);

		oauthHeaderParams.addElement(new HttpParameter("oauth_signature", signature));

		return oauthHeaderParams;
	}

	// implementation for OAuthSupport interface
	// implementations for Authorization
	public String getAuthorizationHeader(final HttpRequest req) {
		return generateAuthorizationHeader(req.getMethod(), req.getSignURL(), req.getParameters(), oauthToken);
	}

	/**
	 * {@inheritDoc}
	 */
	public AccessToken getOAuthAccessToken() throws TwitterException {
		ensureTokenIsAvailable();
		if (oauthToken instanceof AccessToken) {
			return (AccessToken) oauthToken;
		}
		oauthToken = new AccessToken(http.post(conf.getOAuthAccessTokenURL(), conf.getSigningOAuthAccessTokenURL(),
				this));
		return (AccessToken) oauthToken;
	}

	/**
	 * {@inheritDoc}
	 */
	public AccessToken getOAuthAccessToken(final RequestToken requestToken) throws TwitterException {
		oauthToken = requestToken;
		return getOAuthAccessToken();
	}

	/**
	 * {@inheritDoc}
	 */
	public AccessToken getOAuthAccessToken(final RequestToken requestToken, final String oauthVerifier)
			throws TwitterException {
		oauthToken = requestToken;
		return getOAuthAccessToken(oauthVerifier);
	}

	/**
	 * {@inheritDoc}
	 */
	public AccessToken getOAuthAccessToken(final String oauthVerifier) throws TwitterException {
		ensureTokenIsAvailable();
		final String url = conf.getOAuthAccessTokenURL();
		if (0 == url.indexOf("http://")) {
			// SSL is required
			// @see https://dev.twitter.com/docs/oauth/xauth
			// url = "https://" + url.substring(7);
		}
		final String sign_url = conf.getSigningOAuthAccessTokenURL();
		if (0 == sign_url.indexOf("http://")) {
			// SSL is required
			// @see https://dev.twitter.com/docs/oauth/xauth
			// sign_url = "https://" + sign_url.substring(7);
		}
		oauthToken = new AccessToken(http.post(url, sign_url, new HttpParameter[]{new HttpParameter("oauth_verifier",
					oauthVerifier)}, this));
		return (AccessToken) oauthToken;
	}

	/**
	 * {@inheritDoc}
	 */
	public AccessToken getOAuthAccessToken(final String screenName, final String password) throws TwitterException {
		try {
			final String url = conf.getOAuthAccessTokenURL();
			if (0 == url.indexOf("http://")) {
				// SSL is required
				// @see https://dev.twitter.com/docs/oauth/xauth
				// url = "https://" + url.substring(7);
			}
			final String sign_url = conf.getSigningOAuthAccessTokenURL();
			if (0 == sign_url.indexOf("http://")) {
				// SSL is required
				// @see https://dev.twitter.com/docs/oauth/xauth
				// sign_url = "https://" + sign_url.substring(7);
			}
			oauthToken = new AccessToken(http.post(url, sign_url, new HttpParameter[]{
						new HttpParameter("x_auth_username", screenName), new HttpParameter("x_auth_password", password),
						new HttpParameter("x_auth_mode", "client_auth")}, this));
			return (AccessToken) oauthToken;
		} catch (final TwitterException te) {
			throw new TwitterException("The screen name / password combination seems to be invalid.");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public RequestToken getOAuthRequestToken() throws TwitterException {
		return getOAuthRequestToken(null, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public RequestToken getOAuthRequestToken(final String callbackURL) throws TwitterException {
		return getOAuthRequestToken(callbackURL, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public RequestToken getOAuthRequestToken(final String callbackURL, final String xAuthAccessType)
			throws TwitterException {
		if (oauthToken instanceof AccessToken) {
			throw new IllegalStateException("Access token already available.");
		}
		final Vector params = new Vector();
		if (callbackURL != null) {
			params.addElement(new HttpParameter("oauth_callback", callbackURL));
		}
		if (xAuthAccessType != null) {
			params.addElement(new HttpParameter("x_auth_access_type", xAuthAccessType));
		}
		final String url = conf.getOAuthRequestTokenURL();
		if (0 == url.indexOf("http://")) {
			// SSL is required
			// @see https://dev.twitter.com/docs/oauth/xauth
			// url = "https://" + url.substring(7);
		}
		final String sign_url = conf.getSigningOAuthRequestTokenURL();
		if (0 == sign_url.indexOf("http://")) {
			// SSL is required
			// @see https://dev.twitter.com/docs/oauth/xauth
			// sign_url = "https://" + sign_url.substring(7);
		}
		oauthToken = new RequestToken(conf, http.post(url, sign_url, toHttpParameters(params),
				this), this);
		return (RequestToken) oauthToken;
	}
	
	private static HttpParameter[] toHttpParameters(Vector vector) {
		if (vector == null) return null;
		final int length = vector.size();
		final HttpParameter[] params = new HttpParameter[length];
		for (int i = 0; i < length; i++) {
			params[i] = (HttpParameter) vector.elementAt(i);
		}
		return params;
	}

	public int hashCode() {
		int result = consumerKey != null ? consumerKey.hashCode() : 0;
		result = 31 * result + (consumerSecret != null ? consumerSecret.hashCode() : 0);
		result = 31 * result + (oauthToken != null ? oauthToken.hashCode() : 0);
		return result;
	}

	/**
	 * #{inheritDoc}
	 */
	public boolean isEnabled() {
		return oauthToken != null && oauthToken instanceof AccessToken;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setOAuthAccessToken(final AccessToken accessToken) {
		oauthToken = accessToken;
	}

	public void setOAuthConsumer(final String consumerKey, final String consumerSecret) {
		this.consumerKey = consumerKey != null ? consumerKey : "";
		this.consumerSecret = consumerSecret != null ? consumerSecret : "";
	}

	/**
	 * Sets the OAuth realm
	 * 
	 * @param realm OAuth realm
	 * @since Twitter 2.1.4
	 */
	public void setOAuthRealm(final String realm) {
		this.realm = realm;
	}

	public String toString() {
		return "OAuthAuthorization{" + "consumerKey='" + consumerKey + '\''
				+ ", consumerSecret='******************************************\'" + ", oauthToken=" + oauthToken + '}';
	}

	private void ensureTokenIsAvailable() {
		if (null == oauthToken) {
			throw new IllegalStateException("No Token available.");
		}
	}

	/* package */
	private void parseGetParameters(final String url, final Vector signatureBaseParams) {
		final int queryStart = url.indexOf("?");
		if (-1 != queryStart) {
			final String[] queryStrs = InternalStringUtil.split(url.substring(queryStart + 1), "&");
			try {
				final int length = queryStrs.length;
				for (int i = 0; i < length; i++) {
					final String query = queryStrs[i];
					final String[] split = InternalStringUtil.split(query, "=");
					if (split.length == 2) {
						signatureBaseParams.addElement(new HttpParameter(URLDecoder.decode(split[0], "UTF-8"), URLDecoder.decode(split[1], "UTF-8")));
					} else {
						signatureBaseParams.addElement(new HttpParameter(URLDecoder.decode(split[0], "UTF-8"), ""));
					}
				}
			} catch (final UnsupportedEncodingException ignore) {
			}

		}

	}

	/**
	 * @return generated authorization header
	 * @see <a href="http://oauth.net/core/1.0a/#rfc.section.5.4.1">OAuth Core -
	 *      5.4.1. Authorization Header</a>
	 */
	/* package */
	String generateAuthorizationHeader(final String method, final String sign_url,
			final HttpParameter[] params, final OAuthToken token) {
		final long timestamp = System.currentTimeMillis() / 1000;
		final long nonce = timestamp + RAND.nextInt();
		return generateAuthorizationHeader(method, sign_url, params, String.valueOf(nonce), String.valueOf(timestamp),
				token);
	}

	/* package */
	String generateAuthorizationHeader(final String method, final String sign_url, HttpParameter[] params,
			final String nonce, final String timestamp, final OAuthToken otoken) {
		if (null == params) {
			params = new HttpParameter[0];
		}
		final Vector oauthHeaderParams = new Vector(5);
		oauthHeaderParams.addElement(new HttpParameter("oauth_consumer_key", consumerKey));
		oauthHeaderParams.addElement(OAUTH_SIGNATURE_METHOD);
		oauthHeaderParams.addElement(new HttpParameter("oauth_timestamp", timestamp));
		oauthHeaderParams.addElement(new HttpParameter("oauth_nonce", nonce));
		oauthHeaderParams.addElement(new HttpParameter("oauth_version", "1.0"));
		if (otoken != null) {
			oauthHeaderParams.addElement(new HttpParameter("oauth_token", otoken.getToken()));
		}
		final Vector signatureBaseParams = new Vector(oauthHeaderParams.size()
				+ params.length);
		addAll(signatureBaseParams, oauthHeaderParams);
		if (!HttpParameter.containsFile(params)) {
			addAll(signatureBaseParams, params);
		}
		parseGetParameters(sign_url, signatureBaseParams);
		final StringBuffer base = new StringBuffer(method).append("&").append(HttpParameter.encode(constructRequestURL(sign_url))).append("&");
		base.append(HttpParameter.encode(normalizeRequestParameters(signatureBaseParams)));
		final String oauthBaseString = base.toString();
		final String signature = generateSignature(oauthBaseString, otoken);

		oauthHeaderParams.addElement(new HttpParameter("oauth_signature", signature));

		// http://oauth.net/core/1.0/#rfc.section.9.1.1
		if (realm != null) {
			oauthHeaderParams.addElement(new HttpParameter("realm", realm));
		}
		return "OAuth " + encodeParameters(oauthHeaderParams, ",", true);
	}

	private static void addAll(Vector vector, Vector items) {
		if (vector == null || items == null) {
			return;
		}
		final int length = items.size();
		for (int i = 0; i < length; i++) {
			vector.addElement(items.elementAt(i));
		}
	}

	private static void addAll(Vector vector, Object[] items) {
		if (vector == null || items == null) {
			return;
		}
		final int length = items.length;
		for (int i = 0; i < length; i++) {
			vector.addElement(items[i]);
		}
	}

	String generateSignature(final String data) {
		return generateSignature(data, null);
	}

	/**
	 * Computes RFC 2104-compliant HMAC signature.
	 * 
	 * @param data the data to be signed
	 * @param token the token
	 * @return signature
	 * @see <a href="http://oauth.net/core/1.0a/#rfc.section.9.2.1">OAuth Core -
	 *      9.2.1. Generating Signature</a>
	 */
	/* package */
	String generateSignature(final String data, final OAuthToken token) {
		final String oauthSignature;
		if (token == null) {
			oauthSignature = HttpParameter.encode(consumerSecret) + "&";
		} else {
			oauthSignature = HttpParameter.encode(consumerSecret) + "&"
					+ HttpParameter.encode(token.getTokenSecret());
		}
		return BASE64Encoder.encode(HMAC.getHmac(data, oauthSignature));
	}

	/**
	 * The Signature Base String includes the request absolute URL, tying the
	 * signature to a specific endpoint. The URL used in the Signature Base
	 * String MUST include the scheme, authority, and path, and MUST exclude the
	 * query and fragment as defined by [RFC3986] section 3.<br>
	 * If the absolute request URL is not available to the Service Provider (it
	 * is always available to the Consumer), it can be constructed by combining
	 * the scheme being used, the HTTP Host header, and the relative HTTP
	 * request URL. If the Host header is not available, the Service Provider
	 * SHOULD use the host name communicated to the Consumer in the
	 * documentation or other means.<br>
	 * The Service Provider SHOULD document the form of URL used in the
	 * Signature Base String to avoid ambiguity due to URL normalization. Unless
	 * specified, URL scheme and authority MUST be lowercase and include the
	 * port number; http default port 80 and https default port 443 MUST be
	 * excluded.<br>
	 * <br>
	 * For example, the request:<br>
	 * HTTP://Example.com:80/resource?id=123<br>
	 * Is included in the Signature Base String as:<br>
	 * http://example.com/resource
	 * 
	 * @param url the url to be normalized
	 * @return the Signature Base String
	 * @see <a href="http://oauth.net/core/1.0#rfc.section.9.1.2">OAuth Core -
	 *      9.1.2. Construct Request URL</a>
	 */
	public static String constructRequestURL(String url) {
		final int index = url.indexOf("?");
		if (-1 != index) {
			url = url.substring(0, index);
		}
		final int slashIndex = url.indexOf("/", 8);
		String baseURL = url.substring(0, slashIndex).toLowerCase();
		final int colonIndex = baseURL.indexOf(":", 8);
		if (-1 != colonIndex) {
			// url contains port number
			if (baseURL.startsWith("http://") && baseURL.endsWith(":80")) {
				// http default port 80 MUST be excluded
				baseURL = baseURL.substring(0, colonIndex);
			} else if (baseURL.startsWith("https://") && baseURL.endsWith(":443")) {
				// http default port 443 MUST be excluded
				baseURL = baseURL.substring(0, colonIndex);
			}
		}
		url = baseURL + url.substring(slashIndex);

		return url;
	}

	/**
	 * @param httpParams parameters to be encoded and concatenated
	 * @return encoded string
	 * @see <a href="http://wiki.oauth.net/TestCases">OAuth / TestCases</a>
	 * @see <a
	 *      href="http://groups.google.com/group/oauth/browse_thread/thread/a8398d0521f4ae3d/9d79b698ab217df2?hl=en&lnk=gst&q=space+encoding#9d79b698ab217df2">Space
	 *      encoding - OAuth | Google Groups</a>
	 */
	public static String encodeParameters(final Vector httpParams) {
		return encodeParameters(httpParams, "&", false);
	}

	public static String encodeParameters(final Vector httpParams, final String splitter,
			final boolean quot) {
		final StringBuffer buf = new StringBuffer();
		final int params_length = httpParams.size();
		for (int i = 0; i < params_length; i++) {
			final HttpParameter param = (HttpParameter) httpParams.elementAt(i);
			if (!param.isFile()) {
				if (buf.length() != 0) {
					if (quot) {
						buf.append("\"");
					}
					buf.append(splitter);
				}
				buf.append(HttpParameter.encode(param.getName())).append("=");
				if (quot) {
					buf.append("\"");
				}
				buf.append(HttpParameter.encode(param.getValue()));
			}
		}
		if (buf.length() != 0) {
			if (quot) {
				buf.append("\"");
			}
		}
		return buf.toString();
	}

	public static String normalizeAuthorizationHeaders(final Vector params) {
		//Collections.sort(params);
		return encodeParameters(params);
	}

	/**
	 * The request parameters are collected, sorted and concatenated into a
	 * normalized string:<br>
	 * • Parameters in the OAuth HTTP Authorization header excluding the realm
	 * parameter.<br>
	 * • Parameters in the HTTP POST request body (with a content-type of
	 * application/x-www-form-urlencoded).<br>
	 * • HTTP GET parameters added to the URLs in the query part (as defined by
	 * [RFC3986] section 3).<br>
	 * <br>
	 * The oauth_signature parameter MUST be excluded.<br>
	 * The parameters are normalized into a single string as follows:<br>
	 * 1. Parameters are sorted by name, using lexicographical byte value
	 * ordering. If two or more parameters share the same name, they are sorted
	 * by their value. For example:<br>
	 * 2. a=1, c=hi%20there, f=25, f=50, f=a, z=p, z=t<br>
	 * 3. <br>
	 * 4. Parameters are concatenated in their sorted order into a single
	 * string. For each parameter, the name is separated from the corresponding
	 * value by an ‘=’ character (ASCII code 61), even if the value is empty.
	 * Each name-value pair is separated by an ‘&’ character (ASCII code 38).
	 * For example:<br>
	 * 5. a=1&c=hi%20there&f=25&f=50&f=a&z=p&z=t<br>
	 * 6. <br>
	 * 
	 * @param params parameters to be normalized and concatenated
	 * @return normalized and concatenated parameters
	 * @see <a href="http://oauth.net/core/1.0#rfc.section.9.1.1">OAuth Core -
	 *      9.1.1. Normalize Request Parameters</a>
	 */
	public static String normalizeRequestParameters(final HttpParameter[] params) {
		return normalizeRequestParameters(toParamList(params));
	}

	public static String normalizeRequestParameters(final Vector params) {
		//Collections.sort(params);
		return encodeParameters(params);
	}

	public static Vector toParamList(final HttpParameter[] params) {
		final Vector paramList = new Vector(params.length);
		addAll(paramList, params);
		return paramList;
	}
}
