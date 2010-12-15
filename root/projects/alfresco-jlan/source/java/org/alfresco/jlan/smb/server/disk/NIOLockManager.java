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

package org.alfresco.jlan.smb.server.disk;

import java.io.IOException;

import org.alfresco.jlan.locking.FileLock;
import org.alfresco.jlan.locking.LockConflictException;
import org.alfresco.jlan.locking.NotLockedException;
import org.alfresco.jlan.server.SrvSession;
import org.alfresco.jlan.server.filesys.NetworkFile;
import org.alfresco.jlan.server.filesys.TreeConnection;
import org.alfresco.jlan.server.locking.LockManager;

/**
 *	NIO Lock Manager Class
 *
 * <p>File lock manager implementation that uses the Java NIO file locking capabilities.
 *
 * @author gkspencer
 */
public class NIOLockManager implements LockManager {

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
		
		//	Make sure the file and lock are of the correct type
		
		if (( file instanceof NIOJavaNetworkFile) == false)
			throw new IllegalArgumentException("Invalid NetworkFile class");
		
		if (( lock instanceof NIOFileLock) == false)
			throw new IllegalArgumentException("Invalid FileLock class");
			
		//	Get the file state associated with the file
		
		NIOJavaNetworkFile nioFile = (NIOJavaNetworkFile) file;
		NIOFileLock nioLock = (NIOFileLock) lock;
			
		//	Add the lock to the file instance so that locks can be removed if the file is
		//	closed/session abnormally terminates.
		
		nioFile.lockFile( nioLock);
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
		
		//	Make sure the file is of the correct type
	
		if (( file instanceof NIOJavaNetworkFile) == false)
			throw new IllegalArgumentException("Invalid NetworkFile class");
		
		if (( lock instanceof NIOFileLock) == false)
			throw new IllegalArgumentException("Invalid FileLock class");
		
		//	Get the file state associated with the file
	
		NIOJavaNetworkFile nioFile = (NIOJavaNetworkFile) file;
		NIOFileLock nioLock = (NIOFileLock) lock;
		
		//	Remove the lock from the active lock list for the file
	
		nioFile.unlockFile( nioLock);
  }

  /**
	 * Create a lock object, allows the FileLock object to be extended
	 * 
	 * @param sess SrvSession
	 * @param tree TreeConnection
	 * @param file NetworkFile
	 * @param offset long
	 * @param len long
	 * @param pid int
	 * @return FileLock
   */
  public FileLock createLockObject(SrvSession sess, TreeConnection tree, NetworkFile file, long offset, long len, int pid) {

		//	Create a lock object to represent the file lock
		
		return new NIOFileLock(offset, len, pid);
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
}
