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
class HostMappingsFrame extends BaseFrame {

	public HostMappingsFrame(BaseFrame parent) {
		super(parent);
	}

	protected Screen createScreen() {
		return Kuix.loadScreen("settings_host_mappings.xml", null);
	}

	public boolean onMessage(Object identifier, Object[] arguments) {
		final FrameHandler handler = Kuix.getFrameHandler();
		if (ACTION_ID_BACK.equals(identifier)) {
			handler.removeFrame(this);
		} else {
			return false;
		}
		return true;
	}
}
