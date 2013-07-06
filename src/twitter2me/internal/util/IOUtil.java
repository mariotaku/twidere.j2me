/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter2me.internal.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author mariotaku
 */
public class IOUtil {

	public static void closeSliently(final InputStream s) {
		if (s != null) {
			try {
				s.close();
			} catch (IOException ex) {
			}
		}
	}

	public static void closeSliently(final OutputStream s) {
		if (s != null) {
			try {
				s.close();
			} catch (IOException ex) {
			}
		}
	}

	public static void closeSliently(final Reader r) {
		if (r != null) {
			try {
				r.close();
			} catch (IOException ex) {
			}
		}
	}

	/**
	 * Constructs a String using the data read from the passed InputStream.
	 * Data is read using a 1024-chars buffer. Each char is created using the passed 
	 * encoding from one or more bytes.
	 * 
	 * <p>If passed encoding is null, then the default BlackBerry encoding (ISO-8859-1) is used.</p>
	 * 
	 * BlackBerry platform supports the following character encodings:
	 * <ul>
	 * <li>"ISO-8859-1"</li>
	 * <li>"UTF-8"</li>
	 * <li>"UTF-16BE"</li>
	 * <li>"UTF-16LE"</li>
	 * <li>"US-ASCII"</li>
	 * </ul>
	 * 
	 * @param in - InputStream to read data from.
	 * @param encoding - String representing the desired character encoding, can be null.
	 * @return String created using the char data read from the passed InputStream.
	 * @throws IOException if an I/O error occurs.
	 * @throws UnsupportedEncodingException if encoding is not supported.
	 */
	public static String getStringFromStream(InputStream in, String encoding) throws IOException {
		final InputStreamReader reader;
		if (encoding == null) {
			reader = new InputStreamReader(in);
		} else {
			reader = new InputStreamReader(in, encoding);
		}

		final StringBuffer sb = new StringBuffer();

		final char[] buf = new char[1024];
		int len;
		while ((len = reader.read(buf)) > 0) {
			sb.append(buf, 0, len);
		}
		return sb.toString();
	}

	public static String getDefaultEncoding() {
		final String defEnc = System.getProperty("microedition.encoding");
		if (defEnc == null || defEnc.trim().length() == 0) {
			return "UTF-8";
		} else {
			return defEnc;
		}
	}
}
