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

import twitter2me.auth.Authorization;
import twitter2me.http.HttpRequest;

/**
 * An interface represents credentials.
 * 
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public class NullAuthorization implements Authorization {

	private static NullAuthorization SINGLETON = new NullAuthorization();

	private NullAuthorization() {
	}

	public boolean equals(final Object o) {
		return SINGLETON == o;
	}

	public String getAuthorizationHeader(final HttpRequest req) {
		return null;
	}

	public boolean isEnabled() {
		return false;
	}

	public String toString() {
		return "NullAuthentication{SINGLETON}";
	}

	public static NullAuthorization getInstance() {
		return SINGLETON;
	}
}
