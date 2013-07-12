/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mariotaku.twidere.frame;

import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.widget.Desktop;
import org.kalmeo.kuix.widget.Screen;
import org.kalmeo.kuix.widget.Widget;
import org.kalmeo.util.frame.Frame;
import org.mariotaku.twidere.Constants;

/**
 *
 * @author mariotaku
 */
public class BaseFrame implements Frame, Constants {

	private final BaseFrame parent;
	private Desktop desktop;
	private Screen screen;

	BaseFrame(final BaseFrame parent) {
		this.parent = parent;
	}

	public final Frame getParent() {
		return parent;
	}

	public final Screen getScreen() {
		return screen;
	}

	public final Widget getWidget(String id) {
		return screen != null ? screen.getWidget(id) : null;
	}

	public boolean onMessage(Object identifier, Object[] arguments) {
		return false;
	}

	public void onAdded() {
		desktop = Kuix.getCanvas().getDesktop();
		screen = createScreen();
		showScreen();
	}

	public void onRemoved() {
		if (desktop != null) {
			desktop.getCurrentScreen().cleanUp();
		}
		if (parent != null) {
			parent.showScreen();
		}
	}

	public final void showScreen() {
		if (screen == null) {
			screen = getScreen();
		}
		if (screen != null) {
			showScreen(screen);
		}
	}

	protected void showScreen(Screen screen) {
		if (screen != null) {
			screen.setCurrent();
		}
	}

	protected Screen createScreen() {
		return null;
	}
}
