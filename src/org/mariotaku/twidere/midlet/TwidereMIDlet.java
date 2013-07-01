/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mariotaku.twidere.midlet;

import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.core.KuixMIDlet;
import org.kalmeo.kuix.widget.Desktop;
import org.kalmeo.util.frame.FrameHandler;
import org.mariotaku.twidere.frame.HomeFrame;
import org.mariotaku.twidere.frame.SignInFrame;

/**
 *
 * @author mariotaku
 */
public class TwidereMIDlet extends KuixMIDlet {

	public void initDesktopStyles() {
		Kuix.loadCss("/css/style_light.css");
	}

	public void initDesktopContent(final Desktop desktop) {
		final FrameHandler handler = Kuix.getFrameHandler();
		handler.pushFrame(SignInFrame.INSTANCE);
	}
}
