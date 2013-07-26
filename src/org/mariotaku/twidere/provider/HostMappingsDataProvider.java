/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mariotaku.twidere.provider;

import org.kalmeo.kuix.core.model.DataProvider;
import org.kalmeo.util.LinkedListItem;

/**
 *
 * @author mariotaku
 */
public class HostMappingsDataProvider extends DataProvider {

	public final static String LIST_PROPERTY = "mappings";

	public class HostMappingItem extends DataProvider {

		private final static String HOST_PROPERTY = "host";
		private final static String ADDRESS_PROPERTY = "address";
		public String host, address;

		/* (non-Javadoc)
		 * @see org.kalmeo.kuix.core.model.DataProvider#getUserDefinedValue(java.lang.String)
		 */
		protected Object getUserDefinedValue(String property) {
			if (HOST_PROPERTY.equals(property)) {
				return host;
			}
			if (ADDRESS_PROPERTY.equals(property)) {
				return address;
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see org.kalmeo.kuix.core.model.DataProvider#compareTo(org.kalmeo.util.LinkedListItem, int)
		 */
		public int compareTo(LinkedListItem item, int flag) {
			if (item instanceof HostMappingItem) {
				HostMappingItem media = (HostMappingItem) item;
				return host.compareTo(media.host);
			}
			return 0;
		}
	}
}
