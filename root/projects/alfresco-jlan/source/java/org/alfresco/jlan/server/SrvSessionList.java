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

package org.alfresco.jlan.server;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Server Session List Class
 *
 * @author gkspencer
 */
public class SrvSessionList {

	//	Session list
	
	private Hashtable<Integer, SrvSession> m_sessions;
	
	/**
	 * Class constructor
	 */
	public SrvSessionList() {
	  m_sessions = new Hashtable<Integer, SrvSession>();
	}
	
	/**
	 * Return the number of sessions in the list
	 * 
	 * @return int
	 */
	public final int numberOfSessions() {
	  return m_sessions.size();
	}
	
	/**
	 * Add a session to the list
	 * 
	 * @param sess SrvSession
	 */
	public final void addSession(SrvSession sess) {
	  m_sessions.put(new Integer(sess.getSessionId()), sess);
	}
	
	/**
	 * Find the session using the unique session id
	 * 
	 * @param id int
	 * @return SrvSession
	 */
	public final SrvSession findSession(int id) {
	  return findSession(new Integer(id));
	}
	
	/**
	 * Find the session using the unique session id
	 * 
	 * @param id Integer
	 * @return SrvSession
	 */
	public final SrvSession findSession(Integer id) {
	  return m_sessions.get(id);
	}
	
	/**
	 * Remove a session from the list
	 * 
	 * @param id int
	 * @return SrvSession
	 */
	public final SrvSession removeSession(int id) {
	  return removeSession(new Integer(id));
	}
	
	/**
	 * Remove a session from the list
	 * 
	 * @param sess SrvSession
	 * @return SrvSession
	 */
	public final SrvSession removeSession(SrvSession sess) {
	  return removeSession(sess.getSessionId());
	}
	
	/**
	 * Remove a session from the list
	 * 
	 * @param id Integer
	 * @return SrvSession
	 */
	public final SrvSession removeSession(Integer id) {
	  
	  //	Find the required session
	  
	  SrvSession sess = findSession(id);
	  
	  //	Remove the session and return the removed session
	  
	  m_sessions.remove(id);
	  return sess;
	}
	
	/**
	 * Enumerate the session ids
	 * 
	 * @return Enumeration<Integer>
	 */
	public final Enumeration<Integer> enumerate() {
	  return m_sessions.keys();
	}
}
