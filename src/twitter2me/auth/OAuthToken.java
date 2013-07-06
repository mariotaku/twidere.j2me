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

import twitter2me.TwitterException;
import twitter2me.http.HttpResponse;
import twitter2me.internal.util.InternalStringUtil;

abstract class OAuthToken {

	private final String token;
	private final String tokenSecret;

	String[] responseStr = null;

	public OAuthToken(final String token, final String tokenSecret) {
		this.token = token;
		this.tokenSecret = tokenSecret;
	}

	OAuthToken(final HttpResponse response) throws TwitterException {
		this(response.asString());
	}

	OAuthToken(final String string) {
		responseStr = InternalStringUtil.split(string, "&");
		tokenSecret = getParameter("oauth_token_secret");
		token = getParameter("oauth_token");
	}

	public boolean equals(final Object o) {
		if (this == o) return true;
		if (!(o instanceof OAuthToken)) return false;

		final OAuthToken that = (OAuthToken) o;

		if (!token.equals(that.token)) return false;
		if (!tokenSecret.equals(that.tokenSecret)) return false;

		return true;
	}

	public String getParameter(final String parameter) {
		String value = null;
		final int length = responseStr.length;
		for (int i = 0; i < length; i++) {
			final String str = responseStr[i];
			if (str.startsWith(parameter + '=')) {
				value = InternalStringUtil.split(str, "=")[1].trim();
				break;
			}
		}
		return value;
	}

	public String getToken() {
		return token;
	}

	public String getTokenSecret() {
		return tokenSecret;
	}

	public int hashCode() {
		int result = token.hashCode();
		result = 31 * result + tokenSecret.hashCode();
		return result;
	}

	public String toString() {
		return "OAuthToken{" + "token='" + token + '\'' + ", tokenSecret='" + tokenSecret + '\'' + '}';
	}
}
