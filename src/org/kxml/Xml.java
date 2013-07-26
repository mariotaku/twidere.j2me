/* kXML
 *
 * The contents of this file are subject to the Enhydra Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License
 * on the Enhydra web site ( http://www.enhydra.org/ ).
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific terms governing rights and limitations
 * under the License.
 *
 * The Initial Developer of kXML is Stefan Haustein. Copyright (C)
 * 2000, 2001 Stefan Haustein, D-46045 Oberhausen (Rhld.),
 * Germany. All Rights Reserved.
 *
 * Contributor(s): Paul Palaszewski, Wilhelm Fitzpatrick, 
 *                 Eric Foster-Johnson
 *
 * */
package org.kxml;

/** A class containing several static xml methods, mainly for escaping
special characters like angle brakets and quotes. This class
contains also some (partially shared) constants for the parser and
kDOM. */
public class Xml {

	public static final String NO_NAMESPACE = "";
	/** XmlReader return value before the first call to next or skip */
	public static final int START_DOCUMENT = 0;
	/** Integer constant for comments */
	public static final int COMMENT = 1;
	/** Integer constant for doctype */
	public static final int DOCTYPE = 2;
	/** Integer constant for elements */
	public static final int ELEMENT = 4;
	/** Integer constant returned by ParseEvent.getType if the end of
	the document has been reached */
	public static final int END_DOCUMENT = 8;
	/** Integer constant assigned to an EndTag parse event */
	public static final int END_TAG = 16;
	/** Integer constant assigned to a processing instruction */
	public static final int PROCESSING_INSTRUCTION = 32;
	/** Integer constant assigned to StartTag parse event */
	public static final int START_TAG = 64;
	/** Integer constant assigned to text nodes and events */
	public static final int TEXT = 128;
	/** Integer constant for whitespace nodes and events */
	public static final int WHITESPACE = 256;
	/** minimum escaping, quotes are not escaped */
	public static final int ENCODE_MIN = 0;
	/** forces escaping of quotes */
	public static final int ENCODE_QUOT = 1;
	/** forces escaping of all character coded greater than 127 */
	public static int ENCODE_128 = 2;
	/** Constant identifying wap extension events */
	public static final int WAP_EXTENSION = 1024;

	/** convenience method for encode (String raw, ENCODE_MIN) */
	public static String encode(String raw) {
		return encode(raw, ENCODE_MIN);
	}


	/* encodes an attribute with the given name and value.  A single
	space is inserted before the name of the attribute 
	
	public static String encodeAttr (String name, String value) {
	return " "+name+"=\"" + encode (value, ENCODE_QUOT) + "\"";
	}
	 */
	/** encodes a string escaping less than etc. */
	public static String encode(String raw, int flags) {

		int len = raw.length();

		StringBuffer cooked = new StringBuffer(raw.length());

		for (int i = 0; i < len; i++) {
			char c = raw.charAt(i);

			switch (c) {
				case '<':
					cooked.append("&lt;");
					break;
				case '>':
					cooked.append("&gt;");
					break;
				case '&':
					cooked.append("&amp;");
					break;
				case '"': {
					if ((flags & ENCODE_QUOT) != 0) {
						cooked.append("&quot;");
					} else {
						cooked.append('"');
					}
				}
				break;
				default:
					if (c >= 128 && ((flags & ENCODE_128) != 0)) {
						cooked.append("&#" + ((int) c) + ";");
					} else {
						cooked.append(c);
					}
			}
		}
		return cooked.toString();
	}
	/* quotes a string by adding quote chars at the beginning
	and the end and escaping all quotes and XML special
	chars contained in the string
	
	public static String quote (String q) {
	return "\"" + encode (q, ENCODE_QUOT) + "\"";
	}
	 */
	/*  like String.trim, but additionally all groups of one or more
	whitespace characters in the string are replaced by a single
	space (0x040)
	
	public static String trim (String s) {
	
	char [] buf = new char [s.length ()]; 
	boolean ignoring = false;
	int targetPos = 0;
	
	for (int i = 0; i < s.length (); i++) {
	char c = s.charAt (i);
	
	if (c <= ' ') 
	ignoring = true;
	else {
	if (ignoring) {
	if (targetPos != 0) buf [targetPos++] = ' '; 
	ignoring = false;
	}
	buf [targetPos++] = c;
	}
	}
	
	return new String (buf, 0, targetPos);
	}
	
	 */
}
