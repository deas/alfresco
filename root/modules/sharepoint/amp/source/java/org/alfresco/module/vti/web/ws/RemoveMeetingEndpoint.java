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
 * Class for handling RemoveMeeting soap method
 * 
 * @author PavelYur
 */
public class RemoveMeetingEndpoint extends AbstractEndpoint
{

    // handler that provides methods for operating with meetings
    private MeetingServiceHandler handler;

    // xml namespace prefix
    private static String prefix = "mt";

    private static Log logger = LogFactory.getLog(RemoveMeetingEndpoint.class);

    public RemoveMeetingEndpoint(MeetingServiceHandler handler)
    {
        this.handler = handler;
    }

    /**
     * Remove meeting from Meeting Workspace
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

        Element requestElement = soapRequest.getDocument().getRootElement();

        // getting recurrenceId parameter from request
        if (logger.isDebugEnabled())
            logger.debug("Getting recurrenceId from request.");
        XPath recurrenceIdPath = new Dom4jXPath(buildXPath(prefix, "/RemoveMeeting/recurrenceId"));
        recurrenceIdPath.setNamespaceContext(nc);
        Element recurrenceId = (Element) recurrenceIdPath.selectSingleNode(requestElement);

        // getting uid parameter from request
        if (logger.isDebugEnabled())
            logger.debug("Getting uid from request.");
        XPath uidPath = new Dom4jXPath(buildXPath(prefix, "/RemoveMeeting/uid"));
        uidPath.setNamespaceContext(nc);
        Element uid = (Element) uidPath.selectSingleNode(requestElement);

        // getting sequence parameter from request
        if (logger.isDebugEnabled())
            logger.debug("Getting sequence from request.");
        XPath sequencePath = new Dom4jXPath(buildXPath(prefix, "/RemoveMeeting/sequence"));
        sequencePath.setNamespaceContext(nc);
        Element sequence = (Element) sequencePath.selectSingleNode(requestElement);

        // getting utcDateStamp parameter from request
        if (logger.isDebugEnabled())
            logger.debug("Getting utcDateStamp from request.");
        XPath utcDateStampPath = new Dom4jXPath(buildXPath(prefix, "/RemoveMeeting/utcDateStamp"));
        utcDateStampPath.setNamespaceContext(nc);

        // getting cancelMeeting parameter from request
        if (logger.isDebugEnabled())
            logger.debug("Getting cancelMeeting from request.");
        XPath cancelMeetingPath = new Dom4jXPath(buildXPath(prefix, "/RemoveMeeting/cancelMeeting"));
        cancelMeetingPath.setNamespaceContext(nc);
        Element cancelMeeting = (Element) cancelMeetingPath.selectSingleNode(requestElement);

        String siteName = getDwsFromUri(soapRequest).substring(1);

        handler.removeMeeting(siteName, Integer.parseInt(recurrenceId.getText()), uid.getText(), Integer.parseInt(sequence.getText()), null, Boolean.parseBoolean(cancelMeeting
                .getText()));

        // creating soap response
        soapResponse.getDocument().addElement("RemoveMeetingResponse", namespace);

        if (logger.isDebugEnabled())
        {
            logger.debug("SOAP method with name " + getName() + " is finished.");
        }
    }
}