/*
 * Copyright (C) 2006-2011 Alfresco Software Limited.
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

package org.alfresco.jlan.server.filesys.cache.cluster;

import java.util.HashMap;

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.server.filesys.ExistingOpLockException;
import org.alfresco.jlan.server.filesys.cache.FileState;
import org.alfresco.jlan.server.filesys.pseudo.PseudoFileList;
import org.alfresco.jlan.server.locking.LocalOpLockDetails;
import org.alfresco.jlan.smb.server.SMBSrvPacket;
import org.alfresco.jlan.smb.server.SMBSrvSession;

/**
 * Per Node File State Data Class
 * 
 * <p>Contains per node values for a file state that cannot be stored within the cluster cache or have per node
 * values which cannot be shared.
 *
 * @author gkspencer
 */
public class PerNodeState {

	//	File identifier
	
	private int m_fileId = FileState.UnknownFileId;
	
	//	File data status
	
	private int m_dataStatus = FileState.FILE_LOADWAIT;
	
	//	Cache of various file information
	
	private HashMap<String, Object> m_cache;
	
    // Pseudo file list
    
    private PseudoFileList m_pseudoFiles;
    
    // Filesystem specific object
    
    private Object m_filesysObj;
    
    // Local oplock details
    
    private LocalOpLockDetails m_localOpLock;

	// Session and request packet that have been deferred whilst an oplock break
    // is in progress
	
	private SMBSrvSession m_sess;
	private SMBSrvPacket m_pkt;
    
	// Time that an oplock break was requested
	
	private long m_oplockBreakTime;
	
    /**
     * Default constructor
     */
    public PerNodeState() {
    }
    
	/**
	 * Get the file id
	 * 
	 * @return int
	 */
	public int getFileId() {
		return m_fileId;
	}

	/**
	 * Return the file data status
	 * 
	 * @return int
	 */
	public int getDataStatus() {
		return m_dataStatus;
	}

	/**
	 * Set the file identifier
	 * 
	 * @param id int
	 */
	public void setFileId(int id) {
		m_fileId = id;
	}
	
	/**
	 * Set the file data status
	 * 
	 * @param sts int
	 */
	public void setDataStatus(int sts) {
		m_dataStatus = sts;
	}

    /**
     * Determine if a folder has pseudo files associated with it
     * 
     * @return boolean
     */
    public boolean hasPseudoFiles() {
        if ( m_pseudoFiles != null)
            return m_pseudoFiles.numberOfFiles() > 0;
        return false;
    }
    
    /**
     * Return the pseudo file list
     * 
     * @param createList boolean
     * @return PseudoFileList
     */
    protected PseudoFileList getPseudoFileList( boolean createList) {
    	if ( m_pseudoFiles == null && createList == true)
    		m_pseudoFiles = new PseudoFileList();
        return m_pseudoFiles;
    }
    
    /**
     * Return the filesystem object
     * 
     * @return Object
     */
    public Object getFilesystemObject() {
    	return m_filesysObj;
    }
    
    /**
     * Set the filesystem object
     * 
     * @param filesysObj Object
     */
    public void setFilesystemObject( Object filesysObj) {
    	m_filesysObj = filesysObj;
    }
    
	/**
	 * Return the map of additional attribute objects attached to this file state, and
	 * optionally create the map if it does not exist
	 * 
	 * @param createMap boolean
	 * @return HashMap<String, Object>
	 */
	protected HashMap<String, Object> getAttributeMap( boolean createMap) {
		if ( m_cache == null && createMap == true)
			m_cache = new HashMap<String, Object>();
		return m_cache;
	}
	
	/**
	 * Clear the attributes
	 */
	public final void remoteAllAttributes() {
		if ( m_cache != null) {
			m_cache.clear();
			m_cache = null;
		}
	}
	/**
	 * Check if the file has an active oplock
	 * 
	 * @return boolean
	 */
	public boolean hasOpLock() {
		return m_localOpLock != null ? true : false;
	}

	/**
	 * Return the oplock details
	 * 
	 * @return LocalOpLockDetails
	 */
	public LocalOpLockDetails getOpLock() {
		return m_localOpLock;
	}

	/**
	 * Set the oplock for this file
	 * 
	 * @param oplock LocalOpLockDetails
	 * @exception ExistingOpLockException If there is an active oplock on this file
	 */
	public synchronized void setOpLock(LocalOpLockDetails oplock)
		throws ExistingOpLockException {

		if ( m_localOpLock == null)
			m_localOpLock = oplock;
		else
			throw new ExistingOpLockException();
	}

	/**
	 * Clear the oplock
	 */
	public synchronized void clearOpLock() {
		m_localOpLock = null;
		
		// TEST
		
		if ( hasDeferredSession()) {
			Debug.println( "%%% PerNodeState.clearOpLock() with deferred session/packet");
			Thread.dumpStack();
		}
	}
	
	/**
	 * Check if the path has a deferred session/packet
	 * 
	 * @return boolean
	 */
	public final boolean hasDeferredSession() {
		return m_sess != null ? true : false;
	}
	
	/**
	 * Return the deferred session
	 * 
	 * @return SMBSrvSession
	 */
	public final SMBSrvSession getDeferredSession() {
		return m_sess;
	}
	
	/**
	 * Return the deferred CIFS packet
	 * 
	 * @return SMBSrvPacket
	 */
	public final SMBSrvPacket getDeferredPacket() {
		return m_pkt;
	}
	
	/**
	 * Set the deferred session
	 * 
	 * @param sess SMBSrvSession
	 */
	public final void setDeferredSession( SMBSrvSession sess) {
		m_sess = sess;
	}
	
	/**
	 * Set the deferred packet
	 * 
	 * @param pkt SMBSrvPacket
	 */
	public final void setDeferredPacket( SMBSrvPacket pkt) {
		m_pkt = pkt;
	}
	
	/**
	 * Clear the deferred session/packet details
	 */
	public final void clearDeferredSession() {
		m_sess = null;
		m_pkt  = null;
	}
	
	/**
	 * Return the oplock break time
	 * 
	 * @return long
	 */
	public final long getOplockBreakTime() {
		return m_oplockBreakTime;
	}
	
	/**
	 * Set the oplock break request time
	 */
	public final void setOplockBreakStartTime() {
		m_oplockBreakTime = System.currentTimeMillis();
	}
	
	/**
	 * Return the per node state as a string
	 * 
	 * @return String
	 */
	public String toString() {
		StringBuilder str = new StringBuilder();
		
		str.append( "[FID=");
		str.append( getFileId());
		str.append( ",data=");
		str.append( getDataStatus());
		str.append( ",filesysObj=");
		str.append( getFilesystemObject());
		str.append( ",oplock=");
		str.append( getOpLock());
		if ( getDeferredSession() != null) {
			str.append( ",deferSess=");
			str.append( getDeferredSession().getUniqueId());
			str.append( ", deferPkt=");
			str.append( getDeferredPacket());
		}
		str.append( "]");
		
		return str.toString();
	}
}
