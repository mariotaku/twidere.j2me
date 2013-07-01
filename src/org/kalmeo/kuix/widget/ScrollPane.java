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
 * Creation date : 22 nov. 07
 * Copyright (c) Kalmeo 2007-2008. All rights reserved.
 * http://www.kalmeo.org
 */

package org.kalmeo.kuix.widget;

import javax.microedition.lcdui.Graphics;

import org.kalmeo.kuix.core.KuixConstants;
import org.kalmeo.kuix.layout.BorderLayout;
import org.kalmeo.kuix.layout.BorderLayoutData;
import org.kalmeo.kuix.layout.InlineLayout;
import org.kalmeo.kuix.layout.Layout;
import org.kalmeo.kuix.util.Alignment;
import org.kalmeo.kuix.util.Insets;
import org.kalmeo.util.BooleanUtil;
import org.kalmeo.util.MathFP;

/**
 * This class represents a scroll pane. <br>
 * <br>
 * <strong>For further informations, visit the <a
 * href="http://www.kalmeo.org/files/kuix/widgetdoc/index.html"
 * target="new">Kuix widgets reference page</a></strong>.
 * 
 * @author bbeaulant
 */
public class ScrollPane extends Widget {

	// Defaults
	private static final int MAX_INCREMENT_DIVIDER = 4;

	// Inner container widget
	protected final Widget container;

	// ScrollBar widget
	private final ScrollBar scrollBar;
	
	// Markers
	private boolean useMarkers = false;
	private FocusableWidget firstMarker = null;
	private FocusableWidget lastMarker = null;

	// Attributes
	private boolean horizontal;
	private boolean showScrollBar;
	private boolean autoScroll = false;
	private boolean needToAutoScroll = false;
	
	// offsets
	private int xOffset = 0;
	private int yOffset = 0;

	// Internal use
	private int innerWidth;
	private int innerHeight;
	private int contentWidth;
	private int contentHeight;
	private int maxIncrement;
	private int pressedX = 0;
	private int pressedY = 0;
	private int pressedXOffset = 0;
	private int pressedYOffset = 0;
	
	/**
	 * Construct a {@link ScrollPane}
	 */
	public ScrollPane() {
		this(KuixConstants.SCROLL_PANE_WIDGET_TAG);
	}
	
	/**
	 * Construct a {@link ScrollPane}
	 *
	 * @param tag
	 */
	public ScrollPane(String tag) {
		this(tag, true);
	}
	
	/**
	 * Construct a {@link ScrollPane}
	 *
	 * @param tag
	 * @param useMarkers
	 */
	public ScrollPane(String tag, boolean useMarkers) {
		super(tag);
		container = new Widget(KuixConstants.SCROLL_PANE_CONTAINER_WIDGET_TAG) {
			
			private final InlineLayout layout = new InlineLayout(false, Alignment.FILL);
			
			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.Widget#getDisplayX()
			 */
			public int getDisplayX() {
				return super.getDisplayX() - xOffset;
			}
			
			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.Widget#getDisplayY()
			 */
			public int getDisplayY() {
				return super.getDisplayY() - yOffset;
			}

			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.Widget#getLayout()
			 */
			public Layout getLayout() {
				return layout;
			}

			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.Widget#getAlign()
			 */
			public Alignment getAlign() {
				return horizontal ? Alignment.FILL_LEFT : Alignment.FILL_TOP;
			}
			
			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.Widget#getWidgetAt(int, int)
			 */
			public Widget getWidgetAt(int mx, int my) {
				return getWidgetAt(mx + xOffset, my + yOffset, getX(), getY(), contentWidth, contentHeight);
			}
			
			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.Widget#removeAll()
			 */
			public void removeAll() {
				if (getChild() != null) {
					for (Widget widget = getChild().next; widget != null && widget != getLastChild(); widget = widget.next) {
						widget.parent = null;
					}
				}
			}

			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.Widget#doLayout()
			 */
			protected void doLayout() {
				super.doLayout();
				Insets insets = getInsets();
				if (horizontal) {
					innerWidth = container.getInnerWidth();
					contentWidth = container.getPreferredSize(getWidth()).width - insets.left - insets.right;
					contentHeight = container.getInnerHeight();
					maxIncrement = innerWidth / MAX_INCREMENT_DIVIDER;
					if (autoScroll && needToAutoScroll) {
						setXOffset(contentWidth);
						needToAutoScroll = false;
					} else if (xOffset > contentWidth - innerWidth) {
						setXOffset(contentWidth - innerWidth); // Adjust xOffset if contentWidth has changed
					}
				} else {
					innerHeight = container.getInnerHeight();
					contentWidth = container.getInnerWidth();
					contentHeight = container.getPreferredSize(getWidth()).height - insets.top - insets.bottom;
					maxIncrement = innerHeight / MAX_INCREMENT_DIVIDER;
					if (autoScroll && needToAutoScroll) {
						setYOffset(contentHeight);
						needToAutoScroll = false;
					} else if (yOffset > contentHeight - innerHeight) {
						setYOffset(contentHeight - innerHeight); // Adjust yOffset if contentHeight has changed
					}
				}
				updateScrollBarValues();
			}
			
			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.Widget#invalidateAppearanceRegion(int, int, int, int)
			 */
			protected void invalidateAppearanceRegion(int x, int y, int width, int height) {
				super.invalidateAppearanceRegion(x - xOffset, y - yOffset, width, height);
			}

			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.Widget#paintChildrenImpl(javax.microedition.lcdui.Graphics)
			 */
			protected void paintChildrenImpl(Graphics g) {
				g.translate(-xOffset, -yOffset);
				super.paintChildrenImpl(g);
				g.translate(xOffset, yOffset);
			}

		};
		super.add(container);
		
		// Add markers
		this.useMarkers = useMarkers;
		if (useMarkers) {
			firstMarker = new FocusableWidget();
			lastMarker = new FocusableWidget();
			container.add(firstMarker);
			container.add(lastMarker);
		}
		
		scrollBar = new ScrollBar(KuixConstants.SCROLL_PANE_SCROLL_BAR_WIDGET_TAG) {

			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.Widget#getInheritedTag()
			 */
			public String getInheritedTag() {
				return KuixConstants.SCROLL_BAR_WIDGET_TAG;
			}

			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.Widget#getDefaultStylePropertyValue(java.lang.String)
			 */
			protected Object getDefaultStylePropertyValue(String name) {
				if (KuixConstants.LAYOUT_DATA_STYLE_PROPERTY.equals(name)) {
					if (horizontal) {
						return BorderLayoutData.instanceSouth;
					} else {
						return BorderLayoutData.instanceEast;
					}
				}
				return super.getDefaultStylePropertyValue(name);
			}
			
			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.ScrollBar#processChangeEvent()
			 */
			protected void processChangeEvent() {
				if (horizontal) {
					setXOffset(MathFP.mul(getValue(), contentWidth - innerWidth));
				} else {
					setYOffset(MathFP.mul(getValue(), contentHeight - innerHeight));
				}
			}
			
		};
		setShowScrollBar(true);
		setHorizontal(false);
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#setAttribute(java.lang.String, java.lang.String)
	 */
	public boolean setAttribute(String name, String value) {
		if (KuixConstants.HORIZONTAL_ATTRIBUTE.equals(name)) {
			setHorizontal(BooleanUtil.parseBoolean(value));
			return true;
		}
		if (KuixConstants.SHOW_SCROLL_BAR_ATTRIBUTE.equals(name)) {
			setShowScrollBar(BooleanUtil.parseBoolean(value));
			return true;
		}
		if (KuixConstants.AUTO_SCROLL_ATTRIBUTE.equals(name)) {
			setAutoScroll(BooleanUtil.parseBoolean(value));
			return true;
		}
		return super.setAttribute(name, value);
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getInternalChildInstance(java.lang.String)
	 */
	public Widget getInternalChildInstance(String tag) {
		if (KuixConstants.SCROLL_PANE_CONTAINER_WIDGET_TAG.equals(tag)) {
			return getContainer();
		}
		if (KuixConstants.SCROLL_PANE_SCROLL_BAR_WIDGET_TAG.equals(tag)) {
			return getScrollBar();
		}
		return super.getInternalChildInstance(tag);
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getLayout()
	 */
	public Layout getLayout() {
		return BorderLayout.instance;
	}
	
	/**
	 * @return the horizontal
	 */
	public boolean isHorizontal() {
		return horizontal;
	}

	/**
	 * @param horizontal the horizontal to set
	 */
	public void setHorizontal(boolean horizontal) {
		this.horizontal = horizontal;
		xOffset = 0;
		yOffset = 0;
		scrollBar.setHorizontal(horizontal);
		((InlineLayout) container.getLayout()).horizontal = horizontal;
		container.invalidate();
	}

	/**
	 * @return the showScrollBar
	 */
	public boolean isShowScrollBar() {
		return showScrollBar;
	}
	
	/**
	 * @param showScrollBar the showScrollBar to set
	 */
	public void setShowScrollBar(boolean showScrollBar) {
		this.showScrollBar = showScrollBar;
		updateScrollBarVisibility();
	}
	
	/**
	 * @return the autoScroll
	 */
	public boolean isAutoScroll() {
		return autoScroll;
	}

	/**
	 * The autoScroll attribute force the scroll position to the max value each
	 * time the {@link ScrollPane} content change or is invalidated.
	 * 
	 * @param autoScroll the autoScroll to set
	 */
	public void setAutoScroll(boolean autoScroll) {
		this.autoScroll = autoScroll;
	}

	/**
	 * Check if <code>widget</code> is one of the ScrollPane markers.
	 * 
	 * @param widget
	 * @return <code>true</code> if the given widget is one of the ScrollPane
	 *         markers.
	 */
	public boolean isMarkerWidget(Widget widget) {
		return widget != null && (widget == firstMarker || widget == lastMarker);
	}
	
	/**
	 * @return the container
	 */
	protected Widget getContainer() {
		return container;
	}

	/**
	 * Return the {@link ScrollBar} instance.
	 * 
	 * @return the scrollBar
	 */
	public ScrollBar getScrollBar() {
		return scrollBar;
	}

	/**
	 * @param xOffset the xOffset to set
	 * @return <code>true</code> if the xOffset value has changed
	 */
	private boolean setXOffset(int xOffset) {
		if (contentWidth <= innerWidth) {
			this.xOffset = 0;
			return true;
		}
		int lastXOffset = this.xOffset;
		this.xOffset = Math.max(0, Math.min(contentWidth - innerWidth, xOffset));
		return lastXOffset != this.xOffset;
	}

	/**
	 * @param yOffset the yOffset to set
	 * @return <code>true</code> if the yOffset value has changed
	 */
	private boolean setYOffset(int yOffset) {
		if (contentHeight <= innerHeight) {
			this.yOffset = 0;
			return true;
		}
		int lastYOffset = this.yOffset;
		this.yOffset = Math.max(0, Math.min(contentHeight - innerHeight, yOffset));
		return lastYOffset != this.yOffset;
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#invalidate(org.kalmeo.kuix.widget.Widget)
	 */
	public void invalidate(Widget fromWidget) {
		super.invalidate(fromWidget);
		if (fromWidget != this && fromWidget != scrollBar) {
			needToAutoScroll = true;
		}
	}
	
	/**
	 * Check if the given <code>child</code> widget has a part of its area in
	 * the {@link ScrollPane} clipped area.
	 * 
	 * @param child
	 * @return <code>true</code> if the <code>child</code> widget has a part of
	 *         its area in the {@link ScrollPane} clipped area.
	 */
	public boolean isChildInsideClippedArea(Widget child) {
		if (child == null) {
			return false;
		}
		Widget container;
		if (horizontal) {
			int childX = child.getX() - this.container.getInsets().left;
			for (container = child.parent; container != null && container != this.container; container = container.parent) {
				childX += container.getX();
			}
			if (container == null) {
				// This is not a child of this ScrollContainer
				return false;
			}
			if (childX < xOffset) {
				return childX + child.getWidth() > xOffset;
			} else if (childX > xOffset + contentWidth) {
				return false;
			}
			return true;
		} else {
			int childY = child.getY() - this.container.getInsets().top;
			for (container = child.parent; container != null && container != this.container; container = container.parent) {
				childY += container.getY();
			}
			if (container == null) {
				// This is not a child of this ScrollContainer
				return false;
			}
			if (childY < yOffset) {
				return childY + child.getHeight() > yOffset;
			} else if (childY > yOffset + contentHeight) {
				return false;
			}
			return true;
		}
	}

	/**
	 * Arrange the scroll offset according to the child position
	 * 
	 * @param child
	 * @param useIncrementLimit
	 * @return <code>true</code> the child is displayable after scroll, else <code>false</code>
	 */
	public boolean bestScrollToChild(Widget child, boolean useIncrementLimit) {
		if (child == null) {
			return false;
		}
		boolean widgetIsVisible = true;
		if (horizontal) {
			useIncrementLimit &= innerWidth > 0;
			int childX = child.getX() - this.container.getInsets().left;
			Widget container;
			for (container = child.parent; container != null && container != this.container; container = container.parent) {
				childX += container.getX();
			}
			if (container == null) {
				// This is not a child of this ScrollContainer
				return false;
			}
			if (childX < xOffset) {
				int increment = xOffset - childX;
				if (useIncrementLimit && increment > maxIncrement) {
					setXOffset(xOffset - maxIncrement);
					widgetIsVisible = false;
				} else {
					setXOffset(childX);
				}
			} else if (xOffset + innerWidth < childX + child.getWidth()) {
				int increment = childX + child.getWidth() - (xOffset + innerWidth);
				if (useIncrementLimit && increment > maxIncrement) {
					setXOffset(xOffset + maxIncrement);
					widgetIsVisible = false;
				} else {
					setXOffset(childX + child.getWidth() - innerWidth);
				}
			}
		} else {
			useIncrementLimit &= innerHeight > 0;
			int childY = child.getY() - this.container.getInsets().top;
			Widget container;
			for (container = child.parent; container != null && container != this.container; container = container.parent) {
				childY += container.getY();
			}
			if (container == null) {
				// This is not a child of this ScrollContainer
				return true;
			}
			if (childY < yOffset) {
				int increment = yOffset - childY;
				if (useIncrementLimit && increment > maxIncrement) {
					setYOffset(yOffset - maxIncrement);
					widgetIsVisible = false;
				} else {
					setYOffset(childY);
				}
			} else if (yOffset + innerHeight < childY + child.getHeight()) {
				int increment = childY + child.getHeight() - (yOffset + innerHeight);
				if (useIncrementLimit && increment > maxIncrement) {
					setYOffset(yOffset + maxIncrement);
					widgetIsVisible = false;
				} else {
					setYOffset(childY + child.getHeight() - innerHeight);
				}
			}
		}
		updateScrollBarValues();
		return widgetIsVisible;
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#add(org.kalmeo.kuix.widget.Widget)
	 */
	public Widget add(Widget widget) {
		container.add(widget, container.getLastChild(), !useMarkers);
		return this;
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#removeAll()
	 */
	public void removeAll() {
		container.removeAll();
	}

	/**
	 * Apply scroll values to the scrollBar
	 */
	private void updateScrollBarValues() {
		if (horizontal) {
			if (contentWidth != 0) {
				scrollBar.setValue(MathFP.div(xOffset, contentWidth - innerWidth));
				scrollBar.setSelection(MathFP.div(innerWidth, contentWidth));
			}
		} else {
			if (contentHeight != 0) {
				scrollBar.setValue(MathFP.div(yOffset, contentHeight - innerHeight));
				scrollBar.setSelection(MathFP.div(innerHeight, contentHeight));
			}
		}
	}
	
	/**
	 * Update the scrollBar visibility according to its selection size.
	 */
	private void updateScrollBarVisibility() {
		if (showScrollBar) {
			if (scrollBar.parent != this) {
				super.add(scrollBar);
			}
		} else {
			if (scrollBar.parent == this) {
				scrollBar.remove();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#processPointerEvent(byte, int, int)
	 */
	public boolean processPointerEvent(byte type, int x, int y) {
		switch (type) {
			
			case KuixConstants.POINTER_PRESSED_EVENT_TYPE: {
				pressedX = x;
				pressedY = y;
				pressedXOffset = xOffset;
				pressedYOffset = yOffset;
				return true;
			}
			
			case KuixConstants.POINTER_DRAGGED_EVENT_TYPE: {
				if (horizontal) {
					if (setXOffset(pressedXOffset - (x - pressedX) * KuixConstants.SCROLL_BOOSTER_FACTOR)) {
						updateScrollBarValues();
						return true;
					}
				} else {
					if (setYOffset(pressedYOffset - (y - pressedY) * KuixConstants.SCROLL_BOOSTER_FACTOR)) {
						updateScrollBarValues();
						return true;
					}
				}
				return false;
			}
			
		}
		return super.processPointerEvent(type, x, y);
	}

}
