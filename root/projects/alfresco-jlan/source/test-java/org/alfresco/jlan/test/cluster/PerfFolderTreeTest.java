package org.alfresco.jlan.test.cluster;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;

import org.alfresco.jlan.client.DiskSession;
import org.alfresco.jlan.client.SMBFile;
import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.server.config.InvalidConfigurationException;
import org.alfresco.jlan.server.filesys.FileName;
import org.alfresco.jlan.smb.SMBException;
import org.alfresco.jlan.util.MemorySize;
import org.springframework.extensions.config.ConfigElement;

/**
 * Folder Tree Copy Performance Test Class
 * 
 * @author gkspencer
 */
public class PerfFolderTreeTest extends Test {

	// Constants
	//
	// Default folder depth and folders per level
	
	private static final int DefaultFolderDepth		= 5;
	private static final int DefaultFoldersPerLevel	= 5;
	
	// Default files per level, file size and write buffer size
	
	private static final int DefaultFilesPerLevel	= 5;
	private static final int DefaultFileSize		= 80;	// bytes
	private static final int DefaultWriteSize	= (int) (8 * MemorySize.KILOBYTE);
	
	// Maximum/minumum folder depth, folders per level, files per level
	
	private static final int MinimumFolderDepth		= 2;
	private static final int MaximumFolderDepth		= 10;
	
	private static final int MinimumFoldersPerLevel = 2;
	private static final int MaximumFoldersPerLevel = 25;
	
	private static final int MinimumFilesPerLevel 	= 0;
	private static final int MaximumFilessPerLevel = 25;
	
	// Maximum/minimum allowed file size and write size

	private static final long MinimumFileSize	= 1;// byte
	private static final long MaximumFileSize	= 16 * MemorySize.KILOBYTE;

	private static final int MinimumWriteSize	= 128;
	private static final int MaximumWriteSize	= (int) (64 * MemorySize.KILOBYTE);
	
	// File/folder names
	
	private static final String LevelFolderName	= "Folder_";
	private static final String LevelFileName	= "File_";
	private static final String LevelFileExt	= ".txt";
	
	// Folder levels, folders per level parameters
	
	private int m_folderDepth 		= DefaultFolderDepth;
	private int m_foldersPerLevel 	= DefaultFoldersPerLevel;

	// Files per level, file size and file write buffer size parameters
	
	private int m_filesPerLevel = DefaultFilesPerLevel;
	private long m_fileSize 	= DefaultFileSize;
	private int m_writeSize 	= DefaultWriteSize;
	
	// List of paths created
	
	private ArrayList<String> m_folderPaths;
	
	// Total folder/file count
	
	private int m_totalFolders;
	private int m_totalFiles;
	
	/**
	 * Default constructor
	 */
	public PerfFolderTreeTest() {
		super( "PerfFolderTree");
	}
	
	/**
	 * Test specific configuration
	 * 
	 * @param config ConfigElement
	 */
	public void configTest( ConfigElement config)
		throws InvalidConfigurationException {
		
		// Check for a custom folder depth
		
		String valueStr = config.getAttribute( "folderDepth");
		if ( valueStr != null) {
			
			// Parse and validate the folder depth value
			
			try {
				m_folderDepth = Integer.parseInt( valueStr);
				
				if ( m_folderDepth < MinimumFolderDepth || m_folderDepth > MaximumFolderDepth)
					throw new InvalidConfigurationException( "Invalid folder depth (" + MinimumFolderDepth + " - " + MaximumFolderDepth + ")");
			}
			catch ( NumberFormatException ex) {
				throw new InvalidConfigurationException( "Invalid folder depth, " + valueStr);
			}
		}
		
		// Check for a custom folders per level value

		valueStr = config.getAttribute( "foldersPerLevel");
		if ( valueStr != null) {
			
			// Parse and validate the folders per level value
			
			try {
				m_foldersPerLevel = Integer.parseInt( valueStr);
				
				if ( m_foldersPerLevel < MinimumFoldersPerLevel || m_foldersPerLevel > MaximumFoldersPerLevel)
					throw new InvalidConfigurationException( "Invalid folders per level (" + MinimumFoldersPerLevel + " - " + MaximumFoldersPerLevel + ")");
			}
			catch ( NumberFormatException ex) {
				throw new InvalidConfigurationException( "Invalid folders per level, " + valueStr);
			}
		}
		
		// Check for a custom files per level value
		
		valueStr = config.getAttribute( "filessPerLevel");
		if ( valueStr != null) {
			
			// Parse and validate the filess per level value
			
			try {
				m_filesPerLevel = Integer.parseInt( valueStr);
				
				if ( m_filesPerLevel < MinimumFilesPerLevel || m_filesPerLevel > MaximumFilessPerLevel)
					throw new InvalidConfigurationException( "Invalid files per level (" + MinimumFilesPerLevel + " - " + MaximumFilessPerLevel + ")");
			}
			catch ( NumberFormatException ex) {
				throw new InvalidConfigurationException( "Invalid files per level, " + valueStr);
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
	 * Run the folder tree performance test
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

			// Create the list for paths created during the test
			
			m_folderPaths = new ArrayList<String>( 1000);
			
			// Create a test folder name for this iteration
			
			String testFolder = getPerTestFolderName( threadId, iteration);

			// DEBUG
			
			testLog( log, "Folder Tree Performance Test");
			
			// Create the test folder
			
			sess.CreateDirectory( testFolder);
			
			// Check if the test folder exists
			
			if ( sess.FileExists( testFolder) == false) {
				testLog( log, "Folder " + testFolder + " does not exist");
				
				// Set a failure status
				
				result = new BooleanTestResult( true, "Folder does not exist, " + testFolder);
			}
			else {

				// Allocate the I/O buffer
				
				byte[] ioBuf = new byte[ m_writeSize];
				Arrays.fill( ioBuf, (byte) 'A');
				
				// Add the folder to the list of paths
				
				m_folderPaths.add( testFolder);
				
				// Make the current folder path a relative path
				
				String curFolder = testFolder;
				
				if ( curFolder.endsWith( FileName.DOS_SEPERATOR_STR) == false)
					curFolder = curFolder + FileName.DOS_SEPERATOR_STR;
				
				// Record the start time
				
				long startTime = System.currentTimeMillis();
				long endTime = 0L;
				
				// Create the path stacks for the current and next tree layers
				
				ArrayList<String> pathStack = new ArrayList<String>( 250);
				ArrayList<String> nextStack = new ArrayList<String>( 250);
				
				// Stack the starting point path
				
				pathStack.add( curFolder);
				
				// Create the folder levels and files
				
				int curLevel = 1;
				
				while ( curLevel <= m_folderDepth && result == null) {
					
					try {
						
						// Add sub-folders for the current level of paths
						
						for ( int pathIdx = 0; pathIdx < pathStack.size(); pathIdx++) {
							
							// Get the current path
							
							String curPath = pathStack.get( pathIdx);
							
							if ( curPath.endsWith( FileName.DOS_SEPERATOR_STR) == false)
								curPath = curPath + FileName.DOS_SEPERATOR_STR;
							
							// Create the current level of files/folders
						
							createFolderLevel( curPath, sess, curLevel, ioBuf, nextStack);
							
							// Add the path to the list of paths to be deleted by cleanup
							
							m_folderPaths.add( pathStack.get( pathIdx));
						}
						
						// Update the folder level
						
						curLevel++;
						
						// Swap the current and next path stacks, clear the next stack
						
						ArrayList<String> tempStack = pathStack;
						pathStack = nextStack;
						nextStack = tempStack;
						
						nextStack.clear();
					}
					catch ( Exception ex) {
						Debug.println(ex);
						
						result = new ExceptionTestResult(ex);
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
					
					testLog( log, "Created folder tree " + m_folderDepth + " folders deep (" + m_foldersPerLevel + " folders/" + m_filesPerLevel + " files per level) in " + hrs + ":" + mins + ":" + secs + "." + ms +
							" (" + elapsedMs + "ms)");
					testLog( log, "Total of " + m_totalFolders + " folders, " + m_totalFiles + " files");
					
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
	 * Create a folder level at the specified path
	 * 
	 * @param rootPath String
	 * @param sess DiskSession
	 * @param curLevel int
	 * @param ioBuf byte[]
	 * @param pathStack ArrayList<String>
	 */
	private void createFolderLevel( String rootPath, DiskSession sess, int curLevel, byte[] ioBuf, ArrayList<String> pathStack)
		throws SMBException, IOException {
		
		// Create the folder levels and files
		
		StringBuilder pathStr = new StringBuilder(256);
		
		// Create the folders
		
		for ( int folderIdx = 1; folderIdx <= m_foldersPerLevel; folderIdx++) {
			
			// Create a unique folder name
			
			pathStr.setLength( 0);
			
			pathStr.append( rootPath);
			pathStr.append( LevelFolderName);
			pathStr.append( curLevel);
			pathStr.append( "_");
			pathStr.append( folderIdx);
			
			// Create the folder
			
			String folderName = pathStr.toString();
			
			try {
				sess.CreateDirectory( folderName);
			}
			catch ( SMBException ex) {
				System.out.println("Error cretaing folder " + folderName);
				throw ex;
			}
			
			// Add the folder to the path stack
			
			pathStack.add( folderName);
			
			// Update the folder count
			
			m_totalFolders++;
		}
		
		// Create the files
		
		if ( m_filesPerLevel > 0) {
			
			// Create the test files
			
			for ( int fileIdx = 1; fileIdx <= m_filesPerLevel; fileIdx++) {

				// Create a unique file name

				pathStr.setLength( 0);
				
				pathStr.append( rootPath);
				pathStr.append( LevelFileName);
				pathStr.append( fileIdx);
				pathStr.append( LevelFileExt);

				String fileName = pathStr.toString();
				
				// Create a new file
				
				SMBFile testFile = sess.CreateFile( fileName);
				
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
				
				// Update the file count
				
				m_totalFiles++;
			}
		}
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

		// Delete the test folders/files by working backwards through the path list to
		// delete a layer at a time
		
		if ( threadId == 1) {
			
			// Work backwards up the tree deleting files/folders
			
			StringBuilder pathStr = new StringBuilder(256);
			
			for ( int pathIdx = m_folderPaths.size() - 1; pathIdx > 0; pathIdx--) {
				
				// Get the current path
				
				String curPath = m_folderPaths.get( pathIdx);
				
				// Remove the files from the folder
				
				if ( m_filesPerLevel > 0) {
					
					// Remove the files
					
					for ( int fileIdx = 1; fileIdx < m_filesPerLevel; fileIdx++) {
						
						// Build the file path
						
						pathStr.setLength( 0);
						
						pathStr.append( curPath);
						if ( curPath.endsWith( FileName.DOS_SEPERATOR_STR) == false)
							pathStr.append( FileName.DOS_SEPERATOR_STR);
						pathStr.append( LevelFileName);
						pathStr.append( fileIdx);
						pathStr.append( LevelFileExt);

						// Delete the file
						
						sess.DeleteFile( pathStr.toString());
					}
				}
				
				// Delete the folder
				
				if ( curPath.endsWith( FileName.DOS_SEPERATOR_STR))
					curPath = curPath.substring( 0, curPath.length() - 1);
				
				sess.DeleteDirectory( curPath);
			}
		}
	}
	
	
}
