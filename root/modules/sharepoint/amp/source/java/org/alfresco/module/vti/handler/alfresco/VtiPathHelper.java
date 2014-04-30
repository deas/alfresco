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

import java.io.Serializable;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.extensions.surf.util.AbstractLifecycleBean;
import org.springframework.extensions.surf.util.URLDecoder;
import org.springframework.util.StringUtils;

/**
 * Helper for AlfrescoVtiMethodHandler. Help for path resolving and url formatting
 *
 * @author Dmitry Lazurkin
 *
 */
public class VtiPathHelper extends AbstractLifecycleBean
{
    private final static Log logger = LogFactory.getLog("org.alfresco.module.vti.handler");
    public final static String ALTERNATE_PATH_DOCUMENT_IDENTIFICATOR = "_IDX_NODE_";
    public final static String ALTERNATE_PATH_SITE_IDENTIFICATOR = "_IDX_SITE_";

    private NodeService nodeService;
    private FileFolderService fileFolderService;
    private PermissionService permissionService;
    private SearchService searchService;
    private NamespaceService namespaceService;
    private PersonService personService;
    private TenantService tenantService;
    private DictionaryService dictionaryService;
    private AuthenticationComponent authenticationComponent;
    private SimpleCache<String, NodeRef> singletonCache; // eg. for vtiRootNodeRef
    private static final String KEY_VTI_ROOT_NODEREF = "key.vtiRoot.noderef";
    
    private String rootPath;
    private String storePath;
    private String urlPathPrefix;

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
     * Set node service
     * 
     * @param nodeService the node service to set ({@link NodeService})
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * Set file-folder service
     * 
     * @param fileFolderService the file-folder service to set ({@link FileFolderService})
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
     * Set search service
     * 
     * @param searchService the search service to set ({@link SearchService})
     */
    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }

    /**
     * Set root path
     * 
     * @param rootPath the root path to set
     */
    public void setRootPath(String rootPath)
    {
        this.rootPath = rootPath;
    }

    /**
     * Set store path
     * 
     * @param storePath the store path to set
     */
    public void setStorePath(String storePath)
    {
        this.storePath = storePath;
    }
    
    /**
     * @param urlPathPrefix the urlPathPrefix to set
     */
    public void setUrlPathPrefix(String urlPathPrefix)
    {
        this.urlPathPrefix = urlPathPrefix;
    }

    /**
     * Get alfresco context
     * 
     * @return alfresco context
     */
    public String getAlfrescoContext()
    {
        if (urlPathPrefix.equals("/"))
        {
            return "";
        }
        return urlPathPrefix;
    }
    
    /**
     * Set namespace service
     * 
     * @param namespaceService the namespace service to set ({@link NamespaceService})
     */
    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }

    /**
     * Resolve file info for file with URL path
     *
     * @param initialURL URL path
     * @return FileInfo file info null if file or folder doesn't exist
     */
    public FileInfo resolvePathFileInfo(String initialURL)
    {
        initialURL = removeSlashes(initialURL);
        
        NodeRef rootNodeRef = getRootNodeRef();
        
        FileInfo fileInfo = null;

        if (initialURL.length() == 0)
        {
            fileInfo = fileFolderService.getFileInfo(rootNodeRef);
        }
        else
        {
            try
            {
                String alternateIdentificator = ALTERNATE_PATH_SITE_IDENTIFICATOR;
                if (initialURL.contains(ALTERNATE_PATH_DOCUMENT_IDENTIFICATOR))
                {
                    alternateIdentificator = ALTERNATE_PATH_DOCUMENT_IDENTIFICATOR;
                }
                if (initialURL.contains(alternateIdentificator))
                {
                    String[] parts = initialURL.split("/");
                    for (int i = 0; i < parts.length; i++)
                    {
                        if (parts[i].contains(alternateIdentificator))
                        {
                            fileInfo = getFileFolderService().getFileInfo(new NodeRef(rootNodeRef.getStoreRef(), parts[i].replace(alternateIdentificator, "")));
                            break;
                        }
                    }
                }
            }
            catch (InvalidNodeRefException e)
            {
            }
            
            if (fileInfo == null)
            {
                try
                {
                    List<String> splitPath = Arrays.asList(initialURL.split("/"));
                    fileInfo = fileFolderService.resolveNamePath(rootNodeRef, splitPath);
                }
                catch (FileNotFoundException e)
                {
                }
            }
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Resolved file info for '" + initialURL + "' is " + fileInfo);
        }

        return fileInfo;
    }

    /**
     * Resolves file info for file with URL path in parent directory with file info
     *
     * @param parentFileInfo file info for parent directory ({@link FileInfo})
     * @param childName url path relative to parent directory
     * @return FileInfo resolved file info for childName or null, if it doesn't exist
     */
    public FileInfo resolvePathFileInfo(FileInfo parentFileInfo, String childName)
    {
        FileInfo childFileInfo = null;

        if(!parentFileInfo.isFolder())
        {
            //Alternate path was used and provided parentFileInfo is file that we are editing
            return parentFileInfo;
        }
        
        try
        {
            childFileInfo = fileFolderService.resolveNamePath(parentFileInfo.getNodeRef(), Collections.singletonList(childName));
        }
        catch (FileNotFoundException e)
        {
        }

        return childFileInfo;
    }

    /**
     * Split URL path to document name and path to parent folder of that document
     *
     * @param path URL path
     * @return Pair<String, String> first item of pair - path to parent folder, second item - document name
     */
    public static Pair<String, String> splitPathParentChild(String path)
    {
        path = removeSlashes(path);
        
        int indexOfName = path.lastIndexOf("/");

        String name = path.substring(indexOfName + 1);
        String parent = "";

        if (indexOfName != -1)
        {
            parent = path.substring(0, indexOfName);
        }

        return new Pair<String, String>(parent, name);
    }

    /**
     * Format FrontPageExtension URL path from file information
     *
     * @param fileInfo file information ({@link FileInfo})
     * @return URL path
     */
    public String toUrlPath(FileInfo fileInfo)
    {
        String urlPath;
        NodeRef rootNodeRef = getRootNodeRef();
        if (fileInfo.getNodeRef().equals(rootNodeRef))
        {
            urlPath = "";
        }
        else
        {
            StringBuilder builder = new StringBuilder(nodeService.getPath(fileInfo.getNodeRef()).toDisplayPath(nodeService, permissionService));
            builder.delete(0, nodeService.getPath(rootNodeRef).toDisplayPath(nodeService, permissionService).length() +
                    ((String) nodeService.getProperty(rootNodeRef, ContentModel.PROP_NAME)).length() + 1);
            if (builder.length() != 0)
            {
                builder.deleteCharAt(0);
                builder.append("/");
            }
            builder.append(fileInfo.getName());
            urlPath = builder.toString();
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Convert " + fileInfo + " to url path '" + urlPath + "'");
        }

        return urlPath;
    }

    /**
     * Get home space for current user
     * 
     * @return String url to home space
     */
    public String getUserHomeLocation()
    {
        NodeRef currentUser = personService.getPerson(authenticationComponent.getCurrentUserName());
        
        if (currentUser == null)
        {
            throw new AuthenticationException("No user have been authorized.");
        }
        
        NodeRef homeSpace = (NodeRef) nodeService.getProperty(currentUser, ContentModel.PROP_HOMEFOLDER);
        
        if (homeSpace == null)
        {
            throw new RuntimeException("No home space was found.");
        }
        
        return toUrlPath(fileFolderService.getFileInfo(homeSpace));        
    }
    
    /**
     * Breaks down the Document Context + URI into the 
     *  document path and name.
     * @param alfrescoContext The alfresco context
     * @param uri The document URI
     * @param spaceType The type of the space
     */
    public String[] decomposeDocumentURL(final String alfrescoContext,
          final String uri, final QName spaceType)
    {
       String[] parts = AuthenticationUtil.runAs(new RunAsWork<String[]>()
         {
            public String[] doWork() throws Exception
            {
               return doDecomposeURLWork(alfrescoContext, uri, spaceType);
            }
         },
         authenticationComponent.getSystemUserName()
       );  
       return parts;
    }
    
    protected String[] doDecomposeURLWork(final String alfrescoContext, final String uri,
                final QName spaceType)
    {
        // Office 2008/2011 for Mac fix
       if (uri.equals("") || uri.equals("/"))
       {
           return new String[] {"", ""};
       }
       
       // We require the path to start with the context
       if (!uri.startsWith(alfrescoContext))
       {
           if (logger.isDebugEnabled())
           {
               logger.debug("Url must start with alfresco context.");
           }
           throw new VtiHandlerException(VtiHandlerException.BAD_URL);
       }
       
       // Handle the case of the context with no site/document
       // MNT-10128 : delete the last "/" from the requested URI
       if (uri.equalsIgnoreCase(alfrescoContext) ||
               (uri.endsWith("/") ? alfrescoContext.equalsIgnoreCase(uri.substring(0, uri.length() - 1)) : false))
       {
           if (logger.isDebugEnabled())
           {
               logger.debug("WebUrl: " + alfrescoContext + ", fileUrl: ''");
           }
           return new String[]{alfrescoContext, ""};
       }
       
       // Strip off the context before continuing with the path resolution
       String pathUri = getPathForURL(uri);
       // Decode the URL before doing path resolution, so that we correctly
       //  match documents or paths containing special characters
       pathUri = URLDecoder.decode(pathUri);
       // Split it into parts based on slashes
       pathUri = removeSlashes(pathUri); 
       String[] splitPath = pathUri.split("/");
       
       // Build up the path
       String webUrl = "";
       String fileUrl = "";
       
       StringBuilder tempWebUrl = new StringBuilder();
       for (int i = splitPath.length; i > 0; i--)
       {
           tempWebUrl.delete(0, tempWebUrl.length());
           
           for (int j = 0; j < i; j++)
           {
               if ( j < i-1)
               {
                   tempWebUrl.append(splitPath[j] + "/");
               }
               else
               {
                   tempWebUrl.append(splitPath[j]);
               }
           }            
           
           FileInfo fileInfo = resolvePathFileInfo(tempWebUrl.toString());
           
           if (fileInfo != null)
           {
               QName nodeType = nodeService.getType(fileInfo.getNodeRef());
               if (dictionaryService.isSubClass(nodeType, spaceType))
               {
                   webUrl = alfrescoContext + "/" + tempWebUrl;
                   if (uri.replaceAll(webUrl, "").startsWith("/"))
                   {
                       fileUrl = uri.replaceAll(webUrl, "").substring(1);
                   }
                   else
                   {
                       fileUrl = uri.replaceAll(webUrl, "");                        
                   }
                   if (logger.isDebugEnabled())
                   {
                       logger.debug("WebUrl: " + webUrl + ", fileUrl: '" + fileUrl + "'");
                   }
                   return new String[]{webUrl, fileUrl};
               }
           }
       }
       if (webUrl.equals(""))
       {
           throw new VtiHandlerException(VtiHandlerException.BAD_URL);
       }
       return new String[]{webUrl, fileUrl};
    }

    /**
     * Translates an incoming path (as used in a URL) to a repository path, taking into account
     * any translations that may be necessary. For example:
     * <pre>
     *   /alfresco/mysite/documentLibrary/folder1/file1.txt
     * </pre>
     * will be translated to:
     * <pre>
     *   /mysite/documentLibrary/folder1/file1.txt
     * </pre>
     * (assuming that the context-path/servletpath is /alfresco)
     * @param urlPath
     * @return
     */
    public String getPathForURL(String urlPath)
    {
        String path = stripPathPrefix(getAlfrescoContext(), urlPath);
        if (!path.startsWith("/"))
        {
            path = "/" + path;
        }
        return path;
    }
    
    /**
     * Removes a prefix from a given path. For example, given the path "/red/green/blue" and
     * prefix "/red", the method will return "green/blue" - note that this is now a relative path.
     * If the path does not begin with the supplied prefix then the path is returned unprocessed.
     * 
     * @param prefix
     * @param path
     * @return path without prefix
     */
    public String stripPathPrefix(final String prefix, String path)
    {
        // Only manipulate the path if the prefix is not empty.
        if (StringUtils.hasText(prefix))
        {
            if (path.startsWith(prefix))
            {
                path = path.substring(prefix.length());

                // Ensure path is relative before being returned (remove initial slash if present)
                if (path.startsWith("/"))
                {
                    path = path.substring(1);
                }
            }
        }
        return path;
    }
    
    /**
     * Get Document Workspace site from document name
     * 
     * @param document document
     * @param spaceType type of space
     * @return url to Document Workspace site
     */
    public String getDwsFromDocumentName(String document, final QName spaceType)
    {
        final String alfrescoContext = getContextFromDocumentName(document);
        final String uri = getPathFromDocumentName(document);
        String[] parts = decomposeDocumentURL(alfrescoContext, uri, spaceType); 
        return parts[0].substring(parts[0].lastIndexOf("/"));
    }
    
    /**
     * Get host from document name
     * 
     * @param document document
     * @return host
     */
    public String getHostFromDocumentName(String document)
    {       
        try
        {
            URL url = new URL(document);            
            return url.getProtocol() + "://" + url.getHost()+ ":" + url.getPort();
        }
        catch (Exception e)
        {
            throw new VtiHandlerException(VtiHandlerException.BAD_URL);
        }        
    }
    
    /**
     * Get context form document name
     * 
     * @param document document
     * @return context
     */
    public String getContextFromDocumentName(String document)
    {      
        if(document == null)
        {
           throw new VtiHandlerException(VtiHandlerException.BAD_URL);
        }
        
        try
        {
            URL url = new URL(document);
            
            if (url.getPath().startsWith(getAlfrescoContext()))
            {
                return getAlfrescoContext();
            }
            else
            {
                throw new VtiHandlerException(VtiHandlerException.BAD_URL);
            }            
            
        }
        catch (Exception e)
        {
            throw new VtiHandlerException(VtiHandlerException.BAD_URL);
        }        
    }
    
    /**
     * Get path from document name
     * 
     * @param document document
     * @return path
     */
    public String getPathFromDocumentName(String document)
    {
        try
        {
            return new URL(document).getPath();
        }
        catch (Exception e)
        {
            throw new VtiHandlerException(VtiHandlerException.BAD_URL);
        }
    }
    
    /**
     * Helper method that allows to resolve list name from listId
     * 
     * @param listId the id of the list
     * @return the list' name
     */
    public String resolveListName(String listId)
    {
        if (listId.startsWith("{"))
            listId = listId.substring(1);
        if (listId.endsWith("}"))
            listId = listId.substring(0, listId.length() - 1);
        try
        {
            Serializable listName = 
                nodeService.getProperty(new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, listId.toLowerCase()), ContentModel.PROP_NAME);
            return (String)listName;
        }
        catch (Exception e) 
        {
            throw new VtiHandlerException(VtiHandlerException.BAD_URL, e); 
        }
    }
    
    /** 
     * For a given node, looks up the parent tree to find the List
     *  (Site Container) that it is part of.
     *  
     * @param itemInList the nodeRef of item to look parent container for
     * @return the nodeRef of the list this item is part of, or the node itself if it isn't in one
     */
    public NodeRef findList(NodeRef itemInList)
    {
        NodeRef result = null;
        NodeRef nodeToCheck = itemInList;
        while (result == null && nodeToCheck != null)
        {
            if (nodeService.hasAspect(nodeToCheck, SiteModel.ASPECT_SITE_CONTAINER))
            {
                result = nodeToCheck;
                break;
            }
            else
            {
                nodeToCheck = nodeService.getPrimaryParent(nodeToCheck).getParentRef();
            }
        }
        
        if (result == null)
        {
            return itemInList;
        }

        return result;
    }

    /**
     * @see org.springframework.extensions.surf.util.AbstractLifecycleBean#onBootstrap(org.springframework.context.ApplicationEvent)
     */
    protected void onBootstrap(ApplicationEvent event)
    {
    }

    /**
     * Remove slashes from string
     * 
     * @param value input string
     * @return String output string
     */
    public static String removeSlashes(String value)
    {
        value = value.replaceAll("//", "/");

        if (value.startsWith("/"))
            value = value.substring(1);
        if (value.endsWith("/"))
            value = value.substring(0, value.length() - 1);
        return value;
    }

    /**
     * @see org.springframework.extensions.surf.util.AbstractLifecycleBean#onShutdown(org.springframework.context.ApplicationEvent)
     */
    protected void onShutdown(ApplicationEvent event)
    {
        // do nothing
    }

    /**
     * @return Root NodeRef
     */
    public NodeRef getRootNodeRef()
    {
        NodeRef rootNodeRef = singletonCache.get(KEY_VTI_ROOT_NODEREF);
        
        if (rootNodeRef == null)
        {
            rootNodeRef = tenantService.getRootNode(nodeService, searchService, namespaceService, rootPath, new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, "dummy"));
            singletonCache.put(KEY_VTI_ROOT_NODEREF, rootNodeRef);
        }
        
        return rootNodeRef;
    }

    public String getRootPath()
    {
        return rootPath;
    }

    public String getStorePath()
    {
        return storePath;
    }

    public void setPersonService(PersonService personService)
    {
        this.personService = personService;
    }

    public FileFolderService getFileFolderService()
    {
        return fileFolderService;
    }

    public void setTenantService(TenantService tenantService)
    {
        this.tenantService = tenantService;
    }

    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }
    
    public void setSingletonCache(SimpleCache<String, NodeRef> singletonCache)
    {
        this.singletonCache = singletonCache;
    }
}
