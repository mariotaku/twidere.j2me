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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Hashtable;

import org.kalmeo.util.StringTokenizer;

/**
 * @author bbeaulant
 */
public class LightXmlParser {

	private static final String WHITESPACE_DEFINITIONS = " \t\n\r";
	
	/**
	 * Parse an XML input stream
	 * 
	 * @param inputStream
	 * @param encoding
	 * @param handler
	 * @throws IOException
	 */
	public static void parse(InputStream inputStream, String encoding, LightXmlParserHandler handler) throws IOException {
		
		if (handler == null) {
			throw new IllegalArgumentException("No handler");
		}
		handler.startDocument();
		
		int c;
		StringBuffer text = new StringBuffer();
		boolean tagOpen = false;
		
		// Read first non-whitespace xml char
		while ((c = inputStream.read()) != -1 && WHITESPACE_DEFINITIONS.indexOf(c) != -1); 
		
		// Check processing instructions (only if no tag was read before) to extract encoding
		boolean invalidHeader = false;
		if (c == '<') {
			tagOpen = true;
			if ((c = inputStream.read()) == '?') {
				
				boolean question = false; // read until '?>'
				while (((c = inputStream.read()) != '>') || !question) {
					question = (c == '?');
					if (!question) {
						text.append((char) c);
					}
				}
				if (text.length() != 0) {
					String desiredEncoding = extractEncodingFromProcessingInstructions(text.toString());
					if (desiredEncoding != null) {
						encoding = desiredEncoding;
					}
					text.setLength(0);
				}
				
				// Read next non-whitespace char
				while ((c = inputStream.read()) != -1 && WHITESPACE_DEFINITIONS.indexOf(c) != -1); 
				
				// Check if file starts with a '<'
				if (c == '<') {
					c = inputStream.read();
				} else {
					invalidHeader = true;
				}
				
			}
		} else if (inputStream.available() != 0) {
			invalidHeader = true;
		}
		
		// An XML file need to start with a '<'
		if (invalidHeader) {
			throw new IllegalArgumentException("Invalid xml header");
		}
		
		// Create an InputStreamReader with the wanted encoding
		Reader reader;
		if (encoding == null) {
			reader = new InputStreamReader(inputStream);
		} else {
			reader = new InputStreamReader(inputStream, encoding);
		}

		// Read contents
		try {
			Object[] parentlist = null;
			Object current = null;
			Hashtable attributeList = null;
			boolean isCDATA = false;
			while (c != -1) {
				if (c == '<' || tagOpen) {
					if (tagOpen) {
						tagOpen = false;
					} else {
						c = reader.read();
					}
					if (c == '/') { // endtag
						if (text.length() > 0) {
							handler.characters(text.toString(), isCDATA);
							isCDATA = false;
							text.setLength(0);
						}
						String tagName = (String) parentlist[2];
						for (int i = 0; i < tagName.length(); i++) { // compare open tag and close tag
							if ((c = reader.read()) != tagName.charAt(i)) {
								throw new IllegalArgumentException("Invalid close tag : " + tagName);
							}
						}
						c = skipWhitespaces(reader.read(), reader);
						if (c != '>') {
							throw new IllegalArgumentException(tagName); // '>'
						}
						c = reader.read();
						handler.endElement(tagName);
						if (parentlist[0] == null) {
							reader.close();
							handler.endDocument();
							return;
						}
						current = parentlist[0];
						parentlist = (Object[]) parentlist[1];
					} else if (c == '!') { 
						if ((c = reader.read()) == '[') {	//'<![CDATA[' ']]>'
							text.setLength(0);
							while ((c = reader.read()) != '[') {
								text.append((char) c);
							}
							if (text.toString().equals("CDATA")) {
								text.setLength(0);
								byte end = 0;
								while (true) {
									c = reader.read();
									if (c == -1 || (c == '>' && end >= 2)) {
										c = reader.read();
										break;
									}
									if (c == ']') {
										if (end < 2) {
											end++;
										}
									} else {
										end = 0;
									}
									text.append((char) c);
								}
								isCDATA = true;
								text.setLength(text.length() - 2);
							}
						} else if (c == '-') { // Comment
							if ((c = reader.read()) == '-') {
								while (true) {
									if (c == -1) {
										break;
									}
									if (c == '-' && (c = reader.read()) == '-' && (c = reader.read()) == '>') {
										c = reader.read();
										break;
									}
									c = reader.read();
								}
							} else {
								throw new IllegalArgumentException("<-");
							}
						}
					} else { // Start or standalone tag
						if (text.length() != 0) {
							handler.characters(text.toString(), isCDATA);
							isCDATA = false;
							text.setLength(0);
						}
						while (">/ \t\n\r".indexOf(c) == -1) {
							text.append((char) c);
							c = reader.read(); 
						}
						String tagName = text.toString();
						if (tagName.length() == 0) {
							throw new IllegalArgumentException("Invalid open tag");
						}
						parentlist = new Object[] { current, parentlist, tagName };
						current = tagName;
						text.setLength(0);
						while (true) {
							boolean whitespace = false;
							while (WHITESPACE_DEFINITIONS.indexOf(c) != -1) {
								c = reader.read();
								whitespace = true;
							}
							if (c == '>') {
								handler.startElement((String) current, attributeList);
								attributeList = null;
								c = reader.read();
								break;
							} else if (c == '/') {
								if ((c = reader.read()) != '>') {
									throw new IllegalArgumentException(tagName); // '>'
								}
								handler.startElement((String) current, attributeList);
								attributeList = null;
								handler.endElement((String) current);
								if (parentlist[0] == null) {
									reader.close();
									handler.endDocument();
									return;
								}
								current = parentlist[0];
								parentlist = (Object[]) parentlist[1];
								c = reader.read();
								break;
							} else if (whitespace) { // Attributes
								while ("= \t\n\r".indexOf(c) == -1) {
									text.append((char) c);
									c = reader.read();
								}
								String key = text.toString();
								text.setLength(0);
								c = skipWhitespaces(c, reader);
								if (c != '=') {
									throw new IllegalArgumentException("=");
								}
								c = skipWhitespaces(reader.read(), reader);
								char quote = (char) c;
								if ((c != '\"') && (c != '\'')) {
									throw new IllegalArgumentException("\" or '");
								}
								c = reader.read();
								while (c != quote && c != -1) {
									c = decodeSpecialChars(c, text, reader);
								}
								if (attributeList == null) {
									attributeList = new Hashtable();
								}
								attributeList.put(key, text.toString());
								text.setLength(0);
								c = reader.read();
							} else {
								throw new IllegalArgumentException("Unexpected character (" + (char) c + ")");
							}
						}
					}
				} else {
					if (WHITESPACE_DEFINITIONS.indexOf(c) != -1) {
						if ((text.length() > 0) && (text.charAt(text.length() - 1) != ' ')) {
							text.append(' ');
						}
						c = reader.read();
					} else {
						c = decodeSpecialChars(c, text, reader);
					}
				}
			}
			throw new IllegalArgumentException("Unexpected end of file");
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

	}
	
	private static String extractEncodingFromProcessingInstructions(String instructions) {
		String tag = null;
		String attribute = null;
		StringTokenizer st = new StringTokenizer(instructions, WHITESPACE_DEFINITIONS);
		while (st.hasMoreTokens()) {
			if (tag == null) {
				tag = st.nextToken();
			} else if (attribute == null) {
				attribute = st.nextToken("= \t\r\n\"");
			} else {
				String value = st.nextToken("= \t\r\n\"");
				if (tag.equals("xml") && attribute.equals("encoding")) {
					return value;
				}
				attribute = null;
			}
		}
		return null;
	}
	
	private static int skipWhitespaces(int c, Reader reader) throws IOException {
		while (WHITESPACE_DEFINITIONS.indexOf(c) != -1) {
			c = reader.read();
		}
		return c;
	}
	
	private static final String ENCODED_SPECIAL_CHARS_PATTERN = "abcdefABCDEF#x0123456789ltgmpquos";
	
	private static int decodeSpecialChars(int c, StringBuffer text, Reader reader) throws IOException {
		if (c == '&') {
			StringBuffer buffer = new StringBuffer();
			while (ENCODED_SPECIAL_CHARS_PATTERN.indexOf(c = reader.read()) != -1 && c != -1) {
				buffer.append((char) c);
			}
			String entity = buffer.toString();
			if (c == ';') {
				if ("lt".equals(entity)) {
					text.append('<');
				} else if ("gt".equals(entity)) {
					text.append('>');
				} else if ("amp".equals(entity)) {
					text.append('&');
				} else if ("quot".equals(entity)) {
					text.append('"');
				} else if ("apos".equals(entity)) {
					text.append('\'');
				} else if (entity.startsWith("#")) {
					boolean hexa = (entity.charAt(1) == 'x');
					text.append((char) Integer.parseInt(entity.substring(hexa ? 2 : 1), hexa ? 16 : 10));
				}
				c = reader.read();
			} else {
				text.append('&').append(entity);
			}
		} else {
			text.append((char) c);
			c = reader.read();
		}
		return c;
	}
}
