/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mariotaku.twidere.frame;

import org.json.me.JSONArray;
import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.widget.Screen;
import org.kalmeo.kuix.widget.Text;
import org.kalmeo.kuix.widget.TextArea;
import org.kalmeo.util.frame.FrameHandler;
import org.mariotaku.twidere.util.ArrayUtils;
import org.mariotaku.twidere.util.TwidereHostAddressResolver;
import org.mariotaku.twidere.util.http.TwidereHttpClientFactory;
import twitter2me.TwitterConstants;
import twitter2me.TwitterException;
import twitter2me.auth.Authorization;
import twitter2me.auth.BasicAuthorization;
import twitter2me.conf.Configuration;
import twitter2me.conf.ConfigurationBuilder;
import twitter2me.http.HttpClientWrapper;
import twitter2me.http.HttpParameter;
import twitter2me.http.HttpResponse;

/**
 *
 * @author mariotaku
 */
public class SignInFrame extends BaseFrame {

	public SignInFrame(final BaseFrame parent) {
		super(parent);
	}

	protected Screen createScreen() {
		return Kuix.loadScreen("/xml/sign_in.xml", null);
	}

	protected void showScreen(Screen screen) {
		super.showScreen(screen);
		final Text menu_back_text = (Text) getWidget(VIEW_ID_MENU_BACK_TEXT);
		if (menu_back_text != null) {
			final String text = Kuix.getMessage(getParent() != null ? MESSAGE_ID_BACK : MESSAGE_ID_EXIT);
			menu_back_text.setText(text);
		}
	}

	public boolean onMessage(Object identifier, Object[] arguments) {
		final FrameHandler handler = Kuix.getFrameHandler();
		System.out.println("onMessage identifier:" + identifier + ", arguments:[" + ArrayUtils.toString(arguments, ',',
				true) + "]");
		if (ACTION_ID_EDIT_API.equals(identifier)) {
			Kuix.showPopupBox("/xml/edit_api_popup.xml", null);
		} else if (ACTION_ID_SETTINGS.equals(identifier)) {
			handler.pushFrame(new SettingsFrame(this));
		} else if (ACTION_ID_MENU_BACK.equals(identifier)) {
			if (getParent() != null) {
				handler.removeFrame(this);
			} else {
				Kuix.exit();
			}
		} else if (ACTION_ID_SIGN_IN.equals(identifier)) {
			new Thread() {

				public void run() {
					testSSLImpl();
				}
			}.start();
		} else {
			return false;
		}
		return true;
	}

	private void testSSLImpl() {
		final TextArea status = (TextArea) getWidget("status");
		try {
			final ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setGZIPEnabled(true);
			cb.setSSLErrorsIgnored(true);
			cb.setRestBaseURL("https://gtap-120306.appspot.com/api/1.1/");
			cb.setHostAddressResolver(new TwidereHostAddressResolver());
			cb.setHttpClientFactory(new TwidereHttpClientFactory());
			final Configuration conf = cb.build();
			HttpClientWrapper http = new HttpClientWrapper(conf);
			final Authorization auth = new BasicAuthorization("mariotaku", "19950130");
			final String url = conf.getRestBaseURL() + TwitterConstants.ENDPOINT_STATUSES_HOME_TIMELINE;
			final HttpParameter param1 = new HttpParameter("count", 1);
			final HttpResponse resp = http.get(url, url, new HttpParameter[] { param1 }, auth);
			final JSONArray json = resp.asJSONArray();
			status.setText("Response: " + json.toString(4));
		} catch (Exception e) {
			StringBuffer msg = new StringBuffer();
			msg.append(e.getClass().getName() + ":" + e.getMessage());
			if (e instanceof TwitterException) {
				final TwitterException te = (TwitterException) e;
				final Throwable c = te.getCause();
				if (c != null) {
					msg.append("Caused by:");
					msg.append(c.getClass().getName() + ":" + c.getMessage());
				}
			}
			status.setText(msg.toString());
		}

	}
}
