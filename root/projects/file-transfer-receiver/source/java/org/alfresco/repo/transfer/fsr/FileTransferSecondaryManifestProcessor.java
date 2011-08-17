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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.transfer.TransferCommons;
import org.alfresco.repo.transfer.TransferProcessingException;
import org.alfresco.repo.transfer.manifest.TransferManifestDeletedNode;
import org.alfresco.repo.transfer.manifest.TransferManifestNormalNode;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.transfer.TransferException;
import org.alfresco.service.cmr.transfer.TransferReceiver;
import org.alfresco.service.namespace.QName;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.FileCopyUtils;

public class FileTransferSecondaryManifestProcessor extends AbstractFileManifestProcessorBase
{
    private final static Log log = LogFactory.getLog(FileTransferSecondaryManifestProcessor.class);

    protected List<TransferManifestNormalNode> waitingNodeList;

    protected Set<String> receivedNodes;

    public FileTransferSecondaryManifestProcessor(TransferReceiver receiver, String transferId)
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

        waitingNodeList = new ArrayList<TransferManifestNormalNode>();
        // reset the set of received nodes.
        receivedNodes = new HashSet<String>();
    }

    protected void deleteSubtreeInDB(String parent)
    {
        if (log.isDebugEnabled())
        {
            log.debug("Deliting parents in DB:" + parent);
        }
        // get all children of nodeToModify
        List<FileTransferInfoEntity> childrenList = fTReceiver.findFileTransferInfoByParentNodeRef(parent);
        // iterate on children
        // remark probably a global update would be better here
        for (FileTransferInfoEntity curChild : childrenList)
        {
            fTReceiver.deleteNodeByNodeRef(curChild.getNodeRef());
        }

        // call recursively for all children adjustPathInSubtreeInDB(FileTransferInfoEntity nodeToModify, String
        // adjusted containingPAth)
        for (FileTransferInfoEntity curChild : childrenList)
        {
            deleteSubtreeInDB(curChild.getNodeRef());
        }

    }

    @Override
    protected void endManifest()
    {
        if (log.isDebugEnabled())
        {
            log.debug("End manifest!");
        }
        // delete TEMP_VIRT_ROOT is exist
        File tvr = new File(fTReceiver.getDefaultReceivingroot() + "/" + TEMP_VIRT_ROOT);
        if (tvr.exists())
        {
            tvr.delete();
        }

        // if isSyncMode = true the do the implicit delete
        if (this.isSync)
        {
            Set<String> nodesToDeleteInSyncMode = fTReceiver.getListOfDescendentsForSyncMode();
            if (log.isDebugEnabled())
            {
                log.debug("nodesToDeleteInSyncMode...");
                dumpSet(nodesToDeleteInSyncMode);
                log.debug("this.receivedNodes...");
                dumpSet(this.receivedNodes);
            }

            nodesToDeleteInSyncMode.removeAll(this.receivedNodes);

            if (log.isDebugEnabled())
                {
                log.debug("nodesToDeleteInSyncMode after remove:");
                dumpSet(nodesToDeleteInSyncMode);
            }

            // delete all the remaining nodes depth first
            while (!nodesToDeleteInSyncMode.isEmpty())
                purgeDepthFirst(nodesToDeleteInSyncMode, nodesToDeleteInSyncMode.toArray()[0].toString());
        }

        // Give the nodes their final name
        List<FileTransferNodeRenameEntity> cadidatedFoFinalRename = fTReceiver
                .findFileTransferNodeRenameEntityByTransferId(this.fTransferId);
        for (FileTransferNodeRenameEntity fileTransferNodeRenameEntiy : cadidatedFoFinalRename)
        {
            String newName = fileTransferNodeRenameEntiy.getNewName();
            FileTransferInfoEntity fileTransferInfoEntiy = fTReceiver
                    .findFileTransferInfoByNodeRef(fileTransferNodeRenameEntiy.getRenamedNodeRef());
            String oldName = fileTransferInfoEntiy.getContentName();
            fileTransferInfoEntiy.setContentName(newName);
            // update DB
            fTReceiver.updateFileTransferInfoByNodeRef(fileTransferInfoEntiy);
            // update the name on file system using the old name
            String nodePath = fileTransferInfoEntiy.getPath();
            moveFileOrFolderOnFileSytem(nodePath + "/", oldName, nodePath + "/", newName);
            adjustPathInSubtreeInDB(fileTransferInfoEntiy, nodePath + "/" + newName + "/");
        }
        // delete all the info of about the renamed node in the DB
        fTReceiver.deleteFileTransferNodeRenameByTransferId(this.fTransferId);
    }

    protected void dumpSet(Set<String> set)
    {
        Iterator<String> i = set.iterator();
        while (i.hasNext())
        {
            String s = i.next();
            log.debug(":" + s);
        }
    }

    protected void purgeDepthFirst(Set<String> nodesToDeleteInSyncMode, String nodeRef)
    {

        // get all children of nodeToModify
        List<FileTransferInfoEntity> childrenList = fTReceiver.findFileTransferInfoByParentNodeRef(nodeRef.toString());
        // iterate on children
        for (FileTransferInfoEntity curChild : childrenList)
        {
            purgeDepthFirst(nodesToDeleteInSyncMode, curChild.getNodeRef());
        }

        // delete node on file system
        FileTransferInfoEntity deletedNode = fTReceiver.findFileTransferInfoByNodeRef(nodeRef);

        // If null just log and ignore
        // delete on FS
        String pathFileOrFolderToBeDeleted = fTReceiver.getDefaultReceivingroot() + deletedNode.getPath()
                + deletedNode.getContentName();

        File fileOrFolderToBeDeleted = new File(pathFileOrFolderToBeDeleted);

        try
        {
            fileOrFolderToBeDeleted.delete();
        }
        catch (Exception e)
        {
            log.error("Failed to delete :" + pathFileOrFolderToBeDeleted, e);
            throw new TransferException("Failed to delete node:", e);

        }
        // delete in the DB
        fTReceiver.deleteNodeByNodeRef(nodeRef);
        nodesToDeleteInSyncMode.remove(nodeRef);
    }

    @Override
    protected void processNode(TransferManifestNormalNode node) throws TransferProcessingException
    {
        if (log.isDebugEnabled())
        {
            log.debug("Starting processing node" + node.toString());
            log.debug("Starting processing node,nodeRef:" + node.getNodeRef());
            log.debug("Starting processing node,name:" + (String) node.getProperties().get(ContentModel.PROP_NAME));
            log.debug("Starting processing node,content Url:" + this.getContentUrl(node));
            log.debug("Starting processing node,content properties:" + node.getProperties());
        }

        // In sync mode, convention is that if a node is not received then it is an implicit delete
        if (this.isSync)
        {
            String nodeRef = node.getNodeRef().toString();
            receivedNodes.add(nodeRef);
        }

        String name = (String) node.getProperties().get(ContentModel.PROP_NAME);

        // this is not new node
        if (log.isDebugEnabled())
        {
            log.debug("This node is NOT new:" + node.toString());
        }
        // not a new node, it can be a move of a folder or a content
        // it can be a content modification or a rename of folder or content
        // check if we receive a file or a folder
        QName nodeType = node.getType();
        FileTransferInfoEntity nodeToModify = fTReceiver.findFileTransferInfoByNodeRef(node.getNodeRef().toString());
        Boolean isFolder = ContentModel.TYPE_FOLDER.equals(nodeType);
        if (!isFolder)
        {
            if (log.isDebugEnabled())
            {
                log.debug("Tis is content:" + node.toString());
            }
            // check if content has changed
            Boolean isContentModified = fTReceiver.isContentNewOrModified(node.getNodeRef().toString(), this
                    .getContentUrl(node));

            if (isContentModified)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Content URL was mofified:" + name);
                }
                File receivedFile = new File(fTReceiver.getDefaultReceivingroot() + nodeToModify.getPath()
                        + nodeToModify.getContentName());
                String contentKey = TransferCommons.URLToPartName(this.getContentUrl(node));
                File receivedContent = fTReceiver.getContents().get(contentKey);
                if (receivedContent == null)
                {
                    if (log.isDebugEnabled())
                    {
                        log.debug("Error content not there:" + fTReceiver.getDefaultReceivingroot()
                                + nodeToModify.getPath() + nodeToModify.getContentName());
                        log.debug("Key is:" + contentKey);
                    }
                    throw new TransferException("MSG_ERROR_CONTENT_NOT_THERE:" + contentKey);
                }

                this.putFileContent(receivedFile, receivedContent);
                // update DB
                nodeToModify.setContentUrl(getContentUrl(node));
                fTReceiver.updateFileTransferInfoByNodeRef(nodeToModify);
            }

            // Check if the node was moved
            String parentOfNode = node.getPrimaryParentAssoc().getParentRef().toString();
            if (!parentOfNode.equals(nodeToModify.getParent()))
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Node is moved, nodeFef:" + nodeToModify.getNodeRef());
                    log.debug("Node is moved, node Id:" + nodeToModify.getId());
                    log.debug("Node is moved, old parent :" + nodeToModify.getParent());
                    log.debug("Node is moved, new parent :" + parentOfNode);
                    log.debug("Node is moved, old path :" + nodeToModify.getPath());
                }
                // retrieve the new parent
                FileTransferInfoEntity newParentEntity = fTReceiver.findFileTransferInfoByNodeRef(parentOfNode);

                // adjust the parent node
                nodeToModify.setParent(parentOfNode);
                if (newParentEntity != null)
                {
                    // adjust the path because parent already exist
                    String newPath = newParentEntity.getPath() + newParentEntity.getContentName() + "/";
                    if (log.isDebugEnabled())
                    {
                        log.debug("Node is moved, new path :" + newPath);
                    }
                    String oldPath = nodeToModify.getPath();
                    nodeToModify.setPath(newPath);
                    moveFileOrFolderOnFileSytem(oldPath, nodeToModify.getContentName(), newPath, nodeToModify
                            .getContentName());
                }
                else
                {
                    // it could be a node directly under root
                    // should be moved under the root?
                    boolean isNodeUnderRoot = parentOfNode.equals(fTReceiver.getFileTransferRootNodeFileFileSystem());
                    if (log.isDebugEnabled())
                    {
                        log.debug("Node is moved, nodeFef:" + nodeToModify.getNodeRef());
                    }
                    name = nodeToModify.getContentName();
                    if (isNodeUnderRoot)
                    {
                        // adjust the path
                        String newPath = "/";
                        String oldPath = nodeToModify.getPath();
                        nodeToModify.setPath(newPath);
                        moveFileOrFolderOnFileSytem(oldPath, name, newPath, name);
                        // use the new name because maybe it was renamed just before
                        adjustPathInSubtreeInDB(nodeToModify, newPath + name + "/");
                        fTReceiver.updateFileTransferInfoByNodeRef(nodeToModify);
                    }
                }
                fTReceiver.updateFileTransferInfoByNodeRef(nodeToModify);
            }

        }
        else
        {
            // this is folder modified
            if (log.isDebugEnabled())
            {
                log.debug("This is folder:" + node.toString());
            }
            // Maybe parent was also modified then we have to do the move on FS and update the
            // full subtree in the DB
            String parentOfNode = node.getPrimaryParentAssoc().getParentRef().toString();
            if (!parentOfNode.equals(nodeToModify.getParent()))
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Node is moved, nodeFef:" + nodeToModify.getNodeRef());
                    log.debug("Node is moved, node Id:" + nodeToModify.getId());
                    log.debug("Node is moved, old parent :" + nodeToModify.getParent());
                    log.debug("Node is moved, new parent :" + parentOfNode);
                    log.debug("Node is moved, old path :" + nodeToModify.getPath());
                }
                // if the new parent is a descendant of the new received node then
                // it can not be treated now,keep it for later
                if (isDescendant(parentOfNode, node.getNodeRef().toString()))
                {
                    // keep the untreated node in a list to handle it later
                    waitingNodeList.add(node);

                }
                else
                {
                    // retrieve the new parent
                    FileTransferInfoEntity newParentEntity = fTReceiver.findFileTransferInfoByNodeRef(parentOfNode);
                    // adjust the parent node
                    nodeToModify.setParent(parentOfNode);
                    if (newParentEntity != null)
                    {
                        // adjust the path
                        String newPath = newParentEntity.getPath() + newParentEntity.getContentName() + "/";
                        String oldPath = nodeToModify.getPath();
                        nodeToModify.setPath(newPath);
                        moveFileOrFolderOnFileSytem(oldPath, nodeToModify.getContentName(), newPath, nodeToModify
                                .getContentName());
                        // use the new name because maybe it was renamed just before
                        adjustPathInSubtreeInDB(nodeToModify, newPath + nodeToModify.getContentName() + "/");
                    }
                    else
                    {
                        /** **************************************************************************** */
                        /* Some test here to handle the "root" case if parent does not exist in the DB */
                        /** **************************************************************************** */
                        boolean isNodeUnderRoot = parentOfNode.equals(fTReceiver
                                .getFileTransferRootNodeFileFileSystem());
                        if (log.isDebugEnabled())
                        {
                            log.debug("Node is moved, nodeFef:" + nodeToModify.getNodeRef());
                        }
                        if (isNodeUnderRoot)
                        {
                            // adjust the path
                            String newPath = "/";
                            String oldPath = nodeToModify.getPath();
                            nodeToModify.setPath(newPath);
                            moveFileOrFolderOnFileSytem(oldPath, nodeToModify.getContentName(), newPath, nodeToModify
                                    .getContentName());
                            // use the new name because maybe it was renamed just before
                            adjustPathInSubtreeInDB(nodeToModify, newPath + nodeToModify.getContentName() + "/");
                        }
                        else
                        {
                            // throw new TransferException("MSG_NO_PARENT_FOUND:" + nodeToModify);
                        }
                        // throw new TransferException("MSG_NO_PARENT_FOUND:" + nodeToModify);
                    }
                    /** **************************************************************************** */
                    /* Some test here to handle the "root" case if parent does not exist in the DB */
                    /** **************************************************************************** */
                    fTReceiver.updateFileTransferInfoByNodeRef(nodeToModify);
                    // check if some waiting nodes can be handle now
                    HandleWaitingNodes();
                }
            }
        }
    }

    protected void HandleWaitingNodes()
    {
        while (true)
        {
            List<TransferManifestNormalNode> toBeRemoved = new ArrayList<TransferManifestNormalNode>();
            // check the waiting nodes
            for (TransferManifestNormalNode node : waitingNodeList)
            {
                String parentparentOfNode = node.getPrimaryParentAssoc().getParentRef().toString();

                if (!isDescendant(parentparentOfNode, node.getNodeRef().toString()))
                {
                    FileTransferInfoEntity nodeToModify = fTReceiver.findFileTransferInfoByNodeRef(node.getNodeRef()
                            .toString());
                    // now this node can be handled
                    // retrieve the new parent
                    FileTransferInfoEntity newParentEntity = fTReceiver
                            .findFileTransferInfoByNodeRef(parentparentOfNode);
                    // adjust the parent node
                    nodeToModify.setParent(parentparentOfNode);
                    if (newParentEntity != null)
                    {
                        String name = nodeToModify.getContentName();
                        // adjust the path
                        String newPath = newParentEntity.getPath() + newParentEntity.getContentName() + "/";
                        String oldPath = nodeToModify.getPath();
                        nodeToModify.setPath(newPath);
                        moveFileOrFolderOnFileSytem(oldPath, name, newPath, name);
                        // use the new name because maybe it was renamed just before
                        adjustPathInSubtreeInDB(nodeToModify, newPath + name + "/");
                        fTReceiver.updateFileTransferInfoByNodeRef(nodeToModify);
                        // remove current node from the list
                        toBeRemoved.add(node);
                    }
                    else
                    {
                        // should be moved under the root?
                        boolean isNodeUnderRoot = parentparentOfNode.equals(fTReceiver
                                .getFileTransferRootNodeFileFileSystem());
                        if (log.isDebugEnabled())
                        {
                            log.debug("Node is moved, nodeFef:" + nodeToModify.getNodeRef());
                        }
                        String name = nodeToModify.getContentName();
                        if (isNodeUnderRoot)
                        {
                            // adjust the path
                            String newPath = "/";
                            String oldPath = nodeToModify.getPath();
                            nodeToModify.setPath(newPath);
                            moveFileOrFolderOnFileSytem(oldPath, name, newPath, name);
                            // use the new name because maybe it was renamed just before
                            adjustPathInSubtreeInDB(nodeToModify, newPath + name + "/");
                            fTReceiver.updateFileTransferInfoByNodeRef(nodeToModify);
                        }
                        else
                        {
                            throw new TransferException("MSG_NO_PARENT_FOUND:" + nodeToModify);
                        }

                    }
                }
            }
            if (toBeRemoved.isEmpty())
                break;
            waitingNodeList.removeAll(toBeRemoved);
        }
    }

    /**
     * Check if newParentNode is a descendant of the node that has to be moved.
     *
     * @param newParentOfNode
     * @param nodeToBeMoved
     * @return
     */
    protected boolean isDescendant(String newParentOfNode, String nodeToBeMoved)
    {
        // going from the new parent up to the root and check if we encounter the nodeToBeMoved
        FileTransferInfoEntity curNode = null;
        while (true)
        {
            curNode = fTReceiver.findFileTransferInfoByNodeRef(newParentOfNode);
            if (curNode == null)
                return false;
            newParentOfNode = curNode.getParent();
            if (nodeToBeMoved.equals(newParentOfNode))
            {
                return true;
            }
        }
    }

    protected void recursiveDeleteInDB(String nodeRef)
    {
        fTReceiver.deleteNodeByNodeRef(nodeRef.toString());

        // get all children of nodeToModify
        List<FileTransferInfoEntity> childrenList = fTReceiver.findFileTransferInfoByParentNodeRef(nodeRef.toString());
        // iterate on children
        for (FileTransferInfoEntity curChild : childrenList)
        {
            recursiveDeleteInDB(curChild.getNodeRef().toString());
        }
    }

    @Override
    protected void processNode(TransferManifestDeletedNode node) throws TransferProcessingException
    {
        FileTransferInfoEntity deletedNode = fTReceiver.findFileTransferInfoByNodeRef(node.getNodeRef().toString());

        // If null just log and ignore
        // delete on FS
        String pathFileOrFolderToBeDeleted = fTReceiver.getDefaultReceivingroot() + deletedNode.getPath()
                + deletedNode.getContentName();

        File fileOrFolderToBeDeleted = new File(pathFileOrFolderToBeDeleted);

        try
        {
            if (fileOrFolderToBeDeleted.isDirectory())
            {
                FileUtils.deleteDirectory(fileOrFolderToBeDeleted);
                fileOrFolderToBeDeleted.delete();

            }
            else
            {
                fileOrFolderToBeDeleted.delete();
            }
            recursiveDeleteInDB(node.getNodeRef().toString());
        }
        catch (Exception e)
        {
            log.error("Failed to delete :" + pathFileOrFolderToBeDeleted, e);
            throw new TransferException("Failed to delete node:", e);

        }

    }

}
