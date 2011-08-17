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
import org.alfresco.repo.transfer.TransferCommons;
import org.alfresco.repo.transfer.TransferProcessingException;
import org.alfresco.repo.transfer.manifest.TransferManifestDeletedNode;
import org.alfresco.repo.transfer.manifest.TransferManifestNormalNode;
import org.alfresco.service.cmr.transfer.TransferReceiver;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This ManifestProcessor will for every new node, receive it and create it with his UID name and content. Track of the
 * original name will be kept in a separate table (alf_table_to_be_renamed). The nodes (on file system) will only take
 * their final at the end of FileTransferSecondaryManifestProcessor. For existing node we will act on, they will be also
 * be renamed. They will be renamed using counter in order to minimise the path length on the file system.
 *
 * @author philippe
 */
public class FileTransferPrimaryManifestProcessor extends AbstractFileManifestProcessorBase
{
    private final static Log log = LogFactory.getLog(FileTransferPrimaryManifestProcessor.class);

    // counter usesed to rename nodes temporarily in order to avoid
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

        // Prepare management of eventual naming collisions during file folder rearrangement
        // All the modifications are made using move then it is possible that if names are
        // kept during that phase then name collisions would occur on file system
        // Example /A/A/A if reordered
        // this.fTransferId

        // check if node is moved
        // Search the node to check if it exist already
        FileTransferInfoEntity nodeEntity = fTReceiver.findFileTransferInfoByNodeRef(node.getNodeRef().toString());
        if (nodeEntity != null)
        {
            // node is not new, exist in DB
            String parentOfNode = node.getPrimaryParentAssoc().getParentRef().toString();
            // if node moved or renamed
            String newName = (String) node.getProperties().get(ContentModel.PROP_NAME);
            if (!parentOfNode.equals(nodeEntity.getParent()) || !newName.equals(nodeEntity.getContentName()))
            {
                // node is moved therefore there is risk of collision
                // save the new name that will be given finally in FileTransferSecondaryManifestProcessor.endManifest()
                String name = (String) node.getProperties().get(ContentModel.PROP_NAME);
                this.fTReceiver.createNodeRenameEntity(node.getNodeRef().toString(), this.fTransferId, name);
                // rename the current node in DB and rename on file system and update the path in DB
                // adjust the path
                String oldName = nodeEntity.getContentName();
                String newTemporaryTechnicalName = "F" + this.renamingCounter + "-ren";
                nodeEntity.setContentName(newTemporaryTechnicalName);
                fTReceiver.updateFileTransferInfoByNodeRef(nodeEntity);
                // update the name on file system using the old name
                String nodePath = nodeEntity.getPath();
                moveFileOrFolderOnFileSytem(nodePath, oldName, nodePath, newTemporaryTechnicalName);
                // use the new name because maybe it was renamed just before
                adjustPathInSubtreeInDB(nodeEntity, nodePath + newTemporaryTechnicalName + "/");
                this.renamingCounter++;
            }
        }
        else
        {

            if (log.isDebugEnabled())
            {
                log.debug("Node is new:" + node.toString());
            }
            String parentOfNode = node.getPrimaryParentAssoc().getParentRef().toString();

            boolean isNodeUnderRoot = parentOfNode.equals(fTReceiver.getFileTransferRootNodeFileFileSystem());
            FileTransferInfoEntity parentFileTransferInfoEntity = null;
            String parentPath = "/";
            if (!isNodeUnderRoot)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Node is NOT under root:" + node.toString());
                }
                // try to get the ancestor of the node in the DB
                parentFileTransferInfoEntity = fTReceiver.findFileTransferInfoByNodeRef(parentOfNode);

                if (parentFileTransferInfoEntity == null)
                {
                    parentPath = "/" + TEMP_VIRT_ROOT + "/";
                    if (log.isDebugEnabled())
                    {
                        log.debug("Node is temporarilly adopted by TEMP_VIRT_ROOT:" + node.toString());
                        log.debug("ParentPath is temporarilly adopted by TEMP_VIRT_ROOT:" + parentPath);
                    }

                }
                else
                {
                    parentPath = parentFileTransferInfoEntity.getPath() + parentFileTransferInfoEntity.getContentName()
                            + "/";
                    if (log.isDebugEnabled())
                    {
                        log.debug("ParentPath exist:" + node.toString());
                        log.debug("ParentPAth is:" + parentPath);
                    }

                }

            }
            //node is new
            String name = (String) node.getProperties().get(ContentModel.PROP_NAME);
            String newTemporaryTechnicalName = "F" + this.renamingCounter + "-ren";
            this.fTReceiver.createNodeRenameEntity(node.getNodeRef().toString(), this.fTransferId, name);
            this.renamingCounter++;
            // check if we receive a file or a folder
            QName nodeType = node.getType();

            Boolean isFolder = ContentModel.TYPE_FOLDER.equals(nodeType);
            getOrCreateFolderIfNotExist(fTReceiver.getDefaultReceivingroot());
            if (!isFolder)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Tis is content:" + node.toString());
                }
                String contentKey = TransferCommons.URLToPartName(this.getContentUrl(node));
                File receivedContent = fTReceiver.getContents().get(contentKey);


                // this is content
                // create the file with the name
                File receivedFile = new File(fTReceiver.getDefaultReceivingroot() + parentPath + newTemporaryTechnicalName);
                this.putFileContent(receivedFile, receivedContent);
                if (log.isDebugEnabled())
                {
                    log.debug("Content created:" + fTReceiver.getDefaultReceivingroot() + parentPath + newTemporaryTechnicalName);
                }
            }
            else
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Tis is a folder:" + node.toString());
                }
                // we have received a folder, create it
                File receivedFolder = new File(fTReceiver.getDefaultReceivingroot() + parentPath + newTemporaryTechnicalName);
                receivedFolder.mkdir();
                if (log.isDebugEnabled())
                {
                    log.debug("Folder created:" + fTReceiver.getDefaultReceivingroot() + parentPath + newTemporaryTechnicalName);
                }
            }
            String contentUrl = this.getContentUrl(node);
            // create the node in the DB here
            fTReceiver.createNodeInDB(node.getNodeRef().toString(), parentOfNode, parentPath, newTemporaryTechnicalName, contentUrl);
            if (log.isDebugEnabled())
            {
                log.debug("Node created in DB:" + node.getNodeRef().toString());
                log.debug("Child of:" + parentOfNode);
                log.debug("Parent path:" + parentPath);
                log.debug("Node name:" + newTemporaryTechnicalName);
                log.debug("Content URL:" + contentUrl);
            }
            // get the nodes for adoption and adopt here
            // get all children of nodeToModify
            List<FileTransferInfoEntity> childrenList = fTReceiver.findFileTransferInfoByParentNodeRef(node
                    .getNodeRef().toString());
            // iterate on children
            // move and adjust path in DB
            for (FileTransferInfoEntity curChild : childrenList)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Moving on filse system:" + curChild.getPath() + curChild.getContentName());
                    log.debug("Moving to:" + parentPath + newTemporaryTechnicalName + "/" + curChild.getContentName());
                }
                // adjust location on file system for the direct child
                moveFileOrFolderOnFileSytem(curChild.getPath(), curChild.getContentName(), parentPath + newTemporaryTechnicalName + "/",
                        curChild.getContentName());
            }
            FileTransferInfoEntity newlyCreatednode = fTReceiver.findFileTransferInfoByNodeRef(node.getNodeRef()
                    .toString());
            // adjust path in DB
            adjustPathInSubtreeInDB(newlyCreatednode, parentPath + newTemporaryTechnicalName + "/");
            if (log.isDebugEnabled())
            {
                log.debug("adjustPathInSubtreeInDB:" + newlyCreatednode);
                log.debug("Move to:" + parentPath + newTemporaryTechnicalName + "/");
            }

        }

        if (this.isSync)
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
