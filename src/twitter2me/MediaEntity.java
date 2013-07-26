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

import repackaged.java.util.Map;

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.2.3
 */
public interface MediaEntity extends URLEntity {

	/**
	 * Returns the id of the media.
	 * 
	 * @return the id of the media
	 */
	long getId();

	/**
	 * Returns the media URL.
	 * 
	 * @return the media URL
	 */
	String getMediaURL();

	/**
	 * Returns the media secure URL.
	 * 
	 * @return the media secure URL
	 */
	String getMediaURLHttps();

	/**
	 * Returns size variations of the media.
	 * 
	 * @return size variations of the media
	 */
	Map getSizes();

	/**
	 * Returns the media type ("photo").
	 * 
	 * @return the media type ("photo").
	 */
	String getType();

	interface Size {

		static final int THUMB = 0;
		static final int SMALL = 1;
		static final int MEDIUM = 2;
		static final int LARGE = 3;
		static final int FIT = 100;
		static final int CROP = 101;

		int getHeight();

		int getResize();

		int getWidth();
	}
}
