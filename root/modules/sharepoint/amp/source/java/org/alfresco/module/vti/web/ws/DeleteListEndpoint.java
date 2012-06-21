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
import org.alfresco.module.vti.metadata.dic.VtiError;
import org.alfresco.module.vti.metadata.model.ListInfoBean;
import org.alfresco.repo.site.SiteDoesNotExistException;
import org.alfresco.service.cmr.model.FileNotFoundException;

/**
 * Class for handling the DeleteList soap method
 * 
 * @author Nick Burch
 */
public class DeleteListEndpoint extends AbstractListEndpoint
{
    /**
     * constructor
     *
     * @param handler
     */
    public DeleteListEndpoint(ListServiceHandler handler)
    {
        super(handler);
    }

    /**
     * Fetch and delete the list
     */
    @Override
    protected ListInfoBean executeListAction(VtiSoapRequest soapRequest, String dws, String listName,
            String description, int templateID) throws Exception
    {
        // Try to delete it
        try
        {
           handler.deleteList(listName, dws);
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
        
        // List has now gone
        return null;
    }

    /**
     * The Delete response is very simple
     */
    @Override
    protected void renderList(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse, String siteName,
            ListInfoBean list) throws Exception
    {
       // If we managed to delete the list, we simply send an empty <DeleteListResponse />
       soapResponse.getDocument().addElement("DeleteListResponse");
    }
}