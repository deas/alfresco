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

public interface FileSystemReceiverService 
{
	/**
	 * Get the directory to which log (as in journal) files will be written.
	 * @return the logDirectory
	 */
	public String getLogDirectory();

	/**
	 * Get the directory to which work phase files get written.
	 * @return the data directory
	 */
	public String getDataDirectory();
	
	/**
	 * Should an error be generated on overwriting content ?
	 */
	public boolean isErrorOnOverwrite();
	
	/**
	 *  Queue a command object for execution. 
	 */
	public void queueCommand(Runnable command);
	
	/**
	 * poll a queued command.
	 * @return the command or null if there is no command on the queue
	 */
	public Runnable pollCommand();

}
