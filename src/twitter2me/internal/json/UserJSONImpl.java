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
import twitter2me.PagableResponseList;
import twitter2me.ResponseList;
import twitter2me.Status;
import twitter2me.TwitterException;
import twitter2me.URLEntity;
import twitter2me.User;
import twitter2me.conf.Configuration;
import twitter2me.http.HttpResponse;

/**
 * A data class representing Basic user information element
 * 
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
/* package */
final class UserJSONImpl extends TwitterResponseImpl implements User {

	private static final long serialVersionUID = 1401119968489672262L;
	private long id;
	private String name;
	private String screenName;
	private String location;
	private String description;
	private boolean isContributorsEnabled;
	private String profileImageUrl;
	private String profileImageUrlHttps;
	private String url;
	private boolean isProtected;
	private int followersCount;
	private Status status;
	private String profileBackgroundColor;
	private String profileTextColor;
	private String profileLinkColor;
	private String profileSidebarFillColor;
	private String profileSidebarBorderColor;
	private boolean profileUseBackgroundImage;
	private boolean showAllInlineMedia;
	private int friendsCount;
	private Date createdAt;
	private int favouritesCount;
	private int utcOffset;
	private String timeZone;
	private String profileBackgroundImageUrl;
	private String profileBackgroundImageUrlHttps;
	private boolean profileBackgroundTiled;
	private String lang;
	private int statusesCount;
	private boolean isGeoEnabled;
	private boolean isVerified;
	private boolean isTranslator;
	private boolean isFollowing;
	private int listedCount;
	private boolean isFollowRequestSent;
	private String profileBannerImageUrl;
	private boolean isDefaultProfileImage;
	private URLEntity[] urlEntities;
	private URLEntity[] descriptionEntities;

	/* package */
	UserJSONImpl(final HttpResponse res, final Configuration conf) throws TwitterException {
		super(res);
		final JSONObject json = res.asJSONObject();
		init(json);
	}

	/* package */
	UserJSONImpl(final JSONObject json) throws TwitterException {
		super();
		init(json);
	}

	public int compareTo(final User that) {
		return (int) (id - that.getId());
	}

	public boolean equals(final Object obj) {
		if (null == obj) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		return obj instanceof User && ((User) obj).getId() == id;
	}

	/**
	 * {@inheritDoc}
	 */
	public Date getCreatedAt() {
		return createdAt;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDescription() {
		return description;
	}

	public URLEntity[] getDescriptionEntities() {
		return descriptionEntities;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getFavouritesCount() {
		return favouritesCount;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getFollowersCount() {
		return followersCount;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getFriendsCount() {
		return friendsCount;
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
	public String getLang() {
		return lang;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getListedCount() {
		return listedCount;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getProfileBackgroundColor() {
		return profileBackgroundColor;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getProfileBackgroundImageUrl() {
		return profileBackgroundImageUrl;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getProfileBackgroundImageUrlHttps() {
		return profileBackgroundImageUrlHttps;
	}

	public String getProfileBannerImageUrl() {
		return profileBannerImageUrl;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getProfileImageURL() {
		return profileImageUrl;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getProfileImageUrlHttps() {
		return profileImageUrlHttps;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getProfileLinkColor() {
		return profileLinkColor;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getProfileSidebarBorderColor() {
		return profileSidebarBorderColor;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getProfileSidebarFillColor() {
		return profileSidebarFillColor;
	}

	public String getProfileTextColor() {
		return profileTextColor;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getScreenName() {
		return screenName;
	}

	/**
	 * {@inheritDoc}
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getStatusesCount() {
		return statusesCount;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getTimeZone() {
		return timeZone;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getURL() {
		return url;
	}

	public URLEntity[] getURLEntities() {
		return urlEntities;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getUtcOffset() {
		return utcOffset;
	}

	public int hashCode() {
		return (int) id;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isContributorsEnabled() {
		return isContributorsEnabled;
	}

	public boolean isDefaultProfileImage() {
		return isDefaultProfileImage;
	}

	public boolean isFollowing() {
		return isFollowing;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isFollowRequestSent() {
		return isFollowRequestSent;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isGeoEnabled() {
		return isGeoEnabled;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isProfileBackgroundTiled() {
		return profileBackgroundTiled;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isProfileUseBackgroundImage() {
		return profileUseBackgroundImage;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isProtected() {
		return isProtected;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isShowAllInlineMedia() {
		return showAllInlineMedia;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isTranslator() {
		return isTranslator;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isVerified() {
		return isVerified;
	}

	public String toString() {
		return "UserJSONImpl{" + "id=" + id + ", name=" + name + ", screenName=" + screenName + ", location=" + location
				+ ", description=" + description + ", isContributorsEnabled=" + isContributorsEnabled
				+ ", profileImageUrl=" + profileImageUrl + ", profileImageUrlHttps=" + profileImageUrlHttps + ", url="
				+ url + ", isProtected=" + isProtected + ", followersCount=" + followersCount + ", status=" + status
				+ ", profileBackgroundColor=" + profileBackgroundColor + ", profileTextColor=" + profileTextColor
				+ ", profileLinkColor=" + profileLinkColor + ", profileSidebarFillColor=" + profileSidebarFillColor
				+ ", profileSidebarBorderColor=" + profileSidebarBorderColor + ", profileUseBackgroundImage="
				+ profileUseBackgroundImage + ", showAllInlineMedia=" + showAllInlineMedia + ", friendsCount="
				+ friendsCount + ", createdAt=" + createdAt + ", favouritesCount=" + favouritesCount + ", utcOffset="
				+ utcOffset + ", timeZone=" + timeZone + ", profileBackgroundImageUrl=" + profileBackgroundImageUrl
				+ ", profileBackgroundImageUrlHttps=" + profileBackgroundImageUrlHttps + ", profileBackgroundTiled="
				+ profileBackgroundTiled + ", lang=" + lang + ", statusesCount=" + statusesCount + ", isGeoEnabled="
				+ isGeoEnabled + ", isVerified=" + isVerified + ", isTranslator=" + isTranslator + ", isFollowing="
				+ isFollowing + ", listedCount=" + listedCount + ", isFollowRequestSent=" + isFollowRequestSent
				+ ", profileBannerImageUrl=" + profileBannerImageUrl + ", isDefaultProfileImage="
				+ isDefaultProfileImage + ", urlEntities=" + Arrays.toString(urlEntities) + ", descriptionEntities="
				+ Arrays.toString(descriptionEntities) + '}';
	}

	private void init(final JSONObject json) throws TwitterException {
		try {
			id = getLong("id", json);
			name = getRawString("name", json);
			screenName = getRawString("screen_name", json);
			location = getRawString("location", json);
			description = getRawString("description", json);
			isContributorsEnabled = getBoolean("contributors_enabled", json);
			profileImageUrl = getRawString("profile_image_url", json);
			profileImageUrlHttps = getRawString("profile_image_url_https", json);
			url = getRawString("url", json);
			isProtected = getBoolean("protected", json);
			isGeoEnabled = getBoolean("geo_enabled", json);
			isVerified = getBoolean("verified", json);
			isTranslator = getBoolean("is_translator", json);
			isFollowing = getBoolean("following", json);
			followersCount = getInt("followers_count", json);
			profileBannerImageUrl = getRawString("profile_banner_url", json);
			profileBackgroundColor = getRawString("profile_background_color", json);
			profileTextColor = getRawString("profile_text_color", json);
			profileLinkColor = getRawString("profile_link_color", json);
			profileSidebarFillColor = getRawString("profile_sidebar_fill_color", json);
			profileSidebarBorderColor = getRawString("profile_sidebar_border_color", json);
			profileUseBackgroundImage = getBoolean("profile_use_background_image", json);
			showAllInlineMedia = getBoolean("show_all_inline_media", json);
			friendsCount = getInt("friends_count", json);
			createdAt = getDate("created_at", json);
			favouritesCount = getInt("favourites_count", json);
			utcOffset = getInt("utc_offset", json);
			timeZone = getRawString("time_zone", json);
			profileBackgroundImageUrl = getRawString("profile_background_image_url", json);
			profileBackgroundImageUrlHttps = getRawString("profile_background_image_url_https", json);
			profileBackgroundTiled = getBoolean("profile_background_tile", json);
			lang = getRawString("lang", json);
			statusesCount = getInt("statuses_count", json);
			listedCount = getInt("listed_count", json);
			isFollowRequestSent = getBoolean("follow_request_sent", json);
			isDefaultProfileImage = getBoolean("default_profile_image", json);
			if (!json.isNull("status")) {
				final JSONObject statusJSON = json.getJSONObject("status");
				status = new StatusJSONImpl(statusJSON);
			}
			if (!json.isNull("entities")) {
				try {
					final JSONObject entities = json.getJSONObject("entities");
					int len;
					if (!entities.isNull("description")) {
						final JSONObject description = entities.getJSONObject("description");
						if (!description.isNull("urls")) {
							final JSONArray urlsArray = description.getJSONArray("urls");
							len = urlsArray.length();
							descriptionEntities = new URLEntity[len];
							for (int i = 0; i < len; i++) {
								descriptionEntities[i] = new URLEntityJSONImpl(urlsArray.getJSONObject(i));
							}
						}
					}
					if (!entities.isNull("url")) {
						final JSONObject url = entities.getJSONObject("url");
						if (!url.isNull("urls")) {
							final JSONArray urlsArray = url.getJSONArray("urls");
							len = urlsArray.length();
							urlEntities = new URLEntity[len];
							for (int i = 0; i < len; i++) {
								urlEntities[i] = new URLEntityJSONImpl(urlsArray.getJSONObject(i));
							}
						}
					}
				} catch (final JSONException jsone) {
					throw new TwitterException(jsone);
				}
			}
		} catch (final JSONException jsone) {
			throw new TwitterException(jsone.getMessage() + ":" + json.toString(), jsone);
		}
	}

	/* package */
	static PagableResponseList createPagableUserList(final HttpResponse res, final Configuration conf)
			throws TwitterException {
		try {
			final JSONObject json = res.asJSONObject();
			final JSONArray list = json.getJSONArray("users");
			final int size = list.length();
			final PagableResponseList users = new PagableResponseListImpl(size, json, res);
			for (int i = 0; i < size; i++) {
				final JSONObject userJson = list.getJSONObject(i);
				final User user = new UserJSONImpl(userJson);
				users.add(user);
			}
			return users;
		} catch (final JSONException jsone) {
			throw new TwitterException(jsone);
		} catch (final TwitterException te) {
			throw te;
		}
	}

	/* package */
	static ResponseList createUserList(final HttpResponse res, final Configuration conf) throws TwitterException {
		return createUserList(res.asJSONArray(), res, conf);
	}

	/* package */
	static ResponseList createUserList(final JSONArray list, final HttpResponse res, final Configuration conf)
			throws TwitterException {
		try {
			final int size = list.length();
			final ResponseList users = new ResponseListImpl(size, res);
			for (int i = 0; i < size; i++) {
				final JSONObject json = list.getJSONObject(i);
				final User user = new UserJSONImpl(json);
				users.add(user);
			}
			return users;
		} catch (final JSONException jsone) {
			throw new TwitterException(jsone);
		} catch (final TwitterException te) {
			throw te;
		}
	}
}
