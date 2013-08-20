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
import java.util.ArrayList;
import java.util.List;

import org.alfresco.jlan.client.DiskSession;
import org.alfresco.jlan.client.SMBFile;
import org.alfresco.jlan.client.SearchContext;
import org.alfresco.jlan.client.info.FileInfo;
import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.server.config.InvalidConfigurationException;
import org.alfresco.jlan.server.filesys.FileAttribute;
import org.alfresco.jlan.server.filesys.FileName;
import org.springframework.extensions.config.ConfigElement;

/**
 * Folder Search Test Class
 *
 * @author gkspencer
 */
public class FolderSearchTest extends Test {

	// Constants
	
	private static final String TestFileName = "testFile";
	private static final String TestFileExt	 = ".txt";
	
	private static final String TestFolderName = "testFolder";
	
	// Default number of files/folders to create
	
	private static final int DefaultFileCount	= 100;
	private static final int DefaultFolderCount	= 100;
	
	// Maximum/minimum number of files/folders allowed
	
	private static final int MinimumFileCount	= 10;
	private static final int MaximumFileCount	= 5000;
	
	private static final int MinimumFolderCount	= 10;
	private static final int MaximumFolderCount	= 5000;

	// File/folder counts
	
	private int m_fileCount 	= DefaultFileCount;
	private int m_folderCount 	= DefaultFolderCount;
	
	/**
	 * Default constructor
	 */
	public FolderSearchTest() {
		super( "FolderSearch");
	}
	
	/**
	 * Test specific configuration
	 * 
	 * @param config ConfigElement
	 */
	public void configTest( ConfigElement config)
		throws InvalidConfigurationException {
		
		// Check for a custom file count
		
		String valueStr = config.getAttribute( "numberOfFiles");
		if ( valueStr != null) {
			
			// Parse and validate the file count value
			
			try {
				m_fileCount = Integer.parseInt( valueStr);
				
				if ( m_fileCount < MinimumFileCount || m_fileCount > MaximumFileCount)
					throw new InvalidConfigurationException( "Invalid file count (" + MinimumFileCount + " - " + MaximumFileCount + ")");
			}
			catch ( NumberFormatException ex) {
				throw new InvalidConfigurationException( "Invalid file count, " + valueStr);
			}
		}
		
		// Check for a custom folder count

		valueStr = config.getAttribute( "numberOfFolders");
		
		if ( valueStr != null) {
			
			// Parse and validate the folder count value
			
			try {
				m_folderCount = Integer.parseInt( valueStr);
				
				if ( m_folderCount < MinimumFolderCount || m_folderCount > MaximumFolderCount)
					throw new InvalidConfigurationException( "Invalid folder count (" + MinimumFolderCount + " - " + MaximumFolderCount + ")");
			}
			catch ( NumberFormatException ex) {
				throw new InvalidConfigurationException( "Invalid folder count, " + valueStr);
			}
		}
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
		
		// Create the test files/folders, if this is the first test thread
		
		boolean initOK = false;
		
		if ( threadId == 1) {

			try {
				
				// Create the test files

				StringBuilder fnameStr = new StringBuilder( 64);
				
				for ( int fileIdx = 1; fileIdx <= m_fileCount; fileIdx++) {

					// Build the file name
					
					buildFileName( fnameStr, curIter, fileIdx);

					// Create the test file
					
					SMBFile testFile = sess.CreateFile( fnameStr.toString());
					if ( testFile != null)
						testFile.Close();
				}

				// Create the test folders

				for ( int fileIdx = 1; fileIdx <= m_folderCount; fileIdx++) {

					// Build the folder name
					
					buildFolderName( fnameStr, curIter, fileIdx);

					// Create the test folder

					sess.CreateDirectory( fnameStr.toString());
				}
				
				// Indicate initialization successful
				
				initOK = true;
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

			// DEBUG
			
			testLog( log, "FolderSearch Test");
			
			// Build the search path
			
			String searchPath = "*.*";
			if ( getPath() != null) {
				StringBuilder pathStr = new StringBuilder();
				pathStr.append ( getPath());
				pathStr.append ( "*.*");
				
				searchPath = pathStr.toString();
			}
						
			// Get a full listing from the folder
			
			SearchContext search = sess.StartSearch( searchPath, FileAttribute.Directory + FileAttribute.Normal);
			if ( search == null)
				return new BooleanTestResult( false, "Search for *.* failed");
			
			// First two entries should be the '.' and '..' special folder entries
			
			FileInfo fInfo = search.nextFileInfo();
			if ( fInfo == null)
				return new BooleanTestResult( false, "Null entry returned, expected '.' entry");
			else if ( fInfo.getFileName().equals( ".") == false)
				return new BooleanTestResult( false, "Expected '.' entry, got " + fInfo.getFileName());
			else if ( fInfo.isDirectory() == false)
				return new BooleanTestResult( false, "'.' entry is not a directory");
			
			fInfo = search.nextFileInfo();
			if ( fInfo == null)
				return new BooleanTestResult( false, "Null entry returned, expected '..' entry");
			else if ( fInfo.getFileName().equals( "..") == false)
				return new BooleanTestResult( false, "Expected '..' entry, got " + fInfo.getFileName());
			else if ( fInfo.isDirectory() == false)
				return new BooleanTestResult( false, "'..' entry is not a directory");
			
			// Build file/folder lists
			
			List<String> fileList = new ArrayList<String>( m_fileCount);
			List<String> folderList = new ArrayList<String>( m_folderCount);
			
			// Run the folder search, place file names into the file/folder lists
			
			do {
				
				// Get the next search entry
				
				fInfo = search.nextFileInfo();
				
				if ( fInfo != null) {
					
					// Check if the entry is a file or folder
					
					if ( fInfo.isDirectory()) {
						
						// Check the file name
						
						if ( fInfo.getFileName().endsWith( TestFileExt)) {
							
							// File marked as a folder
							
							result = new BooleanTestResult( false, "File marked as a folder, " + fInfo.getFileName());
						}
						else {
							
							// Add to the folder list
							
							folderList.add( fInfo.getFileName());
						}
							
					}
					else {
						
						// Check the name
						
						if ( fInfo.getFileName().startsWith( TestFolderName)) {
							
							// Folder marked as a file
							
							result = new BooleanTestResult( false, "Folder marked as a file, " + fInfo.getFileName());
						}
						else {
							
							// Add to the file list
							
							fileList.add( fInfo.getFileName());
						}
					}
				}
			} while ( fInfo != null && result == null);
			
			// Check the lists to make sure all expected files/folders have been returned in the search
			
			if ( result == null) {
			
				// Check the test files

				StringBuilder fnameStr = new StringBuilder( 64);
				String fName = null;
				int fileIdx = 1;
				
				while ( fileIdx <= m_fileCount && result == null) {

					// Build the file name
					
					buildFileName( fnameStr, iteration, fileIdx);

					// Check the file exists in the file list
					
					fName = fnameStr.toString();
					
					if ( fileList.contains( fName)) {
						
						// Remove the file name
						
						fileList.remove( fName);
					}
					else if ( folderList.contains( fName)) {
						
						// File name found in the wrong list
						
						result = new BooleanTestResult( false, "Found file name in the folder list, " + fName);
					}
					
					// Update the file index
					
					fileIdx++;
				}

				// Check the test folders

				fileIdx = 1;
				
				while ( fileIdx <= m_folderCount && result == null) {

					// Build the folder name
					
					buildFolderName( fnameStr, iteration, fileIdx);

					// Check the file exists in the file list
					
					fName = fnameStr.toString();
					
					if ( folderList.contains( fName)) {
						
						// Remove the folder name
						
						folderList.remove( fName);
					}
					else if ( fileList.contains( fName)) {
						
						// Folder name found in the wrong list
						
						result = new BooleanTestResult( false, "Found folder name in the file list, " + fName);
					}
					
					// Update the file index
					
					fileIdx++;
				}
				
				// Check if the file and folder lists are empty
				
				if ( result == null) {
					
					// Check if the file list contains and unexpected files
					
					if ( fileList.size() > 0)
						result = new BooleanTestResult( false, "File list contains " + fileList.size() + " unexpected entries");
					else if ( folderList.size() > 0)
						result = new BooleanTestResult( false, "Folder list contains " + folderList.size() + " unexpected entries");
					else
						result = new BooleanTestResult( true);
				}
			}
			
			// Finished
			
			testLog( log, "Test completed");
				
			// 
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

		// Delete the test files/folders
		
		if ( threadId == 1) {
			
			// Delete the test files
			
			StringBuilder fnameStr = new StringBuilder( 64);
			
			for ( int fileIdx = 1; fileIdx <= m_fileCount; fileIdx++) {

				// Build the file name
				
				buildFileName( fnameStr, iter, fileIdx);

				// Delete the test file

				sess.DeleteFile( fnameStr.toString());
			}

			// Delete the test folders

			for ( int fileIdx = 1; fileIdx <= m_folderCount; fileIdx++) {

				// Build the folder name
				
				buildFolderName( fnameStr, iter, fileIdx);

				// Delete the test folder

				sess.DeleteDirectory( fnameStr.toString());
			}
		}				
	}
	
	/**
	 * Build a test file name
	 * 
	 * @param str StringBuilder
	 * @param iter int
	 * @param fileIdx int
	 */
	private void buildFileName( StringBuilder str, int iter, int fileIdx) {
		str.setLength( 0);
		
		str.append( TestFileName);
		str.append( "_");
		str.append( iter);
		str.append( "_");
		str.append( fileIdx);
		str.append( TestFileExt);
	}
	
	/**
	 * Build a test folder name
	 * 
	 * @param str StringBuilder
	 * @param iter int
	 * @param fldrIdx int
	 */
	private void buildFolderName( StringBuilder str, int iter, int fldrIdx) {
		str.setLength( 0);
		
		str.append( TestFolderName);
		str.append( "_");
		str.append( iter);
		str.append( "_");
		str.append( fldrIdx);
	}
}
