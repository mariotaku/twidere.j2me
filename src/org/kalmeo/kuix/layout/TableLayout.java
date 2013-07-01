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
import org.kalmeo.kuix.util.Span;
import org.kalmeo.kuix.util.Weight;
import org.kalmeo.kuix.widget.Widget;
import org.kalmeo.util.MathFP;

/**
 * @author bbeaulant
 */
public class TableLayout implements Layout {

	// Static instance of a TableLayout
	public static final TableLayout instance = new TableLayout();
	
	/**
	 * Construct a {@link TableLayout}
	 */
	private TableLayout() {
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
		
		Alignment targetAlignment = target.getAlign();
		Insets insets = target.getInsets();
		Metrics minSize = target.getMinSize();
		Gap gap = target.getGap();
		
		int width = preferredWidth - insets.left - insets.right;
		int height = target.getHeight() - insets.top - insets.bottom;
		int[] colHeights = null;
		int col = 0;
		int row = 0;
		int ncol = 0;
		int nrow = 0;
		Metrics first = null;
		Metrics current = null;

		for (Widget widget = target.getChild(); widget != null; widget = widget.next) {

			if (!widget.isIndividualyVisible()) {
				continue;
			}

			if (KuixConstants.BREAK_WIDGET_TAG.equals(widget.getTag())) {
				if (col != 0) {
					col = 0;
					row++;
				}
				continue;
			}

			Metrics widgetMetrics = widget.getPreferredSize(width);
			if (first == null) {
				first = current = widgetMetrics;
			} else {
				current.next = widgetMetrics;
				current = widgetMetrics;
			}

			Span span = widget.getSpan();
			int colspan = span.colspan;
			int rowspan = span.rowspan;
			if (colHeights != null)
				for (int j = 0; j < colspan; j++) {
					if (colHeights[col + j] > row) {
						col += (j + 1);
						j = -1;
					}
				}

			widgetMetrics.x = col;
			widgetMetrics.y = row;
			ncol = Math.max(ncol, col + colspan);
			nrow = Math.max(nrow, row + rowspan);

			if (rowspan > 1) {
				if (colHeights == null) {
					colHeights = new int[ncol];
				} else if (colHeights.length < ncol) {
					int[] newheights = new int[ncol];
					System.arraycopy(colHeights, 0, newheights, 0, colHeights.length);
					colHeights = newheights;
				}
				for (int j = 0; j < colspan; j++) {
					colHeights[col + j] = row + rowspan;
				}
			}

			col += colspan;
			
		}

		// Compute weights for each col an row
		int[] colWeights = new int[ncol];
		int[] rowWeights = new int[nrow];
		align(first, colWeights, null, true, 0);
		align(first, rowWeights, null, false, 0);
		
		// Compute sizes for each col an row
		int[] colWidths = new int[ncol];
		int[] rowHeights = new int[nrow];
		align(first, colWidths, colWeights, true, width - gap.horizontalGap * (ncol - 1));
		align(first, rowHeights, rowWeights, false, height - gap.verticalGap * (nrow - 1));

		// Compute content size
		int contentWidth = sum(colWidths, 0, ncol, gap.horizontalGap);
		int contentHeight = sum(rowHeights, 0, nrow, gap.verticalGap);
		
		if (!layout) {
			metrics.width = insets.left + Math.max(minSize.width, contentWidth) + insets.right;
			metrics.height = insets.top + Math.max(minSize.height, contentHeight) + insets.bottom;
			return;
		}
		
		// Compute the content box origine
		int contentX = 0;
		int contentY = 0;
		if (targetAlignment != null) {
			contentX = targetAlignment.alignX(width, contentWidth);
			contentY = targetAlignment.alignY(height, contentHeight);
		}
		contentX += insets.left;
		contentY += insets.top;

		// Arrange each child widget
		for (Metrics widgetMetrics = first; widgetMetrics != null; widgetMetrics = widgetMetrics.next) {
			Widget widget = widgetMetrics.widget;
			Span widgetSpan = widget.getSpan();
			int x = contentX + sum(colWidths, 0, widgetMetrics.x, gap.horizontalGap) + ((widgetMetrics.x > 0) ? gap.horizontalGap : 0);
			int y = contentY + sum(rowHeights, 0, widgetMetrics.y, gap.verticalGap) + ((widgetMetrics.y > 0) ? gap.verticalGap : 0);
			int widgetWidth = sum(colWidths, widgetMetrics.x, widgetSpan.colspan, gap.horizontalGap);
			int widgetHeight = sum(rowHeights, widgetMetrics.y, widgetSpan.rowspan, gap.verticalGap);
			height = widgetMetrics.height;
			widget.setBounds(x, y, widgetWidth, widgetHeight);
		}

	}

	private static final void align(Metrics first, int[] values, int[] weights, boolean horizontal, int fullSize) {
		for (int size = 1, next = 0; size != 0; size = next, next = 0) {
			for (Metrics metrics = first; metrics != null; metrics = metrics.next) {
				Span span = metrics.widget.getSpan();
				int orientedSpan = horizontal ? span.colspan : span.rowspan;
				if (orientedSpan == size) {
					
					int value;
					if (weights != null) {
						value = horizontal ? metrics.width : metrics.height;
					} else {
						Weight weight = metrics.widget.getWeight();
						value = horizontal ? weight.weightx : weight.weighty;
					}
					
					int index = horizontal ? metrics.x : metrics.y;
					if (weights != null && weights[index] != 0) {
						value = MathFP.toInt(MathFP.mul(weights[index], MathFP.toFP(fullSize)));
					}
					values[index] = Math.max(values[index], value);
					
				} else if ((orientedSpan > size) && ((next == 0) || (next > orientedSpan))) {
					next = orientedSpan;
				}
			}
		}
	}

	private static final int sum(int[] values, int from, int length, int gap) {
		int sum = 0;
		for (int i = 0; i < length; i++) {
			sum += values[from + i];
		}
		if (length > 1) {
			sum += (length - 1) * gap;
		}
		return sum;
	}
	
}
