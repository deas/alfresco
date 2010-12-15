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

package org.alfresco.deployment.impl.fsr;

import java.io.File;


import org.alfresco.deployment.impl.server.DeploymentCommandQueue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileSystemReceiverServiceImpl implements FileSystemReceiverService
{
    private boolean errorOnOverwrite = false;
    


	private String fLogDirectory;

	private String fDataDirectory;
	
    private DeploymentCommandQueue commandQueue;

	private static Log logger = LogFactory.getLog(FileSystemReceiverServiceImpl.class);



	public void setLogDirectory(String logDirectory)
	{
		fLogDirectory = logDirectory;
	}

	public void setDataDirectory(String dataDirectory)
	{
		fDataDirectory = dataDirectory;
	}

	@SuppressWarnings("unchecked")
	public void init()
	{
	    if(fLogDirectory == null)
	    {
	        throw new RuntimeException("mandatory parameter logDirectory is null");
	    }
	    if(fDataDirectory == null)
	    {
	        throw new RuntimeException("mandatory parameter dataDirectory is null");
	    }

		File log = new File(fLogDirectory);
		if (!log.exists())
		{
			logger.info("creating log data directory:" + log.toString());
			log.mkdirs();
		}
		File data = new File(fDataDirectory);
		if (!data.exists())
		{
			logger.info("creating data directory:" + data.toString());
			data.mkdirs();
		}
	}

	/**
	 * Get the directory to which log (as in journal) files will be written.
	 * @return
	 */
	public String getLogDirectory()
	{
		return fLogDirectory;
	}

	/**
	 * Get the directory to which work phase files get written.
	 * @return
	 */
	public String getDataDirectory()
	{
		return fDataDirectory;
	}
	
	/**
	 * Should there be an error if the FSR attempts to create a file or directory 
	 * that already exists ?   Otherwise the FSR will issue a warning and carry on.
	 * 
	 * @param errorOnOverwrite true an error will occur and deployment will stop, false 
	 * a warning will occur and deployment will continue
	 */
	public void setErrorOnOverwrite(boolean errorOnOverwrite) 
	{
		this.errorOnOverwrite = errorOnOverwrite;
	}

	public boolean isErrorOnOverwrite() 
	{
		return errorOnOverwrite;
	}

	public void queueCommand(Runnable command) 
	{
		commandQueue.queueCommand(command);
	}

	public Runnable pollCommand()
	{
		return commandQueue.pollCommand();
	}
	
	public void setCommandQueue(DeploymentCommandQueue commandQueue) 
	{
		this.commandQueue = commandQueue;
	}

	public DeploymentCommandQueue getCommandQueue() 
	{
		return commandQueue;
	}



}
