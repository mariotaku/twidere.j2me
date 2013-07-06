/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mariotaku.twidere.frame;

import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.util.frame.Frame;
import org.kalmeo.util.frame.FrameHandler;

/**
 *
 * @author mariotaku
 */
public class MainFrame implements Frame {
		// Static frame instance
	public static final Frame INSTANCE = new MainFrame();
	
	public boolean onMessage(Object identifier, Object[] arguments) {
		return true;
	}

	public void onAdded() {
		final FrameHandler handler = Kuix.getFrameHandler();
		
	}

	public void onRemoved() {
		
	}
}
