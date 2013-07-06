/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mariotaku.twidere.util.http;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.SocketConnection;
import org.sun.midp.pki.EmptyCertStore;
import org.sun.midp.ssl.SSLStreamConnection;
import twitter2me.internal.util.HttpUrl;

/**
 *
 * @author mariotaku
 */
public class UnsafeHttpsConnection implements HttpConnection {

	private final String host;
	private final int port;
	private final InputStream tcpIn, in;
	private final OutputStream tcpOut, out;
	
	private UnsafeHttpsConnection(final String host, final int port, final InputStream tcpIn, final OutputStream tcpOut) {
		this.host = host;
		this.port = port;
		this.tcpIn = tcpIn;
		this.tcpOut = tcpOut;
		this.in = new HttpsInputStream(tcpIn);
		this.out = new HttpsOutputStream(tcpOut);
	}
	
	public InputStream openInputStream() throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public DataInputStream openDataInputStream() throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void close() throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public OutputStream openOutputStream() throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public DataOutputStream openDataOutputStream() throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	public static UnsafeHttpsConnection open(final String url_string) throws IOException {
		final HttpUrl url = new HttpUrl(url_string);
		final String host = url.getHost();
		final int url_port = url.getPort();
		final int port = url_port < 0 ? 443 : url_port;
		final SocketConnection tcp = (SocketConnection) Connector.open("socket://" + host + ":" + port);
		tcp.setSocketOption(SocketConnection.DELAY, 0);
		final InputStream tcpIn = tcp.openInputStream();
		final OutputStream tcpOut = tcp.openOutputStream();
		final SSLStreamConnection sc = new SSLStreamConnection(host, port, tcpIn, tcpOut, EmptyCertStore.getInstance());
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public String getURL() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public String getProtocol() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public String getHost() {
		return host;
	}

	public String getFile() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public String getRef() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public String getQuery() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public int getPort() {
		return port;
	}

	public String getRequestMethod() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void setRequestMethod(String method) throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public String getRequestProperty(String key) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void setRequestProperty(String key, String value) throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public int getResponseCode() throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public String getResponseMessage() throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public long getExpiration() throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public long getDate() throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public long getLastModified() throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public String getHeaderField(String name) throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public int getHeaderFieldInt(String name, int def) throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public long getHeaderFieldDate(String name, long def) throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public String getHeaderField(int n) throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public String getHeaderFieldKey(int n) throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public String getType() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public String getEncoding() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public long getLength() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	private static final class HttpsInputStream extends InputStream {

		private final InputStream encrypted;

		HttpsInputStream(final InputStream encrypted) {
			this.encrypted = encrypted;
		}
		
		public int read() throws IOException {
			return encrypted.read();
		}
		
	}
	
	private static final class HttpsOutputStream extends OutputStream {

		private final OutputStream encrypted;

		HttpsOutputStream(final OutputStream encrypted) {
			this.encrypted = encrypted;
		}

		public void write(int b) throws IOException {
			encrypted.write(b);
		}
		
	}
	
}
