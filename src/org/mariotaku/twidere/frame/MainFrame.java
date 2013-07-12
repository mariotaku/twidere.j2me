/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mariotaku.twidere.frame;

import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.util.frame.FrameHandler;
import org.mariotaku.twidere.midlet.TwidereMIDlet;
import org.mariotaku.twidere.util.DataStore;

/**
 *
 * @author mariotaku
 */
public class MainFrame extends BaseFrame {

	public static MainFrame INSTANCE = new MainFrame();
	private DataStore accounts;
	
	public MainFrame() {
		super(null);
	}
	
	public DataStore getAccountsDataStore() {
		return accounts;
	}
	
	public void onAdded() {
		final FrameHandler handler = Kuix.getFrameHandler();
		final TwidereMIDlet midlet = TwidereMIDlet.getActiveInstance();
		accounts = midlet.getAccountsDataStore();
		if (accounts == null) {
			// This should never happen
			return;
		}
		if (accounts.isEmpty()) {
			handler.pushFrame(new SignInFrame(null));
		} else {
			handler.pushFrame(new HomeFrame());
		}
	}

	public void onRemoved() {
		
	}
}
