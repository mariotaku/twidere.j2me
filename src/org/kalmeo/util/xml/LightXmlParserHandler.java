/*
 * This file is part of org.kalmeo.util.
 * 
 * org.kalmeo.util is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * org.kalmeo.util is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with org.kalmeo.util.  If not, see <http://www.gnu.org/licenses/>.
 *  
 * Creation date : 13 févr. 08
 * Copyright (c) Kalmeo 2008. All rights reserved.
 */

package org.kalmeo.util.xml;

import java.util.Hashtable;

/**
 * @author bbeaulant
 */
public interface LightXmlParserHandler {

	/**
	 * Receive notification of the beginning of the document.
	 */
	public void startDocument();
	
	/**
	 * Receive notification of the start of an element.
	 * 
	 * @param name
	 * @param attributes
	 */
	public void startElement(String name, Hashtable attributes);
	
	/**
	 * Receive notification of the end of an element.
	 * 
	 * @param name
	 */
	public void endElement(String name);
	
	/**
	 * Receive notification of character data inside an element.
	 * 
	 * @param characters
	 * @param isCDATA
	 */
	public void characters(String characters, boolean isCDATA);
	
	/**
	 * Receive notification of the end of the document.
	 */
	public void endDocument();
	
}
