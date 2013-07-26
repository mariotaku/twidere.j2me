/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mariotaku.twidere;

/**
 *
 * @author mariotaku
 */
public interface Constants {

	public static final String TWITTER_CONSUMER_KEY = "dCA5gj0kGRBWsmogHWaSg";
	public static final String TWITTER_CONSUMER_SECRET = "WfexVb0BLnp6hofdXDyIoeaWBhOmRyE1sliDDvKkE";
	public static final String OAUTH_CALLBACK_OOB = "oob";
	public static final String DATASTORE_NAME_ACCOUNTS = "accounts";
	public static final String DATASTORE_NAME_PREFERENCES = "preferences";
	/*
	 * Action ids
	 */
	public static final String ACTION_ID_EDIT_API = "edit_api";
	public static final String ACTION_ID_SIGN_IN = "sign_in";
	public static final String ACTION_ID_SETTINGS = "settings";
	public static final String ACTION_ID_BACK = "back";
	public static final String ACTION_ID_MENU_BACK = "menu_back";
	public static final String ACTION_ID_SET_API = "set_api";
	public static final String ACTION_ID_OPEN_SETTINGS = "open_settings";
	
	
	public static final String ACTION_ARGUMENT_NETWORK = "network";
	public static final String ACTION_ARGUMENT_HOST_MAPPING = "host_mapping";
	/**
	 * Widget ids
	 */
	public static final String WIDGET_ID_MENU_BACK_TEXT = "menu_back_text";
	public static final String WIDGET_ID_DARK_THEME = "dark_theme";
	public static final String WIDGET_ID_USER_NAME = "user_name";
	public static final String WIDGET_ID_PASSWORD = "password";
	public static final String WIDGET_ID_USER_NAME_LABEL = "user_name_label";
	public static final String WIDGET_ID_PASSWORD_LABEL = "password_label";
	public static final String WIDGET_ID_GZIP_COMPRESSING = "gzip_compressing";
	/**
	 * I18N text ids
	 */
	public static final String MESSAGE_ID_BACK = "BACK";
	public static final String MESSAGE_ID_EXIT = "EXIT";
	/**
	 * Preference ids
	 */
	public static final String PREFERENCE_ID_DARK_THEME = "dark_theme";
	public static final String PREFERENCE_ID_GZIP_COMPRESSING = "gzip_compressing";
	/**
	 * Data provider keys
	 */
	public static final String DATA_PROVIDER_KEY_AUTH_TYPE = "auth_type";
	public static final String DATA_PROVIDER_KEY_REST_BASE_URL = "rest_base_url";
	public static final String DATA_PROVIDER_KEY_OAUTH_BASE_URL = "oauth_base_url";
	public static final String DATA_PROVIDER_KEY_USER_NAME = "user_name";
	public static final String DATA_PROVIDER_KEY_SCREEN_NAME = "screen_name";
	public static final String DATA_PROVIDER_KEY_PASSWORD = "password";
}
