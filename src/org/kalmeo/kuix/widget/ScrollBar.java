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

import javax.microedition.lcdui.Graphics;

import org.kalmeo.kuix.core.KuixConstants;
import org.kalmeo.kuix.layout.Layout;
import org.kalmeo.kuix.layout.LayoutData;
import org.kalmeo.kuix.layout.StaticLayout;
import org.kalmeo.kuix.layout.StaticLayoutData;
import org.kalmeo.kuix.util.Insets;
import org.kalmeo.util.BooleanUtil;
import org.kalmeo.util.MathFP;

/**
 * This class represents a scroll bar. <br>
 * <br>
 * <strong>For further informations, visit the <a
 * href="http://www.kalmeo.org/files/kuix/widgetdoc/index.html"
 * target="new">Kuix widgets reference page</a></strong>.
 * 
 * @author bbeaulant
 */
public class ScrollBar extends Widget {

	// Bar child
	private final StaticLayoutData barLayoutData;
	private final Widget bar;
	
	// fixed-point integer
	private int selection;
	private int value;
	
	// ScrollBar direction
	private boolean horizontal = false;
	
	// Internal use
	private int pressedX = 0;
	private int pressedY = 0;
	private int pressedValue = 0;
	private boolean barHidden = true;
	
	/**
	 * Construct a {@link ScrollBar}
	 */
	public ScrollBar() {
		this(KuixConstants.SCROLL_BAR_WIDGET_TAG);
	}
	
	/**
	 * Construct a {@link ScrollBar}
	 */
	public ScrollBar(String tag) {
		super(tag);
		barLayoutData = new StaticLayoutData(null, 0, 0);
		bar = new Widget(KuixConstants.SCROLL_BAR_BAR_WIDGET_TAG) {

			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.Widget#getLayoutData()
			 */
			public LayoutData getLayoutData() {
				return barLayoutData;
			}

			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.Widget#paintImpl(javax.microedition.lcdui.Graphics)
			 */
			public void paintImpl(Graphics g) {
				if (!barHidden) {
					super.paintImpl(g);
				}
			}
			
		};
		add(bar);
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#setAttribute(java.lang.String, java.lang.String)
	 */
	public boolean setAttribute(String name, String value) {
		if (KuixConstants.VALUE_ATTRIBUTE.equals(name)) {
			setValue(MathFP.toFP(value));
			return true;
		}
		if (KuixConstants.SELECTION_ATTRIBUTE.equals(name)) {
			setSelection(MathFP.toFP(value));
			return true;
		}
		if (KuixConstants.HORIZONTAL_ATTRIBUTE.equals(name)) {
			setHorizontal(BooleanUtil.parseBoolean(value));
			return true;
		}
		return super.setAttribute(name, value);
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getInternalChildInstance(java.lang.String)
	 */
	public Widget getInternalChildInstance(String tag) {
		if (KuixConstants.SCROLL_BAR_BAR_WIDGET_TAG.equals(tag)) {
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
	 * Return a fixed-point integer representing the selection
	 * 
	 * @return the selection
	 */
	public int getSelection() {
		return selection;
	}

	/**
	 * Define the ScrollBar selection.
	 * 
	 * @param selection a fixed-point integer representing the selection
	 */
	public void setSelection(int selection) {
		int lastSelection = this.selection;
		this.selection = Math.min(MathFP.ONE, Math.max(0, selection));
		if (this.selection != lastSelection) {
			if (horizontal) {
				barLayoutData.width = this.selection;
				barLayoutData.height = -1;
			} else {
				barLayoutData.width = -1;
				barLayoutData.height = this.selection;
			}
			barHidden = this.selection == MathFP.ONE || this.selection == 0;
			bar.invalidate();
		}
	}

	/**
	 * Return a fixed-point integer representing the value
	 * 
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Define the ScrollBar value
	 * 
	 * @param value a fixed-point integer representing the value
	 */
	public void setValue(int value) {
		int lastValue = this.value;
		this.value = Math.min(MathFP.ONE, Math.max(0, value));
		if (this.value != lastValue) {
			invalidate();
		}
	}
	
	/**
	 * @return the horizontal
	 */
	public boolean isHorizontal() {
		return horizontal;
	}

	/**
	 * @param horizontal the horizontal to set
	 */
	public void setHorizontal(boolean horizontal) {
		this.horizontal = horizontal;
		setSelection(selection);	// Reapply the selection to switch direction
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getLayout()
	 */
	public Layout getLayout() {
		return StaticLayout.instance;
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#paintChildrenImpl(javax.microedition.lcdui.Graphics)
	 */
	protected void paintChildrenImpl(Graphics g) {
		int xOffset = horizontal ? MathFP.mul(value, MathFP.mul(getInnerWidth(), MathFP.ONE - selection)) : 0;
		int yOffset = horizontal ? 0 : MathFP.mul(value, MathFP.mul(getInnerHeight(), MathFP.ONE - selection));
		g.translate(xOffset, yOffset);
		super.paintChildrenImpl(g);
		g.translate(-xOffset, -yOffset);
	}

	/**
	 * Process a value change event produce by a UI action.
	 */
	protected void processChangeEvent() {
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#processPointerEvent(byte, int, int)
	 */
	public boolean processPointerEvent(byte type, int x, int y) {
		switch (type) {
			
			case KuixConstants.POINTER_PRESSED_EVENT_TYPE: {
				pressedX = x;
				pressedY = y;
				pressedValue = value;
				return true;
			}
			
			case KuixConstants.POINTER_DRAGGED_EVENT_TYPE: {
				Insets insets = getInsets();
				if (horizontal) {
					int innerWidth = getWidth() - insets.left - insets.right;
					if (innerWidth != 0) {
						setValue(pressedValue + MathFP.div(x - pressedX, MathFP.mul(innerWidth, MathFP.ONE - selection)));
						processChangeEvent();
					}
				} else {
					int innerHeight = getHeight() - insets.top - insets.bottom;
					if (innerHeight != 0) {
						setValue(pressedValue + MathFP.div(y - pressedY, MathFP.mul(innerHeight, MathFP.ONE - selection)));
						processChangeEvent();
					}
				}
				return true;
			}
			
		}
		return super.processPointerEvent(type, x, y);
	}
	
}
