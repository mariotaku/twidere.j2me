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

package org.kalmeo.kuix.layout;

import org.kalmeo.kuix.core.KuixConstants;
import org.kalmeo.kuix.util.Alignment;
import org.kalmeo.kuix.util.Gap;
import org.kalmeo.kuix.util.Insets;
import org.kalmeo.kuix.util.Metrics;
import org.kalmeo.kuix.widget.Widget;

/**
 * @author bbeaulant
 */
public class InlineLayout implements Layout {

	// InlineLayout properties
	public boolean horizontal;
	public Alignment alignment;

	/**
	 * Construct an {@link InlineLayout}
	 */
	public InlineLayout() {
		this(true, Alignment.TOP_LEFT);
	}
	
	/**
	 * Construct an {@link InlineLayout}
	 * 
	 * @param horizontal
	 */
	public InlineLayout(boolean horizontal) {
		this(horizontal, Alignment.TOP_LEFT);
	}

	/**
	 * Construct a {@link InlineLayout}
	 * 
	 * @param horizontal
	 * @param alignment
	 */
	public InlineLayout(boolean horizontal, Alignment alignment) {
		this.horizontal = horizontal;
		this.alignment = alignment;
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.layout.Layout#computePreferredSize(org.kalmeo.kuix.widget.Widget, int, org.kalmeo.kuix.util.Metrics)
	 */
	public void measurePreferredSize(Widget target, int preferredWidth, Metrics metrics) {
		measure(target, false, preferredWidth, metrics);
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.layout.Layout#doLayout(org.kalmeo.kuix.widget.Widget)
	 */
	public void doLayout(Widget target) {
		measure(target, true, target.getWidth(), null);
	}
	
	/**
	 * Measure <code>target</code> children layout
	 * 
	 * @param target
	 * @param layout
	 * @param preferredWidth
	 * @param metrics
	 */
	private void measure(Widget target, boolean layout, int preferredWidth, Metrics metrics) {
		
		Alignment targetAlignment = target.getAlign();
		Insets insets = target.getInsets();
		Metrics minSize = target.getMinSize();
		Gap gap = target.getGap();
		int width = preferredWidth - insets.left - insets.right;
		int height = target.getHeight() - insets.top - insets.bottom;
		int contentWidth = 0;
		int contentHeight = 0;
		Metrics first = null;
		Metrics current = null;
		
		for (Widget widget = target.getChild(); widget != null; widget = widget.next) {
			
			if (!widget.isIndividualyVisible()) {
				continue;
			}

			if (KuixConstants.BREAK_WIDGET_TAG.equals(widget.getTag())) {
				continue;
			}

			Metrics preferredSize = widget.getPreferredSize(width);
			if (horizontal) {
				contentWidth += preferredSize.width + (first != null ? gap.horizontalGap : 0);
				contentHeight = Math.max(preferredSize.height, contentHeight);
			} else {
				contentWidth = Math.max(preferredSize.width, contentWidth);
				contentHeight += preferredSize.height + (first != null ? gap.verticalGap : 0);
			}
			
			if (first == null) {
				first = current = preferredSize;
			} else {
				current.next = preferredSize;
				current = preferredSize;
			}

		}

		if (!layout) {
			metrics.width = insets.left + Math.max(minSize.width, contentWidth) + insets.right;
			metrics.height = insets.top + Math.max(minSize.height, contentHeight) + insets.bottom;
			return;
		}

		int x = 0;
		int y = 0;
		int contentX = 0;
		int contentY = 0;

		if (targetAlignment != null) {
			if (targetAlignment.isFill()) {
				if (horizontal) {
					contentHeight = height;
				} else {
					contentWidth = width;
				}
			}
			x = insets.left + targetAlignment.alignX(width, contentWidth);
			y = insets.top + targetAlignment.alignY(height, contentHeight);
		}
		
		if (horizontal) {
			
			for (Metrics widgetMetrics = first; widgetMetrics != null; widgetMetrics = widgetMetrics.next) {
				Widget widget = widgetMetrics.widget;
				int h = widgetMetrics.height;
				contentY = alignment.alignY(contentHeight, h);
				if (alignment.isFill()) {
					h = contentHeight;
				}
				widget.setBounds(	x, 
									y + contentY, 
									widgetMetrics.width, 
									h);
				x += widgetMetrics.width + gap.horizontalGap;
			}
			
		} else {
			
			for (Metrics widgetMetrics = first; widgetMetrics != null; widgetMetrics = widgetMetrics.next) {
				Widget widget = widgetMetrics.widget;
				int w = widgetMetrics.width;
				contentX = alignment.alignX(contentWidth, w);
				if (alignment.isFill()) {
					w = contentWidth;
				}
				widget.setBounds(	x + contentX, 
									y, 
									w, 
									widgetMetrics.height);
				y += widgetMetrics.height + gap.verticalGap;
			}

		}

	}
}