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

import org.alfresco.jlan.netbios.NetBIOSName;
import org.alfresco.jlan.netbios.RFCNetBIOSProtocol;
import org.alfresco.jlan.netbios.win32.NetBIOSSocket;
import org.alfresco.jlan.netbios.win32.Win32NetBIOS;
import org.alfresco.jlan.server.NetworkServer;
import org.alfresco.jlan.server.SessionHandlerBase;
import org.alfresco.jlan.smb.server.PacketHandler;
import org.alfresco.jlan.smb.server.SMBServer;
import org.alfresco.jlan.smb.server.win32.WinsockNetBIOSPacketHandler;

/**
 * Asynchronous Winsock NetBIOS Session Handler Class
 * 
 * <p>
 * @author gkspencer
 */
public class AsyncWinsockNetBIOSSessionHandler extends SessionHandlerBase {

	// LANA to listen on
	
	private int m_lana;

	// NetBIOS name to listen for incoming requests on
	
	private NetBIOSName m_nbName;
	
	// Listener socket, for incoming connections
	
	private NetBIOSSocket m_socket;
	
	// Associated CIFS server
	
	private SMBServer m_smbServer;
	
	/**
	 * Class constructor
	 * 
	 * @param name String
	 * @param nbName NetBIOSName
	 * @param server NetworkServer
	 */
	public AsyncWinsockNetBIOSSessionHandler( int lana, NetBIOSName nbName, NetworkServer server) {
		super( "Winsock NetBIOS", "SMB", server, null, RFCNetBIOSProtocol.PORT);
		
		m_lana   = lana;
		m_nbName = nbName;
	}

	/**
	 * Return the LANA that this listener is using
	 * 
	 * @return int
	 */
	public final int getLANA() {
		return m_lana;
	}
	
	/**
	 * Return the listener NetBIOS socket
	 * 
	 * @return NetBIOSSocket
	 */
	public final NetBIOSSocket getSocket() {
		return m_socket;
	}

	/**
	 * Return the associated CIFS server
	 * 
	 * @return SMBServer
	 */
	public final SMBServer getSMBServer() {
		return m_smbServer;
	}
	
	/**
	 * Return the name this handler is listening on
	 * 
	 * @return NetBIOSName
	 */
	public final NetBIOSName getNetBIOSName() {
		return m_nbName;
	}
	
	/**
	 * Initialize the session handler
	 * 
	 * @param server NetworkServer
	 */
	public void initializeSessionHandler(NetworkServer server)
		throws IOException {

		// Save the CIFS server
		
		m_smbServer = (SMBServer) server;
		
		// Enumerate the LAN adapters, use the first available if the LANA has not been specified in
		// the configuration

		int[] lanas = Win32NetBIOS.LanaEnumerate();
		if ( lanas.length > 0) {

			// Check if the LANA has been specified via the configuration, if not then use the first
			// available

			if ( m_lana == -1)
				m_lana = lanas[0];
			else {

				// Check if the required LANA is available

				boolean lanaOnline = false;
				int idx = 0;

				while (idx < lanas.length && lanaOnline == false) {

					// Check if the LANA is listed

					if ( lanas[idx++] == getLANA())
						lanaOnline = true;
				}

				// If the LANA is not available then exit with an exception for now

				if ( lanaOnline == false)
					throw new IOException( "LANA " + getLANA() + " is not online");
			}
		}
		else {

			// If the LANA has not been set throw an exception as no LANAs are available

			if ( m_lana == -1)
				throw new IOException("No Win32 NetBIOS LANAs available");
		}

		// Initialize the listener socket
		
		m_socket = NetBIOSSocket.createListenerSocket( getLANA(), m_nbName);
	}
	
	/**
	 * Create a packet handler for the new client socket connection
	 * 
	 * @param clientSock NetBIOSSocket
	 * @return PacketHandler
	 * @exception IOException
	 */
	public PacketHandler createPacketHandler( NetBIOSSocket clientSock)
		throws IOException {
		
		// Create a Winsock NetBIOS packet handler, async mode enabled
		
		return new WinsockNetBIOSPacketHandler( m_lana, clientSock, getSMBServer().getPacketPool(), true);
	}
	
	/**
	 * Check if the specified LANA is online
	 * 
	 * @param lana int
	 * @return boolean
	 */
	private final boolean isLANAOnline(int lana) {

		// Get a list of the available LANAs

		int[] lanas = Win32NetBIOS.LanaEnumerate();

		if ( lanas != null && lanas.length > 0) {

			// Check if the specified LANA is available

			for (int i = 0; i < lanas.length; i++) {
				if ( lanas[i] == lana)
					return true;
			}
		}

		// LANA not online

		return false;
	}

	/**
	 * Close the session handler
	 * 
	 * @param server NetworkServer
	 */
	public void closeSessionHandler(NetworkServer server) {
		
		// Close the socket, this will deregister with the selector
		
		if ( m_socket != null) {
			m_socket.closeSocket();
			m_socket = null;
		}
	}
}
