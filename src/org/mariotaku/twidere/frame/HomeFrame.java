/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mariotaku.twidere.frame;

import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.widget.Screen;

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

}
