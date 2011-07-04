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

/**
 * Concurrent Create File Test Class
 *
 * @author gkspencer
 */
public class ConcurrentCreateFileTest extends Test {

	// Constants
	
	private static final String TestFileName = "createFile";
	private static final String TestFileExt	 = ".txt";
	
	/**
	 * Default constructor
	 */
	public ConcurrentCreateFileTest() {
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
			
			String testFileName = TestFileName + "_" + iteration + TestFileExt;
			
			// DEBUG
			
			testLog( log, "CreateFile Test");
			
			// Check if the test file exists
			
			if ( sess.FileExists( testFileName))
				testLog( log, "File " + testFileName + " exists");
			else {
				
				// Create a new file
				
				testLog( log, "Creating file " + testFileName + " via " + sess.getServer());
				SMBFile testFile = sess.CreateFile( testFileName);
				if ( testFile != null)
					testFile.Close();
				
				// Check the file exists
				
				if ( sess.FileExists( testFileName)) {
					testLog( log, "Re-check, file now exists");
					result = new BooleanTestResult( true);
				}
				else {
					testLog( log, "** File does not exist after create");
					result = new BooleanTestResult( false);
				}
				
				// Delete the test file
				
				if ( hasTestCleanup() && threadId == 1)
					sess.DeleteFile( testFileName);
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
}
