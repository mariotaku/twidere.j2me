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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UTFDataFormatException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

import javax.microedition.lcdui.Display;

import org.kalmeo.kuix.core.model.DataProvider;
import org.kalmeo.kuix.core.style.Style;
import org.kalmeo.kuix.core.style.StyleProperty;
import org.kalmeo.kuix.core.style.StyleSelector;
import org.kalmeo.kuix.util.Method;
import org.kalmeo.kuix.widget.Menu;
import org.kalmeo.kuix.widget.MenuItem;
import org.kalmeo.kuix.widget.Picture;
import org.kalmeo.kuix.widget.PopupBox;
import org.kalmeo.kuix.widget.Screen;
import org.kalmeo.kuix.widget.Text;
import org.kalmeo.kuix.widget.TextWidget;
import org.kalmeo.kuix.widget.Widget;
import org.kalmeo.util.BooleanUtil;
import org.kalmeo.util.Filter;
import org.kalmeo.util.LinkedList;
import org.kalmeo.util.StringTokenizer;
import org.kalmeo.util.StringUtil;
import org.kalmeo.util.frame.FrameHandler;
import org.kalmeo.util.worker.Worker;
import org.kalmeo.util.xml.LightXmlParser;
import org.kalmeo.util.xml.LightXmlParserHandler;

/**
 * This class is the central class for Kuix framework management. It pertmits to
 * load XML files, load CSS files. It contains the {@link FrameHandler} object
 * instance that manages Frames.
 * 
 * @author bbeaulant
 */
public final class Kuix {
	
	// FrameHandler
	private static final FrameHandler frameHandler = new FrameHandler();

	// List of registred styles
	private static final LinkedList registredStyles = new LinkedList();

	// The converter used to convert string representation to java object
	private static KuixConverter converter;
	
	// The KuixCanvas instance. Caution that this variable is null until initialize(KuixCanvas) invokation
	private static KuixCanvas canvas;
	
	// Parameters
	
	// Used in Screen an PopupBox widgets to determine if firstXX is on the left and then the secondXX on the right
	public static boolean firstIsLeft = true;
	
	// Alert label renderers customization
	private static ByteArrayInputStream alertOkLabelRenderer;
	private static ByteArrayInputStream alertCancelLabelRenderer;
	private static ByteArrayInputStream alertYesLabelRenderer;
	private static ByteArrayInputStream alertNoLabelRenderer;

	/**
	 * Construct a {@link Kuix}
	 */
	private Kuix() {
		// Private constructor, no need to instanciate this class.
	}
	
	/**
	 * @return the frameHandler
	 */
	public static FrameHandler getFrameHandler() {
		return frameHandler;
	}

	/**
	 * @return the converter
	 */
	public static KuixConverter getConverter() {
		return converter;
	}

	/**
	 * Returns the {@link KuixCanvas} unique instance.
	 * 
	 * @return the canvas
	 */
	public static KuixCanvas getCanvas() {
		if (canvas == null) {
			throw new IllegalArgumentException("KuixCanvas not initialized");
		}
		return canvas;
	}
	
	/**
	 * Returns the Kuix engine initialization state.
	 * 
	 * @return <code>true</code> if the engine is initialized with a KuixCanvas.
	 */
	public static boolean isInitialized() {
		return canvas != null;
	}

	/**
	 * Initialize the Kuix engine be giving the {@link KuixCanvas} object
	 * instance.
	 * 
	 * @param display the {@link Display} instance. Set this value to
	 *            <code>null</code> if you don't want the <code>canvas</code> is
	 *            displayed during this method.
	 * @param canvas
	 * @param converter
	 */
	public static void initialize(Display display, KuixCanvas canvas, KuixConverter converter) {
		
		// The initialization process could be done only once
		if (Kuix.canvas != null) {
			throw new IllegalArgumentException("KuixCanvas could be defined only once");
		}
		
		// Starts the Worker if not running
		if (!Worker.instance.isRunning()) {
			Worker.instance.start();
		}
		
		// Store canvas and converter
		Kuix.canvas = canvas;
		Kuix.converter = converter == null ? new KuixConverter() : converter;
		
		// Set canvas as current
		if (display != null) {
			display.setCurrent(canvas);
		}
		
		// Initialize the new KuixCanvas instance
		canvas.initialize();
		
	}

	/**
	 * Clean all instances
	 */
	public static void cleanUp() {
		frameHandler.removeAllFrames();
		removeAllStyles();
		canvas = null;
		converter = null;
	}
	
	// Customization ////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Cutomize Kuix alert labels.
	 * 
	 * @param okLabelRenderer the renderer (xml input stream) used as ok label
	 * @param cancelLabelRenderer the renderer (xml input stream)  used as cancel label
	 * @param yesLabelRenderer the renderer (xml input stream)  used as yes label
	 * @param noLabelRenderer the renderer (xml input stream)  used as no label
	 */
	public static void customizeAlertLabels(ByteArrayInputStream okLabelRenderer, ByteArrayInputStream cancelLabelRenderer, ByteArrayInputStream yesLabelRenderer, ByteArrayInputStream noLabelRenderer) {
		alertOkLabelRenderer = okLabelRenderer;
		alertCancelLabelRenderer = cancelLabelRenderer;
		alertYesLabelRenderer = yesLabelRenderer;
		alertNoLabelRenderer = noLabelRenderer;
	}
	
	/**
	 * Cutomize Kuix screen menu labels.
	 * 
	 * @param selectLabelRenderer the renderer (xml input stream) used as select label
	 * @param cancelLabelRenderer the renderer (xml input stream) used as cancel label
	 */
	public static void customizeScreenMenuLabels(ByteArrayInputStream selectLabelRenderer, ByteArrayInputStream cancelLabelRenderer) {
		Screen.customizeScreenMenuLabels(selectLabelRenderer, cancelLabelRenderer);
	}
	
	// PopupBox ////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Create and display a {@link PopupBox} from its XML definition.
	 * 
	 * @param xmlFilePath
	 * @param dataProvider
	 * @return The {@link PopupBox} instance
	 */
	public static PopupBox showPopupBox(String xmlFilePath, DataProvider dataProvider) {
		return showPopupBox(getXmlResourceInputStream(xmlFilePath), dataProvider);
	}

	/**
	 * Create and display a {@link PopupBox} from its XML definition.
	 * 
	 * @param inputStream
	 * @param dataProvider
	 * @return The {@link PopupBox} instance
	 */
	public static PopupBox showPopupBox(InputStream inputStream, DataProvider dataProvider) {
		if (Kuix.getCanvas() != null) {
		
			// Create popupBox and load its xml definition
			PopupBox popupBox = new PopupBox();
			parseXml(popupBox, inputStream, dataProvider, true);
			
			// Add popupBox to desktop
			Kuix.getCanvas().getDesktop().addPopup(popupBox);
			
			return popupBox;
		}
		return null;
	}
	
	/**
 	 * Create and display a {@link PopupBox}.
 	 * This method is a full feature of all {@link PopupBox} helpers like alert, splash.
 	 * 
 	 * @param styleClass The {@link PopupBox} style class
	 * @param duration the duration of the {@link PopupBox}
	 * @param content the content could be a {@link Widget} or a {@link String}
	 * @param firstLabel the label or widget of the first button
	 * @param firstAction action of the first button
	 * @param secondLabel the label or widget of the second button
	 * @param secondAction action of the second button
	 * @param buttonOnActions The ordred buttons onAction
	 * @return The {@link PopupBox} instance
	 */
	public static PopupBox showPopupBox(String styleClass, int duration, Object content, Object firstLabel, String firstAction, Object secondLabel, String secondAction, String onCloseAction) {
		if (Kuix.getCanvas() != null) {
			
			// Construct the PopupBox
			PopupBox popupBox = new PopupBox();
			popupBox.setStyleClass(styleClass);
			popupBox.setDuration(duration);
			popupBox.setContent(content);
			popupBox.setOnAction(onCloseAction);
			
			if (firstLabel != null) {
				MenuItem firstMenuItem = popupBox.getFirstMenuItem();
				if (firstLabel instanceof ByteArrayInputStream) {
					((ByteArrayInputStream) firstLabel).reset();
					firstMenuItem.add(Kuix.loadWidget((ByteArrayInputStream) firstLabel, null));
				} else if (firstLabel instanceof Widget) {
					firstMenuItem.add((Widget) firstLabel);
				} else if (firstLabel instanceof String) {
					firstMenuItem.add(new Text().setText((String) firstLabel));
				}
				firstMenuItem.setOnAction(firstAction);
			}
			
			if (secondLabel != null) {
				MenuItem secondMenuItem = popupBox.getSecondMenuItem();
				if (firstLabel instanceof ByteArrayInputStream) {
					((ByteArrayInputStream) secondLabel).reset();
					secondMenuItem.add(Kuix.loadWidget((ByteArrayInputStream) secondLabel, null));
				} else if (secondLabel instanceof Widget) {
					secondMenuItem.add((Widget) secondLabel);
				} else if (secondLabel instanceof String) {
					secondMenuItem.add(new Text().setText((String) secondLabel));
				}
				secondMenuItem.setOnAction(secondAction);
			}
			
			// Add popupBox to desktop
			Kuix.getCanvas().getDesktop().addPopup(popupBox);
			
			return popupBox;
		}
		return null;
	}

	// Splash ////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Display a splash {@link PopupBox}
	 * 
	 * @param duration the duration of the splash (in ms)
	 * @param content the splash widget content
	 * @return The {@link PopupBox} instance
	 */
	public static PopupBox splash(int duration, Widget content, String onCloseAction) {
		return showPopupBox(KuixConstants.SPLASH_STYLE_CLASS, duration, content, null, null, null, null, onCloseAction);
	}
	
	// Alert ////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Create an open an alert box. An alert can only display text content. It
	 * is usful to display simple text message, error, ask question, etc...<br>
	 * The associated buttons are construct from the <code>options</code>
	 * parameter.
	 * <p>
	 * Example:
	 * </p>
	 * 
	 * <pre>
	 * alert(&quot;Hello world&quot;, KuixConstants.ALERT_OK | KuixConstants.ALERT_INFO, &quot;doOk&quot;, null, null, null);
	 * alert(&quot;Is it rainning ?&quot;, KuixConstants.ALERT_YES | KuixConstants.ALERT_NO | KuixConstants.ALERT_QUESTION, null, null, &quot;doYes&quot;, &quot;doNo&quot;);
	 * </pre>
	 * 
	 * <b>Caution :</b><br>
	 * - <code>KuixConstants.ALERT_OK</code> and <code>KuixConstants.ALERT_YES</code> are always placed on the first menuItem and then couldn't be displaye together.<br>
	 * - <code>KuixConstants.ALERT_CANCEL</code> and <code>KuixConstants.ALERT_NO</code> are always placed on the second menuItem and then couldn't be displaye together.<br>
	 * 
	 * @param message the text message to display
	 * @param options the options {@see KuixConstants}
	 * @param okAction the ok onAction name
	 * @param yesAction the yes onAction name
	 * @param noAction the no onAction name
	 * @param cancelAction the cancel onAction name
	 * @return The {@link PopupBox} instance
	 */
	public static PopupBox alert(String message, int options, String firstAction, String secondAction) {
		
		// Determine alert style class
		String styleClass = KuixConstants.ALERT_DEFAULT_STYLE_CLASS;
		if ((options & KuixConstants.ALERT_DEBUG) == KuixConstants.ALERT_DEBUG) {
			styleClass = KuixConstants.ALERT_DEBUG_STYLE_CLASS;
		}
		if ((options & KuixConstants.ALERT_INFO) == KuixConstants.ALERT_INFO) {
			styleClass = KuixConstants.ALERT_INFO_STYLE_CLASS;
		}
		if ((options & KuixConstants.ALERT_WARNING) == KuixConstants.ALERT_WARNING) {
			styleClass = KuixConstants.ALERT_WARNING_STYLE_CLASS;
		}
		if ((options & KuixConstants.ALERT_ERROR) == KuixConstants.ALERT_ERROR) {
			styleClass = KuixConstants.ALERT_ERROR_STYLE_CLASS;
		}
		if ((options & KuixConstants.ALERT_QUESTION) == KuixConstants.ALERT_QUESTION) {
			styleClass = KuixConstants.ALERT_QUESTION_STYLE_CLASS;
		}
		
		// Extract first and second buttons labels
		Object firstLabel = null;
		Object secondLabel = null;
		if ((options & KuixConstants.ALERT_NO_BUTTON) != KuixConstants.ALERT_NO_BUTTON) {
			
			// First menuItem : OK or Yes
			if ((options & KuixConstants.ALERT_OK) == KuixConstants.ALERT_OK || (options & KuixConstants.ALERT_YES) != KuixConstants.ALERT_YES) {
				if (alertOkLabelRenderer != null) {
					firstLabel = alertOkLabelRenderer;
				} else {
					firstLabel = Kuix.getMessage(KuixConstants.OK_I18N_KEY);
				}
			} else if ((options & KuixConstants.ALERT_YES) == KuixConstants.ALERT_YES) {
				if (alertYesLabelRenderer != null) {
					firstLabel = alertYesLabelRenderer;
				} else {
					firstLabel = Kuix.getMessage(KuixConstants.YES_I18N_KEY);
				}
			}
			
			// Second menuItem : Cancel or No
			if ((options & KuixConstants.ALERT_CANCEL) == KuixConstants.ALERT_CANCEL) {
				if (alertCancelLabelRenderer != null) {
					secondLabel = alertCancelLabelRenderer;
				} else {
					secondLabel = Kuix.getMessage(KuixConstants.CANCEL_I18N_KEY);
				}
			} else if ((options & KuixConstants.ALERT_NO) == KuixConstants.ALERT_NO) {
				if (alertNoLabelRenderer != null) {
					secondLabel = alertNoLabelRenderer;
				} else {
					secondLabel = Kuix.getMessage(KuixConstants.NO_I18N_KEY);
				}
			}
			
		}
		
		// Prepare the alert box
		PopupBox popupBox = showPopupBox(styleClass, -1, message, firstLabel, firstAction, secondLabel, secondAction, null);
		if (popupBox == null) {
			System.out.println(message);
		}
		return popupBox;
		
	}
	
	/**
	 * Open an alert box with options. This alert is a {@link PopupBox} with a
	 * single text message an single OK button. If you try to use other buttons
	 * with <code>options</code>, they will be ignored.
	 * 
	 * @param message the message to display
	 * @param options {@see KuixConstants}
	 * @return The {@link PopupBox} instance
	 */
	public static PopupBox alert(String message, int options) {
		return alert(	message, 
						options, 
						null,
						null);
	}
	
	/**
	 * Open an alert box with the message text and default style class.
	 * 
	 * @param message the message to display
	 * @return The {@link PopupBox} instance
	 */
	public static PopupBox alert(String message) {
		return alert(message, KuixConstants.ALERT_DEFAULT);
	}
	
	/**
	 * Open an alert box with the {@link Throwable} object message and 'alerterror'
	 * style class.
	 * 
	 * @param message the message to display
	 * @param throwable the {@link Throwable} to get message or class name
	 * @return The {@link PopupBox} instance
	 */
	public static PopupBox alert(String message, Throwable throwable) {
		
		// Print stack trace for debug
		if (throwable != null) {
			throwable.printStackTrace();
		}
		
		return alert(composeAltertMessage(message, throwable), KuixConstants.ALERT_ERROR);
	}
	
	/**
	 * Open an alert box with the {@link Throwable} object message and
	 * 'alerterror' style class.
	 * 
	 * @param throwable the {@link Throwable} to get message or class name
	 * @return The {@link PopupBox} instance
	 */
	public static PopupBox alert(Throwable throwable) {
		return alert(null, throwable);
	}
	
	/**
	 * Compose an alert message by using the given <code>message</code> and
	 * <code>throwable</code>.
	 * 
	 * @param message
	 * @param throwable
	 * @return the composed String.
	 */
	protected static String composeAltertMessage(String message, Throwable throwable) {
		StringBuffer buffer = new StringBuffer();
		if (message != null) {
			buffer.append(message);
			if (throwable != null) {
				buffer.append(" : ");
			}
		}
		if (throwable != null) {
			if (throwable.getMessage() != null) {
				buffer.append(throwable.getMessage());
			} else {
				buffer.append(throwable.getClass().getName());
			}
		}
		return buffer.toString();
	}

	// Screen ////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Load a {@link Screen} widget from a XML file. If <code>xmlFilePath</code>
	 * is a relative path (i.e: <code>myScreen.xml</code>) the default xml
	 * folder location is automaticaly added and the path become :
	 * <code>/xml/myScreen.xml</code>. Absolute paths are kept.
	 * 
	 * @param xmlFilePath
	 * @param dataProvider
	 * @return The loaded {@link Screen} widget instance
	 */
	public static Screen loadScreen(String xmlFilePath, DataProvider dataProvider) {
		return loadScreen(getXmlResourceInputStream(xmlFilePath), dataProvider);
	}
	
	/**
	 * Load a {@link Screen} widget from an XML {@link InputStream}.
	 * 
	 * @param inputStream
	 * @param dataProvider
	 * @return The loaded {@link Screen} widget instance
	 */
	public static Screen loadScreen(InputStream inputStream, DataProvider dataProvider) {
		Screen screen = new Screen();
		loadXml(screen, inputStream, dataProvider, true, true);
		return screen;
	}
	
	// Widget ////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Load a {@link Widget} from a XML file. If <code>xmlFilePath</code> is a
	 * relative path (i.e: <code>myWidget.xml</code>) the default xml folder
	 * location is automaticaly added and the path become :
	 * <code>/xml/myWidget.xml</code>. Absolute paths are kept.
	 * 
	 * @param xmlFilePath
	 * @param dataProvider
	 * @return The loaded {@link Widget} instance
	 */
	public static Widget loadWidget(String xmlFilePath, DataProvider dataProvider) {
		return loadWidget(getXmlResourceInputStream(xmlFilePath), dataProvider);
	}
	
	/**
	 * Load a {@link Widget} from an XML {@link InputStream}.<br>
	 * The <code>desiredWidgetClass</code> need to extends {@link Widget} and
	 * correspond to the root xml widget tag.
	 * 
	 * @param inputStream
	 * @param dataProvider
	 * @return The loaded {@link Widget} instance
	 */
	public static Widget loadWidget(InputStream inputStream, DataProvider dataProvider) {
		return parseXml(null, inputStream, dataProvider, false);
	}
	
	// Menu ////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Reload the <code>menu</code> content from an xml definition.<br>
	 * Caution that this method <code>cleanUp</code> and <code>removeAll</code>
	 * previous menu content, but not its attributes (like <code>id</code> or
	 * <code>class</code>).
	 * 
	 * @param menu
	 * @param xmlFilePath
	 * @param dataProvider
	 * @since 1.0.1
	 */
	public static void loadMenuContent(Menu menu, String xmlFilePath, DataProvider dataProvider) {
		loadMenuContent(menu, getXmlResourceInputStream(xmlFilePath), dataProvider);
	}

	/**
	 * Reload the first menu content from an xml definition provides by an
	 * inputStream.<br>
	 * Caution that this method <code>cleanUp</code> and <code>removeAll</code>
	 * previous menu content, but not its attributes (like <code>id</code> or
	 * <code>class</code>).
	 * 
	 * @param menu
	 * @param inputStream
	 * @param dataProvider
	 * @since 1.0.1
	 */
	public static void loadMenuContent(Menu menu, InputStream inputStream, DataProvider dataProvider) {
		if (menu != null) {
			menu.hideMenuTree();
			menu.cleanUp();
			menu.removeAll();
		}
		Kuix.loadXml(menu, inputStream, dataProvider);
	}
	
	// XML ////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Parse and load an XML UI definition and place the content as child of
	 * <code>rootWidget</code>. Initial <code>rootWidget</code> content is removed.
	 * 
	 * @param rootWidget
	 * @param inputStream
	 * @throws Exception
	 */
	public static void loadXml(Widget rootWidget, InputStream inputStream) {
		loadXml(rootWidget, inputStream, null, false, true);
	}

	/**
	 * Parse and load an XML UI definition and place the content as child of
	 * <code>rootWidget</code>. Initial <code>rootWidget</code> content is removed.
	 * 
	 * @param rootWidget
	 * @param inputStream
	 * @param dataProvider
	 * @throws Exception
	 */
	public static void loadXml(Widget rootWidget, InputStream inputStream, DataProvider dataProvider) {
		loadXml(rootWidget, inputStream, dataProvider, false, true);
	}

	/**
	 * Parse an load an XML ui definition and place the content as child of
	 * <code>rootWidget</code>.
	 * 
	 * @param rootWidget
	 * @param inputStream
	 * @param dataProvider
	 * @param append if <code>false</code> loaded content replace current
	 *            <code>rootWidget</code>'s content.
	 * @param mergeRootWidget if <code>true</code> and if loaded content's root
	 *            widget tag equals <code>rootWidget</code>'s tag the given
	 *            <code>rootWidget</code> instance represents the new content
	 *            root else a new widget instance is created and added to the
	 *            <code>rootWidget</code>.
	 * @throws Exception
	 */
	public static void loadXml(Widget rootWidget, InputStream inputStream, DataProvider dataProvider, boolean append, boolean mergeRootWidget) {
		if (!append) {
			rootWidget.removeAll();
		}
		parseXml(rootWidget, inputStream, dataProvider, mergeRootWidget);
		rootWidget.invalidate();
	}

	// CSS ////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Parse an load a CSS style definitions and register it into the
	 * StyleManager. If <code>cssFilePath</code> is a relative path (i.e:
	 * <code>myStyle.css</code>) the default css folder location is
	 * automaticaly added and the path become : <code>/css/myStyle.css</code>.
	 * Absolute paths are kept.
	 * 
	 * @param cssFilePath
	 */
	public static void loadCss(String cssFilePath) {
		if (cssFilePath != null) {
			if (!cssFilePath.startsWith("/")) {
				cssFilePath = new StringBuffer(KuixConstants.DEFAULT_CSS_RES_FOLDER).append(cssFilePath).toString();
			}
			// Use frameHandler.getClass() because of a Object.class bug
			InputStream inputStream = frameHandler.getClass().getResourceAsStream(cssFilePath);
			if (inputStream != null) {
				loadCss(inputStream);
				return;
			}
		}
		throw new IllegalArgumentException("Unknow cssFilePath : " + cssFilePath);
	}
	
	/**
	 * Parse an load a CSS style definitions and register them.
	 * 
	 * @param inputStream
	 */
	public static void loadCss(InputStream inputStream) {
		parseCss(inputStream);
		if (canvas != null) {
			// Clear all style caches to use new loaded styles
			clearStyleCache(canvas.getDesktop(), true);
		}
	}

	/**
	 * Call the specified action method
	 * 
	 * @param method
	 */
	public static void callActionMethod(Method method) {
		if (method != null) {
			if (!frameHandler.processMessage(method.getName(), method.getArguments())) {
				
				// Default KUIX actions
				//////////////////////////////////////////////////////////////////////
				
				// Exit (exits the application)
				if (KuixConstants.EXIT_ACTION.equals(method.getName())) {
					if (canvas != null) {
						canvas.getInitializer().destroyImpl();
					}
				}
				
			}
		}
	}

	/**
	 * Returns a new {@link InputStream} relative to the desired
	 * <code>xmlFilePath</code>.<br>
	 * If <code>xmlFilePath</code> is a relative path (i.e:
	 * <code>myResource.xml</code>) the default xml folder location is
	 * automaticaly added and the path become : <code>/xml/myResource.xml</code>.
	 * Absolute paths are kept.
	 * 
	 * @param xmlFilePath
	 * @return a new {@link InputStream} relative to the desired xml resource
	 */
	public static InputStream getXmlResourceInputStream(String xmlFilePath) {
		if (xmlFilePath != null) {
			if (!xmlFilePath.startsWith("/")) {
				xmlFilePath = new StringBuffer(KuixConstants.DEFAULT_XML_RES_FOLDER).append(xmlFilePath).toString();
			}
			// Use frameHandler.getClass() because of a Object.class bug
			InputStream inputStream = frameHandler.getClass().getResourceAsStream(xmlFilePath);
			if (inputStream != null) {
				return inputStream;
			}
		}
		throw new IllegalArgumentException("Unknow xmlFilePath : " + xmlFilePath);
	}

	/**
	 * Convert resource to a ByteArrayInputStream.
	 * 
	 * @param clazz The {@link Class} where the <code>getResourceAsStream()</code> function is called.
	 * @param path Path of the resource file
	 * @return The corresponding {@link ByteArrayInputStream}, or <code>null</code> if an error occure.
	 */
	public static ByteArrayInputStream getResourceAsByteArrayInputStream(Class clazz, String path) {
		if (path != null) {
			InputStream resourceInputStream = clazz.getResourceAsStream(path);
			byte[] resourceData = null;
			try {
				resourceData = new byte[resourceInputStream.available()];
				resourceInputStream.read(resourceData);
			} catch (IOException e) {
			}
			if (resourceData != null) {
				return new ByteArrayInputStream(resourceData);
			}
		}
		return null;
	}

	// Parser ////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Parse the XML <code>inputStream</code> to build the corresponding
	 * widget tree.
	 * 
	 * @param rootWidget
	 * @param inputStream
	 * @param dataProvider
	 * @param mergeRootWidget
	 * @throws Exception
	 */
	private static Widget parseXml(final Widget rootWidget, InputStream inputStream, final DataProvider dataProvider, final boolean mergeRootWidget) {
		if (inputStream != null) {
			
			// Init the root holder (used if no root widget is defined)
			final Widget[] rootWidgetHolder = (rootWidget == null) ? new Widget[1] : null;
			
			try {
				LightXmlParser.parse(inputStream, KuixConstants.DEFAULT_CHARSET_NAME, new LightXmlParserHandler() {

					private final Stack path = new Stack();
					private final Stack internalWidgets = new Stack();
					
					private Widget currentWidget = rootWidget;
					private String currentAttribute = null;
					
					/* (non-Javadoc)
					 * @see com.kalmeo.util.xml.DefaultHandler#startDocument()
					 */
					public void startDocument() {
					}

					/* (non-Javadoc)
					 * @see com.kalmeo.util.xml.DefaultHandler#startElement(java.lang.String, java.util.Hashtable)
					 */
					public void startElement(String name, Hashtable attributes) {

						// Currently reading inline attribute value, startElement is not allowed
						if (currentAttribute != null) {
							throw new IllegalArgumentException("Attribute tag (" + currentAttribute + ") can't enclose an other tag");
						}
						
						String tag = name.toLowerCase();
						if (tag.startsWith("_")) {
							// Use tag as current widget's attribute
							currentAttribute = tag.substring(1);
						} else {
							// Use tag as new widget
							
							// Create widget
							Widget newWidget = null;
							if (path.isEmpty() && mergeRootWidget && rootWidget != null) {
								if (tag.equals(rootWidget.getTag())) {
									newWidget = rootWidget;
									rootWidget.clearCachedStyle(true);
								} else {
									// Case when mergeRootWidget is true and the real rootWidget != xml root widget
									path.push(rootWidget);
								}
							} 
							if (newWidget == null) {
								if (currentWidget != null) {
									// Try to retrieve an internal instance
									newWidget = currentWidget.getInternalChildInstance(tag);
								}
								if (newWidget == null) {
									// Try to create a new widget instance
									newWidget = converter.convertWidgetTag(tag);
								} else {
									// The widget is internal, push it in the internalWidget stack
									internalWidgets.push(newWidget);
								}
								if (newWidget == null && attributes != null && attributes.containsKey(KuixConstants.PACKAGE_ATTRIBUTE)) {

									// Try to construct a custom widget
									
									// Extract package attribute and construct class name
									String packageName = (String) attributes.get(KuixConstants.PACKAGE_ATTRIBUTE);
									String className = new StringBuffer(packageName).append('.').append(name).toString();	// Use name instead of tag because of Class.forName is case sensitive

									// Create a new custom widget instance
									Object customWidgetInstance = null;
										try {
											customWidgetInstance = Class.forName(className).newInstance();
										} catch (ClassNotFoundException e) {
											throw new IllegalArgumentException("Custom widget not found : " + className);
										} catch (Exception e) {
											e.printStackTrace();
											throw new IllegalArgumentException("Custom widget creation exceptiond : " + className);
										}
									if (customWidgetInstance instanceof Widget) {
										newWidget = (Widget) customWidgetInstance;
									} else {
										throw new IllegalArgumentException("Invalid custom widget : " + className);
									}

									// Remove package attribute to continue
									attributes.remove(KuixConstants.PACKAGE_ATTRIBUTE);

								}
								if (newWidget == null) {
									throw new IllegalArgumentException("Unknow tag : " + tag);
								}
							}
							path.push(newWidget);

							// Extract attributes
							if (attributes != null) {
								String attributeName;
								String attributeValue;
								Enumeration enumeration = attributes.keys();
								while (enumeration.hasMoreElements()) {
									String key = (String) enumeration.nextElement();
									attributeName = key.toLowerCase();
									attributeValue = convertParsePropertyStringValues((String) attributes.get(key));
									if (!newWidget.setAttribute(attributeName, attributeValue)) {
										throw new IllegalArgumentException("Unknow attribute : " + attributeName);
									}
								}
							}

							if (currentWidget == null && rootWidgetHolder != null) {
								rootWidgetHolder[0] = newWidget;
							}
							currentWidget = newWidget;
							
						}
						
					}
					
					/* (non-Javadoc)
					 * @see org.kalmeo.util.xml.LightXmlParserHandler#characters(java.lang.String, boolean)
					 */
					public void characters(String characters, boolean isCDATA) {
						if (characters.trim().length() > 0 && currentWidget != null) {
							
							String usedAttribute = currentAttribute;
							Widget usedWidget = currentWidget;
							
							// Check #inc statment
							if (characters.startsWith(KuixConstants.INCLUDE_KEYWORD_PATTERN)) {
								String fileName = null;
								String dataProviderProperty = null;
								boolean mergeRootWidgetParameter = true;
								String rawParams = StringUtil.extractRawParams(KuixConstants.INCLUDE_KEYWORD_PATTERN, characters);
								if (rawParams != null) {
									StringTokenizer st = new StringTokenizer(rawParams, ",");
									if (st.hasMoreElements()) {
										
										// Extract parameters (File name accept parse properties) / empty file name are ignored
										fileName = convertParsePropertyStringValues(st.nextToken().trim());
										if (fileName.length() != 0) {
											
											// DataProvider ?
											if (st.countTokens() >= 1) {
												dataProviderProperty = st.nextToken().trim();
												if (KuixConstants.NULL_KEYWORD.equals(dataProviderProperty)) {
													dataProviderProperty = null;
												}
											}
											
											// Merge root widget ?
											if (st.countTokens() >= 1) {
												mergeRootWidgetParameter = BooleanUtil.parseBoolean(st.nextToken().trim());
											}
											
											// Retrieve pointed resource
											InputStream inputStream = getXmlResourceInputStream(fileName);
											if (inputStream != null) {
												try {
													if (usedAttribute != null) {
														
														// Attribute value, then the file content is returned as a String
														byte[] rawData = new byte[inputStream.available()];
														inputStream.read(rawData);
														characters = new String(rawData);
														
													} else {
														
														// Parse property variable to define a new dataProvider ?
														DataProvider includeDataProvider = dataProvider;
														if (dataProviderProperty != null && dataProvider != null) {
															if (dataProviderProperty.startsWith(KuixConstants.PARSE_PROPERTY_START_PATTERN)) {
																String property = dataProviderProperty.substring(KuixConstants.PARSE_PROPERTY_START_PATTERN.length(), dataProviderProperty.length() - KuixConstants.PROPERTY_END_PATTERN.length());
																Object value = dataProvider.getValue(property);
																if (value instanceof DataProvider) {
																	includeDataProvider = (DataProvider) value;
																} else {
																	throw new IllegalArgumentException("#inc accept only DataProvider property value");
																}
															} else {
																throw new IllegalArgumentException("#inc accept only parse property");
															}
														}
														
														// Default include: file content is parsed and added to current widget
														parseXml(currentWidget, inputStream, includeDataProvider, mergeRootWidgetParameter);
														return;
														
													}
												} catch (IOException e) {
													throw new IllegalArgumentException("Invalid include file : " + fileName);
												}
											} else {
												throw new IllegalArgumentException("Include file not found : " + fileName);
											}
											
										}
									}
								}
							}
							
							// If no attribute is defined
							boolean defaultTextWidget = false;
							if (usedAttribute == null) {
								if (currentWidget instanceof TextWidget) {
									usedAttribute = KuixConstants.TEXT_ATTRIBUTE;
								} else if (currentWidget instanceof Picture) {
									usedAttribute = KuixConstants.SRC_ATTRIBUTE;
								} else {
									usedAttribute = KuixConstants.TEXT_ATTRIBUTE;
									usedWidget = new Text();
									currentWidget.add(usedWidget);
									defaultTextWidget = true;
								}
							}
							
							if (usedWidget.isObjectAttribute(usedAttribute)) {
								
								// The attribute need an Object value : only a single property variable is allowed
								if (!isCDATA && characters.endsWith(KuixConstants.PROPERTY_END_PATTERN)) {
									String property;
									
									// Parse property variable ?
									if (dataProvider != null && characters.startsWith(KuixConstants.PARSE_PROPERTY_START_PATTERN)) {
										property = characters.substring(KuixConstants.PARSE_PROPERTY_START_PATTERN.length(), characters.length() - KuixConstants.PROPERTY_END_PATTERN.length());
										Object value = dataProvider.getValue(property);
										if (usedWidget.setObjectAttribute(usedAttribute, value)) {
											return;
										}
									}
									
									// Bind property variable ?
									if (characters.startsWith(KuixConstants.BIND_PROPERTY_START_PATTERN)) {
										property = characters.substring(KuixConstants.BIND_PROPERTY_START_PATTERN.length(), characters.length() - KuixConstants.PROPERTY_END_PATTERN.length());
										usedWidget.setAttributeBindInstruction(usedAttribute, new String[] { property }, null);
										return;
									}
									
								}
								throw new IllegalArgumentException("Bad attribute value : " + usedAttribute);
								
							} else {
								
								if (!isCDATA) {
									
									// Convert parse property variables to their string values
									characters = convertParsePropertyStringValues(characters.trim());
									
									// Extract possible bind properties
									String[] properties = extractBindProperties(characters);
									if (properties != null) {
										usedWidget.setAttributeBindInstruction(usedAttribute, properties, characters);
										// Special case for default text widget
										if (defaultTextWidget && dataProvider != null) {
											dataProvider.bind(usedWidget);
										}
										return;
									} else {
										characters = processI18nPattern(characters);
									}
									
								}
								
								// Set attribute value
								if (!usedWidget.setAttribute(usedAttribute, characters)) {
									throw new IllegalArgumentException(usedAttribute);
								}
								
							}
							
						}
					}

					/* (non-Javadoc)
					 * @see com.kalmeo.util.xml.DefaultHandler#endElement(java.lang.String)
					 */
					public void endElement(String name) {
						if (name.startsWith("_")) {
							// Use name as current widget's attribute tag
							currentAttribute = null;
						} else {
							
							// Check widget binds
							if (currentWidget != null && dataProvider != null && currentWidget.hasBindInstruction()) {
								dataProvider.bind(currentWidget);
							}
							
							// Go backward in the path
							path.pop();
							
							// Add current widget to its parent. (The widget is added after all attribute definitions to be sure to be complete.)
							Widget parentWidget = path.isEmpty() ? (mergeRootWidget ? null : rootWidget) : (Widget) path.lastElement();
							boolean internal = !internalWidgets.isEmpty() && internalWidgets.lastElement() == currentWidget;
							if (internal) {
								internalWidgets.pop();
							}
							if (parentWidget != null && currentWidget != null && !internal) {
								parentWidget.add(currentWidget);
							}
							
							// Set current widget to its "parent"
							currentWidget = parentWidget;
							
						}
					}

					/* (non-Javadoc)
					 * @see com.kalmeo.util.xml.DefaultHandler#endDocument()
					 */
					public void endDocument() {
						path.removeAllElements();
						internalWidgets.removeAllElements();
						currentWidget = null;
						currentAttribute = null;
					}
					
					/**
					 * Convert the parse property variables and replace them by their
					 * string values.
					 * <p>Syntax : <code>${varName[|Null text]}</code></p>
					 * <p>Example with var1="Hello" and var2=null</p>
					 * <p><code>${var1}</code> is transform to <code>Hello</code></p>
					 * <p><code>${var1|Nothing}</code> is transform to <code>Hello</code></p>
					 * <p><code>${var2|Nothing}</code> is transform to <code>Nothing</code></p>
					 * 
					 * @param rawData
					 * @param propertyProvider
					 * @return The processed String
					 */
					private String convertParsePropertyStringValues(String rawData) {
						int posStart = rawData.indexOf(KuixConstants.PARSE_PROPERTY_START_PATTERN);
						if (posStart != -1) {
							int posEnd = rawData.indexOf(KuixConstants.PROPERTY_END_PATTERN, posStart);
							if (posEnd != -1) {
								StringBuffer buffer = new StringBuffer(rawData.substring(0, posStart));
								String property = rawData.substring(posStart + 2, posEnd);
								String propertyValue = null;
								int posPipe = property.indexOf(KuixConstants.PROPERTY_ALTERNATIVE_SEPRATOR_PATTERN);
								if (posPipe != -1) {
									if (dataProvider != null) {
										propertyValue = dataProvider.getStringValue(property.substring(0, posPipe));
									}
									if (propertyValue == null) {
										propertyValue = property.substring(posPipe + 1);
									}
								} else if (dataProvider != null) {
									propertyValue = dataProvider.getStringValue(property);
								}
								if (propertyValue != null) {
									buffer.append(propertyValue);
								}
								return buffer.append(convertParsePropertyStringValues(rawData.substring(posEnd + 1))).toString();
							}
						}
						return rawData;
					}
					
					/**
					 * Extract a bind properties list.
					 * 
					 * @param rawData
					 * @return a list of all extracted bind properties or
					 *         <code>null</code> if not bind property is defined
					 *         in the input String.
					 */
					private String[] extractBindProperties(String rawData) {
						Vector properties = null;
						int posStart = 0;
						int posEnd = 0;
						while (true) {
							posStart = rawData.indexOf(KuixConstants.BIND_PROPERTY_START_PATTERN, posEnd);
							if (posStart != -1) {
								posEnd = rawData.indexOf(KuixConstants.PROPERTY_END_PATTERN, posStart);
								if (posEnd != -1) {
									if (properties == null) {
										 properties = new Vector();
									}
									String propertyDefinition = rawData.substring(posStart + 2, posEnd);
									int posPipe = propertyDefinition.indexOf(KuixConstants.PROPERTY_ALTERNATIVE_SEPRATOR_PATTERN);
									if (posPipe == -1) {
										properties.addElement(propertyDefinition);
									} else {
										properties.addElement(propertyDefinition.substring(0, posPipe));
									}
								} else {
									break;
								}
							} else {
								break;
							}
						}
						if (properties != null) {
							String[] propertiesArray = new String[properties.size()];
							properties.copyInto(propertiesArray);
							return propertiesArray;
						}
						return null;
					}

				});
				
				return (rootWidgetHolder == null) ? rootWidget : rootWidgetHolder[0];
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		throw new IllegalArgumentException("Invalid xml inputStream");
	}

	/**
	 * Parse a CSS <code>inputStream</code> and register extracted {@link Style} to the
	 * styles list.
	 * 
	 * @param inputStream
	 * @throws IOException
	 */
	private static void parseCss(InputStream inputStream) {
		if (inputStream != null) {
			Reader reader = new InputStreamReader(inputStream);
			try {
	
				boolean selectorsCapture = true;
				boolean commentCapture = false;
				
				StringBuffer rawSelectors = new StringBuffer();
				StringBuffer rawDefinitions = new StringBuffer();
	
				for (int c = reader.read(); c != -1;) {
	
					if (commentCapture) {
						if (c == '*') {
							if ((c = reader.read()) == '/') {
								commentCapture = false;
							} else {
								continue;
							}
						}
					} else {
						
						if (c == '*') {
							throw new IllegalArgumentException("Invalid css comment block");
						}
	
						if (c == '/') {
							if ((c = reader.read()) == '*') {
								commentCapture = true;
								c = reader.read();
								continue;
							} else {
								if (selectorsCapture) {
									rawSelectors.append('/');
								} else {
									rawDefinitions.append('/');
								}
							}
						}
	
						if (selectorsCapture) {
							if (c == '{') {
	
								// Switch to definition capture
								selectorsCapture = false;
	
							} else {
								rawSelectors.append((char) c);
							}
						} else {
							if (c == '}') {
	
								// Create the Style sheet from raw data
								Style[] styles = converter.convertStyleSheets(rawSelectors.toString(), rawDefinitions.toString());
								for (int i = 0; i < styles.length; ++i) {
									registerStyle(styles[i]);
								}
	
								// Clear StringBuffers
								rawSelectors.delete(0, rawSelectors.length());
								rawDefinitions.delete(0, rawDefinitions.length());
	
								// Switch to selectors capture
								selectorsCapture = true;
	
							} else {
								rawDefinitions.append((char) c);
							}
						}
	
					}
					c = reader.read();
	
				}
				
				if (commentCapture) {
					throw new IllegalArgumentException("CSS : Invalide comment block");
				}
				
				if (!selectorsCapture) {
					throw new IllegalArgumentException("CSS : Invalid selector block");
				}
				
				return;
				
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		throw new IllegalArgumentException("Invalid css inputStream");
	}

	/**
	 * Returns the parsed {@link Method}, or null if no method could be extract.
	 * 
	 * @param data
	 * @param owner
	 * @return The parsed {@link Method}
	 */
	public static Method parseMethod(String data, Widget owner) {
		if (data.length() == 0) {
			return null;
		}
		StringTokenizer st = new StringTokenizer(data, "(,)");
		String methodName = st.nextToken().trim();
		if (methodName != null) {

			// Create the method instance
			Method method = new Method(methodName);

			// Extract arguments
			Object[] arguments = new Object[st.countTokens()];
			int i = 0;
			while (st.hasMoreTokens()) {

				// Extract RAW argument
				String argumentRawValue = st.nextToken().trim();

				// Try to convert a widget path argument (ex: #widget_one_id.#widget_two_id.attribute)
				Object argumentValue = null;
				boolean isString = true;
				Widget widget = null;
				String token = null;
				StringTokenizer widgetPath = new StringTokenizer(argumentRawValue, ".");
				while (widgetPath.hasMoreTokens()) {
					argumentValue = null;
					token = widgetPath.nextToken();
					if (widget == null && "this".equals(token)) {
						isString = false;
						widget = owner;
						continue;
					} else if (token != null && token.startsWith("#")) {
						isString = false;
						Widget foundWidget = null;
						if (widget == null) {
							foundWidget = canvas != null ? canvas.getDesktop().getWidget(token.substring(1)) : null;
						} else {
							foundWidget = widget.getWidget(token.substring(1));
						}
						if (foundWidget != null) {
							widget = foundWidget;
							continue;
						}
					}
					if (widget != null) {
						Object attributeValue = widget.getAttribute(token.toLowerCase());
						if (attributeValue instanceof Widget) {
							widget = (Widget) attributeValue;
						} else {
							widget = null;
							argumentValue = attributeValue;
						}
					} else {
						break;
					}
				}
				
				if (argumentValue == null) {
					if (isString) {
						// The parameter is consider as a string
						argumentValue = argumentRawValue;
					} else if (widget != null) {
						// The parameter is consider as a widget
						argumentValue = widget;
					}
					// Else the parameter couldn't be converted : the value is null (undefined)
				}
				
				// Store converted value
				arguments[i++] = argumentValue;

			}
			method.setArguments(arguments);

			return method;
		}
		return null;
	}

	// Styles management ////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Register a {@link Style}. If an equivalent Style was already registred
	 * the new style properties are copied.
	 * 
	 * @param style
	 */
	public static void registerStyle(final Style style) {
		if (style != null) {
			Style registredStyle = (Style) registredStyles.getFirst();
			for (; registredStyle != null; registredStyle = (Style) registredStyle.getNext()) {
				if (registredStyle.getSelector().equals(style.getSelector())) {
					break;
				}
			}
			if (registredStyle != null) {
				
				// A style is already registred with the same selector, lets copy all StyleProperties into it.
				LinkedList properties = style.getProperties();
				for (StyleProperty property = (StyleProperty) properties.getFirst(); property != null; property = (StyleProperty) property.getNext()) {
					registredStyle.add(property.copy());
				}

			} else {
				registredStyles.add(style);
			}
		}
	}

	/**
	 * Returns the list of {@link Style} associated to the <code>widget</code> or
	 * <code>null</code> if no style was found.
	 * 
	 * @param widget
	 * @return The list of {@link Style} associated to the <code>widget</code>
	 */
	public static Vector getStyles(final Widget widget) {
		if (widget != null) {

			// Define the filter
			Filter filter = new Filter() {
				
				/* (non-Javadoc)
				 * @see org.kalmeo.util.Filter#accept(java.lang.Object)
				 */
				public int accept(Object obj) {

					int score = 0;
					StyleSelector styleSelector = ((Style) obj).getSelector();
					Widget currentWidget = widget;
					while (styleSelector != null) {

						String[] pseudoClasses = widget.getAvailablePseudoClasses();
						boolean isCompatible = false;
						while (currentWidget != null && !isCompatible) {

							// Id
							if (styleSelector.hasId()) {
								if (currentWidget.getId() != null && currentWidget.getId().equals(styleSelector.getId())) {
									isCompatible = true;
									score += 1000000;
								}
							}
							
							// Class
							if (!isCompatible && styleSelector.hasClass()) {
								String[] styleClasses = currentWidget.getStyleClasses();
								if (styleClasses != null) {
									int i = styleClasses.length - 1;
									for (; i >= 0; --i) {
										String styleClass = styleClasses[i];
										if (styleClass != null && styleClass.equals(styleSelector.getStyleClass())) {
											isCompatible = true;
											score += 10000;
											break;
										}
									}
								}
							}

							if (styleSelector.hasTag()) {
								
								// Tag
								if (!isCompatible && currentWidget.getTag() != null && currentWidget.getTag().equals(styleSelector.getTag())) {
									isCompatible = true;
									score += 100;
								}
								
								// Inherited tag
								if (!isCompatible && currentWidget.getInheritedTag() != null && currentWidget.getInheritedTag().equals(styleSelector.getTag())) {
									isCompatible = true;
									score++;
								}
								
							}
							
							if (!isCompatible && score == 0) {
								return 0;
							}

							// Pseudo class
							if (styleSelector.hasPseudoClass() && pseudoClasses != null) {
								for (int i = pseudoClasses.length - 1; i>= 0; --i) {
									for (int j = styleSelector.getPseudoClasses().length - 1; j>=0; --j) {
										if (pseudoClasses[i].equals(styleSelector.getPseudoClasses()[j])) {
											isCompatible = true;
											score += 100000000;
										}
									}
								}
							}
							
							currentWidget = currentWidget.parent;
						}

						styleSelector = styleSelector.parent;
						if (currentWidget == null && styleSelector != null || !isCompatible) {
							return 0;
						}

					}
					return score;

				}
			};

			Vector styles = registredStyles.findAll(filter);
			if (widget.getAuthorStyle() != null) {
				// Insert the author style at the first position
				styles.insertElementAt(widget.getAuthorStyle(), 0);
			}
			if (styles != null) {
				return styles;
			}

		}
		return null;
	}
	
	/**
	 * Remove all registred styles
	 */
	public static void removeAllStyles() {
		registredStyles.removeAll();
	}

	/**
	 * Clear style cache from the specified {@link Widget} and its childs
	 * 
	 * @param target
	 * @param propagateToChildren
	 */
	public static void clearStyleCache(Widget target, boolean propagateToChildren) {
		if (target != null) {
			target.clearCachedStyle(propagateToChildren);
		}
	}
	
	// Internationalization support ////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Initializes internationalization support based on currently set locale (obtained
	 * from "microedition.locale" system property). The initialization method is
	 * called automatically when a call to {@link #getMessage(java.lang.String)}
	 * method is attempted for the first time.
	 * 
	 * You can call this method explicitly to see whether there was any problem
	 * with initialization of the i18n support. Method returns a status
	 * of the successfulness.
	 * 
	 * @return true if the intialization was succesfull, false if there was any
	 *         problem.
	 */
	public static boolean initI18nSupport() {
		return initI18nSupport(KuixConstants.DEFAULT_I18N_MESSAGES_BUNDLE, getLocale());
	}

	/**
	 * Explicit initialization of the internationalization support. This method is
	 * usually called when a particular locale used in the application. E.g. the
	 * application contains only french messages (no default messages, only
	 * <CODE>messages_fr.properties</CODE> files is available), you should
	 * initialize the i18n support (by calling
	 * <CODE>initI18nSupport("fr");</CODE>) before using
	 * {@link #getMessage(java.lang.String)} method for the first time.
	 * 
	 * @param locale locale which will be used to determine which message file
	 *            from bundle will be used
	 * @return true if the intialization was succesfull, false if there was any
	 *         problem.
	 */
	public static boolean initI18nSupport(String locale) {
		return initI18nSupport(KuixConstants.DEFAULT_I18N_MESSAGES_BUNDLE, locale);
	}
	
	/**
	 * Explicit initialization of the internationalization support. This method
	 * is usually called when a particular locale used in the application. E.g.
	 * the application contains only french messages (no default messages, only
	 * <CODE>messages_fr.properties</CODE> files is available), you should
	 * initialize the i18n support (by calling
	 * <CODE>initI18nSupport("fr");</CODE>) before using
	 * {@link #getMessage(java.lang.String)} method for the first time.
	 * 
	 * @param messageBundle full custom messages bundle path
	 * @param locale locale which will be used to determine which message file
	 *            from bundle will be used
	 * @return <code>true</code> if the intialization was succesfull,
	 *         <code>false</code> if there was any problem.
	 */
	public static boolean initI18nSupport(String messageBundle, String locale) {
		
		// Init the message table
		messageTable = new Hashtable();

		// Save the locale
		Kuix.locale = locale;
		
		// Load Kuix default message bundle
		loadI18nBundle(KuixConstants.KUIX_DEFAULT_I18N_MESSAGES_BUNDLE);
		
		// Load user message bundle
		loadI18nBundle(messageBundle);
		
		return messageTable != null;
	}
	
	/**
	 * Load a new bundle and append messages to the messages table.
	 * 
	 * @param messageBundle
	 * @return <code>true</code> if the intialization was succesfull,
	 *         <code>false</code> if there was any problem.
	 */
	public static boolean loadI18nBundle(String messageBundle) {
		
		// Init i18n sopport first
		if (messageTable == null) {
			initI18nSupport();
		}
		
		// Relative path ?
		if (messageBundle != null && !messageBundle.startsWith("/")) {
			messageBundle = new StringBuffer(KuixConstants.DEFAULT_I18N_RES_FOLDER).append(messageBundle).toString();
		}
		
		InputStream inputStream = null;
		// Use frameHandler.getClass() because of a Object.class bug
		Class clazz = frameHandler.getClass();
		try {
			
			// Construct messageBundle
			// try to find localized resource first (in format ${name}_locale.${suffix})
			if ((locale != null) && (locale.length() > 1)) {
				int lastIndex = messageBundle.lastIndexOf('.');
				String prefix = messageBundle.substring(0, lastIndex);
				String suffix = messageBundle.substring(lastIndex);
				// replace '-' with '_', some phones returns locales with
				// '-' instead of '_'. For example Nokia or Motorola
				locale = locale.replace('-', '_');
				inputStream = clazz.getResourceAsStream(new StringBuffer(prefix).append('.').append(locale).append(suffix).toString());
				if (inputStream == null) {
					// if no localized resource is found or localization is available
					// try broader???? locale (i.e. instead og en_US, try just en)
					locale = locale.substring(0, 2); 
					inputStream = clazz.getResourceAsStream(new StringBuffer(prefix).append('.').append(locale).append(suffix).toString());
				}
			}
			if (inputStream == null) {
				// if not found or locale is not set, try default locale
				inputStream = clazz.getResourceAsStream(messageBundle);
			}
			if (inputStream != null) {
				// load messages to messageTable hashtable
				loadMessages(inputStream);
			}
		} catch (UTFDataFormatException e) {
			System.err.println("I18N Error : *.properties files need to be UTF-8 encoded");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return messageTable != null;
	}

	/**
	 * Finds a localized string in a message bundle.
	 * 
	 * @param key key of the localized string to look for
	 * @return the localized string. If key is not found, then
	 *         <CODE>DEFAULT_UNKNOWN_I18N_MESSAGE</CODE> string is returned
	 */
	public static final String getMessage(String key) {
		return getMessage(key, null);
	}
	
	/**
	 * Returns the last used locale, or null if never set.
	 * 
	 * @return the locale
	 */
	public static String getLocale() {
		if (locale == null) {
			try {
				locale = System.getProperty("microedition.locale");
			} catch (Exception e) {
			}
		}
		return locale;
	}

	/**
	 * Finds a localized string in a message bundle and formats the message by
	 * passing requested parameters.
	 * 
	 * @param key key of the localized string to look for
	 * @param args array of arguments to use for formatting the message
	 * @return the localized string. If key is not found, then
	 *         <CODE>DEFAULT_UNKNOWN_I18N_MESSAGE</CODE> string is returned
	 */
	public static final String getMessage(String key, Object[] args) {
		if (messageTable == null) {
			if (!initI18nSupport()) {
				// Error while init i18n support : return the key
				return key;
			}
		}
		String s = (String) messageTable.get(key);
		if (s != null) {
			return StringUtil.format(s, args);
		}
		// The key is not defined : return the key
		return key;
	}

	/**
	 * Process the internationalization and replace found keys by their values.
	 * <p>
	 * Syntax : <code>%KEY%</code> if value is like
	 * <code>KEY=Hello world, thanks for using Kuix.</code>
	 * </p>
	 * <p>
	 * Syntax : <code>%KEY(arg0,arg1)%</code> if value is like
	 * <code>KEY=Hello {0}, thanks for using {1}.</code>
	 * </p>
	 * 
	 * @param pattern
	 * @return The processed String
	 */
	public static String processI18nPattern(String pattern) {
		if (pattern != null && pattern.startsWith("%") && pattern.endsWith("%")) {
			int keyEndIndex = pattern.length() - 1;
			String[] argsValues = null;
			int argStartIndex;
			if ((argStartIndex = pattern.indexOf('(', 1)) != -1) {
				int argEndIndex = pattern.indexOf(')', argStartIndex);
				if (argEndIndex > argStartIndex + 1) {
					StringTokenizer args = new StringTokenizer(pattern.substring(argStartIndex + 1, argEndIndex), ",");
					argsValues = new String[args.countTokens()];
					int i = 0;
					while (args.hasMoreTokens()) {
						argsValues[i++] = args.nextToken().trim();
					}
					keyEndIndex = argStartIndex;
				}
			}
			return Kuix.getMessage(pattern.substring(1, keyEndIndex), argsValues);
		}
		return pattern;
	}
	
	/* 
	 * Internal i18n code
	 */

	// Characters separating keys and values
	private static final String KEY_VALUE_SEPARATORS = "=: \t\r\n\f";
	
	// Characters strictly separating keys and values
	private static final String STRICT_KEY_VALUE_SEPARTORS = "=:";
	
	// white space characters understood by the support (these can be in the message file)
	private static final String WHITESPACE_CHARS = " \t\r\n\f";

	// Contains the parsed message bundle.
	private static Hashtable messageTable;
	
	// Contains the last used locale, or null is never set.
	private static String locale;
	
	/**
	 * Loads messages from input stream to hash table.
	 * 
	 * @param inStream stream from which the messages are read
	 * @throws IOException if there is any problem with reading the messages
	 */
	private static synchronized void loadMessages(InputStream inStream) throws Exception {

		InputStreamReader inputStream = new InputStreamReader(inStream, "UTF-8");
		while (true) {
			// Get next line
			String line = readLine(inputStream);
			if (line == null)
				return;

			if (line.length() > 0) {

				// Find start of key
				int len = line.length();
				int keyStart;
				for (keyStart = 0; keyStart < len; keyStart++) {
					if (WHITESPACE_CHARS.indexOf(line.charAt(keyStart)) == -1) {
						break;
					}
				}

				// Blank lines are ignored
				if (keyStart == len) {
					continue;
				}

				// Continue lines that end in slashes if they are not comments
				char firstChar = line.charAt(keyStart);
				if ((firstChar != '#') && (firstChar != '!')) {
					while (continueLine(line)) {
						String nextLine = readLine(inputStream);
						if (nextLine == null) {
							nextLine = "";
						}
						String loppedLine = line.substring(0, len - 1);
						// Advance beyond whitespace on new line
						int startIndex;
						for (startIndex = 0; startIndex < nextLine.length(); startIndex++) {
							if (WHITESPACE_CHARS.indexOf(nextLine.charAt(startIndex)) == -1) {
								break;
							}
						}
						nextLine = nextLine.substring(startIndex, nextLine.length());
						line = new String(loppedLine + nextLine);
						len = line.length();
					}

					// Find separation between key and value
					int separatorIndex;
					for (separatorIndex = keyStart; separatorIndex < len; separatorIndex++) {
						char currentChar = line.charAt(separatorIndex);
						if (currentChar == '\\') {
							separatorIndex++;
						} else if (KEY_VALUE_SEPARATORS.indexOf(currentChar) != -1) {
							break;
						}
					}

					// Skip over whitespace after key if any
					int valueIndex;
					for (valueIndex = separatorIndex; valueIndex < len; valueIndex++) {
						if (WHITESPACE_CHARS.indexOf(line.charAt(valueIndex)) == -1) {
							break;
						}
					}

					// Skip over one non whitespace key value separators if any
					if (valueIndex < len) {
						if (STRICT_KEY_VALUE_SEPARTORS.indexOf(line.charAt(valueIndex)) != -1) {
							valueIndex++;
						}
					}

					// Skip over white space after other separators if any
					while (valueIndex < len) {
						if (WHITESPACE_CHARS.indexOf(line.charAt(valueIndex)) == -1) {
							break;
						}
						valueIndex++;
					}
					String key = line.substring(keyStart, separatorIndex);
					String value = (separatorIndex < len) ? line.substring(valueIndex, len) : "";

					// Convert then store key and value
					key = convertString(key);
					value = convertString(value);
					messageTable.put(key, value);
				}
			}
		}

	}

	/**
	 * reads a single line from InputStreamReader
	 * 
	 * @param in InputStreamReader used to read the line
	 * @throws IOException if there is any problem with reading
	 * @return the read line
	 */
	private static String readLine(InputStreamReader in) throws IOException {
		StringBuffer strBuf = new StringBuffer("");
		int i;
		while ((i = in.read()) != -1) {
			if ((char) i == '\r' || (char) i == '\n') {
				return strBuf.toString();
			}
			strBuf.append((char) i);
		}
		return strBuf.length() > 0 ? strBuf.toString() : null;
	}

	/**
	 * determines whether the line of the supplied string continues on the next
	 * line
	 * 
	 * @param line a line of String
	 * @return true if the string contines on the next line, false otherwise
	 */
	private static boolean continueLine(String line) {
		int slashCount = 0;
		int index = line.length() - 1;
		while ((index >= 0) && (line.charAt(index--) == '\\'))
			slashCount++;
		return (slashCount % 2 == 1);
	}

	/**
	 * Decodes a String which uses unicode characters in \\uXXXX format.
	 * 
	 * @param theString String with \\uXXXX characters
	 * @return resolved string
	 */
	private static String convertString(String theString) {
		char aChar;
		int len = theString.length();
		StringBuffer outBuffer = new StringBuffer(len);

		for (int x = 0; x < len;) {
			aChar = theString.charAt(x++);
			if (aChar == '\\') {
				aChar = theString.charAt(x++);
				if (aChar == 'u') {
					// Read the xxxx
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = theString.charAt(x++);
						switch (aChar) {
							case '0':
							case '1':
							case '2':
							case '3':
							case '4':
							case '5':
							case '6':
							case '7':
							case '8':
							case '9':
								value = (value << 4) + aChar - '0';
								break;
							case 'a':
							case 'b':
							case 'c':
							case 'd':
							case 'e':
							case 'f':
								value = (value << 4) + 10 + aChar - 'a';
								break;
							case 'A':
							case 'B':
							case 'C':
							case 'D':
							case 'E':
							case 'F':
								value = (value << 4) + 10 + aChar - 'A';
								break;
							default:
								// return KuixConstants.DEFAULT_UNKNOWN_I18N_MESSAGE STRING if there is any problem
								return "???";
						}
					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't') {
						aChar = '\t';
					} else if (aChar == 'r') {
						aChar = '\r';
					} else if (aChar == 'n') {
						aChar = '\n';
					} else if (aChar == 'f') {
						aChar = '\f';
					}
					outBuffer.append(aChar);
				}
			} else {
				outBuffer.append(aChar);
			}
		}
		return outBuffer.toString();
	}

}