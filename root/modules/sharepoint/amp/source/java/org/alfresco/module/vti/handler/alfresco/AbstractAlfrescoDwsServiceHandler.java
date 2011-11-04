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
package org.alfresco.module.vti.handler.alfresco;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.module.vti.handler.DwsException;
import org.alfresco.module.vti.handler.DwsServiceHandler;
import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.handler.alfresco.v3.AlfrescoListServiceHandler;
import org.alfresco.module.vti.metadata.dic.CAMLMethod;
import org.alfresco.module.vti.metadata.dic.DwsError;
import org.alfresco.module.vti.metadata.dic.Permission;
import org.alfresco.module.vti.metadata.dic.VtiError;
import org.alfresco.module.vti.metadata.dic.WorkspaceType;
import org.alfresco.module.vti.metadata.model.AssigneeBean;
import org.alfresco.module.vti.metadata.model.DocumentBean;
import org.alfresco.module.vti.metadata.model.DwsBean;
import org.alfresco.module.vti.metadata.model.DwsData;
import org.alfresco.module.vti.metadata.model.DwsMetadata;
import org.alfresco.module.vti.metadata.model.LinkBean;
import org.alfresco.module.vti.metadata.model.ListInfoBean;
import org.alfresco.module.vti.metadata.model.MemberBean;
import org.alfresco.module.vti.metadata.model.SchemaBean;
import org.alfresco.module.vti.metadata.model.SchemaFieldBean;
import org.alfresco.module.vti.metadata.model.UserBean;
import org.alfresco.repo.SessionUser;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.repo.site.SiteDoesNotExistException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.GUID;
import org.alfresco.util.Pair;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Alfresco implementation of DwsServiceHandler interface
 * 
 * @author EugeneZh
 */
public abstract class AbstractAlfrescoDwsServiceHandler implements DwsServiceHandler
{
    private static Log logger = LogFactory.getLog(AbstractAlfrescoDwsServiceHandler.class);
    private static final Pattern illegalCharactersRegExpPattern = Pattern.compile("[^A-Za-z0-9_]+");

    protected FileFolderService fileFolderService;
    protected NodeService nodeService;
    protected TransactionService transactionService;
    protected PermissionService permissionService;
    protected AuthenticationService authenticationService;
    protected PersonService personService;
    protected ContentService contentService;

    protected VtiPathHelper pathHelper;

    protected Map<String, String> pagesMap;

    /**
     * Get pages map
     * 
     * @return pages map
     */
    public Map<String, String> getPagesMap()
    {
        return pagesMap;
    }

    /**
     * Set pages map
     * 
     * @param pagesMap the pages map to set
     */
    public void setPagesMap(Map<String, String> pagesMap)
    {
        this.pagesMap = pagesMap;
    }

    /**
     * Set path helper
     * 
     * @param pathHelper the path helper to set ({@link VtiPathHelper})
     */
    public void setPathHelper(VtiPathHelper pathHelper)
    {
        this.pathHelper = pathHelper;
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
     * Set node service
     * 
     * @param nodeService the node service to set ({@link NodeService})
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * Set transaction service
     * 
     * @param transactionService the transaction service to set ({@link TransactionService})
     */
    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
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
     * Set authentication service
     * 
     * @param authenticationService the authentication service to set ({@link AuthenticationService})
     */
    public void setAuthenticationService(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }

    /**
     * Set person service
     * 
     * @param personService the person service to set ({@link PersonService})
     */
    public void setPersonService(PersonService personService)
    {
        this.personService = personService;
    }

    /**
     * Set content service
     * 
     * @param contentService the content service to set ({@link ContentService})
     */
    public void setContentService(ContentService contentService)
    {
        this.contentService = contentService;
    }
        
    /**
     * @see org.alfresco.module.vti.handler.DwsServiceHandler#getDWSMetaData(java.lang.String, java.lang.String, boolean)
     */
    public DwsMetadata getDWSMetaData(String document, String id, boolean minimal) throws Exception
    {
        String dws = pathHelper.getDwsFromDocumentName(document, doGetModelType());
        String host = pathHelper.getHostFromDocumentName(document);
        String context = pathHelper.getContextFromDocumentName(document);

        // get the nodeRef for current dws
        FileInfo dwsNode = pathHelper.resolvePathFileInfo(dws);

        if (dwsNode == null)
        {
            throw new VtiHandlerException(VtiHandlerException.URL_NOT_FOUND);
        }

        DwsMetadata dwsMetadata = new DwsMetadata();

        String fullUrl = host + context + dws;
        dwsMetadata.setSubscribeUrl(fullUrl + "/subscribe.vti");
        dwsMetadata.setMtgInstance("");
        dwsMetadata.setSettingsUrl(fullUrl + "/siteSettings.vti");
        dwsMetadata.setPermsUrl(getDwsPermissionsUrl(fullUrl));
        dwsMetadata.setUserInfoUrl(fullUrl + "/userInformation.vti");

        // adding the list of roles
        dwsMetadata.setRoles(permissionService.getSettablePermissions(doGetModelType()));

        // setting the permissions for current user such as add/edit/delete items or users
        List<Permission> permissions = doGetUsersPermissions(dwsNode);

        // includes information about the schemas, lists, documents, links lists of a document workspace site
        if (!minimal)
        {
            // set Documents schema
            List<SchemaBean> schemaItems = new ArrayList<SchemaBean>();

            List<String> choices = new ArrayList<String>();

            List<SchemaFieldBean> fields = new ArrayList<SchemaFieldBean>(5);
            fields.add(new SchemaFieldBean("FileLeafRef", "Invalid", true, choices));
            fields.add(new SchemaFieldBean("_SourceUrl", "Text", false, choices));
            fields.add(new SchemaFieldBean("_SharedFileIndex", "Text", false, choices));
            fields.add(new SchemaFieldBean("Order", "Number", false, choices));
            fields.add(new SchemaFieldBean("Title", "Text", false, choices));
            schemaItems.add(doCreateDocumentSchemaBean(dwsNode, fields));

            // set Links schema
            List<SchemaFieldBean> linkFields = new ArrayList<SchemaFieldBean>(4);
            linkFields.add(new SchemaFieldBean("Attachments", "Attachments", false, choices));
            linkFields.add(new SchemaFieldBean("Order", "Number", false, choices));
            linkFields.add(new SchemaFieldBean("URL", "URL", true, choices));
            linkFields.add(new SchemaFieldBean("Comments", "Note", false, choices));
            SchemaBean linkSchema = doCreateLinkSchemaBean(dwsNode, linkFields);
            if (linkSchema != null)
            {
                schemaItems.add(linkSchema);
            }
            
            dwsMetadata.setSchemaItems(schemaItems);

            // set Documents listInfo for documents list
            List<ListInfoBean> listInfoItems = new ArrayList<ListInfoBean>();
            listInfoItems.add(new ListInfoBean(
                  null, "Documents", AlfrescoListServiceHandler.TYPE_DOCUMENT_LIBRARY, false, permissions));
            
            // set Links listInfo for links list            
            listInfoItems.add(new ListInfoBean(
                  null, "Links", AlfrescoListServiceHandler.TYPE_LINKS, false, permissions));
            
            dwsMetadata.setListInfoItems(listInfoItems);
        }

        // set permissions
        dwsMetadata.setPermissions(permissions);
        dwsMetadata.setHasUniquePerm(true);
        // set the type of document workspace site
        dwsMetadata.setWorkspaceType(getWorkspaceType(dwsNode));
        dwsMetadata.setADMode(false);
        // set url to currently opened document
        dwsMetadata.setDocUrl(document);
        dwsMetadata.setMinimal(minimal);
        // gets dwsData information
        DwsData dwsData = getDwsData(document, "");
        dwsData.setMinimal(minimal);
        dwsMetadata.setDwsData(dwsData);

        if (logger.isDebugEnabled())
        {
            logger.debug("Document workspace meta-data was retrieved for '" + dwsNode.getName() + "' site.");
        }

        return dwsMetadata;
    }

    protected String getDwsPermissionsUrl(String dwsUrl)
    {
        return dwsUrl + "/siteGroupMembership.vti";
    }
    
    public abstract WorkspaceType getWorkspaceType(FileInfo dwsNode);

    /**
     * @see org.alfresco.module.vti.handler.DwsServiceHandler#getDwsData(java.lang.String, java.lang.String)
     */
    public DwsData getDwsData(String document, String lastUpdate) throws SiteDoesNotExistException
    {
        DwsData dwsData = new DwsData();

        String dws = pathHelper.getDwsFromDocumentName(document, doGetModelType());
        String host = pathHelper.getHostFromDocumentName(document);
        String context = pathHelper.getContextFromDocumentName(document);

        FileInfo dwsInfo = pathHelper.resolvePathFileInfo(dws);

        if (dwsInfo == null)
        {
            throw new SiteDoesNotExistException(dws);
        }

        // set the title of the currently opened document workspace site
        Serializable title = nodeService.getProperty(dwsInfo.getNodeRef(), ContentModel.PROP_TITLE);

        if (title == null)
        {
            dwsData.setTitle(nodeService.getProperty(dwsInfo.getNodeRef(), ContentModel.PROP_NAME).toString());
        }
        else
        {
            dwsData.setTitle(title.toString());
        }
        
        // set the description of the currently opened document workspace site
        dwsData.setDescription(nodeService.getProperty(dwsInfo.getNodeRef(), ContentModel.PROP_DESCRIPTION).toString());

        // setting the Documents list for current document workspace site
        List<DocumentBean> dwsContent = new ArrayList<DocumentBean>();

        doGetDwsContentRecursive(dwsInfo, dwsContent);
        dwsData.setDocumentsList(dwsContent);
        
        // setting the Links list for current document workspace site
        List<LinkBean> linksList = doGetDwsLinks(dwsInfo);
        dwsData.setLinksList(linksList);
        
        dwsData.setDocLibUrl(host + context + dws + "/documentLibrary.vti");

        dwsData.setLastUpdate(lastUpdate);

        // setting currently authenticated user
        UserBean user = getCurrentUser();
        dwsData.setUser(user);

        // if current user has permission to view dws users then collect information about dws users
        if (permissionService.hasPermission(dwsInfo.getNodeRef(), PermissionService.READ_PERMISSIONS) == AccessStatus.ALLOWED)
        {
            List<MemberBean> members = doListDwsMembers(dwsInfo);
            dwsData.setMembers(members);

            List<AssigneeBean> assignees = new ArrayList<AssigneeBean>();
            for (MemberBean member : members)
            {
                assignees.add(new AssigneeBean(member.getId(), member.getName(), member.getLoginName()));
            }
            dwsData.setAssignees(assignees);
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Document workspace data was retrieved for '" + dwsInfo.getName() + "' site.");
        }

        return dwsData;
    }

    /**
     * @see org.alfresco.module.vti.handler.DwsServiceHandler#createDws(java.lang.String, java.lang.String, java.util.List, java.lang.String, java.util.List, java.lang.String,
     *      java.lang.String, org.alfresco.repo.SessionUser)
     */
    public DwsBean createDws(String parentDwsUrl, String name, List<UserBean> users, String title, List<DocumentBean> documents, String host, String context, SessionUser user)
    {
        if (dwsExists(name))
        {
            throw new DwsException(DwsError.ALREADY_EXISTS);
        }
        
        if(false == stringExists(name))
        {
            name = title;
        }
        if(false == stringExists(name))
        {
            // Both title and name empty so generate GUID.
            name = GUID.generate();
            title = name;
        }
        else
        {
            int i = 1;
            while(dwsExists(name))
            {
                name = name + "_" + i;
                i++;
            }
            title = name;
        }
        UserTransaction tx = transactionService.getUserTransaction(false);
        String createdDwsUrl = null;
        try
        {
            tx.begin();

            String createdDwsName = doCreateDws(name, title, user);
            createdDwsUrl  = doGetDwsCreationUrl(parentDwsUrl, createdDwsName);

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
                //NOOP
            }

            if (e instanceof AccessDeniedException)
            {
                throw new DwsException(DwsError.NO_ACCESS);
            }
            throw new DwsException(DwsError.SERVER_FAILURE);
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Document workspace with name '" + title + "' was successfully created.");
        }

        return doGetResultBean(parentDwsUrl, createdDwsUrl, host, context);
    }

    private boolean stringExists(String s)
    {
        return false == (s == null || s.isEmpty());
    }
    
    /**
     * @see org.alfresco.module.vti.handler.DwsServiceHandler#deleteDws(java.lang.String, org.alfresco.repo.SessionUser)
     */
    public void deleteDws(String dwsUrl, SessionUser user)
    {
        FileInfo dwsFileInfo = pathHelper.resolvePathFileInfo(dwsUrl);

        if (dwsFileInfo == null || dwsFileInfo.isFolder() == false)
        {
            throw new VtiHandlerException(VtiError.NOT_FOUND);
        }

        UserTransaction tx = transactionService.getUserTransaction(false);
        try
        {
            tx.begin();

            doDeleteDws(dwsFileInfo, user);

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
                //NOOP
            }

            if (e instanceof AccessDeniedException)
            {
                throw new DwsException(DwsError.NO_ACCESS);
            }

            throw new DwsException(DwsError.FAILED);
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Document workspace with name '" + dwsFileInfo.getName() + "' was successfully deleted.");
        }
    }

    /**
     * @see org.alfresco.module.vti.handler.DwsServiceHandler#createFolder(java.lang.String)
     */
    public void createFolder(String url)
    {
        
        for (String part : VtiPathHelper.removeSlashes(url).split("/"))
        {
            if (VtiUtils.hasIllegalCharacter(part))
            {
                throw new VtiHandlerException(VtiError.NOT_FOUND);
            }
        }
        
        FileInfo folderFileInfo = pathHelper.resolvePathFileInfo(url);

        if (folderFileInfo != null)
        {
            throw new VtiHandlerException(VtiError.ALREADY_EXISTS);
        }

        Pair<String, String> parentChildPaths = VtiPathHelper.splitPathParentChild(url);

        String parentPath = parentChildPaths.getFirst();
        FileInfo parentFileInfo = pathHelper.resolvePathFileInfo(parentPath);
        if (parentFileInfo == null)
        {
            throw new VtiHandlerException(VtiError.NOT_FOUND);
        }

        String dwsName = parentChildPaths.getSecond();
        if (dwsName.length() == 0)
        {
            throw new VtiHandlerException(VtiError.NOT_FOUND);
        }

        UserTransaction tx = transactionService.getUserTransaction(false);
        try
        {
            tx.begin();

            folderFileInfo = fileFolderService.create(parentFileInfo.getNodeRef(), dwsName, ContentModel.TYPE_FOLDER);

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
                //NOOP
            }

            if (e instanceof AccessDeniedException)
            {
                throw new VtiHandlerException(VtiError.NO_PERMISSIONS);
            }

            throw VtiExceptionUtils.createRuntimeException(e);
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Folder with url '" + url.substring(url.indexOf('/', 1)) + "' was created.");
        }
    }

    /**
     * @see org.alfresco.module.vti.handler.DwsServiceHandler#deleteFolder(java.lang.String)
     */
    public void deleteFolder(String url)
    {
        FileInfo folderFileInfo = pathHelper.resolvePathFileInfo(url);

        if (folderFileInfo == null)
        {
            throw new VtiHandlerException(VtiError.NOT_FOUND);
        }

        if (folderFileInfo.isFolder() == false)
        {
            throw new VtiHandlerException(VtiError.NOT_FOUND);
        }

        UserTransaction tx = transactionService.getUserTransaction(false);
        try
        {
            tx.begin();

            fileFolderService.delete(folderFileInfo.getNodeRef());

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
                //NOOP
            }

            if (e instanceof AccessDeniedException)
            {
                throw new VtiHandlerException(VtiError.NO_PERMISSIONS);
            }
            throw VtiExceptionUtils.createRuntimeException(e);
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Folder with url '" + url.substring(url.indexOf('/', 1)) + "' was deleted.");
        }
    }

    /**
     * @see org.alfresco.module.vti.handler.DwsServiceHandler#renameDws(java.lang.String, java.lang.String)
     */
    public void renameDws(String oldDwsUrl, String title)
    {
        FileInfo dwsFileInfo = pathHelper.resolvePathFileInfo(oldDwsUrl);

        if (dwsFileInfo == null)
        {
            throw new VtiHandlerException(VtiError.NOT_FOUND);
        }

        UserTransaction tx = transactionService.getUserTransaction(false);
        try
        {
            tx.begin();

            nodeService.setProperty(dwsFileInfo.getNodeRef(), ContentModel.PROP_TITLE, title);

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
                //NOOP
            }

            if (e instanceof AccessDeniedException)
            {
                throw new VtiHandlerException(VtiError.NO_PERMISSIONS);
            }

            throw VtiExceptionUtils.createRuntimeException(e);
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Rename DWS title from '" + dwsFileInfo.getName() + "' to '" + title + "'.");
        }
    }

    /**
     * @see org.alfresco.module.vti.handler.DwsServiceHandler#removeDwsUser(java.lang.String, java.lang.String)
     */
    public void removeDwsUser(String dwsUrl, String id)
    {
        FileInfo dwsInfo = pathHelper.resolvePathFileInfo(dwsUrl);

        if (dwsInfo == null)
        {
            throw new VtiHandlerException(VtiError.NOT_FOUND);
        }

        UserTransaction tx = transactionService.getUserTransaction(false);
        try
        {
            tx.begin();

            doRemoveDwsUser(dwsInfo, id);

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
                //NOOP
            }

            if (e instanceof AccessDeniedException)
            {
                throw new VtiHandlerException(VtiError.NO_PERMISSIONS);
            }

            throw new VtiHandlerException(VtiError.NO_PERMISSIONS);
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("User with name '" + id + "' was successfully removed from site: " + dwsInfo.getName() + ".");
        }
    }

    /**
     * Get a new document workspace site creation url
     * 
     * @param parentUrl url of the parent dws
     * @param title the title of the new document workspace site
     * @return document workspace site url
     */
    protected abstract String doGetDwsCreationUrl(String parentUrl, String title);

    /**
     * Creates a document workspace site
     * @param dwsName TODO
     * @param title the title of the new document workspace site
     * @param user current user
     * 
     * @throws HttpException
     * @throws IOException
     */
    protected abstract String doCreateDws(String dwsName, String title, SessionUser user) throws HttpException, IOException;

    /**
     * @param name
     * @return Returns <code>true</code> if the specified DWS exists.
     */
    protected abstract boolean dwsExists(String name);

    /**
     * Get new document workspace site description
     * 
     * @param parentUrl url of the parent document workspace site
     * @param dwsUrl url of the document workspace site
     * @param host application host
     * @param context application context
     * @return DwsBean new document workspace site description
     */
    protected abstract DwsBean doGetResultBean(String parentUrl, String dwsUrl, String host, String context);

    /**
     * Deletes the current document workspace site and its contents
     * 
     * @param dwsFileInfo document workspace site file info ({@link FileInfo})
     * @param user current user
     * @throws HttpException
     * @throws IOException
     */
    protected abstract void doDeleteDws(FileInfo dwsFileInfo, SessionUser user) throws HttpException, IOException;

    /**
     * Removes the specified user from the list of users for the current document workspace site
     * 
     * @param dwsFileInfo document workspace site file info ({@link FileInfo})
     * @param authority name of the user to be removed from the list of users
     */
    protected abstract void doRemoveDwsUser(FileInfo dwsFileInfo, String authority);

    /**
     * Get document workspace site users
     * 
     * @param dwsFileInfo document workspace site file info ({@link FileInfo})
     * @return List<MemberBean> list of document workspace site users
     */
    protected abstract List<MemberBean> doListDwsMembers(FileInfo dwsFileInfo);

    /**
     * Get document workspace site content
     * 
     * @param fileInfo document workspace site file info ({@link FileInfo})
     * @param dwsContent list of beans with document workspace site content informations ({@link DocumentBean})
     */
    protected abstract void doGetDwsContentRecursive(FileInfo fileInfo, List<DocumentBean> dwsContent);
    
    /**
     * Get document workspace site links
     * 
     * @param fileInfo document workspace site file info ({@link FileInfo})
     * @return linksList list of beans with document workspace site links ({@link LinkBean})
     */
    protected abstract List<LinkBean> doGetDwsLinks(FileInfo fileInfo);

    /**
     * Get type of alfresco document workspace site (Folder or Site)
     * 
     * @return QName type of alfresco document workspace site
     */
    protected abstract QName doGetModelType();

    /**
     * Get users permissions
     * 
     * @param dwsFileInfo document workspace site file info ({@link FileInfo})
     * @return List<Permission> list of permissions
     */
    protected abstract List<Permission> doGetUsersPermissions(FileInfo dwsFileInfo);

    /**
     * Create document schema
     * 
     * @param dwsFileInfo document workspace site file info ({@link FileInfo})
     * @param fields system fields of the document ({@link SchemaFieldBean})
     * @return SchemaBean document schema
     */
    protected abstract SchemaBean doCreateDocumentSchemaBean(FileInfo dwsFileInfo, List<SchemaFieldBean> fields);
    
    /**
     * Create link schema
     * 
     * @param dwsFileInfo document workspace site file info ({@link FileInfo})
     * @param fields system fields of the link ({@link SchemaFieldBean})
     * @return SchemaBean link schema
     */
    protected abstract SchemaBean doCreateLinkSchemaBean(FileInfo dwsFileInfo, List<SchemaFieldBean> fields);

    /**
     * Get current user
     * 
     * @return UserBean represent current user
     */
    protected UserBean getCurrentUser()
    {
        NodeRef person = personService.getPerson(authenticationService.getCurrentUserName());

        String loginName = (String) nodeService.getProperty(person, ContentModel.PROP_USERNAME);
        String email = (String) nodeService.getProperty(person, ContentModel.PROP_EMAIL);
        String firstName = (String) nodeService.getProperty(person, ContentModel.PROP_FIRSTNAME);
        String lastName = (String) nodeService.getProperty(person, ContentModel.PROP_LASTNAME);
        boolean isSiteAdmin = (loginName.equals("admin") ? true : false);

        return new UserBean(person.toString(), firstName + ' ' + lastName, loginName, email, false, isSiteAdmin);
    }

    /**
     * Collect information about files and folders in current document workspace site
     * 
     * @param dwsInfo document workspace site file info ({@link FileInfo})
     * @param result list of beans with document workspace site content informations ({@link DocumentBean})
     * @param documetnLibraryURL relative url on document library folder on site
     */
    protected void addDwsContentRecursive(FileInfo dwsInfo, List<DocumentBean> result, String documetnLibraryURL)
    {

        List<FileInfo> fileInfoList = fileFolderService.list(dwsInfo.getNodeRef());

        for (FileInfo fileInfo : fileInfoList)
        {
            // do not show working copies
            if (!fileInfo.isFolder() && nodeService.hasAspect(fileInfo.getNodeRef(), ContentModel.ASPECT_WORKING_COPY))
            {
                continue;
            }
            String id = "";
            String progID = "";
            String fileRef = documetnLibraryURL + fileInfo.getName();
            String objType = (fileInfo.isFolder()) ? "1" : "0";
            String created = VtiUtils.formatPropfindDate(fileInfo.getCreatedDate());
            String author = (String) nodeService.getProperty(fileInfo.getNodeRef(), ContentModel.PROP_AUTHOR);
            String modified = VtiUtils.formatPropfindDate(fileInfo.getModifiedDate());
            String editor = (String) nodeService.getProperty(fileInfo.getNodeRef(), ContentModel.PROP_MODIFIER);

            result.add(new DocumentBean(id, progID, fileRef, objType, created, author, modified, editor));

            // word dont show list of documents longer then 99 itmes
            if (result.size() > 99)
            {
                return;
            }

            // enter in other folders recursively
            if (fileInfo.isFolder())
            {
                addDwsContentRecursive(fileInfo, result, documetnLibraryURL + fileInfo.getName() + "/");
            }
        }
    }

    /**
     * Remove illegal characters from string
     * 
     * @param value input string
     * @return output string
     */
    protected String removeIllegalCharacters(String value)
    {
        return illegalCharactersRegExpPattern.matcher(value).replaceAll("_");
    }
    
    /**
     * @see org.alfresco.module.vti.handler.DwsServiceHandler#updateDwsData(org.alfresco.module.vti.metadata.model.LinkBean, CAMLMethod, java.lang.String))
     */
    public LinkBean updateDwsData(LinkBean linkBean, CAMLMethod method, String dws)
    {
        if (method.toString().equals("New"))
        {
            return doUpdateDwsDataNew(linkBean, dws);
        }
        
        if (method.toString().equals("Update"))
        {
            doUpdateDwsDataUpdate(linkBean, dws);
        }
        
        if(method.toString().equals("Delete"))
        {
            doUpdateDwsDataDelete(linkBean, dws);
        }
        
        return null;
    }
    
    /**
     * Creates new link in site links container
     * 
     * @param linkBean linkBean that should be created
     * @param dws site name
     * @return linkBean that was created
     */
    protected abstract LinkBean doUpdateDwsDataNew(LinkBean linkBean, String dws);
    
    /**
     * Updates given link in site links container
     * 
     * @param linkBean linkBean that should be updated
     * @param dws site name     
     */
    protected abstract void doUpdateDwsDataUpdate(LinkBean linkBean, String dws);
    
    /**
     * Deletes link in site links container
     * 
     * @param linkBean linkBean that should be deleted
     * @param dws site name     
     */
    protected abstract void doUpdateDwsDataDelete(LinkBean linkBean, String dws);
}
