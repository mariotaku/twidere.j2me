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
import org.mariotaku.twidere.util.ArrayUtils;
import org.mariotaku.twidere.util.DataStore;

/**
 *
 * @author mariotaku
 */
public class NetworkSettingsFrame extends BaseFrame {

	public NetworkSettingsFrame(BaseFrame parent) {
		super(parent);
	}

	protected Screen createScreen() {
		return Kuix.loadScreen("settings_network.xml", null);
	}

	public boolean onMessage(Object identifier, Object[] arguments) {
		System.out.println("onMessage identifier:" + identifier + ", arguments:[" + ArrayUtils.toString(arguments, ',',
				true) + "]");
		final FrameHandler handler = Kuix.getFrameHandler();
		if (ACTION_ID_BACK.equals(identifier)) {
			savePreferences();
			handler.removeFrame(this);
		} else if (ACTION_ID_OPEN_SETTINGS.equals(identifier)) {
			final String arg0 = arguments != null && arguments.length > 0 ? String.valueOf(arguments[0]) : null;
			if (ACTION_ARGUMENT_HOST_MAPPING.equals(arg0)) {
				handler.pushFrame(new HostMappingsFrame(this));
			} else {
				return false;
			}
		} else {
			return false;
		}
		return true;
	}

	protected void showScreen(Screen screen) {
		super.showScreen(screen);
		readPreferences();
	}

	private void readPreferences() {
		final TwidereMIDlet twidere = TwidereMIDlet.getActiveInstance();
		final DataStore prefs = twidere.getPreferences();
		final CheckBox gzip_compressing = (CheckBox) getWidget(WIDGET_ID_GZIP_COMPRESSING);
		gzip_compressing.setSelected(prefs.getBoolean(PREFERENCE_ID_GZIP_COMPRESSING, false));
	}

	private void savePreferences() {
		final TwidereMIDlet twidere = TwidereMIDlet.getActiveInstance();
		final DataStore prefs = twidere.getPreferences();
		final CheckBox gzip_compressing = (CheckBox) getWidget(WIDGET_ID_GZIP_COMPRESSING);
		prefs.putBoolean(PREFERENCE_ID_GZIP_COMPRESSING, gzip_compressing.isSelected());
	}
}
