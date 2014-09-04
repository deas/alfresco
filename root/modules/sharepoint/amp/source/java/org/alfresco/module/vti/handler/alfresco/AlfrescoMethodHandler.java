/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.metadata.dialog.DialogsMetaInfo;
import org.alfresco.module.vti.metadata.dic.DocumentStatus;
import org.alfresco.module.vti.metadata.dic.GetOption;
import org.alfresco.module.vti.metadata.dic.VtiSort;
import org.alfresco.module.vti.metadata.dic.VtiSortField;
import org.alfresco.module.vti.metadata.model.DocMetaInfo;
import org.alfresco.module.vti.metadata.model.DocsMetaInfo;
import org.alfresco.module.vti.metadata.model.Document;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.repo.version.VersionModel;
import org.alfresco.repo.webdav.ActivityPostProducer;
import org.alfresco.repo.webdav.WebDAVActivityPoster;
import org.alfresco.repo.webdav.WebDAV;
import org.alfresco.repo.webdav.WebDAVHelper;
import org.alfresco.repo.webdav.WebDAVServerException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionHistory;
import org.alfresco.service.cmr.version.VersionType;
import org.alfresco.service.cmr.webdav.WebDavService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.util.URLDecoder;
import org.springframework.util.FileCopyUtils;

/**
 * Alfresco implementation of MethodHandler and AbstractAlfrescoMethodHandler
 * 
 * @author PavelYur
 */
public class AlfrescoMethodHandler extends AbstractAlfrescoMethodHandler implements ActivityPostProducer
{
    private final static Log logger = LogFactory.getLog(AlfrescoMethodHandler.class);
    private static final String DAV_EXT_LOCK_TIMEOUT = "X-MSDAVEXTLockTimeout";

    private SiteService siteService;
    private AuthenticationComponent authenticationComponent;
    private ShareUtils shareUtils;
    private WebDAVActivityPoster activityPoster;
    private WebDavService davService;
    private WebDAVHelper davHelper;
    
    /**
     * Set authentication component
     * 
     * @param authenticationComponent the authentication component to set ({@link AuthenticationComponent})
     */
    public void setAuthenticationComponent(AuthenticationComponent authenticationComponent)
    {
        this.authenticationComponent = authenticationComponent;
    }

    /**
     * Get authentication component
     * 
     * @return AuthenticationComponent the authentication component
     */
    public AuthenticationComponent getAuthenticationComponent()
    {
        return authenticationComponent;
    }

    /**
     * Set share utils
     * 
     * @param shareUtils the share utils to set ({@link ShareUtils})
     */
    public void setShareUtils(ShareUtils shareUtils)
    {
        this.shareUtils = shareUtils;
    }

    /**
     * Get share utils
     * 
     * @return ShareUtils the share utils
     */
    public ShareUtils getShareUtils()
    {
        return shareUtils;
    }

    /**
     * Set site service
     * 
     * @param siteService the site service to set ({@link SiteService})
     */
    public void setSiteService(SiteService siteService)
    {
        this.siteService = siteService;
    }

    /**
     * Get site service
     * 
     * @return SiteService the site service
     */
    public SiteService getSiteService()
    {
        return siteService;
    }

    /**
     * @see org.alfresco.module.vti.handler.MethodHandler#decomposeURL(java.lang.String, java.lang.String)
     */
    public String[] decomposeURL(final String url, final String alfrescoContext)
    {
        return getPathHelper().decomposeDocumentURL(alfrescoContext, url, SiteModel.TYPE_SITE);
    }

    /**
     * @see org.alfresco.module.vti.handler.MethodHandler#existResource(HttpServletRequest, HttpServletResponse)
     */
    public boolean existResource(HttpServletRequest request, HttpServletResponse response)
    {
        String decodedUrl = URLDecoder.decode(request.getRequestURI());
        if (decodedUrl.length() > getPathHelper().getAlfrescoContext().length())
        {
            decodedUrl = decodedUrl.substring(getPathHelper().getAlfrescoContext().length() + 1);
        }

        FileInfo resourceFileInfo = getPathHelper().resolvePathFileInfo(decodedUrl);

        // ALF-17662, hidden documents are the same as nonexistent
        if (resourceFileInfo == null || getFileFolderService().isHidden(resourceFileInfo.getNodeRef()))
        {
            try
            {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getOutputStream().write("NOT FOUND".getBytes());
                response.getOutputStream().close();
            }
            catch (Exception e)
            {
            }
        }
        else
        {
            if (!resourceFileInfo.isFolder())
            {
                NodeRef resourceNodeRef = resourceFileInfo.getNodeRef();
                String guid = resourceNodeRef.getId().toUpperCase();
                
                NodeRef workingCopyNodeRef = getCheckOutCheckInService().getWorkingCopy(resourceNodeRef);

                // original node props
                Map<QName, Serializable> props = getNodeService().getProperties(resourceNodeRef);
                // original node reader
                ContentReader contentReader = getFileFolderService().getReader(resourceNodeRef);
                
                if (workingCopyNodeRef != null)
                {
                    String workingCopyOwner = getNodeService().getProperty(workingCopyNodeRef, ContentModel.PROP_WORKING_COPY_OWNER).toString();
                    if (workingCopyOwner.equals(getUserName()))
                    {
                        // allow to see changes in document after it was checked out (only for checked out owner)
                        contentReader = getFileFolderService().getReader(workingCopyNodeRef);

                        // working copy props
                        props = getNodeService().getProperties(workingCopyNodeRef);
                    }
                }
                
                Date lastModified = (Date) props.get(ContentModel.PROP_MODIFIED);

                // Office 2008/2011 for Mac requires following headers
                response.setHeader("Last-Modified", VtiUtils.formatBrowserDate(lastModified));
                String etag = VtiUtils.constructETag(guid, lastModified);
                response.setHeader("ETag", etag);
                response.setHeader("ResourceTag", VtiUtils.constructResourceTag(guid, lastModified));
                
                boolean writeContent = true;
                
                if ("HEAD".equals(request.getMethod()))
                {
                    writeContent = false;
                }
                
                String ifNonMatch = request.getHeader("If-None-Match");
                
                if (ifNonMatch != null && ifNonMatch.equals(etag))
                {
                    writeContent = false;
                    response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                }
                
                if (writeContent)
                {
                    ContentData content = (ContentData) props.get(ContentModel.PROP_CONTENT);
        
                    response.setContentType(content.getMimetype());
        
                    try
                    {
                        FileCopyUtils.copy(contentReader.getContentInputStream(), response.getOutputStream());
                    }
                    catch (Exception e)
                    {
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("Exception while copying content stream to response ", e);
                        }
                    }
                }
            }
        }
        return resourceFileInfo != null;
    }

    /**
     * @see org.alfresco.module.vti.handler.MethodHandler#putResource(HttpServletRequest, HttpServletResponse)
     */
    public void putResource(HttpServletRequest request, HttpServletResponse response)
    {
        // Office 2008/2011 for Mac specific header
        final String lockTimeOut = request.getHeader(DAV_EXT_LOCK_TIMEOUT);
        
        if (request.getContentLength() == 0 && lockTimeOut == null)
        {
            return;
        }

        String decodedUrl = URLDecoder.decode(request.getRequestURI());
        if (decodedUrl.length() > getPathHelper().getAlfrescoContext().length())
        {
            decodedUrl = decodedUrl.substring(getPathHelper().getAlfrescoContext().length() + 1);
        }

        FileInfo resourceFileInfo = getPathHelper().resolvePathFileInfo(decodedUrl);
        NodeRef resourceNodeRef = null;
        
        if (resourceFileInfo != null)
        {
            resourceNodeRef = resourceFileInfo.getNodeRef();
        }
        
        // Does the file already exist (false), or has it been created by this request? 
        boolean newlyCreated = false;
        
        // Office 2008/2011 for Mac
        if (resourceNodeRef == null && lockTimeOut != null)
        {
            try
            {
                final Pair<String, String> parentChild = VtiPathHelper.splitPathParentChild(decodedUrl);
                final FileInfo parent = getPathHelper().resolvePathFileInfo(parentChild.getFirst());
                
                RetryingTransactionCallback<NodeRef> cb = new RetryingTransactionCallback<NodeRef>()
                {
                    @Override
                    public NodeRef execute() throws Throwable
                    {
                        NodeRef result = getFileFolderService().create(parent.getNodeRef(), parentChild.getSecond(), ContentModel.TYPE_CONTENT).getNodeRef();
                        
                        
                        int timeout = Integer.parseInt(lockTimeOut.substring(WebDAV.SECOND.length()));
                        getLockService().lock(result, getUserName(), timeout);
                        
                        return result;
                    }
                };

                resourceNodeRef = getTransactionService().getRetryingTransactionHelper().doInTransaction(cb);
                newlyCreated = true;
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.setHeader(WebDAV.HEADER_LOCK_TOKEN, WebDAV.makeLockToken(resourceNodeRef, getUserName()));
                response.setHeader(DAV_EXT_LOCK_TIMEOUT, lockTimeOut);
            }
            catch (AccessDeniedException e) 
            {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        
        // Check we managed to find the node one way or another
        if (resourceNodeRef == null)
        {
           if(logger.isInfoEnabled())
           {
              logger.info("No node found for resource " + decodedUrl);
           }
           
           // TODO Is this the correct status code to return if they've
           //  tried to query for something that doesn't exist?
           // Or should we return something else, or even create it?
           response.setStatus(HttpServletResponse.SC_NOT_FOUND);
           return;
        }
        
        // Get the working copy of it
        final NodeRef workingCopyNodeRef = getCheckOutCheckInService().getWorkingCopy(resourceNodeRef);
        
        // ALF-15984
        if (!expectedETagForNode(request, resourceNodeRef))
        {
            response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
            return;
        }
        
        // updates changes on the server
        
        UserTransaction tx = getTransactionService().getUserTransaction(false);
        try
        {
            // we don't use RetryingTransactionHelper here cause we can read request input stream only once
            tx.begin();

            ContentWriter writer;

            FileFolderService fileFolderService = getFileFolderService();
            if (workingCopyNodeRef != null)
            {
                if (fileFolderService.isHidden(workingCopyNodeRef))
                {
                    fileFolderService.setHidden(workingCopyNodeRef, false);
                }

                // working copy writer
                writer = getContentService().getWriter(workingCopyNodeRef, ContentModel.PROP_CONTENT, true);
            }
            else
            {
                // ALF-17662, hidden node is the same as non-existed node for SPP
                if (fileFolderService.isHidden(resourceNodeRef))
                {
                    // make it visible for client
                    fileFolderService.setHidden(resourceNodeRef, false);
                }

                // original document writer
                writer = getContentService().getWriter(resourceNodeRef, ContentModel.PROP_CONTENT, true);

            }

            String documentName = getNodeService().getProperty(resourceNodeRef, ContentModel.PROP_NAME).toString();
            String mimetype = getMimetypeService().guessMimetype(documentName);
            writer.setMimetype(mimetype);
            writer.putContent(request.getInputStream());

            // If needed, mark the node as having now had its content supplied
            if (getNodeService().hasAspect(resourceNodeRef, ContentModel.ASPECT_WEBDAV_NO_CONTENT))
            {
                // CLOUD-2209: newly created documents not shown in activity feed.
                newlyCreated = true;
                getNodeService().removeAspect(resourceNodeRef, ContentModel.ASPECT_WEBDAV_NO_CONTENT);
            }
            
            // If we actually have content, it's time to add the versionable aspect and save the current version 
            ContentData contentData = writer.getContentData();
            if (workingCopyNodeRef == null && !getNodeService().hasAspect(resourceNodeRef, ContentModel.ASPECT_VERSIONABLE) && ContentData.hasContent(contentData) && contentData.getSize() > 0)
            {
                getVersionService().createVersion(resourceNodeRef, Collections.<String,Serializable>singletonMap(VersionModel.PROP_VERSION_TYPE, VersionType.MAJOR));
            }
            
            String siteId = davHelper.determineSiteId(getPathHelper().getRootNodeRef(), decodedUrl);
            String tenantDomain = davHelper.determineTenantDomain();
            long fileSize = contentData.getSize();
            reportUploadEvent(decodedUrl, siteId, tenantDomain, newlyCreated, mimetype, fileSize);
            
            tx.commit();
        }
        catch (Exception e)
        {
            try
            {
                tx.rollback();
            }
            catch (Exception tex)
            {
            }
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return;
        }

        // original document properties
        Map<QName, Serializable> props = getNodeService().getProperties(resourceNodeRef);

        if (workingCopyNodeRef != null)
        {
            String workingCopyOwner = getNodeService().getProperty(workingCopyNodeRef, ContentModel.PROP_WORKING_COPY_OWNER).toString();
            if (workingCopyOwner.equals(getAuthenticationService().getCurrentUserName()))
            {
                // working copy properties
                props = getNodeService().getProperties(workingCopyNodeRef);
            }
        }
        String guid = resourceNodeRef.getId().toUpperCase();
        Date lastModified = (Date) props.get(ContentModel.PROP_MODIFIED);
        response.setHeader("Repl-uid", VtiUtils.constructRid(guid));
        response.setHeader("ResourceTag", VtiUtils.constructResourceTag(guid, lastModified));
        response.setHeader("Last-Modified", VtiUtils.formatBrowserDate(lastModified));
        response.setHeader("ETag", VtiUtils.constructETag(guid, lastModified));
        
        ContentReader reader = getContentService().getReader(resourceNodeRef, ContentModel.PROP_CONTENT);
        String mimetype = reader == null ? null : reader.getMimetype();

        if (mimetype != null)
        {
            response.setContentType(mimetype);
        }
    }
    
    // ALF-15984
    /**
     * Check a eTag from request
     * 
     * @param request
     * @param node
     * @return
     */
    private boolean expectedETagForNode(HttpServletRequest request, NodeRef node)
    {
        String ifHeader = request.getHeader(WebDAV.HEADER_IF);
        String reqEtag = null;
        // Extact eTag from request
        if (ifHeader != null && ifHeader.indexOf("[") > -1 && ifHeader.indexOf("]") > -1)
        {
            reqEtag = ifHeader.substring(ifHeader.indexOf("[") + 1, ifHeader.indexOf("]"));
        }
        // Check eTag
        if (reqEtag != null)
        {
            String guid = node.getId().toUpperCase();
            Map<QName, Serializable> props = getNodeService().getProperties(node);
            Date lastModified = (Date) props.get(ContentModel.PROP_MODIFIED);
            // Construct eTag for node. It is based on nodeId and modifyDate of node
            String nodeEtag = VtiUtils.constructETag(guid, lastModified);
            // Compare eTag of request and node eTag
            if (!reqEtag.equalsIgnoreCase(nodeEtag))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * @see org.alfresco.module.vti.handler.MethodHandler#getDocument(java.lang.String, java.lang.String, boolean, java.lang.String, java.util.EnumSet, int)
     */
    public Document getDocument(String serviceName, String documentName, boolean force, String docVersion, EnumSet<GetOption> getOptionSet, int timeout)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Retrieving of document: '" + documentName + "' from site: " + serviceName);
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

        if (getOptionSet.contains(GetOption.none))
        {
            if (docVersion.length() > 0)
            {
                try
                {
                    VersionHistory versionHistory = getVersionService().getVersionHistory(documentFileInfo.getNodeRef());
                    Version version = versionHistory.getVersion(VtiUtils.toAlfrescoVersionLabel(docVersion));
                    NodeRef versionNodeRef = version.getFrozenStateNodeRef();

                    documentFileInfo = getFileFolderService().getFileInfo(versionNodeRef);
                }
                catch (AccessDeniedException e)
                {
                	 throw new VtiHandlerException(VtiHandlerException.OWSSVR_ERRORACCESSDENIED, e);
                }
                catch (RuntimeException e)
                {
                    if (logger.isWarnEnabled())
                    {
                        logger.warn("Version '" + docVersion + "' retrieving exception", e);
                    }

                    // suppress all exceptions and returns the most recent version of the document
                }
            }
        }
        else if (getOptionSet.contains(GetOption.chkoutExclusive) || getOptionSet.contains(GetOption.chkoutNonExclusive))
        {
            try
            {
                // ignore version string parameter
                documentFileInfo = checkout(documentFileInfo, timeout);
            }
            catch (AccessDeniedException e)
            {
            	 throw new VtiHandlerException(VtiHandlerException.OWSSVR_ERRORACCESSDENIED, e);
            }
        }

        Document document = new Document();
        document.setPath(documentName);
        setDocMetaInfo(documentFileInfo, document);
        ContentReader contentReader = getFileFolderService().getReader(documentFileInfo.getNodeRef());
        if (contentReader != null)
        {
            document.setInputStream(contentReader.getContentInputStream());
        }
        else
        {
            // commons-io 1.1 haven't ClosedInputStream
            document.setInputStream(new ByteArrayInputStream(new byte[0]));
        }

        return document;
    }

    /**
     * @see org.alfresco.module.vti.handler.MethodHandler#getListDocuments(java.lang.String, boolean, boolean, java.lang.String, java.lang.String, boolean, boolean, boolean,
     *      boolean, boolean, boolean, boolean, boolean, java.util.Map, boolean)
     */
    public DocsMetaInfo getListDocuments(String serviceName, boolean listHiddenDocs, boolean listExplorerDocs, String platform, String initialURL, boolean listRecurse,
            boolean listLinkInfo, boolean listFolders, boolean listFiles, boolean listIncludeParent, boolean listDerived, boolean listBorders, boolean validateWelcomeNames,
            Map<String, Object> folderList, boolean listChildWebs) throws VtiHandlerException
    {
        // listHiddenDocs ignored
        // listExplorerDocs ignored

        DocsMetaInfo result = new DocsMetaInfo();

        FileInfo folderFileInfo = getPathHelper().resolvePathFileInfo(serviceName + "/" + initialURL);
        AlfrescoMethodHandler.assertValidFileInfo(folderFileInfo);
        AlfrescoMethodHandler.assertFolder(folderFileInfo);
        FileInfo sourceFileInfo = folderFileInfo;

        // show the list of sites that user is member
        if (serviceName.equals(""))
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Generating the list of sites the user is member of.");
            }
            // gets the list of sites that user is member
            List<SiteInfo> sites = siteService.listSites(getAuthenticationService().getCurrentUserName());
            for (SiteInfo site : sites)
            {
                FileInfo siteFileInfo = getFileFolderService().getFileInfo(site.getNodeRef());

                result.getFolderMetaInfoList().add(buildDocMetaInfo(siteFileInfo, folderList));
            }
        }
        else
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Generating list of items under site: " + serviceName + " and initialURL: " + initialURL);
            }
            // we are already in site (lists files and/or folders)
            if (listFolders)
            {
                for (FileInfo folder : getFileFolderService().listFolders(sourceFileInfo.getNodeRef()))
                {
                    result.getFolderMetaInfoList().add(buildDocMetaInfo(folder, folderList));
                }
            }

            if (listFiles)
            {
                for (FileInfo file : getFileFolderService().listFiles(sourceFileInfo.getNodeRef()))
                {
                    if (file.isLink() == false)
                    {
                        // ALF-17662, working copies and hidden documents are not visible
                        if (getNodeService().hasAspect(file.getNodeRef(), ContentModel.ASPECT_WORKING_COPY) == false
                                && !getFileFolderService().isHidden(file.getNodeRef()))
                        {
                            result.getFileMetaInfoList().add(buildDocMetaInfo(file, folderList));
                        }
                    }
                }
            }
        }

        if (listIncludeParent)
        {
            result.getFolderMetaInfoList().add(buildDocMetaInfo(sourceFileInfo, folderList));
        }

        return result;
    }

    /**
     * @see org.alfresco.module.vti.handler.MethodHandler#uncheckOutDocument(java.lang.String, java.lang.String, boolean, java.util.Date, boolean, boolean)
     */
    public DocMetaInfo uncheckOutDocument(String serviceName, String documentName, boolean force, Date timeCheckedOut, boolean rlsshortterm, boolean validateWelcomeNames)
    {
        // force ignored
        // timeCheckedOut ignored

        if (logger.isDebugEnabled())
        {
            logger.debug("Unchecked out document: '" + documentName + "' from site: " + serviceName);
        }
        FileInfo fileFileInfo = getPathHelper().resolvePathFileInfo(serviceName + "/" + documentName);
        AlfrescoMethodHandler.assertValidFileInfo(fileFileInfo);
        AlfrescoMethodHandler.assertFile(fileFileInfo);
        FileInfo documentFileInfo = fileFileInfo;

        DocumentStatus documentStatus = getDocumentHelper().getDocumentStatus(documentFileInfo.getNodeRef());

        // if document isn't checked out then throw exception
        if (VtiDocumentHelper.isCheckedout(documentStatus) == false)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Document not already checked out!!!");
            }
            documentFileInfo = getFileFolderService().getFileInfo(documentFileInfo.getNodeRef());

            DocMetaInfo docMetaInfo = new DocMetaInfo(false);
            docMetaInfo.setPath(documentName);
            setDocMetaInfo(documentFileInfo, docMetaInfo);

            return docMetaInfo;
        }

        // if document is checked out, but user isn't owner, then throw exception
        if (VtiDocumentHelper.isCheckoutOwner(documentStatus) == false)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Not an owner!!!");
            }
            throw new VtiHandlerException(VtiHandlerException.DOC_CHECKED_OUT);
        }

        UserTransaction tx = getTransactionService().getUserTransaction(false);
        try
        {
            tx.begin();

            if (rlsshortterm)
            {
                // try to release short-term checkout
                // if user have long-term checkout then skip releasing short-term checkout
                if (documentStatus.equals(DocumentStatus.LONG_CHECKOUT_OWNER) == false)
                {
                    getLockService().unlock(documentFileInfo.getNodeRef());
                }
            }
            else
            {
                // try to cancel long-term checkout
                NodeRef workingCopyNodeRef = getCheckOutCheckInService().getWorkingCopy(documentFileInfo.getNodeRef());
                getCheckOutCheckInService().cancelCheckout(workingCopyNodeRef);
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

            if ((e instanceof VtiHandlerException) == false)
            {
                throw new VtiHandlerException(VtiHandlerException.DOC_NOT_CHECKED_OUT);
            }

            throw VtiExceptionUtils.createRuntimeException(e);
        }

        // refresh file info for current document
        documentFileInfo = getFileFolderService().getFileInfo(documentFileInfo.getNodeRef());

        DocMetaInfo docMetaInfo = new DocMetaInfo(false);
        docMetaInfo.setPath(documentName);
        setDocMetaInfo(documentFileInfo, docMetaInfo);

        return docMetaInfo;
    }

    /**
     * @see org.alfresco.module.vti.handler.MethodHandler#getFileOpen(java.lang.String, java.lang.String, java.util.List, java.lang.String,
     *      org.alfresco.module.vti.metadata.dic.VtiSortField, org.alfresco.module.vti.metadata.dic.VtiSort, java.lang.String)
     */
    public DialogsMetaInfo getFileOpen(String siteUrl, String location, List<String> fileDialogFilterValue,
            String rootFolder, VtiSortField sortField, VtiSort sortDir, String view)
    {
        if (siteUrl.equals(""))
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Generating list of sites");
            }

            DialogsMetaInfo result = new DialogsMetaInfo();

            List<SiteInfo> sites = siteService.listSites(getAuthenticationService().getCurrentUserName());

            for (SiteInfo site : sites)
            {
                result.getDialogMetaInfoList().add(getDialogMetaInfo(getFileFolderService().getFileInfo(site.getNodeRef())));
            }

            if (logger.isDebugEnabled())
            {
                logger.debug("Retrieved " + result.getDialogMetaInfoList().size() + " sites");
            }

            return result;
        }
        else
        {
            return super.getFileOpen(siteUrl, location, fileDialogFilterValue, rootFolder, sortField, sortDir, view);
        }
    }

    /**
     * Build DocMetaInfo for getListDocuments method
     * 
     * @param fileInfo file info ({@link FileInfo})
     * @param folderList dates list
     * @return DocMetaInfo DocMetaInfo
     */
    private DocMetaInfo buildDocMetaInfo(FileInfo fileInfo, Map<String, Object> folderList)
    {
        boolean isModified = false;

        String path = getPathHelper().toUrlPath(fileInfo);

        Date cacheDate = (Date) folderList.get(path);
        Date srcDate = fileInfo.getModifiedDate();

        if (cacheDate == null || srcDate.after(cacheDate))
        {
            isModified = true;
        }

        DocMetaInfo docMetaInfo = new DocMetaInfo(fileInfo.isFolder());

        if (isModified)
        {
            setDocMetaInfo(fileInfo, docMetaInfo);
        }

        docMetaInfo.setPath(path);

        if (getDictionaryService().isSubClass(getNodeService().getType(fileInfo.getNodeRef()), SiteModel.TYPE_SITE))
        {
            docMetaInfo.setIschildweb("true");
        }

        return docMetaInfo;
    }
    
    /**
     * TODO: may be expanded to a proper event handler with listener registration.
     */
    protected void reportUploadEvent(String path, String siteId, String tenantDomain,
                                     boolean newlyCreated, String mimeType, long fileSize)
                throws WebDAVServerException
    {
        postActivity(path, siteId, tenantDomain, newlyCreated);
    }
    
    /**
     * Create an activity post.
     * 
     * @throws WebDAVServerException 
     */
    protected void postActivity(String path, String siteId, String tenantDomain, boolean newlyCreated)
                throws WebDAVServerException
    {
        if (!davService.activitiesEnabled())
        {
            // Don't post activities if this behaviour is disabled.
            return;
        }
        
        if (siteId.equals(WebDAVHelper.EMPTY_SITE_ID))
        {
            // There is not enough information to publish site activity.
            return;
        }
        
        FileInfo contentNodeInfo = null;
        try
        {
            contentNodeInfo = davHelper.getNodeForPath(getPathHelper().getRootNodeRef(), path);
            NodeRef nodeRef = contentNodeInfo.getNodeRef();
            // Don't post activity data for hidden files, resource forks etc.
            if (!getNodeService().hasAspect(nodeRef, ContentModel.ASPECT_HIDDEN))
            {
                if (newlyCreated)
                {
                    // file added
                    activityPoster.postFileFolderAdded(siteId, tenantDomain, null, contentNodeInfo);
                }
                else
                {
                    // file updated
                    activityPoster.postFileFolderUpdated(siteId, tenantDomain, contentNodeInfo);
                }
            }
        }
        catch (FileNotFoundException error)
        {
            throw new WebDAVServerException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }        
    }

    @Override
    public void setActivityPoster(WebDAVActivityPoster activityPoster)
    {
        this.activityPoster = activityPoster;
    }

    public void setDavService(WebDavService davService)
    {
        this.davService = davService;
    }

    public void setDavHelper(WebDAVHelper davHelper)
    {
        this.davHelper = davHelper;
    }

    public WebDAVHelper getDavHelper()
    {
        return this.davHelper;
    }
}
