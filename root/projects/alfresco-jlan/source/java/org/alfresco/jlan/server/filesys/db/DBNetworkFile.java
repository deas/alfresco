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

package org.alfresco.jlan.server.filesys.db;

import org.alfresco.jlan.server.filesys.NetworkFile;
import org.alfresco.jlan.server.filesys.cache.FileState;
import org.alfresco.jlan.server.filesys.cache.NetworkFileStateInterface;
import org.alfresco.jlan.server.filesys.loader.FileLoader;

/**
 * Database Network File Class
 *
 * @author gkspencer
 */
public abstract class DBNetworkFile extends NetworkFile implements NetworkFileStateInterface {

	//	File state attributes used/set by the database network file
	
	public final static String DBCacheFile		= "DBCacheFile";
	
	//	File state
	
	private FileState m_state;
	
	//	Associated file loader
	
	private FileLoader m_loader;
	
	//	Owner session id
	
	private String m_ownerSess;
	
	/**
	 * Class constructor
	 * 
	 * @param name String
	 * @param fid int
	 * @param stid int
	 * @param did int
	 */
	public DBNetworkFile(String name, int fid, int stid, int did) {
		super(fid,stid,did);
		setName(name);
		
		//	Set the unique file id using the file and directory ids

		setUniqueId(fid,did);		
	}
	
	/**
	 * Class constructor
	 * 
	 * @param name String
	 * @param fullName String
	 * @param fid int
	 * @param stid int
	 * @param did int
	 */
	public DBNetworkFile(String name, String fullName, int fid, int stid, int did) {
	  super(fid,stid,did);
	  setName(name);
	  setFullName(fullName);
		
		//	Set the unique file id using the file and directory ids

		setUniqueId(fid,did);		
	}

	/**
	 * Get the file status
	 * 
	 * @return int
	 */
	public final int getStatus() {
		if ( m_state != null)
			return m_state.getStatus();
		return -1;
	}

	/**
	 * Determine if the network file has an associated cached file state
	 * 
	 * @return boolean
	 */
	public final boolean hasFileState() {
		return m_state != null ? true : false;
	}
		
	/**
	 * Return the associated caching file state
	 * 
	 * @return FileState
	 */
	public final FileState getFileState() {
		return m_state;
	}
	
	/**
	 * Determine if the network file has an associated file loader
	 * 
	 * @return boolean
	 */
	public final boolean hasLoader() {
		return m_loader != null ? true : false;
	}
	
	/**
	 * Return the associated file loader
	 * 
	 * @return FileLoader
	 */
	public final FileLoader getLoader() {
		return m_loader;
	}

	/**
	 * Determine if the owner session id has been set
	 * 
	 * @return boolean
	 */
	public final boolean hasOwnerSessionId() {
		return m_ownerSess != null ? true : false;
	}

	/**
	 * Return the owner session unique id
	 * 
	 * @return String
	 */
	public final String getOwnerSessionId() {
		return m_ownerSess;
	}

	/**
	 * Set the file details from the file information
	 * 
	 * @param info DBFileInfo
	 */
	public final void setFileDetails(DBFileInfo info) {
		setFileId(info.getFileId());
		setName(info.getFileName());
		setFullName(info.getFullName());
		setDirectoryId(info.getDirectoryId());
		
		setFileSize(info.getSize());
		setAttributes(info.getFileAttributes());
		
		if ( info.getCreationDateTime() != 0L)
			setCreationDate( info.getCreationDateTime());
		
		if ( info.getModifyDateTime() != 0L)
			setModifyDate(info.getModifyDateTime());
	}
		
	/**
	 * Set the file state
	 * 
	 * @param state int
	 */
	public final void setStatus(int state) {
		
		//	Set the file state
		
		if ( m_state != null)
			m_state.setStatus(state);
	}
	
	/**
	 * Set the owner session unique id
	 * 
	 * @param id String
	 */
	public final void setOwnerSessionId(String id) {
		m_ownerSess = id;
	}
	
	/**
	 * Set the associated file state
	 * 
	 * @param state FileState
	 */
	public final void setFileState(FileState state) {
		m_state = state;
	}
	
	/**
	 * Set the associated file loader
	 * 
	 * @param loader FileLoader
	 */
	public final void setLoader(FileLoader loader) {
		m_loader = loader;
	}
}
