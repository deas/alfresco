/*
 * Copyright (C) 2006-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.jlan.server.auth;

import org.alfresco.jlan.server.SrvSession;
import org.alfresco.jlan.server.core.ShareType;
import org.alfresco.jlan.server.core.SharedDevice;
import org.alfresco.jlan.smb.server.SMBSrvSession;

/**
 * <p>Local Authenticator Class.
 *
 * <p>The local authenticator implementation enables user level security mode and uses the
 * user account list that is part of the server configuration to determine if a user is allowed
 * to access the server/share.
 *
 * <p>Note: Switching off encrypted password support will cause later NT4 service pack releases and
 * Win2000 to refuse to connect to the server without a registry update on the client.
 *
 * @author gkspencer
 */
public class LocalAuthenticator extends CifsAuthenticator {

  /**
   * Local Authenticator Constructor
   *
   * <p>Default to user mode security with encrypted password support.
   */
  public LocalAuthenticator() {
    setAccessMode(USER_MODE);
    setExtendedSecurity( false);
  }

  /**
   * Authenticate the connection to a share
   *
   * @param client ClienInfo
   * @param share  SharedDevice
   * @param pwd    Share level password.
   * @param sess   Server session
   * @return       Authentication status.
   */
  public int authenticateShareConnect(ClientInfo client, SharedDevice share, String pwd, SrvSession sess) {

    //	If the server is in share mode security allow the user access

    if (this.getAccessMode() == SHARE_MODE)
      return Writeable;

    //	Check if the IPC$ share is being accessed

    if (share.getType() == ShareType.ADMINPIPE)
      return Writeable;

    //	Check if the user is allowed to access the specified shared device
    //
    //	If a user does not have access to the requested share the connection will still be allowed
    //	but any attempts to access files or search directories will result in a 'no access rights'
    //	error being returned to the client.

    UserAccount user = null;
    if ( client != null)
    	user = getUserDetails(client.getUserName());
    	
    if ( user == null) {
    	
    	//	Check if the guest account is enabled
    	
    	return allowGuest() ? Writeable : NoAccess;
    }
    else if ( user.hasShare(share.getName()) == false)
      return NoAccess;

    //	Allow user to access this share

    return Writeable;
  }

  /**
   * Authenticate a user
   *
   * @param client  Client information
   * @param sess		Server session
   * @param alg     Encryption algorithm
   */
  public int authenticateUser(ClientInfo client, SrvSession sess, int alg) {

    //	Check if the user exists in the user list

		UserAccount userAcc = getUserDetails(client.getUserName());
    if (userAcc != null) {

      //	Validate the password

			boolean authSts = false;
			
			if ( client.getPassword() != null) {
				
				//	Validate using the Unicode password
				
        authSts = validatePassword( userAcc, client, sess.getAuthenticationContext(), alg);
			}
			else if ( client.hasANSIPassword()) {
				
				//	Validate using the ANSI password with the LanMan encryption
				
				authSts = validatePassword( userAcc, client, sess.getAuthenticationContext(), LANMAN);
			}

			//	Return the authentication status
			
			return authSts == true ? AUTH_ALLOW : AUTH_BADPASSWORD;
    }

    //	Check if this is an SMB/CIFS null session logon.
    //
    //	The null session will only be allowed to connect to the IPC$ named pipe share.
    
    if ( client.isNullSession() && sess instanceof SMBSrvSession)
      return AUTH_ALLOW;
    
    //	Unknown user

    return allowGuest() ? AUTH_GUEST : AUTH_DISALLOW;
  }
}
