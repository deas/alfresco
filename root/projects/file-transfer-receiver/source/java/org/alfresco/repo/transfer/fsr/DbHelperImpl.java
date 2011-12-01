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

import java.util.List;

import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DbHelperImpl implements DbHelper
{
    private static final Log log = LogFactory.getLog(DbHelperImpl.class); 
    private FileTransferInfoDAO fileTransferInfoDAO;
    private TransactionService transactionService;
    private String sourceRepoId;

    public DbHelperImpl(FileTransferInfoDAO fileTransferInfoDAO, TransactionService transactionService,
            String sourceRepoId)
    {
        super();
        this.fileTransferInfoDAO = fileTransferInfoDAO;
        this.transactionService = transactionService;
        this.sourceRepoId = sourceRepoId;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.transfer.fsr.DbHelper#findFileTransferInfoByNodeRef(java.lang.String)
     */
    public FileTransferInfoEntity findFileTransferInfoByNodeRef(final String nodeRef)
    {
        RetryingTransactionHelper txHelper = transactionService.getRetryingTransactionHelper();

        FileTransferInfoEntity fileTransferInfoEntity = txHelper.doInTransaction(
                new RetryingTransactionHelper.RetryingTransactionCallback<FileTransferInfoEntity>()
                    {
                        public FileTransferInfoEntity execute() throws Throwable
                        {
                            FileTransferInfoEntity fileTransferInfoEntity = 
                                fileTransferInfoDAO.findFileTransferInfoByNodeRef(nodeRef);

                            return fileTransferInfoEntity;

                        }
                    }, true, false);
        return fileTransferInfoEntity;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.transfer.fsr.DbHelper#findFileTransferInfoByParentNodeRef(java.lang.String)
     */
    public List<FileTransferInfoEntity> findFileTransferInfoByParentNodeRef(final String nodeRef)
    {
        RetryingTransactionHelper txHelper = transactionService.getRetryingTransactionHelper();

        List<FileTransferInfoEntity> fileTransferInfoEntityList = txHelper.doInTransaction(
                new RetryingTransactionHelper.RetryingTransactionCallback<List<FileTransferInfoEntity>>()
                    {
                        public List<FileTransferInfoEntity> execute() throws Throwable
                        {
                            List<FileTransferInfoEntity> fileTransferInfoEntityList = 
                                fileTransferInfoDAO.findFileTransferInfoByParentNodeRef(nodeRef);
                            return fileTransferInfoEntityList;
                        }
                    }, true, false);
        return fileTransferInfoEntityList;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.transfer.fsr.DbHelper#updateFileTransferInfoByNodeRef(org.alfresco.repo.transfer.fsr.FileTransferInfoEntity)
     */
    public void updateFileTransferInfoByNodeRef(final FileTransferInfoEntity modifiedEntity)
    {
        RetryingTransactionHelper txHelper = transactionService.getRetryingTransactionHelper();

        txHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                public Void execute() throws Throwable
                {
                    fileTransferInfoDAO.updateFileTransferInfoByNodeRef(modifiedEntity);
                    return null;
                }
            }, false, false);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.transfer.fsr.DbHelper#deleteNodeByNodeRef(java.lang.String)
     */
    public void deleteNodeByNodeRef(final String nodeRef)
    {
        RetryingTransactionHelper txHelper = transactionService.getRetryingTransactionHelper();

        txHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                public Void execute() throws Throwable
                {
                    fileTransferInfoDAO.deleteFileTransferInfoByNodeRef(nodeRef);
                    return null;
                }
            }, false, false);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.transfer.fsr.DbHelper#createNodeInDB(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    public void createNodeInDB(final String nodeRef, final String parentNodeRef, final String path, final String name, 
            final String contentUrl, final boolean isFolder)
    {
        RetryingTransactionHelper txHelper = transactionService.getRetryingTransactionHelper();
        txHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                public Void execute() throws Throwable
                {
                    fileTransferInfoDAO.createFileTransferInfo(nodeRef, parentNodeRef, path, name,
                            contentUrl, isFolder, sourceRepoId);
                    return null;
                }
            }, false, false);
    }
    
    public void updatePathOfChildren(final String parentId, final String parentPath)
    {
        RetryingTransactionHelper txHelper = transactionService.getRetryingTransactionHelper();
        txHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                private void updatePath(String parentId, String parentPath)
                {
                    fileTransferInfoDAO.updatePathOfChildren(parentId, parentPath);
                    List<FileTransferInfoEntity> children = findFileTransferInfoByParentNodeRef(parentId);
                    for (FileTransferInfoEntity child : children)
                    {
                        if (child.isFolder())
                        {
                            String childPath = parentPath + child.getContentName() + "/";
                            if (log.isDebugEnabled())
                            {
                                log.debug("Updating child paths of node " + child.getNodeRef() + " (" + 
                                        child.getContentName() + "). New parent path for children is " + childPath);
                            }
                            updatePath(child.getNodeRef(), childPath);
                        }
                    }
                }
            
                public Void execute() throws Throwable
                {
                    updatePath(parentId, parentPath);
                    return null;
                }
            }, false, false);
    }
}
