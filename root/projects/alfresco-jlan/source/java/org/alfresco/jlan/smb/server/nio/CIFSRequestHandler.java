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

package org.alfresco.jlan.smb.server.nio;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Vector;

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.server.SrvSessionQueue;
import org.alfresco.jlan.server.thread.ThreadRequest;
import org.alfresco.jlan.server.thread.ThreadRequestPool;
import org.alfresco.jlan.smb.server.SMBSrvSession;
import org.alfresco.jlan.smb.server.SMBSrvSessionState;

/**
 * CIFS Request Handler Class
 * 
 * <p>Handles the receiving of CIFS requests for a number of CIFS sessions.
 * 
 * @author gkspencer
 */
public class CIFSRequestHandler extends RequestHandler implements Runnable {

	// Request handler index, used to generate the thread name
	
	private static int _handlerId;
	
	// Selector used to monitor a group of socket channels for incoming requests
	
	private Selector m_selector;
	
    // Lock to regulate access to m_sessionCount
	private Object m_sessionCountLock = new Object();
	
    // Count of the number of selector channels, maintained by the main thread
	private int m_sessionCount;
	
	// Thread that the request handler runs in
	
	private Thread m_thread;
	
	// Thread pool for processing requests
	
	private ThreadRequestPool m_threadPool;
	
	// Queue of sessions that are pending setup with the selector
	
	private SrvSessionQueue m_sessQueue;
	
	// Client socket session timeout
	
	private int m_clientSocketTimeout;
	
	// Shutdown request flag
	
	private boolean m_shutdown;
	
	/**
	 * Class constructor
	 * 
	 * @param threadPool ThreadRequestPool
	 * @param maxSess int
	 * @param sockTmo int
	 * @param debug boolean
	 */
	public CIFSRequestHandler( ThreadRequestPool threadPool, int maxSess, int sockTmo, boolean debug) {
		super( maxSess);
		
		// Set the thread pool to use for request processing
		
		m_threadPool = threadPool;

		// Set the client socket timeout
		
		m_clientSocketTimeout = sockTmo;
		
		// Create the session queue
		
		m_sessQueue = new SrvSessionQueue();
		
		// Set the debug output enable
		
		setDebug( debug);
		
		// Start the request handler in a seperate thread
		
		m_thread = new Thread( this);
		m_thread.setName( "CIFSRequestHandler_" + ++_handlerId);
		m_thread.setDaemon( false);
		
		m_thread.start();
	}
	
	/**
	 * Return the current session count
	 * 
	 * @return int
	 */
	public final int getCurrentSessionCount() {

	    // Wake up the selection thread, causing it to update its count
	    if ( m_selector != null) {
	        m_selector.wakeup();
	    }
		synchronized (m_sessionCountLock) {
		    return m_sessionCount;
		}
	}
	
	/**
	 * Check if this request handler has free session slots available
	 * 
	 * @return boolean
	 */
	public final boolean hasFreeSessionSlot() {
		return ( getCurrentSessionCount() + m_sessQueue.numberOfSessions()) < getMaximumSessionCount() ? true : false;
	}

	/**
	 * Return the client socket timeout, in milliseconds
	 * 
	 * @return int
	 */
	public final int getSocketTimeout() {
		return m_clientSocketTimeout;
	}
	
	/**
	 * Set the client socket timeout, in milliseconds
	 * 
	 * @param tmo int
	 */
	public final void setSocketTimeout(int tmo) {
		m_clientSocketTimeout = tmo;
	}
	
	/**
	 * Queue a new session to the request handler, wakeup the request handler thread to register it with the
	 * selector.
	 * 
	 * @param sess SMBSrvSession
	 */
	public final void queueSessionToHandler( SMBSrvSession sess) {
	
		// Add the new session to the pending queue
		
		m_sessQueue.addSession( sess);
		
		// Wakeup the main thread to process the new session queue
		
		if ( m_selector != null)
			m_selector.wakeup();
	}
	
	/**
	 * Return the request handler name
	 * 
	 * @return String
	 */
	public final String getName() {
		if ( m_thread != null)
			return m_thread.getName();
		return "CIFSRequestHandler";
	}
	
	/**
	 * Enable/disable thread pool debugging
	 * 
	 * @param dbg boolean
	 */
	public final void setThreadDebug( boolean dbg) {
		m_threadPool.setDebug( dbg);
	}
	
	/**
	 * Run the main processing in a seperate thread
	 */
	public void run() {

		// Clear the shutdown flag, may have been restarted
		
		m_shutdown = false;
		
		// Initialize the socket selector
		
		try {
			
			// Create the selector
			
			m_selector = Selector.open();
		}
		catch ( IOException ex) {
			
			// DEBUG
			
			if ( Debug.EnableInfo && hasDebug()) {
				Debug.println( "[SMB] Error opening/registering Selector");
				Debug.println( ex);
			}
			
			m_shutdown = true;
		}
		
		// Loop until shutdown
		
		Vector<ThreadRequest> reqList = new Vector<ThreadRequest>();
		
		while ( m_shutdown == false) {
			
			// Check if there are any sessions registered

			int sessCnt = 0;

			synchronized (m_sessionCountLock) {
			    m_sessionCount = m_selector.keys().size();
			}
			
			if ( m_sessionCount == 0) {

				// Indicate that this request handler has no active sessions
				
				fireRequestHandlerEmptyEvent();
				
				// DEBUG
				
				if ( Debug.EnableInfo && hasDebug())
					Debug.println( "[SMB] Request handler " + m_thread.getName() + " waiting for session ...");
				
				// Wait for a session to be added to the handler
				
				try {
					m_sessQueue.waitWhileEmpty();
				}
				catch ( InterruptedException ex) {
				}
			}
			else {
				
				// Wait for client requests
				
				try {
				    sessCnt = m_selector.select();
					synchronized (m_sessionCountLock) {
					    m_sessionCount = m_selector.keys().size();
					}
				}
				catch ( CancelledKeyException ex) {
					
					// DEBUG
					
					if ( Debug.EnableError && hasDebug() && m_shutdown == false) {
						Debug.println( "[SMB] Request handler error waiting for events");
						Debug.println(ex);
					}
				}
				catch ( IOException ex) {
					
					// DEBUG
					
					if ( Debug.EnableError && hasDebug()) {
						Debug.println( "[SMB] Request handler error waiting for events");
						Debug.println(ex);
					}
				}
			}
			
			// Check if the shutdown flag has been set
			
			if ( m_shutdown == true)
				continue;
			
			// Check if there are any events to process
			
			if ( sessCnt > 0) {
			
				// DEBUG
				
//				if ( Debug.EnableInfo && hasDebug()) // && sessCnt > 1)
//					Debug.println( "[SMB] Request handler " + m_thread.getName() + " session events, sessCnt=" + sessCnt + "/" + m_selector.keys().size());

				// Clear the thread request list
				
				reqList.clear();
				
				// Iterate the selected keys
				
				Iterator<SelectionKey> keysIter = m_selector.selectedKeys().iterator();
				long timeNow = System.currentTimeMillis();
				
				while ( keysIter.hasNext()) {
					
					// Get the current selection key and check if has an incoming request
					
					SelectionKey selKey = keysIter.next();
					keysIter.remove();
					
					if ( selKey.isValid() == false) {
						
						// Remove the selection key
						
						Debug.println("CIFSRequestHandler: Cancelling selection key - " + selKey);
						selKey.cancel();
						
						// DEBUG
						
						if ( Debug.EnableInfo && hasDebug())
							Debug.println( "[SMB] NIO Selection key not valid, sess=" + selKey.attachment());
					}
					else if ( selKey.isReadable()) {
						
						// DEBUG
						
//						if ( Debug.EnableInfo && hasDebug())
//							Debug.println("[SMB] Socket read event");
						
						// Switch off read events for this channel until the current processing is complete
						
						selKey.interestOps( selKey.interestOps() & ~SelectionKey.OP_READ);
						
						// Get the associated session and queue a request to the thread pool to read and process the CIFS request
						
						SMBSrvSession sess = (SMBSrvSession) selKey.attachment();
						reqList.add(  new NIOCIFSThreadRequest( sess, selKey));

						// Update the last I/O time for the session
						
						sess.setLastIOTime( timeNow);
						
						// Check if there are enough thread requests to be queued
						
						if ( reqList.size() >= 5) {
							
							// DEBUG
							
//							if ( Debug.EnableInfo && hasDebug())
//								Debug.println( "[SMB] Queueing " + reqList.size() + " thread requests");
							
							// Queue the requests to the thread pool
							
							m_threadPool.queueRequests( reqList);
							reqList.clear();
						}
					}
					else if ( selKey.isValid() == false) {
						
						// Remove the selection key
						
						Debug.println("CIFSRequestHandler: Cancelling selection key - " + selKey);
						selKey.cancel();
						
						// DEBUG
						
						if ( Debug.EnableInfo && hasDebug())
							Debug.println( "[SMB] NIO Selection key not valid, sess=" + selKey.attachment());
					}
					else {
						
						// DEBUG
						
						if ( Debug.EnableInfo && hasDebug())
							Debug.println("[SMB] Unprocessed selection key, " + selKey);
					}
				}
				
				// Queue the thread requests
				
				if ( reqList.size() > 0) {
					
					// DEBUG
					
//					if ( Debug.EnableInfo && hasDebug()) // && reqList.size() > 1)
//						Debug.println( "[SMB] Queueing " + reqList.size() + " thread requests (last)");
					
					// Queue the requests to the thread pool
					
					m_threadPool.queueRequests( reqList);
					reqList.clear();
				}
			}
			
			// Check if there are any new sessions that need to be registered with the selector, or sessions to be removed
			
			if ( m_sessQueue.numberOfSessions() > 0) {
				
				// Register the new sessions with the selector
				
				while ( m_sessQueue.numberOfSessions() > 0) {
					
					// Get a new session from the queue
					
					SMBSrvSession sess = (SMBSrvSession) m_sessQueue.removeSessionNoWait();
					
					if ( sess != null) {

						// check the session state, if the session is in a setup state it is a new session
						
						if ( sess.getState() <= SMBSrvSessionState.SMBNEGOTIATE) {
							
							// DEBUG
							
							if ( Debug.EnableError && hasDebug())
								Debug.println( "[SMB] Register session with request handler, handler=" + m_thread.getName() + ", sess=" + sess.getUniqueId());
							
							// Get the socket channel from the sessions packet handler
							
							if ( sess.getPacketHandler() instanceof ChannelPacketHandler) {
								
								// Get the channel packet handler and register the socket channel with the selector
								
								ChannelPacketHandler chanPktHandler = (ChannelPacketHandler) sess.getPacketHandler();
								SocketChannel sessChannel = chanPktHandler.getSocketChannel();
								
								try {
									
									// Register the session channel with the selector
									
									sessChannel.configureBlocking( false);
									sessChannel.register( m_selector, SelectionKey.OP_READ, sess);
									
									// Update the last I/O time for the session
									
									sess.setLastIOTime( System.currentTimeMillis());
								}
								catch ( ClosedChannelException ex) {
									
									// DEBUG
									
									if ( Debug.EnableError && hasDebug())
										Debug.println( "[SMB] Failed to register session channel, closed channel");
								}
								catch ( IOException ex) {
									
									// DEBUG
									
									if ( Debug.EnableError && hasDebug())
										Debug.println( "[SMB] Failed to set channel blocking mode, " + ex.getMessage());
								}
							}
						}
						else {
							
							// Remove the session
							
							// TODO:
						}
					}
				}
			}
			
			// Check if the idle session reaper should be run to remove stale sessions
						
			if ( m_sessionCount > 0) {
				
				// Run the idle session reaper
				
				int remCnt = runIdleSessionsReaper();
				
				// DEBUG
				
				if ( remCnt > 0 && Debug.EnableError && hasDebug())
					Debug.println( "[SMB] Idle session reaper removed " + remCnt + " sessions");
			}
		}
		
		// Close all sessions
		
		if ( m_selector != null) {
			
			// Enumerate the selector keys to get the session list
			
			Iterator<SelectionKey> selKeys = m_selector.keys().iterator();
			
			while ( selKeys.hasNext()) {
				
				// Get the current session via the selection key
				
				SelectionKey curKey = selKeys.next();
				SMBSrvSession sess = (SMBSrvSession) curKey.attachment();
				
				// Close the session
				
				sess.closeSession();
			}
			
			// Close the selector
			
			try {
				m_selector.close();
			}
			catch ( IOException ex) {
			}
		}

		// DEBUG
		
		if ( Debug.EnableInfo && hasDebug())
			Debug.println( "[SMB] Closed CIFS request handler, " + m_thread.getName());
	}
	
	/**
	 * Close the request handler
	 */
	public final void closeHandler() {
		
		// Check if the thread is running
		
		if ( m_thread != null) {
			m_shutdown = true;
			try {
				m_thread.interrupt();
				
				if ( m_selector != null)
					m_selector.wakeup();
			}
			catch (Exception ex) {
			}
		}
	}
	
	/**
	 * Check for idle sessions
	 * 
	 * @return int
	 */
	protected final int checkForIdleSessions() {
		
		// Set the run idle session reaper flag and wake up the main selector thread
		
		getCurrentSessionCount();
		
		// Indicate no sessions closed, not run yet
		
		return 0;
	}
	
	/**
	 * Run the idle session check
	 * 
	 * @return int
	 */
	protected final int runIdleSessionsReaper() {
		
		// Check if the request handler has any active sessions

        // Run from the main thread, so no need to synchronize on keys		
		int idleCnt = 0;
		
		if ( m_selector != null && m_selector.keys().size() > 0) {
    
			// Time to check
    			
			long checkTime = System.currentTimeMillis() - (long) m_clientSocketTimeout;
    			
			// Enumerate the selector keys to get the session list
    			
			Iterator<SelectionKey> selKeys = m_selector.keys().iterator();
    			
			while ( selKeys.hasNext()) {
    				
				// Get the current session via the selection key
    				
				SelectionKey curKey = selKeys.next();
				SMBSrvSession sess = (SMBSrvSession) curKey.attachment();
    				
				// Check the time of the last I/O request on this session
    				
				if ( sess != null && sess.getLastIOTime() < checkTime) {
    					
					// DEBUG
    					
					if ( Debug.EnableInfo && hasDebug())
						Debug.println( "[SMB] Closing idle session, " + sess.getUniqueId() + ", addr=" + sess.getRemoteAddress().getHostAddress());
    					
					// Close the session
    					
					sess.closeSession();
					sess.processPacket( null);
    					
					// Update the idle session count
    					
					idleCnt++;
				}
			}
		
    		// If any sessions were closed then wakeup the selector thread
    		
    		if ( idleCnt > 0)
    			m_selector.wakeup();
		}
		
		// Return the count of idle sessions that were closed
		
		return idleCnt;
	}
}
