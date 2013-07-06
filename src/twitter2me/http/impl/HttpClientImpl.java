/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter2me.http.impl;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import twitter2me.TwitterException;
import twitter2me.auth.Authorization;
import twitter2me.conf.Configuration;
import twitter2me.http.HostAddressResolver;
import twitter2me.http.HttpClient;
import twitter2me.http.HttpParameter;
import twitter2me.http.HttpRequest;
import twitter2me.http.HttpResponse;
import twitter2me.internal.util.HttpUrl;
import twitter2me.internal.util.IOUtil;
import twitter2me.internal.util.InternalStringUtil;

/**
 *
 * @author mariotaku
 */
public class HttpClientImpl implements HttpClient {

	protected final Configuration conf;
	protected final HostAddressResolver resolver;

	protected HttpClientImpl(Configuration conf) {
		this.conf = conf;
		this.resolver = conf.getHostAddressResolver();
	}

	public HttpResponse get(String url, String sign_url, HttpParameter[] parameters, Authorization authorization) throws TwitterException {
		return request(new HttpRequest(HttpConnection.GET, url, sign_url, parameters, authorization));
	}

	public HttpResponse post(String url, String sign_url, HttpParameter[] parameters, Authorization authorization) throws TwitterException {
		return request(new HttpRequest(HttpConnection.POST, url, sign_url, parameters, authorization));
	}

	private String resolve(final String host) {
		if (resolver == null) {
			return host;
		}
		try {
			final String resolved = resolver.resolve(host);
			if (!InternalStringUtil.isEmpty(resolved)) {
				return resolved;
			}
		} catch (final IOException e) {
		}
		return host;
	}

	public HttpResponse request(HttpRequest req) throws TwitterException {
		final String method = req.getMethod();
		int retriedCount;
		final int retry = conf.getHttpRetryCount() + 1;
		HttpResponse res = null;
		for (retriedCount = 0; retriedCount < retry; retriedCount++) {
			int responseCode = -1;
			try {
				HttpConnection con;
				OutputStream os = null;
				try {
					con = getConnection(req);
					//setHeaders(req, con);
					con.setRequestMethod(method);
					final HttpParameter[] params = req.getParameters();
					if (HttpConnection.POST.equals(method)) {
						if (HttpParameter.containsFile(params)) {
							String boundary = "----Twitter4J-upload" + System.currentTimeMillis();
							con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
							boundary = "--" + boundary;
							os = con.openOutputStream();
							final DataOutputStream out = con.openDataOutputStream();
							final int params_length = params.length;
							for (int i = 0; i < params_length; i++) {
								final HttpParameter param = params[i];
								if (param.isFile()) {
									write(out, boundary + "\r\n");
									write(out, "Content-Disposition: form-data; name=\"" + param.getName()
											+ "\"; filename=\"" + param.getValue() + "\"\r\n");
									write(out, "Content-Type: " + param.getContentType() + "\r\n\r\n");
									final InputStream in = param.getFileBody();
									int buff;
									while ((buff = in.read()) != -1) {
										out.write(buff);
									}
									write(out, "\r\n");
									in.close();
								} else {
									write(out, boundary + "\r\n");
									write(out, "Content-Disposition: form-data; name=\"" + param.getName() + "\"\r\n");
									write(out, "Content-Type: text/plain; charset=UTF-8\r\n\r\n");
									out.write(param.getValue().getBytes("UTF-8"));
									write(out, "\r\n");
								}
							}
							write(out, boundary + "--\r\n");
							write(out, "\r\n");

						} else {
							con.setRequestProperty(HEADER_KEY_CONTENT_TYPE, "application/x-www-form-urlencoded");
							final String postParam = HttpParameter.encodeParameters(req.getParameters());
							final byte[] bytes = postParam.getBytes("UTF-8");
							con.setRequestProperty(HEADER_KEY_CONTENT_LENGTH, Integer.toString(bytes.length));
							os = con.openOutputStream();
							os.write(bytes);
						}
						os.flush();
						os.close();
					}
					res = new HttpResponseImpl(con, conf);
					responseCode = con.getResponseCode();
					if (responseCode < OK || responseCode > ACCEPTED) {
						if (responseCode == ENHANCE_YOUR_CLAIM || responseCode == BAD_REQUEST
								|| responseCode < INTERNAL_SERVER_ERROR || retriedCount == conf.getHttpRetryCount()) {
							throw new TwitterException(res.asString(), req, res);
						}
					} else {
						break;
					}
				} finally {
					IOUtil.closeSliently(os);
				}
			} catch (final IOException ioe) {
				// connection timeout or read timeout
				if (retriedCount == conf.getHttpRetryCount()) {
					throw new TwitterException(ioe.getMessage(), req, res);
				}
			} catch (final NullPointerException e) {
				// This exception will be thown when URL is invalid.
				throw new TwitterException("The URL requested is invalid.", e);
			} catch (final OutOfMemoryError e) {
				throw new TwitterException(e);
			}
			try {
				Thread.sleep(conf.getHttpRetryIntervalSeconds() * 1000);
			} catch (final InterruptedException ignore) {
				// nothing to do
			}
		}
		return res;
	}

	protected HttpConnection getConnection(final HttpRequest req) throws TwitterException {
		if (req == null) {
			throw new NullPointerException();
		}
		final String url = req.getURL();
		final String userAgent = conf.getHttpUserAgent();
		final HttpUrl httpUrl;
		try {
			httpUrl = new HttpUrl(url);
		} catch (IllegalArgumentException e) {
			throw new TwitterException("Illegal url " + url, e);
		}
		final String host = httpUrl.getHost(), address = resolve(host);
		if (!InternalStringUtil.isEmpty(address)) {
			httpUrl.setHost(address);
		}
		try {
			final HttpConnection http = (HttpConnection) createConnection(httpUrl.toString());
			if (http == null) {
				throw new TwitterException();
			}
			final Authorization authorization = req.getAuthorization();
			final String authorizationHeader = authorization != null ? authorization.getAuthorizationHeader(req) : null;
			if (!InternalStringUtil.isEmpty(authorizationHeader)) {
				http.setRequestProperty(HEADER_KEY_AUTHORIZATION, authorizationHeader);
			}
			if (!InternalStringUtil.isEmpty(userAgent)) {
				http.setRequestProperty(HEADER_KEY_USER_AGENT, userAgent);
			}
			http.setRequestProperty("X-Twitter-Client-Version", conf.getClientVersion());
			http.setRequestProperty("X-Twitter-Client-URL", conf.getClientURL());
			http.setRequestProperty("X-Twitter-Client", conf.getClientName());
			if (!host.equals(address)) {
				http.setRequestProperty(HEADER_KEY_HOST, host);
			}
			if (conf.isGZIPEnabled()) {
				http.setRequestProperty(HEADER_KEY_ACCEPT_ENCODING, "gzip");
			}
			return http;
		} catch (final IOException e) {
			throw new TwitterException(e);
		}
	}
	
	protected Connection createConnection(final String url) throws IOException {
		return Connector.open(url);
	}

	private static void write(final DataOutputStream out, final String outStr) throws IOException {
		writeBytes(out, outStr);
	}

	private static void writeBytes(final DataOutputStream out, final String s) throws IOException {
		int len = s.length();
		for (int i = 0; i < len; i++) {
			out.write((byte) s.charAt(i));
		}
	}
	
	public static HttpClient getInstance(final Configuration conf) {
		return new HttpClientImpl(conf);
	}
}
