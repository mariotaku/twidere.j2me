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
 * Creation date : 1 sept. 2008
 * Copyright (c) Kalmeo 2007-2008. All rights reserved.
 * http://www.kalmeo.org
 */

package org.kalmeo.kuix.core;

import javax.microedition.midlet.MIDlet;

import org.kalmeo.kuix.widget.Desktop;

/**
 * This interface permit to implements custom value during Kuix engine init
 * process.<br>
 * The default implementation of this interface is {@link KuixMIDlet}. But you
 * can init Kuix without the {@link KuixMIDlet} class by implementing you own
 * {@link KuixInitializer} and add this couple of lines to you standard Java ME
 * application :
 * 
 * <pre>
 * KuixInitializer initializer = new MyKuixInitializer();
 * KuixCanvas canvas = new KuixCanvas(initializer, true);
 * KuixConverter converter = new KuixConverter();
 * Kuix.initialize(null, canvas, converter);
 * </pre>
 * 
 * After to be able to switch screen to the KuixCanvas by :
 * 
 * <pre>
 * Display.getDisplay(&lt;myMIDlet&gt;).setCurrent(Kuix.getCanvas());
 * </pre>
 * 
 * @author bbeaulant
 */
public interface KuixInitializer {

	/**
	 * Returns the application MIDlet instance.
	 * 
	 * @return the {@link MIDlet} instance.
	 */
	public MIDlet getMIDlet();

	/**
	 * Returns the initalization background color.
	 * 
	 * @return a color int representation.
	 */
	public int getInitializationBackgroundColor();

	/**
	 * Returns the initalization message color.
	 * 
	 * @return ta color int representation.
	 */
	public int getInitializationMessageColor();

	/**
	 * Returns the initalization message (e.g. "Loading") or <code>null</code>
	 * if no message.
	 * 
	 * @return a string
	 */
	public String getInitializationMessage();

	/**
	 * Returns the initalization message (e.g. "/img/loading.png") or
	 * <code>null</code> if no image.
	 * 
	 * @return a string
	 */
	public String getInitializationImageFile();

	/**
	 * Implement in this method all your style initializations (e.g.
	 * <code>Kuix.loadCss("myStyle.css");</code>).
	 */
	public void initDesktopStyles();

	/**
	 * Init the Desktop's content. This method is call during the initialization
	 * process, then it is preferable to load the first screen there.
	 * 
	 * @param desktop
	 */
	public void initDesktopContent(Desktop desktop);

	/**
	 * Invoked to destroy the MIDlet implementation.
	 */
	public void destroyImpl();

	/**
	 * This method is invoked when a Kuix internal debug infos key event
	 * occured.<br>
	 * You can override this method to implement your own debug infos process.
	 */
	public void processDebugInfosKeyEvent();

}
