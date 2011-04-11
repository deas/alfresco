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
package org.alfresco.module.vti.handler.alfresco;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.module.vti.handler.MethodHandler;
import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.handler.alfresco.v3.AlfrescoMethodHandler;
import org.alfresco.module.vti.metadata.dialog.DialogMetaInfo;
import org.alfresco.module.vti.metadata.dialog.DialogsMetaInfo;
import org.alfresco.module.vti.metadata.dic.DocumentStatus;
import org.alfresco.module.vti.metadata.dic.PutOption;
import org.alfresco.module.vti.metadata.dic.RenameOption;
import org.alfresco.module.vti.metadata.dic.VtiSort;
import org.alfresco.module.vti.metadata.dic.VtiSortField;
import org.alfresco.module.vti.metadata.model.DocMetaInfo;
import org.alfresco.module.vti.metadata.model.DocsMetaInfo;
import org.alfresco.module.vti.metadata.model.Document;
import org.alfresco.repo.version.VersionModel;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.lock.LockType;
import org.alfresco.service.cmr.lock.NodeLockedException;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.cmr.version.VersionType;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Alfresco implementation of MethodHandler interface
 * 
 * @author EugeneZh
 */
public abstract class AbstractAlfrescoMethodHandler implements MethodHandler
{

    private static final Log logger = LogFactory.getLog(AbstractAlfrescoMethodHandler.class);

    private NodeService nodeService;
    private CheckOutCheckInService checkOutCheckInService;
    private FileFolderService fileFolderService;
    private PermissionService permissionService;
    private AuthenticationService authenticationService;
    private VersionService versionService;
    private LockService lockService;
    private ContentService contentService;
    private TransactionService transactionService;
    private MimetypeService mimetypeService;

    private VtiDocumentHepler documentHelper;
    private VtiPathHelper pathHelper;

    /**
     * Set transaction service
     * 
     * @param transactionService the transaction service to set ({@link TransactionService})
     */
    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    /**
     * Set content service
     * 
     * @param contentService the content service to set ({@link ContentService})
     */
    public void setContentService(ContentService contentService)
    {
        this.contentService = contentService;
    }

    /**
     * Set lock service
     * 
     * @param lockService the lock service to set ({@link LockService})
     */
    public void setLockService(LockService lockService)
    {
        this.lockService = lockService;
    }

    /**
     * Set node service
     * 
     * @param nodeService the node service to set ({@link NodeService})
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * Set checkout-checkin service
     * 
     * @param checkOutCheckInService the checkout-checkin service to set ({@link CheckOutCheckInService})
     */
    public void setCheckOutCheckInService(CheckOutCheckInService checkOutCheckInService)
    {
        this.checkOutCheckInService = checkOutCheckInService;
    }

    /**
     * Set file folder service
     * 
     * @param fileFolderService the file folder service to set ({@link FileFolderService})
     */
    public void setFileFolderService(FileFolderService fileFolderService)
    {
        this.fileFolderService = fileFolderService;
    }

    /**
     * Set permission service
     * 
     * @param permissionService the permission service to set ({@link PermissionService})
     */
    public void setPermissionService(PermissionService permissionService)
    {
        this.permissionService = permissionService;
    }

    /**
     * Set authentication service
     * 
     * @param authenticationService the authentication service to set ({@link AuthenticationService})
     */
    public void setAuthenticationService(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }

    /**
     * Set version service
     * 
     * @param versionService the version service to set ({@link VersionService})
     */
    public void setVersionService(VersionService versionService)
    {
        this.versionService = versionService;
    }

    public void setMimetypeService(MimetypeService mimetypeService)
    {
        this.mimetypeService = mimetypeService;
    }

    public MimetypeService getMimetypeService()
    {
        return mimetypeService;
    }

    /**
     * Set document helper
     * 
     * @param checkoutHelper the document helper to set ({@link VtiDocumentHepler})
     */
    public void setDocumentHelper(VtiDocumentHepler checkoutHelper)
    {
        this.documentHelper = checkoutHelper;
    }

    /**
     * Set path helper
     * 
     * @param pathHelper the path helper to set ({@link VtiPathHelper})
     */
    public void setPathHelper(VtiPathHelper pathHelper)
    {
        this.pathHelper = pathHelper;
    }

    /**
     * Get node service
     * 
     * @return NodeService node service
     */
    public NodeService getNodeService()
    {
        return nodeService;
    }

    /**
     * Get check out check in service
     * 
     * @return CheckOutCheckInService check out check in service
     */
    public CheckOutCheckInService getCheckOutCheckInService()
    {
        return checkOutCheckInService;
    }

    /**
     * Get file folder service
     * 
     * @return FileFolderService file folder service
     */
    public FileFolderService getFileFolderService()
    {
        return fileFolderService;
    }

    /**
     * Get permission service
     * 
     * @return PermissionService permission service
     */
    public PermissionService getPermissionService()
    {
        return permissionService;
    }

    /**
     * Get authentication service
     * 
     * @return AuthenticationService authentication service
     */
    public AuthenticationService getAuthenticationService()
    {
        return authenticationService;
    }

    /**
     * Get version service
     * 
     * @return VersionService version service
     */
    public VersionService getVersionService()
    {
        return versionService;
    }

    /**
     * Get lock service
     * 
     * @return LockService lock service
     */
    public LockService getLockService()
    {
        return lockService;
    }

    /**
     * Get content service
     * 
     * @return ContentService content service
     */
    public ContentService getContentService()
    {
        return contentService;
    }

    /**
     * Get transaction service
     * 
     * @return TransactionService transaction service
     */
    public TransactionService getTransactionService()
    {
        return transactionService;
    }

    /**
     * Get document helper
     * 
     * @return VtiDocumentHepler document helper
     */
    public VtiDocumentHepler getDocumentHelper()
    {
        return documentHelper;
    }

    /**
     * Get path helper
     * 
     * @return VtiPathHelper path helper
     */
    public VtiPathHelper getPathHelper()
    {
        return pathHelper;
    }

    /**
     * @see org.alfresco.module.vti.handler.MethodHandler#checkInDocument(java.lang.String, java.lang.String, java.lang.String, boolean, java.util.Date, boolean)
     */
    public DocMetaInfo checkInDocument(String serviceName, String documentName, String comment, boolean keepCheckedOut, Date timeCheckedout, boolean validateWelcomeNames)
    {
        // timeCheckedout ignored
        if (logger.isDebugEnabled())
        {
            logger.debug("Checkin document: " + documentName + ". Site name: " + serviceName);
        }
        
        for(String urlPart : documentName.split("/"))
        {
            if (VtiUtils.hasIllegalCharacter(urlPart))
            {
                throw new VtiHandlerException(VtiHandlerException.HAS_ILLEGAL_CHARACTERS);
            }            
        }
        
        FileInfo fileFileInfo = getPathHelper().resolvePathFileInfo(serviceName + "/" + documentName);
        AlfrescoMethodHandler.assertValidFileInfo(fileFileInfo);
        AlfrescoMethodHandler.assertFile(fileFileInfo);
        FileInfo documentFileInfo = fileFileInfo;

        DocumentStatus documentStatus = getDocumentHelper().getDocumentStatus(documentFileInfo.getNodeRef());

        // if document isn't checked out then throw exception
        if (VtiDocumentHepler.isCheckedout(documentStatus) == false)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Document is not checked out.");
            }
            throw new VtiHandlerException(VtiHandlerException.DOC_NOT_CHECKED_OUT);
        }

        // if document is checked out, but user isn't owner, then throw exception
        if (VtiDocumentHepler.isCheckoutOwner(documentStatus) == false)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Unable to perform check in. Not an owner!!!");
            }
            throw new VtiHandlerException(VtiHandlerException.DOC_CHECKED_OUT);
        }

        UserTransaction tx = getTransactionService().getUserTransaction(false);
        try
        {
            tx.begin();

            if (VtiDocumentHepler.isLongCheckedout(documentStatus))
            {
                // long-term checkout
                Map<String, Serializable> props = new HashMap<String, Serializable>(1, 1.0f);
                props.put(Version.PROP_DESCRIPTION, comment);
                props.put(VersionModel.PROP_VERSION_TYPE, VersionType.MAJOR);

                NodeRef resultNodeRef = getCheckOutCheckInService().checkin(getCheckOutCheckInService().getWorkingCopy(documentFileInfo.getNodeRef()), props, null, keepCheckedOut);

                documentFileInfo = getFileFolderService().getFileInfo(resultNodeRef);
            }
            else
            {
                // short-term checkout
                getLockService().unlock(documentFileInfo.getNodeRef());
                documentFileInfo = getFileFolderService().getFileInfo(documentFileInfo.getNodeRef());
            }

            tx.commit();
            if (logger.isDebugEnabled())
            {
                logger.debug("Document successfully checked in.");
            }
        }
        catch (Throwable e)
        {
            try
            {
                tx.rollback();
            }
            catch (Exception tex)
            {
            }
            throw VtiExceptionUtils.createRuntimeException(e);
        }

        DocMetaInfo docMetaInfo = new DocMetaInfo(false);
        docMetaInfo.setPath(documentName);
        setDocMetaInfo(documentFileInfo, docMetaInfo);

        return docMetaInfo;
    }

    /**
     * @see org.alfresco.module.vti.handler.MethodHandler#checkOutDocument(java.lang.String, java.lang.String, int, int, boolean)
     */
    public DocMetaInfo checkOutDocument(String serviceName, String documentName, int force, int timeout, boolean validateWelcomeNames)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Checkout document: " + documentName + ". Site name: " + serviceName);
        }
        
        for(String urlPart : documentName.split("/"))
        {
            if (VtiUtils.hasIllegalCharacter(urlPart))
            {
                throw new VtiHandlerException(VtiHandlerException.HAS_ILLEGAL_CHARACTERS);
            }            
        }
        
        FileInfo fileFileInfo = getPathHelper().resolvePathFileInfo(serviceName + "/" + documentName);
        AlfrescoMethodHandler.assertValidFileInfo(fileFileInfo);
        AlfrescoMethodHandler.assertFile(fileFileInfo);
        FileInfo documentFileInfo = fileFileInfo;

        checkout(documentFileInfo, timeout);

        documentFileInfo = getFileFolderService().getFileInfo(documentFileInfo.getNodeRef());
        DocMetaInfo docMetaInfo = new DocMetaInfo(false);
        setDocMetaInfo(documentFileInfo, docMetaInfo);
        docMetaInfo.setPath(getPathHelper().toUrlPath(documentFileInfo));

        return docMetaInfo;
    }

    /**
     * @see org.alfresco.module.vti.handler.MethodHandler#createDirectory(java.lang.String, org.alfresco.module.vti.metadata.model.DocMetaInfo)
     */
    public boolean createDirectory(String serviceName, DocMetaInfo dir)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Creating directory: '" + dir.getPath() + "' in site: " + serviceName);
        }
        
        for (String urlPart : dir.getPath().split("/"))
        {
            if (VtiUtils.hasIllegalCharacter(urlPart))
            {
                throw new VtiHandlerException(VtiHandlerException.HAS_ILLEGAL_CHARACTERS);
            }            
        }
        
        Pair<String, String> parentChildPaths = VtiPathHelper.splitPathParentChild(serviceName + "/" + dir.getPath());

        String parentName = parentChildPaths.getFirst();
        String childFolderName = parentChildPaths.getSecond();

        if (childFolderName.length() == 0)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Invalid name for new directory. Name should not be empty.");
            }
            throw new VtiHandlerException(VtiHandlerException.BAD_URL);
        }

        FileInfo parentFileInfo = getPathHelper().resolvePathFileInfo(parentName);
        if (parentFileInfo == null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Parent folder not exists.");
            }
            throw new VtiHandlerException(VtiHandlerException.PRIMARY_PARENT_NOT_EXIST);
        }

        AlfrescoMethodHandler.assertFolder(parentFileInfo);

        UserTransaction tx = getTransactionService().getUserTransaction(false);
        try
        {
            tx.begin();

            getFileFolderService().create(parentFileInfo.getNodeRef(), childFolderName, ContentModel.TYPE_FOLDER);

            tx.commit();

            if (logger.isDebugEnabled())
            {
                logger.debug("Folder successfully was created.");
            }
        }
        catch (Throwable e)
        {
            try
            {
                tx.rollback();
            }
            catch (Exception tex)
            {
            }

            if (e instanceof FileExistsException)
            {
                throw new VtiHandlerException(VtiHandlerException.FOLDER_ALREADY_EXISTS);
            }

            throw VtiExceptionUtils.createRuntimeException(e);
        }

        return true;
    }

    /**
     * @see org.alfresco.module.vti.handler.MethodHandler#getDocsMetaInfo(java.lang.String, boolean, boolean, boolean, java.util.List)
     */
    public DocsMetaInfo getDocsMetaInfo(String serviceName, boolean listHiddenDocs, boolean listLinkInfo, boolean validateWelcomeNames, List<String> urlList)
    {
        DocsMetaInfo docsMetaInfo = new DocsMetaInfo();

        if (urlList.isEmpty())
        {
            urlList.add("");
        }

        for (String url : urlList)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Retrieving meta-info for document: '" + url + "' from site: " + serviceName);
            }
            FileInfo fileInfo = getPathHelper().resolvePathFileInfo(serviceName + "/" + url);

            if (fileInfo != null && fileInfo.isLink() == false)
            {
                DocMetaInfo docMetaInfo = new DocMetaInfo(fileInfo.isFolder());
                setDocMetaInfo(fileInfo, docMetaInfo);
                docMetaInfo.setPath(url);

                if (fileInfo.isFolder())
                {
                    docsMetaInfo.getFolderMetaInfoList().add(docMetaInfo);
                }
                else
                {
                    docsMetaInfo.getFileMetaInfoList().add(docMetaInfo);
                }
            }
            else
            {
                DocMetaInfo docMetaInfo = new DocMetaInfo(false);
                docMetaInfo.setPath(url);
                docsMetaInfo.getFailedUrls().add(docMetaInfo);
            }
        }

        return docsMetaInfo;
    }

    /**
     * @see org.alfresco.module.vti.handler.MethodHandler#getFileOpen(java.lang.String, java.lang.String, java.util.List, java.lang.String,
     *      org.alfresco.module.vti.metadata.dic.VtiSortField, org.alfresco.module.vti.metadata.dic.VtiSort, java.lang.String)
     */
    public DialogsMetaInfo getFileOpen(String siteUrl, String location, List<String> fileDialogFilterValue, String rootFolder, VtiSortField sortField, VtiSort sortDir, String view)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Generating list of items for site: '" + siteUrl + "' and location: " + location);
        }
        FileInfo folderFileInfo;
        folderFileInfo = getPathHelper().resolvePathFileInfo(siteUrl + "/" + location);

        AlfrescoMethodHandler.assertValidFileInfo(folderFileInfo);
        AlfrescoMethodHandler.assertFolder(folderFileInfo);
        FileInfo sourceFileInfo = folderFileInfo;

        DialogsMetaInfo result = new DialogsMetaInfo();

        for (FileInfo fileInfo : getFileFolderService().list(sourceFileInfo.getNodeRef()))
        {
            if (fileInfo.isFolder())
            {
                result.getDialogMetaInfoList().add(getDialogMetaInfo(fileInfo));                
            }
            else if (getNodeService().hasAspect(fileInfo.getNodeRef(), ContentModel.ASPECT_WORKING_COPY) == false
                    && VtiDocumentHepler.applyFilters(fileInfo.getName(), fileDialogFilterValue))
            {                
               result.getDialogMetaInfoList().add(getDialogMetaInfo(fileInfo));                
            }
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Retrieved " + result.getDialogMetaInfoList().size() + " items");
        }
        return result;
    }

    /**
     * @see org.alfresco.module.vti.handler.MethodHandler#getServertimeZone()
     */
    public String getServertimeZone()
    {
        return new SimpleDateFormat("Z").format(new Date());
    }

    /**
     * @see org.alfresco.module.vti.handler.MethodHandler#getUserName()
     */
    public String getUserName()
    {
        return getAuthenticationService().getCurrentUserName();
    }

    /**
     * @see org.alfresco.module.vti.handler.MethodHandler#moveDocument(java.lang.String, java.lang.String, java.lang.String, java.util.List, java.util.EnumSet, java.util.EnumSet,
     *      boolean, boolean)
     */
    public DocsMetaInfo moveDocument(String serviceName, String oldURL, String newURL, List<String> urlList, EnumSet<RenameOption> renameOptionSet,
            EnumSet<PutOption> putOptionSet, boolean docopy, boolean validateWelcomeNames)
    {
        // urlList ignored
        // validateWelcomeNames ignored

        FileInfo sourceFileInfo = getPathHelper().resolvePathFileInfo(serviceName + "/" + oldURL);

        AlfrescoMethodHandler.assertValidFileInfo(sourceFileInfo);

        if (docopy == false)
        {
            if (sourceFileInfo.isFolder() == false)
            {
                AlfrescoMethodHandler.assertRemovableDocument(getDocumentHelper().getDocumentStatus(sourceFileInfo.getNodeRef()));
            }
        }
        
        for (String urlPart : newURL.split("/"))
        {
            if (VtiUtils.hasIllegalCharacter(urlPart))
            {
                throw new VtiHandlerException(VtiHandlerException.HAS_ILLEGAL_CHARACTERS);
            }
        }
        
        Pair<String, String> parentChildPaths = VtiPathHelper.splitPathParentChild(serviceName + "/" + newURL);
        String destName = parentChildPaths.getSecond();
        if (destName.length() == 0)
        {
            throw new VtiHandlerException(VtiHandlerException.BAD_URL);
        }

        UserTransaction tx = getTransactionService().getUserTransaction(false);
        try
        {
            tx.begin();

            // determining existence of parent folder for newURL
            String parentPath = parentChildPaths.getFirst();
            FileInfo destParentFolder = getPathHelper().resolvePathFileInfo(parentPath);
            if (destParentFolder == null)
            {
                // if "createdir" option presents then create only primary parent of new location
                if (putOptionSet.contains(PutOption.createdir) || renameOptionSet.contains(RenameOption.createdir))
                {
                    destParentFolder = createOnlyLastFolder(parentPath);
                }

                if (destParentFolder == null)
                {
                    throw new VtiHandlerException(VtiHandlerException.PRIMARY_PARENT_NOT_EXIST);
                }
            }

            // determining existence of folder or file with newURL
            FileInfo destFileInfo = getPathHelper().resolvePathFileInfo(destParentFolder, destName);
            if (destFileInfo != null)
            {
                // if "overwrite" option presents then overwrite existing file or folder
                if (putOptionSet.contains(PutOption.overwrite))
                {
                    if (destFileInfo.isFolder() == false)
                    {
                        DocumentStatus destDocumentStatus = getDocumentHelper().getDocumentStatus(destFileInfo.getNodeRef());
                        AlfrescoMethodHandler.assertRemovableDocument(destDocumentStatus);

                        // if destination document is long-term checked out then delete working copy
                        if (destDocumentStatus.equals(DocumentStatus.LONG_CHECKOUT_OWNER))
                        {
                            NodeRef workingCopyNodeRef = getCheckOutCheckInService().getWorkingCopy(destFileInfo.getNodeRef());
                            getFileFolderService().delete(workingCopyNodeRef); // beforeDeleteNode policy unlocks original node
                        }
                    }

                    getFileFolderService().delete(destFileInfo.getNodeRef());
                }
                else
                {
                    throw new VtiHandlerException(VtiHandlerException.FILE_ALREADY_EXISTS);
                }
            }

            if (docopy)
            {

                if (logger.isDebugEnabled())
                {
                    logger.debug("Copy document: " + oldURL + " to new location: " + newURL + " in site: " + serviceName);
                }
                destFileInfo = getFileFolderService().copy(sourceFileInfo.getNodeRef(), destParentFolder.getNodeRef(), destName);
            }
            else
            {
                if (sourceFileInfo.isFolder() == false)
                {
                    DocumentStatus sourceDocumentStatus = getDocumentHelper().getDocumentStatus(sourceFileInfo.getNodeRef());

                    // if source document is long-term checked out then delete working copy
                    if (sourceDocumentStatus.equals(DocumentStatus.LONG_CHECKOUT_OWNER))
                    {
                        NodeRef workingCopyNodeRef = getCheckOutCheckInService().getWorkingCopy(sourceFileInfo.getNodeRef());
                        getFileFolderService().delete(workingCopyNodeRef); // beforeDeleteNode policy unlocks original node
                    }
                }
                
                NodeRef sourceParentRef = null;
                if (oldURL != null)
                {
                    // current parent
                    Pair<String, String> sourceParentPaths = VtiPathHelper.splitPathParentChild(serviceName + "/" + oldURL);
                    String sourceParentPath = sourceParentPaths.getFirst();
                    FileInfo sourceParent = getPathHelper().resolvePathFileInfo(sourceParentPath);
                    if (sourceParent != null)
                    {
                        sourceParentRef = sourceParent.getNodeRef();
                    }
                }
                
                if (logger.isDebugEnabled())
                {
                    logger.debug("Move document: " + oldURL + " to new location: " + newURL + " in site: " + serviceName);
                }
                destFileInfo = getFileFolderService().move(sourceFileInfo.getNodeRef(), sourceParentRef, destParentFolder.getNodeRef(), destName);
            }

            tx.commit();

            DocMetaInfo docMetaInfo = new DocMetaInfo(destFileInfo.isFolder());
            docMetaInfo.setPath(newURL);
            setDocMetaInfo(destFileInfo, docMetaInfo);

            DocsMetaInfo result = new DocsMetaInfo();

            if (destFileInfo.isFolder())
            {
                result.getFolderMetaInfoList().add(docMetaInfo);
                addFileFoldersRecursive(destFileInfo, result);
            }
            else
            {
                result.getFileMetaInfoList().add(docMetaInfo);
            }

            return result;
        }
        catch (Throwable e)
        {
            try
            {
                tx.rollback();
            }
            catch (Exception tex)
            {
            }

            if (e instanceof FileNotFoundException)
            {
                throw new VtiHandlerException(VtiHandlerException.BAD_URL);
            }

            if (e instanceof NodeLockedException)
            {
                // only if source or destination folder is locked
                throw new VtiHandlerException(VtiHandlerException.REMOVE_DIRECTORY);
            }

            throw VtiExceptionUtils.createRuntimeException(e);
        }
    }

    /**
     * @see org.alfresco.module.vti.handler.MethodHandler#putDocument(java.lang.String, org.alfresco.module.vti.metadata.model.Document, java.util.EnumSet, java.lang.String,
     *      boolean, boolean)
     */
    public DocMetaInfo putDocument(String serviceName, Document document, EnumSet<PutOption> putOptionSet, String comment, boolean keepCheckedOut, boolean validateWelcomeNames)
    {
        // keepCheckedOut ignored
        // validateWelcomeNames

        // 'atomic' put-option : ignored
        // 'checkin' put-option : ignored
        // 'checkout' put-option : ignored
        // 'createdir' put-option : implemented
        // 'edit' put-option : implemented
        // 'forceversions' put-option : ignored
        // 'migrationsemantics' put-option : ignored
        // 'noadd' put-option : ignored
        // 'overwrite' put-option : implemented
        // 'thicket' put-option : ignored

        if (logger.isDebugEnabled())
        {
            logger.debug("Saving document: '" + document.getPath() + "' to the site: " + serviceName);
        }
        
        for(String urlPart : document.getPath().split("/"))
        {
            if (VtiUtils.hasIllegalCharacter(urlPart))
            {
                throw new VtiHandlerException(VtiHandlerException.HAS_ILLEGAL_CHARACTERS);
            }            
        }
        
        Pair<String, String> parentChildPaths = VtiPathHelper.splitPathParentChild(serviceName + "/" + document.getPath());
        String documentName = parentChildPaths.getSecond();
        if (documentName.length() == 0)
        {
            throw new VtiHandlerException(VtiHandlerException.BAD_URL);
        }

        FileInfo curDocumentFileInfo; // file info for document for put_document method

        UserTransaction tx = getTransactionService().getUserTransaction(false);
        try
        {
            tx.begin();

            String parentPath = parentChildPaths.getFirst();
            FileInfo parentFileInfo = getPathHelper().resolvePathFileInfo(parentPath);
            if (parentFileInfo == null)
            {
                if (putOptionSet.contains(PutOption.createdir))
                {
                    parentFileInfo = createOnlyLastFolder(parentPath);
                }

                if (parentFileInfo == null)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("The folder where file should be placed not exists.");
                    }
                    throw new VtiHandlerException(VtiHandlerException.PRIMARY_PARENT_NOT_EXIST);
                }
            }

            DocumentStatus documentStatus = DocumentStatus.NORMAL; // default status for new document

            curDocumentFileInfo = getPathHelper().resolvePathFileInfo(parentFileInfo, documentName);
            if (curDocumentFileInfo != null)
            {
                documentStatus = getDocumentHelper().getDocumentStatus(curDocumentFileInfo.getNodeRef());

                if (documentStatus.equals(DocumentStatus.READONLY))
                {
                    // document is readonly
                    throw new VtiHandlerException(VtiHandlerException.FILE_OPEN_FOR_WRITE);
                }

                if (VtiDocumentHepler.isCheckedout(documentStatus) && VtiDocumentHepler.isCheckoutOwner(documentStatus) == false)
                {
                    // document already checked out by another user
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Document is checked out by another user");
                    }
                    throw new VtiHandlerException(VtiHandlerException.DOC_CHECKED_OUT);
                }

                if (VtiDocumentHepler.isLongCheckedout(documentStatus))
                {
                    NodeRef workingCopyNodeRef = getCheckOutCheckInService().getWorkingCopy(curDocumentFileInfo.getNodeRef());
                    curDocumentFileInfo = getFileFolderService().getFileInfo(workingCopyNodeRef);
                }

                if ((putOptionSet.contains(PutOption.overwrite) == false && putOptionSet.contains(PutOption.edit) == false)
                        || (putOptionSet.contains(PutOption.edit) && VtiUtils.compare(curDocumentFileInfo.getModifiedDate(), document.getTimelastmodified()) == false))
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("ModifiedDate for document on server = '" + VtiUtils.formatDate(curDocumentFileInfo.getModifiedDate()) + "', "
                                + "ModifiedDate for client document = '" + document.getTimelastmodified() + "'");
                    }

                    throw new VtiHandlerException(VtiHandlerException.FILE_ALREADY_EXISTS);
                }
            }
            else
            {
                curDocumentFileInfo = getFileFolderService().create(parentFileInfo.getNodeRef(), documentName, ContentModel.TYPE_CONTENT);
            }

            NodeRef curDocumentNodeRef = curDocumentFileInfo.getNodeRef();

            if (getNodeService().hasAspect(curDocumentNodeRef, ContentModel.ASPECT_VERSIONABLE) == false)
            {
                getNodeService().addAspect(curDocumentNodeRef, ContentModel.ASPECT_VERSIONABLE, null);
            }

            if (getNodeService().hasAspect(curDocumentNodeRef, ContentModel.ASPECT_AUTHOR) == false)
            {
                getNodeService().addAspect(curDocumentNodeRef, ContentModel.ASPECT_AUTHOR, null);
            }
            getNodeService().setProperty(curDocumentNodeRef, ContentModel.PROP_AUTHOR, getAuthenticationService().getCurrentUserName());

            ContentWriter writer = getContentService().getWriter(curDocumentNodeRef, ContentModel.PROP_CONTENT, true);
            String mimetype = getMimetypeService().guessMimetype(documentName);
            writer.setMimetype(mimetype);
            writer.putContent(document.getInputStream());

            tx.commit();
        }
        catch (Throwable e)
        {
            try
            {
                // set the inputStream in null for correct answer from server
                document.setInputStream(null);
                tx.rollback();
            }
            catch (Exception tex)
            {
            }

            throw VtiExceptionUtils.createRuntimeException(e);
        }

        // refresh file info for new document
        curDocumentFileInfo = getFileFolderService().getFileInfo(curDocumentFileInfo.getNodeRef());

        DocMetaInfo result = new DocMetaInfo(false);
        result.setPath(document.getPath());
        setDocMetaInfo(curDocumentFileInfo, result);

        return result;
    }

    /**
     * @see org.alfresco.module.vti.handler.MethodHandler#removeDocuments(java.lang.String, java.util.List, java.util.List, boolean)
     */
    public DocsMetaInfo removeDocuments(String serviceName, List<String> urlList, List<Date> timeTokens, boolean validateWelcomeNames)
    {
        // timeTokens ignored
        // validateWelcomeNames ignored

        DocsMetaInfo docsMetaInfo = new DocsMetaInfo();

        UserTransaction tx = getTransactionService().getUserTransaction(false);
        try
        {
            tx.begin();

            for (String url : urlList)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Removing item: '" + url + "' from site: " + serviceName);
                }
                FileInfo fileInfo = getPathHelper().resolvePathFileInfo(serviceName + "/" + url);
                AlfrescoMethodHandler.assertValidFileInfo(fileInfo);

                DocMetaInfo docMetaInfo = new DocMetaInfo(fileInfo.isFolder());
                docMetaInfo.setPath(url);
                setDocMetaInfo(fileInfo, docMetaInfo);

                if (fileInfo.isFolder())
                {
                    // add nested files and folders to meta info list
                    addFileFoldersRecursive(fileInfo, docsMetaInfo);
                    docsMetaInfo.getFolderMetaInfoList().add(docMetaInfo);
                }
                else
                {
                    DocumentStatus documentStatus = getDocumentHelper().getDocumentStatus(fileInfo.getNodeRef());
                    AlfrescoMethodHandler.assertRemovableDocument(documentStatus);

                    if (documentStatus.equals(DocumentStatus.LONG_CHECKOUT_OWNER))
                    {
                        NodeRef workingCopyNodeRef = getCheckOutCheckInService().getWorkingCopy(fileInfo.getNodeRef());
                        getFileFolderService().delete(workingCopyNodeRef); // beforeDeletePolicy unlocks original document
                    }

                    docsMetaInfo.getFileMetaInfoList().add(docMetaInfo);
                }

                getFileFolderService().delete(fileInfo.getNodeRef());
            }

            tx.commit();
        }
        catch (Throwable e)
        {
            try
            {
                tx.rollback();
            }
            catch (Exception tex)
            {
            }

            throw VtiExceptionUtils.createRuntimeException(e);
        }

        return docsMetaInfo;
    }

    /**
     * Sets metadata for docMetaInfo
     * 
     * @param fileInfo file info for document, folder or working copy ({@link FileInfo})
     * @param docMetaInfo meta info ({@link DocMetaInfo})
     */
    public void setDocMetaInfo(FileInfo fileInfo, DocMetaInfo docMetaInfo)
    {
        if (fileInfo.isFolder())
        {
            NodeRef folderNodeRef = fileInfo.getNodeRef();

            docMetaInfo.setTimecreated(VtiUtils.formatDate(fileInfo.getCreatedDate()));
            String modifiedDate = VtiUtils.formatDate(fileInfo.getModifiedDate());
            docMetaInfo.setTimelastmodified(modifiedDate);
            docMetaInfo.setTimelastwritten(modifiedDate);

            boolean isBrowsable = getPermissionService().hasPermission(folderNodeRef, PermissionService.READ_CHILDREN).equals(AccessStatus.ALLOWED);
            if (isBrowsable)
            {
                docMetaInfo.setHassubdirs(String.valueOf(getFileFolderService().listFolders(folderNodeRef).isEmpty() == false));
            }
            docMetaInfo.setIsbrowsable(String.valueOf(isBrowsable));

            docMetaInfo.setIsexecutable(Boolean.FALSE.toString());
            docMetaInfo.setIsscriptable(Boolean.FALSE.toString());
        }
        else
        {
            FileInfo originalFileInfo = null;
            FileInfo workingCopyFileInfo = null;

            boolean isLongCheckedout = false;
            boolean isShortCheckedout = false;

            Map<QName, Serializable> originalProps = null;
            Map<QName, Serializable> workingCopyProps = null;

            if (getNodeService().hasAspect(fileInfo.getNodeRef(), ContentModel.ASPECT_WORKING_COPY))
            {
                // we have working copy
                workingCopyFileInfo = fileInfo;
                workingCopyProps = workingCopyFileInfo.getProperties();

                originalFileInfo = getFileFolderService().getFileInfo(getDocumentHelper().getOriginalNodeRef(workingCopyFileInfo.getNodeRef()));

                isLongCheckedout = true;
            }
            else
            {
                // we have original document
                originalFileInfo = fileInfo;

                DocumentStatus documentStatus = getDocumentHelper().getDocumentStatus(originalFileInfo.getNodeRef());
                isLongCheckedout = VtiDocumentHepler.isLongCheckedout(documentStatus);
                isShortCheckedout = VtiDocumentHepler.isShortCheckedout(documentStatus);

                if (isLongCheckedout)
                {
                    // retrieves file info and props for working copy
                    NodeRef workingCopyNodeRef = getCheckOutCheckInService().getWorkingCopy(originalFileInfo.getNodeRef());
                    workingCopyFileInfo = getFileFolderService().getFileInfo(workingCopyNodeRef);
                    workingCopyProps = workingCopyFileInfo.getProperties();
                }
            }

            originalProps = originalFileInfo.getProperties();

            docMetaInfo.setTimecreated(VtiUtils.formatDate(originalFileInfo.getCreatedDate()));
            if (isLongCheckedout)
            {
                String modifiedDate = VtiUtils.formatDate(workingCopyFileInfo.getModifiedDate());
                docMetaInfo.setTimelastmodified(modifiedDate);
                docMetaInfo.setTimelastwritten(modifiedDate);

                docMetaInfo.setFilesize(String.valueOf(workingCopyFileInfo.getContentData().getSize()));

                docMetaInfo.setSourcecontrolcheckedoutby((String) workingCopyProps.get(ContentModel.PROP_WORKING_COPY_OWNER));
                docMetaInfo.setSourcecontroltimecheckedout(modifiedDate);
            }
            else
            {
                String modifiedDate = VtiUtils.formatDate(originalFileInfo.getModifiedDate());
                docMetaInfo.setTimelastmodified(modifiedDate);
                docMetaInfo.setTimelastwritten(modifiedDate);

                docMetaInfo.setFilesize(String.valueOf(originalFileInfo.getContentData().getSize()));

                if (isShortCheckedout)
                {
                    docMetaInfo.setSourcecontroltimecheckedout(VtiUtils.formatDate(new Date()));
                    docMetaInfo.setSourcecontrolcheckedoutby((String) originalProps.get(ContentModel.PROP_LOCK_OWNER));
                    if (originalProps.get(ContentModel.PROP_EXPIRY_DATE) != null)
                    {
                        docMetaInfo.setSourcecontrollockexpires(VtiUtils.formatDate((Date) originalProps.get(ContentModel.PROP_EXPIRY_DATE)));
                    }
                    else
                    {
                        // we have infinite lock
                        // SharePoint doesn't support locks without expiry date
                        // sets expiry date on 10000 years in future
                        Date expiryDate = new Date();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(expiryDate);
                        calendar.add(Calendar.YEAR, 10000);
                        expiryDate = calendar.getTime();
                        docMetaInfo.setSourcecontrollockexpires(VtiUtils.formatDate(expiryDate));
                    }
                }
            }

            docMetaInfo.setTitle((String) originalProps.get(ContentModel.PROP_TITLE));
            docMetaInfo.setAuthor((String) originalProps.get(ContentModel.PROP_CREATOR));
            docMetaInfo.setModifiedBy((String) originalProps.get(ContentModel.PROP_MODIFIER));

            Version currentVersion = getVersionService().getCurrentVersion(originalFileInfo.getNodeRef());
            if (currentVersion != null)
            {
                docMetaInfo.setSourcecontrolversion(currentVersion.getVersionLabel());
            }
            else
            {
                // this document isn't versionable, but SharePoint supports versionable libraries, not documents
                // so send first version for document
                docMetaInfo.setSourcecontrolversion("1.0");
            }
        }
    }

    /**
     * Returns DialogMetaInfo for FileInfo
     * 
     * @param fileInfo file info ({@link FileInfo})
     * @return DialogMetaInfo dialog meta info     
     */
    private DialogMetaInfo getDialogMetaInfo(FileInfo fileInfo)
    {
        DialogMetaInfo dialogMetaInfo = new DialogMetaInfo(fileInfo.isFolder());
        dialogMetaInfo.setPath(getPathHelper().toUrlPath(fileInfo).replace("\'", "%27"));
        dialogMetaInfo.setName(fileInfo.getName().replace("\'", "&#39;"));
        dialogMetaInfo.setModifiedBy((String) fileInfo.getProperties().get(ContentModel.PROP_MODIFIER));

        if (fileInfo.isFolder() == false)
        {
            DocumentStatus documentStatus = getDocumentHelper().getDocumentStatus(fileInfo.getNodeRef());

            if (VtiDocumentHepler.isLongCheckedout(documentStatus))
            {
                NodeRef workingCopyNodeRef = getCheckOutCheckInService().getWorkingCopy(fileInfo.getNodeRef());
                FileInfo workingCopyFileInfo = getFileFolderService().getFileInfo(workingCopyNodeRef);
                dialogMetaInfo.setModifiedTime(VtiUtils.formatVersionDate(workingCopyFileInfo.getModifiedDate()));
                dialogMetaInfo.setCheckedOutTo((String) getNodeService().getProperty(workingCopyNodeRef, ContentModel.PROP_WORKING_COPY_OWNER));
            }
            else
            {
                dialogMetaInfo.setModifiedTime(VtiUtils.formatVersionDate(fileInfo.getModifiedDate()));
            }
        }
        else
        {
            dialogMetaInfo.setModifiedTime(VtiUtils.formatVersionDate(fileInfo.getModifiedDate()));
        }

        return dialogMetaInfo;
    }

    /**
     * Helper method for short-term or long-term checkouts
     * 
     * @param documentFileInfo file info for document ({@link FileInfo})
     * @param timeout timeout in minutes for short-term checkout, if equals 0, then uses long-term checkout
     * @return FileInfo checked out document file info
     */
    public FileInfo checkout(FileInfo documentFileInfo, int timeout)
    {
        DocumentStatus documentStatus = getDocumentHelper().getDocumentStatus(documentFileInfo.getNodeRef());

        if (documentStatus.equals(DocumentStatus.READONLY))
        {
            // document is readonly
            if (logger.isDebugEnabled())
            {
                logger.debug("Unable to perform checked out operation!!! Document is read only.");
            }
            throw new VtiHandlerException(VtiHandlerException.FILE_OPEN_FOR_WRITE);
        }

        if (VtiDocumentHepler.isCheckedout(documentStatus) && VtiDocumentHepler.isCheckoutOwner(documentStatus) == false)
        {
            // document already checked out by another user
            if (logger.isDebugEnabled())
            {
                logger.debug("Unable to perform checked out operation!!! Document is already checked out.");
            }
            throw new VtiHandlerException(VtiHandlerException.DOC_CHECKED_OUT);
        }

        FileInfo checkedoutDocumentFileInfo;

        if (documentStatus.equals(DocumentStatus.LONG_CHECKOUT_OWNER) == false)
        {
            UserTransaction tx = getTransactionService().getUserTransaction(false);
            try
            {
                tx.begin();

                if (timeout == 0)
                {
                    // clearing short-term checkout if necessary
                    if (VtiDocumentHepler.isShortCheckedout(documentStatus))
                    {
                        getLockService().unlock(documentFileInfo.getNodeRef());
                    }

                    NodeRef workingCopyNodeRef = getCheckOutCheckInService().checkout(documentFileInfo.getNodeRef());
                    checkedoutDocumentFileInfo = getFileFolderService().getFileInfo(workingCopyNodeRef);
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Long-term checkout.");
                    }
                }
                else
                {
                    getLockService().lock(documentFileInfo.getNodeRef(), LockType.WRITE_LOCK, VtiUtils.toAlfrescoLockTimeout(timeout));
                    // refresh file info
                    checkedoutDocumentFileInfo = getFileFolderService().getFileInfo(documentFileInfo.getNodeRef());
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Short-term checkout.");
                    }
                }

                tx.commit();
            }
            catch (Throwable e)
            {
                try
                {
                    tx.rollback();
                }
                catch (Exception tex)
                {
                }

                throw VtiExceptionUtils.createRuntimeException(e);
            }
        }
        else
        {
            // document already checked out by same user, just returns file info for working copy
            checkedoutDocumentFileInfo = getFileFolderService().getFileInfo(getCheckOutCheckInService().getWorkingCopy(documentFileInfo.getNodeRef()));
        }

        return checkedoutDocumentFileInfo;
    }

    /**
     * Asserts "document with this status is removable"
     * 
     * @param documentStatus document status ({@link DocumentStatus})
     */
    public static void assertRemovableDocument(DocumentStatus documentStatus)
    {
        if (documentStatus.equals(DocumentStatus.READONLY))
        {
            throw new VtiHandlerException(VtiHandlerException.REMOVE_FILE);
        }

        if (VtiDocumentHepler.isCheckedout(documentStatus) && VtiDocumentHepler.isCheckoutOwner(documentStatus) == false)
        {
            throw new VtiHandlerException(VtiHandlerException.DOC_CHECKED_OUT);
        }
    }

    /**
     * Creates only last folder in path
     * 
     * @param path path
     * @return FileInfo object for last folder in path if it was created, else returns null
     */
    private FileInfo createOnlyLastFolder(String path)
    {
        Pair<String, String> parentChildPaths = VtiPathHelper.splitPathParentChild(path);
        String parentPath = parentChildPaths.getFirst();
        String lastFolderName = parentChildPaths.getSecond();
        FileInfo parentFileInfo = getPathHelper().resolvePathFileInfo(parentPath);

        FileInfo lastFolderFileInfo = null;

        if (parentFileInfo != null && parentFileInfo.isFolder() && lastFolderName.length() != 0)
        {
            try
            {
                lastFolderFileInfo = getFileFolderService().create(parentFileInfo.getNodeRef(), lastFolderName, ContentModel.TYPE_FOLDER);
            }
            catch (FileExistsException e)
            {
            }
        }

        return lastFolderFileInfo;
    }

    /**
     * Collect information about files and folders in current document workspace site
     * 
     * @param rootFolder relative url on document library folder on site ({@link FileInfo})
     * @param docsMetaInfo document workspace site meta info ({@link DocsMetaInfo})
     */
    private void addFileFoldersRecursive(FileInfo rootFolder, DocsMetaInfo docsMetaInfo)
    {
        for (FileInfo fileInfo : getFileFolderService().list(rootFolder.getNodeRef()))
        {
            DocMetaInfo docMetaInfo = new DocMetaInfo(fileInfo.isFolder());
            docMetaInfo.setPath(getPathHelper().toUrlPath(fileInfo));
            setDocMetaInfo(fileInfo, docMetaInfo);

            if (fileInfo.isFolder())
            {
                docsMetaInfo.getFolderMetaInfoList().add(docMetaInfo);
                addFileFoldersRecursive(fileInfo, docsMetaInfo);
            }
            else
            {
                DocumentStatus documentStatus = getDocumentHelper().getDocumentStatus(fileInfo.getNodeRef());
                AlfrescoMethodHandler.assertRemovableDocument(documentStatus);
                docsMetaInfo.getFileMetaInfoList().add(docMetaInfo);
            }
        }
    }

    /**
     * Asserts "this file info of content node reference"
     * 
     * @param fileFileInfo file info ({@link FileInfo})
     * @throws VtiHandlerException thrown if the file info isn't file file info
     */
    public static void assertFile(FileInfo fileFileInfo) throws VtiHandlerException
    {
        if (fileFileInfo.isFolder() == true)
        {
            throw new VtiHandlerException(VtiHandlerException.URL_NOT_FOUND);
        }
    }

    /**
     * Asserts "this file info of folder node reference"
     * 
     * @param folderFileInfo file info ({@link FileInfo})
     * @throws VtiHandlerException thrown if the file info isn't folder file info
     */
    public static void assertFolder(FileInfo folderFileInfo) throws VtiHandlerException
    {
        if (folderFileInfo.isFolder() == false)
        {
            throw new VtiHandlerException(VtiHandlerException.URL_DIR_NOT_FOUND);
        }
    }

    /**
     * Asserts "this file info isn't null"
     * 
     * @param fileInfo file info ({@link FileInfo})
     * @throws VtiHandlerException thrown if the file info is null
     */
    public static void assertValidFileInfo(FileInfo fileInfo) throws VtiHandlerException
    {
        if (fileInfo == null)
        {
            throw new VtiHandlerException(VtiHandlerException.BAD_URL);
        }
    }
}
