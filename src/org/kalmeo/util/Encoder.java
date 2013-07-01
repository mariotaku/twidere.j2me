/*
 * This file is part of org.kalmeo.util.
 * 
 * org.kalmeo.util is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * org.kalmeo.util is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with org.kalmeo.util.  If not, see <http://www.gnu.org/licenses/>.
 *  
 * Creation date : 3 déc. 07
 * Copyright (c) Kalmeo 2007-2008. All rights reserved.
 * http://www.kalmeo.org
 */

package org.kalmeo.util;


/**
 * @author bbeaulant
 */
public class Encoder {

	private static final String HASH = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+-";
	
	/**
	 * Encode 64 a byte array.
	 * 
	 * @param rawData
	 * @return base 64 encoded string
	 */
	public static String encode64(byte[] rawData) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < rawData.length; i += 3) {
			
			// Encode a block
			int block = 0;
			int slack = rawData.length - i - 1;
			int end = (slack >= 2) ? 2 : slack;
			for (int j = 0; j <= end; j++) {
				byte b = rawData[i + j];
				int neuter = (b < 0) ? b + 256 : b;
				block += neuter << (8 * (2 - j));
			}
			char[] base64 = new char[4];
			for (int j = 0; j < 4; j++) {
				int sixbit = (block >>> (6 * (3 - j))) & 0x3f;
				base64[j] = HASH.charAt(sixbit);
			}

			// Add padding at the end
			if (slack < 1) {
				base64[2] = '=';
			}
			if (slack < 2) {
				base64[3] = '=';
			}
			
			buffer.append(base64);
		}
		return buffer.toString();
	}
	
	/**
	 * Decode 64 a a String to a byte array.
	 * 
	 * @param base64Data
	 * @return base 64 decoded byte
	 */
	public static byte[] decode64(String base64Data) {
        try {

			String base64 = new String(base64Data + "====");

			// Calculate the padding
			int pad = 0;
			for (int i = base64Data.length() - 1; base64Data.charAt(i) == '='; i--) {
				pad++;
			}

			// Fill the byte array
			byte[] rawData = new byte[base64Data.length() * 6 / 8 - pad];
			int rawIndex = 0;
			for (int i = 0; i < base64Data.length(); i += 4) {
				int block = (HASH.indexOf(base64.charAt(i)) << 18) + 
							(HASH.indexOf(base64.charAt(i + 1)) << 12) + 
							(HASH.indexOf(base64.charAt(i + 2)) << 6) + 
							(HASH.indexOf(base64.charAt(i + 3)));
				for (int j = 0; j < 3 && rawIndex + j < rawData.length; j++) {
					rawData[rawIndex + j] = (byte) ((block >> (8 * (2 - j))) & 0xff);
				}
				rawIndex += 3;
			}
			
			return rawData;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * @param string
	 * @return The URL encoded String
	 */
	public static String urlEncode(String string) {
		if (string != null) {
			StringBuffer buffer = new StringBuffer();
			int i = 0;
			try {
				while (true) {
					int b = (int) string.charAt(i++);
					if ((b >= 0x30 && b <= 0x39) || (b >= 0x41 && b <= 0x5A) || (b >= 0x61 && b <= 0x7A)) {
						buffer.append((char) b);
					} else {
						buffer.append("%");
						if (b <= 0xf)
							buffer.append("0");
						buffer.append(Integer.toHexString(b));
					}
				}
			} catch (Exception e) {
			}
			return buffer.toString();
		}
		return null;
	}
	
}
