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
import org.alfresco.module.vti.metadata.model.DwsMetadata;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;


/**
 * Class for handling GetDwsMetaData method from dws web service
 *
 * @author PavelYur
 *
 */
public class GetDwsMetaDataEndpoint extends AbstractEndpoint
{
	
	private final static Log logger = LogFactory.getLog(GetDwsMetaDataEndpoint.class);

    // handler that provides methods for operating with documents and folders
    private DwsServiceHandler handler;

    // xml namespace prefix
    private static String prefix = "dws";

    /**
     * constructor
     *
     * @param handler that provides methods for operating with documents and folders
     */
    public GetDwsMetaDataEndpoint(DwsServiceHandler handler)
    {
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
        
        Element reqElement = soapRequest.getDocument().getRootElement();
        
        // getting document parameter from request
        XPath docPath = new Dom4jXPath(buildXPath(prefix, "/GetDwsMetaData/document"));
        docPath.setNamespaceContext(nc);
        String doc = ((Element) docPath.selectSingleNode(reqElement)).getText();

        // getting id parameter from request
        XPath idPath = new Dom4jXPath(buildXPath(prefix, "/GetDwsMetaData/id"));
        idPath.setNamespaceContext(nc);
        Element id = (Element) idPath.selectSingleNode(reqElement);

        // getting minimal parameter from request
        XPath minimalPath = new Dom4jXPath(buildXPath(prefix, "/GetDwsMetaData/minimal"));
        minimalPath.setNamespaceContext(nc);
        Element minimal = (Element) minimalPath.selectSingleNode(reqElement);
        
        //getting information about document workspace
         DwsMetadata dwsMetadata = handler.getDWSMetaData(URLDecoder.decode(doc, "UTF-8"), id.getText(), Boolean.valueOf(minimal.getText()));
        
        // creating soap response
        Element root = soapResponse.getDocument().addElement("GetDwsMetaDataResponse", namespace);
        Element getDwsMetaDataResult = root.addElement("GetDwsMetaDataResult");

        getDwsMetaDataResult.addText(generateXml(dwsMetadata));
        
        if (logger.isDebugEnabled()) {
    		logger.debug("SOAP method with name " + getName() + " is finished.");
    	}
    }

}
