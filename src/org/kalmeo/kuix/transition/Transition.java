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

/**
 * A transition is an effect apply when switching between two screens.
 * Implements this interface to write your own transition.
 * 
 * @author bbeaulant
 */
public interface Transition {

	/**
	 * This method is called before the transition starts.
	 * 
	 * @param oldImage
	 * @param newImage
	 */
	public void init(Image oldImage, Image newImage);
	
	/**
	 * This method is called each frame during the transition.
	 * 
	 * @param g
	 * @return <code>true</code> if the transition is finished
	 */
	public boolean process(Graphics g);
	
}
