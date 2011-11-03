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

import java.net.URLDecoder;

import org.alfresco.module.vti.handler.DwsServiceHandler;
import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.metadata.model.DwsData;
import org.alfresco.repo.site.SiteDoesNotExistException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

/**
 * Class for handling GetWeb soap method
 * 
 * @author Nirck Burch
 */
public class GetWebEndpoint extends AbstractEndpoint
{
	private final static Log logger = LogFactory.getLog(GetWebEndpoint.class);
	
    // handler that provides methods for operating with documents and folders
    private DwsServiceHandler handler;
    
    // xml namespace prefix
    private static String prefix = "dws";

    /**
     * @param handler that provides methods for operating with documents and folders
     */
    public GetWebEndpoint(DwsServiceHandler handler)
    {
        super();
        this.handler = handler;
    }

    /**
    * Creates new document workspace with given title
    * 
    * @param soapRequest Vti soap request ({@link VtiSoapRequest})
    * @param soapResponse Vti soap response ({@link VtiSoapResponse}) 
    */
    public void execute(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse) throws Exception
    {
    	if (logger.isDebugEnabled()) {
    		logger.debug("SOAP method with name " + getName() + " is started.");
    	}
    	
        // mapping xml namespace to prefix
        SimpleNamespaceContext nc = new SimpleNamespaceContext();
        nc.addNamespace(prefix, namespace);
        nc.addNamespace(soapUriPrefix, soapUri);

        // Get the web url parameter from request
        XPath urlPath = new Dom4jXPath(buildXPath(prefix, "/GetWeb/webUrl"));
        urlPath.setNamespaceContext(nc);
        Element urlE = (Element) urlPath.selectSingleNode(soapRequest.getDocument().getRootElement());

        // Turn that into a URL in our required format
        String url = URLDecoder.decode(urlE.getTextTrim(), "UTF-8"); 
        
        // Fetch the details for the site
        DwsData dws;
        try {
           dws = handler.getDwsData(url, null);
        }
        catch(SiteDoesNotExistException e)
        {
           // The specification defines the exact code that must be
           //  returned in case of a site not being found
           long code = 0x82000001l;
           String message = "No Site was found with the given URL";
           throw new VtiSoapException(message, code, e);
        }
        catch(VtiHandlerException vti)
        {
           // Something was wrong with the request given, likely
           //  the URL wasn't in a valid format
           long code = 0x82000001l;
           String message = "Invalid request";
           throw new VtiSoapException(message, code, vti);
        }
        
        
        // creating soap response
        Element root = soapResponse.getDocument().addElement("GetWebResponse", namespace);
        Element result = root.addElement("GetWebResult");
        Element web = result.addElement("Web");

        web.addAttribute("Title", dws.getTitle());
        web.addAttribute("Url", dws.getDocLibUrl());
        
        String description = dws.getDescription();
        if(description != null && description.length() > 0)
        {
           web.addAttribute("Description", dws.getDescription());
        }
        
        // We don't do FSS-HTTP (yet!)
        web.addAttribute("CellStorageWebServiceEnabled", "False");
        
        // We don't support offline editing
        web.addAttribute("ExcludeFromOfflineClient", "False");
        
        // Completed
        if (logger.isDebugEnabled()) {
    		logger.debug("SOAP method with name " + getName() + " is finished.");
    	}        
    }    
}
