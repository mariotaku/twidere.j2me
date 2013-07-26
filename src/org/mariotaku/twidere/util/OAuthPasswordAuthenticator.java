/*
 *				Twidere - Twitter client for Android
 * 
 * Copyright (C) 2012 Mariotaku Lee <mariotaku.lee@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.mariotaku.twidere.util;

import java.io.IOException;

import java.io.Reader;
import javax.microedition.io.HttpConnection;

import org.kxml.Xml;
import org.kxml.parser.ParseEvent;
import org.kxml.parser.XmlParser;
import org.mariotaku.twidere.Constants;

import org.mariotaku.twidere.util.http.TwidereHttpClientFactory;
import twitter2me.Twitter;
import twitter2me.TwitterException;
import twitter2me.auth.AccessToken;
import twitter2me.auth.RequestToken;
import twitter2me.conf.Configuration;
import twitter2me.http.HttpClient;
import twitter2me.http.HttpParameter;
import twitter2me.http.HttpRequest;
import twitter2me.internal.util.InternalStringUtil;

public class OAuthPasswordAuthenticator implements Constants {

	private final Twitter twitter;
	private final HttpClient client;

	public OAuthPasswordAuthenticator(final Twitter twitter) {
		final Configuration conf = twitter.getConfiguration();
		this.twitter = twitter;
		client = new TwidereHttpClientFactory().newInstance(conf);
	}

	public synchronized AccessToken getOAuthAccessToken(final String username, final String password)
			throws AuthenticationException {
		final RequestToken request_token;
		try {
			request_token = twitter.getOAuthRequestToken(OAUTH_CALLBACK_OOB);
		} catch (final TwitterException e) {
			if (e.isCausedByNetworkIssue()) {
				throw new AuthenticationException(e);
			}
			throw new AuthenticityTokenException(e);
		}
		try {
			final String oauth_token = request_token.getToken();
			final String authenticity_token = readAuthenticityToken(getHTTPContent(request_token.getAuthorizationURL(),
					false, null));
			if (authenticity_token == null) {
				throw new AuthenticityTokenException();
			}
			final Configuration conf = twitter.getConfiguration();
			final HttpParameter[] params = new HttpParameter[4];
			params[0] = new HttpParameter("authenticity_token", authenticity_token);
			params[1] = new HttpParameter("oauth_token", oauth_token);
			params[2] = new HttpParameter("session[username_or_email]", username);
			params[3] = new HttpParameter("session[password]", password);
			final String oauth_pin = readOAuthPIN(getHTTPContent(conf.getOAuthAuthorizationURL().toString(), true,
					params));
			if (TextUtils.isEmpty(oauth_pin)) {
				throw new WrongUserPassException();
			}
			return twitter.getOAuthAccessToken(request_token, oauth_pin);
		} catch (final IOException e) {
			throw new AuthenticationException(e);
		} catch (final TwitterException e) {
			throw new AuthenticationException(e);
		} catch (final NullPointerException e) {
			throw new AuthenticationException(e);
		}
	}

	private Reader getHTTPContent(final String url_string, final boolean post, final HttpParameter[] params)
			throws TwitterException {
		final String method = post ? HttpConnection.POST : HttpConnection.GET;
		// headers.put("User-Agent", user_agent);
		final HttpRequest request = new HttpRequest(method, url_string, url_string, params, null);
		return client.request(request).asReader();
	}

	private static String readAuthenticityToken(final Reader reader) throws IOException {
		final XmlParser parser = new XmlParser(reader);
		parser.setRelaxed(true);
		ParseEvent event = null;
		while ((event = parser.read()).getType() != Xml.END_DOCUMENT) {
			final String tag = event.getName();
			if (event.getType() == Xml.START_TAG && "input".equals(tag) && "authenticity_token".equals(event.
					getValueDefault("name", null))) {
				return event.getValueDefault("value", null);
			}
			event = null;
		}
		return null;
	}

	private static String readOAuthPIN(final Reader reader) throws IOException {
		boolean start_div = false, start_code = false;
		final XmlParser parser = new XmlParser(reader);
		parser.setRelaxed(true);
		ParseEvent event = null;
		while ((event = parser.read()).getType() != Xml.END_DOCUMENT) {
			final String tag = event.getName();
			final int type = event.getType();
			if (type == Xml.START_TAG) {
				if (InternalStringUtil.equalsIgnoreCase("div", tag)) {
					start_div = "oauth_pin".equals(event.getValueDefault("id", null));
				} else if (InternalStringUtil.equalsIgnoreCase("code", tag)) {
					if (start_div) {
						start_code = true;
					}
				}
			} else if (type == Xml.END_TAG) {
				if (InternalStringUtil.equalsIgnoreCase("div", tag)) {
					start_div = false;
				} else if (InternalStringUtil.equalsIgnoreCase("code", tag)) {
					start_code = false;
				}
			} else if (type == Xml.TEXT) {
				final String text = event.getText();
				if (start_code && !TextUtils.isEmpty(text) && TextUtils.isDigitsOnly(text)) {
					return text;
				}
			}
			event = null;
		}
		return null;
	}

	public static class AuthenticationException extends Exception {

		private final Throwable cause;

		public Throwable getCause() {
			return cause;
		}

		AuthenticationException() {
			this(null, null);
		}

		AuthenticationException(final Throwable cause) {
			this(cause, cause.getMessage());
		}

		AuthenticationException(final Throwable cause, final String message) {
			super(message);
			this.cause = cause;
		}
	}

	public static final class AuthenticityTokenException extends AuthenticationException {

		private AuthenticityTokenException() {
			super();
		}

		private AuthenticityTokenException(Throwable cause) {
			super(cause);
		}
	}

	public static final class WrongUserPassException extends AuthenticationException {
	}
}
