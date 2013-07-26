/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter2me.internal.json;

import org.json.me.JSONObject;
import twitter2me.PagableResponseList;
import twitter2me.http.HttpResponse;
import twitter2me.internal.util.InternalParseUtil;

/**
 *
 * @author mariotaku
 */
class PagableResponseListImpl extends ResponseListImpl implements PagableResponseList {

	private final long previousCursor;
	private final long nextCursor;

	PagableResponseListImpl(final int size, final JSONObject json, final HttpResponse res) {
		super(size, res);
		this.previousCursor = InternalParseUtil.getLong("previous_cursor", json);
		this.nextCursor = InternalParseUtil.getLong("next_cursor", json);
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNextCursor() {
		return nextCursor;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getPreviousCursor() {
		return previousCursor;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasNext() {
		return 0 != nextCursor;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasPrevious() {
		return 0 != previousCursor;
	}
}
