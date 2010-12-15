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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.OverlappingFileLockException;

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.locking.LockConflictException;
import org.alfresco.jlan.server.filesys.AccessMode;
import org.alfresco.jlan.server.filesys.NetworkFile;
import org.alfresco.jlan.smb.SeekType;

/**
 * Network file implementation that uses the java.io.File class.
 *
 * @author gkspencer
 */
public class NIOJavaNetworkFile extends NetworkFile {

	//	File details

	protected File m_file;

	//	Random access file used to read/write the actual file

	protected RandomAccessFile m_io;

	//	End of file flag

	protected boolean m_eof;

  //	File channel

  protected FileChannel m_channel;

	/**
	 * Class constructor.
	 *
	 * @param file File
	 * @param netPath String
	 */
	public NIOJavaNetworkFile(File file, String netPath) {
		super(file.getName());

		//  Set the file using the existing file object

		m_file = file;

		//  Set the file size

		setFileSize(m_file.length());
		m_eof = false;
    
		//	Set the modification date/time, if available. Fake the creation date/time as it's not
		//	available from the File class
    
		long modDate = m_file.lastModified(); 
		setModifyDate(modDate);
		setCreationDate(modDate);
    
		//	Set the file id
    
		setFileId(netPath.hashCode());
	}

	/**
	 * Class constructor.
	 *
	 * @param name String
	 * @param netPath String
	 */
	public NIOJavaNetworkFile(String name, String netPath) {
		super(name);

		//  Create the file object

		File newFile = new File(name);

		//  Check if the file exists

		if (newFile.exists()) {

			//  Set the file object

			m_file = newFile;
		}
		else {

			//  Convert the file name to lowercase and try again

			String lowerName = name.toLowerCase();
			File newFile2 = new File(lowerName);

			if (newFile2.exists()) {

				//  Set the file

				m_file = newFile2;
			}
			else {

				//  Set the file to be the original file name

				m_file = newFile;

				//  Create the file

				try {
					FileOutputStream outFile = new FileOutputStream(newFile);
					outFile.close();
				}
				catch (Exception ex) {
				}
			}
		}

		//  Set the file size

		setFileSize(m_file.length());
		m_eof = false;
    
		//	Set the modification date/time, if available. Fake the creation date/time as it's not
		//	available from the File class
    
		long modDate = m_file.lastModified(); 
		setModifyDate(modDate);
		setCreationDate(modDate);
    
		//	Set the file id
    
		setFileId(netPath.hashCode());
	}

	/**
	 * Class constructor.
	 *
	 * @param name  File name/path
	 * @param mode  File access mode
	 */
	public NIOJavaNetworkFile(String name, int mode) {
		super(name);

		//  Create the file object

		File newFile = new File(name);

		//  Check if the file exists

		if (newFile.exists() == false) {

			//  Convert the file name to lowercase and try again

			String lowerName = name.toLowerCase();
			File newFile2 = new File(lowerName);

			if (newFile2.exists()) {

				//  Set the file

				m_file = newFile2;
			}
			else {

				//  Set the file to be the original file name

				m_file = newFile;

				//  Create the file, if not opening the file read-only

				if (AccessMode.getAccessMode(mode) != AccessMode.ReadOnly) {

					//  Create a new file

					try {
						FileOutputStream outFile = new FileOutputStream(newFile);
						outFile.close();
					}
					catch (Exception ex) {
					}
				}
			}
		}

		//  Set the file size

		setFileSize(m_file.length());
		m_eof = false;
    
		//	Set the modification date/time, if available. Fake the creation date/time as it's not
		//	available from the File class
    
		long modDate = m_file.lastModified(); 
		setModifyDate(modDate);
		setCreationDate(modDate);
	}

  /**
   * Close the network file.
   */
  public void closeFile() throws java.io.IOException {

    //  Close the file, if used

    if (m_channel != null) {
    	
    	//	Close the file
    	
    	m_io.close();
    	m_io = null;
    	
      m_channel.close();
      m_channel = null;
      
      //	Set the last modified date/time for the file

			if ( this.getWriteCount() > 0)      
      	m_file.setLastModified(System.currentTimeMillis());
      	
      //	Indicate that the file is closed
      
      setClosed(true);
    }
  }

  /**
   * Return the current file position.
   *
   * @return long
   */
  public long currentPosition() {

    //  Check if the file is open

    try {
      if (m_channel != null)
        return m_channel.position();
    }
    catch (Exception ex) {
    }
    return 0;
  }

  /**
   * Flush the file.
   * 
   * @exception IOException
   */
  public void flushFile()
  	throws IOException {
  	
  	//	Flush all buffered data
  	
  	if ( m_channel != null)
  		m_channel.force(false);
  }

  /**
   * Determine if the end of file has been reached.
   *
   * @return boolean
   */
  public boolean isEndOfFile() throws java.io.IOException {

    //  Check if we reached end of file

    if (m_channel != null && m_channel.position() == m_channel.size())
      return true;
    return false;
  }

  /**
   * Open the file.
   * 
   * @param createFlag boolean
   * @exception IOException
   */
  public void openFile(boolean createFlag) throws java.io.IOException {

    //  Open the file

		m_io = new RandomAccessFile(m_file, getGrantedAccess() == NetworkFile.READWRITE ? "rw" : "r");
		m_channel = m_io.getChannel();
		
		//	Indicate that the file is open
		
		setClosed(false);
  }

  /**
   * Read from the file.
   *
   * @param buf byte[]
   * @param len int
   * @param pos int
   * @param fileOff long
   * @return     Length of data read.
   * @exception IOException
   */
  public int readFile(byte[] buf, int len, int pos, long fileOff)
    throws java.io.IOException {

    //  Open the file, if not already open

    if (m_channel == null)
      openFile(false);

		//	Wrap the user buffer
		
		ByteBuffer byteBuf = ByteBuffer.wrap(buf, pos, len);
		
    //  Read from the file

    int rdlen = m_channel.read(byteBuf);
    
    //	Return the actual length of data read
    
    return rdlen;
  }

  /**
   * Seek to the specified file position.
   *
   * @param pos long
   * @param typ int
   * @return long
   * @exception IOException
   */
  public long seekFile(long pos, int typ) throws IOException {

    //  Open the file, if not already open

    if (m_channel == null)
      openFile(false);

    //  Check if the current file position is the required file position

    switch (typ) {

      //  From start of file

      case SeekType.StartOfFile :
        if (currentPosition() != pos)
          m_channel.position(pos);
        break;

        //  From current position

      case SeekType.CurrentPos :
        m_channel.position(currentPosition() + pos);
        break;

        //  From end of file

      case SeekType.EndOfFile :
        {
          long newPos = m_channel.size() + pos;
          m_channel.position(newPos);
        }
        break;
    }

    //  Return the new file position

    return currentPosition();
  }

	/**
	 * Truncate the file
	 * 
	 * @param siz long
   * @exception IOException
	 */
	public void truncateFile(long siz)
		throws IOException {

    //  Open the file, if not already open

    if (m_channel == null)
      openFile(true);

    //  Set the file length

    m_io.setLength(siz);
    
    //	Update the write count to indicate that the file data has changed
    
    incrementWriteCount();
	}
	
  /**
   * Write a block of data to the file.
   *
   * @param buf byte[]
   * @param len int
   * @exception IOException
   */
  public void writeFile(byte[] buf, int len, int pos)
    throws java.io.IOException {

    //  Open the file, if not already open

    if (m_io == null)
      openFile(true);

    //  Write to the file

		ByteBuffer byteBuf = ByteBuffer.wrap(buf, pos, len);
    m_channel.write(byteBuf);
    
    //	Update the write count for the file
    
    incrementWriteCount();
  }

  /**
   * Write a block of data to the file.
   *
   * @param buf byte[]
   * @param len int
   * @param pos int
   * @param offset long
   * @exception IOException
   */
  public void writeFile(byte[] buf, int len, int pos, long offset)
    throws java.io.IOException {

    //  Open the file, if not already open

    if (m_io == null)
      openFile(true);

    //	We need to seek to the write position. If the write position is off the end of the file
    //	we must null out the area between the current end of file and the write position.

    long fileLen = m_io.length();

		if ( offset > fileLen) {
			
			//	Extend the file
			
			m_io.setLength(offset + len);
		}

		//	Check for a zero length write
		
		if ( len == 0)	
			return ;
			
	  //	Seek to the write position
	
	  m_channel.position(offset);
	  
    //  Write to the file

		ByteBuffer byteBuf = ByteBuffer.wrap(buf, pos, len);
    m_channel.write(byteBuf);
    
		//	Update the write count for the file
    
		incrementWriteCount();
  }
  
  /**
   * Lock a byte range within the file
   * 
   * @param lock NIOFileLock
   * @exception IOException
   */
  public void lockFile( NIOFileLock lock)
  	throws IOException {

    //  Open the file, if not already open

    if (m_io == null)
      openFile(true);

    //	Check if lock overlaps any existing locks
    
    if ( hasLocks() && getLockList().allowsLock( lock) == false) {
      Debug.println("** Lock conflict " + lock);
      throw new LockConflictException();
    }
      
    //	If the file is open for write access get an exclusive lock, for read access get a shared lock
    
    boolean sharedLock = getGrantedAccess() == NetworkFile.READONLY ? true : false;
    
    //	Acquire the lock
    
    java.nio.channels.FileLock nioLock = null;
    
    try {
      
      //	Try and acquire the lock

	    if ( lock.isWholeFile()) {
	      if ( sharedLock == true)
	        nioLock = m_channel.tryLock( 0L, getFileSize(), sharedLock);
	      else
	        nioLock = m_channel.tryLock();
	    }
	    else
	      nioLock = m_channel.tryLock( lock.getOffset(), lock.getLength(), sharedLock);
    }
    catch ( OverlappingFileLockException ex) {
      Debug.println("** Lock overlap - " + lock + " **");
    }
    catch ( IOException ex) {
      Debug.println("** Lock IO error - " + lock + " - " + ex.toString() + " **");
    }
    
    //	Failed to get the lock
    
    if ( nioLock == null)
      throw new LockConflictException();
    
    //	Save the lock and add the lock to the active lock list
    
    lock.setNIOLock( nioLock);
    addLock( lock);
    
    Debug.println("** Add lock " + lock + ", cnt=" + getLockList().numberOfLocks());
  }
  
  /**
   * Unlock a byte range within the file
   * 
   * @param lock NIOFileLock
   * @exception IOException
   */
  public void unlockFile( NIOFileLock lock)
  	throws IOException {

    //	Remove the matching lock from the files lock list
    
    if ( getLockList() == null)
      return;
    
    NIOFileLock fLock = (NIOFileLock) getLockList().removeLock( lock);
    
    //	Release the lock
    
    if ( fLock != null && fLock.getNIOLock() != null) {
      
      //	Release the NIO file lock
      
      fLock.getNIOLock().release();
      fLock.setNIOLock( null);

      Debug.println("** Remove lock " + lock + ", cnt=" + getLockList().numberOfLocks());
    }
  }
}
