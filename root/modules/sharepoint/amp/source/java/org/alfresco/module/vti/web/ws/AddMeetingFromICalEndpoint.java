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
import org.dom4j.Element;

/**
 * Class for handling AddMeetingFromICal soap method
 * 
 * @author PavelYur
 */
public class AddMeetingFromICalEndpoint extends AbstractMeetingFromICalEndpoint
{
    public AddMeetingFromICalEndpoint(MeetingServiceHandler handler)
    {
        super(handler);
    }

    /**
     * Add new meeting to Meeting Workspace
     * 
     * @param soapRequest Vti soap request ({@link VtiSoapRequest})
     * @param soapResponse Vti soap response ({@link VtiSoapResponse})
     */
    @Override
    protected void executeMeetingAction(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse, String siteName,
            MeetingBean meetingBean, int sequence, int recurrenceId, boolean ignoreAttendees, boolean cancelMeeting) throws Exception
    {
        // Have the meeting added
        handler.addMeetingFromICal(siteName, meetingBean); 

        // Report what we did
        Element root = soapResponse.getDocument().addElement("AddMeetingFromICalResponse", namespace);
        Element result = root.addElement("AddMeetingFromICalResult");
        Element meetingICal = result.addElement("AddMeetingFromICal");
        meetingICal.addAttribute("Url", getHost(soapRequest) + soapRequest.getAlfrescoContextName() + "/" + siteName + "?calendar=calendar")
                   .addAttribute("HostTitle", meetingBean.getSubject()).addAttribute("UniquePermissions", "true")
                   .addAttribute("MeetingCount", "1").addAttribute("AnonymousAccess", "false")
                   .addAttribute("AllowAuthenticatedUsers", "false");
        meetingICal.addElement("AttendeeUpdateStatus").addAttribute("Code", "0").addAttribute("Detail", "").addAttribute("ManageUserPage", "");

        soapResponse.setContentType("text/xml");
    }
}