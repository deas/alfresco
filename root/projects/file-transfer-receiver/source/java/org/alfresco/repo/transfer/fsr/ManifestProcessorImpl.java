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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.transfer.AbstractManifestProcessorBase;
import org.alfresco.repo.transfer.TransferCommons;
import org.alfresco.repo.transfer.TransferFatalException;
import org.alfresco.repo.transfer.TransferProcessingException;
import org.alfresco.repo.transfer.TransferProgressMonitor;
import org.alfresco.repo.transfer.manifest.TransferManifestDeletedNode;
import org.alfresco.repo.transfer.manifest.TransferManifestHeader;
import org.alfresco.repo.transfer.manifest.TransferManifestNode;
import org.alfresco.repo.transfer.manifest.TransferManifestNormalNode;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.transfer.TransferReceiver;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ManifestProcessorImpl extends AbstractManifestProcessorBase
{
    private static final String MSG_FAILED_TO_CREATE_FOLDER = "ftr.failedToDeleteFolder";

    private static final String MSG_FAILED_TO_DELETE_FILE = "ftr.failedToDeleteFile";

    private static final String MSG_ERROR_COPYING_FILE = "ftr.errorCopyingFile";

    private Log log = LogFactory.getLog(ManifestProcessorImpl.class);

    private Map<String, List<NodeContext>> orphans = new TreeMap<String, List<NodeContext>>();
    private Map<String, NodeContext> foldersToDelete = new TreeMap<String, NodeContext>();
    private Map<String,NodeContext> tempFilesToRename = new TreeMap<String,NodeContext>();
    private Map<String,NodeContext> existingFilesToReplace = new TreeMap<String,NodeContext>();
    private Map<String, NodeContext> foldersToMove = new TreeMap<String,NodeContext>();
    private Set<String> receivedFolderIds = new TreeSet<String>();
    private Set<String> receivedFileIds = new TreeSet<String>();
    private Map<String,Set<String>> parentChildMap = new TreeMap<String, Set<String>>();
    private DbHelper dbHelper;
    private final boolean isDebugEnabled;

    private long processStartTime;

    private FileTransferReceiver fileTransferReceiver;

    private boolean isSync;
    
    public ManifestProcessorImpl(TransferReceiver receiver, String transferId, DbHelper dbHelper)
    {
        super(receiver, transferId);
        this.fileTransferReceiver = (FileTransferReceiver)receiver;
        this.dbHelper = dbHelper;
        this.isDebugEnabled = log.isDebugEnabled();
    }

    @Override
    protected void processHeader(TransferManifestHeader header)
    {
        this.isSync = header.isSync();
    }

    @Override
    protected void startManifest()
    {
        processStartTime = System.currentTimeMillis();
        NodeContext.renamingCounter = 1;
        //Make sure we have the root node recorded...
        //The root node is the only one that has "" as its parent id
        
        String rootNodeId = fileTransferReceiver.getTransferRootNode();
        String rootFolderLocation = fileTransferReceiver.getDefaultReceivingroot();
        if (isDebugEnabled)
        {
            log.debug("Checking that root node and corresponding folder exist: " + rootNodeId + 
                    "   " + rootFolderLocation);
        }
        FileTransferInfoEntity rootNodeEntity = dbHelper.findFileTransferInfoByNodeRef(rootNodeId);
        if (rootNodeEntity == null)
        {
            if (isDebugEnabled)
            {
                log.debug("Failed to find root node in database. Creating...");
            }
            File rootFolder = new File(rootFolderLocation);
            boolean rootFolderExists = rootFolder.exists();
            if (!rootFolderExists)
            {
                if (isDebugEnabled)
                {
                    log.debug("Root folder does not exist. Creating...");
                }
                rootFolderExists = rootFolder.mkdir();
            }
            if (rootFolderExists)
            {
                if (isDebugEnabled)
                {
                    log.debug("Root folder exists. Creating node record in database.");
                }
                dbHelper.createNodeInDB(rootNodeId, "", "", "", "", true);
            }
        }
        else
        {
            if (isDebugEnabled)
            {
                log.debug("Root node already exists in the database.");
            }
        }
    }

    @Override
    protected void endManifest()
    {
        String pathPrefix = fileTransferReceiver.getDefaultReceivingroot();

        if (isDebugEnabled)
        {
            log.debug("Initial pass through manifest is complete. Post-processing has started.");
        }
        
        //Process any existing files that need to be replaced with new versions
        //Copy the collection first so we can safely remove processed files as we go. This helps
        //us later if we ever need to clean up following an error.
        Collection<NodeContext> filesToReplace = new ArrayList<NodeContext>(existingFilesToReplace.values());
        for (NodeContext fileToReplace : filesToReplace)
        {
            if (switchFile(fileToReplace.nodeId, fileToReplace.newParentId, fileToReplace.tempName, pathPrefix))
            {
                //Record this node in the list of temp files to be renamed
                tempFilesToRename.put(fileToReplace.nodeId, fileToReplace);
                existingFilesToReplace.remove(fileToReplace.nodeId);
            }
        }
        
        //Deal with any folders that need moving
        int folderCount;
        Set<String> processedFolders = new TreeSet<String>();
        while (!foldersToMove.isEmpty())
        {
            folderCount = foldersToMove.size();
            if (isDebugEnabled)
            {
                log.debug("Folders that need to be moved: " + folderCount);
            }
            for (NodeContext folder : foldersToMove.values())
            {
                FileTransferInfoEntity folderEntity = dbHelper.findFileTransferInfoByNodeRef(folder.nodeId);
                FileTransferInfoEntity parentEntity = dbHelper.findFileTransferInfoByNodeRef(folder.newParentId);
                if (moveFolder(folderEntity, parentEntity, folder.newName, pathPrefix))
                {
                    processedFolders.add(folder.nodeId);

                    //Log the effect that this has had...
                    if (folder.isNew)
                    {
                        logCreated(folder.nodeId, folder.newParentId, 
                                pathPrefix + folderEntity.getPath() + folderEntity.getContentName(), false);
                    }
                    else
                    {
                        logMoved(folder.nodeId, pathPrefix + folder.currentParentPath + folder.currentName, 
                                folder.newParentId, pathPrefix + folderEntity.getPath() + folderEntity.getContentName());
                    }
                }
            }
            for (String nodeId : processedFolders)
            {
                foldersToMove.remove(nodeId);
            }
            processedFolders.clear();
            if (folderCount == foldersToMove.size())
            {
                //We have reached a point where we have failed to process any more folders
                log.error("We failed to move any folders successfully on that loop.");
                break;
            }
        }
        
        //If any folders need to be deleted then handle them now
        removeDeletedFolders(pathPrefix);
        
        //If we are dealing with a "sync" transfer then we now need to work out
        //if there are any implicit deletions required and process them if so.
        if (isSync)
        {
            if (isDebugEnabled)
            {
                log.debug("Sync-mode transfer: started checking received data for implicit deletes...");
            }
            //For each 
            for (Map.Entry<String, Set<String>> parentChildEntry : parentChildMap.entrySet())
            {
                String parentId = parentChildEntry.getKey();
                Set<String> receivedChildren = parentChildEntry.getValue();
                List<FileTransferInfoEntity> currentChildren = dbHelper.findFileTransferInfoByParentNodeRef(parentId);
                for (FileTransferInfoEntity currentChild : currentChildren)
                {
                    if (!receivedChildren.remove(currentChild.getNodeRef()))
                    {
                        if (isDebugEnabled)
                        {
                            log.debug("Have not received data for existing node " + 
                                    currentChild.getNodeRef() + " (" + currentChild.getPath() + 
                                    currentChild.getContentName() + ")"); 
                        }
                        deleteNode(currentChild, pathPrefix);
                    }
                }
            }
            if (isDebugEnabled)
            {
                log.debug("Sync-mode transfer: finished checking received data for implicit deletes.");
            }
        }
        
        //Finally we need to run through all the new files with temporary names and rename them
        renameTempFiles(pathPrefix);
        
        log.info("Completed processing manifest file. It took " + 
                (System.currentTimeMillis() - processStartTime) + "ms");
    }

    private void removeDeletedFolders(String pathPrefix)
    {
        Collection<NodeContext> folders = new ArrayList<NodeContext>(foldersToDelete.values());
        for (NodeContext folderToDelete : folders)
        {
            FileTransferInfoEntity folderEntity = dbHelper.findFileTransferInfoByNodeRef(folderToDelete.nodeId);
            if (deleteNode(folderEntity, pathPrefix))
            {
                foldersToDelete.remove(folderToDelete.nodeId);
                logDeleted(folderToDelete.nodeId, pathPrefix + folderToDelete.currentParentPath
                        + folderToDelete.currentName);
            }
        }
    }

    //Delete the supplied node, going depth-first if it is a folder
    protected boolean deleteNode(FileTransferInfoEntity nodeToDelete, String pathPrefix)
    {
        boolean success = true;
        if (nodeToDelete != null)
        {
            String nodeId = nodeToDelete.getNodeRef();
            if (nodeToDelete.isFolder())
            {
                List<FileTransferInfoEntity> childrenList = 
                    dbHelper.findFileTransferInfoByParentNodeRef(nodeId);
                List<FileTransferInfoEntity> files = new ArrayList<FileTransferInfoEntity>(childrenList.size());
                for (FileTransferInfoEntity child : childrenList)
                {
                    //Go depth first. Process folders first and files after....
                    if (child.isFolder())
                    {
                        success &= deleteNode(child, pathPrefix);
                        if (!success) return false;
                    }
                    else
                    {
                        //Postpone processing of files
                        files.add(child);
                    }
                }
                //We've now finished deleting the child folders. Now we'll delete the child files... 
                for (FileTransferInfoEntity childFile : files)
                {
                    success &= deleteNode(childFile, pathPrefix);
                    if (!success) return false;
                }
            }
            String path = pathPrefix + nodeToDelete.getPath() + nodeToDelete.getContentName();
            File fileToDelete = new File(path);
            if (isDebugEnabled)
            {
                log.debug("Attempting to delete file/folder " + path);
            }
            if (fileToDelete.delete())
            {
                if (isDebugEnabled)
                {
                    log.debug("Successfully deleted file/folder. Updating database.");
                }
                dbHelper.deleteNodeByNodeRef(nodeId);
                logDeleted(nodeId, path);
            }
            else
            {
                success = false;
                log.error("Failed to delete file/folder.");
            }
        }
        return success;
    }

    private boolean moveFolder(FileTransferInfoEntity nodeEntity, FileTransferInfoEntity parentEntity, 
            String newName, String pathPrefix)
    {
        boolean successful = false;
        if (nodeEntity != null && parentEntity != null)
        {
            String currentParentPath = nodeEntity.getPath();
            String currentFolderName = nodeEntity.getContentName(); 
            String targetParentPath = parentEntity.getPath() + parentEntity.getContentName() + "/";
            File srcFolder = new File(pathPrefix + currentParentPath + currentFolderName);
            File destFolder = new File(pathPrefix + targetParentPath + newName);
            if (isDebugEnabled)
            {
                log.debug("Attempting to move folder \"" + srcFolder.getPath() + "\" to " + 
                        destFolder.getPath() + "\"");
            }
            successful = srcFolder.renameTo(destFolder);
            if (successful)
            {
                if (isDebugEnabled)
                {
                    log.debug("Successfully moved folder. Updating database.");
                }
                nodeEntity.setContentName(newName);
                nodeEntity.setPath(targetParentPath);
                nodeEntity.setParent(parentEntity.getNodeRef());
                dbHelper.updateFileTransferInfoByNodeRef(nodeEntity);
                dbHelper.updatePathOfChildren(nodeEntity.getNodeRef(), targetParentPath + newName + "/");
            }
            else
            {
                log.error("Failed to move folder \"" + srcFolder.getPath() + "\" to " + destFolder.getPath() + "\"");
            }
        }
        return successful;
    }

    private boolean switchFile(String nodeId, String newParentId, String targetFileName, String pathPrefix)
    {
        boolean successful = false;
        
        if (isDebugEnabled)
        {
            log.debug("Switching from old to new file for node " + nodeId);
        }
        
        //Update the database entry to point at the new file
        FileTransferInfoEntity nodeEntity = dbHelper.findFileTransferInfoByNodeRef(nodeId);
        FileTransferInfoEntity parentEntity = dbHelper.findFileTransferInfoByNodeRef(newParentId);
        if (nodeEntity != null && parentEntity != null)
        {
            String currentParentPath = nodeEntity.getPath();
            String currentFileName = nodeEntity.getContentName();
            String newParentPath = parentEntity.getPath() + parentEntity.getContentName() + "/"; 
            nodeEntity.setContentName(targetFileName);
            nodeEntity.setParent(newParentId);
            nodeEntity.setPath(newParentPath);
            dbHelper.updateFileTransferInfoByNodeRef(nodeEntity);
            if (isDebugEnabled)
            {
                log.debug("Switched file from \"" + currentParentPath + currentFileName + 
                        "\" to \"" + newParentPath + targetFileName + "\"");
                log.debug("Attempting to delete the original file");
            }
            
            //Delete the now-obsolete file
            String pathOfDeletedFile = pathPrefix + currentParentPath + currentFileName;
            File fileToDelete = new File(pathOfDeletedFile);
            successful = fileToDelete.delete();
            if (isDebugEnabled)
            {
                log.debug("Deletion of original file " + (successful ? "succeeded" : "FAILED"));
            }
        }
        return successful;
    }

    @Override
    protected void processNode(TransferManifestDeletedNode node) throws TransferProcessingException
    {
        TransferProgressMonitor monitor = fileTransferReceiver.getProgressMonitor();
        String nodeId = node.getNodeRef().toString();
        if (isDebugEnabled)
        {
            log.debug("Processing deleted node " + nodeId);
        }
        boolean nodeIsRoot = nodeId.equals(fileTransferReceiver.getTransferRootNode());
        if (nodeIsRoot)
        {
            monitor.logComment(getTransferId(), "We have received the root node. Skipping " + nodeId);
            return;
        }

        FileTransferInfoEntity nodeEntity = dbHelper.findFileTransferInfoByNodeRef(nodeId);
        if (nodeEntity != null)
        {
            NodeContext ctx = NodeContext.buildNodeContext(node, nodeEntity);
            String pathPrefix = fileTransferReceiver.getDefaultReceivingroot();
            String pathOfDeletedFile = pathPrefix + nodeEntity.getPath()
                    + nodeEntity.getContentName();
            File fileToDelete = new File(pathOfDeletedFile);
            if (fileToDelete.isDirectory())
            {
                if (isDebugEnabled)
                {
                    log.debug("Processing request to delete folder " + fileToDelete.getPath());
                }
                //Rename the folder to a temporary name to avoid any name conflicts that may occur later.
                FileTransferInfoEntity parentEntity = dbHelper.findFileTransferInfoByNodeRef(nodeEntity.getParent());
                if (moveFolder(nodeEntity, parentEntity, ctx.tempName, pathPrefix))
                {
                    if (isDebugEnabled)
                    {
                        log.debug("Recording request to delete folder " + fileToDelete.getPath());
                    }
                    recordFolderDelete(ctx);
                }
            }
            else
            {
                if (isDebugEnabled)
                {
                    log.debug("Processing request to delete file " + fileToDelete.getPath());
                }
                boolean success = fileToDelete.delete();
                if (success)
                {
                    if (isDebugEnabled)
                    {
                        log.debug("Successfully deleted " + fileToDelete.getPath());
                    }
                    dbHelper.deleteNodeByNodeRef(nodeId);
                    if (isDebugEnabled)
                    {
                        log.debug("Updated database to reflect deletion.");
                    }
                    monitor.logDeleted(getTransferId(), node.getNodeRef(), node.getNodeRef(), fileToDelete.getPath());
                }
                else
                {
                    throw new TransferFatalException(MSG_FAILED_TO_DELETE_FILE, new Object[] {nodeId, pathOfDeletedFile});
                }
            }
        }

    }

    private void recordFolderDelete(NodeContext nodeCtx)
    {
        foldersToDelete.put(nodeCtx.nodeId, nodeCtx);
    }

    @Override
    protected void processNode(TransferManifestNormalNode node) throws TransferProcessingException
    {
        TransferProgressMonitor monitor = fileTransferReceiver.getProgressMonitor();

        String nodeId = node.getNodeRef().toString();
        boolean nodeIsRoot = nodeId.equals(fileTransferReceiver.getTransferRootNode());
        String newParentId = node.getPrimaryParentAssoc().getParentRef().toString();

        FileTransferInfoEntity nodeEntity = dbHelper.findFileTransferInfoByNodeRef(nodeId);
        if (nodeIsRoot)
        {
            receivedFolderIds.add(nodeId);
            monitor.logComment(getTransferId(), "We have received the root node. Skipping " + nodeId);
            return;
        }
        if (!ContentModel.ASSOC_CONTAINS.equals(node.getPrimaryParentAssoc().getTypeQName()) ||
                !(ContentModel.TYPE_FOLDER.equals(node.getAncestorType()) ||
                        ContentModel.TYPE_CONTENT.equals(node.getAncestorType())))
        {
            monitor.logComment(getTransferId(), 
                    "Skipping node due to either: not content; not folder; or not cm:contains: " + nodeId);
            return;
        }
        
        FileTransferInfoEntity parentEntity = dbHelper.findFileTransferInfoByNodeRef(newParentId);

        NodeContext ctx = NodeContext.buildNodeContext(node, nodeEntity, parentEntity);

        if (!ctx.parentAlreadyExists)
        {
            //This is an orphan. Record it as such and come back to it later
            if (isDebugEnabled)
            {
                log.debug("We have not received the parent folder yet for " + nodeId + 
                        " (" + ctx.newParentId + "). Recording as an orphan.");
            }
            recordOrphan(ctx);
            return;
        } 
        
        processContext(ctx);
    }

    private void processContext(NodeContext ctx)
    {
        if (isDebugEnabled)
        {
            log.debug("Processing received node " + ctx.nodeId + " (" + ctx.newName + ")");
        }

        String tempName = ctx.tempName;
        String pathPrefix = fileTransferReceiver.getDefaultReceivingroot();
        recordForSyncMode(ctx);
        if (ctx.isFolder)
        {
            if (isDebugEnabled)
            {
                log.debug("This node is a folder");
            }
            if (ctx.isNew)
            {
                if (isDebugEnabled)
                {
                    log.debug("This node has never been received before");
                }
                //Create a new folder in the correct place but with a temporary name
                File newFolder = new File(pathPrefix + ctx.newParentPath, tempName);
                if (isDebugEnabled)
                {
                    log.debug("Attempting to create a new folder: " + newFolder.getPath());
                }
                boolean success = newFolder.mkdir();
                if (!success)
                {
                    if (isDebugEnabled)
                    {
                        log.debug("Failed to create a new folder: " + newFolder.getPath());
                    }
                    throw new TransferFatalException(MSG_FAILED_TO_CREATE_FOLDER, new Object[] {newFolder.getPath()});
                }
                if (isDebugEnabled)
                {
                    log.debug("Successfully created a new folder: " + newFolder.getPath());
                    log.debug("Recording in database");
                }
                //Store in the database
                dbHelper.createNodeInDB(ctx.nodeId, ctx.newParentId, ctx.newParentPath,
                        tempName, "", true);
                //Record the fact that we have to rename this folder and update the database later
                recordFolderMove(ctx);
                //Process any orphans that were waiting for this new folder
                processOrphans(ctx.nodeId);
            }
            else if (ctx.hasMoved)
            {
                //If a folder has been moved then we'll do nothing for now other than to make a note of 
                //it to process later
                if (isDebugEnabled)
                {
                    log.debug("This folder has moved. Recording for processing later.");
                }
                recordFolderMove(ctx);
            }
            else
            {
                if (isDebugEnabled)
                {
                    log.debug("This folder has been neither moved nor renamed. Skipping.");
                }
            }
        }
        else // isFile
        {
            if (isDebugEnabled)
            {
                log.debug("This node represents a file");
            }
            String newContentUrl = ctx.newContentUrl;
            boolean isContentModified = (ctx.isNew || (newContentUrl != null && !newContentUrl.equals(ctx.currentContentUrl)));
            if (ctx.isNew && newContentUrl != null)
            {
                if (isDebugEnabled)
                {
                    log.debug("This file has never been received before");
                }
                //Copy the staged content file to the correct folder with a temporary name
                //Note that we can't simply *move* the staged file, as it's possible that more than one node
                //shares the same content URL (in the case of a copied node, for instance)
                String contentKey = TransferCommons.URLToPartName(newContentUrl);
                File stagedContent = fileTransferReceiver.getContents().get(contentKey);
                File newFile = new File(pathPrefix + ctx.newParentPath, tempName);
                if (isDebugEnabled)
                {
                    log.debug("Attempting to copy from staged file to new file (" + 
                            stagedContent.getPath() + " to " + newFile.getPath() + ")");
                }
                try
                {
                    FileUtils.copyFile(stagedContent, newFile);
                }
                catch (IOException e)
                {
                    log.error("Failed to copy content", e);
                    throw new TransferFatalException(MSG_ERROR_COPYING_FILE, 
                            new Object[] {stagedContent.getPath(), newFile.getPath()}, e);
                }
                //Store in the database
                if (isDebugEnabled)
                {
                    log.debug("File copied successfully. Updating database.");
                }
                dbHelper.createNodeInDB(ctx.nodeId, ctx.newParentId, ctx.newParentPath,
                        tempName, newContentUrl, false);
                //Record the fact that we need to rename this file later
                tempFilesToRename.put(ctx.nodeId, ctx);
            }
            else if (!ctx.isNew)
            {
                if (isContentModified)
                {
                    if (isDebugEnabled)
                    {
                        log.debug("This file has been changed.");
                    }
                    //Copy the staged content file to the correct folder with a temporary name
                    String contentKey = TransferCommons.URLToPartName(newContentUrl);
                    File stagedContent = fileTransferReceiver.getContents().get(contentKey);
                    File newFile = new File(pathPrefix + ctx.newParentPath, tempName);
                    if (isDebugEnabled)
                    {
                        log.debug("Attempting to copy from current file to new file (" + 
                                stagedContent.getPath() + " to " + newFile.getPath() + ")");
                    }
                    try
                    {
                        FileUtils.copyFile(stagedContent, newFile);
                    }
                    catch (IOException e)
                    {
                        log.error("Failed to copy content", e);
                        throw new TransferFatalException(MSG_ERROR_COPYING_FILE, 
                                new Object[] {stagedContent.getPath(), newFile.getPath()}, e);
                    }
                    //Record the fact that we need to rename this file, delete the original file, and update the database later
                    if (isDebugEnabled)
                    {
                        log.debug("File copied successfully. Recording the need to switch files later.");
                    }
                    existingFilesToReplace.put(ctx.nodeId,ctx);
                }
                else if (ctx.hasMoved)
                {
                    //Copy the current content to the correct folder with a temporary name
                    if (isDebugEnabled)
                    {
                        log.debug("This file has been moved.");
                    }
                    File currentFile = new File(pathPrefix + ctx.currentParentPath, ctx.currentName);
                    File newFile = new File(pathPrefix + ctx.newParentPath, tempName);
                    if (isDebugEnabled)
                    {
                        log.debug("Attempting to copy from current file to new file (" + 
                                currentFile.getPath() + " to " + newFile.getPath() + ")");
                    }
                    try
                    {
                        FileUtils.copyFile(currentFile, newFile);
                    }
                    catch (IOException e)
                    {
                        log.error("Failed to copy content", e);
                        throw new TransferFatalException(MSG_ERROR_COPYING_FILE, 
                                new Object[] {currentFile.getPath(), newFile.getPath()}, e);
                    }
                    //Record the fact that we need to rename this file, delete the original file, and update the database later
                    if (isDebugEnabled)
                    {
                        log.debug("File copied successfully. Recording the need to switch files later.");
                    }
                    existingFilesToReplace.put(ctx.nodeId, ctx);
                }
                else
                {
                    //neither moved nor updated - we don't need to do anything with this file.
                    if (isDebugEnabled)
                    {
                        log.debug("This file has been neither moved nor changed. Skipping.");
                    }
                }
            }
        }
    }

    @Override
    protected void localHandleException(TransferManifestNode node, Throwable ex)
    {
        String pathPrefix = fileTransferReceiver.getDefaultReceivingroot();

        if (isDebugEnabled)
        {
            log.debug("Exception has occurred. Attempt to clean up has started.");
        }
        
        //Remove any temporary files that haven't been recorded in the database yet
        for (NodeContext fileToReplace : existingFilesToReplace.values())
        {
            FileTransferInfoEntity parentEntity = dbHelper.findFileTransferInfoByNodeRef(fileToReplace.newParentId);
            if (parentEntity != null)
            {
                String tempFilePath = pathPrefix + parentEntity.getPath() + 
                        parentEntity.getContentName() + "/" + fileToReplace.tempName;
                if (isDebugEnabled)
                {
                    log.debug("Attempting to delete temporary file: " + tempFilePath);
                }
                File tempFile = new File(tempFilePath);
                if (tempFile.exists())
                {
                    if (tempFile.delete())
                    {
                        if (isDebugEnabled)
                        {
                            log.debug("Successfully deleted temporary file: " + tempFilePath);
                        }
                    }
                    else
                    {
                        log.warn("Failed to delete temporary file " + tempFilePath + 
                                "\nIt is highly recommended that action be taken to delete this file.");
                    }
                }
            }
        }
        
        //If there are any folders that were part way through the process of being deleted then
        //have a last-ditch attempt to delete them now
        try
        {
            removeDeletedFolders(pathPrefix);
        }
        catch(Throwable t)
        {
            //Nothing really to do. 
        }
        
        //Finally we'll have a last-ditch attempt at renaming temporary files to their proper name.
        try
        {
            renameTempFiles(pathPrefix);
        }
        catch(Throwable t)
        {
            //Nothing really to do. 
        }
    }

    private void renameTempFiles(String pathPrefix)
    {
        Collection<NodeContext> filesToRename = new ArrayList<NodeContext>(tempFilesToRename.values());
        for (NodeContext file : filesToRename)
        {
            FileTransferInfoEntity fileEntity = dbHelper.findFileTransferInfoByNodeRef(file.nodeId);
            if (fileEntity != null)
            {
                File sourceFile = new File(pathPrefix + fileEntity.getPath() + file.tempName);
                File targetFile = new File(pathPrefix + fileEntity.getPath() + file.newName);
                if (isDebugEnabled)
                {
                    log.debug("Attempting to rename temp file \"" + sourceFile.getPath() + 
                            "\" to \"" + targetFile.getPath() + "\"");
                }
                if (sourceFile.renameTo(targetFile))
                {
                    if (isDebugEnabled)
                    {
                        log.debug("Rename succeeded. Updating database.");
                    }
                    fileEntity.setContentName(file.newName);
                    dbHelper.updateFileTransferInfoByNodeRef(fileEntity);
                    tempFilesToRename.remove(file.nodeId);
                    //Log the effect that this has had...
                    if (file.isNew)
                    {
                        logCreated(file.nodeId, file.newParentId, 
                                pathPrefix + fileEntity.getPath() + fileEntity.getContentName(), false);
                    }
                    else
                    {
                        logMoved(file.nodeId, pathPrefix + file.currentParentPath + file.currentName, 
                                file.newParentId, pathPrefix + fileEntity.getPath() + fileEntity.getContentName());
                    }
                }
                else
                {
                    if (isDebugEnabled)
                    {
                        log.error("Rename FAILED");
                    }
                }
            }
        }
    }

    private void recordForSyncMode(NodeContext ctx)
    {
        if (isSync)
        {
            if (ctx.isFolder)
            {
                receivedFolderIds.add(ctx.nodeId);
            }
            else
            {
                receivedFileIds.add(ctx.nodeId);
            }
            String parentId = ctx.newParentId;
            Set<String> children = parentChildMap.get(parentId);
            if (children == null)
            {
                children = new TreeSet<String>();
                parentChildMap.put(parentId,children);
            }
            children.add(ctx.nodeId);
        }
    }

    private void recordFolderMove(NodeContext ctx)
    {
        foldersToMove.put(ctx.nodeId, ctx);
    }

    private void processOrphans(String nodeId)
    {
        if (isDebugEnabled)
        {
            log.debug("Processing orphans for folder " + nodeId);
        }
        List<NodeContext> relevantOrphans = orphans.get(nodeId);
        if (relevantOrphans != null)
        {
            FileTransferInfoEntity parentEntity = dbHelper.findFileTransferInfoByNodeRef(nodeId);
            String parentPath = parentEntity.getPath() + parentEntity.getContentName() + "/"; 
            for (NodeContext orphan : relevantOrphans)
            {
                orphan.newParentPath = parentPath;
                processContext(orphan);
            }
            orphans.remove(nodeId);
        }
    }

    private void recordOrphan(NodeContext ctx)
    {
        List<NodeContext> existingOrphans = orphans.get(ctx.newParentId);
        if (existingOrphans == null)
        {
            existingOrphans = new ArrayList<NodeContext>();
            orphans.put(ctx.newParentId, existingOrphans);
        }
        existingOrphans.add(ctx);
    }

    protected void logCreated(String sourceNode, String newParentNode, String parentPath, boolean orphan)
    {
        NodeRef srcNodeRef = new NodeRef(sourceNode);
        fileTransferReceiver.getProgressMonitor().logCreated(getTransferId(), srcNodeRef, srcNodeRef,
                new NodeRef(newParentNode), parentPath, orphan);
    }

    protected void logDeleted(String sourceNode, String parentPath)
    {
        NodeRef srcNodeRef = new NodeRef(sourceNode);
        fileTransferReceiver.getProgressMonitor().logDeleted(getTransferId(), srcNodeRef, srcNodeRef, parentPath);
    }

    protected void logUpdated(String sourceNode, String newPath)
    {
        NodeRef srcNodeRef = new NodeRef(sourceNode);
        fileTransferReceiver.getProgressMonitor().logUpdated(getTransferId(), srcNodeRef, srcNodeRef, newPath);
    }

    protected void logMoved(String sourceNode, String oldPath, String newParent, String newPath)
    {
        NodeRef srcNodeRef = new NodeRef(sourceNode);
        fileTransferReceiver.getProgressMonitor().logMoved(getTransferId(), srcNodeRef, srcNodeRef, oldPath,
                new NodeRef(newParent), newPath);
    }

    /**
     * A simple data holder that pulls together useful information about a node in one place.
     * @author Brian Remmington
     */
    private static class NodeContext
    {
        private static int renamingCounter = 1;

        public String nodeId;
        public boolean isFolder;
        public boolean isNew;
        public boolean isRenamed;
        public boolean hasMoved;
        public boolean parentHasChanged;
        public boolean parentAlreadyExists;

        public String currentName;
        @SuppressWarnings("unused")
        public String currentParentId;
        public String currentParentPath;
        public String currentContentUrl;

        public String newName;
        public String newParentId;
        public String newParentPath;
        public String newContentUrl;

        public String tempName;


        public static String getNextTempName()
        {
            return ".ftr" + (renamingCounter++);
        }
        
        public static NodeContext buildNodeContext(TransferManifestNormalNode node, FileTransferInfoEntity nodeEntity, 
                FileTransferInfoEntity parentEntity)
        {
            NodeContext result = new NodeContext();
            //Pull some useful information out of the supplied node object
            result.newName = (String) node.getProperties().get(ContentModel.PROP_NAME);
            result.nodeId = node.getNodeRef().toString();
            result.isFolder = ContentModel.TYPE_FOLDER.equals(node.getAncestorType());
            result.newParentId = node.getPrimaryParentAssoc().getParentRef().toString();
    
            //Look up the node id in our database and extract some info from what we find
            result.isNew = (nodeEntity == null);
            result.isRenamed = (!result.isNew && !result.newName.equals(nodeEntity.getContentName()));
            result.parentHasChanged = (!result.isNew && !result.newParentId.equals(nodeEntity.getParent()));
            result.hasMoved = result.parentHasChanged || result.isRenamed;
            result.currentParentPath = result.isNew ? null : nodeEntity.getPath();
            result.currentParentId = result.isNew ? null : nodeEntity.getParent();
            result.currentContentUrl = result.isNew ? null : nodeEntity.getContentUrl();
            result.currentName = result.isNew ? null : nodeEntity.getContentName();
    
            //Look up the target parent node id in our database and extract some info from what we find
            result.parentAlreadyExists = (parentEntity != null);
            result.newParentPath = result.parentAlreadyExists ? (parentEntity.getPath() + parentEntity.getContentName() + "/") : null;
            result.tempName = getNextTempName();
            
            ContentData contentData = (ContentData) node.getProperties().get(ContentModel.PROP_CONTENT);
            result.newContentUrl = "";
            if (contentData != null)
            {
                result.newContentUrl = contentData.getContentUrl();
            }

            return result;
        }

        public static NodeContext buildNodeContext(TransferManifestDeletedNode node, FileTransferInfoEntity nodeEntity)
        {
            NodeContext result = new NodeContext();
            //Pull some useful information out of the supplied node object
            result.nodeId = node.getNodeRef().toString();
    
            //Look up the node id in our database and extract some info from what we find
            result.isNew = (nodeEntity == null);
            result.currentParentPath = result.isNew ? null : nodeEntity.getPath();
            result.currentParentId = result.isNew ? null : nodeEntity.getParent();
            result.currentContentUrl = result.isNew ? null : nodeEntity.getContentUrl();
            result.currentName = result.isNew ? null : nodeEntity.getContentName();
    
            //Look up the target parent node id in our database and extract some info from what we find
            result.tempName = getNextTempName();
            
            return result;
        }
    }
}
