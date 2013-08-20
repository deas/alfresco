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
import org.alfresco.jlan.client.SMBFile;
import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.smb.SMBException;
import org.alfresco.jlan.smb.SMBStatus;

/**
 * Create File Test Class
 *
 * @author gkspencer
 */
public class CreateFileTest extends Test {

	/**
	 * Default constructor
	 */
	public CreateFileTest() {
		super( "CreateFile");
	}
	
	/**
	 * Run the create file test
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
			
			testLog( log, "CreateFile Test");
			
			// Check if the test file exists
			
			if ( sess.FileExists( testFileName)) {
				testLog( log, "File " + testFileName + " exists");
				
				// Set a failure status
				
				result = new BooleanTestResult( true, "File already exists, " + testFileName);
			}
			else {
				
				// Create a new file
				
				try {
					testLog( log, "Creating file " + testFileName + " via " + sess.getServer());
					SMBFile testFile = sess.CreateFile( testFileName);
					if ( testFile != null)
						testFile.Close();

					// Check the file exists

					if ( result == null) {
						if ( sess.FileExists( testFileName)) {
							testLog( log, "Re-check, file now exists, " + testFileName);
							result = new BooleanTestResult( true);
						}
						else {
							testLog( log, "** File does not exist after create, " + testFileName);
							result = new BooleanTestResult( false, "File does not exist after create, " + testFileName);
						}
					}
				}
				catch ( SMBException ex) {

					// Check for an access denied error code
					
					if ( ex.getErrorClass() == SMBStatus.NTErr && ex.getErrorCode() == SMBStatus.NTAccessDenied) {
						
						// DEBUG
						
						testLog ( log, "Create failed with access denied error (expected), " + testFileName);
						result = new BooleanTestResult( true);
					}
					else if ( ex.getErrorClass() == SMBStatus.NTErr && ex.getErrorCode() == SMBStatus.NTObjectNameCollision) {
						
						// DEBUG
						
						testLog ( log, "Create failed with object name collision (expected), " + testFileName);
						result = new BooleanTestResult( true);
					}
					else {
						ex.printStackTrace();
					
						result = new ExceptionTestResult( ex);
					}
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
			String fName = getPerTestFileName( threadId, iter);
			testLog( log, "Cleanup test file " + fName);
			
			if ( sess.FileExists( fName))
				sess.DeleteFile( fName);
		}
	}
}
