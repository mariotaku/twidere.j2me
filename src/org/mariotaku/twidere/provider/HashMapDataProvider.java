/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mariotaku.twidere.provider;

import org.kalmeo.kuix.core.model.DataProvider;
import repackaged.java.util.HashMap;

/**
 *
 * @author mariotaku
 */
public class HashMapDataProvider extends DataProvider {

	private final HashMap map = new HashMap();

	protected Object getUserDefinedValue(String property) {
		if (map.containsKey(property)) {
			return map.get(property);
		}
		return super.getUserDefinedValue(property);
	}

	public void put(String property, Object value) {
		map.put(property, value);
		dispatchUpdateEvent(property);
	}

	public Object get(String property) {
		return getUserDefinedValue(property);
	}
}
