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
import org.alfresco.module.vti.metadata.model.MeetingBean;
import org.alfresco.util.ISO8601DateFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

/**
 * Parent class of Meeting Endpoints.
 * 
 * @author Nick Burch
 */
public abstract class AbstractMeetingEndpoint extends AbstractEndpoint
{
    // xml namespace prefix
    protected static String prefix = "mt";

    private static Log logger = LogFactory.getLog(AbstractMeetingEndpoint.class);

    // handler that provides methods for operating with meetings
    protected MeetingServiceHandler handler;
    public AbstractMeetingEndpoint(MeetingServiceHandler handler)
    {
        this.handler = handler;
    }

    /**
     * Add/Update/Delete meeting in Meeting Workspace
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
            throw new VtiSoapException("A Site Name must be supplied", 6);
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
        executeRequest(soapRequest, soapResponse, siteName, requestElement, nc);
    }

    protected void executeRequest(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse, String siteName,
                                  Element requestElement, SimpleNamespaceContext nc) throws Exception
    {
        // getting uid parameter from request
        XPath uidPath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/uid"));
        uidPath.setNamespaceContext(nc);
        Element uidE = (Element) uidPath.selectSingleNode(requestElement);
        String uid = uidE.getText();
        if (logger.isDebugEnabled())
            logger.debug("Getting uid from request: " + uid);

        // Get the Organizer Email
        XPath orgEmailPath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/organizerEmail"));
        orgEmailPath.setNamespaceContext(nc);
        Element organizerEmailE = (Element) orgEmailPath.selectSingleNode(requestElement);
        String organizerEmail = null;
        if (organizerEmailE != null && organizerEmailE.getText() != null)
        {
            organizerEmail = organizerEmailE.getText();
        }
        if (logger.isDebugEnabled())
            logger.debug("Getting organizer email from request: " + organizerEmail);
        
        // getting sequence parameter from request
        XPath sequencePath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/sequence"));
        sequencePath.setNamespaceContext(nc);
        Element sequenceE = (Element) sequencePath.selectSingleNode(requestElement);
        int sequence = 0;
        if (sequenceE != null && sequenceE.getText() != null)
        {
            sequence = Integer.parseInt(sequenceE.getText());
        }
        if (logger.isDebugEnabled())
            logger.debug("Getting sequence from request: " + sequence);

        // getting utcDateStamp parameter from request
        if (logger.isDebugEnabled())
            logger.debug("Getting utcDateStamp from request.");
        XPath utcDateStampPath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/utcDateStamp"));
        utcDateStampPath.setNamespaceContext(nc);
        Element utcDateStamp = (Element) utcDateStampPath.selectSingleNode(requestElement);

        // title
        XPath titlePath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/title"));
        titlePath.setNamespaceContext(nc);
        Element titleE = (Element) titlePath.selectSingleNode(requestElement);
        String title = null;
        if (titleE != null && titleE.getText() != null)
        {
            title = titleE.getText();
        }
        if (logger.isDebugEnabled())
            logger.debug("Getting title from request: " + title);

        // location
        XPath locationPath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/location"));
        locationPath.setNamespaceContext(nc);
        Element locationE = (Element) locationPath.selectSingleNode(requestElement);
        String location = null;
        if (locationE != null && locationE.getText() != null)
        {
            location = locationE.getText();
        }
        if (logger.isDebugEnabled())
            logger.debug("Getting location from request: " + location);

        // utcDateStart
        XPath dateStartPath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/utcDateStart"));
        dateStartPath.setNamespaceContext(nc);
        Element dateStartE = (Element) dateStartPath.selectSingleNode(requestElement);
        Date dateStart = null;
        if (dateStartE != null && dateStartE.getText() != null)
        {
            dateStart = ISO8601DateFormat.parse(dateStartE.getText());
        }
        if (logger.isDebugEnabled())
            logger.debug("Getting date start from request: " + dateStart);
        
        // utcDateEnd
        XPath dateEndPath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/utcDateEnd"));
        dateEndPath.setNamespaceContext(nc);
        Element dateEndE = (Element) dateEndPath.selectSingleNode(requestElement);
        Date dateEnd = null;
        if (dateEndE != null && dateEndE.getText() != null)
        {
            dateEnd = ISO8601DateFormat.parse(dateEndE.getText());
        }
        if (logger.isDebugEnabled())
            logger.debug("Getting date end from request: " + dateEnd);
        
        // getting recurrenceId parameter from request
        XPath recurrenceIdPath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/recurrenceId"));
        recurrenceIdPath.setNamespaceContext(nc);
        Element recurrenceIdE = (Element) recurrenceIdPath.selectSingleNode(requestElement);
        int recurrenceId = 0;
        if (recurrenceIdE != null && recurrenceIdE.getText() != null)
        {
            recurrenceId = Integer.parseInt(recurrenceIdE.getText());
        }
        if (logger.isDebugEnabled())
            logger.debug("Getting recurrenceId from request: " + recurrenceId);

        // getting cancelMeeting parameter from request
        XPath cancelMeetingPath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/cancelMeeting"));
        cancelMeetingPath.setNamespaceContext(nc);
        Element cancelMeetingE = (Element) cancelMeetingPath.selectSingleNode(requestElement);
        boolean cancelMeeting = true;
        if (cancelMeetingE != null && cancelMeetingE.getText() != null)
        {
            cancelMeeting = Boolean.parseBoolean(cancelMeetingE.getText());
        }
        if (logger.isDebugEnabled())
            logger.debug("Getting cancelMeeting from request: " + cancelMeeting);

        // Try to turn this into a MeetingBean
        MeetingBean meetingBean = new MeetingBean();
        meetingBean.setId(uid);
        meetingBean.setOrganizer(organizerEmail);
        meetingBean.setTitle(title);
        meetingBean.setLocation(location);
        meetingBean.setStart(dateStart);
        meetingBean.setEnd(dateEnd);
        
        
        // Have the real action performed
        executeMeetingAction(soapRequest, soapResponse, siteName, meetingBean, sequence, recurrenceId, 
                false, cancelMeeting);
        
        
        // All done
        if (logger.isDebugEnabled())
        {
            logger.debug("SOAP method with name " + getName() + " is finished.");
        }
    }
    
    protected abstract void executeMeetingAction(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse,
            String siteName, MeetingBean meetingBean, int sequence, int recurrenceId, 
            boolean ignoreAttendees, boolean cancelMeeting) throws Exception;
    
    /**
     * Builds most of the standard response
     */
    protected Element buildMeetingResponse(VtiSoapResponse soapResponse) throws Exception
    {
        // Creating soap response
        soapResponse.setContentType("text/xml");
        return soapResponse.getDocument().addElement(getName() + "Response", namespace);
    }
}