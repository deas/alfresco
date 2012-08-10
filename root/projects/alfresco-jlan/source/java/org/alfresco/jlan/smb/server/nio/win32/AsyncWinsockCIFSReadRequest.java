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
import org.alfresco.jlan.server.thread.ThreadRequest;
import org.alfresco.jlan.smb.server.PacketHandler;
import org.alfresco.jlan.smb.server.SMBSrvPacket;
import org.alfresco.jlan.smb.server.SMBSrvSession;
import org.alfresco.jlan.smb.server.win32.WinsockNetBIOSPacketHandler;

/**
 * Asynchronous Winsock NIO CIFS Read Request Class
 * 
 * <p>Holds the details of a Winsock NetBIOS JNI socket based CIFS session request for processing by a thread pool.
 * 
 * @author gkspencer
 */
public class AsyncWinsockCIFSReadRequest implements ThreadRequest {

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
	public AsyncWinsockCIFSReadRequest( SMBSrvSession sess, int sockEvent, AsyncWinsockCIFSRequestHandler reqHandler) {
		m_sess         = sess;
		m_socketEvent  = sockEvent;
		m_reqHandler   = reqHandler;
	}
	
	/**
	 * Run the CIFS request
	 */
	public void runRequest() {
		
		// Check if the session is still alive
		
		if ( m_sess.isShutdown() == false) {
			
			SMBSrvPacket smbPkt = null;
			
			try {
				
				// Get the packet handler and read in the CIFS request
				
				WinsockNetBIOSPacketHandler pktHandler = (WinsockNetBIOSPacketHandler) m_sess.getPacketHandler();
				smbPkt = pktHandler.readPacket();
				
				// Clear the read in progress flag for the session
				
				m_sess.setReadInProgress( false);
				
				// DEBUG
				
//				Debug.println("[SMB] Read packet, sess=" + m_sess.getUniqueId() + " ,pkt=" + smbPkt);
				
				// If the request packet is not valid then close the session
				
				if ( smbPkt == null) {
					
					// Close the session
					
					m_sess.hangupSession( "Client closed socket");
				}
				else {
					
					// Re-enable read events for this socket channel
					
//					m_reqHandler.requeueSocketEvent( m_socketEvent, pktHandler.getSocket().getSocket());
				}
				
				// Process the CIFS request
				
				m_sess.processPacket( smbPkt);
				smbPkt = null;
				
				// Process any asynchronous packets (oplock breaks and change notifications)
				
				int asyncCnt = m_sess.sendQueuedAsyncResponses();
				
				// DEBUG
				
				if ( asyncCnt > 0 && Debug.EnableInfo && m_sess.hasDebug( SMBSrvSession.DBG_SOCKET))
					Debug.println("Sent queued async packets (JNI/NIO) count=" + asyncCnt + ", sess=" + m_sess.getUniqueId() + ", addr=" + m_sess.getRemoteAddress().getHostAddress());
			}
			catch ( Throwable ex) {
				Debug.println(ex);
			}
			finally {
				
				// Make sure the request packet is returned to the pool
				
				if ( smbPkt != null)
					m_sess.getPacketPool().releasePacket( smbPkt);
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
		str.append("-Read]");
		
		return str.toString();
	}
}
