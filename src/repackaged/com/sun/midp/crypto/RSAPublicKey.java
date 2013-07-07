/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repackaged.com.sun.midp.crypto;

import java.security.PublicKey;

/**
 * Specifies the RSA public key interface. An RSA key is not ready for
 * us until both the modulus and exponent have been set.
 */
public final class RSAPublicKey extends RSAKey implements PublicKey {
    /**
     * Constructor for RSA public key.
     *
     * @param modulus modulus of key to process
     * @param exponent exponent the key
     */
    public RSAPublicKey(byte[] modulus, byte[] exponent) {
        super(modulus, 0, modulus.length, exponent, 0, exponent.length);
    }

    /**
     * Constructor for RSA public key.
     *
     * @param modulus modulus of key to process
     * @param modOffset offset of the modulus
     * @param modLen length of modulus in bytes
     * @param exponent exponent the key
     * @param expOffset offset of the exponent
     * @param expLen length of the exponent in bytes
     */
    public RSAPublicKey(byte[] modulus, int modOffset, int modLen,
                        byte[] exponent, int expOffset, int expLen) {
        super(modulus, modOffset, modLen, exponent, expOffset, expLen);
    }
}