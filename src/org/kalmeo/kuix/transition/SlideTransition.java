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
 * Creation date : 20 mai 08
 * Copyright (c) Kalmeo 2007-2008. All rights reserved.
 * http://www.kalmeo.org
 */

package org.kalmeo.kuix.transition;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.kalmeo.kuix.util.Alignment;

/**
 * This class implements a slide transition between two screens. This transition
 * could be parametrized with a direction (left, right, top and bottom).
 * 
 * @author bbeaulant
 */
public class SlideTransition implements Transition {

	private Alignment direction;
	
	protected Image oldImage;
	protected Image newImage;
	
	private int xOffset;
	private int yOffset;
	
	/**
	 * Construct a {@link SlideTransition}
	 *
	 * @param direction
	 */
	public SlideTransition(Alignment direction) {
		this.direction = direction;
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.transition.Transition#init(javax.microedition.lcdui.Image, javax.microedition.lcdui.Image)
	 */
	public void init(Image oldImage, Image newImage) {
		
		// Save images
		this.oldImage = oldImage;
		this.newImage = newImage;
		
		// Init offsets
		xOffset = 0;
		yOffset = 0;
		if (direction.isLeft()) {
			xOffset = -newImage.getWidth();
		} else if (direction.isRight()) {
			xOffset = newImage.getWidth();
		} else if (direction.isTop()) {
			yOffset = -newImage.getHeight();
		} else if (direction.isBottom()) {
			yOffset = newImage.getHeight();
		}
		
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.transition.Transition#process(javax.microedition.lcdui.Graphics)
	 */
	public boolean process(Graphics g) {
		xOffset = xOffset / 2;
		yOffset = yOffset / 2;
		g.drawImage(oldImage, 0, 0, 0);
		g.drawImage(newImage, xOffset, yOffset, 0);
		return xOffset == 0 && yOffset == 0;
	}

}
