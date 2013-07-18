/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mariotaku.twidere.util;

import java.io.UnsupportedEncodingException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 *
 * @author mariotaku
 */
public class DataStore {

	private final RecordStore recordStore;
	private final JSONObject json;

	private DataStore(final String name, final boolean createIfNecessary, final boolean is_private, final boolean writeable) throws RecordStoreException {
		final int authmode = is_private ? RecordStore.AUTHMODE_PRIVATE : RecordStore.AUTHMODE_ANY;
		recordStore = RecordStore.openRecordStore(name, createIfNecessary, authmode, writeable);
		if (recordStore.getNumRecords() == 0) {
			recordStore.addRecord(null, 0, 0);
			json = new JSONObject();
		} else {
			final JSONObject tmp = createJSONObject(recordStore.getRecord(1));
			this.json = tmp != null ? tmp : new JSONObject();
		}
	}
	
	public int count() {
		return json.length();
	}
	
	public boolean isEmpty() {
		return count() == 0;
	}

	public static DataStore openDataStore(final String name) {
		return openDataStore(name, true, true, true);
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
	
	public void putJSONArray(final String key, final JSONArray value) {
		try {
			json.put(key, value != null ? value : JSONObject.NULL);
		} catch (JSONException ex) {
		}
	}
	
	public void putJSONObject(final String key, final JSONObject value) {
		try {
			json.put(key, value != null ? value : JSONObject.NULL);
		} catch (JSONException ex) {
		}
	}

	public void putString(final String key, final String value) {
		try {
			json.put(key, value);
		} catch (JSONException ex) {
		}
	}

	public void putBoolean(final String key, final boolean value) {
		try {
			json.put(key, value);
		} catch (JSONException ex) {
		}
	}

	public void putInt(final String key, final int value) {
		try {
			json.put(key, value);
		} catch (JSONException ex) {
		}
	}

	public String getString(final String key, final String def) {
		return json.optString(key, def);
	}

	public boolean getBoolean(final String key) {
		return getBoolean(key, false);
	}
	
	public boolean getBoolean(final String key, final boolean def) {
		return json.optBoolean(key, def);
	}

	public int getInt(final String key, final int def) {
		return json.optInt(key, def);
	}
	
	public JSONObject getJSONObject(final String key) {
		return json.optJSONObject(key);
	}
	
	public JSONArray getJSONArray(final String key) {
		return json.optJSONArray(key);
	}

	public void save() throws RecordStoreException {
		final byte[] data;
		try {
			data = json.toString().getBytes("UTF-8");
		} catch (UnsupportedEncodingException ex) {
			throw new RecordStoreException(ex.getMessage());
		}
		if (recordStore.getNumRecords() == 0) {
			recordStore.addRecord(data, 0, data.length);
		} else {
			recordStore.setRecord(1, data, 0, data.length);
		}
	}

	public void close() throws RecordStoreException {
		recordStore.closeRecordStore();
	}

	private static JSONObject createJSONObject(final byte[] data) {
		if (data == null) {
			return null;
		}
		try {
			return data != null ? new JSONObject(new String(data, "UTF-8")) : new JSONObject();
		} catch (UnsupportedEncodingException e) {
		} catch (JSONException e) {
		}
		return null;
	}
}
