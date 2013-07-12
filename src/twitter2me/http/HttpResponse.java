/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter2me.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import twitter2me.TwitterException;
import twitter2me.internal.util.IOUtil;

/**
 *
 * @author mariotaku
 */
public abstract class HttpResponse {

	protected final HttpConfiguration conf;
	private InputStream is;
	private String responseAsString;
	private boolean streamConsumed;

	protected HttpResponse(final HttpConfiguration conf) throws IOException {
		this.conf = conf;
	}

	public final HttpConfiguration getHttpConfiguration() {
		return conf;
	}

	protected abstract InputStream getInputStream() throws TwitterException;

	private InputStream getInputStreamInternal() throws TwitterException {
		if (is == null) {
			return is = getInputStream();
		} else {
			return is;
		}
	}

	public final Reader asReader() throws TwitterException {
		try {
			return new InputStreamReader(asStream(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			throw new TwitterException("Unsupported encoding", ex);
		}
	}

	public final InputStream asStream() throws TwitterException {
		if (streamConsumed) {
			throw new IllegalStateException("Stream has already been consumed.");
		}
		return getInputStreamInternal();
	}

	public final JSONArray asJSONArray() throws TwitterException {
		try {
			return new JSONArray(asString());
		} catch (JSONException ex) {
			throw new TwitterException(ex);
		}
	}

	public final JSONObject asJSONObject() throws TwitterException {
		try {
			return new JSONObject(asString());
		} catch (JSONException ex) {
			throw new TwitterException(ex);
		}
	}

	/**
	 * Returns the response body as string.<br>
	 * Disconnects the internal HttpURLConnection silently.
	 * 
	 * @return response body
	 * @throws TwitterException
	 */
	public final String asString() throws TwitterException {
		if (null == responseAsString) {
			InputStream stream = null;
			try {
				stream = asStream();
				if (stream == null) {
					return null;
				}
				responseAsString = IOUtil.getStringFromStream(stream, "UTF-8");
				IOUtil.closeSliently(stream);
				streamConsumed = true;
			} catch (final OutOfMemoryError oome) {
				throw new TwitterException(oome.getMessage(), oome);
			} catch (final IOException ioe) {
				throw new TwitterException(ioe.getMessage(), ioe);
			} finally {
				IOUtil.closeSliently(stream);
				disconnectForcibly();
			}
		}
		return responseAsString;
	}

	public abstract void disconnect() throws IOException;

	public abstract long getContentLength();

	public abstract String getResponseHeader(String name);

	public abstract Hashtable getResponseHeaderFields();

	public abstract int getStatusCode();

	private void disconnectForcibly() {
		try {
			disconnect();
		} catch (final Exception ignore) {
		}
	}
}
