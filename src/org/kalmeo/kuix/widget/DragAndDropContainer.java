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

/**
 * This class represents a drag and drop container. <br>
 * <br>
 * <strong>For further informations, visit the <a
 * href="http://www.kalmeo.org/files/kuix/widgetdoc/index.html"
 * target="new">Kuix widgets reference page</a></strong>.
 * 
 * @author bbeaulant
 */
public class DragAndDropContainer extends Widget {

	/**
	 * Construct a {@link DragAndDropContainer}
	 */
	public DragAndDropContainer() {
		super(KuixConstants.DRAG_AND_DROP_CONTAINER_WIDGET_TAG);
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#processPointerEvent(byte, int, int)
	 */
	public boolean processPointerEvent(byte type, int x, int y) {
		
		if (type == KuixConstants.POINTER_DRAGGED_EVENT_TYPE) {
			getDesktop().setDraggedWidget(getChild(), x, y);
			markAsValidate();
			return true;
		}
		
		if (type == KuixConstants.POINTER_DROPPED_EVENT_TYPE) {
			Widget draggedWidget = getDesktop().removeDraggedWidget(false);
			if (draggedWidget != null) {
				add(draggedWidget);
			}
			return true;
		}
		
		return false;
	}

}
