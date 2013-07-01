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

package org.kalmeo.kuix.widget;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Hashtable;

import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.core.KuixConstants;
import org.kalmeo.kuix.core.model.DataProvider;
import org.kalmeo.kuix.layout.InlineLayout;
import org.kalmeo.kuix.layout.Layout;
import org.kalmeo.kuix.util.Alignment;
import org.kalmeo.util.LinkedList;
import org.kalmeo.util.LinkedListItem;
import org.kalmeo.util.LinkedList.LinkedListEnumeration;

/**
 * This class represents a list. <br>
 * <br>
 * <strong>For further informations, visit the <a
 * href="http://www.kalmeo.org/files/kuix/widgetdoc/index.html"
 * target="new">Kuix widgets reference page</a></strong>.
 * 
 * @author bbeaulant
 */
public class List extends Widget {

	// Defaults
	private static final Alignment LIST_ALIGN = Alignment.FILL_TOP;
	private static final Layout LIST_LAYOUT = new InlineLayout(false, Alignment.FILL);
	
	// Default item widget renderer
	private ByteArrayInputStream renderer;
	
	// Represent the mapping between DataProviders and ItemWidgets
	private final Hashtable dataProvidersMapping = new Hashtable();

	/**
	 * Construct a {@link List}
	 */
	public List() {
		this(KuixConstants.LIST_WIDGET_TAG);
	}
	
	/**
	 * Construct a {@link List}
	 *
	 * @param tag
	 */
	public List(String tag) {
		super(tag);
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#setAttribute(java.lang.String, java.lang.String)
	 */
	public boolean setAttribute(String name, String value) {
		if (KuixConstants.RENDERER_ATTRIBUTE.equals(name)) {
			if (value != null && value.length() != 0) {
				setRenderer(new ByteArrayInputStream(value.getBytes()));
				return true;
			}
			return false;
		}
		return super.setAttribute(name, value);
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#setObjectAttribute(java.lang.String, java.lang.Object)
	 */
	public boolean setObjectAttribute(String name, Object value) {
		if (KuixConstants.ITEMS_ATTRIBUTE.equals(name)) {
			if (value instanceof LinkedListEnumeration) {
				setItems((LinkedListEnumeration) value);
				return true;
			} else if (value == null) {
				setItems(null);
				return true;
			}
			return false;
		}
		return super.setObjectAttribute(name, value);
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#isObjectAttribute(java.lang.String)
	 */
	public boolean isObjectAttribute(String name) {
		if (KuixConstants.ITEMS_ATTRIBUTE.equals(name)) {
			return true;
		}
		return super.isObjectAttribute(name);
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getDefaultStyleAttributeValue(java.lang.String)
	 */
	protected Object getDefaultStylePropertyValue(String name) {
		if (KuixConstants.LAYOUT_STYLE_PROPERTY.equals(name)) {
			return LIST_LAYOUT;
		}
		if (KuixConstants.ALIGN_STYLE_PROPERTY.equals(name)) {
			return LIST_ALIGN;
		}
		return super.getDefaultStylePropertyValue(name);
	}
	
	/**
	 * @return the renderer
	 */
	public ByteArrayInputStream getRenderer() {
		return renderer;
	}

	/**
	 * @param renderer the renderer to set
	 */
	public void setRenderer(ByteArrayInputStream renderer) {
		this.renderer = renderer;
	}

	/**
	 * Redifine all item values
	 * 
	 * @param itemsEnumeration
	 */
	public void setItems(LinkedListEnumeration itemsEnumeration) {
		removeAllItems();
		if (itemsEnumeration != null) {
			try {
				itemsEnumeration.reset();
				while (itemsEnumeration.hasNextItems()) {
					addItem((DataProvider) itemsEnumeration.nextItem());
				}
			} catch (ClassCastException e) {
				// An item need to extends the DataProvider model
				e.printStackTrace();
			}
		}
	}

	/**
	 * Add an item value
	 * 
	 * @param item
	 * @return The added {@link Widget} or <code>null</code> if no renderer is set on the list.
	 */
	public Widget addItem(DataProvider item) {
		return addItem(item, null, false);
	}
	
	/**
	 * Add an item value
	 * 
	 * @param item
	 * @param renderer
	 * @return The added {@link Widget}
	 */
	public Widget addItem(DataProvider item, InputStream renderer) {
		return addItem(item, renderer, null, false);
	}
	
	/**
	 * Add an item value
	 * 
	 * @param item
	 * @param enabled
	 * @param prepend
	 * @return The added {@link Widget} or <code>null</code> if no renderer is set on the list
	 */
	public Widget addItem(DataProvider item, DataProvider referenceItem, boolean after) {
		if (renderer != null) {
			renderer.reset();
			return addItem(item, renderer, referenceItem, after);
		}
		return null;
	}
	
	/**
	 * Add an item value near an other.
	 * 
	 * @param item
	 * @param renderer
	 * @param referenceItem
	 * @param after
	 * @return The added {@link Widget}
	 */
	public Widget addItem(DataProvider item, InputStream renderer, DataProvider referenceItem, boolean after) {
		return internalAddItem(item, renderer, referenceItem != null ? getItemWidget(referenceItem) : null, after);
	}
	
	/**
	 * Add an item value near an other {@link Widget}.
	 * 
	 * @param item
	 * @param renderer
	 * @param referenceWidget
	 * @param after
	 * @return The added {@link Widget}
	 */
	private Widget internalAddItem(DataProvider item, InputStream renderer, Widget referenceWidget, boolean after) {
		Widget itemWidget = Kuix.loadWidget(renderer, item);
		itemWidget.setDataProvider(item);
		if (referenceWidget != null) {
			add(itemWidget, referenceWidget, after);
		} else {
			add(itemWidget);
		}
		dataProvidersMapping.put(item, itemWidget);
		return itemWidget;
	}
	
	/**
	 * Remove an item value if it exists in the {@link List}
	 * 
	 * @param item
	 * @return <code>true</code> if the <code>item</code> is found and the
	 *         associated item widget removed
	 */
	public boolean removeItem(DataProvider item) {
		Widget itemWidget = getItemWidget(item);
		internalRemoveItem(itemWidget);
		return itemWidget != null;
	}
	
	/**
	 * @param itemWidget
	 * @return <code>true</code> if the <code>itemWidget</code> is found and the
	 *         associated item widget removed
	 */
	private void internalRemoveItem(Widget itemWidget) {
		if (itemWidget != null) {
			dataProvidersMapping.remove(itemWidget.getDataProvider());
			itemWidget.cleanUp();
			itemWidget.remove();
		}
	}
	
	/**
	 * Remove all items
	 */
	public void removeAllItems() {
		cleanUpChildren();
		removeAll();
		dataProvidersMapping.clear();
	}
	
	/**
	 * @param item
	 * @return The item {@link Widget} associated with the specified
	 *         {@link DataProvider}. If no item is found <code>null</code> is
	 *         returned
	 */
	public Widget getItemWidget(DataProvider item) {
		return (Widget) dataProvidersMapping.get(item);
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#processItemsModelUpdateEvent(byte, java.lang.String, org.kalmeo.kuix.core.model.DataProvider, org.kalmeo.util.LinkedList.LinkedListEnumeration)
	 */
	public boolean processItemsModelUpdateEvent(byte type, String property, DataProvider item, LinkedListEnumeration itemsEnumeration) {
		if (hasBindInstruction()) {
			for (BindInstruction bindInstruction = (BindInstruction) bindInstructions.getFirst(); bindInstruction != null; bindInstruction = bindInstruction.next) {
				if (bindInstruction.hasProperty(property)) {
					switch (type) {

						case DataProvider.ADD_MODEL_UPDATE_EVENT_TYPE:
							addItem(item);
							return true;

						case DataProvider.ADD_BEFORE_MODEL_UPDATE_EVENT_TYPE:
							addItem(item, (DataProvider) item.getNext(), false);
							return true;
							
						case DataProvider.ADD_AFTER_MODEL_UPDATE_EVENT_TYPE:
							addItem(item, (DataProvider) item.getPrevious(), true);
							return true;
							
						case DataProvider.REMOVE_MODEL_UPDATE_EVENT_TYPE:
							removeItem(item);
							return true;

						case DataProvider.SORT_MODEL_UPDATE_EVENT_TYPE: {
							
							// Reorder item widgets
							
							Widget previousItemWidget = null;
							itemsEnumeration.reset();
							if (itemsEnumeration.hasNextItems()) {
								// Bring the first item to front
								previousItemWidget = getItemWidget((DataProvider) itemsEnumeration.nextItem());
								bringToFront(previousItemWidget);
							}
							
							// BringNear the orthers
							Widget itemWidget;
							while (itemsEnumeration.hasNextItems()) {
								itemWidget = getItemWidget((DataProvider) itemsEnumeration.nextItem());
								if (itemWidget != null) {
									bringNear(itemWidget, previousItemWidget, true);
									previousItemWidget = itemWidget;
								}
							}
							return true;
						}
							
						case DataProvider.FILTER_MODEL_UPDATE_EVENT_TYPE: {
							
							itemsEnumeration.reset();
							if (itemsEnumeration.hasNextItems()) {
								
								LinkedList items = itemsEnumeration.getList();
								LinkedListItem linkedListItem = itemsEnumeration.nextItem();
								Widget itemWidget = getChild();
								
								for (LinkedListItem currentItem = items.getFirst(); currentItem != null; currentItem = currentItem.getNext()) {
									
									boolean isEnumerationItem = currentItem.equals(linkedListItem);
									boolean isItemWidgetDataProvider = itemWidget != null && currentItem.equals(itemWidget.getDataProvider());
									
									// If itemWidget and linkedListItem are not in at least one of two list, continue
									if (!isItemWidgetDataProvider && !isEnumerationItem) {
										continue;
									}
									
									// If itemWidget and linkedListItem are in both of two list, get next items and continue
									if (isItemWidgetDataProvider && isEnumerationItem) {
										linkedListItem = null;
										if (itemsEnumeration.hasNextItems()) {
											linkedListItem = itemsEnumeration.nextItem();
										}
										
										itemWidget = itemWidget.next;
										continue;
									}
									
									// If not in list but in enumeration, add in list
									if (!isItemWidgetDataProvider && isEnumerationItem) {
										if (itemWidget != null) {
											if (renderer != null) {
												renderer.reset();
											}
											internalAddItem((DataProvider) linkedListItem, renderer, itemWidget, false);
										} else {
											addItem((DataProvider) linkedListItem);
										}
										
										if (itemsEnumeration.hasNextItems()) {
											linkedListItem = itemsEnumeration.nextItem();
										} else {
											linkedListItem = null;
										}
										
									// If already in list but not in enumeration, remove it
									} else if (isItemWidgetDataProvider && !isEnumerationItem) {
										Widget nextItemWidget = itemWidget.next;
										internalRemoveItem(itemWidget);
										itemWidget = nextItemWidget;
									}
									
									// All items are trailed in linkedList and list
									if (linkedListItem == null && itemWidget == null) {
										break;
									}
								}
								return true;
								
							}
							
							removeAllItems();
							return true;
						}
							
						case DataProvider.CLEAR_MODEL_UPDATE_EVENT_TYPE:
							removeAllItems();
							return true;
							
					}
				}
			}
		}
		return false;
	}

}
