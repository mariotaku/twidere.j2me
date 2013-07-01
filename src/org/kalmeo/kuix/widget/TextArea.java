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
 * Creation date : 21 nov. 07
 * Copyright (c) Kalmeo 2007-2008. All rights reserved.
 * http://www.kalmeo.org
 */

package org.kalmeo.kuix.widget;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Hashtable;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import org.kalmeo.kuix.core.KuixConstants;
import org.kalmeo.kuix.layout.FlowLayout;
import org.kalmeo.kuix.layout.Layout;
import org.kalmeo.kuix.util.Alignment;
import org.kalmeo.kuix.util.Gap;
import org.kalmeo.kuix.util.Metrics;
import org.kalmeo.util.BooleanUtil;
import org.kalmeo.util.xml.LightXmlParser;
import org.kalmeo.util.xml.LightXmlParserHandler;

/**
 * This class represents a textarea. <br>
 * <br>
 * <strong>For further informations, visit the <a
 * href="http://www.kalmeo.org/files/kuix/widgetdoc/index.html"
 * target="new">Kuix widgets reference page</a></strong>.
 * 
 * @author bbeaulant
 */
public class TextArea extends TextWidget {

	// Defaults
	private static final Layout TEXTAREA_DEFAULT_LAYOUT = new FlowLayout(Alignment.BOTTOM);

	// Indicate is the text input need to be parse to extract HTML style tags during reflow 
	private boolean styled = false;

	// Indicate if the textArea need to be reflow before next getPreferredSize call
	private boolean needToReflow;
	
	// The cached objects
	private Gap cachedGap;
	
	/**
	 * Construct a {@link TextArea}
	 */
	public TextArea() {
		super(KuixConstants.TEXT_AREA_WIDGET_TAG);
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#setAttribute(java.lang.String, java.lang.String)
	 */
	public boolean setAttribute(String name, String value) {
		if (KuixConstants.STYLED_ATTRIBUTE.equals(name)) {
			setStyled(BooleanUtil.parseBoolean(value));
			needToReflow = true;
			return true;
		}
		return super.setAttribute(name, value);
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Text#setText(java.lang.String)
	 */
	public TextWidget setText(String text) {
		super.setText(text);
		needToReflow = true;
		return this;
	}
	
	/**
	 * @return the styled
	 */
	public boolean isStyled() {
		return styled;
	}

	/**
	 * @param styled the styled to set
	 */
	public void setStyled(boolean styled) {
		this.styled = styled;
		needToReflow = true;
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Text#getLayout()
	 */
	public Layout getLayout() {
		return TEXTAREA_DEFAULT_LAYOUT;
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getPreferredSize(int)
	 */
	public Metrics getPreferredSize(int preferredWidth) {
		if (needToReflow) {
			reflow();
		}
		return super.getPreferredSize(preferredWidth);
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getGap()
	 */
	public Gap getGap() {
		if (cachedGap == null) {
			cachedGap = new Gap(getFont().charWidth(' '), 0);
		}
		return cachedGap;
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#paintChildrenImpl(javax.microedition.lcdui.Graphics)
	 */
	protected void paintChildrenImpl(Graphics g) {
		int minY = g.getClipY();
		int maxY = minY + g.getClipHeight();
		for (Widget widget = getChild(); widget != null; widget = widget.next) {
			if (widget.getY() + widget.getHeight() >= minY && widget.getY() < maxY) {	// Optimized children paint if in scrollContainer
				widget.paintImpl(g);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.AbstractTextWidget#clearCachedStyle(boolean)
	 */
	public void clearCachedStyle(boolean clearCachedStyle) {
		cachedGap = null;
		super.clearCachedStyle(clearCachedStyle);
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#propagateFocusEvent(org.kalmeo.kuix.widget.Widget, boolean)
	 */
	protected void propagateFocusEvent(Widget focusedWidget, boolean lost) {
		// Do nothing to optimize
	}
	
	/**
	 * Construct all Text child according to the text input ans styled option
	 */
	private void reflow() {
		needToReflow = false;
		removeAll();
		
		if (text == null || text.length() == 0) {
			return;
		}
		
		if (!styled) {
			splitWords(text, Font.STYLE_PLAIN);
			return;
		}
		
		String xmlText = new StringBuffer("<t>").append(text).append("</t>").toString();
		try {

			InputStream inputStream = new ByteArrayInputStream(xmlText.getBytes(KuixConstants.DEFAULT_CHARSET_NAME));
			LightXmlParser.parse(inputStream, KuixConstants.DEFAULT_CHARSET_NAME, new LightXmlParserHandler() {

				short bold = 0;
				short italic = 0;
				short underlined = 0;
				
				String lastCharacters = null;
				String lastHref = null;
				
				/* (non-Javadoc)
				 * @see com.kalmeo.util.xml.DefaultHandler#startDocument()
				 */
				public void startDocument() {
				}
				
				/* (non-Javadoc)
				 * @see com.kalmeo.util.xml.DefaultHandler#startElement(java.lang.String, java.util.Hashtable)
				 */
				public void startElement(String name, Hashtable attributes) {
					processLastCharacters();
					if (KuixConstants.BOLD_TAG.equals(name) || KuixConstants.STRONG_TAG.equals(name)) {
						bold++;
					} else if (KuixConstants.ITALIC_TAG.equals(name)) {
						italic++;
					} else if (KuixConstants.UNDERLINE_TAG.equals(name)) {
						underlined++;
					} else if (KuixConstants.ANCROR_TAG.equals(name)) {
						Object attribute = attributes.get(KuixConstants.ANCROR_TAG_HREF_ATTRIBUTE);
						if (attribute != null) {
							lastHref = (String) attribute;
						}
					} else if (KuixConstants.IMAGE_TAG.equals(name)) {
						Object attribute = attributes.get(KuixConstants.IMAGE_TAG_SRC_ATTRIBUTE);
						if (attribute != null) {
							Picture picture = new Picture().setSource((String) attribute);
							add(picture);
						}
					}
				}
				
				/* (non-Javadoc)
				 * @see org.kalmeo.util.xml.LightXmlParserHandler#characters(java.lang.String, boolean)
				 */
				public void characters(String characters, boolean isCDATA) {
					lastCharacters = characters;
				}

				/* (non-Javadoc)
				 * @see com.kalmeo.util.xml.DefaultHandler#endElement(java.lang.String)
				 */
				public void endElement(String name) {
					if (KuixConstants.ANCROR_TAG.equals(name)) {
						Button hyperlink = new Button(KuixConstants.HYPERLINK_WIDGET_TAG);
						hyperlink.add(new Text().setText(lastCharacters));
						if (lastHref != null) {
							hyperlink.setOnAction("goUrl(" + lastHref + ")");
							lastHref = null;
						}
						add(hyperlink);
						lastCharacters = null;
					} else {
						processLastCharacters();
						if (KuixConstants.BREAD_RETURN_TAG.equals(name) 
								|| KuixConstants.PARAGRAPH_TAG.equals(name) 
								|| KuixConstants.DIV_TAG.equals(name)) {
							add(new Widget(KuixConstants.BREAK_WIDGET_TAG));
							if (KuixConstants.PARAGRAPH_TAG.equals(name) 
									|| KuixConstants.DIV_TAG.equals(name)) {
								add(new Text().setText(" "));
								add(new Widget(KuixConstants.BREAK_WIDGET_TAG));
							}
						} else if (KuixConstants.BOLD_TAG.equals(name) || KuixConstants.STRONG_TAG.equals(name)) {
							bold--;
						} else if (KuixConstants.ITALIC_TAG.equals(name)) {
							italic--;
						} else if (KuixConstants.UNDERLINE_TAG.equals(name)) {
							underlined--;
						}
					}
				}

				/* (non-Javadoc)
				 * @see com.kalmeo.util.xml.DefaultHandler#endDocument()
				 */
				public void endDocument() {
				}
				
				private void processLastCharacters() {
					if (lastCharacters != null) {
						int style = Font.STYLE_PLAIN;
						if (bold != 0) {
							style |= Font.STYLE_BOLD;
						}
						if (italic != 0) {
							style |= Font.STYLE_ITALIC;
						}
						if (underlined != 0) {
							style |= Font.STYLE_UNDERLINED;
						}
						
						splitWords(lastCharacters, style);
						lastCharacters = null;
					}
				}
				
			});
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void splitWords(String fullText, int style) {
		int beginIndex = 0;
		int endIndex = 0;
		String word;
		do {
			endIndex = fullText.indexOf(' ', beginIndex);
			if (endIndex == -1) {
				word = fullText.substring(beginIndex);
			} else {
				word = fullText.substring(beginIndex, endIndex);
			}
			if (word.length() != 0) {
				Text textWidget = new Text();
				textWidget.setText(word);
				if (style != Font.STYLE_PLAIN) {
					textWidget.setDefaultFontStyle(style);
				}
				add(textWidget);
			}
			beginIndex = endIndex + 1;
		} while (endIndex  != -1);
	}
	
}
