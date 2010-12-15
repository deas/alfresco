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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Hashtable;

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.netbios.NetBIOSName;
import org.alfresco.jlan.util.DataBuffer;
import org.alfresco.jlan.util.IPAddress;
import org.alfresco.jlan.util.X64;

/**
 * Win32 NetBIOS Native Call Wrapper Class
 * 
 * @author gkspencer
 */
public class Win32NetBIOS {

	// Constants
	//
	// FIND_NAME_BUFFER structure length

	protected final static int FindNameBufferLen = 33;

	/**
	 * Add a NetBIOS name to the local name table
	 * 
	 * @param lana int
	 * @param name byte[]
	 * @return int
	 */
	public static native int AddName(int lana, byte[] name);

	/**
	 * Add a group NetBIOS name to the local name table
	 * 
	 * @param lana int
	 * @param name byte[]
	 * @return int
	 */
	public static native int AddGroupName(int lana, byte[] name);

	/**
	 * Find a NetBIOS name, return the name buffer
	 * 
	 * @param lana int
	 * @param name byte[]
	 * @param nameBuf byte[]
	 * @param bufLen int
	 * @return int
	 */
	public static native int FindNameRaw(int lana, byte[] name, byte[] nameBuf, int bufLen);

	/**
	 * Find a NetBIOS name
	 * 
	 * @param lana int
	 * @param nbName NetBIOSName
	 * @return int
	 */
	public static int FindName(int lana, NetBIOSName nbName) {

		// Allocate a buffer to receive the name details

		byte[] nameBuf = new byte[nbName.isGroupName() ? 65535 : 4096];

		// Get the raw NetBIOS name data

		int sts = FindNameRaw(lana, nbName.getNetBIOSName(), nameBuf, nameBuf.length);

		if ( sts != NetBIOS.NRC_GoodRet)
			return -sts;

		// Unpack the FIND_NAME_HEADER structure

		DataBuffer buf = new DataBuffer(nameBuf, 0, nameBuf.length);

		int nodeCount = buf.getShort();
		buf.skipBytes(1);
		
		boolean isGroupName = buf.getByte() == 0 ? false : true;
		nbName.setGroup( isGroupName);
		
		// Unpack the FIND_NAME_BUFFER structures

		int curPos = buf.getPosition();

		for (int i = 0; i < nodeCount; i++) {

			// FIND_NAME_BUFFER:
			// UCHAR length
			// UCHAR access_control
			// UCHAR frame_control
			// UCHAR destination_addr[6]
			// UCHAR source_addr[6]
			// UCHAR routing_info[18]

			// Skip to the source_addr field

			buf.skipBytes(9);

			// Source address field format should be 0.0.n.n.n.n for TCP/IP address

			if ( buf.getByte() == 0 && buf.getByte() == 0) {

				// Looks like a TCP/IP format address, unpack it

				byte[] ipAddr = new byte[4];

				ipAddr[0] = (byte) buf.getByte();
				ipAddr[1] = (byte) buf.getByte();
				ipAddr[2] = (byte) buf.getByte();
				ipAddr[3] = (byte) buf.getByte();

				// Add the address to the list of TCP/IP addresses for the NetBIOS name

				nbName.addIPAddress(ipAddr);

				// Skip to the start of the next FIND_NAME_BUFFER structure

				curPos += FindNameBufferLen;
				buf.setPosition(curPos);
			}
		}

		// Return the node count

		return nodeCount;
	}

	/**
	 * Delete a NetBIOS name from the local name table
	 * 
	 * @param lana int
	 * @param name byte[]
	 * @return int
	 */
	public static native int DeleteName(int lana, byte[] name);

	/**
	 * Enumerate the available LANAs
	 * 
	 * @return int[]
	 */
	public static int[] LanaEnumerate() {
		
		// Make sure that there is an active network adapter as making calls to the LanaEnum native
		// call causes problems when there are no active network adapters.

		boolean adapterAvail = false;

		try {

			// Enumerate the available network adapters and check for an active adapter, not
			// including the loopback adapter

			Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();

			while (nis.hasMoreElements() && adapterAvail == false) {

				NetworkInterface ni = nis.nextElement();
				if ( ni.getName().equals("lo") == false) {

					// Make sure the adapter has a valid IP address

					Enumeration<InetAddress> addrs = ni.getInetAddresses();
					if ( addrs.hasMoreElements())
						adapterAvail = true;
				}
			}
		}
		catch (SocketException ex) {
		}

		// Check if there are network adapter(s) available

		if ( adapterAvail == false)
			return null;

		// Call the native code to return the available LANA list

		return LanaEnum();
	}

	/**
	 * Enumerate the available LANAs
	 * 
	 * @return int[]
	 */
	private static native int[] LanaEnum();

	/**
	 * Reset the NetBIOS environment
	 * 
	 * @param lana int
	 * @return int
	 */
	public static native int Reset(int lana);

	/**
	 * Listen for an incoming session request
	 * 
	 * @param lana int
	 * @param toName byte[]
	 * @param fromName byte[]
	 * @param callerName byte[]
	 * @return int
	 */
	public static native int Listen(int lana, byte[] toName, byte[] fromName, byte[] callerName);

	/**
	 * Receive a data packet on a session
	 * 
	 * @param lana int
	 * @param lsn int
	 * @param buf byte[]
	 * @param off int
	 * @param maxLen int
	 * @return int
	 */
	public static native int Receive(int lana, int lsn, byte[] buf, int off, int maxLen);

	/**
	 * Send a data packet on a session
	 * 
	 * @param lana int
	 * @param lsn int
	 * @param buf byte[]
	 * @param off int
	 * @param len int
	 * @return int
	 */
	public static native int Send(int lana, int lsn, byte[] buf, int off, int len);

	/**
	 * Send a datagram to a specified name
	 * 
	 * @param lana int
	 * @param srcNum int
	 * @param destName byte[]
	 * @param buf byte[]
	 * @param off int
	 * @param len int
	 * @return int
	 */
	public static native int SendDatagram(int lana, int srcNum, byte[] destName, byte[] buf, int off, int len);

	/**
	 * Send a broadcast datagram
	 * 
	 * @param lana
	 * @param buf byte[]
	 * @param off int
	 * @param len int
	 * @return int
	 */
	public static native int SendBroadcastDatagram(int lana, byte[] buf, int off, int len);

	/**
	 * Receive a datagram on a specified name
	 * 
	 * @param lana int
	 * @param nameNum int
	 * @param buf byte[]
	 * @param off int
	 * @param maxLen int
	 * @return int
	 */
	public static native int ReceiveDatagram(int lana, int nameNum, byte[] buf, int off, int maxLen);

	/**
	 * Receive a broadcast datagram
	 * 
	 * @param lana int
	 * @param nameNum int
	 * @param buf byte[]
	 * @param off int
	 * @param maxLen int
	 * @return int
	 */
	public static native int ReceiveBroadcastDatagram(int lana, int nameNum, byte[] buf, int off, int maxLen);

	/**
	 * Hangup a session
	 * 
	 * @param lsn int
	 * @return int
	 */
	public static native int Hangup(int lana, int lsn);

	/**
	 * Return the local computers NetBIOS name
	 * 
	 * @return String
	 */
	public static native String GetLocalNetBIOSName();

	/**
	 * Return the local domain name
	 * 
	 * @return String
	 */
	public static native String GetLocalDomainName();

	/**
	 * Return a comma delimeted list of WINS server TCP/IP addresses, or null if no WINS servers are
	 * configured.
	 * 
	 * @return String
	 */
	public static native String getWINSServerList();

	/**
	 * Find the TCP/IP address for a LANA
	 * 
	 * @param lana int
	 * @return String
	 */
	public static final String getIPAddressForLANA(int lana) {

		// Get the local NetBIOS name

		String localName = GetLocalNetBIOSName();
		if ( localName == null)
			return null;

		// Create a NetBIOS name for the local name

		NetBIOSName nbName = new NetBIOSName(localName, NetBIOSName.WorkStation, false);

		// Get the local NetBIOS name details

		int sts = FindName(lana, nbName);

		if ( sts == -NetBIOS.NRC_EnvNotDef) {

			// Reset the LANA then try the name lookup again

			Reset(lana);
			sts = FindName(lana, nbName);
		}

		// Check if the name lookup was successful

		String ipAddr = null;

		if ( sts >= 0) {

			// Get the first IP address from the list

			ipAddr = nbName.getIPAddressString(0);
		}

		// Return the TCP/IP address for the LANA

		return ipAddr;
	}

	/**
	 * Find the adapter name for a LANA
	 * 
	 * @param lana int
	 * @return String
	 */
	public static final String getAdapterNameForLANA(int lana) {

		// Get the TCP/IP address for a LANA

		String ipAddr = getIPAddressForLANA(lana);
		if ( ipAddr == null)
			return null;

		// Get the list of available network adapters

		Hashtable<String, NetworkInterface> adapters = getNetworkAdapterList();
		String adapterName = null;

		if ( adapters != null) {

			// Find the network adapter for the TCP/IP address

			NetworkInterface ni = adapters.get(ipAddr);
			if ( ni != null)
				adapterName = ni.getDisplayName();
		}

		// Return the adapter name for the LANA

		return adapterName;
	}

	/**
	 * Find the LANA for a TCP/IP address
	 * 
	 * @param addr String
	 * @return int
	 */
	public static final int getLANAForIPAddress(String addr) {

		// Check if the address is a numeric TCP/IP address

		if ( IPAddress.isNumericAddress(addr) == false)
			return -1;

		// Get a list of the available NetBIOS LANAs

		int[] lanas = LanaEnum();
		if ( lanas == null || lanas.length == 0)
			return -1;

		// Search for the LANA with the matching TCP/IP address

		for (int i = 0; i < lanas.length; i++) {

			// Get the current LANAs TCP/IP address

			String curAddr = getIPAddressForLANA(lanas[i]);
			if ( curAddr != null && curAddr.equals(addr))
				return lanas[i];
		}

		// Failed to find the LANA for the specified TCP/IP address

		return -1;
	}

	/**
	 * Find the LANA for a network adapter
	 * 
	 * @param name String
	 * @return int
	 */
	public static final int getLANAForAdapterName(String name) {

		// Get the list of available network adapters

		Hashtable<String, NetworkInterface> niList = getNetworkAdapterList();

		// Search for the address of the specified network adapter

		Enumeration<String> niEnum = niList.keys();

		while (niEnum.hasMoreElements()) {

			// Get the current TCP/IP address

			String ipAddr = niEnum.nextElement();
			NetworkInterface ni = (NetworkInterface) niList.get(ipAddr);

			if ( ni.getName().equalsIgnoreCase(name)) {

				// Return the LANA for the network adapters TCP/IP address

				return getLANAForIPAddress(ipAddr);
			}
		}

		// Failed to find matching network adapter

		return -1;
	}

	/**
	 * Return a hashtable of NetworkInterfaces indexed by TCP/IP address
	 * 
	 * @return Hashtable<String, NetworkInterface>
	 */
	private static final Hashtable<String, NetworkInterface> getNetworkAdapterList() {

		// Get a list of the local network adapters

		Hashtable<String, NetworkInterface> niList = new Hashtable<String, NetworkInterface>();

		try {

			// Enumerate the available network adapters

			Enumeration<NetworkInterface> niEnum = NetworkInterface.getNetworkInterfaces();

			while (niEnum.hasMoreElements()) {

				// Get the current network interface details

				NetworkInterface ni = (NetworkInterface) niEnum.nextElement();
				Enumeration<InetAddress> addrEnum = ni.getInetAddresses();

				while (addrEnum.hasMoreElements()) {

					// Get the address and add the adapter to the list indexed via the numeric IP
					// address string

					InetAddress addr = addrEnum.nextElement();
					niList.put(addr.getHostAddress(), ni);
				}
			}
		}
		catch (Exception ex) {
		}

		// Return the network adapter list

		return niList;
	}

	// ---------- Winsock based NetBIOS interface ----------//

	/**
	 * Initialize the NetBIOS socket interface
	 * 
	 * @exception WinsockNetBIOSException If a Winsock error occurs
	 */
	protected static native void InitializeSockets()
		throws WinsockNetBIOSException;

	/**
	 * Shutdown the NetBIOS socket interface
	 */
	protected static native void ShutdownSockets();

	/**
	 * Create a NetBIOS socket
	 * 
	 * @param lana int
	 * @return int
	 * @exception WinsockNetBIOSException If a Winsock error occurs
	 */
	protected static native int CreateSocket(int lana)
		throws WinsockNetBIOSException;

	/**
	 * Bind a NetBIOS socket to a name to listen for incoming sessions
	 * 
	 * @param sockPtr int
	 * @param name byte[]
	 * @exception WinsockNetBIOSException If a Winsock error occurs
	 */
	protected static int BindSocket(int sockPtr, byte[] name)
		throws WinsockNetBIOSException
	{
		return BindSocket( sockPtr, name, false);
	}

	/**
	 * Bind a NetBIOS socket to a name to listen for incoming sessions
	 * 
	 * @param sockPtr int
	 * @param name byte[]
	 * @param fastAddName boolean
	 * @exception WinsockNetBIOSException If a Winsock error occurs
	 */
	protected static native int BindSocket(int sockPtr, byte[] name, boolean fastAddName)
		throws WinsockNetBIOSException;

	/**
	 * Listen for an incoming connection
	 * 
	 * @param sockPtr int
	 * @param callerName byte[]
	 * @return int
	 * @exception WinsockNetBIOSException If a Winsock error occurs
	 */
	protected static native int ListenSocket(int sockPtr, byte[] callerName)
		throws WinsockNetBIOSException;

	/**
	 * Connect to a remote server
	 * 
	 * @param sockPtr int
	 * @param remoteName byte[]
	 * @exception WinsockNetBIOSException If a Winsock error occurs
	 */
	protected static native void ConnectSocket(int sockPtr, byte[] remoteName)
		throws WinsockNetBIOSException;
	
	/**
	 * Close a NetBIOS socket
	 * 
	 * @param sockPtr int
	 */
	protected static native void CloseSocket(int sockPtr);

	/**
	 * Send data on a session socket
	 * 
	 * @param sockPtr int
	 * @param buf byte[]
	 * @param off int
	 * @param len int
	 * @return int
	 * @exception WinsockNetBIOSException If a Winsock error occurs
	 */
	protected static native int SendSocket(int sockPtr, byte[] buf, int off, int len)
		throws WinsockNetBIOSException;

	/**
	 * Receive data on a session socket
	 * 
	 * @param sockPtr int
	 * @param buf byte[]
	 * @param off int
	 * @param maxLen int
	 * @return int
	 * @exception WinsockNetBIOSException If a Winsock error occurs
	 */
	protected static native int ReceiveSocket(int sockPtr, byte[] buf, int off, int maxLen)
		throws WinsockNetBIOSException;

	/**
	 * Return the amount of receive data available on the socket
	 * 
	 * @param sockPtr int
	 * @return int
	 * @exception WinsockNetBIOSException If a Winsock error occurs
	 */
	protected static native int ReceiveLengthSocket(int sockPtr)
		throws WinsockNetBIOSException;
	
	/**
	 * Configure a socket to be non-blocking
	 * 
	 * @param sockPtr int
	 * @param nonBlocking boolean
	 * @exception WinsockNetBIOSException If a Winsock error occurs
	 */
	protected static native int SetNonBlockingSocket(int sockPtr, boolean nonBlocking)
		throws WinsockNetBIOSException;

	/**
	 * Wait for an event on one or more sockets
	 * 
	 * @param sockCnt int
	 * @param readSocksIn int[]
	 * @param readSocksOut int[]
	 * @return int Count of sockets have triggered events
	 * @exception WinsockNetBIOSException If a Winsock error occurs
	 */
	protected static native int SelectReceiveSockets( int sockCnt, int[] readSocksIn, int[] readSocksOut)
		throws WinsockNetBIOSException;
	
	/**
	 * Return the maximum number of sockets that can be configured per SelectSockets() call
	 * 
	 * @return int
	 */
	protected static native int GetMaximumSocketsPerSelect();

	/**
	 * Create a Win32 event
	 * 
	 * @return int
	 * @throws Exception
	 */
	public static native int Win32CreateEvent()
		throws Exception;
	
	/**
	 * Close a Win32 event
	 * 
	 * @param eventHandle int
	 * @throws Exception
	 */
	public static native void Win32CloseEvent( int eventHandle)
		throws Exception;
	
	/**
	 * Set a Win32 event
	 * 
	 * @param eventHandle int
	 * @return boolean
	 */
	public static native boolean Win32SetEvent( int eventHandle);
	
	/**
	 * Reset a Win32 event
	 * 
	 *  @param eventHandle int
	 *  @return boolean
	 */
	public static native boolean Win32ResetEvent( int eventHandle);
	
	/**
	 * Create a Winsock event
	 * 
	 * @return int
	 * @throws WinsockNetBIOSException
	 */
	public static native int WinsockCreateEvent()
		throws WinsockNetBIOSException;
	
	/**
	 * Set a Winsock event
	 * 
	 * @param eventHandle int
	 * @return boolean
	 */
	public static native boolean WinsockSetEvent( int eventHandle);
	
	/**
	 * Reset a Winsock event
	 * 
	 * @param eventHandle int
	 * @return boolean
	 */
	public static native boolean WinsockResetEvent( int eventHandle);

	/**
	 * Close a Winsock event
	 * 
	 *  @param eventHandle int
	 *  @exception WinsockNetBIOSException
	 */
	public static native void WinsockCloseEvent( int eventHandle)
		throws WinsockNetBIOSException;

	/**
	 * Wait for Winsock events
	 * 
	 * @param eventCnt int
	 * @param events int[]
	 * @param waitAll boolean
	 * @param timeout int
	 * @param alertable boolean
	 * @return int
	 * @throws WinsockNetBIOSException
	 */
	public static native int WinsockWaitForMultipleEvents( int eventCnt, int[] events, boolean waitAll, int timeout, boolean alertable)
		throws WinsockNetBIOSException;
	
	/**
	 * Set Winsock events for a socket
	 * 
	 * @param sockPtr int
	 * @param eventHandle int
	 * @param eventMask int
	 * @throws WinsockNetBIOSException
	 */
	public static native void WinsockEventSelect( int sockPtr, int eventHandle, int eventMask)
		throws WinsockNetBIOSException;
	
	/**
	 * Get the list of socket events that have triggered for a particular socket
	 * 
	 * @param sockPtr int
	 * @param eventHandle int
	 * @return int
	 * @throws WinsockNetBIOSException
	 */
	public static native int WinsockEnumNetworkEvents( int sockPtr, int eventHandle)
		throws WinsockNetBIOSException;

	/**
	 * Get the current receive buffer size for the socket
	 * 
	 * @param sockPtr int
	 * @return int
	 * @throws winsockNetBIOSException
	 */
	public static native int getSocketReceiveBufferSize( int sockPtr)
		throws WinsockNetBIOSException;
	
	/**
	 * Set the socket receive buffer size
	 * 
	 * @param sockPtr int
	 * @param bufSize int
	 * @throws WinsockNetBIOSException
	 */
	public static native void setSocketReceiveBufferSize( int sockPtr, int bufSize)
		throws WinsockNetBIOSException;
	
	/**
	 * Get the current send buffer size for the socket
	 * 
	 * @param sockPtr int
	 * @return int
	 * @throws winsockNetBIOSException
	 */
	public static native int getSocketSendBufferSize( int sockPtr)
		throws WinsockNetBIOSException;
	
	/**
	 * Set the socket send buffer size
	 * 
	 * @param sockPtr int
	 * @param bufSize int
	 * @throws WinsockNetBIOSException
	 */
	public static native void setSocketSendBufferSize( int sockPtr, int bufSize)
		throws WinsockNetBIOSException;
	
	/**
	 * Wait for a network address change event, block until a change occurs or the Winsock NetBIOS
	 * interface is shut down
	 */
	public static native void waitForNetworkAddressChange();

	/**
	 * Static initializer used to load the native code library
	 */
	static {

		// Check if we are running under 64 bit Windows

		String dllName = "Win32NetBIOS";

		if ( X64.isWindows64())
			dllName = "Win32NetBIOSx64";

		// Load the Win32 NetBIOS interface library

		try {
			System.loadLibrary(dllName);
		}
		catch (Throwable ex) {
			Debug.println( ex);
		}
	}
}
