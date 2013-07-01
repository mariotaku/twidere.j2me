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

package org.kalmeo.kuix.util;

/**
 * @author bbeaulant
 */
public class Alignment {

	// Values
	private static final byte TOP_VALUE 	= 1 << 0;
	private static final byte LEFT_VALUE 	= 1 << 1;
	private static final byte RIGHT_VALUE 	= 1 << 2;
	private static final byte BOTTOM_VALUE 	= 1 << 3;
	private static final byte CENTER_VALUE 	= 1 << 4;

	private static final byte FILL_VALUE 	= 1 << 5;
	
	// Alignments
	public static final Alignment TOP_LEFT = new Alignment((byte) (TOP_VALUE | LEFT_VALUE));
	public static final Alignment TOP = new Alignment(TOP_VALUE);
	public static final Alignment TOP_RIGHT = new Alignment((byte) (TOP_VALUE | RIGHT_VALUE));
	public static final Alignment LEFT = new Alignment(LEFT_VALUE);
	public static final Alignment CENTER = new Alignment(CENTER_VALUE);
	public static final Alignment RIGHT = new Alignment(RIGHT_VALUE);
	public static final Alignment BOTTOM_LEFT = new Alignment((byte) (BOTTOM_VALUE | LEFT_VALUE));
	public static final Alignment BOTTOM = new Alignment(BOTTOM_VALUE);
	public static final Alignment BOTTOM_RIGHT = new Alignment((byte) (BOTTOM_VALUE | RIGHT_VALUE));
	
	public static final Alignment FILL = new Alignment(FILL_VALUE);
	public static final Alignment FILL_TOP = new Alignment((byte) (FILL_VALUE | TOP_VALUE));
	public static final Alignment FILL_LEFT = new Alignment((byte) (FILL_VALUE | LEFT_VALUE));
	public static final Alignment FILL_CENTER = new Alignment((byte) (FILL_VALUE | CENTER_VALUE));
	public static final Alignment FILL_RIGHT = new Alignment((byte) (FILL_VALUE | RIGHT_VALUE));
	public static final Alignment FILL_BOTTOM = new Alignment((byte) (FILL_VALUE | BOTTOM_VALUE));
	
	// The alignment value
	public byte value;

	/**
	 * Construct a {@link Alignment}
	 * 
	 * @param value
	 */
	private Alignment(byte value) {
		this.value = value;
	}

	/**
	 * @return <code>true</code> if the alignment contains a top constraint
	 */
	public boolean isTop() {
		return (value & TOP_VALUE) == TOP_VALUE;
	}
	
	/**
	 * @return <code>true</code> if the alignment contains a bottom constraint
	 */
	public boolean isBottom() {
		return (value & BOTTOM_VALUE) == BOTTOM_VALUE;
	}
	
	/**
	 * @return <code>true</code> if the alignment contains a left constraint
	 */
	public boolean isLeft() {
		return (value & LEFT_VALUE) == LEFT_VALUE;
	}
	
	/**
	 * @return <code>true</code> if the alignment contains a right constraint
	 */
	public boolean isRight() {
		return (value & RIGHT_VALUE) == RIGHT_VALUE;
	}
	
	/**
	 * @return <code>true</code> if the alignment contains an horizontal center constraint
	 */
	public boolean isHorizontalCenter() {
		return (value & CENTER_VALUE) == CENTER_VALUE
				|| (isTop() || isBottom()) && !(isLeft() || isRight());
	}
	
	/**
	 * @return <code>true</code> if the alignment contains an horizontal center constraint
	 */
	public boolean isVerticalCenter() {
		return (value & CENTER_VALUE) == CENTER_VALUE
				|| (isLeft() || isRight()) && !(isTop() || isBottom()) ;
	}
	
	/**
	 * @return <code>true</code> if the alignment contains a fill constraint
	 */
	public boolean isFill() {
		return (value & FILL_VALUE) == FILL_VALUE;
	}
	
	/**
	 * Calculte the aligned x coordiante from <code>contentWidth</code> in
	 * <code>width</code>
	 * 
	 * @param width
	 * @param contentWidth
	 * @return The aligned x coordiante from <code>contentWidth</code> in
	 *         <code>width</code>
	 */
	public int alignX(int width, int contentWidth) {
		if (isHorizontalCenter()) {
			return (width - contentWidth) / 2;
		} else if (isRight()) {
			return width - contentWidth;
		}
		return 0;
	}
	
	/**
	 * Calculte the aligned y coordiante from <code>contentWidth</code> in
	 * <code>height</code>
	 * 
	 * @param height
	 * @param contentHeight
	 * @return The aligned y coordiante from <code>contentHeight</code> in
	 *         <code>height</code>
	 */
	public int alignY(int height, int contentHeight) {
		if (isVerticalCenter()) {
			return (height - contentHeight) / 2;
		} else if (isBottom()) {
			return height - contentHeight;
		}
		return 0;
	}
	
	/**
	 * Combine <code>verticalAlignment</code> and
	 * <code>horizontalAlignment</code> into a new {@link Alignment} instance.
	 * 
	 * @param alignment1
	 * @param alignment2
	 * @return a new combined alignment instance.
	 */
	public static Alignment combine(Alignment verticalAlignment, Alignment horizontalAlignment) {
		if (verticalAlignment != null && horizontalAlignment != null) {
			byte value = (byte) (((verticalAlignment.isTop() ? TOP_VALUE : 0) 
											| (verticalAlignment.isVerticalCenter() ? CENTER_VALUE : 0)
											| (verticalAlignment.isBottom() ? BOTTOM_VALUE : 0))
								| ((horizontalAlignment.isLeft() ? LEFT_VALUE : 0) 
											| (horizontalAlignment.isHorizontalCenter() ? CENTER_VALUE : 0)
											| (horizontalAlignment.isRight() ? RIGHT_VALUE : 0)));
			return new Alignment(value);
		}
		return null;
	}
	
}
