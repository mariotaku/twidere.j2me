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

import twitter2me.auth.AccessToken;
import twitter2me.auth.Authorization;
import twitter2me.auth.AuthorizationFactory;
import twitter2me.auth.OAuthAuthorization;
import twitter2me.conf.Configuration;
import twitter2me.conf.ConfigurationContext;

/**
 * A factory class for Twitter. <br>
 * An instance of this class is completely thread safe and can be re-used and
 * used concurrently.
 * 
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since twitter2me 2.1.0
 */
public final class TwitterFactory {
	/* AsyncTwitterFactory and TWitterStream will access this field */

	static final Authorization DEFAULT_AUTHORIZATION =
			AuthorizationFactory.getInstance(ConfigurationContext.getInstance());
	private static final Twitter SINGLETON;
	private final Configuration conf;

	static {
		SINGLETON = new TwitterImpl(ConfigurationContext.getInstance(), DEFAULT_AUTHORIZATION);
	}

	/**
	 * Creates a TwitterFactory with the root configuration.
	 */
	public TwitterFactory() {
		this(ConfigurationContext.getInstance());
	}

	/**
	 * Creates a TwitterFactory with the given configuration.
	 * 
	 * @param conf the configuration to use
	 * @since twitter2me 2.1.1
	 */
	public TwitterFactory(final Configuration conf) {
		if (conf == null) {
			throw new NullPointerException("configuration cannot be null");
		}
		this.conf = conf;
	}

	/**
	 * Returns a instance associated with the configuration bound to this
	 * factory.
	 * 
	 * @return default singleton instance
	 */
	public Twitter getInstance() {
		return getInstance(AuthorizationFactory.getInstance(conf));
	}

	/**
	 * Returns a OAuth Authenticated instance.<br>
	 * consumer key and consumer Secret must be provided by
	 * twitter2me.properties, or system properties.<br>
	 * Unlike {@link Twitter#setOAuthAccessToken(twitter2me.auth.AccessToken)} ,
	 * this factory method potentially returns a cached instance.
	 * 
	 * @param accessToken access token
	 * @return an instance
	 * @since twitter2me 2.1.9
	 */
	public Twitter getInstance(final AccessToken accessToken) {
		final String consumerKey = conf.getOAuthConsumerKey();
		final String consumerSecret = conf.getOAuthConsumerSecret();
		if (null == consumerKey && null == consumerSecret) {
			throw new IllegalStateException("Consumer key and Consumer secret not supplied.");
		}
		final OAuthAuthorization oauth = new OAuthAuthorization(conf);
		oauth.setOAuthAccessToken(accessToken);
		return getInstance(oauth);
	}

	public Twitter getInstance(final Authorization auth) {
		return new TwitterImpl(conf, auth);
	}

	/**
	 * Returns default singleton Twitter instance.
	 * 
	 * @return default singleton Twitter instance
	 * @since twitter2me 2.2.4
	 */
	public static Twitter getSingleton() {
		return SINGLETON;
	}
}
