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

import org.alfresco.jlan.smb.server.SMBSrvPacket;
import org.alfresco.jlan.smb.server.SMBSrvSession;

/**
 * OpLock Details Interface
 * 
 * <p>Contains the main oplock details and type, and is also used to store a deferred file open
 * request from another session during an oplock break.
 * 
 * @author gkspencer
 */
public interface OpLockDetails {

	/**
	 * Return the oplock type
	 * 
	 * @return int
	 */
	public int getLockType();
	
	/**
	 * Return the share relative path of the locked file
	 * 
	 * @return String
	 */
	public String getPath();
	
	/**
	 * Check if the oplock is on a file or folder
	 * 
	 * @return boolean
	 */
	public boolean isFolder();
	
	/**
	 * Check if there is a deferred session attached to the oplock, this indicates an oplock break is
	 * in progress for this oplock.
	 * 
	 * @return boolean
	 */
	public boolean hasDeferredSession();
	
	/**
	 * Return the deferred session details
	 * 
	 * @return SMBSrvSession
	 */
	public SMBSrvSession getDeferredSession();
	
	/**
	 * Return the deferred CIFS request packet
	 * 
	 * @return SMBSrvPacket
	 */
	public SMBSrvPacket getDeferredPacket();
	
	/**
	 * Return the time that the oplock break was sent to the client
	 * 
	 * @return long
	 */
	public long getOplockBreakTime();
	
	/**
	 * Check if this oplock is still valid, or an oplock break has failed
	 * 
	 * @return boolean
	 */
	public boolean hasOplockBreakFailed();
	
	/**
	 * Check if this is a remote oplock
	 * 
	 * @return boolean
	 */
	public boolean isRemoteLock();
	
	/**
	 * Set the deferred session/packet details, whilst an oplock break is in progress
	 * 
	 * @param deferredSess SMBSrvSession
	 * @param deferredPkt SMBSrvPacket
	 */
	public void setDeferredSession(SMBSrvSession deferredSess, SMBSrvPacket deferredPkt);

	/**
	 * Clear the deferred session/packet details
	 */
	public void clearDeferredSession();
	
	/**
	 * Set the failed oplock break flag, to indicate the client did not respond to the oplock break
	 * request within a reasonable time.
	 */
	public void setOplockBreakFailed();
	
	/**
	 * Request an oplock break
	 * 
	 * @exception IOException
	 */
	public void requestOpLockBreak()
		throws IOException;
	
	/**
	 * Set the lock type
	 * 
	 * @param lockTyp int
	 */
	public void setLockType( int lockTyp);
}
