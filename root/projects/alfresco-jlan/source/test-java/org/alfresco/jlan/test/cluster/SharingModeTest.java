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
import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.server.filesys.AccessMode;
import org.alfresco.jlan.server.filesys.FileAction;
import org.alfresco.jlan.server.filesys.FileAttribute;
import org.alfresco.jlan.smb.SMBException;
import org.alfresco.jlan.smb.SharingMode;

/**
 * Sharing Test Class
 *
 * @author gkspencer
 */
public class SharingModeTest extends Test {

	// Constants
	
	private static final String TestFileName = "nosharingFile";
	private static final String TestFileExt	 = ".txt";
	
	/**
	 * Default constructor
	 */
	public SharingModeTest() {
		super( "SharingMode");
	}
	
	/**
	 * Run the sharing mode test
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

			// Sleep for a while, depending on the thread id, to stagger/overlap the file creates
/**			
			if ( threadId > 1) {
				try {
					Thread.sleep( threadId * 100L);
				}
				catch (Exception ex) {
				}
			}
**/
			
			// Create the test file with sharing mode none
			
			String testFileName = getPerTestFileName(threadId, iteration);
			
			testLog( log, "Creating file ...");
			
			CIFSDiskSession cifsSess = (CIFSDiskSession) sess;
			CIFSFile noshareFile = cifsSess.NTCreate( "\\" + testFileName, AccessMode.NTReadWrite, FileAttribute.NTNormal,
													  SharingMode.NOSHARING, FileAction.NTCreate, 0, 0);

			testLog( log, "Created file with sharing mode NONE, file=" + noshareFile);
			
			// Sleep a while
			
			try {
				Thread.sleep( 3000L);
			}
			catch (Exception ex) {
			}

			// Close the file
			
			noshareFile.Close();
			testLog( log, "Closed file");
			
			// Successful test result
			
			result = new BooleanTestResult( true);
			
			// Test cleanup
			
			if ( hasTestCleanup() && threadId == 1)
				sess.DeleteFile( testFileName);
			
			// Finished
			
			testLog( log, "Test completed");
		}
		catch ( SMBException ex) {
			testLog( log, "Failed to create file - " + ex.getMessage());
			
			result = new ExceptionTestResult( ex);
		}
		catch ( IOException ex) {
			testLog( log, "Failed to create file - " + ex.getMessage());
			
			result = new ExceptionTestResult( ex);
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
