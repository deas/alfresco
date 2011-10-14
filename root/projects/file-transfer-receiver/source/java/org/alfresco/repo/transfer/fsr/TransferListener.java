/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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
package org.alfresco.repo.transfer.fsr;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.transfer.TransferException;
import org.alfresco.service.cmr.transfer.TransferProgress.Status;

public interface TransferListener
{

    void created(String transferId, NodeRef sourceNode, NodeRef destNode, NodeRef newParent, String newPath,
            boolean orphan) throws TransferException;

    void deleted(String transferId, NodeRef sourceNode, NodeRef destNode, String path) throws TransferException;

    void moved(String transferId, NodeRef sourceNodeRef, NodeRef destNodeRef, String oldPath,
            NodeRef newParent, String newPath) throws TransferException;

    void updated(String transferId, NodeRef sourceNode, NodeRef destNode, String parentPath) throws TransferException;

    void statusChanged(String transferId, Status status) throws TransferException;
}
