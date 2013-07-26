/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter2me;

import twitter2me.auth.Authorization;
import twitter2me.conf.Configuration;
import twitter2me.http.HttpParameter;

/**
 *
 * @author mariotaku
 */
public class TwitterImpl extends TwitterBaseImpl implements Twitter {

	private final HttpParameter INCLUDE_RTS;
	private final HttpParameter INCLUDE_MY_RETWEET;
	private final HttpParameter EXCLUDE_REPLIES;

	/* package */
	TwitterImpl(final Configuration conf, final Authorization auth) {
		super(conf, auth);
		INCLUDE_RTS = new HttpParameter("include_rts", conf.isIncludeRTsEnabled());
		INCLUDE_MY_RETWEET = new HttpParameter("include_my_retweet", 1);
		EXCLUDE_REPLIES = new HttpParameter("exclude_replies", conf.isExcludeRepliesEnabled());
	}

	public ResponseList getHomeTimeline() throws TwitterException {
		return getHomeTimeline(null);
	}

	public ResponseList getHomeTimeline(Paging paging) throws TwitterException {
		ensureAuthorizationEnabled();
		final String url = conf.getRestBaseURL() + ENDPOINT_STATUSES_HOME_TIMELINE;
		final String sign_url = conf.getSigningRestBaseURL() + ENDPOINT_STATUSES_HOME_TIMELINE;
		final HttpParameter[] params = new HttpParameter[] { INCLUDE_ENTITIES, EXCLUDE_REPLIES };
		return getStatusesList(url, sign_url, params, paging);
	}

	public ResponseList getMentionsTimeline() throws TwitterException {
		return getMentionsTimeline(null);
	}

	public ResponseList getMentionsTimeline(Paging paging) throws TwitterException {
		ensureAuthorizationEnabled();
		final String url = conf.getRestBaseURL() + ENDPOINT_STATUSES_MENTIONS_TIMELINE;
		final String sign_url = conf.getSigningRestBaseURL() + ENDPOINT_STATUSES_MENTIONS_TIMELINE;
		final HttpParameter[] params = new HttpParameter[] { INCLUDE_ENTITIES };
		return getStatusesList(url, sign_url, params, paging);
	}

	public ResponseList getRetweetsOfMe() throws TwitterException {
		return getRetweetsOfMe(null);
	}

	public ResponseList getRetweetsOfMe(Paging paging) throws TwitterException {
		ensureAuthorizationEnabled();
		final String url = conf.getRestBaseURL() + ENDPOINT_STATUSES_RETWEETS_OF_ME;
		final String sign_url = conf.getSigningRestBaseURL() + ENDPOINT_STATUSES_RETWEETS_OF_ME;
		return getStatusesList(url, sign_url, null, paging);
	}

	public ResponseList getUserTimeline() throws TwitterException {
		ensureAuthorizationEnabled();
		final String url = conf.getRestBaseURL() + ENDPOINT_STATUSES_USER_TIMELINE;
		final String sign_url = conf.getSigningRestBaseURL() + ENDPOINT_STATUSES_USER_TIMELINE;
		final HttpParameter[] params = new HttpParameter[] { INCLUDE_ENTITIES, EXCLUDE_REPLIES };
		return getStatusesList(url, sign_url, params, null);
	}

	public ResponseList getUserTimeline(long userId) throws TwitterException {
		return getUserTimeline(userId, null);
	}

	public ResponseList getUserTimeline(long userId, Paging paging) throws TwitterException {
		ensureAuthorizationEnabled();
		final String url = conf.getRestBaseURL() + ENDPOINT_STATUSES_USER_TIMELINE;
		final String sign_url = conf.getSigningRestBaseURL() + ENDPOINT_STATUSES_USER_TIMELINE;
		final HttpParameter param = new HttpParameter("user_id", userId);
		final HttpParameter[] params = new HttpParameter[] { INCLUDE_ENTITIES, EXCLUDE_REPLIES, param };
		return getStatusesList(url, sign_url, params, paging);
	}

	public ResponseList getUserTimeline(Paging paging) throws TwitterException {
		ensureAuthorizationEnabled();
		final String url = conf.getRestBaseURL() + ENDPOINT_STATUSES_USER_TIMELINE;
		final String sign_url = conf.getSigningRestBaseURL() + ENDPOINT_STATUSES_USER_TIMELINE;
		final HttpParameter[] params = new HttpParameter[] { INCLUDE_ENTITIES, EXCLUDE_REPLIES };
		return getStatusesList(url, sign_url, params, paging);
	}

	public ResponseList getUserTimeline(String screenName) throws TwitterException {
		return getUserTimeline(screenName, null);
	}

	public ResponseList getUserTimeline(String screenName, Paging paging) throws TwitterException {
		ensureAuthorizationEnabled();
		final String url = conf.getRestBaseURL() + ENDPOINT_STATUSES_USER_TIMELINE;
		final String sign_url = conf.getSigningRestBaseURL() + ENDPOINT_STATUSES_USER_TIMELINE;
		final HttpParameter param = new HttpParameter("screen_name", screenName);
		final HttpParameter[] params = new HttpParameter[] { INCLUDE_ENTITIES, EXCLUDE_REPLIES, param };
		return getStatusesList(url, sign_url, params, paging);
	}

	private ResponseList getStatusesList(final String url, final String sign_url, final HttpParameter[] params1,
			final Paging paging) throws TwitterException {
		final HttpParameter[] params2 = paging != null ? paging.asPostParameterArray() : null;
		return factory.createStatusesList(get(url, sign_url, mergeParameters(params1, params2)));
	}

	public User verifyCredentials() throws TwitterException {
		return super.fillInIDAndScreenName();
	}
}
