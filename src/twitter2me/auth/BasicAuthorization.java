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

import twitter2me.http.HttpRequest;
import twitter2me.internal.util.InternalStringUtil;

/**
 * An authentication implementation implements Basic authentication
 * 
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public class BasicAuthorization implements Authorization {

	private final String userId;
	private final String password;
	private final String basic;

	public BasicAuthorization(final String userId, final String password) {
		this.userId = userId;
		this.password = password;
		basic = encodeBasicAuthenticationString();
	}

	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof BasicAuthorization)) {
			return false;
		}

		final BasicAuthorization that = (BasicAuthorization) o;

		return basic.equals(that.basic);

	}

	public String getAuthorizationHeader(final HttpRequest req) {
		return basic;
	}

	public String getPassword() {
		return password;
	}

	public String getUserId() {
		return userId;
	}

	public int hashCode() {
		return basic.hashCode();
	}

	/**
	 * #{inheritDoc}
	 */
	public boolean isEnabled() {
		return true;
	}

	public String toString() {
		return "BasicAuthorization{" + "userId=" + userId + ", password=" + InternalStringUtil.mask(password) + '}';
	}

	private String encodeBasicAuthenticationString() {
		if (userId != null && password != null) {
			return "Basic " + BASE64Encoder.encode((userId + ":" + password).getBytes());
		}
		return null;
	}
}
