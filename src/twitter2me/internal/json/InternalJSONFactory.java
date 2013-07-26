/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter2me.internal.json;

import org.json.me.JSONObject;
import twitter2me.ResponseList;
import twitter2me.Status;
import twitter2me.TwitterException;
import twitter2me.User;
import twitter2me.http.HttpResponse;

/**
 *
 * @author mariotaku
 */
public interface InternalJSONFactory {

	public User createUser(HttpResponse resp) throws TwitterException;

	public User createUser(JSONObject json) throws TwitterException;

	public Status createStatus(HttpResponse resp) throws TwitterException;

	public Status createStatus(JSONObject json) throws TwitterException;

	public ResponseList createStatusesList(HttpResponse res) throws TwitterException;
}
