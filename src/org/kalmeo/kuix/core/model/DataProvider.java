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
 * Creation date : 7 févr. 08
 * Copyright (c) Kalmeo 2008. All rights reserved.
 */

package org.kalmeo.kuix.core.model;

import java.util.Hashtable;
import java.util.Vector;

import org.kalmeo.kuix.widget.Widget;
import org.kalmeo.util.Filter;
import org.kalmeo.util.LinkedList;
import org.kalmeo.util.LinkedListItem;
import org.kalmeo.util.LinkedList.LinkedListEnumeration;

/**
 * This class represent the base object of the Kuix data model.<br>
 * A {@link DataProvider} help you to organize data model to interact with
 * widgets.<br>
 * You can customize returns value by overriding the
 * <code>getUserDefinedValue</code> function.<br>
 * <b>Since 1.0.1</b>, you can create a tree of dataproviders by adding
 * dataproviders as slave of an other. in this case, the <code>getValue</code>
 * function could returns a value from the dataprovider itself if it exists of
 * from its slaves.<br>
 * <code>dispatchUpdateEvent</code> and <code>dispatchItemsUpdateEvent</code>
 * methods invoke rescursivly each dataprovider's masters.
 * 
 * @author bbeaulant
 */
public class DataProvider implements LinkedListItem {

	// Update event types
	public static final byte ADD_MODEL_UPDATE_EVENT_TYPE 			= 1;
	public static final byte ADD_BEFORE_MODEL_UPDATE_EVENT_TYPE 	= 2;
	public static final byte ADD_AFTER_MODEL_UPDATE_EVENT_TYPE 		= 3;
	public static final byte REMOVE_MODEL_UPDATE_EVENT_TYPE 		= 4;
	public static final byte SORT_MODEL_UPDATE_EVENT_TYPE 			= 5;
	public static final byte FILTER_MODEL_UPDATE_EVENT_TYPE 		= 6;
	public static final byte CLEAR_MODEL_UPDATE_EVENT_TYPE 			= 7;

	// Masters / slaves vectors
	private Vector masters;
	private Vector slaves;
	
	// Hashtable of property / itemsValues (Object) pair
	private Hashtable itemsValues;
	
	// Hashtable of property / itemsFilters (Filter) pair
	private Hashtable itemsFilters;

	// List of binded widgets
	private Vector bindedWidgets;

	// LinkedListItem properties
	private DataProvider previous;
	private DataProvider next;
	
	/* (non-Javadoc)
	 * @see org.kalmeo.util.LinkedListItem#getNext()
	 */
	public LinkedListItem getNext() {
		return next;
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.util.LinkedListItem#getPrevious()
	 */
	public LinkedListItem getPrevious() {
		return previous;
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.util.LinkedListItem#setNext(org.kalmeo.util.LinkedListItem)
	 */
	public void setNext(LinkedListItem next) {
		this.next = (DataProvider) next;
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.util.LinkedListItem#setPrevious(org.kalmeo.util.LinkedListItem)
	 */
	public void setPrevious(LinkedListItem previous) {
		this.previous = (DataProvider) previous;
	}
	
	// Masters / Slaves ////////////////////////////////////////////////////////////////////
	
	/**
	 * Add a slave {@link DataProvider} to this {@link DataProvider}.
	 * 
	 * @param slaveDataProvider
	 * @since 1.0.1
	 */
	public void addSlave(DataProvider slaveDataProvider) {
		if (slaveDataProvider != null) {
			if (slaves == null) {
				slaves = new Vector();
			}
			if (!slaves.contains(slaveDataProvider)) {
				if (slaveDataProvider.masters == null) {
					slaveDataProvider.masters = new Vector();
				}
				slaveDataProvider.masters.addElement(this);
				slaves.addElement(slaveDataProvider);
			}
		}
	}
	
	/**
	 * Remove the <code>slaveDataProvider</code> instance from this
	 * {@link DataProvider} slaves.
	 * 
	 * @param slaveDataProvider
	 * @since 1.0.1
	 */
	public void removeSlave(DataProvider slaveDataProvider) {
		if (slaves != null && slaveDataProvider != null && slaves.contains(slaveDataProvider)) {
			if (slaveDataProvider.masters != null) {
				slaveDataProvider.masters.removeElement(this);
			}
			slaves.removeElement(slaveDataProvider);
		}
	}
	
	/**
	 * Remove all {@link DataProvider} slaves of this instance.
	 * 
	 * @since 1.0.1
	 */
	public void removeAllSlaves() {
		if (slaves != null) {
			for (int i = slaves.size() - 1; i >= 0; --i) {
				removeSlave(((DataProvider) slaves.elementAt(i)));
			}
		}
	}
	
	/**
	 * Remove this {@link DataProvider} from a specific master.
	 * 
	 * @param masterDataProvider
	 * @since 1.0.1
	 */
	public void removeFromMaster(DataProvider masterDataProvider) {
		if (masters != null && masterDataProvider != null && masters.contains(masterDataProvider)) {
			masterDataProvider.removeSlave(this);
		}
	}
	
	/**
	 * Remove this {@link DataProvider} from its masters.
	 * 
	 * @since 1.0.1
	 */
	public void removeFromMasters() {
		if (masters != null) {
			for (int i = masters.size() - 1; i >= 0; --i) {
				((DataProvider) masters.elementAt(i)).removeSlave(this);
			}
		}
	}
	
	// Values ////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the value (user defined values and items values) corresponding the
	 * given <code>property</code>.
	 * 
	 * @param property
	 * @return the value associated with the given <code>property</code>.
	 */
	public final Object getValue(String property) {
		Object value = getUserDefinedValue(property);
		if (value == null) {
			value = enumerateItems(property, true);
		}
		if (value == null && slaves != null) {
			for (int i = slaves.size() - 1; i >= 0; --i) {
				value = ((DataProvider) slaves.elementAt(i)).getValue(property);
				if (value != null) {
					break;
				}
			}
		}
		return value;
	}
	
	/**
	 * Returns the user defined value corresponding the given <code>property</code>.<br>
	 * Override the method to returns your customs values.
	 * 
	 * @return the user defined value associated with the given <code>property</code>.
	 */
	protected Object getUserDefinedValue(String property) {
		return null;
	}

	/**
	 * Returns the <code>property</code> associated string value, or null if
	 * the property has no value or value is not a string.
	 * 
	 * @param property
	 * @return the string value, or null if the property
	 *         has no value or value is not a string.
	 */
	public String getStringValue(String property) {
		try {
			return (String) getValue(property);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Returns the <code>property</code> associated items value, or null if
	 * the property has no value or value is not a {@link LinkedList}.<br>
	 * This method defer from <code>getValue</code> because it returns a unique
	 * {@link LinkedList} instance by property instead of a new
	 * {@link LinkedListEnumeration} each time the method is called.
	 * 
	 * @param property
	 * @return the {@link LinkedList} value, or null if the property has no
	 *         value or value is not a {@link LinkedList}.
	 */
	public LinkedList getItemsValue(String property) {
		try {
			if (itemsValues != null) {
				return (LinkedList) itemsValues.get(property);
			}
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * Returns the {@link LinkedList} relative to the given
	 * <code>property</code>. If the list doesn't exist, a new one is created
	 * and added to itemsValues.
	 * 
	 * @param property
	 * @return the {@link LinkedList} relative to the given
	 *         <code>property</code>, or <code>null</code> if an other non
	 *         items value is associated with this <code>property</code>.
	 */
	private LinkedList getOrCreateItemsValue(String property) {
		if (itemsValues == null) {
			itemsValues = new Hashtable();
		}
		LinkedList items = getItemsValue(property);
		if (items == null && !itemsValues.containsKey(property)) {
			items = new LinkedList();
			itemsValues.put(property, items);
		}
		return items;
	}

	/**
	 * Returns the first or last item for a specific <code>property</code>
	 * items list.
	 * 
	 * @param property
	 * @param first
	 * @return the first or last item
	 */
	private DataProvider getFirstOrLastItem(String property, boolean first) {
		LinkedList items = getItemsValue(property);
		if (items != null) {
			if (first) {
				return (DataProvider) items.getFirst();
			} else {
				return (DataProvider) items.getLast();
			}
		}
		return null;
	}
	
	/**
	 * Returns the count of items assocayed with the specified
	 * <code>property</code>.
	 * 
	 * @param property
	 * @return the count of items assocayed with the specified
	 *         <code>property</code>
	 */
	public int countItemValues(String property) {
		LinkedList items = getItemsValue(property);
		if (items != null) {
			return items.getLength();
		}
		return 0;
	}
	
	/**
	 * Returns the first {@link DataProvider} item of the <code>property</code>
	 * items list or <code>null</code> if the list is empty or doesn't exists.
	 * 
	 * @param property
	 * @return the first {@link DataProvider} item
	 */
	public DataProvider getFirstItem(String property) {
		return getFirstOrLastItem(property, true);
	}

	/**
	 * Returns the last {@link DataProvider} item of the <code>property</code>
	 * items list or <code>null</code> if the list is empty or doesn't exists.
	 * 
	 * @param property
	 * @return the last {@link DataProvider} item
	 */
	public DataProvider getLastItem(String property) {
		return getFirstOrLastItem(property, false);
	}
	
	/**
	 * Returns the {@link LinkedListEnumeration} instance or <code>null</code>
	 * if no value is associated with this <code>property</code>. If a filter
	 * is associated with this <code>property</code> and
	 * <code>useFilter</code> is set to <code>true</code>, the enumeration
	 * use it.
	 * 
	 * @param property
	 * @param useFilter
	 * @return the {@link LinkedListEnumeration} instance or <code>null</code>
	 *         if no value is associated with this <code>property</code>.
	 */
	public LinkedListEnumeration enumerateItems(String property, boolean useFilter) {
		LinkedList items = getItemsValue(property);
		if (items != null) {
			Filter filter = null;
			if (useFilter && itemsFilters != null) {
				// Retrieved filter could be null
				filter = (Filter) itemsFilters.get(property);
			}
			return items.enumerate(filter);
		}
		return null;
	}
	
	/**
	 * Add the <code>item</code> to the <code>property</code> items list.
	 * 
	 * @param property
	 * @param item
	 * @return the new items linkedList size or <code>-1</code> if adding is
	 *         faild.
	 */
	public int addItem(String property, DataProvider item) {
		return addItem(property, item, null, false);
	}
	
	/**
	 * Add the <code>item</code> to the <code>property</code> items list by
	 * placing it after or before the <code>referenceItem</code> according to
	 * the <code>after</code> parameter. If <code>item</code> is
	 * <code>null</code> nothing append and <code>-1</code> is returned.
	 * 
	 * @param property
	 * @param item
	 * @param referenceItem
	 * @param after
	 * @return the new items linkedList size or <code>-1</code> if adding is
	 *         faild.
	 */
	public int addItem(String property, DataProvider item, DataProvider referenceItem, boolean after) {
		if (item != null) {
			LinkedList items = getOrCreateItemsValue(property);
			if (items != null) {
				if (items.isEmpty() || referenceItem == null) {
					items.add(item);
					dispatchItemsUpdateEvent(ADD_MODEL_UPDATE_EVENT_TYPE, property, item, null);
				} else {
					try {
						items.add(item, referenceItem, after);
						dispatchItemsUpdateEvent(after ? ADD_AFTER_MODEL_UPDATE_EVENT_TYPE : ADD_BEFORE_MODEL_UPDATE_EVENT_TYPE, property, item, null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return items.getLength();
			}
		}
		return -1;
	}
	
	/**
	 * Remove the <code>item</code> from the <code>property</code> items
	 * list.
	 * 
	 * @param property
	 * @param item
	 * @return the new items linkedList size or <code>-1</code> if removing is
	 *         faild.
	 */
	public int removeItem(String property, DataProvider item) {
		if (item != null && itemsValues != null) {
			LinkedList items = getItemsValue(property);
			if (items != null) {
				int itemsLength = items.getLength();
				items.remove(item);
				dispatchItemsUpdateEvent(REMOVE_MODEL_UPDATE_EVENT_TYPE, property, item, null);
				return items.getLength() < itemsLength ? itemsLength - 1 : -1;
			}
		}
		return -1;
	}

	/**
	 * Remove all items from the <code>property</code> items list.
	 * 
	 * @param property
	 */
	public void removeAllItems(String property) {
		if (itemsValues != null) {
			itemsValues.remove(property);
			dispatchItemsUpdateEvent(CLEAR_MODEL_UPDATE_EVENT_TYPE, property, null, null);
		}
	}

	/**
	 * Search a {@link LinkedListItem} in {@link LinkedList} and return
	 * <code>true</code> if it's in. The value linked to <code>property</code>
	 * must be a {@link LinkedList}.
	 * 
	 * @param property the property where <code>item</code> may be found
	 * @param item the {@link LinkedListItem} to search
	 * @return <code>true</code> if <code>item</code> exist in
	 *         {@link LinkedList} <code>property</code>, <code>false</code>
	 *         else.
	 */
	public boolean contains(String property, final DataProvider item) {
		if (item != null) {
			LinkedList items = getItemsValue(property);
			if (items != null) {
				if (items.find(new Filter() {
					public int accept(Object obj) {
						if (obj.equals(item)) {
							return 1;
						}
						return 0;
					}
				}) != null) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Sort <code>property</code> items list.
	 * 
	 * @param property
	 * @param flag
	 */
	public void sortItems(String property, int flag) {
		LinkedList items = getItemsValue(property);
		if (items != null) {
			items.sort(flag);
			dispatchItemsUpdateEvent(SORT_MODEL_UPDATE_EVENT_TYPE, property, null, enumerateItems(property, true));
		}
	}
	
	/**
	 * Filter <code>property</code> items list.
	 * 
	 * @param property
	 * @param filter the {@link Filter} to apply to the enumeration. Set it null to retrieve all items of the enumeration
	 */
	public void setItemsFilter(String property, Filter filter) {
		LinkedList items = getItemsValue(property);
		if (items != null) {
			if (filter == null) {
				if (itemsFilters == null) {
					// No previous filter and no new filter : do nothing
					return;
				}	
				if (itemsFilters.containsKey(property)) {
					// Previous filter is removed from filter list
					itemsFilters.remove(property);
				}
			} else {
				if (itemsFilters == null) {
					itemsFilters = new Hashtable();
				}
				itemsFilters.put(property, filter);
			}
			LinkedListEnumeration itemsEnumeration = items.enumerate(filter);
			dispatchItemsUpdateEvent(FILTER_MODEL_UPDATE_EVENT_TYPE, property, null, itemsEnumeration);
		}
	}

	/**
	 * Bind the <code>widget</code> to this {@link DataProvider}.
	 * 
	 * @param widget
	 */
	public void bind(Widget widget) {
		if (bindedWidgets == null) {
			bindedWidgets = new Vector();
		} else if (bindedWidgets.contains(widget)) {
			return;
		}
		bindedWidgets.addElement(widget);
		widget.setDataProvider(this);
		widget.processDataBindEvent();
	}

	/**
	 * Unbind the <code>widget</code> from this {@link DataProvider}.
	 * 
	 * @param widget
	 */
	public void unbind(Widget widget) {
		if (bindedWidgets != null) {
			widget.setDataProvider(null);
			bindedWidgets.removeElement(widget);
		}
	}

	/**
	 * Unbind all widgets from this {@link DataProvider}.
	 */
	public void unbindAll() {
		if (bindedWidgets != null) {
			for (int i = bindedWidgets.size() - 1; i >= 0; --i) {
				((Widget) (bindedWidgets.elementAt(i))).setDataProvider(null);
			}
			bindedWidgets.removeAllElements();
		}
	}

	/**
	 * Dispatch an update event for a specific <code>property</code> to all
	 * binded widgets.
	 * 
	 * @param property
	 */
	protected void dispatchUpdateEvent(String property) {
		if (bindedWidgets != null) {
			for (int i = bindedWidgets.size() - 1; i >= 0; --i) {
				((Widget) (bindedWidgets.elementAt(i))).processModelUpdateEvent(property);
			}
		}
		if (masters != null) {
			for (int i = masters.size() - 1; i >= 0; --i) {
				((DataProvider) masters.elementAt(i)).dispatchUpdateEvent(property);
			}
		}
	}

	/**
	 * Dispatch an items update event for a specific <code>property</code> to all
	 * binded widgets.
	 * 
	 * @param type
	 * @param property
	 * @param item
	 * @param itemsEnumeration
	 */
	protected void dispatchItemsUpdateEvent(byte type, String property, DataProvider item, LinkedListEnumeration itemsEnumeration) {
		if (bindedWidgets != null) {
			for (int i = bindedWidgets.size() - 1; i >= 0; --i) {
				((Widget) (bindedWidgets.elementAt(i))).processItemsModelUpdateEvent(type, property, item, itemsEnumeration);
			}
		}
		if (masters != null) {
			for (int i = masters.size() - 1; i >= 0; --i) {
				((DataProvider) masters.elementAt(i)).dispatchItemsUpdateEvent(type, property, item, itemsEnumeration);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.util.LinkedListItem#compareTo(org.kalmeo.util.LinkedListItem, int)
	 */
	public int compareTo(LinkedListItem item, int flag) {
		return 0;
	}

}
