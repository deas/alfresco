package org.alfresco.repo.transfer.fsr;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.transfer.AbstractManifestProcessorBase;
import org.alfresco.repo.transfer.TransferCommons;
import org.alfresco.repo.transfer.TransferProcessingException;
import org.alfresco.repo.transfer.manifest.TransferManifestDeletedNode;
import org.alfresco.repo.transfer.manifest.TransferManifestHeader;
import org.alfresco.repo.transfer.manifest.TransferManifestNormalNode;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.transfer.TransferException;
import org.alfresco.service.cmr.transfer.TransferReceiver;
import org.springframework.util.FileCopyUtils;
import org.alfresco.service.namespace.QName;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileTransferSecondaryManifestProcessor extends AbstractFileManifestProcessorBase
{
    private final static Log log = LogFactory.getLog(FileTransferSecondaryManifestProcessor.class);


    protected List<TransferManifestNormalNode> waitingNodeList;

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
    }

    @Override
    protected void processHeader(TransferManifestHeader header)
    {

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
        // check if the node already exist
        Boolean isNodeNew = fTReceiver.isContentNewOrModified(node.getNodeRef().toString(), null);
        String name = (String) node.getProperties().get(ContentModel.PROP_NAME);

        if (isNodeNew)
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
                File receivedFile = new File(fTReceiver.getDefaultReceivingroot() + parentPath + name);
                this.putFileContent(receivedFile, receivedContent);
                if (log.isDebugEnabled())
                {
                    log.debug("Content created:" + fTReceiver.getDefaultReceivingroot() + parentPath + name);
                }
            }
            else
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Tis is a folder:" + node.toString());
                }
                // we have received a folder, create it
                File receivedFolder = new File(fTReceiver.getDefaultReceivingroot() + parentPath + name);
                receivedFolder.mkdir();
                if (log.isDebugEnabled())
                {
                    log.debug("Folder created:" + fTReceiver.getDefaultReceivingroot() + parentPath + name);
                }
            }
            String contentUrl = this.getContentUrl(node);
            // create the node in the DB here
            fTReceiver.createNodeInDB(node.getNodeRef().toString(), parentOfNode, parentPath, name, contentUrl);
            if (log.isDebugEnabled())
            {
                log.debug("Node created in DB:" + node.getNodeRef().toString());
                log.debug("Child of:" + parentOfNode);
                log.debug("Parent path:" + parentPath);
                log.debug("Node name:" + name);
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
                    log.debug("Moving to:" + parentPath + name + "/" + curChild.getContentName());
                }
                // adjust location on file system for the direct child
                moveFileOrFolderOnFileSytem(curChild.getPath(), curChild.getContentName(), parentPath + name + "/",
                        curChild.getContentName());
            }
            FileTransferInfoEntity newlyCreatednode = fTReceiver.findFileTransferInfoByNodeRef(node.getNodeRef()
                    .toString());
            // adjust path in DB
            adjustPathInSubtreeInDB(newlyCreatednode, parentPath + name + "/");
            if (log.isDebugEnabled())
            {
                log.debug("adjustPathInSubtreeInDB:" + newlyCreatednode);
                log.debug("Move to:" + parentPath + name + "/");
            }

        }
        else
        {   // this is not new node
            if (log.isDebugEnabled())
            {
                log.debug("This node is NOT new:" + node.toString());
            }
            // not a new node, it can be a move of a folder or a content
            // it can be a content modification or a rename of folder or content
            // check if we receive a file or a folder
            QName nodeType = node.getType();
            FileTransferInfoEntity nodeToModify = fTReceiver
                    .findFileTransferInfoByNodeRef(node.getNodeRef().toString());
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

                // maybe the name of the node has changed
                if (!nodeToModify.getContentName().equals(name))
                {
                    if (log.isDebugEnabled())
                    {
                        log.debug("Node name has changed, nodeFef:" + nodeToModify.getNodeRef());
                        log.debug("Node name has changed, node Id:" + nodeToModify.getId());
                        log.debug("Node name has changed, old node name :" + nodeToModify.getContentName());
                        log.debug("Node name has changed, new node name :" + name);
                    }
                    String oldName = nodeToModify.getContentName();
                    // set the new name and update
                    nodeToModify.setContentName(name);
                    fTReceiver.updateFileTransferInfoByNodeRef(nodeToModify);

                    // Impact File system
                    moveFileOrFolderOnFileSytem(nodeToModify.getPath(), oldName, nodeToModify.getPath(), name);
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
                        moveFileOrFolderOnFileSytem(oldPath, nodeToModify.getContentName(), newPath, name);
                    }
                    else
                    {
                        //it could be a node directly under root
                        //should be moved under the root?
                        boolean isNodeUnderRoot = parentOfNode.equals(fTReceiver.getFileTransferRootNodeFileFileSystem());
                        if (log.isDebugEnabled())
                        {
                            log.debug("Node is moved, nodeFef:" + nodeToModify.getNodeRef());
                        }
                        name = nodeToModify.getContentName();
                        if(isNodeUnderRoot)
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
                // this is a folder and maybe the folder was moved or renamed
                // maybe the name of the node has changed
                String oldName = nodeToModify.getContentName();
                if (!oldName.equals(name))
                {
                    if (log.isDebugEnabled())
                    {
                        log.debug("Node is renamed, nodeFef:" + nodeToModify.getNodeRef());
                        log.debug("Node is renamed, node Id:" + nodeToModify.getId());
                        log.debug("Node is renamed, old parent :" + nodeToModify.getParent());
                        log.debug("Node is moved, old path :" + nodeToModify.getPath());
                    }
                    // set the new name and update
                    nodeToModify.setContentName(name);
                    // impact DB
                    fTReceiver.updateFileTransferInfoByNodeRef(nodeToModify);
                    moveFileOrFolderOnFileSytem(nodeToModify.getPath(), oldName, nodeToModify.getPath(), name);
                    adjustPathInSubtreeInDB(nodeToModify, nodeToModify.getPath() + name + "/");
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
                            moveFileOrFolderOnFileSytem(oldPath, name, newPath, name);
                            // use the new name because maybe it was renamed just before
                            adjustPathInSubtreeInDB(nodeToModify, newPath + name + "/");
                        }
                        else
                        {
                            /** **************************************************************************** */
                            /* Some test here to handle the "root" case if parent does not exist in the DB */
                            /** **************************************************************************** */
                            boolean isNodeUnderRoot = parentOfNode.equals(fTReceiver.getFileTransferRootNodeFileFileSystem());
                            if (log.isDebugEnabled())
                            {
                                log.debug("Node is moved, nodeFef:" + nodeToModify.getNodeRef());
                            }
                            if(isNodeUnderRoot)
                            {
                                // adjust the path
                                String newPath = "/";
                                String oldPath = nodeToModify.getPath();
                                nodeToModify.setPath(newPath);
                                moveFileOrFolderOnFileSytem(oldPath, name, newPath, name);
                                // use the new name because maybe it was renamed just before
                                adjustPathInSubtreeInDB(nodeToModify, newPath + name + "/");
                            }
                            else
                            {
                                //throw new TransferException("MSG_NO_PARENT_FOUND:" + nodeToModify);
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
                    FileTransferInfoEntity newParentEntity = fTReceiver.findFileTransferInfoByNodeRef(parentparentOfNode);
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
                        //should be moved under the root?
                        boolean isNodeUnderRoot = parentparentOfNode.equals(fTReceiver.getFileTransferRootNodeFileFileSystem());
                        if (log.isDebugEnabled())
                        {
                            log.debug("Node is moved, nodeFef:" + nodeToModify.getNodeRef());
                        }
                        String name = nodeToModify.getContentName();
                        if(isNodeUnderRoot)
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
            if(toBeRemoved.isEmpty())
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
        // Method 2 using copy/delete
        // File fileToMove = new File(fTReceiver.getDefaultReceivingroot() + oldPath + nodeToModify.getContentName());
        // File fileToMoveTarget = new File(fTReceiver.getDefaultReceivingroot() + newPath + name);
        // try
        // {
        // fileToMoveTarget.createNewFile();
        // FileCopyUtils.copy(fileToMove, fileToMoveTarget);
        // }
        // catch (IOException e)
        // {
        // throw new TransferException("MSG_ERROR_WHILE_MOVING_FILE", e);
        // }
        // //delete old file
        // fileToMove.delete();
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
        // delte on FS
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
