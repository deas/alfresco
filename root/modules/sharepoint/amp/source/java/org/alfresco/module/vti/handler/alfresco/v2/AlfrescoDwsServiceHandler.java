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
package org.alfresco.module.vti.handler.alfresco.v2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.model.ContentModel;
import org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler;
import org.alfresco.module.vti.metadata.dic.Permission;
import org.alfresco.module.vti.metadata.dic.WorkspaceType;
import org.alfresco.module.vti.metadata.model.DocumentBean;
import org.alfresco.module.vti.metadata.model.DwsBean;
import org.alfresco.module.vti.metadata.model.LinkBean;
import org.alfresco.module.vti.metadata.model.MemberBean;
import org.alfresco.module.vti.metadata.model.SchemaBean;
import org.alfresco.module.vti.metadata.model.SchemaFieldBean;
import org.alfresco.repo.SessionUser;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.security.AccessPermission;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.httpclient.HttpException;

/**
 * Alfresco implementation of DwsServiceHandler and AbstractAlfrescoDwsServiceHandler
 * 
 * @author AndreyAk
 * @author Dmitry Lazurkin
 */
public class AlfrescoDwsServiceHandler extends AbstractAlfrescoDwsServiceHandler
{
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
     * @see org.alfresco.module.vti.handler.DwsServiceHandler#handleRedirect(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void handleRedirect(HttpServletRequest req, HttpServletResponse resp) throws HttpException, IOException
    {
        String uuid = req.getParameter("nodeId");

        String uri = req.getRequestURI();
        String redirectTo;

        if (uuid != null)
        {
            // open site in browser
            final NodeRef siteNodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, uuid.toLowerCase());
            redirectTo = pagesMap.get("siteInBrowser");
            redirectTo = redirectTo.replace("...", siteNodeRef.getId());
        }
        else
        {
            // gets the action is performed
            String action = uri.substring(uri.lastIndexOf("/") + 1, uri.indexOf(".vti"));
            redirectTo = pagesMap.get(action);
        }

        resp.addCookie(new Cookie("alfUser0", ""));
        resp.sendRedirect(redirectTo);
    }

    /**
    * {@inheritDoc}
    */
    public boolean canCreateDwsUrl(String url)
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doRemoveDwsUser(org.alfresco.service.cmr.model.FileInfo, java.lang.String)
     */
    protected void doRemoveDwsUser(FileInfo dwsFileInfo, String authority)
    {
        permissionService.clearPermission(dwsFileInfo.getNodeRef(), authority);
    }

    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doGetDwsContentRecursive(org.alfresco.service.cmr.model.FileInfo, java.util.List)
     */
    protected void doGetDwsContentRecursive(FileInfo fileInfo, List<DocumentBean> dwsContent)
    {
        String rootPath = pathHelper.toUrlPath(fileInfo);
        addDwsContentRecursive(fileInfo, dwsContent, rootPath);
    }
    
    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doGetDwsLinks(org.alfresco.service.cmr.model.FileInfo)
     */
    public List<LinkBean> doGetDwsLinks(FileInfo fileInfo)
    {
        return null;        
    }

    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doListDwsMembers(org.alfresco.service.cmr.model.FileInfo)
     */
    protected List<MemberBean> doListDwsMembers(FileInfo dwsFileInfo)
    {
        List<MemberBean> members = new ArrayList<MemberBean>();
        // gets all permissions for current dws
        Set<AccessPermission> permissions = permissionService.getAllSetPermissions(dwsFileInfo.getNodeRef());

        Set<String> users = new HashSet<String>();
        for (AccessPermission permission : permissions)
        {
            if (permission.getAccessStatus() == AccessStatus.ALLOWED
                    && (permission.getAuthorityType() == AuthorityType.USER || permission.getAuthorityType() == AuthorityType.GUEST || permission.getAuthorityType() == AuthorityType.GROUP))
            {
                String authority = permission.getAuthority();

                if (users.contains(authority))
                {
                    continue;
                }
                else
                {
                    users.add(authority);
                }

                if (permission.getAuthorityType() == AuthorityType.GROUP)
                {
                    members.add(new MemberBean(authority, authority, "", "", true));
                    Set<String> authorities = authorityService.getContainedAuthorities(AuthorityType.USER, authority, false);
                    for (String user : authorities)
                    {
                        if (users.contains(user))
                        {
                            continue;
                        }
                        else
                        {
                            users.add(user);
                        }
                        NodeRef person = personService.getPerson(user);

                        String loginName = (String) nodeService.getProperty(person, ContentModel.PROP_USERNAME);
                        String email = (String) nodeService.getProperty(person, ContentModel.PROP_EMAIL);
                        String firstName = (String) nodeService.getProperty(person, ContentModel.PROP_FIRSTNAME);
                        String lastName = (String) nodeService.getProperty(person, ContentModel.PROP_LASTNAME);
                        boolean isDomainGroup = false;

                        members.add(new MemberBean(loginName, firstName + ' ' + lastName, loginName, email, isDomainGroup));
                    }
                }
                else
                {
                    NodeRef person = personService.getPerson(authority);

                    String loginName = (String) nodeService.getProperty(person, ContentModel.PROP_USERNAME);
                    String email = (String) nodeService.getProperty(person, ContentModel.PROP_EMAIL);
                    String firstName = (String) nodeService.getProperty(person, ContentModel.PROP_FIRSTNAME);
                    String lastName = (String) nodeService.getProperty(person, ContentModel.PROP_LASTNAME);
                    boolean isDomainGroup = false;

                    members.add(new MemberBean(loginName, firstName + ' ' + lastName, loginName, email, isDomainGroup));
                }
            }
        }

        String currentUser = authenticationService.getCurrentUserName();
        if (!users.contains(currentUser))
        {
            NodeRef person = personService.getPerson(currentUser);

            String loginName = (String) nodeService.getProperty(person, ContentModel.PROP_USERNAME);
            String email = (String) nodeService.getProperty(person, ContentModel.PROP_EMAIL);
            String firstName = (String) nodeService.getProperty(person, ContentModel.PROP_FIRSTNAME);
            String lastName = (String) nodeService.getProperty(person, ContentModel.PROP_LASTNAME);

            members.add(new MemberBean(loginName, firstName + ' ' + lastName, loginName, email, false));
        }
        return members;
    }

    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doGetModelType()
     */
    protected QName doGetModelType()
    {
        return ContentModel.TYPE_FOLDER;
    }

    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doGetUsersPermissions(org.alfresco.service.cmr.model.FileInfo)
     */
    protected List<Permission> doGetUsersPermissions(FileInfo dwsFileInfo)
    {
        List<Permission> permissions = new ArrayList<Permission>();

        if (permissionService.hasPermission(dwsFileInfo.getNodeRef(), PermissionService.DELETE_CHILDREN) == AccessStatus.ALLOWED)
        {
            permissions.add(Permission.DELETE_LIST_ITEMS);
        }

        if (permissionService.hasPermission(dwsFileInfo.getNodeRef(), PermissionService.WRITE) == AccessStatus.ALLOWED)
        {
            permissions.add(Permission.EDIT_LIST_ITEMS);
        }

        if (permissionService.hasPermission(dwsFileInfo.getNodeRef(), PermissionService.ADD_CHILDREN) == AccessStatus.ALLOWED)
        {
            permissions.add(Permission.INSERT_LIST_ITEMS);
        }

        if (permissionService.hasPermission(dwsFileInfo.getNodeRef(), PermissionService.CHANGE_PERMISSIONS) == AccessStatus.ALLOWED)
        {
            permissions.add(Permission.MANAGE_ROLES);
        }

        if ((permissionService.hasPermission(dwsFileInfo.getNodeRef(), PermissionService.WRITE_PROPERTIES) == AccessStatus.ALLOWED)
                && (permissionService.hasPermission(dwsFileInfo.getNodeRef(), PermissionService.DELETE) == AccessStatus.ALLOWED))
        {
            permissions.add(Permission.MANAGE_WEB);
        }

        return permissions;
    }

    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doCreateDocumentSchemaBean(org.alfresco.service.cmr.model.FileInfo, java.util.List)
     */
    protected SchemaBean doCreateDocumentSchemaBean(FileInfo dwsFileInfo, List<SchemaFieldBean> fields)
    {
        return new SchemaBean("Documents", pathHelper.toUrlPath(dwsFileInfo), fields);
    }
    
    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doCreateLinkSchemaBean(org.alfresco.service.cmr.model.FileInfo, java.util.List)
     */
    protected SchemaBean doCreateLinkSchemaBean(FileInfo dwsFileInfo, List<SchemaFieldBean> fields)
    {
        return null;
    }

    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doDeleteDws(org.alfresco.service.cmr.model.FileInfo, org.alfresco.repo.SessionUser)
     */
    protected void doDeleteDws(FileInfo dwsFileInfo, SessionUser user) throws HttpException, IOException
    {
        fileFolderService.delete(dwsFileInfo.getNodeRef());
    }

    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doCreateDws(String, java.lang.String, org.alfresco.repo.SessionUser)
     */
    protected String doCreateDws(String dwsName, String title, SessionUser user) throws HttpException, IOException
    {
        fileFolderService.create(null, title, ContentModel.TYPE_FOLDER);
        return title;
    }

    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doGetDwsCreationUrl(java.lang.String, java.lang.String)
     */
    protected String doGetDwsCreationUrl(String parentUrl, String title)
    {
        String dwsUrl = title;
        if (parentUrl.length() != 0)
        {
            dwsUrl = parentUrl + "/" + dwsUrl;
        }
        else
        {
            // this means that no path was provided then lets create new dws in user's home space
            String userHome = pathHelper.getUserHomeLocation();
            if (!userHome.equals(""))
                dwsUrl = pathHelper.getUserHomeLocation() + "/" + dwsUrl;
        }
        return dwsUrl;
    }

    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doGetResultBean(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    protected DwsBean doGetResultBean(String parentUrl, String dwsUrl, String host, String context)
    {
        DwsBean dwsBean = new DwsBean();
        dwsBean.setDoclibUrl(dwsUrl);
        dwsBean.setUrl(host + context);
        dwsBean.setParentWeb(parentUrl);
        return dwsBean;
    }

    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doUpdateDwsDataDelete(org.alfresco.module.vti.metadata.model.LinkBean, java.lang.String)
     */
    protected void doUpdateDwsDataDelete(LinkBean linkBean, String dws)
    {
    }
    
    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doUpdateDwsDataNew(org.alfresco.module.vti.metadata.model.LinkBean, java.lang.String)
     */
    protected LinkBean doUpdateDwsDataNew(LinkBean linkBean, String dws)
    {        
        return null;
    }

    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doUpdateDwsDataUpdate(org.alfresco.module.vti.metadata.model.LinkBean, java.lang.String)
     */
    protected void doUpdateDwsDataUpdate(LinkBean linkBean, String dws)
    {
    }
    
    public WorkspaceType getWorkspaceType(FileInfo dwsNode) {
        return WorkspaceType.DWS;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    protected boolean dwsExists(String name)
    {
        return false;
    }

}