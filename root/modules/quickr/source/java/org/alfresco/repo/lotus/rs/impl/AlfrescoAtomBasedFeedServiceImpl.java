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

package org.alfresco.repo.lotus.rs.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.internet.MimeUtility;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.model.ForumModel;
import org.alfresco.model.QuickrModel;
import org.alfresco.repo.lotus.rs.AtomBasedFeedService;
import org.alfresco.repo.lotus.rs.impl.providers.AtomNodeRefProvider;
import org.alfresco.repo.lotus.ws.ClbDataType;
import org.alfresco.repo.lotus.ws.ClbLabelType;
import org.alfresco.repo.lotus.ws.ClbOptionType;
import org.alfresco.repo.lotus.ws.ClbPropertyType;
import org.alfresco.repo.lotus.ws.ClbStyleType;
import org.alfresco.repo.lotus.ws.ClbVersioning;
import org.alfresco.repo.lotus.ws.impl.auth.LtpaAuthenticator;
import org.alfresco.repo.lotus.ws.impl.helper.AlfrescoQuickrDataTypeHelper;
import org.alfresco.repo.lotus.ws.impl.helper.AlfrescoQuickrDocumentHelper;
import org.alfresco.repo.lotus.ws.impl.helper.AlfrescoQuickrPathHelper;
import org.alfresco.repo.lotus.ws.impl.helper.AlfrescoQuickrPermissionHelper;
import org.alfresco.repo.lotus.ws.impl.helper.AlfrescoQuickrWorkflowHelper;
import org.alfresco.repo.lotus.ws.impl.helper.DocumentStatus;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.repo.version.Version2Model;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionHistory;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.model.Text.Type;
import org.apache.abdera.parser.stax.FOMFactory;
import org.apache.abdera.parser.stax.FOMService;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author PavelYur
 */
public class AlfrescoAtomBasedFeedServiceImpl implements AtomBasedFeedService
{
    // Encoding for multipart message used during uploading files.
    private final static String MULTIPART_ENCODING = "ISO-8859-1";

    private final static String VIEW_ALL_DOCS = "AllDocuments";
    private final static String VIEW_MY_CHECKED_OUT = "CheckedOut";

    private final static Map<String, QName> sortField;

    static
    {
        sortField = new HashMap<String, QName>();
        sortField.put("label", ContentModel.PROP_NAME);
        sortField.put("title", ContentModel.PROP_NAME);
        sortField.put("modified", ContentModel.PROP_MODIFIED);
    }

    private final static String SORT_ORDER_ASC = "asc";

    private final static String CAT_VERSION = "version";
    private final static String CAT_COMMENT = "comment";
    private final static String CAT_DOCUMENT = "document";
    private final static String CAT_FOLDER = "folder";

    private final static String PART_TEXT = "\"file\"";
    private final static String PART_DESCRIPTION = "\"description\"";
    private final static String PART_NAME = "\"label\"";
    private final static String PART_TITLE = "\"title\"";

    // DocType Const
    private final static String PART_PROPS_COUNT = "\"snxPstfCount\""; // snxPstfCount=7
    private final static String PART_PROP_SHEET_BASE = "snxPstId_";// snxPstId_0=SupportedTypes
    private final static String PART_PROP_TITLE_BASE = "snxFid_"; // snxFid_0=long
    private final static String PART_PROP_VALUE_BASE = "snxValue_";// snxValue_0_0=123

    private final static String TOPIC_TITLE = "Topic for ''{0}'' document";
    private final static QName ASPECT_SYNDICATION = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "syndication");
    private final static QName PROP_PUBLISHED = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "published");
    private Feed docTypesFeed = null;

    private NodeService nodeService;

    private PersonService personService;

    private FileFolderService fileFolderService;

    private DictionaryService dictionaryService;

    private AlfrescoQuickrPathHelper pathHelper;

    private AlfrescoQuickrDocumentHelper documentHelper;

    private AlfrescoQuickrWorkflowHelper workflowHelper;

    private TransactionService transactionService;

    private ContentService contentService;

    private VersionService versionService;

    private CheckOutCheckInService checkOutCheckInService;
    
    private SiteService siteService;

    private AlfrescoQuickrPermissionHelper permissionHelper;

    private String generatorVersion;

    private String generatorName = "";

    public void setGeneratorName(String generatorName)
    {
        this.generatorName = generatorName;
    }

    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    public void setDocumentHelper(AlfrescoQuickrDocumentHelper documentHelper)
    {
        this.documentHelper = documentHelper;
    }

    public void setWorkflowHelper(AlfrescoQuickrWorkflowHelper workflowHelper)
    {
        this.workflowHelper = workflowHelper;
    }

    public void setCheckOutCheckInService(CheckOutCheckInService checkOutCheckInService)
    {
        this.checkOutCheckInService = checkOutCheckInService;
    }

    public void setSiteService(SiteService siteService)
    {
        this.siteService = siteService;
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

    public void setPermissionHelper(AlfrescoQuickrPermissionHelper permissionHelper)
    {
        this.permissionHelper = permissionHelper;
    }

    /**
     * Create item in library.
     * 
     * @param id Id of library.
     * @param headers {@link HttpHeaders}.
     * @param body bytes to writen.
     * @return Response {@link Response}.
     * @throws URISyntaxException
     */
    public Response createItemInLibrary(String id, final HttpHeaders headres, final byte[] body, boolean submit, boolean isDraft, final boolean replace,
            boolean retrievePermissions, String opId, String docTypeTitle, String format) throws URISyntaxException
    {
        return createItemInFolder(null, id, headres, body, submit, isDraft, replace, retrievePermissions, opId, docTypeTitle, format);
    }

    /**
     * Process provided multipart part. Extract key and value.
     * 
     * @param part part to process
     * @param result result map.
     */
    private void processPart(String part, Map<String, String> result)
    {
        if (part.length() == 0)
        {
            return;
        }

        int delimIndex = part.indexOf("\r\n\r\n");
        if (delimIndex < 0)
        {
            return;
        }
        String header = part.substring(0, delimIndex);
        String value = part.substring(delimIndex + "\r\n\r\n".length());

        String[] attrs = header.split("; ");
        for (String attr : attrs)
        {
            if (attr.startsWith("name="))
            {
                String key = attr.substring("name=".length());
                result.put(key, value.substring(0, value.length() - 2));// remove last \r\n
            }
        }
    }

    /**
     * Create item in folder.
     * 
     * @param id Id of library.
     * @param headers HttpHeaders.
     * @param body bytes to writen.
     * @return Response {@link Response}.
     * @throws URISyntaxException
     */
    public Response createItemInFolder(String libraryId, String folderId, final HttpHeaders headres, final byte[] body, final boolean submit, final boolean isDraft,
            final boolean replace, boolean retrievePermissions, final String opId, final String docTypeTitle, String format) throws URISyntaxException
    {
        final NodeRef parentNodeRef = new NodeRef(pathHelper.getLibraryStoreRef(), folderId);
        FileInfo resultFileInfo = null;
        resultFileInfo = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<FileInfo>()
        {
            public FileInfo execute() throws UnsupportedEncodingException
            {
                String filePath = "";
                if (getHeaderValue(headres, "slug") != null)
                {
                    // Create usual file
                    filePath = MimeUtility.decodeText(getHeaderValue(headres, "slug"));

                    if (filePath.startsWith("/"))
                    {
                        filePath = filePath.substring(1);
                    }
                }
                else if (opId.equals(""))
                {
                    // Create folder.
                    Document doc = documentHelper.getXMLDocument(body);
                    String name = documentHelper.getNodeValue(doc, "title");
                    String desc = documentHelper.getNodeValue(doc, "summary");

                    FileInfo folder = fileFolderService.create(parentNodeRef, name, ContentModel.TYPE_FOLDER);
                    nodeService.setProperty(folder.getNodeRef(), ContentModel.PROP_DESCRIPTION, desc);
                    return folder;

                }
                else
                {
                    // TODO it's very rube implementation
                    // Crete File from Custom Library
                    String conType = getHeaderValue(headres, "Content-Type");
                    String boundary = "--" + conType.substring(conType.indexOf("boundary") + 9);
                    String[] parts = new String(body, MULTIPART_ENCODING).split(boundary);

                    Map<String, String> result = new HashMap<String, String>();
                    for (String part : parts)
                    {
                        processPart(part, result);
                    }

                    final FileInfo file = fileFolderService.create(parentNodeRef, result.get(PART_NAME), ContentModel.TYPE_CONTENT);
                    nodeService.setProperty(file.getNodeRef(), ContentModel.PROP_TITLE, result.get(PART_TITLE));
                    nodeService.setProperty(file.getNodeRef(), ContentModel.PROP_DESCRIPTION, result.get(PART_DESCRIPTION));

                    // Set docType
                    org.alfresco.service.namespace.QName docTypeQname = documentHelper.searchAspect(QuickrModel.ASPECT_QUICKR_DOC_TYPE, docTypeTitle);
                    nodeService.addAspect(file.getNodeRef(), docTypeQname, null);
                    if (result.get(PART_PROPS_COUNT) != null)
                    {
                        int propCount = Integer.valueOf(result.get(PART_PROPS_COUNT));
                        Map<String, Map<String, String>> aspectWithProps = new HashMap<String, Map<String, String>>();
                        for (int i = 0; i < propCount; i++)
                        {

                            String propSheetTitle = result.get("\"" + PART_PROP_SHEET_BASE + i + "\"");
                            String propTitle = result.get("\"" + PART_PROP_TITLE_BASE + i + "\"");
                            String propValue = result.get("\"" + PART_PROP_VALUE_BASE + i + "_0" + "\"");

                            if (!aspectWithProps.containsKey(propSheetTitle))
                            {
                                aspectWithProps.put(propSheetTitle, new HashMap<String, String>());
                            }

                            Map<String, String> propMap = aspectWithProps.get(propSheetTitle);
                            propMap.put(propTitle, propValue);
                        }
                        for (String aspectTitle : aspectWithProps.keySet())
                        {
                            final org.alfresco.service.namespace.QName propSheetQname = documentHelper.searchAspect(QuickrModel.ASPECT_QUICKR_PROP_SHEET, aspectTitle);
                            final Map<String, String> properties = aspectWithProps.get(aspectTitle);

                            transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Object>()
                            {
                                public Object execute()
                            {
                                    setPropSheetWithProps(file.getNodeRef(), propSheetQname, properties);
                                    return null;
                                }
                            });
                        }
                    }

                    ContentWriter writer = contentService.getWriter(file.getNodeRef(), ContentModel.PROP_CONTENT, true);
                    InputStream contentStream = new ByteArrayInputStream(result.get(PART_TEXT).getBytes(MULTIPART_ENCODING));
                    writer.putContent(contentStream);

                    if (isDraft == true && !pathHelper.isInRmSite(file.getNodeRef()))
                    {
                        nodeService.addAspect(file.getNodeRef(), QuickrModel.ASPECT_QUICKR_INITIAL_DRAFT, null);
                        return fileFolderService.getFileInfo(checkOutCheckInService.checkout(file.getNodeRef()));
                    }

                    return file;
                }

                String[] path = filePath.split("/");
                NodeRef parent = parentNodeRef;
                for (int i = 0; i < path.length - 1; i++)
                {
                    parent = fileFolderService.searchSimple(parent, path[i]);
                }
                FileInfo resultFileInfo = null;

                String fileName = path[path.length - 1];
                NodeRef fileRef = fileFolderService.searchSimple(parent, fileName);
                if (fileRef == null)
                {
                    resultFileInfo = fileFolderService.create(parent, fileName, ContentModel.TYPE_CONTENT);
                }
                else
                {
                    resultFileInfo = fileFolderService.getFileInfo(fileRef);
                }

                // If document is checked out, then we use working copy
                resultFileInfo = fileFolderService.getFileInfo(pathHelper.getDocumentForWork(resultFileInfo.getNodeRef()));

                ContentWriter writer = contentService.getWriter(resultFileInfo.getNodeRef(), ContentModel.PROP_CONTENT, true);
                writer.putContent(new ByteArrayInputStream(body));

                if (getHeaderValue(headres, "x-method-override") == null && submit == false && !pathHelper.isInRmSite(resultFileInfo.getNodeRef()))
                {
                    if (!nodeService.hasAspect(resultFileInfo.getNodeRef(), ContentModel.ASPECT_WORKING_COPY))
                    {
                        resultFileInfo = fileFolderService.getFileInfo(checkOutCheckInService.checkout(resultFileInfo.getNodeRef()));
                    }
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

        Entry entry = createEntry(resultFileInfo.getNodeRef().getId(), retrievePermissions, null, false);

        ResponseBuilder resp = Response.created(entry.getLink("self").getHref().toURI()).entity(entry);
        if ("html".equals(format))
        {
            return resp.type(MediaType.TEXT_HTML_TYPE).build();
        }

        return resp.type(MediaType.APPLICATION_ATOM_XML_TYPE).build();
    }

    /**
     * @param nodeRef document NodeRef
     * @param propSheetQname PropertySheet to set
     * @param properties Properties of specified PropertySheet
     */
    private void setPropSheetWithProps(NodeRef nodeRef, org.alfresco.service.namespace.QName propSheetQname, Map<String, String> properties)
    {
        for (String propTitle : properties.keySet())
        {
            org.alfresco.service.namespace.QName propQname = documentHelper.getPropertyQName(propSheetQname, propTitle);
            PropertyDefinition prop = dictionaryService.getProperty(propQname);

            String type = AlfrescoQuickrDataTypeHelper.getQuickrPropertyType(prop).getDataType().value();
            String propValue = properties.get(propTitle);
            if (propValue != null && propValue.length() > 0)
            {
                if (ClbDataType.DATE_TIME.value().equals(type))
                {
                    try
                    {

                        XMLGregorianCalendar cal = DatatypeFactory.newInstance().newXMLGregorianCalendar(properties.get(propTitle));
                        nodeService.setProperty(nodeRef, propQname, cal.toGregorianCalendar().getTime());
                    }
                    catch (DatatypeConfigurationException e)
                    {
                    }
                }
                else
                {
                    Serializable alfrescoValue = (Serializable) DefaultTypeConverter.INSTANCE.convert(prop.getDataType(), propValue);
                    nodeService.setProperty(nodeRef, propQname, alfrescoValue);
                }
            }
        }
    }

    public Response initialRetrieveListOfLibraries()
    {
        return Response.ok().build();
    }

    /**
     * Retrieve Library
     */
    public Response retrieveLibrary(String id)
    {
        Feed feed = retrieveListOfLibrariesFeed();
        return Response.ok(feed.getEntries().get(0)).build();
    }

    public Response retrieveListOfLibraries()
    {
        Feed feed = retrieveListOfLibrariesFeed();
        return Response.ok(feed).build();
    }

    /**
     * Retrieve list of libraries.
     */
    private Feed retrieveListOfLibrariesFeed()
    {
        NodeRef libraryNodeRef = pathHelper.getRootNodeRef();
        String libraryName = (String) nodeService.getProperty(libraryNodeRef, ContentModel.PROP_NAME);

        Factory factory = new FOMFactory();

        Feed feed = factory.newFeed();
        feed.setId("urn:lsid:ibm.com:td:libraries");
        feed.addLink(pathHelper.getLotusUrl() + "/dm/atom/libraries/feed", "self");
        feed.setUpdated((Date) nodeService.getProperty(libraryNodeRef, ContentModel.PROP_MODIFIED));
        feed.setGenerator("", generatorVersion, generatorName);
        
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
    public Response retrieveListOfContentInLibrary(String id, boolean retrievePermissions, String category, String sortOrder, String sortColumn, int startPage, int pageSize,
            String slotid, String piid)
    {
        return retrieveListOfContentInFolder(null, id, retrievePermissions, null, category, sortOrder, sortColumn, startPage, pageSize, slotid, piid);
    }

    /**
     * Retrieve list of content in folder.
     * 
     * @param libraryId id of library .
     * @param folderId id of folder.
     * @return Feed {@link Feed}.
     */
    public Response retrieveListOfContentInFolder(String libraryId, String folderId, boolean retrievePermissions, String cookieHeader, String category, String sortOrder,
            String sortColumn, int startPage, int pageSize, String slotid, String piid)
    {
        boolean retrFolders = category.contains(CAT_FOLDER);
        boolean retrFiles = category.contains(CAT_DOCUMENT);

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
        
        feed.setGenerator("", generatorVersion, generatorName);

        boolean generateNextLink = false;
        boolean generatePrevLink = startPage > 1;
        boolean sortAsc = sortOrder.equals(SORT_ORDER_ASC);
        QName sortPropery = sortField.get(sortColumn);

        if (retrFolders)
        {
            List<FileInfo> rawFolderList = fileFolderService.listFolders(folderRef);
            List<FileInfo> folderList = new ArrayList<FileInfo>(rawFolderList.size());
            for (FileInfo folderInfo : rawFolderList)
            {
                if (!folderInfo.isLink())
                {
                    folderList.add(folderInfo);
                }
            }

            Collections.sort(folderList, new PropertyComparator(sortPropery, sortAsc));
            int startIndex = pageSize * (startPage - 1);
            for (int count = 0; startIndex < folderList.size() && count < pageSize; startIndex++, count++)
            {
                feed.addEntry(createEntry(folderList.get(startIndex).getNodeRef().getId(), retrievePermissions, cookieHeader, false));
            }
            generateNextLink = startIndex < folderList.size();
        }

        if (retrFiles)
        {
            List<FileInfo> rawFileList = fileFolderService.listFiles(folderRef);
            List<FileInfo> fileList = new ArrayList<FileInfo>(rawFileList.size());
            for (FileInfo fileInfo : rawFileList)
            {
                if (!fileInfo.isLink() && !nodeService.hasAspect(fileInfo.getNodeRef(), ContentModel.ASPECT_WORKING_COPY))
                {
                    fileList.add(fileInfo);
                }
            }

            Collections.sort(fileList, new PropertyComparator(sortPropery, sortAsc));
            int startIndex = pageSize * (startPage - 1);
            for (int count = 0; startIndex < fileList.size() && count < pageSize; startIndex++, count++)
            {
                feed.addEntry(createEntry(pathHelper.getDocumentForWork(fileList.get(startIndex).getNodeRef()).getId(), retrievePermissions, cookieHeader, false));
            }
            generateNextLink = (startIndex < fileList.size()) || generateNextLink;
        }

        // TODO folder or library ?
        if (generateNextLink)
        {
            String link = "folder/" + folderId + "/feed?acls=" + retrievePermissions + "&category=" + category + "&sO=" + sortOrder + "&sK=" + sortColumn + "&pagesize=" + pageSize
                    + "&page=" + (startPage + 1);
            if (!slotid.equals(""))
            {
                link = link + "&hpaa.slotid=" + slotid + "&hpaa.piid=" + piid;
            }
            feed.addLink(link, "next");
        }
        if (generatePrevLink)
        {

            String link = "folder/" + folderId + "/feed?acls=" + retrievePermissions + "&category=" + category + "&sO=" + sortOrder + "&sK=" + sortColumn + "&pagesize=" + pageSize
                    + "&page=" + (startPage - 1);
            if (!slotid.equals(""))
            {
                link = link + "&hpaa.slotid=" + slotid + "&hpaa.piid=" + piid;
            }
            feed.addLink(link, "previous");
        }
        return Response.ok(feed).build();
    }

    /**
     * Retrieve document.
     * 
     * @param libraryId id of library .
     * @param documentId id of document.
     * @return Entry {@link Entry}.
     */
    public Entry retrieveDocumentEntry(String libraryId, String documentId, boolean retrievePermissions, boolean retrievePropSheets)
    {
        Entry entry = createEntry(documentId, retrievePermissions, null, retrievePropSheets);
        entry.setBaseUri(pathHelper.getLotusUrl() + "/dm/atom/library/" + pathHelper.getRootNodeRef().getId() + "/");
        return entry;
    }

    public Response retrieveDocument(String libraryId, String documentId, boolean retrievePermissions, boolean retrievePropSheets)
    {
        Entry entry = retrieveDocumentEntry(libraryId, documentId, retrievePermissions, retrievePropSheets);
        return Response.ok(entry).build();
    }

    /**
     * Retrieve document resources such as permissions and versions.
     * 
     * @throws FileNotFoundException
     */
    public Response retrieveDocumentResources(String libraryId, String documentId, boolean retrievePermissions, String category) throws FileNotFoundException
    {
        NodeRef nodeRef = new NodeRef(pathHelper.getLibraryStoreRef(), documentId);
        Factory factory = new FOMFactory();

        Feed feed = factory.newFeed();
        feed.setGenerator("", generatorVersion, generatorName);
        feed.setId("urn:lsid:ibm.com:td:" + nodeRef.getId());
        feed.addLink("document/" + nodeRef.getId() + "/feed", "self");
        feed.addLink(MessageFormat.format(pathHelper.getShareDocumentUrl(), pathHelper.getNodeRefSiteName(nodeRef), nodeRef.toString()), "alternate");

        if (retrievePermissions)
        {
            StringBuilder allowedPerms = new StringBuilder();
            for (String perm : permissionHelper.getPermissions(nodeRef))
            {
                allowedPerms.append(perm).append(",");
            }
            Element perms = feed.addExtension("urn:ibm.com/td", "permissions", "td");
            perms.setText(allowedPerms.toString().substring(0, allowedPerms.length() - 1));
        }

        if (category != null && category.length() > 0)
        {
            if (category.equalsIgnoreCase(CAT_VERSION))
            {
                VersionHistory versionHistory = versionService.getVersionHistory(pathHelper.getOriginalDocument(nodeRef));

                if (versionHistory != null)
                {
                    Collection<Version> versions = versionHistory.getAllVersions();

                    for (Version version : versions)
                    {
                        feed.addEntry(createVersionEntry(version));
                    }
                }
            }
            if (category.equalsIgnoreCase(CAT_COMMENT))
            {
                String documentName = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);

                String siteName = pathHelper.getSiteNameForDocument(nodeRef);

                if (siteName != null)
                {
                    NodeRef discussionsContainer = siteService.getContainer(siteName, "discussions");

                    if (discussionsContainer != null)
                    {
                        String topicName = MessageFormat.format(TOPIC_TITLE, documentName);

                        List<String> paths = new ArrayList<String>(1);
                        paths.add(topicName);

                        FileInfo topicFileInfo = null;

                        try
                        {
                            topicFileInfo = fileFolderService.resolveNamePath(discussionsContainer, paths);
                        }
                        catch (FileNotFoundException e)
                        {
                            // Do nothing
                        }

                        if (topicFileInfo != null)
                        {
                            List<FileInfo> posts = fileFolderService.list(topicFileInfo.getNodeRef());

                            for (FileInfo post : posts)
                            {
                                if (nodeService.getType(post.getNodeRef()).equals(ForumModel.TYPE_POST))
                                {
                                    feed.addEntry(createCommentEntry(post.getNodeRef()));
                                }
                            }
                        }
                    }
                }
            }
        }

        String authorName = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_CREATOR);
        String email = (String) nodeService.getProperty(personService.getPerson(authorName), ContentModel.PROP_EMAIL);
        feed.addAuthor(authorName, email, "");
        feed.setTitle((String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME));
        feed.setUpdated((Date) nodeService.getProperty(nodeRef, ContentModel.PROP_MODIFIED));

        return Response.ok(feed).build();
    }

    /**
     * Retrieve fodler.
     * 
     * @param libraryId id of library .
     * @param folderId id of folder.
     * @return Entry {@link Entry}.
     */
    public Response retrieveFolder(String libraryId, String folderId, boolean retrievePermissions)
    {
        Entry entry = createEntry(folderId, retrievePermissions, null, false);
        entry.setBaseUri(pathHelper.getLotusUrl() + "/dm/atom/library/" + pathHelper.getRootNodeRef().getId() + "/");
        return Response.ok(entry).build();
    }

    /**
     * Retrieve document types defined in library
     */
    public Response retrieveDocumentTypes(String libraryId)
    {
        Factory factory = new FOMFactory();

        if (docTypesFeed != null)
        {
            return Response.ok(docTypesFeed).build();
        }

        docTypesFeed = factory.newFeed();

        docTypesFeed.setGenerator("", generatorVersion, generatorName);
        docTypesFeed.setId("urn:lsid:ibm.com:td:documenttypes");
        docTypesFeed.addLink("documenttypes/feed", "self");
        docTypesFeed.setTitle("DocumentTypes");

        for (org.alfresco.service.namespace.QName docType : dictionaryService.getSubAspects(QuickrModel.ASPECT_QUICKR_DOC_TYPE, true))
        {
            AspectDefinition docTypeAspect = dictionaryService.getAspect(docType);
            docTypesFeed.addEntry(retrieveDocumentTypeEntry("", docTypeAspect.getTitle(), false));
        }

        return Response.ok(docTypesFeed).build();
    }

    public Response retrieveDocumentType(String libraryId, String documentTypeTitle, boolean retrievePropSheets)
    {
        Entry entry = retrieveDocumentTypeEntry(libraryId, documentTypeTitle, retrievePropSheets);
        return Response.ok(entry).build();
    }

    /**
     * Retrieve requested document type.
     */
    private Entry retrieveDocumentTypeEntry(String libraryId, String documentTypeTitle, boolean retrievePropSheets)
    {
        Factory factory = new FOMFactory();
        Entry documentTypeEntry = factory.newEntry();

        AspectDefinition docType = documentHelper.getDocumentTypeAspect(null, documentTypeTitle);

        documentTypeEntry.setId("urn:lsid:ibm.com:td:" + docType.getTitle());
        documentTypeEntry.addLink("documenttypes/" + docType.getTitle() + "/entry", "self");
        documentTypeEntry.addLink("documenttypes/" + docType.getTitle() + "/entry", "edit");
        documentTypeEntry.addCategory("tag:ibm.com,2006:td/type", "documentType", "documentType");

        // TODO
        // Skip author published updated created modified modifier
        documentTypeEntry.setTitle(docType.getTitle());
        Element uuid = documentTypeEntry.addExtension("urn:ibm.com/td", "uuid", "td");
        uuid.setText(docType.getTitle());
        Element label = documentTypeEntry.addExtension("urn:ibm.com/td", "label", "td");
        label.setText(docType.getTitle());

        documentTypeEntry.setSummary("");
        documentTypeEntry.setContent(new IRI("documenttypes/feed"), "application/atom+xml");
        documentTypeEntry.addExtension("urn:ibm.com/td", "defaultExtension", "td");
        documentTypeEntry.addExtension("urn:ibm.com/td", "template", "td");

        Element vers = documentTypeEntry.addExtension("urn:ibm.com/td", "versioning", "td");
        vers.setText("explicit");

        Element approvalEnabled = documentTypeEntry.addExtension("urn:ibm.com/td", "approvalEnabled", "td");
        approvalEnabled.setText("false");

        Element approvalType = documentTypeEntry.addExtension("urn:ibm.com/td", "approvalType", "td");
        approvalType.setText("none");

        Element expandGroupApprovers = documentTypeEntry.addExtension("urn:ibm.com/td", "expandGroupApprovers", "td");
        expandGroupApprovers.setText("false");

        documentTypeEntry.addExtension("urn:ibm.com/td", "approvers", "td");

        if (retrievePropSheets)
        {
            for (AspectDefinition aspectDefinition : docType.getDefaultAspects(false))
            {
                if (dictionaryService.isSubClass(aspectDefinition.getName(), QuickrModel.ASPECT_QUICKR_PROP_SHEET))
                {
                    ExtensibleElement propSheetType = documentTypeEntry.addExtension("urn:ibm.com/td", "propertySheetType", "td");
                    Entry propSheetEntry = factory.newEntry();

                    propSheetEntry.setId(aspectDefinition.getTitle());
                    propSheetEntry.addLink("documenttypes/" + aspectDefinition.getTitle() + "/entry", "self");
                    propSheetEntry.addLink("documenttypes/" + aspectDefinition.getTitle() + "/entry", "edit");
                    propSheetEntry.addCategory("tag:ibm.com,2006:td/type", "propertySheetType", "propertySheetType");

                    propSheetEntry.setTitle(aspectDefinition.getTitle());
                    Element sheetUuid = propSheetEntry.addExtension("urn:ibm.com/td", "uuid", "td");
                    sheetUuid.setText(aspectDefinition.getTitle());
                    Element sheetLabel = propSheetEntry.addExtension("urn:ibm.com/td", "label", "td");
                    sheetLabel.setText(aspectDefinition.getTitle());

                    propSheetEntry.setSummary("");
                    propSheetEntry.addExtension("urn:ibm.com/td", "constrainedMimeType", "td");
                    Element isExtracted = propSheetEntry.addExtension("urn:ibm.com/td", "isExtracted", "td");
                    isExtracted.setText("false");

                    ExtensibleElement content = factory.newExtensionElement(new javax.xml.namespace.QName("http://metadata.model.xsd.clb.content.ibm.com/1.0",
                            "propertySheetTemplate ", "meta"));
                    content.declareNS("http://www.w3.org/2001/XMLSchema-instance", "xsi");
                    content.declareNS("http://content.ibm.com/clb/1.0", "clb");

                    for (PropertyDefinition prop : aspectDefinition.getProperties().values())
                    {

                        ExtensibleElement metaProperty = content.addExtension("http://metadata.model.xsd.clb.content.ibm.com/1.0", "property", "meta");
                        ClbPropertyType propertyType = AlfrescoQuickrDataTypeHelper.getQuickrPropertyType(prop);

                        metaProperty.setAttributeValue("xsi:type", "meta:ClbPropertyType");
                        metaProperty.setAttributeValue("dataType", documentHelper.lowerCaseFirstCharacter(propertyType.getDataType().value()));
                        metaProperty.setAttributeValue("multiple", propertyType.isMultiple().toString());
                        metaProperty.setAttributeValue("propertyId", propertyType.getPropertyId());
                        metaProperty.setAttributeValue("propertyName", propertyType.getPropertyName());
                        metaProperty.setAttributeValue("readonly", propertyType.isReadOnly().toString());
                        metaProperty.setAttributeValue("required", propertyType.isRequired().toString());
                        metaProperty.setAttributeValue("searchable", propertyType.isSearchable().toString());

                        for (ClbLabelType l : propertyType.getLabels())
                        {
                            Element labelTag = metaProperty.addExtension("http://metadata.model.xsd.clb.content.ibm.com/1.0", "label", "meta");
                            labelTag.setAttributeValue("label", l.getLabel());
                            labelTag.setAttributeValue("lang", l.getLang());
                        }
                        for (ClbOptionType option : propertyType.getOptions())
                        {
                            ExtensibleElement optionTag = metaProperty.addExtension("http://metadata.model.xsd.clb.content.ibm.com/1.0", "option", "meta");
                            Element metaVlue = optionTag.addExtension("http://metadata.model.xsd.clb.content.ibm.com/1.0", "value", "meta");
                            metaVlue.setText(option.getValue());
                            for (ClbLabelType optionLabel : option.getLabels())
                            {
                                Element metaLabel = optionTag.addExtension("http://metadata.model.xsd.clb.content.ibm.com/1.0", "label", "meta");
                                metaLabel.setAttributeValue("label", optionLabel.getLabel());
                                metaLabel.setAttributeValue("lang", optionLabel.getLang());
                            }
                        }
                        for (ClbStyleType style : propertyType.getStyles())
                        {
                            Element styleTag = metaProperty.addExtension("http://metadata.model.xsd.clb.content.ibm.com/1.0", "style", "meta");
                            styleTag.setAttributeValue("name", style.getName());
                            styleTag.setAttributeValue("value", style.getValue());
                        }

                        for (String defaultValue : propertyType.getDefaultValues())
                        {
                            if (defaultValue != null)
                            {
                                Element defaultValueTag = metaProperty.addExtension("http://metadata.model.xsd.clb.content.ibm.com/1.0", "defaultValue", "meta");
                                defaultValueTag.setText(defaultValue);
                            }
                        }

                    }

                    propSheetEntry.setContent("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + content.toString());

                    propSheetType.addExtension(propSheetEntry);
                }
            }
        }

        return documentTypeEntry;
    }

    private Entry createEntry(String id, boolean retrievePermissions, String cookieHeader, boolean retrievePropSheets)
    {
        NodeRef nodeRef = new NodeRef(pathHelper.getLibraryStoreRef(), id);
        if (!fileFolderService.getFileInfo(nodeRef).isFolder())
        {
            nodeRef = pathHelper.getDocumentForWork(nodeRef);
        }
        String nodeTitle = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_TITLE);
        String nodeName = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
        if (nodeTitle == null || nodeTitle.length() == 0)
        {
            nodeTitle = nodeName;
        }

        Factory factory = new FOMFactory();

        Entry entry = factory.newEntry();

        if (fileFolderService.getFileInfo(nodeRef).isFolder())
        {
            entry.addLink("folder/" + nodeRef.getId() + "/entry", "self");
            entry.addLink("folder/" + nodeRef.getId() + "/entry", "edit");
            entry.addCategory("tag:ibm.com,2006:td/type", "folder", "folder");
            entry.setTitle(nodeName);
        }
        else
        {
            if (nodeService.hasAspect(nodeRef, ContentModel.ASPECT_WORKING_COPY))
            {
                String owner = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_WORKING_COPY_OWNER);
                // Change ID. Now all links will be pointed to the original node.
                NodeRef originalNode = (NodeRef) nodeService.getProperty(nodeRef, ContentModel.PROP_COPY_REFERENCE);
                nodeName = (String) nodeService.getProperty(originalNode, ContentModel.PROP_NAME);

                id = originalNode.getId();

                Element locked = entry.addExtension("urn:ibm.com/td", "locked", "td");
                locked.setText("true");
                ExtensibleElement lockOwner = entry.addExtension("urn:ibm.com/td", "lockOwner", "td");
                lockOwner.addExtension("urn:ibm.com/td", "uri", "td");
                lockOwner.addExtension("urn:ibm.com/td", "email", "td");
                Element lockOwnerName = lockOwner.addExtension("urn:ibm.com/td", "name", "td");
                lockOwnerName.setText(owner);

                entry.addLink("checkedout/" + nodeRef.getId() + "/entry", "checked-out");
            }

            entry.setTitle(nodeTitle);
            entry.addLink("document/" + id + "/entry", "self");
            entry.addLink("document/" + id + "/entry", "edit");
            entry.addLink("document/" + id + "/media", "edit-media");

            ContentData contentData = (ContentData) nodeService.getProperty(nodeRef, ContentModel.PROP_CONTENT);
            Link link = factory.newLink();
            link.setHref("document/" + nodeRef.getId() + "/media");
            link.setRel("enclosure");
            link.setTitle(nodeName);
            link.setLength(contentData.getSize());
            link.setMimeType(contentData.getMimetype());
            entry.addLink(link);

            if (pathHelper.getNodePath(nodeRef, true).indexOf("/") != -1)
            {
                entry.addLink(MessageFormat.format(pathHelper.getShareDocumentUrl(), pathHelper.getNodeRefSiteName(nodeRef), nodeRef.toString()), "alternate");
            }
            else
            {
                entry.addLink("");
            }

            entry.addCategory("tag:ibm.com,2006:td/type", "document", "document");

            AspectDefinition docType = documentHelper.getDocumentTypeAspect(nodeRef.getId(), null);
            Element documenttype = entry.addExtension("urn:ibm.com/td", "documenttype", "td");
            documenttype.setText(docType.getTitle());

            if (retrievePropSheets)
            {
                for (AspectDefinition aspectDefinition : docType.getDefaultAspects(false))
                {
                    if (dictionaryService.isSubClass(aspectDefinition.getName(), QuickrModel.ASPECT_QUICKR_PROP_SHEET))
                    {

                        for (PropertyDefinition prop : aspectDefinition.getProperties().values())
                        {
                            Element snxField = entry.addExtension("http://www.ibm.com/xmlns/prod/sn", "field", "snx");
                            snxField.setAttributeValue("fid", prop.getTitle());
                            snxField.setAttributeValue("name", aspectDefinition.getTitle());
                            snxField.setAttributeValue("pstId", aspectDefinition.getTitle());

                            String type = AlfrescoQuickrDataTypeHelper.getQuickrPropertyType(prop).getDataType().value();
                            if (nodeService.getProperty(nodeRef, prop.getName()) != null)
                            {
                                if (ClbDataType.DATE_TIME.value().equals(type))
                                {
                                    GregorianCalendar gcal = new GregorianCalendar();
                                    gcal.setTime((Date) nodeService.getProperty(nodeRef, prop.getName()));
                                    try
                                    {
                                        snxField.setText(DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal).toString());
                                    }
                                    catch (DatatypeConfigurationException e)
                                    {
                                    }
                                }
                                else
                                {
                                    snxField.setText(nodeService.getProperty(nodeRef, prop.getName()).toString());
                                }
                            }
                            String firstChar = type.substring(0, 1).toLowerCase();
                            type = firstChar + type.substring(1);
                            snxField.setAttributeValue("type", type);

                        }
                    }
                }
            }

        }

        if (retrievePermissions && Double.valueOf(generatorVersion) >= 8.5)
        {
            StringBuilder allowedPerms = new StringBuilder();
            for (String perm : permissionHelper.getPermissions(nodeRef))
            {
                allowedPerms.append(perm).append(",");
            }
            Element perms = entry.addExtension("urn:ibm.com/td", "permissions", "td");
            perms.setText(allowedPerms.toString().substring(0, allowedPerms.length() - 1));

        }
        String authorName = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_CREATOR);
        String email = (String) nodeService.getProperty(personService.getPerson(authorName), ContentModel.PROP_EMAIL);
        entry.addAuthor(authorName, email, "");

        String modifierName = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_MODIFIER);
        String modifierEmail = (String) nodeService.getProperty(personService.getPerson(modifierName), ContentModel.PROP_EMAIL);

        ExtensibleElement modifierElement = entry.addExtension("urn:ibm.com/td", "modifier", "td");
        Element modifierUriTag = modifierElement.addExtension("urn:ibm.com/td", "uri", "td");
        Element modifierNameTag = modifierElement.addExtension("urn:ibm.com/td", "name", "td");
        Element modifierEmailTag = modifierElement.addExtension("urn:ibm.com/td", "email", "td");
        modifierUriTag.setText("");
        modifierNameTag.setText(modifierName);
        modifierEmailTag.setText(modifierEmail);

        entry.setPublished((Date) nodeService.getProperty(nodeRef, ContentModel.PROP_CREATED));
        entry.setUpdated((Date) nodeService.getProperty(nodeRef, ContentModel.PROP_MODIFIED));
        entry.setSummary((String) nodeService.getProperty(nodeRef, ContentModel.PROP_DESCRIPTION));

        if (fileFolderService.getFileInfo(nodeRef).isFolder())
        {
            entry.setContent(new IRI("folder/" + id + "/feed"), "application/atom+xml");
        }
        else
        {
            String mimetype = "text/plain";

            ContentData contentData = (ContentData) nodeService.getProperty(nodeRef, ContentModel.PROP_CONTENT);
            if (contentData != null)
            {
                mimetype = contentData.getMimetype();
            }

            entry.setContent(new IRI("document/" + nodeRef.getId() + "/media" + (cookieHeader == null ? "" : getLtpaToken(cookieHeader))), mimetype);
            // Maybe this comment will be helpful if something will not be working.
            // Don't know why, but abdera can't parse it. So add content as Exstension.
            // Element content = entry.addExtension("", "content", "");
            // content.setAttributeValue("type", mimetype);
            // content.setAttributeValue("xml:lang", "en");
            // content.setAttributeValue("src", "document/" + id + "/media" + (cookieHeader == null ? "" : getLtpaToken(cookieHeader)));

        }

        entry.setId("urn:lsid:ibm.com:td:" + id);
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
     * Update document.
     * 
     * @param libraryId id of library .
     * @param documentId id of document.
     * @param headres {@link HttpHeaders}
     * @return Response {@link Response}.
     * @throws URISyntaxException
     */
    public Response updateDocument(String libraryId, String documentId, HttpHeaders headres, final byte[] body, final String docTypeTitle, String opId, String format,
            final String createVersion) throws URISyntaxException
    {
        Factory factory = new FOMFactory();
        Entry documnetEntry = factory.newEntry();

        if ("delete".equalsIgnoreCase(getHeaderValue(headres, "x-method-override")))
        {
            deleteItem(documentId);
        }
        else if ("PUT".equalsIgnoreCase(getHeaderValue(headres, "x-method-override")))
        {
            final NodeRef nodeRef = pathHelper.getDocumentForWork(new NodeRef(pathHelper.getLibraryStoreRef(), documentId));

            final Document doc = documentHelper.getXMLDocument(body);

            transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Object>()
            {
                public Object execute() throws FileExistsException, FileNotFoundException
                {
                    String name = documentHelper.getNodeValue(doc, "label");
                    String title = documentHelper.getNodeValue(doc, "title");
                    String desc = documentHelper.getNodeValue(doc, "summary");
                    if (name.length() > 0)
                    {
                        // Doesn't rename working copy
                        if (!nodeService.hasAspect(nodeRef, ContentModel.ASPECT_WORKING_COPY))
                        {
                            fileFolderService.rename(nodeRef, name);
                        }
                        nodeService.setProperty(nodeRef, ContentModel.PROP_TITLE, title);
                        nodeService.setProperty(nodeRef, ContentModel.PROP_DESCRIPTION, desc);
                    }
                    return null;
                }
            });

            NodeList fields = doc.getElementsByTagName("field");
            Map<String, Map<String, String>> aspectWithProps = new HashMap<String, Map<String, String>>();
            for (int i = 0; i < fields.getLength(); i++)
            {
                Node field = fields.item(i);
                NamedNodeMap attrs = field.getAttributes();
                String propSheetTitle = attrs.getNamedItem("pstId").getNodeValue();
                String propTitle = attrs.getNamedItem("fid").getNodeValue();
                String propValue = "";
                if (field.getChildNodes().getLength() > 0)
                {
                    propValue = field.getChildNodes().item(0).getNodeValue();
                }

                if (!aspectWithProps.containsKey(propSheetTitle))
                {
                    aspectWithProps.put(propSheetTitle, new HashMap<String, String>());
                }

                Map<String, String> propMap = aspectWithProps.get(propSheetTitle);
                propMap.put(propTitle, propValue);

            }

            if (docTypeTitle.length() > 0)
            {
                transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Object>()
                {
                    public Object execute()
                    {
                        Set<org.alfresco.service.namespace.QName> aspectList = nodeService.getAspects(nodeRef);
                        for (org.alfresco.service.namespace.QName aspect : aspectList)
                        {
                            if (dictionaryService.isSubClass(aspect, QuickrModel.ASPECT_QUICKR_DOC_TYPE))
                            {
                                documentHelper.removeDocType(nodeRef, aspect);
                            }

                        }

                        org.alfresco.service.namespace.QName newDocType = documentHelper.searchAspect(QuickrModel.ASPECT_QUICKR_DOC_TYPE, docTypeTitle);
                        nodeService.addAspect(nodeRef, newDocType, null);
                        return null;
                    }
                });
            }

            for (String aspectTitle : aspectWithProps.keySet())
            {
                final org.alfresco.service.namespace.QName propSheetQname = documentHelper.searchAspect(QuickrModel.ASPECT_QUICKR_PROP_SHEET, aspectTitle);
                final Map<String, String> properties = aspectWithProps.get(aspectTitle);

                transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Object>()
                {
                    public Object execute()
                    {
                        setPropSheetWithProps(nodeRef, propSheetQname, properties);
                        return null;
                    }
                });
            }

            NodeRef resultNodeRef = nodeRef;
            String isLocked = documentHelper.getNodeValue(doc, "locked");
            if ("false".equals(isLocked))
            {
                AspectDefinition workingCopyDocType = documentHelper.getDocumentTypeAspect(nodeRef.getId(), null);
                if (!workflowHelper.startWorkflowTask(nodeRef))
                {
                    resultNodeRef = checkOutCheckInService.checkin(nodeRef, null);
                    if (nodeService.hasAspect(resultNodeRef, QuickrModel.ASPECT_QUICKR_INITIAL_DRAFT))
                    {
                        nodeService.removeAspect(resultNodeRef, QuickrModel.ASPECT_QUICKR_INITIAL_DRAFT);
                    }

                    for (org.alfresco.service.namespace.QName aspect : nodeService.getAspects(resultNodeRef))
                    {
                        if (dictionaryService.isSubClass(aspect, QuickrModel.ASPECT_QUICKR_DOC_TYPE) && !workingCopyDocType.getName().equals(aspect))
                        {
                            documentHelper.removeDocType(resultNodeRef, aspect);
                        }
                    }
                }
            }
            documnetEntry = retrieveDocumentEntry(libraryId, resultNodeRef.getId(), false, false);

        }
        else if (!opId.equals("") && opId.startsWith("replace"))
        {
            final NodeRef nodeRef = pathHelper.getDocumentForWork(new NodeRef(pathHelper.getLibraryStoreRef(), documentId));

            // TODO it's very rube implementation
            // Crete File from Custom Library
            String conType = getHeaderValue(headres, "Content-Type");
            final String boundary = "--" + conType.substring(conType.indexOf("boundary") + 9);

            FileInfo resultFileInfo = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<FileInfo>()
            {
                @Override
                public FileInfo execute() throws Throwable
                {
                    if (Boolean.parseBoolean(createVersion))
                    {
                        if (!nodeService.hasAspect(nodeRef, ContentModel.ASPECT_VERSIONABLE))
                        {
                            nodeService.addAspect(nodeRef, ContentModel.ASPECT_VERSIONABLE, null);
                        }
                    }

                    final String[] parts = new String(body, MULTIPART_ENCODING).split(boundary);
                    Map<String, String> result = new HashMap<String, String>();
                    for (String part : parts)
                    {
                        processPart(part, result);
                    }

                    // update content
                    ContentWriter writer = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
                    InputStream contentStream = new ByteArrayInputStream(result.get(PART_TEXT).getBytes(MULTIPART_ENCODING));
                    writer.putContent(contentStream);
                    return fileFolderService.getFileInfo(nodeRef);
                }
            });

            documnetEntry = createEntry(resultFileInfo.getNodeRef().getId(), false, null, false);
        }

        if (documnetEntry.getLink("self") != null)
        {
            ResponseBuilder resp = Response.created(documnetEntry.getLink("self").getHref().toURI()).entity(documnetEntry);
            if ("html".equals(format))
            {
                return resp.type(MediaType.TEXT_HTML_TYPE).build();
            }
        }

        return Response.ok(documnetEntry).build();
    }

    /**
     * Update draft
     * 
     * @param libraryId id of library .
     * @param draftId id of draft.
     * @param headres {@link HttpHeaders}
     * @return Response {@link Response}.
     * @throw URISyntaxException
     */
    public Response updateDraft(String libraryId, String draftId, HttpHeaders headres, final byte[] body) throws URISyntaxException
    {
        Factory factory = new FOMFactory();
        Entry documnetEntry = factory.newEntry();

        if ("PUT".equalsIgnoreCase(getHeaderValue(headres, "x-method-override")))
        {
            final NodeRef nodeRef = pathHelper.getDocumentForWork(new NodeRef(pathHelper.getLibraryStoreRef(), draftId));

            FileInfo resultFileInfo = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<FileInfo>()
            {
                @Override
                public FileInfo execute() throws Throwable
                {
                    // update content
                    ContentWriter writer = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
                    InputStream contentStream = new ByteArrayInputStream(body);
                    writer.putContent(contentStream);
                    return fileFolderService.getFileInfo(nodeRef);
                }
            });

            documnetEntry = createEntry(resultFileInfo.getNodeRef().getId(), false, null, false);

        }

        return Response.ok(documnetEntry).build();
    }

    /**
     * Delete folder.
     * 
     * @param libraryId id of library .
     * @param foldertId id of folder.
     * @param headres {@link HttpHeaders}
     * @return Response {@link Response}.
     */
    public Response updateFolder(String libraryId, String folderId, HttpHeaders headres, byte[] body)
    {
        Factory factory = new FOMFactory();
        Entry folderEntry = factory.newEntry();

        if ("delete".equalsIgnoreCase(getHeaderValue(headres, "x-method-override")))
        {
            deleteItem(folderId);
        }
        else if ("PUT".equalsIgnoreCase(getHeaderValue(headres, "x-method-override")))
        {

            final NodeRef nodeRef = new NodeRef(pathHelper.getLibraryStoreRef(), folderId);

            final Document doc = documentHelper.getXMLDocument(body);

            transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Object>()
            {
                public Object execute() throws FileExistsException, FileNotFoundException
                {
                    String newName = documentHelper.getNodeValue(doc, "title");
                    String desc = documentHelper.getNodeValue(doc, "summary");

                    if (newName.length() > 0)
                    {
                        fileFolderService.rename(nodeRef, newName);
                        nodeService.setProperty(nodeRef, ContentModel.PROP_DESCRIPTION, desc);
                    }
                    return null;
                }
            });

            folderEntry = createEntry(nodeRef.getId(), false, null, false);

        }
        return Response.ok(folderEntry).build();
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

        VersionHistory versionHistory = versionService.getVersionHistory(pathHelper.getOriginalDocument(documentNodeRef));
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

    private String getLtpaToken(String cookieHeader)
    {
        String ltpaToken = "?";

        if (cookieHeader != null && cookieHeader.length() > 0)
        {
            // looking for LtpaToken cookie
            String[] cookies = cookieHeader.split(";");

            for (String cookie : cookies)
            {
                cookie = cookie.trim();
                if (cookie.startsWith(LtpaAuthenticator.LTPA_COOKIE_NAME))
                {
                    ltpaToken += cookie;

                    break;
                }
            }
        }

        return ltpaToken;
    }

    private Entry createVersionEntry(Version version)
    {
        Factory factory = new FOMFactory();
        Entry entry = factory.newEntry();

        NodeRef versionNodeRef = version.getFrozenStateNodeRef();

        entry.setId(versionNodeRef.getId());

        entry.addLink("document/" + versionNodeRef.getId() + "/version/entry", "self");
        entry.addLink("document/" + versionNodeRef.getId() + "/version/entry", "alternate");
        entry.addLink("document/" + versionNodeRef.getId() + "/version/entry", "edit");

        entry.addCategory("tag:ibm.com,2006:td/type", "version", "version");

        entry.setTitle((String) version.getVersionProperty("name"));
        entry.setSummary(version.getDescription());

        Element versionLabel = entry.addExtension("urn:ibm.com/td", "versionLabel", "td");
        versionLabel.setText(version.getVersionLabel());

        Element changeSummary = entry.addExtension("urn:ibm.com/td", "changeSummary", "td");
        changeSummary.setText(version.getDescription());

        Element documentUuid = entry.addExtension("urn:ibm.com/td", "documentUuid", "td");
        documentUuid.setText(versionNodeRef.getId());

        entry.addAuthor(version.getFrozenModifier(), "", "");

        Element created = entry.addExtension("urn:ibm.com/td", "created", "td");
        created.setText(AtomDate.valueOf((Date) version.getVersionProperty(Version2Model.PROP_FROZEN_CREATED)).getValue());

        Element modified = entry.addExtension("urn:ibm.com/td", "modified", "td");
        modified.setText(AtomDate.valueOf((Date) version.getVersionProperty(Version2Model.PROP_FROZEN_CREATED)).getValue());

        entry.setUpdated(version.getFrozenModifiedDate());

        return entry;
    }

    private Entry createCommentEntry(NodeRef commentNodeRef)
    {
        Factory factory = new FOMFactory();
        Entry entry = factory.newEntry();

        entry.setId("urn:lsid:ibm.com:td:" + commentNodeRef.getId());

        entry.addLink("document/" + commentNodeRef.getId() + "/comment/entry", "self");
        entry.addLink("document/" + commentNodeRef.getId() + "/comment/entry", "alternate");
        entry.addLink("document/" + commentNodeRef.getId() + "/comment/entry", "edit");
        entry.addLink("document/" + commentNodeRef.getId() + "/comment/entry", "edit-media");

        entry.addCategory("tag:ibm.com,2006:td/type", "comment", "comment");

        entry.addAuthor((String) nodeService.getProperty(commentNodeRef, ContentModel.PROP_CREATOR), "", "");

        Element modifier = entry.addExtension("urn:ibm.com/td", "modifier", "td");

        Element uri = entry.addExtension("urn:ibm.com/td", "uri", "td");
        uri.setParentElement(modifier);
        Element name = entry.addExtension("urn:ibm.com/td", "name", "td");
        name.setText((String) nodeService.getProperty(commentNodeRef, ContentModel.PROP_MODIFIER));
        name.setParentElement(modifier);
        Element email = entry.addExtension("urn:ibm.com/td", "email", "td");
        email.setParentElement(modifier);

        entry.setTitle("", Type.TEXT);

        Element uuid = entry.addExtension("urn:ibm.com/td", "uuid", "td");
        uuid.setText(commentNodeRef.getId());

        entry.setPublished((Date) nodeService.getProperty(commentNodeRef, ContentModel.PROP_CREATED));
        entry.setUpdated((Date) nodeService.getProperty(commentNodeRef, ContentModel.PROP_MODIFIED));

        Element created = entry.addExtension("urn:ibm.com/td", "created", "td");
        created.setText(AtomDate.format((Date) nodeService.getProperty(commentNodeRef, ContentModel.PROP_CREATED)));
        Element modified = entry.addExtension("urn:ibm.com/td", "modified", "td");
        modified.setText(AtomDate.format((Date) nodeService.getProperty(commentNodeRef, ContentModel.PROP_MODIFIED)));

        ContentReader reader = contentService.getReader(commentNodeRef, ContentModel.PROP_CONTENT);

        entry.setContent(reader.getContentString());

        return entry;
    }

    /**
     * Check out document.
     */
    public Response checkOutDocument(String libraryId, byte[] body)
    {
        Document doc = documentHelper.getXMLDocument(body);
        String docId = documentHelper.getNodeValue(doc, "id");
        docId = docId.substring("urn:lsid:ibm.com:td:".length());
        final NodeRef nodeRef = new NodeRef(pathHelper.getLibraryStoreRef(), docId);

        NodeRef workingCopy = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute()
            {
                return checkOutCheckInService.checkout(nodeRef);
            }
        });

        Entry workingCopyEntry = createEntry(workingCopy.getId(), false, null, false);
        workingCopyEntry.setBaseUri(pathHelper.getLotusUrl() + "/dm/atom/library/" + pathHelper.getRootNodeRef().getId() + "/");

        return Response.ok(workingCopyEntry).build();
    }

    /**
     * Cancel check out.
     */
    public Response cancelCheckOut(String libraryId, String documentId, HttpHeaders headres)
    {
        final NodeRef workingCopyNodeRef = new NodeRef(pathHelper.getLibraryStoreRef(), documentId);

        if ("delete".equalsIgnoreCase(getHeaderValue(headres, "x-method-override")))
        {
            transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Object>()
            {
                public Object execute()
                {
                    workflowHelper.cancelWorkflows(workingCopyNodeRef);
                    NodeRef originalNodeRef = checkOutCheckInService.cancelCheckout(workingCopyNodeRef);
                    if (nodeService.hasAspect(originalNodeRef, QuickrModel.ASPECT_QUICKR_INITIAL_DRAFT))
                    {
                        nodeService.deleteNode(originalNodeRef);
                    }

                    return null;
                }
            });
        }
        return Response.ok().type(MediaType.TEXT_HTML_TYPE).build();
    }

    /**
     * Retrieve supported views.
     */
    public Response retrieveViews(String libraryId)
    {
        Factory factory = new FOMFactory();
        Feed feed = factory.newFeed();

        feed.setBaseUri(pathHelper.getLotusUrl() + "/dm/atom/library/" + pathHelper.getRootNodeRef().getId() + "/");
        feed.setGenerator("", generatorVersion, generatorName);
        feed.setId("urn:lsid:ibm.com:td:views");
        feed.setTitle("Views");
        feed.addLink("views/feed", "self");

        return Response.ok(feed).build();
    }

    /**
     * Retrieve documents according to the provided view.
     */
    public Response applyView(String libraryId, String viewId, boolean retrievePropSheets, String category, String sortOrder, String sortColumn, int startPage, int pageSize)
    {
        boolean retrFolders = category.contains(CAT_FOLDER);
        boolean retrFiles = category.contains(CAT_DOCUMENT);

        Factory factory = new FOMFactory();

        Feed feed = factory.newFeed();

        feed.setId("urn:lsid:ibm.com:td:" + viewId);

        feed.setBaseUri(pathHelper.getLotusUrl() + "/dm/atom/library/" + pathHelper.getRootNodeRef().getId() + "/");

        feed.addLink("view/" + viewId + "/feed", "self");

        feed.setTitle(viewId);
        List<FileInfo> fileInfos = new ArrayList<FileInfo>();

        if (viewId.equals(VIEW_ALL_DOCS))
        {

            for (FileInfo fileInfo : fileFolderService.search(pathHelper.getRootNodeRef(), "*", retrFiles, retrFolders, true))
            {
                if (!fileInfo.isLink() && !nodeService.hasAspect(fileInfo.getNodeRef(), ContentModel.ASPECT_WORKING_COPY))
                {
                    fileInfos.add(fileInfo);
                }
            }

        }
        else if (viewId.equals(VIEW_MY_CHECKED_OUT))
        {
            for (FileInfo fileInfo : fileFolderService.search(pathHelper.getRootNodeRef(), "*", retrFiles, false, true))
            {
                if (!fileInfo.isLink() && nodeService.hasAspect(fileInfo.getNodeRef(), ContentModel.ASPECT_WORKING_COPY))
                {
                    NodeRef originalNode = pathHelper.getOriginalDocument(fileInfo.getNodeRef());
                    DocumentStatus originalNodeStatus = documentHelper.getDocumentStatus(originalNode);
                    if (originalNodeStatus.equals(DocumentStatus.LONG_CHECKOUT_OWNER))
                    {
                        fileInfos.add(fileInfo);
                    }
                }
            }

        }

        boolean sortAsc = sortOrder.equals(SORT_ORDER_ASC);
        QName sortPropery = sortField.get(sortColumn);

        Collections.sort(fileInfos, new PropertyComparator(sortPropery, sortAsc));
        int startIndex = pageSize * (startPage - 1);
        for (int count = 0; startIndex < fileInfos.size() && count < pageSize; startIndex++, count++)
        {
            feed.addEntry(createEntry(fileInfos.get(startIndex).getNodeRef().getId(), false, null, retrievePropSheets));
        }

        boolean generateNextLink = (startIndex < fileInfos.size());
        boolean generatePrevLink = startPage > 1;

        if (generateNextLink)
        {
            feed.addLink("view/" + viewId + "?category=" + category + "&sO=" + sortOrder + "&sK=" + sortColumn + "&pagesize=" + pageSize + "&page=" + (startPage + 1), "next");
        }
        if (generatePrevLink)
        {
            feed.addLink("view/" + viewId + "?category=" + category + "&sO=" + sortOrder + "&sK=" + sortColumn + "&pagesize=" + pageSize + "&page=" + (startPage - 1), "previous");
        }

        return Response.ok(feed).build();
    }

    public Response createDocResource(String libraryId, String documentId, byte[] body) throws FileNotFoundException
    {
        final NodeRef nodeRef = pathHelper.getDocumentForWork(new NodeRef(pathHelper.getLibraryStoreRef(), documentId));
        final Document doc = documentHelper.getXMLDocument(body);

        String resource = documentHelper.getNodeParam(doc, "category", "term");
        if (resource.length() > 0)
        {
            if (resource.equals(CAT_VERSION))
            {
                documentId = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<String>()
                {
                    public String execute()
                    {
                        NodeRef originalNode = pathHelper.getOriginalDocument(nodeRef);
                        if (!documentHelper.getVersionMode(nodeRef).equals(ClbVersioning.EXPLICIT))
                        {
                            throw new AlfrescoRuntimeException("Document must have Document Type with EXPLICIT versionig to perform this operation.");
                        }
                        versionService.createVersion(originalNode, null);
                        return originalNode.getId().toString();
                    }
                });

                return retrieveDocumentResources(libraryId, documentId, false, CAT_VERSION);
            }
            else if (resource.equals(CAT_COMMENT))
            {
                transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Object>()
                {
                    public Object execute()
                    {
                        String content = documentHelper.getNodeValue(doc, "content");

                        String documentName = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);

                        String siteName = pathHelper.getSiteNameForDocument(nodeRef);

                        NodeRef discussionsContainer = siteService.getContainer(siteName, "discussions");

                        if (discussionsContainer == null)
                        {
                            discussionsContainer = siteService.createContainer(siteName, "discussions", ContentModel.TYPE_FOLDER, null);
                        }

                        String topicName = MessageFormat.format(TOPIC_TITLE, documentName);

                        List<String> paths = new ArrayList<String>(1);
                        paths.add(topicName);

                        FileInfo topicFileInfo = null;
                        boolean newTopic = false;
                        try
                        {
                            topicFileInfo = fileFolderService.resolveNamePath(discussionsContainer, paths);
                        }
                        catch (FileNotFoundException e)
                        {
                            topicFileInfo = fileFolderService.create(discussionsContainer, topicName, ForumModel.TYPE_TOPIC);
                            newTopic = true;
                        }

                        nodeService.setProperty(topicFileInfo.getNodeRef(), ContentModel.PROP_TITLE, topicName);

                        String postName = pathHelper.getUniqueName(topicFileInfo.getNodeRef());

                        FileInfo postFileInfo = fileFolderService.create(topicFileInfo.getNodeRef(), postName, ForumModel.TYPE_POST);

                        // get a writer for the content and put the file
                        ContentWriter writer = contentService.getWriter(postFileInfo.getNodeRef(), ContentModel.PROP_CONTENT, true);
                        // set the mimetype and encoding
                        writer.setEncoding("UTF-8");
                        writer.putContent(content);

                        nodeService.setProperty(postFileInfo.getNodeRef(), ContentModel.PROP_TITLE, topicName);

                        Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
                        props.put(PROP_PUBLISHED, new Date());
                        nodeService.addAspect(postFileInfo.getNodeRef(), ASPECT_SYNDICATION, props);

                        if (!newTopic)
                        {
                            FileInfo reference = fileFolderService.list(topicFileInfo.getNodeRef()).get(0);
                            nodeService.addAspect(postFileInfo.getNodeRef(), ContentModel.ASPECT_REFERENCING, null);
                            nodeService.createAssociation(postFileInfo.getNodeRef(), reference.getNodeRef(), ContentModel.ASSOC_REFERENCES);
                        }

                        return null;
                    }
                });

                return retrieveDocumentResources(libraryId, documentId, false, CAT_COMMENT);
            }
        }

        return null;
    }

    private class PropertyComparator implements Comparator<FileInfo>
    {
        private QName sortPropery;
        private boolean sortAsc;

        public PropertyComparator(QName sortPropery, boolean sortAsc)
        {
            this.sortPropery = sortPropery;
            this.sortAsc = sortAsc;
        }

        @Override
        public int compare(FileInfo f1, FileInfo f2)
        {
            int compareResult = f1.getProperties().get(sortPropery).toString().compareToIgnoreCase(f2.getProperties().get(sortPropery).toString());
            if (!sortAsc)
            {
                compareResult *= -1;
            }
            return compareResult;
        }
    }

}
