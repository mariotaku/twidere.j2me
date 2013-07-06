/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter2me.internal.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * Utility class for  form encoding.this class is modified form java.net.URLEncoder so that it can work well in cldc env.
 * This class contains static methods
 * for converting a String to the <CODE>application/x-www-form-urlencoded</CODE> MIME
 * format. For more information about HTML form encoding, consult the HTML 
 * <A HREF="http://www.w3.org/TR/html4/">specification</A>. 
 *
 * <p>
 * When encoding a String, the following rules apply:
 *
 * <p>
 * <ul>
 * <li>The alphanumeric characters "<code>a</code>" through
 *     "<code>z</code>", "<code>A</code>" through
 *     "<code>Z</code>" and "<code>0</code>" 
 *     through "<code>9</code>" remain the same.
 * <li>The special characters "<code>.</code>",
 *     "<code>-</code>", "<code>*</code>", and
 *     "<code>_</code>" remain the same. 
 * <li>The space character "<code> </code>" is
 *     converted into a plus sign "<code>+</code>".
 * <li>All other characters are unsafe and are first converted into
 *     one or more bytes using some encoding scheme. Then each byte is
 *     represented by the 3-character string
 *     "<code>%<i>xy</i></code>", where <i>xy</i> is the
 *     two-digit hexadecimal representation of the byte. 
 *     The recommended encoding scheme to use is UTF-8. However, 
 *     for compatibility reasons, if an encoding is not specified, 
 *     then the default encoding of the platform is used.
 * </ul>
 *
 * <p>
 * For example using UTF-8 as the encoding scheme the string "The
 * string ü@foo-bar" would get converted to
 * "The+string+%C3%BC%40foo-bar" because in UTF-8 the character
 * ü is encoded as two bytes C3 (hex) and BC (hex), and the
 * character @ is encoded as one byte 40 (hex).
 *
 * @author  mingjava
 * @version 0.1 05/06/2006
 * @since   httpme 0.1
 */
public class URLEncoder {

	/** The characters which do not need to be encoded. */
	private static boolean[] dontNeedEncoding;
	private static final int caseDiff = ('a' - 'A');

	static {
		dontNeedEncoding = new boolean[256];
		int i;
		for (i = 'a'; i <= 'z'; i++) {
			dontNeedEncoding[i] = true;
		}
		for (i = 'A'; i <= 'Z'; i++) {
			dontNeedEncoding[i] = true;
		}
		for (i = '0'; i <= '9'; i++) {
			dontNeedEncoding[i] = true;
		}
		dontNeedEncoding[' '] = true; // encoding a space to a + is done in the encode() method
		dontNeedEncoding['-'] = true;
		dontNeedEncoding['_'] = true;
		dontNeedEncoding['.'] = true;
		dontNeedEncoding['*'] = true;
	}

	public static final int MIN_RADIX = 2;
	/**
	 * The maximum radix available for conversion to and from strings.
	 */
	public static final int MAX_RADIX = 36;

	/**
	 * The class is not meant to be instantiated.
	 */
	private URLEncoder() {
	}

	/**
	 * Translates a string into "<CODE>x-www-form-urlencoded</CODE>"
	 * format.This method uses the platform's default encoding
	 * as the encoding scheme to obtain the bytes for unsafe characters.
	 *
	 * @param  s the string to be translated.
	 *
	 * @return The resulting string.
	 */
	public static String encode(String s) throws UnsupportedEncodingException {
		return encode(s, IOUtil.getDefaultEncoding());
	}

	/**
	 * Translates a string into <code>application/x-www-form-urlencoded</code>
	 * format using a specific encoding scheme. This method uses the
	 * supplied encoding scheme to obtain the bytes for unsafe
	 * characters.
	 * <p>
	 * <em><strong>Note:</strong> The <a href=
	 * "http://www.w3.org/TR/html40/appendix/notes.html#non-ascii-chars">
	 * World Wide Web Consortium Recommendation</a> states that
	 * UTF-8 should be used. Not doing so may introduce
	 * incompatibilites.</em>
	 *
	 * @param   s   <code>String</code> to be translated.
	 * @param   enc   The name of a supported character encoding such as UTF-8
	 * @return  the translated <code>String</code>.
	 */
	public static String encode(String s, String enc) throws UnsupportedEncodingException {

		boolean needToChange = false;
		boolean wroteUnencodedChar = false;
		int maxBytesPerChar = 10; // rather arbitrary limit, but safe for now
		StringBuffer out = new StringBuffer(s.length());
		ByteArrayOutputStream buf = new ByteArrayOutputStream(maxBytesPerChar);
		OutputStreamWriter writer = new OutputStreamWriter(buf, enc);

		for (int i = 0; i < s.length(); i++) {
			int c = (int) s.charAt(i);
			//System.out.println("Examining character: " + c);
			if (c < 256 && dontNeedEncoding[c]) {
				if (c == ' ') {
					c = '+';
					needToChange = true;
				}
				//System.out.println("Storing: " + c);
				out.append((char) c);
				wroteUnencodedChar = true;
			} else {
				// convert to external encoding before hex conversion
				try {
					if (wroteUnencodedChar) { // Fix for 4407610
						writer = new OutputStreamWriter(buf, enc);
						wroteUnencodedChar = false;
					}
					if (writer != null) {
						writer.write(c);
					}
					/*
					 * If this character represents the start of a Unicode
					 * surrogate pair, then pass in two characters. It's not
					 * clear what should be done if a bytes reserved in the
					 * surrogate pairs range occurs outside of a legal
					 * surrogate pair. For now, just treat it as if it were
					 * any other character.
					 */
					if (c >= 0xD800 && c <= 0xDBFF) {
						/*
						System.out.println(Integer.toHexString(c)
						+ " is high surrogate");
						 */
						if ((i + 1) < s.length()) {
							int d = (int) s.charAt(i + 1);
							/*
							System.out.println("\tExamining "
							+ Integer.toHexString(d));
							 */
							if (d >= 0xDC00 && d <= 0xDFFF) {
								/*
								System.out.println("\t"
								+ Integer.toHexString(d)
								+ " is low surrogate");
								 */
								writer.write(d);
								i++;
							}
						}
					}
					writer.flush();
				} catch (IOException e) {
					buf.reset();
					continue;
				}
				byte[] ba = buf.toByteArray();
				for (int j = 0; j < ba.length; j++) {
					out.append('%');
					char ch = forDigit((ba[j] >> 4) & 0xF, 16);
					if (isLetter(ch)) {
						ch -= caseDiff;
					}
					out.append(ch);

					ch = forDigit((ba[j] & 0xF), 16);
					//ch = forDigit(ba[j] & 0xF, 16);
					if (isLetter(ch)) {
						ch -= caseDiff;
					}
					out.append(ch);
				}
				buf.reset();
				needToChange = true;
			}
		}

		return (needToChange ? out.toString() : s);
	}

	private static boolean isLetter(char c) {
		if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
			return true;
		}
		return false;
	}

	private static char forDigit(int digit, int radix) {
		if ((digit >= radix) || (digit < 0)) {
			return '\0';
		}
		if ((radix < MIN_RADIX) || (radix > MAX_RADIX)) {
			return '\0';
		}
		if (digit < 10) {
			return (char) ('0' + digit);
		}
		return (char) ('a' - 10 + digit);
	}
}