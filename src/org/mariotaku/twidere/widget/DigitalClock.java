/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mariotaku.twidere.widget;

import java.util.Calendar;
import java.util.Date;
import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.widget.Text;
import org.kalmeo.kuix.widget.Widget;
import org.mariotaku.twidere.util.Utils;

/**
 *
 * @author mariotaku
 */
public class DigitalClock extends Text implements Runnable {

	public static final String WIDGET_TAG = "digitalclock";
	private final boolean is24HourFormat;
	private Thread clockThread;
	private boolean isRemoved;

	public DigitalClock() {
		super(WIDGET_TAG);
		is24HourFormat = Utils.is24HourFormat();
	}

	private void refreshClock() {
		if (isRemoved || !Kuix.isInitialized()) {
			return;
		}
		setText(getTime());
	}

	protected void onAdded(Widget parent) {
		super.onAdded(parent);
		isRemoved = false;
		clockThread = new Thread(this);
		clockThread.start();
	}

	protected void onRemoved(Widget parent) {
		isRemoved = true;
		clockThread = null;
		super.onRemoved(parent);
	}

	private String getTime() {
		final Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		final String hours = String.valueOf(c.get(is24HourFormat
				? Calendar.HOUR_OF_DAY : Calendar.HOUR));
		final String minutes = addZero(c.get(Calendar.MINUTE), 2);
		return hours + ":" + minutes;
	}

	private static String addZero(int i, int size) {
		String s = "0000" + i;
		return s.substring(s.length() - size, s.length());


	}

	public void run() {
		while (true && !isRemoved) {
			refreshClock();
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}