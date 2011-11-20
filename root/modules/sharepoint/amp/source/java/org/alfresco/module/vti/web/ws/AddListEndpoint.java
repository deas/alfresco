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

import org.alfresco.module.vti.handler.ListServiceHandler;
import org.alfresco.module.vti.metadata.model.ListInfoBean;
import org.alfresco.repo.site.SiteDoesNotExistException;
import org.alfresco.service.cmr.dictionary.InvalidTypeException;
import org.alfresco.service.cmr.repository.DuplicateChildNodeNameException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class for handling the AddList soap method
 * 
 * @author Nick Burch
 */
public class AddListEndpoint extends AbstractListEndpoint
{
    @SuppressWarnings("unused")
    private final static Log logger = LogFactory.getLog(AddListEndpoint.class);

    /**
     * constructor
     *
     * @param handler
     */
    public AddListEndpoint(ListServiceHandler handler)
    {
        super(handler);
    }

    /**
     * Adds the new list
     */
    @Override
    protected ListInfoBean executeListAction(VtiSoapRequest soapRequest,
         String dws, String listName, String description, int templateID) throws Exception 
    {
       // We require a template ID parameter
       if(templateID < 0)
       {
          throw new VtiSoapException("Invalid Template ID", -1);
       }
       
       // Have the List Created
       ListInfoBean list;
       try
       {
          list = handler.createList(listName, description, dws, templateID);
       }
       catch(SiteDoesNotExistException se)
       {
          throw new VtiSoapException("No site found with name '" + dws + "'", 0x81020012l, se);
       }
       catch(DuplicateChildNodeNameException dcnne)
       {
          throw new VtiSoapException("List name already in use", 0x81020012l, dcnne);
       }
       catch(InvalidTypeException ite)
       {
          throw new VtiSoapException("Template ID not known", 0x8107058al, ite); 
       }

       // Return the list details
       return list;
    }

}
