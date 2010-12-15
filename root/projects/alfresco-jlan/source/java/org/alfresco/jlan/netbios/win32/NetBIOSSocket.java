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

package org.alfresco.jlan.netbios.win32;

import java.io.IOException;
import java.nio.channels.IllegalBlockingModeException;

import org.alfresco.jlan.netbios.NetBIOSName;

/**
 * NetBIOS Socket Class
 * 
 * <p>
 * Contains the details of a Winsock NetBIOS socket that was opened using native code.
 * 
 * @author gkspencer
 */
public class NetBIOSSocket {

	// Status value to indicate that a write could not be don as it would block the socket
	
	public static final int SocketWouldBlock	= -2;
	
	// Flag to indicate if the NetBIOS socket interface has been initialized

	private static boolean _nbSocketInit;

	// NetBIOS LANA that the socket is associated with

	private int m_lana;

	// Socket pointer (Windows SOCKET)

	private int m_socket;

	// NetBIOS name, either listening name or callers name

	private NetBIOSName m_nbName;

	// Flag to indicate if this is a listener socket

	private boolean m_listenerSocket;
	
	// Socket blocking mode, true if in non-blocking mode
	
	private boolean m_nonBlockMode;

	// Associated selector
	
	private NetBIOSSelector m_selector;
	
	/**
	 * Initialize the Winsock NetBIOS interface
	 */
	public static final void initializeSockets()
		throws WinsockNetBIOSException {

		// Check if the NetBIOS socket interface has been initialized

		if ( _nbSocketInit == false) {

			// Initialize the NetBIOS socket interface

			Win32NetBIOS.InitializeSockets();

			// Indicate that the NetBIOS socket interface is initialized

			_nbSocketInit = true;
		}
	}

	/**
	 * Shutdown the Winsock NetBIOS interface
	 */
	public static final void shutdownSockets() {

		// Check if the NetBIOS socket interface has been initialized

		if ( _nbSocketInit == true) {

			// Indicate that the NetBIOS socket interface is not initialized

			_nbSocketInit = false;

			// Initialize the NetBIOS socket interface

			Win32NetBIOS.ShutdownSockets();
		}
	}

	/**
	 * Determine if the Winsock NetBIOS interface is initialized
	 * 
	 * @return boolean
	 */
	public static final boolean isInitialized() {
		return _nbSocketInit;
	}

	/**
	 * Wait for one or more asynchronous sockets to trigger a receive event
	 * 
	 * @param sockCnt int
	 * @param sockList int[]
	 * @param sockEvent int[]
	 * @return int
	 * @exception WinsockNetBIOSException
	 */
	public static final int SelectReceiveSockets( int sockCnt, int[] sockList, int[] sockEvent)
		throws  WinsockNetBIOSException {

		// Wait for one or more sockets in the list to trigger receive events
		
		return Win32NetBIOS.SelectReceiveSockets( sockCnt, sockList, sockEvent);
	}
	
	/**
	 * Create a NetBIOS socket to listen for incoming sessions on the specified LANA
	 * 
	 * @param lana int
	 * @param nbName NetBIOSName
	 * @return NetBIOSSocket
	 * @exception NetBIOSSocketException
	 * @exception WinsockNetBIOSException
	 */
	public static final NetBIOSSocket createListenerSocket(int lana, NetBIOSName nbName)
		throws WinsockNetBIOSException, NetBIOSSocketException {
		
		// Create the listener socket, check for duplicate names when registering
		
		return NetBIOSSocket.createListenerSocket(lana, nbName, false);
	}
	
	/**
	 * Create a NetBIOS socket to listen for incoming sessions on the specified LANA
	 * 
	 * @param lana int
	 * @param nbName NetBIOSName
	 * @param fastAddName boolean
	 * @return NetBIOSSocket
	 * @exception NetBIOSSocketException
	 * @exception WinsockNetBIOSException
	 */
	public static final NetBIOSSocket createListenerSocket(int lana, NetBIOSName nbName, boolean fastAddName)
		throws WinsockNetBIOSException, NetBIOSSocketException {

		// Initialize the Winsock NetBIOS interface

		initializeSockets();

		// Create a new NetBIOS socket

		int sockPtr = Win32NetBIOS.CreateSocket(lana);
		if ( sockPtr == 0)
			throw new NetBIOSSocketException("Failed to create NetBIOS socket");

		// Bind the socket to a NetBIOS name

		if ( Win32NetBIOS.BindSocket(sockPtr, nbName.getNetBIOSName(), fastAddName) != 0)
			throw new NetBIOSSocketException("Failed to bind NetBIOS socket");

		// Return the NetBIOS socket

		return new NetBIOSSocket(lana, sockPtr, nbName, true);
	}

	/**
	 * Create a NetBIOS socket that is connected to a remote server/service
	 * 
	 * @param lana int
	 * @param remoteName NetBIOSName
	 * @return NetBIOSSocket
	 * @exception NetBIOSSocketException
	 * @exception WinsockNetBIOSException
	 */
	public static final NetBIOSSocket connectSocket( int lana, NetBIOSName remoteName)
	throws WinsockNetBIOSException, NetBIOSSocketException {

		// Initialize the Winsock NetBIOS interface

		initializeSockets();

		// Create a new NetBIOS socket

		int sockPtr = Win32NetBIOS.CreateSocket(lana);
		if ( sockPtr == 0)
			throw new NetBIOSSocketException("Failed to create NetBIOS socket");

		// Connect to the remote server/service

		Win32NetBIOS.ConnectSocket( sockPtr, remoteName.getNetBIOSName());

		// Return the NetBIOS socket

		return new NetBIOSSocket(lana, sockPtr, remoteName, false);
	}
	
	/**
	 * Class constructor
	 * 
	 * @param lana int
	 * @param sockPtr int
	 * @param nbName NetBIOSName
	 * @param listener boolean
	 */
	private NetBIOSSocket(int lana, int sockPtr, NetBIOSName nbName, boolean listener) {
		m_lana = lana;
		m_nbName = nbName;
		m_socket = sockPtr;

		m_listenerSocket = listener;
	}

	/**
	 * Return the NetBIOS LANA the socket is associated with
	 * 
	 * @return int
	 */
	public final int getLana() {
		return m_lana;
	}

	/**
	 * Determine if this is a listener type socket
	 * 
	 * @return boolean
	 */
	public final boolean isListener() {
		return m_listenerSocket;
	}

	/**
	 * Determine if the socket is valid
	 * 
	 * @return boolean
	 */
	public final boolean hasSocket() {
		return m_socket != 0 ? true : false;
	}

	/**
	 * Return the socket pointer
	 * 
	 * @return int
	 */
	public final int getSocket() {
		return m_socket;
	}

	/**
	 * Return the NetBIOS name. For a listening socket this is the local name, for a session socket
	 * this is the remote callers name.
	 * 
	 * @return NetBIOSName
	 */
	public final NetBIOSName getName() {
		return m_nbName;
	}

	/**
	 * Set this socket to use non-blocking I/O
	 * 
	 * @param nonBlocking boolean
	 * @exception WinsockNetBIOSException
	 */
	public final void configureBlocking( boolean nonBlocking)
		throws WinsockNetBIOSException {
		
		// Set the non-blocking mode of the socket
		
		Win32NetBIOS.SetNonBlockingSocket( getSocket(), nonBlocking);
		
		// save the new setting
		
		m_nonBlockMode = nonBlocking ? false : true;
	}
	
	/**
	 * Write data to the session socket
	 * 
	 * @param buf byte[]
	 * @param off int
	 * @param len int
	 * @return int
	 * @exception WinsockNetBIOSException
	 */
	public final int write(byte[] buf, int off, int len)
		throws WinsockNetBIOSException {
		return Win32NetBIOS.SendSocket(getSocket(), buf, off, len);
	}

	/**
	 * Return the available data length for the socket
	 * 
	 * @return int
	 * @exception WinsockNetBIOSException
	 */
	public final int available()
		throws WinsockNetBIOSException {
		return Win32NetBIOS.ReceiveLengthSocket( getSocket());
	}
	
	/**
	 * Read data from the session socket
	 * 
	 * @param buf byte[]
	 * @param off int
	 * @param maxLen int
	 * @return int
	 * @exception WinsockNetBIOSException
	 */
	public final int read(byte[] buf, int off, int maxLen)
		throws WinsockNetBIOSException {
		return Win32NetBIOS.ReceiveSocket(getSocket(), buf, off, maxLen);
	}

	/**
	 * Accept an incoming session connection and create a session socket for the new session. If the socket is in
	 * blocking mode then it will not return until a connection is received.
	 * 
	 * @return NetBIOSSocket
	 * @exception NetBIOSSocketException
	 * @exception winsockNetBIOSException
	 */
	public final NetBIOSSocket accept()
		throws WinsockNetBIOSException, NetBIOSSocketException {

		// Check if this socket is a listener socket, and the socket is valid

		if ( isListener() == false)
			throw new NetBIOSSocketException("Not a listener type socket");

		if ( hasSocket() == false)
			throw new NetBIOSSocketException("NetBIOS socket not valid");

		// Wait for an incoming session request

		byte[] callerName = new byte[NetBIOSName.NameLength];

		int sessSockPtr = Win32NetBIOS.ListenSocket(getSocket(), callerName);
		if ( sessSockPtr == 0)
			throw new NetBIOSSocketException("NetBIOS socket listen failed");

		// Return the new NetBIOS socket session

		return new NetBIOSSocket(getLana(), sessSockPtr, new NetBIOSName(callerName, 0), false);
	}

	/**
	 * Close the socket
	 */
	public final void closeSocket() {

		// Close the native socket, if valid

		if ( hasSocket()) {
			
			// Check if the socket is registered with a selector, remove from the selector

			if ( m_selector != null) {
				
				// Remove from the selector
				
				try {
					m_selector.deregisterSocket( this);
				}
				catch ( Exception ex) {
				}
				
				// Clear the selector
				
				m_selector = null;
			}
			
			// Close the socket
			
			Win32NetBIOS.CloseSocket(getSocket());
			setSocket(0);
		}
	}

	/**
	 * Check if this socket is in a non-blocking mode
	 * 
	 * @return boolean
	 */
	public final boolean isNonBlocking() {
		return m_nonBlockMode;
	}
	
	/**
	 * Set the socket pointer
	 * 
	 * @param sockPtr int
	 */
	protected final void setSocket(int sockPtr) {
		m_socket = sockPtr;
	}

	/**
	 * Register a non-blocking socket with a selector
	 * 
	 * @param selector NetBIOSSelector
	 * @param ops int
	 * @param attachment Object
	 * @return NetBIOSSelectionKey
	 * @exception IllegalBlockingModeException
	 * @exception IOException
	 */
	public final NetBIOSSelectionKey register( NetBIOSSelector selector, int ops, Object attachment)
		throws IllegalBlockingModeException, IOException {
	
		// Check if the socket is in blocking I/O mode
		
		if ( isNonBlocking() == false)
			throw new IllegalBlockingModeException();
		
		// Check if the selector is valid
		
		if ( selector == null)
			throw new IOException("Null NetBIOS selector");
		
		// Register with the selector
		
		NetBIOSSelectionKey selKey = selector.registerSocket(this, ops);
		if ( selKey != null) {
			selKey.attach( attachment);
			m_selector = selector;
		}
		
		// Return the selection key
		
		return selKey;
	}
	
	/**
	 * Return the NetBIOS socket details as a string
	 * 
	 * @return String
	 */
	public String toString() {

		StringBuffer str = new StringBuffer();

		str.append("[LANA:");
		str.append(getLana());
		str.append(",Name:");
		str.append(getName());
		str.append(",Socket:");
		if ( hasSocket()) {
			str.append("0x");
			str.append(Integer.toHexString(getSocket()));
		}
		else
			str.append("<None>");

		if ( isListener())
			str.append(",Listener");
		
		if ( isNonBlocking())
			str.append(",NonBlocking");

		str.append("]");

		return str.toString();
	}

	/**
	 * Return a hash code for the NetBIOS socket, using the socket id
	 * 
	 *  @return int
	 */
	public int hashCode() {
		return getSocket();
	}
}
