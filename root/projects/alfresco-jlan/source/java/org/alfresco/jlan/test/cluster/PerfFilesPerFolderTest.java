package org.alfresco.jlan.test.cluster;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.alfresco.jlan.client.DiskSession;
import org.alfresco.jlan.client.SMBFile;
import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.server.config.InvalidConfigurationException;
import org.alfresco.jlan.server.filesys.FileName;
import org.alfresco.jlan.smb.SMBException;
import org.alfresco.jlan.util.MemorySize;
import org.springframework.extensions.config.ConfigElement;

/**
 * Files Per Folder Performance Test Class
 *  
 * @author gkspencer
 */
public class PerfFilesPerFolderTest extends Test {

	// Constants
	//
	// Default number of files and file size
	
	private static final int DefaultNumberOfFiles	= 2000;
	private static final long DefaultFileSize		= 4 * MemorySize.KILOBYTE;
	private static final int DefaultWriteSize		= (int) (4 * MemorySize.KILOBYTE);
	
	// Maximum/minimum number of files
	
	private static final int MinimumNumberOfFiles	= 50;
	private static final int MaximumNumberOfFiles	= 5000;
	
	// Maximum/minimum allowed file size and write size

	private static final long MinimumFileSize	= 1;// byte
	private static final long MaximumFileSize	= 10 * MemorySize.MEGABYTE;

	private static final int MinimumWriteSize	= 128;
	private static final int MaximumWriteSize	= (int) (64 * MemorySize.KILOBYTE);
	
	// File name components
	//
	// File name format is '<prefix>_<mainpart>_<index>_<random>.txt'
	//
	// Prefixes
	
	private static String[] _namePrefixes = { "aaa", "bbb", "ccc", "ddd", "eee", "fff", "ggg", "hhh", "iii", "jjj", "kkk", "lll", "mmm", "nnn", "ooo", "ppp", "qqq",
		"rrr", "sss", "ttt", "uuu", "vvv", "www", "xxx", "yyy", "zzz", "AAA", "BBB", "CCC", "DDD", "EEE", "FFF", "GGG", "HHH", "III", "JJJ", "KKK", "LLL", "MMM",
		"NNN", "OOO", "PPP", "QQQ", "RRR", "SSS", "TTT", "UUU", "VVV", "WWW", "XXX", "YYY", "ZZZ"
	};
	
	// Test file name main part and extension
	
	private static String _testFileName	= "_FilesPerFolder_";
	private static String _testFileExt	= ".txt";
	
	// Number of files per folder, file size and file write buffer size
	
	private int m_filesPerFolder = DefaultNumberOfFiles;

	private long m_fileSize = DefaultFileSize;
	private int m_writeSize = DefaultWriteSize;
	
	// Test folder path and list of file names created by this test
	
	private String m_testFolder;
	private ArrayList<String> m_fnameList;
	
	/**
	 * Default constructor
	 */
	public PerfFilesPerFolderTest() {
		super( "PerfFilesPerFolder");
	}
	
	/**
	 * Test specific configuration
	 * 
	 * @param config ConfigElement
	 */
	public void configTest( ConfigElement config)
		throws InvalidConfigurationException {
		
		// Check for a custom number of files
		
		String valueStr = config.getAttribute( "fileCount");
		if ( valueStr != null) {
			
			// Parse and validate the file count value
			
			try {
				m_filesPerFolder = Integer.parseInt( valueStr);
				
				if ( m_filesPerFolder < MinimumNumberOfFiles || m_filesPerFolder > MaximumNumberOfFiles)
					throw new InvalidConfigurationException( "Invalid files per folder (" + MinimumNumberOfFiles + " - " + MaximumNumberOfFiles + ")");
			}
			catch ( NumberFormatException ex) {
				throw new InvalidConfigurationException( "Invalid files per folder, " + valueStr);
			}
		}
		
		// Check for a custom file size
		
		valueStr = config.getAttribute( "fileSize");
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
	 * Run the files per folder performance test
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

			// Create a test folder name for this iteration
			
			m_testFolder = getPerTestFolderName( threadId, iteration);
			
			// DEBUG
			
			testLog( log, "Files Per Folder Performance Test");
			
			// Create the test folder
			
			sess.CreateDirectory( m_testFolder);
			
			// Check if the test folder exists
			
			if ( sess.FileExists( m_testFolder) == false) {
				testLog( log, "Folder " + m_testFolder + " does not exist");
				
				// Set a failure status
				
				result = new BooleanTestResult( true, "Folder does not exist, " + m_testFolder);
			}
			else {
				
				// Make sure the folder is a relative path
				
				String testFolderPrefix = m_testFolder;
				
				if ( testFolderPrefix.startsWith( FileName.DOS_SEPERATOR_STR) == false)
					testFolderPrefix = FileName.DOS_SEPERATOR_STR + testFolderPrefix;
				if ( testFolderPrefix.endsWith( FileName.DOS_SEPERATOR_STR) == false)
					testFolderPrefix = testFolderPrefix + FileName.DOS_SEPERATOR_STR;

				// Allocate the file name list
				
				m_fnameList = new ArrayList<String>( m_filesPerFolder);
				
				// Allocate the I/O buffer
				
				byte[] ioBuf = new byte[ m_writeSize];

				// Record the start time
				
				long startTime = System.currentTimeMillis();
				long endTime = 0L;
				
				// Create the test files
				
				int fileCnt = 1;
				Random randNum = new Random();
				
				StringBuilder testFileStr = new StringBuilder(64);
				String testFileName = null;
				
				while ( fileCnt <= m_filesPerFolder && result == null) {
					
					// Create a unique file name

					testFileStr.setLength( 0);
					
					testFileStr.append( testFolderPrefix);
					testFileStr.append( _namePrefixes[ fileCnt % _namePrefixes.length]);
					testFileStr.append( _testFileName);
					testFileStr.append( fileCnt);
					testFileStr.append( "_");
					testFileStr.append( Long.toHexString( randNum.nextLong()));
					testFileStr.append( _testFileExt);

					testFileName = testFileStr.toString();
					
					// Save the file name for cleanup
					
					m_fnameList.add( testFileName);
					
					// Fill the write buffer with a test pattern
					
					byte testPat = (byte) _namePrefixes[ fileCnt % _namePrefixes.length].charAt( 0);
					Arrays.fill( ioBuf, testPat);
					
					// Create a new file
					
					try {

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

						// Update the file counter
						
						fileCnt++;
					}
					catch ( SMBException ex) {

						// Indicate test error
						
						result = new ExceptionTestResult( ex);
					}
				}
				
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
					
					testLog( log, "Created " + m_filesPerFolder + " files (size " + MemorySize.asScaledString( m_fileSize) + ") in " + hrs + ":" + mins + ":" + secs + "." + ms +
							" (" + elapsedMs + "ms)");
					
					// Return a success status
					
					result = new BooleanTestResult( true);
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
			
			// Delete the test files
			
			if ( m_fnameList != null && m_fnameList.size() > 0) {
				
				// Delete the test files
				
				for ( int idx = 0; idx < m_fnameList.size(); idx++) {
					
					// Delete the test file
					
					sess.DeleteFile( m_fnameList.get( idx));
				}
			}
			
			// Delete the test folder
			
			sess.DeleteDirectory( m_testFolder);
		}
	}
}
