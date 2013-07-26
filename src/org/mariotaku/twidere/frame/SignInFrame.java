/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mariotaku.twidere.frame;

import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.widget.PopupBox;
import org.kalmeo.kuix.widget.Screen;
import org.kalmeo.kuix.widget.Text;
import org.kalmeo.kuix.widget.TextField;
import org.kalmeo.kuix.widget.Widget;
import org.kalmeo.util.frame.FrameHandler;
import org.mariotaku.twidere.midlet.TwidereMIDlet;
import org.mariotaku.twidere.model.AuthType;
import org.mariotaku.twidere.util.DataStore;
import org.mariotaku.twidere.provider.HashMapDataProvider;
import org.mariotaku.twidere.util.OAuthPasswordAuthenticator;
import org.mariotaku.twidere.util.ParseUtils;
import org.mariotaku.twidere.util.TwidereHostAddressResolver;
import org.mariotaku.twidere.util.Utils;
import org.mariotaku.twidere.util.http.TwidereHttpClientFactory;
import twitter2me.Twitter;
import twitter2me.TwitterConstants;
import twitter2me.TwitterFactory;
import twitter2me.auth.AccessToken;
import twitter2me.conf.ConfigurationBuilder;

/**
 *
 * @author mariotaku
 */
public class SignInFrame extends BaseFrame {

	private final HashMapDataProvider provider = new HashMapDataProvider();
	private DataStore preferences;

	public SignInFrame(final BaseFrame parent) {
		super(parent);
		provider.put(DATA_PROVIDER_KEY_AUTH_TYPE, String.valueOf(AuthType.OAUTH));
		provider.put(DATA_PROVIDER_KEY_REST_BASE_URL, TwitterConstants.DEFAULT_REST_BASE_URL);
		provider.put(DATA_PROVIDER_KEY_OAUTH_BASE_URL, TwitterConstants.DEFAULT_OAUTH_BASE_URL);
	}

	protected Screen createScreen() {
		return Kuix.loadScreen("sign_in.xml", null);
	}

	protected void showScreen(Screen screen) {
		super.showScreen(screen);
		final TwidereMIDlet twidere = TwidereMIDlet.getActiveInstance();
		preferences = twidere.getPreferences();
		final Text menu_back_text = (Text) getWidget(WIDGET_ID_MENU_BACK_TEXT);
		if (menu_back_text != null) {
			final String text = Kuix.getMessage(getParent() != null ? MESSAGE_ID_BACK : MESSAGE_ID_EXIT);
			menu_back_text.setText(text);
		}
	}

	public boolean onMessage(Object identifier, Object[] arguments) {
		final FrameHandler handler = Kuix.getFrameHandler();
		if (ACTION_ID_EDIT_API.equals(identifier)) {
			Kuix.showPopupBox("edit_api_popup.xml", provider);
		} else if (ACTION_ID_SETTINGS.equals(identifier)) {
			handler.pushFrame(new SettingsFrame(this));
		} else if (ACTION_ID_MENU_BACK.equals(identifier)) {
			if (getParent() != null) {
				handler.removeFrame(this);
			} else {
				Kuix.exit();
			}
		} else if (ACTION_ID_SET_API.equals(identifier)) {
			final byte type = ParseUtils.parseByte(arguments[0]);
			if (AuthType.isValid(type)) {
				provider.put(DATA_PROVIDER_KEY_AUTH_TYPE, String.valueOf(type));
				final Widget user_name = getWidget(WIDGET_ID_USER_NAME);
				final Widget user_name_label = getWidget(WIDGET_ID_USER_NAME_LABEL);
				final Widget password = getWidget(WIDGET_ID_PASSWORD);
				final Widget password_label = getWidget(WIDGET_ID_PASSWORD_LABEL);
				final boolean is_twip_o_mode = type == AuthType.TWIP_O_MODE;
				user_name.setVisible(!is_twip_o_mode);
				user_name_label.setVisible(!is_twip_o_mode);
				password.setVisible(!is_twip_o_mode);
				password_label.setVisible(!is_twip_o_mode);
			}
			final String rest = ParseUtils.parseString(arguments[1]);
			if (Utils.isValidUrl(rest)) {
				provider.put(DATA_PROVIDER_KEY_REST_BASE_URL, rest);
			}
			final String oauth = ParseUtils.parseString(arguments[2]);
			if (Utils.isValidUrl(oauth)) {
				provider.put(DATA_PROVIDER_KEY_OAUTH_BASE_URL, oauth);
			}
		} else if (ACTION_ID_SIGN_IN.equals(identifier)) {
			final TextField user_name = (TextField) getWidget(WIDGET_ID_USER_NAME);
			final TextField password = (TextField) getWidget(WIDGET_ID_PASSWORD);
			provider.put(DATA_PROVIDER_KEY_USER_NAME, user_name.getText());
			provider.put(DATA_PROVIDER_KEY_PASSWORD, password.getText());
			new Thread() {

				public void run() {
					final PopupBox popup = Kuix.showPopupBox("progressbar_indeterminate_popup.xml", null);
					testTwitter();
					popup.remove();
				}
			}.start();
		} else {
			return false;
		}
		return true;
	}

	private void testTwitter() {
		final byte type = ParseUtils.parseByte(provider.get(DATA_PROVIDER_KEY_AUTH_TYPE));
		final String rest = ParseUtils.parseString(provider.get(DATA_PROVIDER_KEY_REST_BASE_URL));
		final String oauth = ParseUtils.parseString(provider.get(DATA_PROVIDER_KEY_OAUTH_BASE_URL));
		if (AuthType.isValid(type)) {
		}
		try {
			final ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setGZIPEnabled(preferences.getBoolean(PREFERENCE_ID_GZIP_COMPRESSING));
			cb.setSSLErrorsIgnored(true);
			if (Utils.isValidUrl(rest)) {
				cb.setRestBaseURL(rest);
			}
			if (Utils.isValidUrl(oauth)) {
				cb.setOAuthBaseURL(oauth);
			}
			cb.setSigningRestBaseURL(Twitter.DEFAULT_SIGNING_REST_BASE_URL);
			cb.setSigningOAuthBaseURL(Twitter.DEFAULT_SIGNING_OAUTH_BASE_URL);
			cb.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
			cb.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
			cb.setHostAddressResolver(new TwidereHostAddressResolver());
			cb.setHttpClientFactory(new TwidereHttpClientFactory());
			final TwitterFactory tf = new TwitterFactory(cb.build());
			final OAuthPasswordAuthenticator authenticator = new OAuthPasswordAuthenticator(tf.getInstance());
			final String user_name = provider.getStringValue(DATA_PROVIDER_KEY_USER_NAME);
			final String password = provider.getStringValue(DATA_PROVIDER_KEY_PASSWORD);
			final AccessToken accessToken = authenticator.getOAuthAccessToken(user_name, password);
			System.out.println("Access Token: " + accessToken);
			final Twitter twitter = tf.getInstance(accessToken);
			System.out.println("User credentials: " + twitter.verifyCredentials());
		} catch (Exception e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			final StringBuffer buf = new StringBuffer();
			buf.append(e.getClass().getName()).append(":").append(e.getMessage());
			final String msg = buf.toString();
			System.out.println(msg);
		}

	}
}
