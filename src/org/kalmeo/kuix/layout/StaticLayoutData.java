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

import org.kalmeo.kuix.util.Alignment;
import org.kalmeo.util.MathFP;

/**
 * @author bbeaulant
 */
public class StaticLayoutData implements LayoutData {

	// Full instance
	public static final StaticLayoutData instanceFull = new StaticLayoutData(Alignment.TOP_LEFT, MathFP.ONE, MathFP.ONE);
	
	public int x;
	public int y;
	
	public Alignment alignment;
	
	// width and height are fixed-point values
	public int width;
	public int height;

	/**
	 * Construct a {@link StaticLayoutData}
	 * 
	 * @param alignment
	 */
	public StaticLayoutData(Alignment alignment) {
		this(alignment, -1, -1);
	}

	/**
	 * Construct a {@link StaticLayoutData} If <code>width</code> or
	 * <code>height</code> is < 1 it is considered as percentage else pixels
	 * size. Both <code>width</code> and <code>height</code> are
	 * representing by fixed-point integer. {@see org.kalmeo.util.FPUtil} for
	 * more informations
	 * 
	 * @param alignment
	 * @param width a fixed-point integer representing the width
	 * @param height a fixed-point integer representing the height
	 */
	public StaticLayoutData(Alignment alignment, int width, int height) {
		this.alignment = alignment;
		this.width = width;
		this.height = height;
	}

}
