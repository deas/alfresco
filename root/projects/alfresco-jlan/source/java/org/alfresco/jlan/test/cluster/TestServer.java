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

package org.alfresco.jlan.test.cluster;

/**
 * Test Server Class
 *
 * @author gkspencer
 */
public class TestServer {

	// Server name
	
	private String m_name;
	
	// CIFS username and password
	
	private String m_userName;
	private String m_password;
	
	// Share to run tests on
	
	private String m_share;
	
	/**
	 * Class constructor
	 * 
	 * @param name String
	 * @param user String
	 * @param pass String
	 * @param share String
	 */
	public TestServer(String name, String user, String pass, String share) {
		m_name     = name;
		m_userName = user;
		m_password = pass;
		m_share    = share;
	}
	
	/**
	 * Return the server name
	 * 
	 * @return String
	 */
	public final String getName() {
		return m_name;
	}
	
	/**
	 * Return the user name
	 * 
	 * @return String
	 */
	public final String getUserName() {
		return m_userName;
	}
	
	/**
	 * Return the password
	 * 
	 * @return String
	 */
	public final String getPassword() {
		return m_password;
	}
	
	/**
	 * Return the share name
	 * 
	 * @return String
	 */
	public final String getShareName() {
		return m_share;
	}
	
	/**
	 * Return the test server details as a string
	 * 
	 * @return String
	 */
	public String toString() {
		StringBuilder str = new StringBuilder();
		
		str.append("[");
		str.append( getName());
		str.append(",username=");
		str.append( getUserName());
		str.append(",password=");
		str.append( getPassword());
		str.append(",share=");
		str.append( getShareName());
		str.append("]");
		
		return str.toString();
	}
}
