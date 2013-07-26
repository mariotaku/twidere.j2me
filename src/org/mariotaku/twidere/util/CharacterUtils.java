/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mariotaku.twidere.util;

/**
 *
 * @author mariotaku
 */
public class CharacterUtils {

	/**
	 * The minimum value of a Unicode high-surrogate code unit in the
	 * UTF-16 encoding. A high-surrogate is also known as a
	 * <i>leading-surrogate</i>.
	 *
	 * @since 1.5
	 */
	public static final char MIN_HIGH_SURROGATE = '\uD800';
	/**
	 * The maximum value of a Unicode high-surrogate code unit in the
	 * UTF-16 encoding. A high-surrogate is also known as a
	 * <i>leading-surrogate</i>.
	 *
	 * @since 1.5
	 */
	public static final char MAX_HIGH_SURROGATE = '\uDBFF';
	/**
	 * The minimum value of a Unicode low-surrogate code unit in the
	 * UTF-16 encoding. A low-surrogate is also known as a
	 * <i>trailing-surrogate</i>.
	 *
	 * @since 1.5
	 */
	public static final char MIN_LOW_SURROGATE = '\uDC00';
	/**
	 * The maximum value of a Unicode low-surrogate code unit in the
	 * UTF-16 encoding. A low-surrogate is also known as a
	 * <i>trailing-surrogate</i>.
	 *
	 * @since 1.5
	 */
	public static final char MAX_LOW_SURROGATE = '\uDFFF';
	/**
	 * The maximum value of a Unicode surrogate code unit in the UTF-16 encoding.
	 *
	 * @since 1.5
	 */
	public static final char MAX_SURROGATE = MAX_LOW_SURROGATE;
	/**
	 * The minimum value of a supplementary code point.
	 *
	 * @since 1.5
	 */
	public static final int MIN_SUPPLEMENTARY_CODE_POINT = 0x010000;
	/**
	 * The minimum value of a Unicode code point.
	 *
	 * @since 1.5
	 */
	public static final int MIN_CODE_POINT = 0x000000;
	/**
	 * The maximum value of a Unicode code point.
	 *
	 * @since 1.5
	 */
	public static final int MAX_CODE_POINT = 0x10ffff;

	/**
	 * Converts the specified character (Unicode code point) to its
	 * UTF-16 representation. If the specified code point is a BMP
	 * (Basic Multilingual Plane or Plane 0) value, the same value is
	 * stored in <code>dst[dstIndex]</code>, and 1 is returned. If the
	 * specified code point is a supplementary character, its
	 * surrogate values are stored in <code>dst[dstIndex]</code>
	 * (high-surrogate) and <code>dst[dstIndex+1]</code>
	 * (low-surrogate), and 2 is returned.
	 *
	 * @param  codePoint the character (Unicode code point) to be converted.
	 * @param  dst an array of <code>char</code> in which the
	 * <code>codePoint</code>'s UTF-16 value is stored.
	 * @param dstIndex the start index into the <code>dst</code>
	 * array where the converted value is stored.
	 * @return 1 if the code point is a BMP code point, 2 if the
	 * code point is a supplementary code point.
	 * @exception IllegalArgumentException if the specified
	 * <code>codePoint</code> is not a valid Unicode code point.
	 * @exception NullPointerException if the specified <code>dst</code> is null.
	 * @exception IndexOutOfBoundsException if <code>dstIndex</code>
	 * is negative or not less than <code>dst.length</code>, or if
	 * <code>dst</code> at <code>dstIndex</code> doesn't have enough
	 * array element(s) to store the resulting <code>char</code>
	 * value(s). (If <code>dstIndex</code> is equal to
	 * <code>dst.length-1</code> and the specified
	 * <code>codePoint</code> is a supplementary character, the
	 * high-surrogate value is not stored in
	 * <code>dst[dstIndex]</code>.)
	 * @since  1.5
	 */
	public static int toChars(int codePoint, char[] dst, int dstIndex) {
		if (codePoint < 0 || codePoint > MAX_CODE_POINT) {
			throw new IllegalArgumentException();
		}
		if (codePoint < MIN_SUPPLEMENTARY_CODE_POINT) {
			dst[dstIndex] = (char) codePoint;
			return 1;
		}
		toSurrogates(codePoint, dst, dstIndex);
		return 2;
	}

	/**
	 * Converts the specified character (Unicode code point) to its
	 * UTF-16 representation stored in a <code>char</code> array. If
	 * the specified code point is a BMP (Basic Multilingual Plane or
	 * Plane 0) value, the resulting <code>char</code> array has
	 * the same value as <code>codePoint</code>. If the specified code
	 * point is a supplementary code point, the resulting
	 * <code>char</code> array has the corresponding surrogate pair.
	 *
	 * @param  codePoint a Unicode code point
	 * @return a <code>char</code> array having
	 *         <code>codePoint</code>'s UTF-16 representation.
	 * @exception IllegalArgumentException if the specified
	 * <code>codePoint</code> is not a valid Unicode code point.
	 * @since  1.5
	 */
	public static char[] toChars(int codePoint) {
		if (codePoint < 0 || codePoint > MAX_CODE_POINT) {
			throw new IllegalArgumentException();
		}
		if (codePoint < MIN_SUPPLEMENTARY_CODE_POINT) {
			return new char[] { (char) codePoint };
		}
		char[] result = new char[2];
		toSurrogates(codePoint, result, 0);
		return result;
	}

	static void toSurrogates(int codePoint, char[] dst, int index) {
		int offset = codePoint - MIN_SUPPLEMENTARY_CODE_POINT;
		dst[index + 1] = (char) ((offset & 0x3ff) + MIN_LOW_SURROGATE);
		dst[index] = (char) ((offset >>> 10) + MIN_HIGH_SURROGATE);
	}
}
