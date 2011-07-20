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

package org.alfresco.repo.lotus.ws.impl.helper;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.NamespaceService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.extensions.surf.util.AbstractLifecycleBean;

/**
 * @author PavelYur
 */
public class AlfrescoQuickrPathHelper extends AbstractLifecycleBean
{

    private static Log logger = LogFactory.getLog(AlfrescoQuickrPathHelper.class);

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    public static DatatypeFactory datatypeFactory;

    private Map<String, String> containersMapping;

    public FileFolderService fileFolderService;

    public void setFileFolderService(FileFolderService fileFolderService)
    {
        this.fileFolderService = fileFolderService;
    }

    static
    {
        try
        {
            datatypeFactory = DatatypeFactory.newInstance();
        }
        catch (DatatypeConfigurationException e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Datatype factory was not properly initialized!!!");
            }
        }
    }

    private NodeRef rootNodeRef;
    private String rootDisplayPath;

    private String rootPath;
    private StoreRef libraryStoreRef;
    private String lotusUrl;
    private String shareDocumentUrl;
    private String shareFolderUrl;
    private String shareSiteUrl;
    private String shareSiteContainerUrl;
    private String shareUserDashboardUrl;

    private NodeService nodeService;
    private SearchService searchService;
    private NamespaceService namespaceService;
    private AuthenticationComponent authenticationComponent;
    private PermissionService permissionService;
    private CheckOutCheckInService checkOutCheckInService;
    private SysAdminParams sysAdminParams;

    public void setRootPath(String rootPath)
    {
        this.rootPath = rootPath;
    }

    public void setLibraryStoreRef(StoreRef libraryStoreRef)
    {
        this.libraryStoreRef = libraryStoreRef;
    }

    public void setLotusUrl(String lotusUrl)
    {
        this.lotusUrl = lotusUrl;
    }

    public String getLotusUrl()
    {
        return lotusUrl;
    }

    public void setShareDocumentUrl(String shareDocumentUrl)
    {
        this.shareDocumentUrl = shareDocumentUrl;
    }

    public void setShareFolderUrl(String shareFolderUrl)
    {
        this.shareFolderUrl = shareFolderUrl;
    }

    public void setShareSiteUrl(String shareSiteUrl)
    {
        this.shareSiteUrl = shareSiteUrl;
    }

    public void setShareSiteContainerUrl(String shareSiteContainerUrl)
    {
        this.shareSiteContainerUrl = shareSiteContainerUrl;
    }

    public void setShareUserDashboardUrl(String shareUserDashboardUrl)
    {
        this.shareUserDashboardUrl = shareUserDashboardUrl;
    }
    
    public void setContainersMapping(Map<String, String> containersMapping)
    {
        this.containersMapping = containersMapping;
    }

    public StoreRef getLibraryStoreRef()
    {
        return libraryStoreRef;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }

    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }

    public void setAuthenticationComponent(AuthenticationComponent authenticationComponent)
    {
        this.authenticationComponent = authenticationComponent;
    }

    public void setPermissionService(PermissionService permissionService)
    {
        this.permissionService = permissionService;
    }

    public void setCheckOutCheckInService(CheckOutCheckInService checkOutCheckInService)
    {
        this.checkOutCheckInService = checkOutCheckInService;
    }

    public void setSysAdminParams(SysAdminParams sysAdminParams)
    {
        this.sysAdminParams = sysAdminParams;
    }

    @Override
    protected void onBootstrap(ApplicationEvent event)
    {
        rootNodeRef = AuthenticationUtil.runAs(new RunAsWork<NodeRef>()
        {
            public NodeRef doWork() throws Exception
            {
                if (nodeService.exists(libraryStoreRef) == false)
                {
                    throw new RuntimeException("No store for path: " + libraryStoreRef);
                }

                NodeRef storeRootNodeRef = nodeService.getRootNode(libraryStoreRef);

                List<NodeRef> nodeRefs = searchService.selectNodes(storeRootNodeRef, rootPath, null, namespaceService, false);

                if (nodeRefs.size() > 1)
                {
                    throw new RuntimeException("Multiple possible roots for : \n" + "   root path: " + rootPath + "\n" + "   results: " + nodeRefs);
                }
                else if (nodeRefs.size() == 0)
                {
                    throw new RuntimeException("No root found for : \n" + "   root path: " + rootPath);
                }
                else
                {
                    return nodeRefs.get(0);
                }
            }
        }, authenticationComponent.getSystemUserName());

        rootDisplayPath = AuthenticationUtil.runAs(new RunAsWork<String>()
        {
            public String doWork() throws Exception
            {
                return nodeService.getPath(rootNodeRef).toDisplayPath(nodeService, permissionService) + "/" + nodeService.getProperty(rootNodeRef, ContentModel.PROP_NAME);
            }
        }, authenticationComponent.getSystemUserName());
    }

    @Override
    protected void onShutdown(ApplicationEvent event)
    {
        // do nothing
    }

    /**
     * @param nodeRef NodeRef of the document/folder.
     * @param isRelative if true then returned path will be relative
     * @return Path to the document/folder.
     */
    public String getNodePath(NodeRef nodeRef, boolean isRelative)
    {
        String urlPath;
        if (nodeRef.equals(rootNodeRef))
        {
            urlPath = rootDisplayPath;
        }
        else
        {
            StringBuilder builder = new StringBuilder(nodeService.getPath(nodeRef).toDisplayPath(nodeService, permissionService));
            builder.append("/");
            if (isRelative)
            {
                builder.delete(0, rootDisplayPath.length() + 1);
            }

            String nodeName = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
            builder.append(nodeName);
            urlPath = builder.toString();
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Convert " + nodeRef + " to url path '" + urlPath + "'");
        }

        return urlPath;
    }

    private NodeRef getNodeRef(NodeRef parentNodeRef, String nodePath) throws FileNotFoundException
    {
        nodePath = resolveNodePath(nodePath, parentNodeRef != null);
        
        if (parentNodeRef == null)
        {
            parentNodeRef = rootNodeRef;
        }

        FileInfo fileInfo = null;

        if (nodePath.length() == 0)
        {
            fileInfo = fileFolderService.getFileInfo(parentNodeRef);
        }
        else
        {
            List<String> splitPath = Arrays.asList(nodePath.split("/"));
            fileInfo = fileFolderService.resolveNamePath(parentNodeRef, splitPath);
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Resolved file info for '" + nodePath + "' is " + fileInfo);
        }

        return fileInfo.getNodeRef();
    }

    /**
     * Return path relative to provided parentNodeRef. If parentNodeRef is null, the path relative to quickr root will be returned.
     * 
     * @param isRelative true if provided path is relative
     * @param path relative node path
     * @return
     */
    public String resolveNodePath(String path, boolean isRelative)
    {
        if (isRelative == false)
        {
            path = path.replaceFirst(rootDisplayPath, "");
        }

        path = path.replaceAll("//", "/");
        if (path.startsWith("/"))
            path = path.substring(1);
        if (path.endsWith("/"))
            path = path.substring(0, path.length() - 1);
        return path;
    }

    public NodeRef getRootNodeRef()
    {
        return rootNodeRef;
    }

    public XMLGregorianCalendar getXmlDate(Date date)
    {
        if (date == null)
        {
            return null;
        }
        return datatypeFactory.newXMLGregorianCalendar(dateFormat.format(date));
    }

    /**
     * @return url of share application document details page.
     */
    public String getShareDocumentUrl()
    {
        return sysAdminParams.getShareProtocol() + "://" + sysAdminParams.getShareHost() + ":" + sysAdminParams.getSharePort() + "/" + sysAdminParams.getShareContext()
                + shareDocumentUrl;
    }

    /**
     * @return url of share application folder page.
     */
    public String getShareFolderUrl()
    {
        return sysAdminParams.getShareProtocol() + "://" + sysAdminParams.getShareHost() + ":" + sysAdminParams.getSharePort() + "/" + sysAdminParams.getShareContext()
                + shareFolderUrl;
    }

    public String getShareSiteUrl()
    {
        return sysAdminParams.getShareProtocol() + "://" + sysAdminParams.getShareHost() + ":" + sysAdminParams.getSharePort() + "/" + sysAdminParams.getShareContext()
                + shareSiteUrl;
    }

    public String getShareSiteContainerUrl()
    {
        return sysAdminParams.getShareProtocol() + "://" + sysAdminParams.getShareHost() + ":"
                + sysAdminParams.getSharePort() + "/" + sysAdminParams.getShareContext() + shareSiteContainerUrl;
    }

    public String getShareUserDashboardUrl()
    {
        return sysAdminParams.getShareProtocol() + "://" + sysAdminParams.getShareHost() + ":"
                + sysAdminParams.getSharePort() + "/" + sysAdminParams.getShareContext() + shareUserDashboardUrl;
    }

    /**
     * Resolve NodeRef using id and path. If only an id is provided, the id must be the uuid of the document. If only a path is provided, that path must be the absolute path to the
     * document. If an id and path are both provided, the id must be the uuid of a folder that is a parent to the document and the path must be the relative path from the parent
     * folder.
     * 
     * @param id The uuid of the document if no path is provided. If both id and path are provided, the id must be the uuid of the parent folder to the document.
     * @param path The absolute path to the document if no id is provided. If both id and path are provided, the path must be the relative path from the id provided.
     * @return NodeRef of the document.
     * @throws FileNotFoundException
     */
    public NodeRef resolveNodeRef(String id, String path) throws FileNotFoundException
    {
        if (id != null)
        {
            if (path == null)
            {
                NodeRef nodeRef = new NodeRef(libraryStoreRef, id);
                if (!nodeService.exists(nodeRef))
                {
                    throw new FileNotFoundException(nodeRef);
                }
                return nodeRef;
            }
            else
            {
                NodeRef parent = new NodeRef(libraryStoreRef, id);
                return getNodeRef(parent, path);
            }
        }
        else
        {
            if (path != null)
            {
                return getNodeRef(null, path);
            }
            else
            {
                throw new FileNotFoundException("id=null  path=null");
            }
        }
    }

    /**
     * Return the name of site where given content is stored.
     * 
     * @param nodeRef the NodeRef of the content
     * @return
     */
    public String getNodeRefSiteName(NodeRef nodeRef)
    {
        boolean found = false;
        NodeRef currentNodeRef = nodeRef;
        String siteName = null;

        while (!found)
        {
            NodeRef parentNodeRef = nodeService.getPrimaryParent(currentNodeRef).getParentRef();

            if (nodeService.getType(parentNodeRef).equals(SiteModel.TYPE_SITE))
            {
                siteName = (String) nodeService.getProperty(parentNodeRef, ContentModel.PROP_NAME);
                found = true;
            }
            else
            {
                currentNodeRef = parentNodeRef;
            }
        }

        return siteName;
    }

    /**
     * Return working copy of document. If working copy doesn't exist return original document.
     * 
     * @param nodeRef
     * @return
     */
    public NodeRef getDocumentForWork(NodeRef nodeRef)
    {

        if (isInRmSite(nodeRef))
        {
            return nodeRef;
        }
        NodeRef workingCopy = checkOutCheckInService.getWorkingCopy(nodeRef);

        if (workingCopy != null)
        {
            return workingCopy;
        }

        return nodeRef;
    }

    /**
     * Return original document for provided workink copy. If provided nodeRef is not working copy return it.
     * 
     * @param nodeRef
     * @return
     */
    public NodeRef getOriginalDocument(NodeRef nodeRef)
    {
        if (isInRmSite(nodeRef))
        {
            return nodeRef;
        }
        NodeRef checkedOutNodeRef = checkOutCheckInService.getCheckedOut(nodeRef);
        if (checkedOutNodeRef == null)
        {
            return nodeRef;
        }
        else
        {
            return checkedOutNodeRef;
        }
    }

    /**
     * Check, if provided content lie in Records Management site.
     * 
     * @param nodeRef NodeRef
     * @return true if Node is in Records Management site.
     */
    public boolean isInRmSite(NodeRef nodeRef)
    {
        NodeRef parent = nodeService.getPrimaryParent(nodeRef).getParentRef();
        while (parent != null && !nodeService.getType(parent).equals(SiteModel.TYPE_SITE))
        {
            nodeRef = parent;
            parent = nodeService.getPrimaryParent(nodeRef).getParentRef();
        }

        if (parent == null || nodeService.getType(nodeRef).equals(ContentModel.TYPE_FOLDER))
        {
            return false;
        }
        return true;
    }

    /**
     * Find the site where document is located
     * 
     * @param documentNodeRef the document's nodeRef
     * @return the name of the site where document is located
     */
    public String getSiteNameForDocument(NodeRef documentNodeRef)
    {
        String siteName = null;

        NodeRef siteNodeRef = documentNodeRef;

        boolean founded = false;

        // find site
        while (!founded && (siteNodeRef != null && nodeService.getPrimaryParent(siteNodeRef) != null))
        {
            siteNodeRef = nodeService.getPrimaryParent(siteNodeRef).getParentRef();

            if (siteNodeRef != null && nodeService.getType(siteNodeRef).equals(SiteModel.TYPE_SITE))
            {
                founded = true;
            }
        }

        if (founded)
        {
            // get site short name
            siteName = (String) nodeService.getProperty(siteNodeRef, ContentModel.PROP_NAME);
        }

        return siteName;

    }

    /**
     * Check if provided "child" is real child of provided "parent".
     * 
     * @param child child node to chek.
     * @param parent supposed parent.
     * @return
     */
    public boolean isChild(NodeRef child, NodeRef parent)
    {
        boolean result = false;

        while (!result && (child != null && nodeService.getPrimaryParent(child) != null))
        {
            child = nodeService.getPrimaryParent(child).getParentRef();

            if (parent.equals(child))
            {
                result = true;
            }
        }

        return result;
    }

    /**
     * Generate a unique name that can be used to create a child of the given parentNode.
     * 
     * @param topicFolderRef parentNode
     * @return unique name that can be used to create a child of the given parentNode.
     */
    public String getUniqueName(NodeRef folderRef)
    {
        String name = "post-" + System.currentTimeMillis();
        String finalName = name + "_" + (int) Math.floor(Math.random() * 1000);
        int count = 0;
        while (nodeService.getChildByName(folderRef, ContentModel.ASSOC_CONTAINS, finalName) != null && count < 100)
        {
            finalName = name + "_" + (int) Math.floor(Math.random() * 1000);
            count++;
        }
        return finalName;
    }
    
    /**
     * Returns the url suffix for site container
     * 
     * @param containerName name of the site container
     * @return url suffix
     */
    public String getContainerUrl(String containerName)
    {
        return containersMapping.get(containerName);
    }
}
