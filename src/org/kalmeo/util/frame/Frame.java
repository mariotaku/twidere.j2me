/*
 * This file is part of org.kalmeo.util.
 * 
 * org.kalmeo.util is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * org.kalmeo.util is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 *  
 * Creation date : 21 nov. 2007
 * Copyright (c) Kalmeo 2007-2008. All rights reserved.
 * http://www.kalmeo.org
 */

package org.kalmeo.util.frame;

/**
 * @author bbeaulant
 */
public interface Frame {

	/**
	 * <b>Since 1.0.1, the old onMessage(String name, Object[] arguments) signature is deprecated.</b><br>
	 * 
	 * Called when message event occure.
	 * 
	 * @param identifier the message identifier
	 * @param arguments the message arguments
	 * @return <code>true</code> if the message should be passed to the next
	 *         frame in the stack.
	 */
	public boolean onMessage(Object identifier, Object[] arguments);
	
	/**
	 * Called when the frame is added to the stack (FrameHandler).
	 */
	public void onAdded();
	
	/**
	 * Called when the frame is removed from the stack (FrameHandler).
	 */
	public void onRemoved();
	
}
