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

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.netbios.win32.Win32NetBIOS;
import org.alfresco.jlan.server.thread.ThreadRequest;
import org.alfresco.jlan.smb.server.SMBSrvSession;
import org.alfresco.jlan.smb.server.nio.AsynchronousWritesHandler;

/**
 * Asynchronous Winsock NIO CIFS Write Request Class
 * 
 * <p>Holds the details of a Winsock NetBIOS JNI socket based CIFS session request for processing by a thread pool.
 * 
 * @author gkspencer
 */
public class AsyncWinsockCIFSWriteRequest implements ThreadRequest {

	// CIFS session
	
	private SMBSrvSession m_sess;
	
	// Socket event for this NetBIOS socket
	
	private int m_socketEvent;
	
	// Request handler
	
	private AsyncWinsockCIFSRequestHandler m_reqHandler;
	
	/**
	 * Class constructor
	 * 
	 * @param sess SMBSrvSession
	 * @param sockEvent int
	 * @param reqHandler AsyncWinsockCIFSRequestHandler
	 */
	public AsyncWinsockCIFSWriteRequest( SMBSrvSession sess, int sockEvent, AsyncWinsockCIFSRequestHandler reqHandler) {
		m_sess         = sess;
		m_socketEvent  = sockEvent;
		m_reqHandler   = reqHandler;
	}
	
	/**
	 * Run the CIFS request
	 */
	public void runRequest() {
		
		// Check if the session is still alive
		
		if ( m_sess.isShutdown() == false &&
				m_sess.getPacketHandler() instanceof AsynchronousWritesHandler) {
			
			try {
				
				// Get the packet handler and check if there are queued write requests
				
				AsynchronousWritesHandler writeHandler = (AsynchronousWritesHandler) m_sess.getPacketHandler();
				
				if ( writeHandler.getQueuedWriteCount() > 0) {
					
					Debug.println("%%% Processing queued writes, queued=" + writeHandler.getQueuedWriteCount() + " %%%");
					
					// Process the queued write requests
					
					int wrCnt = writeHandler.processQueuedWrites();
					
					// DEBUG
					
					Debug.println("%%% Processed " + wrCnt + " queued write requests, queued=" + writeHandler.getQueuedWriteCount() + " %%%");
				}
				
			}
			catch ( Throwable ex) {
				Debug.println(ex);
			}
		}
	}
	
	/**
	 * Return the CIFS request details as a string
	 * 
	 * @reurun String
	 */
	public String toString() {
		StringBuilder str = new StringBuilder();
		
		str.append("[Async Winsock CIFS Sess=");
		str.append( m_sess.getUniqueId());
		str.append("-Write]");
		
		return str.toString();
	}
}
