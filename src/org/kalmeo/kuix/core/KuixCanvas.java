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

import java.io.IOException;
import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;

import org.kalmeo.kuix.core.focus.FocusManager;
import org.kalmeo.kuix.transition.Transition;
import org.kalmeo.kuix.util.Metrics;
import org.kalmeo.kuix.widget.Desktop;
import org.kalmeo.kuix.widget.Widget;
import org.kalmeo.util.NumberUtil;
import org.kalmeo.util.StringTokenizer;
import org.kalmeo.util.worker.Worker;
import org.kalmeo.util.worker.WorkerTask;

/**
 * This is the Kuix implementation of the J2ME {@link GameCanvas}.
 * This canvas intercept all user events (keyboard and pointer)
 * 
 * @author bbeaulant
 */
public final class KuixCanvas extends GameCanvas {

	// Associated KuixInitializer
	private final KuixInitializer initializer;

	// The root Desktop widget
	private Desktop desktop;
	
	// This value is true if the canvas is fully initialized
	private boolean initialized = false;
	
	// Workaround to fix the bug when the vm do not call sizeChanged method
	private boolean sizeInitialized = false;
	
	// Specify if the KuixCanvas is hidded
	private boolean hidded = true;
	
	// The GameCanvas and BuffuredImage graphics instance
	private Graphics canvasGraphics;
	private Graphics imageBufferGraphics;
	
	// Double buffering image for devices that doesn't implement it natively
	private Image imageBuffer = null;
	
	// Unpaintable regions (represented by a list of widget)
	private Vector unpaintableWidgets;

	// Transition stuff
	private Transition transition;
	private boolean transitionRunning = false;
	
	// Last pointer drag status
	private int pointerPressedX = 0;
	private int pointerPressedY = 0;
	private boolean pointerDragged = false;

	// KuixCanvas Worker's task properties
	private boolean needToChangeSize = false;
	private int desiredWidth = -1;
	private int desiredHeight = -1;
	private Vector keyEvents;
	private Vector pointerEvents;
	private boolean needToRevalidate = false;
	private boolean needToRepaint = false;
	private final Metrics repaintRegion = new Metrics();
	private WorkerTask workerTask;
	
	// DebugInfos properties
	private int debugInfosKuixKeyCode = KuixConstants.KUIX_KEY_STAR;
	private byte debugInfosKeyCounter = 0;
	private boolean debugInfosEnabled = false;
	private long lastFpsTickTime = 0;
	
	/**
	 * Construct a {@link KuixCanvas}. By default the canvas is auto created by
	 * the {@link KuixMIDlet}. But if you create it manualy @se
	 * {@link KuixInitializer}.
	 * 
	 * @param initializer
	 * @param fullscreen
	 */
	public KuixCanvas(KuixInitializer initializer, boolean fullscreen) {
		super(false);
		
		if (initializer == null) {
			throw new IllegalArgumentException("initializer couldn't be null");
		}
		this.initializer = initializer;
		
		setFullScreenMode(fullscreen);
		
		// Try to extract the debug infos key from the JAD file
		if (initializer.getMIDlet() != null) {
			String debugInfosKey = initializer.getMIDlet().getAppProperty(KuixConstants.KUIX_DEBUG_INFOS_KEY_APP_PROPERTY);
			if (debugInfosKey != null) {
				byte[] shortcuts = Kuix.getConverter().convertShortcuts(debugInfosKey);
				if (shortcuts != null) {
					setDebugInfosKuixKeyCode(NumberUtil.toInt(shortcuts, 0));
				}
			}
		}

	}
	
	/**
	 * Returns the {@link KuixInitializer} instance. 
	 * 
	 * @return the initializer instance
	 */
	public KuixInitializer getInitializer() {
		return initializer;
	}

	/**
	 * Returns the {@link Desktop} instance. 
	 * 
	 * @return the desktop instance.
	 */
	public Desktop getDesktop() {
		return desktop;
	}
	
	/**
	 * Define the key that the user need to press 3 consecutive times to display
	 * on screen debug infos. This value define one or more keys (ex:
	 * <code>KuixConstants.KUIX_KEY_STAR | KuixConstants.KUIX_KEY_POUND</code>,
	 * press * or # to activete debug onfos display)<br>
	 * By default the value si set to <code>KuixConstants.KUIX_KEY_STAR</code>.
	 * 
	 * @param debugInfosKuixKeyCode the debugInfosKuixKeyCode to set
	 */
	protected void setDebugInfosKuixKeyCode(int debugInfosKuixKeyCode) {
		this.debugInfosKuixKeyCode = debugInfosKuixKeyCode;
	}
	
	/**
	 * @return the debugInfosEnabled
	 */
	public boolean isDebugInfosEnabled() {
		return debugInfosEnabled;
	}

	/**
	 * @param debugInfosEnabled the debugInfosEnabled to set
	 */
	public void setDebugInfosEnabled(boolean debugInfosEnabled) {
		this.debugInfosEnabled = debugInfosEnabled;
		repaintNextFrame();
	}

	/**
	 * Define the next repaint transition. The transition delay depends on the
	 * transition implementation.
	 * 
	 * @param transition
	 */
	public void setTransition(Transition transition) {
		this.transition = transition;
		transitionRunning = false;
	}
	
	/**
	 * Initialize the Canvas
	 */
	protected void initialize() {
		
		// Init keyCode adapter
		platformName = getPlatform();
		softKeyLeft = getLeftSoftkeyCode();
		softKeyRight = getRightSoftkeyCode();
		softKeyDelete = getDeleteKeyCode();
		softKeyBack = getBackKeyCode();
		
		// Try to init statup locale
		if (initializer.getMIDlet() != null) {
			String locale = initializer.getMIDlet().getAppProperty(KuixConstants.KUIX_LOCALE_APP_PROPERTY);
			if (locale != null) {
				Kuix.initI18nSupport(locale);
			}
		}
		
		// Init the desktop styles
		initializer.initDesktopStyles();
		
		// Retrieve the custom or autodetect display size and position
		int displayX = 0;
		int displayY = 0;
		int displayWidth = getWidth();
		int displayHeight = getHeight();
		String customDisplayBounds = initializer.getMIDlet().getAppProperty(KuixConstants.KUIX_DESKTOP_BOUNDS_APP_PROPERTY);
		if (customDisplayBounds != null) {
			StringTokenizer st = new StringTokenizer(customDisplayBounds, ",");
			if (st.countTokens() == 4) {
				displayX = Integer.parseInt(st.nextToken().trim());
				displayY = Integer.parseInt(st.nextToken().trim());
				displayWidth = Integer.parseInt(st.nextToken().trim());
				displayHeight = Integer.parseInt(st.nextToken().trim());
			}
		}
		
		// Create the desktop
		desktop = new Desktop();
		desktop.setBounds(displayX, displayY, displayWidth, displayHeight);
		
		// Add blackberry soft keys emulation commands
		if (platformName == KuixConstants.PLATFORM_BLACKBERRY) {
			final Command softKeyLeftCommand = new Command(Kuix.getMessage(KuixConstants.SOFT_LEFT_I18N_KEY), Command.OK, 1);
			final Command softKeyRightCommand = new Command(Kuix.getMessage(KuixConstants.SOFT_RIGHT_I18N_KEY), Command.OK, 2);
			addCommand(softKeyLeftCommand);
			addCommand(softKeyRightCommand);
			setCommandListener(new CommandListener() {

				/* (non-Javadoc)
				 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
				 */
				public void commandAction(Command c, Displayable d) {
					if (c == softKeyLeftCommand) {
						processKeyEvent(KuixConstants.KEY_PRESSED_EVENT_TYPE, SOFT_KEY_LEFT_DEFAULT);
						return;
					}
					if (c == softKeyRightCommand) {
						processKeyEvent(KuixConstants.KEY_PRESSED_EVENT_TYPE, SOFT_KEY_RIGHT_DEFAULT);
						return;
					}
				}
				
			});
		}
		
		// Init worker's task
		keyEvents = new Vector();
		pointerEvents = new Vector();
		workerTask = new WorkerTask() {
			
			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.core.worker.WorkerTask#execute()
			 */
			public boolean run() {
				
				if (needToChangeSize) {
					forceSizeChanged(desiredWidth, desiredHeight);
				}
				
				if (sizeInitialized) {
				
					// Key events, Pointer events and revalidation are execute only if transition is not running
					if (!transitionRunning) {
						
						// Key events
						if (!keyEvents.isEmpty()) {
							synchronized (this) {
								for (int i = 0; i < keyEvents.size(); ++i) {
									int[] keyEvent = ((int[]) keyEvents.elementAt(i));
									FocusManager focusManager = desktop.getCurrentFocusManager();
									if (focusManager != null && focusManager.processKeyEvent((byte) keyEvent[0], keyEvent[1])) {
										repaintNextFrame();
									}
								}
							}
							keyEvents.removeAllElements();
						}
						
						// Pointer events
						if (!pointerEvents.isEmpty()) {
							synchronized (this) {
								for (int i = 0; i < pointerEvents.size(); ++i) {
									int[] pointerEvent = ((int[]) pointerEvents.elementAt(i));
									FocusManager focusManager = desktop.getCurrentFocusManager();
									if (focusManager != null && focusManager.processPointerEvent((byte) pointerEvent[0], pointerEvent[1], pointerEvent[2])) {
										repaintNextFrame();
									} else if ((byte) pointerEvent[0] == KuixConstants.POINTER_DROPPED_EVENT_TYPE) {
										if (desktop.getDraggedWidget() != null) {
											desktop.removeDraggedWidget(true);
										}
									}
								}
							}
							pointerEvents.removeAllElements();
						}
						
						// Revalidate if needed
						if (needToRevalidate) {
							forceRevalidate();
						}
						
					}
					
					// Repaint
					if (needToRepaint) {
						forceRepaint();
					}
				
				}
				
				return false;
			}

		};
		
		// Init the desktop content (Populate the desktop for the first run)
		initializer.initDesktopContent(desktop);
		
		// Initialization complete
		initialized = true;
		
		// Push the canvas worker task and then the first revalidate and repaint will be done
		Worker.instance.pushTask(workerTask);
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.game.GameCanvas#paint(javax.microedition.lcdui.Graphics)
	 */
	public void paint(Graphics g) {
		if (!sizeInitialized) {
			forceSizeChanged(getWidth(), getHeight());
		}
		super.paint(g);
		if (initialized) {
			repaintNextFrame();
		} else {
			
			// Draw the initialization splash screen. The duration of this screen depend on device
			
			// Extract custom atributes
			String message = initializer.getInitializationMessage();
			String imageFile = initializer.getInitializationImageFile();
			Image image = null;
			
			// Construct image if exists
			if (imageFile != null) {
				try {
					image = Image.createImage(imageFile);
				} catch (IOException ioe) {
				}
			}
			
			// Compute placement
			int textWidth = 0;
			int textHeight = 0;
			int imageWidth = 0;
			int imageHeight = 0;
			
			if (message != null) {
				Font font = Font.getDefaultFont();
				textWidth = font.stringWidth(message);
				textHeight = font.getHeight();
			}
			if (image != null) {
				imageWidth = image.getWidth();
				imageHeight = image.getHeight();
			}
			
			// Draw background
			g.setColor(initializer.getInitializationBackgroundColor());
			g.fillRect(0, 0, getWidth(), getHeight());
			
			// Draw message
			if (message != null) {
				g.setColor(initializer.getInitializationMessageColor());
				g.drawString(	message, 
								(getWidth() - textWidth) / 2, 
								(getHeight() - imageHeight - textHeight) / 2 + imageHeight, 
								0);
			}
			
			// Draw image
			if (image != null) {
				g.drawImage(image, 
							(getWidth() - imageWidth) / 2, 
							(getHeight() - imageHeight - textHeight) / 2, 
							0);
			}
			
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#sizeChanged(int, int)
	 */
	protected void sizeChanged(int w, int h) {
		if (sizeInitialized) {
			needToChangeSize = true;
			desiredWidth = w;
			desiredHeight = h;
			if (!hidded) {
				revalidateNextFrame();
			}
		} else {
			forceSizeChanged(w, h);
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#hideNotify()
	 */
	protected void hideNotify() {
		hidded = true;
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#showNotify()
	 */
	protected void showNotify() {
		hidded = false;
		revalidateNextFrame();
	}

	/**
	 * Repaint through the Worker task
	 */
	public void repaintNextFrame() {
		repaintNextFrame(0, 0, getWidth(), getHeight());
	}

	/**
	 * Repaint through the Worker task
	 * 
	 * @param x	x coordinate of the repaint region
	 * @param y y coordinate of the repaint region
	 * @param width width of the repaint region
	 * @param height height of the repaint region
	 */
	public void repaintNextFrame(int x, int y, int width, int height) {
		needToRepaint = true;
		repaintRegion.add(x, y, width, height);
	}
	
	/**
	 * Revalidate the desktop throught the Worker task. <code>repaintNextFrame</code> is automaticaly call.
	 */
	public void revalidateNextFrame() {
		needToRevalidate = true;
		repaintNextFrame();
	}
	
	/**
	 * Revalidate (and repaint) the desktop as soon as possible. If the current
	 * thread is the worker thread the task is done immedialty else it is
	 * deferred to the next frame.
	 */
	public void revalidateAsSoonAsPossible() {
		if (!Worker.instance.isCurrentThread()) {
			revalidateNextFrame();
		} else {
			forceRevalidate();
			forceRepaint();
		}
	}
	
	/**
	 * Repaint the desktop as soon as possible. If the current thread is the
	 * worker thread the task is done immedialty else it is deferred to the next
	 * frame.
	 */
	public void repaintAsSoonAsPossible() {
		if (!Worker.instance.isCurrentThread()) {
			repaintNextFrame();
		} else {
			forceRepaint();
		}
	}
	
	/**
	 * Resize the {@link KuixCanvas}.
	 * 
	 * @param w
	 * @param h
	 */
	private void forceSizeChanged(int w, int h) {
		needToChangeSize = false;
		desiredWidth = -1;
		desiredHeight = -1;
		imageBuffer = Image.createImage(w, h);
		imageBufferGraphics = imageBuffer.getGraphics();
		canvasGraphics = getGraphics();
		if (desktop != null) {
			desktop.setBounds(0, 0, w, h);
		}
		sizeInitialized = true;
	}

	/**
	 * Force desktop to be revalidated
	 */
	private void forceRevalidate() {
		needToRevalidate = false; // Tag as needToRevalidate = false first, because the revalidate process could cause invalidation
		desktop.revalidate();
		if (needToRevalidate) {
			forceRevalidate();
		}
	}
	
	/**
	 * Force desktop to be repaint
	 */
	private void forceRepaint() {
		
		// Define clip rect region
		boolean repaintRegionOnly = !repaintRegion.isEmpty();
		if (repaintRegionOnly) {
			imageBufferGraphics.setClip(repaintRegion.x, repaintRegion.y, repaintRegion.width, repaintRegion.height);
		}
		
		// Clear unpaintable widget list (this list would be reconstruct with the recursive Widget.paintImpl() method)
		if (unpaintableWidgets != null) {
			clearUnpaintableWidgets();
		}

		if (transition != null) {
			if (!transitionRunning) {
				Image oldImage = Image.createImage(imageBuffer);
				desktop.paintImpl(imageBufferGraphics);
				transition.init(oldImage, imageBuffer);
				transitionRunning = true;
			}
			if (transition.process(canvasGraphics)) {
				transition = null;
				transitionRunning = false;
			}
		} else {
			// Repaint desktop
			desktop.paintImpl(imageBufferGraphics);
			// Paint buffured image
			canvasGraphics.drawImage(imageBuffer, 0, 0, 0);
		}
		
		// Debug infos
		if (debugInfosEnabled) {
			drawDebugInfos(canvasGraphics);
		}
		
		// FlushGraphics
		if (unpaintableWidgets == null || unpaintableWidgets.isEmpty()) {
			if (repaintRegionOnly) {
				flushGraphics(repaintRegion.x, repaintRegion.y, repaintRegion.width, repaintRegion.height);
			} else {
				flushGraphics();
			}
		} else {
			if (repaintRegionOnly) {
				flushRegion(repaintRegion.x, repaintRegion.y, repaintRegion.width, repaintRegion.height);
			} else {
				flushRegion(0, 0, getWidth(), getHeight());
			}
		}
		repaintRegion.setBounds(0, 0, 0, 0);
		
		needToRepaint = false || transitionRunning;
	}

	/**
	 * Flush graphics on a specific region by excluding unpaintaible regions
	 * represented by the unpaintableWidgets list.
	 * 
	 * @param xMin
	 * @param yMin
	 * @param xMax
	 * @param yMax
	 */
	private void flushRegion(int xMin, int yMin, int xMax, int yMax) {
		
		// Search first intersect widget
		Widget widget = null;
		for (int i = unpaintableWidgets.size() - 1; i >= 0 ; --i) {
			Widget tmpWidget = (Widget) unpaintableWidgets.elementAt(i);
			if (intersect(tmpWidget, xMin, yMin, xMax, yMax)) {
				widget = tmpWidget;
				break;
			}
		}
		if (widget == null) {
			// Flush the current display area
			flushGraphics(xMin, yMin, xMax, yMax); 
			return;
		}
		
		int top = Math.max(0, widget.getY() - yMin);
		int right = Math.max(0, xMax - (widget.getX() + widget.getWidth()));
		int bottom = Math.max(0, yMax - (widget.getY() + widget.getHeight()));
		int left = Math.max(0, widget.getX() - xMin);
		
		// Top
		if (top != 0) {
			if (left != 0) {
				flushRegion(xMin, 
						yMin, 
						xMin + left, 
						yMin + top);
			}
			flushRegion(xMin + left, 
					yMin, 
					xMax - right, 
					yMin + top);
			if (right != 0) {
				flushRegion(xMax - right, 
						yMin, 
						xMax, 
						yMin + top);
			}
		}
		
		// Center
		if (left != 0) {
			flushRegion(xMin, 
					yMin + top, 
					xMin + left, 
					yMax - bottom);
		}
		if (right != 0) {
			flushRegion(xMax - right, 
					yMin + top, 
					xMax, 
					yMax - bottom);
		}
		
		// Bottom
		if (bottom != 0) {
			if (left != 0) {
				flushRegion(xMin, 
						yMax - bottom, 
						xMin + left, 
						yMax);
			}
			flushRegion(xMin + left, 
					yMax - bottom, 
					xMax - right, 
					yMax);
			if (right != 0) {
				flushRegion(xMax - right, 
						yMax - bottom, 
						xMax, 
						yMax);
			}
		}
		
	}
	
	/**
	 * Check regions intersection.
	 * 
	 * @param widget
	 * @param xMin
	 * @param yMin
	 * @param xMax
	 * @param yMax
	 * @return <code>true</code> if the <code>widget</code> region intersects
	 *         the <code>xMin</code>, <code>yMin</code>, <code>xMax</code>,
	 *         <code>yMax</code> région.
	 */
	private boolean intersect(Widget widget, int xMin, int yMin, int xMax, int yMax) {
    	return (xMax > widget.getX()
	    		&& yMax > widget.getY()
	    		&& widget.getX() + widget.getWidth() > xMin
	    		&& widget.getY() + widget.getHeight() > yMin);
	}

	/**
	 * Add a new unpaintable widget to the unpaintableWidgets list.<br>
	 * Caution that adding a widget to this list mask a region of the display
	 * event if the given widget is not on top : popup over this region may be
	 * masked and invisible.<br>
	 * <b>The unpaintableWidgets list is cleared before each repaint.</b> 
	 * 
	 * @param widget
	 */
	public void addUnpaintableWidget(Widget widget) {
		if (unpaintableWidgets == null) {
			unpaintableWidgets = new Vector();
		}
		if (!unpaintableWidgets.contains(widget)) {
			unpaintableWidgets.addElement(widget);
		}
	}
	
	/**
	 * Add a widget from the unpaintableWidgets list.
	 * 
	 * @param widget
	 */
	public void removeUnpaintableWidget(Widget widget) {
		if (unpaintableWidgets != null) {
			unpaintableWidgets.removeElement(widget);
		}
	}
	
	/**
	 * Remove all unpaintable widgets.
	 */
	public void clearUnpaintableWidgets() {
		if (unpaintableWidgets != null) {
			unpaintableWidgets.removeAllElements();
		}
	}
	
	/**
	 * Draw debugInfos to the given {@link Graphics}
	 * 
	 * @param g
	 */
	protected void drawDebugInfos(Graphics g) {
		String debugInfos = getDebugInfos();
		StringTokenizer st = new StringTokenizer(debugInfos, "\n");
		if (st.hasMoreTokens()) {
			Font font = Font.getDefaultFont();
			g.setFont(font);
			int x = 2;
			int y = 0;
			while (st.hasMoreTokens()) {
				
				String debugInfo = st.nextToken();
				
				// Draw text shadow
				g.setColor(0x000000);
				for (int dy = -1; dy <= 1; ++dy) {
					for (int dx = -1; dx <= 1; ++dx) {
						g.drawString(debugInfo, x + dx, y + dy, Graphics.TOP | Graphics.LEFT);
					}
				}
				
				// Draw text foreground
				g.setColor(0xFFFFFF);
				g.drawString(debugInfo, 2, y, Graphics.TOP | Graphics.LEFT);
				y += font.getHeight();
				
			}
		}
	}
	
	/**
	 * Returns a String representation of the debug infos. Override this method
	 * to add your own debug infos. Use the \n character to have a multiline text.
	 * 
	 * @return a String representation of the debug infos
	 */
	public String getDebugInfos() {

		long totalMemory = -1;
		long freeMemory = -1;
		try {
			totalMemory = Runtime.getRuntime().totalMemory();
			freeMemory = Runtime.getRuntime().freeMemory();
		} catch (Exception e) {
		}
		
		long currentTime = System.currentTimeMillis();
		long frameDuration = currentTime - lastFpsTickTime;
		int fps = (int) (1000 / frameDuration);
		lastFpsTickTime = currentTime;
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("Kuix ").append(KuixConstants.VERSION).append(" (").append(Worker.instance.getFrameDuration()).append("ms)")
				.append("\ndisplay : ").append(getWidth()).append("x").append(getHeight())
		 		.append("\nfps : ").append(fps)
				.append("\nplatform : ").append(getPlatformName())
				.append("\ntotalMemory : ").append(totalMemory)
				.append("\nfreeMemory : ").append(freeMemory)
				.append('\n').append(Kuix.getFrameHandler().toString());
		
		return buffer.toString();
		
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#keyPressed(int)
	 */
	protected void keyPressed(int keyCode) {
		processKeyEvent(KuixConstants.KEY_PRESSED_EVENT_TYPE, keyCode);
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#keyReleased(int)
	 */
	protected void keyReleased(int keyCode) {
		processKeyEvent(KuixConstants.KEY_RELEASED_EVENT_TYPE, keyCode);
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#keyRepeated(int)
	 */
	protected void keyRepeated(int keyCode) {
		processKeyEvent(KuixConstants.KEY_REPEATED_EVENT_TYPE, keyCode);
	}

	/**
	 * Process the key events
	 * 
	 * @param type
	 * @param keyCode
	 */
	protected void processKeyEvent(byte type, int keyCode) {
		if (initialized) {
			
			int kuixKeyCode = adoptKeyCode(keyCode);
			
			// Intercept debugInfos key
			if (type == KuixConstants.KEY_RELEASED_EVENT_TYPE) {
				if ((debugInfosKuixKeyCode & kuixKeyCode) == kuixKeyCode) {
					debugInfosKeyCounter++;
					if (debugInfosKeyCounter >= 3) {
						initializer.processDebugInfosKeyEvent();
						debugInfosKeyCounter = 0;
					}
				} else {
					debugInfosKeyCounter = 0;
				}
			}
			
			// Add event to queue
			synchronized (this) {
				keyEvents.addElement(new int[] { type, kuixKeyCode });
			}
			
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#pointerDragged(int, int)
	 */
	protected void pointerDragged(int x, int y) {
		if (pointerDragged || Math.abs(x - pointerPressedX) > 5 || Math.abs(y - pointerPressedY) > 5) {
			pointerDragged = true;
			processPointerEvent(KuixConstants.POINTER_DRAGGED_EVENT_TYPE, x, y);
		}
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#pointerPressed(int, int)
	 */
	protected void pointerPressed(int x, int y) {
		pointerPressedX = x;
		pointerPressedY = y;
		processPointerEvent(KuixConstants.POINTER_PRESSED_EVENT_TYPE, x, y);
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#pointerReleased(int, int)
	 */
	protected void pointerReleased(int x, int y) {
		byte type;
		if (pointerDragged) {
			type = KuixConstants.POINTER_DROPPED_EVENT_TYPE;
			pointerDragged = false;
		} else {
			type = KuixConstants.POINTER_RELEASED_EVENT_TYPE;
		}
		processPointerEvent(type, x, y);
	}

	/**
	 * Common process for pointer events
	 * 
	 * @param type
	 * @param x
	 * @param y
	 */
	protected void processPointerEvent(final byte type, final int x, final int y) {
		if (initialized) {
			synchronized (this) {
				pointerEvents.addElement(new int[] { type, x, y });
			}
		}
	}

	// KeyCode adapter support
	///////////////////////////////////////////////////////////////////////////////////

	// Current platform name
	private String platformName;

	// Current platform codeofSoftkey
	private int softKeyLeft;
	private int softKeyRight;
	private int softKeyDelete;
	private int softKeyBack;

	// Standard values for softkeys of different platforms used only in predefining
	private static final int SOFT_KEY_LEFT_DEFAULT 		= -6;
	private static final int SOFT_KEY_RIGHT_DEFAULT 	= -7;
	private static final int DELETE_KEY_DEFAULT 		= -8;
	
	private static final int SOFT_KEY_LEFT_SE 			= -6;
	private static final int SOFT_KEY_RIGHT_SE 			= -7;
	private static final int DELETE_KEY_SE 				= -8;
	private static final int BACK_KEY_SE 				= -11;
	
	private static final int SOFT_KEY_LEFT_SAMSUNG 		= -6;
	private static final int SOFT_KEY_RIGHT_SAMSUNG 	= -7;
	private static final int DELETE_KEY_SAMSUNG 		= -8;
	
	private static final int SOFT_KEY_LEFT_SIEMENS 		= -1;
	private static final int SOFT_KEY_RIGHT_SIEMENS 	= -4;
	
	private static final int SOFT_KEY_LEFT_NOKIA 		= -6;
	private static final int SOFT_KEY_RIGHT_NOKIA 		= -7;
	private static final int DELETE_KEY_NOKIA 			= -8;
	private static final int PENCIL_KEY_NOKIA 			= -50;
	
	private static final int SOFT_KEY_LEFT_MOTOROLA 	= -21;
	private static final int SOFT_KEY_RIGHT_MOTOROLA 	= -22;
	private static final int SOFT_KEY_LEFT_MOTOROLA2 	= -20;
	private static final int SOFT_KEY_LEFT_MOTOROLA1 	= 21;
	private static final int SOFT_KEY_RIGHT_MOTOROLA1 	= 22;
	private static final int DELETE_KEY_MOTOROLA		= -8;

	private static final int SOFT_KEY_LEFT_INTENT 		= 57345;
	private static final int SOFT_KEY_RIGHT_INTENT 		= 57346;

	private static final int DELETE_KEY_BLACKBERRY		= 8;
	
	private static final String SOFT_WORD = "SOFT";

	/**
	 * Returns mobile phone platform
	 * 
	 * @return name mobile phone platform
	 */
	private String getPlatform() {
		// Detecting NOKIA / SonyEricsson / Sun WTK emulator / Intent
		try {
			final String currentPlatform = System.getProperty("microedition.platform");
			if (currentPlatform.indexOf("Nokia") != -1) {
				return KuixConstants.PLATFORM_NOKIA;
			} else if (currentPlatform.indexOf("SonyEricsson") != -1) {
				return KuixConstants.PLATFORM_SONY_ERICSSON;
			} else if (currentPlatform.indexOf("SunMicrosystems") != -1 || currentPlatform.indexOf("j2me") != -1) {	// SunMicrosystems = WTK 2.5.x, j2me = J2ME Platforme SDK 3.0
				return KuixConstants.PLATFORM_SUN;
			} else if (currentPlatform.indexOf("intent") != -1) {
				return KuixConstants.PLATFORM_INTENT;
			}
		} catch (Throwable ex) {
			try {
				Class.forName("com.nokia.mid.ui.FullCanvas");
				return KuixConstants.PLATFORM_NOKIA;
			} catch (Throwable ex2) {
			}
		}
		// Detecting SAMSUNG
		try {
			Class.forName("com.samsung.util.Vibration");
			return KuixConstants.PLATFORM_SAMSUNG;
		} catch (Throwable ex) {
		}
		// Detecting MOTOROLA
		try {
			Class.forName("com.motorola.multimedia.Vibrator");
			return KuixConstants.PLATFORM_MOTOROLA;
		} catch (Throwable ex) {
			try {
				Class.forName("com.motorola.graphics.j3d.Effect3D");
				return KuixConstants.PLATFORM_MOTOROLA;
			} catch (Throwable ex2) {
				try {
					Class.forName("com.motorola.multimedia.Lighting");
					return KuixConstants.PLATFORM_MOTOROLA;
				} catch (Throwable ex3) {
					try {
						Class.forName("com.motorola.multimedia.FunLight");
						return KuixConstants.PLATFORM_MOTOROLA;
					} catch (Throwable ex4) {
					}
				}
			}
		}
		try {
			if (getKeyName(SOFT_KEY_LEFT_MOTOROLA).toUpperCase().indexOf(SOFT_WORD) > -1) {
				return KuixConstants.PLATFORM_MOTOROLA;
			}
		} catch (Throwable e) {
			try {
				if (getKeyName(SOFT_KEY_LEFT_MOTOROLA1).toUpperCase().indexOf(SOFT_WORD) > -1) {
					return KuixConstants.PLATFORM_MOTOROLA;
				}
			} catch (Throwable e1) {
				try {
					if (getKeyName(SOFT_KEY_LEFT_MOTOROLA2).toUpperCase().indexOf(SOFT_WORD) > -1) {
						return KuixConstants.PLATFORM_MOTOROLA;
					}
				} catch (Throwable e2) {
				}
			}
		}
		// Detecting SIEMENS
		try {
			Class.forName("com.siemens.mp.io.File");
			return KuixConstants.PLATFORM_SIEMENS;
		} catch (Throwable ex) {
		}
		// Detecting LG
		try {
			Class.forName("mmpp.media.MediaPlayer");
			return KuixConstants.PLATFORM_LG;
		} catch (Throwable ex) {
			try {
				Class.forName("mmpp.phone.Phone");
				return KuixConstants.PLATFORM_LG;
			} catch (Throwable ex1) {
				try {
					Class.forName("mmpp.lang.MathFP");
					return KuixConstants.PLATFORM_LG;
				} catch (Throwable ex2) {
					try {
						Class.forName("mmpp.media.BackLight");
						return KuixConstants.PLATFORM_LG;
					} catch (Throwable ex3) {
					}
				}
			}
		}
		// Detecting LG
		try {
			Class.forName("net.rim.device.api.ui.UiApplication");
			return KuixConstants.PLATFORM_BLACKBERRY;
		} catch (Throwable ex) {
		}
		return KuixConstants.PLATFORM_NOT_DEFINED;
	}

	/**
	 * define real left soft key code by platform
	 * 
	 * @return code
	 */
	private int getLeftSoftkeyCode() {
		int keyCode = 0;
		try {
			if (platformName.equals(KuixConstants.PLATFORM_MOTOROLA)) {
				String softkeyLeftMoto = "";
				try {
					softkeyLeftMoto = getKeyName(SOFT_KEY_LEFT_MOTOROLA).toUpperCase();
				} catch (Exception e) {
				}
				String softkeyLeftMoto1 = "";
				try {
					softkeyLeftMoto1 = getKeyName(SOFT_KEY_LEFT_MOTOROLA1).toUpperCase();
				} catch (Exception e) {
				}
				String softkeyLeftMoto2 = "";
				try {
					softkeyLeftMoto2 = getKeyName(SOFT_KEY_LEFT_MOTOROLA2).toUpperCase();
				} catch (Exception e) {
				}
				if (softkeyLeftMoto.indexOf(SOFT_WORD) >= 0 && softkeyLeftMoto.indexOf("1") >= 0) {
					return SOFT_KEY_LEFT_MOTOROLA;
				} else if (softkeyLeftMoto1.indexOf(SOFT_WORD) >= 0 && softkeyLeftMoto1.indexOf("1") >= 0) {
					return SOFT_KEY_LEFT_MOTOROLA1;
				} else if (softkeyLeftMoto2.indexOf(SOFT_WORD) >= 0 && softkeyLeftMoto2.indexOf("1") >= 0) {
					return SOFT_KEY_LEFT_MOTOROLA2;
				} else if (softkeyLeftMoto.indexOf(SOFT_WORD) >= 0 && softkeyLeftMoto.indexOf("LEFT") >= 0) {
					return SOFT_KEY_LEFT_MOTOROLA;
				} else if (softkeyLeftMoto1.indexOf(SOFT_WORD) >= 0 && softkeyLeftMoto1.indexOf("LEFT") >= 0) {
					return SOFT_KEY_LEFT_MOTOROLA1;
				} else if (softkeyLeftMoto2.indexOf(SOFT_WORD) >= 0 && softkeyLeftMoto2.indexOf("LEFT") >= 0) {
					return SOFT_KEY_LEFT_MOTOROLA2;
				}

			} else if (platformName.equals(KuixConstants.PLATFORM_NOKIA)) {
				return SOFT_KEY_LEFT_NOKIA;
			} else if (platformName.equals(KuixConstants.PLATFORM_SAMSUNG)) {
				return SOFT_KEY_LEFT_SAMSUNG;
			} else if (platformName.equals(KuixConstants.PLATFORM_SIEMENS)) {
				String leftKeySiemensName = getKeyName(SOFT_KEY_LEFT_SIEMENS).toUpperCase();
				if (leftKeySiemensName.indexOf(SOFT_WORD) >= 0) {
					if (leftKeySiemensName.indexOf("1") >= 0) {
						return SOFT_KEY_LEFT_SIEMENS;
					} else if (leftKeySiemensName.indexOf("LEFT") >= 0) {
						return SOFT_KEY_LEFT_SIEMENS;
					}
				}
			} else if (platformName.equals(KuixConstants.PLATFORM_SONY_ERICSSON)) {
				return SOFT_KEY_LEFT_SE;
			} else if (platformName.equals(KuixConstants.PLATFORM_SUN)) {
				return SOFT_KEY_LEFT_DEFAULT;
			} else if (platformName.equals(KuixConstants.PLATFORM_INTENT)) {
				return SOFT_KEY_LEFT_INTENT;
			} else if (platformName.equals(KuixConstants.PLATFORM_NOT_DEFINED)) {
				for (int i = -125; i <= 125; i++) {
					if (i == 0) {
						i++;
					}
					final String s = getKeyName(i).toUpperCase();
					if (s.indexOf(SOFT_WORD) >= 0) {
						if (s.indexOf("1") >= 0) {
							keyCode = i;
							break;
						}
						if (s.indexOf("LEFT") >= 0) {
							keyCode = i;
							break;
						}
					}
				}
			}
			if (keyCode == 0) {
				return SOFT_KEY_LEFT_DEFAULT;
			}
		} catch (Throwable iaEx) {
			return SOFT_KEY_LEFT_DEFAULT;
		}
		return keyCode;
	}

	/**
	 * define real right soft key code for current platform
	 * 
	 * @return code
	 */
	private int getRightSoftkeyCode() {
		int keyCode = 0;
		try {
			if (platformName.equals(KuixConstants.PLATFORM_MOTOROLA)) {

				String rightSoftMoto1 = "";
				try {
					rightSoftMoto1 = getKeyName(SOFT_KEY_LEFT_MOTOROLA1).toUpperCase();
				} catch (Exception e) {
				}
				String rightSoftMoto = "";
				try {
					rightSoftMoto = getKeyName(SOFT_KEY_RIGHT_MOTOROLA).toUpperCase();
				} catch (Exception e) {
				}
				String rightSoftMoto2 = "";
				try {
					rightSoftMoto2 = getKeyName(SOFT_KEY_RIGHT_MOTOROLA1).toUpperCase();
				} catch (Exception e) {
				}
				if (rightSoftMoto.indexOf(SOFT_WORD) >= 0 && rightSoftMoto.indexOf("2") >= 0) {
					return SOFT_KEY_RIGHT_MOTOROLA;
				} else if (rightSoftMoto1.indexOf(SOFT_WORD) >= 0 && rightSoftMoto1.indexOf("2") >= 0) {
					return SOFT_KEY_RIGHT_MOTOROLA;
				} else if (rightSoftMoto2.indexOf(SOFT_WORD) >= 0 && rightSoftMoto2.indexOf("2") >= 0) {
					return SOFT_KEY_RIGHT_MOTOROLA1;
				} else if (rightSoftMoto.indexOf(SOFT_WORD) >= 0 && rightSoftMoto.indexOf("RIGHT") >= 0) {
					return SOFT_KEY_LEFT_MOTOROLA;
				} else if (rightSoftMoto1.indexOf(SOFT_WORD) >= 0 && rightSoftMoto1.indexOf("RIGHT") >= 0) {
					return SOFT_KEY_RIGHT_MOTOROLA1;
				} else if (rightSoftMoto2.indexOf(SOFT_WORD) >= 0 && rightSoftMoto2.indexOf("RIGHT") >= 0) {
					return SOFT_KEY_RIGHT_MOTOROLA;
				}

			} else if (platformName.equals(KuixConstants.PLATFORM_NOKIA)) {
				return SOFT_KEY_RIGHT_NOKIA;
			} else if (platformName.equals(KuixConstants.PLATFORM_SAMSUNG)) {
				return SOFT_KEY_RIGHT_SAMSUNG;
			} else if (platformName.equals(KuixConstants.PLATFORM_SIEMENS)) {
				String rightSoftSiemens = getKeyName(SOFT_KEY_RIGHT_SIEMENS).toUpperCase();
				if (rightSoftSiemens.indexOf(SOFT_WORD) >= 0) {
					if (rightSoftSiemens.indexOf("4") >= 0) {
						return SOFT_KEY_RIGHT_SIEMENS;
					} else if (rightSoftSiemens.indexOf("RIGHT") >= 0) {
						return SOFT_KEY_RIGHT_SIEMENS;
					}
				}
			} else if (platformName.equals(KuixConstants.PLATFORM_SONY_ERICSSON)) {
				return SOFT_KEY_RIGHT_SE;
			} else if (platformName.equals(KuixConstants.PLATFORM_SUN)) {
				return SOFT_KEY_RIGHT_DEFAULT;
			} else if (platformName.equals(KuixConstants.PLATFORM_INTENT)) {
				return SOFT_KEY_RIGHT_INTENT;
			} else if (platformName.equals(KuixConstants.PLATFORM_NOT_DEFINED)) {
				for (int i = -125; i <= 125; i++) {
					if (i == 0) {
						i++;
					}
					String keyName = getKeyName(i).toUpperCase();
					if (keyName.indexOf(SOFT_WORD) >= 0) {
						if (keyName.indexOf("2") >= 0) {
							keyCode = i;
							break;
						} else if (keyName.indexOf("4") >= 0) {
							keyCode = i;
							break;
						} else if (keyName.indexOf("RIGHT") >= 0) {
							keyCode = i;
							break;
						}
					}
				}
			}
			if (keyCode == 0) {
				return SOFT_KEY_RIGHT_DEFAULT;
			}
		} catch (Throwable iaEx) {
			return SOFT_KEY_RIGHT_DEFAULT;
		}
		return keyCode;
	}

	/**
	 * define real key's C or DELETE code for current platform
	 * 
	 * @return code
	 */
	private int getDeleteKeyCode() {
		try {
			if (platformName.equals(KuixConstants.PLATFORM_NOKIA)) {
				return DELETE_KEY_NOKIA;
			} else if (platformName.equals(KuixConstants.PLATFORM_SAMSUNG)) {
				if (getKeyName(DELETE_KEY_SAMSUNG).toUpperCase().indexOf("CLEAR") >= 0) {
					return DELETE_KEY_SAMSUNG;
				}
			} else if (platformName.equals(KuixConstants.PLATFORM_SONY_ERICSSON)) {
				return DELETE_KEY_SE;
			} else if (platformName.equals(KuixConstants.PLATFORM_SUN)) {
				return DELETE_KEY_DEFAULT;
			} else if (platformName.equals(KuixConstants.PLATFORM_BLACKBERRY)) {
				return DELETE_KEY_BLACKBERRY;
			} else if (platformName.equals(KuixConstants.PLATFORM_MOTOROLA)) {
				return DELETE_KEY_MOTOROLA;
			}
		} catch (Throwable e) {
			return DELETE_KEY_DEFAULT;
		}
		return 0;
	}

	/**
	 * define real key's BACK code for current platform
	 * 
	 * @return code
	 */
	private int getBackKeyCode() {
		if (platformName.equals(KuixConstants.PLATFORM_SONY_ERICSSON)) {
			return BACK_KEY_SE;
		}
		return 0;
	}

	/**
	 * name of curent platform
	 * 
	 * @return PLATFORM_NAME
	 */
	public String getPlatformName() {
		return platformName;
	}

	/**
	 * Used to adopt key code to predefined constances, which are platform
	 * independent. 
	 * 
	 * @param keyCode This code is sent by platform to canvas and redirected
	 *            here
	 * @return this keycode is equal to one of our constants declared in this
	 *         class
	 */
	public int adoptKeyCode(int keyCode) {
		switch (keyCode) {
			case Canvas.KEY_NUM0:
				return KuixConstants.KUIX_KEY_0;
			case Canvas.KEY_NUM1:
				return KuixConstants.KUIX_KEY_1;
			case Canvas.KEY_NUM2:
				return KuixConstants.KUIX_KEY_2;
			case Canvas.KEY_NUM3:
				return KuixConstants.KUIX_KEY_3;
			case Canvas.KEY_NUM4:
				return KuixConstants.KUIX_KEY_4;
			case Canvas.KEY_NUM5:
				return KuixConstants.KUIX_KEY_5;
			case Canvas.KEY_NUM6:
				return KuixConstants.KUIX_KEY_6;
			case Canvas.KEY_NUM7:
				return KuixConstants.KUIX_KEY_7;
			case Canvas.KEY_NUM8:
				return KuixConstants.KUIX_KEY_8;
			case Canvas.KEY_NUM9:
				return KuixConstants.KUIX_KEY_9;
			case Canvas.KEY_STAR:
				return KuixConstants.KUIX_KEY_STAR;
			case Canvas.KEY_POUND:
				return KuixConstants.KUIX_KEY_POUND;
			default:
				if (keyCode == softKeyLeft) {
					return KuixConstants.KUIX_KEY_SOFT_LEFT;
				} else if (keyCode == softKeyRight) {
					return KuixConstants.KUIX_KEY_SOFT_RIGHT;
				} else if (keyCode == softKeyDelete) {
					return KuixConstants.KUIX_KEY_DELETE;
				} else if (keyCode == softKeyBack) {
					return KuixConstants.KUIX_KEY_BACK;
				} else if (keyCode == PENCIL_KEY_NOKIA) {
					return KuixConstants.KUIX_KEY_PENCIL;
				} else {
					try {
						final int gameAction;
						gameAction = getGameAction(keyCode);
						if (gameAction == Canvas.UP) {
							return KuixConstants.KUIX_KEY_UP;
						} else if (gameAction == Canvas.DOWN) {
							return KuixConstants.KUIX_KEY_DOWN;
						} else if (gameAction == Canvas.LEFT) {
							return KuixConstants.KUIX_KEY_LEFT;
						} else if (gameAction == Canvas.RIGHT) {
							return KuixConstants.KUIX_KEY_RIGHT;
						} else if (gameAction == Canvas.FIRE) {
							return KuixConstants.KUIX_KEY_FIRE;
						}
					} catch (IllegalArgumentException e) {
					}
				}
				break;
		}
		return KuixConstants.NOT_DEFINED_KEY;
	}

}
