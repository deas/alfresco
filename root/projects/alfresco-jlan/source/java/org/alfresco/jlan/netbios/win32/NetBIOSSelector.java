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
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 * NetBIOS Selector Class
 * 
 * <p>Selector type class for NetBIOSSocket objects. Groups a set of sockets to wait for events on.
 * 
 * @author gkspencer
 */
public class NetBIOSSelector {

	// Constrants
	//
	// Initial allocation size for the socket id array
	
	private static final int DefaultSockArraySize	= 64;
	
	// Array of socket ids
	
	private int[] m_sockIds;
	private int[] m_eventIds;
	
	// Hash table to link socket ids to selection keys
	
	private Hashtable<Integer, NetBIOSSelectionKey> m_selectionKeys;
	private Set<Integer> m_keySet;
	
	// Current list of sockets that have triggered events
	
	private Set<NetBIOSSelectionKey> m_triggeredKeys;
	
	/**
	 * Default constructor
	 */
	public NetBIOSSelector() {
		m_sockIds = new int[DefaultSockArraySize];
		m_selectionKeys = new Hashtable<Integer, NetBIOSSelectionKey>();
		m_keySet = m_selectionKeys.keySet();
		
		m_triggeredKeys = new HashSet<NetBIOSSelectionKey>();
	}
	
	/**
	 * Class constructor
	 * 
	 * @param initSize int
	 */
	public NetBIOSSelector(int initSize) {
		m_sockIds = new int[initSize];
		m_selectionKeys = new Hashtable<Integer, NetBIOSSelectionKey>();
		m_keySet = m_selectionKeys.keySet();
		
		m_triggeredKeys = new HashSet<NetBIOSSelectionKey>();
	}
	
	/**
	 * Register a socket with this selector
	 * 
	 * @param socket NetBIOSSocket
	 * @param ops int
	 * @return NetBIOSSelectionKey
	 * @exception IOException
	 * @exception IllegalblockingModeException
	 */
	protected final NetBIOSSelectionKey registerSocket(NetBIOSSocket socket, int ops)
		throws IOException, IllegalBlockingModeException {

		NetBIOSSelectionKey selKey = null;
		
		synchronized ( m_selectionKeys) {
			
			// Check if the socket is already registered with this selector
			
			Integer key = new Integer( socket.getSocket());
			if ( m_selectionKeys.containsKey( key))
				throw new IOException( "Socket already registered with selector");
			
			// Check if hte socket is in non-blocking mode
			
			if ( socket.isNonBlocking() == false)
				throw new IllegalBlockingModeException();
	
			// Create the selection key
			
			selKey = new NetBIOSSelectionKey( this, socket, ops, null);
			m_selectionKeys.put( key, selKey);
		}
		
		// Return the new selection key
		
		return selKey;
	}
	
	/**
	 * Remove a socket from this selector
	 * 
	 * @param socket NetBIOSSocket
	 * @return NetBIOSSelectionKey
	 */
	public final NetBIOSSelectionKey deregisterSocket( NetBIOSSocket socket)
		throws IOException {
		
		// Remove the selection key for the specified socket
		
		NetBIOSSelectionKey selKey = m_selectionKeys.remove( new Integer( socket.getSocket()));
		
		// Remove from the triggered set
		
		if ( selKey != null)
			m_triggeredKeys.remove( selKey);
		
		// Return the removed selection key, or null if not found
		
		return selKey;
	}
	
	/**
	 * Wait for events to trigger on one or more sockets
	 * 
	 * @return int
	 * @exception WinsockNetBIOSException
	 */
	public final int select()
		throws WinsockNetBIOSException {

		int idx = 0;
		
		synchronized ( m_selectionKeys) {
			
			// Build the array of socket ids to listen for events on
			
			if ( m_sockIds.length < m_selectionKeys.size())
				m_sockIds = new int[m_selectionKeys.size()];
			
			// Copy sockets that are interested in accept or read events to the socket id list
			
			Iterator<Integer> keys = m_keySet.iterator();
			
			while ( keys.hasNext()) {
				
				// Get the current selection key and check if is interested in accept or read events
				
				Integer curKey = keys.next();
				NetBIOSSelectionKey selKey = m_selectionKeys.get( curKey);
				
				if ( m_triggeredKeys.contains( selKey) == false &&
					( selKey.interestOps() & NetBIOSSelectionKey.OP_ACCEPT + NetBIOSSelectionKey.OP_READ) != 0) {
				
					// Add the socket id to the list
					
					m_sockIds[idx++] = selKey.socket().getSocket();
				}
			}
		}
		
		// Check if any sockets were added to the list
		
		if ( idx == 0)
			return 0;

		// Allocate the array to hold the socket ids of the triggered sockets
		
		if ( m_eventIds == null || m_eventIds.length != m_sockIds.length)
			m_eventIds = new int[m_sockIds.length];
		
		// Clear the current list of triggered sockets
		
		m_triggeredKeys.clear();
		
		// Wait for one or more sockets to generate an event
		
		int eventCnt = Win32NetBIOS.SelectReceiveSockets( idx, m_sockIds, m_eventIds);
		
		if ( eventCnt > 0) {
			
			// Add triggered socket selection keys to the triggered list
			
			for ( int i = 0; i < eventCnt; i++) {
				
				// Get the selection key for the triggered socket, add to the triggered set
				
				NetBIOSSelectionKey selKey = m_selectionKeys.get( new Integer( m_eventIds[ i]));
				if ( selKey != null) {
					selKey.setTriggers( selKey.socket().isListener() ? NetBIOSSelectionKey.OP_ACCEPT : NetBIOSSelectionKey.OP_READ);
				
					m_triggeredKeys.add( selKey);
				}
			}
		}
		
		// Return the count of triggered sockets
		
		return eventCnt;
	}
	
	/**
	 * Close the NetBIOS selector
	 */
	public final void close() {

		// Clear the selection keys
		
		m_selectionKeys.clear();
		m_triggeredKeys.clear();
	}
	
	/**
	 * Return the full selection key list
	 * 
	 * @return Set<Integer>
	 */
	public final Set<Integer> keys() {
		return m_keySet;
	}
	
	/**
	 * Return the selection key for the specified key
	 * 
	 * @param key Integer
	 * @return NetBIOSSelectionKey
	 */
	public final NetBIOSSelectionKey getSelectionKey( Integer key) {
		return m_selectionKeys.get( key);
	}
	/**
	 * Return the selected keys from the last select
	 * 
	 * @return Set<NetBIOSSelectionKey>
	 */
	public final Set<NetBIOSSelectionKey> selectedKeys() {
		return m_triggeredKeys;
	}
}
