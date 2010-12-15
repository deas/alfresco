/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
package org.alfresco.jlan.smb.server;

import org.alfresco.jlan.server.config.InvalidConfigurationException;

/**
 * CIFS Connections Handler Interface
 * 
 * @author gkspencer
 */
public interface CifsConnectionsHandler {

	/**
	 * Initialize the connections handler
	 * 
	 * @param srv SMBServer
	 * @param config CIFSConfigSection
	 * @exception InvalidConfigurationException
	 */
	public void initializeHandler( SMBServer srv, CIFSConfigSection config)
		throws InvalidConfigurationException;
	
	/**
	 * Start the connection handler thread
	 */
	public void startHandler();

	/**
	 * Stop the connections handler
	 */
	public void stopHandler();
	
	/**
	 *  Return the count of active session handlers
	 *  
	 *  @return int
	 */
	public int numberOfSessionHandlers();
}
