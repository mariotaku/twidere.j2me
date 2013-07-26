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
public interface User extends TwitterResponse {

	Date getCreatedAt();

	/**
	 * Returns the description of the user
	 * 
	 * @return the description of the user
	 */
	String getDescription();

	URLEntity[] getDescriptionEntities();

	int getFavouritesCount();

	/**
	 * Returns the number of followers
	 * 
	 * @return the number of followers
	 * @since Twitter4J 1.0.4
	 */
	int getFollowersCount();

	int getFriendsCount();

	/**
	 * Returns the id of the user
	 * 
	 * @return the id of the user
	 */
	long getId();

	/**
	 * Returns the preferred language of the user
	 * 
	 * @return the preferred language of the user
	 * @since Twitter4J 2.1.2
	 */
	String getLang();

	/**
	 * Returns the number of public lists the user is listed on, or -1 if the
	 * count is unavailable.
	 * 
	 * @return the number of public lists the user is listed on.
	 * @since Twitter4J 2.1.4
	 */
	int getListedCount();

	/**
	 * Returns the location of the user
	 * 
	 * @return the location of the user
	 */
	String getLocation();

	/**
	 * Returns the name of the user
	 * 
	 * @return the name of the user
	 */
	String getName();

	String getProfileBackgroundColor();

	String getProfileBackgroundImageUrl();

	String getProfileBackgroundImageUrlHttps();

	String getProfileBannerImageUrl();

	/**
	 * Returns the profile image url of the user
	 * 
	 * @return the profile image url of the user
	 */
	String getProfileImageURL();

	/**
	 * Returns the profile image url of the user, served over SSL
	 * 
	 * @return the profile image url of the user, served over SSL
	 */
	String getProfileImageUrlHttps();

	String getProfileLinkColor();

	String getProfileSidebarBorderColor();

	String getProfileSidebarFillColor();

	String getProfileTextColor();

	/**
	 * Returns the screen name of the user
	 * 
	 * @return the screen name of the user
	 */
	String getScreenName();

	/**
	 * Returns the current status of the user<br>
	 * This can be null if the instance if from Status.getUser().
	 * 
	 * @return current status of the user
	 * @since Twitter4J 2.1.1
	 */
	Status getStatus();

	int getStatusesCount();

	String getTimeZone();

	/**
	 * Returns the url of the user
	 * 
	 * @return the url of the user
	 */
	String getURL();

	URLEntity[] getURLEntities();

	int getUtcOffset();

	/**
	 * Tests if the user is enabling contributors
	 * 
	 * @return if the user is enabling contributors
	 * @since Twitter4J 2.1.2
	 */
	boolean isContributorsEnabled();

	boolean isDefaultProfileImage();

	boolean isFollowing();

	/**
	 * Returns true if the authenticating user has requested to follow this
	 * user, otherwise false.
	 * 
	 * @return true if the authenticating user has requested to follow this
	 *         user.
	 * @since Twitter4J 2.1.4
	 */
	boolean isFollowRequestSent();

	/**
	 * @return the user is enabling geo location
	 * @since Twitter4J 2.0.10
	 */
	boolean isGeoEnabled();

	boolean isProfileBackgroundTiled();

	boolean isProfileUseBackgroundImage();

	/**
	 * Test if the user status is protected
	 * 
	 * @return true if the user status is protected
	 */
	boolean isProtected();

	boolean isShowAllInlineMedia();

	/**
	 * @return returns true if the user is a translator
	 * @since Twitter4J 2.1.9
	 */
	boolean isTranslator();

	/**
	 * @return returns true if the user is a verified celebrity
	 * @since Twitter4J 2.0.10
	 */
	boolean isVerified();
}
