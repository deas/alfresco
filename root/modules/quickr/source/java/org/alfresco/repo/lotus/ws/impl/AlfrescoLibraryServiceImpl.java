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

import java.util.Date;
import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.lotus.ws.ClbCategoryType;
import org.alfresco.repo.lotus.ws.ClbLibrariesByPageResponse;
import org.alfresco.repo.lotus.ws.ClbLibrariesResponse;
import org.alfresco.repo.lotus.ws.ClbLibrary;
import org.alfresco.repo.lotus.ws.ClbLibraryResponse;
import org.alfresco.repo.lotus.ws.LibraryService;
import org.alfresco.repo.lotus.ws.LoginException_Exception;
import org.alfresco.repo.lotus.ws.PageParams;
import org.alfresco.repo.lotus.ws.ServiceException_Exception;
import org.alfresco.repo.lotus.ws.UserInfo;
import org.alfresco.repo.lotus.ws.impl.helper.AlfrescoQuickrPathHelper;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author PavelYur
 */
public class AlfrescoLibraryServiceImpl implements LibraryService
{
    private static Log logger = LogFactory.getLog(AlfrescoLibraryServiceImpl.class);
    private NodeService nodeService;
    private AlfrescoQuickrPathHelper pathHelper;

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setPathHelper(AlfrescoQuickrPathHelper pathHelper)
    {
        this.pathHelper = pathHelper;
    }

    public ClbLibraryResponse getApplicationRoot() throws LoginException_Exception, ServiceException_Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Retrieves the requested library
     * 
     * @param id The uuid of the business component if no path is provided. If both id and path are provided, the id must be the uuid of the parent folder to the business
     *        component.
     * @param path The absolute path to the business component if no id is provided. If both id and path are provided, the path must be the relative path from the id provided.
     * @return ClbLibraryResponse
     * @throws LoginException_Exception, ServiceException_Exception
     * @throws FileNotFoundException 
     */
    public ClbLibraryResponse getBusinessComponent(String id, String path) throws LoginException_Exception, ServiceException_Exception, FileNotFoundException
    {
        ClbLibraryResponse result = new ClbLibraryResponse();

        ClbLibrary library = new ClbLibrary();

        result.setLibrary(library);
        NodeRef requestedLibraryNodeRef = pathHelper.resolveNodeRef(id, path);

        library.setId(requestedLibraryNodeRef.getId());

        library.setPath(pathHelper.getNodePath(requestedLibraryNodeRef, false));

        library.setLocked(false);

        library.setPermissions("");

        Date systemCreateDate = (Date) nodeService.getProperty(requestedLibraryNodeRef, ContentModel.PROP_CREATED);
        library.setSystemCreated(pathHelper.getXmlDate(systemCreateDate));
        library.setCreated(pathHelper.getXmlDate(systemCreateDate));

        Date systemLastModifieDate = (Date) nodeService.getProperty(requestedLibraryNodeRef, ContentModel.PROP_MODIFIED);
        library.setSystemLastModified(pathHelper.getXmlDate(systemLastModifieDate));
        library.setLastModified(pathHelper.getXmlDate(systemLastModifieDate));

        library.setHidden(false);

        String description = (String) nodeService.getProperty(requestedLibraryNodeRef, ContentModel.PROP_DESCRIPTION);
        library.setDescription(description);

        String name = (String) nodeService.getProperty(requestedLibraryNodeRef, ContentModel.PROP_NAME);
        library.setTitle(name);

        UserInfo creator = new UserInfo();
        creator.setCommonName((String) nodeService.getProperty(requestedLibraryNodeRef, ContentModel.PROP_CREATOR));
        library.setCreator(creator);

        UserInfo modifier = new UserInfo();
        modifier.setCommonName((String) nodeService.getProperty(requestedLibraryNodeRef, ContentModel.PROP_MODIFIER));
        library.setLastModifier(modifier);

        library.setLockOwner(null);

        return result;
    }

    public ClbLibrariesResponse getBusinessComponents(String libraryId, String libraryPath, List<ClbCategoryType> categoryTypes) throws LoginException_Exception,
            ServiceException_Exception, FileNotFoundException
    {
        // all passed parameters are ignored as alfresco provide only
        // single library mapped to the SpacesStore workspace.

        ClbLibrariesResponse result = new ClbLibrariesResponse();

        ClbLibraryResponse libraryResponse = getBusinessComponent(pathHelper.getRootNodeRef().getId(), null);

        result.getLibraries().add(libraryResponse.getLibrary());

        return result;
    }

    public ClbLibrariesByPageResponse getBusinessComponentsByPage(String libraryId, String libraryPath, List<ClbCategoryType> categoryTypes, PageParams pageParams)
            throws LoginException_Exception, ServiceException_Exception, FileNotFoundException
    {
        ClbLibrariesByPageResponse result = new ClbLibrariesByPageResponse();

        ClbLibraryResponse libraryResponse = getBusinessComponent(pathHelper.getRootNodeRef().getId(), null);

        result.getLibraries().add(libraryResponse.getLibrary());

        return result;
    }
}
