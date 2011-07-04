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

package org.alfresco.jlan.test.cluster;

import java.io.StringWriter;

import org.alfresco.jlan.client.CIFSDiskSession;
import org.alfresco.jlan.client.CIFSFile;
import org.alfresco.jlan.client.DiskSession;
import org.alfresco.jlan.client.info.FileInfo;
import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.server.filesys.AccessMode;
import org.alfresco.jlan.server.filesys.FileAction;
import org.alfresco.jlan.server.filesys.FileAttribute;
import org.alfresco.jlan.smb.SMBException;
import org.alfresco.jlan.smb.SMBStatus;
import org.alfresco.jlan.smb.SharingMode;
import org.alfresco.jlan.util.MemorySize;

/**
 * Byte Range Locking Test Class
 *
 * @author gkspencer
 */
public class ByteRangeLockingTest extends Test {

	// Constants
	
	private static final long TestFileSize	= MemorySize.KILOBYTE;
	
	/**
	 * Default constructor
	 */
	public ByteRangeLockingTest() {
		super( "ByteRangeLocking");
	}
	
	/**
	 * Initialize the test setup
	 * 
	 * @param threadId int
	 * @param curIter int
	 * @param sess DiskSession
	 * @return boolean
	 */
	public boolean runInit( int threadId, int curIter, DiskSession sess) {
		
		// Create the test file, if this is the first test thread
		
		boolean initOK = false;
		
		if ( threadId == 1) {

			try {
				
				// Check if the test file exists
				
				String testFileName = getPerTestFileName( threadId, curIter);
				
				// Create a new file
				
				if ( isVerbose())
					Debug.println( "Creating file " + testFileName + " via " + sess.getServer());
				
				CIFSDiskSession cifsSess = (CIFSDiskSession) sess;
				CIFSFile testFile = cifsSess.NTCreate( "\\" + testFileName, AccessMode.NTReadWrite, FileAttribute.NTNormal,
   						SharingMode.READWRITEDELETE, FileAction.NTOverwriteIf, 0, 0);

				if ( testFile != null) {

					// Extend the file to 1K

					cifsSess.NTSetEndOfFile( testFile.getFileId(), TestFileSize);
					
					// Close the test file
					
					testFile.Close();
					
					// Check that the file is the required length
					
					FileInfo fInfo = cifsSess.getFileInformation( testFileName);
					if ( fInfo.getSize() < TestFileSize)
						Debug.println( "Test file " + testFileName + " not required size, actual=" + fInfo.getSize());
					else
						initOK = true;
				}
			}
			catch ( Exception ex) {
				Debug.println( ex);
			}
		}
		else
			initOK = true;
		
		// Return the initialization status
		
		return initOK;
	}
	
	/**
	 * Run the byte range locking test
	 *
	 * @param threadId int
	 * @param iteration int
	 * @param sess DiskSession
	 * @param log StringWriter
	 * @return TestResult
	 */
	public TestResult runTest( int threadId, int iteration, DiskSession sess, StringWriter log) {

		TestResult result = null;
		
		try {

			// Create a test file name for this iteration
			
			String testFileName = getPerTestFileName( threadId, iteration);
			
			// DEBUG
			
			testLog( log, "ByteRangeLocking Test");
			
			// Open the test file
			
			testLog( log, "Opening file " + testFileName + " via " + sess.getServer());
			
			CIFSDiskSession cifsSess = (CIFSDiskSession) sess;
			CIFSFile lockFile = null;

			try {
				
				// Use the thread id as the process id for the CIFS requests, locks use the process id
				// to track the lock owner
				
				cifsSess.setProcessId( threadId);
				
				// Open existing file
				
				lockFile = cifsSess.NTCreate( "\\" + testFileName, AccessMode.NTReadWrite, FileAttribute.NTNormal,
					       						SharingMode.READWRITE, FileAction.NTOpen, 0, 0);

				// Lock/unlock a range of bytes
				
				long lockPos = 0L;
				long lockLen = 100L;
				
				for ( int idx = 0; idx < 10; idx++) {
					
					// Lock position and length
					
					boolean isLocked = false;
					
					try {
						
						// Lock a range of bytes, hold the lock for a short while then unlock
						
						lockFile.Lock( lockPos, lockLen);
						isLocked = true;
						
						// DEBUG
						
						testLog( log, "Locked pos=" + lockPos + ", len=" + lockLen);
					}
					catch ( SMBException ex) {
						
						// Check for a lock conflict error code
						
						if (( ex.getErrorClass() == SMBStatus.NTErr && ex.getErrorCode() == SMBStatus.NTLockConflict) ||
								(ex.getErrorClass() == SMBStatus.ErrDos && ex.getErrorCode() == SMBStatus.DOSLockConflict)) {
							
							// DEBUG
							
							testLog ( log, "Lock failed with lock conflict error (expected), " + testFileName);
							result = new BooleanTestResult( true, "Lock conflict error");
						}
						else {
							
							// DEBUG
							
							testLog( log, "Failed to lock file " + testFileName + ", pos=" + lockPos + ", len=" + lockLen + ", ex=" + ex);
							
							result = new ExceptionTestResult( ex);
						}
					}
					catch ( Exception ex) {
						
						// DEBUG
						
						testLog( log, "Failed to lock file " + testFileName + ", pos=" + lockPos + ", len=" + lockLen + ", ex=" + ex);
						
						result = new ExceptionTestResult( ex);
					}
					
					// If we got the lock then release it
					
					if ( isLocked == true) {
						
						try {
							// Release the lock
							
							lockFile.Unlock( lockPos, lockLen);

							// DEBUG
							
							testLog( log, "Unlocked pos=" + lockPos + ", len=" + lockLen);
						}
						catch ( Exception ex) {
							
							// DEBUG
							
							testLog( log, "Failed to unlock file " + testFileName + ", pos=" + lockPos + ", len=" + lockLen + ", ex=" + ex);
							
							result = new ExceptionTestResult( ex);
						}
					}
					else {
						
						// Sleep for a short while
						
						testSleep( 3);
					}
					
					// Update the lock position
					
					lockPos += lockLen;
				}
				
				// Close the lock file
				
				if ( lockFile != null)
					lockFile.Close();
				
				// Set the test result if not already set
				
				if ( result == null)
					result = new BooleanTestResult( true);
			}
			catch ( Exception ex) {
				
				// DEBUG
				
				testLog( log, "Failed to open test file " + testFileName + ", ex=" + ex);
				
				result = new ExceptionTestResult( ex);
			}
			
			// Finished
			
			testLog( log, "Test completed");
				
		}
		catch ( Exception ex) {
			Debug.println(ex);
			
			result = new ExceptionTestResult( ex);
		}
		
		// Return the test result
		
		return result;
	}
	
	/**
	 * Cleanup the test
	 * 
	 * @param threadId int
	 * @param iter int
	 * @param sess DiskSession
	 * @param log StringWriter
	 * @exception Exception
	 */
	public void cleanupTest( int threadId, int iter, DiskSession sess, StringWriter log)
		throws Exception {

		// Delete the test file
		
		if ( threadId == 1)
			sess.DeleteFile( getPerTestFileName( threadId, iter));
	}
}
