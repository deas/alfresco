/*
 * Copyright (C) 2006-2012 Alfresco Software Limited.
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

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.server.filesys.cache.cluster.ClusterFileState;
import org.alfresco.jlan.server.locking.OpLockDetails;
import org.alfresco.jlan.smb.OpLock;

import com.hazelcast.core.IMap;

/**
 * Change OpLock Type Remote Task Class
 * 
 * <p>Used to synchronize changing an oplock type on a file state by executing on the remote
 * node that owns the file state/key.
 * 
 * @author gkspencer
 */
public class ChangeOpLockTypeTask extends RemoteStateTask<Integer> {

	// Serialization id
	
	private static final long serialVersionUID = 1L;

	// New oplock type
	
	private int m_oplockType;
	
	/**
	 * Default constructor
	 */
	public ChangeOpLockTypeTask() {
	}
	
	/**
	 * Class constructor
	 * 
	 * @param mapName String
	 * @param key String
	 * @param newLockType int
	 * @param debug boolean
	 * @param timingDebug boolean
	 */
	public ChangeOpLockTypeTask( String mapName, String key, int newLockType, boolean debug, boolean timingDebug) {
		super( mapName, key, true, false, debug, timingDebug);

		m_oplockType = newLockType;
	}
	
	/**
	 * Run a remote task against a file state
	 * 
	 * @param stateCache IMap<String, ClusterFileState>
	 * @param fState ClusterFileState
	 * @return Integer
	 * @exception Exception
	 */
	protected Integer runRemoteTaskAgainstState(IMap<String, ClusterFileState> stateCache, ClusterFileState fState)
		throws Exception {

		// DEBUG
		
		if ( hasDebug())
			Debug.println( "ChangeOpLockTypeTask: New type=" + OpLock.getTypeAsString( m_oplockType) + " for state " + fState);

		// Get the oplock
		
		OpLockDetails oplock = fState.getOpLock();
		int newType = -1;
		
		if ( oplock != null) {
			
			// Update the oplock type

			int oldOpLockType = oplock.getLockType();
			oplock.setLockType( m_oplockType);
			newType = m_oplockType;
			
			// DEBUG
			
			if ( hasDebug())
				Debug.println("ChangeOpLockTypeTask: Changed type from=" + OpLock.getTypeAsString( oldOpLockType) + " to=" + OpLock.getTypeAsString( m_oplockType));
		}

		// Return the new oplock type, or -1 if no oplock to update
		
		return new Integer( newType);
	}
}
