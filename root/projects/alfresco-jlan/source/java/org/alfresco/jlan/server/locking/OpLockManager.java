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

package org.alfresco.jlan.server.locking;

import org.alfresco.jlan.server.filesys.ExistingOpLockException;

/**
 * OpLock Manager Interface
 * 
 * <p>An oplock manager implementationis used to store oplock details for the CIFS protocol handler.
 * 
 * @author gkspencer
 */
public interface OpLockManager {

	/**
	 * Check if there is an oplock for the specified path, return the oplock type.
	 * 
	 * @param path String
	 * @return int
	 */
	public int hasOpLock(String path);
	
	/**
	 * Return the oplock details for a path, or null if there is no oplock on the path
	 * 
	 * @param path String
	 * @return OpLockDetails
	 */
	public OpLockDetails getOpLockDetails(String path);
	
	/**
	 * Grant an oplock, store the oplock details
	 * 
	 * @param path String
	 * @param oplock OpLockDetails
	 * @return boolean
	 * @exception ExistingOpLockException	If the file already has an oplock
	 */
	public boolean grantOpLock(String path, OpLockDetails oplock)
		throws ExistingOpLockException;
	
	/**
	 * Inform the oplock manager that an oplock break is in progress for the specified file/oplock
	 * 
	 * @param path String
	 * @param oplock OpLockDetails
	 */
	public void informOpLockBreakInProgress(String path, OpLockDetails oplock);
	
	/**
	 * Release an oplock
	 * 
	 * @param path String
	 */
	public void releaseOpLock(String path);
	
	/**
	 * Check for expired oplock break requests
	 * 
	 * @return int
	 */
	public int checkExpiredOplockBreaks();
}
