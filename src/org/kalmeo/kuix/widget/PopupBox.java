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
 * Creation date : 28 nov. 2008
 * Copyright (c) Kalmeo 2007-2008. All rights reserved.
 * http://www.kalmeo.org
 */

package org.kalmeo.kuix.widget;

import javax.microedition.lcdui.Image;

import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.core.KuixConstants;
import org.kalmeo.kuix.core.focus.FocusManager;
import org.kalmeo.kuix.layout.BorderLayout;
import org.kalmeo.kuix.layout.BorderLayoutData;
import org.kalmeo.kuix.layout.GridLayout;
import org.kalmeo.kuix.layout.Layout;
import org.kalmeo.kuix.layout.LayoutData;
import org.kalmeo.kuix.layout.StaticLayout;
import org.kalmeo.kuix.layout.StaticLayoutData;
import org.kalmeo.kuix.util.Alignment;
import org.kalmeo.kuix.util.Color;
import org.kalmeo.kuix.util.Gap;
import org.kalmeo.kuix.util.Insets;
import org.kalmeo.kuix.widget.Screen.ScreenMenu;
import org.kalmeo.util.BooleanUtil;
import org.kalmeo.util.MathFP;
import org.kalmeo.util.worker.Worker;
import org.kalmeo.util.worker.WorkerTask;

/**
 * This class represents a Kuix popup box. <br>
 * <br>
 * <strong>For further informations, visit the <a
 * href="http://www.kalmeo.org/files/kuix/widgetdoc/index.html"
 * target="new">Kuix widgets reference page</a></strong>.
 * 
 * @author bbeaulant
 */
public class PopupBox extends ActionWidget {
	
	/**
	 * This class represents a popupBox menuItem
	 */
	public class PopupBoxMenuItem extends MenuItem {

		private LayoutData layoutData;
		
		private boolean first;
		
		/**
		 * Construct a {@link ScreenMenu}
		 *
		 * @param tag
		 * @param layoutData
		 * @param internal
		 */
		public PopupBoxMenuItem(String tag, boolean first) {
			super(tag);
			this.first = first;
		}
		
		/* (non-Javadoc)
		 * @see org.kalmeo.kuix.widget.Widget#getInheritedTag()
		 */
		public String getInheritedTag() {
			// By default the popup menu items inhérit styles from screen menus.
			if (first) {
				return KuixConstants.SCREEN_FIRST_MENU_WIDGET_TAG;
			} else {
				return KuixConstants.SCREEN_SECOND_MENU_WIDGET_TAG;
			}
		}

		/* (non-Javadoc)
		 * @see org.kalmeo.kuix.widget.Widget#getLayoutData()
		 */
		public LayoutData getLayoutData() {
			if (layoutData == null) {
				Alignment alignment = Kuix.firstIsLeft && first || !Kuix.firstIsLeft && !first ? Alignment.LEFT : Alignment.RIGHT;
				LayoutData superLayoutData = super.getLayoutData();
				if (superLayoutData instanceof StaticLayoutData) {
					StaticLayoutData staticLayoutData = (StaticLayoutData) superLayoutData;
					layoutData = new StaticLayoutData(Alignment.combine(staticLayoutData.alignment, alignment), staticLayoutData.width, staticLayoutData.height);
				} else {
					layoutData = new StaticLayoutData(alignment);
				}
			}
			return layoutData;
		}
		
		/* (non-Javadoc)
		 * @see org.kalmeo.kuix.widget.Widget#clearCachedStyle(boolean)
		 */
		public void clearCachedStyle(boolean propagateToChildren) {
			layoutData = null;
			super.clearCachedStyle(propagateToChildren);
		}

		/* (non-Javadoc)
		 * @see org.kalmeo.kuix.widget.AbstractFocusableWidget#isFocusable()
		 */
		public boolean isFocusable() {
			return false;
		}

		/* (non-Javadoc)
		 * @see org.kalmeo.kuix.widget.MenuItem#processActionEvent()
		 */
		public boolean processActionEvent() {
			boolean superProcess = super.processActionEvent();
			PopupBox.this.remove();
			return superProcess;
		}
		
	}
	
	// Defaults
	private static final LayoutData DEFAULT_CONTENT_CONTAINER_LAYOUT_DATA = new StaticLayoutData(Alignment.CENTER);

	// The duration of presence of this popup (in ms)
	private int duration = -1;
	
	// FocusManager
	private final FocusManager focusManager;

	// The content's widgets
	private final Widget container;
	private final Widget contentContainer;
	private Widget bottomBar;

	// Used to determine if this screen call its cleanUp method when removed from its parent
	public boolean cleanUpWhenRemoved = false;
	
	// Used to determine if topBar and bottomBar are displayed on top of the screen content
	public boolean barsOnTop = false;
	
	// MenuItems
	private PopupBoxMenuItem firstMenuItem;
	private PopupBoxMenuItem secondMenuItem;
	
	/**
	 * Construct a {@link PopupBox}
	 */
	public PopupBox() {
		super(KuixConstants.POPUP_BOX_WIDGET_TAG);
		
		container = new Widget() {

			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.Widget#getLayout()
			 */
			public Layout getLayout() {
				return StaticLayout.instance;
			}

			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.Widget#getLayoutData()
			 */
			public LayoutData getLayoutData() {
				if (barsOnTop) {
					return StaticLayoutData.instanceFull;
				}
				return BorderLayoutData.instanceCenter;
			}
			
		};
		super.add(container);
		
		contentContainer = new Widget() {
			
			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.Widget#getStylePropertyValue(java.lang.String, boolean)
			 */
			protected Object getStylePropertyValue(String name, boolean inherited) {
				return PopupBox.this.getStylePropertyValue(name, inherited);
			}

		};
		container.add(contentContainer);
		
		// Init focusManagers
		focusManager = new FocusManager(this, false) {
			
			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.core.focus.FocusManager#processKeyEvent(byte, int)
			 */
			public boolean processKeyEvent(byte type, int kuixKeyCode) {
				if (!super.processKeyEvent(type, kuixKeyCode)) {
					
					// Default event process
					switch (type) {
						
						case KuixConstants.KEY_PRESSED_EVENT_TYPE:
						case KuixConstants.KEY_REPEATED_EVENT_TYPE: {
							
							if (kuixKeyCode == KuixConstants.KUIX_KEY_SOFT_LEFT || kuixKeyCode == KuixConstants.KUIX_KEY_SOFT_RIGHT) {
								MenuItem menuItem = getPopupBoxMenuItem(kuixKeyCode);
								if (menuItem != null) {
									menuItem.processActionEvent();
								}
								return true;
							}
							break;
							
						}

					}
					
				}
				return true;	
			}

		};
		
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getInternalChildInstance(java.lang.String)
	 */
	public Widget getInternalChildInstance(String tag) {
		if (KuixConstants.POPUP_BOX_BOTTOM_BAR_WIDGET_TAG.equals(tag)) {
			return getBottomBar();
		}
		if (KuixConstants.POPUP_BOX_FIRST_MENU_ITEM_WIDGET_TAG.equals(tag)) {
			return getFirstMenuItem();
		}
		if (KuixConstants.POPUP_BOX_SECOND_MENU_ITEM_WIDGET_TAG.equals(tag)) {
			return getSecondMenuItem();
		}
		return super.getInternalChildInstance(tag);
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#setAttribute(java.lang.String, java.lang.String)
	 */
	public boolean setAttribute(String name, String value) {
		if (KuixConstants.FOCUS_LOOP_ATTRIBUTE.equals(name)) {
			focusManager.setLoop(BooleanUtil.parseBoolean(value));
			return true;
		}
		if (KuixConstants.CLEAN_UP_WHEN_REMOVED_ATTRIBUTE.equals(name)) {
			setCleanUpWhenRemoved(BooleanUtil.parseBoolean(value));
			return true;
		}
		if (KuixConstants.BARS_ON_TOP_ATTRIBUTE.equals(name)) {
			setBarsOnTop(BooleanUtil.parseBoolean(value));
			return true;
		}
		return super.setAttribute(name, value);
	}
	
	/**
	 * @return the cleanUpWhenRemoved
	 */
	public boolean isCleanUpWhenRemoved() {
		return cleanUpWhenRemoved;
	}

	/**
	 * @param cleanUpWhenRemoved the cleanUpWhenRemoved to set
	 */
	public void setCleanUpWhenRemoved(boolean cleanUpWhenRemoved) {
		this.cleanUpWhenRemoved = cleanUpWhenRemoved;
	}

	/**
	 * @return the barsOnTop
	 */
	public boolean isBarsOnTop() {
		return barsOnTop;
	}

	/**
	 * @param barsOnTop the barsOnTop to set
	 */
	public void setBarsOnTop(boolean barsOnTop) {
		this.barsOnTop = barsOnTop;
		invalidate();
	}

	/**
	 * Create the bottomBar instance if it doesn't exist and return it.
	 * 
	 * @return the bottomBar instance
	 */
	public Widget getBottomBar() {
		if (bottomBar == null) {
			bottomBar = new Widget(KuixConstants.POPUP_BOX_BOTTOM_BAR_WIDGET_TAG) {
				
				private StaticLayoutData staticLayoutData;
				
				/* (non-Javadoc)
				 * @see org.kalmeo.kuix.widget.Widget#getInheritedTag()
				 */
				public String getInheritedTag() {
					// By default the popup bottom bar inhérit styles from screen bottom bar
					return KuixConstants.SCREEN_BOTTOM_BAR_WIDGET_TAG;
				}

				/* (non-Javadoc)
				 * @see org.kalmeo.kuix.widget.Widget#getLayout()
				 */
				public Layout getLayout() {
					return StaticLayout.instance;
				}
	
				/* (non-Javadoc)
				 * @see org.kalmeo.kuix.widget.Widget#getLayoutData()
				 */
				public LayoutData getLayoutData() {
					if (barsOnTop) {
						if (staticLayoutData == null) {
							staticLayoutData = new StaticLayoutData(Alignment.BOTTOM_LEFT, MathFP.ONE, -1);
						}
						return staticLayoutData;
					} else {
						return BorderLayoutData.instanceSouth;
					}
				}
				
			};
			super.add(bottomBar);
		}
		return bottomBar;
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getFocusManager()
	 */
	public FocusManager getFocusManager() {
		return focusManager;
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.AbstractFocusableWidget#isFocusable()
	 */
	public boolean isFocusable() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getDefaultStylePropertyValue(java.lang.String)
	 */
	protected Object getDefaultStylePropertyValue(String name) {
		if (KuixConstants.LAYOUT_STYLE_PROPERTY.equals(name)) {
			return GridLayout.instanceOneByOne;
		} else if (KuixConstants.LAYOUT_DATA_STYLE_PROPERTY.equals(name)) {
			return DEFAULT_CONTENT_CONTAINER_LAYOUT_DATA;
		}
		return super.getDefaultStylePropertyValue(name);
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getMargin()
	 */
	public Insets getMargin() {
		return Widget.DEFAULT_MARGIN;
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getBorder()
	 */
	public Insets getBorder() {
		return Widget.DEFAULT_BORDER;
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getPadding()
	 */
	public Insets getPadding() {
		return Widget.DEFAULT_PADDING;
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getAlign()
	 */
	public Alignment getAlign() {
		return Widget.DEFAULT_ALIGN;
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getGap()
	 */
	public Gap getGap() {
		return Widget.DEFAULT_GAP;
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getLayout()
	 */
	public Layout getLayout() {
		if (barsOnTop) {
			return StaticLayout.instance;
		}
		return BorderLayout.instance;
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getLayoutData()
	 */
	public LayoutData getLayoutData() {
		return StaticLayoutData.instanceFull;
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getBackgroundColor()
	 */
	public Color getBackgroundColor() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getBackgroundImage()
	 */
	public Image[] getBackgroundImage() {
		return null;
	}
	
	/**
	 * @param duration the duration to set
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}

	/**
	 * Define the content of the {@link PopupBox}. The content object could be s
	 * straing or a {@link Widget}.
	 * 
	 * @param content the string or widget to add as content
	 */
	public void setContent(Object content) {
		contentContainer.removeAll();
		if (content instanceof String) {
			contentContainer.add(new TextArea().setText((String) content));
		} else if (content instanceof Widget) {
			contentContainer.add((Widget) content);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#doLayout()
	 */
	protected void doLayout() {
		super.doLayout();
		
		// Check if current focused widget is visible (special for popupBox because it has its own focusManager)
		Widget focusedWidget = focusManager.getFocusedWidget();
		if (focusedWidget == null || focusedWidget != null && !focusedWidget.isVisible()) {
			focusManager.requestFirstFocus();
		}
		
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#add(org.kalmeo.kuix.widget.Widget)
	 */
	public Widget add(Widget widget) {
		return contentContainer.add(widget);
	}
	
	/**
	 * Returns the {@link ScreenMenu} that correspond to the given
	 * <code>kuixKeyCode</code>.
	 * 
	 * @param kuixKeyCode
	 * @return the {@link ScreenMenu} that correspond to the given
	 *         <code>kuixKeyCode</code>
	 */
	public MenuItem getPopupBoxMenuItem(int kuixKeyCode) {
		if (Kuix.firstIsLeft && kuixKeyCode == KuixConstants.KUIX_KEY_SOFT_LEFT || !Kuix.firstIsLeft && kuixKeyCode == KuixConstants.KUIX_KEY_SOFT_RIGHT) {
			if (firstMenuItem != null && firstMenuItem.isVisible()) {
				return firstMenuItem;
			}
		} else {
			if (secondMenuItem != null && secondMenuItem.isVisible()) {
				return secondMenuItem;
			}
		}
		return null;
	}
	
	/**
	 * Create the firstMenuItem instance if it doesn't exist and return it.
	 * 
	 * @return the firstMenuItem instance
	 */
	public MenuItem getFirstMenuItem() {
		if (firstMenuItem == null) {
			firstMenuItem = new PopupBoxMenuItem(KuixConstants.POPUP_BOX_FIRST_MENU_ITEM_WIDGET_TAG, true);
			getBottomBar().add(firstMenuItem);
		}
		return firstMenuItem;
	}
	
	/**
	 * Create the secondMenuItem instance if it doesn't exist and return it.
	 * 
	 * @return the secondMenuItem instance
	 */
	public MenuItem getSecondMenuItem() {
		if (secondMenuItem == null) {
			secondMenuItem = new PopupBoxMenuItem(KuixConstants.POPUP_BOX_SECOND_MENU_ITEM_WIDGET_TAG, false);
			getBottomBar().add(secondMenuItem);
		}
		return secondMenuItem;
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#onAdded(org.kalmeo.kuix.widget.Widget)
	 */
	protected void onAdded(Widget parent) {
		if (duration != -1) {
			Worker.instance.pushTask(new WorkerTask() {
				
				private long startTime = System.currentTimeMillis();
				
				/* (non-Javadoc)
				 * @see org.kalmeo.kuix.core.worker.WorkerTask#execute()
				 */
				public boolean run() {
					if ((System.currentTimeMillis() - startTime) > duration) {
						remove();
						return true;
					}
					return false;
				}
				
			});
		}
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#onRemoved(org.kalmeo.kuix.widget.Widget)
	 */
	protected void onRemoved(Widget parent) {
		if (cleanUpWhenRemoved) {
			cleanUp();
		}
		processActionEvent();
		Kuix.getCanvas().repaintNextFrame();
	}
	
}
