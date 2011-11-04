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

package org.alfresco.module.vti.handler;

import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.version.VersionType;

/**
 * Interface for checkOut web service handler
 * 
 * @author DmitryVas
 */
public interface CheckOutCheckInServiceHandler
{
    /**
     * Check out provided document and creates write lock on working copy
     * 
     * @param fileName site relative url to the file
     * @return working copy or null if checkOut operation fails
     */
    NodeRef checkOutDocument(String fileName) throws FileNotFoundException;

    /**
     * Check in provided document and creates write lock on original document
     * 
     * @param fileName site relative url to the file
     * @param type major or minor checkin
     * @param comment checkIn comment
     * @return original node or null if checkIn operation fails
     */
    NodeRef checkInDocument(String fileName, VersionType type, String comment) throws FileNotFoundException;

    /**
     * Undo check out on provided document and creates write lock on original document
     * 
     * @param fileName site relative url to the file
     * @param lockAfterSucess true if original node must be locked after operation
     * @return original node or null if undo checkOut operation fails
     */
    NodeRef undoCheckOutDocument(String fileName, boolean lockAfterSucess) throws FileNotFoundException;
}
