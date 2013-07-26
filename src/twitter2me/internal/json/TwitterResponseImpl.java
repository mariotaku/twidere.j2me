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
package twitter2me.internal.json;

import java.util.Date;
import org.json.me.JSONObject;
import twitter2me.RateLimitStatus;
import twitter2me.TwitterException;
import twitter2me.TwitterResponse;
import twitter2me.http.HttpResponse;
import twitter2me.internal.util.InternalParseUtil;

/**
 * Super interface of Twitter Response data interfaces which indicates that rate
 * limit status is available.
 * 
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @see twitter4j.DirectMessage
 * @see twitter4j.Status
 * @see twitter4j.User
 */
/* package */
abstract class TwitterResponseImpl implements TwitterResponse {

	private transient RateLimitStatus rateLimitStatus = null;
	private transient int accessLevel;

	public TwitterResponseImpl() {
		accessLevel = NONE;
	}

	public TwitterResponseImpl(final HttpResponse res) {
		rateLimitStatus = RateLimitStatusJSONImpl.createFromResponseHeader(res);
		accessLevel = InternalParseUtil.toAccessLevel(res);
	}

	/**
	 * {@inheritDoc}
	 */
	public int getAccessLevel() {
		return accessLevel;
	}

	/**
	 * {@inheritDoc}
	 */
	public RateLimitStatus getRateLimitStatus() {
		return rateLimitStatus;
	}

	public String toString() {
		return "TwitterResponseImpl{" + "rateLimitStatus=" + rateLimitStatus + ", accessLevel=" + accessLevel + '}';
	}

	protected static long getLong(String name, JSONObject json) {
		return InternalParseUtil.getLong(name, json);
	}

	protected static int getInt(String name, JSONObject json) {
		return InternalParseUtil.getInt(name, json);
	}

	protected static Date getDate(String name, JSONObject json) throws TwitterException {
		return InternalParseUtil.getDate(name, json);
	}

	protected static boolean getBoolean(String name, JSONObject json) {
		return InternalParseUtil.getBoolean(name, json);
	}

	protected static String getUnescapedString(String name, JSONObject json) {
		return InternalParseUtil.getUnescapedString(name, json);
	}

	protected static String getHTMLUnescapedString(String name, JSONObject json) {
		return getUnescapedString(name, json);
	}

	protected static String getRawString(String name, JSONObject json) {
		return InternalParseUtil.getRawString(name, json);
	}
	
	protected static int getDouble(final String name, final JSONObject json) {
		return InternalParseUtil.getDouble(name, json);
	}
}
