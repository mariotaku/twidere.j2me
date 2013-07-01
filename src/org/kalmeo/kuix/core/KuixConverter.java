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

package org.kalmeo.kuix.core;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import org.kalmeo.kuix.core.style.Style;
import org.kalmeo.kuix.core.style.StyleProperty;
import org.kalmeo.kuix.core.style.StyleSelector;
import org.kalmeo.kuix.layout.BorderLayout;
import org.kalmeo.kuix.layout.BorderLayoutData;
import org.kalmeo.kuix.layout.FlowLayout;
import org.kalmeo.kuix.layout.GridLayout;
import org.kalmeo.kuix.layout.InlineLayout;
import org.kalmeo.kuix.layout.Layout;
import org.kalmeo.kuix.layout.StaticLayout;
import org.kalmeo.kuix.layout.StaticLayoutData;
import org.kalmeo.kuix.layout.TableLayout;
import org.kalmeo.kuix.transition.FadeTransition;
import org.kalmeo.kuix.transition.SlideTransition;
import org.kalmeo.kuix.transition.Transition;
import org.kalmeo.kuix.util.Alignment;
import org.kalmeo.kuix.util.Color;
import org.kalmeo.kuix.util.Gap;
import org.kalmeo.kuix.util.Insets;
import org.kalmeo.kuix.util.Metrics;
import org.kalmeo.kuix.util.Repeat;
import org.kalmeo.kuix.util.Span;
import org.kalmeo.kuix.util.Weight;
import org.kalmeo.kuix.widget.Button;
import org.kalmeo.kuix.widget.CheckBox;
import org.kalmeo.kuix.widget.Choice;
import org.kalmeo.kuix.widget.DragAndDropContainer;
import org.kalmeo.kuix.widget.Gauge;
import org.kalmeo.kuix.widget.List;
import org.kalmeo.kuix.widget.ListItem;
import org.kalmeo.kuix.widget.Menu;
import org.kalmeo.kuix.widget.MenuItem;
import org.kalmeo.kuix.widget.Picture;
import org.kalmeo.kuix.widget.PopupBox;
import org.kalmeo.kuix.widget.RadioButton;
import org.kalmeo.kuix.widget.RadioGroup;
import org.kalmeo.kuix.widget.ScrollBar;
import org.kalmeo.kuix.widget.ScrollPane;
import org.kalmeo.kuix.widget.TabFolder;
import org.kalmeo.kuix.widget.TabItem;
import org.kalmeo.kuix.widget.Text;
import org.kalmeo.kuix.widget.TextArea;
import org.kalmeo.kuix.widget.TextField;
import org.kalmeo.kuix.widget.Widget;
import org.kalmeo.util.BooleanUtil;
import org.kalmeo.util.MathFP;
import org.kalmeo.util.StringTokenizer;
import org.kalmeo.util.StringUtil;
import org.kalmeo.util.resource.ImageManager;

/**
 * This converter is the default converter for Kuix basic widgets, style,
 * styleProperties.<br>
 * Override this class to create your own converter and adding custom widget for
 * example.
 * 
 * @author bbeaulant
 */
public class KuixConverter {

	/**
	 * Returns the {@link Class} associated with the specified <code>tag</code>,
	 * or <code>null</code> if the tag is unknow.
	 * 
	 * @param tag
	 * @return the {@link Class} associated with the specified <code>tag</code>
	 */
	public Widget convertWidgetTag(String tag) {
		if (KuixConstants.TEXT_WIDGET_TAG.equals(tag)) {
			return new Text();
		}
		if (KuixConstants.TEXT_FIELD_WIDGET_TAG.equals(tag)) {
			return new TextField();
		}
		if (KuixConstants.TEXT_AREA_WIDGET_TAG.equals(tag)) {
			return new TextArea();
		}
		if (KuixConstants.BREAK_WIDGET_TAG.equals(tag)) {
			return new Widget(KuixConstants.BREAK_WIDGET_TAG);
		}
		if (KuixConstants.CONTAINER_WIDGET_TAG.equals(tag)) {
			return new Widget(KuixConstants.CONTAINER_WIDGET_TAG);
		}
		if (KuixConstants.SCROLL_PANE_WIDGET_TAG.equals(tag)) {
			return new ScrollPane();
		}
		if (KuixConstants.DRAG_AND_DROP_CONTAINER_WIDGET_TAG.equals(tag)) {
			return new DragAndDropContainer();
		}
		if (KuixConstants.PICTURE_WIDGET_TAG.equals(tag)) {
			return new Picture();
		}
		if (KuixConstants.BUTTON_WIDGET_TAG.equals(tag)) {
			return new Button();
		}
		if (KuixConstants.HYPERLINK_WIDGET_TAG.equals(tag)) {
			return new Button(KuixConstants.HYPERLINK_WIDGET_TAG);
		}
		if (KuixConstants.CHECKBOX_WIDGET_TAG.equals(tag)) {
			return new CheckBox();
		}
		if (KuixConstants.RADIO_BUTTON_WIDGET_TAG.equals(tag)) {
			return new RadioButton();
		}
		if (KuixConstants.RADIO_GROUP_WIDGET_TAG.equals(tag)) {
			return new RadioGroup();
		}
		if (KuixConstants.GAUGE_WIDGET_TAG.equals(tag)) {
			return new Gauge();
		}
		if (KuixConstants.LIST_WIDGET_TAG.equals(tag)) {
			return new List();
		}
		if (KuixConstants.LIST_ITEM_WIDGET_TAG.equals(tag)) {
			return new ListItem();
		}
		if (KuixConstants.TAB_FOLDER_WIDGET_TAG.equals(tag)) {
			return new TabFolder();
		}
		if (KuixConstants.TAB_ITEM_WIDGET_TAG.equals(tag)) {
			return new TabItem();
		}
		if (KuixConstants.MENU_WIDGET_TAG.equals(tag)) {
			return new Menu();
		}
		if (KuixConstants.MENU_ITEM_WIDGET_TAG.equals(tag)) {
			return new MenuItem();
		}
		if (KuixConstants.SCROLL_BAR_WIDGET_TAG.equals(tag)) {
			return new ScrollBar();
		}
		if (KuixConstants.CHOICE_WIDGET_TAG.equals(tag)) {
			return new Choice();
		}
		if (KuixConstants.POPUP_BOX_WIDGET_TAG.equals(tag)) {
			return new PopupBox();
		}
		return null;
	}

	/**
	 * Extract {@link Style} definitions from raw datas and returns an array of
	 * {@link Style} instances.
	 * 
	 * @param rawSelectors
	 * @param rawDefinitions
	 * @return A list of {@link Style} instance.
	 */
	public Style[] convertStyleSheets(String rawSelectors, String rawDefinitions) {

		// Extract Selectors
		StringTokenizer selectors = new StringTokenizer(rawSelectors, ",");
		int numSelectors = selectors.countTokens();
		Style[] styles = new Style[numSelectors];
		for (int i = 0; i < numSelectors; ++i) {

			// Create the StyleSelector tree
			StyleSelector previousStyleSelector = null;
			StringTokenizer contextualSelectors = new StringTokenizer(selectors.nextToken(), " \t\n\r");
			while (contextualSelectors.hasMoreTokens()) {
				StyleSelector styleSelector = new StyleSelector(contextualSelectors.nextToken().toLowerCase());
				if (previousStyleSelector != null) {
					styleSelector.parent = previousStyleSelector;
				}
				previousStyleSelector = styleSelector;
			}

			// Create the Style
			styles[i] = new Style(previousStyleSelector);

		}

		// Extract definitions
		StringTokenizer definitions = new StringTokenizer(rawDefinitions, ";");
		while (definitions.hasMoreTokens()) {
			String definition = definitions.nextToken().trim();
			if (definition.length() > 2) {
				StringTokenizer property = new StringTokenizer(definition, ":");
				if (property.countTokens() == 2) {

					String name = property.nextToken().trim();
					String rawValue = property.nextToken().trim();

					// Add property to all styles (Because of linked list, new instance is needed for each style)
					for (int i = 0; i < styles.length; ++i) {
						styles[i].add(new StyleProperty(name, rawValue));
					}

				}
			}
		}

		return styles;
	}

	/**
	 * Convert a property raw data string into a specific object instance.
	 * 
	 * @param name
	 * @param rawData
	 * @return a specific object instance.
	 */
	public Object convertStyleProperty(String name, String rawData) throws IllegalArgumentException {
		
		// Color
		if (KuixConstants.COLOR_STYLE_PROPERTY.equals(name) 
				|| KuixConstants.BACKGROUND_COLOR_STYLE_PROPERTY.equals(name)
				|| KuixConstants.GRAYED_COLOR_STYLE_PROPERTY.equals(name)) {
			return convertColor(rawData);
		}
		if (KuixConstants.BORDER_COLOR_STYLE_PROPERTY.equals(name)) {
			return convertBorderColor(rawData);
		}

		// Font face
		if (KuixConstants.FONT_FACE_STYLE_PROPERTY.equals(name)) {
			return convertFontFace(rawData);
		}
		
		// Font style
		if (KuixConstants.FONT_STYLE_STYLE_PROPERTY.equals(name)) {
			return convertFontStyle(rawData);
		}
		
		// Font size
		if (KuixConstants.FONT_SIZE_STYLE_PROPERTY.equals(name)) {
			return convertFontSize(rawData);
		}
		
		// Stroke
		if (KuixConstants.BORDER_STROKE_STYLE_PROPERTY.equals(name)) {
			return convertStroke(rawData);
		}
		
		// Inset
		if (KuixConstants.MARGIN_STYLE_PROPERTY.equals(name) 
				|| KuixConstants.BORDER_STYLE_PROPERTY.equals(name) 
				|| KuixConstants.PADDING_STYLE_PROPERTY.equals(name)) {
			return convertInset(rawData);
		}
		
		// Metrics
		if (KuixConstants.MIN_SIZE_STYLE_PROPERTY.equals(name)) {
			return convertMetrics(rawData);
		}

		// Gap
		if (KuixConstants.GAP_STYLE_PROPERTY.equals(name)) {
			return convertGap(rawData);
		}

		// Span
		if (KuixConstants.SPAN_STYLE_PROPERTY.equals(name)) {
			return convertSpan(rawData);
		}

		// Weight
		if (KuixConstants.WEIGHT_STYLE_PROPERTY.equals(name)) {
			return convertWeight(rawData);
		}

		// Align
		if (KuixConstants.ALIGN_STYLE_PROPERTY.equals(name)) {
			return convertAlignment(rawData);
		}
		if (KuixConstants.BACKGROUND_ALIGN_STYLE_PROPERTY.equals(name)) {
			return convertAlignmentArray(rawData, 1, "|");
		}
		if (KuixConstants.BORDER_ALIGN_STYLE_PROPERTY.equals(name)) {
			return convertAlignmentArray(rawData, 8, StringTokenizer.DEFAULT_DELIM);
		}

		// Image
		if (KuixConstants.BACKGROUND_IMAGE_STYLE_PROPERTY.equals(name)) {
			return convertImageArray(rawData, 1, "|");
		}
		if (KuixConstants.BORDER_IMAGE_STYLE_PROPERTY.equals(name)) {
			return convertBorderImage(rawData);
		}
		
		// Layout
		if (KuixConstants.LAYOUT_STYLE_PROPERTY.equals(name)) {
			return convertLayout(rawData);
		}
		if (KuixConstants.LAYOUT_DATA_STYLE_PROPERTY.equals(name)) {
			return convertLayoutData(rawData);
		}
		
		// Background repeat
		if (KuixConstants.BACKGROUND_REPEAT_STYLE_PROPERTY.equals(name)) {
			return convertRepeatArray(rawData, 1, "|");
		}
		
		// Transition
		if (KuixConstants.TRANSITION_STYLE_PROPERTY.equals(name)) {
			return convertTransition(rawData);
		}
		
		throw new IllegalArgumentException("Unknow style name " + name);
	}
	
	/**
	 * @param rawData
	 * @return The converted {@link Transition}
	 */
	public Transition convertTransition(String rawData) {
		if (isNone(rawData)) {
			return null;
		}
		String rawParams = null;
		if ((rawParams = StringUtil.extractRawParams("slide", rawData)) != null) {
			Alignment alignment = convertAlignment(rawParams);
			return new SlideTransition(alignment);
		}
		if ((rawParams = StringUtil.extractRawParams("fade", rawData)) != null) {
			return new FadeTransition(Integer.parseInt(rawParams));
		}
		throw new IllegalArgumentException("Bad transition value");
	}
	
	/**
	 * @param rawData
	 * @return The converted {@link Color}
	 */
	protected Color convertColor(String rawData) {
		if (isNone(rawData)) {
			return null;
		}
		if (rawData != null) {
			rawData = rawData.trim();
			if (rawData.startsWith("#")) {
				return new Color(Integer.parseInt(rawData.substring(1), 16));
			}
			if ("red".equals(rawData)) {
				return Color.RED;
			}
			if ("green".equals(rawData)) {
				return Color.GREEN;
			}
			if ("blue".equals(rawData)) {
				return Color.BLUE;
			}
			if ("white".equals(rawData)) {
				return Color.WHITE;
			}
			if ("black".equals(rawData)) {
				return Color.BLACK;
			}
		}
		throw new IllegalArgumentException("Bad color value");
	}

	/**
	 * @param rawData
	 * @return The converted font face
	 */
	protected Integer convertFontFace(String rawData) {
		int face = Font.FACE_SYSTEM;
			
		// Face (system|monospace|proportional)
		if ("monospace".equals(rawData)) {
			face = Font.FACE_MONOSPACE;
		} else if ("proportional".equals(rawData)) {
			face = Font.FACE_PROPORTIONAL;
		}
		
		return new Integer(face);
	}
	
	/**
	 * @param rawData
	 * @return The converted font style
	 */
	protected Integer convertFontStyle(String rawData) {
		StringTokenizer values = new StringTokenizer(rawData);
		int style = Font.STYLE_PLAIN;
		while (values.hasMoreTokens()) {
			
			String fontAttribute = values.nextToken().toLowerCase();
			
			// Style (plain bold italic underline)
			if ("bold".equals(fontAttribute)) {
				style |= Font.STYLE_BOLD;
			} else if ("italic".equals(fontAttribute)) {
				style |= Font.STYLE_ITALIC;
			} else if ("underlined".equals(fontAttribute)) {
				style |= Font.STYLE_UNDERLINED;
			}
			
		}
		return new Integer(style);
	}
	
	/**
	 * @param rawData
	 * @return The converted font size
	 */
	protected Integer convertFontSize(String rawData) {
			
		// Size (medium|large|small)
		int size;
		if ("large".equals(rawData)) {
			size = Font.SIZE_LARGE;
		} else if ("small".equals(rawData)) {
			size = Font.SIZE_SMALL;
		} else if ("medium".equals(rawData)) {
			size = Font.SIZE_MEDIUM;
		} else {
			throw new IllegalArgumentException("Invalid font-size value : " + rawData);
		}
		
		return new Integer(size);
	}
	
	/**
	 * @param rawData
	 * @return The converted stoke
	 */
	protected Integer convertStroke(String rawData) {
		
		// Size (solid|dotted)
		int stroke;
		if ("dotted".equals(rawData)) {
			stroke = Graphics.DOTTED;
		} else if ("solid".equals(rawData)) {
			stroke = Graphics.SOLID;
		} else {
			throw new IllegalArgumentException("Invalid stroke value : " + rawData);
		}
		
		return new Integer(stroke);
	}
	
	/**
	 * @param rawData
	 * @return The converted {@link Insets}
	 */
	protected Insets convertInset(String rawData) {
		if (isNone(rawData)) {
			return null;
		}
		int[] intValues = convertIntArray(rawData, 1, StringTokenizer.DEFAULT_DELIM);
		if (intValues != null) {
			if (intValues.length == 1) {
				return new Insets(intValues[0], intValues[0], intValues[0], intValues[0]);
			} else if (intValues.length >= 4) {
				return new Insets(intValues[0], intValues[1], intValues[2], intValues[3]);
			}
		}
		throw new IllegalArgumentException("Bad inset value");
	}
	
	/**
	 * @param rawData
	 * @return The converted {@link Metrics}
	 */
	protected Metrics convertMetrics(String rawData) {
		if (isNone(rawData)) {
			return null;
		}
		int[] intValues = convertIntArray(rawData, 2, StringTokenizer.DEFAULT_DELIM);
		if (intValues != null) {
			if (intValues.length == 2) {
				return new Metrics(null, 0, 0, intValues[0], intValues[1]);
			} else if (intValues.length >= 4) {
				return new Metrics(null, intValues[0], intValues[1], intValues[2], intValues[3]);
			}
		}
		throw new IllegalArgumentException("Bad metrics value");
	}

	/**
	 * @param rawData
	 * @return The converted {@link Gap}
	 */
	protected Gap convertGap(String rawData) {
		if (isNone(rawData)) {
			return null;
		}
		int[] intValues = convertIntArray(rawData, 1, StringTokenizer.DEFAULT_DELIM);
		if (intValues != null) {
			if (intValues.length == 1) {
				return new Gap(intValues[0], intValues[0]);
			} else if (intValues.length >= 1) {
				return new Gap(intValues[0], intValues[1]);
			}
		}
		throw new IllegalArgumentException("Bad gap value");
	}

	/**
	 * @param rawData
	 * @return The converted {@link Repeat}
	 */
	protected Repeat convertRepeat(String rawData) {
		if (isNone(rawData)) {
			return null;
		}
		int[] intValues = convertIntArray(rawData.trim(), 1, StringTokenizer.DEFAULT_DELIM);
		if (intValues != null) {
			if (intValues.length == 1) {
				return new Repeat(intValues[0], intValues[0]);
			} else if (intValues.length >= 2) {
				return new Repeat(intValues[0], intValues[1]);
			}
		}
		throw new IllegalArgumentException("Bad repeat value");
	}
	
	/**
	 * @param rawData
	 * @param wantedSize
	 * @param delim
	 * @return The converted Repeat[]
	 */
	protected Repeat[] convertRepeatArray(String rawData, int wantedSize, String delim) {
		if (isNone(rawData)) {
			return null;
		}
		StringTokenizer values = new StringTokenizer(rawData, delim);
		if (values.countTokens() >= wantedSize) {
			Repeat[] repeats = new Repeat[values.countTokens()];
			for (int i = 0; values.hasMoreTokens(); ++i) {
				try {
					repeats[i] = convertRepeat(values.nextToken());
					continue;
				} catch (Exception e) {
					return null;
				}
			}
			return repeats;
		}
		throw new IllegalArgumentException("Bad repeats value");
	}
	
	/**
	 * @param rawData
	 * @return The converted {@link Span}
	 */
	protected Span convertSpan(String rawData) {
		if (isNone(rawData)) {
			return null;
		}
		int[] intValues = convertIntArray(rawData, 2, StringTokenizer.DEFAULT_DELIM);
		if (intValues != null) {
			return new Span(intValues[0], intValues[1]);
		}
		throw new IllegalArgumentException("Bad span value");
	}

	/**
	 * @param rawData
	 * @return The converted {@link Weight}
	 */
	protected Weight convertWeight(String rawData) {
		if (isNone(rawData)) {
			return null;
		}
		int[] fpValues = convertFPArray(rawData, 2, StringTokenizer.DEFAULT_DELIM);
		if (fpValues != null) {
			return new Weight(fpValues[0], fpValues[1]);
		}
		throw new IllegalArgumentException("Bad weight value");
	}

	/**
	 * @param rawData
	 * @return The converted {@link Alignment}
	 */
	protected Alignment convertAlignment(String rawData) {
		if (isNone(rawData)) {
			return null;
		}
		StringTokenizer values = new StringTokenizer(rawData.trim());
		while (values.hasMoreTokens()) {

			String alignmentValue = values.nextToken().toLowerCase();

			if ("top-left".equals(alignmentValue)) {
				return Alignment.TOP_LEFT;
			} else if ("top".equals(alignmentValue)) {
				return Alignment.TOP;
			} else if ("top-right".equals(alignmentValue)) {
				return Alignment.TOP_RIGHT;
			} else if ("left".equals(alignmentValue)) {
				return Alignment.LEFT;
			} else if ("center".equals(alignmentValue)) {
				return Alignment.CENTER;
			} else if ("right".equals(alignmentValue)) {
				return Alignment.RIGHT;
			} else if ("bottom-left".equals(alignmentValue)) {
				return Alignment.BOTTOM_LEFT;
			} else if ("bottom".equals(alignmentValue)) {
				return Alignment.BOTTOM;
			} else if ("bottom-right".equals(alignmentValue)) {
				return Alignment.BOTTOM_RIGHT;
			} else if ("fill".equals(alignmentValue)) {
				return Alignment.FILL;
			} else if ("fill-top".equals(alignmentValue)) {
				return Alignment.FILL_TOP;
			} else if ("fill-left".equals(alignmentValue)) {
				return Alignment.FILL_LEFT;
			} else if ("fill-center".equals(alignmentValue)) {
				return Alignment.FILL_CENTER;
			} else if ("fill-right".equals(alignmentValue)) {
				return Alignment.FILL_RIGHT;
			} else if ("fill-bottom".equals(alignmentValue)) {
				return Alignment.FILL_BOTTOM;
			}
		}
		throw new IllegalArgumentException("Bad alignment value");
	}

	/**
	 * @param rawData
	 * @param wantedSize
	 * @param delim
	 * @return The converted Alignment[]
	 */
	protected Alignment[] convertAlignmentArray(String rawData, int wantedSize, String delim) {
		if (isNone(rawData)) {
			return null;
		}
		StringTokenizer values = new StringTokenizer(rawData, delim);
		if (values.countTokens() >= wantedSize) {
			Alignment[] alignments = new Alignment[values.countTokens()];
			for (int i = 0; values.hasMoreTokens(); ++i) {
				try {
					alignments[i] = convertAlignment(values.nextToken());
					continue;
				} catch (Exception e) {
					return null;
				}
			}
			return alignments;
		}
		throw new IllegalArgumentException("Bad alignments value");
	}
	
	/**
	 * Syntax :
	 * <ul>
	 * <li><code>url(src)</code>.</li>
	 * <li><code>url(src,x,y,width,height)</code>.</li>
	 * <li><code>url(src,x,y,width,height,transform)</code>.</li>
	 * </ul>
	 * 
	 * @param rawData
	 * @return The converted {@link Image}
	 */
	protected Image convertImage(String rawData) {
		if (isNone(rawData)) {
			return null;
		}
		String rawParams = null;
		String imgSrc = null;
		if ((rawParams = StringUtil.extractRawParams("url", rawData.trim())) != null) {
			Image image = convertImageDefinition(rawParams);
			if (image != null) {
				return image;
			}
		}
		throw new IllegalArgumentException("Bad image value : " + (imgSrc != null ? imgSrc : ""));
	}
	
	/**
	 * Syntax :
	 * <ul>
	 * <li><code>src</code>.</li>
	 * <li><code>src,x,y,width,height</code>.</li>
	 * <li><code>src,x,y,width,height,transform</code>.</li>
	 * </ul>
	 * 
	 * @param rawData
	 * @return The converted {@link Image}
	 */
	public Image convertImageDefinition(String rawData) {
		StringTokenizer st = new StringTokenizer(rawData, ",");
		int numTokens = st.countTokens();
		if (numTokens >= 1) {
			Image fullImage = null;
			String imgSrc = st.nextToken();
			if (!imgSrc.startsWith("/")) {
				// By default the relative path point to /img
				imgSrc = new StringBuffer(KuixConstants.DEFAULT_IMG_RES_FOLDER).append(imgSrc).toString();
			}
			fullImage = ImageManager.instance.getImage(imgSrc);
			if (fullImage != null) {
				if (numTokens >= 5) {
					
					int x = Integer.parseInt(st.nextToken());
					int y = Integer.parseInt(st.nextToken());
					int width = Integer.parseInt(st.nextToken());
					int height = Integer.parseInt(st.nextToken());
					int transform = Sprite.TRANS_NONE;
					
					if (numTokens == 6) {
						transform = convertTransform(st.nextToken());
					}
					
					try {
						return Image.createImage(fullImage, x, y, width, height, transform);
					} catch (Exception e) {
						System.err.println("Error loading custom : " + imgSrc);
					}
					
				} else {
					return fullImage;
				}
			}
		}
		return null;
	}
	
	/**
	 * @param rawData
	 * @param wantedSize
	 * @param delim
	 * @return The converted Image[]
	 */
	protected Image[] convertImageArray(String rawData, int wantedSize, String delim) {
		if (isNone(rawData)) {
			return null;
		}
		StringTokenizer values = new StringTokenizer(rawData, delim);
		if (values.countTokens() >= wantedSize) {
			Image[] images = new Image[values.countTokens()];
			for (int i = 0; values.hasMoreTokens(); ++i) {
				try {
					images[i] = convertImage(values.nextToken());
					continue;
				} catch (Exception e) {
					return null;
				}
			}
			return images;
		}
		throw new IllegalArgumentException("Bad images value");
	}
	
	/**
	 * @param rawData
	 * @param wantedSize
	 * @param delim
	 * @return The converted Color[]
	 */
	protected Color[] convertBorderColor(String rawData) {
		if (isNone(rawData)) {
			return null;
		}
		StringTokenizer values = new StringTokenizer(rawData);
		if (values.countTokens() == 1) {
			Color color = convertColor(values.nextToken());
			return new Color[] { color, color, color, color };
		} else if (values.countTokens() >= 4) {
			return new Color[] { 	convertColor(values.nextToken()), 
									convertColor(values.nextToken()),
									convertColor(values.nextToken()),
									convertColor(values.nextToken()) };
		}
		throw new IllegalArgumentException("Bad border-color value");
	}
	
	/**
	 * @param rawData
	 * @param wantedSize
	 * @param delim
	 * @return The converted Image[]
	 */
	protected Image[] convertBorderImage(String rawData) {
		if (isNone(rawData)) {
			return null;
		}
		StringTokenizer values = new StringTokenizer(rawData);
		int numValues = values.countTokens();
		if (numValues == 1) {
			Image[] images = new Image[8];
			Image image = convertImage(rawData);
			if (image != null) {
				for (int i = 0; i < 8; ++i) {
					images[i] = image;
				}
			}
			return images;
		}
		if (numValues == 5) {
			Image[] images = new Image[8];
			Image image = convertImage(values.nextToken());
			if (image != null) {
				try {
					
					int imageWidth = image.getWidth();
					int imageHeight = image.getHeight();
					int top = Integer.parseInt(values.nextToken().trim());
					int right = Integer.parseInt(values.nextToken().trim());
					int bottom = Integer.parseInt(values.nextToken().trim());
					int left = Integer.parseInt(values.nextToken().trim());
					
					if (top != 0) {
						images[0] = Image.createImage(image, left, 0, imageWidth - left - right, top, 0);							// top
						if (right != 0) {
							images[1] = Image.createImage(image, imageWidth - right, 0, right, top, 0);								// top-right
						}
					}
					if (right != 0) {
						images[2] = Image.createImage(image, imageWidth - right, top, right, imageHeight - top - bottom, 0);		// right
						if (bottom != 0) {
							images[3] = Image.createImage(image, imageWidth - right, imageHeight - bottom, right, bottom, 0);		// bottom-right
						}
					}
					if (bottom != 0) {
						images[4] = Image.createImage(image, left, imageHeight - bottom, imageWidth - left - right, bottom, 0);		// bottom
						if (left != 0) {
							images[5] = Image.createImage(image, 0, imageHeight - bottom, left, bottom, 0);							// bottom-left
						}
					}
					if (left != 0) {
						images[6] = Image.createImage(image, 0, top, left, imageHeight - top - bottom, 0);							// left
						if (top != 0) {
							images[7] = Image.createImage(image, 0, 0, left, top, 0);												// top-left
						}
					}
					
				} catch (Exception e) {
					throw new IllegalArgumentException("Bad top, right, bottom or left value");
				}
			} else {
				throw new IllegalArgumentException("Bad image value");
			}
			return images;
		}
		if (numValues == 8) {
			Image[] images = new Image[8];
			for (int i = 0; values.hasMoreTokens(); ++i) {
				try {
					images[i] = convertImage(values.nextToken());
					continue;
				} catch (Exception e) {
					return null;
				}
			}
			return images;
		}
		throw new IllegalArgumentException("Bad border-image value");
	}
	
	/**
	 * @param rawData
	 * @return The converted {@link Layout}
	 */
	protected Layout convertLayout(String rawData) {
		if (isNone(rawData)) {
			return null;
		}
		String rawParams = null;
		if ((rawParams = StringUtil.extractRawParams("inlinelayout", rawData)) != null) {
			StringTokenizer st = new StringTokenizer(rawParams, ",");
			if (st.countTokens() >= 2) {
				boolean horizontal = BooleanUtil.parseBoolean(st.nextToken());
				Alignment alignment = convertAlignment(st.nextToken());
				if (alignment != null) {
					return new InlineLayout(horizontal, alignment);
				} 
				return new InlineLayout(horizontal);
			}
			return new InlineLayout();
		} else if ((rawParams = StringUtil.extractRawParams("flowlayout", rawData)) != null) {
			Alignment alignment = convertAlignment(rawParams);
			if (alignment != null) {
				return new FlowLayout(alignment);
			} 
			return new FlowLayout();
		} else if (rawData.startsWith("tablelayout")) {
			return TableLayout.instance;
		} else if (rawData.startsWith("borderlayout")) {
			return BorderLayout.instance;
		} else if ((rawParams = StringUtil.extractRawParams("gridlayout", rawData)) != null) {
			StringTokenizer st = new StringTokenizer(rawParams, ",");
			if (st.countTokens() >= 2) {
				int numCols = Integer.parseInt(st.nextToken().trim());
				int numRows = Integer.parseInt(st.nextToken().trim());
				return new GridLayout(numCols, numRows);
			}
		} else if (rawData.startsWith("staticlayout")) {
			return StaticLayout.instance;
		}
		throw new IllegalArgumentException("Bad layout value");
	}

	/**
	 * @param rawData
	 * @return The converted LayoutData
	 */
	protected Object convertLayoutData(String rawData) {
		if (isNone(rawData)) {
			return null;
		}
		String rawParams = null;
		// BorderLayouData
		if ((rawParams = StringUtil.extractRawParams("bld", rawData)) != null) {
			if ("north".equals(rawParams)) {
				return BorderLayoutData.instanceNorth;
			} else if ("west".equals(rawParams)) {
				return BorderLayoutData.instanceWest;
			} else if ("east".equals(rawParams)) {
				return BorderLayoutData.instanceEast;
			} else if ("south".equals(rawParams)) {
				return BorderLayoutData.instanceSouth;
			} else if ("center".equals(rawParams)) {
				return BorderLayoutData.instanceCenter;
			}
			throw new IllegalArgumentException("Invalid bld value : " + rawParams);
		}
		// StaticLayoutData
		if ((rawParams = StringUtil.extractRawParams("sld", rawData)) != null) {
			int pos = rawParams.indexOf(",");
			if (pos != -1) {
				try {
					Alignment alignment = convertAlignment(rawParams.substring(0, pos));
					int[] values = convertFPArray(rawParams.substring(pos + 1), 2, ",");
					if (values != null) {
						return new StaticLayoutData(alignment, values[0], values[1]);
					}
				} catch (Exception e) {
				}
			} else {
				Alignment alignment = convertAlignment(rawParams);
				return new StaticLayoutData(alignment);
			}
			throw new IllegalArgumentException("Invalid sld value : " + rawParams);
		}
		throw new IllegalArgumentException("Bad layout data value");
	}
	
	/**
	 * @param rawData
	 * @return The converted image transform
	 */
	public int convertTransform(String rawData) {
		if (rawData != null) {
			if (rawData.equals("mirror")) {
				return Sprite.TRANS_MIRROR;
			} else if (rawData.equals("mirror_rot270")) {
				return Sprite.TRANS_MIRROR_ROT270;
			} else if (rawData.equals("mirror_rot180")) {
				return Sprite.TRANS_MIRROR_ROT180;
			} else if (rawData.equals("mirror_rot90")) {
				return Sprite.TRANS_MIRROR_ROT90;
			} else if (rawData.equals("rot270")) {
				return Sprite.TRANS_ROT270;
			} else if (rawData.equals("rot180")) {
				return Sprite.TRANS_ROT180;
			} else if (rawData.equals("rot90")) {
				return Sprite.TRANS_ROT90;
			}
		}
		return Sprite.TRANS_NONE;
	}
	
	/**
	 * @param rawData
	 * @return The converted style classes
	 */
	public String[] convertStyleClasses(String rawData) {
		if (isNone(rawData)) {
			return null;
		}
		StringTokenizer values = new StringTokenizer(rawData);
		if (values.hasMoreTokens()) {
			String[] styleClasses = new String[values.countTokens()];
			int i = 0;
			while (values.hasMoreTokens()) {
				styleClasses[i++] = values.nextToken().toLowerCase();
			}
			return styleClasses;
		} else {
			throw new IllegalArgumentException("Bad class value");
		}
	}
	
	/**
	 * Convert a key code (like <code>left</code> or <code>right</code>) string
	 * definition to internal representation.
	 * 
	 * @param rawData
	 * @return the converted kuixKeyCode
	 */
	public int convertKuixKeyCode(String rawData) {
		String value = rawData.trim();
		int kuixKeyCode = KuixConstants.NOT_DEFINED_KEY;
		if ("0".equals(value)) {
			kuixKeyCode = KuixConstants.KUIX_KEY_0;
		} else if ("1".equals(value)) {
			kuixKeyCode = KuixConstants.KUIX_KEY_1;
		} else if ("2".equals(value)) {
			kuixKeyCode = KuixConstants.KUIX_KEY_2;
		} else if ("3".equals(value)) {
			kuixKeyCode = KuixConstants.KUIX_KEY_3;
		} else if ("4".equals(value)) {
			kuixKeyCode = KuixConstants.KUIX_KEY_4;
		} else if ("5".equals(value)) {
			kuixKeyCode = KuixConstants.KUIX_KEY_5;
		} else if ("6".equals(value)) {
			kuixKeyCode = KuixConstants.KUIX_KEY_6;
		} else if ("7".equals(value)) {
			kuixKeyCode = KuixConstants.KUIX_KEY_7;
		} else if ("8".equals(value)) {
			kuixKeyCode = KuixConstants.KUIX_KEY_8;
		} else if ("9".equals(value)) {
			kuixKeyCode = KuixConstants.KUIX_KEY_9;
		} else if ("*".equals(value)) {
			kuixKeyCode = KuixConstants.KUIX_KEY_STAR;
		} else if ("#".equals(value)) {
			kuixKeyCode = KuixConstants.KUIX_KEY_POUND;
		} else if ("softleft".equals(value)) {
			kuixKeyCode = KuixConstants.KUIX_KEY_SOFT_LEFT;
		} else if ("softright".equals(value)) {
			kuixKeyCode = KuixConstants.KUIX_KEY_SOFT_RIGHT;
		} else if ("up".equals(value)) {
			kuixKeyCode = KuixConstants.KUIX_KEY_UP;
		} else if ("left".equals(value)) {
			kuixKeyCode = KuixConstants.KUIX_KEY_LEFT;
		} else if ("right".equals(value)) {
			kuixKeyCode = KuixConstants.KUIX_KEY_RIGHT;
		} else if ("down".equals(value)) {
			kuixKeyCode = KuixConstants.KUIX_KEY_DOWN;
		} else if ("fire".equals(value)) {
			kuixKeyCode = KuixConstants.KUIX_KEY_FIRE;
		} else if ("delete".equals(value)) {
			kuixKeyCode = KuixConstants.KUIX_KEY_DELETE;
		} else if ("back".equals(value)) {
			kuixKeyCode = KuixConstants.KUIX_KEY_BACK;
		} else if ("pencil".equals(value)) {
			kuixKeyCode = KuixConstants.KUIX_KEY_PENCIL;
		}
		return kuixKeyCode;
	}

	/**
	 * Convert a shortcuts (like "left|right=mysAction|1|*") string definition
	 * to internal representation. The result is a byte array where first 4
	 * bytes represents all key codes masks, and other bytes represent a list of
	 * key code / action couple.
	 * 
	 * @param rawData
	 * @return The shortcut kuix key code converted byte array.
	 */
	public byte[] convertShortcuts(String rawData) {
		
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream outputStream = new DataOutputStream(byteArrayOutputStream);
		
		int shortcutKuixKeyCodes = 0;
		StringTokenizer values = new StringTokenizer(rawData, "|");
		String value = null;
		String action;
		int kuixKeyCode;
		while (values.hasMoreTokens()) {
			value = values.nextToken().trim();
			
			// Check if definition is like 'key=action'
			action = null;
			int equalityPos = value.indexOf('=');
			if (equalityPos != -1) {
				action = value.substring(equalityPos + 1);
				value = value.substring(0, equalityPos);
			}
			
			// Convert keyCode
			kuixKeyCode = convertKuixKeyCode(value);
			if (kuixKeyCode != KuixConstants.NOT_DEFINED_KEY) {
				shortcutKuixKeyCodes |= kuixKeyCode;
				if (action != null) {
					try {
						outputStream.writeInt(kuixKeyCode);	// (4 bytes)
						outputStream.writeUTF(action);		// length (2 bytes) + action
					} catch (IOException e) {
					}
				}
			}
		}
		if (shortcutKuixKeyCodes != 0) {
			byte[] actions = byteArrayOutputStream.toByteArray();
			byteArrayOutputStream.reset();
			try {
				outputStream.writeInt(shortcutKuixKeyCodes);
				outputStream.write(actions);
			} catch (IOException e) {
				return null;
			}
			return byteArrayOutputStream.toByteArray();
		}
		return null;
	}
	
	/**
	 * @param rawData
	 * @param wantedMinSize
	 * @param delim
	 * @return The converted int[]
	 */
	public int[] convertIntArray(String rawData, int wantedMinSize, String delim) {
		StringTokenizer values = new StringTokenizer(rawData, delim);
		if (values.countTokens() >= wantedMinSize) {
			int[] intValues = new int[values.countTokens()];
			for (int i = 0; values.hasMoreTokens(); ++i) {
				try {
					intValues[i] = Integer.parseInt(values.nextToken().trim());
					continue;
				} catch (Exception e) {
					return null;
				}
			}
			return intValues;
		}
		return null;
	}

	/**
	 * @param rawData
	 * @param wantedSize
	 * @param delim
	 * @return The converted fixed-point int[]
	 */
	public int[] convertFPArray(String rawData, int wantedSize, String delim) {
		StringTokenizer values = new StringTokenizer(rawData, delim);
		if (values.countTokens() >= wantedSize) {
			int[] fpValues = new int[values.countTokens()];
			for (int i = 0; values.hasMoreTokens(); ++i) {
				try {
					fpValues[i] = MathFP.toFP(values.nextToken().trim());
					continue;
				} catch (Exception e) {
					return null;
				}
			}
			return fpValues;
		}
		return null;
	}
	
	/**
	 * Check if the given <code>rawData</code> is 'none'
	 * 
	 * @param rawData
	 * @return <code>true</code> if rawData equals "none"
	 */
	protected boolean isNone(String rawData) {
		return ("none".equals(rawData));
	}
	
}
