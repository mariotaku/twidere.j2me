/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter2me.conf;

import twitter2me.auth.AuthorizationConfiguration;
import twitter2me.http.HttpConfiguration;

/**
 *
 * @author mariotaku
 */
public interface Configuration extends AuthorizationConfiguration, HttpConfiguration {

	String getClientName();

	String getClientURL();

	String getClientVersion();
	
	String getOAuthAccessTokenURL();

	String getOAuthAuthenticationURL();

	String getOAuthAuthorizationURL();

	String getOAuthBaseURL();

	String getOAuthRequestTokenURL();

	String getRestBaseURL();

	String getSigningOAuthAccessTokenURL();

	String getSigningOAuthAuthenticationURL();

	String getSigningOAuthAuthorizationURL();

	String getSigningOAuthBaseURL();

	String getSigningOAuthRequestTokenURL();

	String getSigningRestBaseURL();

	boolean isIncludeEntitiesEnabled();

	boolean isIncludeRTsEnabled();
}
