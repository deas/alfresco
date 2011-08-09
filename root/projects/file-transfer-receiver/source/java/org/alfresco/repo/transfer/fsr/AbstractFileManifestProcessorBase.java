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

import org.alfresco.repo.transfer.TransferProcessingException;
import org.alfresco.repo.transfer.manifest.TransferManifestDeletedNode;
import org.alfresco.repo.transfer.manifest.TransferManifestHeader;
import org.alfresco.repo.transfer.manifest.TransferManifestNormalNode;
import org.alfresco.service.cmr.transfer.TransferException;
import org.alfresco.service.cmr.transfer.TransferReceiver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractFileManifestProcessorBase extends org.alfresco.repo.transfer.AbstractManifestProcessorBase
{
    private final static Log log = LogFactory.getLog(AbstractFileManifestProcessorBase.class);
    protected static String TEMP_VIRT_ROOT = "T_V_R_1234432123478";
    protected FileTransferReceiver fTReceiver;
    protected String fTransferId;
    protected boolean isSync;


    public AbstractFileManifestProcessorBase(TransferReceiver receiver, String transferId)
    {
        super(receiver, transferId);
        this.fTReceiver = (FileTransferReceiver)receiver;
        this.fTransferId = transferId;

    }

    @Override
    protected void endManifest()
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected void processHeader(TransferManifestHeader header)
    {
        isSync = header.isSync();
    }

    @Override
    protected void processNode(TransferManifestNormalNode node) throws TransferProcessingException
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected void processNode(TransferManifestDeletedNode node) throws TransferProcessingException
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected void startManifest()
    {
        // TODO Auto-generated method stub

    }

    protected void purgeTemporaryVirtualRoot()
    {
        // do the file system clean up first
        // delete TEMP_VIRT_ROOT is exist
        File tvr = new File(fTReceiver.getDefaultReceivingroot() + "/" + TEMP_VIRT_ROOT);
        if (tvr.exists())
        {
            if (log.isDebugEnabled())
            {
                log.debug("Purgin TEMP_VIRT_ROOT:" + fTReceiver.getDefaultReceivingroot() + "/" + TEMP_VIRT_ROOT);
            }
            tvr.delete();
        }
    }

    protected File getOrCreateFolderIfNotExist(String path)
    {
        File tempFolder = new File(path);
        if (!tempFolder.exists())
        {
            if (!tempFolder.mkdirs())
            {
                tempFolder = null;
                log.error("Failed to create temp folder:" + path);
                throw new TransferException("MSG_FAILED_TO_CREATE_STAGING_FOLDER");
            }
        }
        return tempFolder;
    }

}
