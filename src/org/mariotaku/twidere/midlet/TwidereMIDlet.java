/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mariotaku.twidere.midlet;

import javax.microedition.midlet.MIDletStateChangeException;
import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.core.KuixConverter;
import org.kalmeo.kuix.core.KuixMIDlet;
import org.kalmeo.kuix.widget.Desktop;
import org.kalmeo.util.frame.FrameHandler;
import org.mariotaku.twidere.Constants;
import org.mariotaku.twidere.frame.MainFrame;
import org.mariotaku.twidere.util.CustomWidgetConverter;
import org.mariotaku.twidere.util.DataStore;

/**
 *
 * @author mariotaku
 */
public class TwidereMIDlet extends KuixMIDlet implements Constants {

	private static TwidereMIDlet instance;
	private boolean isPaused;
	private DataStore accounts, preferences;

	public DataStore getPreferences() {
		if (preferences != null) {
			return preferences;
		}
		return preferences = DataStore.openDataStore(DATASTORE_NAME_PREFERENCES);
	}

	protected void destroyApp(boolean unconditional) {
		if (preferences != null) {
			try {
				preferences.save();
			} catch (Exception ex) {
			}
		}
		super.destroyApp(unconditional);
		instance = null;
	}

	protected void pauseApp() {
		isPaused = true;
		super.pauseApp();
	}

	public DataStore getAccountsDataStore() {
		if (accounts != null) {
			return accounts;
		}
		return accounts = DataStore.openDataStore(DATASTORE_NAME_ACCOUNTS);
	}

	protected void startApp() throws MIDletStateChangeException {
		if (!isPaused) {
			instance = this;
		}
		isPaused = false;
		super.startApp();
	}

	public static TwidereMIDlet getActiveInstance() {
		return instance;
	}

	public void initDesktopStyles() {
		final DataStore prefs = getPreferences();
		if (prefs == null) {
			Kuix.loadCss("/css/style_light.css");
			return;
		}
		final boolean dark_theme = prefs.getBoolean(PREFERENCE_ID_DARK_THEME);
		Kuix.loadCss("/css/style_" + (dark_theme ? "dark" : "light") + ".css");
	}

	public void initDesktopContent(final Desktop desktop) {
		final FrameHandler handler = Kuix.getFrameHandler();
		handler.pushFrame(MainFrame.INSTANCE);
	}

	protected KuixConverter createNewConverterInstance() {
		return new CustomWidgetConverter();
	}
}
