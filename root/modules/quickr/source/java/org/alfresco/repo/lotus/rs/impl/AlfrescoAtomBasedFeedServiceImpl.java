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
package org.alfresco.repo.lotus.rs.impl;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

import javax.mail.internet.MimeUtility;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.lotus.rs.AtomBasedFeedService;
import org.alfresco.repo.lotus.rs.error.QuickrError;
import org.alfresco.repo.lotus.rs.impl.providers.AtomNodeRefProvider;
import org.alfresco.repo.lotus.ws.impl.helper.AlfrescoQuickrDocumentHelper;
import org.alfresco.repo.lotus.ws.impl.helper.AlfrescoQuickrPathHelper;
import org.alfresco.repo.lotus.ws.impl.helper.DocumentStatus;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionHistory;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.transaction.TransactionService;
import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.parser.stax.FOMFactory;
import org.apache.abdera.parser.stax.FOMService;
import org.apache.abdera.protocol.error.Error;

/**
 * @author PavelYur
 */
public class AlfrescoAtomBasedFeedServiceImpl implements AtomBasedFeedService
{
    private NodeService nodeService;

    private PersonService personService;

    private FileFolderService fileFolderService;

    private AlfrescoQuickrPathHelper pathHelper;

    private AlfrescoQuickrDocumentHelper documentHelper;

    private TransactionService transactionService;

    private ContentService contentService;

    private VersionService versionService;

    private CheckOutCheckInService checkOutCheckInService;
    
    private String generatorVersion;

    public void setDocumentHelper(AlfrescoQuickrDocumentHelper documentHelper)
    {
        this.documentHelper = documentHelper;
    }

    public void setCheckOutCheckInService(CheckOutCheckInService checkOutCheckInService)
    {
        this.checkOutCheckInService = checkOutCheckInService;
    }

    public void setVersionService(VersionService versionService)
    {
        this.versionService = versionService;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setPersonService(PersonService personService)
    {
        this.personService = personService;
    }

    public void setFileFolderService(FileFolderService fileFolderService)
    {
        this.fileFolderService = fileFolderService;
    }

    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    public void setContentService(ContentService contentService)
    {
        this.contentService = contentService;
    }

    public void setPathHelper(AlfrescoQuickrPathHelper pathHelper)
    {
        this.pathHelper = pathHelper;
    }
    
    public void setGeneratorVersion(String generatorVersion)
    {
        this.generatorVersion = generatorVersion;
    }

    /**
     * Create document in library.
     * 
     * @param id Id of library.
     * @param headers {@link HttpHeaders}.
     * @param body bytes to writen.
     * @return Response {@link Response}.
     */
    public Response createDocumentInLibrary(String id, final HttpHeaders headres, final byte[] body, boolean submit, final boolean replace)
    {
        return createDocumentInFolder(null, id, headres, body, submit, replace);
    }

    /**
     * Create document in folder.
     * 
     * @param id Id of library.
     * @param headers HttpHeaders.
     * @param body bytes to writen.
     * @return Response {@link Response}.
     */
    public Response createDocumentInFolder(String libraryId, String folderId, final HttpHeaders headres, final byte[] body, final boolean submit, final boolean replace)
    {
        final NodeRef parentNodeRef = new NodeRef(pathHelper.getLibraryStoreRef(), folderId);
        FileInfo resultFileInfo = null;
        try
        {
            resultFileInfo = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<FileInfo>()
            {
                public FileInfo execute() throws UnsupportedEncodingException
                {
                    String filePath = MimeUtility.decodeText(getHeaderValue(headres, "slug"));

                    if (filePath.startsWith("/"))
                    {
                        filePath = filePath.substring(1);
                    }
                    String[] path = filePath.split("/");
                    NodeRef parent = parentNodeRef;
                    for (int i = 0; i < path.length - 1; i++)
                    {
                        parent = fileFolderService.searchSimple(parent, path[i]);
                    }
                    FileInfo resultFileInfo = null;
                    if (getHeaderValue(headres, "x-method-override") == null)
                    {
                        if (replace)
                        {
                            NodeRef draftDocument = fileFolderService.searchSimple(parent, path[path.length - 1]);

                            if (draftDocument != null)
                            {
                                draftDocument = pathHelper.getDocumentForWork(draftDocument);
                                if (nodeService.hasAspect(draftDocument, ContentModel.ASPECT_WORKING_COPY))
                                {
                                    draftDocument = checkOutCheckInService.cancelCheckout(draftDocument);
                                }
                                fileFolderService.delete(draftDocument);
                            }
                        }
                        resultFileInfo = fileFolderService.create(parent, path[path.length - 1], ContentModel.TYPE_CONTENT);
                    }
                    else
                    {
                        resultFileInfo = fileFolderService.getFileInfo(fileFolderService.searchSimple(parent, path[path.length - 1]));

                        // If document is checked out, then we use working copy
                        resultFileInfo = fileFolderService.getFileInfo(pathHelper.getDocumentForWork(resultFileInfo.getNodeRef()));
                    }
                    ContentWriter writer = contentService.getWriter(resultFileInfo.getNodeRef(), ContentModel.PROP_CONTENT, true);
                    writer.putContent(new ByteArrayInputStream(body));

                    if (getHeaderValue(headres, "x-method-override") == null && submit == false && !pathHelper.isInRmSite(resultFileInfo.getNodeRef()))
                    {
                        resultFileInfo = fileFolderService.getFileInfo(checkOutCheckInService.checkout(resultFileInfo.getNodeRef()));
                    }
                    if (submit && !pathHelper.isInRmSite(resultFileInfo.getNodeRef()))
                    {
                        NodeRef workingCopy = null;
                        if (nodeService.hasAspect(resultFileInfo.getNodeRef(), ContentModel.ASPECT_WORKING_COPY))
                        {
                            workingCopy = resultFileInfo.getNodeRef();
                        }
                        else
                        {
                            final DocumentStatus documentStatus = documentHelper.getDocumentStatus(resultFileInfo.getNodeRef());
                            if (AlfrescoQuickrDocumentHelper.isCheckoutOwner(documentStatus))
                            {
                                workingCopy = pathHelper.getDocumentForWork(resultFileInfo.getNodeRef());
                            }
                        }

                        if (workingCopy != null)
                        {
                            resultFileInfo = fileFolderService.getFileInfo(checkOutCheckInService.checkin(workingCopy, new HashMap<String, Serializable>()));
                        }
                    }

                    return resultFileInfo;
                }
            });
        }
        catch (FileExistsException e)
        {
            return createFaultResponse(QuickrError.ITEM_EXISTS, e.getLocalizedMessage());
        }

        Entry entry = createEntry(resultFileInfo.getNodeRef().getId());

        try
        {
            return Response.created(entry.getLink("self").getHref().toURI()).entity(entry).build();
        }
        catch (URISyntaxException e)
        {
            // ignore
        }
        return null;
    }

    public Response initialRetrieveListOfLibraries()
    {
        return Response.ok().build();
    }

    /**
     * Retrieve list of libraries.
     */
    public Feed retrieveListOfLibraries()
    {
        NodeRef libraryNodeRef = pathHelper.getRootNodeRef();
        String libraryName = (String) nodeService.getProperty(libraryNodeRef, ContentModel.PROP_NAME);

        Factory factory = new FOMFactory();

        Feed feed = factory.newFeed();
        feed.setId("urn:lsid:ibm.com:td:libraries");
        feed.addLink(pathHelper.getLotusUrl() + "/dm/atom/libraries/feed", "self");
        feed.setUpdated((Date) nodeService.getProperty(libraryNodeRef, ContentModel.PROP_MODIFIED));
        feed.setGenerator("", generatorVersion, "Teamspace Documents");
        
        Entry entry = factory.newEntry();
        entry.setId("urn:lsid:ibm.com:td:" + libraryNodeRef.getId());
        entry.addLink(pathHelper.getLotusUrl() + "/dm/atom/library/" + libraryNodeRef.getId() + "/entry", "self");

        entry.addCategory("tag:ibm.com,2006:td/type", "library", "library");

        String authorName = (String) nodeService.getProperty(libraryNodeRef, ContentModel.PROP_CREATOR);
        String email = (String) nodeService.getProperty(personService.getPerson(authorName), ContentModel.PROP_EMAIL);
        entry.addAuthor(authorName, email, "");

        entry.setTitle(libraryName);
        entry.setPublished((Date) nodeService.getProperty(libraryNodeRef, ContentModel.PROP_CREATED));
        entry.setUpdated((Date) nodeService.getProperty(libraryNodeRef, ContentModel.PROP_MODIFIED));
        entry.setSummary((String) nodeService.getProperty(libraryNodeRef, ContentModel.PROP_DESCRIPTION));
        entry.setContent(new IRI(pathHelper.getLotusUrl() + "/dm/atom/library/" + libraryNodeRef.getId() + "/feed"), "application/atom+xml");

        feed.addEntry(entry);

        return feed;
    }

    /**
     * Retrieve list of content in library.
     * 
     * @param id library id.
     * @return Feed {@link Feed}.
     */
    public Feed retrieveListOfContentInLibrary(String id)
    {
        return retrieveListOfContentInFolder(null, id);
    }

    /**
     * Retrieve list of content in folder.
     * 
     * @param libraryId id of library .
     * @param folderId id of folder.
     * @return Feed {@link Feed}.
     */
    public Feed retrieveListOfContentInFolder(String libraryId, String folderId)
    {
        NodeRef folderRef = new NodeRef(pathHelper.getLibraryStoreRef(), folderId);

        String contentName = (String) nodeService.getProperty(folderRef, ContentModel.PROP_NAME);

        Factory factory = new FOMFactory();

        Feed feed = factory.newFeed();
        feed.setId("urn:lsid:ibm.com:td:" + folderId);

        feed.setBaseUri(pathHelper.getLotusUrl() + "/dm/atom/library/" + pathHelper.getRootNodeRef().getId() + "/");

        if (pathHelper.getRootNodeRef().getId().equals(folderId))
        {
            feed.addLink("feed", "self");
        }
        else
        {
            feed.addLink("folder/" + folderId + "/feed", "self");
        }

        String authorName = (String) nodeService.getProperty(folderRef, ContentModel.PROP_CREATOR);
        String email = (String) nodeService.getProperty(personService.getPerson(authorName), ContentModel.PROP_EMAIL);
        feed.addAuthor(authorName, email, "");

        feed.setTitle(contentName);
        feed.setUpdated((Date) nodeService.getProperty(folderRef, ContentModel.PROP_MODIFIED));
        feed.setGenerator("", generatorVersion, "Teamspace Documents");
        
        for (FileInfo fileInfo : fileFolderService.list(folderRef))
        {
            if (!fileInfo.isLink() && !nodeService.hasAspect(fileInfo.getNodeRef(), ContentModel.ASPECT_WORKING_COPY))
            {
                feed.addEntry(createEntry(fileInfo.getNodeRef().getId()));
            }
        }

        return feed;
    }

    /**
     * Retrieve document.
     * 
     * @param libraryId id of library .
     * @param documentId id of document.
     * @return Entry {@link Entry}.
     */
    public Entry retrieveDocument(String libraryId, String documentId)
    {
        Entry entry = createEntry(documentId);
        entry.setBaseUri(pathHelper.getLotusUrl() + "/dm/atom/library/" + pathHelper.getRootNodeRef().getId() + "/");
        return entry;
    }

    /**
     * Retrieve fodler.
     * 
     * @param libraryId id of library .
     * @param folderId id of folder.
     * @return Entry {@link Entry}.
     */
    public Entry retrieveFolder(String libraryId, String folderId)
    {
        Entry entry = createEntry(folderId);
        entry.setBaseUri(pathHelper.getLotusUrl() + "/dm/atom/library/" + pathHelper.getRootNodeRef().getId() + "/");
        return entry;
    }

    private Entry createEntry(String id)
    {
        NodeRef nodeRef = new NodeRef(pathHelper.getLibraryStoreRef(), id);

        String nodeName = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);

        Factory factory = new FOMFactory();

        Entry entry = factory.newEntry();

        entry.setId("urn:lsid:ibm.com:td:" + id);

        if (fileFolderService.getFileInfo(nodeRef).isFolder())
        {
            entry.addLink("folder/" + nodeRef.getId() + "/entry", "self");
            entry.addLink("folder/" + nodeRef.getId() + "/entry", "edit");
            entry.addCategory("tag:ibm.com,2006:td/type", "folder", "folder");
        }
        else
        {
            entry.addLink("document/" + nodeRef.getId() + "/entry", "self");
            entry.addLink("document/" + nodeRef.getId() + "/entry", "edit");
            entry.addLink("document/" + nodeRef.getId() + "/media", "edit-media");

            if (pathHelper.getNodePath(nodeRef).indexOf("/") != -1)
            {
                entry.addLink(MessageFormat.format(pathHelper.getShareDocumentUrl(), pathHelper.getNodeRefSiteName(nodeRef), nodeRef.toString()));
            }
            else
            {
                entry.addLink("");
            }

            entry.addCategory("tag:ibm.com,2006:td/type", "document", "document");
        }

        String authorName = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_CREATOR);
        String email = (String) nodeService.getProperty(personService.getPerson(authorName), ContentModel.PROP_EMAIL);
        entry.addAuthor(authorName, email, "");

        entry.setTitle(nodeName);
        entry.setPublished((Date) nodeService.getProperty(nodeRef, ContentModel.PROP_CREATED));
        entry.setUpdated((Date) nodeService.getProperty(nodeRef, ContentModel.PROP_MODIFIED));
        entry.setSummary((String) nodeService.getProperty(nodeRef, ContentModel.PROP_DESCRIPTION));

        if (fileFolderService.getFileInfo(nodeRef).isFolder())
        {
            entry.setContent(new IRI("folder/" + nodeRef.getId() + "/feed"), "application/atom+xml");
        }
        else
        {
            String mimetype = "text/plain";

            ContentData contentData = (ContentData) nodeService.getProperty(nodeRef, ContentModel.PROP_CONTENT);
            if (contentData != null)
            {
                mimetype = contentData.getMimetype();
            }
            entry.setContent(new IRI("document/" + nodeRef.getId() + "/media"), mimetype);
        }

        return entry;
    }

    /**
     * Return list of libraries.
     * 
     * @return Service {@link Service}.
     */
    public Service getLibraries()
    {
        Service service = new FOMService();

        Workspace workspace = service.addWorkspace(pathHelper.getLibraryStoreRef().toString());

        NodeRef rootNodeRef = pathHelper.getRootNodeRef();

        String rootNodeName = (String) nodeService.getProperty(rootNodeRef, ContentModel.PROP_NAME);
        workspace.addCollection(rootNodeName, pathHelper.getLotusUrl() + "/dm/atom/library/" + rootNodeRef.getId() + "/feed");
        workspace.getCollections().get(0).addAcceptsEntry().addAccepts("*/*");

        return service;
    }

    /**
     * Delete document.
     * 
     * @param libraryId id of library .
     * @param documentId id of document.
     * @param headres {@link HttpHeaders}
     * @return Response {@link Response}.
     */
    public Response deleteDocument(String libraryId, String documentId, HttpHeaders headres)
    {
        if ("delete".equalsIgnoreCase(getHeaderValue(headres, "x-method-override")))
        {
            try
            {
                deleteItem(documentId);
            }
            catch (InvalidNodeRefException e)
            {
                createFaultResponse(QuickrError.ITEM_NOT_FOUND, e.getLocalizedMessage());
            }
        }
        return Response.ok().build();
    }

    /**
     * Delete folder.
     * 
     * @param libraryId id of library .
     * @param foldertId id of folder.
     * @param headres {@link HttpHeaders}
     * @return Response {@link Response}.
     */
    public Response deleteFolder(String libraryId, String folderId, HttpHeaders headres)
    {
        if ("delete".equalsIgnoreCase(getHeaderValue(headres, "x-method-override")))
        {
            try
            {
                deleteItem(folderId);
            }
            catch (InvalidNodeRefException e)
            {
                createFaultResponse(QuickrError.ITEM_NOT_FOUND, e.getLocalizedMessage());
            }
        }
        return Response.ok().build();
    }

    /**
     * Download folder.
     * 
     * @param libraryId id of library .
     * @param documentId id of document.
     * @param headres {@link HttpHeaders}
     * @return Response {@link Response}.
     */
    public Response downloadDocument(String libraryId, String documentId, HttpHeaders headers, boolean lock)
    {
        NodeRef documentNodeRef = new NodeRef(pathHelper.getLibraryStoreRef(), documentId);

        if (!nodeService.exists(documentNodeRef))
        {
            // http status should be 404
            return createFaultResponse(QuickrError.ITEM_NOT_FOUND, "Document doesn't exists.");
        }

        // check http headers
        String ifNoneMatch = getHeaderValue(headers, HttpHeaders.IF_NONE_MATCH);
        String ifModifiedSince = getHeaderValue(headers, HttpHeaders.IF_MODIFIED_SINCE);

        Date lastModified = (Date) nodeService.getProperty(documentNodeRef, ContentModel.PROP_MODIFIED);

        if (ifNoneMatch != null)
        {
            String originalEtag = "\"" + documentNodeRef.getId() + ":" + Long.toString(lastModified.getTime()) + "\"";
            String clientCacheEtag = ifNoneMatch;

            if (clientCacheEtag.equalsIgnoreCase(originalEtag))
            {
                return Response.status(Status.NOT_MODIFIED).build();
            }
        }

        if (ifModifiedSince != null)
        {
            try
            {
                Date clientCacheLastModified = AtomNodeRefProvider.format.parse(ifModifiedSince);
                if (Math.abs(clientCacheLastModified.getTime() - lastModified.getTime()) <= 999)
                {
                    return Response.status(Status.NOT_MODIFIED).build();
                }
            }
            catch (ParseException e)
            {
                // nothing to do
            }
        }

        if (lock)
        {
            documentNodeRef = checkOutCheckInService.checkout(documentNodeRef);
        }

        return Response.ok(documentNodeRef).build();
    }

    /**
     * Download draft.
     * 
     * @param libraryId id of library .
     * @param draftId id of draft.
     * @return Response {@link Response}.
     */
    public Response downloadDraft(String libraryId, String draftId, HttpHeaders headers)
    {
        NodeRef documentNodeRef = new NodeRef(pathHelper.getLibraryStoreRef(), draftId);
        documentNodeRef = pathHelper.getDocumentForWork(documentNodeRef);

        return downloadDocument(libraryId, documentNodeRef.getId(), headers, false);
    }

    /**
     * Download document version.
     * 
     * @param libraryId id of library .
     * @param documentId id of document.
     * @param versionId id of document version.
     * @param headres {@link HttpHeaders}
     * @return Response {@link Response}.
     */
    public Response downloadDocumentVersion(String libraryId, String documentId, String versionId, HttpHeaders headers)
    {
        NodeRef documentNodeRef = new NodeRef(pathHelper.getLibraryStoreRef(), documentId);
        if (!nodeService.exists(documentNodeRef))
        {
            // http status should be 404
            return createFaultResponse(QuickrError.ITEM_NOT_FOUND, "Document doesn't exists.");
        }

        VersionHistory versionHistory = versionService.getVersionHistory(documentNodeRef);
        Version version = versionHistory.getVersion(versionId);

        return Response.ok(version.getFrozenStateNodeRef()).build();
    }

    private void deleteItem(String itemId)
    {
        final NodeRef nodeRef = new NodeRef(pathHelper.getLibraryStoreRef(), itemId);

        transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Object>()
        {
            public Object execute()
            {
                fileFolderService.delete(nodeRef);
                return null;
            }
        });
    }

    private Response createFaultResponse(QuickrError error, String message)
    {
        return Response.status(error.getResponseStatus()).entity(Error.create(new Abdera(), error.getErrorCode(), message)).build();
    }

    private String getHeaderValue(HttpHeaders headres, String name)
    {
        for (String key : headres.getRequestHeaders().keySet())
        {
            if (key.equalsIgnoreCase(name))
            {
                return headres.getRequestHeaders().get(key).get(0);
            }
        }

        return null;
    }
}
