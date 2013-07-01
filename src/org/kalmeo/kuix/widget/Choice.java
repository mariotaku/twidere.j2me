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
 * Creation date : 5 juin 08
 * Copyright (c) Kalmeo 2007-2008. All rights reserved.
 * http://www.kalmeo.org
 */

package org.kalmeo.kuix.widget;

import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.core.KuixConstants;
import org.kalmeo.kuix.core.focus.FocusManager;
import org.kalmeo.kuix.layout.GridLayout;
import org.kalmeo.kuix.layout.Layout;
import org.kalmeo.kuix.util.Alignment;
import org.kalmeo.kuix.util.Gap;

/**
 * This class represents a choice. <br>
 * <br>
 * <strong>For further informations, visit the <a
 * href="http://www.kalmeo.org/files/kuix/widgetdoc/index.html"
 * target="new">Kuix widgets reference page</a></strong>.
 * 
 * @author bbeaulant
 */
public class Choice extends ActionWidget {
	
	// The internal choice container (hold the selected radio button content)
	private final Widget choiceContainer;

	// The screen that is displayed to list choices.
	private final Screen screen;
	private final RadioGroup radioGroup;

	// The screen where this choice is attached
	private Screen ownerScreen;
	private boolean ownerScreenCleanUpWhenRemoved = false;
	
	// Keep there the lastest selected radio button (for internal use)
	private RadioButton lastSelectedRadioButton = null;
	
	// Internal use
	private Widget noChoiceText;

	/**
	 * Construct a {@link Choice}
	 */
	public Choice() {
		super(KuixConstants.CHOICE_WIDGET_TAG);

		// Create the inner choice container
		choiceContainer = new Widget(KuixConstants.CHOICE_CONTAINER_WIDGET_TAG) {

			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.Widget#getAlign()
			 */
			public Alignment getAlign() {
				if (lastSelectedRadioButton != null) {
					return lastSelectedRadioButton.getAlign();
				}
				return super.getAlign();
			}

			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.Widget#getGap()
			 */
			public Gap getGap() {
				if (lastSelectedRadioButton != null) {
					return lastSelectedRadioButton.getGap();
				}
				return super.getGap();
			}

			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.Widget#getLayout()
			 */
			public Layout getLayout() {
				if (lastSelectedRadioButton != null) {
					if (getChild() == null || getChild() == noChoiceText) {
						catchChildrenFrom(lastSelectedRadioButton);
						noChoiceText = null;
					}
					return lastSelectedRadioButton.getLayout();
				} else if (getChild() ==  null) {
					// No radioButton was selected, display the no choice text
					noChoiceText = new Text().setText(Kuix.getMessage(KuixConstants.PLEASE_SELECT_I18N_KEY));
					this.add(noChoiceText);
				}
				return super.getLayout();
			}

		};
		super.add(choiceContainer);
		
		// Create the inner screen
		screen = new Screen(KuixConstants.CHOICE_SCREEN_WIDGET_TAG) {

			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.Widget#getInheritedTag()
			 */
			public String getInheritedTag() {
				return KuixConstants.SCREEN_WIDGET_TAG;
			}

			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.Screen#processMenuAction(org.kalmeo.kuix.widget.Menu, boolean, boolean)
			 */
			protected boolean processMenuAction(Menu menu, boolean internal, boolean isFirst) {
				if (isFirst) {
					FocusManager focusManager = getDesktop().getCurrentFocusManager();
					if (focusManager != null) {
						focusManager.processKeyEvent(KuixConstants.KEY_PRESSED_EVENT_TYPE, KuixConstants.KUIX_KEY_FIRE);
						return true;
					}
				}
				restoreOwnerScreen();
				return true;
			}
			
		};
		screen.switchToInternalMenus();
		screen.setTitle(Kuix.getMessage(KuixConstants.PLEASE_SELECT_I18N_KEY));
		
		// Create the inner scroll container
		ScrollPane scrollContainer = new ScrollPane();
		screen.add(scrollContainer);

		// Create the inner radio group
		radioGroup = new RadioGroup(KuixConstants.CHOICE_RADIO_GROUP_WIDGET_TAG) {

			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.Widget#getInheritedTag()
			 */
			public String getInheritedTag() {
				return KuixConstants.RADIO_GROUP_WIDGET_TAG;
			}

			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.RadioGroup#setSelectedRadioButton(org.kalmeo.kuix.widget.RadioButton, boolean)
			 */
			public void setSelectedRadioButton(RadioButton radioButton, boolean propagateChangeEvent) {
				// Check if selection has changed
				if (radioButton != lastSelectedRadioButton) {
					
					// Restore last selected button content
					if (lastSelectedRadioButton != null && lastSelectedRadioButton.getChild() == null) {
						lastSelectedRadioButton.catchChildrenFrom(choiceContainer);
					}
					
					lastSelectedRadioButton = radioButton;
				}
				
				// Invalidate the choice container
				choiceContainer.invalidate();
				
				// Restore the owner screen if not current
				restoreOwnerScreen();
				
				super.setSelectedRadioButton(radioButton, propagateChangeEvent);
			}

		};
		scrollContainer.add(radioGroup);
		
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.ActionWidget#setAttribute(java.lang.String, java.lang.String)
	 */
	public boolean setAttribute(String name, String value) {
		if (KuixConstants.TITLE_ATTRIBUTE.equals(name)) {
			setTitle(value);
			return true;
		}
		return super.setAttribute(name, value);
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getInternalChildInstance(java.lang.String)
	 */
	public Widget getInternalChildInstance(String tag) {
		if (KuixConstants.CHOICE_CONTAINER_WIDGET_TAG.equals(tag)) {
			return getChoiceContainer();
		}
		if (KuixConstants.CHOICE_SCREEN_WIDGET_TAG.equals(tag)) {
			return getScreen();
		}
		if (KuixConstants.CHOICE_RADIO_GROUP_WIDGET_TAG.equals(tag)) {
			return getRadioGroup();
		}
		return super.getInternalChildInstance(tag);
	}
	
	/**
	 * Define the sub screen title.
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		screen.setTitle(title);
	}
	
	/**
	 * @return the choiceContainer
	 */
	public Widget getChoiceContainer() {
		return choiceContainer;
	}

	/**
	 * @return the screen
	 */
	public Screen getScreen() {
		return screen;
	}

	/**
	 * @return the radioGroup
	 */
	public RadioGroup getRadioGroup() {
		return radioGroup;
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getLayout()
	 */
	public Layout getLayout() {
		return GridLayout.instanceOneByOne;
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getWidget(java.lang.String)
	 */
	public Widget getWidget(String id) {
		Widget widget = screen.getWidget(id);
		if (widget == null) {
			return super.getWidget(id);
		}
		return widget;
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#add(org.kalmeo.kuix.widget.Widget)
	 */
	public Widget add(Widget widget) {
		return this;
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#cleanUp()
	 */
	public void cleanUp() {
		super.cleanUp();
		screen.cleanUp();
	}
	
	/**
	 * Restore the owner screen.
	 */
	private void restoreOwnerScreen() {
		if (ownerScreen != null) {
			
			ownerScreen.setCurrent();
			
			// Restore the cleanUpWhenRemoved property
			ownerScreen.cleanUpWhenRemoved = ownerScreenCleanUpWhenRemoved;
			ownerScreen = null;
			
		}
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.AbstractActionWidget#processActionEvent()
	 */
	public boolean processActionEvent() {
		Desktop desktop = Kuix.getCanvas().getDesktop();
		if (desktop != null) {
			
			if (lastSelectedRadioButton != null) {
				lastSelectedRadioButton.catchChildrenFrom(choiceContainer);
			}
			
			// Retrieve the owner screen instance
			ownerScreen = desktop.getCurrentScreen();
			
			// Keep the cleanUpWhenRemoved property value
			ownerScreenCleanUpWhenRemoved = ownerScreen.cleanUpWhenRemoved;
			ownerScreen.cleanUpWhenRemoved = false;
			
			desktop.setCurrentScreen(screen);
			
		}
		return super.processActionEvent();
	}

}
