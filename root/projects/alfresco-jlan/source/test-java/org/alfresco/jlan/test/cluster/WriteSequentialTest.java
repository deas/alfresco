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
import java.util.Arrays;

import org.alfresco.jlan.client.DiskSession;
import org.alfresco.jlan.client.SMBFile;
import org.alfresco.jlan.client.info.FileInfo;
import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.server.config.InvalidConfigurationException;
import org.alfresco.jlan.server.filesys.AccessMode;
import org.alfresco.jlan.util.MemorySize;
import org.springframework.extensions.config.ConfigElement;

/**
 * Sequential File Write Test Class
 *
 * @author gkspencer
 */
public class WriteSequentialTest extends Test {

	// Constants
	//
	// Default file size and write buffer size
	
	private static final long DefaultFileSize	= 10 * MemorySize.MEGABYTE;
	private static final int DefaultWriteSize	= (int) (8 * MemorySize.KILOBYTE);
	
	// Maximum/minimum allowed file size and write size

	private static final long MinimumFileSize	= 100 * MemorySize.KILOBYTE;
	private static final long MaximumFileSize	= 2 * MemorySize.GIGABYTE;

	private static final int MinimumWriteSize	= 128;
	private static final int MaximumWriteSize	= (int) (64 * MemorySize.KILOBYTE);
	
	// File size and write buffer size
	
	private long m_fileSize  = DefaultFileSize;
	private int m_writeSize = DefaultWriteSize;
	
	// Characters to use in the write buffer patterns
	
	private static final String _writePattern = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXZ0123456789";
	
	/**
	 * Default constructor
	 */
	public WriteSequentialTest() {
		super( "SequentialWrite");
	}
	
	/**
	 * Test specific configuration
	 * 
	 * @param config ConfigElement
	 */
	public void configTest( ConfigElement config)
		throws InvalidConfigurationException {
		
		// Check for a custom file size
		
		String valueStr = config.getAttribute( "fileSize");
		if ( valueStr != null) {
			
			// Parse and validate the file size value
			
			try {
				m_fileSize = MemorySize.getByteValue( valueStr);
				
				if ( m_fileSize < MinimumFileSize || m_fileSize > MaximumFileSize)
					throw new InvalidConfigurationException( "Invalid file size (" + MinimumFileSize + " - " + MaximumFileSize + ")");
			}
			catch ( NumberFormatException ex) {
				throw new InvalidConfigurationException( "Invalid file size, " + valueStr);
			}
		}
		
		// Check for a custom write buffer size

		valueStr = config.getAttribute( "writeSize");
		
		if ( valueStr != null) {
			
			// Parse and validate the write buffer size value
			
			try {
				m_writeSize = MemorySize.getByteValueInt( valueStr);
				
				if ( m_writeSize < MinimumWriteSize || m_writeSize > MaximumWriteSize)
					throw new InvalidConfigurationException( "Invalid write buffer size (" + MinimumWriteSize + " - " + MaximumWriteSize + ")");
			}
			catch ( NumberFormatException ex) {
				throw new InvalidConfigurationException( "Invalid write buffer size, " + valueStr);
			}
		}
	}
	
	
	/**
	 * Run the sequential read/write test
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
			
			String testFileName = getUniqueFileName(threadId, iteration, sess);
			
			// DEBUG
			
			testLog( log, "SequentialWrite Test");
			
			// Check if the test file exists
			
			SMBFile testFile = null;
			
			if ( sess.FileExists( testFileName)) {
				
				// Open the existing file
				
				testLog( log, "Opening existing file " + testFileName + " via " + sess.getServer());
				testFile = sess.OpenFile( testFileName, AccessMode.ReadWrite);
			}
			else {
				
				// Create a new file
				
				testLog( log, "Creating file " + testFileName + " via " + sess.getServer());
				testFile = sess.CreateFile( testFileName);
				
				// Check the file exists
				
				if ( sess.FileExists( testFileName) == false) {
					testLog( log, "** File does not exist after create");
					result = new BooleanTestResult( false, "File not created, " + testFileName);
				}
			}
			
			// Allocate the read/write buffer
			
			byte[] ioBuf = new byte[ m_writeSize];
			
			// Write to the file until we hit the required file size
			
			long fileSize = 0L;
			int patIdx = 0;
			
			while ( fileSize < m_fileSize) {
				
				// Fill each buffer with a different test pattern
				
				if ( patIdx == _writePattern.length())
					patIdx = 0;
				byte fillByte = (byte) _writePattern.charAt( patIdx++);
				Arrays.fill( ioBuf, fillByte);
				
				// Write to the file
				
				testFile.Write( ioBuf, ioBuf.length, 0);
				
				// Update the file size
				
				fileSize += ioBuf.length;
			}

			// Make sure all data has been written to the file
			
			testFile.Flush();
			
			// Refresh the file information to get the latest file size

			testFile.refreshFileInformation();
			
			// Check the file is the expected size
			
			if ( testFile.getFileSize() != m_fileSize) {
				result = new BooleanTestResult( false, "File writes to " + MemorySize.asScaledString( m_fileSize) + " bytes failed");
				return result;
			}
			
			// Read the file back and check the test patterns
			
			long readPos = 0L;
			patIdx = 0;
			boolean chkFail = false;
			
			while ( readPos < fileSize && chkFail == false) {
				
				// Read a buffer of data from the file
				
				int rdlen = testFile.Read( ioBuf);
				if ( rdlen != ioBuf.length)
					throw new IOException( "Read did not match buffer length, rdlen=" + rdlen + ", bufferLen=" + ioBuf.length);
				
				// Check that the buffer contains the expected pattern
				
				if ( patIdx == _writePattern.length())
					patIdx = 0;
				byte chkByte = (byte) _writePattern.charAt( patIdx++);
				
				int chkIdx = 0;
				
				while ( chkIdx < ioBuf.length && chkFail == false) {
					if ( ioBuf[ chkIdx] != chkByte) {
						chkFail = true;
						result = new BooleanTestResult( false, "Pattern check failed at position " + readPos + chkIdx);
					}
					
					chkIdx++;
				}

				// Update the read position
				
				readPos += ioBuf.length;
			}
			
			// Close the file
			
			testFile.Close();
			
			// Check the test file size
			
			if ( result == null) {
				FileInfo fInfo = sess.getFileInformation( testFileName);
				if ( fInfo != null) {
					
					// Check if the file size matches what was written
					
					if ( fInfo.getSize() == fileSize)
						result = new BooleanTestResult( true);
					else
						result = new BooleanTestResult( false, "File size mismatch, written=" + fileSize + ", info=" + fInfo.getSize());
				}
				else
					result = new BooleanTestResult( false, "Failed to get file information for file " + testFileName);
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
		
		sess.DeleteFile( getUniqueFileName( threadId, iter, sess));
	}
}
