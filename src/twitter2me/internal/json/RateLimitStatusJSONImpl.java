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

import java.util.Enumeration;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import repackaged.java.util.Collections;
import repackaged.java.util.HashMap;
import repackaged.java.util.Iterator;
import repackaged.java.util.Map;
import twitter2me.RateLimitStatus;
import twitter2me.TwitterException;
import twitter2me.conf.Configuration;
import twitter2me.http.HttpResponse;
import twitter2me.internal.util.InternalParseUtil;

/**
 * A data class representing Twitter REST API's rate limit status
 * 
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @see <a href="https://dev.twitter.com/docs/rate-limiting">Rate Limiting |
 *      Twitter Developers</a>
 */
/* package */final class RateLimitStatusJSONImpl implements RateLimitStatus {

	private int remaining;
	private int limit;
	private int resetTimeInSeconds;
	private int secondsUntilReset;

	private RateLimitStatusJSONImpl(final int limit, final int remaining, final int resetTimeInSeconds) {
		this.limit = limit;
		this.remaining = remaining;
		this.resetTimeInSeconds = resetTimeInSeconds;
		secondsUntilReset = (int) ((resetTimeInSeconds * 1000L - System.currentTimeMillis()) / 1000);
	}

	RateLimitStatusJSONImpl(final JSONObject json) throws TwitterException {
		init(json);
	}

	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final RateLimitStatusJSONImpl that = (RateLimitStatusJSONImpl) o;

		if (limit != that.limit) return false;
		if (remaining != that.remaining) return false;
		if (resetTimeInSeconds != that.resetTimeInSeconds) return false;
		if (secondsUntilReset != that.secondsUntilReset) return false;

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getLimit() {
		return limit;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getRemaining() {
		return remaining;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getRemainingHits() {
		return getRemaining();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getResetTimeInSeconds() {
		return resetTimeInSeconds;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getSecondsUntilReset() {
		return secondsUntilReset;
	}

	public int hashCode() {
		int result = remaining;
		result = 31 * result + limit;
		result = 31 * result + resetTimeInSeconds;
		result = 31 * result + secondsUntilReset;
		return result;
	}

	public String toString() {
		return "RateLimitStatusJSONImpl{" + "remaining=" + remaining + ", limit=" + limit + ", resetTimeInSeconds="
				+ resetTimeInSeconds + ", secondsUntilReset=" + secondsUntilReset + '}';
	}

	void init(final JSONObject json) throws TwitterException {
		limit = InternalParseUtil.getInt("limit", json);
		remaining = InternalParseUtil.getInt("remaining", json);
		resetTimeInSeconds = InternalParseUtil.getInt("reset", json);
		secondsUntilReset = (int) ((resetTimeInSeconds * 1000L - System.currentTimeMillis()) / 1000);
	}

	static RateLimitStatus createFromResponseHeader(final HttpResponse res) {
		if (null == res) return null;
		int remainingHits;// "X-Rate-Limit-Remaining"
		int limit;// "X-Rate-Limit-Limit"
		int resetTimeInSeconds;// not included in the response header. Need to
								// be calculated.

		final String strLimit = res.getResponseHeader("X-Rate-Limit-Limit");
		if (strLimit != null) {
			limit = Integer.parseInt(strLimit);
		} else
			return null;
		final String remaining = res.getResponseHeader("X-Rate-Limit-Remaining");
		if (remaining != null) {
			remainingHits = Integer.parseInt(remaining);
		} else
			return null;
		final String reset = res.getResponseHeader("X-Rate-Limit-Reset");
		if (reset != null) {
			final long longReset = Long.parseLong(reset);
			resetTimeInSeconds = (int) longReset;
		} else
			return null;
		return new RateLimitStatusJSONImpl(limit, remainingHits, resetTimeInSeconds);
	}

	static Map createRateLimitStatuses(final HttpResponse res, final Configuration conf)
			throws TwitterException {
		final JSONObject json = res.asJSONObject();
		final Map map = createRateLimitStatuses(json);
		return map;
	}

	static Map createRateLimitStatuses(final JSONObject json) throws TwitterException {
		final Map map = new HashMap();
		try {
			final JSONObject resources = json.getJSONObject("resources");
			final Enumeration resourceKeys = resources.keys();
			while (resourceKeys.hasMoreElements()) {
				final JSONObject resource = resources.getJSONObject((String) resourceKeys.nextElement());
				final Enumeration endpointKeys = resource.keys();
				while (endpointKeys.hasMoreElements()) {
					final String endpoint = (String) endpointKeys.nextElement();
					final JSONObject rateLimitStatusJSON = resource.getJSONObject(endpoint);
					final RateLimitStatus rateLimitStatus = new RateLimitStatusJSONImpl(rateLimitStatusJSON);
					map.put(endpoint, rateLimitStatus);
				}
			}
			return Collections.unmodifiableMap(map);
		} catch (final JSONException jsone) {
			throw new TwitterException(jsone);
		}
	}

}
