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
import org.alfresco.jlan.client.SMBFile;
import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.server.filesys.AccessMode;
import org.alfresco.jlan.server.filesys.FileAction;
import org.alfresco.jlan.server.filesys.FileAttribute;
import org.alfresco.jlan.smb.SMBException;
import org.alfresco.jlan.smb.SMBStatus;
import org.alfresco.jlan.smb.SharingMode;

/**
 * Open File With Shared Read Test Class
 *
 * @author gkspencer
 */
public class OpenFileShareReadTest extends Test {

	/**
	 * Default constructor
	 */
	public OpenFileShareReadTest() {
		super( "OpenFileShareRead");
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
				
				if ( sess.FileExists( testFileName)) {
					if ( isVerbose())
						Debug.println( "File " + testFileName + " exists");
					initOK = true;
				}
				else {
					
					// Create a new file
					
					if ( isVerbose())
						Debug.println( "Creating file " + testFileName + " via " + sess.getServer());
					SMBFile testFile = sess.CreateFile( testFileName);
					if ( testFile != null)
						testFile.Close();
					
					// Check the file exists
	
					if ( sess.FileExists( testFileName))
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
	 * Run the open file with sharing mode test
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
			
			testLog( log, "OpenFileShareRead Test");
			
			// Open an existing file with no shared access
			
			testLog( log, "Opening file " + testFileName + " via " + sess.getServer());
			
			CIFSDiskSession cifsSess = (CIFSDiskSession) sess;
			CIFSFile openFile = null;

			boolean openForRead = false;
			
			try {
				
				// Open existing file allowing read access to others
				
				openFile = cifsSess.NTCreate( "\\" + testFileName, AccessMode.NTReadWrite, FileAttribute.NTNormal,
					       						SharingMode.READ, FileAction.NTOpen, 0, 0);
				
				// If we got the file then hold it open for a short while
				
				if ( openFile != null) {
					
					// DEBUG
					
					testLog ( log, "Opened file " + testFileName + " with no shared access allowed");
					
					// Hold the file open for a short while, other threads should fail to open the file
					
					testSleep( 2000);
					
					// Close the test file
					
					openFile.Close();
					
					// Successful test result
					
					result = new BooleanTestResult( true);
				}
			}
			catch ( SMBException ex) {

				// Check for an access denied error code
				
				if ( ex.getErrorClass() == SMBStatus.NTErr && ex.getErrorCode() == SMBStatus.NTAccessDenied) {
					
					// DEBUG
					
					testLog ( log, "Open failed with access denied error (expected)");
					
					// Indicate that the file should be opened for read access
					
					openForRead = true;
				}
				else {
					
					// DEBUG
					
					testLog ( log, "Open failed with wrong error, ex=" + ex);
					
					result = new ExceptionTestResult( ex);
				}
			}

			// Check if the file should be opened for read access
			
			if ( openForRead == true) {
				
				CIFSFile readFile = null;
				
				try {

					// Open the file for read-only access

					readFile = cifsSess.NTCreate( "\\" + testFileName, AccessMode.NTRead, FileAttribute.NTNormal,
       												SharingMode.READ, FileAction.NTOpen, 0, 0);
					
					// Check if we opened the file
					
					if ( readFile != null) {
						
						// DEBUG
						
						testLog ( log, "Opened file " + testFileName + " for read-only access");
						
						// Clsoe the file
						
						readFile.Close();
						
						// Successful test result
						
						result = new BooleanTestResult( true);
					}
					else {
						
						// DEBUG

						String msg = "Failed to open file for read-only access, no exception";
						testLog ( log, msg);
						
						// Failed test result
						
						result = new BooleanTestResult( false, msg);
					}
				}
				catch ( SMBException ex) {
					
					// DEBUG
					
					testLog ( log, "Failed to open file for read access, ex=" + ex);
					
					result = new ExceptionTestResult( ex);
				}
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
