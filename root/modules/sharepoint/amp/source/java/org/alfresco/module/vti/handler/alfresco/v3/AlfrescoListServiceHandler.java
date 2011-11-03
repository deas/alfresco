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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.module.vti.handler.ListServiceHandler;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.site.SiteDoesNotExistException;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.InvalidTypeException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.DuplicateChildNodeNameException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Alfresco implementation of {@link ListServiceHandler}
 * 
 * @author Nick Burch
 */
public class AlfrescoListServiceHandler implements ListServiceHandler
{
    private static Log logger = LogFactory.getLog(AlfrescoListServiceHandler.class);
    
    private static final String DATALIST_CONTAINER = "dataLists";
    private static final QName TYPE_DATALIST = QName.createQName(
          NamespaceService.DATALIST_MODEL_1_0_URI, "dataList");
    private static final QName PROP_DATA_LIST_ITEM_TYPE = QName.createQName(
          NamespaceService.DATALIST_MODEL_1_0_URI, "dataListItemType");


    private FileFolderService fileFolderService;
    private NodeService nodeService;
    private SiteService siteService;
    private ShareUtils shareUtils;
    private DictionaryService dictionaryService;
    private TransactionService transactionService;
    private AuthenticationComponent authenticationComponent;
    
    private Map<Integer,String> listTypes;
    
    /**
     * Set authentication component
     * 
     * @param authenticationComponent the authentication component to set ({@link AuthenticationComponent})
     */
    public void setAuthenticationComponent(AuthenticationComponent authenticationComponent)
    {
        this.authenticationComponent = authenticationComponent;
    }
    
    public void setNodeService(NodeService nodeService) 
    {
       this.nodeService = nodeService;
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

    public void setFileFolderService(FileFolderService fileFolderService) 
    {
       this.fileFolderService = fileFolderService;
    }

    public void setDictionaryService(DictionaryService dictionaryService) 
    {
       this.dictionaryService = dictionaryService;
    }

    public void setTransactionService(TransactionService transactionService) 
    {
       this.transactionService = transactionService;
    }

    /**
     * Sets the list of SharePoint Template IDs to 
     *  Alfresco DataList Types 
     */
    public void setListTypes(Map<Integer, String> listTypes) 
    {
       this.listTypes = listTypes;
    }
    
    private static String dwsToSiteShortName(String dws)
    {
       if(dws.startsWith("/"))
       {
          return dws.substring(1);
       }
       return dws;
    }

    @Override
    public void createList(String listName, String description, String dws, int templateId)
          throws SiteDoesNotExistException, DuplicateChildNodeNameException, InvalidTypeException
    {
       // Check we can find the type
       String typeName = listTypes.get(templateId);
       if (typeName == null)
       {
          throw new InvalidTypeException(QName.createQName(Integer.toString(templateId)));
       }
       
       // Get the site
       String siteName = dwsToSiteShortName(dws);
       SiteInfo site = siteService.getSite(siteName);
       if(site == null)
       {
          throw new SiteDoesNotExistException(siteName);
       }
       
       // Have the component created if needed
       if (!siteService.hasContainer(siteName, DATALIST_CONTAINER))
       {
          siteService.createContainer(siteName, DATALIST_CONTAINER, ContentModel.TYPE_CONTAINER, null);
       }
       NodeRef container = siteService.getContainer(siteName, DATALIST_CONTAINER);
       
       // Check the name is free
       if (nodeService.getChildByName(container, ContentModel.ASSOC_CONTAINS, listName) != null)
       {
          throw new DuplicateChildNodeNameException(container, ContentModel.ASSOC_CONTAINS, listName, null);
       }
       
       // Create the list
       Map<QName,Serializable> props = new HashMap<QName, Serializable>();
       props.put(TYPE_DATALIST, typeName);
       props.put(ContentModel.PROP_NAME, listName);
       props.put(ContentModel.PROP_DESCRIPTION, description);
       
       nodeService.createNode(
             container, ContentModel.ASSOC_CONTAINS, QName.createQName(listName),
             TYPE_DATALIST, props
       );
    }

    @Override
    public void deleteList(String listName, String dws) 
          throws SiteDoesNotExistException, FileNotFoundException 
    {
       String siteName = dwsToSiteShortName(dws);
       SiteInfo site = siteService.getSite(siteName);
       if(site == null)
       {
          throw new SiteDoesNotExistException(siteName);
       }
       
       // Grab the component
       NodeRef container = siteService.getContainer(siteName, DATALIST_CONTAINER);
       if(container == null)
       {
          throw new FileNotFoundException("No DataList Container found in Site");
       }
       
       // Grab the datalist
       NodeRef list = nodeService.getChildByName(container, ContentModel.ASSOC_CONTAINS, listName);
       if(list == null)
       {
          throw new FileNotFoundException("No List found with name '" + listName + "'");
       }
       
       // Delete it
       nodeService.deleteNode(list);
    }
}