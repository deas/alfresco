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

package org.alfresco.jlan.server.filesys.loader;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.alfresco.jlan.server.SrvSession;
import org.alfresco.jlan.server.core.DeviceContext;
import org.alfresco.jlan.server.filesys.FileOpenParams;
import org.alfresco.jlan.server.filesys.NetworkFile;
import org.alfresco.jlan.server.filesys.db.DBDeviceContext;
import org.springframework.extensions.config.ConfigElement;


/**
 * File Loader Interface
 * 
 * <p>A file loader is responsible for loading, storing and deleting the data associated with a file in a
 * virtual filesystem. A file is identified using a unique file id.
 *
 * @author gkspencer
 */
public interface FileLoader {

  /**
   * Return the database features required by this file loader. Return zero if no database features
   * are required by the loader.
   * 
   * @return int
   */
  public int getRequiredDBFeatures();
  
	/**
	 * Create a network file for the specified file
	 * 
	 * @param params FileOpenParams
	 * @param fid int
	 * @param stid int
	 * @param did int
	 * @param create boolean
	 * @param dir boolean
	 * @exception IOException
	 * @exception FileNotFoundException
	 */
	public NetworkFile openFile(FileOpenParams params, int fid, int stid, int did, boolean create, boolean dir)
		throws IOException, FileNotFoundException;

	/**
	 * Close the network file
	 * 
   * @param sess SrvSession
	 * @param netFile NetworkFile
	 * @exception IOException
	 */
	public void closeFile(SrvSession sess, NetworkFile netFile)
		throws IOException;
				
	/**
	 * Delete the specified file data
	 * 
	 * @param fname String
	 * @param fid int
	 * @param stid int
	 * @exception IOException
	 */
	public void deleteFile(String fname, int fid, int stid)
		throws IOException;

	/**
	 * Request file data to be loaded or saved
	 * 
	 * @param fileReq FileRequest
	 */
	public void queueFileRequest(FileRequest fileReq);
	
	/**
	 * Initialize the file loader using the specified parameters
	 * 
	 * @param params ConfigElement
	 * @param ctx DeviceContext
	 * @exception FileLoaderException
	 * @exception IOException
	 */
	public void initializeLoader(ConfigElement params, DeviceContext ctx)
		throws FileLoaderException, IOException;
		
	/**
	 * Start the file loader
	 * 
	 * @param ctx DeviceContext
	 */
	public void startLoader( DeviceContext ctx);
	
	/**
	 * Shutdown the file loader and release all resources
	 * 
	 * @param immediate boolean
	 */
	public void shutdownLoader(boolean immediate);
	
	/**
	 * Determine if the file loader supports NTFS streams
	 * 
	 * @return boolean
	 */
	public boolean supportsStreams();
	
	/**
	 * Add a file processor to process files before storing and after loading.
	 * 
	 * @param fileProc FileProcessor
	 * @exception FileLoaderException
	 */
	public void addFileProcessor(FileProcessor fileProc)
		throws FileLoaderException;
	
	/**
	 * Set the database context, used before the loader is fully initialized so that it can
	 * query the database interface if required.
	 * 
	 * @param dbCtx DBDeviceContext
	 */
	public void setContext(DBDeviceContext dbCtx);
}
