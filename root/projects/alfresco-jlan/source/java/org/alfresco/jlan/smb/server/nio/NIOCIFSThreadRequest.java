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

import java.nio.channels.SelectionKey;

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.server.thread.ThreadRequest;
import org.alfresco.jlan.smb.server.PacketHandler;
import org.alfresco.jlan.smb.server.SMBSrvPacket;
import org.alfresco.jlan.smb.server.SMBSrvSession;

/**
 * NIO CIFS Thread Request Class
 * 
 * <p>Holds the details of an NIO channel based CIFS session request for processing by a thread pool.
 * 
 * @author gkspencer
 */
public class NIOCIFSThreadRequest implements ThreadRequest {

	// Maximum packets to run per thread run
	
	private static final int MaxPacketsPerRun	= 4;
	
	// CIFS session
	
	private SMBSrvSession m_sess;
	
	// Selection key for this socket channel
	
	private SelectionKey m_selectionKey;
	
	/**
	 * Class constructor
	 * 
	 * @param sess SMBSrvSession
	 * @param selKey SelectionKey
	 * @param handler CIFSRequestHandler
	 */
	public NIOCIFSThreadRequest( SMBSrvSession sess, SelectionKey selKey) {
		m_sess         = sess;
		m_selectionKey = selKey;
	}
	
	/**
	 * Run the CIFS request
	 */
	public void runRequest() {
		
		// Check if the session is still alive
		
		if ( m_sess.isShutdown() == false) {
			
			// Read one or more packets from the socket for this session

			int pktCount = 0;
			boolean morePkts = true;
			boolean pktError = false;
			
			SMBSrvPacket smbPkt = null;
			
			while ( pktCount < MaxPacketsPerRun && morePkts == true && pktError == false) {
				
				try {
					
					// Get the packet handler and read in the CIFS request
					
					PacketHandler pktHandler = m_sess.getPacketHandler();
					smbPkt = pktHandler.readPacket();
					
					// If the request packet is not valid then close the session
					
					if ( smbPkt == null) {
						
						// If we have not processed any packets in this run it is an error
						
						if ( pktCount == 0) {
							
							// DEBUG
							
							if ( Debug.EnableInfo && m_sess.hasDebug( SMBSrvSession.DBG_SOCKET))
								Debug.println("Received null packet, closing session sess=" + m_sess.getUniqueId() + ", addr=" + m_sess.getRemoteAddress().getHostAddress());
							
							// Close the session
							
							m_sess.hangupSession( "Client closed socket");
							m_sess.processPacket( null);
							
							// Cancel the selection key
							
							m_selectionKey.cancel();
							m_selectionKey.selector().wakeup();
							
							// Indicate socket/packet error
							
							pktError = true;
						}
						
						// No more packets available
						
						morePkts = false;
					}
					else {
						
						
						// Update the count of packets processed
						
						pktCount++;

						// If this is the last packet before we hit the maximum packets per thread then
						// re-enable read events for this socket channel
						
						if ( pktCount == MaxPacketsPerRun) {
							m_selectionKey.interestOps( m_selectionKey.interestOps() | SelectionKey.OP_READ);
							m_selectionKey.selector().wakeup();
						}
						
						// Process the CIFS request
						
						m_sess.processPacket( smbPkt);
						smbPkt = null;
					}
				}
				catch ( Throwable ex) {
	
					// DEBUG
					
					if ( Debug.EnableInfo && m_sess.hasDebug( SMBSrvSession.DBG_SOCKET))
						Debug.println("Error during packet receive, closing session sess=" + m_sess.getUniqueId() + ", addr=" + m_sess.getRemoteAddress().getHostAddress() + " ex=" + ex.getMessage());
					
					// Close the session
					
					m_sess.hangupSession( "Client closed socket");
					m_sess.processPacket( null);
					
					// Cancel the selection key
					
					m_selectionKey.cancel();
					m_selectionKey.selector().wakeup();
					
					// Indicate socket/packet error
					
					pktError = true;
				}
				finally {
					
					// Make sure the request packet is returned to the pool
					
					if ( smbPkt != null)
						m_sess.getPacketPool().releasePacket( smbPkt);
				}
			}
			
			// Re-enable read events for this socket channel, if there were no errors
			
			if ( pktError == false && pktCount < MaxPacketsPerRun) {
				
				// Re-enable read events for this socket channel
			
				m_selectionKey.interestOps( m_selectionKey.interestOps() | SelectionKey.OP_READ);
				m_selectionKey.selector().wakeup();
			}			

			// DEBUG
			
			if ( Debug.EnableInfo && m_sess.hasDebug( SMBSrvSession.DBG_THREADPOOL) && pktCount > 1)
//			if ( Debug.EnableInfo && pktCount > 1)
				Debug.println("Processed " + pktCount + " packets for addr=" + m_sess.getRemoteAddress().getHostAddress() + " in one thread run (max=" + MaxPacketsPerRun + ")");
		}
	}
	
	/**
	 * Return the CIFS request details as a string
	 * 
	 * @reurun String
	 */
	public String toString() {
		StringBuilder str = new StringBuilder();
		
		str.append("[NIO CIFS Sess=");
		str.append( m_sess.getUniqueId());
		str.append("]");
		
		return str.toString();
	}
}
