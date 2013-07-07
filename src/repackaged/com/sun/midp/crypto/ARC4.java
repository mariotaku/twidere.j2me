/*
 *   
 *
 * Copyright  1990-2007 Sun Microsystems, Inc. All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details (a copy is
 * included at /legal/license.txt).
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa
 * Clara, CA 95054 or visit www.sun.com if you need additional
 * information or have any questions.
 */
package repackaged.com.sun.midp.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

/**
 * This class implements the ARCfour stream cipher
 */
public final class ARC4 extends Cipher {

	private final javax.crypto.Cipher arc4;

	/**
	 * Constructor for algorithm 3 (ALG_ARCFOUR)
	 */
	public ARC4() {
		try {
			arc4 = javax.crypto.Cipher.getInstance("RC4");
		} catch (Exception ex) {
			throw new IllegalStateException(ex.getClass().getName() + ":" + ex.getMessage());
		}
	}

		/**
	 * Called by the factory method to set the mode and padding parameters.
	 * Need because Class.newInstance does not take args.
	 *
	 * @param mode the mode parsed from the transformation parameter of
	 *             getInstance and upper cased
	 * @param padding the padding parsed from the transformation parameter of
	 *                getInstance and upper cased
	 *
	 * @exception NoSuchPaddingException if <code>transformation</code>
	 * contains a padding scheme that is not available.
	 * @exception IllegalArgumentException if mode is incorrect
	 */
	protected void setChainingModeAndPadding(String mode, String padding)
			throws NoSuchPaddingException {

		if (!(mode.equals("") || mode.equals("NONE"))) {
			throw new IllegalArgumentException("illegal chaining mode");
		}

		// NOPADDING is not an option.
		if (!(padding.equals("") || padding.equals("PKCS1PADDING"))) {
			throw new NoSuchPaddingException();
		}
	}

	public void init(int opmode, Key key, CryptoParameter params) throws InvalidKeyException, InvalidAlgorithmParameterException {
		arc4.init(opmode, key, params);
	}

	public int update(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws IllegalStateException, ShortBufferException {
		return arc4.update(input, inputOffset, inputLen, output, outputOffset);
	}

	public int doFinal(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws IllegalStateException, ShortBufferException, IllegalBlockSizeException, BadPaddingException {
		return arc4.doFinal(input, inputOffset, inputLen, output, outputOffset);
	}

}