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
 * You should have received a copy of the GNU Lesser General Public License
 * along with org.kalmeo.util.  If not, see <http://www.gnu.org/licenses/>.
 *  
 * Creation date : 3 déc. 07
 * Copyright (c) Kalmeo 2007-2008. All rights reserved.
 * http://www.kalmeo.org
 */

package org.kalmeo.util;

/**
 * @author bbeaulant
 */
public interface LinkedListItem {

	/**
	 * @return The next {@link LinkedListItem}
	 */
	public LinkedListItem getNext();

	/**
	 * @param next
	 */
	public void setNext(LinkedListItem next);

	/**
	 * @return The previous {@link LinkedListItem}
	 */
	public LinkedListItem getPrevious();

	/**
	 * @param previous
	 */
	public void setPrevious(LinkedListItem previous);

	/**
	 * Compares this {@link LinkedListItem} with the specified <code>item</code>
	 * for order.
	 * 
	 * @param item the {@link LinkedListItem} to be compared.
	 * @param flag the flag witch is transmit from the {@link LinkedList} sort
	 *            method
	 * @return a negative integer, zero, or a positive integer as this item is
	 *         less than, equal to, or greater than the specified item.
	 */
	public int compareTo(LinkedListItem item, int flag);

}
