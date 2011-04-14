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

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.locking.FileLock;
import org.alfresco.jlan.locking.FileLockList;
import org.alfresco.jlan.locking.LockConflictException;
import org.alfresco.jlan.locking.NotLockedException;
import org.alfresco.jlan.server.filesys.ExistingOpLockException;
import org.alfresco.jlan.server.filesys.FileName;
import org.alfresco.jlan.server.filesys.FileOpenParams;
import org.alfresco.jlan.server.filesys.FileStatus;
import org.alfresco.jlan.server.filesys.pseudo.PseudoFile;
import org.alfresco.jlan.server.filesys.pseudo.PseudoFileList;
import org.alfresco.jlan.server.locking.OpLockDetails;
import org.alfresco.jlan.smb.SharingMode;

/**
 * File State Class
 * 
 * <p>Caches information about a file/directory so that the core server does not need
 * to make calls to the shared device driver.
 *
 * @author gkspencer
 */
public class FileState {

	//	File state constants
	
	public final static long NoTimeout		= -1L;
	public final static long DefTimeout		= 2 * 60000L;	// 2 minutes
    public final static long RenameTimeout  = 1 * 60000L;   // 1 minute
    public final static long DeleteTimeout	= 15000L;		// 15 seconds

	public final static int UnknownFileId		= -1;
			
	//	File status codes

	public final static int FILE_LOADWAIT		= 0;
	public final static int FILE_LOADING		= 1;
	public final static int FILE_AVAILABLE		= 2;
	public final static int FILE_UPDATED		= 3;
	public final static int FILE_SAVEWAIT		= 4;
	public final static int FILE_SAVING			= 5;
	public final static int FILE_SAVED			= 6;
	public final static int FILE_DELETED		= 7;
	public final static int FILE_RENAMED		= 8;
	public final static int FILE_DELETEONCLOSE 	= 9;

	//	File state names
	
	private static final String[] _fileStates = { "LoadWait", "Loading", "Available", "Updated", "SaveWait", "Saving", "Saved", "Deleted", "Renamed", "DeleteOnClose" };
		
	//	Standard file information keys
	
	public static final String FileInformation	= "FileInfo";
	public static final String StreamsList		= "StreamsList";
	
	//	File name/path
	
	private String m_path;
	
	//	File identifier
	
	private int m_fileId = UnknownFileId;
	
	//	File state timeout, -1 indicates no timeout
	
	private long m_tmo;
	
	//	File status, indicates if the file/folder exists and if it is a file or folder.
	//	Constants are defined in the FileStatus class.
	
	private int m_fileStatus;
	
	//	File data status
	
	private int m_status = FILE_AVAILABLE;
	
	//	Open file count
	
	private int m_openCount;
	
    // Sharing mode and PID of first process to open the file

    private int m_sharedAccess = SharingMode.READWRITE + SharingMode.DELETE;
    private int m_pid = -1;
	
	//	Cache of various file information
	
	private Hashtable<String, Object> m_cache;
	
	//	File lock list, allocated once there are active locks on this file
	
	private FileLockList m_lockList;
	
	// Oplock details

	private OpLockDetails m_oplock;

	//	Retention period expiry date/time
	
	private long m_retainUntil = -1L;
	
    // Pseudo file list
    
    private PseudoFileList m_pseudoFiles;
    
    // File timestamps updated only whilst file is open
    
    private long m_accessDate;
    private long m_modifyDate;
    private long m_changeDate;
		
    // File allocation size
    
    private long m_allocSize;
    
    // Filesystem specific object
    
    private Object m_filesysObj;
    
	/**
	 * Class constructor
	 * 
	 * @param fname String
	 * @param caseSensitive boolean
	 */
	public FileState(String fname, boolean caseSensitive) {
	  
	  //	Normalize the file path
	  
	  setPath(fname, caseSensitive);
	  setExpiryTime(System.currentTimeMillis() + DefTimeout);
	  
	  //	Set the file/folder status
	  
	  m_fileStatus = FileStatus.Unknown;
	}

	/**
	 * Class constructor
	 * 
	 * @param fname String
	 * @param status int
     * @param caseSensitive boolean
	 */
	public FileState(String fname, int status, boolean caseSensitive) {
	  
	  //	Normalize the file path
	  
	  setPath(fname, caseSensitive);
	  setExpiryTime(System.currentTimeMillis() + DefTimeout);
	  
	  //	Set the file/folder status
	  
	  m_fileStatus = status;
	}

	/**
	 * Return the file name/path
	 * 
	 * @return String
	 */
	public final String getPath() {
	  return m_path;
	}
	
	/**
	 * 	Return the file exists state
	 * 
	 * @return boolean
	 */
	public final boolean fileExists() {
	  if ( m_fileStatus == FileStatus.FileExists || m_fileStatus == FileStatus.DirectoryExists)
	    return true;
	  return false;
	}
	
	/**
	 * Return the file status
	 * 
	 * @return int
	 */
	public final int getFileStatus() {
	  return m_fileStatus;
	}
	
	/**
	 * Return the directory state
	 * 
	 * @return boolean
	 */
	public final boolean isDirectory() {
	  return m_fileStatus == FileStatus.DirectoryExists ? true : false;
	}
	
	/**
	 * Return the file open count
	 * 
	 * @return int
	 */
	public final int getOpenCount() {
		return m_openCount;
	}

	/**
	 * Get the file id
	 * 
	 * @return int
	 */
	public final int getFileId() {
		return m_fileId;
	}

	/**
	 * Return the shared access mode
	 * 
	 * @return int
	 */
	public final int getSharedAccess() {
	  return m_sharedAccess;
	}
	
    /**
     * Return the PID of the first process to open the file, or -1 if the file is not open
     * 
     * @return int
     */
    public final int getProcessId() {
    	return m_pid;
    }
    
	/**
	 * Return the file status
	 * 
	 * @return int
	 */
	public final int getStatus() {
		return m_status;
	}
	
	/**
	 * Check if there are active locks on this file
	 * 
	 * @return boolean
	 */
	public final boolean hasActiveLocks() {
		if ( m_lockList != null && m_lockList.numberOfLocks() > 0)
			return true;
		return false;
	}
	
	/**
	 * Check if this file state does not expire
	 * 
	 * @return boolean
	 */
	public final boolean hasNoTimeout() {
		return m_tmo == NoTimeout ? true : false;
	}

	/**
	 * Check if the file/folder is under retention
	 * 
	 * @return boolean
	 */
	public final boolean hasActiveRetentionPeriod() {
	  if ( m_retainUntil == -1L)
	    return false;
	  return System.currentTimeMillis() < m_retainUntil ? true : false;
	}
	
	/**
	 * Get the retention period expiry date/time for the file/folder
	 * 
	 * @return long
	 */
	public final long getRetentionExpiryDateTime() {
	  return m_retainUntil;
	}
	
    /**
     * Determine if the file/folder exists
     * 
     * @return boolen
     */
    public final boolean exists()
    {
        if ( m_fileStatus == FileStatus.FileExists ||
                m_fileStatus == FileStatus.DirectoryExists)
            return true;
        return false;
    }

	/**
	 * Check if the file can be opened depending on any current file opens and the sharing mode of the
	 * first file open
	 * 
	 * @param params FileOpenParams
	 * @return boolean
	 */
	public final boolean allowsOpen( FileOpenParams params) {
	
	  //	If the file is not currently open then allow the file open
	  
	  if ( getOpenCount() == 0)
	    return true;
	  
	  //	Check the shared access mode
	  
	  if ( getSharedAccess() == SharingMode.READWRITE &&
	       params.getSharedAccess() == SharingMode.READWRITE)
	    return true;
	  else if (( getSharedAccess() & SharingMode.READ) != 0 &&
	      params.isReadOnlyAccess())
	    return true;
	  else if(( getSharedAccess() & SharingMode.WRITE) != 0 &&
	      params.isWriteOnlyAccess())
	    return true;
	  
	  //	Sharing violation, do not allow the file open
	  
	  return false;
	}
	
	/**
	 * Increment the file open count
	 * 
	 * @return int
	 */
	public final synchronized int incrementOpenCount() {
		m_openCount++;
		
		//	Debug
		
//		if ( m_openCount > 1)
//			Debug.println("@@@@@ File open name=" + getPath() + ", count=" + m_openCount);
		return m_openCount;
	}
	
	/**
	 * Decrement the file open count
	 * 
	 * @return int
	 */
	public final synchronized int decrementOpenCount() {
		
		//	Debug
		
		if ( m_openCount <= 0)
			Debug.println("@@@@@ File close name=" + getPath() + ", count=" + m_openCount + " <<ERROR>>");
		else
			m_openCount--;
			
		return m_openCount;
	}
	
	/**
	 * Check if the file state has expired
	 * 
	 * @param curTime long
	 * @return boolean
	 */
	public final boolean hasExpired(long curTime) {
	  if ( m_tmo == NoTimeout)
	  	return false;
	  if ( curTime > m_tmo)
	  	return true;
	  return false;
	}
	
	/**
	 * Return the number of seconds left before the file state expires
	 * 
	 * @param curTime long
	 * @return long
	 */
	public final long getSecondsToExpire(long curTime) {
		if ( m_tmo == NoTimeout)
			return -1;
		return ( m_tmo - curTime)/1000L;
	}
	
	/**
	 * Return a file status code as a string
	 * 
	 * @return String
	 */
	public final String getStatusAsString() {
		if ( m_status >= 0 && m_status < _fileStates.length)
			return _fileStates[m_status];
		return "Unknown";
	}
	
	/**
	 * Set the file status
	 * 
	 * @param status int
	 */
	public final void setFileStatus(int status) {
	  m_fileStatus = status;
	}
	
	/**
	 * Set the file identifier
	 * 
	 * @param id int
	 */
	public final void setFileId(int id) {
		m_fileId = id;
	}
	
	/**
	 * Set the file state expiry time
	 * 
	 * @param expire long
	 */
	public final void setExpiryTime(long expire) {
		m_tmo = expire;
	}

	/**
	 * Set the retention preiod expiry date/time
	 * 
	 * @param expires long
	 */
	public final void setRetentionExpiryDateTime(long expires) {
	  m_retainUntil = expires;
	}
	
	/**
	 * Set the shared access mode, from the first file open
	 * 
	 * @param mode int
	 */
	public final void setSharedAccess( int mode) {
	  if ( getOpenCount() == 0)
	    m_sharedAccess = mode;
	}
	
	/**
	 * Set the file status
	 * 
	 * @param sts int
	 */
	public final void setStatus(int sts) {
		m_status = sts;
	}

	/**
	 * Add an attribute to the file state
	 * 
	 * @param name String
	 * @param attr Object
	 */
	public final synchronized void addAttribute(String name, Object attr) {
	  if ( m_cache == null)
	  	m_cache = new Hashtable<String, Object>();
	  m_cache.put(name,attr);
	}
	
	/**
	 * Find an attribute
	 * 
	 * @param name String
	 * @return Object
	 */
	public final Object findAttribute(String name) {
	  if ( m_cache == null)
	  	return null;
	  return m_cache.get(name);
	}
	
	/**
	 * Remove an attribute from the file state
	 * 
	 * @param name String
	 * @return Object
	 */
	public final synchronized Object removeAttribute(String name) {
	  if ( m_cache == null)
	  	return null;
	  return m_cache.remove(name);
	}
	
	/**
	 * Remove all attributes from the file state
	 */
	public final synchronized void removeAllAttributes() {
	  if ( m_cache != null)
	  	m_cache.clear();
	  m_cache = null;
	}

	/**
	 * Set the file path
	 * 
	 * @param path String
	 * @param caseSensitive boolean
	 */
	public final void setPath(String path, boolean caseSensitive) {

		//	Split the path into directories and file name, only uppercase the directories to normalize
		//	the path.

		m_path = normalizePath(path, caseSensitive);		
	}

    /**
     * Set the PID of the process opening the file
     * 
     * @param pid int
     */
    public final void setProcessId(int pid) {
    	if ( getOpenCount() == 0)
    		m_pid = pid;
    }
    
	/**
	 * Return the count of active locks on this file
	 *
	 * @return int
	 */	
	public final int numberOfLocks() {
		if ( m_lockList != null)
			return m_lockList.numberOfLocks();
		return 0;
	}
	
	/**
	 * Add a lock to this file
	 *
	 * @param lock FileLock
	 * @exception LockConflictException
	 */
	public final void addLock(FileLock lock)
		throws LockConflictException {
			
		//	Check if the lock list has been allocated
		
		if ( m_lockList == null) {
			
			synchronized (this) {
				
				//	Allocate the lock list, check if the lock list has been allocated elsewhere
				//	as we may have been waiting for the lock
				
				if ( m_lockList == null)
					m_lockList = new FileLockList();
			}
		}
		
		//	Add the lock to the list, check if there are any lock conflicts
		
		synchronized (m_lockList) {
			
			//	Check if the new lock overlaps with any existing locks
			
			if ( m_lockList.allowsLock(lock)) {
				
				//	Add the new lock to the list
				
				m_lockList.addLock(lock);
			}
			else
				throw new LockConflictException();
		}
	}
	
	/**
	 * Remove a lock on this file
	 * 
	 * @param lock FileLock
	 * @exception NotLockedException
	 */
	public final void removeLock(FileLock lock)
		throws NotLockedException {
			
		//	Check if the lock list has been allocated
		
		if ( m_lockList == null)
			throw new NotLockedException();
			
		//	Remove the lock from the active list
		
		synchronized ( m_lockList) {
			
			//	Remove the lock, check if we found the matching lock
			
			if ( m_lockList.removeLock(lock) == null)
				throw new NotLockedException();
		}
	}

	/**
	 * Check if the file is readable for the specified section of the file and process id
	 * 
	 * @param offset long
	 * @param len long
	 * @param pid int
	 * @return boolean
	 */
	public final boolean canReadFile(long offset, long len, int pid) {
		
		//	Check if the lock list is valid
		
		if ( m_lockList == null)
			return true;
			
		//	Check if the file section is readable by the specified process

		boolean readOK = false;
				
		synchronized ( m_lockList) {

			//	Check if the file section is readable
			
			readOK = m_lockList.canReadFile(offset, len, pid);						
		}
		
		//	Return the read status
		
		return readOK;
	}
	
	/**
	 * Check if the file is writeable for the specified section of the file and process id
	 * 
	 * @param offset long
	 * @param len long
	 * @param pid int
	 * @return boolean
	 */
	public final boolean canWriteFile(long offset, long len, int pid) {
		
		//	Check if the lock list is valid
		
		if ( m_lockList == null)
			return true;
			
		//	Check if the file section is writeable by the specified process

		boolean writeOK = false;
				
		synchronized ( m_lockList) {

			//	Check if the file section is writeable
			
			writeOK = m_lockList.canWriteFile(offset, len, pid);						
		}
		
		//	Return the write status
		
		return writeOK;
	}

	/**
	 * Check if the file has an active oplock
	 * 
	 * @return boolean
	 */
	public final boolean hasOpLock() {
		return m_oplock != null ? true : false;
	}

	/**
	 * Return the oplock details
	 * 
	 * @return OpLockDetails
	 */
	public final OpLockDetails getOpLock() {
		return m_oplock;
	}

	/**
	 * Set the oplock for this file
	 * 
	 * @param oplock OpLockDetails
	 * @exception ExistingOpLockException If there is an active oplock on this file
	 */
	public final synchronized void setOpLock(OpLockDetails oplock)
		throws ExistingOpLockException {

		if ( m_oplock == null)
			m_oplock = oplock;
		else
			throw new ExistingOpLockException();
	}

	/**
	 * Clear the oplock
	 */
	public final synchronized void clearOpLock() {
		m_oplock = null;
	}

    /**
     * Determine if a folder has pseudo files associated with it
     * 
     * @return boolean
     */
    public final boolean hasPseudoFiles()
    {
        if ( m_pseudoFiles != null)
            return m_pseudoFiles.numberOfFiles() > 0;
        return false;
    }
    
    /**
     * Return the pseudo file list
     * 
     * @return PseudoFileList
     */
    public final PseudoFileList getPseudoFileList()
    {
        return m_pseudoFiles;
    }
    
    /**
     * Add a pseudo file to this folder
     * 
     * @param pfile PseudoFile
     */
    public final void addPseudoFile(PseudoFile pfile)
    {
        if ( m_pseudoFiles == null)
            m_pseudoFiles = new PseudoFileList();
        m_pseudoFiles.addFile( pfile);
    }
    
    /**
     * Check if the access date/time has been set
     * 
     * @return boolean
     */
    public final boolean hasAccessDateTime() {
    	return m_accessDate != 0L ? true : false;
    }
    
    /**
     * Return the access date/time
     * 
     * @return long
     */
    public final long getAccessDateTime() {
    	return m_accessDate;
    }
    
    /**
     * Update the access date/time
     */
    public final void updateAccessDateTime() {
    	m_accessDate = System.currentTimeMillis();
    }
    
    /**
     * Check if the change date/time has been set
     * 
     * @return boolean
     */
    public final boolean hasChangeDateTime() {
    	return m_changeDate != 0L ? true : false;
    }
    
    /**
     * Return the change date/time
     * 
     * @return long
     */
    public final long getChangeDateTime() {
    	return m_changeDate;
    }
    
    /**
     * Update the change date/time
     */
    public final void updateChangeDateTime() {
    	m_changeDate = System.currentTimeMillis();
    }
    
    /**
     * Check if the modification date/time has been set
     * 
     * @return boolean
     */
    public final boolean hasModifyDateTime() {
    	return m_modifyDate != 0L ? true : false;
    }
    
    /**
     * Return the modify date/time
     * 
     * @return long
     */
    public final long getModifyDateTime() {
    	return m_modifyDate;
    }
    
    /**
     * Update the modify date/time
     */
    public final void updateModifyDateTime() {
    	m_modifyDate = System.currentTimeMillis();
    	m_accessDate = m_modifyDate;
    }
    
    /**
     * Update the modify date/time
     * 
     * @param modTime long
     */
    public final void updateModifyDateTime( long modTime) {
    	m_modifyDate = modTime;
    }
    
    /**
     * Check if there is a filesystem object
     * 
     * @return boolean
     */
    public final boolean hasFilesystemObject() {
    	return m_filesysObj != null ? true : false;
    }
    
    /**
     * Return the filesystem object
     * 
     * @return Object
     */
    public final Object getFilesystemObject() {
    	return m_filesysObj;
    }
    
    /**
     * Set the filesystem object
     * 
     * @param filesysObj Object
     */
    public final void setFilesystemObject( Object filesysObj) {
    	m_filesysObj = filesysObj;
    }
    
    /**
     * Check if the allocation size has been set
     * 
     * @return boolean
     */
    public final boolean hasAllocationSize() {
        return m_allocSize > 0 ? true : false;
    }
    
    /**
     * Return the allocation size
     * 
     * @return long
     */
    public final long getAllocationSize() {
        return m_allocSize;
    }
    
    /**
     * Set the allocation size
     * 
     * @param allocSize long
     */
    public final void setAllocationSize(long allocSize) {
        m_allocSize = allocSize;
    }
    
    /**
     * Normalize the path to uppercase the directory names and keep the case of the file name.
     * 
     * @param path String
     * @return String
     */
    public final static String normalizePath(String path) { 
        return normalizePath( path, true);
    }
    
	/**
	 * Normalize the path to uppercase the directory names and keep the case of the file name.
	 * 
	 * @param path String
	 * @param caseSensitive boolean
	 * @return String
	 */
	public final static String normalizePath(String path, boolean caseSensitive) {	
		
	    // Check if the file state names should be case sensitive, if not then just uppercase the whole
	    // path
	    
        String normPath = path;
        
	    if ( caseSensitive == true) {
	        
    		//	Split the path into directories and file name, only uppercase the directories to normalize
    		//	the path.
    
    		if ( path.length() > 3) {
    			
    			//	Split the path to seperate the folders/file name
    			
    			int pos = path.lastIndexOf(FileName.DOS_SEPERATOR);
    			if ( pos != -1) {
    				
    				//	Get the path and file name parts, normalize the path
    				
    				String pathPart = upperCaseAToZ( path.substring(0, pos));
    				String namePart = path.substring(pos);
    				
    				//	Rebuild the path string
    				
    				normPath = pathPart + namePart;
    			}
    		}
	    }
	    else {

	        // Uppercase the whole path
	        
	        normPath = upperCaseAToZ( path);
	    }
	    
		//	Return the normalized path
		
		return normPath;
	}

	/**
	 * Dump the attributes that are attached to the file state
	 * 
	 * @param out PrintStream
	 */
	public final void DumpAttributes(PrintStream out) {
	
	  //	Check if there are any attributes
	  
	  if ( m_cache != null) {

	    //	Enumerate the available attribute objects
	    
	    Enumeration<String> names = m_cache.keys();
	    
	    while ( names.hasMoreElements()) {
	      
	      //	Get the current attribute name
	      
	      String name = names.nextElement();
	      
	      //	Get the associated attribute object
	      
	      Object attrib = m_cache.get(name);
	      
	      //	Output the attribute details
	      
	      out.println("++    " + name + " : " + attrib);
	    }
	  }
	  else
	    out.println("++    No Attributes");
	}
	
	/**
	 * Uppercase a-z characters only, leave any multi-national characters as is
	 * 
	 * @param path String
	 * @return String
	 */
	protected static final String upperCaseAToZ( String path) {
		StringBuilder pathStr = new StringBuilder( path);
		
		for ( int i = 0; i < pathStr.length(); i++) {
			char curChar = pathStr.charAt( i);
			
			if ( Character.isLowerCase( curChar))
				pathStr.setCharAt( i, Character.toUpperCase( curChar));
		}
		
		return pathStr.toString();
	}
	
	/**
	 * Return the file state as a string
	 * 
	 * @return String
	 */
	public String toString() {
	  StringBuffer str = new StringBuffer();
	  
	  str.append("[");
	  str.append(getPath());
	  str.append(",");
	  str.append(FileStatus.asString(getFileStatus()));
	  str.append(":Opn=");
	  str.append(getOpenCount());
	  str.append(",Str=");
	  
	  str.append(",Fid=");
	  str.append(getFileId());

	  str.append(",Expire=");
	  str.append(getSecondsToExpire(System.currentTimeMillis()));
		
	  str.append(",Sts=");
	  str.append(_fileStates[getStatus()]);

	  str.append(",Locks=");
	  str.append(numberOfLocks());
		
	  if ( hasOpLock()) {
		  str.append(",OpLock=");
		  str.append(getOpLock());
	  }
		
	  str.append("]");
	  
	  return str.toString();
	}
}

