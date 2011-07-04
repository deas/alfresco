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
import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import org.alfresco.jlan.client.DiskSession;
import org.alfresco.jlan.client.SessionFactory;
import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.smb.PCShare;

/**
 * Cluster Test Application
 *
 * @author gkspencer
 */
public class ClusterTest {

	// Constants
	//
	//  Barrier wait time
	
	private static long BarrierWaitTimeout	= 60L;	// seconds
	
	// Test configuration
	
	private TestConfiguration m_config;
	
	// Test threads and synchronization object
	
	private TestThread[] m_testThreads;
	private CyclicBarrier m_startBarrier;
	private CyclicBarrier m_stopBarrier;
	
	// Test thread sets bit when test completed
	
	private BitSet m_testDone;
	
	// Test results
	
	private TestResult[] m_results;
	
	// Test result to indicate a test thread did not complete
	
	private TestResult m_didNotFinishResult = new BooleanTestResult( false, "Test thread did not complete");
	
	/**
	 * Test Thread Inner Class
	 */
	class TestThread extends Thread {
		
		// Thread name and id
		
		private String m_name;
		private int m_id;
		
		// Test iteration
		
		private int m_iter;
		
		// Server details
		
		private TestServer m_server;
		
		// Test to run
		
		private Test m_test;
		
		// Thread is waiting on sync object
		
		private boolean m_wait;
		
		// Thread has completed test iterations
		
		private boolean m_complete;
		
		/**
		 * Class constructor
		 * 
		 * @param server TestServer
		 * @param test Test
		 * @param id int
		 * @param iter int
		 */
		public TestThread( TestServer server, Test test, int id, int iter) {
			m_server = server;
			m_test   = test;
			
			// Set the thread name
			
			m_name = m_test.getName() + "_" + id;
			m_id = id;
			
			m_iter = iter;
		}
		
		/**
		 * Run the test
		 */
		public void run() {
			
			// Set the thread name
			
			Thread.currentThread().setName( m_name);
			if ( m_test.isVerbose())
				Debug.println( m_name + " running, using server " + m_server.getName());
			
			// Indicate test running
			
			m_complete = false;
			
			// Connect to the remote server
			
			PCShare share = null;
			DiskSession sess = null;
			boolean initOK = false;
			
			try {
				
				// Connect to the remote server
				
				share = new PCShare( m_server.getName(), m_server.getShareName(), m_server.getUserName(), m_server.getPassword());
				sess = SessionFactory.OpenDisk( share);
				
				// Set the working directory
				
				if ( m_test.getPath() != null) {
					
					// Primary thread sets up the test folder
					
					if ( isPrimaryThread()) {
				
						// Check if the remote path exists
						
						if ( sess.FileExists( m_test.getPath()) == false) {
							
							// Create the test folder
							
							sess.CreateDirectory( m_test.getPath());
						}
					}
					
					// Set the working directory
					
					sess.setWorkingDirectory( m_test.getPath());
				}
				
				// Wait for all threads
				
				waitAtStartBarrier();
				
				// Initialize the test
				
				initOK = m_test.initTest( m_id, m_iter, sess);
				if ( initOK == false)
					Debug.println("Failed to initialize test " + m_test.getName());
				
				// Set the test result to 'not finished'
				
				m_results[ m_id - 1] = m_didNotFinishResult;
				
				// Wait for all threads
				
				waitAtStopBarrier();
			}
			catch ( Exception ex) {
				Debug.println("Error server=" + m_server);
				Debug.println(ex);
			}
			finally {
				
				// Check if initialization was successful
				
				if ( initOK == false) {
					
					// Close the session
				
					if ( sess != null) {
						try {
							sess.CloseSession();
							sess = null;
						}
						catch (Exception ex) {
						}
					}
				}
			}

			// Run the test if connected successfully
			
			if ( sess != null) {
				
				// Loop through the test iterations
				
				int iteration = 1;
				 
				while ( iteration <= m_iter) {
					
					// Perform per run setup
					
					if ( m_test.runInit( m_id, iteration, sess) == false)
						Debug.println( "Run initialization failed, id=" + m_id + ", iter=" + iteration);
					
					// Create the per test thread output
					
					StringWriter testLog = new StringWriter ( 512);
					
					// Wait on synchronization object
					
					waitAtStartBarrier();
					
					try {
						
						// Start of test setup
						
						if ( m_id == 1)
							Debug.println("------- Start iteration " + iteration + " for " + m_test.getName() + " --- " + new Date() + " -----");
						
						// Run the test
						
						TestResult result = m_test.runTest( m_id, iteration, sess, testLog);
						
						// Save the test results
						
						m_results [ m_id - 1] = result;
					}
					catch (Exception ex) {
						Debug.println(ex);
					}

					// Wait for all threads to complete the test
					
					waitAtStopBarrier();
					
					// Run test cleanup
					
					if ( m_test.hasTestCleanup()) {

						// Wait for all threads to reach this point
						
						waitAtStartBarrier();
						
						try {
							m_test.cleanupTest( m_id, iteration, sess, testLog);
						}
						catch (Exception ex) {
							Debug.println( getName() + " Exception during cleanup");
							Debug.println( ex);
						}

						// Wait for all threads to finish cleanup

						waitAtStopBarrier();
					}

					// Dump the test log
					
					if ( testLog.getBuffer().length() > 0)
						Debug.println( testLog.toString());
						
					// Check the test results
					
					if ( m_id == 1) {
						
						List<TestResult> resultsList = Arrays.asList( m_results);
						TestResult finalResult = m_test.processTestResults( resultsList);
						
						if ( finalResult.isSuccess() == false) {
							Debug.println("Final test result: " + finalResult);
							Debug.println("Test results:");

							for ( TestResult result : resultsList)
								Debug.println( "" + result);
							Debug.println("");
						}							
					
						Debug.println("------- End iteration " + iteration + " for " + m_test.getName() + "   --- " + new Date() + " -----");
					}
					
					// Update the iteration count
					
					iteration++;
				}
				
				// Close the session
				
				try {
					sess.CloseSession();
				}
				catch (Exception ex) {
					Debug.println(ex);
				}
			}
			// Indicate that the test is complete
			
			m_complete = true;
		}

		/**
		 * Check if the test thread has completed
		 * 
		 * @return boolean
		 */
		public final boolean isComplete() {
			return m_complete;
		}
		
		/**
		 * Check if the thread is waiting on the synchronization object
		 * 
		 * @return boolean
		 */
		public boolean isWaiting() {
			return m_wait;
		}
		
		/**
		 * Check if this is the primary thread
		 * 
		 * @return boolean
		 */
		public boolean isPrimaryThread() {
			return m_id == 1 ? true : false;
		}
		
		/**
		 * Wait for all test threads
		 */
		protected void waitAtStartBarrier() {
			
			try {
				// Primary thread resets the stop barrier
				
				if ( m_id == 1)
					m_stopBarrier.reset();
				
				m_wait = true;
				m_startBarrier.await( BarrierWaitTimeout, TimeUnit.SECONDS);
			}
			catch ( Exception ex) {
				ex.printStackTrace();
			}
	
			// Clear the wait flag
			
			m_wait = false;
		}		

		/**
		 * Wait for all test threads
		 */
		protected void waitAtStopBarrier() {
			
			try {
				// Primary thread resets the start barrier
				
				if ( m_id == 1)
					m_startBarrier.reset();
				
				m_wait = true;
				m_stopBarrier.await( BarrierWaitTimeout, TimeUnit.SECONDS);
			}
			catch ( Exception ex) {
				ex.printStackTrace();
			}
	
			// Clear the wait flag
			
			m_wait = false;
		}		
	};
	
	/**
	 * Class constructor
	 * 
	 * @param args String[]
	 */
	public ClusterTest( String[] args)
		throws Exception {
		
		// Load the test configuration
		
		m_config = new TestConfiguration();
		m_config.loadConfiguration( args[0]);
	}
	
	/**
	 * Run the tests
	 */
	public void runTests() {
		
		// Setup the JCE provider, required by the JLAN Client code
		
		try {
			Provider provider = (Provider) Class.forName( "cryptix.jce.provider.CryptixCrypto").newInstance();
			Security.addProvider(provider);
		}
		catch ( Exception ex) {
			Debug.println(ex);
		}
		
		// Global JLAN Client setup
		
		SessionFactory.setSMBSigningEnabled( false);
		
		// Startup information
		
		Debug.println("----- Cluster Tests Running --- " + new Date() + " -----");
		Debug.println("Run tests: " + (m_config.runInterleaved() ? "Interleaved" : "Sequentially"));
		Debug.println("Threads per server: " + m_config.getThreadsPerServer());
		Debug.println("");
		
		Debug.println("Servers configured:");
		
		for ( TestServer testSrv : m_config.getServerList()) {
			Debug.print("  ");
			Debug.print(testSrv.getName());
		}
		Debug.println("");
		Debug.println("");
		
		Debug.println("Tests configured:");
		
		for ( Test test : m_config.getTestList()) {
			Debug.print("  ");
			Debug.println( test.toString());
		}
		
		Debug.println("");
		
		// Calculate the number of test threads
		
		int numTestThreads = m_config.getServerList().size() * m_config.getThreadsPerServer();
		
		// Setup the thread synchronization object
		
		m_startBarrier = new CyclicBarrier( numTestThreads);
		m_stopBarrier  = new CyclicBarrier( numTestThreads);
		
		// Create the test thread completion bit set
		
		m_testDone = new BitSet( numTestThreads);
		
		// Create the per iteration test result list
		
		m_results = new TestResult[ numTestThreads];
		
		// Loop through the tests
		
		for ( Test curTest : m_config.getTestList()) {
			
			// Start of test setup
			
			Debug.println("----- Start test " + curTest.getName() + " --- " + new Date() + " -----");
			
			// Setup the test threads
			
			m_testThreads = new TestThread[ numTestThreads];
			
			int idx = 0;
			
			for ( TestServer curSrv : m_config.getServerList()) {
				
				// Create test thread(s) for the current test

				for ( int perSrv = 0; perSrv < m_config.getThreadsPerServer(); perSrv++) {

					// Create a test thread
					
					TestThread testThread = new TestThread( curSrv, curTest, idx+1, curTest.getIterations());
					m_testThreads[idx] = testThread;
					idx++;
					
					// Start the thread
					
					testThread.setDaemon( true);
					testThread.start();
				}
			}

			// Wait for tests to run
			
			int waitThread = m_testThreads.length;
			
			while ( waitThread > 0) {

				try {
					Thread.sleep( 100L);
				}
				catch ( Exception ex) {
				}

				// Check if the test threads have completed
			
				waitThread = 0;
				
				for ( TestThread testThread : m_testThreads) {
					if ( testThread.isComplete() == false)
						waitThread++;
				}
			}
			
			// Clear down the test threads

			for ( int i = 0; i < m_testThreads.length; i++) {
				
				// Stop the current thread, if still alive
				
				TestThread curThread = m_testThreads[i];
				
				if ( curThread.isAlive())
					curThread.interrupt();
			}
			
			m_testThreads = null;
			
			// Run the garbage collector
			
			System.gc();

			// End of current test
			
			Debug.println("----- End test " + curTest.getName() + " --- " + new Date() + " -----");
		}

		// End of all tests
		
		Debug.println("-- End all tests --- " + new Date() + " --");
	}
	
	/**
	 * Application startup
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		// Check there are enough command line parameters
		
		if ( args.length == 0) {
			System.out.println("Usage: <testConfig XML file>");
			System.exit( 1);
		}
		
		try {
			
			// Create the cluster tests
			
			ClusterTest clusterTest = new ClusterTest( args);
			clusterTest.runTests();
		}
		catch ( Exception ex) {
			Debug.println(ex);
		}
	}
}
