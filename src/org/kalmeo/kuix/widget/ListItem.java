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

import org.kalmeo.kuix.core.KuixConstants;
import org.kalmeo.kuix.layout.InlineLayout;
import org.kalmeo.kuix.layout.Layout;
import org.kalmeo.kuix.util.Alignment;

/**
 * This class represents a list item. <br>
 * <br>
 * <strong>For further informations, visit the <a
 * href="http://www.kalmeo.org/files/kuix/widgetdoc/index.html"
 * target="new">Kuix widgets reference page</a></strong>.
 * 
 * @author bbeaulant
 */
public class ListItem extends ActionWidget {

	// Defaults
	private static final Layout LIST_ITEM_DEFAULT_LAYOUT = new InlineLayout(true,
			Alignment.FILL);

	/**
	 * Construct a {@link ListItem}
	 */
	public ListItem() {
		this(KuixConstants.LIST_ITEM_WIDGET_TAG);
	}

	/**
	 * Construct a {@link ListItem}
	 *
	 * @param tag
	 */
	public ListItem(String tag) {
		super(tag);
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.core.focus.FocusManager#processPointerEvent(byte, int, int)
	 */
	public boolean processPointerEvent(byte type, int x, int y) {
		switch (type) {
			case KuixConstants.POINTER_PRESSED_EVENT_TYPE: {
				requestFocus();
				break;
			}
		}
		return super.processPointerEvent(type, x, y);
	}

	public void setPressed(boolean pressed) {
		super.setPressed(pressed);
		setPressedForChilds(this, pressed);
	}

	public boolean processActionEvent() {
		final boolean val = super.processActionEvent();
		processActionEventForChilds(this);
		return val;
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getDefaultStyleAttributeValue(java.lang.String)
	 */
	protected Object getDefaultStylePropertyValue(String name) {
		if (KuixConstants.LAYOUT_STYLE_PROPERTY.equals(name)) {
			return LIST_ITEM_DEFAULT_LAYOUT;
		}
		return super.getDefaultStylePropertyValue(name);
	}

	private static void processActionEventForChilds(Widget parent) {
		for (Widget widget = parent.getChild(); widget != null; widget =
						widget.next) {
			widget.processActionEvent();
			if (widget.getChild() != null) {
				processActionEventForChilds(widget);
			}
		}
	}

	private static void setPressedForChilds(Widget parent, boolean pressed) {
		for (Widget widget = parent.getChild(); widget != null; widget =
						widget.next) {
			if (widget instanceof ActionWidget) {
				((ActionWidget) widget).setPressed(pressed);
			}
			if (widget.getChild() != null) {
				setPressedForChilds(widget, pressed);
			}
		}
	}
}
