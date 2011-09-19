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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Writer;
import java.nio.channels.Channels;

import org.alfresco.repo.transfer.AbstractTransferProgressMonitor;
import org.alfresco.repo.transfer.TransferFatalException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.transfer.TransferException;
import org.alfresco.service.cmr.transfer.TransferProgress;
import org.alfresco.service.cmr.transfer.TransferProgress.Status;

public class FileTransferProgressMonitor extends AbstractTransferProgressMonitor
{
    private TransferStatusDAO transferStatusDao;
    private File logDirectory;

    public void setTransferStatusDao(TransferStatusDAO dao)
    {
        this.transferStatusDao = dao;
    }

    public void setLogDirectory(String logDirectoryPath)
    {
        logDirectory = new File(logDirectoryPath);
    }

    public InputStream getLogInputStream(String transferId) throws TransferException
    {
        try
        {
            return new FileInputStream(getReportFile(transferId));
        }
        catch (FileNotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }

    public TransferProgress getProgressInternal(final String transferId) throws TransferException
    {
        TransferStatusEntity statusEntity = getTransferStatusEntity(transferId);
        TransferProgress progress = new TransferProgress();
        progress.setCurrentPosition(statusEntity.getCurrentPos());
        progress.setEndPosition(statusEntity.getEndPos());
        progress.setStatus(Status.valueOf(statusEntity.getStatus()));
        progress.setError((Throwable) statusEntity.getError());
        return progress;
    }

    public void updateProgressInternal(final String transferId, final int currPos) throws TransferException
    {
        TransferStatusEntity entity = getTransferStatusEntity(transferId);
        entity.setCurrentPos(currPos);
        transferStatusDao.update(entity);
    }

    public void updateProgressInternal(final String transferId, final int currPos, final int endPos) throws TransferException
    {
        TransferStatusEntity entity = getTransferStatusEntity(transferId);
        entity.setCurrentPos(currPos);
        entity.setEndPos(endPos);
        transferStatusDao.update(entity);
    }

    protected void updateStatusInternal(final String transferId, final Status status) throws TransferException
    {
        TransferStatusEntity entity = getTransferStatusEntity(transferId);
        entity.setStatus(status.name());
        transferStatusDao.update(entity);
    }

    private TransferStatusEntity getTransferStatusEntity(String transferId)
    {
        TransferStatusEntity statusEntity = transferStatusDao.findByTransferId(transferId);
        if (statusEntity == null)
        {
            statusEntity = transferStatusDao.createTransferStatus(transferId, 0, 1, Status.PRE_COMMIT.name(), null);
        }
        return statusEntity;
    }

    private File getReportFile(String transferId)
    {
        File logFile = new File(logDirectory, new NodeRef(transferId).getId() + "_report");
        return logFile;
    }

    @Override
    protected Writer createUnderlyingLogWriter(String transferId)
    {
        File logFile = getReportFile(transferId);
        try
        {
            return Channels.newWriter(Channels.newChannel(new FileOutputStream(logFile)), "UTF-8");
        }
        catch (FileNotFoundException e)
        {
            throw new TransferFatalException("error.unableToOpenTransferReport", e);
        }
    }

    @Override
    protected void storeError(final String transferId, final Throwable error)
    {
        TransferStatusEntity entity = getTransferStatusEntity(transferId);
        entity.setError(error);
        transferStatusDao.update(entity);
    }

}
