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

import org.alfresco.module.vti.handler.DwsServiceHandler;
import org.alfresco.module.vti.handler.Error;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

/**
 * Class for handling CanCreateDwsUrl soap method
 * 
 * @author Nick Smith
 *
 */
public class CanCreateDwsUrlEndpoint extends AbstractEndpoint
{
	private final static Log logger = LogFactory.getLog(CanCreateDwsUrlEndpoint.class);
	
    // handler that provides methods for operating with documents and folders
    private DwsServiceHandler handler;
    
    // xml namespace prefix
    private static String prefix = "dws";

    /**
     * @param handler that provides methods for operating with documents and folders
     */
    public CanCreateDwsUrlEndpoint(DwsServiceHandler handler)
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

        // getting title parameter from request
        XPath urlPath = new Dom4jXPath(buildXPath(prefix, "/CanCreateDwsUrl/url"));
        urlPath.setNamespaceContext(nc);
        Element url = (Element) urlPath.selectSingleNode(soapRequest.getDocument().getRootElement());
        String urlText = "";
        Error result = Error.FAILED;
        if(url != null)
        {
            urlText = url.getTextTrim();
            result = handler.canCreateDwsUrl(urlText);
        }
        
        // creating soap response
        Element root = soapResponse.getDocument().addElement("CanCreateDwsUrlResponse", namespace);
        Element resultElement = root.addElement("CanCreateDwsUrlResult");
        
        if(Error.NO_ERROR.equals(result))
        {
            resultElement.setText(processTag("Result", urlText).toString());
        }
        else
        {
            resultElement.setText(generateXml(result));
        }
        if (logger.isDebugEnabled()) {
    		logger.debug("SOAP method with name " + getName() + " is finished.");
    	}        
    }

}
