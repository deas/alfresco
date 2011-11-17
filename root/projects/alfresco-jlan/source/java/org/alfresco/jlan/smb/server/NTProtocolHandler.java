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
import java.sql.Time;

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.locking.FileLock;
import org.alfresco.jlan.locking.LockConflictException;
import org.alfresco.jlan.locking.NotLockedException;
import org.alfresco.jlan.netbios.RFCNetBIOSProtocol;
import org.alfresco.jlan.server.auth.CifsAuthenticator;
import org.alfresco.jlan.server.auth.ICifsAuthenticator;
import org.alfresco.jlan.server.auth.InvalidUserException;
import org.alfresco.jlan.server.auth.acl.AccessControl;
import org.alfresco.jlan.server.auth.acl.AccessControlManager;
import org.alfresco.jlan.server.core.InvalidDeviceInterfaceException;
import org.alfresco.jlan.server.core.ShareType;
import org.alfresco.jlan.server.core.SharedDevice;
import org.alfresco.jlan.server.filesys.AccessDeniedException;
import org.alfresco.jlan.server.filesys.AccessMode;
import org.alfresco.jlan.server.filesys.DeferredPacketException;
import org.alfresco.jlan.server.filesys.DirectoryNotEmptyException;
import org.alfresco.jlan.server.filesys.DiskDeviceContext;
import org.alfresco.jlan.server.filesys.DiskFullException;
import org.alfresco.jlan.server.filesys.DiskInterface;
import org.alfresco.jlan.server.filesys.DiskOfflineException;
import org.alfresco.jlan.server.filesys.ExistingOpLockException;
import org.alfresco.jlan.server.filesys.FileAccess;
import org.alfresco.jlan.server.filesys.FileAction;
import org.alfresco.jlan.server.filesys.FileAttribute;
import org.alfresco.jlan.server.filesys.FileExistsException;
import org.alfresco.jlan.server.filesys.FileInfo;
import org.alfresco.jlan.server.filesys.FileName;
import org.alfresco.jlan.server.filesys.FileNameException;
import org.alfresco.jlan.server.filesys.FileOfflineException;
import org.alfresco.jlan.server.filesys.FileOpenParams;
import org.alfresco.jlan.server.filesys.FileSharingException;
import org.alfresco.jlan.server.filesys.FileStatus;
import org.alfresco.jlan.server.filesys.FileSystem;
import org.alfresco.jlan.server.filesys.IOControlNotImplementedException;
import org.alfresco.jlan.server.filesys.IOCtlInterface;
import org.alfresco.jlan.server.filesys.NetworkFile;
import org.alfresco.jlan.server.filesys.NotifyChange;
import org.alfresco.jlan.server.filesys.PathNotFoundException;
import org.alfresco.jlan.server.filesys.SearchContext;
import org.alfresco.jlan.server.filesys.SecurityDescriptorInterface;
import org.alfresco.jlan.server.filesys.SrvDiskInfo;
import org.alfresco.jlan.server.filesys.TooManyConnectionsException;
import org.alfresco.jlan.server.filesys.TooManyFilesException;
import org.alfresco.jlan.server.filesys.TreeConnection;
import org.alfresco.jlan.server.filesys.UnsupportedInfoLevelException;
import org.alfresco.jlan.server.filesys.VolumeInfo;
import org.alfresco.jlan.server.locking.FileLockingInterface;
import org.alfresco.jlan.server.locking.LocalOpLockDetails;
import org.alfresco.jlan.server.locking.LockManager;
import org.alfresco.jlan.server.locking.OpLockDetails;
import org.alfresco.jlan.server.locking.OpLockInterface;
import org.alfresco.jlan.server.locking.OpLockManager;
import org.alfresco.jlan.smb.DataType;
import org.alfresco.jlan.smb.FileInfoLevel;
import org.alfresco.jlan.smb.FindFirstNext;
import org.alfresco.jlan.smb.InvalidUNCPathException;
import org.alfresco.jlan.smb.LockingAndX;
import org.alfresco.jlan.smb.NTTime;
import org.alfresco.jlan.smb.OpLock;
import org.alfresco.jlan.smb.PCShare;
import org.alfresco.jlan.smb.PacketType;
import org.alfresco.jlan.smb.SMBDate;
import org.alfresco.jlan.smb.SMBException;
import org.alfresco.jlan.smb.SMBStatus;
import org.alfresco.jlan.smb.TreeConnectAndX;
import org.alfresco.jlan.smb.WinNT;
import org.alfresco.jlan.smb.nt.LoadException;
import org.alfresco.jlan.smb.nt.NTIOCtl;
import org.alfresco.jlan.smb.nt.SaveException;
import org.alfresco.jlan.smb.nt.SecurityDescriptor;
import org.alfresco.jlan.smb.server.notify.NotifyChangeEventList;
import org.alfresco.jlan.smb.server.notify.NotifyChangeHandler;
import org.alfresco.jlan.smb.server.notify.NotifyRequest;
import org.alfresco.jlan.smb.server.ntfs.NTFSStreamsInterface;
import org.alfresco.jlan.smb.server.ntfs.StreamInfoList;
import org.alfresco.jlan.util.DataBuffer;
import org.alfresco.jlan.util.DataPacker;
import org.alfresco.jlan.util.MemorySize;
import org.alfresco.jlan.util.WildCard;

/**
 * NT SMB Protocol Handler Class
 * 
 * <p>
 * The NT protocol handler processes the additional SMBs that were added to the protocol in the NT
 * SMB dialect.
 * 
 * @author gkspencer
 */
public class NTProtocolHandler extends CoreProtocolHandler {

	// Constants
	//
	// Flag to enable returning of '.' and '..' directory information in FindFirst request

	public static final boolean ReturnDotFiles = true;

	// Dummy date/time for dot files

	public static final long DotFileDateTime = System.currentTimeMillis();

	// Flag to enable faking of oplock requests when opening files

	public static final boolean FakeOpLocks = false;

	// Number of write requests per file to report file size change notifications

	public static final int FileSizeChangeRate = 10;

	// Maximum path size that the filesystem accepts

	public static final int MaxPathLength = 255;

	// NTFS streams information buffer size
	
	public static final int NTFSStreamsInfoBufsize	= 4096;	// 4K buffer
	
	// Security descriptor to allow Everyone access, returned by the QuerySecurityDescrptor NT
	// transaction when NTFS streams are enabled for a virtual filesystem.

	private static byte[] _sdEveryOne = { 0x01, 0x00, 0x04, (byte) 0x80, 0x14, 0x00, 0x00, 0x00, 0x20, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x2c, 0x00, 0x00, 0x00, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00,
			0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x02, 0x00, 0x1c, 0x00, 0x01, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x14, 0x00, (byte) 0xff, 0x01, 0x1f, 0x00, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00,
			0x00, 0x00, 0x00 };

	/**
	 * Class constructor.
	 */
	protected NTProtocolHandler() {
		super();
	}

	/**
	 * Class constructor
	 * 
	 * @param sess SMBSrvSession
	 */
	protected NTProtocolHandler(SMBSrvSession sess) {
		super(sess);
	}

	/**
	 * Return the protocol name
	 * 
	 * @return String
	 */
	public String getName() {
		return "NT";
	}

	/**
	 * Run the NT SMB protocol handler to process the received SMB packet
	 * 
	 * @param smbPkt SMBSrvPacket
	 * @return boolean true if the packet was processed, else false
	 * @exception IOException
	 * @exception SMBSrvException
	 * @exception TooManyConnectionsException
	 */
	public boolean runProtocol( SMBSrvPacket smbPkt)
		throws java.io.IOException, SMBSrvException, TooManyConnectionsException {

		// Check if the received packet has a valid SMB signature

		if ( smbPkt.checkPacketSignature() == false)
			throw new IOException("Invalid SMB signature");

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_STATE) &&
				hasChainedCommand(smbPkt))
			m_sess.debugPrintln("AndX Command = 0x" + Integer.toHexString(smbPkt.getAndXCommand()));

		// Reset the byte unpack offset

		smbPkt.resetBytePointer();

		// Set the process id from the received packet, this can change for the same session and
		// needs to be set for lock ownership checking
		//
		// TODO: Need to remove this
		
		m_sess.setProcessId(smbPkt.getProcessId());

		// Determine the SMB command type

		boolean handledOK = true;

		switch (smbPkt.getCommand()) {

			// NT Session setup
	
			case PacketType.SessionSetupAndX:
				procSessionSetup(smbPkt);
				break;
	
			// Tree connect
	
			case PacketType.TreeConnectAndX:
				procTreeConnectAndX(smbPkt);
				break;
	
			// Transaction/transaction2
	
			case PacketType.Transaction:
			case PacketType.Transaction2:
				procTransact2(smbPkt);
				break;
	
			// Transaction/transaction2 secondary
	
			case PacketType.TransactionSecond:
			case PacketType.Transaction2Second:
				procTransact2Secondary(smbPkt);
				break;
	
			// Close a search started via the FindFirst transaction2 command
	
			case PacketType.FindClose2:
				procFindClose(smbPkt);
				break;
	
			// Open a file
	
			case PacketType.OpenAndX:
				procOpenAndX(smbPkt);
				break;
	
			// Close a file
	
			case PacketType.CloseFile:
				procCloseFile(smbPkt);
				break;
	
			// Read a file
	
			case PacketType.ReadAndX:
				procReadAndX(smbPkt);
				break;
	
			// Write to a file
	
			case PacketType.WriteAndX:
				procWriteAndX(smbPkt);
				break;
	
			// Rename file
	
			case PacketType.RenameFile:
				procRenameFile(smbPkt);
				break;
	
			// Delete file
	
			case PacketType.DeleteFile:
				procDeleteFile(smbPkt);
				break;
	
			// Delete directory
	
			case PacketType.DeleteDirectory:
				procDeleteDirectory(smbPkt);
				break;
	
			// Tree disconnect
	
			case PacketType.TreeDisconnect:
				procTreeDisconnect(smbPkt);
				break;
	
			// Lock/unlock regions of a file
	
			case PacketType.LockingAndX:
				procLockingAndX(smbPkt);
				break;
	
			// Logoff a user
	
			case PacketType.LogoffAndX:
				procLogoffAndX(smbPkt);
				break;
	
			// NT Create/open file
	
			case PacketType.NTCreateAndX:
				procNTCreateAndX(smbPkt);
				break;
	
			// Tree connection (without AndX batching)
	
			case PacketType.TreeConnect:
				super.runProtocol(smbPkt);
				break;
	
			// NT cancel
	
			case PacketType.NTCancel:
				procNTCancel(smbPkt);
				break;
	
			// NT transaction
	
			case PacketType.NTTransact:
				procNTTransaction(smbPkt);
				break;
	
			// NT transaction secondary
	
			case PacketType.NTTransactSecond:
				procNTTransactionSecondary(smbPkt);
				break;
	
			// Echo request
	
			case PacketType.Echo:
				super.procEcho(smbPkt);
				break;
	
			// Default
	
			default:
	
				// Get the tree connection details, if it is a disk or printer type connection then pass
				// the request to the core protocol handler
	
				int treeId = smbPkt.getTreeId();
				TreeConnection conn = null;
				if ( treeId != -1)
					conn = m_sess.findTreeConnection(smbPkt);
	
				if ( conn != null) {
	
					// Check if this is a disk or print connection, if so then send the request to the
					// core protocol handler
	
					if ( conn.getSharedDevice().getType() == ShareType.DISK || conn.getSharedDevice().getType() == ShareType.PRINTER) {
	
						// Chain to the core protocol handler
	
						handledOK = super.runProtocol( smbPkt);
					}
					else if ( conn.getSharedDevice().getType() == ShareType.ADMINPIPE) {
	
						// Send the request to IPC$ remote admin handler
	
						IPCHandler.processIPCRequest(m_sess, smbPkt);
						handledOK = true;
					}
				}
				else {
					
					// Need to send a response or the client may hang
					
					m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.SRVInvalidTID, SMBStatus.ErrSrv);
//					m_sess.sendErrorResponseSMB( smbPkt, 0x00010002, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
				}
				break;
		}

		// Run any request post processors
		
		runRequestPostProcessors( m_sess);
		
		// Return the handled status

		return handledOK;
	}

	/**
	 * Process the NT SMB session setup request.
	 * 
	 * @param smbPkt SMBSrvPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 * @exception TooManyConnectionsException
	 */
	protected void procSessionSetup(SMBSrvPacket smbPkt)
		throws SMBSrvException, IOException, TooManyConnectionsException {

		// Call the authenticator to process the session setup

		ICifsAuthenticator cifsAuthenticator = m_sess.getSMBServer().getCifsAuthenticator();

		try {

			// Process the session setup request, build the response

			cifsAuthenticator.processSessionSetup(m_sess, smbPkt);
		}
		catch (SMBSrvException ex) {

			// Return an error response to the client

			m_sess.sendErrorResponseSMB( smbPkt, ex.getNTErrorCode(), ex.getErrorCode(), ex.getErrorClass());
			return;
		}

		// Check if a new packet was allocated for the response
		
		SMBSrvPacket outPkt = smbPkt;
		if ( smbPkt.hasAssociatedPacket() && smbPkt.hasAndXCommand() == false)
			outPkt = outPkt.getAssociatedPacket();
		
		// Check if there is a chained command, or commands

		int pos = outPkt.getLength();

		if ( smbPkt.hasAndXCommand() && smbPkt.getPosition() < smbPkt.getReceivedLength()) {

			// Process any chained commands, AndX

			pos = procAndXCommands(outPkt, null);
			pos -= RFCNetBIOSProtocol.HEADER_LEN;
			
			// Switch to the response packet
			
			outPkt = smbPkt.getAssociatedPacket();
		}
		else {

			// Indicate that there are no chained replies

			outPkt.setAndXCommand(SMBSrvPacket.NO_ANDX_CMD);
		}

		// Send the session setup response

		m_sess.sendResponseSMB(outPkt, pos);

		// Update the session state if the response indicates a success status. A multi stage
		// session setup response returns a warning status.

		if ( outPkt.getLongErrorCode() == SMBStatus.NTSuccess) {

			// Update the session state

			m_sess.setState(SMBSrvSessionState.SMBSESSION);

			// Find the virtual circuit allocated, this will set the per-thread ClientInfo on the session
			
			m_sess.findVirtualCircuit( outPkt.getUserId());
			
			// Notify listeners that a user has logged onto the session

			m_sess.getSMBServer().sessionLoggedOn(m_sess);
		}
	}

	/**
	 * Process the chained SMB commands (AndX).
	 * 
	 * @param smbPkt Request packet.
	 * @param file Current file , or null if no file context in chain
	 * @return New offset to the end of the reply packet
	 */
	protected final int procAndXCommands(SMBSrvPacket smbPkt, NetworkFile file) {

		// Get the response packet
		
		SMBSrvPacket respPkt = smbPkt.getAssociatedPacket();
		if ( respPkt == null)
			throw new RuntimeException("No response packet allocated for AndX request");
		
		// Get the chained command and command block offset

		int andxCmd = smbPkt.getAndXCommand();
		int andxOff = smbPkt.getParameter(1) + RFCNetBIOSProtocol.HEADER_LEN;

		// Set the initial chained command and offset

		respPkt.setAndXCommand(andxCmd);

		int endOfPkt = respPkt.getByteOffset() + respPkt.getByteCount();
		respPkt.setParameter(1, endOfPkt - RFCNetBIOSProtocol.HEADER_LEN);

		// Pointer to the last parameter block, starts with the main command parameter block

		int paramBlk = SMBSrvPacket.WORDCNT;

		// Get the current end of the reply packet offset

		boolean andxErr = false;

		while (andxCmd != SMBSrvPacket.NO_ANDX_CMD && andxErr == false) {

			// Determine the chained command type

			int prevEndOfPkt = endOfPkt;
			boolean endOfChain = false;

			switch (andxCmd) {

			// Tree connect

			case PacketType.TreeConnectAndX:
				endOfPkt = procChainedTreeConnectAndX(andxOff, smbPkt, respPkt, endOfPkt);
				break;

			// Close file

			case PacketType.CloseFile:
				endOfPkt = procChainedClose(andxOff, smbPkt, respPkt, endOfPkt);
				endOfChain = true;
				break;

			// Read file

			case PacketType.ReadAndX:
				endOfPkt = procChainedReadAndX(andxOff, smbPkt, respPkt, endOfPkt, file);
				break;

			// Chained command was not handled

			default:
				if ( Debug.EnableError)
					Debug.println("<<<<< Chained command : 0x" + Integer.toHexString(andxCmd) + " Not Processed >>>>>");
				break;
			}

			// Set the next chained command details in the current parameter block

			respPkt.setAndXCommand(paramBlk, andxCmd);
			respPkt.setAndXParameter(paramBlk, 1, prevEndOfPkt - RFCNetBIOSProtocol.HEADER_LEN);

			// Check if the end of chain has been reached, if not then look for the next
			// chained command in the request. End of chain might be set if the current command
			// is not an AndX SMB command.

			if ( endOfChain == false) {

				// Advance to the next chained command block

				andxCmd = smbPkt.getAndXParameter(andxOff, 0) & 0x00FF;
				andxOff = smbPkt.getAndXParameter(andxOff, 1);

				// Advance the current parameter block

				paramBlk = prevEndOfPkt;
			}
			else {

				// Indicate that the end of the command chain has been reached

				andxCmd = SMBSrvPacket.NO_ANDX_CMD;
			}

			// Check if the chained command has generated an error status

			if ( respPkt.getErrorCode() != SMBStatus.Success)
				andxErr = true;
		}

		// Return the offset to the end of the reply packet

		return endOfPkt;
	}

	/**
	 * Process a chained tree connect request.
	 * 
	 * @param cmdOff int Offset to the chained command within the request packet.
	 * @param smbPkt Request packet.
	 * @param respPkt Response packet
	 * @param endOff int Offset to the current end of the reply packet.
	 * @return New end of reply offset.
	 */
	protected final int procChainedTreeConnectAndX(int cmdOff, SMBSrvPacket smbPkt, SMBSrvPacket respPkt, int endOff) {

		// Extract the parameters

		int flags  = smbPkt.getAndXParameter(cmdOff, 2);
		int pwdLen = smbPkt.getAndXParameter(cmdOff, 3);

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(respPkt.getUserId());

		if ( vc == null) {
			respPkt.setError(smbPkt.isLongErrorCode(), SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return endOff;
		}

		// Reset the byte pointer for data unpacking

		smbPkt.setBytePointer(smbPkt.getAndXByteOffset(cmdOff), smbPkt.getAndXByteCount(cmdOff));

		// Extract the password string

		String pwd = null;

		if ( pwdLen > 0) {
			byte[] pwdByt = smbPkt.unpackBytes(pwdLen);
			pwd = new String(pwdByt);
		}

		// Extract the requested share name, as a UNC path

		boolean unicode = smbPkt.isUnicode();

		String uncPath = smbPkt.unpackString(unicode);
		if ( uncPath == null) {
			respPkt.setError(smbPkt.isLongErrorCode(), SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError,	SMBStatus.ErrSrv);
			return endOff;
		}

		// Extract the service type string

		String service = smbPkt.unpackString(false);
		if ( service == null) {
			respPkt.setError(smbPkt.isLongErrorCode(), SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError,	SMBStatus.ErrSrv);
			return endOff;
		}

		// Convert the service type to a shared device type, client may specify '?????' in which
		// case we ignore the error.

		int servType = ShareType.ServiceAsType(service);
		if ( servType == ShareType.UNKNOWN && service.compareTo("?????") != 0) {
			respPkt.setError(smbPkt.isLongErrorCode(), SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return endOff;
		}

		// Debug

		if ( m_sess.hasDebug(SMBSrvSession.DBG_TREE))
			m_sess.debugPrintln("NT ANDX Tree Connect AndX - " + uncPath + ", " + service);

		// Parse the requested share name

		PCShare share = null;

		try {
			share = new PCShare(uncPath);
		}
		catch (InvalidUNCPathException ex) {
			respPkt.setError(smbPkt.isLongErrorCode(), SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return endOff;
		}

		// Map the IPC$ share to the admin pipe type

		if ( share.getShareName().compareTo("IPC$") == 0)
			servType = ShareType.ADMINPIPE;

		// Check if the session is a null session, only allow access to the IPC$ named pipe share

		if ( m_sess.hasClientInformation() && m_sess.getClientInformation().isNullSession() && servType != ShareType.ADMINPIPE) {

			// Return an error status

			respPkt.setError(smbPkt.isLongErrorCode(), SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return endOff;
		}

		// Find the requested shared device

		SharedDevice shareDev = null;

		try {

			// Get/create the shared device

			shareDev = m_sess.getSMBServer().findShare(share.getNodeName(), share.getShareName(), servType, m_sess, true);
		}
		catch (InvalidUserException ex) {

			// Return a logon failure status

			respPkt.setError(smbPkt.isLongErrorCode(), SMBStatus.NTLogonFailure, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return endOff;
		}
		catch (Exception ex) {

			// Return a general status, bad network name

			respPkt.setError(smbPkt.isLongErrorCode(), SMBStatus.NTBadNetName, SMBStatus.SRVInvalidNetworkName, SMBStatus.ErrSrv);
			return endOff;
		}

		// Check if the share is valid

		if ( shareDev == null || (servType != ShareType.UNKNOWN && shareDev.getType() != servType)) {

			// Set the error status

			respPkt.setError(smbPkt.isLongErrorCode(), SMBStatus.NTBadNetName, SMBStatus.SRVInvalidNetworkName, SMBStatus.ErrSrv);
			return endOff;
		}

		// Authenticate the share connect, if the server is using share mode security

		ICifsAuthenticator auth = getSession().getSMBServer().getCifsAuthenticator();
		int sharePerm = FileAccess.Writeable;

		if ( auth != null && auth.getAccessMode() == CifsAuthenticator.SHARE_MODE) {

			// Validate the share connection

			sharePerm = auth.authenticateShareConnect(m_sess.getClientInformation(), shareDev, pwd, m_sess);
			if ( sharePerm < 0) {

				// Invalid share connection request

				respPkt.setError(smbPkt.isLongErrorCode(), SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied,	SMBStatus.ErrDos);
				return endOff;
			}
		}

		// Check if there is an access control manager, if so then run any access controls to
		// determine the sessions access to the share.

		if ( getSession().getServer().hasAccessControlManager() && shareDev.hasAccessControls()) {

			// Get the access control manager

			AccessControlManager aclMgr = getSession().getServer().getAccessControlManager();

			// Update the access permission for this session by processing the access control list
			// for the shared device

			int aclPerm = aclMgr.checkAccessControl(getSession(), shareDev);

			if ( aclPerm == FileAccess.NoAccess) {

				// Invalid share connection request

				respPkt.setError(smbPkt.isLongErrorCode(), SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
				return endOff;
			}

			// If the access controls returned a new access type update the main permission

			if ( aclPerm != AccessControl.Default)
				sharePerm = aclPerm;
		}

		// Allocate a tree id for the new connection

		TreeConnection tree = null;

		try {

			// Allocate the tree id for this connection

			int treeId = vc.addConnection(shareDev);
			respPkt.setTreeId(treeId);

			// Set the file permission that this user has been granted for this share

			tree = vc.findConnection(treeId);
			tree.setPermission(sharePerm);

			// Inform the driver that a connection has been opened

			if ( tree.getInterface() != null)
				tree.getInterface().treeOpened(m_sess, tree);

			// Debug

			if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_TREE))
				m_sess.debugPrintln("ANDX Tree Connect AndX - Allocated Tree Id = " + treeId);
		}
		catch (TooManyConnectionsException ex) {

			// Too many connections open at the moment

			respPkt.setError(SMBStatus.SRVNoResourcesAvailable, SMBStatus.ErrSrv);
			return endOff;
		}

		// Build the tree connect response

		respPkt.setAndXParameterCount(endOff, 2);
		respPkt.setAndXParameter(endOff, 0, SMBSrvPacket.NO_ANDX_CMD);
		respPkt.setAndXParameter(endOff, 1, 0);

		// Pack the service type

		int pos = respPkt.getAndXByteOffset(endOff);
		byte[] outBuf = respPkt.getBuffer();
		pos = DataPacker.putString(ShareType.TypeAsService(shareDev.getType()), outBuf, pos, true);

		// Determine the filesystem type, for disk shares

		String devType = "";

		try {

			// Check if this is a disk shared device

			if ( shareDev.getType() == ShareType.DISK) {

				// Check if the filesystem driver implements the NTFS streams interface, and streams
				// are enabled

				if ( shareDev.getInterface() instanceof NTFSStreamsInterface) {

					// Check if NTFS streams are enabled

					NTFSStreamsInterface ntfsStreams = (NTFSStreamsInterface) shareDev.getInterface();
					if ( ntfsStreams.hasStreamsEnabled(m_sess, tree))
						devType = FileSystem.TypeNTFS;
				}
				else {

					// Get the filesystem type from the context

					DiskDeviceContext diskCtx = (DiskDeviceContext) tree.getContext();
					devType = diskCtx.getFilesystemType();
				}
			}
		}
		catch (InvalidDeviceInterfaceException ex) {
			
			// Debug

			if ( Debug.EnableError && m_sess.hasDebug(SMBSrvSession.DBG_TREE))
				Debug.println("ANDX TreeConnectAndX error " + ex.getMessage());
		}

		// Pack the filesystem type

		pos = DataPacker.putString(devType, outBuf, pos, true, respPkt.isUnicode());

		int bytLen = pos - respPkt.getAndXByteOffset(endOff);
		respPkt.setAndXByteCount(endOff, bytLen);

		// Return the new end of packet offset

		return pos;
	}

	/**
	 * Process a chained read file request
	 * 
	 * @param cmdOff Offset to the chained command within the request packet.
	 * @param smbPkt Request packet.
	 * @param respPkt Response packet
	 * @param endOff Offset to the current end of the reply packet.
	 * @param netFile File to be read, passed down the chained requests
	 * @return New end of reply offset.
	 */
	protected final int procChainedReadAndX(int cmdOff, SMBSrvPacket smbPkt, SMBSrvPacket respPkt, int endOff, NetworkFile netFile) {

		// Get the tree id from the received packet and validate that it is a valid
		// connection id.

		TreeConnection conn = m_sess.findTreeConnection(smbPkt);

		if ( conn == null) {
			respPkt.setError(SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return endOff;
		}

		// Extract the read file parameters

		long offset = (long) smbPkt.getAndXParameterLong(cmdOff, 3); // bottom 32bits of read offset
		offset &= 0xFFFFFFFFL;
		int maxCount = smbPkt.getAndXParameter(cmdOff, 5);

		// Check for the NT format request that has the top 32bits of the file offset

		if ( smbPkt.getAndXParameterCount(cmdOff) == 12) {
			long topOff = (long) smbPkt.getAndXParameterLong(cmdOff, 10);
			offset += topOff << 32;
		}

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_FILE))
			m_sess.debugPrintln("Chained File Read AndX : Size=" + maxCount + " ,Pos=" + offset);

		// Read data from the file

		byte[] buf = respPkt.getBuffer();
		int dataPos = 0;
		int rdlen = 0;

		try {

			// Access the disk interface that is associated with the shared device

			DiskInterface disk = (DiskInterface) conn.getSharedDevice().getInterface();

			// Set the returned parameter count so that the byte offset can be calculated

			respPkt.setAndXParameterCount(endOff, 12);
			dataPos = respPkt.getAndXByteOffset(endOff);
			dataPos = DataPacker.wordAlign(dataPos); // align the data buffer

			// Check if the requested data length will fit into the buffer

			int dataLen = buf.length - dataPos;
			if ( dataLen < maxCount)
				maxCount = dataLen;

			// Read from the file

			rdlen = disk.readFile(m_sess, conn, netFile, buf, dataPos, maxCount, offset);

			// Return the data block

			respPkt.setAndXParameter(endOff, 0, SMBSrvPacket.NO_ANDX_CMD);
			respPkt.setAndXParameter(endOff, 1, 0);

			respPkt.setAndXParameter(endOff, 2, 0); // bytes remaining, for pipes only
			respPkt.setAndXParameter(endOff, 3, 0); // data compaction mode
			respPkt.setAndXParameter(endOff, 4, 0); // reserved
			respPkt.setAndXParameter(endOff, 5, rdlen); // data length
			respPkt.setAndXParameter(endOff, 6, dataPos - RFCNetBIOSProtocol.HEADER_LEN); // offset to data

			// Clear the reserved parameters

			for (int i = 7; i < 12; i++)
				respPkt.setAndXParameter(endOff, i, 0);

			// Set the byte count

			respPkt.setAndXByteCount(endOff, (dataPos + rdlen) - respPkt.getAndXByteOffset(endOff));

			// Update the end offset for the new end of packet

			endOff = dataPos + rdlen;
		}
		catch (InvalidDeviceInterfaceException ex) {

			// Failed to get/initialize the disk interface

			respPkt.setError(SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return endOff;
		}
		catch (java.io.IOException ex) {
		}

		// Return the new end of packet offset

		return endOff;
	}

	/**
	 * Process a chained close file request
	 * 
	 * @param cmdOff int Offset to the chained command within the request packet.
	 * @param smbPkt Request packet.
	 * @param respPkt Response packet
	 * @param endOff int Offset to the current end of the reply packet.
	 * @return New end of reply offset.
	 */
	protected final int procChainedClose(int cmdOff, SMBSrvPacket smbPkt, SMBSrvPacket respPkt, int endOff) {

		// Get the tree id from the received packet and validate that it is a valid
		// connection id.

		TreeConnection conn = m_sess.findTreeConnection(smbPkt);

		if ( conn == null) {
			respPkt.setError(SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return endOff;
		}

		// Get the file id from the request

		int fid   = smbPkt.getAndXParameter(cmdOff, 0);
		int ftime = smbPkt.getAndXParameter(cmdOff, 1);
		int fdate = smbPkt.getAndXParameter(cmdOff, 2);

		NetworkFile netFile = conn.findFile(fid);

		if ( netFile == null) {
			respPkt.setError(SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return endOff;
		}

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_FILE))
			m_sess.debugPrintln("Chained File Close [" + smbPkt.getTreeId() + "] fid=" + fid);

		// Close the file

		try {

			// Access the disk interface that is associated with the shared device

			DiskInterface disk = (DiskInterface) conn.getSharedDevice().getInterface();

			// Close the file
			//
			// The disk interface may be null if the file is a named pipe file

			if ( disk != null)
				disk.closeFile(m_sess, conn, netFile);

			// Indicate that the file has been closed

			netFile.setClosed(true);
		}
		catch (InvalidDeviceInterfaceException ex) {

			// Failed to get/initialize the disk interface

			respPkt.setError(SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return endOff;
		}
		catch (java.io.IOException ex) {
		}

		// Clear the returned parameter count and byte count

		respPkt.setAndXParameterCount(endOff, 0);
		respPkt.setAndXByteCount(endOff, 0);

		endOff = respPkt.getAndXByteOffset(endOff) - RFCNetBIOSProtocol.HEADER_LEN;

		// Remove the file from the connections list of open files

		conn.removeFile(fid, getSession());

		// Return the new end of packet offset

		return endOff;
	}

	/**
	 * Process the SMB tree connect request.
	 * 
	 * @param smbPkt Request packet.
	 * @exception IOException
	 * @exception SMBSrvException
	 * @exception TooManyConnectionsException
	 */
	protected void procTreeConnectAndX(SMBSrvPacket smbPkt)
		throws SMBSrvException, TooManyConnectionsException, java.io.IOException {

		// Check that the received packet looks like a valid tree connect request

		if ( smbPkt.checkPacketIsValid(4, 3) == false) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());
		if ( vc == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}

		// Extract the parameters

		int flags  = smbPkt.getParameter(2);
		int pwdLen = smbPkt.getParameter(3);

		// Initialize the byte area pointer

		smbPkt.resetBytePointer();

		// Determine if ASCII or unicode strings are being used

		boolean unicode = smbPkt.isUnicode();

		// Extract the password string

		String pwd = null;

		if ( pwdLen > 0) {
			byte[] pwdByts = smbPkt.unpackBytes(pwdLen);
			pwd = new String(pwdByts);
		}

		// Extract the requested share name, as a UNC path

		String uncPath = smbPkt.unpackString(unicode);
		if ( uncPath == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}

		// Extract the service type string, always seems to be ASCII

		String service = smbPkt.unpackString(false);
		if ( service == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}

		// Convert the service type to a shared device type, client may specify '?????' in which
		// case we ignore the error.

		int servType = ShareType.ServiceAsType(service);
		if ( servType == ShareType.UNKNOWN && service.compareTo("?????") != 0) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_TREE))
			m_sess.debugPrintln("NT Tree Connect AndX - " + uncPath + ", " + service + ", flags=" + TreeConnectAndX.asStringRequest( flags) + "/0x" + Integer.toHexString( flags));

		// Parse the requested share name

		String shareName = null;
		String hostName = null;

		if ( uncPath.startsWith("\\")) {

			try {
				PCShare share = new PCShare(uncPath);
				shareName = share.getShareName();
				hostName = share.getNodeName();
			}
			catch (InvalidUNCPathException ex) {
				m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
				return;
			}
		}
		else
			shareName = uncPath;

		// Map the IPC$ share to the admin pipe type

		if ( shareName.compareTo("IPC$") == 0)
			servType = ShareType.ADMINPIPE;

		// Check if the session is a null session, only allow access to the IPC$ named pipe share

		if ( m_sess.hasClientInformation() && m_sess.getClientInformation().isNullSession() && servType != ShareType.ADMINPIPE) {

			// Return an error status

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

		// Find the requested shared device

		SharedDevice shareDev = null;

		try {

			// Get/create the shared device

			shareDev = m_sess.getSMBServer().findShare(hostName, shareName, servType, m_sess, true);
		}
		catch (InvalidUserException ex) {

			// Return a logon failure status

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTLogonFailure, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}
		catch (Exception ex) {

			// Return a general status, bad network name

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTBadNetName, SMBStatus.SRVInvalidNetworkName, SMBStatus.ErrSrv);
			return;
		}

		// Check if the share is valid

		if ( shareDev == null || (servType != ShareType.UNKNOWN && shareDev.getType() != servType)) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTBadNetName, SMBStatus.SRVInvalidNetworkName, SMBStatus.ErrSrv);
			return;
		}

		// Authenticate the share connection depending upon the security mode the server is running
		// under

		ICifsAuthenticator auth = getSession().getSMBServer().getCifsAuthenticator();
		int sharePerm = FileAccess.Writeable;

		if ( auth != null) {

			// Validate the share connection

			sharePerm = auth.authenticateShareConnect(m_sess.getClientInformation(), shareDev, pwd, m_sess);
			if ( sharePerm < 0) {

				// DEBUG

				if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_TREE))
					m_sess.debugPrint("Tree connect to " + shareName + ", access denied");

				// Invalid share connection request

				m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
				return;
			}
		}

		// Check if there is an access control manager, if so then run any access controls to
		// determine the sessions access to the share.

		if ( getSession().getServer().hasAccessControlManager() && shareDev.hasAccessControls()) {

			// Get the access control manager

			AccessControlManager aclMgr = getSession().getServer().getAccessControlManager();

			// Update the access permission for this session by processing the access control list
			// for the shared device

			int aclPerm = aclMgr.checkAccessControl(getSession(), shareDev);

			if ( aclPerm == FileAccess.NoAccess) {

				// Invalid share connection request

				m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
				return;
			}

			// If the access controls returned a new access type update the main permission

			if ( aclPerm != AccessControl.Default)
				sharePerm = aclPerm;
		}

		// Allocate a tree id for the new connection

		int treeId = vc.addConnection(shareDev);
		smbPkt.setTreeId(treeId);

		// Set the file permission that this user has been granted for this share

		TreeConnection tree = vc.findConnection(treeId);
		tree.setPermission(sharePerm);

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_TREE))
			m_sess.debugPrintln("Tree Connect AndX - Allocated Tree Id = " + treeId + ", Permission = "
					+ FileAccess.asString(sharePerm) + ", extendedResponse=" + TreeConnectAndX.hasExtendedResponse( flags));

		// Check if an extended format response is required, only return for filesystem shares
		
		if ( TreeConnectAndX.hasExtendedResponse( flags) && servType != ShareType.ADMINPIPE) {

			// Build the extended tree connect response
			
			smbPkt.setParameterCount(7);
			smbPkt.setAndXCommand(0xFF); // no chained reply
			smbPkt.setParameter(1, 0);
			smbPkt.setParameter(2, 0);	// response flags
			
			// Maximal user access rights
			
			if (sharePerm == FileAccess.Writeable)
				smbPkt.setParameterLong(3, AccessMode.NTFileGenericAll);
			else
				smbPkt.setParameterLong(3, AccessMode.NTFileGenericRead);
			
			// Guest maximal access rights
			
			smbPkt.setParameterLong(5, 0);
		}
		else {
			
			// Build the standard tree connect response
	
			smbPkt.setParameterCount(3);
			smbPkt.setAndXCommand(0xFF); // no chained reply
			smbPkt.setParameter(1, 0);
			smbPkt.setParameter(2, 0);	// response flags
		}
		
		// Pack the service type

		int pos = smbPkt.getByteOffset();
		pos = DataPacker.putString(ShareType.TypeAsService(shareDev.getType()), smbPkt.getBuffer(), pos, true);

		// Determine the filesystem type, for disk shares

		String devType = "";

		try {

			// Check if this is a disk shared device

			if ( shareDev.getType() == ShareType.DISK) {

				// Check if the filesystem driver implements the NTFS streams interface, and streams
				// are enabled

				if ( shareDev.getInterface() instanceof NTFSStreamsInterface) {

					// Check if NTFS streams are enabled

					NTFSStreamsInterface ntfsStreams = (NTFSStreamsInterface) shareDev.getInterface();
					if ( ntfsStreams.hasStreamsEnabled(m_sess, tree))
						devType = "NTFS";
				}
				else {

					// Get the filesystem type from the context

					DiskDeviceContext diskCtx = (DiskDeviceContext) tree.getContext();
					devType = diskCtx.getFilesystemType();
				}
			}
		}
		catch (InvalidDeviceInterfaceException ex) {

			// Log the error

			if ( Debug.EnableError && m_sess.hasDebug(SMBSrvSession.DBG_TREE))
				Debug.println("TreeConnectAndX error " + ex.getMessage());
		}

		// Pack the filesystem type

		pos = DataPacker.wordAlign( pos);
		pos = DataPacker.putString(devType, smbPkt.getBuffer(), pos, true, smbPkt.isUnicode());
		smbPkt.setByteCount(pos - smbPkt.getByteOffset());

		// Send the response

		m_sess.sendResponseSMB(smbPkt);

		// Inform the driver that a connection has been opened

		if ( tree.getInterface() != null)
			tree.getInterface().treeOpened(m_sess, tree);
	}

	/**
	 * Close a file that has been opened on the server.
	 * 
	 * @param smbPkt SMBSrvPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	protected void procCloseFile(SMBSrvPacket smbPkt)
		throws IOException, SMBSrvException {

		// Check that the received packet looks like a valid file close request

		if ( smbPkt.checkPacketIsValid(3, 0) == false) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.SRVUnrecognizedCommand, SMBStatus.ErrSrv);
			return;
		}

		// Get the tree id from the received packet and validate that it is a valid
		// connection id.

		TreeConnection conn = m_sess.findTreeConnection(smbPkt);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Check if the user has the required access permission

		if ( conn.hasReadAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.SRVNoAccessRights, SMBStatus.ErrSrv);
			return;
		}

		// Get the file id from the request

		int fid = smbPkt.getParameter(0);
		int ftime = smbPkt.getParameter(1);
		int fdate = smbPkt.getParameter(2);

		NetworkFile netFile = conn.findFile(fid);

		if ( netFile == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidHandle, SMBStatus.ErrDos);
			return;
		}

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_FILE))
			m_sess.debugPrintln("File close [" + smbPkt.getTreeId() + "] fid=" + fid + ", fileId=" + netFile.getFileId());

		// Close the file

		boolean delayedClose = false;
		
		try {

			// Access the disk interface that is associated with the shared device

			DiskInterface disk = (DiskInterface) conn.getSharedDevice().getInterface();

			// Close the file
			//
			// The disk interface may be null if the file is a named pipe file

			if ( disk != null) {
				
				// DEBUG
				
				long startTime = 0L;
				
				if ( netFile.hasDeleteOnClose() && Debug.EnableInfo && m_sess.hasDebug( SMBSrvSession.DBG_BENCHMARK))
					startTime = System.currentTimeMillis();
			
				// Check if the file has an oplock
				
				if ( netFile.hasOpLock())
					releaseOpLock( m_sess, smbPkt, disk, conn, netFile);
				
				// Close the file
				
				disk.closeFile(m_sess, conn, netFile);

				// Release any byte range locks that are on the file
				
			    if ( netFile.hasLocks() && disk instanceof FileLockingInterface) {
			          
			    	//  Get the lock manager
			          
			        FileLockingInterface flIface = (FileLockingInterface) disk;
			        LockManager lockMgr = flIface.getLockManager( m_sess, conn);
			          
			        //  DEBUG
			          
			        if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_LOCK))
			        	Debug.println("Releasing locks for closed file, file=" + netFile.getFullName() + ", locks=" + netFile.numberOfLocks());
			            
			        //  Release all locks on the file owned by this session
			          
			        lockMgr.releaseLocksForFile( m_sess, conn, netFile);
			    }
			        
				// Check if the file close has been delayed by the filesystem driver
				
				if ( netFile.hasDelayedClose()) {
				    delayedClose = true;
				    
				    // Reset the delayed close status
				    
				    netFile.setDelayedClose( false);

				    // DEBUG
		            
		            if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_FILE))
		                m_sess.debugPrintln("File close delayed [" + smbPkt.getTreeId() + "] fid=" + fid + ", path=" + netFile.getFullName());
				}
				
				// DEBUG
				
				if ( startTime != 0L && Debug.EnableInfo && m_sess.hasDebug( SMBSrvSession.DBG_BENCHMARK))
					Debug.println("Benchmark: Delete on close " + netFile.getName() + " took " + ( System.currentTimeMillis() - startTime) + "ms");
			}
			
			// Indicate that the file has been closed

			if ( delayedClose == false)
			    netFile.setClosed(true);
			
			// DEBUG
			
			if ( Debug.EnableInfo && m_sess.hasDebug( SMBSrvSession.DBG_BENCHMARK)) {
				if ( netFile.isDirectory() == false) {
					if ( netFile.wasCreated() && netFile.getWriteCount() > 0)
						m_sess.debugPrintln("Benchmark: File=" + netFile.getFullName() + ", Size=" + MemorySize.asScaledString(netFile.getFileSize()) +
								", Write Time=" + (System.currentTimeMillis() - netFile.getCreationDate()) + "ms" +
								", ClosedAt="  + new Time(System.currentTimeMillis()));
				}
				else if ( netFile.getCreationDate() != 0L)
					m_sess.debugPrintln("Benchmark: Dir=" + netFile.getFullName() +
							", Write Time=" + (System.currentTimeMillis() - netFile.getCreationDate()) + "ms, CreatedAt=" + new Time(netFile.getCreationDate()));
				else
					m_sess.debugPrintln("Benchmark: Dir=" + netFile.getFullName() + ", ClosedAt=" + new Time(System.currentTimeMillis()));
			}
		}
		catch (InvalidDeviceInterfaceException ex) {

			// Failed to get/initialize the disk interface

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
		}
		catch (AccessDeniedException ex) {

			// Not allowed to delete the file, when the delete on close flag has been set

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}
		catch (Throwable t) {
		}

		// Remove the file from the connections list of open files

		if ( delayedClose == false)
		    conn.removeFile(fid, getSession());

		// Build the close file response

		smbPkt.setParameterCount(0);
		smbPkt.setByteCount(0);

		// Send the response packet

		m_sess.sendResponseSMB(smbPkt);

		// Check if there are any file/directory change notify requests active

		DiskDeviceContext diskCtx = (DiskDeviceContext) conn.getContext();
		if ( netFile.getWriteCount() > 0 && diskCtx.hasFileServerNotifications())
			diskCtx.getChangeHandler().notifyFileSizeChanged(netFile.getFullName());

		if ( netFile.hasDeleteOnClose() && diskCtx.hasFileServerNotifications())
			diskCtx.getChangeHandler().notifyFileChanged(NotifyChange.ActionRemoved, netFile.getFullName());
	}

	/**
	 * Process a transact2 request. The transact2 can contain many different sub-requests.
	 * 
	 * @param smbPkt SMBSrvPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	protected void procTransact2(SMBSrvPacket smbPkt)
		throws IOException, SMBSrvException {

		// Check that we received enough parameters for a transact2 request

		if ( smbPkt.checkPacketIsValid(14, 0) == false) {

			// Not enough parameters for a valid transact2 request

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());

		if ( vc == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Get the tree id from the received packet and validate that it is a valid
		// connection id.

		TreeConnection conn = vc.findConnection(smbPkt.getTreeId());

		if ( conn == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Check if the user has the required access permission

		if ( conn.hasReadAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.SRVNoAccessRights, SMBStatus.ErrSrv);
			return;
		}

		// Create a transact packet using the received SMB packet

		SMBSrvTransPacket tranPkt = new SMBSrvTransPacket(smbPkt.getBuffer());

		// Create a transact buffer to hold the transaction setup, parameter and data blocks

		SrvTransactBuffer transBuf = null;
		int subCmd = tranPkt.getSubFunction();

		if ( tranPkt.getTotalParameterCount() == tranPkt.getRxParameterBlockLength()
				&& tranPkt.getTotalDataCount() == tranPkt.getRxDataBlockLength()) {

			// Create a transact buffer using the packet buffer, the entire request is contained in
			// a single packet

			transBuf = new SrvTransactBuffer(tranPkt);
		}
		else {

			// Create a transact buffer to hold the multiple transact request parameter/data blocks

			transBuf = new SrvTransactBuffer(tranPkt.getSetupCount(), tranPkt.getTotalParameterCount(), tranPkt.getTotalDataCount());
			transBuf.setType(tranPkt.getCommand());
			transBuf.setFunction(subCmd);

			// Append the setup, parameter and data blocks to the transaction data

			byte[] buf = tranPkt.getBuffer();

			transBuf.appendSetup(buf, tranPkt.getSetupOffset(), tranPkt.getSetupCount() * 2);
			transBuf.appendParameter(buf, tranPkt.getRxParameterBlock(), tranPkt.getRxParameterBlockLength());
			transBuf.appendData(buf, tranPkt.getRxDataBlock(), tranPkt.getRxDataBlockLength());
		}

		// Set the return data limits for the transaction

		transBuf.setReturnLimits(tranPkt.getMaximumReturnSetupCount(), tranPkt.getMaximumReturnParameterCount(), tranPkt.getMaximumReturnDataCount());

		// Clear the transaction packet buffer, as it is owned by the original packet
		
		tranPkt.setBuffer( null);
		
		// Check for a multi-packet transaction, for a multi-packet transaction we just acknowledge
		// the receive with an empty response SMB

		if ( transBuf.isMultiPacket()) {

			// Save the partial transaction data

			vc.setTransaction(transBuf);

			// Send an intermediate acknowedgement response

			m_sess.sendSuccessResponseSMB( smbPkt);
			return;
		}

		// Check if the transaction is on the IPC$ named pipe, the request requires special processing

		if ( conn.getSharedDevice().getType() == ShareType.ADMINPIPE) {
			IPCHandler.procTransaction(vc, transBuf, m_sess, smbPkt);
			return;
		}

		// DEBUG

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_TRAN))
			m_sess.debugPrintln("Transaction [" + smbPkt.getTreeId() + "] tbuf=" + transBuf);

		// Process the transaction buffer

		processTransactionBuffer(transBuf, smbPkt);
	}

	/**
	 * Process a transact2 secondary request.
	 * 
	 * @param smbPkt SMBSrvPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	protected void procTransact2Secondary(SMBSrvPacket smbPkt)
		throws IOException, SMBSrvException {

		// Check that we received enough parameters for a transact2 request

		if ( smbPkt.checkPacketIsValid(8, 0) == false) {

			// Not enough parameters for a valid transact2 request

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());

		if ( vc == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Get the tree id from the received packet and validate that it is a valid
		// connection id.

		int treeId = smbPkt.getTreeId();
		TreeConnection conn = vc.findConnection(treeId);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Check if the user has the required access permission

		if ( conn.hasReadAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.SRVNoAccessRights, SMBStatus.ErrSrv);
			return;
		}

		// Check if there is an active transaction, and it is an NT transaction

		if ( vc.hasTransaction() == false
				|| (vc.getTransaction().isType() == PacketType.Transaction && smbPkt.getCommand() != PacketType.TransactionSecond)
				|| (vc.getTransaction().isType() == PacketType.Transaction2 && smbPkt.getCommand() != PacketType.Transaction2Second)) {

			// No transaction to continue, or packet does not match the existing transaction, return
			// an error

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}

		// Create an NT transaction using the received packet

		SMBSrvTransPacket tpkt = new SMBSrvTransPacket(smbPkt.getBuffer());
		byte[] buf = tpkt.getBuffer();
		SrvTransactBuffer transBuf = vc.getTransaction();

		// Append the parameter data to the transaction buffer, if any

		int plen = tpkt.getSecondaryParameterBlockCount();
		if ( plen > 0) {

			// Append the data to the parameter buffer

			DataBuffer paramBuf = transBuf.getParameterBuffer();
			paramBuf.appendData(buf, tpkt.getSecondaryParameterBlockOffset(), plen);
		}

		// Append the data block to the transaction buffer, if any

		int dlen = tpkt.getSecondaryDataBlockCount();
		if ( dlen > 0) {

			// Append the data to the data buffer

			DataBuffer dataBuf = transBuf.getDataBuffer();
			dataBuf.appendData(buf, tpkt.getSecondaryDataBlockOffset(), dlen);
		}

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_TRAN))
			m_sess.debugPrintln("Transaction Secondary [" + treeId + "] paramLen=" + plen + ", dataLen=" + dlen);

		// Check if the transaction has been received or there are more sections to be received

		int totParam = tpkt.getTotalParameterCount();
		int totData = tpkt.getTotalDataCount();

		int paramDisp = tpkt.getParameterBlockDisplacement();
		int dataDisp = tpkt.getDataBlockDisplacement();

		if ( (paramDisp + plen) == totParam && (dataDisp + dlen) == totData) {

			// Debug

			if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_TRAN))
				m_sess.debugPrintln("Transaction complete, processing ...");

			// Clear the in progress transaction

			vc.setTransaction(null);

			// Check if the transaction is on the IPC$ named pipe, the request requires special processing

			if ( conn.getSharedDevice().getType() == ShareType.ADMINPIPE) {
				IPCHandler.procTransaction(vc, transBuf, m_sess, smbPkt);
				return;
			}

			// DEBUG

			if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_TRAN))
				m_sess.debugPrintln("Transaction second [" + treeId + "] tbuf=" + transBuf);

			// Process the transaction

			processTransactionBuffer(transBuf, smbPkt);
		}
		else {

			// There are more transaction parameter/data sections to be received, return an
			// intermediate response

			m_sess.sendSuccessResponseSMB( smbPkt);
		}
	}

	/**
	 * Process a transaction buffer
	 * 
	 * @param tbuf TransactBuffer
	 * @param smbtPkt SMBSrvPacket
	 * @exception IOException If a network error occurs
	 * @exception SMBSrvException If an SMB error occurs
	 */
	private final void processTransactionBuffer(SrvTransactBuffer tbuf, SMBSrvPacket smbPkt)
		throws IOException, SMBSrvException {

		// Get the transact2 sub-command code and process the request

		switch (tbuf.getFunction()) {

			// Start a file search
	
			case PacketType.Trans2FindFirst:
				procTrans2FindFirst(tbuf, smbPkt);
				break;
	
			// Continue a file search
	
			case PacketType.Trans2FindNext:
				procTrans2FindNext(tbuf, smbPkt);
				break;
	
			// Query file system information
	
			case PacketType.Trans2QueryFileSys:
				procTrans2QueryFileSys(tbuf, smbPkt);
				break;
	
			// Query path
	
			case PacketType.Trans2QueryPath:
				procTrans2QueryPath(tbuf, smbPkt);
				break;
	
			// Query file information via handle
	
			case PacketType.Trans2QueryFile:
				procTrans2QueryFile(tbuf, smbPkt);
				break;
	
			// Set file information via handle
	
			case PacketType.Trans2SetFile:
				procTrans2SetFile(tbuf, smbPkt);
				break;
	
			// Set file information via path
	
			case PacketType.Trans2SetPath:
				procTrans2SetPath(tbuf, smbPkt);
				break;
	
			// Unknown transact2 command
	
			default:
	
				// Return an unrecognized command error
	
				if ( Debug.EnableError)
					m_sess.debugPrintln("NT Error Transact2 Command = 0x" + Integer.toHexString(tbuf.getFunction()));
				m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
				break;
		}
	}

	/**
	 * Close a search started via the transact2 find first/next command.
	 * 
	 * @param smbPkt SMBSrvPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	protected final void procFindClose(SMBSrvPacket smbPkt)
		throws IOException, SMBSrvException {

		// Check that the received packet looks like a valid find close request

		if ( smbPkt.checkPacketIsValid(1, 0) == false) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());

		if ( vc == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Get the tree connection details

		int treeId = smbPkt.getTreeId();
		TreeConnection conn = vc.findConnection(treeId);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Check if the user has the required access permission

		if ( conn.hasReadAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.SRVNoAccessRights, SMBStatus.ErrSrv);
			return;
		}

		// Get the search id

		int searchId = smbPkt.getParameter(0);

		// Get the search context

		SearchContext ctx = vc.getSearchContext(searchId);

		if ( ctx == null) {

			// Invalid search handle

			m_sess.sendSuccessResponseSMB( smbPkt);
			return;
		}

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_SEARCH))
			m_sess.debugPrintln("Close trans search [" + searchId + "]");

		// Deallocate the search slot, close the search.

		vc.deallocateSearchSlot(searchId);

		// Return a success status SMB

		m_sess.sendSuccessResponseSMB( smbPkt);
	}

	/**
	 * Process the file lock/unlock request.
	 * 
	 * @param smbPkt SMBSrvPacket
	 * @exception IOException
	 * @exception SMBSrvPacket
	 */
	protected final void procLockingAndX(SMBSrvPacket smbPkt)
		throws IOException, SMBSrvException {

		// Check that the received packet looks like a valid locking andX request

		if ( smbPkt.checkPacketIsValid(8, 0) == false) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());

		if ( vc == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Get the tree connection details

		int treeId = smbPkt.getTreeId();
		TreeConnection conn = vc.findConnection(treeId);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Check if the user has the required access permission

		if ( conn.hasReadAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.SRVNoAccessRights, SMBStatus.ErrSrv);
			return;
		}

		// Extract the file lock/unlock parameters

		int fid = smbPkt.getParameter(2);
		int lockType = smbPkt.getParameter(3);
		long lockTmo = smbPkt.getParameterLong(4);
		int unlockCnt = smbPkt.getParameter(6);
		int lockCnt = smbPkt.getParameter(7);

		NetworkFile netFile = conn.findFile(fid);

		if ( netFile == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.Win32InvalidHandle, SMBStatus.DOSInvalidHandle, SMBStatus.ErrDos);
			return;
		}

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_LOCK))
			m_sess.debugPrintln("File Lock [" + netFile.getFileId() + "] : type=0x" + Integer.toHexString(lockType) + ", tmo="
					+ lockTmo + ", locks=" + lockCnt + ", unlocks=" + unlockCnt);

		DiskInterface disk = null;
		try {

			// Get the disk interface for the share

			disk = (DiskInterface) conn.getSharedDevice().getInterface();
		}
		catch (InvalidDeviceInterfaceException ex) {

			// Failed to get/initialize the disk interface

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
		}

		// Check for an oplock break
		
		if ( LockingAndX.hasOplockBreak( lockType)) {
			
			// Debug

			if ( Debug.EnableDbg && m_sess.hasDebug(SMBSrvSession.DBG_OPLOCK))
				Debug.println("Oplock break, file=" + netFile);
				
			// Access the oplock manager via the filesystem
			
			if ( disk instanceof OpLockInterface) {
				
				// Get the oplock manager
				
				OpLockInterface oplockIface = (OpLockInterface) disk;
				OpLockManager oplockMgr = oplockIface.getOpLockManager( m_sess, conn);
				
				if ( oplockMgr == null) {
					
					// DEBUG
					
					if ( Debug.EnableDbg && m_sess.hasDebug( SMBSrvSession.DBG_OPLOCK))
						Debug.print( "  OpLock manager is null, tree=" + conn);
					
					// Return a not supported error
					
					m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.SRVNotSupported, SMBStatus.ErrSrv);
					return;
				}

				// Get the oplock details for the file
				
				OpLockDetails oplock = oplockMgr.getOpLockDetails( netFile.getFullName());
				if ( oplock == null) {
					
					// Return a not locked error
					
					m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTRangeNotLocked, SMBStatus.DOSNotLocked, SMBStatus.ErrDos);
					return;
				}
				
				// Release the oplock
				
				oplockMgr.releaseOpLock( oplock.getPath());
				
				// DEBUG
				
				if ( Debug.EnableDbg && m_sess.hasDebug(SMBSrvSession.DBG_OPLOCK))
					Debug.println("  Oplock released, oplock=" + oplock);
				
				// Check if there is a deferred CIFS request pending for this oplock
				
				if ( oplock.hasDeferredSession()) {
					
					// DEBUG
					
					if ( Debug.EnableDbg && m_sess.hasDebug(SMBSrvSession.DBG_OPLOCK))
						Debug.println("  Queued deferred request to thread pool sess=" + oplock.getDeferredSession().getUniqueId() + ", pkt=" + oplock.getDeferredPacket());

					// Queue the deferred request to the thread pool for processing
					
					m_sess.getThreadPool().queueRequest( new CIFSThreadRequest( oplock.getDeferredSession(), oplock.getDeferredPacket()));
					
					// Do not send a response to the client, it is a response to the oplock break sent from the server
					
					return;
				}
			}
			else {
				
				// Return a not supported error
				
				m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.SRVNotSupported, SMBStatus.ErrSrv);
				return;
			}
		}
		
		// Check for byte range locks/unlocks
		
		if ( unlockCnt > 0 || lockCnt > 0) {
			
			// Check if the virtual filesystem supports file locking
	
			if ( disk instanceof FileLockingInterface) {
	
				// Get the lock manager
	
				FileLockingInterface lockInterface = (FileLockingInterface) disk;
				LockManager lockMgr = lockInterface.getLockManager(m_sess, conn);
	
				// Unpack the lock/unlock structures
	
				smbPkt.resetBytePointer();
				boolean largeFileLock = LockingAndX.hasLargeFiles(lockType);

				int lockIdx = 0;
				
				while ( lockIdx < (unlockCnt + lockCnt)) {
	
					// Get the unlock/lock structure
	
					int pid = smbPkt.unpackWord();
					long offset = -1;
					long length = -1;
	
					if ( largeFileLock == false) {
	
						// Get the lock offset and length, short format
	
						offset = smbPkt.unpackInt();
						length = smbPkt.unpackInt();
					}
					else {
	
						// Get the lock offset and length, large format
	
						smbPkt.skipBytes(2);
	
						offset = ((long) smbPkt.unpackInt()) << 32;
						offset += (long) smbPkt.unpackInt();
	
						length = ((long) smbPkt.unpackInt()) << 32;
						length += (long) smbPkt.unpackInt();
					}
	
					// Create the lock/unlock details
	
					FileLock fLock = lockMgr.createLockObject(m_sess, conn, netFile, offset, length, pid);
					boolean isLock = lockIdx++ < lockCnt;
					
					// Debug
	
					if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_LOCK))
						m_sess.debugPrintln("  " + (isLock ? "Lock" : "UnLock") + " lock=" + fLock);
	
					// Perform the lock/unlock request
	
					try {
	
						// Check if the request is an unlock
	
						if ( isLock == false) {
	
							// Unlock the file
	
							lockMgr.unlockFile(m_sess, conn, netFile, fLock);
						}
						else {
	
							// Lock the file
	
							lockMgr.lockFile(m_sess, conn, netFile, fLock);
						}
					}
					catch (NotLockedException ex) {
	
						// Return an error status
	
						m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTRangeNotLocked, SMBStatus.DOSNotLocked, SMBStatus.ErrDos);
						return;
					}
					catch (LockConflictException ex) {
	
						// Return an error status
	
						m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTLockNotGranted, SMBStatus.DOSLockConflict, SMBStatus.ErrDos);
						return;
					}
					catch (IOException ex) {
	
						// Return an error status
	
						m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.SRVInternalServerError, SMBStatus.ErrSrv);
						return;
					}
				}
			}
			else {
	
				// Filesystem does not support byte range locking
				//
				// Return a 'not locked' status if there are unlocks in the request else return a
				// success status
	
				if ( unlockCnt > 0) {
	
					// Return an error status
	
					m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTRangeNotLocked, SMBStatus.DOSNotLocked, SMBStatus.ErrDos);
					return;
				}
			}
	
			// Return a success response
	
			smbPkt.setParameterCount(2);
			smbPkt.setAndXCommand(0xFF);
			smbPkt.setParameter(1, 0);
			smbPkt.setByteCount(0);
	
			// Send the lock request response
	
			m_sess.sendResponseSMB(smbPkt);
		}
	}

	/**
	 * Process the logoff request.
	 * 
	 * @param smbPkt SMBSrvPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	protected final void procLogoffAndX(SMBSrvPacket smbPkt)
		throws IOException, SMBSrvException {

		// Check that the received packet looks like a valid logoff andX request

		if ( smbPkt.checkPacketIsValid(2, 0) == false) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}

		// Get the virtual circuit for the request

		int uid = smbPkt.getUserId();
		VirtualCircuit vc = m_sess.findVirtualCircuit(uid);

		if ( vc == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// DEBUG

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_NEGOTIATE))
			Debug.println("[SMB] LogoffAndX vc=" + vc);

		// Mark the virtual circuit as logged off

		vc.setLoggedOn( false);

		// Check if there are no tree connections on this virtual circuit
		
		if ( vc.getConnectionCount() == 0) {
			
			// Remove the virtual circuit
			
			m_sess.removeVirtualCircuit( vc.getUID());
			
			// Debug

			if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_NEGOTIATE))
				m_sess.debugPrintln("  Removed virtual circuit " + vc);
		}
			
		// Return a success status SMB

		m_sess.sendSuccessResponseSMB( smbPkt);
		
		// If there are no active virtual circuits then close the session/socket
		
		if ( m_sess.numberOfVirtualCircuits() == 0) {
			
			// DEBUG
			
			if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_NEGOTIATE))
				Debug.println("  Closing session, no more virtual circuits");
			
			// Close the session/socket
			
			m_sess.hangupSession( "Client logoff");
		}
	}

	/**
	 * Process the file open request.
	 * 
	 * @param smbPkt SMBSrvPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	protected final void procOpenAndX(SMBSrvPacket smbPkt)
		throws IOException, SMBSrvException {

		// Check that the received packet looks like a valid open andX request

		if ( smbPkt.checkPacketIsValid(15, 1) == false) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());

		if ( vc == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Get the tree connection details

		int treeId = smbPkt.getTreeId();
		TreeConnection conn = vc.findConnection(treeId);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Check if the user has the required access permission

		if ( conn.hasReadAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

		// If the connection is to the IPC$ remote admin named pipe pass the request to the IPC
		// handler. If the device is not a disk type device then return an error.

		if ( conn.getSharedDevice().getType() == ShareType.ADMINPIPE) {

			// Use the IPC$ handler to process the request

			IPCHandler.processIPCRequest(m_sess, smbPkt);
			return;
		}
		else if ( conn.getSharedDevice().getType() != ShareType.DISK) {

			// Return an access denied error

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

		// Extract the open file parameters

		int flags = smbPkt.getParameter(2);
		int access = smbPkt.getParameter(3);
		int srchAttr = smbPkt.getParameter(4);
		int fileAttr = smbPkt.getParameter(5);
		int crTime = smbPkt.getParameter(6);
		int crDate = smbPkt.getParameter(7);
		int openFunc = smbPkt.getParameter(8);
		int allocSiz = smbPkt.getParameterLong(9);

		// Extract the filename string

		String fileName = smbPkt.unpackString(smbPkt.isUnicode());
		if ( fileName == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
		}

		// Create the file open parameters

		long crDateTime = 0L;
		if ( crTime > 0 && crDate > 0)
			crDateTime = new SMBDate(crDate, crTime).getTime();

		FileOpenParams params = new FileOpenParams(fileName, openFunc, access, srchAttr, fileAttr, allocSiz, crDateTime, smbPkt.getProcessIdFull());

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_FILE))
			m_sess.debugPrintln("File Open AndX [" + treeId + "] params=" + params);

        // Check if the file name is valid
        
        if ( isValidPath( params.getPath()) == false) {
            m_sess.sendErrorResponseSMB(smbPkt, SMBStatus.NTObjectNameInvalid, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
            return;
        }
        
		// Access the disk interface and open the requested file

		int fid;
		NetworkFile netFile = null;
		int respAction = 0;

		try {

			// Access the disk interface that is associated with the shared device

			DiskInterface disk = (DiskInterface) conn.getSharedDevice().getInterface();

			// Check if the requested file already exists

			int fileSts = disk.fileExists(m_sess, conn, fileName);

			if ( fileSts == FileStatus.NotExist) {

				// Check if the file should be created if it does not exist

				if ( FileAction.createNotExists(openFunc)) {

					// Check if the session has write access to the filesystem

					if ( conn.hasWriteAccess() == false) {

						// User does not have the required access rights

						m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
						return;
					}

					// Create a new file

					netFile = disk.createFile(m_sess, conn, params);

					// Indicate that the file did not exist and was created

					respAction = FileAction.FileCreated;
				}
				else {

					// Check if the path is a directory

					if ( fileSts == FileStatus.DirectoryExists) {

						// Return an access denied error

						m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
					}
					else {

						// Return a file not found error

						m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
					}
					return;
				}
			}
			else {

				// Open the requested file

				netFile = disk.openFile(m_sess, conn, params);

				// Set the file action response

				if ( FileAction.truncateExistingFile(openFunc)) {

					// Truncate the existing file

					disk.truncateFile(m_sess, conn, netFile, 0L);

					// Set the response

					respAction = FileAction.FileTruncated;
				}
				else
					respAction = FileAction.FileExisted;
			}

			// Add the file to the list of open files for this tree connection

			fid = conn.addFile(netFile, getSession());

		}
		catch (InvalidDeviceInterfaceException ex) {

			// Failed to get/initialize the disk interface

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
		}
		catch (TooManyFilesException ex) {

			// Too many files are open on this connection, cannot open any more files.

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSTooManyOpenFiles, SMBStatus.ErrDos);
			return;
		}
		catch (AccessDeniedException ex) {

			// Return an access denied error

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}
		catch (FileSharingException ex) {

			// Return a sharing violation error

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTSharingViolation, SMBStatus.DOSFileSharingConflict, SMBStatus.ErrDos);
			return;
		}
		catch (FileOfflineException ex) {

			// File data is unavailable

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTFileOffline, SMBStatus.HRDDriveNotReady, SMBStatus.ErrHrd);
			return;
		}
		catch (DiskOfflineException ex) {

			// Filesystem is offline

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectPathNotFound, SMBStatus.HRDDriveNotReady, SMBStatus.ErrHrd);
			return;
		}
		catch (IOException ex) {

			// Failed to open the file

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
			return;
		}

		// Build the open file response

		smbPkt.setParameterCount(15);

		smbPkt.setAndXCommand(0xFF);
		smbPkt.setParameter(1, 0); // AndX offset

		smbPkt.setParameter(2, fid);
		smbPkt.setParameter(3, netFile.getFileAttributes()); // file attributes

		SMBDate modDate = null;

		if ( netFile.hasModifyDate())
			modDate = new SMBDate(netFile.getModifyDate());

		smbPkt.setParameter(4, modDate != null ? modDate.asSMBTime() : 0); // last write time
		smbPkt.setParameter(5, modDate != null ? modDate.asSMBDate() : 0); // last write date
		smbPkt.setParameterLong(6, netFile.getFileSizeInt()); // file size
		smbPkt.setParameter(8, netFile.getGrantedAccess());
		smbPkt.setParameter(9, OpenAndX.FileTypeDisk);
		smbPkt.setParameter(10, 0); // named pipe state
		smbPkt.setParameter(11, respAction);
		smbPkt.setParameter(12, 0); // server FID (long)
		smbPkt.setParameter(13, 0);
		smbPkt.setParameter(14, 0);

		smbPkt.setByteCount(0);

		// Send the response packet

		m_sess.sendResponseSMB(smbPkt);
	}

	/**
	 * Process the file read request.
	 * 
	 * @param smbPkt SMBSrvPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	protected final void procReadAndX(SMBSrvPacket smbPkt)
		throws IOException, SMBSrvException {

		// Check that the received packet looks like a valid read andX request

		if ( smbPkt.checkPacketIsValid(10, 0) == false) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());

		if ( vc == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Get the tree connection details

		int treeId = smbPkt.getTreeId();
		TreeConnection conn = vc.findConnection(treeId);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Check if the user has the required access permission

		if ( conn.hasReadAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

		// If the connection is to the IPC$ remote admin named pipe pass the request to the IPC
		// handler.

		if ( conn.getSharedDevice().getType() == ShareType.ADMINPIPE) {

			// Use the IPC$ handler to process the request

			IPCHandler.processIPCRequest(m_sess, smbPkt);
			return;
		}

		// Extract the read file parameters

		int fid = smbPkt.getParameter(2);
		long offset = smbPkt.getParameterLong(3); // bottom 32bits of read offset
		offset &= 0xFFFFFFFFL;
		int maxCount = smbPkt.getParameter(5);

		// Check for the NT format request that has the top 32bits of the file offset

		if ( smbPkt.getParameterCount() == 12) {
			long topOff = smbPkt.getParameterLong(10);
			offset += topOff << 32;
		}

		NetworkFile netFile = conn.findFile(fid);

		if ( netFile == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidHandle, SMBStatus.ErrDos);
			return;
		}

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_FILEIO))
			m_sess.debugPrintln("File Read AndX [" + netFile.getFileId() + "] : Size=" + maxCount + " ,Pos=" + offset);

		// Read data from the file

		SMBSrvPacket respPkt = smbPkt;
		byte[] buf = respPkt.getBuffer();
		int dataPos = 0;
		int rdlen = 0;

		try {

			// Access the disk interface that is associated with the shared device

			DiskInterface disk = (DiskInterface) conn.getSharedDevice().getInterface();

			// Set the returned parameter count so that the byte offset can be calculated

			respPkt.setParameterCount(12);
			dataPos = respPkt.getByteOffset();
			dataPos = DataPacker.wordAlign(dataPos); // align the data buffer

			// Check if the requested data will fit into the current packet
			
			if ( maxCount > ( buf.length - dataPos)) {

				// Allocate a larger packet for the response
				
				respPkt = m_sess.getPacketPool().allocatePacket( maxCount + dataPos, smbPkt);
				
				// Switch to the response buffer
				
				buf = respPkt.getBuffer();
				respPkt.setParameterCount( 12);
			}
			
			// Check if the requested data length will fit into the buffer

			int dataLen = buf.length - dataPos;
			if ( dataLen < maxCount)
				maxCount = dataLen;

			// Read from the file

			rdlen = disk.readFile(m_sess, conn, netFile, buf, dataPos, maxCount, offset);
		}
		catch (InvalidDeviceInterfaceException ex) {

			// Failed to get/initialize the disk interface

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
		}
		catch (FileOfflineException ex) {

			// File data is unavailable

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTFileOffline, SMBStatus.HRDReadFault, SMBStatus.ErrHrd);
			return;
		}
		catch (LockConflictException ex) {

			// Debug

			if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_LOCK))
				m_sess.debugPrintln("Read Lock Error [" + netFile.getFileId() + "] : Size=" + maxCount + " ,Pos=" + offset);

			// File is locked

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTLockConflict, SMBStatus.DOSLockConflict, SMBStatus.ErrDos);
			return;
		}
		catch (AccessDeniedException ex) {

			// User does not have the required access rights or file is not accessible

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}
		catch (DiskOfflineException ex) {

			// Debug

			if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_FILEIO))
				m_sess.debugPrintln("Filesystem Offline Error [" + netFile.getFileId() + "] Read File");

			// Filesystem is offline

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectPathNotFound, SMBStatus.HRDDriveNotReady, SMBStatus.ErrHrd);
			return;
		}
		catch (IOException ex) {

			// Debug

			if ( Debug.EnableError && m_sess.hasDebug(SMBSrvSession.DBG_FILEIO)) {
				m_sess.debugPrintln("File Read Error [" + netFile.getFileId() + "] : " + ex.toString());
				m_sess.debugPrintln(ex);

				// Dump the network file details

				m_sess.debugPrintln("  NetworkFile name=" + netFile.getName() + "/" + netFile.getFullName());
				m_sess.debugPrintln("  attr=0x" + Integer.toHexString(netFile.getFileAttributes()) + ", size=" + netFile.getFileSize());
				m_sess.debugPrintln("  fid=" + netFile.getFileId() + ", cdate=" + netFile.getCreationDate() + ", mdate=" + netFile.getModifyDate());
				m_sess.debugPrintln("Offset = " + offset + " (0x" + Long.toHexString(offset) + ")");
			}

			// Failed to read the file

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTFileOffline, SMBStatus.HRDReadFault, SMBStatus.ErrHrd);
			return;
		}

		// Return the data block

		respPkt.setAndXCommand(0xFF); // no chained command
		respPkt.setParameter(1, 0);
		respPkt.setParameter(2, 0); // bytes remaining, for pipes only
		respPkt.setParameter(3, 0); // data compaction mode
		respPkt.setParameter(4, 0); // reserved
		respPkt.setParameter(5, rdlen); // data length
		respPkt.setParameter(6, dataPos - RFCNetBIOSProtocol.HEADER_LEN); // offset to data

		// Clear the reserved parameters

		for (int i = 7; i < 12; i++)
			respPkt.setParameter(i, 0);

		// Set the byte count

		respPkt.setByteCount((dataPos + rdlen) - smbPkt.getByteOffset());

		// Check if there is a chained command, or commands

		if ( smbPkt.hasAndXCommand()) {

			// Process any chained commands, AndX

			int pos = procAndXCommands(smbPkt, netFile);

			// Send the read andX response

			m_sess.sendResponseSMB(smbPkt.getAssociatedPacket(), pos);
		}
		else {

			// Send the normal read andX response

			m_sess.sendResponseSMB(respPkt);
		}
	}

	/**
	 * Rename a file.
	 * 
	 * @param smbPkt SMBSrvPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	protected void procRenameFile(SMBSrvPacket smbPkt)
		throws IOException, SMBSrvException {

		// Check that the received packet looks like a valid rename file request

		if ( smbPkt.checkPacketIsValid(1, 4) == false) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.SRVUnrecognizedCommand, SMBStatus.ErrSrv);
			return;
		}

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());

		if ( vc == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Get the tree id from the received packet and validate that it is a valid
		// connection id.

		int treeId = smbPkt.getTreeId();
		TreeConnection conn = vc.findConnection(treeId);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Check if the user has the required access permission

		if ( conn.hasWriteAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

		// Get the Unicode flag

		boolean isUni = smbPkt.isUnicode();

		// Read the data block

		smbPkt.resetBytePointer();

		// Extract the old file name

		if ( smbPkt.unpackByte() != DataType.ASCII) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
		}

		String oldName = smbPkt.unpackString(isUni);
		if ( oldName == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
		}

		// Extract the new file name

		if ( smbPkt.unpackByte() != DataType.ASCII) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
		}

		String newName = smbPkt.unpackString(isUni);
		if ( newName == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
		}

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_FILE))
			m_sess.debugPrintln("File Rename [" + treeId + "] old name=" + oldName + ", new name=" + newName);

        // Check if the from/to paths are valid
        
        if ( isValidPath( oldName) == false) {
            m_sess.sendErrorResponseSMB(smbPkt, SMBStatus.NTObjectNameInvalid, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
            return;
        }
        
        if ( isValidPath( newName) == false) {
            m_sess.sendErrorResponseSMB(smbPkt, SMBStatus.NTObjectNameInvalid, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
            return;
        }

		// Access the disk interface and rename the requested file

		int fid;
		NetworkFile netFile = null;

		try {

			// Access the disk interface that is associated with the shared device

			DiskInterface disk = (DiskInterface) conn.getSharedDevice().getInterface();

			// Rename the requested file

			disk.renameFile(m_sess, conn, oldName, newName);
		}
		catch (InvalidDeviceInterfaceException ex) {

			// Failed to get/initialize the disk interface

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
		}
		catch (FileNotFoundException ex) {

			// Source file/directory does not exist

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectNotFound, SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
			return;
		}
		catch (FileExistsException ex) {

			// Destination file/directory already exists

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectNameCollision, SMBStatus.DOSFileAlreadyExists, SMBStatus.ErrDos);
			return;
		}
		catch (AccessDeniedException ex) {

			// Not allowed to rename the file/directory

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}
		catch (FileSharingException ex) {

			// Return a sharing violation error

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTSharingViolation, SMBStatus.DOSFileSharingConflict, SMBStatus.ErrDos);
			return;
		}
		catch (DiskOfflineException ex) {

			// Filesystem is offline

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectPathNotFound, SMBStatus.HRDDriveNotReady, SMBStatus.ErrHrd);
		}
		catch (IOException ex) {

			// I/O exception

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Build the rename file response

		smbPkt.setParameterCount(0);
		smbPkt.setByteCount(0);
		smbPkt.setSuccessStatus();

		// Send the response packet

		m_sess.sendResponseSMB(smbPkt);

		// Check if there are any file/directory change notify requests active

		DiskDeviceContext diskCtx = (DiskDeviceContext) conn.getContext();
		if ( diskCtx.hasFileServerNotifications())
			diskCtx.getChangeHandler().notifyRename(oldName, newName);
	}

	/**
	 * Delete a file.
	 * 
	 * @param smbPkt SMBSrvPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	protected void procDeleteFile(SMBSrvPacket smbPkt)
		throws IOException, SMBSrvException {

		// Check that the received packet looks like a valid file delete request

		if ( smbPkt.checkPacketIsValid(1, 2) == false) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.SRVUnrecognizedCommand, SMBStatus.ErrSrv);
			return;
		}

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());

		if ( vc == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Get the tree id from the received packet and validate that it is a valid
		// connection id.

		int treeId = smbPkt.getTreeId();
		TreeConnection conn = vc.findConnection(treeId);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Check if the user has the required access permission

		if ( conn.hasWriteAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

		// Get the Unicode flag

		boolean isUni = smbPkt.isUnicode();

		// Read the data block

		smbPkt.resetBytePointer();

		// Extract the old file name

		if ( smbPkt.unpackByte() != DataType.ASCII) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
		}

		String fileName = smbPkt.unpackString(isUni);
		if ( fileName == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
		}

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_FILE))
			m_sess.debugPrintln("File Delete [" + treeId + "] name=" + fileName);

		// Access the disk interface and delete the file(s)

		int fid;
		NetworkFile netFile = null;
		long startTime = 0L;
		
		try {

			// DEBUG
			
			if ( Debug.EnableInfo && m_sess.hasDebug( SMBSrvSession.DBG_BENCHMARK))
				startTime = System.currentTimeMillis();
			
			// Access the disk interface that is associated with the shared device

			DiskInterface disk = (DiskInterface) conn.getSharedDevice().getInterface();

			// Delete file(s)

			disk.deleteFile(m_sess, conn, fileName);
			
			// DEBUG
			
			if ( Debug.EnableInfo && m_sess.hasDebug( SMBSrvSession.DBG_BENCHMARK))
				Debug.println("Benchmark: Delete file " + fileName + " took " + ( System.currentTimeMillis() - startTime) + "ms");
		}
		catch (InvalidDeviceInterfaceException ex) {

			// Failed to get/initialize the disk interface

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
		}
		catch (AccessDeniedException ex) {

			// Not allowed to delete the file

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}
		catch (DiskOfflineException ex) {

			// Filesystem is offline

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectPathNotFound, SMBStatus.HRDDriveNotReady, SMBStatus.ErrHrd);
			return;
		}
		catch (IOException ex) {

			// Failed to open the file

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectNotFound, SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
			return;
		}

		// Build the delete file response

		smbPkt.setParameterCount(0);
		smbPkt.setByteCount(0);
		smbPkt.setSuccessStatus();
		
		// Send the response packet

		m_sess.sendResponseSMB(smbPkt);

		// Check if there are any file/directory change notify requests active

		DiskDeviceContext diskCtx = (DiskDeviceContext) conn.getContext();
		if ( diskCtx.hasFileServerNotifications())
			diskCtx.getChangeHandler().notifyFileChanged(NotifyChange.ActionRemoved, fileName);
	}

	/**
	 * Delete a directory.
	 * 
	 * @param smbPkt SMBSrvPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	protected void procDeleteDirectory(SMBSrvPacket smbPkt)
		throws IOException, SMBSrvException {

		// Check that the received packet looks like a valid delete directory request

		if ( smbPkt.checkPacketIsValid(0, 2) == false) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.SRVUnrecognizedCommand, SMBStatus.ErrSrv);
			return;
		}

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());

		if ( vc == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Get the tree id from the received packet and validate that it is a valid
		// connection id.

		int treeId = smbPkt.getTreeId();
		TreeConnection conn = vc.findConnection(treeId);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Check if the user has the required access permission

		if ( conn.hasWriteAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

		// Get the Unicode flag

		boolean isUni = smbPkt.isUnicode();

		// Read the data block

		smbPkt.resetBytePointer();

		// Extract the old file name

		if ( smbPkt.unpackByte() != DataType.ASCII) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
		}

		String dirName = smbPkt.unpackString(isUni);
		if ( dirName == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
		}

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_FILE))
			m_sess.debugPrintln("Directory Delete [" + treeId + "] name=" + dirName);

		// Access the disk interface and delete the directory

		try {

			// Access the disk interface that is associated with the shared device

			DiskInterface disk = (DiskInterface) conn.getSharedDevice().getInterface();

			// Delete the directory

			disk.deleteDirectory(m_sess, conn, dirName);
		}
		catch (InvalidDeviceInterfaceException ex) {

			// Failed to get/initialize the disk interface

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
		}
		catch (AccessDeniedException ex) {

			// Not allowed to delete the directory

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}
		catch (DirectoryNotEmptyException ex) {

			// Directory not empty

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSDirectoryNotEmpty, SMBStatus.ErrDos);
			return;
		}
		catch (DiskOfflineException ex) {

			// Filesystem is offline

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectPathNotFound, SMBStatus.HRDDriveNotReady, SMBStatus.ErrHrd);
			return;
		}
		catch (IOException ex) {

			// Failed to delete the directory

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSDirectoryInvalid, SMBStatus.ErrDos);
			return;
		}

		// Build the delete directory response

		smbPkt.setParameterCount(0);
		smbPkt.setByteCount(0);
		smbPkt.setSuccessStatus();

		// Send the response packet

		m_sess.sendResponseSMB(smbPkt);

		// Check if there are any file/directory change notify requests active

		DiskDeviceContext diskCtx = (DiskDeviceContext) conn.getContext();
		if ( diskCtx.hasFileServerNotifications())
			diskCtx.getChangeHandler().notifyDirectoryChanged(NotifyChange.ActionRemoved, dirName);
	}

	/**
	 * Process a transact2 file search request.
	 * 
	 * @param tbuf Transaction request details
	 * @param smbPkt SMBSrvPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	protected final void procTrans2FindFirst(SrvTransactBuffer tbuf, SMBSrvPacket smbPkt)
		throws IOException, SMBSrvException {

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());

		if ( vc == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Get the tree connection details

		int treeId = smbPkt.getTreeId();
		TreeConnection conn = vc.findConnection(treeId);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Check if the user has the required access permission

		if ( conn.hasReadAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.SRVNoAccessRights, SMBStatus.ErrSrv);
			return;
		}

		// Get the search parameters

		DataBuffer paramBuf = tbuf.getParameterBuffer();

		int srchAttr = paramBuf.getShort();
		int maxFiles = paramBuf.getShort();
		int srchFlag = paramBuf.getShort();
		int infoLevl = paramBuf.getShort();
		paramBuf.skipBytes(4);

		String srchPath = paramBuf.getString(tbuf.isUnicode());

		// Check if the search contains Unicode wildcards

		if ( tbuf.isUnicode() && WildCard.containsUnicodeWildcard(srchPath)) {

			// Translate the Unicode wildcards to standard DOS wildcards

			srchPath = WildCard.convertUnicodeWildcardToDOS(srchPath);

			// Debug

			if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_SEARCH))
				m_sess.debugPrintln("Converted Unicode wildcards to:" + srchPath);
		}

        // Check if the search path is valid
        
        if ( isValidSearchPath( srchPath) == false) {
            m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectNameInvalid, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
            return;
        }
        
		// Check if the search path is valid

		if ( srchPath == null || srchPath.length() == 0) {

			// Invalid search request

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
		}
		else if ( srchPath.endsWith( FileName.DOS_SEPERATOR_STR)) {

			// Make the search a wildcard search

			srchPath = srchPath + "*.*";
		}
		else if ( srchPath.startsWith( FileName.DOS_SEPERATOR_STR) == false) {
			
			// Prefix the search path to make it a relative path
			
			srchPath = FileName.DOS_SEPERATOR_STR + srchPath;

			// Debug

			if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_SEARCH))
				m_sess.debugPrintln("Search path missing leading slash, converted to relative path");
		}

		// Check for the Macintosh information level, if the Macintosh extensions are not enabled
		// return an error

		if ( infoLevl == FindInfoPacker.InfoMacHfsInfo && getSession().hasMacintoshExtensions() == false) {

			// Return an error status, Macintosh extensions are not enabled

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNotSupported, SMBStatus.ErrSrv);
			return;
		}

		// Access the shared device disk interface

		SearchContext ctx = null;
		DiskInterface disk = null;
		int searchId = -1;
		boolean wildcardSearch = false;

		try {

			// Access the disk interface

			disk = (DiskInterface) conn.getSharedDevice().getInterface();

			// Allocate a search slot for the new search

			searchId = vc.allocateSearchSlot();
			if ( searchId == -1) {

				// Failed to allocate a slot for the new search

				m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.SRVNoResourcesAvailable, SMBStatus.ErrSrv);
				return;
			}

			// Check if this is a wildcard search or single file search

			if ( WildCard.containsWildcards(srchPath))
				wildcardSearch = true;

			// Start a new search

			ctx = disk.startSearch(m_sess, conn, srchPath, srchAttr);
			if ( ctx != null) {

				// Store details of the search in the context

				ctx.setTreeId(treeId);
				ctx.setMaximumFiles(maxFiles);
			}
			else {

				// Deallocate the search

				if ( searchId != -1)
					vc.deallocateSearchSlot(searchId);

				// Failed to start the search, return a no more files error

				m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTNoSuchFile, SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
				return;
			}

			// Save the search context

			vc.setSearchContext(searchId, ctx);

			// Create the reply transact buffer

			SrvTransactBuffer replyBuf = new SrvTransactBuffer(tbuf);
			DataBuffer dataBuf = replyBuf.getDataBuffer();

			// Determine the maximum return data length

			int maxLen = replyBuf.getReturnDataLimit();

			// Debug

			if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_SEARCH))
				m_sess.debugPrintln("Start trans search [" + searchId + "] - " + srchPath + ", attr=0x"
						+ Integer.toHexString(srchAttr) + ", maxFiles=" + maxFiles + ", maxLen=" + maxLen + ", infoLevel="
						+ infoLevl + ", flags=0x" + Integer.toHexString(srchFlag) + ",dotFiles=" + ctx.hasDotFiles());

			// Loop until we have filled the return buffer or there are no more files to return

			int fileCnt = 0;
			int packLen = 0;
			int lastNameOff = 0;

			// Flag to indicate if resume ids should be returned

			boolean resumeIds = false;
			if ( infoLevl == FindInfoPacker.InfoStandard && (srchFlag & FindFirstNext.ReturnResumeKey) != 0) {

				// Windows servers only seem to return resume keys for the standard information level

				resumeIds = true;
			}

			// If this is a wildcard search then add the '.' and '..' entries

			if ( wildcardSearch == true && WildCard.isWildcardAll( srchPath) && ReturnDotFiles == true) {

				// Pack the '.' file information

				if ( resumeIds == true) {
					dataBuf.putInt(-1);
					maxLen -= 4;
				}

				lastNameOff = dataBuf.getPosition();
				
				// Check if the search has the '.' file entry details
				
				FileInfo dotInfo = new FileInfo(".", 0, FileAttribute.Directory);
				dotInfo.setFileId(dotInfo.getFileName().hashCode());
				
				if ( ctx.hasDotFiles())
					ctx.getDotInfo( dotInfo);

				packLen = FindInfoPacker.packInfo(dotInfo, dataBuf, infoLevl, tbuf.isUnicode());

				// Update the file count for this packet, update the remaining buffer length

				fileCnt++;
				maxLen -= packLen;

				// Pack the '..' file information

				if ( resumeIds == true) {
					dataBuf.putInt(-2);
					maxLen -= 4;
				}

				lastNameOff = dataBuf.getPosition();
				
				// Check if the search has the '..' file entry details
				
				if ( ctx.hasDotFiles())
					ctx.getDotDotInfo( dotInfo);
				else {
					
					// Set dummy details for the '..' file entry
				
					dotInfo.setFileName("..");
					dotInfo.setFileId(dotInfo.getFileName().hashCode());
					dotInfo.setCreationDateTime(DotFileDateTime);
					dotInfo.setModifyDateTime(DotFileDateTime);
					dotInfo.setAccessDateTime(DotFileDateTime);
				}
				
				packLen = FindInfoPacker.packInfo(dotInfo, dataBuf, infoLevl, tbuf.isUnicode());

				// Update the file count for this packet, update the remaining buffer length

				fileCnt++;
				maxLen -= packLen;
			}

			boolean pktDone = false;
			boolean searchDone = false;

			FileInfo info = new FileInfo();

			while (pktDone == false && fileCnt < maxFiles) {

				// Get file information from the search

				if ( ctx.nextFileInfo(info) == false) {

					// No more files

					pktDone = true;
					searchDone = true;
				}

				// Check if the file information will fit into the return buffer

				else if ( FindInfoPacker.calcInfoSize(info, infoLevl, false, true) <= maxLen) {

					// Pack the resume id, if required

					if ( resumeIds == true) {
						dataBuf.putInt(ctx.getResumeId());
						maxLen -= 4;
					}

					// Save the offset to the last file information structure

					lastNameOff = dataBuf.getPosition();

					// Pack the file information

					packLen = FindInfoPacker.packInfo(info, dataBuf, infoLevl, tbuf.isUnicode());

					// Update the file count for this packet

					fileCnt++;

					// Recalculate the remaining buffer space

					maxLen -= packLen;
				}
				else {

					// Set the search restart point

					ctx.restartAt(info);

					// No more buffer space

					pktDone = true;

					// Debug

		            if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_SEARCH))
		                m_sess.debugPrintln("Find first response full, restart at " + info.getFileName());
				}
			}

			// Check for a single file search and the file was not found, in this case return an
			// error status

			if ( fileCnt == 0)
				throw new FileNotFoundException(srchPath);

			// Check for a search where the maximum files is set to one, close the search
			// immediately.

			if ( maxFiles == 1 && fileCnt == 1)
				searchDone = true;

			// Clear the next structure offset, if applicable

			FindInfoPacker.clearNextOffset(dataBuf, infoLevl, lastNameOff);

			// Pack the parameter block

			paramBuf = replyBuf.getParameterBuffer();

			paramBuf.putShort(searchId);
			paramBuf.putShort(fileCnt);
			paramBuf.putShort(ctx.hasMoreFiles() ? 0 : 1);
			paramBuf.putShort(0);
			paramBuf.putShort(lastNameOff);

			// Send the transaction response

			SMBSrvTransPacket tpkt = new SMBSrvTransPacket(smbPkt);
			tpkt.doTransactionResponse(m_sess, replyBuf, smbPkt);

			// Debug

			if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_SEARCH))
				m_sess.debugPrintln("Search [" + searchId + "] Returned " + fileCnt + " files, dataLen=" + dataBuf.getLength()
						+ ", moreFiles=" + ctx.hasMoreFiles());

			// Check if the search is complete

			if ( searchDone == true || ctx.hasMoreFiles() == false) {

				// Debug

				if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_SEARCH))
					m_sess.debugPrintln("End start search [" + searchId + "] (Search complete)");

				// Release the search context

				vc.deallocateSearchSlot(searchId);
			}
			else if ( (srchFlag & FindFirstNext.CloseSearch) != 0) {

				// Debug

				if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_SEARCH))
					m_sess.debugPrintln("End start search [" + searchId + "] (Close)");

				// Release the search context

				vc.deallocateSearchSlot(searchId);
			}
		}
		catch (FileNotFoundException ex) {

			// Deallocate the search

			if ( searchId != -1)
				vc.deallocateSearchSlot(searchId);

			// Search path does not exist

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTNoSuchFile, SMBStatus.DOSNoMoreFiles, SMBStatus.ErrDos);
		}
		catch (PathNotFoundException ex) {

			// Deallocate the search

			if ( searchId != -1)
				vc.deallocateSearchSlot(searchId);

			// Requested path does not exist

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectPathNotFound, SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
			return;
		}
		catch (InvalidDeviceInterfaceException ex) {

			// Deallocate the search

			if ( searchId != -1)
				vc.deallocateSearchSlot(searchId);

			// Failed to get/initialize the disk interface

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
		}
		catch (UnsupportedInfoLevelException ex) {

			// Deallocate the search

			if ( searchId != -1)
				vc.deallocateSearchSlot(searchId);

			// Requested information level is not supported

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidLevel, SMBStatus.SRVNotSupported, SMBStatus.ErrSrv);
		}
		catch (DiskOfflineException ex) {

			// Deallocate the search

			if ( searchId != -1)
				vc.deallocateSearchSlot(searchId);

			// Filesystem is offline

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectPathNotFound, SMBStatus.HRDDriveNotReady, SMBStatus.ErrHrd);
		}
	}

	/**
	 * Process a transact2 file search continue request.
	 * 
	 * @param tbuf Transaction request details
	 * @param smbPkt SMBSrvPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	protected final void procTrans2FindNext(SrvTransactBuffer tbuf, SMBSrvPacket smbPkt)
		throws IOException, SMBSrvException {

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());

		if ( vc == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Get the tree connection details

		int treeId = smbPkt.getTreeId();
		TreeConnection conn = vc.findConnection(treeId);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Check if the user has the required access permission

		if ( conn.hasReadAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

		// Get the search parameters

		DataBuffer paramBuf = tbuf.getParameterBuffer();

		int searchId = paramBuf.getShort();
		int maxFiles = paramBuf.getShort();
		int infoLevl = paramBuf.getShort();
		int reskey = paramBuf.getInt();
		int srchFlag = paramBuf.getShort();

		String resumeName = paramBuf.getString(tbuf.isUnicode());

		// Access the shared device disk interface

		SearchContext ctx = null;
		DiskInterface disk = null;

		try {

			// Access the disk interface

			disk = (DiskInterface) conn.getSharedDevice().getInterface();

			// Retrieve the search context

			ctx = vc.getSearchContext(searchId);
			if ( ctx == null) {

				// DEBUG

				if ( Debug.EnableError)
					m_sess.debugPrintln("Search context null - [" + searchId + "]");

				// Invalid search handle

				m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSNoMoreFiles, SMBStatus.ErrDos);
				return;
			}

			// Create the reply transaction buffer

			SrvTransactBuffer replyBuf = new SrvTransactBuffer(tbuf);
			DataBuffer dataBuf = replyBuf.getDataBuffer();

			// Determine the maximum return data length

			int maxLen = replyBuf.getReturnDataLimit();

			// Debug

			if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_SEARCH))
				m_sess.debugPrintln("Continue search [" + searchId + "] - " + resumeName + ", maxFiles=" + maxFiles + ", maxLen="
						+ maxLen + ", infoLevel=" + infoLevl + ", flags=0x" + Integer.toHexString(srchFlag));

			// Loop until we have filled the return buffer or there are no more files to return

			int fileCnt = 0;
			int packLen = 0;
			int lastNameOff = 0;

			// Flag to indicate if resume ids should be returned

			boolean resumeIds = false;
			if ( infoLevl == FindInfoPacker.InfoStandard && (srchFlag & FindFirstNext.ReturnResumeKey) != 0) {

				// Windows servers only seem to return resume keys for the standard information
				// level

				resumeIds = true;
			}

			// Flags to indicate packet full or search complete

			boolean pktDone = false;
			boolean searchDone = false;

			FileInfo info = new FileInfo();

			while (pktDone == false && fileCnt < maxFiles) {

				// Get file information from the search

				if ( ctx.nextFileInfo(info) == false) {

					// No more files

					pktDone = true;
					searchDone = true;
				}

				// Check if the file information will fit into the return buffer

				else if ( FindInfoPacker.calcInfoSize(info, infoLevl, false, true) <= maxLen) {

					// Pack the resume id, if required

					if ( resumeIds == true) {
						dataBuf.putInt(ctx.getResumeId());
						maxLen -= 4;
					}

					// Save the offset to the last file information structure

					lastNameOff = dataBuf.getPosition();

					// Pack the file information

					packLen = FindInfoPacker.packInfo(info, dataBuf, infoLevl, tbuf.isUnicode());

					// Update the file count for this packet

					fileCnt++;

					// Recalculate the remaining buffer space

					maxLen -= packLen;
				}
				else {

					// Set the search restart point

					ctx.restartAt(info);

					// No more buffer space

					pktDone = true;

                    // Debug

                    if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_SEARCH))
                        m_sess.debugPrintln("Find next response full, restart at " + info.getFileName());
				}
			}

			// Pack the parameter block

			paramBuf = replyBuf.getParameterBuffer();

			paramBuf.putShort(fileCnt);
			paramBuf.putShort(ctx.hasMoreFiles() ? 0 : 1);
			paramBuf.putShort(0);
			paramBuf.putShort(lastNameOff);

			// Send the transaction response

			SMBSrvTransPacket tpkt = new SMBSrvTransPacket(smbPkt);
			tpkt.doTransactionResponse(m_sess, replyBuf, smbPkt);

			// Debug

			if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_SEARCH))
				m_sess.debugPrintln("Search [" + searchId + "] Returned " + fileCnt + " files, dataLen=" + dataBuf.getLength()
						+ ", moreFiles=" + ctx.hasMoreFiles());

			// Check if the search is complete

			if ( searchDone == true || ctx.hasMoreFiles() == false) {

				// Debug

				if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_SEARCH))
					m_sess.debugPrintln("End start search [" + searchId + "] (Search complete)");

				// Release the search context

				vc.deallocateSearchSlot(searchId);
			}
			else if ( (srchFlag & FindFirstNext.CloseSearch) != 0) {

				// Debug

				if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_SEARCH))
					m_sess.debugPrintln("End start search [" + searchId + "] (Close)");

				// Release the search context

				vc.deallocateSearchSlot(searchId);
			}
		}
		catch (FileNotFoundException ex) {

			// Deallocate the search

			if ( searchId != -1)
				vc.deallocateSearchSlot(searchId);

			// Search path does not exist

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSNoMoreFiles, SMBStatus.ErrDos);
		}
		catch (InvalidDeviceInterfaceException ex) {

			// Deallocate the search

			if ( searchId != -1)
				vc.deallocateSearchSlot(searchId);

			// Failed to get/initialize the disk interface

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
		}
		catch (UnsupportedInfoLevelException ex) {

			// Deallocate the search

			if ( searchId != -1)
				vc.deallocateSearchSlot(searchId);

			// Requested information level is not supported

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.SRVNotSupported, SMBStatus.ErrSrv);
		}
	}

	/**
	 * Process a transact2 file system query request.
	 * 
	 * @param tbuf Transaction request details
	 * @param smbPkt SMBSrvPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	protected final void procTrans2QueryFileSys(SrvTransactBuffer tbuf, SMBSrvPacket smbPkt)
		throws IOException, SMBSrvException {

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());

		if ( vc == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Get the tree connection details

		int treeId = smbPkt.getTreeId();
		TreeConnection conn = vc.findConnection(treeId);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Check if the user has the required access permission

		if ( conn.hasReadAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

		// Get the query file system required information level

		DataBuffer paramBuf = tbuf.getParameterBuffer();

		int infoLevl = paramBuf.getShort();

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_INFO))
			m_sess.debugPrintln("Query File System Info - level = 0x" + Integer.toHexString(infoLevl));

		// Access the shared device disk interface

		try {

			// Access the disk interface and context

			DiskInterface disk = (DiskInterface) conn.getSharedDevice().getInterface();
			DiskDeviceContext diskCtx = (DiskDeviceContext) conn.getContext();

			// Set the return parameter count, so that the data area position can be calculated.

			smbPkt.setParameterCount(10);

			// Pack the disk information into the data area of the transaction reply

			byte[] buf = smbPkt.getBuffer();
			int prmPos = DataPacker.longwordAlign(smbPkt.getByteOffset());
			int dataPos = prmPos; // no parameters returned

			// Create a data buffer using the SMB packet. The response should always fit into a
			// single
			// reply packet.

			DataBuffer replyBuf = new DataBuffer(buf, dataPos, buf.length - dataPos);

			// Determine the information level requested

			SrvDiskInfo diskInfo = null;
			VolumeInfo volInfo = null;

			switch (infoLevl) {

			// Standard disk information

			case DiskInfoPacker.InfoStandard:

				// Get the disk information

				diskInfo = getDiskInformation(disk, diskCtx);

				// Pack the disk information into the return data packet

				DiskInfoPacker.packStandardInfo(diskInfo, replyBuf);
				break;

			// Volume label information

			case DiskInfoPacker.InfoVolume:

				// Get the volume label information

				volInfo = getVolumeInformation(disk, diskCtx);

				// Pack the volume label information

				DiskInfoPacker.packVolumeInfo(volInfo, replyBuf, tbuf.isUnicode());
				break;

			// Full volume information

			case DiskInfoPacker.InfoFsVolume:

				// Get the volume information

				volInfo = getVolumeInformation(disk, diskCtx);

				// Pack the volume information

				DiskInfoPacker.packFsVolumeInformation(volInfo, replyBuf, tbuf.isUnicode());
				break;

			// Filesystem size information

			case DiskInfoPacker.InfoFsSize:

				// Get the disk information

				diskInfo = getDiskInformation(disk, diskCtx);

				// Pack the disk information into the return data packet

				DiskInfoPacker.packFsSizeInformation(diskInfo, replyBuf);
				break;

			// Filesystem device information

			case DiskInfoPacker.InfoFsDevice:
				DiskInfoPacker.packFsDevice(NTIOCtl.DeviceDisk, diskCtx.getDeviceAttributes(), replyBuf);
				break;

			// Filesystem attribute information

			case DiskInfoPacker.InfoFsAttribute:
				String fsType = diskCtx.getFilesystemType();

				if ( disk instanceof NTFSStreamsInterface) {

					// Check if NTFS streams are enabled

					NTFSStreamsInterface ntfsStreams = (NTFSStreamsInterface) disk;
					if ( ntfsStreams.hasStreamsEnabled(m_sess, conn))
						fsType = "NTFS";
				}

				// Pack the filesystem type

				DiskInfoPacker.packFsAttribute(diskCtx.getFilesystemAttributes(), MaxPathLength, fsType, tbuf.isUnicode(),
						replyBuf);
				break;

			// Mac filesystem information

			case DiskInfoPacker.InfoMacFsInfo:

				// Check if the filesystem supports NTFS streams
				//
				// We should only return a valid response to the Macintosh information level if the
				// filesystem does NOT support NTFS streams. By returning an error status the Thursby DAVE
				// software will treat the filesystem as a WinXP/2K filesystem with full streams support.

				boolean ntfs = false;

				if ( disk instanceof NTFSStreamsInterface) {

					// Check if streams are enabled

					NTFSStreamsInterface ntfsStreams = (NTFSStreamsInterface) disk;
					ntfs = ntfsStreams.hasStreamsEnabled(m_sess, conn);
				}

				// If the filesystem does not support NTFS streams then send a valid response.

				if ( ntfs == false) {

					// Get the disk and volume information

					diskInfo = getDiskInformation(disk, diskCtx);
					volInfo = getVolumeInformation(disk, diskCtx);

					// Pack the disk information into the return data packet

					DiskInfoPacker.packMacFsInformation(diskInfo, volInfo, ntfs, replyBuf);
				}
				break;

			// Filesystem size information, including per user allocation limit

			case DiskInfoPacker.InfoFullFsSize:

				// Get the disk information

				diskInfo = getDiskInformation(disk, diskCtx);

				// Check if there is a quota manager configured, if so then get the per user free
				// space
				// from the quota manager.

				long userLimit = -1L;

				if ( diskCtx.hasQuotaManager()) {

					// Get the per user free space from the quota manager

					userLimit = diskCtx.getQuotaManager().getUserFreeSpace(m_sess, conn);
				}

				// If the per user free space is not valid then use the total available free space,
				// else convert
				// to allocation units.

				if ( userLimit != -1L)
					userLimit = userLimit / diskInfo.getUnitSize();
				else
					userLimit = diskInfo.getTotalUnits();

				// Pack the disk information into the return data packet

				DiskInfoPacker.packFullFsSizeInformation(userLimit, diskInfo, replyBuf);
				break;
			}

			// Check if any data was packed, if not then the information level is not supported

			if ( replyBuf.getPosition() == dataPos) {
				m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.SRVNotSupported, SMBStatus.ErrSrv);
				return;
			}

			int bytCnt = replyBuf.getPosition() - smbPkt.getByteOffset();
			replyBuf.setEndOfBuffer();
			int dataLen = replyBuf.getLength();
			SMBSrvTransPacket.initTransactReply(smbPkt, 0, prmPos, dataLen, dataPos);
			smbPkt.setByteCount(bytCnt);

			// Send the transact reply

			m_sess.sendResponseSMB(smbPkt);
		}
		catch (InvalidDeviceInterfaceException ex) {

			// Failed to get/initialize the disk interface

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
		}
	}

	/**
	 * Process a transact2 query path information request.
	 * 
	 * @param tbuf Transaction request details
	 * @param smbPkt SMBSrvPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	protected final void procTrans2QueryPath(SrvTransactBuffer tbuf, SMBSrvPacket smbPkt)
		throws IOException, SMBSrvException {

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());

		if ( vc == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Get the tree connection details

		int treeId = smbPkt.getTreeId();
		TreeConnection conn = vc.findConnection(treeId);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Check if the user has the required access permission

		if ( conn.hasReadAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

		// Get the query path information level and file/directory name

		DataBuffer paramBuf = tbuf.getParameterBuffer();

		int infoLevl = paramBuf.getShort();
		paramBuf.skipBytes(4);

		String path = paramBuf.getString(tbuf.isUnicode());
		if ( path.length() == 0)
			path = FileName.DOS_SEPERATOR_STR;

		// Normalize paths that end with the NTFS data stream name
		
		if ( path.endsWith( FileName.DataStreamName))
			path = path.substring( 0, path.length() - FileName.DataStreamName.length());
		
		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_INFO))
			m_sess.debugPrintln("Query Path - level = 0x" + Integer.toHexString(infoLevl) + ", path = " + path);

		// Access the shared device disk interface

		try {

			// Access the disk interface

			DiskInterface disk = (DiskInterface) conn.getSharedDevice().getInterface();

			// Set the return parameter count, so that the data area position can be calculated.

			smbPkt.setParameterCount(10);

			// Pack the file information into the data area of the transaction reply

			byte[] buf = smbPkt.getBuffer();
			int prmPos = DataPacker.longwordAlign(smbPkt.getByteOffset());
			int dataPos = prmPos + 4;

			// Pack the return parametes, EA error offset

			smbPkt.setPosition(prmPos);
			smbPkt.packWord(0);

			// Create a data buffer for the file information

			DataBuffer replyBuf = new DataBuffer( 256);

			// Check if the virtual filesystem supports streams, and streams are enabled

			boolean streams = false;

			if ( disk instanceof NTFSStreamsInterface) {

				// Check if NTFS streams are enabled

				NTFSStreamsInterface ntfsStreams = (NTFSStreamsInterface) disk;
				streams = ntfsStreams.hasStreamsEnabled(m_sess, conn);
			}

			// Check if the path is for an NTFS stream, return an error if streams are not supported
			// or not enabled

			if ( streams == false && path.indexOf(FileOpenParams.StreamSeparator) != -1) {
				
				// NTFS streams not supported, return an error status

				m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectNameInvalid, SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
				return;
			}

			// Check for the file streams information level

			int dataLen = 0;

			if ( streams == true && (infoLevl == FileInfoLevel.PathFileStreamInfo || infoLevl == FileInfoLevel.NTFileStreamInfo)) {

				// Debug

				if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_STREAMS))
					m_sess.debugPrintln("Get NTFS streams list path=" + path);

				// Get the list of streams from the share driver

				NTFSStreamsInterface ntfsStreams = (NTFSStreamsInterface) disk;
				StreamInfoList streamList = ntfsStreams.getStreamList(m_sess, conn, path);

				if ( streamList == null) {
					m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTNoSuchFile, SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
//					m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectNotFound, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
					return;
				}

				// Pack the file streams information into the return data packet

				dataLen = QueryInfoPacker.packStreamFileInfo(streamList, replyBuf, true);
			}
			else {

				// Get the file information

				FileInfo fileInfo = disk.getFileInformation(m_sess, conn, path);

				if ( fileInfo == null) {
					m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTNoSuchFile, SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
//					m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectNotFound, SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
					return;
				}

				// Pack the file information into the return data packet

				dataLen = QueryInfoPacker.packInfo(fileInfo, replyBuf, infoLevl, true);
			}

			// Check if any data was packed, if not then the information level is not supported

			if ( dataLen == 0) {
				m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
				return;
			}

			// Check if the file information response will fit into the current packet
			
			SMBSrvPacket respPkt = smbPkt;
			SMBSrvTransPacket.initTransactReply( respPkt, 2, prmPos, dataLen, dataPos);
			
			if ( respPkt.getAvailableLength() < (dataLen + 4)) {
				
				// Allocate a new buffer for the response
				
				respPkt = m_sess.getPacketPool().allocatePacket( smbPkt.getByteOffset() + dataLen + 4, smbPkt, smbPkt.getByteOffset());
			}

			// Copy the file information to the response packet
			
			replyBuf.setEndOfBuffer();
			replyBuf.copyData( respPkt.getBuffer(), dataPos);
			
			// Set the byte count
			
			respPkt.setByteCount(( dataPos + dataLen) - respPkt.getByteOffset());

			// Send the transact reply

			m_sess.sendResponseSMB(respPkt);
		}
		catch (FileNotFoundException ex) {

			// Requested file does not exist

//			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectNotFound, SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTNoSuchFile, SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
			return;
		}
		catch (PathNotFoundException ex) {

			// Requested path does not exist

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectPathNotFound, SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
			return;
		}
		catch (InvalidDeviceInterfaceException ex) {

			// Failed to get/initialize the disk interface

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}
		catch (UnsupportedInfoLevelException ex) {

			// Requested information level is not supported

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}
		catch (DiskOfflineException ex) {

			// Filesystem is offline

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectPathNotFound, SMBStatus.HRDDriveNotReady, SMBStatus.ErrHrd);
			return;
		}
		catch (AccessDeniedException ex) {
		    
		    // access denied

		    m_sess.sendErrorResponseSMB(smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
		    return;
		} 
	}

	/**
	 * Process a transact2 query file information (via handle) request.
	 * 
	 * @param tbuf Transaction request details
	 * @param smbPkt SMBSrvPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	protected final void procTrans2QueryFile(SrvTransactBuffer tbuf, SMBSrvPacket smbPkt)
		throws IOException, SMBSrvException {

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());

		if ( vc == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Get the tree connection details

		int treeId = smbPkt.getTreeId();
		TreeConnection conn = vc.findConnection(treeId);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Check if the user has the required access permission

		if ( conn.hasReadAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

		// Get the file id and query path information level

		DataBuffer paramBuf = tbuf.getParameterBuffer();

		int fid = paramBuf.getShort();
		int infoLevl = paramBuf.getShort();

		// Get the file details via the file id

		NetworkFile netFile = conn.findFile(fid);

		if ( netFile == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidHandle, SMBStatus.ErrDos);
			return;
		}

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_INFO))
			m_sess.debugPrintln("Query File - level=0x" + Integer.toHexString(infoLevl) + ", fid=" + fid + ", stream="
					+ netFile.getStreamId() + ", name=" + netFile.getFullName());

		// Access the shared device disk interface

		try {

			// Access the disk interface

			DiskInterface disk = (DiskInterface) conn.getSharedDevice().getInterface();

			// Set the return parameter count, so that the data area position can be calculated.

			smbPkt.setParameterCount(10);

			// Pack the file information into the data area of the transaction reply

			byte[] buf = smbPkt.getBuffer();
			int prmPos = DataPacker.longwordAlign(smbPkt.getByteOffset());
			int dataPos = prmPos + 4;

			// Pack the return parametes, EA error offset

			smbPkt.setPosition(prmPos);
			smbPkt.packWord(0);

			// Check if the virtual filesystem supports streams, and streams are enabled

			boolean streams = false;
			DataBuffer replyBuf = null;

			if ( disk instanceof NTFSStreamsInterface) {

				// Check if NTFS streams are enabled

				NTFSStreamsInterface ntfsStreams = (NTFSStreamsInterface) disk;
				streams = ntfsStreams.hasStreamsEnabled(m_sess, conn);
			}

			// Check for the file streams information level

			int dataLen = 0;

			if ( streams == true && (infoLevl == FileInfoLevel.PathFileStreamInfo || infoLevl == FileInfoLevel.NTFileStreamInfo)) {

				// Debug

				if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_STREAMS))
					m_sess.debugPrintln("Get NTFS streams list fid=" + fid + ", name=" + netFile.getFullName());

				// Get the list of streams from the share driver

				NTFSStreamsInterface ntfsStreams = (NTFSStreamsInterface) disk;
				StreamInfoList streamList = ntfsStreams.getStreamList(m_sess, conn, netFile.getFullName());

				if ( streamList == null) {
					m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectNotFound, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
					return;
				}

				// Allocate a larger response buffer if there is more than one stream to return information for
				
				if ( streamList.numberOfStreams() > 1 && buf.length < NTFSStreamsInfoBufsize) {
					
					// Allocate a larger packet for the response
					
					smbPkt = m_sess.getPacketPool().allocatePacket( NTFSStreamsInfoBufsize, smbPkt, dataPos);
					
					// Switch to the response buffer
					
					buf = smbPkt.getBuffer();
				}
				
				// Create a data buffer using the SMB packet. The response should always fit into a
				// single reply packet.

				replyBuf = new DataBuffer(buf, dataPos, buf.length - dataPos);

				// Pack the file streams information into the return data packet

				dataLen = QueryInfoPacker.packStreamFileInfo(streamList, replyBuf, true);
			}
			else {

				// Get the file information

				FileInfo fileInfo = disk.getFileInformation(m_sess, conn, netFile.getFullName());

				if ( fileInfo == null) {
					m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectNotFound, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
					return;
				}

				// Copy current file size and access date/time from the open file
				
				fileInfo.setFileSize( netFile.getFileSize());
				fileInfo.setAllocationSize((fileInfo.getSize() + 511L) & 0xFFFFFE00L);
				
				if ( netFile.hasAccessDate())
					fileInfo.setAccessDateTime( netFile.getAccessDate());
				
				// Create a data buffer using the SMB packet. The response should always fit into a
				// single reply packet.

				replyBuf = new DataBuffer(buf, dataPos, buf.length - dataPos);

				// Pack the file information into the return data packet

				dataLen = QueryInfoPacker.packInfo(fileInfo, replyBuf, infoLevl, true);
			}

			// Check if any data was packed, if not then the information level is not supported

			if ( dataLen == 0) {
				m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
				return;
			}

			SMBSrvTransPacket.initTransactReply(smbPkt, 2, prmPos, dataLen, dataPos);
			smbPkt.setByteCount(replyBuf.getPosition() - smbPkt.getByteOffset());

			// Send the transact reply

			m_sess.sendResponseSMB(smbPkt);
		}
		catch (FileNotFoundException ex) {

			// Requested file does not exist

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectNotFound, SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
			return;
		}
		catch (PathNotFoundException ex) {

			// Requested path does not exist

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectPathNotFound, SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
			return;
		}
		catch (InvalidDeviceInterfaceException ex) {

			// Failed to get/initialize the disk interface

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}
		catch (UnsupportedInfoLevelException ex) {

			// Requested information level is not supported

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}
		catch (DiskOfflineException ex) {

			// Filesystem is offline

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectPathNotFound, SMBStatus.HRDDriveNotReady, SMBStatus.ErrHrd);
		}
	}

	/**
	 * Process a transact2 set file information (via handle) request.
	 * 
	 * @param tbuf Transaction request details
	 * @param smbPkt SMBSrvPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	protected final void procTrans2SetFile(SrvTransactBuffer tbuf, SMBSrvPacket smbPkt)
		throws IOException, SMBSrvException {

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());

		if ( vc == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Get the tree connection details

		int treeId = smbPkt.getTreeId();
		TreeConnection conn = vc.findConnection(treeId);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Check if the user has the required access permission

		if ( conn.hasWriteAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

		// Get the file id and information level

		DataBuffer paramBuf = tbuf.getParameterBuffer();

		int fid = paramBuf.getShort();
		int infoLevl = paramBuf.getShort();

		// Get the file details via the file id

		NetworkFile netFile = conn.findFile(fid);

		if ( netFile == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidHandle, SMBStatus.ErrDos);
			return;
		}

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_INFO))
			m_sess.debugPrintln("Set File - level=0x" + Integer.toHexString(infoLevl) + ", fid=" + fid + ", name="
					+ netFile.getFullName());

		// Access the shared device disk interface

		try {

			// Access the disk interface

			DiskInterface disk = (DiskInterface) conn.getSharedDevice().getInterface();

			// Process the set file information request

			DataBuffer dataBuf = tbuf.getDataBuffer();
			FileInfo finfo = null;

			switch (infoLevl) {

			// Set basic file information (dates/attributes)

			case FileInfoLevel.SetBasicInfo:

				// Create the file information template

				int setFlags = 0;
				finfo = new FileInfo(netFile.getFullName(), 0, -1);

				// Set the creation date/time, if specified

				long timeNow = System.currentTimeMillis();

				long nttim = dataBuf.getLong();
				boolean hasSetTime = false;

				if ( nttim != 0L) {
					if ( nttim != -1L) {
						finfo.setCreationDateTime(NTTime.toJavaDate(nttim));
						setFlags += FileInfo.SetCreationDate;
					}
					hasSetTime = true;
				}

				// Set the last access date/time, if specified

				nttim = dataBuf.getLong();

				if ( nttim != 0L) {
					if ( nttim != -1L) {
						finfo.setAccessDateTime(NTTime.toJavaDate(nttim));
						setFlags += FileInfo.SetAccessDate;
					}
					else {
						finfo.setAccessDateTime(timeNow);
						setFlags += FileInfo.SetAccessDate;
					}
					hasSetTime = true;
				}

				// Set the last write date/time, if specified

				nttim = dataBuf.getLong();

				if ( nttim > 0L) {
					if ( nttim != -1L) {
						finfo.setModifyDateTime(NTTime.toJavaDate(nttim));
						setFlags += FileInfo.SetModifyDate;
					}
					else {
						finfo.setModifyDateTime(timeNow);
						setFlags += FileInfo.SetModifyDate;
					}
					hasSetTime = true;
				}

				// Set the modify date/time, if specified

				nttim = dataBuf.getLong();

				if ( nttim > 0L) {
					if ( nttim != -1L) {
						finfo.setChangeDateTime(NTTime.toJavaDate(nttim));
						setFlags += FileInfo.SetChangeDate;
					}
					hasSetTime = true;
				}

				// Set the attributes

				int attr = dataBuf.getInt();
				int unknown = dataBuf.getInt();

				if ( hasSetTime == false && unknown == 0) {
					finfo.setFileAttributes(attr);
					setFlags += FileInfo.SetAttributes;
				}

				// Store the associated network file in the file information object
				
				finfo.setNetworkFile(netFile);
				
				// Set the file information for the specified file/directory

				finfo.setFileInformationFlags(setFlags);
				disk.setFileInformation(m_sess, conn, netFile.getFullName(), finfo);

				// Debug

				if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_INFO))
					m_sess.debugPrintln("  Set Basic Info [" + treeId + "] name=" + netFile.getFullName() + ", attr=0x"
							+ Integer.toHexString(attr) + ", setTime=" + hasSetTime + ", setFlags=0x"
							+ Integer.toHexString(setFlags) + ", unknown=" + unknown);
				break;

			// Set end of file position for a file

			case FileInfoLevel.SetEndOfFileInfo:

				// Get the new end of file position

				long eofPos = dataBuf.getLong();

				// Set the new end of file position

				disk.truncateFile(m_sess, conn, netFile, eofPos);

				// Debug

				if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_INFO))
					m_sess.debugPrintln("  Set end of file position fid=" + fid + ", eof=" + eofPos);
				break;

			// Set the allocation size for a file

			case FileInfoLevel.SetAllocationInfo:

				// Get the new end of file position

				long allocSize = dataBuf.getLong();

				// Set the new end of file position

				disk.truncateFile(m_sess, conn, netFile, allocSize);

				// Debug

				if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_INFO))
					m_sess.debugPrintln("  Set allocation size fid=" + fid + ", allocSize=" + allocSize);
				break;

			// Rename a stream

			case FileInfoLevel.NTFileRenameInfo:

				// Check if the virtual filesystem supports streams, and streams are enabled

				boolean streams = false;

				if ( disk instanceof NTFSStreamsInterface) {

					// Check if NTFS streams are enabled

					NTFSStreamsInterface ntfsStreams = (NTFSStreamsInterface) disk;
					streams = ntfsStreams.hasStreamsEnabled(m_sess, conn);
				}

				// If streams are not supported or are not enabled then return an error status

				if ( streams == false) {

					// Return a not supported error status

					m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTNotSupported, SMBStatus.SRVNotSupported, SMBStatus.ErrSrv);
					return;
				}

				// Get the overwrite flag

				boolean overwrite = dataBuf.getByte() == 1 ? true : false;
				dataBuf.skipBytes(3);

				int rootFid = dataBuf.getInt();
				int nameLen = dataBuf.getInt();
				String newName = dataBuf.getString(nameLen, true);

				// Debug

				if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_INFO))
					m_sess.debugPrintln("  Set rename fid=" + fid + ", newName=" + newName + ", overwrite=" + overwrite
							+ ", rootFID=" + rootFid);

				// Check if the new path contains a directory, only rename of a stream on the same
				// file is supported

				if ( newName.indexOf(FileName.DOS_SEPERATOR_STR) != -1) {

					// Return a not supported error status

					m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTNotSupported, SMBStatus.SRVNotSupported, SMBStatus.ErrSrv);
					return;
				}

				// Debug

				if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_STREAMS))
					m_sess.debugPrintln("Rename stream fid=" + fid + ", name=" + netFile.getFullNameStream() + ", newName="
							+ newName + ", overwrite=" + overwrite);

				// Rename the stream

				NTFSStreamsInterface ntfsStreams = (NTFSStreamsInterface) disk;
				ntfsStreams.renameStream(m_sess, conn, netFile.getFullNameStream(), newName, overwrite);
				break;

			// Mark or unmark a file/directory for delete

			case FileInfoLevel.SetDispositionInfo:
			case FileInfoLevel.NTFileDispositionInfo:

				// Get the delete flag

				int flag = dataBuf.getByte();
				boolean delFlag = flag == 1 ? true : false;

				// Call the filesystem driver set file information to see if the file can be marked
				// for
				// delete.

				FileInfo delInfo = new FileInfo();
				delInfo.setDeleteOnClose(delFlag);
				delInfo.setFileInformationFlags(FileInfo.SetDeleteOnClose);

				disk.setFileInformation(m_sess, conn, netFile.getFullName(), delInfo);

				// Mark/unmark the file/directory for deletion

				netFile.setDeleteOnClose(delFlag);

				// Debug

				if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_INFO))
					m_sess.debugPrintln("  Set file disposition fid=" + fid + ", name=" + netFile.getName() + ", delete="
							+ delFlag);
				break;
			}

			// Set the return parameter count, so that the data area position can be calculated.

			smbPkt.setParameterCount(10);

			// Pack the return information into the data area of the transaction reply

			byte[] buf = smbPkt.getBuffer();
			int prmPos = smbPkt.getByteOffset();

			// Longword align the parameters, return an unknown word parameter
			//
			// Note: Make sure the data offset is on a longword boundary, NT has problems if this is
			// not done

			prmPos = DataPacker.longwordAlign(prmPos);
			DataPacker.putIntelShort(0, buf, prmPos);

			SMBSrvTransPacket.initTransactReply(smbPkt, 2, prmPos, 0, prmPos + 4);
			smbPkt.setByteCount((prmPos - smbPkt.getByteOffset()) + 4);

			// Send the transact reply

			m_sess.sendResponseSMB(smbPkt);

			// Check if there are any file/directory change notify requests active

			DiskDeviceContext diskCtx = (DiskDeviceContext) conn.getContext();

			if ( diskCtx.hasFileServerNotifications() && netFile.getFullName() != null) {

				// Get the change handler

				NotifyChangeHandler changeHandler = diskCtx.getChangeHandler();

				// Check for file attributes and last write time changes

				if ( finfo != null) {

					// File attributes changed

					if ( finfo.hasSetFlag(FileInfo.SetAttributes))
						changeHandler.notifyAttributesChanged(netFile.getFullName(), netFile.isDirectory());

					// Last write time changed

					if ( finfo.hasSetFlag(FileInfo.SetModifyDate))
						changeHandler.notifyLastWriteTimeChanged(netFile.getFullName(), netFile.isDirectory());
				}
				else if ( infoLevl == FileInfoLevel.SetAllocationInfo || infoLevl == FileInfoLevel.SetEndOfFileInfo) {

					// File size changed

					changeHandler.notifyFileSizeChanged(netFile.getFullName());
				}
			}
		}
		catch (FileNotFoundException ex) {

			// Requested file does not exist

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectNotFound, SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
		}
		catch (AccessDeniedException ex) {

			// Not allowed to change file attributes/settings

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
		}
		catch (DiskFullException ex) {

			// Disk is full

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTDiskFull, SMBStatus.HRDWriteFault, SMBStatus.ErrHrd);
		}
		catch (InvalidDeviceInterfaceException ex) {

			// Failed to get/initialize the disk interface

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
		}
		catch (DiskOfflineException ex) {

			// Filesystem is offline

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectPathNotFound, SMBStatus.HRDDriveNotReady, SMBStatus.ErrHrd);
		}
		catch (DirectoryNotEmptyException ex) {

			// Directory not empty

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSDirectoryNotEmpty, SMBStatus.ErrDos);
		}
		catch (Exception ex) {

			// Other error during set file

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
		}
	}

	/**
	 * Process a transact2 set path information request.
	 * 
	 * @param tbuf Transaction request details
	 * @param smbPkt SMBSrvPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	protected final void procTrans2SetPath(SrvTransactBuffer tbuf, SMBSrvPacket smbPkt)
		throws IOException, SMBSrvException {

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());

		if ( vc == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Get the tree connection details

		int treeId = smbPkt.getTreeId();
		TreeConnection conn = vc.findConnection(treeId);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Check if the user has the required access permission

		if ( conn.hasWriteAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

		// Get the path and information level

		DataBuffer paramBuf = tbuf.getParameterBuffer();

		int infoLevl = paramBuf.getShort();
		paramBuf.skipBytes(4);

		String path = paramBuf.getString(tbuf.isUnicode());
		if ( path.length() == 0)
			path = FileName.DOS_SEPERATOR_STR;

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_INFO))
			m_sess.debugPrintln("Set Path - path=" + path + ", level=0x" + Integer.toHexString(infoLevl));

        // Check if the file name is valid
        
        if ( isValidPath( path) == false) {
            m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectNameInvalid, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
            return;
        }
        
		// Access the shared device disk interface

		try {

			// Access the disk interface

			DiskInterface disk = (DiskInterface) conn.getSharedDevice().getInterface();

			// Process the set file information request

			DataBuffer dataBuf = tbuf.getDataBuffer();
			FileInfo finfo = null;

			int setFlags = 0;
			int attr = 0;

			switch (infoLevl) {

			// Set standard file information (dates/attributes)

			case FileInfoLevel.SetStandard:

				// Create the file information template

				finfo = new FileInfo(path, 0, -1);

				// Set the creation date/time, if specified

				int smbDate = dataBuf.getShort();
				int smbTime = dataBuf.getShort();

				boolean hasSetTime = false;

				if ( smbDate != 0 && smbTime != 0) {
					finfo.setCreationDateTime(new SMBDate(smbDate, smbTime).getTime());
					setFlags += FileInfo.SetCreationDate;
					hasSetTime = true;
				}

				// Set the last access date/time, if specified

				smbDate = dataBuf.getShort();
				smbTime = dataBuf.getShort();

				if ( smbDate != 0 && smbTime != 0) {
					finfo.setAccessDateTime(new SMBDate(smbDate, smbTime).getTime());
					setFlags += FileInfo.SetAccessDate;
					hasSetTime = true;
				}

				// Set the last write date/time, if specified

				smbDate = dataBuf.getShort();
				smbTime = dataBuf.getShort();

				if ( smbDate != 0 && smbTime != 0) {
					finfo.setModifyDateTime(new SMBDate(smbDate, smbTime).getTime());
					setFlags += FileInfo.SetModifyDate;
					hasSetTime = true;
				}

				// Set the file size/allocation size

				int fileSize = dataBuf.getInt();
				if ( fileSize != 0) {
					finfo.setFileSize(fileSize);
					setFlags += FileInfo.SetFileSize;
				}

				fileSize = dataBuf.getInt();
				if ( fileSize != 0) {
					finfo.setAllocationSize(fileSize);
					setFlags += FileInfo.SetAllocationSize;
				}

				// Set the attributes

				attr = dataBuf.getInt();
				int eaListLen = dataBuf.getInt();

				if ( hasSetTime == false && eaListLen == 0) {
					finfo.setFileAttributes(attr);
					setFlags += FileInfo.SetAttributes;
				}

				// Set the file information for the specified file/directory

				finfo.setFileInformationFlags(setFlags);
				disk.setFileInformation(m_sess, conn, path, finfo);

				// Debug

				if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_INFO))
					m_sess.debugPrintln("  Set Standard Info [" + treeId + "] name=" + path + ", attr=0x"
							+ Integer.toHexString(attr) + ", setTime=" + hasSetTime + ", setFlags=0x"
							+ Integer.toHexString(setFlags) + ", eaListLen=" + eaListLen);
				break;

			// Set basic file information (dates/attributes)

			case FileInfoLevel.SetBasicInfo:

				// Create the file information template

				finfo = new FileInfo(path, 0, -1);

				// Set the creation date/time, if specified

				long dateTime = NTTime.toJavaDate(dataBuf.getLong());

				if ( dateTime != 0L) {
					finfo.setCreationDateTime(dateTime);
					setFlags += FileInfo.SetCreationDate;
				}

				// Set the last access date/time, if specified

				dateTime = NTTime.toJavaDate(dataBuf.getLong());

				if ( dateTime != 0L) {
					finfo.setAccessDateTime(dateTime);
					setFlags += FileInfo.SetAccessDate;
				}

				// Set the last write date/time, if specified

				dateTime = NTTime.toJavaDate(dataBuf.getLong());

				if ( dateTime != 0L) {
					finfo.setModifyDateTime(dateTime);
					setFlags += FileInfo.SetModifyDate;
				}

				// Set the change write date/time, if specified

				dateTime = NTTime.toJavaDate(dataBuf.getLong());

				if ( dateTime != 0L) {
					finfo.setChangeDateTime(dateTime);
					setFlags += FileInfo.SetChangeDate;
				}

				// Set the attributes

				attr = dataBuf.getInt();

				if ( attr != 0) {
					finfo.setFileAttributes(attr);
					setFlags += FileInfo.SetAttributes;
				}

				// Set the file information for the specified file/directory

				finfo.setFileInformationFlags(setFlags);
				disk.setFileInformation(m_sess, conn, path, finfo);

				// Debug

				if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_INFO))
					m_sess.debugPrintln("  Set Basic Info [" + treeId + "] name=" + path + ", attr=0x"
							+ Integer.toHexString(attr) + ", setFlags=0x" + Integer.toHexString(setFlags));
				break;
			}

			// Set the return parameter count, so that the data area position can be calculated.

			smbPkt.setParameterCount(10);

			// Pack the return information into the data area of the transaction reply

			byte[] buf = smbPkt.getBuffer();
			int prmPos = smbPkt.getByteOffset();

			// Longword align the parameters, return an unknown word parameter
			//
			// Note: Make sure the data offset is on a longword boundary, NT has problems if this is
			// not done

			prmPos = DataPacker.longwordAlign(prmPos);
			DataPacker.putIntelShort(0, buf, prmPos);

			SMBSrvTransPacket.initTransactReply(smbPkt, 2, prmPos, 0, prmPos + 4);
			smbPkt.setByteCount((prmPos - smbPkt.getByteOffset()) + 4);

			// Send the transact reply

			m_sess.sendResponseSMB(smbPkt);

			// Check if there are any file/directory change notify requests active

			DiskDeviceContext diskCtx = (DiskDeviceContext) conn.getContext();

			if ( diskCtx.hasFileServerNotifications() && path != null) {

				// Get the change handler

				NotifyChangeHandler changeHandler = diskCtx.getChangeHandler();

				// Check for file attributes and last write time changes

				if ( finfo != null) {

					// Check if the path refers to a file or directory

					int fileSts = disk.fileExists(m_sess, conn, path);

					// File attributes changed

					if ( finfo.hasSetFlag(FileInfo.SetAttributes))
						changeHandler.notifyAttributesChanged(path, fileSts == FileStatus.DirectoryExists ? true : false);

					// Last write time changed

					if ( finfo.hasSetFlag(FileInfo.SetModifyDate))
						changeHandler.notifyLastWriteTimeChanged(path, fileSts == FileStatus.DirectoryExists ? true : false);
				}
			}
		}
		catch (FileNotFoundException ex) {

			// Requested file does not exist

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectNotFound, SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
		}
		catch (AccessDeniedException ex) {

			// Not allowed to change file attributes/settings

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
		}
		catch (DiskFullException ex) {

			// Disk is full

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTDiskFull, SMBStatus.HRDWriteFault, SMBStatus.ErrHrd);
		}
		catch (InvalidDeviceInterfaceException ex) {

			// Failed to get/initialize the disk interface

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
		}
		catch (DiskOfflineException ex) {

			// Filesystem is offline

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectPathNotFound, SMBStatus.HRDDriveNotReady, SMBStatus.ErrHrd);
		}
		catch (Exception ex) {

			// Other error during set file

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
		}
	}

	/**
	 * Process the file write request.
	 * 
	 * @param smbPkt SMBSrvPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	protected final void procWriteAndX(SMBSrvPacket smbPkt)
		throws IOException, SMBSrvException {

		// Check that the received packet looks like a valid write andX request

		if ( smbPkt.checkPacketIsValid(12, 0) == false) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());

		if ( vc == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Get the tree connection details

		int treeId = smbPkt.getTreeId();
		TreeConnection conn = vc.findConnection(treeId);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Check if the user has the required access permission

		if ( conn.hasWriteAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

		// If the connection is to the IPC$ remote admin named pipe pass the request to the IPC
		// handler.

		if ( conn.getSharedDevice().getType() == ShareType.ADMINPIPE) {

			// Use the IPC$ handler to process the request

			IPCHandler.processIPCRequest(m_sess, smbPkt);
			return;
		}

		// Extract the write file parameters

		int fid = smbPkt.getParameter(2);

		// Bottom 32bits of file offset
		
		long offset = (long) (((long) smbPkt.getParameterLong(3)) & 0xFFFFFFFFL);
		int dataPos = smbPkt.getParameter(11) + RFCNetBIOSProtocol.HEADER_LEN;

		int dataLen = smbPkt.getParameter(10);
		int dataLenHigh = 0;

		if ( smbPkt.getReceivedLength() > 0xFFFF)
			dataLenHigh = smbPkt.getParameter(9) & 0x0001;

		if ( dataLenHigh > 0)
			dataLen += (dataLenHigh << 16);

		// Check for the NT format request that has the top 32bits of the file offset

		if ( smbPkt.getParameterCount() == 14) {
			long topOff = (long) (((long) smbPkt.getParameterLong(12)) & 0xFFFFFFFFL);
			offset += topOff << 32;
		}

		NetworkFile netFile = conn.findFile(fid);

		if ( netFile == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidHandle, SMBStatus.ErrDos);
			return;
		}

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_FILEIO))
			m_sess.debugPrintln("File Write AndX [" + netFile.getFileId() + "] : Size=" + dataLen + " ,Pos=" + offset);

		// Write data to the file

		byte[] buf = smbPkt.getBuffer();
		int wrtlen = 0;

		// Access the disk interface and write to the file

		try {

			// Access the disk interface that is associated with the shared device

			DiskInterface disk = (DiskInterface) conn.getSharedDevice().getInterface();

			// Synchronize writes using the network file
			
			synchronized ( netFile) {
				
				// Write to the file
	
				wrtlen = disk.writeFile(m_sess, conn, netFile, buf, dataPos, dataLen, offset);
			}
		}
		catch (InvalidDeviceInterfaceException ex) {

			// Failed to get/initialize the disk interface

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
		}
		catch (AccessDeniedException ex) {

			// Debug

			if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_FILEIO))
				m_sess.debugPrintln("File Write Error [" + netFile.getFileId() + "] : " + ex.toString());

			// Not allowed to write to the file

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}
		catch (LockConflictException ex) {

			// Debug

			if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_LOCK))
				m_sess.debugPrintln("Write Lock Error [" + netFile.getFileId() + "] : Size=" + dataLen + " ,Pos=" + offset);

			// File is locked

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTLockConflict, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}
		catch (DiskFullException ex) {

			// Debug

			if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_FILEIO))
				m_sess.debugPrintln("Write Quota Error [" + netFile.getFileId() + "] Disk full : Size=" + dataLen + " ,Pos="
						+ offset);

			// Disk is full

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTDiskFull, SMBStatus.HRDWriteFault, SMBStatus.ErrHrd);
			return;
		}
		catch (DiskOfflineException ex) {

			// Debug

			if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_FILEIO))
				m_sess.debugPrintln("Filesystem Offline Error [" + netFile.getFileId() + "] Write File");

			// Filesystem is offline

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectPathNotFound, SMBStatus.HRDDriveNotReady, SMBStatus.ErrHrd);
		}
		catch (IOException ex) {

			// Debug

			if ( Debug.EnableError && m_sess.hasDebug(SMBSrvSession.DBG_FILEIO))
				m_sess.debugPrintln("File Write Error [" + netFile.getFileId() + "] : " + ex.toString());

			// Failed to read the file

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.HRDWriteFault, SMBStatus.ErrHrd);
			return;
		}

		// Return the count of bytes actually written

		smbPkt.setSuccessStatus();
		smbPkt.setParameterCount(6);
		smbPkt.setAndXCommand(0xFF);
		smbPkt.setParameter(1, 0); // AndX offset
		smbPkt.setParameter(2, wrtlen);
		smbPkt.setParameter(3, 0xFFFF);

		if ( dataLenHigh > 0) {
			smbPkt.setParameter(4, dataLen >> 16);
			smbPkt.setParameter(5, 0);
		}
		else {
			smbPkt.setParameterLong(4, 0);
		}

		smbPkt.setByteCount(0);
		smbPkt.setParameter(1, smbPkt.getLength());

		// Send the write response

		m_sess.sendResponseSMB(smbPkt);

		// Report file size change notifications every so often
		//
		// We do not report every write due to the increased overhead of change notifications

		DiskDeviceContext diskCtx = (DiskDeviceContext) conn.getContext();

		if ( netFile.getWriteCount() % FileSizeChangeRate == 0 && diskCtx.hasFileServerNotifications() && netFile.getFullName() != null) {

			// Get the change handler

			NotifyChangeHandler changeHandler = diskCtx.getChangeHandler();

			// File size changed

			changeHandler.notifyFileSizeChanged(netFile.getFullName());
		}
	}

	/**
	 * Process the file create/open request.
	 * 
	 * @param smbPkt SMBSrvPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	protected final void procNTCreateAndX(SMBSrvPacket smbPkt)
		throws IOException, SMBSrvException {

		// Check that the received packet looks like a valid NT create andX request

		if ( smbPkt.checkPacketIsValid(24, 1) == false) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());

		if ( vc == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Get the tree connection details

		int treeId = smbPkt.getTreeId();
		TreeConnection conn = vc.findConnection(treeId);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.SRVInvalidTID, SMBStatus.ErrSrv);
			return;
		}

		// Check if the user has the required access permission

		if ( conn.hasReadAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

		// If the connection is to the IPC$ remote admin named pipe pass the request to the IPC
		// handler. If the device is not a disk type device then return an error.

		if ( conn.getSharedDevice().getType() == ShareType.ADMINPIPE) {

			// Use the IPC$ handler to process the request

			IPCHandler.processIPCRequest(m_sess, smbPkt);
			return;
		}
		else if ( conn.getSharedDevice().getType() != ShareType.DISK) {

			// Return an access denied error

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

		// Extract the NT create andX parameters

		NTParameterPacker prms = new NTParameterPacker(smbPkt.getBuffer(), SMBSrvPacket.PARAMWORDS + 5);

		int nameLen = prms.unpackWord();
		int flags = prms.unpackInt();
		int rootFID = prms.unpackInt();
		int accessMask = prms.unpackInt();
		long allocSize = prms.unpackLong();
		int attrib = prms.unpackInt();
		int shrAccess = prms.unpackInt();
		int createDisp = prms.unpackInt();
		int createOptn = prms.unpackInt();
		int impersonLev = prms.unpackInt();
		int secFlags = prms.unpackByte();

		// Extract the filename string

		String fileName = DataPacker.getUnicodeString(smbPkt.getBuffer(), DataPacker.wordAlign(smbPkt.getByteOffset()),
				nameLen / 2);
		if ( fileName == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}

		// Access the disk interface that is associated with the shared device

		DiskInterface disk = null;
		try {

			// Get the disk interface for the share

			disk = (DiskInterface) conn.getSharedDevice().getInterface();
		}
		catch (InvalidDeviceInterfaceException ex) {

			// Failed to get/initialize the disk interface

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
		}

		// Check if the file name contains a file stream name. If the disk interface does not
		// implement the optional NTFS streams interface then return an error status, not supported.

		if ( fileName.indexOf(FileOpenParams.StreamSeparator) != -1) {

			// Check if the driver implements the NTFS streams interface and it is enabled

			boolean streams = false;

			if ( disk instanceof NTFSStreamsInterface) {

				// Check if streams are enabled

				NTFSStreamsInterface ntfsStreams = (NTFSStreamsInterface) disk;
				streams = ntfsStreams.hasStreamsEnabled(m_sess, conn);
			}

			// Check if streams are enabled/available

			if ( streams == false) {

				// Return a file not found error

				m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectNameInvalid, SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
				return;
			}
		}

		// Create the file open parameters to be passed to the disk interface

		FileOpenParams params = new FileOpenParams(fileName, createDisp, accessMask, attrib, shrAccess, allocSize, createOptn,
				rootFID, impersonLev, secFlags, smbPkt.getProcessIdFull());
		
		// Set the create flags, with oplock requests
		
		params.setNTCreateFlags( flags);
		params.setTreeId( treeId);
		params.setSession( m_sess);

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_FILE))
			m_sess.debugPrintln("NT Create AndX [" + treeId + "] params=" + params);

        // Check if the file name is valid
        
        if ( isValidPath( params.getPath()) == false) {
            m_sess.sendErrorResponseSMB(smbPkt, SMBStatus.NTObjectNameInvalid, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
            return;
        }
        
		// Access the disk interface and open the requested file

		int fid;
		NetworkFile netFile = null;
		int respAction = 0;
		LocalOpLockDetails oplock = null;

		try {

			// Check if the requested file already exists

			int fileSts = disk.fileExists(m_sess, conn, params.getFullPath());

            // Check if the file exists and it is a pseudo file, in which case the file already exists so change a create request to
            // an open request
            
            if ( fileSts == FileStatus.FileExists) {
                
                // Check for a pseudo file
                
                FileInfo finfo = disk.getFileInformation(m_sess, conn, params.getFullPath());
                if ( finfo != null && finfo.isPseudoFile()) {
                    createDisp = FileAction.NTOpen;

	                // Debug
	
	                if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_FILE))
	                    m_sess.debugPrintln("Converted create to open for pseudo file " + params.getFullPath());
                }
            }

            // Check if the file should be created
            
			if ( fileSts == FileStatus.NotExist) {

				// Check if the file should be created if it does not exist

				if ( createDisp == FileAction.NTCreate || createDisp == FileAction.NTOpenIf
						|| createDisp == FileAction.NTOverwriteIf || createDisp == FileAction.NTSupersede) {

					// Check if the user has the required access permission

					if ( conn.hasWriteAccess() == false) {

						// User does not have the required access rights

						m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
						return;
					}

					// Check if a new file or directory should be created

					if ( (createOptn & WinNT.CreateDirectory) == 0) {

						// Create a new file

						netFile = disk.createFile(m_sess, conn, params);
						
						// Indicate the file was created
						
						if ( netFile != null && m_sess.hasDebug( SMBSrvSession.DBG_BENCHMARK)) {
							netFile.setStatusFlag( NetworkFile.Created, true);
							netFile.setCreationDate( System.currentTimeMillis());
						}
						
						// Check if an oplock was requested, grant the oplock if possible and return the granted oplock details, or null
						// if no oplock granted or requested.
						
						oplock = grantOpLock( m_sess, smbPkt, disk, conn, params, netFile);
					}
					else {

						// Split the path and walk to see which folder(s) need creating
						
						String[] paths = FileName.splitAllPaths( params.getPath());
						StringBuilder pathStr = new StringBuilder( params.getPath().length());
						int fldrSts = FileStatus.Unknown;
						int idx = 0;
						
						while ( idx < paths.length) {
						
							// Add the current path component and check if it exists, and it is a folder
							
							pathStr.append( FileName.DOS_SEPERATOR_STR);
							pathStr.append( paths[ idx++]);

							fldrSts = disk.fileExists( m_sess, conn, pathStr.toString());
							
							// If the current path exists and it is a file then return an error
							
							if ( fldrSts == FileStatus.FileExists) {
								if ( idx < paths.length)
									m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectNameCollision, SMBStatus.DOSFileAlreadyExists, SMBStatus.ErrDos);
								else
									m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectPathNotFound, SMBStatus.DOSDirectoryInvalid, SMBStatus.ErrDos);
								return;
							}
							else if ( fldrSts == FileStatus.NotExist) {
								
								// Create the current part of the path
								
								FileOpenParams fldrParams = new FileOpenParams( pathStr.toString(), createDisp, accessMask, attrib, shrAccess, allocSize, createOptn,
										rootFID, impersonLev, secFlags, smbPkt.getProcessIdFull());
								disk.createDirectory( m_sess, conn, fldrParams);
							}
						}
						
						// Open the requested folder, should now exist

						netFile = disk.openFile(m_sess, conn, params);
						
						// Indicate the directory was created
						
						if ( netFile != null && m_sess.hasDebug( SMBSrvSession.DBG_BENCHMARK)) {
							netFile.setStatusFlag( NetworkFile.Created, true);
							netFile.setCreationDate( System.currentTimeMillis());
						}
					}

					// Check if the delete on close option is set

					if ( netFile != null && (createOptn & WinNT.CreateDeleteOnClose) != 0)
						netFile.setDeleteOnClose(true);

					// Indicate that the file did not exist and was created

					respAction = FileAction.FileCreated;
				}
				else {

					// Check if the path is a directory

					if ( fileSts == FileStatus.DirectoryExists) {

						// Return an access denied error

						m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectNameCollision, SMBStatus.DOSFileAlreadyExists,
								SMBStatus.ErrDos);
						return;
					}
					else {

						// Return a file not found error

						m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectNotFound, SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
						return;
					}
				}
			}
			else if ( createDisp == FileAction.NTCreate) {

				// Check for a file or directory

				if ( fileSts == FileStatus.FileExists || fileSts == FileStatus.DirectoryExists) {

					// Return a file exists error

					m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectNameCollision, SMBStatus.DOSFileAlreadyExists, SMBStatus.ErrDos);
					return;
				}
				else {

					// Return an access denied exception

					m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
					return;
				}
			}
			else {

				// Check if the open should be a file, not a directory

				if ( (createOptn & WinNT.CreateNonDirectory) != 0 && fileSts == FileStatus.DirectoryExists) {

					// Return a file is a directory error

					m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTFileIsADirectory, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
					return;
				}

				// Check if the filesystem supports oplocks, check if there is an oplock on the file
				
				checkOpLock( m_sess, smbPkt, disk, params, conn);
				
				// Open the requested file/directory

				netFile = disk.openFile(m_sess, conn, params);

				// Check if an oplock was requested, grant the oplock if possible and return the granted oplock details, or null
				// if no oplock granted or requested.
				
				oplock = grantOpLock( m_sess, smbPkt, disk, conn, params, netFile);
				
				// Check if the file should be truncated

				if ( createDisp == FileAction.NTSupersede || createDisp == FileAction.NTOverwriteIf) {

					// Truncate the file

					disk.truncateFile(m_sess, conn, netFile, 0L);

					// Debug

					if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_FILE))
						m_sess.debugPrintln("  [" + treeId + "] name=" + fileName + " truncated");
					
					// Treat the file as if it is a newly created file
					
					if ( netFile != null && m_sess.hasDebug( SMBSrvSession.DBG_BENCHMARK)) {
						netFile.setStatusFlag( NetworkFile.Created, true);
						netFile.setCreationDate( System.currentTimeMillis());
					}
					
				}

				// Set the file action response

				respAction = FileAction.FileExisted;
			}

			// Add the file to the list of open files for this tree connection

			fid = conn.addFile(netFile, getSession());
			
			// If the file has been granted an oplock then update the file id, needed for the oplock break
			
			if ( oplock != null && oplock.getLockType() != OpLock.TypeNone)
				oplock.setOwnerFileId( fid);
			
			// DEBUG
			
			if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_FILE))
				m_sess.debugPrintln("  [" + treeId + "] name=" + fileName + " fid=" + fid + ", fileId=" + netFile.getFileId() + ", opLock=" + oplock);
		}
		catch (TooManyFilesException ex) {

			// Too many files are open on this connection, cannot open any more files.

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTTooManyOpenFiles, SMBStatus.DOSTooManyOpenFiles, SMBStatus.ErrDos);
			return;
		}
		catch (AccessDeniedException ex) {

			// Return an access denied error

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}
		catch (FileExistsException ex) {

			// File/directory already exists

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectNameCollision, SMBStatus.DOSFileAlreadyExists, SMBStatus.ErrDos);
			return;
		}
		catch (FileSharingException ex) {

			// Return a sharing violation error

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTSharingViolation, SMBStatus.DOSFileSharingConflict, SMBStatus.ErrDos);
			return;
		}
		catch (FileOfflineException ex) {

			// File data is unavailable

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTFileOffline, SMBStatus.HRDDriveNotReady, SMBStatus.ErrHrd);
			return;
		}
		catch (FileNameException ex) {

			// File name too long or contains invalid characters

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectNameInvalid, SMBStatus.DOSInvalidFormat, SMBStatus.ErrDos);
			return;
		}
		catch (DiskOfflineException ex) {

			// Filesystem is offline

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectPathNotFound, SMBStatus.HRDDriveNotReady, SMBStatus.ErrHrd);
			return;
		}
		catch (DiskFullException ex) {

			// Disk is full

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTDiskFull, SMBStatus.HRDWriteFault, SMBStatus.ErrHrd);
			return;
		}
		catch (DeferredPacketException ex) {
			
			// Deferred packet, oplock break in progress, rethrow the exception
			
			throw ex;
		}
		catch (IOException ex) {

			// Failed to open the file

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectNotFound, SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
			return;
		}

		// Build the NT create andX response

		boolean extendedResponse = params.requestExtendedResponse();
		smbPkt.setParameterCount( extendedResponse ? 42 : 34);

		smbPkt.setAndXCommand(0xFF);
		smbPkt.setParameter(1, 0); // AndX offset

		prms.reset(smbPkt.getBuffer(), SMBSrvPacket.PARAMWORDS + 4);

		// Pack the oplock type, if granted

		if (oplock != null)
			prms.packByte( oplock.getLockType());
		else
			prms.packByte(0);

		// Pack the file id

		prms.packWord(fid);
		prms.packInt(respAction);

		// Pack the file/directory dates
		//
		// Creation
		// Access
		// Modify
		// Change

		if ( netFile.hasCreationDate())
			prms.packLong(NTTime.toNTTime(netFile.getCreationDate()));
		else
			prms.packLong(0);

		if ( netFile.hasAccessDate())
			prms.packLong(NTTime.toNTTime(netFile.getAccessDate()));
		else {
			
			// Use the modify date/time if access ate/time has not been set

			if ( netFile.hasModifyDate())
				prms.packLong(NTTime.toNTTime(netFile.getModifyDate()));
			else
				prms.packLong(0);
		}
		
		if ( netFile.hasModifyDate()) {
			long modDate = NTTime.toNTTime(netFile.getModifyDate());
			prms.packLong(modDate);
			prms.packLong(modDate);
		}
		else {
			prms.packLong(0); // Last write time
			prms.packLong(0); // Change time
		}

		prms.packInt(netFile.getFileAttributes());

		// Pack the file size/allocation size

		long fileSize = netFile.getFileSize();
		if ( fileSize > 0L)
			fileSize = (fileSize + 512L) & 0xFFFFFFFFFFFFFE00L;

		prms.packLong(fileSize); 	// Allocation size
		prms.packLong(netFile.getFileSize()); // End of file
		prms.packWord(0); 			// File type - disk file
		prms.packWord( extendedResponse ? 7 : 0); // Device state
		prms.packByte(netFile.isDirectory() ? 1 : 0);

		prms.packWord(0); // byte count = 0
		
		// Pack the extra extended response area, if requested
		
		if ( extendedResponse == true) {
			
			// 22 byte block of zeroes
			
			prms.packLong( 0);
			prms.packLong( 0);
			prms.packInt( 0);
			prms.packWord( 0);
			
			// Pack the permissions
			
			if ( netFile.isDirectory() || netFile.getAllowedAccess() == NetworkFile.READWRITE)
				prms.packInt( AccessMode.NTFileGenericAll);
			else
				prms.packInt( AccessMode.NTFileGenericRead);
			
			// 8 byte block of zeroes
			
			prms.packInt( 0);
			prms.packInt( 0);
		}

		// Set the AndX offset

		int endPos = prms.getPosition();
		smbPkt.setParameter(1, endPos - RFCNetBIOSProtocol.HEADER_LEN);

		// Set the status
		
		smbPkt.setLongErrorCode( SMBStatus.NTSuccess);
		
		// Check if there is a chained request

		if ( smbPkt.hasAndXCommand()) {

			// Process the chained requests

			endPos = procAndXCommands(smbPkt, netFile);
		}

		// Send the response packet

		m_sess.sendResponseSMB(smbPkt, endPos - RFCNetBIOSProtocol.HEADER_LEN);

		// Check if there are any file/directory change notify requests active

		DiskDeviceContext diskCtx = (DiskDeviceContext) conn.getContext();
		if ( diskCtx.hasFileServerNotifications() && respAction == FileAction.FileCreated) {

			// Check if a file or directory has been created

			if ( netFile.isDirectory())
				diskCtx.getChangeHandler().notifyDirectoryChanged(NotifyChange.ActionAdded, fileName);
			else
				diskCtx.getChangeHandler().notifyFileChanged(NotifyChange.ActionAdded, fileName);
		}
	}

	/**
	 * Process the cancel request.
	 * 
	 * @param smbPkt SMBSrvPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	protected final void procNTCancel(SMBSrvPacket smbPkt)
		throws IOException, SMBSrvException {

		// Check that the received packet looks like a valid NT cancel request

		if ( smbPkt.checkPacketIsValid(0, 0) == false) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());

		if ( vc == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Get the tree connection details

		int treeId = smbPkt.getTreeId();
		TreeConnection conn = vc.findConnection(treeId);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Find the matching notify request and remove it

		NotifyRequest req = m_sess.findNotifyRequest(smbPkt.getMultiplexId(), smbPkt.getTreeId(), smbPkt.getUserId(),
				smbPkt.getProcessId());
		if ( req != null) {

			// Remove the request

			m_sess.removeNotifyRequest(req);

			// Return a cancelled status

			smbPkt.setParameterCount(0);
			smbPkt.setByteCount(0);

			// Enable the long error status flag

			if ( smbPkt.isLongErrorCode() == false)
				smbPkt.setFlags2(smbPkt.getFlags2() + SMBSrvPacket.FLG2_LONGERRORCODE);

			// Set the NT status code

			smbPkt.setLongErrorCode(SMBStatus.NTCancelled);

			// Set the Unicode strings flag

			if ( smbPkt.isUnicode() == false)
				smbPkt.setFlags2(smbPkt.getFlags2() + SMBSrvPacket.FLG2_UNICODE);

			// Return the error response to the client

			m_sess.sendResponseSMB(smbPkt);

			// Debug

			if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_NOTIFY)) {
				DiskDeviceContext diskCtx = (DiskDeviceContext) conn.getContext();
				m_sess.debugPrintln("NT Cancel notify mid=" + req.getMultiplexId() + ", dir=" + req.getWatchPath() + ", queue="
						+ diskCtx.getChangeHandler().getRequestQueueSize());
			}
		}
		else {

			// Nothing to cancel

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
		}
	}

	/**
	 * Process an NT transaction
	 * 
	 * @param smbPkt SMBSrvPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	protected final void procNTTransaction(SMBSrvPacket smbPkt)
		throws IOException, SMBSrvException {

		// Check that we received enough parameters for a transact2 request

		if ( smbPkt.checkPacketIsValid(19, 0) == false) {

			// Not enough parameters for a valid transact2 request

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());

		if ( vc == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Get the tree id from the received packet and validate that it is a valid
		// connection id.

		int treeId = smbPkt.getTreeId();
		TreeConnection conn = vc.findConnection(treeId);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Check if the user has the required access permission

		if ( conn.hasReadAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

		// Check if the transaction request is for the IPC$ pipe

		if ( conn.getSharedDevice().getType() == ShareType.ADMINPIPE) {
			IPCHandler.processIPCRequest(m_sess, smbPkt);
			return;
		}

		// Create an NT transaction using the received packet

		NTTransPacket ntTrans = new NTTransPacket(smbPkt.getBuffer());
		int subCmd = ntTrans.getNTFunction();

		// Check for a notfy change request, this needs special processing

		if ( subCmd == PacketType.NTTransNotifyChange) {

			// Handle the notify change setup request

			procNTTransactNotifyChange(ntTrans, smbPkt);
			return;
		}

		// Create a transact buffer to hold the transaction parameter block and data block

		SrvTransactBuffer transBuf = null;

		if ( ntTrans.getTotalParameterCount() == ntTrans.getParameterBlockCount()
				&& ntTrans.getTotalDataCount() == ntTrans.getDataBlockCount()) {

			// Create a transact buffer using the packet buffer, the entire request is contained in
			// a single packet

			transBuf = new SrvTransactBuffer(ntTrans);
		}
		else {

			// Create a transact buffer to hold the multiple transact request parameter/data blocks

			transBuf = new SrvTransactBuffer(ntTrans.getSetupCount(), ntTrans.getTotalParameterCount(), ntTrans.getTotalDataCount());
			transBuf.setType(ntTrans.getCommand());
			transBuf.setFunction(subCmd);

			// Debug

			if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_TRAN))
				m_sess.debugPrintln("NT Transaction [" + treeId + "] transbuf=" + transBuf);

			// Append the setup, parameter and data blocks to the transaction data

			byte[] buf = ntTrans.getBuffer();
			int cnt = ntTrans.getSetupCount();

			if ( cnt > 0)
				transBuf.appendSetup(buf, ntTrans.getSetupOffset(), cnt * 2);

			// Debug

			if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_TRAN))
				m_sess.debugPrintln("NT Transaction [" + treeId + "] pcnt=" + ntTrans.getNTParameter(4) + ", offset="
						+ ntTrans.getNTParameter(5));

			cnt = ntTrans.getParameterBlockCount();

			if ( cnt > 0)
				transBuf.appendParameter(buf, ntTrans.getParameterBlockOffset(), cnt);

			cnt = ntTrans.getDataBlockCount();
			if ( cnt > 0)
				transBuf.appendData(buf, ntTrans.getDataBlockOffset(), cnt);
		}

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_TRAN))
			m_sess.debugPrintln("NT Transaction [" + treeId + "] cmd=0x" + Integer.toHexString(subCmd) + ", multiPkt="
					+ transBuf.isMultiPacket());

		// Check for a multi-packet transaction, for a multi-packet transaction we just acknowledge
		// the receive with an empty response SMB

		if ( transBuf.isMultiPacket()) {

			// Save the partial transaction data

			vc.setTransaction(transBuf);

			// Send an intermediate acknowedgement response

			m_sess.sendSuccessResponseSMB( smbPkt);
			return;
		}

		// Process the transaction buffer

		processNTTransactionBuffer(transBuf, ntTrans);
	}

	/**
	 * Process an NT transaction secondary packet
	 * 
	 * @param smbPkt SMBSrvPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	protected final void procNTTransactionSecondary(SMBSrvPacket smbPkt)
		throws IOException, SMBSrvException {

		// Check that we received enough parameters for a transact2 request

		if ( smbPkt.checkPacketIsValid(18, 0) == false) {

			// Not enough parameters for a valid transact2 request

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());

		if ( vc == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Get the tree id from the received packet and validate that it is a valid
		// connection id.

		int treeId = smbPkt.getTreeId();
		TreeConnection conn = vc.findConnection(treeId);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Check if the user has the required access permission

		if ( conn.hasReadAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

		// Check if the transaction request is for the IPC$ pipe

		if ( conn.getSharedDevice().getType() == ShareType.ADMINPIPE) {
			IPCHandler.processIPCRequest(m_sess, smbPkt);
			return;
		}

		// Check if there is an active transaction, and it is an NT transaction

		if ( vc.hasTransaction() == false || vc.getTransaction().isType() != PacketType.NTTransact) {

			// No NT transaction to continue, return an error

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}

		// Create an NT transaction using the received packet

		NTTransPacket ntTrans = new NTTransPacket(smbPkt.getBuffer());
		byte[] buf = ntTrans.getBuffer();
		SrvTransactBuffer transBuf = vc.getTransaction();

		// Append the parameter data to the transaction buffer, if any

		int plen = ntTrans.getParameterBlockCount();
		if ( plen > 0) {

			// Append the data to the parameter buffer

			DataBuffer paramBuf = transBuf.getParameterBuffer();
			paramBuf.appendData(buf, ntTrans.getParameterBlockOffset(), plen);
		}

		// Append the data block to the transaction buffer, if any

		int dlen = ntTrans.getDataBlockCount();
		if ( dlen > 0) {

			// Append the data to the data buffer

			DataBuffer dataBuf = transBuf.getDataBuffer();
			dataBuf.appendData(buf, ntTrans.getDataBlockOffset(), dlen);
		}

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_TRAN))
			m_sess.debugPrintln("NT Transaction Secondary [" + treeId + "] paramLen=" + plen + ", dataLen=" + dlen);

		// Check if the transaction has been received or there are more sections to be received

		int totParam = ntTrans.getTotalParameterCount();
		int totData = ntTrans.getTotalDataCount();

		int paramDisp = ntTrans.getParameterBlockDisplacement();
		int dataDisp = ntTrans.getDataBlockDisplacement();

		if ( (paramDisp + plen) == totParam && (dataDisp + dlen) == totData) {

			// Debug

			if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_TRAN))
				m_sess.debugPrintln("NT Transaction complete, processing ...");

			// Clear the in progress transaction

			vc.setTransaction(null);

			// Process the transaction

			processNTTransactionBuffer(transBuf, ntTrans);
		}

		// No response is sent for a transaction secondary
	}

	/**
	 * Process an NT transaction buffer
	 * 
	 * @param tbuf TransactBuffer
	 * @param smbPkt NTTransPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	private final void processNTTransactionBuffer(SrvTransactBuffer tbuf, NTTransPacket smbPkt)
		throws IOException, SMBSrvException {

		// Process the NT transaction buffer

		switch (tbuf.getFunction()) {

			// Create file/directory
	
			case PacketType.NTTransCreate:
				procNTTransactCreate(tbuf, smbPkt);
				break;
	
			// I/O control
	
			case PacketType.NTTransIOCtl:
				procNTTransactIOCtl(tbuf, smbPkt);
				break;
	
			// Query security descriptor
	
			case PacketType.NTTransQuerySecurityDesc:
				procNTTransactQuerySecurityDesc(tbuf, smbPkt);
				break;
	
			// Set security descriptor
	
			case PacketType.NTTransSetSecurityDesc:
				procNTTransactSetSecurityDesc(tbuf, smbPkt);
				break;
	
			// Rename file/directory via handle
	
			case PacketType.NTTransRename:
				procNTTransactRename(tbuf, smbPkt);
				break;
	
			// Get user quota
	
			case PacketType.NTTransGetUserQuota:
	
				// DEBUG
	
				if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_TRAN))
					m_sess.debugPrintln("NT GetUserQuota transaction");
	
				// Return a not implemented error status
	
				m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTNotImplemented, SMBStatus.SRVNotSupported, SMBStatus.ErrSrv);
				break;
	
			// Set user quota
	
			case PacketType.NTTransSetUserQuota:
	
				// DEBUG
	
				if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_TRAN))
					m_sess.debugPrintln("NT SetUserQuota transaction");
	
				// Return a not implemented error status
	
				m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTNotImplemented, SMBStatus.SRVNotSupported, SMBStatus.ErrSrv);
				break;
	
			// Unknown NT transaction command
	
			default:
	
				// Return an unrecognized command error
	
				if ( Debug.EnableError)
					m_sess.debugPrintln("NT Error unknown NT transact command = 0x" + Integer.toHexString(tbuf.isType()));
				m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
				break;
		}
	}

	/**
	 * Process an NT create file/directory transaction
	 * 
	 * @param tbuf TransactBuffer
	 * @param smbPkt NTTransPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	protected final void procNTTransactCreate(SrvTransactBuffer tbuf, NTTransPacket smbPkt)
		throws IOException, SMBSrvException {

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_TRAN))
			m_sess.debugPrintln("NT TransactCreate");

		// Check that the received packet looks like a valid NT create transaction

		if ( tbuf.hasParameterBuffer() && tbuf.getParameterBuffer().getLength() < 52) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());

		if ( vc == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Get the tree connection details

		int treeId = tbuf.getTreeId();
		TreeConnection conn = vc.findConnection(treeId);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Check if the user has the required access permission

		if ( conn.hasWriteAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

		// If the connection is not a disk share then return an error.

		if ( conn.getSharedDevice().getType() != ShareType.DISK) {

			// Return an access denied error

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

		// Extract the file create parameters

		DataBuffer tparams = tbuf.getParameterBuffer();

		int flags = tparams.getInt();
		int rootFID = tparams.getInt();
		int accessMask = tparams.getInt();
		long allocSize = tparams.getLong();
		int attrib = tparams.getInt();
		int shrAccess = tparams.getInt();
		int createDisp = tparams.getInt();
		int createOptn = tparams.getInt();
		int sdLen = tparams.getInt();
		int eaLen = tparams.getInt();
		int nameLen = tparams.getInt();
		int impersonLev = tparams.getInt();
		int secFlags = tparams.getByte();

		// Extract the filename string

		tparams.wordAlign();
		String fileName = tparams.getString(nameLen, true);

		if ( fileName == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}

		// Access the disk interface that is associated with the shared device

		DiskInterface disk = null;
		try {

			// Get the disk interface for the share

			disk = (DiskInterface) conn.getSharedDevice().getInterface();
		}
		catch (InvalidDeviceInterfaceException ex) {

			// Failed to get/initialize the disk interface

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
		}

		// Check if the file name contains a file stream name. If the disk interface does not
		// implement the optional NTFS
		// streams interface then return an error status, not supported.

		if ( fileName.indexOf(FileOpenParams.StreamSeparator) != -1) {

			// Check if the driver implements the NTFS streams interface and it is enabled

			boolean streams = false;

			if ( disk instanceof NTFSStreamsInterface) {

				// Check if streams are enabled

				NTFSStreamsInterface ntfsStreams = (NTFSStreamsInterface) disk;
				streams = ntfsStreams.hasStreamsEnabled(m_sess, conn);
			}

			// Check if streams are enabled/available

			if ( streams == false) {

				// Return a file not found error

				m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectNotFound, SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
				return;
			}
		}

		// Create the file open parameters to be passed to the disk interface

		FileOpenParams params = new FileOpenParams(fileName, createDisp, accessMask, attrib, shrAccess, allocSize, createOptn,
				rootFID, impersonLev, secFlags, smbPkt.getProcessIdFull());
		
		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_FILE)) {
			m_sess.debugPrintln("NT TransactCreate [" + treeId + "] params=" + params);
			m_sess.debugPrintln("  secDescLen=" + sdLen + ", extAttribLen=" + eaLen);
		}

		// Access the disk interface and open/create the requested file

		int fid;
		NetworkFile netFile = null;
		int respAction = 0;

		try {

			// Check if the requested file already exists

			int fileSts = disk.fileExists(m_sess, conn, fileName);

			if ( fileSts == FileStatus.NotExist) {

				// Check if the file should be created if it does not exist

				if ( createDisp == FileAction.NTCreate || createDisp == FileAction.NTOpenIf
						|| createDisp == FileAction.NTOverwriteIf || createDisp == FileAction.NTSupersede) {

					// Check if a new file or directory should be created

					if ( (createOptn & WinNT.CreateDirectory) == 0) {

						// Create a new file

						netFile = disk.createFile(m_sess, conn, params);
					}
					else {

						// Create a new directory and open it

						disk.createDirectory(m_sess, conn, params);
						netFile = disk.openFile(m_sess, conn, params);
					}

					// Indicate that the file did not exist and was created

					respAction = FileAction.FileCreated;
				}
				else {

					// Check if the path is a directory

					if ( fileSts == FileStatus.DirectoryExists) {

						// Return an access denied error

						m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectNameCollision, SMBStatus.DOSFileAlreadyExists,
								SMBStatus.ErrDos);
						return;
					}
					else {

						// Return a file not found error

						m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectNotFound, SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
						return;
					}
				}
			}
			else if ( createDisp == FileAction.NTCreate) {

				// Check for a file or directory

				if ( fileSts == FileStatus.FileExists || fileSts == FileStatus.DirectoryExists) {

					// Return a file exists error

					m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectNameCollision, SMBStatus.DOSFileAlreadyExists, SMBStatus.ErrDos);
					return;
				}
				else {

					// Return an access denied exception

					m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
					return;
				}
			}
			else {

				// Open the requested file/directory

				netFile = disk.openFile(m_sess, conn, params);

				// Check if the file should be truncated

				if ( createDisp == FileAction.NTSupersede || createDisp == FileAction.NTOverwriteIf) {

					// Truncate the file

					disk.truncateFile(m_sess, conn, netFile, 0L);

					// Debug

					if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_FILE))
						m_sess.debugPrintln("  [" + treeId + "] name=" + fileName + " truncated");
				}

				// Set the file action response

				respAction = FileAction.FileExisted;
			}

			// Add the file to the list of open files for this tree connection

			fid = conn.addFile(netFile, getSession());
		}
		catch (TooManyFilesException ex) {

			// Too many files are open on this connection, cannot open any more files.

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTTooManyOpenFiles, SMBStatus.DOSTooManyOpenFiles, SMBStatus.ErrDos);
			return;
		}
		catch (AccessDeniedException ex) {

			// Return an access denied error

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}
		catch (FileExistsException ex) {

			// File/directory already exists

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectNameCollision, SMBStatus.DOSFileAlreadyExists, SMBStatus.ErrDos);
			return;
		}
		catch (FileSharingException ex) {

			// Return a sharing violation error

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTSharingViolation, SMBStatus.DOSFileSharingConflict, SMBStatus.ErrDos);
			return;
		}
		catch (FileOfflineException ex) {

			// File data is unavailable

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTFileOffline, SMBStatus.HRDDriveNotReady, SMBStatus.ErrHrd);
			return;
		}
		catch (DiskOfflineException ex) {

			// Filesystem is offline

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectPathNotFound, SMBStatus.HRDDriveNotReady, SMBStatus.ErrHrd);
			return;
		}
		catch (IOException ex) {

			// Failed to open the file

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectNotFound, SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
			return;
		}

		// Build the NT transaction create response

		DataBuffer prms = new DataBuffer(128);

		// If an oplock was requested indicate it was granted, for now

		if ( (flags & WinNT.RequestBatchOplock) != 0) {

			// Batch oplock granted

			prms.putByte(2);
		}
		else if ( (flags & WinNT.RequestExclusiveOplock) != 0) {

			// Exclusive oplock granted

			prms.putByte(1);
		}
		else {

			// No oplock granted

			prms.putByte(0);
		}
		prms.putByte(0); // alignment

		// Pack the file id

		prms.putShort(fid);
		prms.putInt(respAction);

		// EA error offset

		prms.putInt(0);

		// Pack the file/directory dates

		if ( netFile.hasCreationDate())
			prms.putLong(NTTime.toNTTime(netFile.getCreationDate()));
		else
			prms.putLong(0);

		if ( netFile.hasModifyDate()) {
			long modDate = NTTime.toNTTime(netFile.getModifyDate());
			prms.putLong(modDate);
			prms.putLong(modDate);
			prms.putLong(modDate);
		}
		else {
			prms.putLong(0); // Last access time
			prms.putLong(0); // Last write time
			prms.putLong(0); // Change time
		}

		prms.putInt(netFile.getFileAttributes());

		// Pack the file size/allocation size

		prms.putLong(netFile.getFileSize()); // Allocation size
		prms.putLong(netFile.getFileSize()); // End of file
		prms.putShort(0); // File type - disk file
		prms.putShort(0); // Device state
		prms.putByte(netFile.isDirectory() ? 1 : 0);

		// Initialize the transaction response

		smbPkt.initTransactReply(prms.getBuffer(), prms.getLength(), null, 0);

		// Send back the response

		m_sess.sendResponseSMB(smbPkt);

		// Check if there are any file/directory change notify requests active

		DiskDeviceContext diskCtx = (DiskDeviceContext) conn.getContext();
		if ( diskCtx.hasFileServerNotifications() && respAction == FileAction.FileCreated) {

			// Check if a file or directory has been created

			if ( netFile.isDirectory())
				diskCtx.getChangeHandler().notifyDirectoryChanged(NotifyChange.ActionAdded, fileName);
			else
				diskCtx.getChangeHandler().notifyFileChanged(NotifyChange.ActionAdded, fileName);
		}
	}

	/**
	 * Process an NT I/O control transaction
	 * 
	 * @param tbuf TransactBuffer
	 * @param smbPkt NTTransPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	protected final void procNTTransactIOCtl(SrvTransactBuffer tbuf, NTTransPacket smbPkt)
		throws IOException, SMBSrvException {

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());

		if ( vc == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Get the tree connection details

		int treeId = tbuf.getTreeId();
		TreeConnection conn = vc.findConnection(treeId);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Unpack the request details

		DataBuffer setupBuf = tbuf.getSetupBuffer();

		int ctrlCode = setupBuf.getInt();
		int fid = setupBuf.getShort();
		boolean fsctrl = setupBuf.getByte() == 1 ? true : false;
		int filter = setupBuf.getByte();

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_TRAN))
			m_sess.debugPrintln("NT IOCtl code=" + NTIOCtl.asString(ctrlCode) + ", fid=" + fid + ", fsctrl=" + fsctrl
					+ ", filter=" + filter);

		// Access the disk interface that is associated with the shared device

		DiskInterface disk = null;
		try {

			// Get the disk interface for the share

			disk = (DiskInterface) conn.getSharedDevice().getInterface();
		}
		catch (InvalidDeviceInterfaceException ex) {

			// Failed to get/initialize the disk interface

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
		}

		// Check if the disk interface implements the optional IO control interface

		if ( disk instanceof IOCtlInterface) {

			// Access the IO control interface

			IOCtlInterface ioControl = (IOCtlInterface) disk;
			NTTransPacket respPkt = smbPkt;

			try {

				// Pass the request to the IO control interface for processing

				DataBuffer response = ioControl.processIOControl(m_sess, conn, ctrlCode, fid, tbuf.getDataBuffer(), fsctrl, filter);

				// Pack the response

				if ( response != null) {

					// Check if a larger buffer needs to be allocated for the response packet
					
					int respPktLen = NTTransPacket.calculateResponseLength( 0, response.getLength(), 1);
					
					if ( smbPkt.getBufferLength() < respPktLen) {

						// Allocate a larger response packet
						
						SMBSrvPacket pkt = m_sess.getPacketPool().allocatePacket( respPktLen, smbPkt, smbPkt.getLength());
						
						// Create a new NT transaction packet from the new buffer
						
						respPkt = new NTTransPacket( pkt.getBuffer());
					}
						
					// Pack the response data block

					respPkt.initTransactReply(null, 0, response.getBuffer(), response.getLength(), 1);
					respPkt.setSetupParameter(0, response.getLength());
				}
				else {

					// Pack an empty response data block

					respPkt.initTransactReply(null, 0, null, 0, 1);
					respPkt.setSetupParameter(0, 0);
				}
			}
			catch (IOControlNotImplementedException ex) {

				// Return a not implemented error status

				m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTNotImplemented, SMBStatus.SRVInternalServerError, SMBStatus.ErrSrv);
				return;
			}
			catch (SMBException ex) {

				// Return the specified SMB status, this should be an NT status code

				m_sess.sendErrorResponseSMB( smbPkt, ex.getErrorCode(), SMBStatus.SRVInternalServerError, SMBStatus.ErrSrv);
				return;
			}

			// Send the IOCtl response

			m_sess.sendResponseSMB(respPkt);
		}
		else {

			// Send back an error, IOctl not supported

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTNotImplemented, SMBStatus.SRVNotSupported, SMBStatus.ErrSrv);
		}
	}

	/**
	 * Process an NT query security descriptor transaction
	 * 
	 * @param tbuf TransactBuffer
	 * @param smbPkt NTTransPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	protected final void procNTTransactQuerySecurityDesc(SrvTransactBuffer tbuf, NTTransPacket smbPkt)
		throws IOException, SMBSrvException {

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());

		if ( vc == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Get the tree connection details

		int treeId = tbuf.getTreeId();
		TreeConnection conn = vc.findConnection(treeId);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Check if the user has the required access permission

		if ( conn.hasReadAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

		// Unpack the request details

		DataBuffer paramBuf = tbuf.getParameterBuffer();

		int fid = paramBuf.getShort();
		int flags = paramBuf.getShort();

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_TRAN))
			m_sess.debugPrintln("NT QuerySecurityDesc fid=" + fid + ", flags=" + flags);

		// Get the file details

		NetworkFile netFile = conn.findFile(fid);

		if ( netFile == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Access the disk interface that is associated with the shared device

		DiskInterface disk = null;
		try {

			// Get the disk interface for the share

			disk = (DiskInterface) conn.getSharedDevice().getInterface();
		}
		catch (InvalidDeviceInterfaceException ex) {

			// Failed to get/initialize the disk interface

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
		}

		// Check if the disk interface implements the optional security descriptor interface

		NTTransPacket respPkt = smbPkt;
		
		if ( disk instanceof SecurityDescriptorInterface) {

			// Access the security descriptor interface

			SecurityDescriptorInterface secDescInterface = (SecurityDescriptorInterface) disk;

			// Check if this is a buffer length check, if so the maximum returned data count will be
			// zero

			if ( tbuf.getReturnDataLimit() == 0) {

				// Get the security descriptor length

				int secDescLen = secDescInterface.getSecurityDescriptorLength(m_sess, conn, netFile);

				// Return the security descriptor length in the parameter block

				byte[] paramblk = new byte[4];
				DataPacker.putIntelInt(secDescLen, paramblk, 0);

				// Initialize the transaction reply

				respPkt.initTransactReply(paramblk, paramblk.length, null, 0);

				// Set a warning status to indicate the supplied data buffer was too small to return
				// the security descriptor

				respPkt.setLongErrorCode(SMBStatus.NTBufferTooSmall);
			}
			else {

				// Get the security descriptor for the file

				SecurityDescriptor secDesc = secDescInterface.loadSecurityDescriptor(m_sess, conn, netFile);

				byte[] secBuf = null;
				int secLen = 0;
				byte[] paramblk = new byte[4];

				if ( secDesc != null) {

					// Pack the security descriptor

					DataBuffer buf = new DataBuffer( 4096);

					try {
						secLen = secDesc.saveDescriptor( buf);
						secBuf = buf.getBuffer();
					}
					catch (SaveException ex) {
						m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
						return;
					}

					// Calculate the available space for the security descriptor in the current packet
					
					respPkt.initTransactReply(paramblk, paramblk.length, null, 0);
					int availLen = respPkt.getBufferLength() - respPkt.getLength();
					
					if ( availLen <= (secLen + 8)) {
						
						// Allocate a larger packet for the response
						
						SMBSrvPacket pkt = m_sess.getPacketPool().allocatePacket( respPkt.getLength() + secLen + 8, smbPkt);
						
						// Create a new NT transaction packet from the new buffer
						
						respPkt = new NTTransPacket( pkt.getBuffer());
					}
				}

				// Return the security descriptor length in the parameter block

				DataPacker.putIntelInt(secLen, paramblk, 0);

				// Initialize the transaction reply.

				respPkt.initTransactReply(paramblk, paramblk.length, secBuf, secLen);
			}
		}
		else {

			// Check if this is a buffer length check, if so the maximum returned data count will be
			// zero

			if ( tbuf.getReturnDataLimit() == 0) {

				// Return the security descriptor length in the parameter block

				byte[] paramblk = new byte[4];
				DataPacker.putIntelInt(_sdEveryOne.length, paramblk, 0);

				// Initialize the transaction reply

				respPkt.initTransactReply(paramblk, paramblk.length, null, 0);

				// Set a warning status to indicate the supplied data buffer was too small to return
				// the security descriptor

				respPkt.setLongErrorCode(SMBStatus.NTBufferTooSmall);
			}
			else {

				// Return the security descriptor length in the parameter block

				byte[] paramblk = new byte[4];
				DataPacker.putIntelInt(_sdEveryOne.length, paramblk, 0);

				// Initialize the transaction reply. Return the fixed security descriptor that
				// allows anyone to access the file/directory

				respPkt.initTransactReply(paramblk, paramblk.length, _sdEveryOne, _sdEveryOne.length);
			}
		}

		// Send back the response

		m_sess.sendResponseSMB( respPkt);
	}

	/**
	 * Process an NT set security descriptor transaction
	 * 
	 * @param tbuf TransactBuffer
	 * @param smbPkt NTTransPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	protected final void procNTTransactSetSecurityDesc(SrvTransactBuffer tbuf, NTTransPacket smbPkt)
		throws IOException, SMBSrvException {

		// Unpack the request details

		DataBuffer paramBuf = tbuf.getParameterBuffer();

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());

		if ( vc == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Get the tree connection details

		int treeId = tbuf.getTreeId();
		TreeConnection conn = vc.findConnection(treeId);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Check if the user has the required access permission

		if ( conn.hasWriteAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

		// Get the file details

		int fid = paramBuf.getShort();
		paramBuf.skipBytes(2);
		int flags = paramBuf.getInt();

		// Unpack the security descriptor

		DataBuffer dataBuf = tbuf.getDataBuffer();
		SecurityDescriptor secDesc = new SecurityDescriptor();

		try {
			secDesc.loadDescriptor(dataBuf.getBuffer(), dataBuf.getOffset());
		}
		catch (LoadException ex) {

			// Invalid security descriptor

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_TRAN)) {
			m_sess.debugPrintln("NT SetSecurityDesc fid=" + fid + ", flags=" + flags);
			m_sess.debugPrintln("   sd=" + secDesc);
		}

		// Access the disk interface that is associated with the shared device

		DiskInterface disk = null;
		try {

			// Get the disk interface for the share

			disk = (DiskInterface) conn.getSharedDevice().getInterface();
		}
		catch (InvalidDeviceInterfaceException ex) {

			// Failed to get/initialize the disk interface

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
		}

		// Check if the disk interface implements the optional security descriptor interface

		if ( disk instanceof SecurityDescriptorInterface) {

			// Access the security descriptor interface

			SecurityDescriptorInterface secDescInterface = (SecurityDescriptorInterface) disk;

			// Get the file details

			NetworkFile netFile = conn.findFile(fid);

			if ( netFile == null) {
				m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
				return;
			}

			// Save the security descriptor

			secDescInterface.saveSecurityDescriptor(m_sess, conn, netFile, secDesc);

			// Return a success status

			smbPkt.initTransactReply(null, 0, null, 0);
			smbPkt.setError(SMBStatus.Success, SMBStatus.Success);
			m_sess.sendResponseSMB(smbPkt);
		}
		else {

			// Send back an error, security descriptors not supported

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
		}
	}

	/**
	 * Process an NT change notification transaction
	 * 
	 * @param ntpkt NTTransPacket
	 * @param smbPkt SMBSrvPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	protected final void procNTTransactNotifyChange(NTTransPacket ntpkt, SMBSrvPacket smbPkt)
		throws IOException, SMBSrvException {

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());

		if ( vc == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Get the tree connection details

		int treeId = ntpkt.getTreeId();
		TreeConnection conn = vc.findConnection(treeId);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Check if the user has the required access permission

		if ( conn.hasReadAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

		// Make sure the tree connection is for a disk device

		if ( conn.getContext() == null || conn.getContext() instanceof DiskDeviceContext == false) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}

		// Check if the device has change notification enabled

		DiskDeviceContext diskCtx = (DiskDeviceContext) conn.getContext();
		if ( diskCtx.hasChangeHandler() == false) {

			// Return an error status, share does not have change notification enabled

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTNotImplemented, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}

		// Unpack the request details

		ntpkt.resetSetupPointer();

		int filter = ntpkt.unpackInt();
		int fid = ntpkt.unpackWord();
		boolean watchTree = ntpkt.unpackByte() == 1 ? true : false;
		int mid = ntpkt.getMultiplexId();

		// Get the file details

		NetworkFile dir = conn.findFile(fid);
		if ( dir == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}

		// Get the maximum notifications to buffer whilst waiting for the request to be reset after
		// a notification has been triggered

		int maxQueue = 0;

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_NOTIFY))
			m_sess.debugPrintln("NT NotifyChange fid=" + fid + ", mid=" + mid + ", filter=0x" + Integer.toHexString(filter)
					+ ", dir=" + dir.getFullName() + ", maxQueue=" + maxQueue);

		// Check if there is an existing request in the notify list that matches the new request and
		// is in a completed state. If so then the client is resetting the notify request so reuse the existing
		// request.

		NotifyRequest req = m_sess.findNotifyRequest(dir, filter, watchTree);

		if ( req != null && req.isCompleted()) {

			// Reset the existing request with the new multiplex id

			req.setMultiplexId(mid);
			req.setCompleted(false);

			// Check if there are any buffered notifications for this session

			if ( req.hasBufferedEvents() || req.hasNotifyEnum()) {

				// Get the buffered events from the request, clear the list from the request

				NotifyChangeEventList bufList = req.getBufferedEventList();
				req.clearBufferedEvents();

				// Send the buffered events

				diskCtx.getChangeHandler().sendBufferedNotifications(req, bufList);

				// DEBUG

				if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_NOTIFY)) {
					if ( bufList == null)
						m_sess.debugPrintln("   Sent buffered notifications, req=" + req.toString() + ", Enum");
					else
						m_sess.debugPrintln("   Sent buffered notifications, req=" + req.toString() + ", count="
								+ bufList.numberOfEvents());
				}
			}
			else {

				// DEBUG

				if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_NOTIFY))
					m_sess.debugPrintln("   Reset notify request, " + req.toString());
			}
		}
		else {

			// Create a change notification request

			req = new NotifyRequest(filter, watchTree, m_sess, dir, mid, ntpkt.getTreeId(), ntpkt.getProcessId(), ntpkt.getUserId(), maxQueue);

			// Add the request to the pending notify change lists

			m_sess.addNotifyRequest(req, diskCtx);

			// Debug

			if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_NOTIFY)) {
				m_sess.debugPrintln("   Added new request, " + req.toString());
				m_sess.debugPrintln("   Global notify mask = 0x"
						+ Integer.toHexString(diskCtx.getChangeHandler().getGlobalNotifyMask()) + ", reqQueue="
						+ diskCtx.getChangeHandler().getRequestQueueSize());
			}
		}

		// NOTE: If the change notification request is accepted then no reply is sent to the client.
		// A reply will be sent asynchronously if the change notification is triggered.
	}

	/**
	 * Process an NT rename via handle transaction
	 * 
	 * @param tbuf TransactBuffer
	 * @param smbPkt NTTransPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	protected final void procNTTransactRename(SrvTransactBuffer tbuf, NTTransPacket smbPkt)
		throws IOException, SMBSrvException {

		// Unpack the request details

//		DataBuffer paramBuf = tbuf.getParameterBuffer();

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());

		if ( vc == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Get the tree connection details

		int treeId = tbuf.getTreeId();
		TreeConnection conn = vc.findConnection(treeId);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Check if the user has the required access permission

		if ( conn.hasWriteAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_TRAN))
			m_sess.debugPrintln("NT TransactRename");

		// Send back an error, NT rename not supported

		m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
	}
	
	
	/**
	 * Check if a file has an oplock, start the oplock break and defer the packet until the oplock
	 * break has finished processing.
	 * 
	 * @param sess SMBSrvSession
	 * @param pkt SMBSrvPacket
	 * @param disk DiskInterface
	 * @param params FileOpenParams
	 * @param conn TreeConnection
	 * @exception DeferredPacketException	If an oplock break has been started
	 * @exception AccessDeniedException 	If the oplock break send fails
	 */
	private final void checkOpLock(SMBSrvSession sess, SMBSrvPacket pkt, DiskInterface disk, FileOpenParams params, TreeConnection tree)
		throws DeferredPacketException, AccessDeniedException {
		
		// Check if the filesystem supports oplocks
		
		if ( disk instanceof OpLockInterface) {
			
			// Get the oplock interface, check if oplocks are enabled
			
			OpLockInterface oplockIface = (OpLockInterface) disk;
			if ( oplockIface.isOpLocksEnabled(sess, tree) == false)
				return;
			
			OpLockManager oplockMgr = oplockIface.getOpLockManager( sess, tree);
			
			if ( oplockMgr == null) {
				
				// DEBUG
				
				if ( Debug.EnableDbg && sess.hasDebug( SMBSrvSession.DBG_OPLOCK))
					m_sess.debugPrintln( "OpLock manager is null, tree=" + tree);
				
				// Nothing to do
				
				return;
			}
			
			// Check if the file has an oplock
			
			OpLockDetails oplock = oplockMgr.getOpLockDetails( params.getFullPath());
			
			if ( oplock != null) {

				// DEBUG
				
				if ( Debug.EnableDbg && sess.hasDebug( SMBSrvSession.DBG_OPLOCK))
					m_sess.debugPrintln( "Check oplock on file " + params.getPath() + ", oplock=" + oplock);
				
				// Check if the oplock is local
				
				boolean deferredPkt = false;
				
				if ( oplock instanceof LocalOpLockDetails) {
					
					// Access the local oplock details
					
					LocalOpLockDetails localOpLock = (LocalOpLockDetails) oplock;
					
					// Check if the session that owns the oplock is still valid
					
					SMBSrvSession opSess = localOpLock.getOwnerSession();
					
					if ( opSess.isShutdown() == false) {
						
						// Check if the open is not accessing the file data, ie. accessing attributes only
						
						if (( params.getAccessMode() & (AccessMode.NTSynchronize + AccessMode.NTReadAttrib + AccessMode.NTWriteAttrib)) == 0 &&
								(params.getAccessMode() & (AccessMode.NTGenericRead + AccessMode.NTGenericWrite + AccessMode.NTGenericExecute)) == 0) {
							
							// DEBUG
							
							if ( Debug.EnableDbg && m_sess.hasDebug( SMBSrvSession.DBG_OPLOCK))
								m_sess.debugPrintln("No oplock break, access attributes only, params=" + params + ", oplock=" + oplock);
							
							// Oplock break not required
							
							return;
						}
					
						// Check if the oplock has a failed break timeout, do not send another break request to the client, fail the open
						// request with an access denied error
						
						if ( oplock.hasOplockBreakFailed()) {
						
							// DEBUG
							
							if ( Debug.EnableDbg && m_sess.hasDebug( SMBSrvSession.DBG_OPLOCK))
								m_sess.debugPrintln("Oplock has failed break attempt, failing open request params=" + params);
							
							// Fail the open request with an access denied error
							
							throw new AccessDeniedException( "Oplock has failed break");
						}
						
						// Need to send an oplock break to the oplock owner before we can continue processing the current file open request
						
						try {
							
							// DEBUG
							
							if ( Debug.EnableDbg && m_sess.hasDebug( SMBSrvSession.DBG_OPLOCK))
								m_sess.debugPrintln("Oplock break required, owner=" + oplock + ", open=" + sess.getUniqueId() + ", PID=" + pkt.getProcessId() + ", MID=" + pkt.getMultiplexId());
								
							// Request the owner session break the oplock
							
							oplockMgr.requestOpLockBreak( oplock.getPath(), oplock, m_sess, pkt);
							
							// Indicate that the current CIFS request packet processing should be deferred, until the oplock break is received
							// from the owner
							
							deferredPkt = true;
						}
	
						catch ( IOException ex) {
							
							// Log the error
							
							if ( Debug.EnableError) {
								Debug.println("Failed to send local oplock break:", Debug.Error);
								Debug.println(ex, Debug.Error);
							}
							
							// Throw an access denied exception so that the file open is rejected
							
							throw new AccessDeniedException( "Oplock break send failed");
						}
					}
					else {
						
						//	Oplock owner session is no longer valid, release the oplock
						
						oplockMgr.releaseOpLock( oplock.getPath());
	
						// DEBUG
						
						if ( Debug.EnableDbg && m_sess.hasDebug( SMBSrvSession.DBG_OPLOCK))
							m_sess.debugPrintln("Oplock released, session invalid sess=" + opSess.getUniqueId());
					}
				}
				else if ( oplock.isRemoteLock()) {
					
					// Check if the open is not accessing the file data, ie. accessing attributes only
					
//					if (( params.getAccessMode() & (AccessMode.NTSynchronize + AccessMode.NTReadAttrib + AccessMode.NTWriteAttrib)) == 0)
					if (( params.getAccessMode() & (AccessMode.NTRead + AccessMode.NTWrite + AccessMode.NTAppend)) == 0)
						return;

					// Check if the oplock has a failed break timeout, do not send another break request to the client, fail the open
					// request with an access denied error
					
					if ( oplock.hasOplockBreakFailed()) {
					
						// DEBUG
						
						if ( Debug.EnableDbg && m_sess.hasDebug( SMBSrvSession.DBG_OPLOCK))
							m_sess.debugPrintln("Oplock has failed break attempt, failing open request params=" + params);
						
						// Fail the open request with an access denied error
						
						throw new AccessDeniedException( "Oplock has failed break");
					}
					
					try {
						
						// Send a remote oplock break request to the owner node
						
						oplockMgr.requestOpLockBreak( oplock.getPath(), oplock, m_sess, pkt);
						
						// DEBUG
						
						if ( Debug.EnableDbg && m_sess.hasDebug( SMBSrvSession.DBG_OPLOCK))
							m_sess.debugPrintln("Remote oplock break sent, oplock=" + oplock);
						
						// Indicate that the current CIFS request packet processing should be deferred, until the oplock break is received
						// from the owner
						
						deferredPkt = true;
					}
					catch ( IOException ex) {
						
						// Log the error
						
						if ( Debug.EnableError) {
							Debug.println("Failed to send remote oplock break:", Debug.Error);
							Debug.println(ex, Debug.Error);
						}
						
						// Clear the deferred session details
						
						oplock.clearDeferredSession();
						
						// Throw an access denied exception so that the file open is rejected
						
						throw new AccessDeniedException( "Oplock break send failed");
					}
				}

				// Check if the CIFS file open request processing should be deferred until the oplock break has completed
				
				if ( deferredPkt == true)
					throw new DeferredPacketException( "Waiting for oplock break");
			}
		}
		
		// Returning without an exception indicates that there is no oplock on the file, so the
		// file open request can continue
	}
	
	/**
	 * Grant an oplock, check if the filesystem supports oplocks, grant the requested oplock and return the
	 * oplock details, or null if no oplock granted or requested.
	 * 
	 * @param sess SMBSrvSession
	 * @param pkt SMBSrvPacket
	 * @param disk DiskInterface
	 * @param tree TreeConnection
	 * @param params FileOpenParams
	 * @param netFile NetworkFile
	 * @return LocalOpLockDetails
	 */
	private final LocalOpLockDetails grantOpLock(SMBSrvSession sess, SMBSrvPacket pkt, DiskInterface disk, TreeConnection tree, FileOpenParams params, NetworkFile netFile) {
		
		// Check if the client requested an oplock, or the file open is on a folder
		
		if ( netFile.isDirectory() || (params.requestBatchOpLock() == false && params.requestExclusiveOpLock() == false))
			return null;
		
		// Check if the filesystem supports oplocks
		
		LocalOpLockDetails oplock = null;
		
		if ( disk instanceof OpLockInterface) {
			
			// Get the oplock interfcae, check if oplocks are enabled
			
			OpLockInterface oplockIface = (OpLockInterface) disk;
			if ( oplockIface.isOpLocksEnabled(sess, tree) == false)
				return null;
			
			OpLockManager oplockMgr = oplockIface.getOpLockManager( sess, tree);
			
			if ( oplockMgr != null) {
				
				// Get the oplock type
				
				int oplockTyp = OpLock.TypeNone;
				
				if ( params.requestBatchOpLock())
					oplockTyp = OpLock.TypeBatch;
				else
					oplockTyp = OpLock.TypeExclusive;
					
				// Create the oplock details
				
				oplock = new LocalOpLockDetails( oplockTyp, params.getPath(), sess, pkt, netFile.isDirectory());
				
				try {
					
					// Store the oplock via the oplock manager, check if the oplock grant was allowed
					
					if ( oplockMgr.grantOpLock( params.getPath(), oplock, netFile)) {
					
						// Save the oplock details with the opened file
						
						netFile.setOpLock( oplock);
						
						// DEBUG
						
						if ( Debug.EnableDbg && sess.hasDebug( SMBSrvSession.DBG_OPLOCK))
							m_sess.debugPrintln( "Granted oplock sess=" + sess.getUniqueId() + " oplock=" + oplock);
					}
					else {
						
						// DEBUG
						
						if ( Debug.EnableDbg && sess.hasDebug( SMBSrvSession.DBG_OPLOCK))
							m_sess.debugPrintln( "Oplock not granted sess=" + sess.getUniqueId() + " oplock=" + oplock + " (Open count)");

						// Clear the oplock, not granted
						
						oplock = null;
					}
				}
				catch (ExistingOpLockException ex) {
					
					// DEBUG
					
					if ( Debug.EnableDbg && sess.hasDebug( SMBSrvSession.DBG_OPLOCK))
						m_sess.debugPrintln( "Failed to grant oplock sess=" + sess.getUniqueId() + ", file=" + params.getPath() + " (Oplock exists)");
					
					// Indicate no oplock was granted
					
					oplock = null;
				}
			}
			else {
				
				// DEBUG
				
				if ( Debug.EnableDbg && sess.hasDebug( SMBSrvSession.DBG_OPLOCK))
					m_sess.debugPrintln( "OpLock manager is null, tree=" + tree);
			}
		}

		// Return the oplock details, or null if no oplock granted/not requested/not supported
		
		return oplock;
	}
	
	/**
	 * Release an oplock
	 * 
	 * @param sess SMBSrvSession
	 * @param pkt SMBSrvPacket
	 * @param disk DiskInterface
	 * @param tree TreeConnection
	 * @param netFile NetworkFile
	 */
	private final void releaseOpLock(SMBSrvSession sess, SMBSrvPacket pkt, DiskInterface disk, TreeConnection tree, NetworkFile netFile) {
		
		// Check if the filesystem supports oplocks
		
		if ( disk instanceof OpLockInterface) {
			
			// Get the oplock manager
			
			OpLockInterface oplockIface = (OpLockInterface) disk;
			OpLockManager oplockMgr = oplockIface.getOpLockManager( sess, tree);
			
			if ( oplockMgr != null) {
				
				// Get the oplock details
				
				OpLockDetails oplock = netFile.getOpLock();

				if ( oplock != null) {
					
					// Release the oplock
					
					oplockMgr.releaseOpLock( oplock.getPath());
					
					// Clear the network file oplock
					
					netFile.setOpLock( null);
					
					// DEBUG
					
					if ( Debug.EnableDbg && sess.hasDebug( SMBSrvSession.DBG_OPLOCK))
						m_sess.debugPrintln( "Released oplock sess=" + sess.getUniqueId() + " oplock=" + oplock);
				}
			}
		}
	}
}
