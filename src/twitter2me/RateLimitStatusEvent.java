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

/**
 * A data interface representing Twitter REST API's rate limit status
 * 
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @see <a href="https://dev.twitter.com/docs/rate-limiting">Rate Limiting |
 *      Twitter Developers</a>
 */
public final class RateLimitStatusEvent {

	private static final long serialVersionUID = 3749366911109722414L;

	private final Object source;
	private final RateLimitStatus rateLimitStatus;
	private final boolean isAccountRateLimitStatus;

	RateLimitStatusEvent(final Object source, final RateLimitStatus rateLimitStatus,
			final boolean isAccountRateLimitStatus) {
		this.source = source;
		this.rateLimitStatus = rateLimitStatus;
		this.isAccountRateLimitStatus = isAccountRateLimitStatus;
	}

	public RateLimitStatus getRateLimitStatus() {
		return rateLimitStatus;
	}
	
	public Object getSource() {
		return source;
	}

	public boolean isAccountRateLimitStatus() {
		return isAccountRateLimitStatus;
	}

	public boolean isIPRateLimitStatus() {
		return !isAccountRateLimitStatus;
	}

}
