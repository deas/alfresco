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

package org.alfresco.repo.lotus.ws.impl;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.model.QuickrModel;
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
import org.alfresco.repo.lotus.ws.ClbDraftApprovalState;
import org.alfresco.repo.lotus.ws.ClbDraftApprovalType;
import org.alfresco.repo.lotus.ws.ClbDraftResponse;
import org.alfresco.repo.lotus.ws.ClbDraftsResponse;
import org.alfresco.repo.lotus.ws.ClbDynamicBooleanValue;
import org.alfresco.repo.lotus.ws.ClbDynamicDateValue;
import org.alfresco.repo.lotus.ws.ClbDynamicDoubleValue;
import org.alfresco.repo.lotus.ws.ClbDynamicLongValue;
import org.alfresco.repo.lotus.ws.ClbDynamicStringValue;
import org.alfresco.repo.lotus.ws.ClbError;
import org.alfresco.repo.lotus.ws.ClbErrorType;
import org.alfresco.repo.lotus.ws.ClbFolder;
import org.alfresco.repo.lotus.ws.ClbFolderResponse;
import org.alfresco.repo.lotus.ws.ClbFoldersResponse;
import org.alfresco.repo.lotus.ws.ClbLabelType;
import org.alfresco.repo.lotus.ws.ClbMoveResponse;
import org.alfresco.repo.lotus.ws.ClbOptionType;
import org.alfresco.repo.lotus.ws.ClbPropertySheet;
import org.alfresco.repo.lotus.ws.ClbPropertySheetType;
import org.alfresco.repo.lotus.ws.ClbPropertySheetTypeResponse;
import org.alfresco.repo.lotus.ws.ClbPropertyType;
import org.alfresco.repo.lotus.ws.ClbResponse;
import org.alfresco.repo.lotus.ws.ClbServiceOption;
import org.alfresco.repo.lotus.ws.ClbStyleType;
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
import org.alfresco.repo.lotus.ws.impl.helper.AlfrescoQuickrDataTypeHelper;
import org.alfresco.repo.lotus.ws.impl.helper.AlfrescoQuickrDocumentHelper;
import org.alfresco.repo.lotus.ws.impl.helper.AlfrescoQuickrPathHelper;
import org.alfresco.repo.lotus.ws.impl.helper.AlfrescoQuickrPermissionHelper;
import org.alfresco.repo.lotus.ws.impl.helper.AlfrescoQuickrWorkflowHelper;
import org.alfresco.repo.lotus.ws.impl.helper.DocumentStatus;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.repo.version.Version2Model;
import org.alfresco.repo.version.VersionModel;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.lock.LockType;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileFolderUtil;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionHistory;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.cmr.workflow.WorkflowDefinition;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.cmr.workflow.WorkflowTaskQuery;
import org.alfresco.service.cmr.workflow.WorkflowTaskState;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * @author PavelYur
 */
public class AlfrescoDocumentServiceImpl implements DocumentService
{
    private final static String WORKFLOW_APPROVE_ACTION = "approve";
    private final static String WORKFLOW_REJECT_ACTION = "reject";

    private final static String WORKFLOW_STATUS_REVIEW = "review";

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

    private AlfrescoQuickrWorkflowHelper workflowHelper;

    private ClbDocumentTypesResponse documentTypesResponse;

    private VersionService versionService;

    private PersonService personService;

    private DictionaryService dictionaryService;

    private WorkflowService workflowService;

    public void setWorkflowService(WorkflowService workflowService)
    {
        this.workflowService = workflowService;
    }

    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    public void setPersonService(PersonService personService)
    {
        this.personService = personService;
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

    public void setWorkflowHelper(AlfrescoQuickrWorkflowHelper workflowHelper)
    {
        this.workflowHelper = workflowHelper;
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

    /**
     * Approve draft. If there are additional approvers, the draft will be sent to the next approver in the chain. If there are no other approvers, the draft will be published. If
     * the draft is published, the draftPublished attribute in the ClbApproveDraftResponse will be true.
     * 
     * @param id The uuid of the draft if no path is provided. If both id and path are provided, the id must be the uuid of the parent folder to the draft.
     * @param path The absolute path to the draft if no id is provided. If both id and path are provided, the path must be the relative path from the id provided.
     * @return ClbApproveDraftResponse
     */
    public ClbApproveDraftResponse approveDraft(String id, String path) throws LoginException_Exception, ServiceException_Exception, FileNotFoundException
    {

        final NodeRef documentNodeRef = pathHelper.getDocumentForWork(pathHelper.resolveNodeRef(id, path));
        ClbApproveDraftResponse result = new ClbApproveDraftResponse();

        boolean isPublished = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Boolean>()
        {
            public Boolean execute() throws LoginException_Exception, ServiceException_Exception
            {
                return endWorkflowTask(documentNodeRef, WORKFLOW_APPROVE_ACTION);
            }
        });

        result.setDraftPublished(isPublished);

        return result;
    }

    /**
     * Reject draft.
     * 
     * @param id The uuid of the draft if no path is provided. If both id and path are provided, the id must be the uuid of the parent folder to the draft.
     * @param path The absolute path to the draft if no id is provided. If both id and path are provided, the path must be the relative path from the id provided.
     * @return ClbResponse
     */
    public ClbResponse rejectDraft(String id, String path) throws LoginException_Exception, ServiceException_Exception, FileNotFoundException
    {
        final NodeRef documentNodeRef = pathHelper.getDocumentForWork(pathHelper.resolveNodeRef(id, path));
        transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Object>()
        {
            public Object execute() throws LoginException_Exception, ServiceException_Exception
            {
                endWorkflowTask(documentNodeRef, WORKFLOW_REJECT_ACTION);
                return null;
            }
        });

        return new ClbResponse();
    }

    private boolean endWorkflowTask(final NodeRef documentRef, String transition)
    {
        boolean draftPublished = workflowHelper.endWorkflowTask(documentRef, transition);
        if (draftPublished)
        {
            // Current user may not has checkIn permission. But checkIn must be done.
            AuthenticationUtil.runAs(new RunAsWork<Object>()
            {
                public Object doWork() throws Exception
                {
                    performCheckIn(documentRef, (String) nodeService.getProperty(documentRef, ContentModel.PROP_DESCRIPTION), false);
                    return null;
                }
            }, AuthenticationUtil.getSystemUserName());
        }

        return draftPublished;
    }

    /**
     * Cancel document checkout.
     * 
     * @param id The uuid of the document if no path is provided. If both id and path are provided, the id must be the uuid of the parent folder to the document.
     * @param path The absolute path to the document if no id is provided. If both id and path are provided, the path must be the relative path from the id provided.
     * @return ClbCancelDocumentResponse.
     * @throws LoginException_Exception, ServiceException_Exception
     * @throws FileNotFoundException
     */
    public ClbCancelDocumentResponse cancelDocument(String id, String path) throws LoginException_Exception, ServiceException_Exception, FileNotFoundException
    {
        ClbCancelDocumentResponse result = new ClbCancelDocumentResponse();
        final NodeRef nodeRef = pathHelper.resolveNodeRef(id, path);
        boolean isDocumentDeleted = false;
        if (!pathHelper.isInRmSite(nodeRef))
        {
            isDocumentDeleted = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Boolean>()
            {
                public Boolean execute()
                {
                    NodeRef workingCopy = checkOutCheckInService.getWorkingCopy(nodeRef);
                    if (workingCopy != null)
                    {
                        workflowHelper.cancelWorkflows(workingCopy);
                        NodeRef originalNodeRef = checkOutCheckInService.cancelCheckout(workingCopy);
                        if (nodeService.hasAspect(nodeRef, QuickrModel.ASPECT_QUICKR_INITIAL_DRAFT))
                        {
                            nodeService.deleteNode(originalNodeRef);
                            return true;
                        }

                    }
                    else
                    {
                        lockService.unlock(nodeRef);
                    }
                    return false;
                }
            });
        }

        result.setDocumentDeleted(isDocumentDeleted);

        return result;
    }

    /**
     * Checkin document.
     * 
     * @param document The id or path attribute is the only required attribute to be supplied in the document parameter. The id or path should reference the document to be checked
     *        in. If any other data is supplied in the document, that data will be updated on the document prior to checking it in.
     * @return ClbCheckinResponse.
     * @throws LoginException_Exception, ServiceException_Exception
     * @throws FileNotFoundException
     */
    public ClbCheckinResponse checkinDocument(final ClbDocument document) throws LoginException_Exception, ServiceException_Exception, FileNotFoundException
    {
        ClbCheckinResponse result = new ClbCheckinResponse();

        final boolean keepCheckedOut = false;
        final NodeRef nodeRef = pathHelper.resolveNodeRef(document.getId(), document.getPath());

        FileInfo resultFileInfo = fileFolderService.getFileInfo(nodeRef);
        if (!pathHelper.isInRmSite(nodeRef))
        {
            resultFileInfo = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<FileInfo>()
            {
                public FileInfo execute() throws LoginException_Exception, ServiceException_Exception
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
                        if (workflowHelper.startWorkflowTask(workingCopy))
                        {
                            return documentFileInfo;

                        }
                        else
                        {
                            return performCheckIn(workingCopy, document.getDescription(), keepCheckedOut);
                        }
                    }
                    else
                    {
                        return performCheckIn(documentFileInfo.getNodeRef(), null, false);
                    }
                }
            });
        }
        result.setId(resultFileInfo.getNodeRef().getId());
        result.setPath(pathHelper.getNodePath(resultFileInfo.getNodeRef(), false));
        result.setLastModified(pathHelper.getXmlDate((Date) nodeService.getProperty(resultFileInfo.getNodeRef(), ContentModel.PROP_MODIFIED)));

        return result;
    }

    private FileInfo performCheckIn(NodeRef document, String description, boolean keepCheckedOut)
    {
        if (nodeService.hasAspect(document, ContentModel.ASPECT_WORKING_COPY))
        {
            Map<String, Serializable> props = new HashMap<String, Serializable>(1, 1.0f);
            props.put(Version.PROP_DESCRIPTION, description);
            props.put(VersionModel.PROP_VERSION_TYPE, org.alfresco.service.cmr.version.VersionType.MAJOR);

            AspectDefinition workingCopyDocType = documentHelper.getDocumentTypeAspect(document.getId(), null);
            ClbVersioning versionMode = documentHelper.getVersionMode(workingCopyDocType);
            if (!versionMode.equals(ClbVersioning.IMPLICIT))
            {
                // Set version properties to null to avoid creation of new version during CheckIn
                props = null;
            }

            NodeRef resultNodeRef = checkOutCheckInService.checkin(document, props, null, keepCheckedOut);

            // TODO not sure that this is correct place, maybe approveDraft more suitable.
            if (nodeService.hasAspect(resultNodeRef, QuickrModel.ASPECT_QUICKR_INITIAL_DRAFT))
            {
                nodeService.removeAspect(resultNodeRef, QuickrModel.ASPECT_QUICKR_INITIAL_DRAFT);
            }

            for (QName aspect : nodeService.getAspects(resultNodeRef))
            {
                if (dictionaryService.isSubClass(aspect, QuickrModel.ASPECT_QUICKR_DOC_TYPE) && !workingCopyDocType.getName().equals(aspect))
                {
                    documentHelper.removeDocType(resultNodeRef, aspect);
                }
            }

            return fileFolderService.getFileInfo(resultNodeRef);
        }
        else
        {
            lockService.unlock(document);
            return fileFolderService.getFileInfo(document);
        }

    }

    /**
     * Checkout document.
     * 
     * @param id The uuid of the document if no path is provided. If both id and path are provided, the id must be the uuid of the parent folder to the document.
     * @param path The absolute path to the document if no id is provided. If both id and path are provided, the path must be the relative path from the id provided.
     * @return ClbDocumentResponse.
     * @throws LoginException_Exception, ServiceException_Exception
     * @throws FileNotFoundException
     */
    public ClbDocumentResponse checkoutDocument(String id, String path, ClbDownloadOption downloadOption) throws LoginException_Exception, ServiceException_Exception,
            FileNotFoundException
    {
        ClbDocumentResponse result = new ClbDocumentResponse();
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

        transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute()
            {
                if (!pathHelper.isInRmSite(nodeRef))
                {
                    return checkOutCheckInService.checkout(nodeRef);
                }
                return null;
            }
        });

        result.setDocument(createDocument(pathHelper.getDocumentForWork(nodeRef), new ArrayList<ClbServiceOption>(), null));

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
     * @throws FileNotFoundException
     */
    public ClbCopyResponse copyDocument(final String srcId, String srcPath, final String destId, String destPath, boolean shallowCopy) throws LoginException_Exception,
            ServiceException_Exception, FileNotFoundException
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
     * @throws FileNotFoundException
     */
    public ClbCopyResponse copyFolder(final String srcId, String srcPath, final String destId, String destPath, boolean shallowCopy) throws LoginException_Exception,
            ServiceException_Exception, FileNotFoundException
    {
        final ClbCopyResponse result = new ClbCopyResponse();
        final NodeRef source = pathHelper.resolveNodeRef(srcId, srcPath);
        final NodeRef destination = pathHelper.resolveNodeRef(destId, destPath);

        transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Object>()
        {
            public Object execute() throws FileExistsException, FileNotFoundException
            {
                copyItem(source, destination, result);
                return null;
            }
        });

        return result;
    }

    private void copyItem(NodeRef srcNodeRef, NodeRef destNodeRef, ClbCopyResponse response) throws FileExistsException, FileNotFoundException
    {
        FileInfo resultFileInfo = fileFolderService.copy(srcNodeRef, destNodeRef, null);

        response.setCreated(pathHelper.getXmlDate((Date) nodeService.getProperty(resultFileInfo.getNodeRef(), ContentModel.PROP_CREATED)));
        response.setId(resultFileInfo.getNodeRef().getId());
        response.setLastModified(pathHelper.getXmlDate((Date) nodeService.getProperty(resultFileInfo.getNodeRef(), ContentModel.PROP_MODIFIED)));
        response.setPath(pathHelper.getNodePath(resultFileInfo.getNodeRef(), false));
    }

    /**
     * Creates a published ClbDocument. The path attribute of the document parameter is used to determine where the document will be created.
     * 
     * @param document The new document to create. Return ClbDocumentResponse.
     * @throws LoginException_Exception, ServiceException_Exception
     * @throws FileNotFoundException
     */
    public ClbDocumentResponse createDocument(final ClbDocument document) throws LoginException_Exception, ServiceException_Exception, FileNotFoundException
    {
        ClbDocumentResponse result = new ClbDocumentResponse();
        final NodeRef parentNodeRef = pathHelper.resolveNodeRef(null, document.getPath());

        NodeRef newDoc = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute()
            {
                NodeRef nodeRef = fileFolderService.create(parentNodeRef, document.getLabel(), ContentModel.TYPE_CONTENT).getNodeRef();

                nodeService.setProperty(nodeRef, ContentModel.PROP_TITLE, document.getTitle());
                nodeService.setProperty(nodeRef, ContentModel.PROP_DESCRIPTION, document.getDescription());

                ContentData contentData = (ContentData) nodeService.getProperty(nodeRef, ContentModel.PROP_CONTENT); 
                ContentData.setMimetype(contentData, documentHelper.getMimeType(nodeRef));
                nodeService.setProperty(nodeRef, ContentModel.PROP_CONTENT, contentData);
                
                return nodeRef;
            }
        });

        result.setDocument(createDocument(newDoc, new ArrayList<ClbServiceOption>(), null));

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
     * @throws FileNotFoundException
     */
    public ClbDraftResponse createDraft(final String id, final String path, final ClbDraft draft) throws LoginException_Exception, ServiceException_Exception,
            FileNotFoundException
    {
        final ClbDraftResponse result = new ClbDraftResponse();

        transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Object>()
        {
            public Object execute() throws LoginException_Exception, ServiceException_Exception, FileNotFoundException
            {
                NodeRef parentNodeRef = null;
                String name = null;
                if (id == null)
                {
                    try
                    {
                        parentNodeRef = pathHelper.resolveNodeRef(null, path.substring(0, path.lastIndexOf("/")));
                    }
                    catch (FileNotFoundException e)
                    {

                        // Create missing folders
                        String formattedPath = pathHelper.resolveNodePath(path.substring(0, path.lastIndexOf("/")), false);

                        List<String> pathElements = Arrays.asList(formattedPath.split("/"));
                        parentNodeRef = FileFolderUtil.makeFolders(fileFolderService, pathHelper.getRootNodeRef(), pathElements, ContentModel.TYPE_FOLDER).getNodeRef();
                    }
                    name = path.substring(path.lastIndexOf("/") + 1);
                }
                else
                {
                    parentNodeRef = pathHelper.resolveNodeRef(id, null);
                    name = path;
                }

                final NodeRef finalParentNodeRef = parentNodeRef;
                final String finalName = name;

                NodeRef nodeRef = fileFolderService.create(finalParentNodeRef, finalName, ContentModel.TYPE_CONTENT).getNodeRef();
                ContentWriter writer = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
                writer.setMimetype(documentHelper.getMimeType(nodeRef));
                writer.putContent("");
                setDocType(nodeRef, draft);
                if (!pathHelper.isInRmSite(nodeRef))
                {
                    nodeService.addAspect(nodeRef, QuickrModel.ASPECT_QUICKR_INITIAL_DRAFT, null);
                    nodeRef = checkOutCheckInService.checkout(nodeRef);
                }
                result.setDraft(createDraft(nodeRef));
                return null;
            }
        });

        return result;
    }

    /**
     * The creates a Folder in the repository. The data provided in the folder parameter will be used to populate the new Folder in the repository.
     * 
     * @param folder - The data that will be used to populate the new folder. The path attribute must contain the absolute path of the folder to be created.
     * @return ClbFolderResponse.
     * @throws LoginException_Exception, ServiceException_Exception
     * @throws FileNotFoundException
     */
    public ClbFolderResponse createFolder(final ClbFolder folder) throws LoginException_Exception, ServiceException_Exception, FileNotFoundException
    {
        ClbFolderResponse result = new ClbFolderResponse();
        final String path = folder.getPath();

        NodeRef newFolder = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute()
            {
                NodeRef parentNodeRef = null;
                try
                {
                    parentNodeRef = pathHelper.resolveNodeRef(null, path.substring(0, path.lastIndexOf("/")));
                }
                catch (FileNotFoundException e)
                {
                    // Create multiple folders
                    String formattedPath = pathHelper.resolveNodePath(path, false);

                    List<String> pathElements = Arrays.asList(formattedPath.split("/"));
                    return FileFolderUtil.makeFolders(fileFolderService, pathHelper.getRootNodeRef(), pathElements, ContentModel.TYPE_FOLDER).getNodeRef();
                }

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
     * @throws FileNotFoundException
     */
    public ClbVersionResponse createVersion(String id, String path, String comments) throws LoginException_Exception, ServiceException_Exception, FileNotFoundException
    {
        ClbVersionResponse result = new ClbVersionResponse();
        NodeRef nodeRef = pathHelper.resolveNodeRef(id, path);

        if (pathHelper.isInRmSite(nodeRef))
        {
            ClbError error = new ClbError();
            error.setType(ClbErrorType.ACCESS_DENIED);
            result.setError(error);

            return result;
        }
        nodeRef = pathHelper.getOriginalDocument(nodeRef);

        if (documentHelper.getVersionMode(nodeRef).equals(ClbVersioning.EXPLICIT))
        {
            Map<String, Serializable> versionProperties = new HashMap<String, Serializable>();
            versionProperties.put(Version.PROP_DESCRIPTION, comments);
            versionProperties.put(VersionModel.PROP_VERSION_TYPE, org.alfresco.service.cmr.version.VersionType.MAJOR);

            Version version = versionService.createVersion(nodeRef, versionProperties);
            result.setVersionName(version.getVersionLabel());
        }
        else
        {
            ClbError error = new ClbError();
            error.setType(ClbErrorType.INVALID_TYPE_FOR_OPERATION);
            error.setMessage("Document must have Document Type with EXPLICIT versionig to perform this operation.");
            result.setError(error);
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
     * @throws FileNotFoundException
     */
    public ClbResponse deleteDocument(final String id, String path) throws LoginException_Exception, ServiceException_Exception, FileNotFoundException
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
     * @throws FileNotFoundException
     */
    public ClbResponse deleteFolder(final String id, String path) throws LoginException_Exception, ServiceException_Exception, FileNotFoundException
    {
        final ClbResponse result = new ClbResponse();
        final NodeRef nodeRefToDelete = pathHelper.resolveNodeRef(id, path);

        transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Object>()
        {
            public Object execute()
            {
                deleteItem(nodeRefToDelete, result);
                return null;
            }
        });

        return result;
    }

    private void deleteItem(NodeRef nodeRef, ClbResponse response)
    {
        fileFolderService.delete(nodeRef);
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
        ClbViewsResponse result = new ClbViewsResponse();
        return result;
    }

    /**
     * Return list of drafts that current user have to approve/reject.
     * 
     * @param id If no path is provided, this should be the uuid of the folder to look for drafts under. If both id and path are provided, the id must be the uuid of the parent
     *        folder of the search folder.
     * @param path If no id is provided, the absolute path to the folder to look for drafts under. If both id and path are provided, the path must be the relative path from the id
     *        provided.
     * @return ClbDraftsResponse
     */
    public ClbDraftsResponse getApproveDrafts(String id, String path) throws LoginException_Exception, ServiceException_Exception, FileNotFoundException
    {
        final NodeRef rootFolder = pathHelper.resolveNodeRef(id, path);

        ClbDraftsResponse result = new ClbDraftsResponse();

        List<ClbDraft> drafts = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<List<ClbDraft>>()
        {
            public List<ClbDraft> execute() throws LoginException_Exception, ServiceException_Exception
            {
                List<ClbDraft> result = new ArrayList<ClbDraft>();
                List<WorkflowTask> assignedTasks = workflowService.getAssignedTasks(AuthenticationUtil.getFullyAuthenticatedUser(), WorkflowTaskState.IN_PROGRESS);
                for (WorkflowTask workflowTask : assignedTasks)
                {
                    if (workflowTask.title.equalsIgnoreCase(WORKFLOW_STATUS_REVIEW))
                    {
                        NodeRef packageNodeRef = (NodeRef) workflowTask.properties.get(WorkflowModel.TYPE_PACKAGE);
                        for (ChildAssociationRef child : nodeService.getChildAssocs(packageNodeRef))
                        {
                            if (pathHelper.isChild(child.getChildRef(), rootFolder))
                            {
                                result.add(createDraft(child.getChildRef()));
                            }

                        }
                    }
                }

                return result;
            }
        });

        result.getDrafts().addAll(drafts);

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
     * @throws FileNotFoundException
     */
    public ClbDocumentResponse getDocument(String id, String path, ClbDownloadOption downloadOption, List<ClbServiceOption> serviceOptions) throws LoginException_Exception,
            ServiceException_Exception, FileNotFoundException
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
                document.setDocumentType(createDocumentType(QuickrModel.ASPECT_QUICKR_DOC_TYPE));
                result.setDocument(document);
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
        ClbDocumentType documentType = new ClbDocumentType();
        AspectDefinition docType = documentHelper.getDocumentTypeAspect(id, path);

        documentType.setDefaultExtension("");
        if (docType != null)
        {
            documentType = createDocumentType(docType.getName());
        }
        result.setDocumentType(documentType);

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

        for (QName docType : dictionaryService.getSubAspects(QuickrModel.ASPECT_QUICKR_DOC_TYPE, true))
        {
            documentTypes.add(createDocumentType(docType));
        }

        return documentTypesResponse;
    }

    private ClbDocumentType createDocumentType(QName typeQname)
    {
        ClbDocumentType documentType = new ClbDocumentType();
        AspectDefinition docType = dictionaryService.getAspect(typeQname);
        documentType.setVersioning(documentHelper.getVersionMode(docType));
        documentType.setDefaultExtension("");
        documentType.setId(docType.getTitle());
        documentType.setDescription(docType.getDescription());
        documentType.setTitle(docType.getTitle());
        documentType.setApprovalEnabled(false);
        documentType.setPath(docType.getTitle());

        List<ClbPropertySheetType> propSheetTypes = documentType.getPropertySheetTypes();
        for (AspectDefinition aspectDefinition : docType.getDefaultAspects(false))
        {
            if (dictionaryService.isSubClass(aspectDefinition.getName(), QuickrModel.ASPECT_QUICKR_PROP_SHEET))
            {
                ClbPropertySheetTypeResponse propertySheetType = getPropertySheetType(aspectDefinition.getTitle(), false);
                propSheetTypes.add(propertySheetType.getPropertySheetType());

                // propSheetTypes.add(propertySheetType);
            }
            else if (dictionaryService.isSubClass(aspectDefinition.getName(), QuickrModel.ASPECT_QUICKR_DRAFT_APPROVAL_TYPE))
            {
                documentType.setApprovalEnabled(true);
                documentType.setApprovalType(ClbDraftApprovalType.fromValue(aspectDefinition.getTitle()));
            }

        }

        return documentType;
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
     * @throws FileNotFoundException
     */
    public ClbContentURLResponse getDocumentURL(String id, String path, ClbContentURLMode mode, boolean secureURL) throws LoginException_Exception, ServiceException_Exception,
            FileNotFoundException
    {
        ClbContentURLResponse result = new ClbContentURLResponse();
        NodeRef nodeRef = pathHelper.resolveNodeRef(id, path);
        String nodePath = pathHelper.getNodePath(nodeRef, true);
        if (nodePath.indexOf("/") != -1 && nodePath.startsWith(pathHelper.getNodeRefSiteName(nodeRef) + "/documentLibrary"))
        {
            result.setUrl(MessageFormat.format(pathHelper.getShareDocumentUrl(), pathHelper.getNodeRefSiteName(nodeRef), nodeRef.toString()));
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
     * @throws FileNotFoundException
     */
    public ClbVersionsResponse getDocumentVersions(String id, String path) throws LoginException_Exception, ServiceException_Exception, FileNotFoundException
    {
        NodeRef nodeRef;
        ClbVersionsResponse result = new ClbVersionsResponse();

        nodeRef = pathHelper.resolveNodeRef(id, path);

        nodeRef = pathHelper.getOriginalDocument(nodeRef);
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

        return result;
    }

    private org.alfresco.repo.lotus.ws.Version createVersion(Version alfVersion, boolean isActive)
    {
        org.alfresco.repo.lotus.ws.Version version = new org.alfresco.repo.lotus.ws.Version();

        NodeRef versionNodeRef = alfVersion.getFrozenStateNodeRef();

        UserInfo creator = createUserInfo((String) nodeService.getProperty(versionNodeRef, ContentModel.PROP_CREATOR));

        version.setCreator(creator);
        version.setName(alfVersion.getVersionLabel());
        version.setActiveVersion(isActive ? 1 : 0);
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
     * @throws FileNotFoundException
     */
    public ClbDocumentsResponse getDocuments(String id, String path, List<ClbServiceOption> serviceOptions) throws LoginException_Exception, ServiceException_Exception,
            FileNotFoundException
    {
        ClbDocumentsResponse result = new ClbDocumentsResponse();
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
     * @throws FileNotFoundException
     */
    public ClbDraftResponse getDraft(String id, String path, ClbDownloadOption downloadOption) throws LoginException_Exception, ServiceException_Exception, FileNotFoundException
    {
        ClbDraftResponse result = new ClbDraftResponse();
        NodeRef draftNodeRef = pathHelper.resolveNodeRef(id, path);

        result.setDraft(createDraft(draftNodeRef));

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
     * @throws FileNotFoundException
     */
    public ClbFolderResponse getFolder(String id, String path, List<ClbServiceOption> serviceOptions) throws LoginException_Exception, ServiceException_Exception,
            FileNotFoundException
    {
        ClbFolderResponse result = new ClbFolderResponse();
        NodeRef folderNodeRef = pathHelper.resolveNodeRef(id, path);

        result.setFolder(createFolder(folderNodeRef, serviceOptions));

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
     * @throws FileNotFoundException
     */
    public ClbContentURLResponse getFolderURL(String id, String path, ClbContentURLMode mode, boolean secureURL) throws LoginException_Exception, ServiceException_Exception,
            FileNotFoundException
    {
        ClbContentURLResponse result = new ClbContentURLResponse();
        NodeRef nodeRef = pathHelper.resolveNodeRef(id, path);
        String folderPath = pathHelper.getNodePath(nodeRef, true);
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
                result.setUrl(MessageFormat.format(pathHelper.getShareFolderUrl(), siteName, nodeRef));
            }
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
     * @throws FileNotFoundException
     */
    public ClbFoldersResponse getFolders(String id, String path) throws LoginException_Exception, ServiceException_Exception, FileNotFoundException
    {
        ClbFoldersResponse result = new ClbFoldersResponse();

        List<ClbFolder> folders = result.getFolders();
        NodeRef selectedFolderRef = pathHelper.resolveNodeRef(id, path);

        for (FileInfo fileInfo : fileFolderService.listFolders(selectedFolderRef))
        {
            folders.add(createFolder(fileInfo.getNodeRef(), null));
        }
        return result;
    }

    /**
     * Retrieves a list of documents, which are checked out by the current user, and that are under the specified folder.
     * 
     * @param id The uuid of the folder where the search will begin if no path is provided. If both id and path are provided, the uuid must be the uuid of the folder that is a
     *        parent to the target folder.
     * @param path The absolute path to the folder where the search will begin if no path is provided. If id and path are provided the path must be the relative path from the
     *        parent folder to the target folder.
     * @return ClbDocumentsResponse
     */
    public ClbDocumentsResponse getLockedDocuments(String id, String path) throws LoginException_Exception, ServiceException_Exception, FileNotFoundException
        {
        final NodeRef rootFolder = pathHelper.resolveNodeRef(id, path);

        ClbDocumentsResponse result = new ClbDocumentsResponse();

        List<FileInfo> drafts = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<List<FileInfo>>()
        {
            public List<FileInfo> execute() throws LoginException_Exception, ServiceException_Exception
            {
                return getPrivateDraftsRec(rootFolder, false, AuthenticationUtil.getFullyAuthenticatedUser());
            }
        });

        for (FileInfo draft : drafts)
        {
            result.getDocuments().add(createDocument(draft.getNodeRef(), new ArrayList<ClbServiceOption>(), null));
        }

        return result;
    }

    /**
     * Retrieves a list of documents, which are checked out by the current user but not submitted for approval, and that are under the specified folder.
     * 
     * @param id The uuid of the folder where the search will begin if no path is provided. If both id and path are provided, the uuid must be the uuid of the folder that is a
     *        parent to the target folder.
     * @param path The absolute path to the folder where the search will begin if no path is provided. If id and path are provided the path must be the relative path from the
     *        parent folder to the target folder.
     * @return ClbDraftsResponse
     */
    public ClbDraftsResponse getPrivateDrafts(String id, String path) throws LoginException_Exception, ServiceException_Exception, FileNotFoundException
    {
        final NodeRef rootFolder = pathHelper.resolveNodeRef(id, path);

        List<FileInfo> drafts = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<List<FileInfo>>()
        {
            public List<FileInfo> execute() throws LoginException_Exception, ServiceException_Exception
            {
                return getPrivateDraftsRec(rootFolder, true, AuthenticationUtil.getFullyAuthenticatedUser());
            }
        });

        ClbDraftsResponse result = new ClbDraftsResponse();
        for (FileInfo draft : drafts)
        {
            result.getDrafts().add(createDraft(draft.getNodeRef()));
        }

        return result;
    }

    private List<FileInfo> getPrivateDraftsRec(NodeRef parent, boolean skipWorkflows, String userName) throws LoginException_Exception, ServiceException_Exception
    {
        List<FileInfo> result = new ArrayList<FileInfo>();

        List<FileInfo> files = fileFolderService.listFiles(parent);
        for (FileInfo fileInfo : files)
        {
            if (nodeService.hasAspect(fileInfo.getNodeRef(), ContentModel.ASPECT_WORKING_COPY))
            {
                boolean isInWorkflow = false;

                if (skipWorkflows)
                {
                    if (workflowService.getWorkflowsForContent(fileInfo.getNodeRef(), true).size() > 0)
                    {
                        isInWorkflow = true;
                    }
                }
                if (!isInWorkflow || !skipWorkflows)
                {
                    if (fileInfo.getProperties().get(ContentModel.PROP_CREATOR).equals(userName))
                    {
                        result.add(fileInfo);
                    }
                }

            }
        }

        List<FileInfo> folders = fileFolderService.listFolders(parent);
        for (FileInfo folderInfo : folders)
        {
            result.addAll(getPrivateDraftsRec(folderInfo.getNodeRef(), skipWorkflows, userName));
        }

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

    /**
     * Returns the requested ClbPropertySheetType in the ClbPropertySheetTypeResponse.
     * 
     * @param aspect Title of property sheet aspect.
     */
    public ClbPropertySheetTypeResponse getPropertySheetType(String aspect) throws LoginException_Exception, ServiceException_Exception
    {
        return getPropertySheetType(aspect, true);
    }

    private ClbPropertySheetTypeResponse getPropertySheetType(String aspect, boolean extractTemplate)
    {
        AspectDefinition aspectDefinition = dictionaryService.getAspect(documentHelper.searchAspect(QuickrModel.ASPECT_QUICKR_PROP_SHEET, aspect));
        ClbPropertySheetType propertySheetType = new ClbPropertySheetType();
        propertySheetType.setId(aspectDefinition.getTitle());
        propertySheetType.setTitle(aspectDefinition.getTitle());

        if (extractTemplate)
        {
            String templateXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><meta:propertySheetTemplate xmlns:meta=\"http://metadata.model.xsd.clb.content.ibm.com/1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" >";
            Map<QName, PropertyDefinition> props = aspectDefinition.getProperties();
            for (PropertyDefinition prop : props.values())
            {
                ClbPropertyType propertyType = AlfrescoQuickrDataTypeHelper.getQuickrPropertyType(prop);

                String propString = "<meta:property xsi:type=\"meta:ClbPropertyType\"";
                propString += " propertyId=\"" + propertyType.getPropertyId() + "\"";
                propString += " dataType=\"" + documentHelper.lowerCaseFirstCharacter(propertyType.getDataType().value()) + "\"";
                propString += " propertyName=\"" + propertyType.getPropertyName() + "\"";
                propString += " multiple=\"" + propertyType.isMultiple() + "\" "
                        + (propertyType.getMaxLength() != null ? "maxLength=\"" + propertyType.getMaxLength() + "\" " : "") + "readonly=\"" + propertyType.isReadOnly() + "\" "
                        + "required=\"" + propertyType.isRequired() + "\" " + "searchable=\"" + propertyType.isSearchable() + "\">";
                for (ClbLabelType label : propertyType.getLabels())
                {
                    propString += "<meta:label label=\"" + label.getLabel() + "\" lang=\"" + label.getLang() + "\"/>";
                }
                for (ClbStyleType style : propertyType.getStyles())
                {
                    propString += "<meta:style name=\"" + style.getName() + "\" value=\"" + style.getValue() + "\"/>";
                }
                for (ClbOptionType option : propertyType.getOptions())
                {
                    propString += "<meta:option>";
                    propString += "<meta:value>" + option.getValue() + "</meta:value>";
                    for (ClbLabelType optionLabel : option.getLabels())
                    {
                        propString += "<meta:label label=\"" + optionLabel.getLabel() + "\" lang=\"" + optionLabel.getLang() + "\"/>";
                    }
                    propString += "</meta:option>";
                }
                for (String defaultValue : propertyType.getDefaultValues())
                {
                    if (defaultValue != null)
                    {
                        propString += "<meta:defaultValue>" + defaultValue + "</meta:defaultValue>";
                    }
                }
                propString += "</meta:property>";
                templateXML += propString;
            }

            String templateEnd = "</meta:propertySheetTemplate>";

            propertySheetType.setTemplateXml(templateXML + templateEnd);
        }
        ClbPropertySheetTypeResponse result = new ClbPropertySheetTypeResponse();
        result.setPropertySheetType(propertySheetType);

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

    /**
     * Retrieve list of documents that the current user has submitted for approval.
     * 
     * @param id If no path is provided, this should be the uuid of the folder to look for drafts under. If both id and path are provided, the id must be the uuid of the parent
     *        folder of the search folder.
     * @param path If no id is provided, the absolute path to the folder to look for drafts under. If both id and path are provided, the path must be the relative path from the id
     *        provided.
     * @return ClbDraftsResponse
     */
    public ClbDraftsResponse getSubmittedDrafts(String id, String path) throws LoginException_Exception, ServiceException_Exception, FileNotFoundException
    {
        final NodeRef rootFolder = pathHelper.resolveNodeRef(id, path);
        ClbDraftsResponse result = new ClbDraftsResponse();

        List<ClbDraft> drafts = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<List<ClbDraft>>()
        {
            public List<ClbDraft> execute() throws LoginException_Exception, ServiceException_Exception
            {
                List<ClbDraft> result = new ArrayList<ClbDraft>();
                String currentUser = AuthenticationUtil.getFullyAuthenticatedUser();
                List<WorkflowDefinition> workflowDefs = workflowService.getAllDefinitions();
                for (WorkflowDefinition workflowDefinition : workflowDefs)
                {
                    List<WorkflowInstance> activeWorkflows = workflowService.getActiveWorkflows(workflowDefinition.getId());
                    for (WorkflowInstance workflowInstance : activeWorkflows)
                    {
                        String workflowInitiator = (String) nodeService.getProperty(workflowInstance.workflowPackage, ContentModel.PROP_CREATOR);
                        if (workflowInitiator.equalsIgnoreCase(currentUser))
                        {
                            List<ChildAssociationRef> drafts = nodeService.getChildAssocs(workflowInstance.workflowPackage);
                            for (ChildAssociationRef draft : drafts)
                            {
                                if (pathHelper.isChild(draft.getChildRef(), rootFolder))
                                {
                                    result.add(createDraft(draft.getChildRef()));
                                }
                            }

                        }
                    }
                }

                return result;

            }
        });

        result.getDrafts().addAll(drafts);
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
     * @throws FileNotFoundException
     */
    public ClbResponse lockDocument(String id, String path) throws LoginException_Exception, ServiceException_Exception, FileNotFoundException
    {
        ClbResponse result = new ClbResponse();
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
     * @throws FileNotFoundException
     */
    public ClbMoveResponse moveDocument(final String srcId, String srcPath, final String destId, String destPath) throws LoginException_Exception, ServiceException_Exception,
            FileNotFoundException
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
     * @throws FileNotFoundException
     */
    public ClbMoveResponse moveFolder(final String srcId, String srcPath, final String destId, String destPath) throws LoginException_Exception, ServiceException_Exception,
            FileNotFoundException
    {
        final ClbMoveResponse result = new ClbMoveResponse();
        final NodeRef source = pathHelper.resolveNodeRef(srcId, srcPath);
        final NodeRef destination = pathHelper.resolveNodeRef(destId, destPath.substring(0, destPath.lastIndexOf("/")));

        transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Object>()
        {
            public Object execute() throws FileExistsException, FileNotFoundException
            {
                moveItem(source, destination, result);
                return null;
            }
        });
        return result;
    }

    private void moveItem(NodeRef srcNodeRef, NodeRef destNodeRef, ClbMoveResponse response) throws FileExistsException, FileNotFoundException
    {
        FileInfo resultFileInfo = fileFolderService.move(srcNodeRef, destNodeRef, null);

        response.setId(resultFileInfo.getNodeRef().getId());
        response.setLastModified(pathHelper.getXmlDate((Date) nodeService.getProperty(resultFileInfo.getNodeRef(), ContentModel.PROP_MODIFIED)));
        response.setPath(pathHelper.getNodePath(resultFileInfo.getNodeRef(), false));
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
     * @throws FileNotFoundException
     */
    public ClbResponse renameDocument(final String id, String path, final String newName) throws LoginException_Exception, ServiceException_Exception, FileNotFoundException
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
     * @throws FileNotFoundException
     */
    public ClbResponse renameFolder(final String id, String path, final String newName) throws LoginException_Exception, ServiceException_Exception, FileNotFoundException
    {
        final ClbResponse result = new ClbResponse();
        final NodeRef nodeRefToRename = pathHelper.resolveNodeRef(id, path);

        transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Object>()
        {
            public Object execute() throws FileExistsException, FileNotFoundException
            {
                renameItem(nodeRefToRename, newName, result);
                return null;
            }
        });

        return result;
    }

    private void renameItem(NodeRef nodeRef, String newName, ClbResponse response) throws FileExistsException, FileNotFoundException
    {
        fileFolderService.rename(nodeRef, newName);
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
     * @throws FileNotFoundException
     */
    public ClbResponse restoreVersion(String id, String path, String version, VersionType versionType) throws LoginException_Exception, ServiceException_Exception,
            FileNotFoundException
    {
        ClbResponse result = new ClbResponse();
        NodeRef documentNodeRef = pathHelper.resolveNodeRef(id, path);
        documentNodeRef = pathHelper.getOriginalDocument(documentNodeRef);
        VersionHistory versionHistory = versionService.getVersionHistory(documentNodeRef);
        versionService.revert(documentNodeRef, versionHistory.getVersion(version), false);

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
     * @throws FileNotFoundException
     */
    @SuppressWarnings("unchecked")
    public ClbResponse updateDraft(final ClbDraft draft, boolean createDocument) throws LoginException_Exception, ServiceException_Exception, FileNotFoundException
    {
        final ClbDocumentResponse result = new ClbDocumentResponse();
        NodeRef documentNodeRef = pathHelper.resolveNodeRef(draft.getId(), draft.getPath());

        final NodeRef finalDocumentNodeRef = pathHelper.getDocumentForWork(documentNodeRef);

        transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Object>()
        {
            public Object execute() throws LoginException_Exception, ServiceException_Exception
            {
                nodeService.setProperty(finalDocumentNodeRef, ContentModel.PROP_TITLE, draft.getTitle());
                nodeService.setProperty(finalDocumentNodeRef, ContentModel.PROP_DESCRIPTION, draft.getDescription());

                setDocType(finalDocumentNodeRef, draft);

                return null;
            }
        });

        result.setDocument(createDocument(finalDocumentNodeRef, Collections.EMPTY_LIST, null));

        return result;
    }

    /**
     * Set provided document type to the node.
     * 
     * @param documentNodeRef target node .
     * @param draft draft element with document type and property sheets
     */
    private void setDocType(NodeRef documentNodeRef, ClbDraft draft)
    {
        if (draft.getDocumentType() == null || draft.getDocumentType().getId() == null || draft.getDocumentType().getId().length() == 0)
        {
            //Document type was not changed.
            return;
        }
        QName newDocType = documentHelper.searchAspect(QuickrModel.ASPECT_QUICKR_DOC_TYPE, draft.getDocumentType().getId());
        Set<QName> aspects = nodeService.getAspects(documentNodeRef);
        for (QName aspect : aspects)
        {
            if (dictionaryService.isSubClass(aspect, QuickrModel.ASPECT_QUICKR_DOC_TYPE) && !aspect.equals(newDocType))
            {
                documentHelper.removeDocType(documentNodeRef, aspect);
                break;
            }
        }

        nodeService.addAspect(documentNodeRef, newDocType, null);

        ClbVersioning versionMode = documentHelper.getVersionMode(documentNodeRef);
        Map<QName, Serializable> versionProperties = new HashMap<QName, Serializable>();
        versionProperties.put(ContentModel.PROP_INITIAL_VERSION, false);

        switch (versionMode)
        {
        case NONE:
        case EXPLICIT:
            versionProperties.put(ContentModel.PROP_AUTO_VERSION, false);
            versionProperties.put(ContentModel.PROP_AUTO_VERSION_PROPS, false);

            break;
        case IMPLICIT:
            versionProperties.put(ContentModel.PROP_AUTO_VERSION, true);
            versionProperties.put(ContentModel.PROP_AUTO_VERSION_PROPS, true);

            break;
        }

        nodeService.addAspect(documentNodeRef, ContentModel.ASPECT_VERSIONABLE, versionProperties);

        for (ClbPropertySheet propSheet : draft.getPropertySheets())
        {
            QName propSheetType = documentHelper.searchAspect(QuickrModel.ASPECT_QUICKR_PROP_SHEET, propSheet.getPropertySheetTypeId());
            if (propSheetType == null)
            {
                continue;
            }
            // TODO avoid code dublication
            for (ClbDynamicDoubleValue dynDouble : propSheet.getDynamicDoubles())
            {
                if (dynDouble.getValues().size() > 0)
                {
                    QName qname = documentHelper.getPropertyQName(propSheetType, dynDouble.getKey());
                    if (qname != null)
                    {
                        nodeService.setProperty(documentNodeRef, qname, dynDouble.getValues().get(0));
                    }
                }
            }
            for (ClbDynamicLongValue dynLong : propSheet.getDynamicLongs())
            {
                if (dynLong.getValues().size() > 0)
                {
                    QName qname = documentHelper.getPropertyQName(propSheetType, dynLong.getKey());
                    if (qname != null)
                    {
                        nodeService.setProperty(documentNodeRef, qname, dynLong.getValues().get(0));
                    }
                }
            }
            for (ClbDynamicBooleanValue dynBoolean : propSheet.getDynamicBooleans())
            {
                if (dynBoolean.getValues().size() > 0)
                {
                    QName qname = documentHelper.getPropertyQName(propSheetType, dynBoolean.getKey());
                    if (qname != null)
                    {
                        nodeService.setProperty(documentNodeRef, qname, dynBoolean.getValues().get(0));
                    }

                }
            }
            for (ClbDynamicStringValue dynString : propSheet.getDynamicStrings())
            {
                if (dynString.getValues().size() > 0)
                {
                    QName qname = documentHelper.getPropertyQName(propSheetType, dynString.getKey());
                    if (qname != null)
                    {
                        nodeService.setProperty(documentNodeRef, qname, dynString.getValues().get(0));
                    }
                }
            }
            for (ClbDynamicDateValue dynDate : propSheet.getDynamicDates())
            {
                if (dynDate.getValues().size() > 0)
                {
                    QName qname = documentHelper.getPropertyQName(propSheetType, dynDate.getKey());
                    if (qname != null)
                    {
                        nodeService.setProperty(documentNodeRef, qname, dynDate.getValues().get(0).toGregorianCalendar().getTime());
                    }
                }
            }
        }
    }

    /**
     * Update corresponding folder in the repository.
     * 
     * @param folder - The id or path attribute of the foler should reference the folder in the repository to be updated. The data supplied in the folder will be used to update the
     *        folder in the repository.
     * @return ClbResponse
     * @throws LoginException_Exception, ServiceException_Exception
     * @throws FileNotFoundException
     */
    public ClbResponse updateFolder(final ClbFolder folder) throws LoginException_Exception, ServiceException_Exception, FileNotFoundException
    {
        final ClbResponse result = new ClbResponse();
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

        return result;
    }

    private ClbFolder createFolder(NodeRef folderRef, List<ClbServiceOption> serviceOptions) throws LoginException_Exception, ServiceException_Exception, FileNotFoundException
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

        folder.setPath(pathHelper.getNodePath(folderRef, false));

        String fullPath = pathHelper.getNodePath(folderRef, true);
        if (fullPath.indexOf("/") != -1)
        {
            folder.setDisplayLocation(fullPath.substring(0, fullPath.lastIndexOf("/")));
        }

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
            NodeRef workingCopyNodeRef = checkOutCheckInService.getCheckedOut(nodeRef);
            boolean isWorkingCopy = workingCopyNodeRef != null;

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
                    nodeRef = workingCopyNodeRef;
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

        data.setPath(pathHelper.getNodePath(nodeRef, false));
        String fullPath = pathHelper.getNodePath(nodeRef, true);
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
        String nodePath = pathHelper.getNodePath(nodeRef, true);
        if (nodePath.indexOf("/") != -1 && nodePath.startsWith(pathHelper.getNodeRefSiteName(nodeRef) + "/documentLibrary"))
        {
            url = MessageFormat.format(pathHelper.getShareDocumentUrl(), pathHelper.getNodeRefSiteName(nodeRef), nodeRef.toString());
        }

        if (serviceOptions.contains(ClbServiceOption.RETRIEVE_DOWNLOAD_URL) || serviceOptions.contains(ClbServiceOption.RETRIEVE_VIEW_URL))
        {
            document.setUrl(url);
        }

        document.setDocumentType(getDocumentType(nodeRef.getId(), null, downloadOption).getDocumentType());

        List<ClbPropertySheet> propSheets = document.getPropertySheets();
        List<ClbPropertySheetType> propSheetTypes = document.getPropertySheetTypes();
        for (ClbPropertySheetType propSheetType : document.getDocumentType().getPropertySheetTypes())
        {
            ClbPropertySheet propSheet = new ClbPropertySheet();
            propSheet.setPropertySheetTypeId(propSheetType.getId());
            propSheet.setDescription(propSheetType.getTitle());
            propSheet.setExtracted(false);
            propSheet.setLabel(propSheetType.getTitle());
            propSheet.setTitle(propSheetType.getTitle());

            QName aspectQname = documentHelper.searchAspect(QuickrModel.ASPECT_QUICKR_PROP_SHEET, propSheetType.getId());
            Map<QName, PropertyDefinition> props = dictionaryService.getAspect(aspectQname).getProperties();
            for (PropertyDefinition prop : props.values())
            {
                AlfrescoQuickrDataTypeHelper.addValue(propSheet, prop, nodeService.getProperty(nodeRef, prop.getName()), prop.getTitle());
            }

            propSheets.add(propSheet);
            propSheetTypes.add(propSheetType);
        }

        if (document.isLocked() && serviceOptions.contains(ClbServiceOption.INCLUDE_DRAFTS))
        {
            ClbDraft draft = createDraft(nodeRef);
            draft.setId("this id is never used");
            document.getDrafts().add(draft);
        }

        return document;
    }

    private ClbDraft createDraft(NodeRef nodeRef) throws LoginException_Exception, ServiceException_Exception
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

        draft.setDocumentType(getDocumentType(nodeRef.getId(), null, null).getDocumentType());
        List<ClbPropertySheet> propSheets = draft.getPropertySheets();
        List<ClbPropertySheetType> propSheetTypes = draft.getPropertySheetTypes();
        for (ClbPropertySheetType propSheetType : draft.getDocumentType().getPropertySheetTypes())
        {
            ClbPropertySheet propSheet = new ClbPropertySheet();
            propSheet.setPropertySheetTypeId(propSheetType.getId());
            propSheet.setDescription(propSheetType.getTitle());
            propSheet.setExtracted(false);
            propSheet.setLabel(propSheetType.getTitle());
            propSheet.setTitle(propSheetType.getTitle());

            QName aspectQname = documentHelper.searchAspect(QuickrModel.ASPECT_QUICKR_PROP_SHEET, propSheetType.getId());
            Map<QName, PropertyDefinition> props = dictionaryService.getAspect(aspectQname).getProperties();
            for (PropertyDefinition prop : props.values())
            {
                AlfrescoQuickrDataTypeHelper.addValue(propSheet, prop, nodeService.getProperty(nodeRef, prop.getName()), prop.getTitle());
            }

            propSheets.add(propSheet);
            propSheetTypes.add(propSheetType);
        }

        if (draft.getDocumentType().isApprovalEnabled())
    {
            draft.setApprovalEnabled(true);
            draft.setApprovalType(draft.getDocumentType().getApprovalType());

            if (nodeService.hasAspect(nodeRef, ContentModel.ASPECT_WORKING_COPY))
            {
                List<WorkflowInstance> activeWorkflows = workflowService.getWorkflowsForContent(nodeRef, true);
                // We suppose only one active workflow for node.
                if (activeWorkflows.size() > 0)
                {
                    draft.setSubmitted(true);

                    WorkflowInstance activeWorkflow = activeWorkflows.get(0);

                    WorkflowTaskQuery filter = new WorkflowTaskQuery();
                    filter.setProcessId(activeWorkflow.id);
                    filter.setOrderBy(new WorkflowTaskQuery.OrderBy[] { WorkflowTaskQuery.OrderBy.TaskName_Asc, WorkflowTaskQuery.OrderBy.TaskState_Asc });
                    filter.setTaskState(WorkflowTaskState.IN_PROGRESS);
                    List<WorkflowTask> inProgressTasks = workflowService.queryTasks(filter);
    
                    filter.setTaskState(WorkflowTaskState.COMPLETED);
                    List<WorkflowTask> completedTasks = workflowService.queryTasks(filter);

                    for (WorkflowTask workflowTask : inProgressTasks)
                    {
                        if (workflowTask.title.equalsIgnoreCase(WORKFLOW_STATUS_REVIEW))
                        {
                            String user = createUserInfo((String) workflowTask.properties.get(ContentModel.PROP_OWNER)).getDn();
                            draft.getApprovers().add(user);
                            draft.getApprovalStates().add(ClbDraftApprovalState.PENDING);
                        }
                    }

                    for (WorkflowTask workflowTask : completedTasks)
                    {
                        if (workflowTask.title.equalsIgnoreCase(WORKFLOW_STATUS_REVIEW))
                        {
                            String outcome = (String) workflowTask.properties.get(WorkflowModel.PROP_OUTCOME);
                            String user = createUserInfo((String) workflowTask.properties.get(ContentModel.PROP_OWNER)).getDn();

                            draft.getApprovers().add(user);
                            if (outcome.equalsIgnoreCase(WORKFLOW_REJECT_ACTION))
                            {
                                draft.getApprovalStates().add(ClbDraftApprovalState.REJECTED);
                            }
                            else
                            {
                                draft.getApprovalStates().add(ClbDraftApprovalState.APPROVED);
                            }
                        }
                    }
                }
            }
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

}
