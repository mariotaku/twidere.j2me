/*
 * J2ME Fixed-Point Math Library
 *
 * Dan Carter, 2004
 * http://orbisstudios.com
 */

package org.kalmeo.util;

/**
 * <b>J2ME Fixed-Point Math Library</b>
 *
 * <p>Provided under the artistic license: <a href="http://www.opensource.org/licenses/artistic-license.html">http://www.opensource.org/licenses/artistic-license.html</a></p>
 *
 * <p>Basically it means you can use this library for free, even for commercial purposes.</p>
 *
 * <p><b>References:</b></p>
 * <ul>
 * <li>exp(), log(), atan2() converted from this free, floating-point implementation:<br>
 * <a href="http://www.netlib.org/fdlibm/">http://www.netlib.org/fdlibm</a></li>
 * <li>sin(), asin() converted from the free, fast trigonometric library found at:<br>
 * <a href="http://www.magic-software.com">http://www.magic-software.com</a></li>
 * </ul>
 *
 * @author Dan Carter (<a href="http://orbisstudios.com">orbisstudios.com</a>)
 * @author bbeaulant (Refactor and bug fix)
 */
public abstract class MathFP {

	public static final int DEFAULT_PRECISION = 12;

	/**
	 * number of fractional bits in all operations, do not modify directly
	 */
	private static int precision = 0;
	private static int fracMask = 0;
	private static final int maxPrecision = 30;

	private static final int ePrecision = 29;
	private static final int e = 1459366444; // 2.7182818284590452353602874713527 * 2^29
	private static final int piPrecision = 29;
	private static final int pi = 1686629713; // 3.1415926535897932384626433832795
	private static int oneEightyOverPi;
	private static int piOverOneEighty;
	private static int maxDigitsCount;
	private static int maxDigitsMul;
	public static int ONE, HALF, TWO, E, PI, PI_HALF, PI_TWO;

	/**
	 * largest possible number
	 */
	public static final int INFINITY = 0x7fffffff;

	private static final int skPrecision = 31;
	private static final int sk[] = { 
			16342350, //7.61e-03 * 2^31
			356589659, //1.6605e-01
	};
	private static int SK[] = new int[sk.length];

	private static final int asPrecision = 30;
	private static final int as[] = { 
			-20110432, //-0.0187293 * 2^30
			79737141, //0.0742610 
			227756102, //0.2121144
			1686557206 //1.5707288
	};
	private static int AS[] = new int[as.length];

	private static final int ln2Precision = 30;
	private static final int ln2 = 744261117; //0.69314718055994530941723212145818 * 2^30
	private static final int ln2_inv = 1549082004; //1.4426950408889634073599246810019
	private static int LN2, LN2_INV;

	private static final int lgPrecision = 31;
	private static final int lg[] = { 
			1431655765, //6.666666666666735130e-01 * 2^31
			858993459, //3.999999999940941908e-01
			613566760, //2.857142874366239149e-01
			477218077, //2.222219843214978396e-01
			390489238, //1.818357216161805012e-01
			328862160, //1.531383769920937332e-01
			317788895 //1.479819860511658591e-01
	};
	private static int LG[] = new int[lg.length];

	private static final int expPPrecision = 31;
	private static final int expP[] = { 
			357913941, //1.66666666666666019037e-01 * 2^31
			-5965232, //-2.77777777770155933842e-03
			142029, //6.61375632143793436117e-05
			-3550, //-1.65339022054652515390e-06
			88, //4.13813679705723846039e-08
	};
	private static int EXP_P[] = new int[expP.length];

	// Init the default precision
	static {
		setPrecision(DEFAULT_PRECISION);
	}
	
	/**
	 * @return the precision
	 */
	public static int getPrecision() {
		return precision;
	}

	/**
	 * Sets the precision for all fixed-point operations. <br>
	 * The maximum precision is 31 bits.
	 * 
	 * @param precision the desired precision in number of bits
	 */
	public static void setPrecision(int precision) {
		if (precision > maxPrecision || precision < 0) {
			return;
		}
		int i;
		MathFP.precision = precision;
		ONE = 1 << precision;
		HALF = ONE >> 1;
		TWO = ONE << 1;
		PI = (precision <= piPrecision) ? pi >> (piPrecision - precision) : pi << (precision - piPrecision);
		PI_HALF = PI >> 1;
		PI_TWO = PI << 1;
		E = (precision <= ePrecision) ? e >> (ePrecision - precision) : e >> (precision - ePrecision);
		for (i = 0; i < sk.length; i++) {
			SK[i] = (precision <= skPrecision) ? sk[i] >> (skPrecision - precision) : sk[i] << (precision - skPrecision);
		}
		for (i = 0; i < as.length; i++) {
			AS[i] = (precision <= asPrecision) ? as[i] >> (asPrecision - precision) : as[i] << (precision - asPrecision);
		}
		LN2 = (precision <= ln2Precision) ? ln2 >> (ln2Precision - precision) : ln2 << (precision - ln2Precision);
		LN2_INV = (precision <= ln2Precision) ? ln2_inv >> (ln2Precision - precision) : ln2_inv << (precision - ln2Precision);
		for (i = 0; i < lg.length; i++) {
			LG[i] = (precision <= lgPrecision) ? lg[i] >> (lgPrecision - precision) : lg[i] << (precision - lgPrecision);
		}
		for (i = 0; i < expP.length; i++) {
			EXP_P[i] = (precision <= expPPrecision) ? expP[i] >> (expPPrecision - precision) : expP[i] << (precision - expPPrecision);
		}
		fracMask = ONE - 1;
		piOverOneEighty = div(PI, toFP(180));
		oneEightyOverPi = div(toFP(180), PI);

		maxDigitsMul = 1;
		maxDigitsCount = 0;
		for (i = ONE; i != 0;) {
			i /= 10;
			maxDigitsMul *= 10;
			maxDigitsCount++;
		}
	}

	/**
	 * Converts a fixed-point value to the current set precision.
	 * 
	 * @param fp the fixed-point value to convert.
	 * @param precision the precision of the fixed-point value passed in.
	 * @return a fixed-point value of the current precision
	 */
	public static int convert(int fp, int precision) {
		int num, xabs = Math.abs(fp);
		if (precision > maxPrecision || precision < 0) {
			return fp;
		}
		if (precision > MathFP.precision) {
			num = xabs >> (precision - MathFP.precision);
		} else {
			num = xabs << (MathFP.precision - precision);
		}
		if (fp < 0) {
			num = -num;
		}
		return num;
	}

	/**
	 * Converts an int to a fixed-point int.
	 * 
	 * @param i int to convert.
	 * @return the converted fixed-point value.
	 */
	public static int toFP(int i) {
		return (i < 0) ? -(-i << precision) : i << precision;
	}

	/**
	 * Converts a string to a fixed-point value. <br>
	 * The string should trimmed of any whitespace before-hand. <br>
	 * A few examples of valid strings:<br>
	 * 
	 * <pre>
	 * .01
	 * 0.01
	 * 10
	 * 130.0
	 * -30000.12345
	 * </pre>
	 * 
	 * @param s the string to convert.
	 * @return the fixed-point value.
	 */
	public static int toFP(String s) {
		int fp, i, integer, frac = 0;
		String fracString = null;
		boolean neg = false;
		if (s.charAt(0) == '-') {
			neg = true;
			s = s.substring(1);
		}
		int index = s.indexOf('.');

		if (index < 0) {
			integer = Integer.parseInt(s);
		} else if (index == 0) {
			integer = 0;
			fracString = s.substring(1);
		} else if (index == s.length() - 1) {
			integer = Integer.parseInt(s.substring(0, index));
		} else {
			integer = Integer.parseInt(s.substring(0, index));
			fracString = s.substring(index + 1);
		}

		if (fracString != null) {
			if (fracString.length() > maxDigitsCount) {
				fracString = fracString.substring(0, maxDigitsCount);
			}
			if (fracString.length() > 0) {
				frac = Integer.parseInt(fracString);
				for (i = maxDigitsCount - fracString.length(); i > 0; --i) {
					frac *= 10;
				}
			}
		}
		fp = (integer << precision) + (frac << precision) / maxDigitsMul;
		if (neg) {
			fp = -fp;
		}
		return fp;
	}

	/**
	 * Converts a fixed-point value to an int.
	 * 
	 * @param fp fixed-point value to convert
	 * @return the converted int value.
	 */
	public static int toInt(int fp) {
		return (fp < 0) ? -(-fp >> precision) : fp >> precision;
	}

	/**
	 * Converts a fixed-point value to a string.
	 * 
	 * Same as <code>toString(x, 0, max_possible_digits)</code>
	 * 
	 * @param fp the fixed-point value to convert.
	 * @return a string representing the fixed-point value with a minimum of
	 *         decimals in the string.
	 */
	public static String toString(int fp) {
		boolean neg = false;
		if (fp < 0) {
			neg = true;
			fp = -fp;
		}
		int integer = fp >> precision;
		String fracString = String.valueOf(((fp & fracMask) * maxDigitsMul) >> precision);
		
		int len = maxDigitsCount - fracString.length();
		for (int i = len; i > 0; --i) {
			fracString = "0" + fracString;
		}
		if ((neg && integer != 0)) {
			integer = -integer;
		}
		return String.valueOf(integer) + "." + fracString.toString();
	}

	/**
	 * Returns the smallest (closest to negative infinity) fixed-point value
	 * that is greater than or equal to the argument and is equal to a
	 * mathematical integer.
	 * 
	 * @param fp a fixed-point value.
	 * @return the smallest (closest to negative infinity) fixed-point value
	 *         that is greater than or equal to the argument and is equal to a
	 *         mathematical integer.
	 */
	public static int ceil(int fp) {
		boolean neg = false;
		if (fp < 0) {
			fp = -fp;
			neg = true;
		}
		if ((fp & fracMask) == 0) {
			return (neg) ? -fp : fp;
		}
		if (neg) {
			return -(fp & ~fracMask);
		}
		return (fp & ~fracMask) + ONE;
	}

	/**
	 * Returns the largest (closest to positive infinity) fixed-point value
	 * value that is less than or equal to the argument and is equal to a
	 * mathematical integer.
	 * 
	 * @param fp a fixed-point value.
	 * @return the largest (closest to positive infinity) fixed-point value that
	 *         less than or equal to the argument and is equal to a mathematical
	 *         integer.
	 */
	public static int floor(int fp) {
		boolean neg = false;
		if (fp < 0) {
			fp = -fp;
			neg = true;
		}
		if ((fp & fracMask) == 0) {
			return (neg) ? -fp : fp;
		}
		if (neg)
			return -(fp & ~fracMask) - ONE;
		return (fp & ~fracMask);
	}

	/**
	 * Removes the fractional part of a fixed-point value.
	 * 
	 * @param fp the fixed-point value to truncate.
	 * @return a truncated fixed-point value.
	 */
	public static int trunc(int fp) {
		return (fp < 0) ? -(-fp & ~fracMask) : fp & ~fracMask;
	}

	/**
	 * Returns the fractional part of a fixed-point value.
	 * 
	 * @param fp a fixed-point value to get fractional part of.
	 * @return positive fractional fixed-point value if input is positive,
	 *         negative fractional otherwise.
	 */
	public static int frac(int fp) {
		return (fp < 0) ? -(-fp & fracMask) : fp & fracMask;
	}

	/**
	 * Converts a fixed-point integer to an int with only the decimal value.
	 * <p>
	 * For example, if <code>fp</code> represents <code>12.34</code> the
	 * method returns <code>34</code>
	 * </p>
	 * 
	 * @param fp the fixed-point integer to be converted
	 * @return a int in a normal integer representation
	 */
	public static int fracAsInt(int fp) {
		if (fp < 0) {
			fp = -fp;
		}
		return maxDigitsMul * (fp & fracMask) >> precision;
	}
	
	/**
	 * Returns the closest integer to the argument.
	 * 
	 * @param fp the fixed-point value to round
	 * @return the value of the argument rounded to the nearest integer value.
	 */
	public static int round(int fp) {
		boolean neg = false;
		if (fp < 0) {
			fp = -fp;
			neg = true;
		}
		fp += HALF;
		fp &= ~fracMask;
		return (neg) ? -fp : fp;
	}

	/**
	 * Multiplies two fixed-point values.
	 * 
	 * @param fp1 first fixed-point value.
	 * @param fp2 second fixed-point value.
	 * @return the result of the multiplication.
	 */
	public static int mul(int fp1, int fp2) {
		return (int) ((long) fp1 * (long) fp2 >> precision);
	}

	/**
	 * Divides two fixed-point values.
	 * 
	 * @param fp1 mumerator fixed-point value.
	 * @param fp2 denominator fixed-point value.
	 * @return the result of the division.
	 */
	public static int div(int fp1, int fp2) {
		if (fp1 == 0) {
			return 0;
		}
		if (fp2 == 0) {
			return (fp1 < 0) ? -INFINITY : INFINITY;
		}
		int xneg = 0, yneg = 0;
		if (fp1 < 0) {
			xneg = 1;
			fp1 = -fp1;
		}
		if (fp2 < 0) {
			yneg = 1;
			fp2 = -fp2;
		}
		int msb = 0, lsb = 0;
		while ((fp1 & (1 << maxPrecision - msb)) == 0) {
			msb++;
		}
		while ((fp2 & (1 << lsb)) == 0) {
			lsb++;
		}
		int shifty = precision - (msb + lsb);
		int res = ((fp1 << msb) / (fp2 >> lsb));
		if (shifty > 0) {
			res <<= shifty;
		} else {
			res >>= -shifty;
		}
		if ((xneg ^ yneg) == 1) {
			res = -res;
		}
		return res;
	}

	/**
	 * Returns the correctly rounded positive square root of a fixed-point
	 * value.
	 * 
	 * @param fp a fixed-point value.
	 * @return the positive square root of <code>fp</code>. If the argument
	 *         is NaN or less than zero, the result is NaN.
	 */
	public static int sqrt(int fp) {
		int s = (fp + ONE) >> 1;
		for (int i = 0; i < 8; i++) {
			s = (s + div(fp, s)) >> 1;
		}
		return s;
	}

	/**
	 * Returns the trigonometric sine of an angle.
	 * 
	 * @param fp the angle in radians
	 * @return the sine of the argument.
	 */
	public static int sin(int fp) {
		int sign = 1;
		fp %= PI * 2;
		if (fp < 0) {
			fp = PI * 2 + fp;
		}
		if ((fp > PI_HALF) && (fp <= PI)) {
			fp = PI - fp;
		} else if ((fp > PI) && (fp <= (PI + PI_HALF))) {
			fp = fp - PI;
			sign = -1;
		} else if (fp > (PI + PI_HALF)) {
			fp = (PI << 1) - fp;
			sign = -1;
		}

		int sqr = mul(fp, fp);
		int result = SK[0];
		result = mul(result, sqr);
		result -= SK[1];
		result = mul(result, sqr);
		result += ONE;
		result = mul(result, fp);
		return sign * result;
	}

	/**
	 * Returns the trigonometric cosine of an angle.
	 * 
	 * @param fp the angle in radians
	 * @return the cosine of the argument.
	 */
	public static int cos(int fp) {
		return sin(PI_HALF - fp);
	}

	/**
	 * Returns the trigonometric tangent of an angle.
	 * 
	 * @param fp the angle in radians
	 * @return the tangent of the argument.
	 */
	public static int tan(int fp) {
		return div(sin(fp), cos(fp));
	}

	/**
	 * Returns the arc sine of a value; the returned angle is in the range -<i>pi</i>/2
	 * through <i>pi</i>/2.
	 * 
	 * @param fp the fixed-point value whose arc sine is to be returned.
	 * @return the arc sine of the argument.
	 */
	public static int asin(int fp) {
		boolean neg = false;
		if (fp < 0) {
			neg = true;
			fp = -fp;
		}

		int fRoot = sqrt(ONE - fp);
		int result = AS[0];

		result = mul(result, fp);
		result += AS[1];
		result = mul(result, fp);
		result -= AS[2];
		result = mul(result, fp);
		result += AS[3];
		result = PI_HALF - (mul(fRoot, result));
		if (neg) {
			result = -result;
		}

		return result;
	}

	/**
	 * Returns the arc cosine of a value; the returned angle is in the range 0.0
	 * through <i>pi</i>.
	 * 
	 * @param fp the fixed-point value whose arc cosine is to be returned.
	 * @return the arc cosine of the argument.
	 */
	public static int acos(int fp) {
		return PI_HALF - asin(fp);
	}

	/**
	 * Returns the arc tangent of a value; the returned angle is in the range -<i>pi</i>/2
	 * through <i>pi</i>/2.
	 * 
	 * @param fp the fiexed-point value whose arc tangent is to be returned.
	 * @return the arc tangent of the argument.
	 */
	public static int atan(int fp) {
		return asin(div(fp, sqrt(ONE + mul(fp, fp))));
	}

	// This is a finely tuned error around 0. The inaccuracies stabilize at around this value.
	private static int ATAN2_ZERO_ERROR = 65;

	/**
	 * Returns the angle <i>theta</i> from the conversion of rectangular
	 * coordinates (<code>fpX</code>,&nbsp;<code>fpY</code>) to polar
	 * coordinates (r,&nbsp;<i>theta</i>).
	 * 
	 * @param fpX the ordinate coordinate
	 * @param fpY the abscissa coordinate
	 * @return the <i>theta</i> component of the point (<i>r</i>,&nbsp;<i>theta</i>)
	 *         in polar coordinates that corresponds to the point (<i>fpX</i>,&nbsp;<i>fpY</i>)
	 *         in Cartesian coordinates.
	 */
	public static int atan2(int fpX, int fpY) {
		if (fpX == 0) {
			if (fpY >= 0) {
				return 0;
			} else if (fpY < 0) {
				return PI;
			}
		} else if (fpY >= -ATAN2_ZERO_ERROR && fpY <= ATAN2_ZERO_ERROR) {
			return (fpX > 0) ? PI_HALF : -PI_HALF;
		}
		int z = atan(Math.abs(div(fpX, fpY)));
		if (fpY > 0) {
			return (fpX > 0) ? z : -z;
		} else {
			return (fpX > 0) ? PI - z : z - PI;
		}
	}

	/**
	 * Returns Euler's number <i>e</i> raised to the power of a fixed-point
	 * value.
	 * 
	 * @param fp the exponent to raise <i>e</i> to.
	 * @return the value <i>e</i><sup><code>fp</code></sup>, where <i>e</i>
	 *         is the base of the natural logarithms.
	 */
	public static int exp(int fp) {
		if (fp == 0) {
			return ONE;
		}
		int xabs = Math.abs(fp);
		int k = mul(xabs, LN2_INV);
		k += HALF;
		k &= ~fracMask;
		if (fp < 0) {
			k = -k;
		}
		fp -= mul(k, LN2);
		int z = mul(fp, fp);
		int R = TWO + mul(z, EXP_P[0] + mul(z, EXP_P[1] + mul(z, EXP_P[2] + mul(z, EXP_P[3] + mul(z, EXP_P[4])))));
		int xp = ONE + div(mul(TWO, fp), R - fp);
		if (k < 0) {
			k = ONE >> (-k >> precision);
		} else {
			k = ONE << (k >> precision);
		}
		return mul(k, xp);
	}

	/**
	 * Returns the natural logarithm (base e) of a fixed-point value.
	 * 
	 * @param fp a fixed-point value
	 * @return the value ln&nbsp;<code>a</code>, the natural logarithm of
	 *         <code>fp</code>.
	 */
	public static int log(int x) {
		if (x < 0) {
			return 0;
		}
		if (x == 0) {
			return -INFINITY;
		}
		int log2 = 0, xi = x;
		while (xi >= TWO) {
			xi >>= 1;
			log2++;
		}
		int f = xi - ONE;
		int s = div(f, TWO + f);
		int z = mul(s, s);
		int w = mul(z, z);
		int R = mul(w, LG[1] + mul(w, LG[3] + mul(w, LG[5]))) + mul(z, LG[0] + mul(w, LG[2] + mul(w, LG[4] + mul(w, LG[6]))));
		return mul(LN2, (log2 << precision)) + f - mul(s, f - R);
	}

	/**
	 * Returns the logarithm (base <code>base</code>) of a fixed-point value.
	 * 
	 * @param fp a fixed-point value
	 * @param base
	 * @return the value log&nbsp;<code>a</code>, the logarithm of
	 *         <code>fp</code>
	 */
	public static int log(int fp, int base) {
		return div(log(fp), log(base));
	}

	/**
	 * Returns the value of the first argument raised to the power of the second
	 * argument
	 * 
	 * @param fp1 the base
	 * @param fp2 the exponent
	 * @return the value <code>a<sup>b</sup></code>.
	 */
	public static int pow(int fp1, int fp2) {
		if (fp2 == 0) {
			return ONE;
		}
		if (fp1 < 0) {
			return 0;
		}
		return exp(mul(log(fp1), fp2));
	}

	/**
	 * Converts an angle measured in degrees to an approximately equivalent
	 * angle measured in radians.
	 * 
	 * @param fp a fixed-point angle in degrees
	 * @return the measurement of the angle angrad in radians.
	 */
	public static int toRadians(int fp) {
		return mul(fp, piOverOneEighty);
	}

	/**
	 * Converts an angle measured in radians to an approximately equivalent
	 * angle measured in degrees.
	 * 
	 * @param fp a fixed-point angle in radians
	 * @return the measurement of the angle angrad in degrees.
	 */
	public static int toDegrees(int fp) {
		return mul(fp, oneEightyOverPi);
	}

}
