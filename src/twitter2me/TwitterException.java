/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter2me;

import java.io.IOException;
import twitter2me.http.HttpRequest;
import twitter2me.http.HttpResponse;

/**
 *
 * @author mariotaku
 */
public final class TwitterException extends Exception {

	private final Throwable cause;
	private final HttpResponse resp;
	private final HttpRequest req;

	public TwitterException() {
		this(null, null);
	}
	
	public TwitterException(String message) {
		this(message, null);
	}
	
	public TwitterException(Throwable cause) {
		this(null, cause);
	}
	
	public TwitterException(String message, Throwable cause) {
		this(message, cause, null, null);
	}
	
	public TwitterException(String message, HttpRequest req, HttpResponse resp) {
		this(message, null, req, resp);
	}
	
	private TwitterException(String message, Throwable cause, HttpRequest req, HttpResponse resp) {
		super(cause != null ? cause.getMessage() : message);
		this.cause = cause;
		this.req = req;
		this.resp = resp;
	}
	
	public Throwable getCause() {
		return cause;
	}
	
	public HttpResponse getHttpResponse() {
		return resp;
	}
	
	public HttpRequest getHttpRequest() {
		return req;
	}
	
	public int getStatusCode() {
		return resp != null ? resp.getStatusCode() : -1;
	}

	public boolean isCausedByNetworkIssue() {
		return cause instanceof IOException;
	}
	
}
