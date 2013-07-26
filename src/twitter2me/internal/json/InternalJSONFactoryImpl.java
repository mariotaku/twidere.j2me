/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter2me.internal.json;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import twitter2me.GeoLocation;
import twitter2me.ResponseList;
import twitter2me.Status;
import twitter2me.TwitterException;
import twitter2me.User;
import twitter2me.conf.Configuration;
import twitter2me.http.HttpResponse;
import twitter2me.internal.util.InternalStringUtil;

/**
 *
 * @author mariotaku
 */
public class InternalJSONFactoryImpl implements InternalJSONFactory {

	private final Configuration conf;

	public InternalJSONFactoryImpl(Configuration conf) {
		this.conf = conf;
	}

	public User createUser(HttpResponse resp) throws TwitterException {
		return new UserJSONImpl(resp, conf);
	}

	public User createUser(JSONObject json) throws TwitterException {
		return new UserJSONImpl(json);
	}

	public Status createStatus(HttpResponse resp) throws TwitterException {
		return new StatusJSONImpl(resp, conf);
	}

	public Status createStatus(JSONObject json) throws TwitterException {
		return new StatusJSONImpl(json);
	}

	public ResponseList createStatusesList(HttpResponse res) throws TwitterException {
		return StatusJSONImpl.createStatusList(res, conf);
	}

	static GeoLocation[][] coordinatesAsGeoLocationArray(final JSONArray coordinates) throws TwitterException {
		try {
			final GeoLocation[][] boundingBox = new GeoLocation[coordinates.length()][];
			for (int i = 0; i < coordinates.length(); i++) {
				final JSONArray array = coordinates.getJSONArray(i);
				boundingBox[i] = new GeoLocation[array.length()];
				for (int j = 0; j < array.length(); j++) {
					final JSONArray coordinate = array.getJSONArray(j);
					boundingBox[i][j] = new GeoLocation(coordinate.optString(1), coordinate.optString(0));
				}
			}
			return boundingBox;
		} catch (final JSONException jsone) {
			throw new TwitterException(jsone);
		}
	}
	
		/**
	 * returns a GeoLocation instance if a "geo" element is found.
	 * 
	 * @param json JSONObject to be parsed
	 * @return GeoLocation instance
	 * @throws TwitterException when coordinates is not included in geo element
	 *             (should be an API side issue)
	 */
	/* package */
	static GeoLocation createGeoLocation(final JSONObject json) throws TwitterException {
		try {
			if (!json.isNull("geo")) {
				String coordinates = json.getJSONObject("geo").getString("coordinates");
				coordinates = coordinates.substring(1, coordinates.length() - 1);
				final String[] point = InternalStringUtil.split(coordinates, ",");
				return new GeoLocation(point[0], point[1]);
			}
		} catch (final JSONException jsone) {
			throw new TwitterException(jsone);
		}
		return null;
	}
}
