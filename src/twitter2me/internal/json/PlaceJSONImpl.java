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

import repackaged.java.util.Arrays;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import twitter2me.GeoLocation;
import twitter2me.Place;
import twitter2me.ResponseList;
import twitter2me.TwitterException;
import twitter2me.conf.Configuration;
import twitter2me.http.HttpResponse;

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since twitter2me 2.1.1
 */
final class PlaceJSONImpl extends TwitterResponseImpl implements Place {

	private String name;
	private String streetAddress;
	private String countryCode;
	private String id;
	private String country;
	private String placeType;
	private String url;
	private String fullName;
	private String boundingBoxType;
	private GeoLocation[][] boundingBoxCoordinates;
	private String geometryType;
	private GeoLocation[][] geometryCoordinates;
	private Place[] containedWithIn;

	/* For serialization purposes only. */
	PlaceJSONImpl() {
	}

	/* package */
	PlaceJSONImpl(final HttpResponse res, final Configuration conf) throws TwitterException {
		super(res);
		final JSONObject json = res.asJSONObject();
		init(json);
	}

	PlaceJSONImpl(final JSONObject json) throws TwitterException {
		super();
		init(json);
	}

	PlaceJSONImpl(final JSONObject json, final HttpResponse res) throws TwitterException {
		super(res);
		init(json);
	}

	public int compareTo(final Place that) {
		return id.compareTo(that.getId());
	}

	public boolean equals(final Object obj) {
		if (null == obj) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		return obj instanceof Place && ((Place) obj).getId().equals(id);
	}

	public GeoLocation[][] getBoundingBoxCoordinates() {
		return boundingBoxCoordinates;
	}

	public String getBoundingBoxType() {
		return boundingBoxType;
	}

	public Place[] getContainedWithIn() {
		return containedWithIn;
	}

	public String getCountry() {
		return country;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public String getFullName() {
		return fullName;
	}

	public GeoLocation[][] getGeometryCoordinates() {
		return geometryCoordinates;
	}

	public String getGeometryType() {
		return geometryType;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPlaceType() {
		return placeType;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public String getURL() {
		return url;
	}

	public int hashCode() {
		return id.hashCode();
	}

	public String toString() {
		return "PlaceJSONImpl{" + "name='" + name + '\'' + ", streetAddress='" + streetAddress + '\''
				+ ", countryCode='" + countryCode + '\'' + ", id='" + id + '\'' + ", country='" + country + '\''
				+ ", placeType='" + placeType + '\'' + ", url='" + url + '\'' + ", fullName='" + fullName + '\''
				+ ", boundingBoxType='" + boundingBoxType + '\'' + ", boundingBoxCoordinates="
				+ (boundingBoxCoordinates == null ? null : Arrays.asList(boundingBoxCoordinates)) + ", geometryType='"
				+ geometryType + '\'' + ", geometryCoordinates="
				+ (geometryCoordinates == null ? null : Arrays.asList(geometryCoordinates)) + ", containedWithIn="
				+ (containedWithIn == null ? null : Arrays.asList(containedWithIn)) + '}';
	}

	private void init(final JSONObject json) throws TwitterException {
		try {
			name = getHTMLUnescapedString("name", json);
			streetAddress = getHTMLUnescapedString("street_address", json);
			countryCode = getRawString("country_code", json);
			id = getRawString("id", json);
			country = getRawString("country", json);
			if (!json.isNull("place_type")) {
				placeType = getRawString("place_type", json);
			} else {
				placeType = getRawString("type", json);
			}
			url = getRawString("url", json);
			fullName = getRawString("full_name", json);
			if (!json.isNull("bounding_box")) {
				final JSONObject boundingBoxJSON = json.getJSONObject("bounding_box");
				boundingBoxType = getRawString("type", boundingBoxJSON);
				final JSONArray array = boundingBoxJSON.getJSONArray("coordinates");
				boundingBoxCoordinates = InternalJSONFactoryImpl.coordinatesAsGeoLocationArray(array);
			} else {
				boundingBoxType = null;
				boundingBoxCoordinates = null;
			}

			if (!json.isNull("geometry")) {
				final JSONObject geometryJSON = json.getJSONObject("geometry");
				geometryType = getRawString("type", geometryJSON);
				final JSONArray array = geometryJSON.getJSONArray("coordinates");
				if (geometryType.equals("Point")) {
					geometryCoordinates = new GeoLocation[1][1];
					geometryCoordinates[0][0] = new GeoLocation(array.optString(0), array.optString(1));
				} else if (geometryType.equals("Polygon")) {
					geometryCoordinates = InternalJSONFactoryImpl.coordinatesAsGeoLocationArray(array);
				} else {
					// MultiPolygon currently unsupported.
					geometryType = null;
					geometryCoordinates = null;
				}
			} else {
				geometryType = null;
				geometryCoordinates = null;
			}

			if (!json.isNull("contained_within")) {
				final JSONArray containedWithInJSON = json.getJSONArray("contained_within");
				containedWithIn = new Place[containedWithInJSON.length()];
				for (int i = 0; i < containedWithInJSON.length(); i++) {
					containedWithIn[i] = new PlaceJSONImpl(containedWithInJSON.getJSONObject(i));
				}
			} else {
				containedWithIn = null;
			}
		} catch (final JSONException jsone) {
			throw new TwitterException(jsone.getMessage() + ":" + json.toString(), jsone);
		}
	}

	/* package */
	static ResponseList createPlaceList(final HttpResponse res, final Configuration conf)
			throws TwitterException {
		JSONObject json = null;
		try {
			json = res.asJSONObject();
			return createPlaceList(json.getJSONObject("result").getJSONArray("places"), res, conf);
		} catch (final JSONException jsone) {
			throw new TwitterException(jsone.getMessage() + ":" + json.toString(), jsone);
		}
	}

	/* package */
	static ResponseList createPlaceList(final JSONArray list, final HttpResponse res, final Configuration conf)
			throws TwitterException {
		try {
			final int size = list.length();
			final ResponseList places = new ResponseListImpl(size, res);
			for (int i = 0; i < size; i++) {
				final JSONObject json = list.getJSONObject(i);
				final Place place = new PlaceJSONImpl(json);
				places.add(place);
			}
			return places;
		} catch (final JSONException jsone) {
			throw new TwitterException(jsone);
		} catch (final TwitterException te) {
			throw te;
		}
	}
}
