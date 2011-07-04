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

package org.alfresco.jlan.client;

import java.net.*;
import java.util.*;

import org.alfresco.jlan.client.admin.AdminSession;
import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.netbios.NetBIOSName;
import org.alfresco.jlan.netbios.NetBIOSPacket;
import org.alfresco.jlan.netbios.NetBIOSSession;
import org.alfresco.jlan.netbios.RFCNetBIOSProtocol;
import org.alfresco.jlan.smb.Dialect;
import org.alfresco.jlan.smb.PCShare;
import org.alfresco.jlan.smb.dcerpc.info.ServerInfo;
import org.alfresco.jlan.util.StringList;

/**
 * The network discovery thread attempts to locate a browse master, determine the local domain name
 * and find nodes on the local network.
 * 
 * @author gkspencer
 */
class NetworkDiscovery extends Thread {

	// RFC NetBIOS name service datagram socket

	private static DatagramSocket m_dgramSock = null;

	// Subnet mask, required for broadcast name lookup requests

	private static String m_subnetMask = null;

	// Local domain name/browse master

	private String m_domainName;
	private String m_browseMaster;

	// List of known servers on the network

	private Vector m_nodeList;

	// Debug enable flag

	private boolean m_debug;

	/**
	 * NetworkDiscovery constructor comment.
	 */
	public NetworkDiscovery() {
		super();

		// Set the thread to be a daemon type thread

		setDaemon(true);

		// Allocate the node list

		m_nodeList = new Vector();
	}

	/**
	 * Return the browse master node name, if known.
	 * 
	 * @return java.lang.String
	 */
	String getBrowseMaster() {
		return m_browseMaster;
	}

	/**
	 * Return the local domain name string, if known.
	 * 
	 * @return java.lang.String
	 */
	String getDomainName() {
		return m_domainName;
	}

	/**
	 * Return the list of known nodes.
	 * 
	 * @return java.util.Vector
	 */
	Vector getNodeList() {
		return m_nodeList;
	}

	/**
	 * Return the subnet mask string.
	 * 
	 * @return java.lang.String
	 */
	String getSubnetMask() {
		return m_subnetMask;
	}

	/**
	 * Return the debug flag state.
	 * 
	 * @return boolean
	 */
	boolean hasDebug() {
		return m_debug;
	}

	/**
	 * Network discovery class test code.
	 * 
	 * @param args java.lang.String[]
	 */
	public static void main(String[] args) {

		// Output a startup banner

		System.out.println("NetworkDiscovery Test");

		SessionFactory.enableDialect(Dialect.NT);
		// SMBSession.setDebug(0xFFFF);

		// Create a network discovery thread and start it

		NetworkDiscovery netDiscover = new NetworkDiscovery();
		// netDiscover.setDebug(true);
		netDiscover.start();

		// Loop forever

		while (true) {

			// Check if the network discovery thread has found any nodes

			if ( netDiscover.getNodeList().size() > 0) {

				// Display the known node list

				Vector nodeList = netDiscover.getNodeList();
				System.out.println("Known node list :-");

				for (int i = 0; i < nodeList.size(); i++)
					System.out.println(" " + nodeList.elementAt(i));
			}

			// Sleep for a while

			try {
				sleep(2000);
			}
			catch (java.lang.InterruptedException ex) {
				System.out.println(ex.toString());
			}
		}
	}

	/**
	 * Main network discovery thread code.
	 */
	public void run() {

		// Debug mode

		if ( hasDebug())
			Debug.println("NetworkDiscovery: run () called");

		// Create the datagram socket

		try {

			// Create a datagram socket

			m_dgramSock = new DatagramSocket();
			m_dgramSock.setSoTimeout(3000);
		}
		catch (java.net.SocketException ex) {
			Debug.println("NetworkDiscovery: " + ex.toString());
			return;
		}

		// Check if the subnet mask string is set, if not then try and generate a valid
		// subnet mask.

		if ( m_subnetMask == null) {
			try {
				m_subnetMask = NetBIOSSession.GenerateSubnetMask(null);
			}
			catch (java.net.UnknownHostException ex) {
				Debug.println("NetworkDiscovery: " + ex.toString());
				return;
			}
		}

		// Build the browse master NetBIOS name string

		StringBuffer browseName = new StringBuffer();

		browseName.append((char) 0x01);
		browseName.append((char) 0x02);
		browseName.append("__MSBROWSE__");
		browseName.append((char) 0x02);

		String browseStr = browseName.toString();

		// Create a NetBIOS broadcast packet to find the browse master

		NetBIOSPacket pkt = new NetBIOSPacket();

		pkt.setOpcode(NetBIOSPacket.NAME_QUERY);
		pkt.setFlags(NetBIOSPacket.FLG_BROADCAST);
		pkt.setQuestionCount(1);
		pkt.setQuestionName(browseStr, NetBIOSName.BrowseMasterGroup, NetBIOSPacket.NAME_TYPE_NB, NetBIOSPacket.NAME_CLASS_IN);

		// Build a broadcast destination address

		InetAddress destAddr = null;
		try {
			destAddr = InetAddress.getByName(getSubnetMask());
		}
		catch (java.net.UnknownHostException ex) {
			Debug.println("NetworkDiscovery: " + ex.toString());
			return;
		}

		// Allocate a datagram packet to send the name query

		DatagramPacket dgram = new DatagramPacket(pkt.getBuffer(), pkt.getLength(), destAddr, RFCNetBIOSProtocol.NAME_PORT);

		// Allocate the receive buffer/datagram packet

		byte[] rxbuf = new byte[256];
		DatagramPacket rxdgram = new DatagramPacket(rxbuf, rxbuf.length);

		// Create a NetBIOS packet using the receive buffer

		NetBIOSPacket rxpkt = new NetBIOSPacket(rxbuf);

		// Loop until the browse master and local domain name have been found

		while (m_browseMaster == null || m_domainName == null) {

			// Try a name lookup request

			try {

				// Debug mode

				if ( hasDebug()) {
					Debug.println("NetworkDiscovery: Send name lookup request ...");
					pkt.DumpPacket(false);
				}

				// Send the name lookup datagram

				m_dgramSock.send(dgram);

				// Receive datagrams

				boolean rxOK = false;

				do {

					// Receive a datagram packet

					m_dgramSock.receive(rxdgram);

					// Debug mode

					if ( hasDebug()) {

						// Dump the packet details

						Debug.println("NetworkDiscovery: Rx Datagram");
						rxpkt.DumpPacket(false);
					}

					// Check if the packet is a valid response

					if ( rxpkt.isResponse() && rxpkt.getOpcode() == NetBIOSPacket.RESP_QUERY)
						rxOK = true;

				} while (rxOK == false);

				// Check if a valid packet was received

				if ( rxOK == true) {

					// Set the browse master node name

					m_browseMaster = rxdgram.getAddress().getHostName();

					// Debug mode

					if ( hasDebug())
						Debug.println("NetworkDiscovery: Found browse master " + m_browseMaster);
				}

				// Connect to the browse master and get a list of nodes

				PCShare shr = new PCShare(m_browseMaster, "IPC$", "guest", "");
				AdminSession admSess = SessionFactory.OpenAdminSession(shr);

				StringList srvList = admSess.getServerNames(0x0000FFFF);

				if ( srvList != null) {
					for (int i = 0; i < srvList.numberOfStrings(); i++)
						if ( m_nodeList.contains(srvList.getStringAt(i)) == false)
							m_nodeList.addElement(srvList.getStringAt(i));
				}
			}
			catch (Exception ex) {
				if ( hasDebug())
					Debug.println("Error: " + ex.toString());
			}

			// Check if we found a browse master

			if ( m_browseMaster == null) {

				// Check the local node

				try {

					// Try a NetBIOS name lookup for the current workgroup name

					StringList nameList = NetBIOSSession.FindNameList("STARLASOFT", NetBIOSName.WorkStation, 2000);

					if ( nameList != null) {
						for (int i = 0; i < nameList.numberOfStrings(); i++) {
							if ( m_nodeList.contains(nameList.getStringAt(i)) == false)
								m_nodeList.addElement(nameList.getStringAt(i));
						}
					}

					// Open a session to the IPC$ pipe on the local node

					PCShare shr = new PCShare(InetAddress.getLocalHost().getHostName(), "IPC$", "", "");
					AdminSession admSess = SessionFactory.OpenAdminSession(shr);

					// Get the local server information

					ServerInfo srvInfo = admSess.getServerInfo();
					if ( srvInfo != null && m_nodeList.contains(srvInfo.getServerName()) == false)
						m_nodeList.addElement(srvInfo.getServerName());

					// Close the admin session

					admSess.CloseSession();
				}
				catch (Exception ex) {
					if ( hasDebug())
						Debug.println("LocalHost Error: " + ex.toString());
				}
			}

			// Sleep for a while

			try {
				sleep(5000);
			}
			catch (java.lang.InterruptedException ex) {
			}
		}
	}

	/**
	 * Enable/disable debug messages.
	 * 
	 * @param dbg boolean
	 */
	void setDebug(boolean dbg) {
		m_debug = dbg;
	}

	/**
	 * Set the subnet mask string.
	 * 
	 * @param subnet java.lang.String
	 */
	void setSubnetMask(String subnet) {
		m_subnetMask = subnet;
	}
}