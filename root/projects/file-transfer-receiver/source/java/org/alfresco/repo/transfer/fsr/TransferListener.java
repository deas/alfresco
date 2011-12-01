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

/**
 * The interface to implement in order to monitor and react to incoming transfers. Objects of this type
 * can be registered with an instance of the {@link HookableTransferMonitorImpl} class via its 
 * {@link HookableTransferMonitorImpl#setListeners(java.util.List)} method.
 * @author Brian Remmington
 * @since 4.0
 */
public interface TransferListener
{

    /**
     * Called when a folder or file is created in response to an inbound transfer. The creation is complete
     * at the point this operation is invoked.
     * @param transferId The identifier of the transfer currently in progress.
     * @param sourceNode The identifier of the node from the source repository  
     * @param parentNode The identifier of the parent node from the source repository 
     * @param newPath The path to the file/folder that has been created
     * @param orphan True if the parent folder does not currently exist locally. In this case the new file/folder
     * will have been created in a temporary location 
     * @throws TransferException Throwing an exception from this operation will cause the transfer to stop.
     */
    void created(String transferId, NodeRef sourceNode, NodeRef parentNode, String newPath,
            boolean orphan) throws TransferException;

    /**
     * Called when a folder or file is deleted in response to an inbound transfer. The deletion is complete
     * at the point that this operation is invoked.
     * @param transferId The identifier of the transfer currently in progress.
     * @param sourceNode The identifier of the node from the source repository
     * @param path The path of the file/folder that has been deleted
     * @throws TransferException Throwing an exception from this operation will cause the transfer to stop.
     */
    void deleted(String transferId, NodeRef sourceNode, String path) throws TransferException;

    /**
     * Called when a folder or file is moved in response to an inbound transfer. The move is complete 
     * at the point this operation is invoked.
     * @param transferId The identifier of the transfer currently in progress.
     * @param sourceNode The identifier of the node from the source repository
     * @param oldPath The location where the file/folder used to be
     * @param newParent The identifier of the new parent node in the source repository
     * @param newPath The location to which the file/folder has been moved
     * @throws TransferException Throwing an exception from this operation will cause the transfer to stop.
     */
    void moved(String transferId, NodeRef sourceNode, String oldPath,
            NodeRef newParent, String newPath) throws TransferException;

    /**
     * Called when a file is updated in response to an inbound transfer. The update is complete
     * at the point this operation is invoked.
     * @param transferId The identifier of the transfer currently in progress.
     * @param sourceNode The identifier of the node from the source repository
     * @param path The location of the file that has been updated.
     * @throws TransferException Throwing an exception from this operation will cause the transfer to stop.
     */
    void updated(String transferId, NodeRef sourceNode, String path) throws TransferException;

    /**
     * Called when an inbound transfer changes state. See {@link Status} for the possible statuses.
     * @param transferId The identifier of the transfer currently in progress.
     * @param status The new status of the transfer.
     * @throws TransferException Throwing an exception from this operation will cause the transfer to stop 
     * if it isn't already complete.
     * @see Status
     */
    void statusChanged(String transferId, Status status) throws TransferException;
}
