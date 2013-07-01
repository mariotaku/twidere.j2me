/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mariotaku.twidere.frame;

import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.widget.Screen;
import org.kalmeo.util.frame.Frame;
import org.mariotaku.twidere.Constants;
import org.mariotaku.twidere.util.ArrayUtils;

/**
 *
 * @author mariotaku
 */
public class SignInFrame implements Frame, Constants {

	// Static frame instance
	public static final SignInFrame INSTANCE = new SignInFrame();
	
	private final Screen screen = Kuix.loadScreen("/xml/sign_in.xml", null);
	
	public boolean onMessage(Object identifier, Object[] arguments) {
		System.out.println("onMessage identifier:" + identifier + ", arguments:[" + ArrayUtils.toString(arguments, ',', true) + "]");
		if (ACTION_IDENTIFIER_EDIT_API.equals(identifier)) {
			Kuix.showPopupBox("/xml/edit_api_popup.xml", null);
			return true;
		}
		return false;
	}

	public void onAdded() {
		screen.setCurrent();
	}

	public void onRemoved() {
		
	}
	
}
