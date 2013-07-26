/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter2me.api;

import twitter2me.TwitterException;
import twitter2me.User;

/**
 *
 * @author mariotaku
 */
public interface UsersResources {
		/**
	 * Returns an HTTP 200 OK response code and a representation of the
	 * requesting user if authentication was successful; returns a 401 status
	 * code and an error message if not. Use this method to test if supplied
	 * user credentials are valid. <br>
	 * This method calls
	 * http://api.twitter.com/1.1/account/verify_credentials.json
	 * 
	 * @return user
	 * @throws twitter4j.TwitterException when Twitter service or network is
	 *             unavailable, or if supplied credential is wrong
	 *             (TwitterException.getStatusCode() == 401)
	 * @see <a
	 *      href="https://dev.twitter.com/docs/api/1.1/get/account/verify_credentials">GET
	 *      account/verify_credentials | Twitter Developers</a>
	 * @since Twitter4J 2.0.0
	 */
	User verifyCredentials() throws TwitterException;
}
