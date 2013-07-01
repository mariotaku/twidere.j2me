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
 * Creation date : 7 oct. 2008
 * Copyright (c) Kalmeo 2007-2008. All rights reserved.
 * http://www.kalmeo.org
 */

package org.kalmeo.kuix.widget;

import javax.microedition.lcdui.Graphics;

import org.kalmeo.kuix.core.Kuix;

/**
 * This widget is used to mask region of display from Kuix repaint process.<br>
 * Basicly it could be used to implement a video render region.
 * 
 * @author bbeaulant
 */
public class UnpaintableWidget extends Widget {
	
	/**
	 * Construct a {@link UnpaintableWidget}
	 * 
	 * @param tag
	 */
	public UnpaintableWidget(String tag) {
		super(tag);
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#paintImpl(javax.microedition.lcdui.Graphics)
	 */
	public void paintImpl(Graphics g) {
		Kuix.getCanvas().addUnpaintableWidget(this);
		
		// Ignore children painting
		
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#cleanUp()
	 */
	public void cleanUp() {
		super.cleanUp();
		
		// Remove this widget from the KuixCanvas unpaintable widget list
		Kuix.getCanvas().removeUnpaintableWidget(this);
		
	}
	

}
