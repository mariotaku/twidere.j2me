/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mariotaku.twidere.frame;

import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.widget.Screen;
import org.kalmeo.util.frame.Frame;

/**
 *
 * @author mariotaku
 */
public class MainFrame implements Frame {
		// Static frame instance
	public static final Frame INSTANCE = new MainFrame();
	
	private final Screen screen = Kuix.loadScreen("/xml/home.xml", null);
	
	public boolean onMessage(Object identifier, Object[] arguments) {
		return true;
	}

	public void onAdded() {
		screen.setCurrent();
	}

	public void onRemoved() {
		
	}
}
