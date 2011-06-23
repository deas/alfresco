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
package org.alfresco.module.vti.handler.alfresco.v3;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoMethodHandler;
import org.alfresco.module.vti.handler.alfresco.VtiDocumentHepler;
import org.alfresco.module.vti.handler.alfresco.VtiExceptionUtils;
import org.alfresco.module.vti.handler.alfresco.VtiUtils;
import org.alfresco.module.vti.metadata.dic.DocumentStatus;
import org.alfresco.module.vti.metadata.dic.GetOption;
import org.alfresco.module.vti.metadata.dic.VtiError;
import org.alfresco.module.vti.metadata.model.DocMetaInfo;
import org.alfresco.module.vti.metadata.model.DocsMetaInfo;
import org.alfresco.module.vti.metadata.model.Document;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionHistory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.FileCopyUtils;

/**
 * Alfresco implementation of MethodHandler and AbstractAlfrescoMethodHandler
 * 
 * @author PavelYur
 */
public class AlfrescoMethodHandler extends AbstractAlfrescoMethodHandler
{
    private final static Log logger = LogFactory.getLog(AlfrescoMethodHandler.class);

    private SiteService siteService;
    private AuthenticationComponent authenticationComponent;
    private ShareUtils shareUtils;

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
        return AuthenticationUtil.runAs(new RunAsWork<String[]>()
        {

            public String[] doWork() throws Exception
            {
                if (!url.startsWith(alfrescoContext))
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Url must start with alfresco context.");
                    }
                    throw new VtiHandlerException(VtiError.V_BAD_URL);
                }

                if (url.equalsIgnoreCase(alfrescoContext))
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("WebUrl: " + alfrescoContext + ", fileUrl: ''");
                    }
                    return new String[] { alfrescoContext, "" };
                }

                String webUrl = "";
                String fileUrl = "";

                String[] splitPath = url.replaceAll(alfrescoContext, "").substring(1).split("/");

                StringBuilder tempWebUrl = new StringBuilder();

                for (int i = splitPath.length; i > 0; i--)
                {
                    tempWebUrl.delete(0, tempWebUrl.length());

                    for (int j = 0; j < i; j++)
                    {
                        if (j < i - 1)
                        {
                            tempWebUrl.append(splitPath[j] + "/");
                        }
                        else
                        {
                            tempWebUrl.append(splitPath[j]);
                        }
                    }

                    FileInfo fileInfo = getPathHelper().resolvePathFileInfo(tempWebUrl.toString());

                    if (fileInfo != null)
                    {
                        if (getDictionaryService().isSubClass(getNodeService().getType(fileInfo.getNodeRef()), SiteModel.TYPE_SITE))
                        {
                            webUrl = alfrescoContext + "/" + tempWebUrl;
                            if (url.replaceAll(webUrl, "").startsWith("/"))
                            {
                                fileUrl = url.replaceAll(webUrl, "").substring(1);
                            }
                            else
                            {
                                fileUrl = url.replaceAll(webUrl, "");
                            }
                            if (logger.isDebugEnabled())
                            {
                                logger.debug("WebUrl: " + webUrl + ", fileUrl: '" + fileUrl + "'");
                            }
                            return new String[] { webUrl, fileUrl };
                        }
                    }
                }
                if (webUrl.equals(""))
                {
                    throw new VtiHandlerException(VtiError.V_BAD_URL);
                }
                return new String[] { webUrl, fileUrl };
            }

        }, authenticationComponent.getSystemUserName());
    }

    /**
     * @see org.alfresco.module.vti.handler.MethodHandler#existResource(java.lang.String, javax.servlet.http.HttpServletResponse)
     */
    public boolean existResource(String uri, HttpServletResponse response)
    {
        FileInfo resourceFileInfo = getPathHelper().resolvePathFileInfo(uri);
        if (resourceFileInfo == null)
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
                ContentData contentData = (ContentData) getNodeService().getProperty(resourceNodeRef, ContentModel.PROP_CONTENT);
                
                NodeRef workingCopyNodeRef = getCheckOutCheckInService().getWorkingCopy(resourceNodeRef);
                if (workingCopyNodeRef != null)
                {
                    String workingCopyOwner = getNodeService().getProperty(workingCopyNodeRef, ContentModel.PROP_WORKING_COPY_OWNER).toString();
                    if (workingCopyOwner.equals(getAuthenticationService().getCurrentUserName()))
                    {
                        // allow to see changes in document after it was checked out (only for checked out owner)
                        contentData = (ContentData) getNodeService().getProperty(workingCopyNodeRef, ContentModel.PROP_CONTENT);
                        resourceNodeRef = workingCopyNodeRef;
                    }
                }
                
                response.setContentType(contentData.getMimetype());
                ContentReader reader = getFileFolderService().getReader(resourceNodeRef);
                try
                {
                    FileCopyUtils.copy(reader.getContentInputStream(), response.getOutputStream());
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
        return resourceFileInfo != null;
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
        
        String role = siteService.getMembersRole(serviceName, getAuthenticationService().getCurrentUserName());
        if (role.equals(SiteModel.SITE_CONSUMER))
        {
            throw new VtiHandlerException(VtiHandlerException.OWSSVR_ERRORACCESSDENIED);
        }

        FileInfo fileFileInfo = getPathHelper().resolvePathFileInfo(serviceName + "/" + documentName);
        AlfrescoMethodHandler.assertValidFileInfo(fileFileInfo);
        AlfrescoMethodHandler.assertFile(fileFileInfo);

        String author = (String) getNodeService().getProperty(fileFileInfo.getNodeRef(), ContentModel.PROP_CREATOR);

        if (role.equals(SiteModel.SITE_CONTRIBUTOR) && !author.equals(getAuthenticationService().getCurrentUserName()))
        {
            throw new VtiHandlerException(VtiHandlerException.OWSSVR_ERRORACCESSDENIED);
        }
        
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
                    throw e;
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
                // open document in read-only mode without cheking out (in case if user open content of other user)
                Document document = new Document();
                document.setPath(documentName);
                ContentReader contentReader = getFileFolderService().getReader(documentFileInfo.getNodeRef());
                document.setInputStream(contentReader.getContentInputStream());
                return document;
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
                        if (getNodeService().hasAspect(file.getNodeRef(), ContentModel.ASPECT_WORKING_COPY) == false)
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
        if (VtiDocumentHepler.isCheckedout(documentStatus) == false)
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
        if (VtiDocumentHepler.isCheckoutOwner(documentStatus) == false)
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
}
