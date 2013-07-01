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

import org.kalmeo.util.worker.Worker;

/**
 * This class implements a fade transition between two screens. This transition
 * could be parametrized with a duration.
 * 
 * @author bbeaulant
 */
public class FadeTransition implements Transition {

	private int duration;
	private long startTime = -1;

	private int alphaIncrement;
	private int argbIncrement;

	private Image newImage;

	private int[] oldRgb;
	private int oldImageWidth;
	private int oldImageHeight;

	private int frameIndex = 0;

	/**
	 * Construct a {@link FadeTransition}
	 * 
	 * @param duration the transition duraction in milliseconds
	 */
	public FadeTransition(int duration) {
		this.duration = duration;
		int frameDuration = Worker.instance.getFrameDuration();
		alphaIncrement = 0xFF / (duration / frameDuration);
		argbIncrement = alphaIncrement << 24;
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.transition.Transition#init(javax.microedition.lcdui.Image, javax.microedition.lcdui.Image)
	 */
	public void init(Image oldImage, Image newImage) {

		startTime = -1;
		
		oldImageWidth = oldImage.getWidth();
		oldImageHeight = oldImage.getHeight();
		oldRgb = new int[oldImageWidth * oldImageHeight];
		oldImage.getRGB(oldRgb, 0, oldImageWidth, 0, 0, oldImageWidth, oldImageHeight);

		// Save images
		this.newImage = newImage;

	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.transition.Transition#process(javax.microedition.lcdui.Graphics)
	 */
	public boolean process(Graphics g) {
		g.drawImage(newImage, 0, 0, 0);
		if (startTime == -1) {
			startTime = System.currentTimeMillis();
		}
		if (System.currentTimeMillis() - startTime <= duration) {
			if (oldRgb.length != 0 && ((oldRgb[0] >> 24) & 0xFF) >= alphaIncrement) {
				int parity = frameIndex++ % 2;
				for (int i = oldRgb.length - 1 - parity; i >= 0; i -= 2) {
					oldRgb[i] -= argbIncrement;
				}
				g.drawRGB(oldRgb, 0, oldImageWidth, 0, 0, oldImageWidth, oldImageHeight, true);
				return false;
			}
		}
		return true;
	}

}
