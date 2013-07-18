/*
 * This file is part of org.kalmeo.kuix.
 * 
 * org.kalmeo.kuix is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * org.kalmeo.kuix is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with org.kalmeo.kuix.  If not, see <http://www.gnu.org/licenses/>.
 *  
 * Creation date : 21 nov. 2007
 * Copyright (c) Kalmeo 2007-2008. All rights reserved.
 * http://www.kalmeo.org
 */
package org.kalmeo.kuix.widget;

import org.kalmeo.kuix.core.KuixConstants;
import org.kalmeo.kuix.layout.LayoutData;
import org.kalmeo.kuix.layout.StaticLayoutData;
import org.kalmeo.kuix.util.Alignment;
import org.kalmeo.kuix.util.Insets;
import org.kalmeo.util.BooleanUtil;
import org.kalmeo.util.MathFP;

/**
 * This class represents a gauge. <br>
 * <br>
 * <strong>For further informations, visit the <a
 * href="http://www.kalmeo.org/files/kuix/widgetdoc/index.html"
 * target="new">Kuix widgets reference page</a></strong>.
 * 
 * @author bbeaulant
 */
public class SeekBar extends ProgressBar {

	// Handle child
	private final StaticLayoutData handleLayoutData;
	private final Widget handle;
	private boolean isDown;

	public SeekBar() {
		super(KuixConstants.SEEK_BAR_WIDGET_TAG);
		handleLayoutData = new StaticLayoutData(Alignment.LEFT, -1, -1); 	// Caution the bar would not be resized by the gauge. But it's the bar that resize the gauge.
		handle = new SeekBarHandle(handleLayoutData);
		add(handle);
		setFocusable(true);
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#processKeyEvent(byte, int)
	 */
	public boolean processKeyEvent(byte type, int kuixKeyCode) {
		if (type == KuixConstants.KEY_PRESSED_EVENT_TYPE
				|| type == KuixConstants.KEY_REPEATED_EVENT_TYPE) {
			final int value = getValue(), increment = getIncrement();
			switch (kuixKeyCode) {
				case KuixConstants.KUIX_KEY_LEFT:
					setValue(value - increment);
					return true;
				case KuixConstants.KUIX_KEY_RIGHT:
					setValue(value + increment);
					return true;
			}
		}
		return super.processKeyEvent(type, kuixKeyCode);
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#processPointerEvent(byte, int, int)
	 */
	public boolean processPointerEvent(byte type, int x, int y) {
		final boolean isDownPrev = isDown;
		switch (type) {
			case KuixConstants.POINTER_PRESSED_EVENT_TYPE:
			case KuixConstants.POINTER_DRAGGED_EVENT_TYPE:
				isDown = true;
				break;
			case KuixConstants.POINTER_RELEASED_EVENT_TYPE:
			case KuixConstants.POINTER_DROPPED_EVENT_TYPE:
			default:
				isDown = false;
				break;
		}
		if (isDown != isDownPrev) {
			handle.invalidateStylePropertiesCache(false);
			handle.invalidate();
		}
		if (!isDown) {
			return false;
		}
		if (type == KuixConstants.POINTER_PRESSED_EVENT_TYPE
				|| type == KuixConstants.POINTER_DRAGGED_EVENT_TYPE) {

			// Convert coordinates in widget space
			for (Widget widget = this; widget != null; widget = widget.parent) {
				x -= widget.getX();
				y -= widget.getY();
			}
			Insets insets = getInsets();
			x -= insets.left;
			y -= insets.top;

			setValue(MathFP.div(MathFP.toFP(x), MathFP.toFP(getWidth() - insets.left - insets.right)));

			return true;
		}
		return super.processPointerEvent(type, x, y);
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#doLayout()
	 */
	protected void doLayout() {
		// Layout the bar
		final int width = getWidth();
		Insets insets = getInsets();
		int barWidth = MathFP.mul(MathFP.toFP(width - insets.left - insets.right), getValue());
		if (barWidth != 0 && barWidth < MathFP.ONE) {
			barWidth = MathFP.toFP("1.1");	// 1.1 to be sure to have a pixel size (value > 1.0) and not a percent bar size
		}
		handleLayoutData.x = MathFP.toInt(barWidth);

		super.doLayout();
	}

	protected String getBarTag() {
		return KuixConstants.SEEK_BAR_PROGRESS_WIDGET_TAG;
	}

	private static class SeekBarHandle extends Widget {

		private final LayoutData layoutData;

		SeekBarHandle(LayoutData layoutData) {
			super(KuixConstants.SEEK_BAR_HANDLE_WIDGET_TAG);
			this.layoutData = layoutData;
		}

		/* (non-Javadoc)
		 * @see org.kalmeo.kuix.widget.Widget#getPseudoClass()
		 */
		public String[] getAvailablePseudoClasses() {
			return new String[]{ActionWidget.PRESSED_PSEUDO_CLASS, FOCUSED_PSEUDO_CLASS,
						DISABLED_PSEUDO_CLASS};
		}

		/* (non-Javadoc)
		 * @see org.kalmeo.kuix.widget.Widget#getLayoutData()
		 */
		public LayoutData getLayoutData() {
			return layoutData;
		}

		/* (non-Javadoc)
		 * @see org.kalmeo.kuix.widget.Widget#isPseudoClassCompatible(java.lang.String)
		 */
		public boolean isPseudoClassCompatible(String pseudoClass) {
			if (ActionWidget.PRESSED_PSEUDO_CLASS.equals(pseudoClass)) {
				return ((SeekBar) parent).isDown;
			}
			if (FOCUSED_PSEUDO_CLASS.equals(pseudoClass)) {
				return parent.isFocused();
			}
			if (DISABLED_PSEUDO_CLASS.equals(pseudoClass)) {
				//return !parent.isEnabled();
			}
			return super.isPseudoClassCompatible(pseudoClass);
		}
	}
}
