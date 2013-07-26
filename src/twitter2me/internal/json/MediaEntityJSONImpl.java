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
import repackaged.java.util.HashMap;
import repackaged.java.util.Map;
import twitter2me.MediaEntity;
import twitter2me.TwitterException;

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.2.3
 */
public class MediaEntityJSONImpl implements MediaEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1634113112942821363L;
	private long id;
	private int start = -1;
	private int end = -1;
	private String url;
	private String mediaURL;
	private String mediaURLHttps;
	private String expandedURL;
	private String displayURL;
	private Map sizes;
	private String type;

	public MediaEntityJSONImpl(final JSONObject json) throws TwitterException {
		try {
			final JSONArray indicesArray = json.getJSONArray("indices");
			start = Integer.parseInt(indicesArray.getString(0));
			end = Integer.parseInt(indicesArray.getString(1));
			id = json.optLong("id");
			url = json.optString("url");
			expandedURL = json.optString("expanded_url");
			mediaURL = json.optString("media_url");
			mediaURLHttps = json.optString("media_url_https");
			displayURL = json.optString("display_url");
			final JSONObject sizes = json.optJSONObject("sizes");
			if (sizes != null) {
				this.sizes = new HashMap(4);
				// thumbworkarounding API side issue
				addMediaEntitySizeIfNotNull(this.sizes, sizes, Size.LARGE, "large");
				addMediaEntitySizeIfNotNull(this.sizes, sizes, Size.MEDIUM, "medium");
				addMediaEntitySizeIfNotNull(this.sizes, sizes, Size.SMALL, "small");
				addMediaEntitySizeIfNotNull(this.sizes, sizes, Size.THUMB, "thumb");
			}
			type = json.optString("type");
		} catch (final JSONException jsone) {
			throw new TwitterException(jsone);
		}
	}

	/* For serialization purposes only. */
	/* package */
	MediaEntityJSONImpl() {
	}

	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof MediaEntityJSONImpl)) {
			return false;
		}

		final MediaEntityJSONImpl that = (MediaEntityJSONImpl) o;

		if (id != that.id) {
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
	public long getId() {
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getMediaURL() {
		return mediaURL;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getMediaURLHttps() {
		return mediaURLHttps;
	}

	public Map getSizes() {
		return sizes;
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
	public String getType() {
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getURL() {
		return url;
	}

	public int hashCode() {
		return (int) (id ^ id >>> 32);
	}

	public String toString() {
		return "MediaEntityJSONImpl{" + "id=" + id + ", start=" + start + ", end=" + end + ", url=" + url
				+ ", mediaURL=" + mediaURL + ", mediaURLHttps=" + mediaURLHttps + ", expandedURL=" + expandedURL
				+ ", displayURL='" + displayURL + '\'' + ", sizes=" + sizes + ", type=" + type + '}';
	}

	private void addMediaEntitySizeIfNotNull(final Map sizes, final JSONObject sizes_json,
			final int size, final String key) throws JSONException {
		final JSONObject size_json = sizes_json.optJSONObject(key);
		if (size_json != null) {
			sizes.put(new Integer(size), new SizeImpl(size_json));
		}
	}

	static class SizeImpl implements Size {

		int width;
		int height;
		int resize;

		SizeImpl(final JSONObject json) throws JSONException {
			width = json.getInt("w");
			height = json.getInt("h");
			resize = "fit".equals(json.getString("resize")) ? MediaEntity.Size.FIT : MediaEntity.Size.CROP;
		}

		public boolean equals(final Object o) {
			if (!(o instanceof Size)) {
				return false;
			}
			final Size size = (Size) o;
			if (height != size.getHeight()) {
				return false;
			}
			if (resize != size.getResize()) {
				return false;
			}
			if (width != size.getWidth()) {
				return false;
			}

			return true;
		}

		public int getHeight() {
			return height;
		}

		public int getResize() {
			return resize;
		}

		public int getWidth() {
			return width;
		}

		public int hashCode() {
			int result = width;
			result = 31 * result + height;
			result = 31 * result + resize;
			return result;
		}

		public String toString() {
			return "Size{" + "width=" + width + ", height=" + height + ", resize=" + resize + '}';
		}
	}
}
