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
import org.alfresco.module.vti.metadata.model.MeetingBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

/**
 * Class for handling UpdateMeetingFromICal soap method
 * 
 * @author PavelYur
 */
public class UpdateMeetingFromICalEndpoint extends AddMeetingFromICalEndpoint
{
    // handler that provides methods for operating with meetings
    private MeetingServiceHandler handler;

    // xml namespace prefix
    private static String prefix = "mt";

    private static Log logger = LogFactory.getLog(UpdateMeetingFromICalEndpoint.class);

    public UpdateMeetingFromICalEndpoint(MeetingServiceHandler handler)
    {
        super(handler);
        this.handler = handler;
    }

    /**
     * Update meeting in Meeting Workspace
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

        // getting icalText parameter from request
        if (logger.isDebugEnabled())
            logger.debug("Getting icalText from request.");
        XPath icalTextPath = new Dom4jXPath(buildXPath(prefix, "/UpdateMeetingFromICal/icalText"));
        icalTextPath.setNamespaceContext(nc);
        Element icalText = (Element) icalTextPath.selectSingleNode(requestElement);

        // getting ignoreAttendees parameter from request
        if (logger.isDebugEnabled())
            logger.debug("Getting ignoreAttendees from request.");
        XPath ignoreAttendeesPath = new Dom4jXPath(buildXPath(prefix, "/UpdateMeetingFromICal/ignoreAttendees"));
        ignoreAttendeesPath.setNamespaceContext(nc);
        Element ignoreAttendees = (Element) ignoreAttendeesPath.selectSingleNode(requestElement);

        MeetingBean meetingBean = getMeeting(icalText.getText());
        String siteName = getDwsFromUri(soapRequest).substring(1);
        handler.updateMeetingFromICal(siteName, meetingBean, Boolean.parseBoolean(ignoreAttendees.getText()));

        // creating soap response
        Element updateStatus = soapResponse.getDocument().addElement("UpdateMeetingFromICalResponse", namespace).addElement("UpdateMeetingFromICalResult").addElement(
                "UpdateMeetingFromICal").addElement("AttendeeUpdateStatus");
        updateStatus.addAttribute("Code", "0");
        updateStatus.addAttribute("Detail", "");
        updateStatus.addAttribute("ManageUserPage", "");

        if (logger.isDebugEnabled())
        {
            logger.debug("SOAP method with name " + getName() + " is finished.");
        }
    }
}