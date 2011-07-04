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

import org.alfresco.jlan.server.filesys.FileAccessToken;
import org.alfresco.jlan.server.filesys.cache.cluster.ClusterNode;
import org.alfresco.jlan.smb.OpLock;

/**
 * HazelCast Access Token Class
 * 
 * <p>File access token used by the grantFileAccess()/releaseFileAccess() methods of the clustered state cache
 *
 * @author gkspencer
 */
public class HazelCastAccessToken implements Serializable, FileAccessToken {

	// Serialization id
	
	private static final long serialVersionUID = 3L;

	//	Cluster node that owns the token
	
	private String m_ownerName;
	
	// Process id that owns the file
	
	private int m_pid;
	
	// Granted oplock type, if requested, and flag to indicate if the oplock is not available 
	
	private int m_oplock;
	private boolean m_oplockNotAvailable;
	
	/**
	 * Default constructor
	 */
	public HazelCastAccessToken() {
	}
	
	/**
	 * Class constructor
	 * 
	 * @param clName String
	 * @param pid int
	 */
	protected HazelCastAccessToken( String clName, int pid) {
		m_ownerName = clName;
		m_pid = pid;
	}
	
	/**
	 * Class constructor
	 * 
	 * @param clNode ClusterNode
	 * @param pid int
	 */
	protected HazelCastAccessToken( ClusterNode clNode, int pid) {
		m_ownerName = clNode.getName();
		m_pid = pid;
	}
	
	/**
	 * Class constructor
	 * 
	 * @param clName String
	 * @param pid int
	 * @param oplock int
	 * @param oplockNotAvailable boolean
	 */
	protected HazelCastAccessToken( String clName, int pid, int oplock, boolean oplockNotAvail) {
		m_ownerName = clName;
		m_pid = pid;
		m_oplock = oplock;
		m_oplockNotAvailable = oplockNotAvail;
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
	 * Return the process id
	 * 
	 * @return int
	 */
	public final int getProcessId() {
		return m_pid;
	}

	/**
	 * Check if the oplock was available
	 * 
	 * @return boolean
	 */
	public final boolean isOplockAvailable() {
		return m_oplockNotAvailable ? false : true;
	}
	
	/**
	 * Return the oplock type
	 * 
	 * @return int
	 */
	public final int getOpLockType() {
		return m_oplock;
	}

	/**
	 * Set the oplock type granted
	 * 
	 * @param oplock int
	 */
	public final void setOpLockType( int oplock) {
		m_oplock = oplock;
	}
	
	/**
	 * Return the access token as a string
	 * 
	 * @return String
	 */
	public String toString() {
		StringBuilder str = new StringBuilder();
		
		str.append( "[Token owner=");
		str.append( getOwnerName());
		str.append( ",pid=");
		str.append( getProcessId());
		if ( getOpLockType() != OpLock.TypeNone) {
			str.append( ",oplock=");
			str.append( OpLock.getTypeAsString( getOpLockType()));
		}
		else {
			str.append( ",opavail=");
			str.append( isOplockAvailable());
		}
		str.append( "]");
		
		return str.toString();
	}
}
