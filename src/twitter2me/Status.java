/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter2me;

import java.util.Date;

/**
 *
 * @author mariotaku
 */
/**
 * A data interface representing one single status of a user.
 * 
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public interface Status extends TwitterResponse, EntitySupport {

	/**
	 * Returns an array of contributors, or null if no contributor is associated
	 * with this status.
	 * 
	 * @since Twitter4J 2.2.3
	 */
	long[] getContributors();

	/**
	 * returns the created_at
	 * 
	 * @return the created_at
	 */
	Date getCreatedAt();

	long getCurrentUserRetweet();

	/**
	 * Returns The location that this tweet refers to if available.
	 * 
	 * @return returns The location that this tweet refers to if available (can
	 *         be null)
	 */
	GeoLocation getGeoLocation();

	/**
	 * returns the status id of the tweet
	 * 
	 * @return the status id
	 */
	long getId();

	/**
	 * Returns the in_reply_to_screen_name
	 * 
	 * @return the in_in_reply_to_screen_name
	 * @since Twitter4J 2.0.4
	 */
	String getInReplyToScreenName();

	/**
	 * Returns the in_reply_tostatus_id
	 * 
	 * @return the in_reply_tostatus_id
	 */
	long getInReplyToStatusId();

	/**
	 * Returns the in_reply_user_id
	 * 
	 * @return the in_reply_tostatus_id
	 * @since Twitter4J 1.0.4
	 */
	long getInReplyToUserId();

	/**
	 * Returns the place associated with the Tweet.
	 * 
	 * @return The place associated with the Tweet
	 */
	Place getPlace();

	/**
	 * returns the raw text
	 * 
	 * @return the raw text
	 */
	String getRawText();

	/**
	 * Returns the number of times this tweet has been retweeted, or -1 when the
	 * tweet was created before this feature was enabled.
	 * 
	 * @return the retweet count.
	 */
	long getRetweetCount();

	/**
	 * @since Twitter4J 2.1.0
	 */
	Status getRetweetedStatus();

	/**
	 * returns the source of the tweet
	 * 
	 * @return the source of the tweet
	 */
	String getSource();

	/**
	 * returns the text
	 * 
	 * @return the text
	 */
	String getText();

	/**
	 * Return the user associated with the status.<br>
	 * This can be null if the instance if from User.getStatus().
	 * 
	 * @return the user
	 */
	User getUser();

	/**
	 * Test if the status is favorited
	 * 
	 * @return true if favorited
	 * @since Twitter4J 1.0.4
	 */
	boolean isFavorited();

	boolean isPossiblySensitive();

	/**
	 * @since Twitter4J 2.0.10
	 */
	boolean isRetweet();

	/**
	 * Returns true if the authenticating user has retweeted this tweet, or
	 * false when the tweet was created before this feature was enabled.
	 * 
	 * @return whether the authenticating user has retweeted this tweet.
	 * @since Twitter4J 2.1.4
	 */
	boolean isRetweetedByMe();

	/**
	 * Test if the status is truncated
	 * 
	 * @return true if truncated
	 * @since Twitter4J 1.0.4
	 */
	boolean isTruncated();

}
