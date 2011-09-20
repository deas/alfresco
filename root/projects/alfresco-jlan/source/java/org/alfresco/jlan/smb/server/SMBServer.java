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

package org.alfresco.jlan.smb.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.server.ServerListener;
import org.alfresco.jlan.server.SrvSessionList;
import org.alfresco.jlan.server.Version;
import org.alfresco.jlan.server.auth.ICifsAuthenticator;
import org.alfresco.jlan.server.config.ConfigId;
import org.alfresco.jlan.server.config.ConfigurationListener;
import org.alfresco.jlan.server.config.CoreServerConfigSection;
import org.alfresco.jlan.server.config.InvalidConfigurationException;
import org.alfresco.jlan.server.config.ServerConfiguration;
import org.alfresco.jlan.server.core.DeviceContext;
import org.alfresco.jlan.server.core.InvalidDeviceInterfaceException;
import org.alfresco.jlan.server.core.ShareType;
import org.alfresco.jlan.server.core.SharedDevice;
import org.alfresco.jlan.server.filesys.DiskInterface;
import org.alfresco.jlan.server.filesys.NetworkFileServer;
import org.alfresco.jlan.server.thread.ThreadRequestPool;
import org.alfresco.jlan.smb.Dialect;
import org.alfresco.jlan.smb.DialectSelector;
import org.alfresco.jlan.smb.ServerType;
import org.alfresco.jlan.smb.dcerpc.UUID;
import org.alfresco.jlan.smb.server.nio.NIOCifsConnectionsHandler;
import org.alfresco.jlan.smb.server.nio.win32.AsyncWinsockCifsConnectionsHandler;
import org.alfresco.jlan.smb.server.win32.Win32NetBIOSLanaMonitor;

/**
 * CIFS Server Class
 * 
 * @author gkspencer
 */
public class SMBServer extends NetworkFileServer implements Runnable, ConfigurationListener {

	// Constants
	//
	// Server version

	private static final String ServerVersion = Version.SMBServerVersion;

	// CIFS server custom server events

	public static final int CIFSNetBIOSNamesAdded = ServerListener.ServerCustomEvent;

	// Configuration sections

	private CIFSConfigSection m_cifsConfig;
	private CoreServerConfigSection m_coreConfig;
	
	// Server thread

	private Thread m_srvThread;

	// Session connections handler
	
	private CifsConnectionsHandler m_connectionsHandler;
	
	// Active session list

	private SrvSessionList m_sessions;

	// Server type flags, used when announcing the host

	private int m_srvType = ServerType.WorkStation + ServerType.Server;

	// Server GUID

	private UUID m_serverGUID;

	// CIFS packet pool
	
	private CIFSPacketPool m_packetPool;
	
	/**
	 * Create an SMB server using the specified configuration.
	 * 
	 * @param cfg ServerConfiguration
	 */
	public SMBServer(ServerConfiguration cfg) throws IOException {

		super("CIFS", cfg);

		// Call the common constructor

		CommonConstructor();
	}

	/**
	 * Add a new session to the server
	 * 
	 * @param sess SMBSrvSession
	 */
	public final void addSession(SMBSrvSession sess) {

		// Add the session to the session list

		m_sessions.addSession(sess);

		// Propagate the debug settings to the new session

		if ( Debug.EnableInfo && hasDebug()) {

			// Enable session debugging, output to the same stream as the server

			sess.setDebug(getCIFSConfiguration().getSessionDebugFlags());
		}
	}

	/**
	 * Check if the disk share is read-only.
	 * 
	 * @param shr SharedDevice
	 */
	protected final void checkReadOnly(SharedDevice shr) {

		// For disk devices check if the shared device is read-only, this should also check if the
		// shared device path actually exists.

		if ( shr.getType() == ShareType.DISK) {

			// Check if the disk device is read-only

			try {

				// Get the device interface for the shared device

				DiskInterface disk = (DiskInterface) shr.getInterface();
				if ( disk.isReadOnly(null, shr.getContext())) {

					// The disk is read-only, mark the share as read-only

					int attr = shr.getAttributes();
					if ( (attr & SharedDevice.ReadOnly) == 0)
						attr += SharedDevice.ReadOnly;
					shr.setAttributes(attr);

					// Debug

					if ( Debug.EnableInfo && hasDebug())
						Debug.println("[SMB] Add Share " + shr.toString() + " : isReadOnly");
				}
			}
			catch (InvalidDeviceInterfaceException ex) {

				// Shared device interface error

				if ( Debug.EnableInfo && hasDebug())
					Debug.println("[SMB] Add Share " + shr.toString() + " : " + ex.toString());
			}
			catch (FileNotFoundException ex) {

				// Shared disk device local path does not exist

				if ( Debug.EnableInfo && hasDebug())
					Debug.println("[SMB] Add Share " + shr.toString() + " : " + ex.toString());
			}
			catch (IOException ex) {

				// Shared disk device access error

				if ( Debug.EnableInfo && hasDebug())
					Debug.println("[SMB] Add Share " + shr.toString() + " : " + ex.toString());
			}
		}
	}

	/**
	 * Common constructor code.
	 */
	private void CommonConstructor()
		throws IOException {

		// Get the CIFS server configuration

		m_cifsConfig = (CIFSConfigSection) getConfiguration().getConfigSection(CIFSConfigSection.SectionName);

		if ( m_cifsConfig != null) {

			// Add the SMB server as a configuration change listener of the server configuration

			getConfiguration().addListener(this);

			// Check if debug output is enabled

			if ( getCIFSConfiguration().getSessionDebugFlags() != 0)
				setDebug(true);

			// Set the server version

			setVersion(ServerVersion);

			// Create the active session list

			m_sessions = new SrvSessionList();
			
			// Get the core server configuration
			
			m_coreConfig = (CoreServerConfigSection) getConfiguration().getConfigSection( CoreServerConfigSection.SectionName);
			if ( m_coreConfig != null) {

				// Create the CIFS packet pool using the global memory pool
				
				m_packetPool = new CIFSPacketPool( m_coreConfig.getMemoryPool());
				
				// Check if packet pool debugging is enabled
				
				if (( m_cifsConfig.getSessionDebugFlags() & SMBSrvSession.DBG_PKTPOOL) != 0)
					m_packetPool.setDebug( true);
			}
		}
		else
			setEnabled(false);
		
	}

	/**
	 * Delete temporary shares created by the share mapper for the specified session
	 * 
	 * @param sess SMBSrvSession
	 */
	public final void deleteTemporaryShares(SMBSrvSession sess) {

		// Delete temporary shares via the share mapper

		getShareMapper().deleteShares(sess);
	}

	/**
	 * Return the CIFS server configuration
	 * 
	 * @return CIFSConfigSection
	 */
	public final CIFSConfigSection getCIFSConfiguration() {
		return m_cifsConfig;
	}

	/**
	 * Return the server comment.
	 * 
	 * @return java.lang.String
	 */
	public final String getComment() {
		return getCIFSConfiguration().getComment();
	}

	/**
	 * Return the CIFS server name
	 * 
	 * @return String
	 */
	public final String getServerName() {
		return getCIFSConfiguration().getServerName();
	}

	/**
	 * Return the server type flags.
	 * 
	 * @return int
	 */
	public final int getServerType() {
		return m_srvType;
	}

	/**
	 * Return the per session debug flag settings.
	 */
	public final int getSessionDebug() {
		return getCIFSConfiguration().getSessionDebugFlags();
	}

	/**
	 * Return the list of SMB dialects that this server supports.
	 * 
	 * @return DialectSelector
	 */
	public final DialectSelector getSMBDialects() {
		return getCIFSConfiguration().getEnabledDialects();
	}

	/**
	 * Return the CIFS authenticator
	 * 
	 * @return CifsAuthenticator
	 */
	public final ICifsAuthenticator getCifsAuthenticator() {
		return getCIFSConfiguration().getAuthenticator();
	}

	/**
	 * Return the active session list
	 * 
	 * @return SrvSessionList
	 */
	public final SrvSessionList getSessions() {
		return m_sessions;
	}

	/**
	 * Return the CIFS packet pool
	 * 
	 * @return CIFSPacketPool
	 */
	public final CIFSPacketPool getPacketPool() {
		return m_packetPool;
	}
	
	/**
	 * Return the thread pool
	 * 
	 * @return ThreadRequestPool
	 */
	public final ThreadRequestPool getThreadPool() {
		return m_coreConfig.getThreadPool();
	}
	
	/**
	 * Start the SMB server.
	 */
	public void run() {

		// Fire a server startup event

		fireServerEvent(ServerListener.ServerStartup);

		// Indicate that the server is active

		setActive(true);

		// Check if we are running under Windows

		boolean isWindows = isWindowsNTOnwards();

		// Generate a GUID for the server based on the server name

		Random r = new Random();
		m_serverGUID = new UUID(r.nextLong(), r.nextLong());

		// Debug

		if ( Debug.EnableInfo && hasDebug()) {

			// Dump the server name/version and Java runtime details

			Debug.println("[SMB] CIFS Server " + getServerName() + " starting");
			Debug.print("[SMB] Version " + isVersion());
			Debug.print(", Java VM " + System.getProperty("java.vm.version"));
			Debug.println(", OS " + System.getProperty("os.name") + ", version " + System.getProperty("os.version"));

			// Check for server alias names

			if ( getCIFSConfiguration().hasAliasNames())
				Debug.println("[SMB] Server alias(es) : " + getCIFSConfiguration().getAliasNames());

			// Output the authenticator details

			if ( getCifsAuthenticator() != null)
				Debug.println("[SMB] Using authenticator " + getCifsAuthenticator().toString());

			// Display the timezone offset/name

			if ( getGlobalConfiguration().getTimeZone() != null)
				Debug.println("[SMB] Server timezone " + getGlobalConfiguration().getTimeZone() + ", offset from UTC = "
						+ getGlobalConfiguration().getTimeZoneOffset() / 60 + "hrs");
			else
				Debug.println("[SMB] Server timezone offset = " + getGlobalConfiguration().getTimeZoneOffset() / 60 + "hrs");

			// Dump the available dialect list

			Debug.println("[SMB] Dialects enabled = " + getSMBDialects());

			// Dump the share list

			Debug.println("[SMB] Shares:");
			Enumeration<SharedDevice> enm = getFullShareList(getCIFSConfiguration().getServerName(), null).enumerateShares();

			while (enm.hasMoreElements()) {
				SharedDevice share = enm.nextElement();
				Debug.println("[SMB]  " + share.toString() + " "
						+ (share.getContext() != null ? share.getContext().toString() : ""));
			}
		}

		// Create a server socket to listen for incoming session requests

		try {

			// Add the IPC$ named pipe shared device

			AdminSharedDevice admShare = new AdminSharedDevice();
			getFilesystemConfiguration().addShare(admShare);

			// Clear the server shutdown flag

			setShutdown(false);

			// Get the list of IP addresses the server is bound to

			getServerIPAddresses();

			// Check if the NT SMB dialect is enabled, if so then update the server flags to
			// indicate that this is an NT server

			if ( getCIFSConfiguration().getEnabledDialects().hasDialect(Dialect.NT) == true) {

				// Enable the NT server flag

				getCIFSConfiguration().setServerType(getServerType() + ServerType.NTServer);

				// Debug

				if ( Debug.EnableInfo && hasDebug())
					Debug.println("[SMB] Added NTServer flag to host announcement");
			}

			// Create the CIFS connections handler
			//
			// Note: The older thread per session/socket handler is used for Win32 NetBIOS connections

			if ( getCIFSConfiguration().hasDisableNIOCode() || getCIFSConfiguration().hasWin32NetBIOS()) {
				
				// Use the older threaded connections handler (thread per session model)
			
				m_connectionsHandler = new ThreadedCifsConnectionsHandler();
			}
			else {
				
				// Check if the Java socket or JNI based connections handler should be used
				
				if ( getCIFSConfiguration().hasTcpipSMB() || getCIFSConfiguration().hasNetBIOSSMB()) {
					
					// Use the NIO based native SMB/NetBIOS SMB connections handler
				
					m_connectionsHandler = new NIOCifsConnectionsHandler();
				}
				else {
					
					// Use the JNI based Winsock NetBIOS connections handler
					
					m_connectionsHandler = new AsyncWinsockCifsConnectionsHandler();
				}
			}
			
			// Initialize the connections handler
			
			m_connectionsHandler.initializeHandler( this, getCIFSConfiguration());
			m_connectionsHandler.startHandler();
			
			// Check if there are any session handlers installed, if not then close the server

			if ( m_connectionsHandler.numberOfSessionHandlers() > 0 || getCIFSConfiguration().hasWin32NetBIOS()) {

				// Fire a server active event

				fireServerEvent(ServerListener.ServerActive);

				// Wait for incoming connection requests

				while (hasShutdown() == false) {

					// Sleep for a while

					try {
						Thread.sleep(3000L);
					}
					catch (InterruptedException ex) {
					}
				}
			}
			else if ( Debug.EnableError && hasDebug()) {

				// DEBUG

				Debug.println("[SMB] No valid session handlers, server closing");
			}
		}
		catch (Exception ex) {

			// Do not report an error if the server has shutdown, closing the server socket
			// causes an exception to be thrown.

			if ( hasShutdown() == false) {
				Debug.println("[SMB] Server error : " + ex.toString(), Debug.Error);
				Debug.println(ex);

				// Store the error, fire a server error event

				setException(ex);
				fireServerEvent(ServerListener.ServerError);
			}
		}

		// Debug

		if ( Debug.EnableInfo && hasDebug())
			Debug.println("[SMB] SMB Server shutting down ...");

		// Close the host announcer and session handlers

		m_connectionsHandler.stopHandler();

		// Shutdown the Win32 NetBIOS LANA monitor, if enabled

		if ( isWindows && Win32NetBIOSLanaMonitor.getLanaMonitor() != null) {

			// Shutdown the LANA monitor

			Win32NetBIOSLanaMonitor.getLanaMonitor().shutdownRequest();
		}

		// Indicate that the server is not active

		setActive(false);
		fireServerEvent(ServerListener.ServerShutdown);
		
		// DEBUG
		
		if ( Debug.EnableInfo && hasDebug())
			Debug.println("[SMB] Packet pool at shutdown: " + getPacketPool());
	}

	/**
	 * Notify the server that a session has been closed.
	 * 
	 * @param sess SMBSrvSession
	 */
	protected final void sessionClosed(SMBSrvSession sess) {

		// Remove the session from the active session list

		m_sessions.removeSession(sess);

		// DEBUG
		
		if ( hasDebug()) {
			Debug.println("[SMB] Closed session " + sess.getSessionId() + ", sessions=" + m_sessions.numberOfSessions());
			if ( m_sessions.numberOfSessions() > 0 && m_sessions.numberOfSessions() <= 10) {
				Enumeration<Integer> sessIds = m_sessions.enumerate();
				Debug.print("      Active sessions [");
				while ( sessIds.hasMoreElements()) {
					SMBSrvSession curSess = (SMBSrvSession) m_sessions.findSession( sessIds.nextElement());
					Debug.print("" + curSess.getSessionId() + "=" + ( curSess.hasRemoteAddress() ? curSess.getRemoteAddress().getHostAddress() : "NoAddress") + ",");
				}
				Debug.println("]");
			}
		}
		
		// Notify session listeners that a session has been closed

		fireSessionClosedEvent(sess);
	}

	/**
	 * Notify the server that a user has logged on.
	 * 
	 * @param sess SMBSrvSession
	 */
	protected final void sessionLoggedOn(SMBSrvSession sess) {

		// Notify session listeners that a user has logged on.

		fireSessionLoggedOnEvent(sess);
	}

	/**
	 * Notify the server that a session has been closed.
	 * 
	 * @param sess SMBSrvSession
	 */
	protected final void sessionOpened(SMBSrvSession sess) {

		// Notify session listeners that a session has been closed

		fireSessionOpenEvent(sess);
	}

	/**
	 * Shutdown the SMB server
	 * 
	 * @param immediate boolean
	 */
	public final void shutdownServer(boolean immediate) {

		// Indicate that the server is closing

		setShutdown(true);

		try {

			// Wakeup the main CIFS server thread

			m_srvThread.interrupt();
		}
		catch (Exception ex) {
		}

		// Close the active sessions

		Enumeration<Integer> enm = m_sessions.enumerate();

		while (enm.hasMoreElements()) {

			// Get the session id and associated session

			Integer sessId = enm.nextElement();
			SMBSrvSession sess = (SMBSrvSession) m_sessions.findSession(sessId);

			// Inform listeners that the session has been closed

			fireSessionClosedEvent(sess);

			// Close the session

			sess.closeSession();
		}

		// Wait for the main server thread to close

		if ( m_srvThread != null) {

			try {
				m_srvThread.join(3000);
			}
			catch (Exception ex) {
			}
		}

		// Fire a shutdown notification event

		fireServerEvent(ServerListener.ServerShutdown);
	}

	/**
	 * Start the SMB server in a seperate thread
	 */
	public void startServer() {

		// Create a seperate thread to run the SMB server

		m_srvThread = new Thread(this);
		m_srvThread.setName("CIFS Server");

		m_srvThread.start();
	}

	/**
	 * Validate configuration changes that are relevant to the SMB server
	 * 
	 * @param id int
	 * @param config ServerConfiguration
	 * @param newVal Object
	 * @return int
	 * @throws InvalidConfigurationException
	 */
	public int configurationChanged(int id, ServerConfiguration config, Object newVal)
		throws InvalidConfigurationException {

		int sts = ConfigurationListener.StsIgnored;

		try {

			// Check if the configuration change affects the SMB server

			switch (id) {

			// Server enable/disable

			case ConfigId.ServerSMBEnable:

				// Check if the server is active

				Boolean enaSMB = (Boolean) newVal;

				if ( isActive() && enaSMB.booleanValue() == false) {

					// Shutdown the server

					shutdownServer(false);
				}
				else if ( isActive() == false && enaSMB.booleanValue() == true) {

					// Start the server

					startServer();
				}

				// Indicate that the setting was accepted

				sts = ConfigurationListener.StsAccepted;
				break;

			// Changes that can be accepted without restart

			case ConfigId.SMBComment:
			case ConfigId.SMBDialects:
			case ConfigId.SMBTCPPort:
			case ConfigId.SMBMacExtEnable:
			case ConfigId.SMBDebugEnable:
			case ConfigId.ServerTimezone:
			case ConfigId.ServerTZOffset:
			case ConfigId.ShareList:
			case ConfigId.ShareMapper:
			case ConfigId.SecurityAuthenticator:
			case ConfigId.UsersList:
			case ConfigId.DebugDevice:
				sts = ConfigurationListener.StsAccepted;
				break;

			// Changes that affect new sessions only

			case ConfigId.SMBSessionDebug:
				sts = ConfigurationListener.StsNewSessionsOnly;
				if ( newVal instanceof Integer) {
					Integer dbgVal = (Integer) newVal;
					setDebug( dbgVal.intValue() != 0 ? true : false);
				}
				break;

			// Changes that require a restart

			case ConfigId.SMBHostName:
			case ConfigId.SMBAliasNames:
			case ConfigId.SMBDomain:
			case ConfigId.SMBBroadcastMask:
			case ConfigId.SMBAnnceEnable:
			case ConfigId.SMBAnnceInterval:
			case ConfigId.SMBAnnceDebug:
			case ConfigId.SMBTCPEnable:
			case ConfigId.SMBBindAddress:
				sts = ConfigurationListener.StsRestartRequired;
				break;
			}
		}
		catch (Exception ex) {
			throw new InvalidConfigurationException("SMB Server configuration error", ex);
		}

		// Return the status

		return sts;
	}

	/**
	 * Determine if we are running under Windows NT onwards
	 * 
	 * @return boolean
	 */
	private final boolean isWindowsNTOnwards() {

		// Get the operating system name property

		String osName = System.getProperty("os.name");

		if ( osName.startsWith("Windows")) {
			if ( osName.endsWith("95") || osName.endsWith("98") || osName.endsWith("ME")) {

				// Windows 95-ME

				return false;
			}

			// Looks like Windows NT onwards

			return true;
		}

		// Not Windows

		return false;
	}

	/**
	 * Get the list of local IP addresses
	 * 
	 */
	private final void getServerIPAddresses() {

		try {

			// Get the local IP address list

			Enumeration<NetworkInterface> enm = NetworkInterface.getNetworkInterfaces();
			Vector<InetAddress> addrList = new Vector<InetAddress>();

			while (enm.hasMoreElements()) {

				// Get the current network interface

				NetworkInterface ni = enm.nextElement();

				// Get the address list for the current interface

				Enumeration<InetAddress> addrs = ni.getInetAddresses();

				while (addrs.hasMoreElements())
					addrList.add(addrs.nextElement());
			}

			// Convert the vector of addresses to an array

			if ( addrList.size() > 0) {

				// Convert the address vector to an array

				InetAddress[] inetAddrs = new InetAddress[addrList.size()];

				// Copy the address details to the array

				for (int i = 0; i < addrList.size(); i++)
					inetAddrs[i] = addrList.elementAt(i);

				// Set the server IP address list

				setServerAddresses(inetAddrs);
			}
		}
		catch (Exception ex) {

			// DEBUG

			if ( Debug.EnableError && hasDebug())
				Debug.println("[SMB] Error getting local IP addresses, " + ex.toString());
		}
	}

	/**
	 * Return the server GUID
	 * 
	 * @return UUID
	 */
	public final UUID getServerGUID() {
		return m_serverGUID;
	}

	/**
	 * Send a NetBIOS names added event to server listeners
	 * 
	 * @param lana int
	 */
	public final void fireNetBIOSNamesAddedEvent(int lana) {

		// Send the event to registered listeners, encode the LANA id in the top of the event id

		fireServerEvent(CIFSNetBIOSNamesAdded + (lana << 16));
	}
}
