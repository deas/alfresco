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

import org.alfresco.jlan.client.DiskSession;
import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.smb.SMBException;
import org.alfresco.jlan.smb.SMBStatus;

/**
 * Create Folder Test Class
 *
 * @author gkspencer
 */
public class CreateFolderTest extends Test {

	/**
	 * Default constructor
	 */
	public CreateFolderTest() {
		super( "CreateFolder");
	}
	
	/**
	 * Run the create folder test
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

			// Create a test folder name for this iteration
			
			String testFolderName = getPerTestFolderName( threadId, iteration);
			
			// DEBUG
			
			testLog( log, "CreateFolder Test");
			
			// Check if the test folder exists
			
			if ( sess.FileExists( testFolderName)) {
				testLog( log, "Folder " + testFolderName + " exists");
				
				result = new BooleanTestResult( true);
			}
			else {
				
				// Create a new folder
				
				testLog( log, "Creating folder " + testFolderName + " via " + sess.getServer());
				
				try {

					// Create the folder
					
					sess.CreateDirectory( testFolderName);
					
					// Check the folder exists
					
					if ( sess.FileExists( testFolderName)) {
						testLog( log, "Re-check, folder now exists, " + testFolderName);
						result = new BooleanTestResult( true);
					}
					else {
						testLog( log, "** Folder does not exist after create, " + testFolderName);
						result = new BooleanTestResult( false, "Folder does not exist after create, " + testFolderName);
					}
				}
				catch ( SMBException ex) {

					// Check for an access denied error code
					
					if ( ex.getErrorClass() == SMBStatus.NTErr && ex.getErrorCode() == SMBStatus.NTObjectNameCollision) {
						
						// DEBUG
						
						testLog ( log, "Create failed with object name collision (expected), " + testFolderName);
						result = new BooleanTestResult( true);
					}
					else
						result = new ExceptionTestResult( ex);
				}
			}
			
			// Finished
			
			testLog( log, "Test completed");
				
		}
		catch ( Exception ex) {
			Debug.println(ex);
			
			result = new ExceptionTestResult(ex);
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
		
		if ( threadId == 1) {
			String fName = getPerTestFolderName( threadId, iter);
			testLog( log, "Cleanup test folder " + fName);
			
			sess.DeleteDirectory( fName);
		}
	}
}
