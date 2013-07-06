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
package org.sun.midp.crypto;

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

	/** Current cipher mode. */
	private int mode;
	/** Local certificate key. */
	private SecretKey ckey = null;
	private byte[] key;
	private byte[] state;
	private int x;
	private int y;

	/**
	 * Constructor for algorithm 3 (ALG_ARCFOUR)
	 */
	public void ARC4() {
		mode = Cipher.MODE_UNINITIALIZED;
	}

	/**
	 * Called by the factory method to set the mode and padding parameters.
	 * Need because Class.newInstance does not take args.
	 *
	 * @param mode the mode parsed from the transformation parameter of
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

	/**
	 * Initializes the cipher's S-boxes based on the key.
	 *  This code is based on the cipher's description in 
	 * Bruce Schenier's "Applied Cryptography", Second Edition, pp 397-398,
	 * ISBN 0-471-11709-9
	 *
	 * @param opmode the operation mode of this cipher (this is one of the
	 * following:
	 * <code>ENCRYPT_MODE</code> or <code>DECRYPT_MODE</code>)
	 * @param key the encryption key
	 * @param params the algorithm parameters
	 *
	 * @exception InvalidKeyException if the given key is inappropriate for
	 * initializing this cipher, or its keysize exceeds the maximum allowable
	 * keysize.
	 * @exception InvalidAlgorithmParameterException if the given algorithm
	 * parameters are inappropriate for this cipher,
	 * or this cipher is being initialized for decryption and requires
	 * algorithm parameters and <code>params</code> is null, or the given
	 * algorithm parameters imply a cryptographic strength that would exceed
	 * the legal limits.
	 * @exception IllegalArgumentException if the opmode is invalid
	 */
	public void init(int mode, Key ckey, CryptoParameter params)
			throws InvalidKeyException, InvalidAlgorithmParameterException {

		if (!(ckey instanceof SecretKey)) {
			throw new InvalidKeyException();
		}

		if (mode != Cipher.ENCRYPT_MODE && mode != Cipher.DECRYPT_MODE) {
			throw new IllegalArgumentException();
		}

		this.mode = mode;
		this.ckey = (SecretKey) ckey;
		key = ckey.getEncoded();
		if (key == null) {
			int length = Math.min(256, key.length);
			byte[] keyCopy = new byte[length];
			System.arraycopy(key, 0, keyCopy, 0, length);
			key = keyCopy;
		}
		state = new byte[256];
		// The key-scheduling algorithm
		for (int i = 0; i < 256; i++) {
			state[i] = (byte) i;
		}
		int j = 0;
		for (int i = 0; i < 256; i++) {
			j = (j + state[i] + key[i % key.length]) & 0xff;
			byte temp = state[i];
			state[i] = state[j];
			state[j] = temp;
		}

		x = 0;
		y = 0;
	}

	/**
	 * Transform a buffer of data,
	 * @param inBuf input buffer of data 
	 * @param inOff offset in the provided input buffer
	 * @param inLen length of data to be processed
	 * @param outBuf output buffer of data 
	 * @param outOff offset in the provided output buffer
	 * @return number of bytes copied to output buffer
	 */
	private int transform(byte[] inBuf, int inOff, int inLen,
			byte[] outBuf, int outOff) {
		final byte[] input = new byte[inLen], output = new byte[inLen];
		System.arraycopy(inBuf, inOff, output, 0, inLen);
		// The pseudo-random generation algorithm
		for (int i = 0; i < input.length; i++) {
			x = (x + 1) & 0xff;
			y = (state[x] + y) & 0xff;

			byte temp = state[x];
			state[x] = state[y];
			state[y] = temp;

			output[i] = (byte) ((input[i] ^ state[(state[x] + state[y]) & 0xff]));
		}
		System.arraycopy(output, 0, outBuf, outOff, inLen);
		return inLen;
	}

	/**
	 * Update the current data record.
	 * 
	 * @param input the input buffer
	 * @param inputOffset the offset in <code>input</code> where the input
	 * starts
	 * @param inputLen the input length
	 * @param output the buffer for the result
	 * @param outputOffset the offset in <code>output</code> where the result
	 * is stored
	 *
	 * @return the number of bytes stored in <code>output</code>
	 *
	 * @exception IllegalStateException if this cipher is in a wrong state
	 * (e.g., has not been initialized)
	 * @exception ShortBufferException if the given output buffer is too small
	 * to hold the result
	 */
	public int update(byte[] input, int inputOffset, int inputLen,
			byte[] output, int outputOffset)
			throws IllegalStateException, ShortBufferException {

		Util.checkBounds(input, inputOffset, inputLen,
				output, outputOffset);

		if (mode == Cipher.MODE_UNINITIALIZED) {
			throw new IllegalStateException();
		}

		if (inputLen == 0) {
			return 0;
		}

		if (output.length - outputOffset < inputLen) {
			throw new ShortBufferException();
		}

		return transform(input, inputOffset, inputLen, output, outputOffset);
	}

	/**
	 * Encrypts or decrypts data in a single-part operation, or finishes a
	 * multiple-part operation. The data is encrypted or decrypted,
	 * depending on how this cipher was initialized.
	 *
	 * <p>The first <code>inputLen</code> bytes in the <code>input</code>
	 * buffer, starting at <code>inputOffset</code> inclusive, and any input
	 * bytes that may have been buffered during a previous
	 * <code>update</code> operation, are processed, with padding
	 * (if requested) being applied.
	 * The result is stored in the <code>output</code> buffer, starting at
	 * <code>outputOffset</code> inclusive.
	 *
	 * <p>If the <code>output</code> buffer is too small to hold the result,
	 * a <code>ShortBufferException</code> is thrown. In this case, repeat this
	 * call with a larger output buffer.
	 *
	 * <p>Upon finishing, this method resets this cipher object to the state
	 * it was in when previously initialized via a call to <code>init</code>.
	 * That is, the object is reset and available to encrypt or decrypt
	 * (depending on the operation mode that was specified in the call to
	 * <code>init</code>) more data.
	 *
	 * <p>Note: if any exception is thrown, this cipher object may need to
	 * be reset before it can be used again.
	 *
	 * <p>Note: this method should be copy-safe, which means the
	 * <code>input</code> and <code>output</code> buffers can reference
	 * the same byte array and no unprocessed input data is overwritten
	 * when the result is copied into the output buffer.
	 *
	 * @param input the input buffer
	 * @param inputOffset the offset in <code>input</code> where the input
	 * starts
	 * @param inputLen the input length
	 * @param output the buffer for the result
	 * @param outputOffset the offset in <code>output</code> where the result
	 * is stored
	 *
	 * @return the number of bytes stored in <code>output</code>
	 *
	 * @exception IllegalStateException if this cipher is in a wrong state
	 * (e.g., has not been initialized)
	 * @exception IllegalBlockSizeException if this cipher is a block cipher,
	 * no padding has been requested (only in encryption mode), and the total
	 * input length of the data processed by this cipher is not a multiple of
	 * block size
	 * @exception ShortBufferException if the given output buffer is too small
	 * to hold the result
	 * @exception BadPaddingException if this cipher is in decryption mode,
	 * and (un)padding has been requested, but the decrypted data is not
	 * bounded by the appropriate padding bytes
	 */
	public int doFinal(byte[] input, int inputOffset, int inputLen,
			byte[] output, int outputOffset)
			throws IllegalStateException, ShortBufferException,
			IllegalBlockSizeException, BadPaddingException {
		int val = update(input, inputOffset, inputLen, output, outputOffset);

		try {
			init(mode, ckey);
		} catch (InvalidKeyException ike) {
			// ignore, the key was already checked
		}

		return val;
	}
}