/*
 *				Twidere - Twitter client for Android
 * 
 * Copyright (C) 2012 Mariotaku Lee <mariotaku.lee@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mariotaku.twidere.util;

public final class ArrayUtils {

	private ArrayUtils() {
		
	}

	public static boolean contains(final long[] array, final long value) {
		if (array == null) return false;
		final int length = array.length;
		for (int i = 0; i < length; i++) {
			final long item = array[i];
			if (item == value) return true;
		}
		return false;
	}

	public static boolean contains(final Object[] array, final Object[] values) {
		if (array == null || values == null) return false;
		final int array_length = array.length;
		for (int i = 0; i < array_length; i++) {
			final Object item = array[i];
			final int values_length = values.length;
			for (int j = 0; i < values_length; i++) {
				final Object value = values[i];
				if (item == null || value == null) {
					if (item == value) return true;
					continue;
				}
				if (item.equals(value)) return true;
			}
		}
		return false;
	}

	public static boolean contentMatch(final Object[] array1, final Object[] array2) {
		if (array1 == null || array2 == null) return array1 == array2;
		if (array1.length != array2.length) return false;
		final int length = array1.length;
		for (int i = 0; i < length; i++) {
			if (!contains(array2, new Object[] { array1[i] })) return false;
		}
		return true;
	}

	public static int indexOf(final long[] array, final long value) {
		final int length = array.length;
		for (int i = 0; i < length; i++) {
			if (array[i] == value) return i;
		}
		return -1;
	}

	public static int indexOf(final Object[] array, final Object value) {
		final int length = array.length;
		for (int i = 0; i < length; i++) {
			if (array[i].equals(value)) return i;
		}
		return -1;
	}

	public static String mergeArrayToString(final String[] array) {
		if (array == null) return null;
		final StringBuffer builder = new StringBuffer();
		final int length = array.length;
		for (int i = 0; i < length; i++) {
			builder.append(array[i]);
		}
		return builder.toString();
	}

	public static long[] subArray(final long[] array, final int start, final int end) {
		final int length = end - start;
		if (length < 0) throw new IllegalArgumentException();
		final long[] result = new long[length];
		System.arraycopy(array, start, result, 0, length);
		return result;
	}

	public static Object[] subArray(final Object[] array, final int start, final int end) {
		final int length = end - start;
		if (length < 0) throw new IllegalArgumentException();
		final Object[] result = new Object[length];
		System.arraycopy(array, start, result, 0, length);
		return result;
	}

	public static String[] subArray(final String[] array, final int start, final int end) {
		final int length = end - start;
		if (length < 0) throw new IllegalArgumentException();
		final String[] result = new String[length];
		System.arraycopy(array, start, result, 0, length);
		return result;
	}

	public static String toString(final long[] array, final char token, final boolean include_space) {
		if (array == null) return null;
		final StringBuffer builder = new StringBuffer();
		final int length = array.length;
		for (int i = 0; i < length; i++) {
			final String id_string = String.valueOf(array[i]);
			if (id_string != null) {
				if (i > 0) {
					builder.append(token);
					if (include_space) {
						builder.append(' ');
					}
				}
				builder.append(id_string);
			}
		}
		return builder.toString();
	}

	public static String toString(final Object[] array, final char token, final boolean include_space) {
		if (array == null) return null;
		final StringBuffer builder = new StringBuffer();
		final int length = array.length;
		for (int i = 0; i < length; i++) {
			final String id_string = String.valueOf(array[i]);
			if (id_string != null) {
				if (i > 0) {
					builder.append(token);
					if (include_space) {
						builder.append(' ');
					}
				}
				builder.append(id_string);
			}
		}
		return builder.toString();
	}

}
