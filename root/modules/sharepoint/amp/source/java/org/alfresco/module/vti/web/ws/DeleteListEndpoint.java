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
import org.alfresco.repo.site.SiteDoesNotExistException;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

/**
 * Class for handling the DeleteList soap method
 * 
 * @author Nick Burch
 */
public class DeleteListEndpoint extends AbstractEndpoint
{
	
	private final static Log logger = LogFactory.getLog(DeleteListEndpoint.class);

    // handler that provides methods for operating with lists
    private ListServiceHandler handler;

    // xml namespace prefix
    private static String prefix = "listsws";

    /**
     * constructor
     *
     * @param handler
     */
    public DeleteListEndpoint(ListServiceHandler handler)
    {
        this.handler = handler;
    }

    /**
     * Deletes a given data list
     * 
     * @param soapRequest Vti soap request ({@link VtiSoapRequest})
     * @param soapResponse Vti soap response ({@link VtiSoapResponse}) 
     */
    public void execute(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse) throws Exception   {
       if (logger.isDebugEnabled()) {
          logger.debug("SOAP method with name " + getName() + " is started.");
       }

       // mapping xml namespace to prefix
       SimpleNamespaceContext nc = new SimpleNamespaceContext();
       nc.addNamespace(soapUriPrefix, soapUri);
       nc.addNamespace(prefix, namespace);
       
       
       String dws = getDwsFromUri(soapRequest);        

       // getting listName parameter from request
       XPath listNameXPath = new Dom4jXPath(buildXPath(prefix, "/DeleteList/listName"));
       listNameXPath.setNamespaceContext(nc);
       Element listNameE = (Element) listNameXPath.selectSingleNode(soapRequest.getDocument().getRootElement());

       String listName = null;
       if (listNameE != null)
       {
          listName = listNameE.getTextTrim();
       }
       
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
       
       // If we managed to delete the list, we simply send an empty <DeleteListResponse />
       soapResponse.getDocument().addElement("DeleteListResponse");

       if (logger.isDebugEnabled()) {
          logger.debug("SOAP method with name " + getName() + " is finished.");
       }        
    }

}
