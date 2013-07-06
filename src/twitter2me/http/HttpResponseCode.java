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
package twitter2me.http;

import javax.microedition.io.HttpConnection;

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.1.2
 */
public interface HttpResponseCode {

	/** OK: Success! **/
	int OK = HttpConnection.HTTP_OK;
	int CREATED = HttpConnection.HTTP_CREATED;
	int ACCEPTED = HttpConnection.HTTP_ACCEPTED;
	int MULTIPLE_CHOICES = HttpConnection.HTTP_MULT_CHOICE;
	int FOUND = HttpConnection.HTTP_MOVED_TEMP;
	/** Not Modified: There was no new data to return. **/
	int NOT_MODIFIED = HttpConnection.HTTP_NOT_MODIFIED;
	/**
	 * Bad Request: The request was invalid. An accompanying error message will
	 * explain why. This is the status code will be returned during rate
	 * limiting.
	 **/
	int BAD_REQUEST = HttpConnection.HTTP_BAD_REQUEST;
	/** Not Authorized: Authentication credentials were missing or incorrect. **/
	int UNAUTHORIZED = HttpConnection.HTTP_UNAUTHORIZED;
	/**
	 * Forbidden: The request is understood, but it has been refused. An
	 * accompanying error message will explain why.
	 **/
	int FORBIDDEN = HttpConnection.HTTP_FORBIDDEN;
	/**
	 * Not Found: The URI requested is invalid or the resource requested, such
	 * as a user, does not exists.
	 **/
	int NOT_FOUND = HttpConnection.HTTP_NOT_FOUND;
	/**
	 * Not Acceptable: Returned by the Search API when an invalid format is
	 * specified in the request.
	 **/
	int NOT_ACCEPTABLE = HttpConnection.HTTP_NOT_ACCEPTABLE;
	/**
	 * Enhance Your Calm: Returned by the Search and Trends API when you are
	 * being rate limited. Not registered in RFC.
	 **/
	int ENHANCE_YOUR_CLAIM = 420;
	/**
	 * Returned when an image uploaded to POST account/update_profile_banner is
	 * unable to be processed.
	 **/
	int UNPROCESSABLE_ENTITY = 422;
	/**
	 * Returned in API v1.1 when a request cannot be served due to the
	 * application's rate limit having been exhausted for the resource. See Rate
	 * Limiting in API v1.1.
	 **/
	int TOO_MANY_REQUESTS = 429;
	/**
	 * Internal Server Error: Something is broken. Please post to the group so
	 * the Twitter team can investigate.
	 **/
	int INTERNAL_SERVER_ERROR = HttpConnection.HTTP_INTERNAL_ERROR;
	/** Bad Gateway: Twitter is down or being upgraded. **/
	int BAD_GATEWAY = HttpConnection.HTTP_BAD_GATEWAY;
	/**
	 * Service Unavailable: The Twitter servers are up, but overloaded with
	 * requests. Try again later. The search and trend methods use this to
	 * indicate when you are being rate limited.
	 **/
	int SERVICE_UNAVAILABLE = HttpConnection.HTTP_UNAVAILABLE;
	/**
	 * The Twitter servers are up, but the request couldn't be serviced due to
	 * some failure within our stack. Try again later.
	 **/
	int GATEWAY_TIMEOUT = HttpConnection.HTTP_GATEWAY_TIMEOUT;
}
