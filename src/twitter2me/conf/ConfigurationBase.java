/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter2me.conf;

import twitter2me.TwitterConstants;
import twitter2me.Version;
import twitter2me.http.BaseHttpClientFactory;
import twitter2me.http.HostAddressResolver;
import twitter2me.http.HttpClientFactory;

/**
 * Configuration base class with default settings.
 * 
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
class ConfigurationBase implements TwitterConstants, Configuration {

	static final boolean DEFAULT_USE_SSL = true;
	private boolean debug;
	private String user;
	private String password;
	private boolean sslErrorsIgnored;
	private boolean gzipEnabled;
	private int httpRetryCount;
	private int httpRetryIntervalSeconds;
	private String oAuthConsumerKey;
	private String oAuthConsumerSecret;
	private String oAuthAccessToken;
	private String oAuthAccessTokenSecret;
	private String oAuthRequestTokenURL;
	private String oAuthAuthorizationURL;
	private String oAuthAccessTokenURL;
	private String oAuthAuthenticationURL;
	private String signingOAuthRequestTokenURL;
	private String signingOAuthAuthorizationURL;
	private String signingOAuthAccessTokenURL;
	private String signingOAuthAuthenticationURL;
	private String oAuthBaseURL = DEFAULT_OAUTH_BASE_URL;
	private String signingOAuthBaseURL;
	private String signingRestBaseURL;
	private String restBaseURL;
	private boolean includeRTsEnabled;
	private boolean includeEntitiesEnabled;
	private String clientVersion;
	private String clientURL;
	private String clientName;
	private String httpUserAgent;
	private HttpClientFactory httpClientFactory;
	private HostAddressResolver hostAddressResolver;

	protected ConfigurationBase() {
		setDebug(false);
		setUser(null);
		setPassword(null);
		setGZIPEnabled(true);
		setHttpRetryCount(0);
		setHttpRetryIntervalSeconds(5);
		setHttpClientFacory(null);
		setOAuthConsumerKey(null);
		setOAuthConsumerSecret(null);
		setOAuthAccessToken(null);
		setOAuthAccessTokenSecret(null);
		setClientName("Twitter2ME");
		setClientVersion(Version.getVersion());
		setClientURL("http://twitter4j.org/en/twitter4j-" + Version.getVersion() + ".xml");
		setHttpUserAgent("twitter4j http://twitter4j.org/ /" + Version.getVersion());

		setIncludeRTsEnbled(true);

		setIncludeEntitiesEnbled(true);

		setOAuthBaseURL(DEFAULT_OAUTH_BASE_URL);
		setSigningOAuthBaseURL(DEFAULT_SIGNING_OAUTH_BASE_URL);

		setRestBaseURL(DEFAULT_REST_BASE_URL);
		setSigningRestBaseURL(DEFAULT_SIGNING_REST_BASE_URL);
		setIncludeRTsEnbled(true);
	}

	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ConfigurationBase)) {
			return false;
		}
		final ConfigurationBase other = (ConfigurationBase) obj;
		if (clientName == null) {
			if (other.clientName != null) {
				return false;
			}
		} else if (!clientName.equals(other.clientName)) {
			return false;
		}
		if (clientURL == null) {
			if (other.clientURL != null) {
				return false;
			}
		} else if (!clientURL.equals(other.clientURL)) {
			return false;
		}
		if (clientVersion == null) {
			if (other.clientVersion != null) {
				return false;
			}
		} else if (!clientVersion.equals(other.clientVersion)) {
			return false;
		}
		if (debug != other.debug) {
			return false;
		}
		if (gzipEnabled != other.gzipEnabled) {
			return false;
		}
		if (hostAddressResolver == null) {
			if (other.hostAddressResolver != null) {
				return false;
			}
		} else if (!hostAddressResolver.equals(other.hostAddressResolver)) {
			return false;
		}
		if (httpRetryCount != other.httpRetryCount) {
			return false;
		}
		if (httpRetryIntervalSeconds != other.httpRetryIntervalSeconds) {
			return false;
		}
		if (sslErrorsIgnored != other.sslErrorsIgnored) {
			return false;
		}
		if (includeEntitiesEnabled != other.includeEntitiesEnabled) {
			return false;
		}
		if (includeRTsEnabled != other.includeRTsEnabled) {
			return false;
		}
		if (oAuthAccessToken == null) {
			if (other.oAuthAccessToken != null) {
				return false;
			}
		} else if (!oAuthAccessToken.equals(other.oAuthAccessToken)) {
			return false;
		}
		if (oAuthAccessTokenSecret == null) {
			if (other.oAuthAccessTokenSecret != null) {
				return false;
			}
		} else if (!oAuthAccessTokenSecret.equals(other.oAuthAccessTokenSecret)) {
			return false;
		}
		if (oAuthAccessTokenURL == null) {
			if (other.oAuthAccessTokenURL != null) {
				return false;
			}
		} else if (!oAuthAccessTokenURL.equals(other.oAuthAccessTokenURL)) {
			return false;
		}
		if (oAuthAuthenticationURL == null) {
			if (other.oAuthAuthenticationURL != null) {
				return false;
			}
		} else if (!oAuthAuthenticationURL.equals(other.oAuthAuthenticationURL)) {
			return false;
		}
		if (oAuthAuthorizationURL == null) {
			if (other.oAuthAuthorizationURL != null) {
				return false;
			}
		} else if (!oAuthAuthorizationURL.equals(other.oAuthAuthorizationURL)) {
			return false;
		}
		if (oAuthBaseURL == null) {
			if (other.oAuthBaseURL != null) {
				return false;
			}
		} else if (!oAuthBaseURL.equals(other.oAuthBaseURL)) {
			return false;
		}
		if (oAuthConsumerKey == null) {
			if (other.oAuthConsumerKey != null) {
				return false;
			}
		} else if (!oAuthConsumerKey.equals(other.oAuthConsumerKey)) {
			return false;
		}
		if (oAuthConsumerSecret == null) {
			if (other.oAuthConsumerSecret != null) {
				return false;
			}
		} else if (!oAuthConsumerSecret.equals(other.oAuthConsumerSecret)) {
			return false;
		}
		if (oAuthRequestTokenURL == null) {
			if (other.oAuthRequestTokenURL != null) {
				return false;
			}
		} else if (!oAuthRequestTokenURL.equals(other.oAuthRequestTokenURL)) {
			return false;
		}
		if (password == null) {
			if (other.password != null) {
				return false;
			}
		} else if (!password.equals(other.password)) {
			return false;
		}
		if (restBaseURL == null) {
			if (other.restBaseURL != null) {
				return false;
			}
		} else if (!restBaseURL.equals(other.restBaseURL)) {
			return false;
		}
		if (signingOAuthAccessTokenURL == null) {
			if (other.signingOAuthAccessTokenURL != null) {
				return false;
			}
		} else if (!signingOAuthAccessTokenURL.equals(other.signingOAuthAccessTokenURL)) {
			return false;
		}
		if (signingOAuthAuthenticationURL == null) {
			if (other.signingOAuthAuthenticationURL != null) {
				return false;
			}
		} else if (!signingOAuthAuthenticationURL.equals(other.signingOAuthAuthenticationURL)) {
			return false;
		}
		if (signingOAuthAuthorizationURL == null) {
			if (other.signingOAuthAuthorizationURL != null) {
				return false;
			}
		} else if (!signingOAuthAuthorizationURL.equals(other.signingOAuthAuthorizationURL)) {
			return false;
		}
		if (signingOAuthBaseURL == null) {
			if (other.signingOAuthBaseURL != null) {
				return false;
			}
		} else if (!signingOAuthBaseURL.equals(other.signingOAuthBaseURL)) {
			return false;
		}
		if (signingOAuthRequestTokenURL == null) {
			if (other.signingOAuthRequestTokenURL != null) {
				return false;
			}
		} else if (!signingOAuthRequestTokenURL.equals(other.signingOAuthRequestTokenURL)) {
			return false;
		}
		if (signingRestBaseURL == null) {
			if (other.signingRestBaseURL != null) {
				return false;
			}
		} else if (!signingRestBaseURL.equals(other.signingRestBaseURL)) {
			return false;
		}
		if (user == null) {
			if (other.user != null) {
				return false;
			}
		} else if (!user.equals(other.user)) {
			return false;
		}
		if (httpUserAgent == null) {
			if (other.httpUserAgent != null) {
				return false;
			}
		} else if (!httpUserAgent.equals(other.httpUserAgent)) {
			return false;
		}
		return true;
	}

	public final String getClientName() {
		return clientName;
	}

	public final String getClientURL() {
		return clientURL;
	}

	public final String getClientVersion() {
		return clientVersion;
	}

	public HostAddressResolver getHostAddressResolver() {
		return hostAddressResolver;
	}

	public HttpClientFactory getHttpClientFactory() {
		if (httpClientFactory == null) {
			return httpClientFactory = new BaseHttpClientFactory();
		}
		return httpClientFactory;
	}

	public final int getHttpRetryCount() {
		return httpRetryCount;
	}

	public final int getHttpRetryIntervalSeconds() {
		return httpRetryIntervalSeconds;
	}

	public String getOAuthAccessToken() {
		return oAuthAccessToken;
	}

	public String getOAuthAccessTokenSecret() {
		return oAuthAccessTokenSecret;
	}

	public String getOAuthAccessTokenURL() {
		return oAuthAccessTokenURL;
	}

	public String getOAuthAuthenticationURL() {
		return oAuthAuthenticationURL;
	}

	public String getOAuthAuthorizationURL() {
		return oAuthAuthorizationURL;
	}

	public String getOAuthBaseURL() {
		return oAuthBaseURL;
	}

	public final String getOAuthConsumerKey() {
		return oAuthConsumerKey;
	}

	public final String getOAuthConsumerSecret() {
		return oAuthConsumerSecret;
	}

	public String getOAuthRequestTokenURL() {
		return oAuthRequestTokenURL;
	}

	public final String getPassword() {
		return password;
	}

	public String getRestBaseURL() {
		return restBaseURL;
	}

	public String getSigningOAuthAccessTokenURL() {
		return signingOAuthAccessTokenURL != null ? signingOAuthAccessTokenURL : oAuthAccessTokenURL;
	}

	public String getSigningOAuthAuthenticationURL() {
		return signingOAuthAuthenticationURL != null ? signingOAuthAuthenticationURL : oAuthAuthenticationURL;
	}

	public String getSigningOAuthAuthorizationURL() {
		return signingOAuthAuthorizationURL != null ? signingOAuthAuthorizationURL : oAuthAuthorizationURL;
	}

	public String getSigningOAuthBaseURL() {
		return signingOAuthBaseURL != null ? signingOAuthBaseURL : oAuthBaseURL;
	}

	public String getSigningOAuthRequestTokenURL() {
		return signingOAuthRequestTokenURL != null ? signingOAuthRequestTokenURL : oAuthRequestTokenURL;
	}

	public String getSigningRestBaseURL() {
		return signingRestBaseURL != null ? signingRestBaseURL : restBaseURL;
	}

	public final String getUser() {
		return user;
	}

	public final String getHttpUserAgent() {
		return httpUserAgent;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (clientName == null ? 0 : clientName.hashCode());
		result = prime * result + (clientURL == null ? 0 : clientURL.hashCode());
		result = prime * result + (clientVersion == null ? 0 : clientVersion.hashCode());
		result = prime * result + (debug ? 1231 : 1237);
		result = prime * result + (gzipEnabled ? 1231 : 1237);
		result = prime * result + (hostAddressResolver == null ? 0 : hostAddressResolver.hashCode());
		result = prime * result + httpRetryCount;
		result = prime * result + httpRetryIntervalSeconds;
		result = prime * result + (sslErrorsIgnored ? 1231 : 1237);
		result = prime * result + (includeEntitiesEnabled ? 1231 : 1237);
		result = prime * result + (includeRTsEnabled ? 1231 : 1237);
		result = prime * result + (oAuthAccessToken == null ? 0 : oAuthAccessToken.hashCode());
		result = prime * result + (oAuthAccessTokenSecret == null ? 0 : oAuthAccessTokenSecret.hashCode());
		result = prime * result + (oAuthAccessTokenURL == null ? 0 : oAuthAccessTokenURL.hashCode());
		result = prime * result + (oAuthAuthenticationURL == null ? 0 : oAuthAuthenticationURL.hashCode());
		result = prime * result + (oAuthAuthorizationURL == null ? 0 : oAuthAuthorizationURL.hashCode());
		result = prime * result + (oAuthBaseURL == null ? 0 : oAuthBaseURL.hashCode());
		result = prime * result + (oAuthConsumerKey == null ? 0 : oAuthConsumerKey.hashCode());
		result = prime * result + (oAuthConsumerSecret == null ? 0 : oAuthConsumerSecret.hashCode());
		result = prime * result + (oAuthRequestTokenURL == null ? 0 : oAuthRequestTokenURL.hashCode());
		result = prime * result + (password == null ? 0 : password.hashCode());
		result = prime * result + (restBaseURL == null ? 0 : restBaseURL.hashCode());
		result = prime * result + (signingOAuthAccessTokenURL == null ? 0 : signingOAuthAccessTokenURL.hashCode());
		result = prime * result
				+ (signingOAuthAuthenticationURL == null ? 0 : signingOAuthAuthenticationURL.hashCode());
		result = prime * result + (signingOAuthAuthorizationURL == null ? 0 : signingOAuthAuthorizationURL.hashCode());
		result = prime * result + (signingOAuthBaseURL == null ? 0 : signingOAuthBaseURL.hashCode());
		result = prime * result + (signingOAuthRequestTokenURL == null ? 0 : signingOAuthRequestTokenURL.hashCode());
		result = prime * result + (signingRestBaseURL == null ? 0 : signingRestBaseURL.hashCode());
		result = prime * result + (user == null ? 0 : user.hashCode());
		result = prime * result + (httpUserAgent == null ? 0 : httpUserAgent.hashCode());
		return result;
	}

	public final boolean isDebugEnabled() {
		return debug;
	}

	public boolean isGZIPEnabled() {
		return gzipEnabled;
	}

	public boolean isIncludeEntitiesEnabled() {
		return includeEntitiesEnabled;
	}

	public boolean isIncludeRTsEnabled() {
		return includeRTsEnabled;
	}

	public final boolean isSSLErrorsIgnored() {
		return sslErrorsIgnored;
	}

	public void setHostAddressResolver(final HostAddressResolver resolver) {
		hostAddressResolver = resolver;
	}

	protected final void setHttpClientFacory(final HttpClientFactory httpClientFactory) {
		this.httpClientFactory = httpClientFactory;
	}

	protected final void setClientName(final String clientName) {
		this.clientName = clientName;
	}

	protected final void setClientURL(final String clientURL) {
		this.clientURL = clientURL;
	}

	protected final void setClientVersion(final String clientVersion) {
		this.clientVersion = clientVersion;
	}

	protected final void setDebug(final boolean debug) {
		this.debug = debug;
	}

	protected final void setGZIPEnabled(final boolean gzipEnabled) {
		this.gzipEnabled = gzipEnabled;
	}

	protected final void setHttpRetryCount(final int retryCount) {
		httpRetryCount = retryCount;
	}

	protected final void setHttpRetryIntervalSeconds(final int retryIntervalSeconds) {
		httpRetryIntervalSeconds = retryIntervalSeconds;
	}

	protected final void setSSLErrorsIgnored(final boolean sslErrorsIgnored) {
		this.sslErrorsIgnored = sslErrorsIgnored;
	}

	protected final void setIncludeEntitiesEnbled(final boolean enabled) {
		includeEntitiesEnabled = enabled;
	}

	protected final void setIncludeRTsEnbled(final boolean enabled) {
		includeRTsEnabled = enabled;
	}

	protected final void setOAuthAccessToken(final String oAuthAccessToken) {
		this.oAuthAccessToken = oAuthAccessToken;
	}

	protected final void setOAuthAccessTokenSecret(final String oAuthAccessTokenSecret) {
		this.oAuthAccessTokenSecret = oAuthAccessTokenSecret;
	}

	protected final void setOAuthBaseURL(String oAuthBaseURL) {
		if (isNullOrEmpty(oAuthBaseURL)) {
			oAuthBaseURL = DEFAULT_OAUTH_BASE_URL;
		}
		this.oAuthBaseURL = fixURLSlash(oAuthBaseURL);

		oAuthAccessTokenURL = oAuthBaseURL + PATH_SEGMENT_ACCESS_TOKEN;
		oAuthAuthenticationURL = oAuthBaseURL + PATH_SEGMENT_AUTHENTICATION;
		oAuthAuthorizationURL = oAuthBaseURL + PATH_SEGMENT_AUTHORIZATION;
		oAuthRequestTokenURL = oAuthBaseURL + PATH_SEGMENT_REQUEST_TOKEN;

		setSigningOAuthBaseURL(oAuthBaseURL);
		fixOAuthBaseURL();
	}

	protected final void setOAuthConsumerKey(final String oAuthConsumerKey) {
		this.oAuthConsumerKey = oAuthConsumerKey;
		fixRestBaseURL();
	}

	protected final void setOAuthConsumerSecret(final String oAuthConsumerSecret) {
		this.oAuthConsumerSecret = oAuthConsumerSecret;
		fixRestBaseURL();
	}

	protected final void setPassword(final String password) {
		this.password = password;
	}

	protected final void setRestBaseURL(String restBaseURL) {
		if (isNullOrEmpty(restBaseURL)) {
			restBaseURL = DEFAULT_REST_BASE_URL;
		}
		this.restBaseURL = fixURLSlash(restBaseURL);
		fixRestBaseURL();
	}

	protected final void setSigningOAuthBaseURL(String signingOAuthBaseURL) {
		if (isNullOrEmpty(signingOAuthBaseURL)) {
			signingOAuthBaseURL = DEFAULT_SIGNING_OAUTH_BASE_URL;
		}
		this.signingOAuthBaseURL = fixURLSlash(signingOAuthBaseURL);

		signingOAuthAccessTokenURL = signingOAuthBaseURL + PATH_SEGMENT_ACCESS_TOKEN;
		signingOAuthAuthenticationURL = signingOAuthBaseURL + PATH_SEGMENT_AUTHENTICATION;
		signingOAuthAuthorizationURL = signingOAuthBaseURL + PATH_SEGMENT_AUTHORIZATION;
		signingOAuthRequestTokenURL = signingOAuthBaseURL + PATH_SEGMENT_REQUEST_TOKEN;

		fixOAuthBaseURL();
	}

	protected final void setSigningRestBaseURL(String signingRestBaseURL) {
		if (isNullOrEmpty(signingRestBaseURL)) {
			signingRestBaseURL = DEFAULT_SIGNING_REST_BASE_URL;
		}
		this.signingRestBaseURL = fixURLSlash(signingRestBaseURL);
		fixRestBaseURL();
	}

	protected final void setUser(final String user) {
		this.user = user;
	}

	protected final void setHttpUserAgent(final String userAgent) {
		this.httpUserAgent = userAgent;
	}

	private void fixOAuthBaseURL() {
		if (DEFAULT_OAUTH_BASE_URL.equals(fixURL(oAuthBaseURL))) {
			oAuthBaseURL = fixURL(oAuthBaseURL);
		}
		if (oAuthBaseURL != null && oAuthBaseURL.equals(fixURL(signingOAuthBaseURL))) {
			signingOAuthBaseURL = fixURL(signingOAuthBaseURL);
		}
		if (oAuthBaseURL != null
				&& (oAuthBaseURL + PATH_SEGMENT_ACCESS_TOKEN).equals(fixURL(oAuthAccessTokenURL))) {
			oAuthAccessTokenURL = fixURL(oAuthAccessTokenURL);
		}
		if (oAuthBaseURL != null
				&& (oAuthBaseURL + PATH_SEGMENT_AUTHENTICATION).equals(fixURL(oAuthAuthenticationURL))) {
			oAuthAuthenticationURL = fixURL(oAuthAuthenticationURL);
		}
		if (oAuthBaseURL != null
				&& (oAuthBaseURL + PATH_SEGMENT_AUTHORIZATION).equals(fixURL(oAuthAuthorizationURL))) {
			oAuthAuthorizationURL = fixURL(oAuthAuthorizationURL);
		}
		if (oAuthBaseURL != null
				&& (oAuthBaseURL + PATH_SEGMENT_REQUEST_TOKEN).equals(fixURL(oAuthRequestTokenURL))) {
			oAuthRequestTokenURL = fixURL(oAuthRequestTokenURL);
		}
		if (signingOAuthBaseURL != null
				&& (signingOAuthBaseURL + PATH_SEGMENT_ACCESS_TOKEN).equals(fixURL(
				signingOAuthAccessTokenURL))) {
			signingOAuthAccessTokenURL = fixURL(signingOAuthAccessTokenURL);
		}
		if (signingOAuthBaseURL != null
				&& (signingOAuthBaseURL + PATH_SEGMENT_ACCESS_TOKEN).equals(fixURL(
				signingOAuthAuthenticationURL))) {
			signingOAuthAuthenticationURL = fixURL(signingOAuthAuthenticationURL);
		}
		if (signingOAuthBaseURL != null
				&& (signingOAuthBaseURL + PATH_SEGMENT_ACCESS_TOKEN).equals(fixURL(
				signingOAuthAuthorizationURL))) {
			signingOAuthAuthorizationURL = fixURL(signingOAuthAuthorizationURL);
		}
		if (signingOAuthBaseURL != null
				&& (signingOAuthBaseURL + PATH_SEGMENT_ACCESS_TOKEN).equals(fixURL(
				signingOAuthRequestTokenURL))) {
			signingOAuthRequestTokenURL = fixURL(signingOAuthRequestTokenURL);
		}
	}

	private void fixRestBaseURL() {
		if (DEFAULT_REST_BASE_URL.equals(fixURL(restBaseURL))) {
			restBaseURL = fixURL(restBaseURL);
		}
		if (restBaseURL != null && restBaseURL.equals(fixURL(signingRestBaseURL))) {
			signingRestBaseURL = fixURL(signingRestBaseURL);
		}
	}

	static String fixURL(String url) {
		if (null == url) {
			return null;
		}
		if (!url.startsWith("http://") || !url.startsWith("https://")) {
			url = "https://" + url;
		}
		final int index = url.indexOf("://");
		if (-1 == index) {
			throw new IllegalArgumentException("url should contain '://'");
		}
		final String hostAndLater = url.substring(index + 3);
		if (url.startsWith("https://")) {
			return "https://" + hostAndLater;
		} else {
			return "http://" + hostAndLater;
		}
	}

	static String fixURLSlash(final String urlOrig) {
		if (urlOrig == null) {
			return null;
		}
		if (!urlOrig.endsWith("/")) {
			return urlOrig + "/";
		}
		return urlOrig;
	}

	static boolean isNullOrEmpty(final String string) {
		if (string == null) {
			return true;
		}
		if (string.length() == 0) {
			return true;
		}
		return false;
	}

}