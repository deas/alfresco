/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
import org.alfresco.jlan.server.core.SharedDevice;
import org.alfresco.jlan.smb.server.SMBSrvException;
import org.alfresco.jlan.smb.server.SMBSrvPacket;
import org.alfresco.jlan.smb.server.SMBSrvSession;

/**
 * CIFS Authenticator Interface
 * 
 * <p>
 * An authenticator is used by the CIFS server to authenticate users when in user level access mode
 * and authenticate requests to connect to a share when in share level access.
 * 
 * @author gkspencer
 * @author dward
 */
public interface ICifsAuthenticator
{

    public static final int SHARE_MODE = 0;
    public static final int USER_MODE = 1;
    public static final int LANMAN = PasswordEncryptor.LANMAN;
    public static final int NTLM1 = PasswordEncryptor.NTLM1;
    public static final int NTLM2 = PasswordEncryptor.NTLM2;
    public static final int AUTH_ALLOW = 0;
    public static final int AUTH_GUEST = 0x10000000;
    public static final int AUTH_DISALLOW = -1;
    public static final int AUTH_BADPASSWORD = -2;
    public static final int AUTH_BADUSER = -3;
    public static final int AUTH_PASSEXPIRED = -4;
    public static final int AUTH_ACCDISABLED = -5;
    public static final int NoAccess = 0;
    public static final int ReadOnly = 1;
    public static final int Writeable = 2;
    public static final int STANDARD_PASSWORD_LEN = 24;
    public static final int STANDARD_CHALLENGE_LEN = 8;

    /**
     * Authenticate a connection to a share.
     * 
     * @param client User/client details from the tree connect request.
     * @param share Shared device the client wants to connect to.
     * @param sharePwd Share password.
     * @param sess Server session.
     * @return int Granted file permission level or disallow status if negative. See the
     *         FilePermission class.
     */
    public int authenticateShareConnect(ClientInfo client, SharedDevice share, String sharePwd, SrvSession sess);

    /**
     * Authenticate a user. A user may be granted full access, guest access or no access.
     * 
     * @param client User/client details from the session setup request.
     * @param sess Server session
     * @param alg Encryption algorithm
     * @return int Access level or disallow status.
     */
    public int authenticateUser(ClientInfo client, SrvSession sess, int alg);

    /**
     * Return the access mode of the server, either SHARE_MODE or USER_MODE.
     * 
     * @return int
     */
    public int getAccessMode();

    /**
     * Determine if extended security methods are available
     * 
     * @return boolean
     */
    public boolean hasExtendedSecurity();

    /**
     * Return the security mode flags
     * 
     * @return int
     */
    public int getSecurityMode();

    /**
     * Generate the CIFS negotiate response packet, the authenticator should add authentication
     * specific fields to the response.
     * 
     * @param sess SMBSrvSession
     * @param respPkt SMBSrvPacket
     * @param extendedSecurity boolean
     * @exception AuthenticatorException
     */
    public void generateNegotiateResponse(SMBSrvSession sess, SMBSrvPacket respPkt, boolean extendedSecurity)
            throws AuthenticatorException;

    /**
     * Process the CIFS session setup request packet and build the session setup response
     * 
     * @param sess SMBSrvSession
     * @param reqPkt SMBSrvPacket
     * @exception SMBSrvException
     */
    public void processSessionSetup(SMBSrvSession sess, SMBSrvPacket reqPkt) throws SMBSrvException;

    /**
     * Return the encryption key/challenge length
     * 
     * @return int
     */
    public int getEncryptionKeyLength();

    /**
     * Return the server capability flags
     * 
     * @return int
     */
    public int getServerCapabilities();

    /**
     * Close the authenticator, perform any cleanup
     */
    public void closeAuthenticator();
    
    /**
     * Set the current authenticated user context for this thread
     * 
     * @param client ClientInfo
     */
    public void setCurrentUser(ClientInfo client);
}