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

/**
 * Representing authorized Access Token which is passed to the service provider
 * in order to access protected resources.<br>
 * the token and token secret can be stored into some persistent stores such as
 * file system or RDBMS for the further accesses.
 * 
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public class AccessToken extends OAuthToken {
	private String screenName;
	private long userId;

	public AccessToken(final String token, final String tokenSecret) {
		super(token, tokenSecret);
		String sUserId;
		try {
			sUserId = token.substring(0, token.indexOf("-"));
		} catch (final IndexOutOfBoundsException e) {
			throw new IllegalArgumentException("Invalid access token format.");
		}
		if (sUserId != null) {
			userId = Long.parseLong(sUserId);
		}
	}

	AccessToken(final HttpResponse res) throws TwitterException {
		this(res.asString());
	}

	AccessToken(final String str) {
		super(str);
		screenName = getParameter("screen_name");
		final String sUserId = getParameter("user_id");
		if (sUserId != null) {
			userId = Long.parseLong(sUserId);
		}
	}

	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		final AccessToken that = (AccessToken) o;

		if (userId != that.userId) return false;
		if (screenName != null ? !screenName.equals(that.screenName) : that.screenName != null) return false;

		return true;
	}

	/**
	 * @return screen name
	 * @since twitter2me 2.0.4
	 */

	public String getScreenName() {
		return screenName;
	}

	/**
	 * @return user id
	 * @since twitter2me 2.0.4
	 */

	public long getUserId() {
		return userId;
	}

	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (screenName != null ? screenName.hashCode() : 0);
		result = 31 * result + (int) (userId ^ userId >>> 32);
		return result;
	}

	public String toString() {
		return "AccessToken{" + "screenName='" + screenName + '\'' + ", userId=" + userId + '}';
	}
}
