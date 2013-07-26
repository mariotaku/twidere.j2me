package org.kxml.wap;

import org.kxml.*;
import org.kxml.parser.*;


public class WapExtensionEvent extends ParseEvent {

    int id;
    Object content;
	
    public WapExtensionEvent (int id, Object content) {
	super (id, null);
	this.content = content;
    }



    /** returns Xml.WAP_EXTENSION */
    
    public int getType () {
	return Xml.WAP_EXTENSION;
    }

    /** returns the id of the WAP extendsion, one of Wbxml.EXT_0,
	Wbxml.EXT_1, Wbxml.EXT_2, Wbxml.EXT_T_0, Wbxml.EXT_T_1,
	Wbxml.EXT_T_2, Wbxml.EXT_I_0, Wbxml.EXT_I_1, Wbxml.EXT_I_2, or
	Wbxml.OPAQUE. */

    public int getId () {
	return id;
    }


    /** returns the content of the wap extension. The class
	depends on the type of the extension. null for EXT_0..EXT_2,
	String for EXT_I_0..EXT_I_2, Integer for EXT_T_0..EXT_T_2,
	byte [] for OPAQUE. */

    public Object getContent () {
	return content;
    }
}
