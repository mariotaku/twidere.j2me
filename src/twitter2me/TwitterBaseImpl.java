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
package twitter2me;

import repackaged.java.util.List;
import twitter2me.auth.AccessToken;
import twitter2me.auth.Authorization;
import twitter2me.auth.AuthorizationFactory;
import twitter2me.auth.BasicAuthorization;
import twitter2me.auth.NullAuthorization;
import twitter2me.auth.OAuthAuthorization;
import twitter2me.auth.OAuthSupport;
import twitter2me.auth.RequestToken;
import twitter2me.auth.XAuthAuthorization;
import twitter2me.conf.Configuration;
import twitter2me.http.HttpClientWrapper;
import twitter2me.http.HttpParameter;
import twitter2me.http.HttpResponse;
import twitter2me.http.HttpResponseCode;
import twitter2me.internal.json.InternalJSONFactory;
import twitter2me.internal.json.InternalJSONFactoryImpl;

/**
 * Base class of Twitter / AsyncTwitter / TwitterStream supports OAuth.
 * 
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
abstract class TwitterBaseImpl implements TwitterBase {

	protected final HttpParameter INCLUDE_ENTITIES;
	protected Configuration conf;
	protected transient String screenName = null;
	protected transient long id = 0;
	protected transient HttpClientWrapper http;
	protected InternalJSONFactory factory;
	protected Authorization auth;

	/* package */
	TwitterBaseImpl(final Configuration conf, final Authorization auth) {
		this.conf = conf;
		this.auth = auth;
		INCLUDE_ENTITIES = new HttpParameter("include_entities", conf.isIncludeEntitiesEnabled());
		init();
	}

	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof TwitterBaseImpl)) {
			return false;
		}

		final TwitterBaseImpl that = (TwitterBaseImpl) o;

		if (auth != null ? !auth.equals(that.auth) : that.auth != null) {
			return false;
		}
		if (!conf.equals(that.conf)) {
			return false;
		}
		if (http != null ? !http.equals(that.http) : that.http != null) {
			return false;
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public final Authorization getAuthorization() {
		return auth;
	}

	/**
	 * {@inheritDoc}
	 */
	public Configuration getConfiguration() {
		return conf;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getId() throws TwitterException, IllegalStateException {
		if (!auth.isEnabled()) {
			throw new IllegalStateException(
					"Neither user ID/password combination nor OAuth consumer key/secret combination supplied");
		}
		if (0 == id) {
			fillInIDAndScreenName();
		}
		// retrieve the screen name if this instance is authenticated with OAuth
		// or email address
		return id;
	}

	/**
	 * {@inheritDoc} Basic authenticated instance of this class will try
	 * acquiring an AccessToken using xAuth.
	 * In order to get access acquire AccessToken using xAuth, you must apply by
	 * sending an email to <a href="mailto:api@twitter.com">api@twitter.com</a>
	 * all other applications will receive an HTTP 401 error. Web-based
	 * applications will not be granted access, except on a temporary basis for
	 * when they are converting from basic-authentication support to full OAuth
	 * support.
	 * Storage of Twitter usernames and passwords is forbidden. By using xAuth,
	 * you are required to store only access tokens and access token secrets. If
	 * the access token expires or is expunged by a user, you must ask for their
	 * login and password again before exchanging the credentials for an access
	 * token.
	 * 
	 * @throws TwitterException When Twitter service or network is unavailable,
	 *             when the user has not authorized, or when the client
	 *             application is not permitted to use xAuth
	 * @see <a href="https://dev.twitter.com/docs/oauth/xauth">xAuth | Twitter
	 *      Developers</a>
	 */
	public synchronized AccessToken getOAuthAccessToken() throws TwitterException {
		Authorization auth = getAuthorization();
		AccessToken oauthAccessToken;
		if (auth instanceof BasicAuthorization) {
			final BasicAuthorization basicAuth = (BasicAuthorization) auth;
			auth = AuthorizationFactory.getInstance(conf);
			if (auth instanceof OAuthAuthorization) {
				this.auth = auth;
				final OAuthAuthorization oauthAuth = (OAuthAuthorization) auth;
				oauthAccessToken = oauthAuth.getOAuthAccessToken(basicAuth.getUserId(), basicAuth.getPassword());
			} else {
				throw new IllegalStateException("consumer key / secret combination not supplied.");
			}
		} else {
			if (auth instanceof XAuthAuthorization) {
				final XAuthAuthorization xauth = (XAuthAuthorization) auth;
				this.auth = xauth;
				final OAuthAuthorization oauthAuth = new OAuthAuthorization(conf);
				oauthAuth.setOAuthConsumer(xauth.getConsumerKey(), xauth.getConsumerSecret());
				oauthAccessToken = oauthAuth.getOAuthAccessToken(xauth.getUserId(), xauth.getPassword());
			} else {
				oauthAccessToken = getOAuth().getOAuthAccessToken();
			}
		}
		screenName = oauthAccessToken.getScreenName();
		id = oauthAccessToken.getUserId();
		return oauthAccessToken;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IllegalStateException when AccessToken has already been retrieved
	 *             or set
	 */
	public synchronized AccessToken getOAuthAccessToken(final RequestToken requestToken) throws TwitterException {
		final OAuthSupport oauth = getOAuth();
		final AccessToken oauthAccessToken = oauth.getOAuthAccessToken(requestToken);
		screenName = oauthAccessToken.getScreenName();
		return oauthAccessToken;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IllegalStateException when AccessToken has already been retrieved
	 *             or set
	 */
	public synchronized AccessToken getOAuthAccessToken(final RequestToken requestToken, final String oauthVerifier)
			throws TwitterException {
		return getOAuth().getOAuthAccessToken(requestToken, oauthVerifier);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IllegalStateException when AccessToken has already been retrieved
	 *             or set
	 */
	public synchronized AccessToken getOAuthAccessToken(final String oauthVerifier) throws TwitterException {
		final AccessToken oauthAccessToken = getOAuth().getOAuthAccessToken(oauthVerifier);
		screenName = oauthAccessToken.getScreenName();
		return oauthAccessToken;
	}

	/**
	 * {@inheritDoc}
	 */
	public synchronized AccessToken getOAuthAccessToken(final String screenName, final String password)
			throws TwitterException {
		return getOAuth().getOAuthAccessToken(screenName, password);
	}

	/* OAuth support methods */
	/**
	 * {@inheritDoc}
	 */
	public RequestToken getOAuthRequestToken() throws TwitterException {
		return getOAuthRequestToken(null);
	}

	/**
	 * {@inheritDoc}
	 */
	public RequestToken getOAuthRequestToken(final String callbackUrl) throws TwitterException {
		return getOAuth().getOAuthRequestToken(callbackUrl);
	}

	/**
	 * {@inheritDoc}
	 */
	public RequestToken getOAuthRequestToken(final String callbackUrl, final String xAuthAccessType)
			throws TwitterException {
		return getOAuth().getOAuthRequestToken(callbackUrl, xAuthAccessType);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getScreenName() throws TwitterException, IllegalStateException {
		if (!auth.isEnabled()) {
			throw new IllegalStateException(
					"Neither user ID/password combination nor OAuth consumer key/secret combination supplied");
		}
		if (null == screenName) {
			if (auth instanceof BasicAuthorization) {
				screenName = ((BasicAuthorization) auth).getUserId();
				if (-1 != screenName.indexOf("@")) {
					screenName = null;
				}
			}
			if (null == screenName) {
				// retrieve the screen name if this instance is authenticated
				// with OAuth or email address
				fillInIDAndScreenName();
			}
		}
		return screenName;
	}

	// methods declared in OAuthSupport interface
	public int hashCode() {
		int result = conf.hashCode();
		result = 31 * result + (http != null ? http.hashCode() : 0);
		result = 31 * result + (auth != null ? auth.hashCode() : 0);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public synchronized void setOAuthAccessToken(final AccessToken accessToken) {
		getOAuth().setOAuthAccessToken(accessToken);
	}

	/**
	 * {@inheritDoc}
	 */
	public synchronized void setOAuthConsumer(final String consumerKey, final String consumerSecret) {
		if (null == consumerKey) {
			throw new NullPointerException("consumer key is null");
		}
		if (null == consumerSecret) {
			throw new NullPointerException("consumer secret is null");
		}
		if (auth instanceof NullAuthorization) {
			final OAuthAuthorization oauth = new OAuthAuthorization(conf);
			oauth.setOAuthConsumer(consumerKey, consumerSecret);
			auth = oauth;
		} else if (auth instanceof BasicAuthorization) {
			final XAuthAuthorization xauth = new XAuthAuthorization((BasicAuthorization) auth);
			xauth.setOAuthConsumer(consumerKey, consumerSecret);
			auth = xauth;
		} else if (auth instanceof OAuthAuthorization) {
			throw new IllegalStateException("consumer key/secret pair already set.");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void shutdown() {
		if (http != null) {
			http.shutdown();
		}
	}

	public String toString() {
		return "TwitterBase{" + "conf=" + conf + ", http=" + http + ", auth=" + auth + '}';
	}

	protected void addParameterToList(final List params, final String paramName, final boolean param) {
		params.add(new HttpParameter(paramName, param));
	}

	protected void addParameterToList(final List params, final String paramName, final int param) {
		params.add(new HttpParameter(paramName, param));
	}

	protected void addParameterToList(final List params, final String paramName, final String param) {
		if (param != null) {
			params.add(new HttpParameter(paramName, param));
		}
	}

	protected final void ensureAuthorizationEnabled() {
		if (!auth.isEnabled()) {
			throw new IllegalStateException("Authentication credentials are missing.");
		}
	}

	protected final void ensureOAuthEnabled() {
		if (!(auth instanceof OAuthAuthorization)) {
			throw new IllegalStateException("OAuth required. Authentication credentials are missing.");
		}
	}

	protected User fillInIDAndScreenName() throws TwitterException {
		ensureAuthorizationEnabled();
		final String url = conf.getRestBaseURL() + ENDPOINT_ACCOUNT_VERIFY_CREDENTIALS;
		final String sign_url = conf.getSigningRestBaseURL() + ENDPOINT_ACCOUNT_VERIFY_CREDENTIALS;
		final User user = factory.createUser(http.get(url, sign_url, new HttpParameter[] { INCLUDE_ENTITIES }, auth));
		screenName = user.getScreenName();
		id = user.getId();
		return user;
	}

	protected HttpResponse get(final String url, final String sign_url, final HttpParameter[] parameters)
			throws TwitterException {
		return http.get(url, sign_url, parameters, auth);
	}

	protected boolean isOk(final HttpResponse response) {
		if (response == null) {
			return false;
		}
		final int statusCode = response.getStatusCode();
		return statusCode == HttpResponseCode.OK || statusCode == HttpResponseCode.CREATED || statusCode
				== HttpResponseCode.ACCEPTED;
	}

	protected HttpParameter[] mergeParameters(final HttpParameter[] params1, final HttpParameter[] params2) {
		if (params1 != null && params2 != null) {
			final HttpParameter[] params = new HttpParameter[params1.length + params2.length];
			System.arraycopy(params1, 0, params, 0, params1.length);
			System.arraycopy(params2, 0, params, params1.length, params2.length);
			return params;
		}
		if (null == params1 && null == params2) {
			return new HttpParameter[0];
		}
		if (params1 != null) {
			return params1;
		} else {
			return params2;
		}
	}

	protected HttpResponse post(final String url, final String sign_url, final HttpParameter[] parameters)
			throws TwitterException {
		return http.post(url, sign_url, parameters, auth);
	}

	protected void setFactory() {
		factory = new InternalJSONFactoryImpl(conf);
	}

	private OAuthSupport getOAuth() {
		if (!(auth instanceof OAuthSupport)) {
			throw new IllegalStateException("OAuth consumer key/secret combination not supplied");
		}
		return (OAuthSupport) auth;
	}

	private void init() {
		if (null == auth) {
			// try to populate OAuthAuthorization if available in the
			// configuration
			final String consumerKey = conf.getOAuthConsumerKey();
			final String consumerSecret = conf.getOAuthConsumerSecret();
			// try to find oauth tokens in the configuration
			if (consumerKey != null && consumerSecret != null) {
				final OAuthAuthorization oauth = new OAuthAuthorization(conf);
				final String accessToken = conf.getOAuthAccessToken();
				final String accessTokenSecret = conf.getOAuthAccessTokenSecret();
				if (accessToken != null && accessTokenSecret != null) {
					oauth.setOAuthAccessToken(new AccessToken(accessToken, accessTokenSecret));
				}
				auth = oauth;
			} else {
				auth = NullAuthorization.getInstance();
			}
		}
		http = new HttpClientWrapper(conf);
		setFactory();
	}
}