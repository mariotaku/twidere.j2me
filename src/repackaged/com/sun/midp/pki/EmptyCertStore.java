/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repackaged.com.sun.midp.pki;

/**
 *
 * @author mariotaku
 */
public class EmptyCertStore implements CertStore {

	public static final X509Certificate[] EMPTY = new X509Certificate[0];
	
	public X509Certificate[] getCertificates(String subjectName) {
		return null;
	}
	
	public static CertStore getInstance() {
		return new EmptyCertStore();
	}
	
}
