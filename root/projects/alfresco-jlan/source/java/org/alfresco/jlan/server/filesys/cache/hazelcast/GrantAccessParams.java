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

package org.alfresco.jlan.server.filesys.cache.hazelcast;

import java.io.Serializable;

import org.alfresco.jlan.server.filesys.AccessMode;
import org.alfresco.jlan.server.filesys.FileOpenParams;
import org.alfresco.jlan.server.filesys.FileStatus;
import org.alfresco.jlan.server.filesys.cache.cluster.ClusterNode;
import org.alfresco.jlan.smb.OpLock;
import org.alfresco.jlan.smb.WinNT;

/**
 * Grant Access Params Class
 * 
 * <p>Contains a subset of the parameters from a FileOpenParams object that are sent to a grant file access
 * remote task on the cluster.
 *
 * @author gkspencer
 */
public class GrantAccessParams implements Serializable {

	// Serialization id
	
	private static final long serialVersionUID = 2L;
	
	//	Cluster node that owns the token
	
	private String m_ownerName;
	
	// Process id that owns the file
	
	private int m_pid;

	// File status, if FileStatus.Unknown then do not set on file state
	
	private int m_fileSts;
	
	// File open parameter value required by the access check
	
	private int m_accessMode;
	private int m_sharedAccess;
	private int m_secLevel;
	private int m_createOptions;
	
	// Oplock requested/type
	
	private int m_oplock = OpLock.TypeNone;
	
	/**
	 * Default constructor
	 */
	public GrantAccessParams() {
	}

	/**
	 * Class constructor
	 * 
	 * @param clNode ClusterNode
	 * @param openParams FileOpenParams
	 * @param fileSts int
	 */
	public GrantAccessParams( ClusterNode clNode, FileOpenParams openParams, int fileSts) {
		m_ownerName = clNode.getName();
		
		// New file status, or unknown to not set
		
		m_fileSts = fileSts;
		
		// Copy required file open params
		
		m_pid = openParams.getProcessId();
		m_accessMode = openParams.getAccessMode();
		m_sharedAccess = openParams.getSharedAccess();
		m_secLevel = openParams.getSecurityLevel();
		m_createOptions = openParams.getCreateOptions();
		
		// Check if an oplock has been requested
		
		if ( openParams.requestBatchOpLock())
			m_oplock = OpLock.TypeBatch;
		else if ( openParams.requestExclusiveOpLock())
			m_oplock = OpLock.TypeExclusive;
	}
	
	/**
	 * Return the owner name
	 * 
	 * @return String
	 */
	public final String getOwnerName() {
		return m_ownerName;
	}
	
	/**
	 * Return the file status
	 * 
	 * @return int
	 */
	public final int getFileStatus() {
		return m_fileSts;
	}
	
	/**
	 * Return the process id
	 * 
	 * @return int
	 */
	public final int getProcessId() {
		return m_pid;
	}

	/**
	 * Return the shared access mode, zero equals allow any shared access
	 * 
	 * @return int
	 */
	public final int getSharedAccess() {
		return m_sharedAccess;
	}
	
	/**
	 * Determine if security impersonation is enabled
	 * 
	 * @return boolean
	 */
	public final boolean hasSecurityLevel() {
		return m_secLevel != -1 ? true : false;
	}
	
	/**
	 * Return the security impersonation level. Levels are defined in the WinNT class.
	 * 
	 * @return int
	 */
	public final int getSecurityLevel() {
		return m_secLevel;
	}
	
	/**
	 * Determine if the file is to be opened read-only
	 * 
	 * @return boolean
	 */
	public final boolean isReadOnlyAccess() {
		if (( m_accessMode & AccessMode.NTReadWrite) == AccessMode.NTRead)
			return true;
		return false;
	}
	
	/**
	 * Determine if the file is to be opened write-only
	 * 
	 * @return boolean
	 */
	public final boolean isWriteOnlyAccess() {
		if (( m_accessMode & AccessMode.NTReadWrite) == AccessMode.NTWrite)
			return true;
		return false;
	}
	
	/**
	 * Determine if the file is to be opened read/write
	 * 
	 * @return boolean
	 */
	public final boolean isReadWriteAccess() {
		if (( m_accessMode & AccessMode.NTReadWrite)  == AccessMode.NTReadWrite)
			return true;
		return false;
	}
	
	/**
	 * Determine if the file open is to access the file attributes/metadata only
	 * 
	 * @return boolean
	 */
	public final boolean isAttributesOnlyAccess() {
		if (( m_accessMode & (AccessMode.NTReadWrite + AccessMode.NTAppend)) == 0 &&
			(m_accessMode & (AccessMode.NTReadAttrib + AccessMode.NTWriteAttrib)) != 0)
			return true;
		return false;
	}
	
	/**
	 * Return the access mode flags
	 * 
	 * @return int
	 */
	public final int getAccessMode() {
		return m_accessMode;
	}
	
	/**
	 * Check if an oplock has been requested
	 * 
	 * @return boolean
	 */
	public final boolean hasOpLockRequest() {
		return m_oplock != OpLock.TypeNone ? true : false;
	}
	
	/**
	 * Return the oplock type requested (batch or exclusive)
	 * 
	 * @return int
	 */
	public final int getOpLockType() {
		return m_oplock;
	}
	
	/**
	 * Check if the file being creasted/opened must be a directory
	 * 
	 * @return boolean
	 */
	public final boolean isDirectory() {
		return hasCreateOption(WinNT.CreateDirectory) || getFileStatus() == FileStatus.DirectoryExists;
	}
	
	/**
	 * Check if the specified create option is enabled, specified in the WinNT class.
	 * 
	 * @param flag int
	 * @return boolean
	 */
	protected final boolean hasCreateOption(int flag) {
		return (m_createOptions & flag) != 0 ? true : false;
	}

	/**
	 * Return the access parameters as a string
	 * 
	 * @return String
	 */
	public String toString() {
		StringBuilder str = new StringBuilder();
		
		str.append( "[Owner=");
		str.append( getOwnerName());
		str.append( ",pid=");
		str.append( getProcessId());
		str.append( ",fileSts=");
		if ( getFileStatus() != -1)
			str.append( FileStatus.asString( getFileStatus()));
		else
			str.append( "Unknown");
		str.append( ",create=0x");
		str.append( Integer.toHexString( m_createOptions));
		str.append( ",access=0x");
		str.append( Integer.toHexString( getAccessMode()));
		str.append( ",sharing=0x");
		str.append( Integer.toHexString( getSharedAccess()));
		str.append( ",secLevel=");
		str.append( getSecurityLevel());
		str.append( ",oplock=");
		str.append( OpLock.getTypeAsString( getOpLockType()));
		
		if ( isDirectory())
			str.append( " DIR");
		str.append( "]");
		
		return str.toString();
	}
}
