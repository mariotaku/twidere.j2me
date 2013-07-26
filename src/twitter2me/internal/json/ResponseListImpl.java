/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter2me.internal.json;

import repackaged.java.util.ArrayList;
import twitter2me.RateLimitStatus;
import twitter2me.ResponseList;
import twitter2me.http.HttpResponse;
import twitter2me.internal.util.InternalParseUtil;

/**
 *
 * @author mariotaku
 */
class ResponseListImpl extends ArrayList implements ResponseList {

	private transient RateLimitStatus rateLimitStatus = null;
	private transient int accessLevel;

	ResponseListImpl(final HttpResponse res) {
		super();
		init(res);
	}

	ResponseListImpl(final int size, final HttpResponse res) {
		super(size);
		init(res);
	}

	public int getAccessLevel() {
		return accessLevel;
	}

	public RateLimitStatus getRateLimitStatus() {
		return rateLimitStatus;
	}

	public String toString() {
		return "ResponseListImpl{" + "rateLimitStatus=" + rateLimitStatus + ", accessLevel=" + accessLevel + '}';
	}

	private void init(final HttpResponse res) {
		this.rateLimitStatus = RateLimitStatusJSONImpl.createFromResponseHeader(res);
		this.accessLevel = InternalParseUtil.toAccessLevel(res);
	}
}
