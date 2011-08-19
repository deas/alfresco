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

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.server.filesys.ExistingOpLockException;
import org.alfresco.jlan.server.filesys.FileAccessToken;
import org.alfresco.jlan.server.filesys.FileAction;
import org.alfresco.jlan.server.filesys.FileExistsException;
import org.alfresco.jlan.server.filesys.FileSharingException;
import org.alfresco.jlan.server.filesys.FileStatus;
import org.alfresco.jlan.server.filesys.cache.FileState;
import org.alfresco.jlan.server.filesys.cache.cluster.ClusterFileState;
import org.alfresco.jlan.smb.OpLock;
import org.alfresco.jlan.smb.SharingMode;
import org.alfresco.jlan.smb.WinNT;

import com.hazelcast.core.IMap;

/**
 * Grant File Access Task Class
 * 
 * <p>Check if the specified file can be accessed using the requested sharing mode, access mode. Return
 * a file sharing exception if the access cannot be granted.
 *
 * @author gkspencer
 */
public class GrantFileAccessTask extends RemoteStateTask<FileAccessToken> {

	// Serialization id
	
	private static final long serialVersionUID = 1L;

	// File open parameters
	
	private GrantAccessParams m_params;
	
	/**
	 * Default constructor
	 */
	public GrantFileAccessTask() {
	}
	
	/**
	 * Class constructor
	 * 
	 * @param mapName String
	 * @param key String
	 * @param openParams GrantAccessParams 
	 * @param debug boolean
	 * @param timingDebug boolean
	 */
	public GrantFileAccessTask( String mapName, String key, GrantAccessParams params, boolean debug, boolean timingDebug) {
		super( mapName, key, true, false, debug, timingDebug);
		
		m_params = params;
	}
	
	/**
	 * Run a remote task against a file state
	 * 
	 * @param stateCache IMap<String, HazelCastFileState>
	 * @param fState HazelCastFileState
	 * @return FileAccessToken
	 * @exception Exception
	 */
	protected FileAccessToken runRemoteTaskAgainstState( IMap<String, ClusterFileState> stateCache, ClusterFileState fState)
		throws Exception {
	
		// DEBUG
		
		if ( hasDebug())
			Debug.println( "GrantFileAccessTask: Open params=" + m_params + " path " + fState);
		
		// Check if the current file open allows the required shared access
		
		boolean nosharing = false;
		int grantedOplock = OpLock.TypeNone;
		boolean oplockNotAvailable = false;
		String noshrReason = null;
		
		if ( fState.getOpenCount() > 0) {

			// Get the current primary owner details, the owner node name/port
			
			String curPrimaryOwner = (String)  fState.getPrimaryOwner();
			
			// DEBUG
			
			if ( hasDebug())
				Debug.println( "File already open by " + curPrimaryOwner + ", pid=" + fState.getProcessId() + 
								", sharingMode=" + SharingMode.getSharingModeAsString( fState.getSharedAccess()));
				
			// Check if the open action indicates a new file create
			
			if ( m_params.getOpenAction() == FileAction.NTCreate)
				throw new FileExistsException();
				
			// Check for impersonation security level from the original process that opened the file
			
			if ( m_params.getSecurityLevel() == WinNT.SecurityImpersonation && m_params.getProcessId() == fState.getProcessId() &&
					curPrimaryOwner.equalsIgnoreCase( m_params.getOwnerName()))
				nosharing = false;

			// Check if the caller wants read access, check the sharing mode
			
	    	else if ( m_params.isReadOnlyAccess() && (fState.getSharedAccess() & SharingMode.READ) != 0)
	    		nosharing = false;
			
			// Check if the caller wants write access, check the sharing mode
			
	    	else if (( m_params.isReadWriteAccess() || m_params.isWriteOnlyAccess()) && (fState.getSharedAccess() & SharingMode.WRITE) == 0)
	    	{
				nosharing = true;
				noshrReason = "Sharing mode disallows write";
				
	    		// DEBUG
	    		
	    		if ( Debug.EnableDbg && hasDebug())
	    			Debug.println("Sharing mode disallows write access path=" + fState.getPath());
	    	}
	    	
			// Check if the file has been opened for exclusive access
			
			else if ( fState.getSharedAccess() == SharingMode.NOSHARING) {
				nosharing = true;
				noshrReason = "Sharing mode exclusive";
			}
			
			// Check if the required sharing mode is allowed by the current file open
			
			else if ((fState.getSharedAccess() & m_params.getSharedAccess()) != m_params.getSharedAccess()) {
				nosharing = true;
				noshrReason = "Sharing mode mismatch";
				
	    		// DEBUG
	    		
	    		if ( Debug.EnableDbg && hasDebug())
	    			Debug.println("Local share mode=0x" + Integer.toHexString( fState.getSharedAccess()) + ", params share mode=0x" + Integer.toHexString( m_params.getSharedAccess()));
			}
			
			// Check if the caller wants exclusive access to the file
			
	    	else if ( m_params.getSharedAccess() == SharingMode.NOSHARING) {
	    		nosharing = true;
	    		noshrReason = "Requestor wants exclusive mode";
	    	}
			
			// Indicate that an oplock is not available, file already open by another client
			
			oplockNotAvailable = true;;
		}
		else if ( m_params.hasOpLockRequest() && m_params.isDirectory() == false) {
			
			// Grant the requested oplock, file is not open by any other users
			
			grantedOplock = m_params.getOpLockType();

			// DEBUG
    		
    		if ( Debug.EnableDbg && hasDebug())
    			Debug.println("Granted oplock type=" + OpLock.getTypeAsString( grantedOplock));
		}
		
		// Check if there is a sharing mode mismatch
		
		if ( nosharing == true)
			throw new FileSharingException( "File sharing violation, reason " + noshrReason);
		else {
			
			// Update the file sharing mode, process id and primary owner details, if this is the first file open
			
			fState.setSharedAccess( m_params.getSharedAccess());
			fState.setProcessId( m_params.getProcessId());
			fState.setPrimaryOwner( m_params.getOwnerName());
			
			// Add oplock details

			if ( grantedOplock != OpLock.TypeNone) {
				
				try {

					// Create the remote oplock details
					
					RemoteOpLockDetails remoteOplock = new RemoteOpLockDetails( m_params.getOwnerName(), grantedOplock, fState.getPath(), null);
					fState.setOpLock( remoteOplock);
				}
				catch ( ExistingOpLockException ex) {
					
					// DEBUG
					
					if ( hasDebug())
						Debug.println( "Failed to set oplock on " + fState + ", existing oplock=" + fState.getOpLock());
					
					// Reset the oplock to not granted
					
					grantedOplock = OpLock.TypeNone;
					oplockNotAvailable = true;
				}
			}
			
			// Increment the file open count
			
			fState.incrementOpenCount();
			
			// Set the file status
			
			if ( m_params.getFileStatus() != FileStatus.Unknown)
				fState.setFileStatusInternal( m_params.getFileStatus(), FileState.ReasonNone);
		}
	
		// Return an access token
		
		return new HazelCastAccessToken( m_params.getOwnerName(), m_params.getProcessId(), grantedOplock, oplockNotAvailable);
	}
}
