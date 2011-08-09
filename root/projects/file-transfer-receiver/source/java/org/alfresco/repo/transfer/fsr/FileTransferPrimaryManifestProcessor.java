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
import org.alfresco.repo.transfer.manifest.TransferManifestNormalNode;
import org.alfresco.service.cmr.transfer.TransferReceiver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This ManifestProcessor will for every new node, receive it and create it with his UID name and content.
 * Track of the original name will be kept in a separate table (alf_table_to_be_renamed).
 * The nodes (on file system) will only take their final at the end of FileTransferSecondaryManifestProcessor.
 * For existing node we will act on, they will be also be renamed. They will be renamed using counter in order
 * to minimise the path length on the file system.
 *
 *
 * @author philippe
 *
 */
public class FileTransferPrimaryManifestProcessor extends AbstractFileManifestProcessorBase
{
    private final static Log log = LogFactory.getLog(FileTransferPrimaryManifestProcessor.class);

    //counter usesed to rename nodes temporarily in order to avoid
    private long renamingCounter = 0;

    public FileTransferPrimaryManifestProcessor(TransferReceiver receiver, String transferId)
    {
        super(receiver, transferId);

    }

    @Override
    protected void startManifest()
    {
        if (log.isDebugEnabled())
        {
            log.debug("Processing manigest started");
        }

        purgeTemporaryVirtualRoot();
        // recreate temporary root on file system
        getOrCreateFolderIfNotExist(fTReceiver.getDefaultReceivingroot());
        getOrCreateFolderIfNotExist(fTReceiver.getDefaultReceivingroot() + "/" + TEMP_VIRT_ROOT);

        this.fTReceiver.resetListOfNodesBeforeSyncMode();

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

    @Override
    protected void endManifest()
    {
        if (log.isDebugEnabled())
        {
            log.debug("End manifest!");
        }
    }


    @Override
    protected void processNode(TransferManifestNormalNode node) throws TransferProcessingException
    {
        if(this.isSync)
        {
            String nodeRef = node.getNodeRef().toString();
            this.fTReceiver.updateListOfDescendantsForSyncMode(nodeRef);
        }
    }

    @Override
    protected void processNode(TransferManifestDeletedNode node) throws TransferProcessingException
    {

    }


}
