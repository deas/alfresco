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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.module.vti.handler.DwsException;
import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler;
import org.alfresco.module.vti.handler.alfresco.VtiExceptionUtils;
import org.alfresco.module.vti.handler.alfresco.VtiPathHelper;
import org.alfresco.module.vti.handler.alfresco.VtiUtils;
import org.alfresco.module.vti.metadata.dic.DwsError;
import org.alfresco.module.vti.metadata.dic.Permission;
import org.alfresco.module.vti.metadata.dic.VtiError;
import org.alfresco.module.vti.metadata.dic.WorkspaceType;
import org.alfresco.module.vti.metadata.model.DocumentBean;
import org.alfresco.module.vti.metadata.model.DwsBean;
import org.alfresco.module.vti.metadata.model.LinkBean;
import org.alfresco.module.vti.metadata.model.MemberBean;
import org.alfresco.module.vti.metadata.model.SchemaBean;
import org.alfresco.module.vti.metadata.model.SchemaFieldBean;
import org.alfresco.repo.SessionUser;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.util.URLDecoder;
import org.springframework.extensions.surf.util.URLEncoder;

/**
 * Alfresco implementation of DwsServiceHandler and AbstractAlfrescoDwsServiceHandler
 * 
 * @author PavelYur
 */
public class AlfrescoDwsServiceHandler extends AbstractAlfrescoDwsServiceHandler
{
    private static Log logger = LogFactory.getLog(AlfrescoDwsServiceHandler.class);
    
    private static final QName TYPE_LINK = QName.createQName("http://www.alfresco.org/model/linksmodel/1.0", "link");
    private static final QName PROP_LINK_TITLE = QName.createQName("http://www.alfresco.org/model/linksmodel/1.0", "title");
    private static final QName PROP_LINK_URL = QName.createQName("http://www.alfresco.org/model/linksmodel/1.0", "url");
    private static final QName PROP_LINK_DESCRIPTION = QName.createQName("http://www.alfresco.org/model/linksmodel/1.0", "description");    

    private AuthenticationComponent authenticationComponent;
    private SiteService siteService;
    private ShareUtils shareUtils;
    private AuthorityService authorityService;
    
    /**
     * Set authority service
     * 
     * @param authorityService the authority service to set ({@link AuthorityService})
     */
    public void setAuthorityService(AuthorityService authorityService)
    {
        this.authorityService = authorityService;
    }
    
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
     * Set site service
     * 
     * @param siteService the site service to set ({@link SiteService})
     */
    public void setSiteService(SiteService siteService)
    {
        this.siteService = siteService;
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
     * @see org.alfresco.module.vti.handler.DwsServiceHandler#handleRedirect(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void handleRedirect(HttpServletRequest req, HttpServletResponse resp) throws HttpException, IOException
    {

        String uri = VtiPathHelper.removeSlashes(req.getRequestURI());
        String docLibPath = null;
        
        if (uri.contains("documentLibrary") && req.getAttribute("VALID_SITE_URL") != null)
        {
            int pos = uri.indexOf("documentLibrary");
            docLibPath = URLDecoder.decode(uri.substring(pos + "documentLibrary".length()));
            uri = uri.substring(0, pos + "documentLibrary".length()) + ".vti";            
        }
        
        String redirectTo;

        if (!uri.endsWith(".vti"))
        {
            if (logger.isDebugEnabled())
                logger.debug("Redirection to site in browser");
            if (req.getParameter("calendar") != null)
            {
                redirectTo = pagesMap.get("calendar");
            }
            else
            {
                redirectTo = pagesMap.get("siteInBrowser");
            }

            String siteName = uri.substring(uri.lastIndexOf('/') + 1);

            redirectTo = redirectTo.replace("...", siteName);
            if (logger.isDebugEnabled())
                logger.debug("Redirection URI: " + redirectTo);
        }
        else
        {
            // gets the action is performed
            String action = uri.substring(uri.lastIndexOf("/") + 1, uri.indexOf(".vti"));
            if (logger.isDebugEnabled())
                logger.debug("Redirection to specific action: " + action);
            // gets the uri for redirection from configuration
            redirectTo = pagesMap.get(action);
            if (action.equals("userInformation"))
            {
                // redirect to user profile
                final String userID = req.getParameter("ID");
                
                String userName = AuthenticationUtil.runAs(new RunAsWork<String>()
                {
                    public String doWork() throws Exception
                    {                        
                        return (String)nodeService.getProperty(new NodeRef(userID), ContentModel.PROP_USERNAME);
                    }
                }, authenticationComponent.getSystemUserName());
                
                redirectTo = redirectTo.replace("...", URLEncoder.encode(userName));
            }
            else
            {
                // redirect to site information (dashboard, site members ...)
                String[] parts = uri.split("/");
                String siteName = parts[parts.length - 2];
                redirectTo = redirectTo.replace("...", siteName);
                if (action.equals("documentLibrary") && docLibPath != null && docLibPath.length() != 0)
                {
                    redirectTo = redirectTo + "#path=" + ShareUtils.encode(docLibPath);
                }
            }
            String doc = req.getParameter("doc");
            if (doc != null)
            {
                redirectTo = redirectTo + "?nodeRef=" + doc;
            }
            if (logger.isDebugEnabled())
                logger.debug("Redirection URI: " + redirectTo);

        }

        String redirectionUrl = shareUtils.getShareHostWithPort() + shareUtils.getShareContext() + redirectTo;
        if (logger.isDebugEnabled())
            logger.debug("Executing redirect to URL: '" + redirectionUrl + "'.");

        resp.setHeader("Location", redirectionUrl);
        resp.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean canCreateDwsUrl(String url)
    {
        if(siteService.hasCreateSitePermissions())
        {
            return true;
        }
        throw new DwsException(DwsError.NO_ACCESS);
    }
    
    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doRemoveDwsUser(org.alfresco.service.cmr.model.FileInfo, java.lang.String)
     */
    public void doRemoveDwsUser(FileInfo dwsFileInfo, String authority)
    {
        String username = authorityService.getName(AuthorityType.GROUP, authority);
        if (!authorityService.authorityExists(username))
        {
            NodeRef personNodeRef = new NodeRef(authority);
            username = (String) nodeService.getProperty(personNodeRef, ContentModel.PROP_USERNAME);
        }

        siteService.removeMembership(dwsFileInfo.getName(), username);
    }

    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doGetDwsContentRecursive(org.alfresco.service.cmr.model.FileInfo, java.util.List)
     */
    public void doGetDwsContentRecursive(FileInfo fileInfo, List<DocumentBean> dwsContent)
    {
        String path = pathHelper.toUrlPath(fileInfo);
        FileInfo docLibFileInfo = pathHelper.resolvePathFileInfo(path + "/documentLibrary");

        if (docLibFileInfo == null)
        {
            throw new VtiHandlerException(VtiError.V_BAD_URL);
        }

        addDwsContentRecursive(docLibFileInfo, dwsContent, "documentLibrary/");
    }
    
    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doGetDwsLinks(org.alfresco.service.cmr.model.FileInfo)
     */
    public List<LinkBean> doGetDwsLinks(FileInfo fileInfo)
    { 
        
        if (!siteService.hasContainer(fileInfo.getName(), "links"))
        {            
            siteService.createContainer(fileInfo.getName(), "links", ContentModel.TYPE_FOLDER, null);            
        }
        
        List<LinkBean> linkList = new ArrayList<LinkBean>();
        
        NodeRef linksContainer = siteService.getContainer(fileInfo.getName(), "links");
        List<FileInfo> containerContent = fileFolderService.list(linksContainer);
        
        for (FileInfo item : containerContent)
        {
            if (nodeService.getType(item.getNodeRef()).equals(TYPE_LINK))
            {
                Map<QName, Serializable> props = nodeService.getProperties(item.getNodeRef());
                String url = (String) props.get(PROP_LINK_URL);
                String description = (String) props.get(PROP_LINK_TITLE);
                String comments = (String) props.get(PROP_LINK_DESCRIPTION);
                String created = VtiUtils.formatPropfindDate((Date) props.get(ContentModel.PROP_CREATED));
                String author = getFullUsername((String) props.get(ContentModel.PROP_CREATOR));
                String modified = VtiUtils.formatPropfindDate((Date)props.get(ContentModel.PROP_MODIFIED));
                String editor = getFullUsername((String) props.get(ContentModel.PROP_MODIFIER));
                int owshiddenversion = 0;
                String id = (String) props.get(ContentModel.PROP_NAME);
                LinkBean link = new LinkBean(url, description, comments, created, author, modified, editor, owshiddenversion, id);
                linkList.add(link);
            }
            else
            {
                continue;
            }
        }
        
        return linkList;
    }

    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doListDwsMembers(org.alfresco.service.cmr.model.FileInfo)
     */
    public List<MemberBean> doListDwsMembers(FileInfo dwsFileInfo)
    {
        List<MemberBean> members = new ArrayList<MemberBean>();
        // gets list of site users names
        Set<String> membersName = siteService.listMembers(dwsFileInfo.getName(), null, null, -1).keySet();
        Iterator<String> userIterator = membersName.iterator();

        while (userIterator.hasNext())
        {
            String username = userIterator.next();
            if (AuthorityType.getAuthorityType(username) == AuthorityType.GROUP)
            {
                String shortName = authorityService.getShortName(username);
                members.add(new MemberBean(shortName, shortName, "", "", true));
            }
            else
            {
                NodeRef personNodeRef = personService.getPerson(username);
                String firstName = nodeService.getProperty(personNodeRef, ContentModel.PROP_FIRSTNAME).toString();
                String lastName = nodeService.getProperty(personNodeRef, ContentModel.PROP_LASTNAME).toString();
                String email = nodeService.getProperty(personNodeRef, ContentModel.PROP_EMAIL).toString();
                members.add(new MemberBean(personNodeRef.toString(), firstName + " " + lastName, username, email, false));
            }
        }

        return members;
    }

    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doGetModelType()
     */
    protected QName doGetModelType()
    {
        return SiteModel.TYPE_SITE;
    }

    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doGetUsersPermissions(org.alfresco.service.cmr.model.FileInfo)
     */
    protected List<Permission> doGetUsersPermissions(FileInfo dwsFileInfo)
    {
        List<Permission> permissions = new ArrayList<Permission>();
        String userRole = siteService.getMembersRole(dwsFileInfo.getName(), authenticationComponent.getCurrentUserName());

        if (userRole.equals(SiteModel.SITE_CONSUMER))
        {
        }
        else if (userRole.equals(SiteModel.SITE_CONTRIBUTOR))
        {
            permissions.add(Permission.DELETE_LIST_ITEMS);
            permissions.add(Permission.EDIT_LIST_ITEMS);
            permissions.add(Permission.INSERT_LIST_ITEMS);
        }
        else if (userRole.equals(SiteModel.SITE_COLLABORATOR))
        {
            permissions.add(Permission.EDIT_LIST_ITEMS);
            permissions.add(Permission.DELETE_LIST_ITEMS);
            permissions.add(Permission.INSERT_LIST_ITEMS);
            permissions.add(Permission.MANAGE_LISTS);
        }
        else if (userRole.equals(SiteModel.SITE_MANAGER))
        {
            permissions.add(Permission.EDIT_LIST_ITEMS);
            permissions.add(Permission.DELETE_LIST_ITEMS);
            permissions.add(Permission.INSERT_LIST_ITEMS);
            permissions.add(Permission.MANAGE_LISTS);
            permissions.add(Permission.MANAGE_ROLES);
            permissions.add(Permission.MANAGE_SUBWEBS);
            permissions.add(Permission.MANAGE_WEB);
        }

        return permissions;
    }

    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doCreateDocumentSchemaBean(org.alfresco.service.cmr.model.FileInfo, java.util.List)
     */
    protected SchemaBean doCreateDocumentSchemaBean(FileInfo dwsFileInfo, List<SchemaFieldBean> fields)
    {
        return new SchemaBean("Documents", "documentLibrary", fields);
    }
    
    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doCreateLinkSchemaBean(org.alfresco.service.cmr.model.FileInfo, java.util.List)
     */
    protected SchemaBean doCreateLinkSchemaBean(FileInfo dwsFileInfo, List<SchemaFieldBean> fields)
    {
        return new SchemaBean("Links", null, fields);
    }

    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doDeleteDws(org.alfresco.service.cmr.model.FileInfo, org.alfresco.repo.SessionUser)
     */
    protected void doDeleteDws(FileInfo dwsFileInfo, SessionUser user) throws HttpException, IOException
    {
        shareUtils.deleteSite(user, dwsFileInfo.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doCreateDws(String dwsName, String title, SessionUser user) throws HttpException, IOException
    {
        shareUtils.createSite(user, "document-workspace", dwsName, title, "", true);
        return dwsName;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    protected boolean dwsExists(String name)
    {
        if(name == null || name.isEmpty())
        {
            return false;
        }
        return siteService.getSite(name) != null;
    }
    
    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doGetDwsCreationUrl(java.lang.String, java.lang.String)
     */
    @Override
    protected String doGetDwsCreationUrl(String parentUrl, String title)
    {
        // ensure that new dws will be created in Sites space
        if (!parentUrl.equals(""))
        {
            throw new VtiHandlerException(VtiError.V_BAD_URL);
        }

        // replace all illegal characters
        title = removeIllegalCharacters(title);

        return parentUrl + "/" + title;
    }

    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doGetResultBean(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    protected DwsBean doGetResultBean(String parentUrl, String dwsUrl, String host, String context)
    {
        DwsBean dwsBean = new DwsBean();
        dwsBean.setDoclibUrl("documentLibrary");
        dwsBean.setUrl(host + context + dwsUrl);
        dwsBean.setParentWeb(parentUrl);
        return dwsBean;
    }

    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doUpdateDwsDataDelete(org.alfresco.module.vti.metadata.model.LinkBean, java.lang.String)
     */
    @Override
    protected void doUpdateDwsDataDelete(LinkBean linkBean, String dws)
    {        
        NodeRef linksContainer = null;        

        linksContainer = siteService.getContainer(dws, "links");        
        
        if (linksContainer == null)
        {
            throw new VtiHandlerException(VtiHandlerException.LIST_NOT_FOUND);
        }
        
        NodeRef linkRef = nodeService.getChildByName(linksContainer, ContentModel.ASSOC_CONTAINS, linkBean.getId());
        
        if (linkRef == null)
        {
            throw new VtiHandlerException(VtiHandlerException.ITEM_NOT_FOUND);            
        }
        
        UserTransaction tx = transactionService.getUserTransaction(false);
        try
        {
            tx.begin();
            
            nodeService.deleteNode(linkRef);
            
            tx.commit();
        }
        catch (Throwable t)
        {
            try
            {
                tx.rollback();
            }
            catch (Exception e)
            {
            }
            
            if (t instanceof AccessDeniedException)
            {
                throw new VtiHandlerException(VtiHandlerException.NO_PERMISSIONS);
            }

            throw VtiExceptionUtils.createRuntimeException(t);
        }
        
        if (logger.isDebugEnabled())
        {
            logger.debug("Link with name '" + linkBean.getDescription() + "' was deleted.");
        }
    }

    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doUpdateDwsDataNew(org.alfresco.module.vti.metadata.model.LinkBean, java.lang.String)
     */
    protected LinkBean doUpdateDwsDataNew(LinkBean linkBean, String dws)
    {
        LinkBean result = null;
        
        NodeRef linksContainer = null;        

        if (siteService.hasContainer(dws, "links"))
        {
            linksContainer = siteService.getContainer(dws, "links");
        }
        else
        {
            linksContainer = siteService.createContainer(dws, "links", ContentModel.TYPE_FOLDER, null);
        }
        
        if (linksContainer == null)
        {
            throw new VtiHandlerException(VtiHandlerException.LIST_NOT_FOUND);
        }

        Map<QName, Serializable> props = new HashMap<QName, Serializable>();
        
        String name = getUniqueName(linksContainer);
        props.put(ContentModel.PROP_NAME, name);
        props.put(PROP_LINK_URL, linkBean.getUrl());
        props.put(PROP_LINK_TITLE, linkBean.getDescription());
        props.put(PROP_LINK_DESCRIPTION, linkBean.getComments());      
        
        UserTransaction tx = transactionService.getUserTransaction(false);
        try
        {
            tx.begin();            
            
            ChildAssociationRef association = nodeService.createNode(linksContainer, ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_PREFIX, name), TYPE_LINK, props);
                        
            ContentWriter writer = contentService.getWriter(association.getChildRef(), ContentModel.PROP_CONTENT, true);
            writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
            writer.setEncoding("UTF-8");
            String text = linkBean.getUrl();
            writer.putContent(text);     
            
            result = buildLinkBean(association.getChildRef());
            
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
            
            if (e instanceof AccessDeniedException)
            {
                throw new VtiHandlerException(VtiHandlerException.NO_PERMISSIONS);
            }

            throw VtiExceptionUtils.createRuntimeException(e);
        }        
            
        if (logger.isDebugEnabled())
        {
            logger.debug("Link with name '" + linkBean.getDescription() + "' was created.");
        }            
        return result;
    }

    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doUpdateDwsDataUpdate(org.alfresco.module.vti.metadata.model.LinkBean, java.lang.String)
     */
    protected void doUpdateDwsDataUpdate(LinkBean linkBean, String dws)
    {          
        NodeRef linksContainer = null;        

        linksContainer = siteService.getContainer(dws, "links");                
        
        if (linksContainer == null)
        {
            throw new VtiHandlerException(VtiHandlerException.LIST_NOT_FOUND);
        }
        
        NodeRef linkRef = nodeService.getChildByName(linksContainer, ContentModel.ASSOC_CONTAINS, linkBean.getId());
        
        if (linkRef == null)
        {
            throw new VtiHandlerException(VtiHandlerException.ITEM_NOT_FOUND);
        }

        UserTransaction tx = transactionService.getUserTransaction(false);
        try
        {
            tx.begin();
            nodeService.setProperty(linkRef, PROP_LINK_URL, linkBean.getUrl());
            nodeService.setProperty(linkRef, PROP_LINK_TITLE, linkBean.getDescription());
            nodeService.setProperty(linkRef, PROP_LINK_DESCRIPTION, linkBean.getComments());
            
            ContentWriter writer = contentService.getWriter(linkRef, ContentModel.PROP_CONTENT, true);
            writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
            writer.setEncoding("UTF-8");
            String text = linkBean.getUrl();
            writer.putContent(text);
            
            tx.commit();
        }
        catch (Throwable t)
        {
            try
            {
                tx.rollback();
            }
            catch (Exception e)
            {
            }
            
            if (t instanceof AccessDeniedException)
            {
                throw new VtiHandlerException(VtiHandlerException.NO_PERMISSIONS);
            }

            throw VtiExceptionUtils.createRuntimeException(t);
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Link with name '" + linkBean.getDescription() + "' was updated.");
        }
    }
    
    /**
     * Generate a unique name that can be used to create a child of the given parentNode.
     * 
     * @param linkFolderRef parentNode
     * @return unique name that can be used to create a child of the given parentNode.
     */
    private String getUniqueName(NodeRef linkFolderRef)
    {
        String name = "link-" + System.currentTimeMillis();
        String finalName = name + "_" + (int) Math.floor(Math.random() * 1000);
        int count = 0;
        while (nodeService.getChildByName(linkFolderRef, ContentModel.ASSOC_CONTAINS, finalName) != null && count < 100)
        {
            finalName = name + "_" + (int) Math.floor(Math.random() * 1000);
            count++;
        }
        return finalName;
    }
    
    /*
     *  build LinkBean from NodeRef
     */    
    private LinkBean buildLinkBean(NodeRef linkNodeRef)
    {
        LinkBean result = new LinkBean();
        Map<QName, Serializable> props= nodeService.getProperties(linkNodeRef);
        result.setId((String)props.get(ContentModel.PROP_NAME));
        result.setUrl((String)props.get(PROP_LINK_URL));
        result.setDescription((String)props.get(PROP_LINK_TITLE));
        result.setComments((String)props.get(PROP_LINK_DESCRIPTION));
        result.setAuthor(getFullUsername((String)props.get(ContentModel.PROP_CREATOR)));
        result.setEditor(getFullUsername((String)props.get(ContentModel.PROP_MODIFIER)));
        result.setModified(VtiUtils.formatPropfindDate((Date)props.get(ContentModel.PROP_MODIFIED)));
        result.setCreated(VtiUtils.formatPropfindDate((Date)props.get(ContentModel.PROP_CREATED))); 
        result.setOwshiddenversion(0);
        return result;
    }
    
    /*
     * gets full username by the given short name
     */
    private String getFullUsername(String username)
    {
        NodeRef person = personService.getPerson(username);
        
        if (person == null)        
        {
            return username;
        }
        
        String firstname = (String) nodeService.getProperty(person, ContentModel.PROP_FIRSTNAME);
        String lastname = (String) nodeService.getProperty(person, ContentModel.PROP_LASTNAME);
        
        return firstname + " " + lastname;
    }
    
    public WorkspaceType getWorkspaceType(FileInfo dwsNode)
    {
        SiteInfo siteInfo = siteService.getSite(dwsNode.getName());
        WorkspaceType result = WorkspaceType.EMPTY;
        if (siteInfo != null && siteInfo.getSitePreset() != null)
        {
            if (siteInfo.getSitePreset().equals("site-dashboard"))
            {
                result = WorkspaceType.SPS;
            }
            else if (siteInfo.getSitePreset().equals("document-workspace"))
            {
                result = WorkspaceType.DWS;
            }
            else if (siteInfo.getSitePreset().equals(AlfrescoMeetingServiceHandler.MEETING_WORKSPACE_NAME))
            {
                result = WorkspaceType.MWS;
            }
        }
        return result;
    }

}