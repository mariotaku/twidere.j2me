/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mariotaku.twidere.frame;

import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.widget.Screen;
import org.kalmeo.util.frame.Frame;
import org.mariotaku.twidere.util.ArrayUtils;

/**
 *
 * @author mariotaku
 */
public class HomeFrame implements Frame {

	// Static frame instance
	public static final HomeFrame INSTANCE = new HomeFrame();
	
	private final Screen screen = Kuix.loadScreen("/xml/home.xml", null);
	
	public boolean onMessage(Object identifier, Object[] arguments) {
		System.out.println("onMessage identifier:" + identifier + ", arguments:[" + ArrayUtils.toString(arguments, ',', true) + "]");
		return true;
	}

	public void onAdded() {
		screen.setCurrent();
	}

	public void onRemoved() {
		
	}
	
}
