/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mariotaku.twidere.util;

import org.kalmeo.kuix.core.KuixConverter;
import org.kalmeo.kuix.widget.Widget;
import org.mariotaku.twidere.widget.DigitalClock;

/**
 *
 * @author mariotaku
 */
public class CustomWidgetConverter extends KuixConverter {

	public Widget convertWidgetTag(final String tag) {
		if (DigitalClock.WIDGET_TAG.equals(tag)) {
			return new DigitalClock();
		}
		return super.convertWidgetTag(tag);
	}
}
