/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sun.midp.pki;

/**
 * This <i>interface</i> supports storage of certificates (not private keys or 
 * symmetric keys).
 *
 */
public interface CertStore {
    /**
     * Returns the certificate(s) corresponding to a 
     * subject name string.
     * 
     * @param subjectName subject name of the certificate in printable form.
     *
     * @return corresponding certificates or null (if not found)
     */ 
    abstract public X509Certificate[] getCertificates(String subjectName);
	
}