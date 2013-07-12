/*
 * @(#)Properties.java	1.8 02/09/24 @(#)
 *
 * Copyright (c) 1995-2001 Sun Microsystems, Inc.  All rights reserved.
 * PROPRIETARY/CONFIDENTIAL
 * Use is subject to license terms.
 */
package repackaged.com.sun.midp.io;

import java.util.Vector;
import twitter2me.internal.util.InternalStringUtil;

/**
 * The <code>Properties</code> class represents a persistent set of
 * properties. Each key and its corresponding value in
 * the property list is a string.
 * <p>
 */
public class Properties {

	/** An appropriate initial size for storage vectors (10). */
	private static int INITIAL_SIZE = 10;
	/** A vector of property keys. */
	private final Vector keys;
	/** A vector of property keys. (lower case) */
	private final Vector lowerKeys;
	/** A vector of property values. */
	private final Vector vals;

	/**
	 * Constructor - creates an empty property list.
	 */
	public Properties() {
		keys = new Vector(INITIAL_SIZE);
		lowerKeys = new Vector(INITIAL_SIZE);
		vals = new Vector(INITIAL_SIZE);
	}

	/**
	 * Store multiple key:value pair.  Provided for parallelism with the 
	 * <tt>getProperty</tt> method. Enforces use of strings for 
	 * property keys and values. 
	 *
	 * @param key the key to be placed into this property list.
	 * @param value the value corresponding to <tt>key</tt>.
	 * @see #getProperty
	 */
	public synchronized void addProperty(String key,
			String value) {
		if (InternalStringUtil.isEmpty(key)) {
			throw new IllegalArgumentException();
		}
		lowerKeys.addElement(key.toLowerCase());
		keys.addElement(key);
		vals.addElement(value);
		return;
	}

	/**
	 * Store a single key:value pair.  Provided for parallelism with the 
	 * <tt>getProperty</tt> method. Enforces use of strings for 
	 * property keys and values.  If a key already exists in storage,
	 * the value corresponing to that key will be replaced and returned.
	 *
	 * @param key the key to be placed into this property list.
	 * @param value the value corresponding to <tt>key</tt>.
	 * @return if the new property value replaces an existing one, the old 
	 * value is returned.  otherwise, null is returned.
	 * @see #getProperty
	 * @see #removeProperty
	 */
	public synchronized String setProperty(String key,
			String value) {

		int idx = lowerKeys.indexOf(key);

		String rv = null;
		if (idx == -1) {    // If I don't have this, add it and return null
			lowerKeys.addElement(key);
			keys.addElement(key);
			vals.addElement(value);
		} else {	    // Else replace it and return the old one.
			rv = (String) vals.elementAt(idx);
			vals.setElementAt(value, idx);
		}
		return rv;
	}

	/**
	 * Replace the value of the property at the given index.
	 *
	 * @param index 0 based index of a property
	 * @param value the new value for the property at <tt>index</tt>.
	 *
	 * @return previous value
	 *
	 * @exception IndexOutOfBoundsException if the index is out of bounds
	 */
	public synchronized String setPropertyAt(int index, String value) {
		String rv = (String) vals.elementAt(index);

		vals.setElementAt(value, index);

		return rv;
	}

	/**
	 * Searches for the property with the specified key in this property list.
	 * The method returns <code>null</code> if the property is not found.
	 *
	 * @param   key   the property key.
	 * @return  the value in this property list with the specified key value.
	 * @exception NullPointerException is thrown if key is <code>null</code>.
	 * @see     #setProperty
	 * @see     #removeProperty
	 */
	public String getProperty(String key) {
		if (key == null) {
			throw new NullPointerException();
		}

		final int idx = lowerKeys.indexOf(key);
		if (idx > -1) {
			return (String) vals.elementAt(idx);
		}
		return null;
	}

	/**
	 * Gets a property value by index. Used by the JadWriter as part of the
	 * JAD Tool.
	 *
	 * @param index 0 based index of a property
	 * @return  the value of the property with the specified index.
	 * @exception ArrayIndexOutOfBoundsException
	 *     if an invalid index was given.
	 */
	public String getValueAt(int index) {
		return (String) vals.elementAt(index);
	}

	/**
	 * Gets a property key by index. Used by the JadWriter as part of the
	 * JAD Tool.
	 *
	 * @param index 0 based index of a property
	 * @return  the key of the property with the specified index.
	 * @exception ArrayIndexOutOfBoundsException
	 *     if an invalid index was given.
	 */
	public String getKeyAt(int index) {
		return (String) keys.elementAt(index);
	}
	
	public boolean hasKey(final String key) {
		return lowerKeys.indexOf(key) != -1;
	}

	/**
	 * Gets the number of properties.
	 *
	 * @return  number of properties
	 */
	public int size() {
		return keys.size();
	}

	/**
	 * Removes a property (key:value pair) from the property
	 * list based on the key string.
	 *
	 * @param key the key to be removed from the property list. 
	 * @return  the element associated with the key.
	 * @see #setProperty
	 * @see #getProperty
	 */
	public synchronized String removeProperty(String key) {
		final int idx = lowerKeys.indexOf(key);
		if (idx > -1) {
			final String rv = (String) vals.elementAt(idx);
			lowerKeys.removeElementAt(idx);
			keys.removeElementAt(idx);
			vals.removeElementAt(idx);
			return rv;
		}
		return null;
	}
}
