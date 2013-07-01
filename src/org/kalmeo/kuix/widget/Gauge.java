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

import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.core.KuixConstants;
import org.kalmeo.kuix.layout.Layout;
import org.kalmeo.kuix.layout.LayoutData;
import org.kalmeo.kuix.layout.StaticLayout;
import org.kalmeo.kuix.layout.StaticLayoutData;
import org.kalmeo.kuix.util.Alignment;
import org.kalmeo.kuix.util.Insets;
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
public class Gauge extends FocusableWidget {

	// Bar child
	private final StaticLayoutData barLayoutData;
	private final Widget bar;
	
	// The gauge value (fixed-point integer)
	private int value;
	
	// Increment value (0 represents the minimal increment)
	private int increment = 0;

	// The change method
	private String onChange;
	
	/**
	 * Construct a {@link Gauge}
	 */
	public Gauge() {
		super(KuixConstants.GAUGE_WIDGET_TAG);
		barLayoutData = new StaticLayoutData(Alignment.LEFT, 0, -1); 	// Caution the bar would not be resized by the gauge. But it's the bar that resize the gauge.
		bar = new Widget(KuixConstants.GAUGE_BAR_WIDGET_TAG) {

			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.Widget#getLayoutData()
			 */
			public LayoutData getLayoutData() {
				return barLayoutData;
			}

		};
		add(bar);
		
		// By default this widget is not focusable
		setFocusable(false);
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#setAttribute(java.lang.String, java.lang.String)
	 */
	public boolean setAttribute(String name, String value) {
		if (KuixConstants.VALUE_ATTRIBUTE.equals(name)) {
			setValue(MathFP.toFP(value));
			return true;
		}
		if (KuixConstants.INCREMENT_ATTRIBUTE.equals(name)) {
			setIncrement(MathFP.toFP(value));
			return true;
		}
		if (KuixConstants.ON_CHANGE_ATTRIBUTE.equals(name)) {
			setOnChange(value);
			return true;
		}
		return super.setAttribute(name, value);
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String name) {
		if (KuixConstants.VALUE_ATTRIBUTE.equals(name)) {
			return MathFP.toString(value);
		}
		return super.getAttribute(name);
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getInternalChildInstance(java.lang.String)
	 */
	public Widget getInternalChildInstance(String tag) {
		if (KuixConstants.GAUGE_BAR_WIDGET_TAG.equals(tag)) {
			return bar;
		}
		return super.getInternalChildInstance(tag);
	}
	
	/**
	 * @return the bar
	 */
	public Widget getBar() {
		return bar;
	}

	/**
	 * @return a fixed-point integer representing the value
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * @param value a fixed-point integer representing the value
	 */
	public void setValue(int value) {
		// First change check
		if (value != this.value) {
			
			if (increment != 0) {
				value = MathFP.mul(MathFP.ceil(MathFP.div(value, increment)), increment);
			}
			value = Math.min(MathFP.ONE, Math.max(0, value));
			
			// Second change check
			if (value != this.value) {
				this.value = value;
				invalidate();
				if (isFocusable() && onChange != null) {	// Enable and focusable
					Kuix.callActionMethod(Kuix.parseMethod(onChange, this));
				}
			}
			
		}
	}
	
	/**
	 * @return the increment
	 */
	public int getIncrement() {
		return increment;
	}

	/**
	 * @param increment the increment to set
	 */
	public void setIncrement(int increment) {
		this.increment = increment;
	}
	
	/**
	 * @return the onChange
	 */
	public String getOnChange() {
		return onChange;
	}

	/**
	 * @param onChange the onChange to set
	 */
	public void setOnChange(String onChange) {
		this.onChange = onChange;
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getLayout()
	 */
	public Layout getLayout() {
		return StaticLayout.instance;
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#doLayout()
	 */
	protected void doLayout() {
		// Layout the bar
		Insets insets = getInsets();
		int barWidth = MathFP.mul(MathFP.toFP(getWidth() - insets.left - insets.right),  value);
		if (barWidth != 0 && barWidth < MathFP.ONE) {
			barWidth = MathFP.toFP("1.1");	// 1.1 to be sure to have a pixel size (value > 1.0) and not a percent bar size
		}
		barLayoutData.width = barWidth;
		
		super.doLayout();
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#processKeyEvent(byte, int)
	 */
	public boolean processKeyEvent(byte type, int kuixKeyCode) {
		if (isFocusable() 
				&& (type == KuixConstants.KEY_PRESSED_EVENT_TYPE
					|| type == KuixConstants.KEY_REPEATED_EVENT_TYPE)) {
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
		if (isFocusable() 
				&& (type == KuixConstants.POINTER_PRESSED_EVENT_TYPE 
						|| type == KuixConstants.POINTER_DRAGGED_EVENT_TYPE)) {
			
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

}
