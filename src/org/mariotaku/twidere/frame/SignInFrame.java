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
import java.security.Key;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.SocketConnection;
import javax.microedition.pki.CertificateException;
import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.widget.Screen;
import org.kalmeo.kuix.widget.TextArea;
import org.kalmeo.util.frame.Frame;
import org.mariotaku.twidere.Constants;
import org.mariotaku.twidere.util.ArrayUtils;
import org.sun.midp.crypto.Cipher;
import org.sun.midp.crypto.RSAPublicKey;
import org.sun.midp.crypto.SecretKey;
import org.sun.midp.pki.EmptyCertStore;
import org.sun.midp.pki.Utils;
import org.sun.midp.ssl.SSLStreamConnection;

/**
 *
 * @author mariotaku
 */
public class SignInFrame implements Frame, Constants {

	private static final String MODULUS = "98:6E:8D:4E:CB:E2:3D:C2:B2:11:8E:76:FD:E3:65:7C:D6:8F:93:9C:4E:A7:CD:80:01:4E:38:72:27:AC:33:ED:DD:6D:50:B6:22:02:F0:7A:EA:F7:BC:5B:9C:52:B7:64:5E:25:C6:82:FE:15:43:C1:F0:80:58:4A:9B:75:D9:06:48:12:6F:A4:6F:F2:77:F8:6E:8F:FB:A5:8C:C7:F2:48:92:F3:59:E6:2D:9E:5A:40:9B:FD:85:50:4C:B7:BB:15:E9:26:2A:0C:E0:E7:FA:73:51:EB:15:54:B2:C0:8D:C9:3A:D0:91:E2:99:64:F2:FC:62:38:34:2F:AF:DF:5E:01";
	private static final String EXPONENT = "01:00:01";
	private static final String TEST_DATA = "01:23:45:67:89:AB:CD:EF";
	private static final String TEST_KEY = "FE:DC:BA:98:76:54:32:10";
	// Static frame instance
	public static final SignInFrame INSTANCE = new SignInFrame();
	private final Screen screen = Kuix.loadScreen("/xml/sign_in.xml", null);

	private static byte[] parseByteData(final String str, final String exp) {
		final String[] raw = split(str, ":");
		final int length = raw.length;
		final byte[] data = new byte[length];
		for (int i = 0; i < length; i++) {
			data[i] = (byte) (Integer.parseInt(raw[i], 16) & 0xFF);
		}
		return data;
	}

	public static String[] split(String original, String regex) {
		int startIndex = 0;
		Vector v = new Vector();
		String[] str = null;
		int index = 0;
		startIndex = original.indexOf(regex);
		while (startIndex < original.length() && startIndex != -1) {
			String temp = original.substring(index, startIndex);
			v.addElement(temp);
			index = startIndex + regex.length();
			startIndex = original.indexOf(regex, startIndex + regex.length());
		}
		v.addElement(original.substring(index + 1 - regex.length()));
		str = new String[v.size()];
		for (int i = 0; i < v.size(); i++) {
			str[i] = (String) v.elementAt(i);
		}
		return str;
	}

	private void testRC4() {
		try {
			final byte[] test = parseByteData(TEST_KEY, ":");
			final Key key = new SecretKey(test, 0, test.length, "RC4");
			final Cipher rsa = Cipher.getInstance("RC4");
			rsa.init(Cipher.ENCRYPT_MODE, key);
			final byte[] input = parseByteData(TEST_DATA, ":");
			final byte[] output = new byte[1024];
			rsa.doFinal(input, 0, input.length, output, 0);
			System.out.println("key:" + key);
			System.out.println(Utils.hexEncode(output));
		} catch (Exception ex) {
			System.out.println(ex.getClass().getName() + ":" + ex.getMessage());
		}
	}

	private void testRSA() {
		try {
			final byte[] modulus = parseByteData(MODULUS, ":"), exponent = parseByteData(EXPONENT, ":");
			final Key key = new RSAPublicKey(modulus, exponent);
			final Cipher rsa = Cipher.getInstance("RSA");
			rsa.init(Cipher.ENCRYPT_MODE, key);
			final byte[] input = parseByteData(TEST_DATA, ":");
			final byte[] output = new byte[1024];
			rsa.doFinal(input, 0, input.length, output, 0);
			System.out.println(Utils.hexEncode(input));
			System.out.println(Utils.hexEncode(output));
		} catch (Exception ex) {
			System.out.println(ex.getClass().getName() + ":" + ex.getMessage());
		}
	}

	public boolean onMessage(Object identifier, Object[] arguments) {
		System.out.println("onMessage identifier:" + identifier + ", arguments:[" + ArrayUtils.toString(arguments, ',', true) + "]");
		if (ACTION_IDENTIFIER_EDIT_API.equals(identifier)) {
			Kuix.showPopupBox("/xml/edit_api_popup.xml", null);
			return true;
		} else if (ACTION_IDENTIFIER_SIGN_IN.equals(identifier)) {
			//testRSA();
			//testRC4();
			testSSLImpl();
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
		final TextArea indicator1 = (TextArea) screen.getWidget("indicator1");
		final TextArea indicator2 = (TextArea) screen.getWidget("indicator2");
		try {
			final SocketConnection tcp = (SocketConnection) Connector.open("socket://www.google.com:443");
			tcp.setSocketOption(SocketConnection.DELAY, 0);
			final InputStream tcpIn = tcp.openInputStream();
			final OutputStream tcpOut = tcp.openOutputStream();
			final SSLStreamConnection sc = new SSLStreamConnection("www.google.com", 443, tcpIn, tcpOut, EmptyCertStore.getInstance());
			final DataOutputStream os = sc.openDataOutputStream();
			os.writeUTF("GET / HTTP/1.1\n");
			os.writeUTF("Host: www.google.com\n");
			os.writeUTF("Connection: close\n");
			os.writeUTF("\n\n");
			final DataInputStream is = sc.openDataInputStream();
			indicator1.setText(is.readUTF());
		} catch (IOException e) {
			final String msg = e.getClass().getName() + ":" + e.getMessage();
			indicator1.setText(msg);
			System.out.println(msg);
			if (e instanceof CertificateException) {
				final CertificateException ce = (CertificateException) e;
				System.out.println(ce.getCertificate());
				System.out.println("reason:" + ce.getReason());
			}
		}
		try {
			final HttpConnection http = (HttpConnection) Connector.open("https://www.google.com/");
			indicator2.setText(http.getResponseMessage());
		} catch (IOException e) {
			final String msg = e.getClass().getName() + ":" + e.getMessage();
			indicator2.setText(msg);
			System.out.println(msg);
			if (e instanceof CertificateException) {
				final CertificateException ce = (CertificateException) e;
				System.out.println(ce.getCertificate());
				System.out.println("reason:" + ce.getReason());
			}
		}
	}
}
