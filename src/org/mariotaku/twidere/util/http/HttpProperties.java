/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mariotaku.twidere.util.http;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 *
 * @author mariotaku
 */
public class HttpProperties {
	
	private final Hashtable valueTable = new Hashtable();
	private final Hashtable nameTable = new Hashtable();
	
	public void put(String name, String value) {
		if (name == null) throw new NullPointerException();
		final String nameLower = name.toLowerCase();
		nameTable.put(nameLower, name);
		valueTable.put(nameLower, value);
	}
	
	public String get(String name) {
		if (name == null) throw new NullPointerException();
		final String nameLower = name.toLowerCase();
		return (String) valueTable.get(nameLower);
	}
	
	public int size() {
		return valueTable.size();
	}
	
	public void clear() {
		nameTable.clear();
		valueTable.clear();
	}
	
	public Property[] getAll() {
		final int size = nameTable.size();
		final Property[] properties = new Property[size];
		final Enumeration enumeration = nameTable.keys();
		int i = 0;
		while (enumeration.hasMoreElements()) {
			final String nameLower = (String) enumeration.nextElement();
			final String name = (String) nameTable.get(nameLower);
			final String value = (String) valueTable.get(nameLower);
			properties[i] = new Property(name, value);
		}
		return properties;
	}
	
	public static final class Property {
		
		private final String name, value;
		
		Property(final String name, final String value) {
			this.name = name;
			this.value = value;
		}
		
		public String getName() {
			return name;
		}
		
		public String getValue() {
			return value;
		}
		
	}
	
}
