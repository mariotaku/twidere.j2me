/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter2me.http;

/**
 *
 * @author mariotaku
 */
public interface HttpConfiguration {
	
	public boolean isGZIPEnabled();

	public boolean isSSLErrorsIgnored();
	
	public String getHttpUserAgent();
	
	public int getHttpRetryCount();

	public int getHttpRetryIntervalSeconds();
	
	public HttpClientFactory getHttpClientFactory();
	
	public HostAddressResolver getHostAddressResolver();
}
