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

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.netbios.NetBIOSException;
import org.alfresco.jlan.netbios.NetBIOSName;
import org.alfresco.jlan.netbios.NetBIOSSession;
import org.alfresco.jlan.netbios.RFCNetBIOSProtocol;
import org.alfresco.jlan.server.SrvSession;
import org.alfresco.jlan.server.auth.AuthenticatorException;
import org.alfresco.jlan.server.auth.ICifsAuthenticator;
import org.alfresco.jlan.server.filesys.DeferredPacketException;
import org.alfresco.jlan.server.filesys.DiskDeviceContext;
import org.alfresco.jlan.server.filesys.NetworkFile;
import org.alfresco.jlan.server.filesys.TooManyConnectionsException;
import org.alfresco.jlan.server.filesys.TreeConnection;
import org.alfresco.jlan.server.thread.ThreadRequestPool;
import org.alfresco.jlan.smb.Capability;
import org.alfresco.jlan.smb.DataType;
import org.alfresco.jlan.smb.Dialect;
import org.alfresco.jlan.smb.DialectSelector;
import org.alfresco.jlan.smb.NTTime;
import org.alfresco.jlan.smb.PacketType;
import org.alfresco.jlan.smb.SMBDate;
import org.alfresco.jlan.smb.SMBErrorText;
import org.alfresco.jlan.smb.SMBStatus;
import org.alfresco.jlan.smb.server.notify.NotifyRequest;
import org.alfresco.jlan.smb.server.notify.NotifyRequestList;
import org.alfresco.jlan.util.DataPacker;
import org.alfresco.jlan.util.HexDump;
import org.alfresco.jlan.util.StringList;

/**
 * <p>
 * The SMB server creates a server session object for each incoming session request.
 * 
 * <p>
 * The server session holds the context of a particular session, including the list of open files
 * and active searches.
 * 
 * @author gkspencer
 */
public class SMBSrvSession extends SrvSession implements Runnable {

	// Define the default receive buffer size to allocate.

	public static final int DefaultBufferSize = 0x010000 + RFCNetBIOSProtocol.HEADER_LEN;
	public static final int LanManBufferSize = 8192;

	// Maximum multiplexed packets allowed (client can send up to this many SMBs before waiting for
	// a response)
	//
	// Setting NTMaxMultiplexed to one will disable asynchronous notifications on the client

	public static final int LanManMaxMultiplexed = 1;
	public static final int NTMaxMultiplexed = 4;

	// Maximum number of virtual circuits

	private static final int MaxVirtualCircuits = 0;

	// Debug flag values

	public static final int DBG_NETBIOS 	= 0x00000001; // NetBIOS layer
	public static final int DBG_STATE 		= 0x00000002; // Session state changes
	public static final int DBG_RXDATA 		= 0x00000004; // Received data
	public static final int DBG_TXDATA 		= 0x00000008; // Transmit data
	public static final int DBG_DUMPDATA 	= 0x00000010; // Dump data packets
	public static final int DBG_NEGOTIATE 	= 0x00000020; // Protocol negotiate phase
	public static final int DBG_TREE 		= 0x00000040; // Tree connection/disconnection
	public static final int DBG_SEARCH 		= 0x00000080; // File/directory search
	public static final int DBG_INFO 		= 0x00000100; // Information requests
	public static final int DBG_FILE 		= 0x00000200; // File open/close/info
	public static final int DBG_FILEIO 		= 0x00000400; // File read/write
	public static final int DBG_TRAN 		= 0x00000800; // Transactions
	public static final int DBG_ECHO 		= 0x00001000; // Echo requests
	public static final int DBG_ERROR 		= 0x00002000; // Errors
	public static final int DBG_IPC 		= 0x00004000; // IPC$ requests
	public static final int DBG_LOCK 		= 0x00008000; // Lock/unlock requests
	public static final int DBG_PKTTYPE 	= 0x00010000; // Received packet type
	public static final int DBG_DCERPC 		= 0x00020000; // DCE/RPC
	public static final int DBG_STATECACHE 	= 0x00040000; // File state cache
	public static final int DBG_TIMING 		= 0x00080000; // Time packet processing
	public static final int DBG_NOTIFY 		= 0x00100000; // Asynchronous change notification
	public static final int DBG_STREAMS 	= 0x00200000; // NTFS streams
	public static final int DBG_SOCKET 		= 0x00400000; // NetBIOS/native SMB socket connections
	public static final int DBG_PKTPOOL     = 0x00800000; // Packet pool allocate/release
	public static final int DBG_PKTSTATS    = 0x01000000; // Packet pool statistics
	public static final int DBG_THREADPOOL  = 0x02000000; // Thread pool
	public static final int DBG_BENCHMARK	= 0x04000000; // Benchmarking
	public static final int DBG_OPLOCK		= 0x08000000; // Opportunistic locks

	// Server session object factory

	private static SrvSessionFactory m_factory = new DefaultSrvSessionFactory();

	// Packet handler used to send/receive SMB packets over a particular protocol

	private PacketHandler m_pktHandler;

	// Protocol handler for this session, depends upon the negotiated SMB dialect

	private ProtocolHandler m_handler;

	// SMB session state.

	private int m_state = SMBSrvSessionState.NBSESSREQ;

	// SMB dialect that this session has negotiated to use.

	private int m_dialect = Dialect.Unknown;

	// Callers NetBIOS name and target name

	private String m_callerNBName;
	private String m_targetNBName;

	// Notify change requests and notifications pending flag

	private NotifyRequestList m_notifyList;
	private boolean m_notifyPending;

	// Default SMB/CIFS flags and flags2, ORed with the SMB packet flags/flags2 before sending a
	// response
	// to the client.

	private int m_defFlags;
	private int m_defFlags2;

	// Asynchronous response packet queue
	//
	// Contains SMB response packets that could not be sent due to SMB requests being processed. The
	// asynchronous responses must be sent after any pending requests have been processed as the client may
	// disconnect the session.

	private Vector<SMBSrvPacket> m_asynchQueue;

	// Maximum client buffer size and multiplex count

	private int m_maxBufSize;
	private int m_maxMultiplex;

	// Client capabilities

	private int m_clientCaps;

	// Virtual circuit list

	private VirtualCircuitList m_vcircuits;

	// Setup objects used during two stage session setup before the virtual circuit is allocated

	private Hashtable<Integer, Object> m_setupObjects;

	// Flag to indicate an asynchronous read has been queued/is being processed
	
	private boolean m_asyncRead;
	
	/**
	 * Class constructor.
	 * 
	 * @param handler Packet handler used to send/receive SMBs
	 * @param srv Server that this session is associated with.
	 */
	protected SMBSrvSession(PacketHandler handler, SMBServer srv) {
		super(-1, srv, handler.isProtocolName(), null);

		// Set the packet handler

		m_pktHandler = handler;

		// If this is a TCPIP SMB or Win32 NetBIOS session then bypass the NetBIOS session setup
		// phase.

		if ( isProtocol() == SMBSrvPacket.PROTOCOL_TCPIP || isProtocol() == SMBSrvPacket.PROTOCOL_WIN32NETBIOS) {

			// Advance to the SMB negotiate dialect phase

			setState(SMBSrvSessionState.SMBNEGOTIATE);

			// Check if the client name is available

			if ( handler.hasClientName())
				m_callerNBName = handler.getClientName();
		}

		// Allocate the virtual circuit list

		m_vcircuits = new VirtualCircuitList();
	}

	/**
	 * Return the session protocol type
	 * 
	 * @return int
	 */
	public final int isProtocol() {
		return m_pktHandler.isProtocol();
	}

	/**
	 * Find the tree connection for the request
	 * 
	 * @param smbPkt SMBSrvPacket
	 * @return TreeConnection
	 */
	public final TreeConnection findTreeConnection(SMBSrvPacket smbPkt) {

		// Find the virtual circuit for the request

		TreeConnection tree = null;
		VirtualCircuit vc = findVirtualCircuit(smbPkt.getUserId());

		if ( vc != null) {

			// Find the tree connection

			tree = vc.findConnection(smbPkt.getTreeId());
		}

		// Return the tree connection, or null if invalid UID or TID

		return tree;
	}

	/**
	 * Add a new virtual circuit, return the allocated UID
	 * 
	 * @param vc VirtualCircuit
	 * @return int
	 */
	public final int addVirtualCircuit(VirtualCircuit vc) {

		// Add the new virtual circuit

		return m_vcircuits.addCircuit(vc);
	}

	/**
	 * Find a virtual circuit with the allocated UID
	 * 
	 * @param uid int
	 * @return VirtualCircuit
	 */
	public final VirtualCircuit findVirtualCircuit(int uid) {

		// Find the virtual circuit with the specified UID

		VirtualCircuit vc = m_vcircuits.findCircuit(uid);
		if ( vc != null) {

			// Set the session client information from the virtual circuit

			setClientInformation(vc.getClientInformation());
			
			// Setup any authentication context
			
			getSMBServer().getCifsAuthenticator().setCurrentUser( getClientInformation());
		}

		// Return the virtual circuit

		return vc;
	}

	/**
	 * Remove a virtual circuit
	 * 
	 * @param uid int
	 */
	public final void removeVirtualCircuit(int uid) {

		// Remove the virtual circuit with the specified UID

		m_vcircuits.removeCircuit(uid, this);
	}

	/**
	 * Return the active virtual circuit count
	 * 
	 * @return int
	 */
	public final int numberOfVirtualCircuits() {
		return (m_vcircuits != null ? m_vcircuits.getCircuitCount() : 0);
	}
	
	/**
	 * Cleanup any resources owned by this session, close virtual circuits and change notification
	 * requests.
	 */
	protected final void cleanupSession() {

	    // Debug
	    try
	    {

	        if ( Debug.EnableInfo && hasDebug(DBG_STATE))
	            debugPrintln("Cleanup session, vcircuits=" + m_vcircuits.getCircuitCount() + ", changeNotify="
	                    + getNotifyChangeCount());

	        // Close the virtual circuits

	        if ( m_vcircuits.getCircuitCount() > 0) {

	            // Enumerate the virtual circuits and close all circuits

	            Enumeration<Integer> uidEnum = m_vcircuits.enumerateUIDs();

	            while (uidEnum.hasMoreElements()) {

	                // Get the UID for the current circuit

	                Integer uid = uidEnum.nextElement();

	                // Close the virtual circuit

	                VirtualCircuit vc = m_vcircuits.findCircuit(uid);
	                if ( vc != null) {

	                    // DEBUG

	                    if ( Debug.EnableInfo && hasDebug(DBG_STATE))
	                        debugPrintln("  Cleanup vc=" + vc);

	                    vc.closeCircuit(this);
	                }
	            }

	            // Clear the virtual circuit list

	            m_vcircuits.clearCircuitList();
	        }

	        // Check if there are active change notification requests

	        if ( m_notifyList != null && m_notifyList.numberOfRequests() > 0) {

	            // Remove the notify requests from the associated device context notify list

	            for (int i = 0; i < m_notifyList.numberOfRequests(); i++) {

	                // Get the current change notification request and remove from the global notify
	                // list

	                NotifyRequest curReq = m_notifyList.getRequest(i);
	                if ( curReq.getDiskContext().hasChangeHandler())
	                    curReq.getDiskContext().getChangeHandler().removeNotifyRequests(this);
	            }
	        }

	        // Delete any temporary shares that were created for this session

	        getSMBServer().deleteTemporaryShares(this);

	     
	    } 
	    finally 
	    {
	        // Commit any outstanding transaction that may have been started during cleanup
	        if ( hasTransaction())
	            endTransaction();
	    }
	}

	/**
	 * Close the session socket
	 */
	protected final void closeSocket() {

		// Indicate that the session is being shutdown

		setShutdown(true);

		// Close the packet handler

		try {
			m_pktHandler.closeHandler();
		}
		catch (Exception ex) {
			Debug.println( ex);
		}
	}

	/**
	 * Close the session
	 */
	public final void closeSession() {

		// Cleanup the session (open files/virtual circuits/searches)
		
		cleanupSession();
		
		// Call the base class

		super.closeSession();

		try {

			// Set the session into a hangup state

			setState(SMBSrvSessionState.NBHANGUP);

			// Close the socket
			
			closeSocket();
		}
		catch (Exception ex) {
		}

	}

	/**
	 * Finalize, object is about to be garbage collected. Make sure resources are released.
	 */
	public void finalize() {

		// Check if there are any active resources

		cleanupSession();

		// Make sure the socket is closed and deallocated

		closeSocket();
	}

	/**
	 * Return the default flags SMB header value
	 * 
	 * @return int
	 */
	public final int getDefaultFlags() {
		return m_defFlags;
	}

	/**
	 * Return the default flags2 SMB header value
	 * 
	 * @return int
	 */
	public final int getDefaultFlags2() {
		return m_defFlags2;
	}

	/**
	 * Return the count of active change notification requests
	 * 
	 * @return int
	 */
	public final int getNotifyChangeCount() {
		if ( m_notifyList == null)
			return 0;
		return m_notifyList.numberOfRequests();
	}

	/**
	 * Return the client maximum buffer size
	 * 
	 * @return int
	 */
	public final int getClientMaximumBufferSize() {
		return m_maxBufSize;
	}

	/**
	 * Return the client maximum muliplexed requests
	 * 
	 * @return int
	 */
	public final int getClientMaximumMultiplex() {
		return m_maxMultiplex;
	}

	/**
	 * Return the client capability flags
	 * 
	 * @return int
	 */
	public final int getClientCapabilities() {
		return m_clientCaps;
	}

	/**
	 * Determine if the client has the specified capability enabled
	 * 
	 * @param cap int
	 * @return boolean
	 */
	public final boolean hasClientCapability(int cap) {
		if ( (m_clientCaps & cap) != 0)
			return true;
		return false;
	}

	/**
	 * Return the SMB dialect type that the server/client have negotiated.
	 * 
	 * @return int
	 */
	public final int getNegotiatedSMBDialect() {
		return m_dialect;
	}

	/**
	 * Return the packet handler used by the session
	 * 
	 * @return PacketHandler
	 */
	public final PacketHandler getPacketHandler() {
		return m_pktHandler;
	}

	/**
	 * Return the CIFS packet pool from the packet handler
	 * 
	 * @return CIFSPacketPool
	 */
	public final CIFSPacketPool getPacketPool() {
		return m_pktHandler.getPacketPool();
	}
	
	/**
	 * Return the thread pool
	 * 
	 * @return ThreadRequestPool
	 */
	public final ThreadRequestPool getThreadPool() {
		return getSMBServer().getThreadPool();
	}
	
	/**
	 * Return the remote NetBIOS name that was used to create the session.
	 * 
	 * @return String
	 */
	public final String getRemoteNetBIOSName() {
		return m_callerNBName;
	}

	/**
	 * Check if the session has a target NetBIOS name
	 * 
	 * @return boolean
	 */
	public final boolean hasTargetNetBIOSName() {
		return m_targetNBName != null ? true : false;
	}

	/**
	 * Return the target NetBIOS name that was used to create the session
	 * 
	 * @return String
	 */
	public final String getTargetNetBIOSName() {
		return m_targetNBName;
	}

	/**
	 * Cehck if the clients remote address is available
	 * 
	 * @return boolean
	 */
	public final boolean hasRemoteAddress() {
		return m_pktHandler.hasRemoteAddress();
	}

	/**
	 * Return the client network address
	 * 
	 * @return InetAddress
	 */
	public final InetAddress getRemoteAddress() {
		return m_pktHandler.getRemoteAddress();
	}

	/**
	 * Return the server that this session is associated with.
	 * 
	 * @return SMBServer
	 */
	public final SMBServer getSMBServer() {
		return (SMBServer) getServer();
	}

	/**
	 * Return the server name that this session is associated with.
	 * 
	 * @return String
	 */
	public final String getServerName() {
		return getSMBServer().getServerName();
	}

	/**
	 * Return the session state
	 * 
	 * @return int
	 */
	public final int getState() {
		return m_state;
	}
	
	/**
	 * Hangup the session.
	 * 
	 * @param reason java.lang.String Reason the session is being closed.
	 */
	public void hangupSession(String reason) {

		// Debug

		if ( Debug.EnableInfo && hasDebug(DBG_STATE)) {
			debugPrint("## Session closing. ");
			debugPrintln(reason);
		}

		// Set the session into a NetBIOS hangup state

		setState(SMBSrvSessionState.NBHANGUP);
	}

	/**
	 * Check if the Macintosh exteniosn SMBs are enabled
	 * 
	 * @return boolean
	 */
	public final boolean hasMacintoshExtensions() {
		return getSMBServer().getCIFSConfiguration().hasMacintoshExtensions();
	}

	/**
	 * Check if there is a change notification update pending
	 * 
	 * @return boolean
	 */
	public final boolean hasNotifyPending() {
		return m_notifyPending;
	}

	/**
	 * Determine if the session has a setup object for the specified PID
	 * 
	 * @param pid int
	 * @return boolean
	 */
	public final boolean hasSetupObject(int pid) {
		if ( m_setupObjects == null)
			return false;
		return m_setupObjects.get(new Integer(pid)) != null ? true : false;
	}

	/**
	 * Return the session setup object for the specified PID
	 * 
	 * @param pid int
	 * @return Object
	 */
	public final Object getSetupObject(int pid) {
		if ( m_setupObjects == null)
			return null;
		return m_setupObjects.get(new Integer(pid));
	}

	/**
	 * Store the setup object for the specified PID
	 * 
	 * @param pid int
	 * @param obj Object
	 */
	public final void setSetupObject(int pid, Object obj) {
		if ( m_setupObjects == null)
			m_setupObjects = new Hashtable<Integer, Object>();
		m_setupObjects.put(new Integer(pid), obj);
	}

	/**
	 * Remove the session setup object for the specified PID
	 * 
	 * @param pid int
	 * @return Object
	 */
	public final Object removeSetupObject(int pid) {
		if ( m_setupObjects == null)
			return null;
		return m_setupObjects.remove(new Integer(pid));
	}

	/**
	 * Set the change notify pending flag
	 * 
	 * @param pend boolean
	 */
	public final void setNotifyPending(boolean pend) {
		m_notifyPending = pend;
	}

	/**
	 * Set the client maximum buffer size
	 * 
	 * @param maxBuf int
	 */
	public final void setClientMaximumBufferSize(int maxBuf) {
		m_maxBufSize = maxBuf;
	}

	/**
	 * Set the client maximum multiplexed
	 * 
	 * @param maxMpx int
	 */
	public final void setClientMaximumMultiplex(int maxMpx) {
		m_maxMultiplex = maxMpx;
	}

	/**
	 * Set the client capability flags
	 * 
	 * @param flags int
	 */
	public final void setClientCapabilities(int flags) {
		m_clientCaps = flags;
	}

	/**
	 * Set the default flags value to be ORed with outgoing response packet flags
	 * 
	 * @param flags int
	 */
	public final void setDefaultFlags(int flags) {
		m_defFlags = flags;
	}

	/**
	 * Set the default flags2 value to be ORed with outgoing response packet flags2 field
	 * 
	 * @param flags int
	 */
	public final void setDefaultFlags2(int flags) {
		m_defFlags2 = flags;
	}

	/**
	 * Set the session state.
	 * 
	 * @param state int
	 */
	protected void setState(int state) {

		// Debug

		if ( Debug.EnableInfo && hasDebug(DBG_STATE))
			debugPrintln("State changed to " + SMBSrvSessionState.getStateAsString(state));

		// Change the session state

		m_state = state;
	}

	/**
	 * Process the NetBIOS session request message, either accept the session request and send back
	 * a NetBIOS accept or reject the session and send back a NetBIOS reject and hangup the session.
	 * 
	 * 2param smbPkt SMBSrvPacket
	 */
	protected void procNetBIOSSessionRequest( SMBSrvPacket smbPkt)
		throws IOException, NetBIOSException {

		// Check if the received packet contains enough data for a NetBIOS session request packet.

		if ( smbPkt.getReceivedLength() < RFCNetBIOSProtocol.SESSREQ_LEN || smbPkt.getHeaderType() != RFCNetBIOSProtocol.SESSION_REQUEST) {
			
			// Debug
			
			if ( Debug.EnableInfo && hasDebug(DBG_NETBIOS)) {
				Debug.println("NBREQ invalid packet len=" + smbPkt.getReceivedLength() + ", header=0x" + Integer.toHexString(smbPkt.getHeaderType()));
				HexDump.Dump( smbPkt.getBuffer(), smbPkt.getReceivedLength(), 0, Debug.getDebugInterface());
			}
		
			throw new NetBIOSException("NBREQ Invalid packet len=" + smbPkt.getReceivedLength());
		}

		// Do a few sanity checks on the received packet

		byte[] buf = smbPkt.getBuffer();
		
		if ( buf[4] != (byte) 32 || buf[38] != (byte) 32)
			throw new NetBIOSException("NBREQ Invalid NetBIOS name data");

		// Extract the from/to NetBIOS encoded names, and convert to normal strings.

		StringBuffer nbName = new StringBuffer(32);
		for (int i = 0; i < 32; i++)
			nbName.append((char) buf[5 + i]);
		
		String toName = NetBIOSSession.DecodeName(nbName.toString());
		toName = toName.trim();

		nbName.setLength(0);
		for (int i = 0; i < 32; i++)
			nbName.append((char) buf[39 + i]);
		
		String fromName = NetBIOSSession.DecodeName(nbName.toString());
		fromName = fromName.trim();

		// Debug

		if ( Debug.EnableInfo && hasDebug(DBG_NETBIOS))
			debugPrintln("NetBIOS CALL From " + fromName + " to " + toName);

		// Check that the request is for this server

		boolean forThisServer = false;

		if ( toName.compareTo(getServerName()) == 0 || toName.compareTo(NetBIOSName.SMBServer) == 0
				|| toName.compareTo(NetBIOSName.SMBServer2) == 0 || toName.compareTo("*") == 0) {

			// Request is for this server

			forThisServer = true;
		}
		else if ( getSMBServer().getCIFSConfiguration().hasAliasNames() == true) {

			// Check for a connection to one of the alias server names

			StringList aliasNames = getSMBServer().getCIFSConfiguration().getAliasNames();
			if ( aliasNames.containsString(toName))
				forThisServer = true;
		}
		else {

			// Check if the caller is using an IP address

			InetAddress[] srvAddr = getSMBServer().getServerAddresses();
			if ( srvAddr != null) {

				// Check for an address match

				int idx = 0;

				while (idx < srvAddr.length && forThisServer == false) {

					// Check the current IP address

					if ( srvAddr[idx++].getHostAddress().compareTo(toName) == 0)
						forThisServer = true;
				}
			}
		}

		// If we did not find an address match then reject the session request

		if ( forThisServer == false)
			throw new NetBIOSException("NBREQ Called name is not this server (" + toName + ")");

		// Debug

		if ( Debug.EnableInfo && hasDebug(DBG_NETBIOS))
			debugPrintln("NetBIOS session request from " + fromName);

		// Save the callers name and target name

		m_callerNBName = fromName;
		m_targetNBName = toName;

		// Move the session to the SMB negotiate state

		setState(SMBSrvSessionState.SMBNEGOTIATE);
		
		// Set the remote client name

		setRemoteName(fromName);

		// Build a NetBIOS session accept message

		smbPkt.setHeaderType(RFCNetBIOSProtocol.SESSION_ACK);
		smbPkt.setHeaderFlags(0);
		smbPkt.setHeaderLength(0);

		// Output the NetBIOS session accept packet

		m_pktHandler.writePacket( smbPkt, 4, true);
	}

	/**
	 * Process an SMB dialect negotiate request.
	 * 
	 * @param smbPkt SMBSrvPacket
	 */
	protected void procSMBNegotiate( SMBSrvPacket smbPkt)
		throws SMBSrvException, IOException {

		// Initialize the NetBIOS header

		byte[] buf = smbPkt.getBuffer();
		buf[0] = (byte) RFCNetBIOSProtocol.SESSION_MESSAGE;

		// Check if the received packet looks like a valid SMB

		if ( smbPkt.getCommand() != PacketType.Negotiate || smbPkt.checkPacketIsValid(0, 2) == false) {
			sendErrorResponseSMB( smbPkt, SMBStatus.SRVUnrecognizedCommand, SMBStatus.ErrSrv);
			return;
		}

		// Decode the data block into a list of requested SMB dialects

		int dataPos = smbPkt.getByteOffset();
		int dataLen = smbPkt.getByteCount();

		String diaStr = null;
		StringList dialects = new StringList();

		while (dataLen > 0) {

			// Decode an SMB dialect string from the data block, always ASCII strings

			diaStr = DataPacker.getDataString(DataType.Dialect, buf, dataPos, dataLen, false);
			if ( diaStr != null) {
				
				// Add the dialect string to the list of requested dialects

				dialects.addString(diaStr);
			}
			else {
				// Invalid dialect block in the negotiate packet, send an error response and hangup
				// the session.

				sendErrorResponseSMB( smbPkt, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
				setState(SMBSrvSessionState.NBHANGUP);
				return;
			}

			// Update the remaining data position and count

			dataPos += diaStr.length() + 2; // data type and null
			dataLen -= diaStr.length() + 2;
		}

		// Find the highest level SMB dialect that the server and client both support

		DialectSelector dia = getSMBServer().getCIFSConfiguration().getEnabledDialects();
		int diaIdx = -1;

		for (int i = 0; i < Dialect.Max; i++) {

			// Check if the current dialect is supported by the server

			if ( dia.hasDialect(i)) {

				// Check if the client supports the current dialect. If the current dialect is a
				// higher level dialect than the currently nominated dialect, update the nominated
				// dialect index.

				for (int j = 0; j < Dialect.SMB_PROT_MAXSTRING; j++) {

					// Check if the dialect string maps to the current dialect index

					if ( Dialect.DialectType(j) == i && dialects.containsString(Dialect.DialectString(j))) {

						// Update the selected dialect type, if the current dialect is a newer
						// dialect

						if ( i > diaIdx)
							diaIdx = i;
					}
				}
			}
		}

		// Debug

		if ( Debug.EnableInfo && hasDebug(DBG_NEGOTIATE)) {
			if ( diaIdx == -1)
				debugPrintln("Failed to negotiate SMB dialect");
			else
				debugPrintln("Negotiated SMB dialect - " + Dialect.DialectTypeString(diaIdx));
		}

		// Check if we successfully negotiated an SMB dialect with the client

		if ( diaIdx != -1) {

			// Store the negotiated SMB diialect type

			m_dialect = diaIdx;

			// Convert the dialect type to an index within the clients SMB dialect list

			diaIdx = dialects.findString(Dialect.DialectTypeString(diaIdx));

			// Allocate a protocol handler for the negotiated dialect, if we cannot get a protocol
			// handler then bounce the request.

			m_handler = ProtocolFactory.getHandler(m_dialect);
			if ( m_handler != null) {

				// Debug

				if ( Debug.EnableInfo && hasDebug(DBG_NEGOTIATE))
					debugPrintln("Assigned protocol handler - " + m_handler.getClass().getName());

				// Set the protocol handlers associated session

				m_handler.setSession(this);
			}
			else {

				// Could not get a protocol handler for the selected SMB dialect, indicate to the
				// client that no suitable dialect available.

				diaIdx = -1;
			}
		}

		// Check if the extended security flag has been set by the client

		boolean extendedSecurity = (smbPkt.getFlags2() & SMBSrvPacket.FLG2_EXTENDEDSECURITY) != 0 ? true : false;

		// Build the negotiate response SMB for Core dialect

		if ( m_dialect == -1 || m_dialect <= Dialect.CorePlus) {

			// Core dialect negotiate response, or no valid dialect response

			smbPkt.setParameterCount(1);
			smbPkt.setParameter(0, diaIdx);
			smbPkt.setByteCount(0);

			smbPkt.setTreeId(0);
			smbPkt.setUserId(0);
		}
		else if ( m_dialect <= Dialect.LanMan2_1) {

			// We are using case sensitive pathnames and long file names

			smbPkt.setFlags(SMBSrvPacket.FLG_CASELESS);
			smbPkt.setFlags2(SMBSrvPacket.FLG2_LONGFILENAMES);

			// Access the authenticator for this server and determine if the server is in share or
			// user level security mode.

			ICifsAuthenticator auth = getSMBServer().getCifsAuthenticator();

			// LanMan dialect negotiate response

			smbPkt.setParameterCount(13);
			smbPkt.setParameter(0, diaIdx);
			smbPkt.setParameter(1, auth.getSecurityMode());
			smbPkt.setParameter(2, LanManBufferSize);
			smbPkt.setParameter(3, LanManMaxMultiplexed); // maximum multiplexed requests
			smbPkt.setParameter(4, MaxVirtualCircuits); // maximum number of virtual circuits
			smbPkt.setParameter(5, 0); // read/write raw mode support

			// Create a session token, using the system clock

			smbPkt.setParameterLong(6, (int) (System.currentTimeMillis() & 0xFFFFFFFF));

			// Return the current server date/time

			SMBDate srvDate = new SMBDate(System.currentTimeMillis());
			smbPkt.setParameter(8, srvDate.asSMBTime());
			smbPkt.setParameter(9, srvDate.asSMBDate());

			// Server timezone offset from UTC

			smbPkt.setParameter(10, getServer().getGlobalConfiguration().getTimeZoneOffset());

			// Encryption key length

			smbPkt.setParameter(11, auth.getEncryptionKeyLength());
			smbPkt.setParameter(12, 0);

			smbPkt.setTreeId(0);
			smbPkt.setUserId(0);

			// Let the authenticator pack any remaining fields in the negotiate response

			try {

				// Pack the remaining negotiate response fields

				auth.generateNegotiateResponse(this, smbPkt, false);
			}
			catch (AuthenticatorException ex) {

				// Log the error

				if ( Debug.EnableError && hasDebug(DBG_NEGOTIATE))
					debugPrintln("Negotiate error - " + ex.getMessage());

				// Close the session

				setState(SMBSrvSessionState.NBHANGUP);
				return;
			}
		}
		else if ( m_dialect == Dialect.NT) {

			// We are using case sensitive pathnames and long file names

			setDefaultFlags(SMBSrvPacket.FLG_CASELESS);
			setDefaultFlags2(SMBSrvPacket.FLG2_LONGFILENAMES + SMBSrvPacket.FLG2_UNICODE);

			// Access the authenticator for this server and determine if the server is in share or
			// user level security mode.

			ICifsAuthenticator auth = getSMBServer().getCIFSConfiguration().getAuthenticator();

			// Check if the authenticator supports extended security, override the client setting
			
			if ( auth.hasExtendedSecurity() == false)
				extendedSecurity = false;
			
			// NT dialect negotiate response

			NTParameterPacker nt = new NTParameterPacker( smbPkt.getBuffer());

			smbPkt.setParameterCount(17);
			nt.packWord(diaIdx); 				// selected dialect index
			nt.packByte(auth.getSecurityMode());
			nt.packWord(NTMaxMultiplexed); 		// maximum multiplexed requests
												// setting to 1 will disable change notify requests from the client
			nt.packWord(MaxVirtualCircuits); 	// maximum number of virtual circuits

			int maxBufSize = getSMBServer().getPacketPool().getLargestSize() - RFCNetBIOSProtocol.HEADER_LEN;
			nt.packInt(maxBufSize);

			nt.packInt(0); // maximum raw size

			// Create a session token, using the system clock

			if ( auth.hasExtendedSecurity() == false || extendedSecurity == false)
				nt.packInt((int) (System.currentTimeMillis() & 0xFFFFFFFFL));
			else
				nt.packInt(0);

			// Set server capabilities, switch off extended security if the client does not support
			// it

			int srvCapabs = auth.getServerCapabilities();
			if ( auth.hasExtendedSecurity() == false || extendedSecurity == false)
				srvCapabs &= ~Capability.ExtendedSecurity;

			nt.packInt(srvCapabs);

			// Return the current server date/time, and timezone offset

			long srvTime = NTTime.toNTTime(new java.util.Date(System.currentTimeMillis()));

			nt.packLong(srvTime);
			nt.packWord(getServer().getGlobalConfiguration().getTimeZoneOffset());

			// Encryption key length

			if ( auth.hasExtendedSecurity() == false || extendedSecurity == false)
				nt.packByte(auth.getEncryptionKeyLength());
			else
				nt.packByte(0);

			smbPkt.setFlags(getDefaultFlags());
			smbPkt.setFlags2(getDefaultFlags2());

			smbPkt.setTreeId(0);
			smbPkt.setUserId(0);

			// Let the authenticator pack any remaining fields in the negotiate response

			try {

				// Pack the remaining negotiate response fields

				auth.generateNegotiateResponse(this, smbPkt, extendedSecurity);
			}
			catch (AuthenticatorException ex) {

				// Log the error

				if ( Debug.EnableError && hasDebug(DBG_NEGOTIATE))
					debugPrintln("Negotiate error - " + ex.getMessage());

				// Close the session

				setState(SMBSrvSessionState.NBHANGUP);
				return;
			}
		}

		// Make sure the response flag is set

		if ( smbPkt.isResponse() == false)
			smbPkt.setFlags( smbPkt.getFlags() + SMBPacket.FLG_RESPONSE);

		// Send the negotiate response

		m_pktHandler.writePacket( smbPkt, smbPkt.getLength());

		// Check if the negotiated SMB dialect supports the session setup command, if not then
		// bypass the session setup phase.

		if ( m_dialect == -1)
			setState(SMBSrvSessionState.NBHANGUP);
		else if ( Dialect.DialectSupportsCommand(m_dialect, PacketType.SessionSetupAndX))
			setState(SMBSrvSessionState.SMBSESSSETUP);
		else
			setState(SMBSrvSessionState.SMBSESSION);

		// If a dialect was selected inform the server that the session has been opened

		if ( m_dialect != -1)
			getSMBServer().sessionOpened(this);
	}

	/**
	 * Start the SMB server session in a seperate thread.
	 */
	public void run() {

		// Server packet allocated from the pool
		
		SMBSrvPacket smbPkt = null;
		
		try {

			// Debug

			if ( Debug.EnableInfo && hasDebug(SMBSrvSession.DBG_NEGOTIATE))
				debugPrintln("Server session started");

			// The server session loops until the NetBIOS hangup state is set.

			while (m_state != SMBSrvSessionState.NBHANGUP) {

				try {
					
					// Wait for a request packet
	
					smbPkt = m_pktHandler.readPacket();
				}
				catch (SocketTimeoutException ex) {
					
					// Debug

					if ( Debug.EnableInfo && hasDebug(SMBSrvSession.DBG_SOCKET))
						debugPrintln("Socket read timed out, closing session");
					
					// Socket read timed out
					
					hangupSession("Socket read timeout");
					
					// Clear the request packet
					
					smbPkt = null;
				}
				catch (IOException ex) {
					
					// Check if there is no more data, the other side has dropped the connection

					hangupSession("Remote disconnect");
					
					// Clear the request packet
					
					smbPkt = null;
				}

				// Check for an empty packet

				if ( smbPkt == null)
					continue;

				// Check the packet signature if we are in an SMB state

				if ( m_state > SMBSrvSessionState.NBSESSREQ) {

					// Check for an SMB2 packet signature

					if ( smbPkt.isSMB2()) {

						// Debug

						if ( Debug.EnableInfo && hasDebug(DBG_PKTTYPE))
							debugPrintln("SMB2 request received, ignoring");

						continue;
					}

					// Check the packet signature

					if ( smbPkt.checkPacketSignature() == false) {

						// Debug

						if ( Debug.EnableInfo && hasDebug(DBG_PKTTYPE))
							debugPrintln("Invalid SMB packet signature received, packet ignored");

						continue;
					}
				}

				// Queue the request to the thread pool for processing
				
				getThreadPool().queueRequest( new CIFSThreadRequest( this, smbPkt));
				smbPkt = null;
			}
			
			// Cleanup the session, then close the session/socket
			
			closeSession();
		}
		catch (Exception ex) {

			// Output the exception details

			if ( isShutdown() == false) {
				debugPrintln("Closing session due to exception");
				debugPrintln(ex);
				Debug.println( ex);
			}
		}
		catch (Throwable ex) {
			debugPrintln("Closing session due to throwable");
			debugPrintln(ex.toString());
			Debug.println( ex);
		}
		finally {
			
			// Release any allocated request packet back to the pool
			
			if ( smbPkt != null)
				getSMBServer().getPacketPool().releasePacket( smbPkt);
		}
	}

	/**
	 * Handle a session message, receive all data and run the SMB protocol handler.
	 * 
	 * @param smbPkt SMBSrvPacket
	 */
	protected final void runHandler( SMBSrvPacket smbPkt)
		throws IOException, SMBSrvException, TooManyConnectionsException {

		// DEBUG

		if ( Debug.EnableInfo && hasDebug(DBG_PKTTYPE))
			debugPrintln("Rx packet type - " + smbPkt.getPacketTypeString() + ", SID=" + smbPkt.getSID());

		// Call the protocol handler

		if ( m_handler.runProtocol( smbPkt) == false) {

			// The sessions protocol handler did not process the request, return an unsupported
			// SMB error status.

			sendErrorResponseSMB( smbPkt, SMBStatus.SRVNotSupported, SMBStatus.ErrSrv);
		}

		// Commit/rollback any active transaction
		
		if ( hasTransaction())
			endTransaction();
		
		// Check if there are any pending asynchronous response packets

		while (hasAsynchResponse()) {

			// Remove the current asynchronous response SMB packet and send to the client

			SMBSrvPacket asynchPkt = removeFirstAsynchResponse();
			sendResponseSMB(asynchPkt, asynchPkt.getLength());

			// DEBUG

			if ( Debug.EnableInfo && hasDebug(DBG_NOTIFY)) {
				debugPrintln("Sent queued asynch response type=" + asynchPkt.getPacketTypeString() + ", mid="
						+ asynchPkt.getMultiplexId() + ", pid=" + asynchPkt.getProcessId());
				debugPrintln("  Async queue len=" + m_asynchQueue.size());
			}
		}
	}

	/**
	 * Process a CIFS request packet
	 * 
	 * @param smbPkt SMBSrvPacket
	 */
	public final void processPacket( SMBSrvPacket smbPkt) {
		
		// Process the packet, if valid
		
		if ( smbPkt != null) {
			
			try {
	
				// Start/end times if timing debug is enabled
	
				long startTime = 0L;
				long endTime = 0L;
	
				// Debug
	
				if ( Debug.EnableInfo && hasDebug(DBG_TIMING))
					startTime = System.currentTimeMillis();
	
				// Debug
	
				if ( Debug.EnableInfo && hasDebug(DBG_RXDATA)) {
					debugPrintln("Rx Data len=" + smbPkt.getReceivedLength());
					HexDump.Dump( smbPkt.getBuffer(), smbPkt.getReceivedLength(), 0, Debug.getDebugInterface());
				}
	
				// Process the received packet
				
				if ( smbPkt.getReceivedLength() > 0) {
					
					switch (m_state) {
		
						// NetBIOS session request pending
		
						case SMBSrvSessionState.NBSESSREQ:
							procNetBIOSSessionRequest( smbPkt);
							break;
		
						// SMB dialect negotiate
		
						case SMBSrvSessionState.SMBNEGOTIATE:
							procSMBNegotiate( smbPkt);
							break;
		
						// SMB session setup
		
						case SMBSrvSessionState.SMBSESSSETUP:
							m_handler.runProtocol( smbPkt);
							break;
		
						// SMB session main request processing
		
						case SMBSrvSessionState.SMBSESSION:
		
							// Run the main protocol handler
		
							runHandler( smbPkt);
		
							// Debug
		
							if ( Debug.EnableInfo && hasDebug(DBG_TIMING)) {
								endTime = System.currentTimeMillis();
								long duration = endTime - startTime;
								if ( duration > 20)
									debugPrintln("Processed packet " + PacketType.getCommandName( smbPkt.getCommand()) + " (0x"
											+ Integer.toHexString( smbPkt.getCommand()) + ") in " + duration + "ms, MID=" + smbPkt.getMultiplexId());
							}
							break;
					}
				}
				
				// Release the current packet back to the pool
				
				getPacketPool().releasePacket( smbPkt);
				smbPkt = null;
				
				// DEBUG
				
				if ( Debug.EnableInfo && hasDebug(DBG_PKTSTATS))
					Debug.println("[SMB] Packet pool stats: " + getPacketPool());
	
			}
			catch ( DeferredPacketException ex) {
				
				// Packet processing has been deferred, waiting on completion of some other processing
				// Make sure the request packet is not released yet
				
				smbPkt = null;
			}
			catch (SocketException ex) {
	
				// DEBUG
	
				if ( Debug.EnableInfo && hasDebug(DBG_STATE))
					debugPrintln("Socket closed by remote client");
			}
			catch (Exception ex) {
	
				// Output the exception details
	
				if ( isShutdown() == false) {
					debugPrintln("Closing session due to exception");
					debugPrintln(ex);
					Debug.println( ex);
				}
			}
			catch (Throwable ex) {
				debugPrintln("Closing session due to throwable");
				debugPrintln(ex.toString());
				Debug.println( ex);
			}
			finally {
				
				// Release any allocated request packet back to the pool
				
				if ( smbPkt != null)
					getSMBServer().getPacketPool().releasePacket( smbPkt);
			}
		}
		
		// Check if there is an active transaction
		
		if ( hasTransaction()) {
		
			// DEBUG
			
			if ( Debug.EnableError)
				debugPrintln("** Active transaction after packet processing, cleaning up **");
			
			// Close the active transaction
			
			endTransaction();
		}
		
		// Check if the session has been closed, either cleanly or due to an exception
		
		if ( m_state == SMBSrvSessionState.NBHANGUP) {
			
			// Cleanup the session, make sure all resources are released
	
			cleanupSession();
	
			// Debug
	
			if ( Debug.EnableInfo && hasDebug(DBG_STATE))
				debugPrintln("Server session closed");
	
			// Close the session
	
			closeSocket();
	
			// Notify the server that the session has closed
	
			getSMBServer().sessionClosed(this);
		}
		
		// Clear any user context
		
		if ( hasClientInformation())
			getSMBServer().getCifsAuthenticator().setCurrentUser( null);
	}
	
	/**
	 * Send an SMB response
	 * 
	 * @param pkt SMBSrvPacket
	 * @exception IOException
	 */
	public final void sendResponseSMB(SMBSrvPacket pkt)
		throws IOException {
		sendResponseSMB(pkt, pkt.getLength());
	}

	/**
	 * Send an SMB response
	 * 
	 * @param pkt SMBSrvPacket
	 * @param len int
	 * @exception IOException
	 */
	public synchronized final void sendResponseSMB(SMBSrvPacket pkt, int len)
		throws IOException {

		// Commit/rollback any active transactions before sending the response
		
		if ( hasTransaction()) {
			
			// DEBUG
			
			long startTime = 0L;
			
			if ( Debug.EnableInfo && hasDebug( DBG_BENCHMARK))
				startTime = System.currentTimeMillis();
		
			// Commit or rollback the transaction
			
			endTransaction();
			
			// DEBUG
			
			if ( Debug.EnableInfo && hasDebug( DBG_BENCHMARK)) {
				long elapsedTime = System.currentTimeMillis() - startTime;
				if ( elapsedTime > 5L)
					Debug.println("Benchmark: End transaction took " + elapsedTime + "ms");
			}
		}			
		
		// Make sure the response flag is set

		if ( pkt.isResponse() == false)
			pkt.setFlags(pkt.getFlags() + SMBSrvPacket.FLG_RESPONSE);

		// Add default flags/flags2 values

		pkt.setFlags(pkt.getFlags() | getDefaultFlags());

		// Mask out certain flags that the client may have sent

		int flags2 = pkt.getFlags2() | getDefaultFlags2();
		flags2 &= ~(SMBSrvPacket.FLG2_EXTENDEDATTRIB + SMBSrvPacket.FLG2_DFSRESOLVE + SMBSrvPacket.FLG2_SECURITYSIGS);

		pkt.setFlags2(flags2);

		// Send the response packet

		m_pktHandler.writePacket(pkt, len);
		m_pktHandler.flushPacket();

		// Debug

		if ( Debug.EnableInfo && hasDebug(DBG_TXDATA)) {
			debugPrintln("Tx Data len=" + len);
			HexDump.Dump(pkt.getBuffer(), 64, 0, Debug.getDebugInterface());
		}
	}

	/**
	 * Send a success response SMB
	 *
	 * @param smbPkt SMBSrvPacket
	 * @exception IOException If a network error occurs
	 */
	public final void sendSuccessResponseSMB( SMBSrvPacket smbPkt)
		throws IOException {

		// Make sure the response flag is set

		if ( smbPkt.isResponse() == false)
			smbPkt.setFlags( smbPkt.getFlags() + SMBSrvPacket.FLG_RESPONSE);

		// Add default flags/flags2 values

		smbPkt.setFlags( smbPkt.getFlags() | getDefaultFlags());
		smbPkt.setFlags2( smbPkt.getFlags2() | getDefaultFlags2());

		// Clear the parameter and byte counts

		smbPkt.setParameterCount(0);
		smbPkt.setByteCount(0);

		if ( smbPkt.isLongErrorCode())
			smbPkt.setLongErrorCode(SMBStatus.NTSuccess);
		else {
			smbPkt.setErrorClass(SMBStatus.Success);
			smbPkt.setErrorCode(SMBStatus.Success);
		}

		// Return the success response to the client

		sendResponseSMB( smbPkt, smbPkt.getLength());

		// Debug

		if ( Debug.EnableInfo && hasDebug(DBG_TXDATA))
			debugPrintln("Tx Data len=" + smbPkt.getLength() + ", success SMB");
	}

	/**
	 * Send an error response SMB. The returned code depends on the client long error code flag
	 * setting.
	 * 
	 * @param smbPkt SMBSrvPacket 
	 * @param ntCode 32bit error code
	 * @param stdCode Standard error code
	 * @param stdClass Standard error class
	 */
	public final void sendErrorResponseSMB( SMBSrvPacket smbPkt, int ntCode, int stdCode, int stdClass)
		throws java.io.IOException {

		// Check if long error codes are required by the client

		if ( smbPkt.isLongErrorCode()) {

			// Return the long/NT status code

			if ( ntCode != -1) {

				// Use the 32bit NT error code

				sendErrorResponseSMB( smbPkt, ntCode, SMBStatus.NTErr);
			}
			else {

				// Use the DOS error code

				sendErrorResponseSMB( smbPkt, stdCode, stdClass);
			}
		}
		else {

			// Return the standard/DOS error code

			sendErrorResponseSMB( smbPkt, stdCode, stdClass);
		}
	}

	/**
	 * Send an error response SMB.
	 * 
	 * @param smbPkt SMBSrvPacket
	 * @param errCode int Error code.
	 * @param errClass int Error class.
	 */
	public final void sendErrorResponseSMB( SMBSrvPacket smbPkt, int errCode, int errClass)
		throws java.io.IOException {

		// Make sure the response flag is set

		if ( smbPkt.isResponse() == false)
			smbPkt.setFlags( smbPkt.getFlags() + SMBSrvPacket.FLG_RESPONSE);

		// Set the error code and error class in the response packet

		smbPkt.setParameterCount(0);
		smbPkt.setByteCount(0);

		// Add default flags/flags2 values

		smbPkt.setFlags( smbPkt.getFlags() | getDefaultFlags());
		smbPkt.setFlags2( smbPkt.getFlags2() | getDefaultFlags2());

		// Check if the error is a NT 32bit error status

		if ( errClass == SMBStatus.NTErr) {

			// Enable the long error status flag

			if ( smbPkt.isLongErrorCode() == false)
				smbPkt.setFlags2( smbPkt.getFlags2() + SMBSrvPacket.FLG2_LONGERRORCODE);

			// Set the NT status code

			smbPkt.setLongErrorCode(errCode);
		}
		else {

			// Disable the long error status flag

			if ( smbPkt.isLongErrorCode() == true)
				smbPkt.setFlags2(smbPkt.getFlags2() - SMBSrvPacket.FLG2_LONGERRORCODE);

			// Set the error status/class

			smbPkt.setErrorCode(errCode);
			smbPkt.setErrorClass(errClass);
		}

		// Return the error response to the client

		sendResponseSMB( smbPkt, smbPkt.getLength());

		// Debug

		if ( Debug.EnableInfo && hasDebug(DBG_ERROR))
			debugPrintln("Error : Cmd = " + smbPkt.getPacketTypeString() + " - " + SMBErrorText.ErrorString(errClass, errCode));
	}

	/**
	 * Send an asynchonous error response SMB.
	 * 
	 * @param smbPkt SMBSrvPacket
	 * @param errCode int Error code.
	 * @param errClass int Error class.
	 * @return boolean
	 */
	public final boolean sendAsyncErrorResponseSMB( SMBSrvPacket smbPkt, int errCode, int errClass)
		throws java.io.IOException {

		// Make sure the response flag is set

		if ( smbPkt.isResponse() == false)
			smbPkt.setFlags( smbPkt.getFlags() + SMBSrvPacket.FLG_RESPONSE);

		// Set the error code and error class in the response packet

		smbPkt.setParameterCount(0);
		smbPkt.setByteCount(0);

		// Add default flags/flags2 values

		smbPkt.setFlags( smbPkt.getFlags() | getDefaultFlags());
		smbPkt.setFlags2( smbPkt.getFlags2() | getDefaultFlags2());

		// Check if the error is a NT 32bit error status

		if ( errClass == SMBStatus.NTErr) {

			// Enable the long error status flag

			if ( smbPkt.isLongErrorCode() == false)
				smbPkt.setFlags2( smbPkt.getFlags2() + SMBSrvPacket.FLG2_LONGERRORCODE);

			// Set the NT status code

			smbPkt.setLongErrorCode(errCode);
		}
		else {

			// Disable the long error status flag

			if ( smbPkt.isLongErrorCode() == true)
				smbPkt.setFlags2(smbPkt.getFlags2() - SMBSrvPacket.FLG2_LONGERRORCODE);

			// Set the error status/class

			smbPkt.setErrorCode(errCode);
			smbPkt.setErrorClass(errClass);
		}

		// Return the error response to the client

		boolean sentOK = sendAsynchResponseSMB( smbPkt, smbPkt.getLength());

		// Debug

		if ( Debug.EnableInfo && hasDebug(DBG_ERROR))
			debugPrintln("Async Error : Cmd = " + smbPkt.getPacketTypeString() + " - " + SMBErrorText.ErrorString(errClass, errCode) + ", sent=" + sentOK);
		
		// Return the send status
		
		return sentOK;
	}

	/**
	 * Send, or queue, an asynchronous response SMB
	 * 
	 * @param pkt SMBSrvPacket
	 * @param len int
	 * @return true if the packet was sent, or false if it was queued
	 * @exception IOException If an I/O error occurs
	 */
	public final boolean sendAsynchResponseSMB(SMBSrvPacket pkt, int len)
		throws IOException {

		// Check if there is pending data from the client

		boolean sts = false;

		if ( m_pktHandler.availableBytes() == 0) {

			// Send the asynchronous response immediately

			sendResponseSMB(pkt, len);
			m_pktHandler.flushPacket();

			// Indicate that the SMB response has been sent

			sts = true;
		}
		else {

			// Queue the packet to send out when current SMB requests have been processed

			queueAsynchResponseSMB(pkt);
		}

		// Return the sent/queued status

		return sts;
	}

	/**
	 * Queue an asynchronous response SMB for sending when current SMB requests have been processed.
	 * 
	 * @param pkt SMBSrvPacket
	 */
	protected final synchronized void queueAsynchResponseSMB(SMBSrvPacket pkt) {

		// Check if the asynchronous response queue has been allocated

		if ( m_asynchQueue == null) {

			// Allocate the asynchronous response queue

			m_asynchQueue = new Vector<SMBSrvPacket>();
		}

		// Add the SMB response packet to the queue

		m_asynchQueue.add(pkt);
	}

	/**
	 * Check if there are any asynchronous requests queued
	 * 
	 * @return boolean
	 */
	protected final synchronized boolean hasAsynchResponse() {

		// Check if the queue is valid

		if ( m_asynchQueue != null && m_asynchQueue.size() > 0)
			return true;
		return false;
	}

	/**
	 * Remove an asynchronous response packet from the head of the list
	 * 
	 * @return SMBSrvPacket
	 */
	protected final synchronized SMBSrvPacket removeFirstAsynchResponse() {

		// Check if there are asynchronous response packets queued

		if ( m_asynchQueue == null || m_asynchQueue.size() == 0)
			return null;

		// Return the SMB packet from the head of the queue

		SMBSrvPacket pkt = (SMBSrvPacket) m_asynchQueue.elementAt(0);
		m_asynchQueue.removeElementAt(0);
		return pkt;
	}

	/**
	 * Find the notify request with the specified ids
	 * 
	 * @param mid int
	 * @param tid int
	 * @param uid int
	 * @param pid int
	 * @return NotifyRequest
	 */
	public final NotifyRequest findNotifyRequest(int mid, int tid, int uid, int pid) {

		// Check if the local notify list is valid

		if ( m_notifyList == null)
			return null;

		// Find the matching notify request

		return m_notifyList.findRequest(mid, tid, uid, pid);
	}

	/**
	 * Find an existing notify request for the specified directory and filter
	 * 
	 * @param dir NetworkFile
	 * @param filter int
	 * @param watchTree boolean
	 * @return NotifyRequest
	 */
	public final NotifyRequest findNotifyRequest(NetworkFile dir, int filter, boolean watchTree) {

		// Check if the local notify list is valid

		if ( m_notifyList == null)
			return null;

		// Find the matching notify request

		return m_notifyList.findRequest(dir, filter, watchTree);
	}

	/**
	 * Add a change notification request
	 * 
	 * @param req NotifyRequest
	 * @param ctx DiskDeviceContext
	 */
	public final void addNotifyRequest(NotifyRequest req, DiskDeviceContext ctx) {

		// Check if the local notify list has been allocated

		if ( m_notifyList == null)
			m_notifyList = new NotifyRequestList();

		// Add the request to the local list and the shares global list

		m_notifyList.addRequest(req);
		ctx.addNotifyRequest(req);
	}

	/**
	 * Remove a change notification request
	 * 
	 * @param req NotifyRequest
	 */
	public final void removeNotifyRequest(NotifyRequest req) {

		// Check if the local notify list has been allocated

		if ( m_notifyList == null)
			return;

		// Remove the request from the local list and the shares global list

		m_notifyList.removeRequest(req);
		if ( req.getDiskContext() != null)
			req.getDiskContext().removeNotifyRequest(req);
	}

	/**
	 * Return the server session object factory
	 * 
	 * @return SrvSessionFactory
	 */
	public static final SrvSessionFactory getFactory() {
		return m_factory;
	}

	/**
	 * Set the server session object factory
	 * 
	 * @param factory SrvSessionFactory
	 */
	public static final void setFactory(SrvSessionFactory factory) {
		m_factory = factory;
	}

	/**
	 * Create a new server session instance
	 * 
	 * @param handler PacketHandler
	 * @param server SMBServer
	 * @param sessId int
	 * @return SMBSrvSession
	 */
	public static final SMBSrvSession createSession(PacketHandler handler, SMBServer server, int sessId) {
		return m_factory.createSession(handler, server, sessId);
	}
	
	/**
	 * Check if an asynchronous read is queued/being processed by this session
	 * 
	 * @return boolean
	 */
	public final boolean hasReadInProgress() {
		return m_asyncRead;
	}
	
	/**
	 * Set/clear the read in progress flag
	 * 
	 * @param inProgress boolean
	 */
	public final void setReadInProgress(boolean inProgress) {
		m_asyncRead = inProgress;
	}
	
	/**
	 * Indicate that CIFS filesystem searches are not case sensitive
	 * 
	 * @return boolean
	 */
	public boolean useCaseSensitiveSearch() {
		return false;
	}
}
