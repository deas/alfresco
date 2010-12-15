/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.repo.lotus.ws.impl;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.alfresco.i18n.I18NUtil;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.lotus.ws.ClbApproveDraftResponse;
import org.alfresco.repo.lotus.ws.ClbCancelDocumentResponse;
import org.alfresco.repo.lotus.ws.ClbCheckinResponse;
import org.alfresco.repo.lotus.ws.ClbContentURLMode;
import org.alfresco.repo.lotus.ws.ClbContentURLResponse;
import org.alfresco.repo.lotus.ws.ClbCopyResponse;
import org.alfresco.repo.lotus.ws.ClbData;
import org.alfresco.repo.lotus.ws.ClbDocTypeOption;
import org.alfresco.repo.lotus.ws.ClbDocument;
import org.alfresco.repo.lotus.ws.ClbDocumentResponse;
import org.alfresco.repo.lotus.ws.ClbDocumentType;
import org.alfresco.repo.lotus.ws.ClbDocumentTypeResponse;
import org.alfresco.repo.lotus.ws.ClbDocumentTypesResponse;
import org.alfresco.repo.lotus.ws.ClbDocumentsResponse;
import org.alfresco.repo.lotus.ws.ClbDownloadOption;
import org.alfresco.repo.lotus.ws.ClbDraft;
import org.alfresco.repo.lotus.ws.ClbDraftResponse;
import org.alfresco.repo.lotus.ws.ClbDraftsResponse;
import org.alfresco.repo.lotus.ws.ClbError;
import org.alfresco.repo.lotus.ws.ClbErrorType;
import org.alfresco.repo.lotus.ws.ClbFolder;
import org.alfresco.repo.lotus.ws.ClbFolderResponse;
import org.alfresco.repo.lotus.ws.ClbFoldersResponse;
import org.alfresco.repo.lotus.ws.ClbMoveResponse;
import org.alfresco.repo.lotus.ws.ClbPropertySheet;
import org.alfresco.repo.lotus.ws.ClbPropertySheetTypeResponse;
import org.alfresco.repo.lotus.ws.ClbResponse;
import org.alfresco.repo.lotus.ws.ClbServiceOption;
import org.alfresco.repo.lotus.ws.ClbTreeResponse;
import org.alfresco.repo.lotus.ws.ClbVersionResponse;
import org.alfresco.repo.lotus.ws.ClbVersioning;
import org.alfresco.repo.lotus.ws.ClbVersionsResponse;
import org.alfresco.repo.lotus.ws.ClbViewFormatResponse;
import org.alfresco.repo.lotus.ws.ClbViewResponse;
import org.alfresco.repo.lotus.ws.ClbViewResultsResponse;
import org.alfresco.repo.lotus.ws.ClbViewsResponse;
import org.alfresco.repo.lotus.ws.DocumentService;
import org.alfresco.repo.lotus.ws.LoginException_Exception;
import org.alfresco.repo.lotus.ws.ServiceException_Exception;
import org.alfresco.repo.lotus.ws.UserInfo;
import org.alfresco.repo.lotus.ws.VersionType;
import org.alfresco.repo.lotus.ws.impl.helper.AlfrescoQuickrDocumentHelper;
import org.alfresco.repo.lotus.ws.impl.helper.AlfrescoQuickrPathHelper;
import org.alfresco.repo.lotus.ws.impl.helper.AlfrescoQuickrPermissionHelper;
import org.alfresco.repo.lotus.ws.impl.helper.DocumentStatus;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.repo.version.Version2Model;
import org.alfresco.repo.version.VersionModel;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.lock.LockType;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionHistory;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * @author PavelYur
 */
public class AlfrescoDocumentServiceImpl implements DocumentService
{
    private static Log logger = LogFactory.getLog(AlfrescoDocumentServiceImpl.class);

    private NodeService nodeService;

    private FileFolderService fileFolderService;

    private CheckOutCheckInService checkOutCheckInService;

    private LockService lockService;

    private ContentService contentService;

    private AlfrescoQuickrPathHelper pathHelper;

    private TransactionService transactionService;

    private AlfrescoQuickrDocumentHelper documentHelper;

    private AlfrescoQuickrPermissionHelper permissionHelper;

    private ClbDocumentTypesResponse documentTypesResponse;

    private VersionService versionService;

    private MimetypeService mimetypeService;

    private PersonService personService;

    public void setPersonService(PersonService personService)
    {
        this.personService = personService;
    }

    public void setMimetypeService(MimetypeService mimetypeService)
    {
        this.mimetypeService = mimetypeService;
    }

    public void setVersionService(VersionService versionService)
    {
        this.versionService = versionService;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setFileFolderService(FileFolderService fileFolderService)
    {
        this.fileFolderService = fileFolderService;
    }

    public void setCheckOutCheckInService(CheckOutCheckInService checkOutCheckInService)
    {
        this.checkOutCheckInService = checkOutCheckInService;
    }

    public void setLockService(LockService lockService)
    {
        this.lockService = lockService;
    }

    public void setContentService(ContentService contentService)
    {
        this.contentService = contentService;
    }

    public void setPathHelper(AlfrescoQuickrPathHelper pathHelper)
    {
        this.pathHelper = pathHelper;
    }

    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    public void setDocumentHelper(AlfrescoQuickrDocumentHelper documentHelper)
    {
        this.documentHelper = documentHelper;
    }

    public void setPermissionHelper(AlfrescoQuickrPermissionHelper permissionHelper)
    {
        this.permissionHelper = permissionHelper;
    }

    public ClbResponse addVersionLabel(String id, String path, String version, String label) throws LoginException_Exception, ServiceException_Exception
    {
        // This operation is not supported now
        ClbResponse result = new ClbResponse();
        ClbError error = new ClbError();

        error.setType(ClbErrorType.UNSUPPORTED_OPERATION);
        result.setError(error);

        return result;
    }

    public ClbApproveDraftResponse approveDraft(String id, String path) throws LoginException_Exception, ServiceException_Exception
    {
        // This operation is not supported now
        ClbApproveDraftResponse result = new ClbApproveDraftResponse();
        ClbError error = new ClbError();

        error.setType(ClbErrorType.UNSUPPORTED_OPERATION);
        result.setError(error);

        return result;
    }

    /**
     * Cancel document checkout.
     * 
     * @param id The uuid of the document if no path is provided. If both id and path are provided, the id must be the uuid of the parent folder to the document.
     * @param path The absolute path to the document if no id is provided. If both id and path are provided, the path must be the relative path from the id provided.
     * @return ClbCancelDocumentResponse.
     * @throws LoginException_Exception, ServiceException_Exception
     */
    public ClbCancelDocumentResponse cancelDocument(String id, String path) throws LoginException_Exception, ServiceException_Exception
    {
        ClbCancelDocumentResponse result = new ClbCancelDocumentResponse();
        try
        {
            final NodeRef nodeRef = pathHelper.resolveNodeRef(id, path);
            if (!pathHelper.isInRmSite(nodeRef))
            {
                transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Object>()
                {
                    public Object execute()
                    {
                        NodeRef workingCopy = checkOutCheckInService.getWorkingCopy(nodeRef);
                        if (workingCopy != null)
                        {
                            checkOutCheckInService.cancelCheckout(workingCopy);
                        }
                        else
                        {
                            lockService.unlock(nodeRef);
                        }
                        return null;
                    }
                });
            }

        }
        catch (FileNotFoundException e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error(e);
            }
            ClbError error = new ClbError();
            error.setMessage(e.getLocalizedMessage());
            error.setType(ClbErrorType.ITEM_NOT_FOUND);
            result.setError(error);

            return result;
        }

        result.setDocumentDeleted(false);

        return result;
    }

    /**
     * Checkin document.
     * 
     * @param document The id or path attribute is the only required attribute to be supplied in the document parameter. The id or path should reference the document to be checked
     *        in. If any other data is supplied in the document, that data will be updated on the document prior to checking it in.
     * @return ClbCheckinResponse.
     * @throws LoginException_Exception, ServiceException_Exception
     */
    public ClbCheckinResponse checkinDocument(final ClbDocument document) throws LoginException_Exception, ServiceException_Exception
    {
        ClbCheckinResponse result = new ClbCheckinResponse();

        final boolean keepCheckedOut = false;
        try
        {
            final NodeRef nodeRef = pathHelper.resolveNodeRef(document.getId(), document.getPath());

            FileInfo resultFileInfo = fileFolderService.getFileInfo(nodeRef);
            if (!pathHelper.isInRmSite(nodeRef))
            {
                resultFileInfo = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<FileInfo>()
                {
                    public FileInfo execute()
                    {
                        FileInfo documentFileInfo = fileFolderService.getFileInfo(nodeRef);

                        NodeRef workingCopy = checkOutCheckInService.getWorkingCopy(documentFileInfo.getNodeRef());
                        if (workingCopy == null)
                        {
                            if (nodeService.hasAspect(documentFileInfo.getNodeRef(), ContentModel.ASPECT_WORKING_COPY))
                            {
                                workingCopy = documentFileInfo.getNodeRef();
                            }
                        }

                        if (workingCopy != null)
                        {
                            Map<String, Serializable> props = new HashMap<String, Serializable>(1, 1.0f);
                            props.put(Version.PROP_DESCRIPTION, document.getDescription());
                            props.put(VersionModel.PROP_VERSION_TYPE, org.alfresco.service.cmr.version.VersionType.MAJOR);

                            NodeRef resultNodeRef = checkOutCheckInService.checkin(workingCopy, props, null, keepCheckedOut);

                            return fileFolderService.getFileInfo(resultNodeRef);
                        }
                        else
                        {
                            lockService.unlock(documentFileInfo.getNodeRef());
                            return fileFolderService.getFileInfo(documentFileInfo.getNodeRef());
                        }
                    }
                });
            }
            result.setId(resultFileInfo.getNodeRef().getId());
            result.setPath(pathHelper.getNodePath(resultFileInfo.getNodeRef()));
            result.setLastModified(pathHelper.getXmlDate((Date) nodeService.getProperty(resultFileInfo.getNodeRef(), ContentModel.PROP_MODIFIED)));
        }
        catch (FileNotFoundException e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error(e);
            }

            ClbError error = new ClbError();
            error.setMessage(e.getLocalizedMessage());
            error.setType(ClbErrorType.ITEM_NOT_FOUND);
            result.setError(error);

            return result;
        }
        return result;
    }

    /**
     * Checkout document.
     * 
     * @param id The uuid of the document if no path is provided. If both id and path are provided, the id must be the uuid of the parent folder to the document.
     * @param path The absolute path to the document if no id is provided. If both id and path are provided, the path must be the relative path from the id provided.
     * @return ClbDocumentResponse.
     * @throws LoginException_Exception, ServiceException_Exception
     */
    public ClbDocumentResponse checkoutDocument(String id, String path, ClbDownloadOption downloadOption) throws LoginException_Exception, ServiceException_Exception
    {
        ClbDocumentResponse result = new ClbDocumentResponse();
        try
        {
            final NodeRef nodeRef = pathHelper.resolveNodeRef(id, path);

            DocumentStatus documentStatus = documentHelper.getDocumentStatus(nodeRef);

            if (documentStatus.equals(DocumentStatus.READONLY) || pathHelper.isInRmSite(nodeRef))
            {
                ClbError error = new ClbError();
                error.setMessage("You don't have permissions to perform this operation.");
                error.setType(ClbErrorType.ACCESS_DENIED);
                result.setError(error);

                return result;
            }

            if (AlfrescoQuickrDocumentHelper.isCheckedout(documentStatus) && AlfrescoQuickrDocumentHelper.isCheckoutOwner(documentStatus) == false)
            {
                String user = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_LOCK_OWNER);

                ClbError error = new ClbError();
                error.setMessage("Document has been already checked out by " + user);
                error.setType(ClbErrorType.DOCUMENT_ALREADY_LOCKED);
                result.setError(error);

                return result;
            }

            transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Object>()
            {
                public Object execute()
                {
                    lockService.lock(nodeRef, LockType.READ_ONLY_LOCK);
                    return null;
                }
            });

            result.setDocument(createDocument(nodeRef, new ArrayList<ClbServiceOption>(), null));
        }
        catch (FileNotFoundException e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error(e);
            }

            ClbError error = new ClbError();
            error.setMessage(e.getLocalizedMessage());
            error.setType(ClbErrorType.ITEM_NOT_FOUND);
            result.setError(error);

            return result;
        }
        return result;
    }

    /**
     * Copy document.
     * 
     * @param srcId The uuid of the source document if no path is provided. If both id and path are provided, the id must be the uuid of the parent folder to the document.
     * @param srcPath The absolute path to the source document if no id is provided. If both id and path are provided, the path must be the relative path from the id provided.
     * @param destId The uuid of the destination folder if no path is provided. If both id and path are provided, the id must be the uuid of the parent folder to the destination
     *        folder.
     * @param destPath The absolute path to the destination folder if no id is provided. If both id and path are provided, the path must be the relative path from the id provided.
     * @param shallowCopy If true, the copy will not include the children of the document. If false, the document and children will be copied to the target. Currently, the only
     *        child types that are copied are property sheets. The drafts and comments of a document are never copied.
     * @return ClbCopyResponse.
     * @throws LoginException_Exception
     */
    public ClbCopyResponse copyDocument(final String srcId, String srcPath, final String destId, String destPath, boolean shallowCopy) throws LoginException_Exception,
            ServiceException_Exception
    {
        return copyFolder(srcId, srcPath, destId, destPath, shallowCopy);
    }

    /**
     * Copy folder.
     * 
     * @param srcId The uuid of the source folder if no path is provided. If both id and path are provided, the id must be the uuid of the parent folder to the item.
     * @param srcPath The absolute path to the source folder if no id is provided. If both id and path are provided, the path must be the relative path from the id provided.
     * @param destId The uuid of the destination folder if no path is provided. If both id and path are provided, the id must be the uuid of the parent folder to the destination
     *        folder.
     * @param destPath The absolute path to the destination folder if no id is provided. If both id and path are provided, the path must be the relative path from the id provided.
     * @param shallowCopy If true, the copy will only be one level deep. If false, the entire tree under the source node will be copied to the target.
     * @return ClbCopyResponse.
     * @throws LoginException_Exception
     */
    public ClbCopyResponse copyFolder(final String srcId, String srcPath, final String destId, String destPath, boolean shallowCopy) throws LoginException_Exception,
            ServiceException_Exception
    {
        final ClbCopyResponse result = new ClbCopyResponse();
        try
        {
            final NodeRef source = pathHelper.resolveNodeRef(srcId, srcPath);
            final NodeRef destination = pathHelper.resolveNodeRef(destId, destPath);

            transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Object>()
            {
                public Object execute()
                {
                    copyItem(source, destination, result);
                    return null;
                }
            });
        }
        catch (FileNotFoundException e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error(e);
            }

            ClbError error = new ClbError();
            error.setMessage(e.getLocalizedMessage());
            error.setType(ClbErrorType.ITEM_NOT_FOUND);
            result.setError(error);

            return result;
        }
        return result;
    }

    private void copyItem(NodeRef srcNodeRef, NodeRef destNodeRef, ClbCopyResponse response)
    {
        try
        {
            FileInfo resultFileInfo = fileFolderService.copy(srcNodeRef, destNodeRef, null);

            response.setCreated(pathHelper.getXmlDate((Date) nodeService.getProperty(resultFileInfo.getNodeRef(), ContentModel.PROP_CREATED)));
            response.setId(resultFileInfo.getNodeRef().getId());
            response.setLastModified(pathHelper.getXmlDate((Date) nodeService.getProperty(resultFileInfo.getNodeRef(), ContentModel.PROP_MODIFIED)));
            response.setPath(pathHelper.getNodePath(resultFileInfo.getNodeRef()));
        }
        catch (FileExistsException e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error(e);
            }

            ClbError error = new ClbError();
            error.setType(ClbErrorType.ITEM_EXISTS);
            error.setMessage(e.getLocalizedMessage());
            response.setError(error);
        }
        catch (FileNotFoundException e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error(e);
            }

            ClbError error = new ClbError();
            error.setType(ClbErrorType.ITEM_NOT_FOUND);
            error.setMessage(e.getLocalizedMessage());
            response.setError(error);
        }
    }

    /**
     * Creates a published ClbDocument. The path attribute of the document parameter is used to determine where the document will be created.
     * 
     * @param document The new document to create. Return ClbDocumentResponse.
     * @throws LoginException_Exception, ServiceException_Exception
     */
    public ClbDocumentResponse createDocument(final ClbDocument document) throws LoginException_Exception, ServiceException_Exception
    {
        ClbDocumentResponse result = new ClbDocumentResponse();
        try
        {
            final NodeRef parentNodeRef = pathHelper.resolveNodeRef(null, document.getPath());

            try
            {
                NodeRef newDoc = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<NodeRef>()
                {
                    public NodeRef execute()
                    {
                        NodeRef nodeRef = fileFolderService.create(parentNodeRef, document.getLabel(), ContentModel.TYPE_CONTENT).getNodeRef();

                        nodeService.setProperty(nodeRef, ContentModel.PROP_TITLE, document.getTitle());
                        nodeService.setProperty(nodeRef, ContentModel.PROP_DESCRIPTION, document.getDescription());

                        return nodeRef;
                    }
                });

                result.setDocument(createDocument(newDoc, new ArrayList<ClbServiceOption>(), null));
            }
            catch (FileExistsException e)
            {
                if (logger.isErrorEnabled())
                {
                    logger.error(e);
                }

                ClbError error = new ClbError();
                error.setType(ClbErrorType.ITEM_EXISTS);
                error.setMessage("Document '" + document.getLabel() + "' already exist.");
                result.setError(error);
            }
        }
        catch (FileNotFoundException e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error(e);
            }

            ClbError error = new ClbError();
            error.setMessage(e.getLocalizedMessage());
            error.setType(ClbErrorType.ITEM_NOT_FOUND);
            result.setError(error);

            return result;
        }
        return result;
    }

    /**
     * Create draft in repository.
     * 
     * @param id The uuid of the document if no path is provided. If both id and path are provided, the id must be the uuid of the parent folder to the document.
     * @param path The absolute path to the document if no id is provided. If both id and path are provided, the path must be the relative path from the id provided.
     * @param draft The data that will be used to populate the new draft.
     * @return ClbDraftResponse.
     * @throws LoginException_Exception, ServiceException_Exception
     */
    public ClbDraftResponse createDraft(final String id, final String path, final ClbDraft draft) throws LoginException_Exception, ServiceException_Exception
    {
        final ClbDraftResponse result = new ClbDraftResponse();
        try
        {
            NodeRef parentNodeRef = null;
            String name = null;
            if (id == null)
            {
                parentNodeRef = pathHelper.resolveNodeRef(null, path.substring(0, path.lastIndexOf("/")));
                name = path.substring(path.lastIndexOf("/") + 1);
            }
            else
            {
                parentNodeRef = pathHelper.resolveNodeRef(id, null);
                name = path;
            }

            try
            {
                final NodeRef finalParentNodeRef = parentNodeRef;
                final String finalName = name;
                transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Object>()
                {
                    public Object execute()
                    {
                        NodeRef nodeRef = fileFolderService.create(finalParentNodeRef, finalName, ContentModel.TYPE_CONTENT).getNodeRef();
                        ContentWriter writer = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
                        writer.putContent("");
                        
                        if (draft.getDocumentType() != null)
                        {
                            String docType = draft.getDocumentType().getId();
                            if (docType!= null && docType.length() != 0)
                            {
                                ContentData contentData = (ContentData) nodeService.getProperty(nodeRef, ContentModel.PROP_CONTENT);
                                contentData = ContentData.setMimetype(contentData, docType);
                                nodeService.setProperty(nodeRef, ContentModel.PROP_CONTENT, contentData);
                            }
                        }
                        
                        
                        if (!pathHelper.isInRmSite(nodeRef))
                        {
                            nodeRef = checkOutCheckInService.checkout(nodeRef);
                        }
                        result.setDraft(createDraft(nodeRef));
                        return null;
                    }
                });

            }
            catch (FileExistsException e)
            {
                if (logger.isErrorEnabled())
                {
                    logger.error(e);
                }

                ClbError error = new ClbError();
                error.setType(ClbErrorType.ITEM_EXISTS);
                error.setMessage("Document '" + draft.getLabel() + "' already exist.");
                result.setError(error);
            }
        }
        catch (FileNotFoundException e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error(e);
            }

            ClbError error = new ClbError();
            error.setMessage(e.getLocalizedMessage());
            error.setType(ClbErrorType.ITEM_NOT_FOUND);
            result.setError(error);

            return result;
        }

        return result;
    }

    /**
     * The creates a Folder in the repository. The data provided in the folder parameter will be used to populate the new Folder in the repository.
     * 
     * @param folder - The data that will be used to populate the new folder. The path attribute must contain the absolute path of the folder to be created.
     * @return ClbFolderResponse.
     * @throws LoginException_Exception, ServiceException_Exception
     */
    public ClbFolderResponse createFolder(final ClbFolder folder) throws LoginException_Exception, ServiceException_Exception
    {
        ClbFolderResponse result = new ClbFolderResponse();
        try
        {
            final String path = folder.getPath();
            final NodeRef parentNodeRef = pathHelper.resolveNodeRef(null, path.substring(0, path.lastIndexOf("/")));

            try
            {
                NodeRef newFolder = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<NodeRef>()
                {
                    public NodeRef execute()
                    {
                        String folderName = folder.getLabel();
                        if (folderName == null)
                        {
                            folderName = path.substring(path.lastIndexOf("/") + 1);
                        }
                        NodeRef nodeRef = fileFolderService.create(parentNodeRef, folderName, ContentModel.TYPE_FOLDER).getNodeRef();

                        nodeService.setProperty(nodeRef, ContentModel.PROP_TITLE, folder.getTitle());
                        nodeService.setProperty(nodeRef, ContentModel.PROP_DESCRIPTION, folder.getDescription());

                        return nodeRef;
                    }
                });

                result.setFolder(createFolder(newFolder, new ArrayList<ClbServiceOption>()));
            }
            catch (FileExistsException e)
            {
                if (logger.isErrorEnabled())
                {
                    logger.error(e);
                }

                ClbError error = new ClbError();
                error.setType(ClbErrorType.ITEM_EXISTS);
                error.setMessage("Folder '" + folder.getLabel() + "' already exist.");
                result.setError(error);
            }
        }
        catch (FileNotFoundException e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error(e);
            }

            ClbError error = new ClbError();
            error.setMessage(e.getLocalizedMessage());
            error.setType(ClbErrorType.ITEM_NOT_FOUND);
            result.setError(error);

            return result;
        }
        return result;
    }

    /**
     * Creates new version of document.
     * 
     * @param id - The uuid of the document if no path is provided. If both id and path are provided, the id must be the uuid of the parent folder to the document.
     * @param path - The absolute path to the document if no id is provided. If both id and path are provided, the path must be the relative path from the id provided.
     * @param comments - Optional comments text that will be part of this version.
     * @return ClbVersionResponse
     * @throws LoginException_Exception, ServiceException_Exception
     */
    public ClbVersionResponse createVersion(String id, String path, String comments) throws LoginException_Exception, ServiceException_Exception
    {
        ClbVersionResponse result = new ClbVersionResponse();
        try
        {
            NodeRef nodeRef = pathHelper.resolveNodeRef(id, path);

            if (pathHelper.isInRmSite(nodeRef))
            {
                ClbError error = new ClbError();
                error.setType(ClbErrorType.ACCESS_DENIED);
                result.setError(error);

                return result;
            }
            // If document is checked out, then we use working copy
            nodeRef = pathHelper.getDocumentForWork(nodeRef);

            Map<String, Serializable> versionProperties = new HashMap<String, Serializable>();
            versionProperties.put(Version.PROP_DESCRIPTION, comments);
            versionProperties.put(VersionModel.PROP_VERSION_TYPE, org.alfresco.service.cmr.version.VersionType.MAJOR);

            Version version = versionService.createVersion(nodeRef, versionProperties);
            result.setVersionName(version.getVersionLabel());
        }
        catch (FileNotFoundException e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error(e);
            }

            ClbError error = new ClbError();
            error.setMessage(e.getLocalizedMessage());
            error.setType(ClbErrorType.ITEM_NOT_FOUND);
            result.setError(error);

            return result;
        }
        return result;
    }

    /**
     * Delete document.
     * 
     * @param id The uuid of the document if no path is provided. If both id and path are provided, the id must be the uuid of the parent folder to the document.
     * @param path The absolute path to the document if no id is provided. If both id and path are provided, the path must be the relative path from the id provided.
     * @return ClbResponse
     * @throws LoginException_Exception, ServiceException_Exception
     */
    public ClbResponse deleteDocument(final String id, String path) throws LoginException_Exception, ServiceException_Exception
    {
        return deleteFolder(id, path);
    }

    /**
     * Delete Folder.
     * 
     * @param id The uuid of the folder if no path is provided. If both id and path are provided, the id must be the uuid of the parent folder to the folder.
     * @param path The absolute path to the folder if no id is provided. If both id and path are provided, the path must be the relative path from the id provided.
     * @return ClbResponse
     * @throws LoginException_Exception, ServiceException_Exception
     */
    public ClbResponse deleteFolder(final String id, String path) throws LoginException_Exception, ServiceException_Exception
    {
        final ClbResponse result = new ClbResponse();
        try
        {
            final NodeRef nodeRefToDelete = pathHelper.resolveNodeRef(id, path);

            transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Object>()
            {
                public Object execute()
                {
                    deleteItem(nodeRefToDelete, result);
                    return null;
                }
            });
        }
        catch (FileNotFoundException e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error(e);
            }

            ClbError error = new ClbError();
            error.setMessage(e.getLocalizedMessage());
            error.setType(ClbErrorType.ITEM_NOT_FOUND);
            result.setError(error);

            return result;
        }
        return result;
    }

    private void deleteItem(NodeRef nodeRef, ClbResponse response)
    {
        try
        {
            fileFolderService.delete(nodeRef);
        }
        catch (InvalidNodeRefException e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error(e);
            }

            ClbError error = new ClbError();
            error.setType(ClbErrorType.ITEM_NOT_FOUND);
            error.setMessage(e.getLocalizedMessage());
            response.setError(error);
        }
    }

    public ClbViewResultsResponse executeView(String viewId, List<ClbServiceOption> serviceOptions) throws LoginException_Exception, ServiceException_Exception
    {
        // This operation is not supported now
        ClbViewResultsResponse result = new ClbViewResultsResponse();
        ClbError error = new ClbError();

        error.setType(ClbErrorType.UNSUPPORTED_OPERATION);
        result.setError(error);

        return result;
    }

    public ClbViewsResponse getAllViews(String folderId) throws LoginException_Exception, ServiceException_Exception
    {
        // This operation is not supported now
        ClbViewsResponse result = new ClbViewsResponse();
        ClbError error = new ClbError();

        error.setType(ClbErrorType.UNSUPPORTED_OPERATION);
        result.setError(error);

        return result;
    }

    public ClbDraftsResponse getApproveDrafts(String id, String path) throws LoginException_Exception, ServiceException_Exception
    {
        // This operation is not supported now
        ClbDraftsResponse result = new ClbDraftsResponse();
        ClbError error = new ClbError();

        error.setType(ClbErrorType.UNSUPPORTED_OPERATION);
        result.setError(error);

        return result;
    }

    /**
     * Retrieves the requested document.
     * 
     * @param id The uuid of the document if no path is provided. If both id and path are provided, the id must be the uuid of the parent folder to the document.
     * @param path The absolute path to the document if no id is provided. If both id and path are provided, the path must be the relative path from the id provided.
     * @param downloadOption Placeholder for future MTOM attachment support.
     * @param serviceOptions Multiple service options can be provided to control the returned results. The supported values are None, IncludeDrafts, IncludeReferences,
     *        IncludePermissions, IncludePropertySheets, ResolveLockOwner, IncludeApproveDrafts, RetrieveDownloadURL and RetrieveViewURL.
     * @return ClbDocumentResponse
     * @throws LoginException_Exception, ServiceException_Exception
     */
    public ClbDocumentResponse getDocument(String id, String path, ClbDownloadOption downloadOption, List<ClbServiceOption> serviceOptions) throws LoginException_Exception,
            ServiceException_Exception
    {
        ClbDocumentResponse result = new ClbDocumentResponse();
        try
        {
            NodeRef documentNodeRef = pathHelper.resolveNodeRef(id, path);

            // If document is checked out, then we use working copy
            documentNodeRef = pathHelper.getDocumentForWork(documentNodeRef);

            result.setDocument(createDocument(documentNodeRef, serviceOptions, downloadOption));
        }
        catch (FileNotFoundException e)
        {
            if (serviceOptions.contains(ClbServiceOption.RESOLVE_DEFAULT_DOCUMENT_TYPE))
            {
                ClbDocument document = new ClbDocument();
                document.setDocumentType(createDocumentType(null, path.substring(path.lastIndexOf(".") + 1, path.length())));
                result.setDocument(document);
            }

            if (logger.isErrorEnabled())
            {
                logger.error(e);
            }

            ClbError error = new ClbError();
            error.setMessage(e.getLocalizedMessage());
            error.setType(ClbErrorType.ITEM_NOT_FOUND);
            result.setError(error);

            return result;
        }
        return result;
    }

    /**
     * Return document type of specified document.
     * 
     * @param id - The uuid of the ClbDocumentType if no path is provided. If both id and path are provided, the id must be the uuid of the parent folder to the ClbDocumentType.
     * @param path - The absolute path to the ClbDocumentType if no id is provided. If both id and path are provided, the path must be the relative path from the id provided.
     * @param downloadOption - Placeholder for future MTOM attachment support.
     * @return ClbDocumentTypeResponse
     * @throws LoginException_Exception, ServiceException_Exception
     */
    public ClbDocumentTypeResponse getDocumentType(String id, String path, ClbDownloadOption downloadOption) throws LoginException_Exception, ServiceException_Exception
    {
        ClbDocumentTypeResponse result = new ClbDocumentTypeResponse();

        try
        {
            NodeRef nodeRef = pathHelper.resolveNodeRef(id, path);
            ContentData contentData = (ContentData) nodeService.getProperty(nodeRef, ContentModel.PROP_CONTENT);

            ClbDocumentType documentType = new ClbDocumentType();
            if (contentData != null)
            {
                documentType = createDocumentType(contentData.getMimetype(), null);
            }
            else
            {
                documentType.setVersioning(ClbVersioning.EXPLICIT);
            }
            result.setDocumentType(documentType);
        }
        catch (FileNotFoundException e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error(e);
            }

            ClbError error = new ClbError();
            error.setMessage(e.getLocalizedMessage());
            error.setType(ClbErrorType.ITEM_NOT_FOUND);
            result.setError(error);

            return result;
        }

        return result;
    }

    /**
     * Return supported document types.
     * 
     * @param libraryId - The uuid of the library if no path is provided. If both id and path are provided, the id must be the uuid of the parent folder to the library.
     * @param libraryPath - The absolute path to the library if no id is provided. If both id and path are provided, the path must be the relative path from the id provided.
     * @param extensionFilter - Results can be filtered by providing an extension. For example, if you want document types for Microsoft Word documents, you would supply a value of
     *        "doc".
     * @param docTypeOption - If null or a value of LibraryScope is supplied, the operation will return document types that are scoped to the supplied library. If a value of All is
     *        supplied, you will get both the document types that are scoped to the supplied library and those scoped to the application root library.
     * @return ClbDocumentTypesResponse
     * @throws LoginException_Exception,
     */
    public ClbDocumentTypesResponse getDocumentTypes(String libraryId, String libraryPath, String extensionFilter, ClbDocTypeOption docTypeOption) throws LoginException_Exception,
            ServiceException_Exception
    {
        if (documentTypesResponse != null)
        {
            return documentTypesResponse;
        }

        documentTypesResponse = new ClbDocumentTypesResponse();

        List<ClbDocumentType> documentTypes = documentTypesResponse.getDocumentTypes();

        for (String mimeType : mimetypeService.getMimetypes())
        {
            documentTypes.add(createDocumentType(mimeType, null));
        }

        return documentTypesResponse;
    }

    /**
     * Return URL to the document.
     * 
     * @param id - The uuid of the document if no path is provided. If both id and path are provided, the id must be the uuid of the parent folder to the document.
     * @param path - The absolute path to the document if no id is provided. If both id and path are provided, the path must be the relative path from the id provided.
     * @param mode - Indicates if the returned URL should bring up a web page to view the document, or if the URL should prompt the user to download the document.
     * @param secureURL - If true, the returned URL will be prefixed with https.
     * @return ClbContentURLResponse
     * @throws LoginException_Exception, ServiceException_Exception
     */
    public ClbContentURLResponse getDocumentURL(String id, String path, ClbContentURLMode mode, boolean secureURL) throws LoginException_Exception, ServiceException_Exception
    {
        ClbContentURLResponse result = new ClbContentURLResponse();
        try
        {
            NodeRef nodeRef = pathHelper.resolveNodeRef(id, path);
            String nodePath = pathHelper.getNodePath(nodeRef);
            if (nodePath.indexOf("/") != -1 && nodePath.startsWith(pathHelper.getNodeRefSiteName(nodeRef) + "/documentLibrary"))
            {
                result.setUrl(MessageFormat.format(pathHelper.getShareDocumentUrl(), pathHelper.getNodeRefSiteName(nodeRef), nodeRef.toString()));
            }
        }
        catch (FileNotFoundException e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error(e);
            }

            ClbError error = new ClbError();
            error.setMessage(e.getLocalizedMessage());
            error.setType(ClbErrorType.ITEM_NOT_FOUND);
            result.setError(error);

            return result;
        }

        return result;
    }

    public ClbDocumentResponse getDocumentVersion(String id, String path, String version, VersionType versionType, ClbDownloadOption downloadOption)
            throws LoginException_Exception, ServiceException_Exception
    {
        // This operation is not supported now
        ClbDocumentResponse result = new ClbDocumentResponse();
        ClbError error = new ClbError();

        error.setType(ClbErrorType.UNSUPPORTED_OPERATION);
        result.setError(error);

        return result;
    }

    /**
     * Return all document versions.
     * 
     * @param id - The uuid of the document if no path is provided. If both id and path are provided, the id must be the uuid of the parent folder to the document.
     * @param path - The absolute path to the document if no id is provided. If both id and path are provided, the path must be the relative path from the id provided.
     * @return ClbVersionsResponse
     * @throws LoginException_Exception, ServiceException_Exception
     */
    public ClbVersionsResponse getDocumentVersions(String id, String path) throws LoginException_Exception, ServiceException_Exception
    {
        NodeRef nodeRef;
        ClbVersionsResponse result = new ClbVersionsResponse();

        try
        {
            nodeRef = pathHelper.resolveNodeRef(id, path);

            // If document is checked out, then we use working copy
            nodeRef = pathHelper.getDocumentForWork(nodeRef);
            VersionHistory versinHistory = versionService.getVersionHistory(nodeRef);
            Version currentVersion = versionService.getCurrentVersion(nodeRef);
            if (versinHistory == null)
            {
                return result;
            }
            for (Version alfVersion : versinHistory.getAllVersions())
            {
                boolean isActive = false;
                if (alfVersion.getFrozenStateNodeRef().equals(currentVersion.getFrozenStateNodeRef()))
                {
                    isActive = true;
                }
                org.alfresco.repo.lotus.ws.Version version = createVersion(alfVersion, isActive);

                result.getVersions().add(version);
            }

        }
        catch (FileNotFoundException e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error(e);
            }

            ClbError error = new ClbError();
            error.setMessage(e.getLocalizedMessage());
            error.setType(ClbErrorType.ITEM_NOT_FOUND);
            result.setError(error);

            return result;
        }

        return result;
    }

    private org.alfresco.repo.lotus.ws.Version createVersion(Version alfVersion, boolean isActive)
    {
        org.alfresco.repo.lotus.ws.Version version = new org.alfresco.repo.lotus.ws.Version();

        NodeRef versionNodeRef = alfVersion.getFrozenStateNodeRef();

        UserInfo creator = createUserInfo((String) nodeService.getProperty(versionNodeRef, ContentModel.PROP_CREATOR));

        version.setCreator(creator);
        version.setName(alfVersion.getVersionLabel());
        version.setActiveVersion(isActive);
        version.setComments(alfVersion.getDescription());
        version.getLabels().add(alfVersion.getVersionLabel());

        // Don't know why, but user see createdDate in "Date Modified" column in quickr.
        version.setCreated(pathHelper.getXmlDate((Date) alfVersion.getVersionProperty(Version2Model.PROP_FROZEN_MODIFIED)));
        version.setLastModified(pathHelper.getXmlDate((Date) alfVersion.getVersionProperty(Version2Model.PROP_FROZEN_CREATED)));

        return version;
    }

    /**
     * Retrieves a list of documents that are in a specified folder.
     * 
     * @param id The uuid of the folder that contains the documents to retrieve if no path is provided. If both id and path are provided, the uuid must be the uuid of the folder
     *        that is a parent to the target folder that contains the documents to retrieve.
     * @param path The absolute path to the document if no id is provided. If id and path are provided the path must be the relative path from the parent folder to the target
     *        folder that contains the documents to retrieve.
     * @param serviceOptions Multiple service options can be provided to control the returned results. The supported values are None, IncludeDrafts, IncludeSubmittedDrafts,
     *        IncludeReferences, IncludePermissions and ResolveLockOwner.
     * @return ClbDocumentsResponse
     * @throws LoginException_Exception, ServiceException_Exception
     */
    public ClbDocumentsResponse getDocuments(String id, String path, List<ClbServiceOption> serviceOptions) throws LoginException_Exception, ServiceException_Exception
    {
        ClbDocumentsResponse result = new ClbDocumentsResponse();
        try
        {
            NodeRef parent = pathHelper.resolveNodeRef(id, path);

            for (FileInfo fileInfo : fileFolderService.listFiles(parent))
            {
                if (!fileInfo.isLink() && !nodeService.hasAspect(fileInfo.getNodeRef(), ContentModel.ASPECT_WORKING_COPY))
                {
                    // If document is checked out, then we use working copy
                    NodeRef documentNodeRef = pathHelper.getDocumentForWork(fileInfo.getNodeRef());

                    result.getDocuments().add(createDocument(documentNodeRef, serviceOptions, null));
                }
            }
        }
        catch (FileNotFoundException e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error(e);
            }

            ClbError error = new ClbError();
            error.setMessage(e.getLocalizedMessage());
            error.setType(ClbErrorType.ITEM_NOT_FOUND);
            result.setError(error);

            return result;
        }
        return result;
    }

    /**
     * Retrieves the requested draft
     * 
     * @param id The uuid of the draft if no path is provided. If both id and path are provided, the id must be the uuid of the parent folder to the document.
     * @param path The absolute path to the draft if no id is provided. If both id and path are provided, the path must be the relative path from the id provided.
     * @param downloadOption Placeholder for future MTOM attachment support.
     * @return ClbDraftResponse
     * @throws LoginException_Exception, ServiceException_Exception
     */
    public ClbDraftResponse getDraft(String id, String path, ClbDownloadOption downloadOption) throws LoginException_Exception, ServiceException_Exception
    {
        ClbDraftResponse result = new ClbDraftResponse();
        try
        {
            NodeRef draftNodeRef = pathHelper.resolveNodeRef(id, path);

            result.setDraft(createDraft(draftNodeRef));
        }
        catch (FileNotFoundException e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error(e);
            }

            ClbError error = new ClbError();
            error.setMessage(e.getLocalizedMessage());
            error.setType(ClbErrorType.ITEM_NOT_FOUND);
            result.setError(error);

            return result;
        }
        return result;
    }

    /**
     * Retrieves the requested folder.
     * 
     * @param id The uuid of the folder if no path is provided. If both id and path are provided, the id must be the uuid of the parent folder to the target folder.
     * @param path The absolute path to the folder if no id is provided. If both id and path are provided, the path must be the relative path from the id provided.
     * @param serviceOptions Multiple service options can be provided to control the returned results. The supported values are None, IncludeFolderChildren, IncludePermissions, and
     *        IncludePropertySheets.
     * @return ClbFolderResponse
     * @throws LoginException_Exception, ServiceException_Exception
     */
    public ClbFolderResponse getFolder(String id, String path, List<ClbServiceOption> serviceOptions) throws LoginException_Exception, ServiceException_Exception
    {
        ClbFolderResponse result = new ClbFolderResponse();
        try
        {
            NodeRef folderNodeRef = pathHelper.resolveNodeRef(id, path);

            result.setFolder(createFolder(folderNodeRef, serviceOptions));
        }
        catch (FileNotFoundException e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error(e);
            }

            ClbError error = new ClbError();
            error.setMessage(e.getLocalizedMessage());
            error.setType(ClbErrorType.ITEM_NOT_FOUND);
            result.setError(error);

            return result;
        }
        return result;
    }

    /**
     * Return URL to the folder.
     * 
     * @param id - The uuid of the folder if no path is provided. If both id and path are provided, the id must be the uuid of the parent folder to the requested folder.
     * @param path - The absolute path to the folder if no id is provided. If both id and path are provided, the path must be the relative path from the id provided.
     * @param mode - Ignored for this operation. Only view URLs are supported.
     * @param secureURL - If true, the returned URL will be prefixed with https.
     * @return ClbContentURLResponse
     * @throws LoginException_Exception, ServiceException_Exception
     */
    public ClbContentURLResponse getFolderURL(String id, String path, ClbContentURLMode mode, boolean secureURL) throws LoginException_Exception, ServiceException_Exception
    {
        ClbContentURLResponse result = new ClbContentURLResponse();
        try
        {
            NodeRef nodeRef = pathHelper.resolveNodeRef(id, path);
            String folderPath = pathHelper.getNodePath(nodeRef);
            if (nodeService.getType(nodeRef).equals(SiteModel.TYPE_SITE))
            {
                result.setUrl(MessageFormat.format(pathHelper.getShareSiteUrl(), folderPath));
                return result;
            }
            if (folderPath.indexOf("/") != -1)
            {
                String siteName = pathHelper.getNodeRefSiteName(nodeRef);
                if (folderPath.startsWith(siteName + "/documentLibrary"))
                {
                    result.setUrl(MessageFormat.format(pathHelper.getShareFolderUrl(), siteName, folderPath.replaceAll(siteName + "/documentLibrary", "")));
                }
            }
        }
        catch (FileNotFoundException e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error(e);
            }

            ClbError error = new ClbError();
            error.setMessage(e.getLocalizedMessage());
            error.setType(ClbErrorType.ITEM_NOT_FOUND);
            result.setError(error);

            return result;
        }

        return result;
    }

    /**
     * Retrieves a list of folders that are in a specified folder.
     * 
     * @param id The uuid of the folder that contains the folders to retrieve if no path is provided. If both id and path are provided, the uuid must be the uuid of the folder that
     *        is a parent to the target folder.
     * @param path The absolute path to the folder if no id is provided. If id and path are provided the path must be the relative path from the parent folder to the target folder.
     * @return ClbFoldersResponse
     * @throws LoginException_Exception, ServiceException_Exception
     */
    public ClbFoldersResponse getFolders(String id, String path) throws LoginException_Exception, ServiceException_Exception
    {
        ClbFoldersResponse result = new ClbFoldersResponse();

        List<ClbFolder> folders = result.getFolders();
        try
        {
            NodeRef selectedFolderRef = pathHelper.resolveNodeRef(id, path);

            for (FileInfo fileInfo : fileFolderService.listFolders(selectedFolderRef))
            {
                folders.add(createFolder(fileInfo.getNodeRef(), null));
            }
        }
        catch (FileNotFoundException e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error(e);
            }

            ClbError error = new ClbError();
            error.setMessage(e.getLocalizedMessage());
            error.setType(ClbErrorType.ITEM_NOT_FOUND);
            result.setError(error);

            return result;
        }
        return result;
    }

    public ClbDocumentsResponse getLockedDocuments(String id, String path) throws LoginException_Exception, ServiceException_Exception
    {
        // This operation is not supported now
        ClbDocumentsResponse result = new ClbDocumentsResponse();
        ClbError error = new ClbError();

        error.setType(ClbErrorType.UNSUPPORTED_OPERATION);
        result.setError(error);

        return result;
    }

    public ClbDraftsResponse getPrivateDrafts(String id, String path) throws LoginException_Exception, ServiceException_Exception
    {
        // This operation is not supported now
        ClbDraftsResponse result = new ClbDraftsResponse();
        ClbError error = new ClbError();

        error.setType(ClbErrorType.UNSUPPORTED_OPERATION);
        result.setError(error);

        return result;
    }

    public ClbViewsResponse getPrivateViews(String folderId) throws LoginException_Exception, ServiceException_Exception
    {
        // This operation is not supported now
        ClbViewsResponse result = new ClbViewsResponse();
        ClbError error = new ClbError();

        error.setType(ClbErrorType.UNSUPPORTED_OPERATION);
        result.setError(error);

        return result;
    }

    public ClbPropertySheetTypeResponse getPropertySheetType(String id) throws LoginException_Exception, ServiceException_Exception
    {
        // This operation is not supported now
        ClbPropertySheetTypeResponse result = new ClbPropertySheetTypeResponse();
        ClbError error = new ClbError();

        error.setType(ClbErrorType.UNSUPPORTED_OPERATION);
        result.setError(error);

        return result;
    }

    public ClbViewsResponse getSharedViews(String folderId) throws LoginException_Exception, ServiceException_Exception
    {
        // This operation is not supported now
        ClbViewsResponse result = new ClbViewsResponse();
        ClbError error = new ClbError();

        error.setType(ClbErrorType.UNSUPPORTED_OPERATION);
        result.setError(error);

        return result;
    }

    public ClbDraftsResponse getSubmittedDrafts(String id, String path) throws LoginException_Exception, ServiceException_Exception
    {
        // This operation is not supported now
        ClbDraftsResponse result = new ClbDraftsResponse();
        ClbError error = new ClbError();

        error.setType(ClbErrorType.UNSUPPORTED_OPERATION);
        result.setError(error);

        return result;
    }

    public ClbTreeResponse getTree(String id, String path) throws LoginException_Exception, ServiceException_Exception
    {
        // This operation is not supported now
        ClbTreeResponse result = new ClbTreeResponse();
        ClbError error = new ClbError();

        error.setType(ClbErrorType.UNSUPPORTED_OPERATION);
        result.setError(error);

        return result;
    }

    public ClbViewResponse getView(String viewId) throws LoginException_Exception, ServiceException_Exception
    {
        // This operation is not supported now
        ClbViewResponse result = new ClbViewResponse();
        ClbError error = new ClbError();

        error.setType(ClbErrorType.UNSUPPORTED_OPERATION);
        result.setError(error);

        return result;
    }

    public ClbViewFormatResponse getViewFormat(String viewId) throws LoginException_Exception, ServiceException_Exception
    {
        // This operation is not supported now
        ClbViewFormatResponse result = new ClbViewFormatResponse();
        ClbError error = new ClbError();

        error.setType(ClbErrorType.UNSUPPORTED_OPERATION);
        result.setError(error);

        return result;
    }

    /**
     * Check out document.
     * 
     * @param id - The uuid of the document if no path is provided. If both id and path are provided, the id must be the uuid of the parent folder to the document.
     * @param path - The absolute path to the document if no id is provided. If both id and path are provided, the path must be the relative path from the id provided.
     * @return ClbResponse
     * @throws LoginException_Exception, ServiceException_Exception
     */
    public ClbResponse lockDocument(String id, String path) throws LoginException_Exception, ServiceException_Exception
    {
        ClbResponse result = new ClbResponse();
        try
        {
            final NodeRef nodeRef = pathHelper.resolveNodeRef(id, path);
            if (!pathHelper.isInRmSite(nodeRef))
            {
                checkOutCheckInService.checkout(nodeRef);
            }
            else
            {
                ClbError error = new ClbError();
                error.setType(ClbErrorType.ACCESS_DENIED);
                result.setError(error);

                return result;
            }
        }
        catch (FileNotFoundException e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error(e);
            }

            ClbError error = new ClbError();
            error.setMessage(e.getLocalizedMessage());
            error.setType(ClbErrorType.ITEM_NOT_FOUND);
            result.setError(error);

            return result;
        }

        return result;
    }

    /**
     * Move a document.
     * 
     * @param srcId The uuid of the source document if no path is provided. If both srcId and srcPath are provided, the srcId must be the uuid of the parent folder to the document
     *        that is being moved.
     * @param srcPath The absolute path to the source document if no srcId is provided. If both srcId and srcPath are provided, the path must be the relative path from the id
     *        provided.
     * @param destId The uuid of the destination parent folder. The destId should not be supplied without also providing a destPath. If both destId and destPath are provided, the
     *        destId must be the uuid of the destination parent folder
     * @param destPath The absolute path to the resulting document after the move. If both destId and destPath are provided, the path must be the relative path from the id
     *        provided.
     * @return ClbMoveResponse
     * @throws LoginException_Exception, ServiceException_Exception
     */
    public ClbMoveResponse moveDocument(final String srcId, String srcPath, final String destId, String destPath) throws LoginException_Exception, ServiceException_Exception
    {
        return moveFolder(srcId, srcPath, destId, destPath);
    }

    /**
     * Move a folder.
     * 
     * @param srcId The uuid of the source folder if no path is provided. If both srcId and srcPath are provided, the srcId must be the uuid of the parent folder to the folder that
     *        is being moved.
     * @param srcPath The absolute path to the source folder if no srcId is provided. If both srcId and srcPath are provided, the path must be the relative path from the srcId
     *        provided.
     * @param destId The uuid of the detination parent folder. The destId should not be supplied without also providing a destPath. If both destId and destPath are provided, the
     *        destId must be the uuid of the destination parent folder
     * @param destPath The absolute path to the resulting folder after the move. If both destId and destPath are provided, the path must be the relative path from the id provided.
     * @return ClbMoveResponse
     * @throws LoginException_Exception, ServiceException_Exception
     */
    public ClbMoveResponse moveFolder(final String srcId, String srcPath, final String destId, String destPath) throws LoginException_Exception, ServiceException_Exception
    {
        final ClbMoveResponse result = new ClbMoveResponse();
        try
        {
            final NodeRef source = pathHelper.resolveNodeRef(srcId, srcPath);
            final NodeRef destination = pathHelper.resolveNodeRef(destId, destPath.substring(0, destPath.lastIndexOf("/")));

            transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Object>()
            {
                public Object execute()
                {
                    moveItem(source, destination, result);
                    return null;
                }
            });
        }
        catch (FileNotFoundException e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error(e);
            }

            ClbError error = new ClbError();
            error.setMessage(e.getLocalizedMessage());
            error.setType(ClbErrorType.ITEM_NOT_FOUND);
            result.setError(error);

            return result;
        }
        return result;
    }

    private void moveItem(NodeRef srcNodeRef, NodeRef destNodeRef, ClbMoveResponse response)
    {
        try
        {
            FileInfo resultFileInfo = fileFolderService.move(srcNodeRef, destNodeRef, null);

            response.setId(resultFileInfo.getNodeRef().getId());
            response.setLastModified(pathHelper.getXmlDate((Date) nodeService.getProperty(resultFileInfo.getNodeRef(), ContentModel.PROP_MODIFIED)));
            response.setPath(pathHelper.getNodePath(resultFileInfo.getNodeRef()));
        }
        catch (FileExistsException e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error(e);
            }

            ClbError error = new ClbError();
            error.setType(ClbErrorType.ITEM_EXISTS);
            error.setMessage(e.getLocalizedMessage());
            response.setError(error);
        }
        catch (FileNotFoundException e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error(e);
            }

            ClbError error = new ClbError();
            error.setType(ClbErrorType.ITEM_NOT_FOUND);
            error.setMessage(e.getLocalizedMessage());
            response.setError(error);
        }
    }

    public ClbResponse rejectDraft(String id, String path) throws LoginException_Exception, ServiceException_Exception
    {
        // This operation is not supported now
        ClbResponse result = new ClbResponse();
        ClbError error = new ClbError();

        error.setType(ClbErrorType.UNSUPPORTED_OPERATION);
        result.setError(error);

        return result;
    }

    public ClbResponse removeVersionLabel(String id, String path, String version, String label) throws LoginException_Exception, ServiceException_Exception
    {
        // This operation is not supported now
        ClbResponse result = new ClbResponse();
        ClbError error = new ClbError();

        error.setType(ClbErrorType.UNSUPPORTED_OPERATION);
        result.setError(error);

        return result;
    }

    /**
     * Rename a document.
     * 
     * @param id The uuid of the document if no path is provided. If both id and path are provided, the id must be the uuid of the parent folder to the document.
     * @param path The absolute path to the document if no id is provided. If both id and path are provided, the path must be the relative path from the id provided.
     * @param newName name that the document will be renamed to. The base name of a document can be changed, but not the extension.
     * @return ClbResponse
     * @throws LoginException_Exception, ServiceException_Exception
     */
    public ClbResponse renameDocument(final String id, String path, final String newName) throws LoginException_Exception, ServiceException_Exception
    {
        return renameFolder(id, path, newName);
    }

    /**
     * Rename a folder.
     * 
     * @param id The uuid of the folder if no path is provided. If both id and path are provided, the id must be the uuid of the parent folder to the folder that is being renamed.
     * @param path The absolute path to the folder if no id is provided. If both id and path are provided, the path must be the relative path from the id provided.
     * @param newName name that the folder will be renamed to.
     * @return ClbResponse
     * @throws LoginException_Exception, ServiceException_Exception
     */
    public ClbResponse renameFolder(final String id, String path, final String newName) throws LoginException_Exception, ServiceException_Exception
    {
        final ClbResponse result = new ClbResponse();
        try
        {
            final NodeRef nodeRefToRename = pathHelper.resolveNodeRef(id, path);

            transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Object>()
            {
                public Object execute()
                {
                    renameItem(nodeRefToRename, newName, result);
                    return null;
                }
            });

            return result;
        }
        catch (FileNotFoundException e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error(e);
            }

            ClbError error = new ClbError();
            error.setMessage(e.getLocalizedMessage());
            error.setType(ClbErrorType.ITEM_NOT_FOUND);
            result.setError(error);

            return result;
        }
    }

    private void renameItem(NodeRef nodeRef, String newName, ClbResponse response)
    {
        try
        {
            fileFolderService.rename(nodeRef, newName);
        }
        catch (FileExistsException e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error(e);
            }

            ClbError error = new ClbError();
            error.setType(ClbErrorType.ITEM_EXISTS);
            error.setMessage(e.getLocalizedMessage());
            response.setError(error);
        }
        catch (FileNotFoundException e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error(e);
            }

            ClbError error = new ClbError();
            error.setType(ClbErrorType.ITEM_NOT_FOUND);
            error.setMessage(e.getLocalizedMessage());
            response.setError(error);
        }
    }

    /**
     * Restore specified version of document.
     * 
     * @param id - The uuid of the document if no path is provided. If both id and path are provided, the id must be the uuid of the parent folder to the document.
     * @param path - The absolute path to the document if no id is provided. If both id and path are provided, the path must be the relative path from the id provided.
     * @param version - Either the verion name or verion label.
     * @param versionType - An enumeration value of either Name or Label.
     * @return ClbResponse
     * @throws LoginException_Exception, ServiceException_Exception
     */
    public ClbResponse restoreVersion(String id, String path, String version, VersionType versionType) throws LoginException_Exception, ServiceException_Exception
    {
        ClbResponse result = new ClbResponse();
        try
        {
            NodeRef documentNodeRef = pathHelper.resolveNodeRef(id, path);
            VersionHistory versionHistory = versionService.getVersionHistory(documentNodeRef);
            versionService.revert(documentNodeRef, versionHistory.getVersion(version), false);
        }
        catch (FileNotFoundException e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error(e);
            }

            ClbError error = new ClbError();
            error.setType(ClbErrorType.ITEM_NOT_FOUND);
            error.setMessage(e.getLocalizedMessage());
            result.setError(error);
        }

        return result;
    }

    public ClbResponse submitDraft(String id, String path) throws LoginException_Exception, ServiceException_Exception
    {
        // This operation is not supported now
        ClbResponse result = new ClbResponse();
        ClbError error = new ClbError();

        error.setType(ClbErrorType.UNSUPPORTED_OPERATION);
        result.setError(error);

        return result;
    }

    public ClbResponse unlockDocument(String id, String path) throws LoginException_Exception, ServiceException_Exception
    {
        // This operation is not supported now
        ClbResponse result = new ClbResponse();
        ClbError error = new ClbError();

        error.setType(ClbErrorType.UNSUPPORTED_OPERATION);
        result.setError(error);

        return result;
    }

    public ClbResponse updateDocument(ClbDocument document) throws LoginException_Exception, ServiceException_Exception
    {
        // This operation is not supported now
        ClbResponse result = new ClbResponse();
        ClbError error = new ClbError();

        error.setType(ClbErrorType.UNSUPPORTED_OPERATION);
        result.setError(error);

        return result;
    }

    /**
     * Update corresponding draft in the repository.
     * 
     * @param draft - The data supplied in the draft will be used to update the draft in the repository.
     * @param createDocument - If the createDocument=true and the parent document does not exist, it will be automatically created as part of the draft creation. In this scenario,
     *        the path of the draft should be that of the parent document that will be created.
     * @return ClbResponse
     * @throws LoginException_Exception, ServiceException_Exception
     */
    @SuppressWarnings("unchecked")
    public ClbResponse updateDraft(final ClbDraft draft, boolean createDocument) throws LoginException_Exception, ServiceException_Exception
    {
        final ClbDocumentResponse result = new ClbDocumentResponse();
        try
        {
            NodeRef documentNodeRef = pathHelper.resolveNodeRef(draft.getId(), draft.getPath());

            final NodeRef finalDocumentNodeRef = pathHelper.getDocumentForWork(documentNodeRef);
            ;
            transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Object>()
            {
                public Object execute() throws LoginException_Exception, ServiceException_Exception
                {
                    nodeService.setProperty(finalDocumentNodeRef, ContentModel.PROP_TITLE, draft.getTitle());
                    nodeService.setProperty(finalDocumentNodeRef, ContentModel.PROP_DESCRIPTION, draft.getDescription());

                    ContentData newContentData = ContentData.setMimetype((ContentData) nodeService.getProperty(finalDocumentNodeRef, ContentModel.PROP_CONTENT), draft
                            .getDocumentType().getId());
                    nodeService.setProperty(finalDocumentNodeRef, ContentModel.PROP_CONTENT, newContentData);

                    return null;
                }
            });

            result.setDocument(createDocument(finalDocumentNodeRef, Collections.EMPTY_LIST, null));
        }
        catch (FileNotFoundException e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error(e);
            }

            ClbError error = new ClbError();
            error.setMessage(e.getLocalizedMessage());
            error.setType(ClbErrorType.ITEM_NOT_FOUND);
            result.setError(error);

            return result;
        }

        return result;
    }

    /**
     * Update corresponding folder in the repository.
     * 
     * @param folder - The id or path attribute of the foler should reference the folder in the repository to be updated. The data supplied in the folder will be used to update the
     *        folder in the repository.
     * @return ClbResponse
     * @throws LoginException_Exception, ServiceException_Exception
     */
    public ClbResponse updateFolder(final ClbFolder folder) throws LoginException_Exception, ServiceException_Exception
    {
        final ClbResponse result = new ClbResponse();
        try
        {
            NodeRef folderNodeRef;
            if (folder.getId() != null)
            {
                folderNodeRef = pathHelper.resolveNodeRef(folder.getId(), null);
            }
            else
            {
                folderNodeRef = pathHelper.resolveNodeRef(null, folder.getPath());
            }

            final NodeRef finalFolderNodeRef = folderNodeRef;
            transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Object>()
            {
                public Object execute() throws LoginException_Exception, ServiceException_Exception
                {
                    nodeService.setProperty(finalFolderNodeRef, ContentModel.PROP_DESCRIPTION, folder.getDescription());
                    return null;
                }
            });
        }
        catch (FileNotFoundException e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error(e);
            }

            ClbError error = new ClbError();
            error.setMessage(e.getLocalizedMessage());
            error.setType(ClbErrorType.ITEM_NOT_FOUND);
            result.setError(error);

            return result;
        }

        return result;
    }

    private ClbFolder createFolder(NodeRef folderRef, List<ClbServiceOption> serviceOptions)
    {
        ClbFolder folder = new ClbFolder();

        Date createdDate = (Date) nodeService.getProperty(folderRef, ContentModel.PROP_CREATED);
        folder.setCreated(pathHelper.getXmlDate(createdDate));
        folder.setSystemCreated(pathHelper.getXmlDate(createdDate));

        UserInfo creator = createUserInfo((String) nodeService.getProperty(folderRef, ContentModel.PROP_CREATOR));
        folder.setCreator(creator);

        folder.setDescription((String) nodeService.getProperty(folderRef, ContentModel.PROP_DESCRIPTION));

        Date expirationDate = (Date) nodeService.getProperty(folderRef, ContentModel.PROP_EXPIRY_DATE);
        folder.setExpirationDate(pathHelper.getXmlDate(expirationDate));

        folder.setHidden(false);

        folder.setId(folderRef.getId());

        folder.setLabel((String) nodeService.getProperty(folderRef, ContentModel.PROP_NAME));

        Date lastModifiedDate = (Date) nodeService.getProperty(folderRef, ContentModel.PROP_MODIFIED);
        folder.setLastModified(pathHelper.getXmlDate(lastModifiedDate));
        folder.setSystemLastModified(pathHelper.getXmlDate(lastModifiedDate));

        UserInfo modifier = createUserInfo((String) nodeService.getProperty(folderRef, ContentModel.PROP_MODIFIER));
        folder.setLastModifier(modifier);

        folder.setPath(pathHelper.getNodePath(folderRef));

        folder.setTitle((String) nodeService.getProperty(folderRef, ContentModel.PROP_TITLE));

        if (serviceOptions != null)
        {
            if (serviceOptions.contains(ClbServiceOption.INCLUDE_PROPERTY_SHEETS))
            {
                folder.getPropertySheets().add(new ClbPropertySheet());
            }

            if (serviceOptions.contains(ClbServiceOption.INCLUDE_PERMISSIONS))
            {
                StringBuilder allowedPerms = new StringBuilder();
                for (String perm : permissionHelper.getPermissions(folderRef))
                {
                    allowedPerms.append(perm).append(",");
                }
                folder.setPermissions(allowedPerms.toString().substring(0, allowedPerms.length() - 1));
            }

            if (serviceOptions.contains(ClbServiceOption.INCLUDE_FOLDER_CHILDREN))
            {
                try
                {
                    ClbDocumentsResponse children = getDocuments(folderRef.getId(), null, serviceOptions);
                    folder.getDocuments().addAll(children.getDocuments());
                }
                catch (LoginException_Exception e)
                {
                    // Do nothing
                }
                catch (ServiceException_Exception e)
                {
                    // Do nothing
                }

            }
        }

        return folder;
    }

    /**
     * Fill data that is common for ClbDocument and ClbDraft
     */
    private ClbData fillCommonData(NodeRef nodeRef, ClbData data)
    {
        // Fill fields from nodeRef
        data.setId(nodeRef.getId());

        Date created = (Date) nodeService.getProperty(nodeRef, ContentModel.PROP_CREATED);
        data.setSystemCreated(pathHelper.getXmlDate(created));
        data.setCreated(pathHelper.getXmlDate(created));

        Date lastModified = (Date) nodeService.getProperty(nodeRef, ContentModel.PROP_MODIFIED);
        data.setSystemLastModified(pathHelper.getXmlDate(lastModified));
        data.setLastModified(pathHelper.getXmlDate(lastModified));

        data.setHidden(false);

        data.setLanguage(I18NUtil.getLocale().toString());

        data.setDescription((String) nodeService.getProperty(nodeRef, ContentModel.PROP_DESCRIPTION));

        data.setTitle((String) nodeService.getProperty(nodeRef, ContentModel.PROP_TITLE));

        UserInfo creator = createUserInfo((String) nodeService.getProperty(nodeRef, ContentModel.PROP_CREATOR));
        data.setCreator(creator);
        data.getAuthors().add(creator);

        UserInfo modifier = createUserInfo((String) nodeService.getProperty(nodeRef, ContentModel.PROP_MODIFIER));
        data.setLastModifier(modifier);

        if (!pathHelper.isInRmSite(nodeRef))
        {
            LockType lock = lockService.getLockType(nodeRef);
            boolean isWorkingCopy = nodeService.hasAspect(nodeRef, ContentModel.ASPECT_WORKING_COPY);

            if (lock == null && !isWorkingCopy)
            {
                data.setLocked(false);
                data.setLockOwner(null);
            }
            else
            {
                String owner = "";
                if (isWorkingCopy)
                {
                    owner = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_WORKING_COPY_OWNER);
                    nodeRef = (NodeRef) nodeService.getProperty(nodeRef, ContentModel.PROP_COPY_REFERENCE);
                }
                else
                {
                    owner = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_LOCK_OWNER);
                }

                data.setLocked(true);
                UserInfo lockOwner = createUserInfo(owner);
                data.setLockOwner(lockOwner);
                data.getOwners().add(lockOwner);
            }
        }
        // If working copy was passed then we fill these filds from original nodeRef
        String fullPath = pathHelper.getNodePath(nodeRef);
        data.setPath(fullPath);
        if (fullPath.indexOf("/") != -1)
        {
            data.setDisplayLocation(fullPath.substring(0, fullPath.lastIndexOf("/")));
        }
        else
        {
            data.setDisplayLocation("");
        }

        String name = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
        data.setLabel(name);

        StringBuilder allowedPerms = new StringBuilder();
        for (String perm : permissionHelper.getPermissions(nodeRef))
        {
            allowedPerms.append(perm).append(",");
        }
        data.setPermissions(allowedPerms.toString().substring(0, allowedPerms.length() - 1));

        return data;
    }

    private ClbDocument createDocument(NodeRef nodeRef, List<ClbServiceOption> serviceOptions, ClbDownloadOption downloadOption) throws LoginException_Exception,
            ServiceException_Exception
    {
        ClbDocument document = new ClbDocument();
        fillCommonData(nodeRef, document);

        Date lastModified = (Date) nodeService.getProperty(nodeRef, ContentModel.PROP_MODIFIED);

        document.setDataLastModified(pathHelper.getXmlDate(lastModified));

        ContentData contentData = fileFolderService.getFileInfo(nodeRef).getContentData();
        if (contentData != null)
        {
            document.setDataLength(Long.valueOf(contentData.getSize()));
            document.setDataMimeType(contentData.getMimetype());
        }
        // changes related to new rootPath that points to Sites folder.
        String url = "";
        String nodePath = pathHelper.getNodePath(nodeRef);
        if (nodePath.indexOf("/") != -1 && nodePath.startsWith(pathHelper.getNodeRefSiteName(nodeRef) + "/documentLibrary"))
        {
            url = MessageFormat.format(pathHelper.getShareDocumentUrl(), pathHelper.getNodeRefSiteName(nodeRef), nodeRef.toString());
        }

        if (serviceOptions.contains(ClbServiceOption.RETRIEVE_DOWNLOAD_URL) || serviceOptions.contains(ClbServiceOption.RETRIEVE_VIEW_URL))
        {
            document.setUrl(url);
        }

        document.setDocumentType(getDocumentType(nodeRef.getId(), null, downloadOption).getDocumentType());

        return document;
    }

    private ClbDraft createDraft(NodeRef nodeRef)
    {
        ClbDraft draft = new ClbDraft();
        fillCommonData(nodeRef, draft);

        Date lastModified = (Date) nodeService.getProperty(nodeRef, ContentModel.PROP_MODIFIED);

        draft.setDataLastModified(pathHelper.getXmlDate(lastModified));
        ContentData contentData = fileFolderService.getFileInfo(nodeRef).getContentData();
        if (contentData != null)
        {
            draft.setDataLength(Long.valueOf(contentData.getSize()));
            draft.setDataMimeType(contentData.getMimetype());
        }

        return draft;
    }

    private UserInfo createUserInfo(String userName)
    {
        UserInfo userInfo = new UserInfo();
        NodeRef personNodeRef = personService.getPerson(userName);

        String userFirstName = (String) nodeService.getProperty(personNodeRef, ContentModel.PROP_FIRSTNAME);
        String userLastName = (String) nodeService.getProperty(personNodeRef, ContentModel.PROP_LASTNAME);

        userInfo.setCommonName(userFirstName + " " + userLastName);
        userInfo.setDn("uid=" + userName + ",o=Default Organization");
        userInfo.setEmailAddress((String) nodeService.getProperty(personNodeRef, ContentModel.PROP_EMAIL));

        return userInfo;
    }
    
    /**
     * Create document type by mime-type or extension
     * 
     * @param mimeType mime-type
     * @param extension extension
     * @return document type
     * @throws ServiceException_Exception
     */
    private ClbDocumentType createDocumentType(String mimeType, String extension) throws ServiceException_Exception
    {
        if (mimeType != null && extension == null)
        {
            extension = mimetypeService.getExtensionsByMimetype().get(mimeType);
        }

        if (extension != null && mimeType == null)
        {
            mimeType = mimetypeService.getMimetypesByExtension().get(extension);
        }

        String title = mimetypeService.getDisplaysByMimetype().get(mimeType) == null ? "" : mimetypeService.getDisplaysByMimetype().get(mimeType);

        ClbDocumentType documentType = new ClbDocumentType();
        documentType.setVersioning(ClbVersioning.EXPLICIT);
        documentType.setDefaultExtension(extension);
        documentType.setId(mimeType);
        documentType.setDescription(title);
        try
        {
            documentType.setTitle(new String(title.getBytes(), "UTF-8"));
        }
        catch (UnsupportedEncodingException e)
        {
            throw new ServiceException_Exception(e.getMessage());
        }

        return documentType;
    }
}
