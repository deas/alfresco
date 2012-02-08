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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.module.vti.handler.ListServiceHandler;
import org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoListServiceHandler;
import org.alfresco.module.vti.metadata.model.ListInfoBean;
import org.alfresco.module.vti.metadata.model.ListTypeBean;
import org.alfresco.query.PagingRequest;
import org.alfresco.query.PagingResults;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.site.SiteDoesNotExistException;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.InvalidTypeException;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.DuplicateChildNodeNameException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.springframework.beans.factory.InitializingBean;

/**
 * Alfresco implementation of {@link ListServiceHandler}
 * 
 * @author Nick Burch
 */
public class AlfrescoListServiceHandler extends AbstractAlfrescoListServiceHandler implements InitializingBean
{
    private FileFolderService fileFolderService;
    private NodeService nodeService;
    private SiteService siteService;
    //private ShareUtils shareUtils;
    private NamespaceService namespaceService;
    private DictionaryService dictionaryService;
    //private TransactionService transactionService;
    //private AuthenticationComponent authenticationComponent;
    
    private Map<Integer,String> dataListTypes;
    private Map<Integer, ListTypeBean> listTypes; 
    
    /**
     * Set authentication component
     * 
     * @param authenticationComponent the authentication component to set ({@link AuthenticationComponent})
     */
    /*public void setAuthenticationComponent(AuthenticationComponent authenticationComponent)
    {
        this.authenticationComponent = authenticationComponent;
    }*/
    
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
    /*public void setShareUtils(ShareUtils shareUtils)
    {
        this.shareUtils = shareUtils;
    }*/

    public void setFileFolderService(FileFolderService fileFolderService) 
    {
       this.fileFolderService = fileFolderService;
    }
    
    public void setNamespaceService(NamespaceService namespaceService)
    {
       this.namespaceService = namespaceService;
    }

    public void setDictionaryService(DictionaryService dictionaryService) 
    {
       this.dictionaryService = dictionaryService;
    }

    /*public void setTransactionService(TransactionService transactionService) 
    {
       this.transactionService = transactionService;
    }*/

    /**
     * Sets the list of SharePoint Template IDs to 
     *  Alfresco DataList Types 
     */
    public void setDataListTypes(Map<Integer, String> dataListTypes) 
    {
       this.dataListTypes = dataListTypes;
    }
    
    private static String dwsToSiteShortName(String dws)
    {
       if(dws.startsWith("/"))
       {
          return dws.substring(1);
       }
       return dws;
    }
    
    /**
     * Identifies the NodeRef of a list, be it a DataList list or
     *  a Container list
     */
    private NodeRef locateList(String listName, SiteInfo site) throws FileNotFoundException
    {
       // Is this a Container Based or DataList based one?
       NodeRef dataListNodeRef = null;
       NodeRef containerNodeRef = null;
       String siteName = site.getShortName();
       
       // Check the DataList
       NodeRef dataLists = siteService.getContainer(siteName, DATALIST_CONTAINER);
       if(dataLists != null)
       {
          dataListNodeRef = nodeService.getChildByName(dataLists, ContentModel.ASSOC_CONTAINS, listName);
       }
       
       // Check the Container
       containerNodeRef = siteService.getContainer(siteName, listName);

       // Sanity check
       if(dataListNodeRef == null && containerNodeRef == null)
       {
          throw new FileNotFoundException("No List found with name '" + listName + "'");
       }
       else if(dataListNodeRef != null && containerNodeRef != null)
       {
          throw new FileNotFoundException("Two different Lists found with name '" + listName + "' - can't distinguish");
       }
       else if(dataListNodeRef != null)
       {
          return dataListNodeRef;
       }
       else
       {
          return containerNodeRef;
       }
    }
    
    /**
     * Populates the non core parts of the List Info 
     */
    private void populateListInfo(ListInfoBean list)
    {
       // Grab all the properties
       Map<QName,Serializable> props = nodeService.getProperties(list.getNodeRef());
       
       // Set the ones of interest
       list.setTitle((String)props.get(ContentModel.PROP_TITLE));
       list.setDescription((String)props.get(ContentModel.PROP_DESCRIPTION));
       list.setAuthor((String)props.get(ContentModel.PROP_CREATOR));
       
       list.setCreated((Date)props.get(ContentModel.PROP_CREATED));
       list.setModified((Date)props.get(ContentModel.PROP_MODIFIED));
       
       // How many items in the list?
       PagingRequest paging = new PagingRequest(1);
       paging.setRequestTotalCountMax(1000);
       PagingResults<FileInfo> items = fileFolderService.list(list.getNodeRef(), true, false, null, null, paging);
       list.setNumItems(items.getTotalResultCount().getFirst());
       
       // All done
    }

    @Override
    public ListInfoBean getList(String listName, String dws)
         throws SiteDoesNotExistException, FileNotFoundException 
    {
        String siteName = dwsToSiteShortName(dws);
        SiteInfo site = siteService.getSite(siteName);
        if(site == null)
        {
           throw new SiteDoesNotExistException(siteName);
        }

        // Get the NodeRef
        NodeRef listNodeRef = locateList(listName, site);
        
        // Identify the type
        ListTypeBean type = null;
        
        // Is it a DataList type?
        String dlType = (String)nodeService.getProperty(listNodeRef, PROP_DATA_LIST_ITEM_TYPE);
        if(dlType != null)
        {
           for(Integer id : dataListTypes.keySet())
           {
              if(dlType.equals(dataListTypes.get(id)))
              {
                 // It's this type
                 type = listTypes.get(id);
                 break;
              }
           }
        }
        else
        {
           // Container based
           String containerName = (String)nodeService.getProperty(listNodeRef, ContentModel.PROP_NAME);
           for(VtiBuiltInListType t : VtiBuiltInListType.values())
           {
              if(containerName.equals(t.component))
              {
                 type = listTypes.get(t.id);
                 break;
              }
           }
        }
        if(type == null)
        {
           throw new FileNotFoundException("Entry with name '" + listName + "' is not a list");
        }
        
        // Wrap it
        ListInfoBean list = new ListInfoBean(listNodeRef, listName, type, false, null);
        populateListInfo(list);
        return list;
    }

    @Override
    public ListInfoBean createList(String listName, String description, String dws, int templateId)
          throws SiteDoesNotExistException, DuplicateChildNodeNameException, InvalidTypeException
    {
       // Check we can find the type
       ListTypeBean type = listTypes.get(templateId);
       if (type == null)
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
       
       // Is it a Container or a DataList?
       ListInfoBean list;
       if(type.isDataList())
       {
          list = createDataList(site, listName, description, type);
       }
       else
       {
          list = createComponentList(site, listName, description, type);
       }
       
       // Attach additional information to it
       populateListInfo(list);
       
       // All done
       return list;
    }
    
    private ListInfoBean createComponentList(SiteInfo site, String listName, String description, ListTypeBean type)
    {
       // Get the matching builting
       VtiBuiltInListType builtin = null;
       for(VtiBuiltInListType t : VtiBuiltInListType.values())
       {
          if (t.id == type.getId())
          {
             builtin = t;
             break;
          }
       }
       if(builtin == null)
       {
          throw new IllegalStateException("No matching builtin found for " + type.getId());
       }
          
       // For component based ones, we're very picky about the name
       if(! listName.equals(builtin.component))
       {
          throw new IllegalArgumentException("Name '" + listName + "' didn't match required name '" + builtin.component + "'");
       }
       
       // Check it isn't there already
       if(siteService.hasContainer(site.getShortName(), listName))
       {
          throw new DuplicateChildNodeNameException(site.getNodeRef(), ContentModel.ASSOC_CONTAINS, listName, null);
       }
       
       // Have it created
       Map<QName,Serializable> props = new HashMap<QName, Serializable>();
       props.put(ContentModel.PROP_DESCRIPTION, description);
       NodeRef nodeRef = siteService.createContainer(site.getShortName(), listName, ContentModel.TYPE_CONTAINER, props);
       
       // Return a wrapper around it
       return new ListInfoBean(nodeRef, listName, type, false, null);
    }
    
    private ListInfoBean createDataList(SiteInfo site, String listName, String description, ListTypeBean type)
    {
       String siteName = site.getShortName();
       String typeName = dataListTypes.get(type.getId());
       
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
       props.put(PROP_DATA_LIST_ITEM_TYPE, typeName);
       props.put(ContentModel.PROP_NAME, listName);
       props.put(ContentModel.PROP_DESCRIPTION, description);
       
       NodeRef nodeRef = nodeService.createNode(
             container, ContentModel.ASSOC_CONTAINS, QName.createQName(listName),
             TYPE_DATALIST, props
       ).getChildRef();
       
       // Return a wrapper around it
       return new ListInfoBean(nodeRef, listName, type, false, null);
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
       
       // Get the NodeRef
       NodeRef listNodeRef = locateList(listName, site);

       // Delete it
       nodeService.deleteNode(listNodeRef);
    }
    
    @Override
    public List<ListTypeBean> getAvailableListTypes() 
    {
       return Collections.unmodifiableList(
          new ArrayList<ListTypeBean>( listTypes.values() )
       );
    }
    
    @Override
    public void afterPropertiesSet() throws Exception 
    {
        PropertyCheck.mandatory(this, "dataListTypes", dataListTypes);

        listTypes = new HashMap<Integer, ListTypeBean>();
        
        // Do the build in types
        for(VtiBuiltInListType type : VtiBuiltInListType.values())
        {
           ListTypeBean list = buildType(type);
           if(list != null)
           {
              listTypes.put(type.id, list);
           }
           else
           {
              if(logger.isDebugEnabled())
              {
                 logger.debug("Skipping list template " + type.id + " " + 
                       type.name + " as no Alfresco component found for it");
              }
           }
        }
        
        // Now override with the datalist ones
        for(Integer id : dataListTypes.keySet())
        {
           String typeName = dataListTypes.get(id);
           QName typeQName = QName.createQName(typeName, namespaceService);
           
           ListTypeBean list;
           VtiBuiltInListType builtin = null;
           for(VtiBuiltInListType type : VtiBuiltInListType.values())
           {
              if(type.id == id)
              {
                 builtin = type;
                 break;
              }
           }
           
           String name = typeName;
           if(builtin != null)
           {
              name = builtin.name;
           }
           
           TypeDefinition type = dictionaryService.getType(typeQName);
           if(type != null)
           {
              list = new ListTypeBean(
                    id, VtiListBaseType.GENERIC_LIST.id, true,
                    name, type.getTitle(), type.getDescription()
              );
           }
           else
           {
              list = new ListTypeBean(
                    id, VtiListBaseType.GENERIC_LIST.id, true,
                    name, null, null
              );
           }
           
           // Record it now it's built
           listTypes.put(id, list);
        }
    }
}
