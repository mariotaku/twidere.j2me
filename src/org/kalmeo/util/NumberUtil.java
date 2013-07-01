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
 * Creation date : 17 janv. 08
 * Copyright (c) Kalmeo 2007-2008. All rights reserved.
 * http://www.kalmeo.org
 */

package org.kalmeo.util;

/**
 * @author omarino
 */
public class NumberUtil {

	// formatByte values
	public static final int MEGA_BYTE = 1048576;
	public static final int KILO_BYTE = 1024;
	
	// formatByte flags
	public static final int MEGA_FORMAT = 1;
	public static final int KILO_FORMAT = 2;
	public static final int ALL_FORMAT = 3;
	
	/**
	 * Format a bytes's count in string
	 * 
	 * @param bytes the bytes's count
	 * @param formsAllowed specifie wich form are autorized by the formater.
	 * 		  MEGA_FORMAT return bytes under MegaByte form, Byte else
	 * 		  KILO_FORMAT return bytes under KiloByte form, Byte else
	 * 		  ALL_FORMAT return bytes under MegaByte, KiloByte ot Byte form.
	 * @return formated string containing bytes's count
	 */
	public static String formatByte(int bytes, int formsAllowed) {
		if (bytes > MEGA_BYTE && (formsAllowed & MEGA_FORMAT) == MEGA_FORMAT) {
			return Integer.toString(bytes / MEGA_BYTE) + "M";
		}
		if (bytes > KILO_BYTE && (formsAllowed & KILO_FORMAT) == KILO_FORMAT) {
			return Integer.toString(bytes / KILO_BYTE) + "K";
		}
		return Integer.toString(bytes);
	}
	
	/**
	 * Format an integer value with specifie integer digits number
	 * 
	 * @param value the value wich be formated
	 * @param numDigit specifie the integer part size
	 * @return formated integer
	 */
	public static String formatInt(int value, int numDigit) {
		if (numDigit == 0) {
			return "";
		}

		// First parse value to string
		String stringValue = Integer.toString(value);

		// numDigit lower than 0 imply no limit is fixed
		if (numDigit < 0) {
			return stringValue;
		}

		// Remove minus sign if value lower than 0
		int lengthValue = stringValue.length();
		boolean negative;
		if (negative = value < 0) {
			stringValue = stringValue.substring(1);
			lengthValue--;
		}

		// If stringValue's length greater than numDigit
		// return the numDigit later characters
		// Else complete with "0" characters
		if (lengthValue > numDigit) {
			stringValue = stringValue.substring(lengthValue - numDigit);
		} else {
			for (int i = lengthValue; i++ < numDigit; stringValue = "0" + stringValue);
		}

		// Return the value with is sign
		return (negative ? "-" : "") + stringValue;
	}

	/**
	 * Format a fpValue with specified integer and decimal digits number
	 * 
	 * @param fpValue is the fixed point integer wich be formated
	 * @param integerDigit specifie the integer part size
	 * @param decimalDigit specifie the decimal part size
	 * @return formated fixed point
	 */
	public static String formatFP(int fpValue, int integerDigit, int decimalDigit) {
		// Take integer and decimal part of fpValue
		boolean negative = false;
		if (fpValue < 0) {
			negative = true;
		}
		int integerPart = MathFP.toInt(fpValue);
		int decimalPart = MathFP.fracAsInt(fpValue);

		// Format each part
		//  First integer part
		String stringIntegerPart = formatInt(integerPart, integerDigit);

		// Second decimal part
		String stringDecimalPart = Integer.toString(decimalPart);
		// If decimalDigit is negative, no limit is fixed to decimal part 
		if (decimalDigit >= 0) {
			// If there are to much digit stringValue is truncated
			if (stringDecimalPart.length() > decimalDigit) {
				stringDecimalPart = stringDecimalPart.substring(0, decimalDigit);
			}
			// Else if there is not enought digit, decimal part is completed
			for (; stringDecimalPart.length() < decimalDigit; stringDecimalPart += "0");
		}

		// And concatenate them
		String stringValue = "";
		if (stringIntegerPart.length() > 0) {
			stringValue += stringIntegerPart;
		}
		if (stringDecimalPart.length() > 0) {
			stringValue += "." + stringDecimalPart;
		}

		// Add minus sign if integer part si limited to 0 digits and decimal part isn't 'null'
		return (negative && integerDigit == 0 && decimalDigit != 0 ? "-" : "") + stringValue;
	}
	
	/**
	 * Convert a short to a byte array
	 * 
	 * @param value
	 * @return the byte array corresponding to the short value
	 */
	public static byte[] toBytes(short value) {
		byte[] bytes = new byte[2];
		toBytes(value, bytes, 0);
		return bytes;
	}
	
	/**
	 * Convert an int to a byte array
	 * 
	 * @param value
	 * @return the byte array corresponding to the int value
	 */
	public static byte[] toBytes(int value) {
		byte[] bytes = new byte[4];
		toBytes(value, bytes, 0);
		return bytes;
	}
	
	/**
	 * Convert a long to a byte array
	 * 
	 * @param value
	 * @return the byte array corresponding to the int value
	 */
	public static byte[] toBytes(long value) {
		byte[] bytes = new byte[8];
		toBytes(value, bytes, 0);
		return bytes;
	}
	
	/**
	 * Convert a short to a byte array and set it into <code>buffer</code>
	 * at specified <code>offset</code>
	 * 
	 * @param value
	 * @param buffer
	 * @param offset
	 * @return the byte array corresponding to the short value
	 */
	public static void toBytes(short value, byte[] buffer, int offset) {
		if (buffer.length - offset < 2) {
			throw new ArrayIndexOutOfBoundsException();
		}
		
		buffer[offset] = (byte) (value >> 8);
		buffer[offset + 1] = (byte) value;
	}
	
	/**
	 * Convert an int to a byte array and set it into <code>buffer</code>
	 * at specified <code>offset</code>
	 * 
	 * @param value
	 * @param buffer
	 * @param offset
	 * @return the byte array corresponding to the short value
	 */
	public static void toBytes(int value, byte[] buffer, int offset) {
		if (buffer.length - offset < 4) {
			throw new ArrayIndexOutOfBoundsException();
		}
		for (int i = 0; i < 4; ++i) {
			buffer[offset + i] = (byte) (value >> ((3 - i) * 8));
		}
	}
	
	/**
	 * Convert a long to a byte array and set it into <code>buffer</code>
	 * at specified <code>offset</code>
	 * 
	 * @param value
	 * @param buffer
	 * @param offset
	 * @return the byte array corresponding to the short value
	 */
	public static void toBytes(long value, byte[] buffer, int offset) {
		if (buffer.length - offset < 8) {
			throw new ArrayIndexOutOfBoundsException();
		}
		for (int i = 0; i < 8; ++i) {
			buffer[offset + i] = (byte) (value >> ((7 - i) * 8));
		}
	}
	
	/**
	 * Convert a byte array to a short value
	 * 
	 * @param bytes
	 * @param offset
	 * @return the short value corresponding to the 2 first bytes of the byte
	 *         array
	 */
	public static short toShort(byte[] bytes, int offset) {
		short value = 0;
		if (bytes != null && bytes.length >= 2) {
			value += (0x000000FF & bytes[offset]) << 8;
			value += (0x000000FF & bytes[offset + 1]);
		}
		return value;
	}
	
	/**
	 * Convert a byte array to an int value
	 * 
	 * @param bytes
	 * @param offset
	 * @return the int value corresponding to the 4 first bytes of the byte
	 *         array
	 */
	public static int toInt(byte[] bytes, int offset) {
		int value = 0;
		if (bytes != null && bytes.length >= 4) {
			for (int i = 0; i < 4; ++i) {
				value += (0x000000FF & bytes[offset + i]) << ((3 - i) * 8);
			}
		}
		return value;
	}
	
	/**
	 * Convert a byte array to a long value
	 * 
	 * @param bytes
	 * @param offset
	 * @return the long value corresponding to the 8 first bytes of the byte
	 *         array
	 */
	public static long toLong(byte[] bytes, int offset) {
		int value = 0;
		if (bytes != null && bytes.length >= 8) {
			for (int i = 0; i < 8; ++i) {
				value += (0x000000FF & bytes[offset + i]) << ((7 - i) * 8);
			}
		}
		return value;
	}
	
}
