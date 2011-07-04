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

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.netbios.RFCNetBIOSProtocol;
import org.alfresco.jlan.server.auth.ClientInfo;
import org.alfresco.jlan.server.auth.ICifsAuthenticator;
import org.alfresco.jlan.server.auth.InvalidUserException;
import org.alfresco.jlan.server.config.GlobalConfigSection;
import org.alfresco.jlan.server.core.InvalidDeviceInterfaceException;
import org.alfresco.jlan.server.core.ShareType;
import org.alfresco.jlan.server.core.SharedDevice;
import org.alfresco.jlan.server.filesys.AccessDeniedException;
import org.alfresco.jlan.server.filesys.DiskDeviceContext;
import org.alfresco.jlan.server.filesys.DiskInterface;
import org.alfresco.jlan.server.filesys.FileAccess;
import org.alfresco.jlan.server.filesys.FileAction;
import org.alfresco.jlan.server.filesys.FileAttribute;
import org.alfresco.jlan.server.filesys.FileInfo;
import org.alfresco.jlan.server.filesys.FileOfflineException;
import org.alfresco.jlan.server.filesys.FileOpenParams;
import org.alfresco.jlan.server.filesys.FileSharingException;
import org.alfresco.jlan.server.filesys.FileStatus;
import org.alfresco.jlan.server.filesys.NetworkFile;
import org.alfresco.jlan.server.filesys.PathNotFoundException;
import org.alfresco.jlan.server.filesys.SearchContext;
import org.alfresco.jlan.server.filesys.SrvDiskInfo;
import org.alfresco.jlan.server.filesys.TooManyConnectionsException;
import org.alfresco.jlan.server.filesys.TooManyFilesException;
import org.alfresco.jlan.server.filesys.TreeConnection;
import org.alfresco.jlan.server.filesys.UnsupportedInfoLevelException;
import org.alfresco.jlan.server.filesys.VolumeInfo;
import org.alfresco.jlan.smb.DataType;
import org.alfresco.jlan.smb.FileInfoLevel;
import org.alfresco.jlan.smb.FindFirstNext;
import org.alfresco.jlan.smb.InvalidUNCPathException;
import org.alfresco.jlan.smb.PCShare;
import org.alfresco.jlan.smb.PacketType;
import org.alfresco.jlan.smb.SMBDate;
import org.alfresco.jlan.smb.SMBStatus;
import org.alfresco.jlan.smb.server.ntfs.NTFSStreamsInterface;
import org.alfresco.jlan.smb.server.ntfs.StreamInfoList;
import org.alfresco.jlan.util.DataBuffer;
import org.alfresco.jlan.util.DataPacker;
import org.alfresco.jlan.util.WildCard;

/**
 * LanMan SMB Protocol Handler Class.
 * 
 * <p>
 * The LanMan protocol handler processes the additional SMBs that were added to the protocol in the
 * LanMan1 and LanMan2 SMB dialects.
 * 
 * @author gkspencer
 */
class LanManProtocolHandler extends CoreProtocolHandler {

	// Locking type flags

	protected static final int LockShared = 0x01;
	protected static final int LockOplockRelease = 0x02;
	protected static final int LockChangeType = 0x04;
	protected static final int LockCancel = 0x08;
	protected static final int LockLargeFiles = 0x10;

	// Dummy date/time for dot files

	public static final long DotFileDateTime = System.currentTimeMillis();

	/**
	 * LanManProtocolHandler constructor.
	 */
	protected LanManProtocolHandler() {
		super();
	}

	/**
	 * LanManProtocolHandler constructor.
	 * 
	 * @param sess SMBSrvSession
	 */
	protected LanManProtocolHandler(SMBSrvSession sess) {
		super(sess);
	}

	/**
	 * Return the protocol name
	 * 
	 * @return String
	 */
	public String getName() {
		return "LanMan";
	}

	/**
	 * Process the chained SMB commands (AndX).
	 * 
	 * @param smbPkt SMBSrvPacket
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
		respPkt.setParameter(1, andxOff - RFCNetBIOSProtocol.HEADER_LEN);

		// Pointer to the last parameter block, starts with the main command parameter block

		int paramBlk = SMBSrvPacket.WORDCNT;

		// Get the current end of the reply packet offset

		int endOfPkt = respPkt.getByteOffset() + respPkt.getByteCount();
		boolean andxErr = false;

		while (andxCmd != SMBSrvPacket.NO_ANDX_CMD && andxErr == false) {

			// Determine the chained command type

			int prevEndOfPkt = endOfPkt;

			switch (andxCmd) {

			// Tree connect

			case PacketType.TreeConnectAndX:
				endOfPkt = procChainedTreeConnectAndX(andxOff, smbPkt, respPkt, endOfPkt);
				break;

			// Close file

			case PacketType.CloseFile:
				endOfPkt = procChainedClose(andxOff, smbPkt, respPkt, endOfPkt);
				break;

			// Read file

			case PacketType.ReadAndX:
				endOfPkt = procChainedReadAndX(andxOff, smbPkt, respPkt, endOfPkt, file);
				break;
			}

			// Advance to the next chained command block

			andxCmd = smbPkt.getAndXParameter(andxOff, 0) & 0x00FF;
			andxOff = smbPkt.getAndXParameter(andxOff, 1);

			// Set the next chained command details in the current parameter block

			respPkt.setAndXCommand(prevEndOfPkt, andxCmd);
			respPkt.setAndXParameter(paramBlk, 1, prevEndOfPkt - RFCNetBIOSProtocol.HEADER_LEN);

			// Advance the current parameter block

			paramBlk = prevEndOfPkt;

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
	 * @param cmdOff int Offset to the chained command within the request packet
	 * @param smbPkt Request packet
	 * @param respPkt SMBSrvPacket Reply packet
	 * @param endOff int Offset to the current end of the reply packet.
	 * @return New end of reply offset.
	 */
	protected final int procChainedTreeConnectAndX(int cmdOff, SMBSrvPacket smbPkt, SMBSrvPacket respPkt, int endOff) {

		// Extract the parameters

		int flags = smbPkt.getAndXParameter(cmdOff, 2);
		int pwdLen = smbPkt.getAndXParameter(cmdOff, 3);

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(respPkt.getUserId());

		if ( vc == null) {
			respPkt.setError(smbPkt.isLongErrorCode(), SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError,
					SMBStatus.ErrSrv);
			return endOff;
		}

		// Get the data bytes position and length

		int dataPos = smbPkt.getAndXByteOffset(cmdOff);
		int dataLen = smbPkt.getAndXByteCount(cmdOff);
		byte[] buf = smbPkt.getBuffer();

		// Extract the password string

		String pwd = null;

		if ( pwdLen > 0) {
			pwd = new String(buf, dataPos, pwdLen);
			dataPos += pwdLen;
			dataLen -= pwdLen;
		}

		// Extract the requested share name, as a UNC path

		String uncPath = DataPacker.getString(buf, dataPos, dataLen);
		if ( uncPath == null) {
			respPkt.setError(SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return endOff;
		}

		// Extract the service type string

		dataPos += uncPath.length() + 1; // null terminated
		dataLen -= uncPath.length() + 1; // null terminated

		String service = DataPacker.getString(buf, dataPos, dataLen);
		if ( service == null) {
			respPkt.setError(SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return endOff;
		}

		// Convert the service type to a shared device type, client may specify '?????' in which
		// case we ignore the error.

		int servType = ShareType.ServiceAsType(service);
		if ( servType == ShareType.UNKNOWN && service.compareTo("?????") != 0) {
			respPkt.setError(SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return endOff;
		}

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_TREE))
			m_sess.debugPrintln("ANDX Tree Connect AndX - " + uncPath + ", " + service);

		// Parse the requested share name

		PCShare share = null;

		try {
			share = new PCShare(uncPath);
		}
		catch (InvalidUNCPathException ex) {
			respPkt.setError(SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return endOff;
		}

		// Map the IPC$ share to the admin pipe type

		if ( share.getShareName().compareTo("IPC$") == 0)
			servType = ShareType.ADMINPIPE;

		// Find the requested shared device

		SharedDevice shareDev = null;

		try {

			// Get/create the shared device

			shareDev = m_sess.getSMBServer().findShare(share.getNodeName(), share.getShareName(), servType, getSession(), true);
		}
		catch (InvalidUserException ex) {

			// Return a logon failure status

			respPkt.setError(SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return endOff;
		}
		catch (Exception ex) {

			// Return a general status, bad network name

			respPkt.setError(SMBStatus.SRVInvalidNetworkName, SMBStatus.ErrSrv);
			return endOff;
		}

		// Check if the share is valid

		if ( shareDev == null || (servType != ShareType.UNKNOWN && shareDev.getType() != servType)) {
			respPkt.setError(SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return endOff;
		}

		// Authenticate the share connect, if the server is using share mode security

		ICifsAuthenticator auth = getSession().getSMBServer().getCifsAuthenticator();
		int filePerm = FileAccess.Writeable;

		if ( auth != null && auth.getAccessMode() == ICifsAuthenticator.SHARE_MODE) {

			// Validate the share connection

			filePerm = auth.authenticateShareConnect(m_sess.getClientInformation(), shareDev, pwd, m_sess);
			if ( filePerm < 0) {

				// Invalid share connection request

				respPkt.setError(SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
				return endOff;
			}
		}

		// Allocate a tree id for the new connection

		try {

			// Allocate the tree id for this connection

			int treeId = vc.addConnection(shareDev);
			respPkt.setTreeId(treeId);

			// Set the file permission that this user has been granted for this share

			TreeConnection tree = vc.findConnection(treeId);
			tree.setPermission(filePerm);

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
		int bytLen = pos - respPkt.getAndXByteOffset(endOff);
		respPkt.setAndXByteCount(endOff, bytLen);

		// Return the new end of packet offset

		return pos;
	}

	/**
	 * Process a chained read file request
	 * 
	 * @param cmdOff Offset to the chained command within the request packet
	 * @param smbPkt Request packet
	 * @param respPkt Reply packet.
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

		if ( Debug.EnableDbg && m_sess.hasDebug(SMBSrvSession.DBG_FILE))
			Debug.println("Chained File Read AndX : Size=" + maxCount + " ,Pos=" + offset);

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

			respPkt.setAndXParameter(endOff, 2, 0xFFFF);
			respPkt.setAndXParameter(endOff, 3, 0);
			respPkt.setAndXParameter(endOff, 4, 0);
			respPkt.setAndXParameter(endOff, 5, rdlen);
			respPkt.setAndXParameter(endOff, 6, dataPos - RFCNetBIOSProtocol.HEADER_LEN);

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
	 * @param cmdOff int Offset to the chained command within the request packet
	 * @param smbPkt Request packet
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

		int fid = smbPkt.getAndXParameter(cmdOff, 0);
		NetworkFile netFile = conn.findFile(fid);

		if ( netFile == null) {
			respPkt.setError(SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return endOff;
		}

		// Debug

		if ( Debug.EnableDbg && m_sess.hasDebug(SMBSrvSession.DBG_FILE))
			Debug.println("Chained File Close [" + smbPkt.getTreeId() + "] fid=" + fid);

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
	 * Close a search started via the transact2 find first/next command.
	 * 
	 * @param smbPkt SMBSrvPacket
	 * @exception java.io.IOException The exception description.
	 * @exception org.alfresco.aifs.smb.SMBSrvException The exception description.
	 */
	protected final void procFindClose(SMBSrvPacket smbPkt)
		throws java.io.IOException, SMBSrvException {

		// Check that the received packet looks like a valid find close request

		if ( smbPkt.checkPacketIsValid(1, 0) == false) {
			m_sess.sendErrorResponseSMB(smbPkt, SMBStatus.SRVUnrecognizedCommand, SMBStatus.ErrSrv);
			return;
		}

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());
		if ( vc == null) {
			m_sess.sendErrorResponseSMB(smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}

		// Get the tree connection details

		int treeId = smbPkt.getTreeId();
		TreeConnection conn = vc.findConnection(treeId);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB(smbPkt, SMBStatus.SRVInvalidTID, SMBStatus.ErrSrv);
			return;
		}

		// Check if the user has the required access permission

		if ( conn.hasReadAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB(smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

		// Get the search id

		int searchId = smbPkt.getParameter(0);

		// Get the search context

		SearchContext ctx = vc.getSearchContext(searchId);

		if ( ctx == null) {

			// Invalid search handle

			m_sess.sendSuccessResponseSMB(smbPkt);
			return;
		}

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_SEARCH))
			m_sess.debugPrintln("Close trans search [" + searchId + "]");

		// Deallocate the search slot, close the search.

		vc.deallocateSearchSlot(searchId);

		// Return a success status SMB

		m_sess.sendSuccessResponseSMB(smbPkt);
	}

	/**
	 * Process the file lock/unlock request.
	 * 
	 * @param smbPkt SMBSrvPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	protected final void procLockingAndX(SMBSrvPacket smbPkt)
		throws IOException, SMBSrvException {

		// Check that the received packet looks like a valid locking andX request

		if ( smbPkt.checkPacketIsValid(8, 0) == false) {
			m_sess.sendErrorResponseSMB(smbPkt, SMBStatus.SRVUnrecognizedCommand, SMBStatus.ErrSrv);
			return;
		}

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());
		if ( vc == null) {
			m_sess.sendErrorResponseSMB(smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}

		// Get the tree connection details

		int treeId = smbPkt.getTreeId();
		TreeConnection conn = vc.findConnection(treeId);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB(smbPkt, SMBStatus.SRVInvalidTID, SMBStatus.ErrSrv);
			return;
		}

		// Check if the user has the required access permission

		if ( conn.hasReadAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB(smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

		// Extract the file lock/unlock parameters

		int fid = smbPkt.getParameter(2);
		int lockType = smbPkt.getParameter(3);
		long lockTmo = smbPkt.getParameterLong(4);
		int lockCnt = smbPkt.getParameter(6);
		int unlockCnt = smbPkt.getParameter(7);

		NetworkFile netFile = conn.findFile(fid);

		if ( netFile == null) {
			m_sess.sendErrorResponseSMB(smbPkt, SMBStatus.DOSInvalidHandle, SMBStatus.ErrDos);
			return;
		}

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_LOCK))
			m_sess.debugPrintln("File Lock [" + netFile.getFileId() + "] : type=0x" + Integer.toHexString(lockType) + ", tmo="
					+ lockTmo + ", locks=" + lockCnt + ", unlocks=" + unlockCnt);

		// Return a success status for now

		smbPkt.setParameterCount(2);
		smbPkt.setAndXCommand(0xFF);
		smbPkt.setParameter(1, 0);
		smbPkt.setByteCount(0);

		// Send the lock request response

		m_sess.sendResponseSMB(smbPkt);
	}

	/**
	 * Process the logoff request.
	 * 
	 * @param outPkt SMBSrvPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	protected final void procLogoffAndX(SMBSrvPacket smbPkt)
		throws java.io.IOException, SMBSrvException {

		// Check that the received packet looks like a valid logoff andX request

		if ( smbPkt.checkPacketIsValid(2, 0) == false) {
			m_sess.sendErrorResponseSMB(smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}

		// Get the virtual circuit for the request

		int uid = smbPkt.getUserId();
		VirtualCircuit vc = m_sess.findVirtualCircuit(uid);

		if ( vc == null) {
			m_sess.sendErrorResponseSMB(smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// DEBUG

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_NEGOTIATE))
			Debug.println("[SMB] Logoff vc=" + vc);

		// Close the virtual circuit

		m_sess.removeVirtualCircuit(uid);

		// Return a success status SMB

		m_sess.sendSuccessResponseSMB(smbPkt);
	}

	/**
	 * Process the file open request.
	 * 
	 * @param smbPkt SMBSrvPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	protected final void procOpenAndX(SMBSrvPacket smbPkt)
		throws java.io.IOException, SMBSrvException {

		// Check that the received packet looks like a valid open andX request

		if ( smbPkt.checkPacketIsValid(15, 1) == false) {
			m_sess.sendErrorResponseSMB(smbPkt, SMBStatus.SRVUnrecognizedCommand, SMBStatus.ErrSrv);
			return;
		}

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());
		if ( vc == null) {
			m_sess.sendErrorResponseSMB(smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}

		// Get the tree connection details

		int treeId = smbPkt.getTreeId();
		TreeConnection conn = vc.findConnection(treeId);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB(smbPkt, SMBStatus.SRVInvalidTID, SMBStatus.ErrSrv);
			return;
		}

		// Check if the user has the required access permission

		if ( conn.hasReadAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB(smbPkt, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
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

			m_sess.sendErrorResponseSMB(smbPkt, SMBStatus.SRVNoAccessRights, SMBStatus.ErrSrv);
			return;
		}

		// Extract the open file parameters

		int flags    = smbPkt.getParameter(2);
		int access   = smbPkt.getParameter(3);
		int srchAttr = smbPkt.getParameter(4);
		int fileAttr = smbPkt.getParameter(5);
		int crTime   = smbPkt.getParameter(6);
		int crDate   = smbPkt.getParameter(7);
		int openFunc = smbPkt.getParameter(8);
		int allocSiz = smbPkt.getParameterLong(9);

		// Extract the filename string

		String fileName = smbPkt.unpackString(smbPkt.isUnicode());
		if ( fileName == null) {
			m_sess.sendErrorResponseSMB(smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
		}

		// Create the file open parameters

		SMBDate crDateTime = null;
		if ( crTime > 0 && crDate > 0)
			crDateTime = new SMBDate(crDate, crTime);

		FileOpenParams params = new FileOpenParams(fileName, openFunc, access, srchAttr, fileAttr, allocSiz,
				crDateTime != null ? crDateTime.getTime() : 0L, smbPkt.getProcessIdFull());

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_FILE))
			m_sess.debugPrintln("File Open AndX [" + treeId + "] params=" + params);

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

						m_sess.sendErrorResponseSMB(smbPkt, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
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

						m_sess.sendErrorResponseSMB(smbPkt, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
					}
					else {

						// Return a file not found error

						m_sess.sendErrorResponseSMB(smbPkt, SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
					}
					return;
				}
			}
			else {

				// Open the requested file

				netFile = disk.openFile(m_sess, conn, params);

				// Set the file action response

				if ( FileAction.truncateExistingFile(openFunc))
					respAction = FileAction.FileTruncated;
				else
					respAction = FileAction.FileExisted;
			}

			// Add the file to the list of open files for this tree connection

			fid = conn.addFile(netFile, getSession());

		}
		catch (InvalidDeviceInterfaceException ex) {

			// Failed to get/initialize the disk interface

			m_sess.sendErrorResponseSMB(smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
		}
		catch (TooManyFilesException ex) {

			// Too many files are open on this connection, cannot open any more files.

			m_sess.sendErrorResponseSMB(smbPkt, SMBStatus.DOSTooManyOpenFiles, SMBStatus.ErrDos);
			return;
		}
		catch (AccessDeniedException ex) {

			// Return an access denied error

			m_sess.sendErrorResponseSMB(smbPkt, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}
		catch (FileSharingException ex) {

			// Return a sharing violation error

			m_sess.sendErrorResponseSMB(smbPkt, SMBStatus.DOSFileSharingConflict, SMBStatus.ErrDos);
			return;
		}
		catch (FileOfflineException ex) {

			// File data is unavailable

			m_sess.sendErrorResponseSMB(smbPkt, SMBStatus.NTFileOffline, SMBStatus.HRDDriveNotReady, SMBStatus.ErrHrd);
			return;
		}
		catch (java.io.IOException ex) {

			// Failed to open the file

			m_sess.sendErrorResponseSMB(smbPkt, SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
			return;
		}

		// Check if there is a chain command to process
		
		SMBSrvPacket respPkt = smbPkt;
		boolean andX = false;
		
		if ( smbPkt.hasAndXCommand()) {

			// Allocate a new packet for the response
			
			respPkt = m_sess.getPacketPool().allocatePacket( smbPkt.getLength(), smbPkt);
			
			// Indicate that there is an AndX chained command to process
			
			andX = true;
		}
		
		// Build the open file response

		respPkt.setParameterCount(15);

		respPkt.setAndXCommand(0xFF);
		respPkt.setParameter(1, 0); // AndX offset

		respPkt.setParameter(2, fid);
		respPkt.setParameter(3, netFile.getFileAttributes() & StandardAttributes);

		long modDate = 0L;

		if ( netFile.hasModifyDate()) {
			GlobalConfigSection gblConfig = (GlobalConfigSection) m_sess.getServer().getConfiguration().getConfigSection(
					GlobalConfigSection.SectionName);
			modDate = (netFile.getModifyDate() / 1000L) + (gblConfig != null ? gblConfig.getTimeZoneOffset() : 0);
		}

		respPkt.setParameterLong(4, (int) modDate);
		respPkt.setParameterLong(6, netFile.getFileSizeInt()); // file size
		respPkt.setParameter(8, netFile.getGrantedAccess());
		respPkt.setParameter(9, OpenAndX.FileTypeDisk);
		respPkt.setParameter(10, 0); // named pipe state
		respPkt.setParameter(11, respAction);
		respPkt.setParameter(12, 0); // server FID (long)
		respPkt.setParameter(13, 0);
		respPkt.setParameter(14, 0);

		respPkt.setByteCount(0);

		// Check if there is a chained command, or commands

		if ( andX == true) {

			// Process any chained commands, AndX

			int pos = procAndXCommands(smbPkt, netFile);

			// Send the read andX response

			m_sess.sendResponseSMB(respPkt, pos);
		}
		else {

			// Send the normal read AndX response

			m_sess.sendResponseSMB(respPkt);
		}
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
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.SRVUnrecognizedCommand, SMBStatus.ErrSrv);
			return;
		}

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());
		if ( vc == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
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

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
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

		int fid      = smbPkt.getParameter(2);
		int offset   = smbPkt.getParameterLong(3);
		int maxCount = smbPkt.getParameter(5);

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
		int rdlen   = 0;

		// Set the returned parameter count so that the byte offset can be calculated

		smbPkt.setParameterCount(12);
		int dataPos = smbPkt.getByteOffset();
		
		try {

			// Check if the requested data will fit into the current packet
			
			if ( maxCount > ( buf.length - dataPos)) {

				// Allocate a larger packet for the response
				
				respPkt = m_sess.getPacketPool().allocatePacket( maxCount + dataPos, smbPkt);
				
				// Switch to the response buffer
				
				buf = respPkt.getBuffer();
				respPkt.setParameterCount( 12);
			}
			
			// Access the disk interface that is associated with the shared device

			DiskInterface disk = (DiskInterface) conn.getSharedDevice().getInterface();

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
		catch (AccessDeniedException ex) {

			// No access to file, or file is a directory
			//    	
			// Debug

			if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_FILEIO))
				m_sess.debugPrintln("File Read Error [" + netFile.getFileId() + "] : " + ex.toString());

			// Failed to read the file

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}
		catch (java.io.IOException ex) {

			// Debug

			if ( Debug.EnableError && m_sess.hasDebug(SMBSrvSession.DBG_FILEIO))
				m_sess.debugPrintln("File Read Error [" + netFile.getFileId() + "] : " + ex.toString());

			// Failed to read the file

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.HRDReadFault, SMBStatus.ErrHrd);
			return;
		}

		// Return the data block

		respPkt.setAndXCommand(0xFF); 		// no chained command
		respPkt.setParameter(1, 0);
		respPkt.setParameter(2, 0xFFFF); 	// bytes remaining, for pipes only
		respPkt.setParameter(3, 0); 		// data compaction mode
		respPkt.setParameter(4, 0); 		// reserved
		respPkt.setParameter(5, rdlen); 	// data length
		respPkt.setParameter(6, dataPos - RFCNetBIOSProtocol.HEADER_LEN);
											// offset to data

		// Clear the reserved parameters

		for (int i = 7; i < 12; i++)
			respPkt.setParameter(i, 0);

		// Set the byte count

		respPkt.setByteCount((dataPos + rdlen) - respPkt.getByteOffset());

		// Send the read andX response

		m_sess.sendResponseSMB(respPkt);
	}

	/**
	 * Process the file read MPX request.
	 * 
	 * @param smbPkt SMBSrvPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	protected final void procReadMPX(SMBSrvPacket smbPkt)
		throws IOException, SMBSrvException {

		// Check that the received packet looks like a valid read andX request

		if ( smbPkt.checkPacketIsValid(8, 0) == false) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.SRVUnrecognizedCommand, SMBStatus.ErrSrv);
			return;
		}

		// Get the tree connection details

		TreeConnection conn = m_sess.findTreeConnection(smbPkt);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.SRVInvalidTID, SMBStatus.ErrSrv);
			return;
		}

		// Check if the user has the required access permission

		if ( conn.hasReadAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
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

		int fid      = smbPkt.getParameter(0);
		int offset   = smbPkt.getParameterLong(1);
		int maxCount = smbPkt.getParameter(3);

		NetworkFile netFile = conn.findFile(fid);

		if ( netFile == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidHandle, SMBStatus.ErrDos);
			return;
		}

		// Debug

		if ( Debug.EnableDbg && m_sess.hasDebug(SMBSrvSession.DBG_FILEIO))
			Debug.println("File ReadMPX [" + netFile.getFileId() + "] : Size=" + maxCount + " ,Pos=" + offset + ",MaxCount="
					+ maxCount);

		// Get the maximum buffer size the client allows

		int clientMaxSize = m_sess.getClientMaximumBufferSize();

		// Read data from the file

		SMBSrvPacket respPkt = smbPkt;
		byte[] buf = respPkt.getBuffer();
		int dataPos = 0;
		int rdlen = 0;
		int rdRemaining = maxCount;

		try {

			// Access the disk interface that is associated with the shared device

			DiskInterface disk = (DiskInterface) conn.getSharedDevice().getInterface();

			// Check if the read data will fit into the current packet
			
			if ( smbPkt.getBufferLength() < clientMaxSize) {
				
				// Allocate a new packet for the responses
				
				respPkt = m_sess.getPacketPool().allocatePacket( clientMaxSize, smbPkt);
			
				// Switch to the new buffer
				
				buf = respPkt.getBuffer();
			}
			
			// Set the returned parameter count so that the byte offset can be calculated

			respPkt.setParameterCount(8);
			dataPos = respPkt.getByteOffset();

			// Calculate the maximum read size to return

			clientMaxSize -= dataPos;

			// Loop until all required data has been read

			while (rdRemaining > 0) {

				// Check if the requested data length will fit into the buffer

				rdlen = rdRemaining;
				if ( rdlen > clientMaxSize)
					rdlen = clientMaxSize;

				// Read from the file

				rdlen = disk.readFile(m_sess, conn, netFile, buf, dataPos, rdlen, offset);

				// Build the reply packet

				respPkt.setParameterLong(0, offset);
				respPkt.setParameter(2, maxCount);
				respPkt.setParameter(3, 0xFFFF);
				respPkt.setParameterLong(4, 0);
				respPkt.setParameter(6, rdlen);
				respPkt.setParameter(7, dataPos - RFCNetBIOSProtocol.HEADER_LEN);

				respPkt.setByteCount(rdlen);

				// Update the read offset and remaining read length

				if ( rdlen > 0) {
					rdRemaining -= rdlen;
					offset += rdlen;
				}
				else
					rdRemaining = 0;

				// Set the response command

				respPkt.setCommand(PacketType.ReadMpxSecondary);

				// Debug

				if ( Debug.EnableDbg && m_sess.hasDebug(SMBSrvSession.DBG_FILEIO))
					Debug.println("File ReadMPX Secondary [" + netFile.getFileId() + "] : Size=" + rdlen + " ,Pos=" + offset);

				// Send the packet

				m_sess.sendResponseSMB(smbPkt);
			}
		}
		catch (InvalidDeviceInterfaceException ex) {

			// Failed to get/initialize the disk interface

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
		}
		catch (AccessDeniedException ex) {

			// No access to file, or file is a directory
			//      
			// Debug

			if ( Debug.EnableDbg && m_sess.hasDebug(SMBSrvSession.DBG_FILEIO))
				Debug.println("File ReadMPX Error [" + netFile.getFileId() + "] : " + ex.toString());

			// Failed to read the file

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}
		catch (IOException ex) {

			// Debug

			if ( Debug.EnableError)
				Debug.println("File ReadMPX Error [" + netFile.getFileId() + "] : " + ex);

			// Failed to read the file

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.HRDReadFault, SMBStatus.ErrHrd);
			return;
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
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}

		// Get the tree id from the received packet and validate that it is a valid
		// connection id.

		int treeId = smbPkt.getTreeId();
		TreeConnection conn = vc.findConnection(treeId);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Check if the user has the required access permission

		if ( conn.hasWriteAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
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
		if ( oldName == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
		}

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_FILE))
			m_sess.debugPrintln("File Rename [" + treeId + "] old name=" + oldName + ", new name=" + newName);

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
		catch (IOException ex) {

			// Failed to open the file

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
			return;
		}

		// Build the rename file response

		smbPkt.setParameterCount(0);
		smbPkt.setByteCount(0);

		// Send the response packet

		m_sess.sendResponseSMB(smbPkt);
	}

	/**
	 * Process the SMB session setup request.
	 * 
	 * @param smbPkt SMBSrvPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	protected void procSessionSetup(SMBSrvPacket smbPkt)
		throws SMBSrvException, IOException, TooManyConnectionsException {

		// Extract the client details from the session setup request

		int dataPos = smbPkt.getByteOffset();
		int dataLen = smbPkt.getByteCount();
		byte[] buf  = smbPkt.getBuffer();

		// Extract the session details

		int maxBufSize = smbPkt.getParameter(2);
		int maxMpx = smbPkt.getParameter(3);
		int vcNum  = smbPkt.getParameter(4);

		// Extract the password string

		byte[] pwd = null;
		int pwdLen = smbPkt.getParameter(7);

		if ( pwdLen > 0) {
			pwd = new byte[pwdLen];
			for (int i = 0; i < pwdLen; i++)
				pwd[i] = buf[dataPos + i];
			dataPos += pwdLen;
			dataLen -= pwdLen;
		}

		// Extract the user name string

		String user = DataPacker.getString(buf, dataPos, dataLen);
		if ( user == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
		}
		else {

			// Update the buffer pointers

			dataLen -= user.length() + 1;
			dataPos += user.length() + 1;
		}

		// Extract the clients primary domain name string

		String domain = "";

		if ( dataLen > 0) {

			// Extract the callers domain name

			domain = DataPacker.getString(buf, dataPos, dataLen);
			if ( domain == null) {
				m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
				return;
			}
			else {

				// Update the buffer pointers

				dataLen -= domain.length() + 1;
				dataPos += domain.length() + 1;
			}
		}

		// Extract the clients native operating system

		String clientOS = "";

		if ( dataLen > 0) {

			// Extract the callers operating system name

			clientOS = DataPacker.getString(buf, dataPos, dataLen);
			if ( clientOS == null) {
				m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
				return;
			}
		}

		// DEBUG

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_NEGOTIATE))
			m_sess.debugPrintln("Session setup from user=" + user + ", password=" + pwd + ", domain=" + domain + ", os="
					+ clientOS + ", VC=" + vcNum + ", maxBuf=" + maxBufSize + ", maxMpx=" + maxMpx);

		// Store the client maximum buffer size and maximum multiplexed requests count

		m_sess.setClientMaximumBufferSize(maxBufSize);
		m_sess.setClientMaximumMultiplex(maxMpx);

		// Create the client information and store in the session

		ClientInfo client = ClientInfo.createInfo(user, pwd);
		client.setDomain(domain);
		client.setOperatingSystem(clientOS);
		if ( m_sess.hasRemoteAddress())
			client.setClientAddress(m_sess.getRemoteAddress().getHostAddress());

		if ( m_sess.getClientInformation() == null || m_sess.getClientInformation().getUserName().length() == 0) {

			// Set the session client details

			m_sess.setClientInformation(client);
		}
		else {

			// Get the current client details from the session

			ClientInfo curClient = m_sess.getClientInformation();

			if ( curClient.getUserName() == null || curClient.getUserName().length() == 0) {

				// Update the client information

				m_sess.setClientInformation(client);
			}
			else {

				// DEBUG

				if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_NEGOTIATE))
					m_sess.debugPrintln("Session already has client information set");
			}
		}

		// Authenticate the user, if the server is using user mode security

		ICifsAuthenticator auth = getSession().getSMBServer().getCifsAuthenticator();
		boolean isGuest = false;

		if ( auth != null && auth.getAccessMode() == ICifsAuthenticator.USER_MODE) {

			// Validate the user

			int sts = auth.authenticateUser(client, m_sess, ICifsAuthenticator.LANMAN);
			if ( sts > 0 && (sts & ICifsAuthenticator.AUTH_GUEST) != 0)
				isGuest = true;
			else if ( sts != ICifsAuthenticator.AUTH_ALLOW) {

				// Invalid user, reject the session setup request

				m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
				return;
			}
		}

		// Set the guest flag for the client and logged on status

		client.setGuest(isGuest);
		getSession().setLoggedOn(true);

		// If the user is logged on then allocate a virtual circuit

		int uid = 0;

		// Create a virtual circuit for the new logon

		VirtualCircuit vc = new VirtualCircuit(vcNum, client);
		uid = m_sess.addVirtualCircuit(vc);

		if ( uid == VirtualCircuit.InvalidUID) {

			// DEBUG

			if ( Debug.EnableDbg && m_sess.hasDebug(SMBSrvSession.DBG_NEGOTIATE))
				Debug.println("Failed to allocate UID for virtual circuit, " + vc);

			// Failed to allocate a UID

			throw new SMBSrvException( SMBStatus.NTLogonFailure, SMBStatus.ErrDos, SMBStatus.DOSAccessDenied);
		}
		else if ( Debug.EnableDbg && m_sess.hasDebug(SMBSrvSession.DBG_NEGOTIATE)) {

			// DEBUG

			Debug.println("Allocated UID=" + uid + " for VC=" + vc);
		}

		// Check if there is a chained commmand with the session setup request (usually a TreeConnect)
		
		SMBSrvPacket respPkt = smbPkt;
		boolean andX = false;
		
		if ( smbPkt.hasAndXCommand() && dataPos < smbPkt.getReceivedLength()) {

			// Allocate a new packet for the response
			
			respPkt = m_sess.getPacketPool().allocatePacket( smbPkt.getLength(), smbPkt);
			
			// Indicate that there is an AndX chained command to process
			
			andX = true;
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
		respPkt.setFlags2(SMBSrvPacket.FLG2_LONGFILENAMES);

		// Pack the OS, dialect and domain name strings.

		int pos = respPkt.getByteOffset();
		buf = respPkt.getBuffer();

		pos = DataPacker.putString("Java", buf, pos, true);
		pos = DataPacker.putString("Alfresco AIFS Server " + m_sess.getServer().isVersion(), buf, pos, true);
		pos = DataPacker.putString(m_sess.getSMBServer().getCIFSConfiguration().getDomainName(), buf, pos, true);

		respPkt.setByteCount(pos - respPkt.getByteOffset());

		// Check if there is a chained command, or commands

		if ( andX ==true) {

			// Process any chained commands, AndX

			pos = procAndXCommands(smbPkt, null);
		}
		else {

			// Indicate that there are no chained replies

			respPkt.setAndXCommand(SMBSrvPacket.NO_ANDX_CMD);
		}

		// Send the negotiate response

		m_sess.sendResponseSMB(respPkt, pos);

		// Update the session state

		m_sess.setState(SMBSrvSessionState.SMBSESSION);

		// Find the virtual circuit allocated, this will set the per-thread ClientInfo on the session
		
		m_sess.findVirtualCircuit( respPkt.getUserId());
		
		// Notify listeners that a user has logged onto the session

		m_sess.getSMBServer().sessionLoggedOn(m_sess);
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

		if ( smbPkt.checkPacketIsValid(15, 0) == false) {

			// Not enough parameters for a valid transact2 request

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.NTErr);
			return;
		}

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());
		if ( vc == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}

		// Get the tree id from the received packet and validate that it is a valid
		// connection id.

		int treeId = smbPkt.getTreeId();
		TreeConnection conn = vc.findConnection(treeId);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.NTErr);
			return;
		}

		// Check if the user has the required access permission

		if ( conn.hasReadAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

		// Create a transact packet using the received SMB packet

		SMBSrvTransPacket tranPkt = new SMBSrvTransPacket(smbPkt.getBuffer());

		// Create a transact buffer to hold the transaction setup, parameter and data blocks

		SrvTransactBuffer transBuf = null;
		int subCmd = tranPkt.getSubFunction();

		if ( tranPkt.getTotalParameterCount() == tranPkt.getParameterBlockCount()
				&& tranPkt.getTotalDataCount() == tranPkt.getDataBlockCount()) {

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
			transBuf.appendParameter(buf, tranPkt.getParameterBlockOffset(), tranPkt.getParameterBlockCount());
			transBuf.appendData(buf, tranPkt.getDataBlockOffset(), tranPkt.getDataBlockCount());
		}

		// Set the return data limits for the transaction

		transBuf.setReturnLimits(tranPkt.getMaximumReturnSetupCount(), tranPkt.getMaximumReturnParameterCount(), tranPkt.getMaximumReturnDataCount());

		// Check for a multi-packet transaction, for a multi-packet transaction we just acknowledge
		// the receive with an empty response SMB

		if ( transBuf.isMultiPacket()) {

			// Save the partial transaction data

			vc.setTransaction(transBuf);

			// Send an intermediate acknowedgement response

			m_sess.sendSuccessResponseSMB( smbPkt);
			return;
		}

		// Check if the transaction is on the IPC$ named pipe, the request requires special
		// processing

		if ( conn.getSharedDevice().getType() == ShareType.ADMINPIPE) {
			IPCHandler.procTransaction(vc, transBuf, m_sess, smbPkt);
			return;
		}

		// DEBUG

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_TRAN))
			m_sess.debugPrintln("Transaction [" + treeId + "] tbuf=" + transBuf);

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

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.NTErr);
			return;
		}

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());
		if ( vc == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}

		// Get the tree id from the received packet and validate that it is a valid
		// connection id.

		int treeId = smbPkt.getTreeId();
		TreeConnection conn = vc.findConnection(treeId);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.NTErr);
			return;
		}

		// Check if the user has the required access permission

		if ( conn.hasReadAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
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

			// Check if the transaction is on the IPC$ named pipe, the request requires special
			// processing

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

			m_sess.sendSuccessResponseSMB(smbPkt);
		}
	}

	/**
	 * Process a transaction buffer
	 * 
	 * @param tbuf TransactBuffer
	 * @param smbPkt SMBSrvPacket
	 * @exception IOException If a network error occurs
	 * @exception SMBSrvException If an SMB error occurs
	 */
	private final void processTransactionBuffer(SrvTransactBuffer tbuf, SMBSrvPacket smbPkt)
		throws IOException, SMBSrvException {

		// Get the transaction sub-command code and validate

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

		// Unknown transact2 command

		default:

			// Return an unrecognized command error

			if ( Debug.EnableError)
				m_sess.debugPrintln("Error Transact2 Command = 0x" + Integer.toHexString(tbuf.getFunction()));
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.SRVUnrecognizedCommand, SMBStatus.ErrSrv);
			break;
		}
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
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
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

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
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

		// Check if the search path is valid

		if ( srchPath == null || srchPath.length() == 0) {

			// Invalid search request

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
		}

		// Access the shared device disk interface

		SearchContext ctx = null;
		DiskInterface disk = null;
		int searchId = -1;

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

			// Debug

			if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_SEARCH))
				m_sess.debugPrintln("Start trans search [" + searchId + "] - " + srchPath + ", attr=0x"
						+ Integer.toHexString(srchAttr) + ", maxFiles=" + maxFiles + ", infoLevel=" + infoLevl + ", flags=0x"
						+ Integer.toHexString(srchFlag));

			// Start a new search

			ctx = disk.startSearch(m_sess, conn, srchPath, srchAttr);
			if ( ctx != null) {

				// Store details of the search in the context

				ctx.setTreeId(treeId);
				ctx.setMaximumFiles(maxFiles);
			}
			else {

				// Failed to start the search, return a no more files error

				m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSFileNotFound, SMBStatus.ErrDos);
				return;
			}

			// Save the search context

			vc.setSearchContext(searchId, ctx);

			// Create the reply transact buffer

			SrvTransactBuffer replyBuf = new SrvTransactBuffer(tbuf);
			DataBuffer dataBuf = replyBuf.getDataBuffer();

			// Determine the maximum return data length

			int maxLen = replyBuf.getReturnDataLimit();

			// Check if resume keys are required

			boolean resumeReq = (srchFlag & FindFirstNext.ReturnResumeKey) != 0 ? true : false;

			// Loop until we have filled the return buffer or there are no more files to return

			int fileCnt = 0;
			int packLen = 0;
			int lastNameOff = 0;

			boolean pktDone = false;
			boolean searchDone = false;

			FileInfo info = new FileInfo();

			// If this is a wildcard search then add the '.' and '..' entries

			if ( WildCard.containsWildcards(srchPath)) {

				// Pack the '.' file information

				if ( resumeReq == true) {
					dataBuf.putInt(-1);
					maxLen -= 4;
				}

				lastNameOff = dataBuf.getPosition();
				FileInfo dotInfo = new FileInfo(".", 0, FileAttribute.Directory);
				dotInfo.setFileId(dotInfo.getFileName().hashCode());
				dotInfo.setCreationDateTime(DotFileDateTime);
				dotInfo.setModifyDateTime(DotFileDateTime);
				dotInfo.setAccessDateTime(DotFileDateTime);

				packLen = FindInfoPacker.packInfo(dotInfo, dataBuf, infoLevl, tbuf.isUnicode());

				// Update the file count for this packet, update the remaining buffer length

				fileCnt++;
				maxLen -= packLen;

				// Pack the '..' file information

				if ( resumeReq == true) {
					dataBuf.putInt(-2);
					maxLen -= 4;
				}

				lastNameOff = dataBuf.getPosition();
				dotInfo.setFileName("..");
				dotInfo.setFileId(dotInfo.getFileName().hashCode());

				packLen = FindInfoPacker.packInfo(dotInfo, dataBuf, infoLevl, tbuf.isUnicode());

				// Update the file count for this packet, update the remaining buffer length

				fileCnt++;
				maxLen -= packLen;
			}

			// Pack the file information records

			while (pktDone == false && fileCnt < maxFiles) {

				// Get file information from the search

				if ( ctx.nextFileInfo(info) == false) {

					// No more files

					pktDone = true;
					searchDone = true;
				}

				// Check if the file information will fit into the return buffer

				else if ( FindInfoPacker.calcInfoSize(info, infoLevl, false, true) <= maxLen) {

					// Pack a dummy resume key, if required

					if ( resumeReq) {
						dataBuf.putZeros(4);
						maxLen -= 4;
					}

					// Save the offset to the last file information structure

					lastNameOff = dataBuf.getPosition();

					// Mask the file attributes

					info.setFileAttributes(info.getFileAttributes() & StandardAttributes);

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
				}
			}

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
				m_sess.debugPrintln("Search [" + searchId + "] Returned " + fileCnt + " files, moreFiles=" + ctx.hasMoreFiles());

			// Check if the search is complete

			if ( searchDone == true) {

				// Debug

				if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_SEARCH))
					m_sess.debugPrintln("End start search [" + searchId + "] (Search complete)");

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
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
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

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
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

				if ( Debug.EnableError && m_sess.hasDebug(SMBSrvSession.DBG_SEARCH))
					m_sess.debugPrintln("Search context null - [" + searchId + "]");

				// Invalid search handle

				m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSNoMoreFiles, SMBStatus.ErrDos);
				return;
			}

			// Debug

			if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_SEARCH))
				m_sess.debugPrintln("Continue search [" + searchId + "] - " + resumeName + ", maxFiles=" + maxFiles
						+ ", infoLevel=" + infoLevl + ", flags=0x" + Integer.toHexString(srchFlag));

			// Create the reply transaction buffer

			SrvTransactBuffer replyBuf = new SrvTransactBuffer(tbuf);
			DataBuffer dataBuf = replyBuf.getDataBuffer();

			// Determine the maximum return data length

			int maxLen = replyBuf.getReturnDataLimit();

			// Check if resume keys are required

			boolean resumeReq = (srchFlag & FindFirstNext.ReturnResumeKey) != 0 ? true : false;

			// Loop until we have filled the return buffer or there are no more files to return

			int fileCnt = 0;
			int packLen = 0;
			int lastNameOff = 0;

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

					// Pack a dummy resume key, if required

					if ( resumeReq)
						dataBuf.putZeros(4);

					// Save the offset to the last file information structure

					lastNameOff = dataBuf.getPosition();

					// Mask the file attributes

					info.setFileAttributes(info.getFileAttributes() & StandardAttributes);

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
				m_sess.debugPrintln("Search [" + searchId + "] Returned " + fileCnt + " files, moreFiles=" + ctx.hasMoreFiles());

			// Check if the search is complete

			if ( searchDone == true) {

				// Debug

				if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_SEARCH))
					m_sess.debugPrintln("End start search [" + searchId + "] (Search complete)");

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
	 * Process a transact2 query file information (via handle) request.
	 * 
	 * @param tbuf Transaction request details
	 * @param smbPkt SMBSrvPacket
	 * @exception IOException If an I/O error occurs
	 * @exception SMBSrvException SMB protocol exception
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

		if ( Debug.EnableDbg && m_sess.hasDebug(SMBSrvSession.DBG_INFO))
			Debug.println("Query File - level=0x" + Integer.toHexString(infoLevl) + ", fid=" + fid + ", stream="
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

			// Create a data buffer using the SMB packet. The response should always fit into a
			// single
			// reply packet.

			DataBuffer replyBuf = new DataBuffer(buf, dataPos, buf.length - dataPos);

			// Check if the virtual filesystem supports streams, and streams are enabled

			boolean streams = false;

			if ( disk instanceof NTFSStreamsInterface) {

				// Check if NTFS streams are enabled

				NTFSStreamsInterface ntfsStreams = (NTFSStreamsInterface) disk;
				streams = ntfsStreams.hasStreamsEnabled(m_sess, conn);
			}

			// Check for the file streams information level

			int dataLen = 0;

			if ( streams == true && (infoLevl == FileInfoLevel.PathFileStreamInfo || infoLevl == FileInfoLevel.NTFileStreamInfo)) {

				// Debug

				if ( Debug.EnableDbg && m_sess.hasDebug(SMBSrvSession.DBG_STREAMS))
					Debug.println("Get NTFS streams list fid=" + fid + ", name=" + netFile.getFullName());

				// Get the list of streams from the share driver

				NTFSStreamsInterface ntfsStreams = (NTFSStreamsInterface) disk;
				StreamInfoList streamList = ntfsStreams.getStreamList(m_sess, conn, netFile.getFullName());

				if ( streamList == null) {
					m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectNotFound, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
					return;
				}

				// Pack the file streams information into the return data packet

				dataLen = QueryInfoPacker.packStreamFileInfo(streamList, replyBuf, true);
			}
			else {

				// Get the file information

				FileInfo fileInfo = disk.getFileInformation(m_sess, conn, netFile.getFullNameStream());

				if ( fileInfo == null) {
					m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectNotFound, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
					return;
				}

				// Mask the file attributes

				fileInfo.setFileAttributes(fileInfo.getFileAttributes() & StandardAttributes);

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
		catch (AccessDeniedException ex) {

			// Not allowed to access the file/folder

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTAccessDenied, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
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
	}

	/**
	 * Process a transact2 file system query request.
	 * 
	 * @param tbuf Transaction request details
	 * @param smbtPkt SMBSrvPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	protected final void procTrans2QueryFileSys(SrvTransactBuffer tbuf, SMBSrvPacket smbPkt)
		throws IOException, SMBSrvException {

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());
		if ( vc == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
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

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
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
				DiskInfoPacker.packFsDevice(0, 0, replyBuf);
				break;

			// Filesystem attribute information

			case DiskInfoPacker.InfoFsAttribute:
				DiskInfoPacker.packFsAttribute(0, 255, "JLAN", tbuf.isUnicode(), replyBuf);
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
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
			return;
		}

		// Get the tree connection details

		int treeId = smbPkt.getTreeId();
		TreeConnection conn = vc.findConnection(treeId);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.NTErr);
			return;
		}

		// Check if the user has the required access permission

		if ( conn.hasReadAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}

		// Get the query path information level and file/directory name

		DataBuffer paramBuf = tbuf.getParameterBuffer();

		int infoLevl = paramBuf.getShort();
		paramBuf.skipBytes(4);

		String path = paramBuf.getString(tbuf.isUnicode());

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
			int dataPos = prmPos; // no parameters returned

			// Create a data buffer using the SMB packet. The response should always fit into a
			// single
			// reply packet.

			DataBuffer replyBuf = new DataBuffer(buf, dataPos, buf.length - dataPos);

			// Get the file information

			FileInfo fileInfo = disk.getFileInformation(m_sess, conn, path);

			if ( fileInfo == null) {
				m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectNotFound, SMBStatus.NTErr);
				return;
			}

			// Mask the file attributes

			fileInfo.setFileAttributes(fileInfo.getFileAttributes() & StandardAttributes);

			// Pack the file information into the return data packet

			int dataLen = QueryInfoPacker.packInfo(fileInfo, replyBuf, infoLevl, true);

			// Check if any data was packed, if not then the information level is not supported

			if ( dataLen == 0) {
				m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.NTErr);
				return;
			}

			SMBSrvTransPacket.initTransactReply(smbPkt, 0, prmPos, dataLen, dataPos);
			smbPkt.setByteCount(replyBuf.getPosition() - smbPkt.getByteOffset());

			// Send the transact reply

			m_sess.sendResponseSMB(smbPkt);
		}
		catch (FileNotFoundException ex) {

			// Requested file does not exist

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTObjectNotFound, SMBStatus.NTErr);
			return;
		}
		catch (InvalidDeviceInterfaceException ex) {

			// Failed to get/initialize the disk interface

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.NTErr);
			return;
		}
		catch (UnsupportedInfoLevelException ex) {

			// Requested information level is not supported

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.NTErr);
			return;
		}
	}

	/**
	 * Process the SMB tree connect request.
	 * 
	 * @param smbPkt SMBSrvPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 * @exception TooManyConnectionsException
	 */
	protected void procTreeConnectAndX(SMBSrvPacket smbPkt)
		throws SMBSrvException, TooManyConnectionsException, IOException {

		// Check that the received packet looks like a valid tree connect request

		if ( smbPkt.checkPacketIsValid(4, 3) == false) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.SRVUnrecognizedCommand, SMBStatus.ErrSrv);
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

		// Get the data bytes position and length

		int dataPos = smbPkt.getByteOffset();
		int dataLen = smbPkt.getByteCount();
		byte[] buf  = smbPkt.getBuffer();

		// Extract the password string

		String pwd = null;

		if ( pwdLen > 0) {
			pwd = new String(buf, dataPos, pwdLen);
			dataPos += pwdLen;
			dataLen -= pwdLen;
		}

		// Extract the requested share name, as a UNC path

		String uncPath = DataPacker.getString(buf, dataPos, dataLen);
		if ( uncPath == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
		}

		// Extract the service type string

		dataPos += uncPath.length() + 1; // null terminated
		dataLen -= uncPath.length() + 1; // null terminated

		String service = DataPacker.getString(buf, dataPos, dataLen);
		if ( service == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
		}

		// Convert the service type to a shared device type, client may specify '?????' in which
		// case we ignore the error.

		int servType = ShareType.ServiceAsType(service);
		if ( servType == ShareType.UNKNOWN && service.compareTo("?????") != 0) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
		}

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_TREE))
			m_sess.debugPrintln("Tree Connect AndX - " + uncPath + ", " + service);

		// Parse the requested share name

		PCShare share = null;

		try {
			share = new PCShare(uncPath);
		}
		catch (InvalidUNCPathException ex) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
		}

		// Map the IPC$ share to the admin pipe type

		if ( servType == ShareType.NAMEDPIPE && share.getShareName().compareTo("IPC$") == 0)
			servType = ShareType.ADMINPIPE;

		// Find the requested shared device

		SharedDevice shareDev = null;

		try {

			// Get/create the shared device

			shareDev = m_sess.getSMBServer().findShare(share.getNodeName(), share.getShareName(), servType, getSession(), true);
		}
		catch (InvalidUserException ex) {

			// Return a logon failure status

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
			return;
		}
		catch (Exception ex) {

			// Return a general status, bad network name

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.SRVInvalidNetworkName, SMBStatus.ErrSrv);
			return;
		}

		// Check if the share is valid

		if ( shareDev == null || (servType != ShareType.UNKNOWN && shareDev.getType() != servType)) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidDrive, SMBStatus.ErrDos);
			return;
		}

		// Authenticate the share connection depending upon the security mode the server is running
		// under

		ICifsAuthenticator auth = getSession().getSMBServer().getCifsAuthenticator();
		int filePerm = FileAccess.Writeable;

		if ( auth != null) {

			// Validate the share connection

			filePerm = auth.authenticateShareConnect(m_sess.getClientInformation(), shareDev, pwd, m_sess);
			if ( filePerm < 0) {

				// Invalid share connection request

				m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.SRVNoAccessRights, SMBStatus.ErrSrv);
				return;
			}
		}

		// Allocate a tree id for the new connection

		int treeId = vc.addConnection(shareDev);
		smbPkt.setTreeId(treeId);

		// Set the file permission that this user has been granted for this share

		TreeConnection tree = vc.findConnection(treeId);
		tree.setPermission(filePerm);

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_TREE))
			m_sess.debugPrintln("Tree Connect AndX - Allocated Tree Id = " + treeId + ", Permission = "
					+ FileAccess.asString(filePerm));

		// Build the tree connect response

		smbPkt.setParameterCount(3);
		smbPkt.setAndXCommand(0xFF); // no chained reply
		smbPkt.setParameter(1, 0);
		smbPkt.setParameter(2, 0);

		// Pack the service type

		int pos = smbPkt.getByteOffset();
		pos = DataPacker.putString(ShareType.TypeAsService(shareDev.getType()), buf, pos, true);
		smbPkt.setByteCount(pos - smbPkt.getByteOffset());

		// Send the response

		m_sess.sendResponseSMB(smbPkt);

		// Inform the driver that a connection has been opened

		if ( tree.getInterface() != null)
			tree.getInterface().treeOpened(m_sess, tree);
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
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.SRVUnrecognizedCommand, SMBStatus.ErrSrv);
			return;
		}

		// Get the virtual circuit for the request

		VirtualCircuit vc = m_sess.findVirtualCircuit(smbPkt.getUserId());
		if ( vc == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.NTInvalidParameter, SMBStatus.SRVNonSpecificError, SMBStatus.ErrSrv);
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

		if ( conn.hasWriteAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
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

		int fid     = smbPkt.getParameter(2);
		int offset  = smbPkt.getParameterLong(3);
		int dataLen = smbPkt.getParameter(10);
		int dataPos = smbPkt.getParameter(11) + RFCNetBIOSProtocol.HEADER_LEN;

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

			// Write to the file

			wrtlen = disk.writeFile(m_sess, conn, netFile, buf, dataPos, dataLen, offset);
		}
		catch (InvalidDeviceInterfaceException ex) {

			// Failed to get/initialize the disk interface

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
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

		smbPkt.setParameterCount(6);
		smbPkt.setAndXCommand(0xFF);
		smbPkt.setParameter(1, 0);
		smbPkt.setParameter(2, wrtlen);
		smbPkt.setParameter(3, 0); // remaining byte count for pipes only
		smbPkt.setParameter(4, 0); // reserved
		smbPkt.setParameter(5, 0); // "
		smbPkt.setByteCount(0);

		// Send the write response

		m_sess.sendResponseSMB(smbPkt);
	}

	/**
	 * Process the file write MPX request.
	 * 
	 * @param smbPkt SMBSrvPacket
	 * @exception IOException
	 * @exception SMBSrvException
	 */
	protected final void procWriteMPX(SMBSrvPacket smbPkt)
		throws IOException, SMBSrvException {

		// Check that the received packet looks like a valid write andX request

		if ( smbPkt.checkPacketIsValid(12, 0) == false) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.SRVUnrecognizedCommand, SMBStatus.ErrSrv);
			return;
		}

		// Get the tree connection details

		TreeConnection conn = m_sess.findTreeConnection(smbPkt);

		if ( conn == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.SRVInvalidTID, SMBStatus.ErrSrv);
			return;
		}

		// Check if the user has the required access permission

		if ( conn.hasWriteAccess() == false) {

			// User does not have the required access rights

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSAccessDenied, SMBStatus.ErrDos);
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

		int fid     = smbPkt.getParameter(0);
		int totLen  = smbPkt.getParameter(1);
		int offset  = smbPkt.getParameterLong(3);
		int dataLen = smbPkt.getParameter(10);
		int dataPos = smbPkt.getParameter(11) + RFCNetBIOSProtocol.HEADER_LEN;

		NetworkFile netFile = conn.findFile(fid);

		if ( netFile == null) {
			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidHandle, SMBStatus.ErrDos);
			return;
		}

		// Debug

		if ( Debug.EnableDbg && m_sess.hasDebug(SMBSrvSession.DBG_FILEIO))
			Debug.println("File WriteMPX [" + netFile.getFileId() + "] : Size=" + dataLen + " ,Pos=" + offset + ", TotLen="
					+ totLen);

		// Write data to the file

		byte[] buf = smbPkt.getBuffer();
		int wrtlen = 0;

		// Access the disk interface and write to the file

		try {
			// Access the disk interface that is associated with the shared device

			DiskInterface disk = (DiskInterface) conn.getSharedDevice().getInterface();

			// Write to the file

			wrtlen = disk.writeFile(m_sess, conn, netFile, buf, dataPos, dataLen, offset);

			// Return the initial MPX response

			smbPkt.setParameterCount(1);
			smbPkt.setAndXCommand(0xFF);
			smbPkt.setParameter(1, 0xFFFF);
			smbPkt.setByteCount(0);

			// Send the write response

			m_sess.sendResponseSMB(smbPkt);

			// Update the remaining data length and write offset

			totLen -= wrtlen;
			offset += wrtlen;

			int rxlen = 0;
			SMBSrvPacket curPkt = null;

			while (totLen > 0) {

				// Release the associated packet
				
				if ( smbPkt.hasAssociatedPacket()) {
					
					// Release the current associated packet back to the pool, and clear
					
					m_sess.getPacketPool().releasePacket( smbPkt.getAssociatedPacket());
					smbPkt.setAssociatedPacket( null);
				}
				
				// Receive the next write packet

				curPkt = m_sess.getPacketHandler().readPacket();
				smbPkt.setAssociatedPacket( curPkt);
				
				// Make sure it is a secondary WriteMPX type packet

				if ( smbPkt.getCommand() != PacketType.WriteMpxSecondary)
					throw new IOException("Write MPX invalid packet type received");

				// Get the write length and buffer offset

				dataLen = smbPkt.getParameter(6);
				dataPos = smbPkt.getParameter(7) + RFCNetBIOSProtocol.HEADER_LEN;

				// Debug

				if ( Debug.EnableDbg && m_sess.hasDebug(SMBSrvSession.DBG_FILEIO))
					Debug.println("File WriteMPX Secondary [" + netFile.getFileId() + "] : Size=" + dataLen + " ,Pos=" + offset);

				// Write the block of data

				wrtlen = disk.writeFile(m_sess, conn, netFile, buf, dataPos, dataLen, offset);

				// Update the remaining data length and write offset

				totLen -= wrtlen;
				offset += wrtlen;
			}
		}
		catch (InvalidDeviceInterfaceException ex) {

			// Failed to get/initialize the disk interface

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.DOSInvalidData, SMBStatus.ErrDos);
			return;
		}
		catch (IOException ex) {

			// Debug

			if ( Debug.EnableError)
				Debug.println("File WriteMPX Error [" + netFile.getFileId() + "] : " + ex);

			// Failed to read the file

			m_sess.sendErrorResponseSMB( smbPkt, SMBStatus.HRDWriteFault, SMBStatus.ErrHrd);
			return;
		}
	}

	/**
	 * Run the LanMan protocol handler
	 * 
	 * @param smbPkt SMBSrvPacket
	 * @return boolean true if the packet was processed, else false
	 * @exception IOException
	 * @exception SMBSrvException
	 * @exception tooManyConnectionsException
	 */
	public boolean runProtocol( SMBSrvPacket smbPkt)
		throws IOException, SMBSrvException, TooManyConnectionsException {

		// Check if the received packet has a valid SMB signature

		if ( smbPkt.checkPacketSignature() == false)
			throw new IOException("Invalid SMB signature");

		// Debug

		if ( Debug.EnableInfo && m_sess.hasDebug(SMBSrvSession.DBG_STATE) &&
				hasChainedCommand(smbPkt))
			m_sess.debugPrintln("AndX Command = 0x" + Integer.toHexString(smbPkt.getAndXCommand()));

		// Reset the byte unpack offset

		smbPkt.resetBytePointer();

		// Determine the SMB command type

		boolean handledOK = true;

		switch (smbPkt.getCommand()) {

			// Session setup
	
			case PacketType.SessionSetupAndX:
				procSessionSetup(smbPkt);
				break;
	
			// Tree connect
	
			case PacketType.TreeConnectAndX:
				procTreeConnectAndX(smbPkt);
				break;
	
			// Transaction2
	
			case PacketType.Transaction2:
			case PacketType.Transaction:
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
	
			// Read a file
	
			case PacketType.ReadAndX:
				procReadAndX(smbPkt);
				break;
	
			// Read MPX
	
			case PacketType.ReadMpx:
				procReadMPX(smbPkt);
				break;
	
			// Write to a file
	
			case PacketType.WriteAndX:
				procWriteAndX(smbPkt);
				break;
	
			// Write MPX
	
			case PacketType.WriteMpx:
				procWriteMPX(smbPkt);
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
	
			// Tree connection (without AndX batching)
	
			case PacketType.TreeConnect:
				super.runProtocol( smbPkt);
				break;
	
			// Rename file
	
			case PacketType.RenameFile:
				procRenameFile(smbPkt);
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
				break;
		}

		// Run any request post processors
		
		runRequestPostProcessors( m_sess);
		
		// Return the handled status

		return handledOK;
	}
}
