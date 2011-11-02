/*
 * Copyright (C) 2006-2008 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */

package org.alfresco.jlan.server.filesys.cache;

import java.io.IOException;

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.locking.FileLock;
import org.alfresco.jlan.locking.LockConflictException;
import org.alfresco.jlan.locking.NotLockedException;
import org.alfresco.jlan.server.config.InvalidConfigurationException;
import org.alfresco.jlan.server.config.ServerConfiguration;
import org.alfresco.jlan.server.filesys.AccessDeniedException;
import org.alfresco.jlan.server.filesys.DiskSharedDevice;
import org.alfresco.jlan.server.filesys.ExistingOpLockException;
import org.alfresco.jlan.server.filesys.FileAccessToken;
import org.alfresco.jlan.server.filesys.FileAction;
import org.alfresco.jlan.server.filesys.FileExistsException;
import org.alfresco.jlan.server.filesys.FileOpenParams;
import org.alfresco.jlan.server.filesys.FileSharingException;
import org.alfresco.jlan.server.filesys.FileStatus;
import org.alfresco.jlan.server.filesys.NetworkFile;
import org.alfresco.jlan.server.locking.OpLockDetails;
import org.alfresco.jlan.smb.SharingMode;
import org.alfresco.jlan.smb.WinNT;
import org.alfresco.jlan.smb.server.SMBSrvPacket;
import org.alfresco.jlan.smb.server.SMBSrvSession;
import org.springframework.extensions.config.ConfigElement;

/**
 * File State Cache Abstract Class
 * 
 * <p>Base class for file state cache implementations.
 * 
 * @author gkspencer
 */
public abstract class FileStateCache {

	// Default expire check thread interval, in ms

	protected static final long DefaultExpireCheckInterval = 60000; // 1 minute
	protected static final long MinimumExpireCheckInterval = 5000;	// 5 secs

	// Default file state expiry interval, in ms
	
	protected static final long DefaultFileStateExpireInterval = 5 * 60000L;	// 5 mins
	protected static final long MinimumFileStateExpireInterval = 15000;			// 15 secs
	
	// Wakeup interval for the expire file state checker thread

	private long m_expireCheckInterval = DefaultExpireCheckInterval;

	// File state expiry time in seconds

	private long m_fileStateExpireInterval = DefaultFileStateExpireInterval;

	// File state listener

	private FileStateListener m_stateListener;

	// File state cache listener
	
	private FileStateCacheListener m_cacheListener;
	
	// Case sensitive filesystem
	
	private boolean m_caseSensitive;
	
	// Debug enable

	private boolean m_debug = false;
	private boolean m_debugExpired = false;
	
	/**
	 * Class constructor
	 */
	public FileStateCache() {

		// Default to case sensitive cache for backwards compatability
		
		m_caseSensitive = true;
	}

	/**
	 * Return the expired file state checker interval, in milliseconds
	 * 
	 * @return long
	 */
	public final long getCheckInterval() {
		return m_expireCheckInterval;
	}

	/**
	 * Get the file state cache timer, in milliseconds
	 * 
	 * @return long
	 */
	public final long getFileStateExpireInterval() {
		return m_fileStateExpireInterval;
	}

	/**
	 * Determine if the cache is using case sensitive file names
	 * 
	 * @return boolean
	 */
	public final boolean isCaseSensitive() {
	    return m_caseSensitive;
	}
	
	/**
	 * Determine if the cache is a clustered cache
	 * 
	 * @return boolean
	 */
	public boolean isClusteredCache() {
		return false;
	}
	
	/**
	 * Set the default file state cache timer, in milliseconds
	 * 
	 * @param tmo long
	 */
	public final void setFileStateExpireInterval(long tmo) {
		m_fileStateExpireInterval = tmo;
	}

	/**
	 * Set the expired file state checker interval, in milliseconds
	 * 
	 * @param chkIntval long
	 */
	public final void setCheckInterval(long chkIntval) {
		m_expireCheckInterval = chkIntval;
	}

	/**
	 * Enable/disable case sensitive file names
	 * 
	 * @param caseSensitive boolean
	 */
	public final void setCaseSensitive(boolean caseSensitive) {
	    m_caseSensitive = caseSensitive;
	}
	
	/**
	 * Determine if debug output is enabled
	 * 
	 * @return boolean
	 */
	public final boolean hasDebug() {
		return m_debug;
	}

	/**
	 * Determine if additional expired file state debugging is enabled
	 * 
	 * @return boolean
	 */
	public final boolean hasDebugExpiredStates() {
		return m_debugExpired;
	}
	
	/**
	 * Enable/disable debug output
	 * 
	 * @param dbg boolean
	 */
	public final void setDebug(boolean dbg) {
		m_debug = dbg;
	}

	/**
	 * Enable/disable additional debug output for expired file states
	 * 
	 * @param dbg boolean
	 */
	public final void setDebugExpiredStates(boolean dbg) {
		m_debugExpired = dbg;
	}

	/**
	 * Add a file state listener
	 * 
	 * @param l FileStateListener
	 */
	public final void addStateListener(FileStateListener l) {
		m_stateListener = l;
	}

	/**
	 * Remove a file state listener
	 * 
	 * @param l FileStateListener
	 */
	public final void removeStateListener(FileStateListener l) {
		if ( m_stateListener == l)
			m_stateListener = null;
	}

	/**
	 * Check if the file state listener has been set
	 * 
	 * @return boolean
	 */
	public final boolean hasStateListener() {
		return m_stateListener != null ? true : false;
	}
	
	/**
	 * Return the file state listener
	 * 
	 * @return FileStateListener
	 */
	protected final FileStateListener getStateListener() {
		return m_stateListener;
	}
	
	/**
	 * Add a file state cache listener
	 * 
	 * @param l FileStateCacheListener
	 */
	public final void addStateCacheListener(FileStateCacheListener l) {
		m_cacheListener = l;
	}

	/**
	 * Remove a file state cache listener
	 * 
	 * @param l FileStateCacheListener
	 */
	public final void removeStateCacheListener(FileStateCacheListener l) {
		if ( m_cacheListener == l)
			m_cacheListener = null;
	}

	/**
	 * Check if the file state cache listener has been set
	 * 
	 * @return boolean
	 */
	public final boolean hasStateCacheListener() {
		return m_cacheListener != null ? true : false;
	}
	
	/**
	 * Return the file state cache listener
	 * 
	 * @return FileStateCacheListener
	 */
	protected final FileStateCacheListener getStateCacheListener() {
		return m_cacheListener;
	}
	
	/**
	 * Return the number of states in the cache
	 * 
	 * @return int
	 */
	public abstract int numberOfStates();

	/**
	 * Return a file state proxy for the specified file state
	 * 
	 * @param fstate FileState
	 */
	public FileStateProxy getFileStateProxy( FileState fstate) {
		
		// Default to using a local proxy
		
		return new LocalFileStateProxy( fstate);
	}
	
	/**
	 * Find the file state for the specified path
	 * 
	 * @param path String
	 * @return FileState
	 */
	public abstract FileState findFileState(String path);

	/**
	 * Find the file state for the specified path, and optionally create a new file state if not
	 * found
	 * 
	 * @param path String
	 * @param create boolean
	 * @return FileState
	 */
	public abstract FileState findFileState(String path, boolean create);
	
    /**
     * Find the file state for the specified path, and optionally create a new file state if not
     * found with the specified initial status
     * 
     * @param path String
     * @param create boolean
     * @param status int
     * @return FileState
     */
    public abstract FileState findFileState(String path, boolean create, int status);
    
	/**
	 * Remove the file state for the specified path
	 * 
	 * @param path String
	 * @return FileState
	 */
	public abstract FileState removeFileState(String path);

	/**
	 * Rename a file state, remove the existing entry, update the path and add the state back into
	 * the cache using the new path.
	 * 
	 * @param newPath String
	 * @param state FileState
	 * @param isDir boolean
	 */
	public abstract void renameFileState(String newPath, FileState state, boolean isDir);

	/**
	 * Remove all file states from the cache
	 */
	public abstract void removeAllFileStates();

	/**
	 * Remove expired file states from the cache
	 * 
	 * @return int
	 */
	public abstract int removeExpiredFileStates();

	/**
	 * Dump the state cache entries to the debug device
	 * 
	 * @param dumpAttribs boolean
	 */
	public abstract void dumpCache(boolean dumpAttribs);
	
	/**
	 * Return the oplock details for a file, or null if there is no oplock
	 * 
	 * @param fstate FileState
	 * @return OpLockDetails
	 */
	public OpLockDetails getOpLock( FileState fstate) {
		return fstate.getOpLock();
	}
	
	/**
	 * Add an oplock
	 * 
	 * @param fstate FileState
	 * @param oplock OpLockDetails
	 * @param netFile NetworkFile
	 * @exception ExistingOpLockException
	 * @return boolean
	 */
	public boolean addOpLock( FileState fstate, OpLockDetails oplock, NetworkFile netFile)
		throws ExistingOpLockException {
		
		// Check if the file is only being accessed by one client
		
		if ( fstate.getOpenCount() != 1)
			return false;
		
		// Default to storing the oplock in the file state
		
		fstate.setOpLock( oplock);
		return true;
	}
	
	/**
	 * Clear an oplock
	 * 
	 * @param fstate FileState
	 */
	public void clearOpLock( FileState fstate) {
	
		// Clear oplock details from the file state
		
		fstate.clearOpLock();
	}
	
	/**
	 * Request an oplock break
	 * 
	 * @param clNode ClusterNode
	 * @param clState ClusterFileState
	 * @param sess SMBSrvSession
	 * @param pkt SMBSrvPacket
	 * @exception IOException
	 */
	public void requestOplockBreak( String path, OpLockDetails oplock, SMBSrvSession sess, SMBSrvPacket pkt)
		throws IOException {
		
		// Store the session/packet details to continue request once the oplock break has been
		// handled by the client owning the oplock
		
		oplock.setDeferredSession( sess, pkt);
		
		// Request an oplock break
		
		oplock.requestOpLockBreak();
	}

	/**
	 * Create a lock object, use the standard FileLock object.
	 * 
	 * @param file NetworkFile
	 * @param offset long
	 * @param len long
	 * @param pid int
	 */
	public FileLock createFileLockObject( NetworkFile file, long offset, long len, int pid) {

		//	Create a lock object to represent the file lock
		
		return new FileLock(offset, len, pid);
	}
	
	/**
	 * Check if there are active locks on this file
	 * 
	 * @param fstate FileState
	 * @return boolean
	 */
	public boolean hasActiveLocks( FileState fstate) {
		return fstate.hasActiveLocks();
	}
	
	/**
	 * Add a lock to this file
	 *
	 * @param fstate FileState
	 * @param lock FileLock
	 * @exception LockConflictException
	 */
	public void addLock(FileState fstate, FileLock lock)
		throws LockConflictException {
	
		// Add the lock
		
		fstate.addLock( lock);
	}
	
	/**
	 * Remove a lock on this file
	 *
	 * @param fstate FileState
	 * @param lock FileLock
	 * @exception NotLockedException
	 */
	public void removeLock( FileState fstate, FileLock lock)
		throws NotLockedException {
	
		// Remove a lock
		
		fstate.removeLock( lock);
	}
	
	/**
	 * Initialize the file state cache
	 * 
	 * @param config ConfigElement
	 * @param srvConfig ServerConfiguration
	 * @throws InvalidConfigurationException
	 */
	public void initializeCache( ConfigElement config, ServerConfiguration srvConfig)
		throws InvalidConfigurationException {
		
		// Check if the file state expiry interval has been specified
		
		ConfigElement elem = config.getChild("fileStateExpire");
		if ( elem != null && elem.getValue() != null) {
			
			// Validate the file state expiry interval
			
			long stateExpire = 0L;
			
			try {
			
				// Convert the file state expiry interval value to ms
				
				stateExpire = Long.parseLong( elem.getValue()) * 1000L;
				
				// Range check the file state expiry interval
				
				if ( stateExpire < MinimumFileStateExpireInterval)
					throw new InvalidConfigurationException( "File state expiry interval too low, " + elem.getValue());
				
				// Set the file state expiry interval
				
				setFileStateExpireInterval( stateExpire);
			}
			catch ( NumberFormatException ex) {
				throw new InvalidConfigurationException( "Invalid file state expiry interval, " + elem.getValue());
			}
		}
		
		// Check if the cache check interval has been specified
		
		elem = config.getChild( "cacheCheckInterval");
		if ( elem != null && elem.getValue() != null) {
			
			// Validate the cache check interval
			
			long checkInterval = 0L;
			
			try {
				
				// Convert the cache check interval value to ms
				
				checkInterval = Long.parseLong( elem.getValue()) * 1000L;
				
				// Range check the cache check interval
				
				if ( checkInterval < MinimumExpireCheckInterval)
					throw new InvalidConfigurationException( "Cache check interval too low, " + elem.getValue());
				
				// Set the cache check interval
				
				setCheckInterval( checkInterval);
			}
			catch ( NumberFormatException ex) {
				throw new InvalidConfigurationException( "Invalid cache check interval, " + elem.getValue());
			}
		}
		
		// Check if file state cache debug output is enabled
		
		if ( config.getChild( "Debug") != null)
			setDebug( true);
		
		// Check if additional file state expiry debug is enabled
		
		if ( config.getChild( "expiryDebug") != null)
			setDebugExpiredStates( true);
	}
	
	/**
	 * Set the filesystem driver and driver context details, if required by the cache
	 * 
	 * @param diskDev DiskSharedDevice
	 */
	public void setDriverDetails( DiskSharedDevice diskDev) {
	}
	
	/**
	 * Cache started
	 */
	public void stateCacheStarted() {
		
		// Inform listener that cache is running
		
		if ( hasStateCacheListener())
			getStateCacheListener().stateCacheRunning();
	}
	
	/**
	 * Cache shutting down
	 */
	public void stateCacheShuttingDown() {
		
		// Inform listener that cache is shutting down
		
		if ( hasStateCacheListener())
			getStateCacheListener().stateCacheShuttingDown();
	}
	
	/**
	 * Grant the required file access
	 * 
	 * @param params FileOpenParams
	 * @param fstate FileState
	 * @param fileSts int
	 * @return Object
	 * @exception FileSharingException
	 * @exception AccessDeniedException
	 * @exception FileExistsException
	 */
	public FileAccessToken grantFileAccess( FileOpenParams params, FileState fstate, int fileSts)
		throws FileSharingException, AccessDeniedException, FileExistsException {
		
		synchronized ( fstate) {
			
			// Check if the current file open allows the required shared access
			
			boolean nosharing = false;
			String noshrReason = null;
			
			if ( fstate.getOpenCount() > 0) {
				
				// Check if the open action indicates a new file create
				
				if ( params.getOpenAction() == FileAction.NTCreate)
					throw new FileExistsException( params.getFullPath());
					
				// Check for impersonation security level from the original process that opened the file
				
				if ( params.getSecurityLevel() == WinNT.SecurityImpersonation && params.getProcessId() == fstate.getProcessId())
					nosharing = false;
	
				// Check if the caller wants read access, check the sharing mode
				// Check if the caller wants write access, check if the sharing mode allows write
				
		    	else if ( params.isReadOnlyAccess() && (fstate.getSharedAccess() & SharingMode.READ) != 0)
		    		nosharing = false;
				
				// Check if the caller wants write access, check the sharing mode
				
		    	else if (( params.isReadWriteAccess() || params.isWriteOnlyAccess()) && (fstate.getSharedAccess() & SharingMode.WRITE) == 0)
		    	{
					nosharing = true;
					noshrReason = "Sharing mode disallows write";
					
		    		// DEBUG
		    		
		    		if ( Debug.EnableDbg && hasDebug())
		    			Debug.println("Sharing mode disallows write access path=" + params.getPath());
		    	}
		    	
				// Check if the file has been opened for exclusive access
				
				else if ( fstate.getSharedAccess() == SharingMode.NOSHARING) {
					nosharing = true;
					noshrReason = "Sharing mode exclusive";
				}
				
				// Check if the required sharing mode is allowed by the current file open
				
				else if ((fstate.getSharedAccess() & params.getSharedAccess()) != params.getSharedAccess()) {
					nosharing = true;
					noshrReason = "Sharing mode mismatch";
					
		    		if ( Debug.EnableDbg && hasDebug())
		    			Debug.println("Local share mode=0x" + Integer.toHexString(fstate.getSharedAccess()) + ", params share mode=0x" + Integer.toHexString(params.getSharedAccess()));
				}
				
				// Check if the caller wants exclusive access to the file
				
		    	else if ( params.getSharedAccess() == SharingMode.NOSHARING) {
		    		nosharing = true;
		    		noshrReason = "Requestor wants exclusive mode";
		    	}
			}
			
			// Check if there is a sharing mode mismatch
			
			if ( nosharing == true)
				throw new FileSharingException( "File sharing violation, reason " + noshrReason);
			else {
				
				// Update the file sharing mode and process id, if this is the first file open
				
				fstate.setSharedAccess( params.getSharedAccess());
				fstate.setProcessId( params.getProcessId());
				
				// Increment the file open count
				
				fstate.incrementOpenCount();
				
				// Set the file status
				
				if ( fileSts != FileStatus.Unknown)
					fstate.setFileStatus( fileSts);
			}
		}
		
		// Use the PID as the access token
		
		return new LocalFileAccessToken( params.getProcessId());
	}
	
	/**
	 * Release access to a file
	 * 
	 * @params fstate FileState
	 * @param token FileAccessToken
	 * @return int
	 */
	public int releaseFileAccess( FileState fstate, FileAccessToken token) {

		int openCount = -1;
		
		synchronized ( fstate) {
			
			// Decrement the file open count, if the count is now zero then reset the sharing mode
			
			openCount = fstate.decrementOpenCount();
			
			if ( openCount == 0)
				fstate.setSharedAccess( SharingMode.READWRITEDELETE);
		}
		
		// Return the current open count for the file
		
		return openCount;
	}
	
	/**
	 * Indicate a data update is in progress for the specified file
	 * 
	 * @param fstate FileState
	 */
	public void setDataUpdateInProgress( FileState fstate) {
		
		// Default implementation, do nothing
	}
	
	/**
	 * Indicate that a data update has completed for the specified file
	 * 
	 * @param fstate FileState
	 */
	public void setDataUpdateCompleted( FileState fstate) {
		
		// Default implementation, do nothing
	}
}
