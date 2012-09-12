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

import java.net.InetAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.springframework.extensions.config.ConfigElement;
import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.netbios.RFCNetBIOSProtocol;
import org.alfresco.jlan.server.SrvSession;
import org.alfresco.jlan.server.auth.passthru.DomainMapping;
import org.alfresco.jlan.server.config.InvalidConfigurationException;
import org.alfresco.jlan.server.config.SecurityConfigSection;
import org.alfresco.jlan.server.config.ServerConfiguration;
import org.alfresco.jlan.server.config.ServerConfigurationAccessor;
import org.alfresco.jlan.server.core.NoPooledMemoryException;
import org.alfresco.jlan.server.core.SharedDevice;
import org.alfresco.jlan.smb.Capability;
import org.alfresco.jlan.smb.Dialect;
import org.alfresco.jlan.smb.DialectSelector;
import org.alfresco.jlan.smb.SMBStatus;
import org.alfresco.jlan.smb.server.CIFSConfigSection;
import org.alfresco.jlan.smb.server.SMBSrvException;
import org.alfresco.jlan.smb.server.SMBSrvPacket;
import org.alfresco.jlan.smb.server.SMBSrvSession;
import org.alfresco.jlan.smb.server.SecurityMode;
import org.alfresco.jlan.smb.server.VirtualCircuit;
import org.alfresco.jlan.util.DataPacker;
import org.alfresco.jlan.util.HexDump;
import org.alfresco.jlan.util.IPAddress;

/**
 * CIFS Authenticator Class
 * 
 * <p>
 * An authenticator is used by the CIFS server to authenticate users when in user level access mode
 * and authenticate requests to connect to a share when in share level access.
 * 
 * @author gkspencer
 */
public abstract class CifsAuthenticator implements ICifsAuthenticator {

	// Server access mode

	protected static final String GUEST_USERNAME = "guest";

	// Default SMB dialects to enable

	private DialectSelector m_dialects;

	// Security mode flags

	private int m_securityMode = SecurityMode.UserMode + SecurityMode.EncryptedPasswords;

	// Password encryption algorithms

	private PasswordEncryptor m_encryptor = new PasswordEncryptor();

	// Server access mode

	private int m_accessMode = SHARE_MODE;

	// Enable extended security mode

	private boolean m_extendedSecurity;

	// Flag to enable/disable the guest account, and control mapping of unknown users to the guest
	// account

	private boolean m_allowGuest;
	private boolean m_mapToGuest;

	// Default guest user name

	private String m_guestUserName = GUEST_USERNAME;

	// Random number generator used to generate challenge keys

	protected Random m_random = new Random(System.currentTimeMillis());

	// Server configuration and required sections

	protected ServerConfigurationAccessor m_config;

	// Cleanup sessions from the same client address/name if a session setup using virtual circuit zero
	// is received
	
	private boolean m_sessCleanup = true;
	
	// Debug output enable

	private boolean m_debug;

	/**
     * @param debug activate debug mode?
     */
    public void setDebug(boolean debug)
    {
        this.m_debug = debug;
    }
    
    /**
     * @param config an accessor for the file server configuration sections
     */
    public void setConfig(ServerConfigurationAccessor config)
    {
        this.m_config = config;
    }

    /* (non-Javadoc)
     * @see org.alfresco.jlan.server.auth.ICifsAuthenticator#authenticateShareConnect(org.alfresco.jlan.server.auth.ClientInfo, org.alfresco.jlan.server.core.SharedDevice, java.lang.String, org.alfresco.jlan.server.SrvSession)
     */
	public int authenticateShareConnect(ClientInfo client, SharedDevice share, String sharePwd, SrvSession sess) {

		// Allow write access
		//
		// Main authentication is handled by authenticateUser()

		return ICifsAuthenticator.Writeable;
	}

	/* (non-Javadoc)
     * @see org.alfresco.jlan.server.auth.ICifsAuthenticator#authenticateUser(org.alfresco.jlan.server.auth.ClientInfo, org.alfresco.jlan.server.SrvSession, int)
     */
	public int authenticateUser(ClientInfo client, SrvSession sess, int alg) {

		// Check if the user exists in the user list

		UserAccount userAcc = getUserDetails(client.getUserName());
		if ( userAcc != null) {

			// Validate the password

			boolean authSts = false;

			if ( client.getPassword() != null) {

				// Validate using the Unicode password

				authSts = validatePassword(userAcc, client, sess.getAuthenticationContext(), alg);
			}
			else if ( client.hasANSIPassword()) {

				// Validate using the ANSI password with the LanMan encryption

				authSts = validatePassword(userAcc, client, sess.getAuthenticationContext(), LANMAN);
			}

			// Return the authentication status

			return authSts == true ? AUTH_ALLOW : AUTH_BADPASSWORD;
		}

		// Check if this is an SMB/CIFS null session logon.
		//
		// The null session will only be allowed to connect to the IPC$ named pipe share.

		if ( client.isNullSession() && sess instanceof SMBSrvSession)
			return AUTH_ALLOW;

		// Unknown user

		return allowGuest() ? AUTH_GUEST : AUTH_DISALLOW;
	}

	/* (non-Javadoc)
     * @see org.alfresco.jlan.server.auth.ICifsAuthenticator#authenticateUserPlainText(org.alfresco.jlan.server.auth.ClientInfo, org.alfresco.jlan.server.SrvSession)
     */
	public final int authenticateUserPlainText(ClientInfo client, SrvSession sess) {

		// Get a challenge key

		NTLanManAuthContext authCtx = (NTLanManAuthContext) sess.getAuthenticationContext();
		if ( authCtx == null) {
			authCtx = new NTLanManAuthContext();
			sess.setAuthenticationContext(authCtx);
		}

		// Get the plain text password

		String textPwd = client.getPasswordAsString();
		if ( textPwd == null)
			textPwd = client.getANSIPasswordAsString();

		// Encrypt the password

		byte[] encPwd = generateEncryptedPassword(textPwd, authCtx.getChallenge(), NTLM1, client.getUserName(), client
				.getDomain());
		client.setPassword(encPwd);

		// Authenticate the user

		return authenticateUser(client, sess, NTLM1);
	}

	/**
	 * Initialize the authenticator, after properties have been set
	 * 
	 * @exception InvalidConfigurationException
	 */
	public void initialize()
		throws InvalidConfigurationException {

	    // Check all required properties have been set

	    if ( m_config == null)
            throw new InvalidConfigurationException("server configuration accessor not set");

        // Allocate the SMB dialect selector, and initialize using the default list of dialects

		m_dialects = new DialectSelector();

		m_dialects.AddDialect(Dialect.DOSLanMan1);
		m_dialects.AddDialect(Dialect.DOSLanMan2);
		m_dialects.AddDialect(Dialect.LanMan1);
		m_dialects.AddDialect(Dialect.LanMan2);
		m_dialects.AddDialect(Dialect.LanMan2_1);
		m_dialects.AddDialect(Dialect.NT);
	}

    /**
     * Initialize the authenticator
     * 
     * @param config ServerConfiguration
     * @param params ConfigElement
     * @exception InvalidConfigurationException
     */
    public void initialize(ServerConfiguration config, ConfigElement params)
        throws InvalidConfigurationException {

        if ( params.getChild("Debug") != null)
            setDebug(true);
        
        // Save the server configuration so we can access the authentication component
        setConfig(config);
        
        initialize();
    }

    /**
	 * Encrypt the plain text password with the specified encryption key using the specified
	 * encryption algorithm.
	 * 
	 * @param plainPwd String
	 * @param encryptKey byte[]
	 * @param alg int
	 * @param userName String
	 * @param domain String
	 * @return byte[]
	 */
	protected final byte[] generateEncryptedPassword(String plainPwd, byte[] encryptKey, int alg, String userName, String domain) {

		// Use the password encryptor

		byte[] encPwd = null;

		try {
			// Encrypt the password

			encPwd = m_encryptor.generateEncryptedPassword(plainPwd, encryptKey, alg, userName, domain);
		}
		catch (NoSuchAlgorithmException ex) {
		}
		catch (InvalidKeyException ex) {
		}

		// Return the encrypted password

		return encPwd;
	}

	/* (non-Javadoc)
     * @see org.alfresco.jlan.server.auth.ICifsAuthenticator#getAccessMode()
     */
	public final int getAccessMode() {
		return m_accessMode;
	}

	/* (non-Javadoc)
     * @see org.alfresco.jlan.server.auth.ICifsAuthenticator#hasExtendedSecurity()
     */
	public final boolean hasExtendedSecurity() {
		return m_extendedSecurity;
	}

	/* (non-Javadoc)
     * @see org.alfresco.jlan.server.auth.ICifsAuthenticator#getAuthContext(org.alfresco.jlan.smb.server.SMBSrvSession)
     */
	public AuthContext getAuthContext(SMBSrvSession sess) {

		AuthContext authCtx = null;

		if ( sess.hasAuthenticationContext() && sess.getAuthenticationContext() instanceof NTLanManAuthContext) {

			// Use the existing authentication context

			authCtx = sess.getAuthenticationContext();
		}
		else {

			// Create a new authentication context for the session

			authCtx = new NTLanManAuthContext();
			sess.setAuthenticationContext(authCtx);
		}

		// Return the authentication context

		return authCtx;
	}

	/* (non-Javadoc)
     * @see org.alfresco.jlan.server.auth.ICifsAuthenticator#getEnabledDialects()
     */
	public final DialectSelector getEnabledDialects() {
		return m_dialects;
	}

	/* (non-Javadoc)
     * @see org.alfresco.jlan.server.auth.ICifsAuthenticator#getSecurityMode()
     */
	public final int getSecurityMode() {
		return m_securityMode;
	}

	/* (non-Javadoc)
     * @see org.alfresco.jlan.server.auth.ICifsAuthenticator#getCIFSConfig()
     */
	public final CIFSConfigSection getCIFSConfig() {
        return (CIFSConfigSection) m_config.getConfigSection(CIFSConfigSection.SectionName);
	}

	/**
	 * Return the security configuration section
	 * 
	 * @return SecurityConfigSection
	 */
	public final SecurityConfigSection getsecurityConfig() {
        return (SecurityConfigSection) m_config.getConfigSection(SecurityConfigSection.SectionName);
	}

	/* (non-Javadoc)
     * @see org.alfresco.jlan.server.auth.ICifsAuthenticator#hasDebug()
     */
	public final boolean hasDebug() {
		return m_debug;
	}

	/* (non-Javadoc)
     * @see org.alfresco.jlan.server.auth.ICifsAuthenticator#generateNegotiateResponse(org.alfresco.jlan.smb.server.SMBSrvSession, org.alfresco.jlan.smb.server.SMBSrvPacket, boolean)
     */
	public void generateNegotiateResponse(SMBSrvSession sess, SMBSrvPacket respPkt, boolean extendedSecurity)
		throws AuthenticatorException {

		// Pack the negotiate response for NT/LanMan challenge/response authentication

		ChallengeAuthContext authCtx = (ChallengeAuthContext) getAuthContext(sess);

		// Encryption key and primary domain string should be returned in the byte area

		int pos = respPkt.getByteOffset();
		byte[] buf = respPkt.getBuffer();

		if ( authCtx == null || authCtx.getChallenge() == null) {

			// Return a dummy encryption key

			for (int i = 0; i < 8; i++)
				buf[pos++] = 0;
		}
		else {

			// Store the encryption key

			byte[] key = authCtx.getChallenge();
			for (int i = 0; i < key.length; i++)
				buf[pos++] = key[i];
		}

		// Pack the local domain name

		String domain = sess.getSMBServer().getCIFSConfiguration().getDomainName();
		if ( domain != null)
			pos = DataPacker.putString(domain, buf, pos, true, true);

		// Pack the local server name

		pos = DataPacker.putString(sess.getSMBServer().getServerName(), buf, pos, true, true);

		// Set the packet length

		respPkt.setByteCount(pos - respPkt.getByteOffset());
	}

	/* (non-Javadoc)
     * @see org.alfresco.jlan.server.auth.ICifsAuthenticator#processSessionSetup(org.alfresco.jlan.smb.server.SMBSrvSession, org.alfresco.jlan.smb.server.SMBSrvPacket)
     */
	public void processSessionSetup(SMBSrvSession sess, SMBSrvPacket reqPkt)
		throws SMBSrvException {

		// Check that the received packet looks like a valid NT session setup andX request

		if ( reqPkt.checkPacketIsValid(13, 0) == false) {
			throw new SMBSrvException(SMBStatus.NTInvalidParameter, SMBStatus.ErrSrv, SMBStatus.SRVNonSpecificError);
		}

		// Extract the session details

		int maxBufSize = reqPkt.getParameter(2);
		int maxMpx = reqPkt.getParameter(3);
		int vcNum = reqPkt.getParameter(4);
		int ascPwdLen = reqPkt.getParameter(7);
		int uniPwdLen = reqPkt.getParameter(8);
		int capabs = reqPkt.getParameterLong(11);

		// Extract the client details from the session setup request

		byte[] buf = reqPkt.getBuffer();

		// Determine if ASCII or unicode strings are being used

		boolean isUni = reqPkt.isUnicode();

		// Extract the password strings

		byte[] ascPwd = reqPkt.unpackBytes(ascPwdLen);
		byte[] uniPwd = reqPkt.unpackBytes(uniPwdLen);

		// Extract the user name string

		String user = reqPkt.unpackString(isUni);

		if ( user == null)
			throw new SMBSrvException(SMBStatus.NTInvalidParameter, SMBStatus.ErrSrv, SMBStatus.SRVNonSpecificError);

		// Extract the clients primary domain name string

		String domain = "";

		if ( reqPkt.hasMoreData()) {

			// Extract the callers domain name

			domain = reqPkt.unpackString(isUni);

			if ( domain == null)
				throw new SMBSrvException(SMBStatus.NTInvalidParameter, SMBStatus.ErrSrv, SMBStatus.SRVNonSpecificError);
		}

		// Extract the clients native operating system

		String clientOS = "";

		if ( reqPkt.hasMoreData()) {

			// Extract the callers operating system name

			clientOS = reqPkt.unpackString(isUni);

			if ( clientOS == null)
				throw new SMBSrvException(SMBStatus.NTInvalidParameter, SMBStatus.ErrSrv, SMBStatus.SRVNonSpecificError);
		}

		// DEBUG

		if ( Debug.EnableInfo && sess.hasDebug(SMBSrvSession.DBG_NEGOTIATE)) {
			Debug.println("[SMB] NT Session setup from user=" + user + ", password="
					+ (uniPwd != null ? HexDump.hexString(uniPwd) : "none") + ", ANSIpwd="
					+ (ascPwd != null ? HexDump.hexString(ascPwd) : "none") + ", domain=" + domain + ", os=" + clientOS + ", VC="
					+ vcNum + ", maxBuf=" + maxBufSize + ", maxMpx=" + maxMpx + ", authCtx=" + sess.getAuthenticationContext());
			Debug.println("[SMB]  MID=" + reqPkt.getMultiplexId() + ", UID=" + reqPkt.getUserId() + ", PID="
					+ reqPkt.getProcessId());
		}

		// Store the client maximum buffer size, maximum multiplexed requests count and client
		// capability flags

		sess.setClientMaximumBufferSize(maxBufSize != 0 ? maxBufSize : SMBSrvSession.DefaultBufferSize);
		sess.setClientMaximumMultiplex(maxMpx);
		sess.setClientCapabilities(capabs);

		// Create the client information and store in the session

		ClientInfo client = ClientInfo.getFactory().createInfo(user, uniPwd);
		client.setANSIPassword(ascPwd);
		client.setDomain(domain);
		client.setOperatingSystem(clientOS);

		if ( sess.hasRemoteAddress())
			client.setClientAddress(sess.getRemoteAddress().getHostAddress());

		// Check if this is a null session logon

		if ( user.length() == 0 && domain.length() == 0 && uniPwdLen == 0 && ascPwdLen == 1)
			client.setLogonType(ClientInfo.LogonNull);

		// Authenticate the user

		boolean isGuest = false;

		int sts = authenticateUser(client, sess, ICifsAuthenticator.NTLM1);

		if ( sts > 0 && (sts & ICifsAuthenticator.AUTH_GUEST) != 0) {

			// Guest logon

			isGuest = true;

			// DEBUG

			if ( Debug.EnableInfo && sess.hasDebug(SMBSrvSession.DBG_NEGOTIATE))
				Debug.println("[SMB] User " + user + ", logged on as guest");
		}
		else if ( sts != ICifsAuthenticator.AUTH_ALLOW) {

			// DEBUG

			if ( Debug.EnableInfo && sess.hasDebug(SMBSrvSession.DBG_NEGOTIATE))
				Debug.println("[SMB] User " + user + ", access denied");

			// Invalid user, reject the session setup request

			throw new SMBSrvException(SMBStatus.NTLogonFailure, SMBStatus.ErrDos, SMBStatus.DOSAccessDenied);
		}
		else if ( Debug.EnableInfo && sess.hasDebug(SMBSrvSession.DBG_NEGOTIATE)) {

			// DEBUG

			Debug.println("[SMB] User " + user + " logged on "
					+ (client != null ? " (type " + client.getLogonTypeString() + ")" : ""));
		}

		// Create a virtual circuit and allocate a UID to the new circuit

		VirtualCircuit vc = new VirtualCircuit(vcNum, client);
		int uid = sess.addVirtualCircuit(vc);

		if ( uid == VirtualCircuit.InvalidUID) {

			// DEBUG

			if ( Debug.EnableInfo && sess.hasDebug(SMBSrvSession.DBG_NEGOTIATE))
				Debug.println("[SMB] Failed to allocate UID for virtual circuit, " + vc);

			// Failed to allocate a UID

			throw new SMBSrvException(SMBStatus.NTLogonFailure, SMBStatus.ErrDos, SMBStatus.DOSAccessDenied);
		}
		else if ( Debug.EnableInfo && sess.hasDebug(SMBSrvSession.DBG_NEGOTIATE)) {

			// DEBUG

			Debug.println("[SMB] Allocated UID=" + uid + " for VC=" + vc);
		}

		// Set the guest flag for the client, indicate that the session is logged on

		if ( client.isNullSession() == false)
			client.setGuest(isGuest);
		sess.setLoggedOn(true);

		// Check for virtual circuit zero, disconnect any other sessions from this client
		
		if ( vcNum == 0 && hasSessionCleanup()) {
		
			// Disconnect other sessions from this client, cleanup any open files/locks/oplocks
			
			int discCnt = sess.disconnectClientSessions();

			// DEBUG

			if ( discCnt > 0 && Debug.EnableInfo && sess.hasDebug(SMBSrvSession.DBG_NEGOTIATE))
				Debug.println("[SMB] Disconnected " + discCnt + " existing sessions from client, sess=" + sess);
		}
		
		// Check if there is a chained commmand with the session setup request (usually a TreeConnect)
		
		SMBSrvPacket respPkt = reqPkt;
		
		if ( reqPkt.hasAndXCommand()) {

			try {

				// Allocate a new packet for the response
			
				respPkt = sess.getPacketPool().allocatePacket( reqPkt.getLength(), reqPkt);
			}
			catch ( NoPooledMemoryException ex) {
				
				// No memory, return a server error
				
				throw new SMBSrvException(SMBStatus.ErrSrv, SMBStatus.SRVNoBuffers);
			}
		}
		
		// Build the session setup response SMB

		respPkt.setParameterCount(3);
		respPkt.setParameter(0, 0); // No chained response
		respPkt.setParameter(1, 0); // Offset to chained response
		respPkt.setParameter(2, isGuest ? 1 : 0);
		respPkt.setByteCount(0);

		respPkt.setTreeId(0);
		respPkt.setUserId(uid);

		// Set the various flags

		int flags = respPkt.getFlags();
		flags &= ~SMBSrvPacket.FLG_CASELESS;
		respPkt.setFlags(flags);

		int flags2 = SMBSrvPacket.FLG2_LONGFILENAMES;
		if ( isUni)
			flags2 += SMBSrvPacket.FLG2_UNICODE;

		if ( hasExtendedSecurity() == false)
			flags2 &= ~SMBSrvPacket.FLG2_EXTENDEDSECURITY;

		respPkt.setFlags2(flags2);

		// Pack the OS, dialect and domain name strings.

		int pos = respPkt.getByteOffset();
		buf = respPkt.getBuffer();

		if ( isUni)
			pos = DataPacker.wordAlign(pos);

		pos = DataPacker.putString("Java", buf, pos, true, isUni);
		pos = DataPacker.putString("Alfresco CIFS Server " + sess.getServer().isVersion(), buf, pos, true, isUni);
		pos = DataPacker.putString(sess.getSMBServer().getCIFSConfiguration().getDomainName(), buf, pos, true, isUni);

		respPkt.setByteCount(pos - respPkt.getByteOffset());
		respPkt.setParameter(1, pos - RFCNetBIOSProtocol.HEADER_LEN);
	}

	/* (non-Javadoc)
     * @see org.alfresco.jlan.server.auth.ICifsAuthenticator#getEncryptionKeyLength()
     */
	public int getEncryptionKeyLength() {

		return STANDARD_CHALLENGE_LEN;
	}

	/* (non-Javadoc)
     * @see org.alfresco.jlan.server.auth.ICifsAuthenticator#getServerCapabilities()
     */
	public int getServerCapabilities() {

		return Capability.Unicode + Capability.RemoteAPIs + Capability.NTSMBs + Capability.NTFind + Capability.NTStatus
				+ Capability.LargeFiles + Capability.LargeRead + Capability.LargeWrite;
	}

	/* (non-Javadoc)
     * @see org.alfresco.jlan.server.auth.ICifsAuthenticator#allowGuest()
     */
	public final boolean allowGuest() {
		return m_allowGuest;
	}

	/* (non-Javadoc)
     * @see org.alfresco.jlan.server.auth.ICifsAuthenticator#getGuestUserName()
     */
	public final String getGuestUserName() {
		return m_guestUserName;
	}

	/* (non-Javadoc)
     * @see org.alfresco.jlan.server.auth.ICifsAuthenticator#mapUnknownUserToGuest()
     */
	public final boolean mapUnknownUserToGuest() {
		return m_mapToGuest;
	}

	/**
	 * Enable/disable the guest account
	 * 
	 * @param ena Enable the guest account if true, only allow defined user accounts access if false
	 */
	public final void setAllowGuest(boolean ena) {
		m_allowGuest = ena;
	}

	/**
	 * Set the guest user name
	 * 
	 * @param guest String
	 */
	public final void setGuestUserName(String guest) {
		m_guestUserName = guest;
	}

	/**
	 * Enable/disable mapping of unknown users to the guest account
	 * 
	 * @param ena Enable mapping of unknown users to the guest if true
	 */
	public final void setMapToGuest(boolean ena) {
		m_mapToGuest = ena;
	}

	/**
	 * Set the security mode flags
	 * 
	 * @param flg int
	 */
	protected final void setSecurityMode(int flg) {
		m_securityMode = flg;
	}

	/**
	 * Set the extended security flag
	 * 
	 * @param extSec boolean
	 */
	protected final void setExtendedSecurity(boolean extSec) {
		m_extendedSecurity = extSec;
	}

	/**
	 * Cleanup existing sessions from the same client address/name
	 * 
	 * @return boolean
	 */
	public final boolean hasSessionCleanup() {
		return m_sessCleanup;
	}
	
	/**
	 * Enable/disable session cleanup when a new logon is received using virtual circuit zero
	 * 
	 * @param ena boolean
	 */
	public void setSessionCleanup( boolean ena) {
		m_sessCleanup = ena;
	}
    
	/* (non-Javadoc)
     * @see org.alfresco.jlan.server.auth.ICifsAuthenticator#closeAuthenticator()
     */
	public void closeAuthenticator() {

		// Override if cleanup required
	}

	/**
	 * Validate a password by encrypting the plain text password using the specified encryption key
	 * and encryption algorithm.
	 * 
	 * @param user UserAccount
	 * @param client ClientInfo
	 * @param authCtx AuthContext
	 * @param alg int
	 * @return boolean
	 */
	protected final boolean validatePassword(UserAccount user, ClientInfo client, AuthContext authCtx, int alg) {

		// Get the challenge

		byte[] encryptKey = null;

		if ( authCtx != null && authCtx instanceof NTLanManAuthContext) {

			// Get the NT/LanMan challenge

			NTLanManAuthContext ntlmCtx = (NTLanManAuthContext) authCtx;
			encryptKey = ntlmCtx.getChallenge();
		}
		else
			return false;

		// Get the encrypted password

		byte[] encryptedPwd = null;

		if ( alg == LANMAN)
			encryptedPwd = client.getANSIPassword();
		else
			encryptedPwd = client.getPassword();

		// Check if the user account has the MD4 password hash

		byte[] encPwd = null;

		if ( user.hasMD4Password() && alg != LANMAN) {

			try {

				// Generate the encrpyted password

				if ( alg == NTLM1) {

					// Get the MD4 hashed password

					byte[] p21 = new byte[21];
					System.arraycopy(user.getMD4Password(), 0, p21, 0, user.getMD4Password().length);

					// Generate an NTLMv1 encrypted password

					encPwd = getEncryptor().doNTLM1Encryption(p21, encryptKey);
				}
				else if ( alg == NTLM2) {

					// Generate an NTLMv2 encrypted password

					encPwd = getEncryptor().doNTLM2Encryption(user.getMD4Password(), client.getUserName(), client.getDomain());
				}
			}
			catch (NoSuchAlgorithmException ex) {
			}
			catch (InvalidKeyException ex) {
			}
		}
		else {

			// Generate an encrypted version of the plain text password

			encPwd = generateEncryptedPassword(user.getPassword() != null ? user.getPassword() : "", encryptKey, alg, client
					.getUserName(), client.getDomain());
		}

		// Compare the generated password with the received password

		if ( encPwd != null && encryptedPwd != null && encPwd.length == STANDARD_PASSWORD_LEN
				&& encryptedPwd.length == STANDARD_PASSWORD_LEN) {

			// Compare the password arrays

			for (int i = 0; i < STANDARD_PASSWORD_LEN; i++)
				if ( encPwd[i] != encryptedPwd[i])
					return false;

			// Password is valid

			return true;
		}

		// User or password is invalid

		return false;
	}

	/**
	 * Convert the password string to a byte array
	 * 
	 * @param pwd String
	 * @return byte[]
	 */

	protected final byte[] convertPassword(String pwd) {

		// Create a padded/truncated 14 character string

		StringBuffer p14str = new StringBuffer();
		p14str.append(pwd);
		if ( p14str.length() > 14)
			p14str.setLength(14);
		else {
			while (p14str.length() < 14)
				p14str.append((char) 0x00);
		}

		// Convert the P14 string to an array of bytes. Allocate the return 16 byte array.

		return p14str.toString().getBytes();
	}

	/**
	 * Return the password encryptor
	 * 
	 * @return PasswordEncryptor
	 */
	protected final PasswordEncryptor getEncryptor() {
		return m_encryptor;
	}

	/**
	 * Return the authentication status as a string
	 * 
	 * @param sts int
	 * @return String
	 */
	protected final String getStatusAsString(int sts) {

		String str = null;

		switch (sts) {
			case AUTH_ALLOW:
				str = "Allow";
				break;
			case AUTH_DISALLOW:
				str = "Disallow";
				break;
			case AUTH_GUEST:
				str = "Guest";
				break;
			case AUTH_BADPASSWORD:
				str = "BadPassword";
				break;
			case AUTH_BADUSER:
				str = "BadUser";
				break;
		}

		return str;
	}

	/**
	 * Set the access mode of the server.
	 * 
	 * @param mode Either SHARE_MODE or USER_MODE.
	 */
	public final void setAccessMode(int mode) {
		m_accessMode = mode;
	}

	/**
	 * Logon using the guest user account
	 * 
	 * @param client ClientInfo
	 * @param sess SrvSession
	 */
	protected void doGuestLogon(ClientInfo client, SrvSession sess) {

		// Set the home folder for the guest user

		client.setUserName(getGuestUserName());

		// Mark the client as being a guest logon

		client.setGuest(true);
	}

	/* (non-Javadoc)
     * @see org.alfresco.jlan.server.auth.ICifsAuthenticator#getUserDetails(java.lang.String)
     */
	public final UserAccount getUserDetails(String user) {

		// Get the user account details via the users interface

		return getsecurityConfig().getUsersInterface().getUserAccount(user);
	}

	/**
	 * Set the current authenticated user context for this thread
	 * 
	 * @param client ClientInfo
	 */
	public void setCurrentUser(ClientInfo client) {
	}

	/**
	 * Map a client IP address to a domain
	 * 
	 * @param clientIP InetAddress
	 * @return String
	 */
	protected final String mapClientAddressToDomain(InetAddress clientIP) {

		// Check if there are any domain mappings

	    SecurityConfigSection securityConfig = getsecurityConfig();
		if ( securityConfig.hasDomainMappings() == false)
			return null;

		// Convert the client IP address to an integer value

		int clientAddr = IPAddress.asInteger(clientIP);

		for (DomainMapping domainMap : securityConfig.getDomainMappings()) {

			if ( domainMap.isMemberOfDomain(clientAddr)) {

				// DEBUG

				if ( Debug.EnableInfo && hasDebug())
					Debug.println("Mapped client IP " + clientIP + " to domain " + domainMap.getDomain());

				return domainMap.getDomain();
			}
		}

		// DEBUG

		if ( Debug.EnableInfo && hasDebug())
			Debug.println("Failed to map client IP " + clientIP + " to a domain");

		// No domain mapping for the client address

		return null;
	}

	/**
	 * Generate a description for debugging purposes
	 */
    @Override
    public String toString()
    {
        return getClass().getName() + ", mode="
        + (getAccessMode() == ICifsAuthenticator.SHARE_MODE ? "SHARE" : "USER");
    }
}
