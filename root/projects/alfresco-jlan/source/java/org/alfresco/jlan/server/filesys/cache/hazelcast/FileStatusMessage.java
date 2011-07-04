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

package org.alfresco.jlan.server.filesys.cache.hazelcast;

import org.alfresco.jlan.server.filesys.FileStatus;

/**
 * File Status Update Message Class
 * 
 * <p>Contains the details of a file status update.
 *
 * @author gkspencer
 */
public class FileStatusMessage extends ClusterMessage {

	// Serialization id
	
	private static final long serialVersionUID = 1L;
	
	// Updated path and file status
	
	private String m_path;
	private int m_fileStatus;
	
	/**
	 * Default constructor
	 */
	public FileStatusMessage() {
	}
	
	/**
	 * Class constructor
	 * 
	 * @param targetNode String
	 * @param path String
	 * @parma fileSts int
	 */
	public FileStatusMessage( String targetNode, String path, int fileSts) {
		super ( targetNode, ClusterMessageType.FileStateUpdate);
		m_path = path;
		m_fileStatus = fileSts;
	}
	
	/**
	 * Return the normalized path of the file/folder
	 * 
	 * @return String
	 */
	public final String getPath() {
		return m_path;
	}

	/**
	 * Return the new file status
	 * 
	 * @return int
	 */
	public final int getFileStatus() {
		return m_fileStatus;
	}
	
	/**
	 * Return the file status message as a string
	 * 
	 * @return String
	 */
	public String toString() {
		StringBuilder str = new StringBuilder();
		
		str.append( "[");
		str.append( super.toString());
		str.append( ",path=");
		str.append( getPath());
		str.append( ",fileSts=");
		str.append( FileStatus.asString( getFileStatus()));
		str.append( "]");
		
		return str.toString();
	}

}
