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
import javax.microedition.io.SecurityInfo;
import javax.microedition.io.SocketConnection;
import javax.microedition.io.StreamConnection;
import repackaged.com.sun.midp.pki.X509Certificate;
import repackaged.com.sun.midp.ssl.SSLStreamConnection;

/**
 *
 * @author mariotaku
 */
public final class UnsafeSSLConnection implements StreamConnection {

	private final SSLStreamConnection sc;

	UnsafeSSLConnection(final String host, final int port) throws IOException {
		final SocketConnection tcp = (SocketConnection) Connector.open("socket://" + host + ":" + port);
		tcp.setSocketOption(SocketConnection.DELAY, 0);
		final InputStream tcpIn = tcp.openInputStream();
		final OutputStream tcpOut = tcp.openOutputStream();
		sc = new SSLStreamConnection(host, port, tcpIn, tcpOut);
	}

	public synchronized OutputStream openOutputStream() throws IOException {
		return sc.openOutputStream();
	}

	public synchronized InputStream openInputStream() throws IOException {
		return sc.openInputStream();
	}

	public DataOutputStream openDataOutputStream() throws IOException {
		return sc.openDataOutputStream();
	}

	public DataInputStream openDataInputStream() throws IOException {
		return sc.openDataInputStream();
	}

	public X509Certificate getServerCertificate() {
		return sc.getServerCertificate();
	}

	public SecurityInfo getSecurityInfo() throws IOException {
		return sc.getSecurityInfo();
	}

	public synchronized void close() throws IOException {
		sc.close();
	}

	public static UnsafeSSLConnection open(final String host, final int port) throws IOException {
		if (host == null && port < 0) {
			return new UnsafeSSLConnection("127.0.0.1", 443);
		} else if (host != null) {
			return new UnsafeSSLConnection(host, 443);
		}
		return new UnsafeSSLConnection(host, port);
	}
}
