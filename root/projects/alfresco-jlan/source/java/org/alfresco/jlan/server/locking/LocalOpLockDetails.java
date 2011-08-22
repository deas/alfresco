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

package org.alfresco.jlan.server.locking;

import java.io.IOException;

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.smb.LockingAndX;
import org.alfresco.jlan.smb.OpLock;
import org.alfresco.jlan.smb.PacketType;
import org.alfresco.jlan.smb.server.SMBSrvPacket;
import org.alfresco.jlan.smb.server.SMBSrvSession;

/**
 * Local OpLock Details Class
 * 
 * <p>Contains the details of an oplock that is owned by a session on the local node.
 *
 * @author gkspencer
 */
public class LocalOpLockDetails implements OpLockDetails {

	// Oplock type
	
	private int m_type;
	
	// Relative path of oplocked file/folder
	
	private String m_path;
	private boolean m_folder;
	
	// Oplock break deferred file open details
	
	private SMBSrvSession m_deferredSess;
	private SMBSrvPacket m_deferredPkt;
	
	// Time that the oplock break was sent to the client
	
	private long m_opBreakTime;
	
	// Flag to indicate the oplock break timed out
	
	private boolean m_failedBreak;
	
	// Oplock owner details
	
	private SMBSrvSession m_ownerSess;
	private int m_pid;
	private int m_uid;
	private int m_treeId;
	private int m_fileId;
	
	/**
	 * Class constructor
	 *
	 * @param lockTyp int
	 * @param path String
	 * @param sess SMBSrvSession
	 * @param pid int
	 * @param uid int
	 * @param treeId int
	 * @param fileId int
	 * @param folder boolean
	 */
	public LocalOpLockDetails( int lockTyp, String path, SMBSrvSession sess, int pid, int uid, int treeId, int fileId, boolean folder) {
		m_type = lockTyp;
		m_path = path;
		
		m_folder = folder;
		
		m_ownerSess = sess;
		m_pid = pid;
		m_uid = uid;
		m_treeId = treeId;
		m_fileId = fileId;
	}
	
	/**
	 * Class constructor
	 *
	 * @param lockTyp int
	 * @param path String
	 * @param sess SMBSrvSession
	 * @param pkt SMBSrvPacket
	 * @param folder boolean
	 */
	public LocalOpLockDetails( int lockTyp, String path, SMBSrvSession sess, SMBSrvPacket pkt, boolean folder) {
		m_type = lockTyp;
		m_path = path;
		
		m_folder = folder;
		
		m_ownerSess = sess;
		
		m_pid = pkt.getProcessId();
		m_uid = pkt.getUserId();
		m_treeId = pkt.getTreeId();
		
		m_fileId = -1;
	}
	
	/**
	 * Class constructor
	 *
	 * @param lockTyp int
	 * @param path String
	 * @param sess SMBSrvSession
	 * @param pid int
	 * @param treeId int
	 * @param folder boolean
	 */
	public LocalOpLockDetails( int lockTyp, String path, SMBSrvSession sess, int pid, int treeId, boolean folder) {
		m_type = lockTyp;
		m_path = path;
		
		m_folder = folder;
		
		m_ownerSess = sess;
		m_pid = pid;
		m_treeId = treeId;

		m_uid = -1;
		m_fileId = -1;
	}
	
	/**
	 * Return the oplock type
	 * 
	 * @return int
	 */
	public int getLockType() {
		return m_type;
	}
	
	/**
	 * Return the lock owner session
	 * 
	 * @return SMBSrvSession 
	 */
	public final SMBSrvSession getOwnerSession() {
		return m_ownerSess;
	}
	
	/**
	 * Return the owner process id
	 * 
	 * @return int
	 */
	public final int getOwnerPID() {
		return m_pid;
	}
	
	/**
	 * Return the owner virtual circuit id/UID
	 * 
	 * @return int
	 */
	public final int getOwnerUID() {
		return m_uid;
	}
	
	/**
	 * Return the owner tree id
	 * 
	 * @return int
	 */
	public final int getOwnerTreeId() {
		return m_treeId;
	}

	/**
	 * Return the owner file id (FID)
	 * 
	 * @return int
	 */
	public final int getOwnerFileId() {
		return m_fileId;
	}
	
	/**
	 * Return the share relative path of the locked file
	 * 
	 * @return String
	 */
	public String getPath() {
		return m_path;
	}
	
	/**
	 * Check if the oplock is on a file or folder
	 * 
	 * @return boolean
	 */
	public boolean isFolder() {
		return m_folder;
	}
	
	/**
	 * Check if there is a deferred session attached to the oplock, this indicates an oplock break is
	 * in progress for this oplock.
	 * 
	 * @return boolean
	 */
	public boolean hasDeferredSession() {
		return m_deferredSess != null ? true : false;
	}
	
	/**
	 * Return the deferred session details
	 * 
	 * @return SMBSrvSession
	 */
	public SMBSrvSession getDeferredSession() {
		return m_deferredSess;
	}
	
	/**
	 * Return the deferred CIFS request packet
	 * 
	 * @return SMBSrvPacket
	 */
	public SMBSrvPacket getDeferredPacket() {
		return  m_deferredPkt;
	}
	
	/**
	 * Return the time that the oplock break was sent to the client
	 * 
	 * @return long
	 */
	public long getOplockBreakTime() {
		return m_opBreakTime;
	}
	
	/**
	 * Check if this oplock is still valid, or an oplock break has failed
	 * 
	 * @return boolean
	 */
	public boolean hasOplockBreakFailed() {
		return m_failedBreak;
	}
	
	/**
	 * Check if this is a remote oplock
	 * 
	 * @return boolean
	 */
	public boolean isRemoteLock() {
		
		// Always local
		
		return false;
	}
	
	/**
	 * Set the owner file id
	 * 
	 * @param fileId int
	 */
	public final void setOwnerFileId(int fileId) {
		m_fileId = fileId;
	}
	
	/**
	 * Set the deferred session/packet details, whilst an oplock break is in progress
	 * 
	 * @param deferredSess SMBSrvSession
	 * @param deferredPkt SMBSrvPacket
	 */
	public final void setDeferredSession(SMBSrvSession deferredSess, SMBSrvPacket deferredPkt) {
		m_deferredSess = deferredSess;
		m_deferredPkt  = deferredPkt;
		
		// Set the time that the oplock break was sent to the client
		
		m_opBreakTime = System.currentTimeMillis();
		
		// Update the deferred processing count for the CIFS packet
		
		if ( deferredPkt != null)
			deferredPkt.incrementDeferredCount();
	}

	/**
	 * Clear the deferred session/packet details
	 */
	public final void clearDeferredSession() {
		m_deferredSess = null;
		m_deferredPkt  = null;
	}
	
	/**
	 * Set the failed oplock break flag, to indicate the client did not respond to the oplock break
	 * request within a reasonable time.
	 */
	public final void setOplockBreakFailed() {
		m_failedBreak = true;
	}
	
	/**
	 * Set the lock type
	 * 
	 * @param lockTyp int
	 */
	public void setLockType( int lockTyp) {
		m_type = lockTyp;
	}

	/**
	 * Request an oplock break
	 */
	public void requestOpLockBreak()
		throws IOException {
		
		// Check that the session is valid
		
		if ( getOwnerSession() == null || hasOplockBreakFailed())
			return;
		
		// Alocate a packet for the oplock break request to be sent on the owner client session
	
		SMBSrvPacket opBreakPkt = new SMBSrvPacket( 128);
		
		// Build the oplock break request
		
		opBreakPkt.clearHeader();
		
		opBreakPkt.setCommand( PacketType.LockingAndX);
		
		opBreakPkt.setFlags( 0);
		opBreakPkt.setFlags2( 0);
		
		opBreakPkt.setTreeId( getOwnerTreeId());
		opBreakPkt.setProcessId( 0xFFFF);
		opBreakPkt.setUserId( 0);
		opBreakPkt.setMultiplexId( 0xFFFF);
		
		opBreakPkt.setParameterCount( 8);
		opBreakPkt.setAndXCommand( PacketType.NoChainedCommand);
		opBreakPkt.setParameter(1, 0);							// AndX offset
		opBreakPkt.setParameter(2, getOwnerFileId());	// FID
		opBreakPkt.setParameter(3, LockingAndX.OplockBreak + LockingAndX.Level2OpLock);
		opBreakPkt.setParameterLong(4, 0);						// timeout
		opBreakPkt.setParameter(6, 0);							// number of unlocks
		opBreakPkt.setParameter(7, 0);							// number of locks
		
		opBreakPkt.setByteCount( 0);
		
		// Mark the packet as a request packet
		
		opBreakPkt.setRequestPacket( true);
		
		// Send the oplock break to the session that owns the oplock
		
		boolean breakSent = getOwnerSession().sendAsynchResponseSMB( opBreakPkt, opBreakPkt.getLength());
		
		// Set the time the oplock break was sent
		
		m_opBreakTime = System.currentTimeMillis();
		
		// DEBUG
		
		if ( Debug.EnableDbg && getOwnerSession().hasDebug( SMBSrvSession.DBG_OPLOCK))
			getOwnerSession().debugPrintln("Oplock break sent to " + getOwnerSession().getUniqueId() + " async=" + (breakSent ? "Sent" : "Queued"));
	}
	
	/**
	 * Return the oplock details as a string
	 * 
	 * @return String
	 */
	public String toString() {

		StringBuilder str = new StringBuilder();
		
		str.append("[Local Type=");
		str.append(OpLock.getTypeAsString( getLockType()));
		str.append(",");
		str.append(getPath());
		if(getOwnerSession() != null) {
		    str.append(",Owner=");
		    str.append(getOwnerSession().getUniqueId());
		}
		str.append(",PID=");
		str.append(getOwnerPID());
		str.append(",UID=");
		str.append(getOwnerUID());
		str.append(",TreeID=");
		str.append(getOwnerTreeId());
		str.append(",FileId=");
		str.append(getOwnerFileId());
		
		if ( hasDeferredSession()) {
			str.append(",Deferred=");
			str.append(getDeferredSession());
		}
		
		if ( hasOplockBreakFailed())
			str.append("BreakFailed");

		str.append("]");
		
		return str.toString();
	}
}
