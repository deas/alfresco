/*
 * Copyright (C) 2006-2011 Alfresco Software Limited.
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

/**
 * Rename Folder Test Class
 *
 * @author gkspencer
 */
public class RenameFolderTest extends Test {

	// Constants
	
	private static final String TestFolderName 	= "fromFolder";
	private static final String TestFolderNew	= "toFolder";
	
	/**
	 * Default constructor
	 */
	public RenameFolderTest() {
		super( "RenameFolder");
	}
	
	/**
	 * Run the rename folder test
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
			
			String testFolderName = TestFolderName + "_" + threadId + "_" + iteration;
			String newFolderName  = TestFolderNew  + "_" + threadId + "_" + iteration;
			
			// DEBUG
			
			testLog( log, "RenameFolder Test");
			
			// Check if the test folder exists
			
			if ( sess.FileExists( testFolderName)) {
				testLog( log, "File " + testFolderName + " exists");
				
				// Set a failure status
				
				result = new BooleanTestResult( false, "Folder already exists, " + testFolderName);
			}
			else {
				
				// Create a new folder
				
				testLog( log, "Creating folder " + testFolderName + " via " + sess.getServer());
				sess.CreateDirectory( testFolderName);
				
				// Check the folder exists
				
				if ( sess.FileExists( testFolderName)) {
					
					// Rename the folder
					
					sess.RenameFile( testFolderName, newFolderName);
					
					// Check that the new folder exists
					
					if ( sess.FileExists( newFolderName)) {
						
						// Check that the old folder name does not exist

						if ( sess.FileExists( testFolderName) == false)
							result = new BooleanTestResult( true);
						else
							result = new BooleanTestResult( false, "Old folder exists after rename, " + testFolderName);
					}
					else
						result = new BooleanTestResult( false, "New folder does not exist after rename, " + newFolderName);
				}
				else {
					testLog( log, "** Folder does not exist after create");
					result = new BooleanTestResult( false, "Folder does not exist, " + testFolderName);
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
		
		sess.DeleteDirectory( TestFolderNew  + "_" + threadId + "_" + iter);
	}
}
