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

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import repackaged.java.util.Arrays;
import twitter2me.GeoLocation;
import twitter2me.HashtagEntity;
import twitter2me.MediaEntity;
import twitter2me.Place;
import twitter2me.ResponseList;
import twitter2me.Status;
import twitter2me.TwitterException;
import twitter2me.URLEntity;
import twitter2me.User;
import twitter2me.UserMentionEntity;
import twitter2me.conf.Configuration;
import twitter2me.http.HttpResponse;
import twitter2me.internal.logging.Logger;

/**
 * A data class representing one single status of a user.
 * 
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
final class StatusJSONImpl extends TwitterResponseImpl implements Status {

	private static final Logger logger = Logger.getLogger("StatusJSONImpl");
	private Date createdAt;
	private long id;
	private String text;
	private String rawText;
	private String source;
	private boolean isTruncated;
	private long inReplyToStatusId;
	private long inReplyToUserId;
	private long currentUserRetweet;
	private boolean isFavorited;
	private String inReplyToScreenName;
	private GeoLocation geoLocation = null;
	private Place place = null;
	private long retweetCount;
	private boolean wasRetweetedByMe;
	private boolean isPossiblySensitive;
	private String[] contributors = null;
	private long[] contributorsIDs;
	private Status retweetedStatus;
	private UserMentionEntity[] userMentionEntities;
	private URLEntity[] urlEntities;
	private HashtagEntity[] hashtagEntities;
	private MediaEntity[] mediaEntities;
	private User user = null;

	/* package */
	StatusJSONImpl(final HttpResponse res, final Configuration conf) throws TwitterException {
		super(res);
		final JSONObject json = res.asJSONObject();
		init(json);
	}

	/* package */
	StatusJSONImpl(final JSONObject json) throws TwitterException {
		super();
		init(json);
	}

	public int compareTo(final Status that) {
		final long delta = id - that.getId();
		if (delta < Integer.MIN_VALUE) {
			return Integer.MIN_VALUE;
		} else if (delta > Integer.MAX_VALUE) {
			return Integer.MAX_VALUE;
		}
		return (int) delta;
	}

	public boolean equals(final Object obj) {
		if (null == obj) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		return obj instanceof Status && ((Status) obj).getId() == id;
	}

	/**
	 * {@inheritDoc}
	 */
	public long[] getContributors() {
		if (contributors != null) {
			// http://twitter2me.org/jira/browse/TFJ-592
			// preserving serialized form compatibility with older versions
			contributorsIDs = new long[contributors.length];
			for (int i = 0; i < contributors.length; i++) {
				try {
					contributorsIDs[i] = Long.parseLong(contributors[i]);
				} catch (final NumberFormatException nfe) {
					nfe.printStackTrace();
					logger.warn("failed to parse contributors:" + nfe);
				}
			}
			contributors = null;
		}
		return contributorsIDs;
	}

	/**
	 * {@inheritDoc}
	 */
	public Date getCreatedAt() {
		return createdAt;
	}

	public long getCurrentUserRetweet() {
		return currentUserRetweet;
	}

	/**
	 * {@inheritDoc}
	 */
	public GeoLocation getGeoLocation() {
		return geoLocation;
	}

	/**
	 * {@inheritDoc}
	 */
	public HashtagEntity[] getHashtagEntities() {
		return hashtagEntities;
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
	public String getInReplyToScreenName() {
		return inReplyToScreenName;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getInReplyToStatusId() {
		return inReplyToStatusId;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getInReplyToUserId() {
		return inReplyToUserId;
	}

	/**
	 * {@inheritDoc}
	 */
	public MediaEntity[] getMediaEntities() {
		return mediaEntities;
	}

	/**
	 * {@inheritDoc}
	 */
	public Place getPlace() {
		return place;
	}

	public String getRawText() {
		return rawText;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getRetweetCount() {
		return retweetCount;
	}

	/**
	 * {@inheritDoc}
	 */
	public Status getRetweetedStatus() {
		return retweetedStatus;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getSource() {
		return source;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getText() {
		return text;
	}

	/**
	 * {@inheritDoc}
	 */
	public URLEntity[] getURLEntities() {
		return urlEntities;
	}

	/**
	 * {@inheritDoc}
	 */
	public User getUser() {
		return user;
	}

	/**
	 * {@inheritDoc}
	 */
	public UserMentionEntity[] getUserMentionEntities() {
		return userMentionEntities;
	}

	public int hashCode() {
		return (int) id;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isFavorited() {
		return isFavorited;
	}

	public boolean isPossiblySensitive() {
		return isPossiblySensitive;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isRetweet() {
		return retweetedStatus != null;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isRetweetedByMe() {
		return wasRetweetedByMe;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isTruncated() {
		return isTruncated;
	}

	public String toString() {
		return "StatusJSONImpl{createdAt=" + createdAt + ", id=" + id + ", text=" + text + ", rawText=" + rawText
				+ ", source=" + source + ", isTruncated=" + isTruncated + ", inReplyToStatusId=" + inReplyToStatusId
				+ ", inReplyToUserId=" + inReplyToUserId + ", currentUserRetweet=" + currentUserRetweet
				+ ", isFavorited=" + isFavorited + ", inReplyToScreenName=" + inReplyToScreenName + ", geoLocation="
				+ geoLocation + ", place=" + place + ", retweetCount=" + retweetCount + ", wasRetweetedByMe="
				+ wasRetweetedByMe + ", isPossiblySensitive=" + isPossiblySensitive + ", contributors="
				+ Arrays.toString(contributors) + ", contributorsIDs=" + Arrays.toString(contributorsIDs)
				+ ", retweetedStatus=" + retweetedStatus + ", userMentionEntities="
				+ Arrays.toString(userMentionEntities) + ", urlEntities=" + Arrays.toString(urlEntities)
				+ ", hashtagEntities=" + Arrays.toString(hashtagEntities) + ", mediaEntities="
				+ Arrays.toString(mediaEntities) + ", user=" + user + "}";
	}

	private void init(final JSONObject json) throws TwitterException {
		id = getLong("id", json);
		rawText = getRawString("text", json);
		text = getUnescapedString("text", json);
		source = getHTMLUnescapedString("source", json);
		createdAt = getDate("created_at", json);
		isTruncated = getBoolean("truncated", json);
		inReplyToStatusId = getLong("in_reply_to_status_id", json);
		inReplyToUserId = getLong("in_reply_to_user_id", json);
		isFavorited = getBoolean("favorited", json);
		inReplyToScreenName = getHTMLUnescapedString("in_reply_to_screen_name", json);
		isPossiblySensitive = getBoolean("possibly_sensitive", json);
		retweetCount = getLong("retweet_count", json);
		try {
			if (!json.isNull("user")) {
				user = new UserJSONImpl(json.getJSONObject("user"));
			}
		} catch (final JSONException jsone) {
			throw new TwitterException(jsone);
		}
		geoLocation = InternalJSONFactoryImpl.createGeoLocation(json);
		if (!json.isNull("place")) {
			place = new PlaceJSONImpl(json.optJSONObject("place"));
		}

		if (!json.isNull("retweeted_status")) {
			retweetedStatus = new StatusJSONImpl(json.optJSONObject("retweeted_status"));
		}
		if (!json.isNull("contributors")) {
			try {
				final JSONArray contributorsArray = json.getJSONArray("contributors");
				contributorsIDs = new long[contributorsArray.length()];
				for (int i = 0; i < contributorsArray.length(); i++) {
					contributorsIDs[i] = Long.parseLong(contributorsArray.getString(i));
				}
			} catch (final NumberFormatException ignore) {
				ignore.printStackTrace();
				logger.warn("failed to parse contributors:" + json);
			} catch (final JSONException ignore) {
				ignore.printStackTrace();
				logger.warn("failed to parse contributors:" + json);
			}
		} else {
			contributors = null;
		}
		if (!json.isNull("entities")) {
			try {
				final JSONObject entities = json.getJSONObject("entities");
				int len;
				if (!entities.isNull("user_mentions")) {
					final JSONArray userMentionsArray = entities.getJSONArray("user_mentions");
					len = userMentionsArray.length();
					userMentionEntities = new UserMentionEntity[len];
					for (int i = 0; i < len; i++) {
						userMentionEntities[i] = new UserMentionEntityJSONImpl(userMentionsArray.getJSONObject(i));
					}
				}
				if (!entities.isNull("urls")) {
					final JSONArray urlsArray = entities.getJSONArray("urls");
					len = urlsArray.length();
					urlEntities = new URLEntity[len];
					for (int i = 0; i < len; i++) {
						urlEntities[i] = new URLEntityJSONImpl(urlsArray.getJSONObject(i));
					}
				}
				if (!entities.isNull("hashtags")) {
					final JSONArray hashtagsArray = entities.getJSONArray("hashtags");
					len = hashtagsArray.length();
					hashtagEntities = new HashtagEntity[len];
					for (int i = 0; i < len; i++) {
						hashtagEntities[i] = new HashtagEntityJSONImpl(hashtagsArray.getJSONObject(i));
					}
				}
				if (!entities.isNull("media")) {
					final JSONArray mediaArray = entities.getJSONArray("media");
					len = mediaArray.length();
					mediaEntities = new MediaEntity[len];
					for (int i = 0; i < len; i++) {
						mediaEntities[i] = new MediaEntityJSONImpl(mediaArray.getJSONObject(i));
					}
				}
			} catch (final JSONException jsone) {
				throw new TwitterException(jsone);
			}
		}
		if (!json.isNull("current_user_retweet")) {
			try {
				currentUserRetweet = getLong("id", json.getJSONObject("current_user_retweet"));
				wasRetweetedByMe = currentUserRetweet > 0;
			} catch (final JSONException ignore) {
				ignore.printStackTrace();
				logger.warn("failed to parse current_user_retweet:" + json);
			}
		}
	}

	/* package */
	static ResponseList createStatusList(final HttpResponse res, final Configuration conf)
			throws TwitterException {
		try {
			final JSONArray list = res.asJSONArray();
			final int size = list.length();
			final ResponseList statuses = new ResponseListImpl(size, res);
			for (int i = 0; i < size; i++) {
				final JSONObject json = list.getJSONObject(i);
				final Status status = new StatusJSONImpl(json);
				statuses.add(status);
			}
			return statuses;
		} catch (final JSONException jsone) {
			throw new TwitterException(jsone);
		} catch (final TwitterException te) {
			throw te;
		}
	}
}
