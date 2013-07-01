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

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.TextBox;

import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.core.KuixConstants;
import org.kalmeo.kuix.util.Alignment;
import org.kalmeo.kuix.util.Color;
import org.kalmeo.kuix.util.Metrics;
import org.kalmeo.util.BooleanUtil;
import org.kalmeo.util.StringTokenizer;
import org.kalmeo.util.worker.Worker;
import org.kalmeo.util.worker.WorkerTask;

/**
 * This class represents a textfield. <br>
 * <br>
 * <strong>For further informations, visit the <a
 * href="http://www.kalmeo.org/files/kuix/widgetdoc/index.html"
 * target="new">Kuix widgets reference page</a></strong>.
 * 
 * @author bbeaulant
 */
public class TextField extends Text implements CommandListener {

	// Allowed constraints
	public static final String ANY = "any";
	public static final String EMAILADDR = "emailaddr";
	public static final String NUMERIC = "numeric";
	public static final String PHONENUMBER = "phonenumber";
	public static final String DECIMAL = "decimal";
	public static final String URL = "url";
	
	public static final String PASSWORD = "password";
	
	public static final String SENSITIVE = "sensitive";
	public static final String NON_PREDICTIVE = "non_predictive";
	public static final String INITIAL_CAPS_WORD = "initial_caps_word";
	public static final String INITIAL_CAPS_SENTENCE = "initial_caps_sentence";
	
	// TextBox command
	private final Command validateCommand;
	private final Command cancelCommand;
	
	// The associated textBox
	private TextBox textBox;
	
	// TextBox's title
	private String title = null;
	
	// TextBox's maxSize
	private int maxSize = 1000;

	// TextBox's constraints
	private int constraints = javax.microedition.lcdui.TextField.ANY;
	
	// Define if the edit dialog is opened when a key is hit
	private boolean editOnAllKeys = true;

	// Tooltip
	private long tooltipTimer;
	private WorkerTask tooltipTask;
	private Text tooltipText;
	private boolean tooltipVisible;

	// The change method
	private String onChange;
	
	// Internal 
	private String displayedText = null;

	/**
	 * Construct a {@link TextField}
	 */
	public TextField() {
		super(KuixConstants.TEXT_FIELD_WIDGET_TAG);
		
		// Create command to be sure that labels have the correct value
		validateCommand = new Command(Kuix.getMessage(KuixConstants.VALIDATE_I18N_KEY), Command.OK, 1);
		cancelCommand = new Command(Kuix.getMessage(KuixConstants.CANCEL_I18N_KEY), Command.BACK, 0);
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Text#setAttribute(java.lang.String, java.lang.String)
	 */
	public boolean setAttribute(String name, String value) {
		if (KuixConstants.TITLE_ATTRIBUTE.equals(name)) {
			setTitle(value);
			return true;
		}
		if (KuixConstants.MAX_SIZE_ATTRIBUTE.equals(name)) {
			setMaxSize(Integer.parseInt(value));
			return true;
		}
		if (KuixConstants.CONSTRAINTS_ATTRIBUTE.equals(name)) {
			constraints = javax.microedition.lcdui.TextField.ANY;
			StringTokenizer st = new StringTokenizer(value, ", ");
			while (st.hasMoreTokens()) {
				String constraint = st.nextToken();
				if (EMAILADDR.equals(constraint)) {
					constraints |= javax.microedition.lcdui.TextField.EMAILADDR;
				} else if (NUMERIC.equals(constraint)) {
					constraints |= javax.microedition.lcdui.TextField.NUMERIC;
				} else if (PHONENUMBER.equals(constraint)) {
					constraints |= javax.microedition.lcdui.TextField.PHONENUMBER;
				} else if (URL.equals(constraint)) {
					constraints |= javax.microedition.lcdui.TextField.URL;
				} else if (DECIMAL.equals(constraint)) {
					constraints |= javax.microedition.lcdui.TextField.DECIMAL;
				} else if (PASSWORD.equals(constraint)) {
					constraints |= javax.microedition.lcdui.TextField.PASSWORD;
				} else if (SENSITIVE.equals(constraint)) {
					constraints |= javax.microedition.lcdui.TextField.SENSITIVE;
				} else if (NON_PREDICTIVE.equals(constraint)) {
					constraints |= javax.microedition.lcdui.TextField.NON_PREDICTIVE;
				} else if (INITIAL_CAPS_WORD.equals(constraint)) {
					constraints |= javax.microedition.lcdui.TextField.INITIAL_CAPS_WORD;
				} else if (INITIAL_CAPS_SENTENCE.equals(constraint)) {
					constraints |= javax.microedition.lcdui.TextField.INITIAL_CAPS_SENTENCE;
				}
			}
			return true;
		}
		if (KuixConstants.EDIT_ON_ALL_KEYS_ATTRIBUTE.equals(name)) {
			setEditOnAllKeys(BooleanUtil.parseBoolean(value));
			return true;
		}
		if (KuixConstants.TOOLTIP_ATTRIBUTE.equals(name)) {
			setTooltip(value);
			return true;
		}
		if (KuixConstants.ON_CHANGE_ATTRIBUTE.equals(name)) {
			setOnChange(value);
			return true;
		}
		return super.setAttribute(name, value);
	}
	
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the maxSize
	 */
	public int getMaxSize() {
		return maxSize;
	}

	/**
	 * @param maxSize the maxSize to set
	 */
	public void setMaxSize(int maxSize) {
		this.maxSize = Math.max(1, maxSize);
		if (text != null && text.length() > maxSize) {
			setText(text);
		}
	}

	/**
	 * @return the constraints
	 */
	public int getConstraints() {
		return constraints;
	}

	/**
	 * @param constraints the constraints to set
	 */
	public void setConstraints(int constraints) {
		this.constraints = constraints;
	}
	
	/**
	 * @return the editOnAllKeys
	 */
	public boolean isEditOnAllKeys() {
		return editOnAllKeys;
	}

	/**
	 * @param editOnAllKeys the editOnAllKeys to set
	 */
	public void setEditOnAllKeys(boolean editOnAllKeys) {
		this.editOnAllKeys = editOnAllKeys;
	}

	/**
	 * @return th tooltip
	 */
	public String getTooltip() {
		if (tooltipText != null) {
			return tooltipText.getText();
		}
		return null;
	}
	
	/**
	 * Define the {@link TextField} tooltip text.
	 * 
	 * @param text
	 */
	public void setTooltip(String text) {
		if (tooltipText == null) {
			tooltipText = new Text();
			tooltipText.parent = this;
			tooltipText.setStyleClass(KuixConstants.TEXT_FIELD_TOOLTIP_STYLE_CLASS);
		}
		tooltipText.setText(text);
	}
	
	/**
	 * @return the onChange
	 */
	public String getOnChange() {
		return onChange;
	}

	/**
	 * @param onChange the onChange to set
	 */
	public void setOnChange(String onChange) {
		this.onChange = onChange;
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.FocusableWidget#isFocusable()
	 */
	public boolean isFocusable() {
		return enabled && focusable;
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.AbstractTextWidget#setText(java.lang.String)
	 */
	public TextWidget setText(String text) {
		displayedText = null;
		return super.setText(text != null ? text.substring(0, Math.min(text.length(), maxSize)) : null);
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.AbstractTextWidget#getDisplayedText()
	 */
	protected String getDisplayedText() {
		if ((constraints & javax.microedition.lcdui.TextField.PASSWORD) == javax.microedition.lcdui.TextField.PASSWORD) {
			if (displayedText == null && text != null) {
				StringBuffer buffer = new StringBuffer();
				for(int i=0; i<text.length(); ++i) {
					buffer.append('*');
				}
				displayedText = buffer.toString();
			}
			return displayedText;
		}
		return super.getDisplayedText();
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#doLayout()
	 */
	protected void doLayout() {
		super.doLayout();
		if (tooltipText != null) {
			Metrics preferredSize = tooltipText.getPreferredSize(getWidth());
			int x = Alignment.CENTER.alignX(getWidth(), preferredSize.width);
			int y = Alignment.CENTER.alignY(getHeight(), preferredSize.height);
			tooltipText.setBounds(x, y, preferredSize.width, preferredSize.height);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Text#paint(javax.microedition.lcdui.Graphics)
	 */
	public void paint(Graphics g) {
		super.paint(g);
		paintBorder(g);
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Text#paintChildrenImpl(javax.microedition.lcdui.Graphics)
	 */
	protected void paintChildrenImpl(Graphics g) {
		super.paintChildrenImpl(g);
		if (isFocused()) {
			Color color = getColor();
			if (color != null) {
				g.setColor(color.getRGB());
			} else {
				g.setColor(0x000000);
			}
			g.setStrokeStyle(Graphics.SOLID);
			g.drawLine(textX, textY, textX, textY + insetHeight - 1);
		}
		if (tooltipText != null && tooltipVisible) {
			tooltipText.paintImpl(g);
		}
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
	 */
	public void commandAction(Command command, Displayable displayable) {
		if (command == validateCommand) {
			String textBoxString = textBox.getString();
			hideTooltip();
			boolean changed = textBoxString != null && !textBoxString.equals(getText());
			setText(textBoxString);
			if (changed && onChange != null) {
				Worker.instance.pushTask(new WorkerTask() {

					public boolean run() {
						Kuix.callActionMethod(Kuix.parseMethod(onChange, TextField.this));
						return true;
					}
					
				});
			}
		}
		Display.getDisplay(Kuix.getCanvas().getInitializer().getMIDlet()).setCurrent(Kuix.getCanvas());
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.AbstractFocusableWidget#processPointerEvent(byte, int, int)
	 */
	public boolean processPointerEvent(byte type, int x, int y) {
		if (isEnabled() && type == KuixConstants.POINTER_RELEASED_EVENT_TYPE) {
			processActionEvent();
		}
		return super.processPointerEvent(type, x, y);
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#processKeyEvent(byte, int)
	 */
	public boolean processKeyEvent(byte type, int kuixKeyCode) {
		if (isEnabled() && type == KuixConstants.KEY_PRESSED_EVENT_TYPE
				 && (kuixKeyCode == KuixConstants.KUIX_KEY_FIRE
						 || (editOnAllKeys
								 && ( 	   kuixKeyCode == KuixConstants.KUIX_KEY_1
										|| kuixKeyCode == KuixConstants.KUIX_KEY_2
										|| kuixKeyCode == KuixConstants.KUIX_KEY_3
										|| kuixKeyCode == KuixConstants.KUIX_KEY_4
										|| kuixKeyCode == KuixConstants.KUIX_KEY_5
										|| kuixKeyCode == KuixConstants.KUIX_KEY_6
										|| kuixKeyCode == KuixConstants.KUIX_KEY_7
										|| kuixKeyCode == KuixConstants.KUIX_KEY_8
										|| kuixKeyCode == KuixConstants.KUIX_KEY_9
										|| kuixKeyCode == KuixConstants.KUIX_KEY_STAR
										|| kuixKeyCode == KuixConstants.KUIX_KEY_POUND
										|| kuixKeyCode == KuixConstants.KUIX_KEY_PENCIL))
				)) {
			return processActionEvent();
		}
		return super.processKeyEvent(type, kuixKeyCode);
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#processActionEvent()
	 */
	public boolean processActionEvent() {

		// Setup TextBox properties
		textBox = new TextBox(title == null ? "" : title, text == null ? "" : text, maxSize, constraints);
		textBox.addCommand(validateCommand);
		textBox.addCommand(cancelCommand);
		textBox.setCommandListener(this);

		// Show TextBox
		Display.getDisplay(Kuix.getCanvas().getInitializer().getMIDlet()).setCurrent(textBox);

		return true;
	}

	/**
	 * Hide the TextField tooltip
	 */
	private void hideTooltip() {
		if (tooltipTask != null) {
			tooltipTimer = 0;
			tooltipTask = null;
			tooltipVisible = false;
		}
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#onFocus(org.kalmeo.kuix.widget.Widget)
	 */
	protected void onFocus(Widget focusedWidget) {
		super.onFocus(focusedWidget);
		if (focusedWidget == this && tooltipText != null) {
			if (tooltipTimer != 0) {
				tooltipTimer = System.currentTimeMillis();
			} else {
				tooltipTimer = System.currentTimeMillis();
				tooltipTask = new WorkerTask() {

					/* (non-Javadoc)
					 * @see com.kalmeo.util.worker.WorkerTask#execute()
					 */
					public boolean run() {
						if (tooltipTimer == 0) {
							return true;
						}
						if (System.currentTimeMillis() - tooltipTimer > 2000) {
							tooltipVisible = true;
							invalidateAppearance();
							return true;
						}
						return false;
					}
					
				};
				Worker.instance.pushTask(tooltipTask);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#onLostFocus(org.kalmeo.kuix.widget.Widget)
	 */
	protected void onLostFocus(Widget focusedWidget) {
		super.onLostFocus(focusedWidget);
		hideTooltip();
	}

}
