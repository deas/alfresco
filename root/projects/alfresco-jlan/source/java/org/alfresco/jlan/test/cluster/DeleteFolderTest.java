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
import org.alfresco.jlan.client.DiskSession;
import org.alfresco.jlan.debug.Debug;

/**
 * Delete Folder Test Class
 *
 * @author gkspencer
 */
public class DeleteFolderTest extends Test {

	/**
	 * Default constructor
	 */
	public DeleteFolderTest() {
		super( "DeleteFolder");
	}
	
	/**
	 * Per run initialization
	 * 
	 * @param threadId int
	 * @param curIter int
	 * @param sess DiskSession
	 * @return boolean
	 */
	public boolean runInit( int threadId, int curIter, DiskSession sess) {
		
		// Create the test folder, if this is the first test thread
		
		boolean initOK = false;
		
		if ( threadId == 1) {

			try {
				
				// Check if the test folder exists
				
				String testFolderName = getPerTestFolderName( threadId, curIter);
				
				if ( sess.FileExists( testFolderName) && sess.isDirectory( testFolderName)) {
					Debug.println("Folder " + testFolderName + " exists");
					initOK = true;
				}
				else {
					
					// Create a new folder
					
					if ( isVerbose())
						Debug.println("Creating folder " + testFolderName + " via " + sess.getServer());
					sess.CreateDirectory( testFolderName);
					
					// Check the folder exists
	
					if ( sess.FileExists( testFolderName))
						initOK = true;
				}
			}
			catch ( Exception ex) {
				Debug.println( "Init error");
				Debug.println ( ex);
			}
		}
		else
			initOK = true;
		
		// Return the initialization status
		
		return initOK;
	}
	
	/**
	 * Run the delete folder test
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

			// Delete the folder
			
			String testFolderName = getPerTestFolderName( threadId, iteration);
			
			CIFSDiskSession cifsSess = (CIFSDiskSession) sess;
			try {
				cifsSess.DeleteDirectory( testFolderName);

				testLog( log, "Deleted folder " + testFolderName + " on server " + sess.getServer());
			}
			catch ( Exception ex) {
				testLog( log, "Error deleting folder " + testFolderName + " on server " + sess.getServer() + ", ex=" + ex.getMessage());
			}

			// Check if the folder exists

			if ( cifsSess.FileExists( testFolderName) && cifsSess.isDirectory( testFolderName)) {
				testLog( log, "Folder status on server " + sess.getServer() + "=Exists");
				result = new BooleanTestResult( false);
			}
			else {
				testLog( log, "Folder status on server " + sess.getServer() + "=NotExist");
				result = new BooleanTestResult( true);
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
}
