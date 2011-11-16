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

package org.alfresco.jlan.smb.server.win32;

import java.io.IOException;

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.netbios.win32.NetBIOSSocket;
import org.alfresco.jlan.netbios.win32.WinsockNetBIOSException;
import org.alfresco.jlan.server.core.NoPooledMemoryException;
import org.alfresco.jlan.smb.server.CIFSPacketPool;
import org.alfresco.jlan.smb.server.PacketHandler;
import org.alfresco.jlan.smb.server.SMBSrvPacket;
import org.alfresco.jlan.smb.server.SMBSrvPacketQueue;
import org.alfresco.jlan.smb.server.SMBSrvPacketQueue.QueuedSMBPacket;
import org.alfresco.jlan.smb.server.nio.AsynchronousWritesHandler;

/**
 * Winsock NetBIOS Packet Handler Class
 * 
 * <p>
 * Uses a Windows Winsock NetBIOS socket to provide the low level session layer for better
 * integration with Windows.
 * 
 * @author gkspencer
 */
public class WinsockNetBIOSPacketHandler extends PacketHandler implements AsynchronousWritesHandler {

	// Constants
	//
	// Receive error indicating a receive buffer error

	private static final int ReceiveBufferSizeError = 0x80000000;

	// Network LAN adapter to use

	private int m_lana;

	// NetBIOS session socket

	private NetBIOSSocket m_sessSock;

	// Asynchronous I/O packet queue
	
	private SMBSrvPacketQueue m_asyncQueue;
	
	// Asynchronous mode enabled
	
	private boolean m_asyncMode;
	
	/**
	 * Class constructor
	 * 
	 * @param lana int
	 * @param sock NetBIOSSocket
	 * @param packetPool CIFSPacketPool
	 * @param asyncMode boolean
	 */
	public WinsockNetBIOSPacketHandler(int lana, NetBIOSSocket sock, CIFSPacketPool packetPool, boolean asyncMode) {
		super(SMBSrvPacket.PROTOCOL_WIN32NETBIOS, "WinsockNB", "WSNB", sock.getName().getName(), packetPool);

		m_lana = lana;
		m_sessSock = sock;
		
		m_asyncMode = asyncMode;
		
		// If asynchrnous mode is enabled then allocate the asynchronous packet queue
		
		if ( hasAsynchronousMode())
			m_asyncQueue = new SMBSrvPacketQueue();
	}

	/**
	 * Return the LANA number
	 * 
	 * @return int
	 */
	public final int getLANA() {
		return m_lana;
	}

	/**
	 * Return the NetBIOS socket
	 * 
	 * @return NetBIOSSocket
	 */
	public final NetBIOSSocket getSocket() {
		return m_sessSock;
	}

	/**
	 * Return the count of available bytes in the receive input stream
	 * 
	 * @return int
	 * @exception IOException If a network error occurs.
	 */
	public int availableBytes()
		throws IOException {

		// Do not know the available byte count

		return -1;
	}

	/**
	 * Check if asynchronous mode is enabled
	 * 
	 * @return boolean
	 */
	public final boolean hasAsynchronousMode() {
		return m_asyncMode;
	}
	
	/**
	 * Read a packet from the client
	 * 
	 * @return SMBSrvPacket
	 * @throws IOException
	 */
	public SMBSrvPacket readPacket()
		throws IOException {

		// Get the length of the pending receive data, so we can allocate the correct sized buffer
		
		int rxlen = m_sessSock.available();

		// Check if data is showing as available yet
		
		if ( rxlen == 0) {
			int loop = 0;
			
			while ( loop++ < 50 && rxlen == 0) {
				rxlen = m_sessSock.available();
				if ( rxlen == 0) {
					try {
						Thread.sleep( 2);
					}
					catch (Exception e) {
					}
				}
			}
			if ( rxlen == 0) {
				
				// DEBUG
				
				if ( hasDebug())
					Debug.println("***** Still no data after 100ms *****");
				
				// Check for asynchronous mode, return a null packet
			
				if ( hasAsynchronousMode())
					return null;
			}
		}
		
		SMBSrvPacket pkt = null;
		
		// Receive an SMB/CIFS request packet via the Winsock NetBIOS socket

		try {

			// Allocate a packet for the data
			
			pkt = getPacketPool().allocatePacket( rxlen + 8);
			
			// Read a packet of data

			rxlen = m_sessSock.read(pkt.getBuffer(), 4, pkt.getBufferLength() - 4);

			// Check if the buffer is not big enough to receive the entire packet, extend the buffer
			// and read the remaining part of the packet

			if ( rxlen == ReceiveBufferSizeError) {

				// Check if there is a larger buffer size available from the packet pool
				
				if ( pkt.getBufferLength() >= getPacketPool().getLargestSize()) {
					
					// Release the packet back to the pool
					
					getPacketPool().releasePacket( pkt);
					pkt = null;
				
					// Throw an exception
					
					throw new RuntimeException("Winsock NetBIOS receive over max available buffer size");
				}
				
				// Get the remaining data length
				
				int rxlen2 = m_sessSock.available();
				
				if ( rxlen2 > 0) {
					
					// Allocate a larger buffer to hold the full packet
					
					SMBSrvPacket pkt2 = null;
					
					try {
						pkt2 = getPacketPool().allocatePacket( getPacketPool().getLargestSize());
					}
					catch ( NoPooledMemoryException ex) {
						
						// Release the original packet back to the pool
						
						getPacketPool().releasePacket( pkt);
						pkt = null;
					
						// Throw an exception
						
						throw new RuntimeException("Winsock NetBIOS receive error on second stage receive (no pooled memory)");
					}
					
					// Copy the existing receive data to the new packet
					
					rxlen = pkt.getBufferLength();
					System.arraycopy(pkt.getBuffer(), 4, pkt2.getBuffer(), 4, rxlen - 4);
					
					// Release the original packet buffer, switch to the new packet
					
					getPacketPool().releasePacket( pkt);
					pkt = pkt2;
					
					// Read the remaining data

					rxlen2 = m_sessSock.read( pkt.getBuffer(), rxlen, pkt.getBufferLength() - rxlen);
					
					// Check the status of the second read
					
					if ( rxlen2 == ReceiveBufferSizeError) {
						
						// Release the packet back to the pool
						
						getPacketPool().releasePacket( pkt);
						pkt = null;
					
						// Throw an exception
						
						throw new RuntimeException("Winsock NetBIOS receive error on second stage receive");
					}
					
					// Update the total receive length
					
					rxlen += rxlen2 - 4;
				}
			}
		}
		catch ( WinsockNetBIOSException ex) {
			
			// DEBUG
			
			if ( Debug.EnableDbg && hasDebug())
				Debug.println( ex);

			// Check the Winsock error code
			
			if ( ex.getErrorCode() != 0) {
				
				// Release the packet back to the pool
				
				getPacketPool().releasePacket( pkt);
				
				// Clear the received packet to indicate error
				
				pkt = null;
				
				// Rethrow the exception
				
				throw ex;
			}
			else {

				// DEBUG
				
				if ( Debug.EnableDbg && hasDebug())
					Debug.println("Winsock error code zero, ignored, rxlen=" + rxlen + ", pktlen=" + pkt.getBuffer().length);
				
				// Indicate a zero length receive
				
				rxlen = 0;
			}
		}
		catch ( Throwable ex) {

			// DEBUG
			
			if ( Debug.EnableDbg && hasDebug())
				Debug.println( ex);
			
			// Release the packet back to the pool
			
			getPacketPool().releasePacket( pkt);
			
			// Clear the received packet to indicate error
			
			pkt = null;
			
			// Rethrow the exception
			
			rethrowException(ex);
		}

		// Set the received packet length
		
		if ( pkt != null)
			pkt.setReceivedLength( rxlen);
		
		// Return the received packet

		return pkt;
	}

	/**
	 * Write a packet to the client
	 * 
	 * @param pkt SMBSrvPacket
	 * @param len int
	 * @param writeRaw boolean
	 * @throws IOException
	 */
	public void writePacket(SMBSrvPacket pkt, int len, boolean writeRaw)
		throws IOException {

		// If asynchronous mode is enabled and the queue is not empty then queue this write request
		
		if ( hasAsynchronousMode() && m_asyncQueue.numberOfPackets() > 0) {
			
			// DEBUG
			
			if ( Debug.EnableDbg && hasDebug())
				Debug.println("*** Queued packet for async I/O pkt=" + pkt.getPacketTypeString() + ", len=" + len + ", raw=" + writeRaw + "  (QueueLen) ***");
			
			// Queue the request
			
			m_asyncQueue.addToQueue(pkt, 4, len, writeRaw);
			return;
		}
		
		// Output the packet via the Winsock NetBIOS socket
		//
		// As Windows is handling the NetBIOS session layer we do not send the 4 byte header that is
		// used by the NetBIOS over TCP/IP and native SMB packet handlers.

		int pos = 4;
		int wrlen = len;
		int txlen = 0;
		
		while ( wrlen > 0) {
			
			// Write the packet
		
			txlen = m_sessSock.write(pkt.getBuffer(), pos, wrlen);
			
			// Check if asynchronous mode is enabled and the socket would block, in this case we queue the response
			// to be sent when the socket signals that it is writeable
			
			if ( hasAsynchronousMode() && txlen == NetBIOSSocket.SocketWouldBlock) {

				// DEBUG

				if ( Debug.EnableDbg && hasDebug())
					Debug.println("*** Queued packet for async I/O pkt=" + pkt.getPacketTypeString() + ", len=" + len + ", raw=" + writeRaw + "  (WouldBlock) ***");
				
				// Queue the request
				
				m_asyncQueue.addToQueue(pkt, pos, wrlen, writeRaw);
				return;
			}
			
			// If the write length is zero wait a short while before retrying
			
			else if ( txlen == 0) {
				try {
					Thread.sleep( 10);
					
					// DEBUG
					
//					Debug.println( "*** Zero length write, wait 10ms wrlen=" + wrlen + ", pktType=" + pkt.getPacketTypeString() + " ***");
				}
				catch ( InterruptedException ex) {
				}
			}
			else {
				
				// Adjust the remaining write length
				
				wrlen -= txlen;
				pos   += txlen;
			}
		}

		// Do not check the status, if the session has been closed the next receive will fail
	}

	/**
	 * Flush the output socket
	 * 
	 * @exception IOException If a network error occurs
	 */
	public void flushPacket()
		throws IOException {

		// Nothing to do
	}

	/**
	 * Close the Winsock NetBIOS packet handler.
	 */
	public void closeHandler() {

		super.closeHandler();

		// Release any queued packets back to the pool
		
		if ( hasAsynchronousMode()) {
			
			// Release queued writes back to the packet pool
			
			while ( m_asyncQueue.numberOfPackets() > 0) {
				
				// Get a packet from the queue and release back to the packet pool
				
				QueuedSMBPacket queuedPkt = m_asyncQueue.removeFromQueue();
				getPacketPool().releasePacket( queuedPkt.getPacket());
			}
		}
		
		// Close the session socket

		if ( m_sessSock != null)
			m_sessSock.closeSocket();
	}
	
	/**
	 * Return the count of queued writes
	 * 
	 * @return int
	 */
	public int getQueuedWriteCount() {
		
		// Check if the asynchronous mode is enabled
		
		if ( hasAsynchronousMode() == false || m_asyncQueue == null)
			return 0;
		return m_asyncQueue.numberOfPackets();
	}
	
	/**
	 * Process the write queue and send pending data until outgoing buffers are full
	 * 
	 * @return int Number of requests that were removed from the queue
	 */
	public int processQueuedWrites() {

		// Process the queued write requests
		
		int procCnt = 0;
		
		if ( m_asyncQueue != null) {
			
			// Loop until the queue is emptied or the socket buffer is full again
			
			boolean wouldBlock = false;
			
			while ( m_asyncQueue.numberOfPackets() > 0 && wouldBlock == false) {
			
				// Get a request from the queue, leave the request on the queue until the send is successful
				
				QueuedSMBPacket queuedPkt = m_asyncQueue.getHeadOfQueue();
				
				// Output the packet via the Winsock NetBIOS socket

				int pos = queuedPkt.getWriteOffset();
				int wrlen = queuedPkt.getWriteLength();
				int txlen = 0;
				
				while ( wrlen > 0 && txlen != NetBIOSSocket.SocketWouldBlock) {
					
					// Write the packet
				
					synchronized ( m_sessSock) {
						
						try {
							txlen = m_sessSock.write( queuedPkt.getPacket().getBuffer(), pos, wrlen);
						}
						catch ( WinsockNetBIOSException ex) {
							
							// Socket error, stop processing the queue
							
							txlen = NetBIOSSocket.SocketWouldBlock;
						}
					}
					
					// Check if the write status indicates the socket would block, in this case we update the queued request and exit
					
					if ( txlen == NetBIOSSocket.SocketWouldBlock) {

						// DEBUG

						if ( hasDebug())
							Debug.println("*** Process queued writes sts=WouldBlock ***");
						
						// Update the request if part of the buffer was sent
						
						if ( pos != queuedPkt.getWriteOffset()) {
							
							// Update the write offset and length with the new values
							
							queuedPkt.updateSettings( pos, wrlen);
						}
					}
					else {
						
						// Adjust the remaining write length
						
						wrlen -= txlen;
						pos   += txlen;
					}
				}
				
				// Set the I/O blocking flag
				
				if ( txlen == NetBIOSSocket.SocketWouldBlock) {
					
					// Stop processing the queue, socket buffer is full again
					
					wouldBlock = true;
				}
				else if ( wrlen == 0) {
					
					// Queued request has been sent, remove it from the queue
					
					m_asyncQueue.removeFromQueue();
					
					// DEBUG

					if ( hasDebug())
						Debug.println("*** Sent queued pkt=" + queuedPkt.getPacket() + ", len=" + queuedPkt.getWriteLength() + ", offset=" + queuedPkt.getWriteOffset());
					
					// Release the packet back to the pool
					
					queuedPkt.getPacket().setQueuedForAsyncIO( false);
					getPacketPool().releasePacket( queuedPkt.getPacket());
					
					// Update the count of packets sent
					
					procCnt++;
				}
			}
		}
		
		// Return the count of write requests that were removed from the queue
		
		return procCnt;
	}
}
