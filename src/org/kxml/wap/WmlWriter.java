// provided by Michael Wallbaum

package org.kxml.wap;

import java.io.*;

/** A parser for WML built on top of the WbxmlParser by 
    setting the corresponding TagTable, AttrStartTable and
    AttrValueTable defined in the class Wml */


public class WmlWriter extends WbxmlWriter {

    public WmlWriter (OutputStream out) throws IOException {

	super (out);
	
	setTagTable (0, Wml.tagTable);
	setAttrStartTable (0, Wml.attrStartTable);
	setAttrValueTable (0, Wml.attrValueTable);
    }

    /*
      public ParseEvent parseWapExtension (int id) {
    
      public void ext_i (int id, String par) throws SAXException {
      String dec = "$("+par+")";

	dh.characters (dec.toCharArray (), 0, dec.length ());
    }
    
    public void ext_t (int id, int par) throws SAXException {

	StringBuffer buf = new StringBuffer ();
	
	while (stringTable [par] != 0) 
	    buf.append (stringTable [par++]);
 
	ext_i (id, buf.toString ());
    }

    public void opaque (byte [] bytes) throws SAXException {
	throw new SAXException ("OPAQUE invalid in WML");
    }

    public void ext (int id)  throws SAXException {
	throw new SAXException ("EXT_"+id+" reserved in WML");
    }

    */
}

