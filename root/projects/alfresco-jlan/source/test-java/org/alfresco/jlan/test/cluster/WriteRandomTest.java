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
import java.util.Random;

import org.alfresco.jlan.client.CIFSDiskSession;
import org.alfresco.jlan.client.DiskSession;
import org.alfresco.jlan.client.SMBFile;
import org.alfresco.jlan.client.info.FileInfo;
import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.server.config.InvalidConfigurationException;
import org.alfresco.jlan.server.filesys.AccessMode;
import org.alfresco.jlan.smb.SeekType;
import org.alfresco.jlan.util.MemorySize;
import org.springframework.extensions.config.ConfigElement;

/**
 * Random Access File Write Test Class
 *
 * @author gkspencer
 */
public class WriteRandomTest extends Test {

	// Constants
	//
	// Default file size and write buffer size
	
	private static final long DefaultFileSize	= 10 * MemorySize.MEGABYTE;
	private static final int DefaultWriteSize	= (int) (8 * MemorySize.KILOBYTE);
	
	// Default number of writes
	
	private static final int DefaultWriteCount	= 100; 
		
	// Maximum/minimum allowed file size, write size and write count

	private static final long MinimumFileSize	= 100 * MemorySize.KILOBYTE;
	private static final long MaximumFileSize	= 2 * MemorySize.GIGABYTE;

	private static final int MinimumWriteSize	= 128;
	private static final int MaximumWriteSize	= (int) (64 * MemorySize.KILOBYTE);

	private static final int MinimumWriteCount	= 10;
	private static final int MaximumWriteCount	= 100000;
	
	// File size, write buffer size and write count
	
	private long m_fileSize  = DefaultFileSize;
	private int m_writeSize = DefaultWriteSize;

	private int m_writeCount = DefaultWriteCount;
	
	// Characters to use in the write buffer patterns
	
	private static final String _writePattern = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXZ0123456789";
	
	/**
	 * Default constructor
	 */
	public WriteRandomTest() {
		super( "RandomWrite");
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
		
		// Check if the write count has been specified
		
		valueStr = config.getAttribute( "writeCount");
		if ( valueStr != null) {
			
			// Parse and validate the write count value
			
			try {
				m_writeCount = Integer.parseInt( valueStr);
				
				if ( m_writeCount < MinimumWriteCount || m_writeCount > MaximumWriteCount)
					throw new InvalidConfigurationException( "Invalid write count (" + MinimumWriteCount + " - " + MaximumWriteCount + ")");
			}
			catch ( NumberFormatException ex) {
				throw new InvalidConfigurationException( "Invalid write count, " + valueStr);
			}
		}
	}
	
	/**
	 * Run the random access read/write test
	 *
	 * @param threadId int
	 * @param iteration int
	 * @param sess DiskSession
	 * @param log StringWriter
	 * @return TestResult
	 */
	public TestResult runTest( int threadId, int iteration, DiskSession sess, StringWriter log) {
		
		TestResult result = null;
		SMBFile testFile = null;
		
		try {

			// Create a test file name for this iteration
			
			String testFileName = getUniqueFileName(threadId, iteration, sess); 
			
			// DEBUG
			
			testLog( log, "RandomWrite Test");
			
			// Make sure we are using an NT dialect session
			
			CIFSDiskSession cifsSess = null;
			
			if ( sess instanceof CIFSDiskSession)
				cifsSess = (CIFSDiskSession) sess;
			else {
				result = new BooleanTestResult( false, "Not an NT dialect CIFS session");
				return result;
			}
			
			// Check if the test file exists
			
			if ( sess.FileExists( testFileName)) {
				
				// Open the existing file
				
				testLog( log, "Opening existing file " + testFileName + " via " + sess.getServer());
				testFile = sess.OpenFile( testFileName, AccessMode.ReadWrite);
			}
			else {
				
				// Create a new file
				
				testLog( log, "Creating file " + testFileName + " via " + sess.getServer());
				testFile = cifsSess.CreateFile( testFileName);
				
				// Check the file exists
				
				if ( sess.FileExists( testFileName) == false) {
					testLog( log, "** File does not exist after create");
					result = new BooleanTestResult( false, "File not created, " + testFileName);
				}
			}
			
			// Extend the file to the required size
			
			cifsSess.NTSetEndOfFile( testFile.getFileId(), m_fileSize);
			
			// Check that the file was extended
			
			FileInfo fInfo = cifsSess.getFileInformation( testFileName);
			if ( fInfo.getSize() != m_fileSize) {
				result = new BooleanTestResult( false, "File extend to " + MemorySize.asScaledString( m_fileSize) + " bytes failed");
				return result;
			}
			
			// Refresh the file information to get the latest file size

			testFile.refreshFileInformation();
			
			// Allocate the read/write buffer
			
			byte[] ioBuf = new byte[ m_writeSize];

			// Use a random file position for each write
			
			Random randomPos = new Random();
			int maxPos = (int) (m_fileSize - m_writeSize);
			
			// Write to the file until we hit the required write count
			
			int writeCount = 0;
			int patIdx = 0;
			long writePos = 0L;
			
			while ( writeCount < m_writeCount && result == null) {
				
				// Fill each buffer with a different test pattern
				
				if ( patIdx == _writePattern.length())
					patIdx = 0;
				byte fillByte = (byte) _writePattern.charAt( patIdx++);
				Arrays.fill( ioBuf, fillByte);
			
				// Set the write position
				
				writePos = randomPos.nextInt( maxPos);
				testFile.Seek( writePos, SeekType.StartOfFile);
				
				// Write to the file
				
				testFile.Write( ioBuf, ioBuf.length, 0);

				// Read the data back from the file

				testFile.Seek( writePos, SeekType.StartOfFile);
				int rdlen = testFile.Read( ioBuf);
				
				if ( rdlen != ioBuf.length)
					throw new IOException( "Read did not match buffer length, rdlen=" + rdlen + ", bufferLen=" + ioBuf.length);
				
				// Check that the buffer contains the expected pattern
				
				int chkIdx = 0;
				
				while ( chkIdx < ioBuf.length && result == null) {
					if ( ioBuf[ chkIdx] != fillByte)
						result = new BooleanTestResult( false, "Pattern check failed at position " + writePos + ", writeCount=" + writeCount);
					
					chkIdx++;
				}
				
				// Update the write count
				
				writeCount++;
			}

			// Close the file
			
			testFile.Close();
			testFile = null;

			// If the result has not been set then the test has been successful
			
			if (result == null)
				result = new BooleanTestResult( true);
			
			// Finished
			
			testLog( log, "Test completed");
				
		}
		catch ( Exception ex) {
			Debug.println(ex);

			result = new ExceptionTestResult((Exception) ex);
		}
		finally {
			
			// Make sure the test file is closed
			
			if ( testFile != null) {
				try {
					testFile.Close();
				}
				catch ( Exception ex) {
				}
			}
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
