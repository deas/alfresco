/*
 * Copyright (C) 2006-2011 Alfresco Software Limited.
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

package org.alfresco.jlan.server.filesys.cache;

import org.alfresco.jlan.server.filesys.FileAccessToken;

/**
 * Local File Access Token Class
 *
 * @author gkspencer
 */
public class LocalFileAccessToken implements FileAccessToken {

	// Use the request process id
	
	private int m_pid;
	
	/**
	 * Class constructor
	 * 
	 * @param pid int
	 */
	public LocalFileAccessToken( int pid) {
		m_pid = pid;
	}
	
	/**
	 * Return the process id
	 * 
	 * @return int
	 */
	public final int getProcessId() {
		return m_pid;
	}
	
	/**
	 * Return the access token as a string
	 * 
	 * @return String
	 */
	public String toString() {
		StringBuilder str = new StringBuilder();
		
		str.append( "[Token pid=");
		str.append( getProcessId());
		str.append( "]");
		
		return str.toString();
	}
}
