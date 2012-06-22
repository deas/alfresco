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
package org.alfresco.module.vti.web.ws;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.module.vti.handler.ListServiceHandler;
import org.alfresco.module.vti.handler.ListServiceHandler.ListItemOperationType;
import org.alfresco.module.vti.metadata.dic.VtiError;
import org.alfresco.module.vti.metadata.model.ListInfoBean;
import org.alfresco.repo.site.SiteDoesNotExistException;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

/**
 * Class for handling the UpdateListItems soap method, which
 *  is how entries are added/updated/deleted in a List.
 * 
 * @author Nick Burch
 */
public class UpdateListItemsEndpoint extends AbstractListEndpoint
{
    private final static Log logger = LogFactory.getLog(UpdateListItemsEndpoint.class);
    
    private NamespaceService namespaceService;
    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }

    /**
     * constructor
     *
     * @param handler
     */
    public UpdateListItemsEndpoint(ListServiceHandler handler)
    {
        super(handler);
    }
    
    /**
     * Fetches all the details of the update, and processes
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void executeListActionDetails(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse, String siteName,
            String listName, Element requestElement, SimpleNamespaceContext nc) throws Exception
    {
        // Have the List Fetched
        ListInfoBean list = null;
        try
        {
           list = handler.getList(listName, listName);
        }
        catch(SiteDoesNotExistException se)
        {
           // The specification defines the exact code that must be
           //  returned in case of a file not being found
           long code = VtiError.V_LIST_NOT_FOUND.getErrorCode();
           String message = "Site not found: " + se.getMessage();
           throw new VtiSoapException(message, code, se);
        }
        catch(FileNotFoundException fnfe)
        {
           // The specification defines the exact code that must be
           //  returned in case of a file not being found
           long code = VtiError.V_LIST_NOT_FOUND.getErrorCode();
           String message = "List not found: " + fnfe.getMessage();
           throw new VtiSoapException(message, code, fnfe);
        }
        
        // Fetch the update details
        XPath updatesXPath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/updates"));
        updatesXPath.setNamespaceContext(nc);
        Element updates = (Element) updatesXPath.selectSingleNode(requestElement);
        if (updates == null)
        {
            throw new VtiSoapException("No updates found", 0);
        }
        
        // Loop over the batches of item updates
        for (Element batch : (List<Element>)updates.selectNodes("Batch"))
        {
           for (Element method : (List<Element>)batch.selectNodes("Method"))
           {
               // Get the ID, aka cm:name, needed for Update and Delete
               String id = method.attributeValue("ID");
               
               // What operation are they performing?
               ListItemOperationType operation = ListItemOperationType.valueOf(
                       method.attributeValue("Cmd"));
               
               // Build the list of fields
               Map<QName,String> fields = new HashMap<QName, String>();
               for (Element field : (List<Element>)method.selectNodes("Field"))
               {
                   // Turn the name into an Alfresco QName
                   QName qname = null;
                   String fieldName = field.attributeValue("Name");
                   
                   // Get the value, as a string (will be converted later)
                   String value = field.getText().toString();
                   
                   // Handle names with special meanings
                   if ("ID".equals(fieldName))
                   {
                       qname = ContentModel.PROP_NAME;
                       if (id == null && value != null)
                       {
                           id = value;
                       }
                   }
                   else
                   {
                       qname = QName.createQName(fieldName, namespaceService);
                   }
                   
                   // Store the value for the operations
                   if (value != null)
                   {
                       fields.put(qname, value);
                       
                       if (logger.isDebugEnabled())
                           logger.debug("Field add/update of " + qname + " = " + value);
                   }
                   else
                   {
                       if (logger.isInfoEnabled())
                           logger.info("Skipping field with no value: " + fieldName + " / " + qname);
                   }
               }
               
               // Have the operation performed
               handler.updateListItem(list, operation, id, fields);
           }
        }
    }

    /**
     * Not used, we are too specific
     */
    @Override
    protected ListInfoBean executeListAction(VtiSoapRequest soapRequest,
         String dws, String listName, String description, int templateID) throws Exception 
    {
        throw new IllegalStateException("Should not be called, UpdateListItems has special handling");
    }
}
