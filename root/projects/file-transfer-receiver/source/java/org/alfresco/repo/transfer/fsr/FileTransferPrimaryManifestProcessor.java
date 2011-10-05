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
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.repo.transfer.TransferCommons;
import org.alfresco.repo.transfer.TransferProcessingException;
import org.alfresco.repo.transfer.manifest.TransferManifestDeletedNode;
import org.alfresco.repo.transfer.manifest.TransferManifestNormalNode;
import org.alfresco.service.cmr.transfer.TransferReceiver;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
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

    // counter used to rename nodes temporarily in order to avoid naming conflicts
    private long renamingCounter = 0;
    private TransactionService transactionService;

    public FileTransferPrimaryManifestProcessor(TransferReceiver receiver, String transferId, TransactionService transactionService)
    {
        super(receiver, transferId);
        this.transactionService = transactionService;
    }

    @Override
    protected void startManifest()
    {
        if (log.isDebugEnabled())
        {
            log.debug("Processing manifest started");
        }

        purgeTemporaryVirtualRoot();
        // recreate temporary root on file system
        getOrCreateFolderIfNotExist(fTReceiver.getDefaultReceivingroot());
        getOrCreateFolderIfNotExist(fTReceiver.getDefaultReceivingroot() + getTemporaryFolderPath());

        this.fTReceiver.resetListOfNodesBeforeSyncMode();

    }

    protected void purgeTemporaryVirtualRoot()
    {
        // do the file system clean up first
        // delete TEMP_VIRT_ROOT is exist
        File tvr = new File(fTReceiver.getDefaultReceivingroot() + getTemporaryFolderPath());
        if (tvr.exists())
        {
            if (log.isDebugEnabled())
            {
                log.debug("Purging TEMP_VIRT_ROOT:" + fTReceiver.getDefaultReceivingroot() + getTemporaryFolderPath());
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
    protected void processNode(final TransferManifestNormalNode node) throws TransferProcessingException
    {

        // Prepare management of eventual naming collisions during file folder rearrangement
        // All the modifications are made using move then it is possible that if names are
        // kept during that phase then name collisions would occur on file system
        // Example /A/A/A if reordered
        // this.fTransferId

        if (log.isDebugEnabled())
        {
            log.debug("Processing received node: " + node.getNodeRef());
        }
        
        //Skip over any nodes that are not parented with a cm:contains association or 
        //are not content or folders
        if (!ContentModel.ASSOC_CONTAINS.equals(node.getPrimaryParentAssoc().getTypeQName()) ||
                !(ContentModel.TYPE_FOLDER.equals(node.getType()) ||
                        ContentModel.TYPE_CONTENT.equals(node.getType())))
        {
            if (log.isInfoEnabled())
            {
                log.info("Skipping node due to either: not content; not folder; or not cm:contains");
            }
            return;
        }
        
        transactionService.getRetryingTransactionHelper().doInTransaction(new NormalNodeProcessor(node), false, true);
    }

    @Override
    protected void processNode(TransferManifestDeletedNode node) throws TransferProcessingException
    {

    }
    

    private class NormalNodeProcessor implements RetryingTransactionCallback<Void>
    {
        private TransferManifestNormalNode node;
        
        public NormalNodeProcessor(TransferManifestNormalNode nodeToProcess)
        {
            this.node = nodeToProcess;
        }
        
        public Void execute() throws Throwable
        {
            // check if node is moved
            // Search the node to check if it exist already
            FileTransferInfoEntity nodeEntity = fTReceiver.findFileTransferInfoByNodeRef(node.getNodeRef().toString());
            if (nodeEntity != null)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Found existing record for this node");
                }
                // node is not new - it exists in our database
                String parentOfNode = node.getPrimaryParentAssoc().getParentRef().toString();
                String newName = (String) node.getProperties().get(ContentModel.PROP_NAME);
                if (!parentOfNode.equals(nodeEntity.getParent()) || !newName.equals(nodeEntity.getContentName()))
                {
                    if (log.isDebugEnabled())
                    {
                        log.debug("Node appears to have been moved or renamed");
                        log.debug("Previous parent == " + nodeEntity.getParent());
                        log.debug("Previous name == " + nodeEntity.getContentName());
                        log.debug("Reported parent == " + parentOfNode);
                        log.debug("Reported name == " + newName);
                    }
                    // node is moved therefore there is risk of collision
                    // save the new name that will be given finally in FileTransferSecondaryManifestProcessor.endManifest()
                    fTReceiver.createNodeRenameEntity(node.getNodeRef().toString(), fTransferId, newName);
                    // rename the current node in DB and rename on file system and update the path in DB
                    // adjust the path
                    String oldName = nodeEntity.getContentName();
                    String newTemporaryTechnicalName = "F" + renamingCounter + "-ren";
    
                    if (log.isDebugEnabled())
                    {
                        log.debug("Assigning temporary name of " + newTemporaryTechnicalName);
                    }
                    nodeEntity.setContentName(newTemporaryTechnicalName);
                    fTReceiver.updateFileTransferInfoByNodeRef(nodeEntity);
                    // update the name on file system using the old name
                    String nodePath = nodeEntity.getPath();
                    moveFileOrFolderOnFileSytem(nodePath, oldName, nodePath, newTemporaryTechnicalName);
                    // use the new name because maybe it was renamed just before
                    adjustPathInSubtreeInDB(nodeEntity, nodePath + newTemporaryTechnicalName + "/");
                    renamingCounter++;
                }
            }
            else
            {
                if (log.isDebugEnabled())
                {
                    log.debug("No existing record found.");
                }
                String parentOfNode = node.getPrimaryParentAssoc().getParentRef().toString();
    
                boolean nodeIsRoot = node.getNodeRef().toString().equals(fTReceiver.getTransferRootNode());
                boolean isNodeUnderRoot = parentOfNode.equals(fTReceiver.getTransferRootNode());
                FileTransferInfoEntity parentFileTransferInfoEntity = null;
                String parentPath = "/";
                if (log.isDebugEnabled())
                {
                    log.debug("Parent node id == " + parentOfNode);
                }
                if (!isNodeUnderRoot)
                {
                    if (log.isDebugEnabled())
                    {
                        log.debug("Node is NOT in root directory");
                    }
                    // try to get the ancestor of the node in the DB
                    parentFileTransferInfoEntity = fTReceiver.findFileTransferInfoByNodeRef(parentOfNode);
                    
                    if (parentFileTransferInfoEntity == null)
                    {
                        if (log.isDebugEnabled())
                        {
                            log.debug("No existing record found for the parent node. Placing node in temporary folder");
                        }
                        parentPath = getTemporaryFolderPath();
                    }
                    else
                    {
                        if (log.isDebugEnabled())
                        {
                            log.debug("Found existing record for the parent node");
                        }
                        parentPath = parentFileTransferInfoEntity.getPath() + parentFileTransferInfoEntity.getContentName()
                                + "/";
                    }
                }
                if (!nodeIsRoot)
                {
                    String name = (String) node.getProperties().get(ContentModel.PROP_NAME);
                    String newTemporaryTechnicalName = "F" + renamingCounter + "-ren";
                    fTReceiver.createNodeRenameEntity(node.getNodeRef().toString(), fTransferId, name);
                    renamingCounter++;
                    // check if we receive a file or a folder
                    QName nodeType = node.getAncestorType();
        
                    boolean isFolder = ContentModel.TYPE_FOLDER.equals(nodeType);
                    getOrCreateFolderIfNotExist(fTReceiver.getDefaultReceivingroot());
                    if (!isFolder)
                    {
                        if (log.isDebugEnabled())
                        {
                            log.debug("This is content");
                        }
                        String contentKey = TransferCommons.URLToPartName(getContentUrl(node));
                        File receivedContent = fTReceiver.getContents().get(contentKey);
        
                        // this is content
                        // create the file with the name
                        File receivedFile = new File(fTReceiver.getDefaultReceivingroot() + parentPath
                                + newTemporaryTechnicalName);
                        putFileContent(receivedFile, receivedContent);
                        if (log.isDebugEnabled())
                        {
                            log.debug("Content created:" + fTReceiver.getDefaultReceivingroot() + parentPath
                                    + newTemporaryTechnicalName);
                        }
                    }
                    else
                    {
                        if (log.isDebugEnabled())
                        {
                            log.debug("This is a folder");
                        }
                        // we have received a folder, create it
                        File receivedFolder = new File(fTReceiver.getDefaultReceivingroot() + parentPath
                                + newTemporaryTechnicalName);
                        receivedFolder.mkdir();
                        if (log.isDebugEnabled())
                        {
                            log.debug("Folder created:" + fTReceiver.getDefaultReceivingroot() + parentPath
                                    + newTemporaryTechnicalName);
                        }
                    }
                    String contentUrl = getContentUrl(node);
                    // create the node in the DB here
                    fTReceiver.createNodeInDB(node.getNodeRef().toString(), parentOfNode, parentPath,
                            newTemporaryTechnicalName, contentUrl);
                    if (log.isDebugEnabled())
                    {
                        log.debug("Node created in DB:" + node.getNodeRef().toString());
                        log.debug("Child of:" + parentOfNode);
                        log.debug("Parent path:" + parentPath);
                        log.debug("Node name:" + newTemporaryTechnicalName);
                        log.debug("Content URL:" + contentUrl);
                    }
                    
                    if (isFolder)
                    {
                        // get the nodes for adoption and adopt here
                        // get all children of nodeToModify
                        List<FileTransferInfoEntity> childrenList = fTReceiver.findFileTransferInfoByParentNodeRef(node
                                .getNodeRef().toString());
                        // iterate on children
                        // move and adjust path in DB
                        for (FileTransferInfoEntity curChild : childrenList)
                        {
                            // adjust location on file system for the direct child
                            moveFileOrFolderOnFileSytem(curChild.getPath(), curChild.getContentName(), parentPath
                                    + newTemporaryTechnicalName + "/", curChild.getContentName());
                        }
                    }
                    FileTransferInfoEntity newlyCreatednode = fTReceiver.findFileTransferInfoByNodeRef(node.getNodeRef()
                            .toString());
                    // adjust path in DB
                    adjustPathInSubtreeInDB(newlyCreatednode, parentPath + newTemporaryTechnicalName + "/");
                }
                else
                {
                    if (log.isDebugEnabled())
                    {
                        log.debug("This is the root node. Skipping.");
                    }
                }
            }
    
            if (isSync)
            {
                String nodeRef = node.getNodeRef().toString();
                fTReceiver.updateListOfDescendantsForSyncMode(nodeRef);
            }
            return null;
        }
    }
}
