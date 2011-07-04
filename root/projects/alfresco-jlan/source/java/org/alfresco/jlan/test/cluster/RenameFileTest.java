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
import org.alfresco.jlan.client.SMBFile;
import org.alfresco.jlan.debug.Debug;

/**
 * Rename File Test Class
 *
 * @author gkspencer
 */
public class RenameFileTest extends Test {

	// Constants
	
	private static final String TestFileName 	= "renameFile";
	private static final String TestFileExt	 	= ".txt";
	private static final String TestFileNewExt	= ".renamed";
	
	/**
	 * Default constructor
	 */
	public RenameFileTest() {
		super( "RenameFile");
	}
	
	/**
	 * Run the rename file test
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
			
			String testFileName = TestFileName + "_" + threadId + "_" + iteration + TestFileExt;
			String newFileName  = TestFileName + "_" + threadId + "_" + iteration + TestFileNewExt;
			
			// DEBUG
			
			testLog( log, "RenameFile Test");
			
			// Check if the test file exists
			
			if ( sess.FileExists( testFileName)) {
				testLog( log, "File " + testFileName + " exists");
				
				// Set a failure status
				
				result = new BooleanTestResult( false, "File already exists, " + testFileName);
			}
			else {
				
				// Create a new file
				
				testLog( log, "Creating file " + testFileName + " via " + sess.getServer());
				SMBFile testFile = sess.CreateFile( testFileName);
				if ( testFile != null)
					testFile.Close();
				
				// Check the file exists
				
				if ( sess.FileExists( testFileName)) {
					
					// Rename the file
					
					sess.RenameFile( testFileName, newFileName);
					
					// Check that the new file exists
					
					if ( sess.FileExists( newFileName)) {
						
						// Check that the old file name does not exist

						if ( sess.FileExists( testFileName) == false)
							result = new BooleanTestResult( true);
						else
							result = new BooleanTestResult( false, "Old file exists after rename, " + testFileName);
					}
					else
						result = new BooleanTestResult( false, "New file does not exist after rename, " + newFileName);
				}
				else {
					testLog( log, "** File does not exist after create");
					result = new BooleanTestResult( false, "File does not exist, " + testFileName);
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
		
		String fName = TestFileName + "_" + threadId + "_" + iter + TestFileNewExt;
		sess.DeleteFile( fName);
	}
}
