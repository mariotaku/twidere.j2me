/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter2me.http.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import javax.microedition.io.HttpConnection;
import twitter2me.http.HttpConfiguration;
import twitter2me.http.HttpResponse;
import twitter2me.http.StreamingGZIPInputStream;

/**
 *
 * @author mariotaku
 */
public final class HttpResponseImpl extends HttpResponse {

	private final HttpConnection conn;
	private final InputStream is;

	HttpResponseImpl(HttpConnection conn, HttpConfiguration conf) throws IOException {
		super(conf);
		this.conn = conn;
		final InputStream is = conn.openInputStream();
		if (is != null && "gzip".equals(conn.getEncoding())) {
			// the response is gzipped
			this.is = new StreamingGZIPInputStream(is);
		} else {
			this.is = is;
		}
	}

	protected InputStream getInputStream() {
		return is;
	}

	public long getContentLength() {
		return conn.getLength();
	}

	public String getResponseHeader(final String name) {
		try {
			return conn.getHeaderField(name);
		} catch (IOException ex) {
			return null;
		}
	}

	public Hashtable getResponseHeaderFields() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public int getStatusCode() {
		try {
			return conn.getResponseCode();
		} catch (IOException ex) {
			return -1;
		}
	}

	public void disconnect() throws IOException {
		conn.close();
	}
}
