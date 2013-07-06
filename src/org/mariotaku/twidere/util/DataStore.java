/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mariotaku.twidere.util;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/**
 *
 * @author mariotaku
 */
public class DataStore {

	private final RecordStore recordStore;
	
	private DataStore(final String name, final boolean createIfNecessary, final boolean is_private, final boolean writeable) throws RecordStoreException {
		final int authmode = is_private ? RecordStore.AUTHMODE_PRIVATE : RecordStore.AUTHMODE_ANY;
		recordStore = RecordStore.openRecordStore(name, createIfNecessary, authmode, writeable);
	}
	
	public static DataStore openDataStore(final String name, final boolean createIfNecessary, final boolean is_private, final boolean writeable) {
		try {
			return new DataStore(name, createIfNecessary, is_private, writeable);
		} catch (RecordStoreException ex) {
			return null;
		}
	}
	
	public static boolean deleteDataStore(final String name) {
		try {
			RecordStore.deleteRecordStore(name);
		} catch (RecordStoreException ex) {
			return false;
		}
		return true;
	}
	
}
