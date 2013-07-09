/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mariotaku.twidere.frame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.SecureConnection;
import javax.microedition.io.SocketConnection;
import javax.microedition.pki.CertificateException;
import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.widget.Screen;
import org.kalmeo.util.frame.Frame;
import org.mariotaku.twidere.Constants;
import org.mariotaku.twidere.util.ArrayUtils;
import repackaged.com.sun.midp.ssl.SSLStreamConnection;

/**
 *
 * @author mariotaku
 */
public class SignInFrame implements Frame, Constants {

	// Static frame instance
	public static final SignInFrame INSTANCE = new SignInFrame();
	private final Screen screen = Kuix.loadScreen("/xml/sign_in.xml", null);

	public boolean onMessage(Object identifier, Object[] arguments) {
		System.out.println("onMessage identifier:" + identifier + ", arguments:[" + ArrayUtils.toString(arguments, ',', true) + "]");
		if (ACTION_IDENTIFIER_EDIT_API.equals(identifier)) {
			Kuix.showPopupBox("/xml/edit_api_popup.xml", null);
			return true;
		} else if (ACTION_IDENTIFIER_SIGN_IN.equals(identifier)) {
			testSSLImpl();
			//testSecureSocket();
			return true;
		}
		return false;
	}

	public void onAdded() {
		screen.setCurrent();
	}

	public void onRemoved() {
	}

	private void testSSLImpl() {
		try {
			final SocketConnection tcp = (SocketConnection) Connector.open("socket://localhost:443");
			tcp.setSocketOption(SocketConnection.DELAY, 0);
			final InputStream tcpIn = tcp.openInputStream();
			final OutputStream tcpOut = tcp.openOutputStream();
			final SSLStreamConnection sc = new SSLStreamConnection("localhost", 443, tcpIn, tcpOut);
			final DataOutputStream os = sc.openDataOutputStream();
			os.writeUTF("GET / HTTP/1.1\n");
			os.writeUTF("\n\n");
			final DataInputStream is = sc.openDataInputStream();
			System.out.println(is.readUTF());
		} catch (IOException e) {
			final String msg = e.getClass().getName() + ":" + e.getMessage();
			System.out.println(msg);
			if (e instanceof CertificateException) {
				final CertificateException ce = (CertificateException) e;
				System.out.println(ce.getCertificate());
				System.out.println("reason:" + ce.getReason());
			}
		}

	}

	private void testSecureSocket() {
		try {
			final SecureConnection sc = (SecureConnection) Connector.open("ssl://localhost:443");
			final DataOutputStream os = sc.openDataOutputStream();
			final DataInputStream is = sc.openDataInputStream();
			os.writeUTF("GET / HTTP/1.1\n");
			os.writeUTF("\n\n");
			System.out.println(is.readChar());
		} catch (IOException e) {
			final String msg = e.getClass().getName() + ":" + e.getMessage();
			System.out.println(msg);
			if (e instanceof CertificateException) {
				final CertificateException ce = (CertificateException) e;
				System.out.println(ce.getCertificate());
				System.out.println("reason:" + ce.getReason());
			}
		}
	}
}
