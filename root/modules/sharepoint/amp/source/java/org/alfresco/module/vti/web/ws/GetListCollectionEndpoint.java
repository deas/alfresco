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

import java.util.ArrayList;
import java.util.List;

import org.alfresco.module.vti.handler.ListServiceHandler;
import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.metadata.model.ListInfoBean;
import org.alfresco.repo.site.SiteDoesNotExistException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;

/**
 * Class for handling GetListCollection method from lists web service
 *
 * @author PavelYur
 */
public class GetListCollectionEndpoint extends AbstractEndpoint
{
    private static Log logger = LogFactory.getLog(GetWebCollectionEndpoint.class);

    // handler that provides methods for operating with documents and folders
    private ListServiceHandler handler;    

    // xml namespace prefix
    private static String prefix = "lists";
    
    /**
     * constructor
     *
     * @param handler that provides methods for operating with documents and folders
     */
    public GetListCollectionEndpoint(ListServiceHandler handler)
    {
        this.handler = handler;
    }
    
    /**
     * Returns the names and GUIDs for all the lists in the site
     * 
     * @param soapRequest Vti soap request ({@link VtiSoapRequest})
     * @param soapResponse Vti soap response ({@link VtiSoapResponse}) 
     */
    public void execute(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug("Soap Method with name " + getName() + " is started.");

        // mapping xml namespace to prefix
        SimpleNamespaceContext nc = new SimpleNamespaceContext();
        nc.addNamespace(prefix, namespace);
        nc.addNamespace(soapUriPrefix, soapUri);

        // get site name that is used to list subsites
        String siteName = getDwsFromUri(soapRequest);
        
        List<ListInfoBean> lists;
        
        if (siteName.equals(""))
        {
            lists = new ArrayList<ListInfoBean>(0); 
        }
        else
        {
            try
            {
                lists = handler.getListCollection(siteName.substring(1));
            }
            catch (SiteDoesNotExistException e)
            {
                throw new VtiHandlerException(VtiHandlerException.BAD_URL);
            }
        }

        // creating soap response
        Element responseElement = soapResponse.getDocument().addElement("GetListCollectionResponse", namespace);
        Element resultElement = responseElement.addElement("GetListCollectionResult");       
        Element listsElement = resultElement.addElement("Lists");
        
        for (ListInfoBean list : lists)
        {
            Element listElement = listsElement.addElement("List");
            listElement.addAttribute("ID", list.getId());
            listElement.addAttribute("Name", list.getName());
            listElement.addAttribute("Title", list.getTitle());
            listElement.addAttribute("Description", list.getDescription());
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Soap Method with name " + getName() + " is finished.");
        }
    }

}
