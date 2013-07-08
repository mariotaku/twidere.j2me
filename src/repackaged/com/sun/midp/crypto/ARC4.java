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

	private int mode = MODE_UNINITIALIZED;
	private SecretKey skey;
	// Key routines.
	private byte[] state = new byte[256];
	private int x, y;

	public ARC4() {
	}
	/* @param mode the mode parsed from the transformation parameter of
	 *             getInstance and upper cased
	 * @param padding the paddinge parsed from the transformation parameter of
	 *                getInstance and upper cased
	 *
	 * @exception NoSuchPaddingException if <code>transformation</code>
	 * contains a padding scheme that is not available
	 * @exception IllegalArgumentException if the mode is invalid for the
	 * cipher
	 */

	protected void setChainingModeAndPadding(String mode, String padding)
			throws NoSuchPaddingException {

		if (!(mode.equals("") || mode.equals("NONE"))) {
			throw new IllegalArgumentException();
		}

		// NOPADDING is not an option.
		if (!(padding.equals("") || padding.equals("NOPADDING"))) {
			throw new NoSuchPaddingException();
		}
	}

	public void init(int opmode, Key key, CryptoParameter params) throws InvalidKeyException, InvalidAlgorithmParameterException {

		if (!(key instanceof SecretKey)) {
			throw new InvalidKeyException();
		}
		if (opmode != Cipher.ENCRYPT_MODE && opmode != Cipher.DECRYPT_MODE) {
			throw new IllegalArgumentException();
		}
		mode = opmode;
		skey = (SecretKey) key;
		setKey(skey);
	}

	public int update(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws IllegalStateException, ShortBufferException {
		return transform(input, inputOffset, inputLen, output, outputOffset);
	}

	public int doFinal(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws IllegalStateException, ShortBufferException, IllegalBlockSizeException, BadPaddingException {
		final int val = update(input, inputOffset, inputLen, output, outputOffset);
		try {
			init(mode, skey);
		} catch (InvalidKeyException ex) {
		}
		return val;
	}

	private int nextState() {
		byte temp;
		x = (x + 1) & 0xff;
		y = (y + state[x]) & 0xff;
		temp = state[x];
		state[x] = state[y];
		state[y] = temp;
		return (state[x] + state[y]) & 0xff;
	}

	/// Set the key.
	private void setKey(final SecretKey skey) {
		final byte[] key = skey.getEncoded();
		int index1;
		int index2;
		int counter;
		byte temp;

		for (counter = 0; counter < 256; ++counter) {
			state[counter] = (byte) counter;
		}
		x = 0;
		y = 0;
		index1 = 0;
		index2 = 0;
		for (counter = 0; counter < 256; ++counter) {
			index2 = (key[index1] + state[counter] + index2) & 0xff;
			temp = state[counter];
			state[counter] = state[index2];
			state[index2] = temp;
			index1 = (index1 + 1) % key.length;
		}
	}

	private int transform(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) {
		for (int i = 0; i < inputLen; ++i) {
			output[outputOffset + i] = (byte) (input[inputOffset + i] ^ state[nextState()]);
		}
		return inputLen;
	}
}