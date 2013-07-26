/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter2me.http;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.mariotaku.twidere.util.TextUtils;
import repackaged.java.util.Comparable;
import twitter2me.internal.util.InternalStringUtil;
import twitter2me.internal.util.URLEncoder;

/**
 *
 * @author mariotaku
 */
public class HttpParameter implements Comparable {

	private static final String JPEG = "image/jpeg";
	private static final String GIF = "image/gif";
	private static final String PNG = "image/png";
	private static final String OCTET = "application/octet-stream";
	private final String name, value;
	private final InputStream fileBody;

	public HttpParameter(final String name, final boolean value) {
		this(name, String.valueOf(value));
	}

	public HttpParameter(final String name, final int value) {
		this(name, String.valueOf(value));
	}

	public HttpParameter(final String name, final long value) {
		this(name, String.valueOf(value));
	}

	public HttpParameter(final String name, final String value) {
		this(name, value, null);
	}

	public HttpParameter(final String name, final String value, final InputStream fileBody) {
		this.name = name;
		this.value = value;
		this.fileBody = fileBody;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public InputStream getFileBody() {
		return fileBody;
	}

	public boolean isFile() {
		return fileBody != null && TextUtils.isEmpty(value);
	}

	public static boolean containsFile(final HttpParameter[] params) {
		if (params == null) {
			return false;
		}
		final int length = params.length;
		for (int i = 0; i < length; i++) {
			if (params[i].isFile()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param value string to be encoded
	 * @return encoded string
	 * @see <a href="http://wiki.oauth.net/TestCases">OAuth / TestCases</a>
	 * @see <a
	 *      href="http://groups.google.com/group/oauth/browse_thread/thread/a8398d0521f4ae3d/9d79b698ab217df2?hl=en&lnk=gst&q=space+encoding#9d79b698ab217df2">Space
	 *      encoding - OAuth | Google Groups</a>
	 * @see <a href="http://tools.ietf.org/html/rfc3986#section-2.1">RFC 3986 -
	 *      Uniform Resource Identifier (URI): Generic Syntax - 2.1.
	 *      Percent-Encoding</a>
	 */
	public static String encode(final String value) {
		String encoded = null;
		try {
			encoded = URLEncoder.encode(value, "UTF-8");
		} catch (final UnsupportedEncodingException ignore) {
		}
		final StringBuffer buf = new StringBuffer(encoded.length());
		char focus;
		for (int i = 0; i < encoded.length(); i++) {
			focus = encoded.charAt(i);
			if (focus == '*') {
				buf.append("%2A");
			} else if (focus == '+') {
				buf.append("%20");
			} else if (focus == '%' && i + 1 < encoded.length() && encoded.charAt(i + 1) == '7'
					&& encoded.charAt(i + 2) == 'E') {
				buf.append('~');
				i += 2;
			} else {
				buf.append(focus);
			}
		}
		return buf.toString();
	}

	public static String encodeParameters(final HttpParameter[] httpParams) {
		if (null == httpParams) {
			return "";
		}
		final StringBuffer buf = new StringBuffer();
		for (int j = 0; j < httpParams.length; j++) {
			if (httpParams[j].isFile()) {
				throw new IllegalArgumentException("parameter [" + httpParams[j].name + "]should be text");
			}
			if (j != 0) {
				buf.append("&");
			}
			buf.append(encode(httpParams[j].name)).append("=").append(encode(httpParams[j].value));
		}
		return buf.toString();
	}

	public String getContentType() {
		if (!isFile()) {
			throw new IllegalStateException("not a file");
		}
		final int name_idx = value.lastIndexOf(getFileSeparator());
		if (name_idx == -1) {
			// invalid file name;
			return OCTET;
		}
		final String file_name = value.substring(name_idx + 1);
		final int index = file_name.lastIndexOf('.');
		if (-1 == index) {
			// no extension
			return OCTET;
		} else {
			final String extensions = file_name.substring(index + 1);
			if (extensions.length() == 3) {
				if (InternalStringUtil.equalsIgnoreCase(extensions, "gif"))  {
					return GIF;
				} else if (InternalStringUtil.equalsIgnoreCase(extensions, "png")) {
					return PNG;
				} else if (InternalStringUtil.equalsIgnoreCase(extensions, "jpg")) {
					return JPEG;
				}
			} else if (extensions.length() == 4) {
				if (InternalStringUtil.equalsIgnoreCase(extensions, "jpeg")) {
					return JPEG;
				}
			}
		}
		return OCTET;
	}
	
	private static char getFileSeparator() {
		final String separator = System.getProperty("file.separator");
		if (TextUtils.isEmpty(separator)) {
			return '/';
		}
		return separator.charAt(0);
	}

	public String toString() {
		return "HttpParameter{" + "name=" + name + ", value=" + value + ", isFile=" + (fileBody != null) + '}';
	}

	public int compareTo(Comparable another) {
		return name.compareTo(((HttpParameter) another).getName());
	}
}
