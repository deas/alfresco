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
import org.alfresco.module.vti.metadata.dic.VtiError;
import org.alfresco.module.vti.metadata.model.DwsData;
import org.alfresco.repo.site.SiteDoesNotExistException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

/**
 * Class for handling GetDwsData soap method
 * 
 * @author AndreyAk
 *
 */

public class GetDwsDataEndpoint extends AbstractEndpoint
{
	private final static Log logger = LogFactory.getLog(GetDwsDataEndpoint.class);

    // handler that provides methods for operating with documents and folders
    private DwsServiceHandler handler;

    // xml namespace prefix
    private static String prefix = "dws";

    /**
     * @param handler that provides methods for operating with documents and folders
     */
    public GetDwsDataEndpoint(DwsServiceHandler handler)
    {
        super();
        this.handler = handler;
    }

    /**
     * Retrieves information about document workspace
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

        // getting document parameter from request
        XPath docPath = new Dom4jXPath(buildXPath(prefix, "/GetDwsData/document"));
        docPath.setNamespaceContext(nc);
        String doc = ((Element) docPath.selectSingleNode(soapRequest.getDocument().getRootElement())).getText();

        // getting lastUpdate parameter from request
        XPath lastUpdatePath = new Dom4jXPath(buildXPath(prefix, "/GetDwsData/lastUpdate"));
        lastUpdatePath.setNamespaceContext(nc);
        Element lastUpdate = (Element) lastUpdatePath.selectSingleNode(soapRequest.getDocument().getRootElement());                
       
        DwsData dwsData;
        try
        {
           dwsData = handler.getDwsData(URLDecoder.decode(doc, "UTF-8"), lastUpdate.getTextTrim());
        }
        catch(SiteDoesNotExistException e)
        {
           throw new VtiHandlerException(VtiError.V_URL_NOT_FOUND);
        }
        
        // creating soap response
        Element root = soapResponse.getDocument().addElement("GetDwsDataResponse", namespace);
        Element getDwsDataResult = root.addElement("GetDwsDataResult");

        getDwsDataResult.setText(generateXml(dwsData));
        
        if (logger.isDebugEnabled()) {
    		logger.debug("SOAP method with name " + getName() + " is finished.");
    	}
    }

}
