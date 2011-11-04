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
import org.alfresco.module.vti.metadata.model.DwsBean;
import org.alfresco.repo.SessionUser;
import org.alfresco.repo.webdav.auth.SharepointConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jaxen.JaxenException;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

/**
 * Class for handling CreateDws soap method
 * 
 * @author AndreyAk
 *
 */
public class CreateDwsEndpoint extends AbstractEndpoint
{
	private final static Log logger = LogFactory.getLog(CreateDwsEndpoint.class);
	
    // handler that provides methods for operating with documents and folders
    private DwsServiceHandler handler;
    
    // xml namespace prefix
    private static String prefix = "dws";

    /**
     * @param handler that provides methods for operating with documents and folders
     */
    public CreateDwsEndpoint(DwsServiceHandler handler)
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

        Element rootElement = soapRequest.getDocument().getRootElement();

        // getting title parameter from request
        String dwsName = getParameter(rootElement, "/CreateDws/name", nc);
        
        // getting title parameter from request
        String title = getParameter(rootElement, "/CreateDws/title", nc);
        
        String parentDws = getDwsForCreationFromUri(soapRequest);
        DwsBean dws = handler.createDws(parentDws, dwsName, null, title, null, getHost(soapRequest), getContext(soapRequest), (SessionUser) soapRequest.getSession().getAttribute(SharepointConstants.USER_SESSION_ATTRIBUTE));
        
        // creating soap response
        Element resultElement = buildResultTag(soapResponse);
        
        resultElement.addText(generateXml(dws));
        
        if (logger.isDebugEnabled()) {
    		logger.debug("SOAP method with name " + getName() + " is finished.");
    	}        
    }

    protected String getParameter(Element rootElement, String path, SimpleNamespaceContext nc) throws JaxenException
    {
        XPath xPath = new Dom4jXPath(buildXPath(prefix, path));
        xPath.setNamespaceContext(nc);
        Element titleEl = (Element) xPath.selectSingleNode(rootElement);
        return titleEl.getTextTrim();
    }
}
