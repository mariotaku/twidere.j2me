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
package twitter2me.conf;

import twitter2me.http.HostAddressResolver;
import twitter2me.http.HttpClientFactory;

/**
 * A builder that can be used to construct a twitter4j configuration with
 * desired settings. This builder has sensible defaults such that
 * {@code new ConfigurationBuilder().build()} would create a usable
 * configuration. This configuration builder is useful for clients that wish to
 * configure twitter4j in unit tests or from command line flags for example.
 * 
 * @author John Sirois - john.sirois at gmail.com
 */
public final class ConfigurationBuilder {

	private ConfigurationBase configuration = new ConfigurationBase();

	public Configuration build() {
		checkNotBuilt();
		try {
			return configuration;
		} finally {
			configuration = null;
		}
	}

	public ConfigurationBuilder setClientName(final String clientName) {
		checkNotBuilt();
		configuration.setClientName(clientName);
		return this;
	}

	public ConfigurationBuilder setClientURL(final String clientURL) {
		checkNotBuilt();
		configuration.setClientURL(clientURL);
		return this;
	}

	public ConfigurationBuilder setClientVersion(final String clientVersion) {
		checkNotBuilt();
		configuration.setClientVersion(clientVersion);
		return this;
	}

	public ConfigurationBuilder setDebugEnabled(final boolean debugEnabled) {
		checkNotBuilt();
		configuration.setDebug(debugEnabled);
		return this;
	}

	public ConfigurationBuilder setGZIPEnabled(final boolean gzipEnabled) {
		checkNotBuilt();
		configuration.setGZIPEnabled(gzipEnabled);
		return this;
	}

	public ConfigurationBuilder setHostAddressResolver(final HostAddressResolver resolver) {
		checkNotBuilt();
		configuration.setHostAddressResolver(resolver);
		return this;
	}

	public ConfigurationBuilder setHttpClientFactory(final HttpClientFactory httpClientFactory) {
		checkNotBuilt();
		configuration.setHttpClientFacory(httpClientFactory);
		return this;
	}

	public ConfigurationBuilder setHttpRetryCount(final int httpRetryCount) {
		checkNotBuilt();
		configuration.setHttpRetryCount(httpRetryCount);
		return this;
	}

	public ConfigurationBuilder setHttpRetryIntervalSeconds(final int httpRetryIntervalSeconds) {
		checkNotBuilt();
		configuration.setHttpRetryIntervalSeconds(httpRetryIntervalSeconds);
		return this;
	}

	public ConfigurationBuilder setSSLErrorsIgnored(final boolean sslErrorsIgnored) {
		checkNotBuilt();
		configuration.setSSLErrorsIgnored(sslErrorsIgnored);
		return this;
	}

	public ConfigurationBuilder setExcludeRepliesEnabled(final boolean enabled) {
		checkNotBuilt();
		configuration.setExcludeRepliesEnabled(enabled);
		return this;
	}

	public ConfigurationBuilder setIncludeEntitiesEnabled(final boolean enabled) {
		checkNotBuilt();
		configuration.setIncludeEntitiesEnbled(enabled);
		return this;
	}

	public ConfigurationBuilder setIncludeRTsEnabled(final boolean enabled) {
		checkNotBuilt();
		configuration.setIncludeRTsEnbled(enabled);
		return this;
	}

	public ConfigurationBuilder setOAuthAccessToken(final String oAuthAccessToken) {
		checkNotBuilt();
		configuration.setOAuthAccessToken(oAuthAccessToken);
		return this;
	}

	public ConfigurationBuilder setOAuthAccessTokenSecret(final String oAuthAccessTokenSecret) {
		checkNotBuilt();
		configuration.setOAuthAccessTokenSecret(oAuthAccessTokenSecret);
		return this;
	}

	public ConfigurationBuilder setOAuthBaseURL(final String oAuthBaseURL) {
		checkNotBuilt();
		configuration.setOAuthBaseURL(oAuthBaseURL);
		return this;
	}

	public ConfigurationBuilder setOAuthConsumerKey(final String oAuthConsumerKey) {
		checkNotBuilt();
		configuration.setOAuthConsumerKey(oAuthConsumerKey);
		return this;
	}

	public ConfigurationBuilder setOAuthConsumerSecret(final String oAuthConsumerSecret) {
		checkNotBuilt();
		configuration.setOAuthConsumerSecret(oAuthConsumerSecret);
		return this;
	}

	public ConfigurationBuilder setPassword(final String password) {
		checkNotBuilt();
		configuration.setPassword(password);
		return this;
	}

	public ConfigurationBuilder setRestBaseURL(final String restBaseURL) {
		checkNotBuilt();
		configuration.setRestBaseURL(restBaseURL);
		return this;
	}

	public ConfigurationBuilder setSigningOAuthBaseURL(final String signingOAuthBaseURL) {
		checkNotBuilt();
		configuration.setSigningOAuthBaseURL(signingOAuthBaseURL);
		return this;
	}

	public ConfigurationBuilder setSigningRestBaseURL(final String signingRestBaseURL) {
		checkNotBuilt();
		configuration.setSigningRestBaseURL(signingRestBaseURL);
		return this;
	}

	public ConfigurationBuilder setUser(final String user) {
		checkNotBuilt();
		configuration.setUser(user);
		return this;
	}

	public ConfigurationBuilder setHttpUserAgent(final String userAgent) {
		checkNotBuilt();
		configuration.setHttpUserAgent(userAgent);
		return this;
	}

	private void checkNotBuilt() {
		if (configuration == null) {
			throw new IllegalStateException("Cannot use this builder any longer, build() has already been called");
		}
	}
}
