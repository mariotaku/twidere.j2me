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

import org.kalmeo.util.MathFP;
import org.mariotaku.twidere.util.TextUtils;

/**
 * A data class representing geo location.
 * 
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public class GeoLocation {

	protected final int latitude;
	protected final int longitude;

	/**
	 * Creates a GeoLocation instance
	 * 
	 * @param latitude the latitude
	 * @param longitude the longitude
	 */
	public GeoLocation(final String latitude, final String longitude) {
		this.latitude = parseDouble(latitude);
		this.longitude = parseDouble(longitude);
	}

	public boolean equals(final Object o) {
		if (!(o instanceof GeoLocation)) {
			return false;
		}
		final GeoLocation that = (GeoLocation) o;
		if (that.getLatitude() != latitude) {
			return false;
		}
		if (that.getLongitude() != longitude) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		int hash = 7;
		hash = 53 * hash + this.latitude;
		hash = 53 * hash + this.longitude;
		return hash;
	}

	/**
	 * returns the latitude of the geo location
	 * 
	 * @return the latitude
	 */
	public int getLatitude() {
		return latitude;
	}

	/**
	 * returns the longitude of the geo location
	 * 
	 * @return the longitude
	 */
	public int getLongitude() {
		return longitude;
	}

	public String toString() {
		return "GeoLocation{" + "latitude=" + MathFP.toString(latitude) + ", longitude=" + MathFP.toString(longitude)
				+ '}';
	}

	private int parseDouble(String str) {
		if (TextUtils.isEmpty(str)) {
			return 0;
		}
		if (str.startsWith("\"") && str.endsWith("\"")) {
			return MathFP.toFP(str.substring(1, str.length() - 1));
		}
		try {
			return MathFP.toFP(str);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
}
