/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mariotaku.twidere.frame;

import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.widget.CheckBox;
import org.kalmeo.kuix.widget.Screen;
import org.kalmeo.util.frame.FrameHandler;
import org.mariotaku.twidere.midlet.TwidereMIDlet;
import org.mariotaku.twidere.util.DataStore;

/**
 *
 * @author mariotaku
 */
public class SettingsFrame extends BaseFrame {

	public SettingsFrame(BaseFrame parent) {
		super(parent);
	}

	protected Screen createScreen() {
		return Kuix.loadScreen("/xml/settings.xml", null);
	}

	public boolean onMessage(Object identifier, Object[] arguments) {
		final FrameHandler handler = Kuix.getFrameHandler();
		if (ACTION_ID_BACK.equals(identifier)) {
			savePreferences();
			handler.removeFrame(this);
		} else {
			return false;
		}
		return true;
	}

	private void readPreferences() {
		final TwidereMIDlet twidere = TwidereMIDlet.getActiveInstance();
		final DataStore prefs = twidere.getPreferences();
		final CheckBox dark_theme = (CheckBox) getWidget(VIEW_ID_DARK_THEME);
		dark_theme.setSelected(prefs.getBoolean(PREFERENCE_ID_DARK_THEME, false));
	}

	protected void showScreen(Screen screen) {
		super.showScreen(screen);
		readPreferences();
	}

	private void savePreferences() {
		final TwidereMIDlet twidere = TwidereMIDlet.getActiveInstance();
		final DataStore prefs = twidere.getPreferences();
		final CheckBox dark_theme = (CheckBox) getWidget(VIEW_ID_DARK_THEME);
		prefs.putBoolean(PREFERENCE_ID_DARK_THEME, dark_theme.isSelected());
	}
}
