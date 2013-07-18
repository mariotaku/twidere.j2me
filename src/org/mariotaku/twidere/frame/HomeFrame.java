/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mariotaku.twidere.frame;

import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.widget.Screen;
import org.kalmeo.util.frame.FrameHandler;
import org.mariotaku.twidere.util.ArrayUtils;

/**
 *
 * @author mariotaku
 */
public class HomeFrame extends BaseFrame {

	public HomeFrame() {
		super(null);

	}

	protected Screen createScreen() {
		return Kuix.loadScreen("/xml/home.xml", null);
	}
	
	public boolean onMessage(Object identifier, Object[] arguments) {
		System.out.println("onMessage id:" + identifier + ", args:" + ArrayUtils.toString(arguments, ',', true));
		final FrameHandler handler = Kuix.getFrameHandler();
		if (ACTION_ID_SETTINGS.equals(identifier)) {
			handler.pushFrame(new SettingsFrame(this));
		} else {
			return super.onMessage(identifier, arguments);
		}
		return true;
	}

}
