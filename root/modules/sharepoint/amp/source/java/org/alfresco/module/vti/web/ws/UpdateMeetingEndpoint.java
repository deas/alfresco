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
import org.alfresco.module.vti.handler.ObjectNotFoundException;
import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.metadata.model.MeetingBean;
import org.alfresco.repo.site.SiteDoesNotExistException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

/**
 * Class for handling UpdateMeeting soap method
 * 
 * TODO Finish this off, then generalise it for AddMeeting and RestoreMeeting
 * TODO Link SetAttendeeResponse with this
 * 
 * @author Nick Burch
 */
public class UpdateMeetingEndpoint extends AddMeetingFromICalEndpoint
{
    // handler that provides methods for operating with meetings
    private MeetingServiceHandler handler;

    // xml namespace prefix
    private static String prefix = "mt";

    private static Log logger = LogFactory.getLog(UpdateMeetingEndpoint.class);

    public UpdateMeetingEndpoint(MeetingServiceHandler handler)
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

        // getting uid parameter from request
        if (logger.isDebugEnabled())
            logger.debug("Getting uid from request.");
        XPath uidPath = new Dom4jXPath(buildXPath(prefix, "/UpdateMeeting/uid"));
        uidPath.setNamespaceContext(nc);
        Element uid = (Element) uidPath.selectSingleNode(requestElement);

        // getting sequence parameter from request
        if (logger.isDebugEnabled())
            logger.debug("Getting sequence from request.");
        XPath sequencePath = new Dom4jXPath(buildXPath(prefix, "/UpdateMeeting/sequence"));
        sequencePath.setNamespaceContext(nc);
        Element sequence = (Element) sequencePath.selectSingleNode(requestElement);

        // getting utcDateStamp parameter from request
        if (logger.isDebugEnabled())
            logger.debug("Getting utcDateStamp from request.");
        XPath utcDateStampPath = new Dom4jXPath(buildXPath(prefix, "/UpdateMeeting/utcDateStamp"));
        utcDateStampPath.setNamespaceContext(nc);

        // TODO
        MeetingBean meetingBean = null;
        String siteName = getDwsFromUri(soapRequest).substring(1);
        
        try
        {
            handler.updateMeeting(siteName, meetingBean);
        }
        catch (SiteDoesNotExistException se)
        {
            throw new VtiHandlerException(getMessage("vti.meeting.error.no_site_update"));
        }
        catch (ObjectNotFoundException eo)
        {
            throw new VtiHandlerException(getMessage("vti.meeting.error.no_meeting_update"));
        }

        // creating soap response
        soapResponse.setContentType("text/xml");
        soapResponse.getDocument().addElement("UpdateMeetingResponse", namespace);
        
        if (logger.isDebugEnabled())
        {
            logger.debug("SOAP method with name " + getName() + " is finished.");
        }
    }
}
