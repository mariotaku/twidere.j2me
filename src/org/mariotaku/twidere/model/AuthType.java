/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mariotaku.twidere.model;

/**
 *
 * @author mariotaku
 */
public class AuthType {

	public static final byte OAUTH = 1;
	public static final byte BASIC = 2;
	public static final byte TWIP_O_MODE = 3;

	public static boolean isValid(byte type) {
		return type == OAUTH || type == BASIC || type == TWIP_O_MODE;
	}
}
