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

import java.security.DigestException;
import java.security.NoSuchAlgorithmException;

/**
 * This MessageDigest class provides applications the functionality of a
 * message digest algorithm, such as MD5 or SHA.
 * Message digests are secure one-way hash functions that take arbitrary-sized
 * data and output a fixed-length hash value.
 *
 * <p>A <code>MessageDigest</code> object starts out initialized. The data is 
 * processed through it using the <code>update</code>
 * method. At any point {@link #reset() reset} can be called
 * to reset the digest. Once all the data to be updated has been
 * updated, the <code>digest</code> method should 
 * be called to complete the hash computation.
 *
 * <p>The <code>digest</code> method can be called once for a given number 
 * of updates. After <code>digest</code> has been called, 
 * the <code>MessageDigest</code>
 * object is reset to its initialized state.
 */
public class MessageDigest {

	public static final int DIGEST_LENGTH_MD2 = 16;
	public static final int DIGEST_LENGTH_MD5 = 16;
	public static final int DIGEST_LENGTH_SHA = 20;
	public static final String DIGEST_NAME_MD2 = "MD2";
	public static final String DIGEST_NAME_MD5 = "MD5";
	public static final String DIGEST_NAME_SHA = "SHA-1";
	private final java.security.MessageDigest digest;
	private final String algorithm;

	private MessageDigest(final String algorithm) throws NoSuchAlgorithmException {
		this.digest = java.security.MessageDigest.getInstance(algorithm);
		this.algorithm = algorithm;
	}

	/** 
	 * Clones the MessageDigest object.
	 * @return a clone of this object
	 */
	public Object clone() {
		try {
			return getInstance(algorithm);
		} catch (NoSuchAlgorithmException ex) {
		}
		return null;
	}

	/*
	 * Completes the hash computation by performing final operations
	 * such as padding. The digest is reset after this call is made.
	 *
	 * @param buf output buffer for the computed digest
	 *
	 * @param offset offset into the output buffer to begin storing the digest
	 *
	 * @param len number of bytes within buf allotted for the digest
	 *
	 * @return the number of bytes placed into <code>buf</code>
	 *
	 * @exception DigestException if an error occurs.
	 */
	public int digest(byte[] buf, int offset, int len) throws DigestException {
		return digest.digest(buf, offset, len);
	}

	/** 
	 * Gets the message digest algorithm.
	 * @return algorithm implemented by this MessageDigest object
	 */
	public String getAlgorithm() {
		return algorithm;
	}

	/** 
	 * Gets the length (in bytes) of the hash.
	 * @return byte-length of the hash produced by this object
	 */
	public int getDigestLength() {
		if (DIGEST_NAME_MD2.equals(digest)) {
			return DIGEST_LENGTH_MD2;
		} else if (DIGEST_NAME_MD5.equals(algorithm)) {
			return DIGEST_LENGTH_MD5;
		} else if (DIGEST_NAME_SHA.equals(algorithm)) {
			return DIGEST_LENGTH_SHA;
		}
		// should never happen
		throw new IllegalStateException();
	}

	/**
	 * Resets the digest for further use.
	 */
	public void reset() {
		digest.reset();
	}

	/**
	 * Updates the digest using the specified array of bytes, starting
	 * at the specified offset.
	 *
	 * @param input the array of bytes.
	 *
	 * @param offset the offset to start from in the array of bytes.
	 *
	 * @param len the number of bytes to use, starting at
	 * <code>offset</code>.
	 */
	public void update(byte[] input, int offset, int len) {
		digest.update(input, offset, len);
	}

	/**
	 * Generates a <code>MessageDigest</code> object that implements
	 * the specified digest
	 * algorithm. 
	 *
	 * @param algorithm the name of the algorithm requested. 
	 * See Appendix A in the 
	 * Java Cryptography Architecture API Specification & Reference
	 * for information about standard algorithm names.
	 *
	 * @return a MessageDigest object implementing the specified
	 * algorithm.
	 *
	 * @exception NoSuchAlgorithmException if the algorithm is
	 * not available in the caller's environment.  
	 */
	public static MessageDigest getInstance(String algorithm)
			throws NoSuchAlgorithmException {
		return new MessageDigest(algorithm);
	}
}
