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

import org.alfresco.module.vti.handler.CheckOutCheckInServiceHandler;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

/**
 * Class for handling CheckIn soap method
 * 
 * @author DmitryVas
 */
public class CheckInFileEndpoint extends AbstractEndpoint
{

    private static Log logger = LogFactory.getLog(CheckInFileEndpoint.class);

    // handler that provides methods for checkOut/CheckIn operations
    private CheckOutCheckInServiceHandler handler;

    // xml namespace prefix
    private static String prefix = "webs";

    /**
     * constructor
     *
     * @param handler
     */
    public CheckInFileEndpoint(CheckOutCheckInServiceHandler handler)
    {
        this.handler = handler;
    }

    /**
     * Check in file
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

        String host = getHost(soapRequest);
        String context = soapRequest.getAlfrescoContextName();

        // getting pageUrl parameter from request
        XPath xpath = new Dom4jXPath(buildXPath(prefix, "/CheckInFile/pageUrl"));
        xpath.setNamespaceContext(nc);
        Element docE = (Element) xpath.selectSingleNode(soapRequest.getDocument().getRootElement());
        if (docE == null || docE.getTextTrim().length() == 0)
        {
           throw new VtiSoapException("pageUrl must be supplied", 0x82000001l);
        }
        String docPath = URLDecoder.decode(docE.getTextTrim(), "UTF-8");
        docPath = docPath.substring(host.length() + context.length());

        xpath = new Dom4jXPath(buildXPath(prefix, "/CheckInFile/comment"));
        xpath.setNamespaceContext(nc);
        String comment = ((Element) xpath.selectSingleNode(
                    soapRequest.getDocument().getRootElement())).getTextTrim();
        
        if (logger.isDebugEnabled())
        {
            logger.debug("item parameter for this request: " + docPath);
        }

        NodeRef originalNode;
        try
        {
           originalNode = handler.checkInDocument(docPath, comment);
        }
        catch(FileNotFoundException fnfe)
        {
           throw new VtiSoapException("File not found", -1, fnfe);
        }

        // creating soap response
        Element responsElement = soapResponse.getDocument().addElement("CheckInFileResponse", namespace);
        Element result = responsElement.addElement("CheckInFileResult");
        result.setText(originalNode != null ? "true" : "false");

        soapResponse.setContentType("text/xml");
        if (logger.isDebugEnabled())
        {
            logger.debug("Soap Method with name " + getName() + " is finished.");
        }

    }
}
