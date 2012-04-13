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

import java.io.IOException;
import java.io.StringWriter;

import org.alfresco.jlan.client.CIFSDiskSession;
import org.alfresco.jlan.client.CIFSFile;
import org.alfresco.jlan.client.DiskSession;
import org.alfresco.jlan.client.OplockAdapter;
import org.alfresco.jlan.client.SMBFile;
import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.server.filesys.AccessMode;
import org.alfresco.jlan.server.filesys.FileAction;
import org.alfresco.jlan.server.filesys.FileAttribute;
import org.alfresco.jlan.smb.OpLock;
import org.alfresco.jlan.smb.SMBException;
import org.alfresco.jlan.smb.SMBStatus;
import org.alfresco.jlan.smb.SharingMode;
import org.alfresco.jlan.smb.WinNT;

/**
 * Oplock Grant Test Class
 *
 * @author gkspencer
 */
public class AttributesOnlyOplockGrantTest extends Test {

	/**
	 * Oplock Break Callback Class
	 */
	public class OplockBreakHandler extends OplockAdapter {
	
		// Run log
		
		private StringWriter m_log;
		
		// Oplock break received
		
		private boolean m_breakReceived;
		
		/**
		 * Class constructor
		 * 
		 * @param log StringWriter
		 */
		public OplockBreakHandler( StringWriter log) {
			m_log = log;
		}
		
		/**
		 * Oplock break callback
		 * 
		 * @param cifsFile CIFSFile
		 * @return int
		 */
		public int oplockBreak( CIFSFile cifsFile) {
			
			// DEBUG
			
			testLog( m_log, "Oplock break on file " + cifsFile.getFileName());
			
			// Flush the file
			
			try {
				cifsFile.Flush();
			}
			catch (Exception ex) {
			}

			// Indicate an oplock break was received
			
			m_breakReceived = true;
			
			// Return the oplock break response
			
			return OpLock.TypeNone;
		}
		
		/**
		 * Check if an oplock break was received
		 * 
		 * @return boolean
		 */
		public final boolean breakReceived() {
			return m_breakReceived;
		}
	}
	
	/**
	 * Default constructor
	 */
	public AttributesOnlyOplockGrantTest() {
		super( "AttributesOnlyOplockGrant");
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
		
		try {
			
			// Check if the test file exists
			
			String testFileName = getPerThreadFileName(threadId, curIter);
			
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
		
		// Return the initialization status
		
		return initOK;
	}
	
	/**
	 * Run the oplock grant test
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

			// Open the test file for attributes only access
			
			String testFileName = getPerThreadFileName(threadId, iteration);
			
			CIFSDiskSession cifsSess = (CIFSDiskSession) sess;
			CIFSFile attribFile = cifsSess.NTCreate( testFileName, AccessMode.NTReadAttributesOnly, FileAttribute.NTNormal,
														       SharingMode.READWRITEDELETE, FileAction.NTOpen, 0, 0);

			testLog( log, "Opened for attributes only access on server " + sess.getServer());

			// Now open the same file with an oplock
			
			OplockBreakHandler oplockHandler = new OplockBreakHandler( log);
			CIFSFile oplockFile = cifsSess.NTCreateWithOplock( testFileName, WinNT.RequestBatchOplock + WinNT.RequestExclusiveOplock, oplockHandler, AccessMode.NTReadWrite, FileAttribute.NTNormal,
				       SharingMode.READWRITEDELETE, FileAction.NTOverwriteIf, 0, 0);
			
			if ( oplockFile.getOplockType() != OpLock.TypeNone)
				testLog( log, "Opened file with oplock type " + OpLock.getTypeAsString( oplockFile.getOplockType()));
			else {
				testLog( log, "Failed to get oplock");
				result = new BooleanTestResult( false, "Failed to get oplock on file");
			}
			
			// Ping the server, runs the asynchrounous receive check to see if we received an oplock
			// break from the server
			
			sess.pingServer();

			// Close the attributes only and oplock files
			
			attribFile.Close();
			oplockFile.Close();
			
			// Make sure an oplock break was not received

			if ( result == null) {
				if ( oplockHandler.breakReceived() == true)
					result = new BooleanTestResult( false, "Unexpected oplock break received");
				else
					result = new BooleanTestResult( true);
			}
			
			// Finished
			
			testLog( log, "Test completed");
		}
		catch ( SMBException ex) {

			// Check for a no such object error code
			
			if ( ex.getErrorClass() == SMBStatus.NTErr && ex.getErrorCode() == SMBStatus.NTObjectNotFound) {
				
				// DEBUG
				
				testLog ( log, "Open failed with object not found error (expected)");
				
				// Successful test result
				
				result = new BooleanTestResult( true, "Object not found error (expected)");
			}
			else {
				
				// DEBUG
				
				testLog ( log, "Open failed with wrong error, ex=" + ex);
				
				// Failure test result
				
				result = new ExceptionTestResult( ex);
			}
		}
		catch ( IOException ex) {
			
			// DEBUG
			
			testLog( log, "Open failed with error, ex=" + ex);
			
			// Failure test result
			
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
		
		sess.DeleteFile( getPerThreadFileName( threadId, iter));
	}
}
