// (c) 2001 Stefan Haustein
// Contributors: Bjorn Aadland

package org.kxml.wap;

import java.io.*;
import java.util.*;

import org.kxml.*;
import org.kxml.io.*;
import org.kxml.parser.*;


/** Still Todo: 
   <ul>
   <li>implement Processing Instructions</li>
   <li>implement support for more than one codepages</li>
   </ul>
 */ 




public class WbxmlParser extends AbstractXmlParser {

    InputStream in;

    String [] attrStartTable;
    String [] attrValueTable;
    String [] tagTable;
    String stringTable;

    int version;
    int publicIdentifierId;
    int charSet;

    StartTag current;
    ParseEvent next;
    boolean whitespace;
    
    public WbxmlParser (InputStream in) throws IOException {

	this.in = in;
	
	version = readByte (); 
	publicIdentifierId = readInt ();  
	
	if (publicIdentifierId == 0) readInt (); 
	
	charSet = readInt (); // skip charset
	
	int strTabSize = readInt ();

	StringBuffer buf = new StringBuffer (strTabSize);
	    
	for (int i = 0; i < strTabSize; i++) 
	    buf.append ((char) readByte ());

	stringTable = buf.toString ();
    }



    public ParseEvent peek () throws IOException {

	String s;

	if (next != null) return next;

	if (current != null && current.getDegenerated ()) {
	    next = new Tag 
		(Xml.END_TAG, current, 
		 current.getNamespace (), 
		 current.getName ());

	    current = current.getParent ();
	    return next;
	}

	ParseEvent result = null;
	
	do {
	    int id = in.read ();
	    
	    switch (id) {
	    case -1: 
		if (current != null) 
		    throw new RuntimeException ("unclosed elements: "+current);
		
		next = new ParseEvent (Xml.END_DOCUMENT, null);
		break;
		
	    case Wbxml.SWITCH_PAGE: 
		if (readByte () != 0) 
		    throw new IOException ("Curr. only CP0 supported");
		break;
		
	    case Wbxml.END:
		next = new Tag (Xml.END_TAG, current, 
				current.getNamespace (),
				current.getName ());

		current = current.getParent ();
		break;
		
	    case Wbxml.ENTITY:
		next = new ParseEvent (Xml.TEXT, "" + (char) readInt ());
		break;
		
	    case Wbxml.STR_I: {
		s = readStrI ();
		next = new ParseEvent (whitespace ? Xml.WHITESPACE : Xml.TEXT, s);
		break;
	    }
		
	    case Wbxml.EXT_I_0: 
	    case Wbxml.EXT_I_1: 
	    case Wbxml.EXT_I_2:
	    case Wbxml.EXT_T_0: 
	    case Wbxml.EXT_T_1: 
	    case Wbxml.EXT_T_2:
	    case Wbxml.EXT_0:   
	    case Wbxml.EXT_1:   
	    case Wbxml.EXT_2:
	    case Wbxml.OPAQUE:
		next = parseWapExtension (id);
		break;
		    
	    case Wbxml.PI:
		throw new RuntimeException ("PI curr. not supp.");
		// readPI;
		// break;
		
	    case Wbxml.STR_T: {
		int pos = readInt ();
		int end = stringTable.indexOf ('\0', pos);
		next = new ParseEvent 
		    (Xml.TEXT, stringTable.substring (pos, end));
		break;
	    }
	      
	    default: 
		next = parseElement (id);
	    }
	}
	while (next == null);

	return next;
    }


    public ParseEvent read () throws IOException {
	if (next == null) peek ();
	ParseEvent result = next;
	next = null;
	return result;
    }



    /** For handling wap extensions in attributes, overwrite this
	method, call super and return a corresponding TextEvent. */

    public ParseEvent parseWapExtension (int id) throws IOException {
	
	switch (id) {
	case Wbxml.EXT_I_0: 
	case Wbxml.EXT_I_1: 
	case Wbxml.EXT_I_2:     
	    return new WapExtensionEvent (id, readStrI ());
	    
	case Wbxml.EXT_T_0: 
	case Wbxml.EXT_T_1: 
	case Wbxml.EXT_T_2:
	    return new WapExtensionEvent (id, new Integer (readInt ()));
	    
	case Wbxml.EXT_0:   
	case Wbxml.EXT_1: 
	case Wbxml.EXT_2:
	    return new WapExtensionEvent (id, null);
	    
	case Wbxml.OPAQUE: { 
	    int len = readInt ();
	    byte [] buf = new byte[len];

	    for (int i = 0; i < len; i++)   // enhance with blockread!
		buf [i] = (byte) readByte ();
	    
	    return new WapExtensionEvent (id, buf);
	} // case OPAQUE
	} // SWITCH

	throw new IOException ("illegal id!");
    }
    

    public Vector readAttr () throws IOException {
	
	Vector result = new Vector ();

	int id = readByte ();

	while (id != 1) {
	    
	    String name = resolveId (attrStartTable, id); 
	    StringBuffer value;

	    int cut = name.indexOf ('=');

	    if (cut == -1) 
		value = new StringBuffer (); 
	    else {
		value = new StringBuffer (name.substring (cut+1));
		name = name.substring (0, cut);
	    }
	    
	    id = readByte ();
	    while (id > 128 || id == Wbxml.ENTITY 
		   || id == Wbxml.STR_I || id == Wbxml.STR_T  
		   || (id >= Wbxml.EXT_I_0 && id <= Wbxml.EXT_I_2)
		   || (id >= Wbxml.EXT_T_0 && id <= Wbxml.EXT_T_2)) {		

		switch (id) {
		case Wbxml.ENTITY: 
		    value.append ((char) readInt ());
		    break;

		case Wbxml.STR_I: 
		    value.append (readStrI ());
		    break;
		
		case Wbxml.EXT_I_0: case Wbxml.EXT_I_1: case Wbxml.EXT_I_2:     
		case Wbxml.EXT_T_0: case Wbxml.EXT_T_1: case Wbxml.EXT_T_2:
		case Wbxml.EXT_0:   case Wbxml.EXT_1: case Wbxml.EXT_2:
		case Wbxml.OPAQUE:

		    ParseEvent e = parseWapExtension (id);
		    if (!(e.getType () != Xml.TEXT 
			  && e.getType () != Xml.WHITESPACE))
			throw new RuntimeException ("parse WapExtension must return Text Event in order to work inside Attributes!");
		    
		    value.append (e.getText ());

		    //value.append (handleExtension (id)); // skip EXT in ATTR
		    //break;
 
		case Wbxml.STR_T: 
		    value.append (readStrT ());
		    break;
		   
		default:
		    value.append (resolveId (attrValueTable, id));
		}

		id = readByte ();
	    }

	    result.addElement (new Attribute (null, name, value.toString ()));
	}

	return result;
    }


    String resolveId (String [] tab, int id) throws IOException {
	int idx = (id & 0x07f) - 5;
	if (idx == -1) return readStrT ();
	if (idx < 0 || tab == null 
	    || idx >= tab.length || tab [idx] == null) 
	    throw new IOException ("id "+id+" undef."); 

	return tab [idx];
    }



    StartTag parseElement (int id) throws IOException {

	String tag = resolveId (tagTable, id & 0x03f);

	// ok, now let's care about attrs etc

	try {
	    current = new StartTag 
		(current,             // previous
		 null,                // namespace
		 tag,                 // name
		 (((id & 128) != 0)   // attributes
		  ? readAttr () 
		  : null), 
		 (id & 64) == 0,           // degenerated
		 processNamespaces);  // processing
	}
	catch (Exception e) {
	    throw new ParseException (null, e, -1, -1);
	}

	return current;
    }


    int readByte () throws IOException {
	int i = in.read ();
	if (i == -1) throw new IOException ("Unexpected EOF");
	return i;
    }


    int readInt () throws IOException {
	int result = 0;
	int i;
	
	do {
	    i = readByte ();
	    result = (result << 7) | (i & 0x7f);
	}
	while ((i & 0x80) != 0);

	return result;
    }
    

    String readStrI () throws IOException {
	StringBuffer buf = new StringBuffer ();
	boolean wsp = true;
	while (true) {
	    int i = in.read ();
	    if (i == -1) throw new IOException ("Unexpected EOF");
	    if (i == 0) break;
	    if (i > 32) wsp = false;
	    buf.append ((char) i);
	}
	this.whitespace = wsp;
	return buf.toString ();
    }

    
    String readStrT () throws IOException {
	int pos = readInt ();
	int end = stringTable.indexOf ('\0', pos);

	return stringTable.substring (pos, end);
    }



    /** Sets the tag table for a given page.
     *	The first string in the array defines tag 5, the second tag 6 etc.
     *  Currently, only page 0 is supported
     */

    public void setTagTable (int page, String [] tagTable)  {
	this.tagTable = tagTable; 
	if (page != 0) throw new RuntimeException ("code pages curr. not supp."); 
    }


    /** Sets the attribute start Table for a given page.
     *	The first string in the array defines attribute 
     *  5, the second attribute 6 etc.
     *  Currently, only page 0 is supported. Please use the 
     *  character '=' (without quote!) as delimiter 
     *  between the attribute name and the (start of the) value 
     */
    
    public void setAttrStartTable (int page, String [] attrStartTable) {
	this.attrStartTable = attrStartTable;
	if (page != 0) throw new RuntimeException ("code pages curr. not supp."); 
    }

    /** Sets the attribute value Table for a given page.
     *	The first string in the array defines attribute value 0x85, 
     *  the second attribute value 0x86 etc.
     *  Currently, only page 0 is supported.
     */
    
    public void setAttrValueTable (int page, String [] attrStartTable) {
	this.attrValueTable = attrStartTable;
	if (page != 0) throw new RuntimeException ("code pages curr. not supp."); 
    }


}






