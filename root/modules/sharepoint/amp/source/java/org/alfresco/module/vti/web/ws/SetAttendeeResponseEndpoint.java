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

import java.util.Date;

import org.alfresco.module.vti.handler.MeetingServiceHandler;
import org.alfresco.module.vti.handler.MeetingServiceHandler.AttendeeStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

/**
 * Class for handling SetAttendeeResponse soap method
 *
 * TODO The underlying handler currently does nothing with the details
 * 
 * @author Nick Burch
 */
public class SetAttendeeResponseEndpoint extends AbstractEndpoint
{
    // handler that provides methods for operating with meetings
    private MeetingServiceHandler handler;

    // xml namespace prefix
    private static String prefix = "mt";

    private static Log logger = LogFactory.getLog(SetAttendeeResponseEndpoint.class);

    public SetAttendeeResponseEndpoint(MeetingServiceHandler handler)
    {
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

        // Get the site
        String siteName = getDwsFromUri(soapRequest);
        if (siteName == null || siteName.length() == 0)
        {
            // TODO Is this the right exception?
            throw new VtiSoapException("A Site Name must be supplied", 0);
        }
        else
        {
            if (siteName.startsWith("/"))
            {
                siteName = siteName.substring(1);
            }
        }
        
        
        // Process the request
        Element requestElement = soapRequest.getDocument().getRootElement();

        // Meeting UID this refers to
        XPath uidPath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/uid"));
        uidPath.setNamespaceContext(nc);
        Element uidE = (Element) uidPath.selectSingleNode(requestElement);
        String uid = uidE.getText();

        // The replying Attendee's email address
        XPath attendeeEmailPath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/attendeeEmail"));
        attendeeEmailPath.setNamespaceContext(nc);
        Element attendeeEmailE = (Element) attendeeEmailPath.selectSingleNode(requestElement);
        String attendeeEmail = null;
        if (attendeeEmailE != null && attendeeEmailE.getText() != null)
        {
            attendeeEmail = attendeeEmailE.getText();
        }
        
        // Meeting sequence number
        XPath sequencePath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/sequence"));
        sequencePath.setNamespaceContext(nc);
        Element sequenceE = (Element) sequencePath.selectSingleNode(requestElement);
        int sequence = 0;
        if (sequenceE != null && sequenceE.getText() != null)
        {
            sequence = Integer.parseInt(sequenceE.getText());
        }
        
        // Meeting Recurrence ID
        XPath recurrenceIdPath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/recurrenceId"));
        recurrenceIdPath.setNamespaceContext(nc);
        Element recurrenceIdE = (Element) recurrenceIdPath.selectSingleNode(requestElement);
        int recurrenceId = 0;
        if (recurrenceIdE != null && recurrenceIdE.getText() != null)
        {
            recurrenceId = Integer.parseInt(recurrenceIdE.getText());
        }

        // Response Status
        XPath responsePath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/attendeeResponse"));
        responsePath.setNamespaceContext(nc);
        Element responseE = (Element) responsePath.selectSingleNode(requestElement);
        AttendeeStatus status = AttendeeStatus.Pending;
        if (responseE != null && responseE.getText() != null)
        {
            if ("responseAccepted".equals(responseE.getText()))
            {
                status = AttendeeStatus.Accepted;
            }
            if ("responseTentative".equals(responseE.getText()))
            {
                status = AttendeeStatus.Tentative;
            }
            if ("responseDeclined".equals(responseE.getText()))
            {
                status = AttendeeStatus.Declined;
            }
        }
        
        
        // Have their response set
        handler.updateAttendeeResponse(siteName, attendeeEmail, status, uid, recurrenceId, sequence, (Date)null);
        
        
        // Send the response
        soapResponse.setContentType("text/xml");
        soapResponse.getDocument().addElement("SetAttendeeResponseResponse", namespace);
        
        if (logger.isDebugEnabled())
        {
            logger.debug("SOAP method with name " + getName() + " is finished.");
        }
    }
}
