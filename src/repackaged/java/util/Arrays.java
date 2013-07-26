package repackaged.java.util;

import java.util.Vector;
import org.mariotaku.twidere.util.ArrayUtils;
import repackaged.java.math.BigInteger;

public class Arrays {

	public static String toString(Object[] array) {
		if (array == null) {
			return null;
		}
		return "[" + ArrayUtils.toString(array, ',', true) + "]";
	}

	public static String toString(long[] array) {
		if (array == null) {
			return null;
		}
		return "[" + ArrayUtils.toString(array, ',', true) + "]";
	}

	private Arrays() {
	}

	public static void fill(byte[] ret, byte v) {
		for (int i = 0; i != ret.length; i++) {
			ret[i] = v;
		}
	}

	public static boolean equals(byte[] a, byte[] a2) {
		if (a == a2) {
			return true;
		}
		if (a == null || a2 == null) {
			return false;
		}
		int length = a.length;
		if (a2.length != length) {
			return false;
		}
		for (int i = 0; i < length; i++) {
			if (a[i] != a2[i]) {
				return false;
			}
		}
		return true;
	}

	public static boolean equals(boolean[] a, boolean[] b) {
		if (a == b) {
			return true;
		}

		if (a == null || b == null) {
			return false;
		}

		if (a.length != b.length) {
			return false;
		}

		for (int i = 0; i != a.length; i++) {
			if (a[i] != b[i]) {
				return false;
			}
		}

		return true;
	}

	public static boolean equals(char[] a, char[] b) {
		if (a == b) {
			return true;
		}

		if (a == null || b == null) {
			return false;
		}

		if (a.length != b.length) {
			return false;
		}

		for (int i = 0; i != a.length; i++) {
			if (a[i] != b[i]) {
				return false;
			}
		}

		return true;
	}

	/**
	 * A constant time equals comparison - does not terminate early if
	 * test will fail.
	 *
	 * @param a first array
	 * @param b second array
	 * @return true if arrays equal, false otherwise.
	 */
	public static boolean constantTimeAreEqual(byte[] a, byte[] b) {
		if (a == b) {
			return true;
		}

		if (a == null || b == null) {
			return false;
		}

		if (a.length != b.length) {
			return false;
		}

		int nonEqual = 0;

		for (int i = 0; i != a.length; i++) {
			nonEqual |= (a[i] ^ b[i]);
		}

		return nonEqual == 0;
	}

	public static boolean equals(int[] a, int[] b) {
		if (a == b) {
			return true;
		}

		if (a == null || b == null) {
			return false;
		}

		if (a.length != b.length) {
			return false;
		}

		for (int i = 0; i != a.length; i++) {
			if (a[i] != b[i]) {
				return false;
			}
		}

		return true;
	}

	public static boolean equals(long[] a, long[] b) {
		if (a == b) {
			return true;
		}

		if (a == null || b == null) {
			return false;
		}

		if (a.length != b.length) {
			return false;
		}

		for (int i = 0; i != a.length; i++) {
			if (a[i] != b[i]) {
				return false;
			}
		}

		return true;
	}

	public static boolean equals(BigInteger[] a, BigInteger[] b) {
		if (a == b) {
			return true;
		}

		if (a == null || b == null) {
			return false;
		}

		if (a.length != b.length) {
			return false;
		}

		for (int i = 0; i != a.length; i++) {
			if (!a[i].equals(b[i])) {
				return false;
			}
		}

		return true;
	}

	public static void fill(char[] array, char value) {
		for (int i = 0; i < array.length; i++) {
			array[i] = value;
		}
	}

	public static void fill(long[] array, long value) {
		for (int i = 0; i < array.length; i++) {
			array[i] = value;
		}
	}

	public static void fill(short[] array, short value) {
		for (int i = 0; i < array.length; i++) {
			array[i] = value;
		}
	}

	public static void fill(int[] array, int value) {
		for (int i = 0; i < array.length; i++) {
			array[i] = value;
		}
	}

	public static int hashCode(byte[] data) {
		if (data == null) {
			return 0;
		}

		int i = data.length;
		int hc = i + 1;

		while (--i >= 0) {
			hc *= 257;
			hc ^= data[i];
		}

		return hc;
	}

	public static int hashCode(char[] data) {
		if (data == null) {
			return 0;
		}

		int i = data.length;
		int hc = i + 1;

		while (--i >= 0) {
			hc *= 257;
			hc ^= data[i];
		}

		return hc;
	}

	public static int hashCode(int[][] ints) {
		int hc = 0;

		for (int i = 0; i != ints.length; i++) {
			hc = hc * 257 + hashCode(ints[i]);
		}

		return hc;
	}

	public static int hashCode(int[] data) {
		if (data == null) {
			return 0;
		}

		int i = data.length;
		int hc = i + 1;

		while (--i >= 0) {
			hc *= 257;
			hc ^= data[i];
		}

		return hc;
	}

	public static int hashCode(short[][][] shorts) {
		int hc = 0;

		for (int i = 0; i != shorts.length; i++) {
			hc = hc * 257 + hashCode(shorts[i]);
		}

		return hc;
	}

	public static int hashCode(short[][] shorts) {
		int hc = 0;

		for (int i = 0; i != shorts.length; i++) {
			hc = hc * 257 + hashCode(shorts[i]);
		}

		return hc;
	}

	public static int hashCode(short[] data) {
		if (data == null) {
			return 0;
		}

		int i = data.length;
		int hc = i + 1;

		while (--i >= 0) {
			hc *= 257;
			hc ^= (data[i] & 0xff);
		}

		return hc;
	}

	public static int hashCode(BigInteger[] data) {
		if (data == null) {
			return 0;
		}

		int i = data.length;
		int hc = i + 1;

		while (--i >= 0) {
			hc *= 257;
			hc ^= data[i].hashCode();
		}

		return hc;
	}

	public static byte[] clone(byte[] data) {
		if (data == null) {
			return null;
		}
		byte[] copy = new byte[data.length];

		System.arraycopy(data, 0, copy, 0, data.length);

		return copy;
	}

	public static byte[][] clone(byte[][] data) {
		if (data == null) {
			return null;
		}

		byte[][] copy = new byte[data.length][];

		for (int i = 0; i != copy.length; i++) {
			copy[i] = clone(data[i]);
		}

		return copy;
	}

	public static byte[][][] clone(byte[][][] data) {
		if (data == null) {
			return null;
		}

		byte[][][] copy = new byte[data.length][][];

		for (int i = 0; i != copy.length; i++) {
			copy[i] = clone(data[i]);
		}

		return copy;
	}

	public static int[] clone(int[] data) {
		if (data == null) {
			return null;
		}
		int[] copy = new int[data.length];

		System.arraycopy(data, 0, copy, 0, data.length);

		return copy;
	}

	public static short[] clone(short[] data) {
		if (data == null) {
			return null;
		}
		short[] copy = new short[data.length];

		System.arraycopy(data, 0, copy, 0, data.length);

		return copy;
	}

	public static BigInteger[] clone(BigInteger[] data) {
		if (data == null) {
			return null;
		}
		BigInteger[] copy = new BigInteger[data.length];

		System.arraycopy(data, 0, copy, 0, data.length);

		return copy;
	}

	public static String[][] clone(String[][] data) {
		if (data == null) {
			return null;
		}

		String[][] copy = new String[data.length][];

		for (int i = 0; i != copy.length; i++) {
			copy[i] = clone(data[i]);
		}

		return copy;
	}

	public static String[] clone(String[] data) {
		if (data == null) {
			return null;
		}
		String[] copy = new String[data.length];

		System.arraycopy(data, 0, copy, 0, data.length);

		return copy;
	}

	public static byte[] copyOf(byte[] data, int newLength) {
		byte[] tmp = new byte[newLength];

		if (newLength < data.length) {
			System.arraycopy(data, 0, tmp, 0, newLength);
		} else {
			System.arraycopy(data, 0, tmp, 0, data.length);
		}

		return tmp;
	}

	public static char[] copyOf(char[] data, int newLength) {
		char[] tmp = new char[newLength];

		if (newLength < data.length) {
			System.arraycopy(data, 0, tmp, 0, newLength);
		} else {
			System.arraycopy(data, 0, tmp, 0, data.length);
		}

		return tmp;
	}

	public static int[] copyOf(int[] data, int newLength) {
		int[] tmp = new int[newLength];

		if (newLength < data.length) {
			System.arraycopy(data, 0, tmp, 0, newLength);
		} else {
			System.arraycopy(data, 0, tmp, 0, data.length);
		}

		return tmp;
	}

	public static long[] copyOf(long[] data, int newLength) {
		long[] tmp = new long[newLength];

		if (newLength < data.length) {
			System.arraycopy(data, 0, tmp, 0, newLength);
		} else {
			System.arraycopy(data, 0, tmp, 0, data.length);
		}

		return tmp;
	}

	public static BigInteger[] copyOf(BigInteger[] data, int newLength) {
		BigInteger[] tmp = new BigInteger[newLength];

		if (newLength < data.length) {
			System.arraycopy(data, 0, tmp, 0, newLength);
		} else {
			System.arraycopy(data, 0, tmp, 0, data.length);
		}

		return tmp;
	}

	public static byte[] copyOfRange(byte[] data, int from, int to) {
		int newLength = getLength(from, to);

		byte[] tmp = new byte[newLength];

		if (data.length - from < newLength) {
			System.arraycopy(data, from, tmp, 0, data.length - from);
		} else {
			System.arraycopy(data, from, tmp, 0, newLength);
		}

		return tmp;
	}

	public static int[] copyOfRange(int[] data, int from, int to) {
		int newLength = getLength(from, to);

		int[] tmp = new int[newLength];

		if (data.length - from < newLength) {
			System.arraycopy(data, from, tmp, 0, data.length - from);
		} else {
			System.arraycopy(data, from, tmp, 0, newLength);
		}

		return tmp;
	}

	public static long[] copyOfRange(long[] data, int from, int to) {
		int newLength = getLength(from, to);

		long[] tmp = new long[newLength];

		if (data.length - from < newLength) {
			System.arraycopy(data, from, tmp, 0, data.length - from);
		} else {
			System.arraycopy(data, from, tmp, 0, newLength);
		}

		return tmp;
	}

	public static BigInteger[] copyOfRange(BigInteger[] data, int from, int to) {
		int newLength = getLength(from, to);

		BigInteger[] tmp = new BigInteger[newLength];

		if (data.length - from < newLength) {
			System.arraycopy(data, from, tmp, 0, data.length - from);
		} else {
			System.arraycopy(data, from, tmp, 0, newLength);
		}

		return tmp;
	}

	private static int getLength(int from, int to) {
		int newLength = to - from;
		if (newLength < 0) {
			StringBuffer sb = new StringBuffer(from);
			sb.append(" > ").append(to);
			throw new IllegalArgumentException(sb.toString());
		}
		return newLength;
	}

	public static byte[] concatenate(byte[] a, byte[] b) {
		if (a != null && b != null) {
			byte[] rv = new byte[a.length + b.length];

			System.arraycopy(a, 0, rv, 0, a.length);
			System.arraycopy(b, 0, rv, a.length, b.length);

			return rv;
		} else if (b != null) {
			return clone(b);
		} else {
			return clone(a);
		}
	}

	public static byte[] concatenate(byte[] a, byte[] b, byte[] c) {
		if (a != null && b != null && c != null) {
			byte[] rv = new byte[a.length + b.length + c.length];

			System.arraycopy(a, 0, rv, 0, a.length);
			System.arraycopy(b, 0, rv, a.length, b.length);
			System.arraycopy(c, 0, rv, a.length + b.length, c.length);

			return rv;
		} else if (b == null) {
			return concatenate(a, c);
		} else {
			return concatenate(a, b);
		}
	}

	public static byte[] concatenate(byte[] a, byte[] b, byte[] c, byte[] d) {
		if (a != null && b != null && c != null && d != null) {
			byte[] rv = new byte[a.length + b.length + c.length + d.length];

			System.arraycopy(a, 0, rv, 0, a.length);
			System.arraycopy(b, 0, rv, a.length, b.length);
			System.arraycopy(c, 0, rv, a.length + b.length, c.length);
			System.arraycopy(d, 0, rv, a.length + b.length + c.length, d.length);

			return rv;
		} else if (d == null) {
			return concatenate(a, b, c);
		} else if (c == null) {
			return concatenate(a, b, d);
		} else if (b == null) {
			return concatenate(a, c, d);
		} else {
			return concatenate(b, c, d);
		}
	}

	public static List asList(Object[] a) {
		return new ArrayList(a);
	}

	public static void sort(Object[] array) {
		Vector v = new Vector();
		for (int count = 0; count < array.length; count++) {
			Comparable s = (Comparable) array[count];
			int i = 0;
			for (i = 0; i < v.size(); i++) {
				int c = s.compareTo((Comparable) v.elementAt(i));
				if (c < 0) {
					v.insertElementAt(s, i);
					break;
				} else if (c == 0) {
					break;
				}
			}
			if (i >= v.size()) {
				v.addElement(s);
			}
		}
		v.copyInto(array);
	}

	private static class ArrayList extends AbstractList {

		private Object[] a;

		ArrayList(Object[] array) {
			a = array;
		}

		public int size() {
			return a.length;
		}

		public Object[] toArray() {
			Object[] tmp = new Object[a.length];
			System.arraycopy(a, 0, tmp, 0, tmp.length);
			return tmp;
		}

		public Object get(int index) {
			return a[index];
		}

		public Object set(int index, Object element) {
			Object oldValue = a[index];
			a[index] = element;
			return oldValue;
		}

		public int indexOf(Object o) {
			if (o == null) {
				for (int i = 0; i < a.length; i++) {
					if (a[i] == null) {
						return i;
					}
				}
			} else {
				for (int i = 0; i < a.length; i++) {
					if (o.equals(a[i])) {
						return i;
					}
				}
			}
			return -1;
		}

		public boolean contains(Object o) {
			return indexOf(o) != -1;
		}
	}
}