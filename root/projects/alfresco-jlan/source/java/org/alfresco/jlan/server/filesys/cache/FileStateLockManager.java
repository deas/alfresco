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

package org.alfresco.jlan.server.filesys.cache;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.locking.FileLock;
import org.alfresco.jlan.locking.LockConflictException;
import org.alfresco.jlan.locking.NotLockedException;
import org.alfresco.jlan.server.SrvSession;
import org.alfresco.jlan.server.filesys.ExistingOpLockException;
import org.alfresco.jlan.server.filesys.NetworkFile;
import org.alfresco.jlan.server.filesys.TreeConnection;
import org.alfresco.jlan.server.locking.LockManager;
import org.alfresco.jlan.server.locking.OpLockDetails;
import org.alfresco.jlan.server.locking.OpLockManager;
import org.alfresco.jlan.server.thread.ThreadRequestPool;
import org.alfresco.jlan.server.thread.TimedThreadRequest;
import org.alfresco.jlan.smb.OpLock;
import org.alfresco.jlan.smb.SMBStatus;
import org.alfresco.jlan.smb.server.SMBSrvPacket;
import org.alfresco.jlan.smb.server.SMBSrvSession;

/**
 * File State Lock Manager Class
 * 
 * <p>Lock manager implementation for byte range locking and oplocks that uses the file state cache to
 * track locks/oplocks.
 *
 * @author gkspencer
 */
public class FileStateLockManager implements LockManager, OpLockManager, Runnable {

	// Oplock break timeout
	
	private static final long OpLockBreakTimeout	= 5000L;	// 5 seconds
	
	// File state cache used for byte range locks/oplocks
	
	private FileStateCache m_stateCache;
	
	// Oplock breaks in progress
	
	private Hashtable<String, OpLockDetails> m_oplockQueue;
	
	// Oplock break timeout thread
	
	private Thread m_expiryThread;
	private boolean m_shutdown;
	
	// Thread pool and timed thread request
	
	private ThreadRequestPool m_threadPool;
	private OplockExpiryTimedRequest m_threadReq;
	
	/**
	 * Oplock Expiry Checker Timed Thread Request Class
	 */
	private class OplockExpiryTimedRequest extends TimedThreadRequest {
	    
	    /**
	     * Constructor
	     * 
	     * @param name String
	     * @param interval long
	     */
	    public OplockExpiryTimedRequest(String name, long interval) {
	        super( name, TimedRequestPaused, interval);
	    }
	    
	    /**
	     * Expiry checker method
	     */
	    protected void runTimedRequest() {
	        
	        // Check for expired oplock break requests
	        
	        checkExpiredOplockBreaks();
	        
	        // If the shutdown flag is set then clear the repeat interval so the timed request
	        // does not get requeued
	        
	        if ( m_shutdown == true) {
	            
	            // Clear the repeat interval so the request does not get requeued
	        
	            setRepeatInterval( 0L);
	        }
	        else if ( m_oplockQueue.size() == 0) {
	            
	            // Pause the timed checker request
	            
	            setRunAtTime( TimedThreadRequest.TimedRequestPaused);
	        }
	    }
	}
	
	/**
	 * Class constructor
	 * 
	 * @param stateCache FileStateCache
	 */
	public FileStateLockManager(FileStateCache stateCache) {
		
		// Save the associated state cache
		
		m_stateCache = stateCache;
		
		// Create the oplock break queue
		
		m_oplockQueue = new Hashtable<String, OpLockDetails>();
	}
	
	/**
	 * Lock a byte range within a file, or the whole file. 
	 *
	 * @param sess SrvSession
	 * @param tree TreeConnection
	 * @param file NetworkFile
	 * @param lock FileLock
	 * @exception LockConflictException
	 * @exception IOException
	 */
	public void lockFile(SrvSession sess, TreeConnection tree, NetworkFile file, FileLock lock)
		throws LockConflictException, IOException {
			
		//	Make sure the file implements the file state interface
		
		if (( file instanceof NetworkFileStateInterface) == false)
			throw new IllegalArgumentException("NetworkFile does not implement NetworkFileStateInterface, path=" + file.getFullName());
			
		//	Get the file state associated with the file
		
		NetworkFileStateInterface fstateIface = (NetworkFileStateInterface) file;
		FileState fstate = fstateIface.getFileState();
		
		if ( fstate == null)
			throw new IOException("Open file without state (lock)");
			
		//	Add the lock to the active lock list for the file, check if the new lock conflicts with
		//	any existing locks. Add the lock to the file instance so that locks can be removed if the
		//	file is closed/session abnormally terminates.
		
		m_stateCache.addLock( fstate, lock);
		file.addLock(lock);
	}

	/**
	 * Unlock a byte range within a file, or the whole file
	 *
	 * @param sess SrvSession
	 * @param tree TreeConnection
	 * @param file NetworkFile
	 * @param lock FileLock
	 * @exception NotLockedException
	 * @exception IOException
	 */
	public void unlockFile(SrvSession sess, TreeConnection tree, NetworkFile file, FileLock lock)
		throws NotLockedException, IOException {
			
		//	Make sure the file implements the file state interface
		
		if (( file instanceof NetworkFileStateInterface) == false)
			throw new IllegalArgumentException("NetworkFile does not implement NetworkFileStateInterface");
			
		//	Get the file state associated with the file
		
		NetworkFileStateInterface fstateIface = (NetworkFileStateInterface) file;
		FileState fstate = fstateIface.getFileState();
	
		if ( fstate == null)
			throw new IOException("Open file without state (unlock)");
		
		//	Remove the lock from the active lock list for the file, and the file instance
	
		try {
			m_stateCache.removeLock( fstate, lock);
		}
		finally {
			file.removeLock(lock);
		}
	}
	
	/**
	 * Create a file lock object.
	 * 
	 * @param sess SrvSession
	 * @param tree TreeConnection
	 * @param file NetworkFile
	 * @param offset long
	 * @param len long
	 * @param pid int
	 */
	public FileLock createLockObject(SrvSession sess, TreeConnection tree, NetworkFile file, long offset, long len, int pid) {

		//	Create a lock object to represent the file lock
		
		return m_stateCache.createFileLockObject( file, offset, len, pid);
	}
	
	/**
	 * Release all locks that a session has on a file. This method is called to perform cleanup if a file
	 * is closed that has active locks or if a session abnormally terminates.
	 *
	 * @param sess SrvSession
	 * @param tree TreeConnection
	 * @param file NetworkFile
	 */
	public void releaseLocksForFile(SrvSession sess, TreeConnection tree, NetworkFile file) {
		
		//	Check if the file has active locks
		
		if ( file.hasLocks()) {
			
			synchronized ( file) {
				
				//	Enumerate the locks and remove
				
				while ( file.numberOfLocks() > 0) {
					
					//	Get the current file lock
					
					FileLock curLock = file.getLockAt(0);
					
					//	Remove the lock, ignore errors
					
					try {
						
						//	Unlock will remove the lock from the global list and the local files list
						
						unlockFile(sess, tree, file, curLock);
					}
					catch (Exception ex) {
					}
				}
			}
		}
	}
	
	/**
	 * Check if there is an oplock for the specified path, return the oplock type.
	 * 
	 * @param path String
	 * @return int
	 */
	public int hasOpLock(String path) {
		
		// Get the file state
		
		FileState fstate = m_stateCache.findFileState(path);
		if ( fstate != null && fstate.hasOpLock()) {
		
			// Return the oplock type
			
			OpLockDetails oplock = fstate.getOpLock();
			if ( oplock != null)
				return oplock.getLockType();
		}
		
		// No oplock
		
		return OpLock.TypeNone;
	}
	
	/**
	 * Return the oplock details for a path, or null if there is no oplock on the path
	 * 
	 * @param path String
	 * @return OpLockDetails
	 */
	public OpLockDetails getOpLockDetails(String path) {
		
		// Get the file state
		
		FileState fstate = m_stateCache.findFileState(path);
		if ( fstate != null)
			return m_stateCache.getOpLock( fstate);

		// No oplock
		
		return null;
	}
	
	/**
	 * Grant an oplock, store the oplock details
	 * 
	 * @param path String
	 * @param oplock OpLockDetails
	 * @param netFile NetworkFile
	 * @return boolean
	 * @exception ExistingOpLockException	If the file already has an oplock
	 */
	public boolean grantOpLock(String path, OpLockDetails oplock, NetworkFile netFile)
		throws ExistingOpLockException {

		// Get, or create, a file state
		
		FileState fstate = m_stateCache.findFileState(path, true);
		
		// Set the oplock

		return m_stateCache.addOpLock( fstate, oplock, netFile);
	}

	/**
	 * Request an oplock break on the specified oplock
	 * 
	 * @param path String
	 * @param oplock OpLockDetails
	 * @param sess SMBSrvSession
	 * @param pkt SMBSrvPacket
	 * @exception IOException
	 */
	public void requestOpLockBreak( String path, OpLockDetails oplock, SMBSrvSession sess, SMBSrvPacket pkt)
		throws IOException {

		// Request an oplock break
		
		m_stateCache.requestOplockBreak( path, oplock, sess, pkt);
		
		// Add the oplock to the break in progress queue
		
		synchronized ( m_oplockQueue) {
			m_oplockQueue.put( path, oplock);
			
			// Inform the checker thread or restart the timed request
			
			if ( m_threadPool == null)
			    m_oplockQueue.notify();
			else
			    m_threadReq.restartRequest();
		}
	}
	
	/**
	 * Release an oplock
	 * 
	 * @param path String
	 */
	public void releaseOpLock(String path) {
		
		// Get the file state
		
		FileState fstate = m_stateCache.findFileState(path);

		// Remove the oplock from the file
		
		if ( fstate != null)
			m_stateCache.clearOpLock( fstate);
		
		// Remove from the pending oplock break queue
		
		synchronized ( m_oplockQueue) {
			m_oplockQueue.remove( path);
		}
	}
	
	/**
	 * Check for expired oplock break requests
	 * 
	 * @return int
	 */
	public int checkExpiredOplockBreaks() {
		
		// Check if there are any oplock breaks in progress
		
		if ( m_oplockQueue.size() == 0)
			return 0;
		
		// Check for oplock break requests that have expired

		int expireCnt = 0;
		
		long timeNow = System.currentTimeMillis();
		Enumeration<String> opBreakKeys = m_oplockQueue.keys();
		
		while ( opBreakKeys.hasMoreElements()) {
			
			// Check the current oplock break
			
			String path = opBreakKeys.nextElement();
			OpLockDetails opLock = m_oplockQueue.get( path);
			if ( opLock != null) {
				
				// Check if the oplock break has timed out
				
				if ( opLock.hasDeferredSession()) {
					
					// Check if the oplock break request has timed out
					
					if ((opLock.getOplockBreakTime() + OpLockBreakTimeout) <= timeNow) {
					
						// Get the deferred request details
						
						SMBSrvSession sess = opLock.getDeferredSession();
						SMBSrvPacket  pkt  = opLock.getDeferredPacket();
						
						try {
							
							// Return an error for the deferred file open request
							
							if ( sess.sendAsyncErrorResponseSMB( pkt, SMBStatus.NTAccessDenied, SMBStatus.NTErr) == true) {
							
								// DEBUG
								
								if ( Debug.EnableDbg && sess.hasDebug( SMBSrvSession.DBG_OPLOCK))
									sess.debugPrintln( "Oplock break timeout, oplock=" + opLock);
							
							// Release the packet back to the pool
							
							}
							else if ( Debug.EnableDbg && sess.hasDebug( SMBSrvSession.DBG_OPLOCK))
								sess.debugPrintln( "Failed to send open reject, oplock break timed out, oplock=" + opLock);
						}
						catch ( IOException ex) {
							
						}
						finally {
							
							// Make sure the packet is released back to the memory pool
							
							if ( pkt != null)
								sess.getPacketPool().releasePacket( pkt);
						}
						
						// Remove the oplock break from the queue
						
						m_oplockQueue.remove( path);
						
						// Clear the deferred packet details
						
						opLock.clearDeferredSession();
						
						// Mark the oplock has having a failed oplock break
						
						opLock.setOplockBreakFailed();
						
						// Update the expired oplock break count
						
						expireCnt++;
					}
				}
				else {
					
					// Oplock no longer has a deferred request, remove it from the queue
					
					m_oplockQueue.remove( path);

					// Update the expired oplock break count
					
					expireCnt++;
				}
			}
		}
		
		// Return the count of expired oplock breaks
		
		return expireCnt;
	}
	
	/**
	 * Run the oplock break expiry
	 */
    public void run()
    {
        // Loop forever

    	m_shutdown = false;
    	
        while ( m_shutdown == false)
        {
            // Wait for an oplock break or sleep for a while if there are active oplock break requests

        	try
            {
        		synchronized ( m_oplockQueue) {
        			if ( m_oplockQueue.size() == 0)
        				m_oplockQueue.wait();
        		}
        		
        		// Oplock break added to the queue, wait a while before checking the queue
        		
        		if ( m_oplockQueue.size() > 0)
        			Thread.sleep( OpLockBreakTimeout);
            }
            catch (InterruptedException ex)
            {
            }

            //	Check for shutdown
            
            if ( m_shutdown == true)
            	return;
            
            // Check for expired oplock break requests
            
            checkExpiredOplockBreaks();
        }
    }

	/**
	 * Request the oplock break expiry thread to shutdown
	 */
	public final void shutdownRequest() {
		m_shutdown = true;
		
		if ( m_expiryThread != null)
		{
			try {
				m_expiryThread.interrupt();
			}
			catch (Exception ex) {
			}
		}
	}
	
	/**
	 * Start the lock manager
	 * 
	 * @param threadName String
	 * @param threadPool ThreadRequestPool
	 */
	public final void startLockManager( String threadName, ThreadRequestPool threadPool) {

	    // Save the thread pool, if specified
	    
	    m_threadPool = threadPool;
	    
        // If the thread pool has not been specified then use a seperate thread for the oplock expiry checker
        
        if ( m_threadPool == null) {
            
            // Start the oplock break expiry thread
            
            m_expiryThread = new Thread(this);
            m_expiryThread.setDaemon(true);
            m_expiryThread.setName(threadName);
            m_expiryThread.start();
        }
        else {
            
            // Queue a timed request to the thread pool to run the oplock expiry check
            
            m_threadReq = new OplockExpiryTimedRequest( threadName, OpLockBreakTimeout / 1000L);
            m_threadPool.queueTimedRequest( m_threadReq);
        }
	    
	}
}
