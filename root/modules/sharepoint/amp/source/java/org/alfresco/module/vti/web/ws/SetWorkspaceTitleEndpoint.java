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

import org.alfresco.module.vti.handler.MeetingServiceHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

/**
 * Class for handling SetWorkspaceTitle soap method
 * 
 * @author PavelYur
 */
public class SetWorkspaceTitleEndpoint extends AbstractEndpoint
{

    // handler that provides methods for operating with meetings
    private MeetingServiceHandler handler;

    // xml namespace prefix
    private static String prefix = "mt";

    private static Log logger = LogFactory.getLog(SetWorkspaceTitleEndpoint.class);

    public SetWorkspaceTitleEndpoint(MeetingServiceHandler handler)
    {
        this.handler = handler;
    }

    /**
     * Set Meeting Workspace Title on Alfresco server
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

        // Get the site name to update
        String siteName = getDwsFromUri(soapRequest).substring(1);
        
        
        Element requestElement = soapRequest.getDocument().getRootElement();

        // If no new title is given, then an empty string is used
        if (logger.isDebugEnabled())
            logger.debug("Getting title from request.");
        XPath titlePath = new Dom4jXPath(buildXPath(prefix, "/SetWorkspaceTitle/title"));
        titlePath.setNamespaceContext(nc);
        Element titleE = (Element) titlePath.selectSingleNode(requestElement);

        String title = "";
        if (titleE != null && titleE.getText() != null || titleE.getText().length() < 1)
        {
            title = titleE.getText();
        }
        

        // Perform the title update
        handler.updateWorkspaceTitle(siteName, title);

        // creating soap response
        soapResponse.setContentType("text/xml");
        soapResponse.getDocument().addElement("SetWorkspaceTitleResponse", namespace);

        if (logger.isDebugEnabled())
        {
            logger.debug("SOAP method with name " + getName() + " is finished.");
        }
    }
}