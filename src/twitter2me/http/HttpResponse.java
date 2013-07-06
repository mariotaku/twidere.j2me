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

	protected abstract InputStream getInputStream();
	
	private InputStream getInputStreamInternal() {
		if (is == null) {
			return is = getInputStream();
		} else {
			return is;
		}
	}

	public Reader asReader() {
		try {
			return new InputStreamReader(asStream(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			return null;
		}
	}

	
	public InputStream asStream() {
		if (streamConsumed) {
			throw new IllegalStateException("Stream has already been consumed.");
		}
		return getInputStreamInternal();
	}

	/**
	 * Returns the response body as string.<br>
	 * Disconnects the internal HttpURLConnection silently.
	 * 
	 * @return response body
	 * @throws TwitterException
	 */
	public String asString() throws TwitterException {
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
