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

import org.kalmeo.kuix.util.Gap;
import org.kalmeo.kuix.util.Insets;
import org.kalmeo.kuix.util.Metrics;
import org.kalmeo.kuix.widget.Widget;

/**
 * @author bbeaulant
 */
public class BorderLayout implements Layout {

	// Static instance of a BorderLayout
	public static final BorderLayout instance = new BorderLayout();
	
	/**
	 * Construct a {@link BorderLayout}
	 */
	private BorderLayout() {
		// Constructor is private because only the static instance could be use
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
		
		Insets insets = target.getInsets();
		Metrics minSize = target.getMinSize();
		Gap gap = target.getGap();
		int width = preferredWidth - insets.left - insets.right;
		int height = target.getHeight() - insets.top - insets.bottom;
		int top = 0;
		int left = 0;
		int right = 0;
		int bottom = 0;
		int centerHeight = 0;
		int centerWidth = 0;
		
		// Order widgets 
		Widget northWidget = null;
		Widget westWidget = null;
		Widget eastWidget = null;
		Widget southWidget = null;
		Widget centerWidget = null;
		for (Widget widget = target.getChild(); widget != null; widget = widget.next) {
			
			if (!widget.isIndividualyVisible()) {
				continue;
			}
			
			LayoutData layoutData = widget.getLayoutData();
			if (layoutData instanceof BorderLayoutData) {
				byte position = ((BorderLayoutData) layoutData).position;
				switch (position) {
					
					case BorderLayoutData.NORTH:
						northWidget = widget;
						break;
						
					case BorderLayoutData.WEST:
						westWidget = widget;
						break;
						
					case BorderLayoutData.EAST:
						eastWidget = widget;
						break;
						
					case BorderLayoutData.SOUTH:
						southWidget = widget;
						break;
						
					default:
						centerWidget = widget;
						break;
						
				}
			} else if (centerWidget == null) {
				centerWidget = widget;
			}
			
		}

		
		// Compute gaps values
		int verticalTopGap = (northWidget != null && (westWidget != null || centerWidget != null || eastWidget != null || southWidget != null)) ? gap.verticalGap : 0;
		int verticalBottompGap = (southWidget != null && (westWidget != null || centerWidget != null || eastWidget != null)) ? gap.verticalGap : 0;
		int horizontalLeftGap = (westWidget != null && (centerWidget != null || eastWidget != null)) ? gap.horizontalGap : 0;
		int horizontalRightGap = (eastWidget != null && (centerWidget != null || westWidget != null)) ? gap.horizontalGap : 0;
		
		int horizontalGap = horizontalLeftGap + horizontalRightGap;
		int verticalGap = verticalTopGap + verticalBottompGap;
		
		// North
		if (northWidget != null) {
			Metrics preferredSize = northWidget.getPreferredSize(width);
			centerWidth = preferredSize.width;
			top = preferredSize.height;
		}
		
		// West
		if (westWidget != null) {
			Metrics preferredSize = westWidget.getPreferredSize(width - horizontalGap);
			left = preferredSize.width;
			centerHeight = preferredSize.height;
		}
		
		// East
		if (eastWidget != null) {
			Metrics preferredSize = eastWidget.getPreferredSize(width - left - horizontalGap);
			right = preferredSize.width;
			centerHeight = Math.max(centerHeight, preferredSize.height);
		}
		
		// South
		if (southWidget != null) {
			Metrics preferredSize = southWidget.getPreferredSize(width);
			centerWidth = Math.max(centerWidth, preferredSize.width);
			bottom = preferredSize.height;
		}
		
		// Center
		if (centerWidget != null) {
			Metrics preferredSize = centerWidget.getPreferredSize(width - left - right - horizontalGap);
			centerWidth = Math.max(centerWidth, preferredSize.width);
			centerHeight = Math.max(centerHeight, preferredSize.height);
		}
		
		if (!layout) {
			metrics.width = insets.left + Math.max(minSize.width, left + centerWidth + right + horizontalGap) + insets.right;
			metrics.height = insets.top + Math.max(minSize.height, top + centerHeight + bottom + verticalGap) + insets.bottom;
			return;
		}
			
		centerWidth = width - left - right - horizontalGap;
		centerHeight = height - top - bottom - verticalGap;
		
		// Center
		if (centerWidget != null) {
			centerWidget.setBounds(	insets.left + left + horizontalLeftGap, 
									insets.top + top + verticalTopGap, 
									centerWidth, 
									centerHeight);
		}
		
		// North
		if (northWidget != null) {
			northWidget.setBounds(	insets.left, 
									insets.top, 
									width, 
									top);
		}
		
		// West
		if (westWidget != null) {
			westWidget.setBounds(	insets.left, 
									insets.top + top + verticalTopGap, 
									left, 
									centerHeight);
		}
		
		// East
		if (eastWidget != null) {
			eastWidget.setBounds(	insets.left + width - right, 
									insets.top + top + verticalTopGap, 
									right, 
									centerHeight);
		}
		
		// South
		if (southWidget != null) {
			southWidget.setBounds(	insets.left, 
									insets.top + height - bottom, 
									width, 
									bottom);
		}
			
	}
	
}
