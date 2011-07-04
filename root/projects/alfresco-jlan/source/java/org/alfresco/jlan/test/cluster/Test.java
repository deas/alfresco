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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.alfresco.jlan.client.DiskSession;
import org.alfresco.jlan.server.config.InvalidConfigurationException;
import org.alfresco.jlan.server.filesys.FileName;
import org.springframework.extensions.config.ConfigElement;

/**
 * Test Base Class
 *
 * @author gkspencer
 */
public abstract class Test {

	// Date/time formatter
	
	private static SimpleDateFormat m_dateFormat = new SimpleDateFormat( "HH:mm:ss.S");
	
	// Test name
	
	private String m_name;
	
	// Number of times to run the test
	
	private int m_iterations;
	
	// Output additional logging
	
	private boolean m_verbose;
	
	// Flag to control test cleanup
	
	private boolean m_cleanup = true;
	
	// Test path
	
	private String m_path;
	
	/**
	 * class constructor
	 * 
	 * @param name String
	 */
	public Test( String name) {
		m_name = name;
	}
	
	/**
	 * Return the test name
	 * 
	 * @return String
	 */
	public final String getName() {
		return m_name;
	}
	
	/**
	 * Return the test relative path
	 * 
	 * @return String
	 */
	public final String getPath() {
		return m_path;
	}
	
	/**
	 * Return the test iteration count
	 * 
	 * @return int
	 */
	public final int getIterations() {
		return m_iterations;
	}
	
	/**
	 * Check if additional logging should be output
	 * 
	 * @return boolean
	 */
	public final boolean isVerbose() {
		return m_verbose;
	}
	
	/**
	 * Check if the test cleanup is disabled
	 * 
	 * @return boolean
	 */
	public final boolean hasTestCleanup() {
		return m_cleanup;
	}
	
	/**
	 * Set the cleanup flag for the test
	 * 
	 * @param cleanup boolean
	 */
	public final void setTestCleanup( boolean cleanup) {
		m_cleanup = cleanup;
	}
	
	/**
	 * Set the test path
	 * 
	 * @param path String
	 */
	public final void setPath( String path) {
		m_path = path;

		if ( m_path != null) {
			if ( m_path.startsWith( FileName.DOS_SEPERATOR_STR) == false)
				m_path = FileName.DOS_SEPERATOR_STR + m_path;
			if ( m_path.endsWith( FileName.DOS_SEPERATOR_STR) == false)
				m_path = m_path + FileName.DOS_SEPERATOR_STR;
		}
	}

	/**
	 * Set the 
	 */
	public final void setIterations( int iter) {
		m_iterations = iter;
	}
	
	/**
	 * Set the test for verbose output
	 * 
	 * @param verbose boolean
	 */
	public final void setVerbose( boolean verbose) {
		m_verbose = verbose;
	}
	
	/**
	 * Generate a test file name that is unique per test
	 * 
	 * @param threadId int
	 * @param iter int
	 * @return String 
	 */
	public final String getPerTestFileName( int threadId, int iter) {
		
		StringBuilder fName = new StringBuilder();
		
		fName.append( getName());
		fName.append( "_");
		fName.append( iter);
		fName.append( ".txt");
		
		return fName.toString();
	}
	
	/**
	 * Generate a test file name that is unique per thread
	 * 
	 * @param threadId int
	 * @param iter int
	 * @return String 
	 */
	public final String getPerThreadFileName( int threadId, int iter) {
		
		StringBuilder fName = new StringBuilder();
		
		fName.append( getName());
		fName.append( "_");
		fName.append( threadId);
		fName.append( "_");
		fName.append( iter);
		fName.append( ".txt");
		
		return fName.toString();
	}
	
	/**
	 * Generate a test folder name that is unique per test
	 * 
	 * @param threadId int
	 * @param iter int
	 * @return String 
	 */
	public final String getPerTestFolderName( int threadId, int iter) {
		
		StringBuilder fName = new StringBuilder();
		
		fName.append( getName());
		fName.append( "_");
		fName.append( iter);
		
		return fName.toString();
	}
	
	/**
	 * Generate a test folder name that is unique per thread
	 * 
	 * @param threadId int
	 * @param iter int
	 * @return String 
	 */
	public final String getPerThreadFolderName( int threadId, int iter) {
		
		StringBuilder fName = new StringBuilder();
		
		fName.append( getName());
		fName.append( "_");
		fName.append( threadId);
		fName.append( "_");
		fName.append( iter);
		
		return fName.toString();
	}
	
	/**
	 * Generate a unique test file name
	 * 
	 * @param threadId int
	 * @param iter int
	 * @param sess DiskSession
	 * @return String 
	 */
	public final String getUniqueFileName( int threadId, int iter, DiskSession sess) {
		
		StringBuilder fName = new StringBuilder();
		
		fName.append( getName());
		fName.append( "_");
		fName.append( threadId);
		fName.append( "_");
		fName.append( iter);
		fName.append( "_");
		fName.append( sess.getServer());
		fName.append( ".txt");
		
		return fName.toString();
	}
	
	/**
	 * Generate a unique test folder name
	 *  
	 * @param threadId int
	 * @param iter int
	 * @param sess DiskSession
	 * @return String 
	 */
	public final String getUniqueFolderName( int threadId, int iter, DiskSession sess) {
		
		StringBuilder fName = new StringBuilder();
		
		fName.append( getName());
		fName.append( "_");
		fName.append( threadId);
		fName.append( "_");
		fName.append( iter);
		fName.append( "_");
		fName.append( sess.getServer());
		
		return fName.toString();
	}
	
	/**
	 * Test specific configuration
	 * 
	 * @param config ConfigElement
	 */
	public void configTest( ConfigElement config)
		throws InvalidConfigurationException {
	}
	
	/**
	 * Initialize the test setup
	 * 
	 * @param threadId int
	 * @param iter int
	 * @param sess DiskSession
	 * @return boolean
	 */
	public boolean initTest( int threadId, int iter, DiskSession sess) {
		return true;
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
		return true;
	}
	
	/**
	 * Run the test
	 * 
	 * @param threadId int
	 * @param iter int
	 * @param sess DiskSession
	 * @param log StringWriter
	 * @return TestResult
	 */
	public abstract TestResult runTest( int threadId, int iter, DiskSession sess, StringWriter log);
	
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
	}
	
	/**
	 * Process a set of results from a run of the test.
	 * 
	 * Default implementation counts failure status results, or missing results.
	 * 
	 * @param testResults List<TestResult>
	 * @return TestResult
	 */
	public TestResult processTestResults( List<TestResult> testResults) {
		
		// Check for all success status
		
		int failCnt = 0;
		
		for ( TestResult result : testResults) {
			
			// Check for a boolean result
			
			if ( result == null || result.isSuccess() == false) {
				
				// Bad status, update the fail count
				
				failCnt++;
			}
		}
		
		// Check if there were any failed results
		
		TestResult finalResult = null;
		
		if ( failCnt == 0)
			finalResult = new BooleanTestResult( true);
		else {
			finalResult = new BooleanTestResult( false);
			finalResult.setComment( "" + failCnt + "/" + testResults.size() + " failures");
		}
		
		// Return the final test result
		
		return finalResult;
	}
	
	/**
	 * Return the prefix string for this test thread
	 * 
	 * @return String
	 */
	protected String getPrefix() {
		return m_dateFormat.format(new Date()) + " [" + Thread.currentThread().getName() + "] ";
	}

	/**
	 * Test logging
	 * 
	 * @param log StringWriter
	 * @param str String
	 */
	public void testLog( StringWriter log, String str) {
		if ( isVerbose()) {
			log.append( getPrefix());
			log.append( str);
			log.append( "\n");
		}
	}
	
	/**
	 * Test logging
	 * 
	 * @param log StringWriter
	 * @param ex Exception
	 */
	public void testLog( StringWriter log, Exception ex) {
		log.append ( getPrefix());
		log.append ( ex.getMessage());
		log.append ( "\n");
		ex.printStackTrace(new PrintWriter(log, true));
		log.append ( "\n");
	}
	
	/**
	 * Sleep for a while
	 * 
	 * @param sleepMs long
	 */
	protected final void testSleep( long sleepMs) {
		try {
			Thread.sleep( sleepMs);
		}
		catch ( InterruptedException ex) {
		}
	}
	
	/**
	 * Return the test details as a string
	 * 
	 * @return String
	 */
	public String toString() {
		StringBuilder str = new StringBuilder();
		
		str.append("[");
		str.append(getName());
		str.append(",");
		
		if ( getPath() == null)
			str.append( FileName.DOS_SEPERATOR_STR);
		else
			str.append(getPath());
		str.append(",");
		str.append(getIterations());
		str.append(",");
		str.append(isVerbose() ? "Verbose" : "Quiet");
		
		if ( hasTestCleanup() == false)
			str.append(",NoCleanup");
		str.append("]");
		
		return str.toString();
	}
}
