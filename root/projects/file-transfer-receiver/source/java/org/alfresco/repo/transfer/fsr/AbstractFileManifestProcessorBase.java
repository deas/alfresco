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
import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.transfer.TransferProcessingException;
import org.alfresco.repo.transfer.manifest.TransferManifestDeletedNode;
import org.alfresco.repo.transfer.manifest.TransferManifestHeader;
import org.alfresco.repo.transfer.manifest.TransferManifestNormalNode;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.transfer.TransferException;
import org.alfresco.service.cmr.transfer.TransferReceiver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.FileCopyUtils;

public abstract class AbstractFileManifestProcessorBase extends org.alfresco.repo.transfer.AbstractManifestProcessorBase
{
    private final static Log log = LogFactory.getLog(AbstractFileManifestProcessorBase.class);
    protected String TEMP_VIRT_ROOT;
    protected FileTransferReceiver fTReceiver;
    protected String fTransferId;
    protected boolean isSync;


    public AbstractFileManifestProcessorBase(TransferReceiver receiver, String transferId)
    {
        super(receiver, transferId);
        this.fTReceiver = (FileTransferReceiver)receiver;
        this.fTransferId = transferId;
        NodeRef node = new NodeRef(transferId);
        TEMP_VIRT_ROOT = node.getId();

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

    protected void moveFileOrFolderOnFileSytem(String oldPath, String oldName, String newPath, String newName)
    {
        // Method 1 using rename does not seem to work
        // move the node on the receiving file system
        // File (or directory) to be moved
        File fileToBeMoved = new File(fTReceiver.getDefaultReceivingroot() + oldPath + oldName);
        // Destination directory
        File newDirLocation = new File(fTReceiver.getDefaultReceivingroot() + newPath);
        // Move file to new directory
        boolean success = fileToBeMoved.renameTo(new File(newDirLocation, newName));
        if (!success)
        {
            log.error("Unable to move:" + oldPath + oldName + " to " + newPath + newName);
            // operation failed, maybe use Method 2
            // probably failing if the source and the destination are not on the same volume/partition
            // to be tested
            throw new TransferException("MSG_ERROR_WHILE_MOVING_FILE_OR_FOLDER");

        }
    }

    protected void adjustPathInSubtreeInDB(FileTransferInfoEntity nodeToModify, String containingPath)
    {
        // get all children of nodeToModify
        List<FileTransferInfoEntity> childrenList = fTReceiver.findFileTransferInfoByParentNodeRef(nodeToModify
                .getNodeRef());
        // iterate on children
        // remark probably a global update would be better here
        for (FileTransferInfoEntity curChild : childrenList)
        {
            // adjust path
            curChild.setPath(containingPath);
            fTReceiver.updateFileTransferInfoByNodeRef(curChild);
        }

        // call recursively for all children adjustPathInSubtreeInDB(FileTransferInfoEntity nodeToModify, String
        // adjusted containingPAth)
        for (FileTransferInfoEntity curChild : childrenList)
        {
            adjustPathInSubtreeInDB(curChild, containingPath + curChild.getContentName() + "/");
        }

    }

    protected void putFileContent(File receivedFile, File receivedContent)
    {
        try
        {
            if (!receivedFile.exists())
                receivedFile.createNewFile();
            FileCopyUtils.copy(receivedContent, receivedFile);
        }
        catch (Exception ex)
        {
            log.error("Unable to put file content in " + receivedFile.getPath() + "/" + receivedFile.getName());
            throw new TransferException("MSG_ERROR_WHILE_STAGING_CONTENT", ex);
        }
    }

    protected String getContentUrl(TransferManifestNormalNode node)
    {
        ContentData contentData = (ContentData) node.getProperties().get(ContentModel.PROP_CONTENT);

        String contentUrl = "";
        if (contentData != null)
        {
            contentUrl = contentData.getContentUrl();
        }
        return contentUrl;
    }

}
