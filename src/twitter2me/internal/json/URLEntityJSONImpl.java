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

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import twitter2me.TwitterException;
import twitter2me.URLEntity;
import twitter2me.internal.util.InternalStringUtil;

/**
 * A data class representing one single URL entity.
 * 
 * @author Mocel - mocel at guma.jp
 * @since twitter2me 2.1.9
 */
/* package */
final class URLEntityJSONImpl implements URLEntity {

	private int start = -1;
	private int end = -1;
	private String url;
	private String expandedURL;
	private String displayURL;

	/* For serialization purposes only. */
	/* package */
	URLEntityJSONImpl() {
	}

	/* package */
	URLEntityJSONImpl(final int start, final int end, final String url, final String expandedURL,
			final String displayURL) {
		super();
		this.start = start;
		this.end = end;
		this.url = url;
		this.expandedURL = expandedURL;
		this.displayURL = displayURL;
	}

	/* package */
	URLEntityJSONImpl(final JSONObject json) throws TwitterException {
		super();
		init(json);
	}

	public boolean equals(final Object o) {
		if (!(o instanceof URLEntity)) {
			return false;
		}
		final URLEntity that = (URLEntity) o;
		if (end != that.getEnd()) {
			return false;
		}
		if (start != that.getStart()) {
			return false;
		}
		if (!InternalStringUtil.equalsIgnoreCase(displayURL, that.getDisplayURL())) {
			return false;
		}
		if (!InternalStringUtil.equalsIgnoreCase(expandedURL, that.getExpandedURL())) {
			return false;
		}
		if (!InternalStringUtil.equalsIgnoreCase(url, that.getURL())) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDisplayURL() {
		return displayURL;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getExpandedURL() {
		return expandedURL;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getStart() {
		return start;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getURL() {
		return url;
	}

	public int hashCode() {
		int result = start;
		result = 31 * result + end;
		result = 31 * result + (url != null ? url.toString().hashCode() : 0);
		result = 31 * result + (expandedURL != null ? expandedURL.toString().hashCode() : 0);
		result = 31 * result + (displayURL != null ? displayURL.hashCode() : 0);
		return result;
	}

	public String toString() {
		return "URLEntityJSONImpl{" + "start=" + start + ", end=" + end + ", url=" + url + ", expandedURL="
				+ expandedURL + ", displayURL=" + displayURL + '}';
	}

	private void init(final JSONObject json) throws TwitterException {
		try {
			final JSONArray indicesArray = json.getJSONArray("indices");
			start = Integer.parseInt(indicesArray.getString(0));
			end = Integer.parseInt(indicesArray.getString(1));
			url = json.optString("url");
			expandedURL = json.optString("expanded_url");
			displayURL = json.optString("display_url");
		} catch (final JSONException jsone) {
			throw new TwitterException(jsone);
		}
	}
}
