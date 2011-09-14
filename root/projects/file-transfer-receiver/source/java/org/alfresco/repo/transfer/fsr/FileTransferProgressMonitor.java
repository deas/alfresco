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
package org.alfresco.repo.transfer.fsr;

import java.io.InputStream;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.alfresco.repo.transfer.TransferProgressMonitor;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.transfer.TransferException;
import org.alfresco.service.cmr.transfer.TransferProgress;
import org.alfresco.service.cmr.transfer.TransferProgress.Status;

public class FileTransferProgressMonitor implements TransferProgressMonitor
{
    private ConcurrentMap<String,TransferProgress> progressMap = new ConcurrentSkipListMap<String, TransferProgress>();
    
    public InputStream getLogInputStream(String transferId) throws TransferException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public TransferProgress getProgress(String transferId) throws TransferException
    {
        return getOrCreateProgress(transferId);

    }

    public void logComment(String transferId, Object obj) throws TransferException
    {
        // TODO Auto-generated method stub

    }

    public void logCreated(
            String transferId,
            NodeRef sourceNode,
            NodeRef destNode,
            NodeRef newParent,
            Path newPath,
            boolean orphan)
    {
        // TODO Auto-generated method stub

    }

    public void logDeleted(String transferId, NodeRef sourceNode, NodeRef destNode, Path parentPath)
    {
        // TODO Auto-generated method stub

    }

    public void logException(String transferId, Object obj, Throwable ex) throws TransferException
    {
        // TODO Auto-generated method stub

    }

    public void logMoved(
            String transferId,
            NodeRef sourceNodeRef,
            NodeRef destNodeRef,
            Path oldPath,
            NodeRef newParent,
            Path newPath)
    {
        // TODO Auto-generated method stub

    }

    public void logUpdated(String transferId, NodeRef sourceNode, NodeRef destNode, Path parentPath)
    {
        // TODO Auto-generated method stub

    }

    public void updateProgress(String transferId, int currPos) throws TransferException
    {
        TransferProgress progress = getOrCreateProgress(transferId);
        progress.setCurrentPosition(currPos);
    }

    public void updateProgress(String transferId, int currPos, int endPos) throws TransferException
    {
        TransferProgress progress = getOrCreateProgress(transferId);
        progress.setCurrentPosition(currPos);
        progress.setEndPosition(endPos);
    }

    public void updateStatus(String transferId, Status status) throws TransferException
    {
        TransferProgress progress = getOrCreateProgress(transferId);
        progress.setStatus(status);
    }

    private TransferProgress getOrCreateProgress(String transferId)
    {
        TransferProgress existingProgress = progressMap.get(transferId);
        if (existingProgress == null)
        {
            TransferProgress progress = new TransferProgress();
            progress.setCurrentPosition(0);
            progress.setEndPosition(1);
            progress.setStatus(Status.PRE_COMMIT);
            progressMap.putIfAbsent(transferId, progress);
        }
        return progressMap.get(transferId);
    }

}
