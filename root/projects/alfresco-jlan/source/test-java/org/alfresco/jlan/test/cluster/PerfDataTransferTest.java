package org.alfresco.jlan.test.cluster;

import java.io.StringWriter;

import org.alfresco.jlan.client.DiskSession;
import org.alfresco.jlan.client.SMBFile;
import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.server.config.InvalidConfigurationException;
import org.alfresco.jlan.smb.SMBException;
import org.alfresco.jlan.util.MemorySize;
import org.springframework.extensions.config.ConfigElement;

/**
 * Data Transfer Performance Test Class
 * 
 * @author gkspencer
 */
public class PerfDataTransferTest extends Test {

	// Constants
	//
	// Default file size and write buffer size
	
	private static final long DefaultFileSize	= 500 * MemorySize.MEGABYTE;
	private static final int DefaultWriteSize	= (int) (32 * MemorySize.KILOBYTE);
	
	// Maximum/minimum allowed file size and write size

	private static final long MinimumFileSize	= 50 * MemorySize.MEGABYTE;
	private static final long MaximumFileSize	= 5 * MemorySize.TERABYTE;

	private static final int MinimumWriteSize	= 128;
	private static final int MaximumWriteSize	= (int) (64 * MemorySize.KILOBYTE);
	
	// File size and write buffer size
	
	private long m_fileSize = DefaultFileSize;
	private int m_writeSize = DefaultWriteSize;
	
	/**
	 * Default constructor
	 */
	public PerfDataTransferTest() {
		super( "PerfDataTransfer");
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
	 * Run the data transfer performance test
	 *
	 * @param threadId int
	 * @param iteration int
	 * @param sess DiskSession
	 * @param log StringWriter
	 * @return TestResult
	 */
	public TestResult runTest( int threadId, int iteration, DiskSession sess, StringWriter log) {
		
		// Only run performance tests with one thread
		
		if (threadId > 1)
			return new BooleanTestResult( true);
		
		TestResult result = null;
		
		try {

			// Create a test file name for this iteration
			
			String testFileName = getPerTestFileName( threadId, iteration);
			
			// DEBUG
			
			testLog( log, "Data Transfer Performance Test");
			
			// Check if the test file exists
			
			if ( sess.FileExists( testFileName)) {
				testLog( log, "File " + testFileName + " exists");
				
				// Set a failure status
				
				result = new BooleanTestResult( true, "File already exists, " + testFileName);
			}
			else {
				
				// Allocate the I/O buffer
				
				byte[] ioBuf = new byte[ m_writeSize];

				// Record the start time
				
				long startTime = System.currentTimeMillis();
				long endTime = 0L;
				
				try {

					// Create a new file
					
					SMBFile testFile = sess.CreateFile( testFileName);
					
					// Write to the file until we hit the required file size
					
					long fileSize = 0L;
					
					while ( fileSize < m_fileSize) {
						
						// Write to the file
						
						testFile.Write( ioBuf, ioBuf.length, 0);
						
						// Update the file size
						
						fileSize += ioBuf.length;
					}

					// Make sure all data has been written to the file
					
					testFile.Flush();

					// Close the test file
					
					testFile.Close();
					
					// Save the end time
					
					endTime = System.currentTimeMillis();
					
					// If there were no errors then output the elapsed time
					
					if ( result == null) {
						
						// Output the elapsed time
						
						long elapsedMs = endTime - startTime;
						int ms = (int) (elapsedMs % 1000L);
						
						long elapsedSecs = elapsedMs/1000;
						int secs = (int) ( elapsedSecs % 60L);
						int mins = (int) (( elapsedSecs/60L) % 60L);
						int hrs  = (int) ( elapsedSecs/3600L);
						
						// Calculate the average throughput
						
						long throughput = m_fileSize/elapsedSecs;
						
						testLog( log, "Created " + testFileName + " (size " + MemorySize.asScaledString( m_fileSize) + "/writes " + MemorySize.asScaledString( m_writeSize) +
								") in " + hrs + ":" + mins + ":" + secs + "." + ms +
								" (" + elapsedMs + "ms) with average speed of " + MemorySize.asScaledString( throughput) + "/sec");
						
						// Return a success status
						
						result = new BooleanTestResult( true);
					}
				}
				catch ( SMBException ex) {
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
			String fName = getPerTestFileName( threadId, iter);
			testLog( log, "Cleanup test file " + fName);
			
			if ( sess.FileExists( fName))
				sess.DeleteFile( fName);
		}
	}
}
