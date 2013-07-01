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

import java.util.NoSuchElementException;
import java.util.Vector;

/**
 * This class represent a linked list.
 * 
 * @author bbeaulant
 * @author omarino
 */
public class LinkedList {

	/**
	 * This class represent a {@link LinkedList} enumeration.
	 */
	public class LinkedListEnumeration {
		
		// The LinkedListItem count of this enumeration
		private int length = -1;
		
		// The current item
		private LinkedListItem currentItem;
		
		// The brothers items
		private LinkedListItem previousItem;
		private LinkedListItem nextItem;
		
		// true when a bound is reached
		private boolean endReach;
		private boolean beginReach;
		
		// The filter to apply to the enumeration
		private Filter filter;
		
		/**
		 * Construct a {@link LinkedListEnumeration}
		 */
		private LinkedListEnumeration(Filter filter) {
			reset(filter);
		}
		
		/**
		 * @return the associated {@link LinkedList}
		 */
		public LinkedList getList() {
			return LinkedList.this;
		}
		
		/**
		 * @return a value between 0 and the associated {@link LinkedList} length
		 * corresponding to the {@link LinkedListEnumeration} length
		 */
		public int getLength() {
			if (length < 0) {
				// Get current state of the enumeration
				LinkedListItem 	currentItemBackup = currentItem,
								previousItemBackup = previousItem,
								nextItemBackup = nextItem;
				boolean endReachBackup = endReach,
						beginReachBackup = beginReach;
						
				// Compute the length
				length = 0;
				while (hasNextItems()) {
					nextItem();
					++length;
				}
				
				// Restore the enumeration state
				currentItem = currentItemBackup;
				previousItem = previousItemBackup;
				nextItem = nextItemBackup;
				beginReach = beginReachBackup;
				endReach = endReachBackup;
			}
			return length;
		}
		
		/**
		 * @return the next {@link LinkedListItem} in this enumeration
		 */
		private LinkedListItem getNextItem() {
			if (endReach) {
				return null;
			}
			
			LinkedListItem linkedListItem;
			if (currentItem == null) {
				linkedListItem = first;
			} else {
				linkedListItem = currentItem.getNext();
			}
			
			if (filter != null) {
				for (; linkedListItem != null; linkedListItem = linkedListItem.getNext()) {
					if (filter.accept(linkedListItem) != 0) {
						break;
					}
				}
			}
			endReach = linkedListItem == null;
			
			return linkedListItem;
		}

		/**
		 * @return the previous {@link LinkedListItem} in this enumeration
		 */
		private LinkedListItem getPreviousItem() {
			if (beginReach) {
				return null;
			}
			
			LinkedListItem linkedListItem;
			if (currentItem == null) {
				linkedListItem = last;
			} else {
				linkedListItem = currentItem.getPrevious();
			}
			
			if (filter != null) {
				for (; linkedListItem != null; linkedListItem = linkedListItem.getPrevious()) {
					if (filter.accept(linkedListItem) != 0) {
						break;
					}
				}
			}
			beginReach = linkedListItem == null;
			
			return linkedListItem;
		}

		/**
		 * Returns the next item in this enumeration, where next is defined by
		 * the filter of this enumerator. After calling this method, the
		 * enumeration is advanced to the next available record.
		 * 
		 * @return the previous {@link LinkedListItem} in this enumeration
		 * 
		 * @throws NoSuchElementException When there is no next item available in the enumeration
		 */
		public LinkedListItem nextItem() {
			LinkedListItem item = nextItem != null ? nextItem : getNextItem();
			if (item == null) {
				throw new NoSuchElementException(); 
			}
			currentItem = item;
			nextItem = null;
			previousItem = null;
			return currentItem;
		}

		/**
		 * Returns the previous item in this enumeration, where previous is
		 * defined by the filter of this enumerator. After calling this method,
		 * the enumeration is advanced to the next (previous) available record.
		 * 
		 * @return the previous {@link LinkedListItem} in this enumeration
		 * 
		 * @throws NoSuchElementException When there is no previous item available in the enumeration
		 */
		public LinkedListItem previousItem() {
			LinkedListItem item = previousItem != null ? previousItem : getPreviousItem();
			if (item == null) {
				throw new NoSuchElementException(); 
			}
			currentItem = item;
			nextItem = null;
			previousItem = null;
			return currentItem;
		}

		/**
		 * Returns true if more items exist in the next direction.
		 * 
		 * @return <code>true</code> if more items exist in the next
		 *         direction
		 */
		public boolean hasNextItems() {
			if (!endReach && nextItem == null) {
				nextItem = getNextItem();
				if (nextItem == null) {
					endReach = true;
				}
			}
			return !endReach && nextItem != null;
		}

		/**
		 * Returns true if more items exist in the previous direction.
		 * 
		 * @return <code>true</code> if more items exist in the previous
		 *         direction
		 */
		public boolean hasPreviousItems() {
			if (!beginReach && previousItem == null) {
				previousItem = getPreviousItem();
				if (previousItem == null) {
					beginReach = true;
				}
			}
			return !beginReach && previousItem != null;
		}

		/**
		 * Reset the enumaration with a new {@link Filter}.
		 * 
		 * @param filter
		 */
		public void reset(Filter filter) {
			this.filter = filter;
			length = -1;
			reset();
		}
		
		/**
		 * Returns the enumeration index to the same state as right after the
		 * enumeration was created.
		 */
		public void reset() {
			currentItem = previousItem = nextItem = null;
			endReach = beginReach = false;
		}
		
	}
	
	// The first and last item of the list
	private LinkedListItem first;
	private LinkedListItem last;

	// The size of the list
	private int length;

	/**
	 * @return the first
	 */
	public LinkedListItem getFirst() {
		return first;
	}

	/**
	 * @return the last
	 */
	public LinkedListItem getLast() {
		return last;
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}
	
	/**
	 * @return <code>true</code> if this {@link LinkedList} is empty
	 */
	public boolean isEmpty() {
		return first == null;
	}
	
	/**
	 * Returns the best occurence of the corresponding {@link LinkedListItem}
	 * witch is acceptable by the <code>filter</code>
	 * 
	 * @param filter
	 * @return The best occurence of the corresponding {@link LinkedListItem}
	 *         witch is acceptable by the <code>filter</code>
	 */
	public LinkedListItem find(Filter filter) {
		int bestScore = 0;
		LinkedListItem bestItem = null;
		for (LinkedListItem i = first; i != null; i = i.getNext()) {
			int score = filter.accept(i);
			if (score != 0 && score >= bestScore) {
				bestScore = score;
				bestItem = i;
			}
		}
		return bestItem;
	}

	/**
	 * Returns a {@link Vector} that contains the list of {@link LinkedListItem}
	 * witch iare acceptable by the <code>filter</code>
	 * 
	 * @param filter
	 * @return A {@link Vector} that contains the list of acceptable occurences
	 */
	public Vector findAll(Filter filter) {
		Vector scores = new Vector();
		Vector items = new Vector();
		for (LinkedListItem i = first; i != null; i = i.getNext()) {
			int score = filter.accept(i);
			if (score != 0) {
				Integer integerScore = new Integer(score);
				boolean added = false;
				for (int j = 0; j < scores.size(); ++j) {
					if (score >= ((Integer) scores.elementAt(j)).intValue()) {
						scores.insertElementAt(integerScore, j);
						items.insertElementAt(i, j);
						added = true;
						break;
					}
				}
				if (!added) {
					scores.addElement(integerScore);
					items.addElement(i);
				}
			}
		}
		return items;
	}

	/**
	 * Add a {@link LinkedListItem} to this {@link LinkedList}
	 * 
	 * @param item
	 */
	public void add(LinkedListItem item) {
		if (item != null) {
			if (first == null) {
				first = last = item;
				item.setPrevious(null);
				length = 1;
			} else {
				item.setPrevious(last);
				last.setNext(item);
				last = item;
				length++;
			}
			item.setNext(null);
		}
	}
	
	/**
	 * Add a {@link LinkedListItem} to this {@link LinkedList} by placing it
	 * after or before the <code>referenceItem</code> according to the
	 * <code>after</code> parameter.
	 * 
	 * @param item the {@link LinkedListItem} to add
	 * @param referenceItem the {@link LinkedListItem} used as reference
	 * @param after <code>true</code> if the item is added after the <code>referenceItem</code>
	 */
	public void add(LinkedListItem item, LinkedListItem referenceItem, boolean after) {
		
		// By default the item is appended
		if (isEmpty() || referenceItem == null) {
			add(item);
		}
		
		// Check if referenceItem is realy an item of this list
		boolean valid = false;
		for (LinkedListItem i = referenceItem; i != null; i = i.getPrevious()) {
			if (i == first) {
				valid = true;
				break;
			}
		}
		if (valid) {
			
			if (after) {
				LinkedListItem next = referenceItem.getNext();
				referenceItem.setNext(item);
				item.setPrevious(referenceItem);
				item.setNext(next);
				if (next != null) {
					next.setPrevious(item);
				} else {
					last = item;
				}
			} else {
				LinkedListItem previous = referenceItem.getPrevious();
				referenceItem.setPrevious(item);
				item.setPrevious(previous);
				item.setNext(referenceItem);
				if (previous != null) {
					previous.setNext(item);
				} else {
					first = item;
				}
			}
			length++;
			return;
			
		}
			
		throw new IllegalArgumentException();
	}
	
	/**
	 * Remove a {@link LinkedListItem} from this {@link LinkedList}
	 * 
	 * @param item
	 */
	public void remove(LinkedListItem item) {
		for (LinkedListItem i = first; i != null; i = i.getNext()) {
			if (i == item) {
				if (i == first) {
					first = i.getNext();
				}
				if (i == last) {
					last = i.getPrevious();
				}
				if (i.getPrevious() != null) {
					i.getPrevious().setNext(i.getNext());
				}
				if (i.getNext() != null) {
					i.getNext().setPrevious(i.getPrevious());
					i.setNext(null);
				}
				i.setPrevious(null);
				length--;
				return;
			}
		}
	}

	/**
	 * Remove all items
	 */
	public void removeAll() {
		for (LinkedListItem i = first; i != null; i = i.getNext()) {
			i.setPrevious(null);
		}
		first = null;
		last = null;
		length = 0;
	}
	
	/** 
	 * Sort a list
	 * e.g. : 
	 * <p><code>Person</code> class implement <code>LinkedListItem</code>, a <code>Person</code> have name and firstname value</p>
	 * <p>Set NAME = 0 and FIRSTNAME = 1 flags as <code>public static final int</code> variable and then, when sort is called with NAME flag, the list is sorted by name</p>
	 * 
	 * @param flag is an int value used for specifie test wich be done for sorting the list
	 */
	public void sort(int flag) {
		LinkedListItem item1 = null;
		LinkedListItem item2 = null;
		
		// True if an other sort is needed, imply that almost 1 switch was done during a list's route
		boolean reDo = true;

		while (reDo) {
			reDo = false;
			item1 = first;
			if (item1 != null) {
				item2 = item1.getNext();
			}
			while (item2 != null) {
				// If item1 need to be placed after item2...
				if (item1.compareTo(item2, flag) > 0) {
					// If there are previous items
					//  Item just before item1 change is next for item2
					LinkedListItem previousItems = item1.getPrevious();
					if (previousItems != null) {
						previousItems.setNext(item2);
					} else {
						first = item2;
					}
					
					// If there are next items
					//  Item just after item2 change is previous for item1
					LinkedListItem nextItems = item2.getNext();
					if (nextItems != null) {
						nextItems.setPrevious(item1);
					} else {
						last = item1;
					}
					
					// new previous item for item2 are previousItems 
					item2.setPrevious(previousItems);
					// new next item for item2 is item1
					item2.setNext(item1);
					// new previous item for item1 is item2
					item1.setPrevious(item2);
					// new next item for item1 are nextItems
					item1.setNext(nextItems);

					item2 = item1.getNext();
					
					// While a switch is done, reDo is true
					reDo = true;
				} else {
					// ...else take next elements
					item1 = item2;
					item2 = item2.getNext();
				}
			}
		}
	}

	/**
	 * Returns an Object array containing all references of the elements in this {@link LinkedList}.
	 * 
	 * @return an array representation of this list
	 */
	public LinkedListItem[] toArray() {
		LinkedListItem[] array = new LinkedListItem[getLength()];
		int i = 0;
		for (LinkedListItem item = first; item != null; item = item.getNext()) {
			array[i++] = item;
		}
		return array;
	}
	
	/**
	 * Return the {@link LinkedListEnumeration}
	 *
	 * @param filter the filter to apply to the {@link LinkedListEnumeration}
	 */
	public LinkedListEnumeration enumerate(Filter filter) {
		return new LinkedListEnumeration(filter);
	}
	
}
