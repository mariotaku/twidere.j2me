/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter2me.http;

import java.io.IOException;

/**
 *
 * @author mariotaku
 */
public interface HostAddressResolver {
	
	public String resolve(final String host) throws IOException;
	
}
