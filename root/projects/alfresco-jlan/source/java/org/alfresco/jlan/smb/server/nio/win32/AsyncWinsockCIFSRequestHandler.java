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

package org.alfresco.jlan.smb.server.nio.win32;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.netbios.NetBIOSName;
import org.alfresco.jlan.netbios.win32.NetBIOSSocket;
import org.alfresco.jlan.netbios.win32.Win32NetBIOS;
import org.alfresco.jlan.netbios.win32.Winsock2;
import org.alfresco.jlan.netbios.win32.WinsockNetBIOSException;
import org.alfresco.jlan.server.SrvSessionQueue;
import org.alfresco.jlan.server.thread.ThreadRequestPool;
import org.alfresco.jlan.smb.server.SMBSrvSession;
import org.alfresco.jlan.smb.server.nio.AsynchronousWritesHandler;
import org.alfresco.jlan.smb.server.nio.RequestHandler;
import org.alfresco.jlan.smb.server.win32.WinsockNetBIOSPacketHandler;

/**
 * CIFS Request Handler Class
 * 
 * <p>Handles the receiving of CIFS requests for a number of CIFS sessions.
 * 
 * @author gkspencer
 */
public class AsyncWinsockCIFSRequestHandler extends RequestHandler implements Runnable {

	// Request handler index, used to generate the thread name
	
	private static int _handlerId;
	
	// NetBIOS name that the clients are connecting to, required to make a loopback connection
	
	private NetBIOSName m_srvName;
	private int m_srvLANA;
	
	// Selector used to monitor a group of socket channels for incoming requests
	
//	private NetBIOSSelector m_nbSelector;
	
	// Thread that the request handler runs in
	
	private Thread m_thread;
	private int m_threadId;
	
	// Thread pool for processing requests
	
	private ThreadRequestPool m_threadPool;
	
	// Queue of sessions that are pending setup with the selector
	
	private SrvSessionQueue m_sessQueue;

	// Client socket timeout
	
	private int m_clientSocketTimeout;
	
	// Socket event to session mapping
	
	private Hashtable<Integer, SMBSrvSession> m_eventTable;
	
	// Socket event arrays
	//
	// The first element in the active array is used to wakeup the main request handler thread.
	
	private int[] m_win32ActiveEvents;
	private int m_activeEventsLen;
	
	private int[] m_requeueEvents;
	private int m_requeueLen;
	
	// shutdown request flag
	
	private boolean m_shutdown;
	
	/**
	 * Class constructor
	 *
	 * @param srvName NetBIOSName
	 * @param srvLANA int
	 * @param threadPool ThreadRequestPool
	 * @param maxSess int
	 * @param sockTmo int
	 */
	public AsyncWinsockCIFSRequestHandler( NetBIOSName srvName, int srvLANA, ThreadRequestPool threadPool, int maxSess, int sockTmo) {
		super( maxSess);
		
		// File server name and LANA
		
		m_srvName = srvName;
		m_srvLANA = srvLANA;
		
		// Set the thread pool to use for request processing
		
		m_threadPool = threadPool;
		
		// Set the client socket timeout
		
		m_clientSocketTimeout = sockTmo;
		
		// Create the session queue
		
		m_sessQueue = new SrvSessionQueue();

		// Allocate the active event array, and requeue array
		
		m_win32ActiveEvents   = new int[maxSess];
		m_requeueEvents       = new int[maxSess];
		
		// Create the event to CIFS session mapping table
		
		m_eventTable = new Hashtable<Integer, SMBSrvSession>( maxSess);
		
		// Unique id for the thread
		
		m_threadId = ++_handlerId;
		
		// Start the request handler in a seperate thread
		
		m_thread = new Thread( this);
		m_thread.setName( "AsyncWinsockRequestHandler_" + m_threadId);
		m_thread.setDaemon( false);
		
		m_thread.start();
	}
	
	/**
	 * Return the current session count
	 * 
	 * @return int
	 */
	public final int getCurrentSessionCount() {
		return m_eventTable.size();
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
		
		wakeupRequestHandler();
	}
	
	/**
	 * Return the request handler name
	 * 
	 * @return String
	 */
	public final String getName() {
		if ( m_thread != null)
			return m_thread.getName();
		return "AsyncWinsockRequestHandler";
	}
	
	/**
	 * Run the main processing in a seperate thread
	 */
	public void run() {

		// Clear the shutdown flag, may have been restarted
		
		m_shutdown = false;
		
		// Create the wakeup event
		
		try {
			
			// Create the event used to wakeup the request handler thread to add/remove sessions
			
			m_win32ActiveEvents[0] = Win32NetBIOS.Win32CreateEvent();
			m_activeEventsLen++;
		}
		catch ( Exception ex) {
			
			// DEBUG
			
			if ( Debug.EnableError && hasDebug()) {
				Debug.println( "[SMB] Failed to initialize wakeup event " + m_thread.getName());
				Debug.println(ex);
			}
				
			// Force the request handler to shut down
			
			m_shutdown = true;
		}
		
		// Loop until shutdown
		
		int eventIdx = -1;
		
		while ( m_shutdown == false) {
			
			// Check if there are any sessions registered

			if ( m_activeEventsLen == 1 && m_eventTable.size() == 0) {

				// Indicate that this request handler has no active sessions
				
				fireRequestHandlerEmptyEvent();
				
				// DEBUG
				
				if ( Debug.EnableInfo && hasDebug())
					Debug.println( "[SMB] Request handler " + m_thread.getName() + " waiting for session ...");
			}				

			// Wait for a wakeup event or socket event
			
			try {
				
				eventIdx = Win32NetBIOS.WinsockWaitForMultipleEvents( m_activeEventsLen, m_win32ActiveEvents, false, Winsock2.WSA_INFINITE, false);
			}
			catch ( WinsockNetBIOSException ex) {
				
				// DEBUG
				
				if ( Debug.EnableDbg && hasDebug()) {
					Debug.println("[SMB] Error waiting for event");
					Debug.println( ex);
				}
				
				// Set the event index, might as well process the session queue
				
				eventIdx = 0;
			}
			
			// Check if the shutdown flag has been set
			
			if ( m_shutdown == true)
				continue;
			
			// Determine if the triggered event is wakeup or socket event
			
			if ( eventIdx == 0) {
			
				// DEBUG
				
//				if ( Debug.EnableInfo && hasDebug())
//					Debug.println( "[SMB] Wakeup event, active=" + m_activeEventsLen);

				// Reset the wakeup event
				
				Win32NetBIOS.Win32ResetEvent( m_win32ActiveEvents[ 0]);
				
				// Requeue socket events
				
				if ( m_requeueLen > 0) {
				
					// DEBUG
					
//					if ( Debug.EnableDbg && hasDebug())
//						Debug.println("[SMB] Requeue events to active list, requeueLen=" + m_requeueLen);
					
					// Requeue socket events to the active event list
					
					synchronized ( m_requeueEvents) {
						
						// Copy the events to the active list
						
						for ( int i = 0; i < m_requeueLen; i++) {
							
							// Get the event id of the event being requeued
							
							Integer eventId = new Integer( m_requeueEvents[ i]);
							SMBSrvSession sess = m_eventTable.get( eventId);
						
							// Get the socket via the sessions packet handler
						
							WinsockNetBIOSPacketHandler winsockPktHandler = (WinsockNetBIOSPacketHandler) sess.getPacketHandler();
							NetBIOSSocket nbSocket = winsockPktHandler.getSocket();
							
							// Reset the events to trigger on

							try {
								
								// Process any pending events for the socket
								
								processSocketEvent( m_requeueEvents [ i]);

								int evtEnum = Win32NetBIOS.WinsockEnumNetworkEvents( nbSocket.getSocket(), 0);
								if ( evtEnum != 0)
									Debug.println("[SMB] Requeue event, evtEnum=0x" + Integer.toHexString( evtEnum));
								
								// Reset the events to be reported for the socket
								
								Win32NetBIOS.WinsockEventSelect( nbSocket.getSocket(), m_requeueEvents[ i], Winsock2.FD_READ | Winsock2.FD_WRITE | Winsock2.FD_CLOSE);
							}
							catch ( Exception ex) {
								Debug.println("[SMB] Error re-enabling FD_READ events sess=" + sess.getUniqueId());
								Debug.println(ex);
							}
						}
						
						// Reset the requeue list
						
						m_requeueLen = 0;
					}
				}
				
				// Register the new sessions
				
				while ( m_sessQueue.numberOfSessions() > 0) {
					
					// Get a new session from the queue
					
					SMBSrvSession sess = (SMBSrvSession) m_sessQueue.removeSessionNoWait();
					
					if ( sess != null) {
						
						// DEBUG
						
						if ( Debug.EnableError && hasDebug())
							Debug.println( "[SMB] Register session with request handler, handler=" + m_thread.getName() + ", sess=" + sess.getUniqueId());
						
						// Get the NetBIOS socket from the sessions packet handler
						
						if ( sess.getPacketHandler() instanceof WinsockNetBIOSPacketHandler) {
							
							// Get the channel packet handler and create a socket event for the new session
							
							WinsockNetBIOSPacketHandler winsockPktHandler = (WinsockNetBIOSPacketHandler) sess.getPacketHandler();
							NetBIOSSocket nbSocket = winsockPktHandler.getSocket();
							
							try {

								// Create a socket event for the new session
								
								int sockEvent = Win32NetBIOS.WinsockCreateEvent();
								
								// Set the socket buffer sizes
								
								Win32NetBIOS.setSocketReceiveBufferSize(nbSocket.getSocket(), 128000);
								Win32NetBIOS.setSocketSendBufferSize(nbSocket.getSocket(), 256000);
								
								// Enable the required socket events for the session socket
								
								nbSocket.configureBlocking( false);
								Win32NetBIOS.WinsockEventSelect( nbSocket.getSocket(), sockEvent, Winsock2.FD_READ | Winsock2.FD_WRITE | Winsock2.FD_CLOSE);
								
								// Add the event/session mapping
								
								m_eventTable.put( new Integer( sockEvent), sess);
								
								// Add the socket event to the active event list
								
								m_win32ActiveEvents[ m_activeEventsLen++] = sockEvent;
							}
							catch ( IOException ex) {
								
								// DEBUG
								
								if ( Debug.EnableError && hasDebug()) {
									Debug.println( "[SMB] Failed to initialize socket event for new session");
									Debug.println( ex);
								}
							}
						}
					}
				}
			}
			else {

				// Process the socket event
				
				processSocketEvent( m_win32ActiveEvents[ eventIdx]);
			}
		}
			
		// Close all sessions and events
		
		if ( m_eventTable.size() > 0) {
			
			// Enumerate the event ids
			
			Enumeration<Integer> eventIds = m_eventTable.keys();
			
			while ( eventIds.hasMoreElements()) {
				
				// Get the current session via the associated event id
				
				Integer eventId = eventIds.nextElement();
				SMBSrvSession sess = m_eventTable.get( eventId);
				
				if ( sess != null) {
					
					// DEBUG
					
					if ( Debug.EnableDbg && hasDebug())
						Debug.println("[SMB] Closing session " + sess.getUniqueId() + ", event=" + eventId);
						
					// Release the socket event
					
					try {
						Win32NetBIOS.WinsockCloseEvent( eventId.intValue());
					}
					catch ( Exception ex) {
						Debug.println(ex);
					}
					
					// Close the session

					try {
						sess.closeSession();
					}
					catch ( Exception ex) {
						Debug.println(ex);
					}
				}
			}
		}

		// Close the wakeup event
		
		if ( m_win32ActiveEvents[0] != 0) {
			try {
				Win32NetBIOS.Win32CloseEvent( m_win32ActiveEvents[0]);
			}
			catch ( Exception ex) {
				Debug.println(ex);
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
			
			// Wakeup the request handler thread

			wakeupRequestHandler();
		}
	}
	
	/**
	 * Check for idle sessions
	 * 
	 * @return int
	 */
	protected final int checkForIdleSessions() {
		
		// Check if the request handler has any active sessions
		
		int idleCnt = 0;
		
		if ( m_eventTable.size() > 0) {

			// Time to check
			
			long checkTime = System.currentTimeMillis() - (long) m_clientSocketTimeout;
			
			// Enumerate the session list
			
			Enumeration<SMBSrvSession> enumSess = m_eventTable.elements();
			Vector<SMBSrvSession> idleSessList = null;
			
			while ( enumSess.hasMoreElements()) {
				
				// Get the current session
				
				SMBSrvSession sess = enumSess.nextElement();
				
				// Check the time of the last I/O request on this session
				
				if ( sess != null && sess.getLastIOTime() < checkTime) {
					
					// Add to the list of idle sessions
					
					if ( idleSessList == null)
						idleSessList = new Vector<SMBSrvSession>();
					idleSessList.add( sess);
				}
			}
			
			// Close any sessions that are on the idle list
			
			if ( idleSessList != null) {
				
				// Close the idle sessions
				
				for ( int i = 0; i < idleSessList.size(); i++) {

					// Get the current idle session
					
					SMBSrvSession sess = idleSessList.get( i);
					
					// DEBUG
					
					if ( Debug.EnableInfo && hasDebug())
						Debug.println( "[SMB] Closing idle session, " + sess.getUniqueId());
					
					// Close the session
					
					sess.closeSession();
					sess.processPacket( null);
					
					// Update the idle session count
					
					idleCnt++;
				}
			}
		}
		
		// Return the count of idle sessions that were queued for removal
		
		return idleCnt;
	}
	
	/**
	 * Wakeup the main thread
	 */
	protected void wakeupRequestHandler() {
		
		// Set the wakeup event
		
		if ( Win32NetBIOS.Win32SetEvent( m_win32ActiveEvents[0]) == false) {
			
			// DEBUG
			
			if ( Debug.EnableDbg && hasDebug())
				Debug.println("[SMB] Failed to wakeup request handler, " + m_thread.getName());
		}
	}
	
	/**
	 * Requeue a socket event to the active list
	 * 
	 * @param sockEvent int
	 * @param sockPtr int
	 */
	protected void requeueSocketEvent( int sockEvent, int sockPtr) {
		
		// Reset the event status
		
		Win32NetBIOS.WinsockResetEvent( sockEvent);
/**		
		// Re-enable read events for the socket
		
		try {
			Win32NetBIOS.WinsockEventSelect( sockPtr, sockEvent, Winsock2.FD_READ | Winsock2.FD_WRITE | Winsock2.FD_CLOSE);
		}
		catch ( WinsockNetBIOSException ex) {
			
			// DEBUG
			
			if ( Debug.EnableDbg && hasDebug()) {
				Debug.println("[SMB] Error setting FD_READ socket event flag");
				Debug.println(ex);
			}
		}
**/		
		// Add to the requeue list
		
		synchronized ( m_requeueEvents) {
			m_requeueEvents[ m_requeueLen++] = sockEvent;
		}
		
		// Wakeup the main request handler thread
		
		// Set the wakeup event
		
		if ( Win32NetBIOS.Win32SetEvent( m_win32ActiveEvents[0]) == false) {
			
			// DEBUG
			
			if ( Debug.EnableDbg && hasDebug())
				Debug.println("[SMB] Failed to wakeup request handler (requeue), " + m_thread.getName());
		}
	}

	/**
	 * Process the socket event
	 * 
	 * @param eventIdx int
	 */
	private void processSocketEvent( int eventId) {
		
		// Get the associated sessions, and socket, for the event
		
		Integer eventIdKey = new Integer( eventId);
		SMBSrvSession sess = m_eventTable.get( eventIdKey);
		
		// Get the socket via the sessions packet handler
		
		WinsockNetBIOSPacketHandler winsockPktHandler = (WinsockNetBIOSPacketHandler) sess.getPacketHandler();
		NetBIOSSocket nbSocket = winsockPktHandler.getSocket();
		
		int triggeredEvent = 0;
		
		try {
			
			// Find out the event that triggered, check for socket errors
			
			triggeredEvent = Win32NetBIOS.WinsockEnumNetworkEvents( nbSocket.getSocket(), 0);
		}
		catch ( WinsockNetBIOSException ex) {

			// DEBUG
			
			if ( Debug.EnableDbg && hasDebug()) {
				Debug.println("[SMB] Socket error event, sess=" + sess.getUniqueId() + ", eventId=" + eventId);
				Debug.println(ex);
			}
			
			// Close the socket
			
			triggeredEvent = Winsock2.FD_CLOSE;
		}
			
		// DEBUG
		
//		if ( Debug.EnableDbg && hasDebug())
//			Debug.println("[SMB] Triggered id=" + eventId + ", sess=" + sess.getUniqueId() + ", event=0x" + Integer.toHexString( triggeredEvent));
		
		// Check if any events are pending on the socket
		
		if ( triggeredEvent == 0)
			return;
		
		// Socket closed
		
		if (( triggeredEvent & Winsock2.FD_CLOSE) != 0) {
			
			// DEBUG
			
			if ( Debug.EnableDbg && hasDebug())
				Debug.println("[SMB] Close session event, sess=" + sess.getUniqueId());
			
			// Find the socket event within the list
			
			int eventIdx = 0;
			
			while ( m_win32ActiveEvents[ eventIdx] != eventId && eventIdx < m_activeEventsLen)
				eventIdx++;
			
			if ( eventIdx == m_activeEventsLen)
				return;
			
			// Remove the socket event from the active list, and shorten the list
			
			m_win32ActiveEvents [ eventIdx] = m_win32ActiveEvents[ m_activeEventsLen--];
			
			// Close the session
			
			sess.hangupSession( "Client closed socket");
			
			// Close the event
			
			try {
				Win32NetBIOS.WinsockCloseEvent( eventIdKey.intValue());
			}
			catch ( WinsockNetBIOSException ex) {
				
				// DEBUG
				
				if ( Debug.EnableDbg && hasDebug()) {
					Debug.println("[SMB] Error closing socket event");
					Debug.println(ex);
				}
			}
			
			// Remove the event/session mapping
			
			m_eventTable.remove( eventId);
			
			// DEBUG
			
			if ( Debug.EnableDbg && hasDebug())
				Debug.println("[SMB] Removed session " + sess.getUniqueId());
		}
		
		// Check for a read event, incoming data on the socket
		
		if (( triggeredEvent & Winsock2.FD_READ) != 0) {

			// DEBUG
			
//			if ( Debug.EnableDbg && hasDebug())
//				Debug.println("[SMB] Read event, sess=" + sess.getUniqueId());
			
			// Clear the event
			
			Win32NetBIOS.WinsockResetEvent( eventIdKey.intValue());
			
			// Remove the read event for the socket
/**
			try {
				Win32NetBIOS.WinsockEventSelect( nbSocket.getSocket(), eventId, Winsock2.FD_WRITE | Winsock2.FD_CLOSE);
			}
			catch ( WinsockNetBIOSException ex) {
				
				// DEBUG
				
				if ( Debug.EnableDbg && hasDebug()) {
					Debug.println("[SMB] Error clearing FD_READ socket event flag");
					Debug.println(ex);
				}
			}
**/			
			// Check if the session is already processing the incoming request
			
			if ( sess.hasReadInProgress() == false) {
				
				// Update the last I/O time for the session
				
				sess.setLastIOTime( System.currentTimeMillis());
				
				// Queue the session to the thread pool for processing
				
				AsyncWinsockCIFSReadRequest threadReq = new AsyncWinsockCIFSReadRequest( sess, eventIdKey.intValue(), this);
				m_threadPool.queueRequest( threadReq);
			}
		}
		
		// Check for a write event, socket has buffer space for outgoing data
		
		if (( triggeredEvent & Winsock2.FD_WRITE) != 0) {
			
			// DEBUG
			
			if ( Debug.EnableDbg && hasDebug())
				Debug.println("[SMB] Write event, sess=" + sess.getUniqueId());
			
			// Clear the event
			
			Win32NetBIOS.WinsockResetEvent( eventIdKey.intValue());
			
			// Check if the packet handler has any queued writes
			
			AsynchronousWritesHandler writesHandler = (AsynchronousWritesHandler) winsockPktHandler;
			if ( writesHandler.getQueuedWriteCount() > 0) {
				
				// DEBUG
				
				if ( Debug.EnableDbg && hasDebug())
					Debug.println("[SMB] Submit queued writes for processing, sess=" + sess.getUniqueId());

				// Queue the packet handler to the thread pool for processing
				
				AsyncWinsockCIFSWriteRequest threadReq = new AsyncWinsockCIFSWriteRequest(sess, eventIdKey.intValue(), this);
				m_threadPool.queueRequest( threadReq);
			}
		}
	}
}
