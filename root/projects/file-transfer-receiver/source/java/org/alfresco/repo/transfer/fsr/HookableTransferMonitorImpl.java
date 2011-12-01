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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.repo.transfer.TransferProgressMonitor;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.transfer.TransferException;
import org.alfresco.service.cmr.transfer.TransferProgress;
import org.alfresco.service.cmr.transfer.TransferProgress.Status;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HookableTransferMonitorImpl implements TransferProgressMonitor
{
    private static Log log = LogFactory.getLog(HookableTransferMonitorImpl.class);
    
    private TransferProgressMonitor systemMonitor;
    private List<TransferListener> listeners = new ArrayList<TransferListener>();
    
    public void setListeners(List<TransferListener> listeners)
    {
        this.listeners = listeners == null ? new ArrayList<TransferListener>() : 
            new ArrayList<TransferListener>(listeners);
    }

    public void setSystemMonitor(TransferProgressMonitor systemMonitor)
    {
        this.systemMonitor = systemMonitor;
    }

    @Override
    public InputStream getLogInputStream(String transferId) throws TransferException
    {
        return systemMonitor.getLogInputStream(transferId);
    }

    @Override
    public TransferProgress getProgress(String transferId) throws TransferException
    {
        return systemMonitor.getProgress(transferId);
    }

    @Override
    public void logComment(String transferId, Object obj) throws TransferException
    {
        systemMonitor.logComment(transferId, obj);
    }

    @Override
    public void logCreated(String transferId, NodeRef sourceNode, NodeRef destNode, NodeRef newParent, String newPath,
            boolean orphan)
    {
        systemMonitor.logCreated(transferId, sourceNode, destNode, newParent, newPath, orphan);
        for (TransferListener listener : listeners)
        {
            try
            {
                listener.created(transferId, sourceNode, newParent, newPath, orphan);
            }
            catch(Throwable t)
            {
                log.warn("Caught and discarding exception from external transfer listener", t);
            }
        }
    }

    @Override
    public void logDeleted(String transferId, NodeRef sourceNode, NodeRef destNode, String path)
    {
        systemMonitor.logDeleted(transferId, sourceNode, destNode, path);
        for (TransferListener listener : listeners)
        {
            try
            {
                listener.deleted(transferId, sourceNode, path);
            }
            catch (Throwable t)
            {
                log.warn("Caught and discarding exception from external transfer listener", t);
            }
        }
    }

    @Override
    public void logException(String transferId, Object obj, Throwable ex) throws TransferException
    {
        systemMonitor.logException(transferId, obj, ex);
    }

    @Override
    public void logMoved(String transferId, NodeRef sourceNodeRef, NodeRef destNodeRef, String oldPath,
            NodeRef newParent, String newPath)
    {
        systemMonitor.logMoved(transferId, sourceNodeRef, destNodeRef, oldPath, newParent, newPath);
        for (TransferListener listener : listeners)
        {
            try
            {
                listener.moved(transferId, sourceNodeRef, oldPath, newParent, newPath);
            }
            catch (Throwable t)
            {
                log.warn("Caught and discarding exception from external transfer listener", t);
            }
        }
    }

    @Override
    public void logUpdated(String transferId, NodeRef sourceNode, NodeRef destNode, String path)
    {
        systemMonitor.logUpdated(transferId, sourceNode, destNode, path);
        for (TransferListener listener : listeners)
        {
            try
            {
                listener.updated(transferId, sourceNode, path);
            }
            catch (Throwable t)
            {
                log.warn("Caught and discarding exception from external transfer listener", t);
            }
        }
    }

    @Override
    public void updateProgress(String transferId, int currPos) throws TransferException
    {
        systemMonitor.updateProgress(transferId, currPos);
    }

    @Override
    public void updateProgress(String transferId, int currPos, int endPos) throws TransferException
    {
        systemMonitor.updateProgress(transferId, currPos, endPos);
    }

    @Override
    public void updateStatus(String transferId, Status status) throws TransferException
    {
        systemMonitor.updateStatus(transferId, status);
        for (TransferListener listener : listeners)
        {
            try
            {
                listener.statusChanged(transferId, status);
            }
            catch (Throwable t)
            {
                log.warn("Caught and discarding exception from external transfer listener", t);
            }
        }
    }

}
